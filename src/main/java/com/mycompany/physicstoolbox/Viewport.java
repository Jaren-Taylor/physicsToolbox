package com.mycompany.physicstoolbox;

import com.mycompany.physicstoolbox.Substance.State;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;
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
        private Random rand;
        
        // 0 = Not clicked
        // 1 = Left click
        // 2 = Right click
        private int mouseStatus;
        
        public Updater() {
            super();
            updateSelectedSubstance();
            
            clock = 1;
            rand = new Random();
            mouseStatus = 0;
        }
        
        // Logic that executes each tick to run the update
        @Override
        public void run() {
            if(mouseStatus == 1) {
                Point mousePixel = getMouseGridLocation();
                if(mousePixel != null && grid[mousePixel.y][mousePixel.x].getSubstance().equals(Substance.NONE)) {
                    grid[mousePixel.y][mousePixel.x].setSubstance(Substance.getCurrentlySelected());
                }
            }
            if(mouseStatus == 2) {
                Point mousePixel = getMouseGridLocation();
                if(mousePixel != null && !grid[mousePixel.y][mousePixel.x].getSubstance().equals(Substance.NONE)) {
                    grid[mousePixel.y][mousePixel.x].setSubstance(Substance.NONE);
                }
            }
            
            for(int y = 0; y < grid.length; y++) {
                for(int x = 0; x < grid[y].length; x++) {
                    // Ensures that no single pixel of substance is calculated more than once during the iteration
                    if(grid[y][x].getClockSync() != clock) {
                        grid[y][x].setClockSync(clock);
                        if(grid[y][x].containsSubstance()) {
                            if(shouldApplyWeight(x, y)) {
                                fallOnePixel(x, y);
                            }
                            
                            if(shouldApplyViscosity(x, y)) {
                                flowOnePixel(x, y);
                            }
                        }
                    }
                }
            }
            
            clock++;
            if(clock == Integer.MAX_VALUE) {
                clock = 1;
            }
            repaint();
        }
        
        public void updateSelectedSubstance() {
            substance = Substance.getCurrentlySelected();
        }
        
        public void updateMouseStatus(int status) {
            mouseStatus = status;
        }
        
        // Checks that the time interval for applying the substance weight has been reached
        // Also checks that the pixel below is unoccupied and is within the viewport bounds
        private boolean shouldApplyWeight(int gridX, int gridY) {
            Substance substance = grid[gridY][gridX].getSubstance();
            // If the substance weight is 0, become a value that will ensure that the weight application is bypassed
            long mappedWeightInterval = substance.getWeight() == 0 ? clock + 1 : Math.round(FRAME_CYCLE / Math.abs(substance.getWeight()));
            
            boolean isLiquid = substance.getState() == State.LIQUID;
            boolean isNotFlush = (substance.getWeight() >= 0 && gridY + 1 < grid.length) || (substance.getWeight() < 0 && gridY - 1 >= 0);
            boolean isNotAtRest = isNotFlush && !grid[gridY + (substance.getWeight() >= 0 ? 1 : -1)][gridX].containsSubstance();
            boolean weightTimerReached = mappedWeightInterval <= clock && clock % mappedWeightInterval == 0;
            
            return isLiquid && weightTimerReached && isNotFlush && isNotAtRest;
        }
        
        // Checks that the probability of a viscous flow occurrence is met
        // Also checks that the substance is configured to be able to flow
        private boolean shouldApplyViscosity(int gridX, int gridY) {
            Pixel pixel = grid[gridY][gridX];
            Long mappedViscosityProb = Math.round((100 * pixel.getSubstance().getViscosity()) + 1);
            
            boolean isNotSolid = pixel.getSubstance().getState() != State.SOLID;
            boolean isFlowable = pixel.getSubstance().getViscosity() < 1;
            boolean viscosityProbMet = rand.nextInt(mappedViscosityProb.intValue()) == 0;
            
            return isNotSolid && isFlowable && viscosityProbMet;
        }
        
        // Swaps the current Pixel with the one above/below it to cause a one-pixel drop
        private void fallOnePixel(int gridX, int gridY) {
            Pixel temp = grid[gridY][gridX];
            
            // -1 denotes up, 1 denotes down
            int fallDirection = temp.getSubstance().getWeight() >= 0 ? 1 : -1;
            
            grid[gridY][gridX] = grid[gridY + fallDirection][gridX];
            grid[gridY + fallDirection][gridX] = temp;
        }
        
        // Randomly determines a sideways direction to flow based on the current pixel's available choices
        private void flowOnePixel(int gridX, int gridY) {
            boolean leftOccupied = gridX - 1 < 0 || grid[gridY][gridX - 1].containsSubstance();
            boolean rightOccupied = gridX + 1 >= grid[gridY].length || grid[gridY][gridX + 1].containsSubstance();
            
            // -1 denotes left, 1 denotes right
            int flowDirection;
            if(leftOccupied && rightOccupied) {
                return;
            } else if(!leftOccupied && rightOccupied) {
                flowDirection = -1;
            } else if(leftOccupied && !rightOccupied) {
                flowDirection = 1;
            } else {
                flowDirection = !rand.nextBoolean() ? -1 : 1;
            }
            
            // Applying the corresponding flow
            Pixel temp = grid[gridY][gridX];
            grid[gridY][gridX] = grid[gridY][gridX + flowDirection];
            grid[gridY][gridX + flowDirection] = temp;
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
            int status = 0;
            if(e.getButton() == MouseEvent.BUTTON1) {
                status = 1;
            } else if(e.getButton() == MouseEvent.BUTTON3) {
                status = 2;
            }
            updater.updateMouseStatus(status);
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {
            updater.updateMouseStatus(0);
        }
    }
}
