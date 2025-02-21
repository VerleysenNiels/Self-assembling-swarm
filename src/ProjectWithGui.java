/*
 * @author Niels
 * 
 * This file contains the main code for running the project in a GUI.
 * It sets up the GUI and connects it to the simulation environment.
 * The play, pause and stop buttons in the GUI control the simulation process.
 * By pressing pause the play button changes into a next step button.
 * To resume the simulation as normal, press pause again.
 * When pressing the stop button the application needs to be run again.
 */

import Environment.Environment;
import sim.portrayal.continuous.*;
import sim.engine.*;
import sim.display.*;
import javax.swing.*;
import java.awt.Color;

public class ProjectWithGui extends GUIState{

    /**
     * @param args the command line arguments
     */
    
    // GUI is built up from multiple layers
    public Display2D display;
    public JFrame displayFrame;
    
    // Portrayal of the environment, which is a continuous field
    ContinuousPortrayal2D fieldPortrayal = new ContinuousPortrayal2D();
    
    // Start the application by creating a controller
    public static void main(String[] args) {
        new ProjectWithGui().createController();
    }
    
    // Standard constructors from MASON, either load the simulated environment or make a new one
    public ProjectWithGui(SimState state) {
        super(state);
    }
    
    public ProjectWithGui() {
        super(new Environment(System.currentTimeMillis()));
    }
    
    // Define displayed text in the GUI
    public static String getName() { return "Self assembling swarm"; }
    public static String getInfo() { return "<p>In this simulation a swarm of robots try to draw a given shape by moving around the group of robots that are not moving. The bots in this simulation have different colors based on their shape. Seed robots have a green color. Bots that have joined the state are red and those that have formed a bridge are cyan. The bots that are moving outside the shape are yellow and those that are moving inside the shape are orange. The other bots are waiting to move and are colored blue.</p> <p>Shape is determined by the file in Shapes/Using. Some example shapes are given in the Shapes folder, these can be used by replacing the file in Shapes/Using. You can also define your own shape with a textfile that contains the coordinates of the left bottom corner (usually \"1,1\") and then each following row marks the coordinates that are in the shape with X and outside with O. Each of these lines should be ended with an O and the final line should only contain O characters. When using bridges it is also possible to add these to the shape file. A bridge going up or down is marked by U characters and left or right by R characters. </p>"; }

    // Start the simulation
    public void start() {
        super.start();
        setupPortrayals();
        this.scheduleRepeatingImmediatelyAfter(new RateAdjuster(30.0));
    }
    
    // Load a simulation environment
    public void load(SimState state) {
        super.load(state);
        setupPortrayals();
        this.scheduleRepeatingImmediatelyAfter(new RateAdjuster(30.0));
    }
    
    // Setup of the portrayal of the environment
    public void setupPortrayals() {
        // Tell the portrayals what to portray and how to portray them
        fieldPortrayal.setField(((Environment)state).field);
                
        // Reschedule the displayer
        display.reset();
        display.setBackdrop(Color.white);
                
        // Redraw the display
        display.repaint();
    }
    
    // Create the controller, defining the display and connecting different layers
    public void init(Controller c) {
        super.init(c);

        // make the displayer
        display = new Display2D(900,900,this);

        displayFrame = display.createFrame();
        displayFrame.setTitle("Self Assembling Swarm");
        c.registerFrame(displayFrame);   // register the frame so it appears in the "Display" list
        displayFrame.setVisible(true);
        display.attach( fieldPortrayal, "Swarm" );
    }
    
    // Handles closing of the application
    public void quit() {
        super.quit();
        
        if (displayFrame!=null) displayFrame.dispose();
        displayFrame = null;
        display = null;
    }
}
