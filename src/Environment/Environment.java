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
import sim.field.continuous.Continuous2D;
import sim.util.Double2D;

public class Environment extends SimState{
    private static final long serialVersionUID = 1;
    
    private static double DISCRETIZATION = 1.0;
    private static double WIDTH = 900.0;
    private static double HEIGHT = 900.0;
    private static int NUMBOTS = 100;
    private static int NUMSTEPS = 100000;
    private static int BOTSIZE = 10;
    private static int NRROWS = 10; // 25;
    private static int NRCOLS = 10; // 40;
    private static double middlex = 400;
    private static double middley = 700;
    
    public Continuous2D field;
    
    public Environment(long seed) {
        super(seed);
        this.field = new Continuous2D(this.DISCRETIZATION, this.WIDTH, this.HEIGHT);
    }   
    
    public void start() {
        super.start();  // very important!  This resets and cleans out the Schedule.
        // Define pattern to assemble
        Shape shape = new Shape();
        shape.rectangle(1, 1, 5, 20);
        
        // Add agents
        double stepsize = 2 * BOTSIZE;
        // Add SEED bots
        Bot seed1 = new Bot(true, true, 0.0, -1.0, new Double2D(-0.5, 0.1), shape); 
        this.schedule.scheduleRepeating(seed1);
        this.field.setObjectLocation(seed1, new Double2D((-0.5 * this.BOTSIZE) + middlex, (-0.1 * this.BOTSIZE) + middley));
        
        Bot seed2 = new Bot(true, false, 0.0, -1.0, new Double2D(0.5, 0.0), shape); 
        this.schedule.scheduleRepeating(seed2);
        this.field.setObjectLocation(seed2, new Double2D((0.5 * this.BOTSIZE) + middlex, 0.0 + middley));
        
        Bot seed3 = new Bot(true, false, 0.0, -1.0, new Double2D(0.1, 1.0), shape); 
        this.schedule.scheduleRepeating(seed3);
        this.field.setObjectLocation(seed3, new Double2D((0.1 * this.BOTSIZE) + middlex, (-1.0 * this.BOTSIZE) + middley));
        
        Bot seed4 = new Bot(true, false, 0.0, -1.0, new Double2D(-0.02, -1.0), shape); 
        this.schedule.scheduleRepeating(seed4);
        this.field.setObjectLocation(seed4, new Double2D((-0.02 * this.BOTSIZE) + middlex, (1.0 * this.BOTSIZE) + middley));
        
        // Add bots to the environment
        Bot b;
        for(int row = 1 ; row <= NRROWS ; row++){
            for(int col = 1 ; col <= NRCOLS ; col++){
                b = new Bot(false, false, 0.0, -1.0, new Double2D(0.0, 0.0), shape); 
                this.schedule.scheduleRepeating(b);
                this.field.setObjectLocation(b, new Double2D((double)(col * this.BOTSIZE + middlex), (double)(row * this.BOTSIZE  + middley))); 
            }
        }
    }
    
    public static void main(String[] args) {
        doLoop(Environment.class, args);
        System.exit(0);
    }    
}
