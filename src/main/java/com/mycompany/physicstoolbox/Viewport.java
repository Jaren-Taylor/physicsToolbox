package com.mycompany.physicstoolbox;

import java.awt.*;
import javax.swing.*;

// Viewport is a SINGLETON, meaning only one instance of it may exist.
// Use static method "getInstance(size)" to instantiate the Viewport.
public class Viewport extends JPanel {
    private static Viewport instance;
    
    public static Viewport getInstance(Dimension size) {
        if(size.width % 2 != 0 || size.height % 2 != 0) {
            throw new NumberFormatException("Dimensions must be even for pixel mapping.");
        }
        
        if(instance == null) {
            instance = new Viewport(size);
        }
        return instance;
    }
    
    private Color backgroundColor;
    private Pixel[][] grid;
    
    private final int PIXEL_GRID_WIDTH = 400;
    private final int PIXEL_GRID_HEIGHT = 300;
    
    private Viewport(Dimension size) {
        super();
        
        backgroundColor = new Color(0, 0, 0);
        grid = new Pixel[PIXEL_GRID_HEIGHT][PIXEL_GRID_WIDTH];
        
        Pixel.setRenderSize(size.width / PIXEL_GRID_WIDTH, size.height / PIXEL_GRID_HEIGHT);
        
        for(int y = 0; y < grid.length; y++) {
            for(int x = 0; x < grid[y].length; x++) {
                grid[y][x] = new Pixel(Substance.NONE, x, y);
            }
        }
        
        setPreferredSize(size);
        setBackground(backgroundColor);
    }
    
    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        
        Dimension gridLocation = new Dimension(0, 0);
        
        for(int y = 0; y < grid.length; y++) {
            for(int x = 0; x < grid[y].length; x++) {
                graphics.setColor(grid[y][x].getSubstance().getColor());
                graphics.fillRect(gridLocation.width, gridLocation.height, Pixel.getRenderSize().width, Pixel.getRenderSize().height);
                
                gridLocation.width += Pixel.getRenderSize().width;
            }
            
            gridLocation.width = 0;
            gridLocation.height += Pixel.getRenderSize().height;
        }
    }
}
