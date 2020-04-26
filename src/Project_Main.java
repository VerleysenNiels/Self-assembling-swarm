
import Agents.Bot;
import Environment.Environment;
import Shape.Shape;
import sim.util.Double2D;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Niels
 */
public class Project_Main {

    /**
     * @param args the command line arguments
     */
    
    private static double DISCRETIZATION = 1.0;
    private static double WIDTH = 10000.0;
    private static double HEIGHT = 10000.0;
    private static int NUMBOTS = 1000;
    private static int NUMSTEPS = 100000;
    private static int BOTSIZE = 1;
    private static int NRROWS = 25;
    private static int NRCOLS = 40;
    
    public static void main(String[] args) {
        
        // Init environment
        Environment env = new Environment(System.currentTimeMillis(), Project_Main.DISCRETIZATION, Project_Main.WIDTH, Project_Main.HEIGHT);
        
        // Define pattern to assemble
        Shape shape = null;
        
        // Add agents
        double stepsize = 2 * BOTSIZE;
        // Add SEED bots
        Bot seed1 = new Bot(true, 0.0, -1.0, new Double2D(-1.0, 0.0), shape); 
        env.schedule.scheduleRepeating(seed1);
        env.field.setObjectLocation(seed1, new Double2D(0.0, 0.0));
        
        Bot seed2 = new Bot(true, 0.0, -1.0, new Double2D(1.0, 0.0), shape); 
        env.schedule.scheduleRepeating(seed2);
        env.field.setObjectLocation(seed2, new Double2D(2.0 * stepsize, 0.0));
        
        Bot seed3 = new Bot(true, 0.0, -1.0, new Double2D(0.0, 1.0), shape); 
        env.schedule.scheduleRepeating(seed3);
        env.field.setObjectLocation(seed3, new Double2D(1.0 * stepsize, 1.0 * stepsize));
        
        Bot seed4 = new Bot(true, 0.0, -1.0, new Double2D(0.0, -1.0), shape); 
        env.schedule.scheduleRepeating(seed4);
        env.field.setObjectLocation(seed4, new Double2D(1.0 * stepsize, -1.0 * stepsize));
        
        // Add bots to the environment
        Bot b;
        for(int row = 1 ; row <= NRROWS ; row++){
            for(int col = 1 ; col <= NRCOLS ; col++){
                b = new Bot(false, 0.0, -1.0, new Double2D(0.0, 0.0), shape); 
                env.schedule.scheduleRepeating(b);
                env.field.setObjectLocation(b, new Double2D((double)(col * stepsize), (double)(row * stepsize)));  //TODO: Place bots in a group
            }
        }
        
        // Run simulation
        long steps;
        do {
            if (!env.schedule.step(env))
                break;
            steps = env.schedule.getSteps();
            if (steps % 500 == 0)
                System.out.println("Steps: " + steps + " Time: " + env.schedule.getTime());
        }
        while(steps < NUMSTEPS);
        env.finish();
        System.exit(0);  // make sure any threads finish up
    }
    
}
