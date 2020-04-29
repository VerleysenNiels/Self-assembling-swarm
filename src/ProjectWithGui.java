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
import sim.portrayal.continuous.*;
import sim.engine.*;
import sim.display.*;
import javax.swing.*;
import java.awt.Color;

public class ProjectWithGui extends GUIState{

    /**
     * @param args the command line arguments
     */
    
    public Display2D display;
    public JFrame displayFrame;

    ContinuousPortrayal2D fieldPortrayal = new ContinuousPortrayal2D();
    
    public static void main(String[] args) {
        new ProjectWithGui().createController();
    }

    public ProjectWithGui(SimState state) {
        super(state);
    }
    
    public ProjectWithGui() {
        super(new Environment(System.currentTimeMillis()));
    }
    
    public static String getName() { return "Woims"; }

    public void start() {
        super.start();
        setupPortrayals();
        this.scheduleRepeatingImmediatelyAfter(new RateAdjuster(30.0));
    }

    public void load(SimState state) {
        super.load(state);
        setupPortrayals();
        this.scheduleRepeatingImmediatelyAfter(new RateAdjuster(30.0));
    }
        
    public void setupPortrayals() {
        // tell the portrayals what to portray and how to portray them
        fieldPortrayal.setField(((Environment)state).field);
                
        // reschedule the displayer
        display.reset();
        display.setBackdrop(Color.white);
                
        // redraw the display
        display.repaint();
    }
    
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
    
    public void quit() {
        super.quit();
        
        if (displayFrame!=null) displayFrame.dispose();
        displayFrame = null;
        display = null;
    }
}
