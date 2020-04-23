/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Shape;

import java.util.ArrayList;

/**
 *
 * @author Niels
 */
public class Shape {
    
    // Shape is defined by a grid of pixels that can be filled in or not
    // Representation is done in two steps:
    //      1) y_ranges defines the different ranges on the y-axis which have filled in pixels
    //      2) x_ranges contain for each y value the ranges on the x-axis of which the pixels at this y-value are filled
    // This allows any shape to be represented and makes it easy to determine if a given point is in the shape
    
    private ArrayList<int[]> y_ranges;
    private ArrayList<ArrayList<int[]>> x_ranges;

    // Constructs empty shape
    public Shape() {
        this.y_ranges = new ArrayList<int[]>();
        this.x_ranges = new ArrayList<ArrayList<int[]>>();
    }
    
    // Constructor with given ranges
    public Shape(ArrayList<int[]> y_ranges, ArrayList<ArrayList<int[]>> x_ranges) {
        this.y_ranges = y_ranges;
        this.x_ranges = x_ranges;
    }
    
    // Other constructors, maybe read from file or basic shapes
    
    // Determines if a given point is inside the shape
    public boolean inside(int x, int y){
        int y_index = -1;
        for(int[] range : this.y_ranges){
            if((y >= range[0]) && (y <= range[1])){
                y_index = y;
            }
        }
        if(y_index < 0){
            return false;
        }
        else{
            for(int[] range : this.x_ranges.get(y_index)){
                if((x >= range[0]) && (x <= range[1])){
                    return true;
                }
            }
        }
        return false;
    }
    
    // Maybe add ability to add basic shapes like rectangles or circles to the shape
    
}
