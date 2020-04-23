/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Agents;

/**
 *
 * @author Niels
 */

import Environment.Environment;
import java.util.ArrayList;
import sim.engine.*;
import sim.util.Bag;
import sim.util.Double2D;

public class Bot implements Steppable{
    private static final long serialVersionUID = 1;

    // STATES
    enum State {
    WAIT_TO_MOVE,
    MOVE_WHILE_OUTSIDE,
    MOVE_WHILE_INSIDE,
    JOINED_SHAPE
  }
    
    // BOT SETTINGS
    private int MAXGRAD;
    private double VISIBLE_DIST;
    private int ID_SIZE;
    private double MAX_DISTANCE;
    private double DESIRED_DISTANCE;
    private double ROTATION_STEP;
    private double STEPSIZE;
    private int BOTSIZE;
    
    // BOT VARIABLES
    private Boolean seed;
    private int gradient;
    private int ID;
    private double orientation_x;
    private double orientation_y;    
    private boolean generated_ID = false;    
    private Double2D location;
    private boolean localized = false;
    private State state = State.WAIT_TO_MOVE;
    private double previous_distance; // = this.MAX_DISTANCE;
    
    
    // Agent behavior
    @Override
    public void step(SimState state) {
        // GET ENVIRONMENT STATE
        Environment env = (Environment)state;
        
        Double2D position = env.field.getObjectLocationAsDouble2D(this);
        Bag neighbors = env.field.getNeighborsWithinDistance(position, this.VISIBLE_DIST, false);
        
        // Use locally unique ID, remove this line when using globally unique ID
        this.check_id(neighbors, env);
        
        if(this.state != State.JOINED_SHAPE){
            // Perform gradient formation and try to localize
            this.gradient = this.gradient_formation(neighbors);
            this.localization(neighbors, env);
            
            //BEHAVIOR IN DIFFERENT STATES
            // Wait in the starting shape and observe neighbors to determine if bot can start moving
            if(this.state == State.WAIT_TO_MOVE){
                if(this.no_moving_neighbors(neighbors)){
                    int highest_grad = this.highest_gradient(neighbors);
                    if(this.gradient > highest_grad){
                        this.state = State.MOVE_WHILE_OUTSIDE;
                    }
                    else if(this.gradient == highest_grad){
                        ArrayList<Bot> same_gradient = this.find_same_gradient(neighbors);
                        int highest_id = this.highest_id(same_gradient);
                        if(this.ID > highest_id){
                            this.state = State.MOVE_WHILE_OUTSIDE;
                        }
                    }
                }
            }
            // Edge following outside the shape
            else if(this.state == State.MOVE_WHILE_OUTSIDE){
                if(this.inside_shape()){
                    this.state = State.MOVE_WHILE_INSIDE;
                }
                this.edge_follow(neighbors, env);
            }
            // Edge following inside the shape
            else if(this.state == State.MOVE_WHILE_INSIDE){
                if(!this.inside_shape()){
                    this.state = State.JOINED_SHAPE;
                }
                else{
                    Bot closest = this.nearest_neighbor(position, neighbors, env);
                    if(closest.getGradient() < this.gradient){
                        this.edge_follow(neighbors, env);
                    }
                    else{
                        this.state = State.JOINED_SHAPE;
                    }
                }
            }
        }
    }
    
    // USED ALGORITHMS
    // Edge-following
    private void edge_follow(Bag neighbors, Environment env) {
        // Search for shortest distance to neighbor
        double dist = this.MAX_DISTANCE;
        Double2D current_location = env.field.getObjectLocationAsDouble2D(this);
        for(Object n : neighbors){
            Bot neighbor = (Bot) n;
            double d = current_location.distance(env.field.getObjectLocationAsDouble2D(neighbor));
            if(d < dist){
                dist = d;
            }
        }
        
        // Determine if the bot needs to rotate
        if(dist < this.DESIRED_DISTANCE && this.previous_distance > dist){
            // Too close to the other bots, rotate counterclockwise
            this.rotate(false);
        }
        else if(dist > this.DESIRED_DISTANCE && this.previous_distance < dist){
            // Too far from the other bots, rotate clockwise
            this.rotate(true);
        }
        
        if(this.move(env)){
            this.previous_distance = dist;
        }
    }
    
    // Gradient formation
    private int gradient_formation(Bag neighbors) {
        if(seed){
          return 0;  
        }
        else{
            int new_gradient = this.MAXGRAD; 
            for(Object n : neighbors){
                Bot neighbor = (Bot) n;
                if(new_gradient < neighbor.getGradient()){
                    new_gradient = neighbor.getGradient();
                }
            }
            return new_gradient+1;
        }
    }
    
    // Localization
    private void localization(Bag neighbors, Environment env) {
        if(!seed){
            ArrayList<Bot> localized_n = new ArrayList<Bot>(); 
            for(Object n : neighbors){
                Bot neighbor = (Bot) n;
                if(neighbor.has_joined_shape() && neighbor.isLocalized()){
                    localized_n.add(neighbor);
                }
            }
            if(this.at_least_three_noncollinear(localized_n)){
                Double2D real_location_this = env.field.getObjectLocationAsDouble2D(this);
                for(Bot n : localized_n){
                    Double2D n_location = env.field.getObjectLocationAsDouble2D(n);
                    // Euclidian distance based on coordinate system of the swarm
                    double c = this.location.distance(n.getLocation());
                    // Vector components of unit vector fromthis bot to the neighbor
                    double vx = Math.sqrt((this.location.getX() - n.getLocation().getX())*(this.location.getX() - n.getLocation().getX()))/c;
                    double vy = Math.sqrt((this.location.getY() - n.getLocation().getY())*(this.location.getY() - n.getLocation().getY()))/c;
                    // Determine new location following this point
                    Double2D real_location_other = env.field.getObjectLocationAsDouble2D(n);
                    double nx = real_location_this.getX() +  Math.sqrt((real_location_this.getX() - real_location_other.getX())*(real_location_this.getX() - real_location_other.getX()))*vx;
                    double ny = real_location_this.getY() +  Math.sqrt((real_location_this.getY() - real_location_other.getY())*(real_location_this.getY() - real_location_other.getY()))*vy;
                    // Update location
                    double x = this.location.getX() - ((this.location.getX() - nx)/4);
                    double y = this.location.getY() - ((this.location.getY() - ny)/4);
                    this.location = new Double2D(x, y);
                }
                this.localized = true;
            }  
        }
    }
    
    // Check ID if not locally unique -> make it unique
    // This is the algorithm from the paper, however if this gives problems it is also possible to give globally unique ID at creation
    private void check_id(Bag neighbors, Environment env) {
        // Init ID if not done yet
        if(!this.generated_ID){
            this.ID = env.random.nextInt(this.ID_SIZE);
            this.generated_ID = true;
        }
        for(Object n : neighbors){
            Bot neighbor = (Bot) n;
            
            if(neighbor.getID() == this.ID){
                this.generated_ID = false;
            }    
        } 
        if(!this.generated_ID){
            this.ID = env.random.nextInt(this.ID_SIZE);
            this.generated_ID = true;
        }
    }
    
    // Getters

    public Boolean has_joined_shape() {
        return this.state == State.JOINED_SHAPE;
    }

    public int getGradient() {
        return gradient;
    }

    public int getID() {
        return ID;
    }    

    public Double2D getLocation() {
        return location;
    }

    public boolean isLocalized() {
        return localized;
    }
    
    // Extra functions
    // Check if there are at least three noncollinear bots in the given list
    private boolean at_least_three_noncollinear(ArrayList<Bot> localized_n) {
        
        if(localized_n.size() < 3){
            return false;
        }
        
        boolean noncollinear = false;
        int third = 2;
        
        double ydiff = localized_n.get(1).getLocation().getY() - localized_n.get(0).getLocation().getY();
        double xdiff = localized_n.get(1).getLocation().getX() - localized_n.get(0).getLocation().getX();
        double slope1 = ydiff / xdiff;
        
        while(!noncollinear && third < localized_n.size()){
            ydiff = localized_n.get(third).getLocation().getY() - localized_n.get(0).getLocation().getY();
            xdiff = localized_n.get(third).getLocation().getX() - localized_n.get(0).getLocation().getX();
            double slope2 = ydiff / xdiff;
            noncollinear = (slope1 == slope2);
        }
        
        return noncollinear;
    }
    
    // Rotate the bot clockwise or counterclockwise
    private void rotate(boolean clockwise) {
        double beta = this.ROTATION_STEP;
        if(clockwise){
            beta = -beta;
        }
        
        double new_ox = Math.cos(beta*this.orientation_x) - Math.sin(beta*this.orientation_y);
        double new_oy = Math.sin(beta*this.orientation_x) + Math.cos(beta*this.orientation_y);
        
        this.orientation_x = new_ox;
        this.orientation_y = new_oy;
    }
    
    // Move the bot forward, following its orientation
    private boolean move(Environment env) {
        // move forward
        Double2D current = env.field.getObjectLocationAsDouble2D(this);
        double new_x = current.getX() + this.STEPSIZE * this.orientation_x;
        double new_y = current.getY() + this.STEPSIZE * this.orientation_y;
        
        Double2D new_location = new Double2D(new_x, new_y);
        
        // Check if new location not obstructed by other bots
        Bag neighbors = env.field.getNeighborsExactlyWithinDistance(new_location, 2*this.BOTSIZE);
        if(neighbors.isEmpty()){
            env.field.setObjectLocation(this, new_location);
            return true;
        }
        return false;
    }
    
    // Checks if the bot is currently in the given shape
    private boolean inside_shape(){
        return false;
    }
    
    // Checks if there are visible neighbors in a moving state
    private boolean no_moving_neighbors(Bag neighbors) {
        boolean moving = false;
        for(Object n : neighbors){
            Bot neighbor = (Bot) n;
            if(neighbor.isMoving()){
                moving = true;
            }
        }
        return moving;
    }
    
    // Find highest gradient among neighbors
    private int highest_gradient(Bag neighbors) {
        int highest = 0;
        for(Object n : neighbors){
            Bot neighbor = (Bot) n;
            if(neighbor.getGradient() > highest){
                highest = neighbor.getGradient();
            }
        }
        return highest;
    }
    
    // Return a list with all neighbors with the same gradient
    private ArrayList<Bot> find_same_gradient(Bag neighbors) {
        ArrayList<Bot> same_grad = new ArrayList<Bot>();
        for(Object n : neighbors){
            Bot neighbor = (Bot) n;
            if(neighbor.getGradient() == this.gradient){
                same_grad.add(neighbor);
            }
        }
        return same_grad;
    }
    
    // Return the highest ID from a list of bots
    private int highest_id(ArrayList<Bot> bots) {
        int highest = 0;
        for(Bot n : bots){
            if(n.getID() > highest){
                highest = n.getID();
            }
        }
        return highest;
    }
    
    // Find the single nearest visible neighbor
    private Bot nearest_neighbor(Double2D position, Bag neighbors, Environment env) {
        Bot nearest = null;
        double shortest_dist = 0;
        for(Object n : neighbors){
            Bot neighbor = (Bot) n;
            if(nearest == null){
                nearest = neighbor;
                shortest_dist = position.distance(env.field.getObjectLocationAsDouble2D(neighbor));
            }
            else{
                double dist = position.distance(env.field.getObjectLocationAsDouble2D(neighbor));
                if(dist < shortest_dist){
                    nearest = neighbor;
                    shortest_dist = dist;
                }
            }
        }
        return nearest;
    }
    
    // Returns true if this bot is in a moving state
    private boolean isMoving() {
        return (this.state == State.MOVE_WHILE_INSIDE) || (this.state == State.MOVE_WHILE_OUTSIDE);
    }
    
}
