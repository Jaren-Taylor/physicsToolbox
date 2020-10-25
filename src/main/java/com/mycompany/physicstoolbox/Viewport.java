package com.mycompany.physicstoolbox;

import com.mycompany.physicstoolbox.Substance.State;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
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
                        if(grid[y][x].containsSubstance()) {
                            if(shouldApplyWeight(x, y)) {
                                fallOnePixel(x, y, false);
                            }
                            
                            if(shouldApplyViscosity(x, y)) {
                                flowOnePixel(x, y);
                            }
                            
                            Substance[] touchingSubstances = getTouchingSubstances(x, y);
                            if(touchingSubstances != null) {
                                if(shouldApplyDensity(x, y, grid[y][x].getSubstance().getWeight() >= 0 ? touchingSubstances[0] : touchingSubstances[2])) {
                                    fallOnePixel(x, y, true);
                                }
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
            
            if(substance.getWeight() == 0) {
                return false;
            }
            long mappedWeightInterval = Math.round(FRAME_CYCLE / Math.abs(substance.getWeight()));
            
            boolean isLiquid = substance.getState() == State.LIQUID;
            boolean isNotFlush = (substance.getWeight() >= 0 && gridY + 1 < grid.length) || (substance.getWeight() < 0 && gridY - 1 >= 0);
            boolean isNotAtRest = isNotFlush && !grid[gridY + (substance.getWeight() >= 0 ? 1 : -1)][gridX].containsSubstance();
            boolean weightTimerReached = mappedWeightInterval <= clock && clock % mappedWeightInterval == 0;
            
            return isLiquid && weightTimerReached && isNotFlush && isNotAtRest;
        }
        
        // Checks that the probability of a viscous flow occurrence is met
        // Also checks that the substance is configured to be able to flow
        private boolean shouldApplyViscosity(int gridX, int gridY) {
            Substance substance = grid[gridY][gridX].getSubstance();
            Long mappedViscosityProb = Math.round((100 * Math.pow(substance.getViscosity(), 1.5)) + 1);
            
            boolean isNotSolid = substance.getState() != State.SOLID;
            boolean isFlowable = substance.getViscosity() < 1;
            boolean viscosityProbMet = rand.nextInt(mappedViscosityProb.intValue()) == 0;
            
            return isNotSolid && isFlowable && viscosityProbMet;
        }
        
        // Checks that the substance is beneath a substance with a higher density
        // Also checks that the time interval for applying the substance density has been reached
        private boolean shouldApplyDensity(int gridX, int gridY, Substance subOnTop) {
            Substance substance = grid[gridY][gridX].getSubstance();
            // No substances are above the current pixel, or the least dense substance is already on top
            // Also checks that the pixel to be swapped hasn't already been calculated
            if(subOnTop == null || subOnTop.getDensity() <= substance.getDensity() || grid[gridY + (substance.getWeight() >= 0 ? -1 : 1)][gridX].getClockSync() == clock) {
                return false;
            }
            // Gases cannot be trapped under liquids, so density will always be applied in this case
            if(substance.getState() == State.GAS && subOnTop.getState() == State.LIQUID) {
                return true;
            }
            
            long mappedDensityInterval = Math.round(FRAME_CYCLE / (substance.getDensity() - subOnTop.getDensity()));
            
            boolean areBothNotSolid = substance.getState() != State.SOLID && subOnTop.getState() != State.SOLID;
            boolean densityTimerReached = mappedDensityInterval <= clock && clock % mappedDensityInterval == 0;
            
            return areBothNotSolid && densityTimerReached;
        }
        
        // Swaps the current Pixel with the one above/below it to cause a one-pixel drop
        // Boolean mode: FALSE denotes weight, TRUE denotes density
        private void fallOnePixel(int gridX, int gridY, boolean mode) {
            Pixel temp = grid[gridY][gridX];
            
            // -1 denotes up, 1 denotes down
            int fallDirection;
            if(mode) {
                fallDirection = temp.getSubstance().getWeight() >= 0 ? -1 : 1;
            } else {
                fallDirection = temp.getSubstance().getWeight() >= 0 ? 1 : -1;
            }
            
            grid[gridY][gridX] = grid[gridY + fallDirection][gridX];
            grid[gridY + fallDirection][gridX] = temp;
            
            setClockSyncs(grid[gridY][gridX], grid[gridY + fallDirection][gridX]);
        }
        
        // Randomly determines a sideways direction to flow based on the current pixel's available choices
        private void flowOnePixel(int gridX, int gridY) {
            boolean leftOccupied = gridX - 1 < 0 || grid[gridY][gridX - 1].getSubstance().equals(grid[gridY][gridX].getSubstance());
            boolean rightOccupied = gridX + 1 >= grid[gridY].length || grid[gridY][gridX + 1].getSubstance().equals(grid[gridY][gridX].getSubstance());
            
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
            
            setClockSyncs(grid[gridY][gridX], grid[gridY][gridX + flowDirection]);
        }
        
        // Synchronizes the pixels involved in an interaction so they don't get recalculated in the same iteration
        private void setClockSyncs(Pixel p1, Pixel p2) {
            p1.setClockSync(clock);
            p2.setClockSync(clock);
        }
        
        // Indices in this array are mapped to the directions of the touching substances
        // 0: Above the substance in question
        // 1: Right of the substance in question
        // 2: Below the substance in question
        // 3: Left the substance in question
        private Substance[] getTouchingSubstances(int gridX, int gridY) {
            Substance substance = grid[gridY][gridX].getSubstance();
            
            Substance[] array = new Substance[4];
            array[0] = gridY - 1 >= 0 && grid[gridY - 1][gridX].containsSubstance() && !grid[gridY - 1][gridX].getSubstance().equals(substance)
                    ? grid[gridY - 1][gridX].getSubstance() : null;
            array[1] = gridX + 1 < grid[gridY].length && grid[gridY][gridX + 1].containsSubstance() && !grid[gridY][gridX + 1].getSubstance().equals(substance)
                    ? grid[gridY][gridX + 1].getSubstance() : null;
            array[2] = gridY + 1 < grid.length && grid[gridY + 1][gridX].containsSubstance() && !grid[gridY + 1][gridX].getSubstance().equals(substance)
                    ? grid[gridY + 1][gridX].getSubstance() : null;
            array[3] = gridX - 1 >= 0 && grid[gridY][gridX - 1].containsSubstance() && !grid[gridY][gridX - 1].getSubstance().equals(substance)
                    ? grid[gridY][gridX - 1].getSubstance() : null;
            
            return !Arrays.equals(array, new Substance[] {null, null, null, null}) ? array : null;
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
