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

public class Bot implements Steppable{
    private static final long serialVersionUID = 1;
    
    private Boolean seed;
    private Boolean joined_shape = false;
    
    // Agent behaviour
    @Override
    public void step(SimState state) {
        // GET ENVIRONMENT STATE
        Environment env = (Environment)state;
    }
    
    // USED ALGORITHMS
    // Edge-following
    private void edge_follow() {
        
    }
    
    // Gradient formation
    private int gradient_formation() {
        if(seed){
          return 0;  
        }
        else{
            // For all neighbours with a lower distance than a given distance
            // Get smallest gradient
            // Own gradient = smallest gradient + 1
            return 0;
        }
    }
    
    // Localization
    private void localization() {
        
    }
    
    // Check ID if not locally unique -> make it unique
    private void check_id() {
        
    }
}
