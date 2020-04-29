
import Environment.Environment;

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
    private static int NUMSTEPS = 100000;
    
    public static void main(String[] args) {
        
        // Init environment
        Environment env = new Environment(System.currentTimeMillis());
        
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
