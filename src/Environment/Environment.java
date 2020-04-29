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

import Agents.Bot;
import Shape.Shape;
import sim.engine.*;
import ec.util.*;
import sim.field.continuous.Continuous2D;
import sim.util.Double2D;

public class Environment extends SimState{
    private static final long serialVersionUID = 1;
    
    private static double DISCRETIZATION = 1.0;
    private static double WIDTH = 10000.0;
    private static double HEIGHT = 10000.0;
    private static int NUMBOTS = 1000;
    private static int NUMSTEPS = 100000;
    private static int BOTSIZE = 1;
    private static int NRROWS = 25;
    private static int NRCOLS = 40;
    
    public Continuous2D field;
    
    public Environment(long seed) {
        super(seed);
        this.field = new Continuous2D(this.DISCRETIZATION, this.WIDTH, this.HEIGHT);
        
        // Define pattern to assemble
        Shape shape = new Shape();
        shape.rectangle(0, 0, 100, 100);
        
        // Add agents
        double stepsize = 2 * BOTSIZE;
        // Add SEED bots
        Bot seed1 = new Bot(true, 0.0, -1.0, new Double2D(0.0, 0.0), shape); 
        this.schedule.scheduleRepeating(seed1);
        this.field.setObjectLocation(seed1, new Double2D(0.0, 0.0));
        
        Bot seed2 = new Bot(true, 0.0, -1.0, new Double2D(1.0, 0.0), shape); 
        this.schedule.scheduleRepeating(seed2);
        this.field.setObjectLocation(seed2, new Double2D(1.0 * stepsize, 0.0));
        
        Bot seed3 = new Bot(true, 0.0, -1.0, new Double2D(0.0, 1.0), shape); 
        this.schedule.scheduleRepeating(seed3);
        this.field.setObjectLocation(seed3, new Double2D(0.0, 1.0 * stepsize));
        
        Bot seed4 = new Bot(true, 0.0, -1.0, new Double2D(0.0, -1.0), shape); 
        this.schedule.scheduleRepeating(seed4);
        this.field.setObjectLocation(seed4, new Double2D(0.0, -1.0 * stepsize));
        
        // Add bots to the environment
        Bot b;
        for(int row = 1 ; row <= NRROWS ; row++){
            for(int col = 1 ; col <= NRCOLS ; col++){
                b = new Bot(false, 0.0, -1.0, new Double2D(0.0, 0.0), shape); 
                this.schedule.scheduleRepeating(b);
                this.field.setObjectLocation(b, new Double2D((double)(col * stepsize), (double)(row * stepsize)));  //TODO: Place bots in a group
            }
        }
    }   
    
}
