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
import Shape.Shape;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import sim.engine.*;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.SimplePortrayal2D;
import sim.util.Bag;
import sim.util.Double2D;

// This is a modified version of the regular bot that can form bridges to traverse gaps between separated target shapes
public class BridgeBot extends SimplePortrayal2D implements Steppable {
    private static final long serialVersionUID = 1;

    // STATES
    enum State {
    WAIT_TO_MOVE,
    MOVE_WHILE_OUTSIDE,
    MOVE_WHILE_INSIDE,
    JOINED_SHAPE,
    JOINED_BRIDGE
  }
    
    // BOT SETTINGS
    private final int MAXGRAD = 10000;
    private final double VISIBLE_DIST = 70;
    private final int ID_SIZE = 100000;
    private final double MAX_DISTANCE = 500;
    private final double DESIRED_DISTANCE = 11;
    private final double ROTATION_STEP = 5;
    private final double STEPSIZE = 1;
    private final int BOTSIZE = 10;
    private final double ZEROX = 100;
    private final double ZEROY = 700;
    //test
    private final int STEPTHRESHOLD = 60;
    
    // BOT VARIABLES
    private boolean seed;
    private int gradient;
    private int ID;
    private double orientation_x;
    private double orientation_y;    
    private boolean generated_ID = false;    
    private Double2D location;
    private boolean localized = false;
    private State state = State.WAIT_TO_MOVE;
    private double previous_distance;
    private final Shape shape;
    //test
    private int step = 0;
    //Extension for bridge formation
    private boolean dissolve = false;
    private boolean ybridge = false;
    private boolean xbridge = false;
    
    // Constructor
    public BridgeBot(boolean seed, boolean gradient_seed, double orientation_x, double orientation_y, Double2D location, Shape shape) {
        this.seed = seed;
        this.orientation_x = orientation_x;
        this.orientation_y = orientation_y;
        this.location = location;
        this.shape = shape;
        this.previous_distance = this.MAX_DISTANCE;
        this.gradient = this.MAXGRAD;
        
        if(seed){
            this.state = State.JOINED_SHAPE;
            this.localized = true;
            if(gradient_seed){ this.gradient = 0;}
            else {this.gradient = 1;}
        }
    }
    
    public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
        final double width = info.draw.width * this.BOTSIZE;
        final double height = info.draw.height * this.BOTSIZE;

        if (this.seed){
            graphics.setColor(Color.green);;
        }
        else if (this.state == State.JOINED_SHAPE){
            graphics.setColor(Color.red);
        }
        else if (this.state == State.JOINED_BRIDGE){
            graphics.setColor(Color.cyan);
        }
        else if (this.state == State.MOVE_WHILE_OUTSIDE){
            graphics.setColor(Color.yellow);
        }
        else if (this.state == State.MOVE_WHILE_INSIDE){
            graphics.setColor(Color.orange);
        }        
        else {
            graphics.setColor(Color.blue);
        }

        final int x = (int)(info.draw.x - width / 2.0);
        final int y = (int)(info.draw.y - height / 2.0);
        final int w = (int)(width);
        final int h = (int)(height);

        // Draw centered on the origin
        graphics.fillOval(x,y,w,h);
        // Show gradient
        graphics.setColor(Color.black);        
        graphics.drawString(Integer.toString(this.gradient), x+2, y+10);
        //graphics.drawString(this.location.toCoordinates(), x, y+10);
    }
        
    // Agent behavior
    @Override
    public void step(SimState state) {    
        // GET ENVIRONMENT STATE
        Environment env = (Environment)state;
        
        Double2D position = env.field.getObjectLocationAsDouble2D(this);
        
        // Determine bags of neighbors, there are different visible distances for different algorithms
        Bag neighbors = env.field.getNeighborsWithinDistance(position, this.VISIBLE_DIST, false);
        neighbors.remove(this);
        
        Bag neighbors_start = env.field.getNeighborsWithinDistance(position, this.BOTSIZE*2, false);
        neighbors_start.remove(this);
        
        Bag neighbors_grad = env.field.getNeighborsWithinDistance(position, this.BOTSIZE+1, false);
        neighbors_grad.remove(this);
        
        // Use locally unique ID, remove this line when using globally unique ID
        this.check_id(neighbors, env);
        
        if(this.state != State.JOINED_SHAPE){
            // Perform gradient formation and try to localize
            int newgrad = this.gradient_formation(neighbors_grad);
            if(!((newgrad >= this.MAXGRAD) && (this.gradient < this.MAXGRAD))){  // EXTENSION -> prevent going back to maxgradient if you already have a gradient
                this.gradient = newgrad;
            }
            //if(!this.isLocalized()){
            this.localization(neighbors, env);
            //}
            
            // BEHAVIOR IN DIFFERENT STATES
            // Wait in the starting shape and observe neighbors to determine if bot can start moving
            if(this.state == State.WAIT_TO_MOVE && this.gradient < this.MAXGRAD){
                if(this.no_moving_neighbors(neighbors_start)){
                    int highest_grad = this.highest_gradient(neighbors_start);
                    if(this.gradient > highest_grad){
                        this.state = State.MOVE_WHILE_OUTSIDE;
                        this.move(env);
                    }
                    else if(this.gradient == highest_grad){
                        ArrayList<BridgeBot> same_gradient = this.find_same_gradient(neighbors_start);
                        int highest_id = this.highest_id(same_gradient);
                        if(this.ID > highest_id){
                            this.state = State.MOVE_WHILE_OUTSIDE;
                            this.move(env);
                        }
                    }
                }
            }
            // Edge following outside the shape
            else if(this.state == State.MOVE_WHILE_OUTSIDE){
                if(this.localized && this.inside_shape()){
                    this.state = State.MOVE_WHILE_INSIDE;
                }
                if(this.localized && this.inside_bridge()){
                    this.state = State.JOINED_BRIDGE;
                    boolean y = this.inside_ybridge();
                    this.ybridge = y;
                    this.xbridge = !y;
                }
                this.edge_follow(neighbors, env);
            }
            // Edge following inside the shape
            else if(this.state == State.MOVE_WHILE_INSIDE){
                if(this.localized && !this.inside_shape()){
                    this.state = State.JOINED_SHAPE;
                }
                else{
                    BridgeBot closest = this.nearest_neighbor(position, neighbors, env);
                    if(closest != null && closest.getGradient() < this.gradient){
                        if(this.localized && this.inside_bridge()){
                            this.state = State.JOINED_BRIDGE;
                            boolean y = this.inside_ybridge();
                            this.ybridge = y;
                            this.xbridge = !y;
                        }
                        this.edge_follow(neighbors, env);
                    }
                    else{
                        this.state = State.JOINED_SHAPE;
                    }
                }
            }
            // Part of a bridge
            else if(this.state == State.JOINED_BRIDGE){
                if(this.startdissolving(neighbors)){
                    this.dissolve = true;
                }
                
                if(this.dissolve){
                    if(this.min_bridge_gradient(neighbors)){
                        this.state = State.MOVE_WHILE_OUTSIDE;
                        this.ybridge = false;
                        this.xbridge = false;
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
            BridgeBot neighbor = (BridgeBot) n;
            if(!neighbor.isMoving()){
                double d = current_location.distance(env.field.getObjectLocationAsDouble2D(neighbor));
                if(d < dist){
                    dist = d;
                }
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
                BridgeBot neighbor = (BridgeBot) n;
                if(new_gradient > neighbor.getGradient()){
                    new_gradient = neighbor.getGradient();
                }
            }
            int grad = new_gradient+1;
            return grad;
        }
    }
    
    // Localization
    private void localization(Bag neighbors, Environment env) {
        if(!seed){
            ArrayList<BridgeBot> localized_n = new ArrayList<BridgeBot>(); 
            for(Object n : neighbors){
                BridgeBot neighbor = (BridgeBot) n;
                if(!neighbor.isMoving() && neighbor.isLocalized()){
                    localized_n.add(neighbor);
                }
            }
            if(this.at_least_three_noncollinear(localized_n)){
                for(BridgeBot n : localized_n){
                    // Measured distance
                    double m = env.field.getObjectLocationAsDouble2D(this).distance(env.field.getObjectLocationAsDouble2D(n)) / this.BOTSIZE;                    
                    // Euclidian distance based on coordinate system of the swarm
                    double c = this.location.distance(n.getLocation());
                    // Vector components of unit vector from this bot to the neighbor
                    double vx = (this.location.getX() - n.getLocation().getX())/c;
                    double vy = (this.location.getY() - n.getLocation().getY())/c;
                    // Determine new location following this point
                    double nx = n.location.getX() +  m*vx;
                    double ny = n.location.getY() +  m*vy;
                    // Update location
                    double x = this.location.getX() - (((this.location.getX() - nx)/4));
                    double y = this.location.getY() - (((this.location.getY() - ny)/4));
                    this.location = new Double2D(x, y);
                }
                this.step++;
                if(this.step > this.STEPTHRESHOLD){
                    this.localized = true;
                }
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
            BridgeBot neighbor = (BridgeBot) n;
            
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
    
    public boolean isSeed() {
        return seed;
    }
    
    // Extra functions
    // Check if there are at least three noncollinear bots in the given list
    private boolean at_least_three_noncollinear(ArrayList<BridgeBot> localized_n) {
        
        if(localized_n.size() < 3){
            return false;
        }
        
        boolean noncollinear = false;
        int third = 2;
        
        double ydiff1 = localized_n.get(1).getLocation().getY() - localized_n.get(0).getLocation().getY();
        double xdiff1 = localized_n.get(1).getLocation().getX() - localized_n.get(0).getLocation().getX();
        
        while(!noncollinear && third < localized_n.size()){
            double ydiff = localized_n.get(third).getLocation().getY() - localized_n.get(0).getLocation().getY();
            double xdiff = localized_n.get(third).getLocation().getX() - localized_n.get(0).getLocation().getX();
            double det = xdiff1*ydiff - ydiff1*xdiff; // Determinant of matrix of vector coordinates -> is zero when colinear
            noncollinear = (det != 0);
            third++;
        }
        
        return noncollinear;
    }
    
    // Rotate the bot clockwise or counterclockwise
    private void rotate(boolean clockwise) {
        double beta = this.ROTATION_STEP;
        if(!clockwise){
            beta = -beta;
        }
        
        double cos = Math.cos(beta);
        double sin = Math.sin(beta);
        
        double new_ox = this.orientation_x * cos - this.orientation_y * sin;
        double new_oy = this.orientation_x * sin + this.orientation_y * cos;
        
        this.orientation_x = new_ox;
        this.orientation_y = new_oy;
    }
    
    // Move the bot forward, following its orientation
    private boolean move(Environment env) {
        // move forward
        Double2D current = env.field.getObjectLocationAsDouble2D(this);
        double new_x = current.getX() + this.STEPSIZE * this.orientation_x;
        double new_y = current.getY() - this.STEPSIZE * this.orientation_y;
        
        Double2D new_location = new Double2D(new_x, new_y);
        
        // Check if new location not obstructed by other bots
        Bag neighbors = env.field.getNeighborsExactlyWithinDistance(new_location, 1);
        neighbors.remove(this);
        if(neighbors.isEmpty()){
            env.field.setObjectLocation(this, new_location);
            this.localized = false;
            return true;
        }
        return false;
    }
    
    // Checks if the bot is currently in the given shape
    private boolean inside_shape(){
        int x = (int) Math.round(this.location.getX());
        int y = (int) Math.round(this.location.getY());
        return this.shape.inside(x, y);
    }
    
    // Checks if there are visible neighbors in a moving state
    private boolean no_moving_neighbors(Bag neighbors) {
        boolean moving = true;
        for(Object n : neighbors){
            BridgeBot neighbor = (BridgeBot) n;
            if(neighbor.isMoving() && !neighbor.equals(this)){
                moving = false;
            }
        }
        return moving;
    }
    
    // Find highest gradient among neighbors
    private int highest_gradient(Bag neighbors) {
        int highest = 0;
        for(Object n : neighbors){
            BridgeBot neighbor = (BridgeBot) n;
            if(neighbor.getGradient() > highest && !neighbor.has_joined_shape()){  // EXTENSION
                highest = neighbor.getGradient();
            }
        }
        return highest;
    }
    
    // Return a list with all neighbors with the same gradient
    private ArrayList<BridgeBot> find_same_gradient(Bag neighbors) {
        ArrayList<BridgeBot> same_grad = new ArrayList<BridgeBot>();
        for(Object n : neighbors){
            BridgeBot neighbor = (BridgeBot) n;
            if(neighbor.getGradient() == this.gradient && !neighbor.has_joined_shape()){  // EXTENSION
                same_grad.add(neighbor);
            }
        }
        return same_grad;
    }
    
    // Return the highest ID from a list of bots
    private int highest_id(ArrayList<BridgeBot> bots) {
        int highest = 0;
        for(BridgeBot n : bots){
            if(n.getID() > highest){
                highest = n.getID();
            }
        }
        return highest;
    }
    
    // Find the single nearest visible neighbor
    private BridgeBot nearest_neighbor(Double2D position, Bag neighbors, Environment env) {
        BridgeBot nearest = null;
        double shortest_dist = 0;
        for(Object n : neighbors){
            BridgeBot neighbor = (BridgeBot) n;
            if(neighbor.has_joined_shape()){
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
        }
        return nearest;
    }
    
    // Returns true if this bot is in a moving state
    private boolean isMoving() {
        return (this.state == State.MOVE_WHILE_INSIDE) || (this.state == State.MOVE_WHILE_OUTSIDE);
    }
    
    // EXTENSIONS FOR BRIDGE FORMATION
    // Check if the bridge can start dissolving
    private boolean startdissolving(Bag neighbors) {
        boolean start = false;
        if(this.ybridge){
            for(Object n : neighbors){
                BridgeBot neighbor = (BridgeBot) n;
                if(neighbor.isMoving()){
                    if(Math.signum(this.orientation_y) != Math.signum(this.orientation_y)){
                        start = true;
                    }
                }
            }
        }
        else if(this.xbridge){
            for(Object n : neighbors){
                BridgeBot neighbor = (BridgeBot) n;
                if(neighbor.isMoving()){
                    if(Math.signum(this.orientation_x) != Math.signum(this.orientation_x)){
                        start = true;
                    }
                }
            }
        }
        return start;
    }
    
    // Check if this bot can start moving again
    private boolean min_bridge_gradient(Bag neighbors) {
        boolean start = true;
        for(Object n : neighbors){
            BridgeBot neighbor = (BridgeBot) n;
            if(neighbor.has_joined_bridge()){
                if(neighbor.getGradient() < this.getGradient()){
                    start = false;
                }
            }
        }
        return start;
    }
            
    // Returns true if this bot is part of a bridge
    private boolean has_joined_bridge() {
        return this.state == State.JOINED_BRIDGE;
    }

    // Is this bot inside the area of a bridge?
    private boolean inside_bridge() {
        int x = (int) Math.round(this.location.getX());
        int y = (int) Math.round(this.location.getY());
        return this.shape.inside_bridge(x, y);
    }
    
    // What is the axis of this bridge?
    private boolean inside_ybridge() {
        int x = (int) Math.round(this.location.getX());
        int y = (int) Math.round(this.location.getY());
        return this.shape.inside_ybridge(x, y);
    }
}
