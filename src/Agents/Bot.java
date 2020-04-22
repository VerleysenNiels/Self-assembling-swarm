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
    
    private Boolean seed;
    private Boolean joined_shape = false;
    private int gradient;
    private int ID;
    private int MAXGRAD;
    private double VISIBLE_DIST;
    private boolean generated_ID = false;
    private int ID_SIZE;
    private Double2D location;
    private boolean localized = false;
    
    // Agent behaviour
    @Override
    public void step(SimState state) {
        // GET ENVIRONMENT STATE
        Environment env = (Environment)state;
        
        Double2D position = env.field.getObjectLocationAsDouble2D(this);
        Bag neighbors = env.field.getNeighborsWithinDistance(position, this.VISIBLE_DIST, false);
        
        // Use locally unique ID, remove this line when using globally unique ID
        this.check_id(neighbors, env);
        
        if(!this.joined_shape){
            this.gradient = this.gradient_formation(neighbors);
        }
    }
    
    // USED ALGORITHMS
    // Edge-following
    private void edge_follow() {
        
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
                if(neighbor.getJoined_shape() && neighbor.isLocalized()){
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

    public Boolean getJoined_shape() {
        return joined_shape;
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

    private boolean at_least_three_noncollinear(ArrayList<Bot> localized_n) {
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
       
}
