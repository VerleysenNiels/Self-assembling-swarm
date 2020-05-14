/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Shape;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
    
    // Construct shape from file, this is slow
    public Shape(String filename) {
        this.y_ranges = new ArrayList<int[]>();
        this.x_ranges = new ArrayList<ArrayList<int[]>>();
        

        // This will reference one line at a time
        String line = null;

        try {
            // Init FileReader
            FileReader fileReader = new FileReader(filename);

            // Wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            
            // Determine startpoint
            line = bufferedReader.readLine();
            int startx = Integer.parseInt(line.split(",")[0]);
            int y = Integer.parseInt(line.split(",")[1]);
            
            // Update xranges if y is not zero
            int i = 0;
            while(i < y){
                ArrayList<int[]> l = new ArrayList<int[]>();
                this.x_ranges.add(l);
                i++;
            }
            
            // Variables that keep information of a range over multiple rows
            boolean yrange = false;
            int yrange_start = 0;
            
            // Read file
            while((line = bufferedReader.readLine()) != null) {
                
                // New xranges
                ArrayList<int[]> l = new ArrayList<int[]>();
                
                boolean xrange = false;
                boolean values = false;
                int xrange_start = 0;               
                
                for (i=0; i < line.length(); i++){
                    if((!xrange) && (line.charAt(i) == 'X')){
                        // Range starts
                        xrange_start = i + startx;
                        xrange = true;
                        values = true;
                    }
                    else if(xrange && (line.charAt(i) == 'O')){ //Always bound range with an O -> also on the end of a row and on the bottom row
                        // Range ends
                        l.add(new int[]{xrange_start, i + startx - 1});
                        xrange = false;
                    }                   
                }
                
                // Add list to x_ranges
                this.x_ranges.add(l);
                
                // If this row has values -> add to y_ranges
                if(!yrange && values){
                    // Range starts
                    yrange_start = y;
                    yrange = true;
                }
                else if(yrange && !values){
                    // Range ends
                    this.y_ranges.add(new int[]{yrange_start, y});
                    yrange = false;
                }
                
                // Move up one row
                y++;
            }   

            // Close file
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + filename + "'");                
        }
        catch(IOException ex) {
            System.out.println("Error reading file '" + filename + "'");                  
            ex.printStackTrace();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
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
    
    // Initialize shape as a rectangle
    public void rectangle(int cornerx, int cornery, int sizex, int sizey){
        this.y_ranges = new ArrayList<int[]>();
        this.x_ranges = new ArrayList<ArrayList<int[]>>();
        
        this.y_ranges.add(new int[]{cornery, (cornery+sizey)});
        int index = 0;
        while(index < cornery){
            this.x_ranges.add(new ArrayList<int[]>());
            index++;
        }
        for(int i = 0; i <= sizey; i++){
            ArrayList<int[]> l = new ArrayList<int[]>();
            l.add(new int[]{cornerx, (cornerx+sizex)});
            this.x_ranges.add(l);
        }
    }
}
