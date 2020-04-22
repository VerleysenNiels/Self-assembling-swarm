
import Agents.Bot;
import Environment.Environment;
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
    private static int NUMSTEPS = 10000;
    
    public static void main(String[] args) {
        
        // Init environment
        Environment env = new Environment(System.currentTimeMillis(), Project_Main.DISCRETIZATION, Project_Main.WIDTH, Project_Main.HEIGHT);
        
        // Define pattern to assemble
        
        
        // Add SEED bots
        
        
        // Add bots to the environment
        Bot b;
        for(int i=0 ; i < Project_Main.NUMBOTS ; i++)
            {
            b = new Bot(); 
            env.schedule.scheduleRepeating(b);
            env.field.setObjectLocation(b, new Double2D(0.0, 0.0));  //TODO: Place bots in a group
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
