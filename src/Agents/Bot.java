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
import sim.engine.*;
import sim.util.Bag;
import sim.util.Double2D;

public class Bot implements Steppable{
    private static final long serialVersionUID = 1;
    
    private Boolean seed;
    private Boolean joined_shape = false;
    private int gradient;
    private int MAXGRAD;
    private double VISIBLE_DIST;
    
    // Agent behaviour
    @Override
    public void step(SimState state) {
        // GET ENVIRONMENT STATE
        Environment env = (Environment)state;
        
        Double2D position = env.field.getObjectLocationAsDouble2D(this);
        Bag neighbors = env.field.getNeighborsWithinDistance(position, this.VISIBLE_DIST, false);
        
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
    private void localization() {
        
    }
    
    // Check ID if not locally unique -> make it unique
    private void check_id() {
        
    }
    
    // Getters

    public Boolean getJoined_shape() {
        return joined_shape;
    }

    public int getGradient() {
        return gradient;
    }
    
}
