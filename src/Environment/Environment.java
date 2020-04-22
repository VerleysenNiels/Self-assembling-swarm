/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Environment;

/**
 *
 * @author Niels
 */

import sim.engine.*;
import ec.util.*;
import sim.field.continuous.Continuous2D;

public class Environment extends SimState{
    private static final long serialVersionUID = 1;
    
    public Continuous2D field;
    
    public Environment(long seed, double discr, double width, double height) {
        super(seed);
        this.field = new Continuous2D(discr, width, height);
    }
    
    
    
}
