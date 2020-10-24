package com.mycompany.physicstoolbox;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;

// Viewport is a SINGLETON, meaning only one instance of it may exist.
// Use static method "getInstance(size)" to instantiate the Viewport.
public class Viewport extends JPanel {
    private static Viewport instance;
    
    private static final int PIXEL_GRID_WIDTH = 400;
    private static final int PIXEL_GRID_HEIGHT = 300;
    
    public static Viewport getInstance(Dimension size) {
        if(size.width % PIXEL_GRID_WIDTH != 0 || size.height % PIXEL_GRID_HEIGHT != 0) {
            throw new NumberFormatException("Dimensions must be divisible by (" + PIXEL_GRID_WIDTH + ", " + PIXEL_GRID_HEIGHT + ") for pixel mapping.");
        }
        
        if(instance == null) {
            instance = new Viewport(size);
        }
        return instance;
    }
    
    private final Timer TIMER = new Timer();
    private final int TIMER_SPEED = 1;
    private Updater updater = new Updater();
    
    private Color backgroundColor;
    private Pixel[][] grid;
    
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
        addMouseListener(new MouseListener());
        
        // Starts the timer that will run the update calculations at 60 FPS
        TIMER.scheduleAtFixedRate(updater, 0, TIMER_SPEED);
    }
    
    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        
        Point gridLocation = new Point(0, 0);
        for(int y = 0; y < grid.length; y++) {
            for(int x = 0; x < grid[y].length; x++) {
                graphics.setColor(grid[y][x].getSubstance().getColor());
                graphics.fillRect(gridLocation.x, gridLocation.y, Pixel.getRenderSize().width, Pixel.getRenderSize().height);
                
                gridLocation.x += Pixel.getRenderSize().width;
            }
            
            gridLocation.x = 0;
            gridLocation.y += Pixel.getRenderSize().height;
        }
    }
    
    // The class that contains all the information to update the viewport at 60 FPS
    private class Updater extends TimerTask {
        private Substance substance;
        
        // The smaller this number is, the faster the substances move in the viewport
        private final int FRAME_CYCLE = 8;
        private int clock;
        
        private boolean isMouseActive;
        
        public Updater() {
            super();
            updateSelectedSubstance();
            
            clock = 1;
            isMouseActive = false;
        }
        
        // Logic that executes each tick to run the update
        @Override
        public void run() {
            if(isMouseActive) {
                Point mousePixel = getMouseGridLocation();
                if(mousePixel != null && grid[mousePixel.y][mousePixel.x].getSubstance().equals(Substance.NONE)) {
                    grid[mousePixel.y][mousePixel.x].setSubstance(Substance.getCurrentlySelected());
                }
            }
            
            // Grid is traversed from bottom to top to avoid repeatedly recalculating falling substances
            for(int y = grid.length - 1; y >= 0; y--) {
                for(int x = grid[y].length - 1; x >= 0; x--) {
                    if(grid[y][x].containsSubstance() && shouldAdjustWeight(x, y)) {
                        fallOnePixel(x, y);
                    }
                }
            }
            
            clock++;
            if(clock == Long.MAX_VALUE) {
                clock = 1;
            }
            repaint();
        }
        
        public void updateSelectedSubstance() {
            substance = Substance.getCurrentlySelected();
        }
        
        public void updateMouseStatus(boolean status) {
            isMouseActive = status;
        }
        
        // Checks that the time interval for applying the substance weight has been reached
        // Also checks that the pixel below is unoccupied and is within the viewport bounds
        private boolean shouldAdjustWeight(int gridX, int gridY) {
            Pixel pixel = grid[gridY][gridX];
            long mappedWeightInterval = Math.round(FRAME_CYCLE / pixel.getSubstance().getWeight());
            
            boolean weightTimerReached = mappedWeightInterval <= clock && clock % mappedWeightInterval == 0;
            boolean isNotOnFloor = gridY + 1 < grid.length;
            boolean isNotAtRest = isNotOnFloor && !grid[gridY + 1][gridX].containsSubstance();
            
            return weightTimerReached && isNotOnFloor && isNotAtRest;
        }
        
        // Swaps the current Pixel with the one below it to cause a one-pixel drop
        private void fallOnePixel(int gridX, int gridY) {
            Pixel temp = grid[gridY][gridX];
            grid[gridY][gridX] = grid[gridY + 1][gridX];
            grid[gridY + 1][gridX] = temp;
        }
        
        // Gets cursor coordinates in terms of Pixel grid indices
        private Point getMouseGridLocation() {
            Point mouse = getMousePosition();
            return mouse == null ? null : new Point(Math.floorDiv(mouse.x, Pixel.getRenderSize().width), Math.floorDiv(mouse.y, Pixel.getRenderSize().height));
        }
    }
    
    private class MouseListener extends MouseAdapter {
        public MouseListener() {
            super();
        }
        
        @Override
        public void mousePressed(MouseEvent e) {
            updater.updateMouseStatus(true);
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {
            updater.updateMouseStatus(false);
        }
    }
}
