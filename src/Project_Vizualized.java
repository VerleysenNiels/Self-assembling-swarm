/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Niels
 */

import Environment.Environment;

import sim.engine.*;
import sim.display.*;
import javax.swing.*;
import sim.portrayal.continuous.ContinuousPortrayal2D;

public class Project_Vizualized extends GUIState {
    
    public Project_Vizualized() {
        super(new Environment(System.currentTimeMillis())); 
    }

    public Project_Vizualized(SimState state) {
        super(state);
    }
    
    // Give a name and description to the simulation
    public static String getName() { return "Self assembling swarm"; }
    
    public static Object getInfo() {
        return "<H2>Self assembling swarm</H2>" + "<p>..."; 
    }
    
    // This class holds the visualization stuff, here add swing 2D display and add it to a displayframe
    public Display2D display;
    public JFrame displayFrame;
    
    // Portrayals know how to draw an object and can allow the user to manipulate it
    ContinuousPortrayal2D fieldPortrayal = new ContinuousPortrayal2D();
    
    public void setupPortrayals(){
        // tell the portrayals what to portray and how to portray them
        fieldPortrayal.setField(((Environment)state).field);  // Attach portrayal to grid
        fieldPortrayal.setDisplayingToroidally(true);
    }
    
    // START AND FINISH SIMULATION
    public void start() {
        super.start();      
        setupPortrayals();  // set up our portrayals
        this.scheduleRepeatingImmediatelyAfter(new RateAdjuster(30.0));
        display.reset();    // reschedule the displayer
        display.repaint();  // redraw the display
    }
    
    // OPEN AND CLOSE VISUALIZATION
    // Controller is responsible for running the simulation. The Controller calls the start() and finish() methods, and calls the GUIState's step() method
    public void init(Controller c){
        super.init(c);
        
        // Make the Display2D.  We'll have it display stuff later.
        Environment env = (Environment)state;
        display = new Display2D(750, 750,this)
            {
            public void quit()                                          // we close our controller when we die
                {
                super.quit();
                ((SimpleController) c).doClose();
                }
            };
        displayFrame = display.createFrame();
        c.registerFrame(displayFrame);   // register the frame so it appears in the "Display" list
        displayFrame.setVisible(true);

        display.attach(fieldPortrayal, "Swarm");  // attach the portrayals
    }
    
    // LAUNCH THE PROGRAM
    public static void main(String[] args){
        Project_Vizualized viz = new Project_Vizualized();
        viz.createController();        
    }
}
