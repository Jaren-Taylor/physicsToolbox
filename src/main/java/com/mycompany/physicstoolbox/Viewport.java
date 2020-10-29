package com.mycompany.physicstoolbox;

import com.mycompany.physicstoolbox.Substance.State;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.List;
import java.util.ArrayList;
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
    private int brushSize;
    
    private Viewport(Dimension size) {
        super();
        backgroundColor = new Color(0, 0, 0);
        grid = new Pixel[PIXEL_GRID_HEIGHT][PIXEL_GRID_WIDTH];
        brushSize = 4;
        
        Pixel.setRenderSize(size.width / PIXEL_GRID_WIDTH, size.height / PIXEL_GRID_HEIGHT);
        
        for(int y = 0; y < grid.length; y++) {
            for(int x = 0; x < grid[y].length; x++) {
                grid[y][x] = new Pixel(Substance.NONE, x, y);
            }
        }
        
        setPreferredSize(size);
        setBackground(backgroundColor);
        
        MouseListener listener = new MouseListener();
        addMouseListener(listener);
        addMouseWheelListener(listener);
        
        // Starts the timer that will run the update calculations at 60 FPS
        TIMER.scheduleAtFixedRate(updater, 0, TIMER_SPEED);
    }
    
    public void setBackgroundColor(Color c) {
        backgroundColor = c;
        setBackground(c);
    }
    
    public void resetViewport() {
        for(Pixel[] y : grid) {
            for(Pixel xy : y) {
                xy.setSubstance(Substance.NONE);
            }
        }
    }
    
    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        
        Point gridLocation = new Point(0, 0);
        for(Pixel[] y : grid) {
            for(Pixel xy : y) {
                graphics.setColor(xy.getSubstance().getColor());
                graphics.fillRect(gridLocation.x, gridLocation.y, Pixel.getRenderSize().width, Pixel.getRenderSize().height);
                gridLocation.x += Pixel.getRenderSize().width;
            }
            gridLocation.x = 0;
            gridLocation.y += Pixel.getRenderSize().height;
        }
        
        Point mouse = getMousePosition();
        if(mouse != null) {
            int cursorR = backgroundColor.getRed() - 127;
            int cursorG = backgroundColor.getGreen() - 127;
            int cursorB = backgroundColor.getBlue() - 127;
            
            graphics.setColor(new Color(128 - cursorR, 128 - cursorG, 128 - cursorB));
            graphics.drawOval(mouse.x - (brushSize * Pixel.getRenderSize().width),
                              mouse.y - (brushSize * Pixel.getRenderSize().height),
                              brushSize * 2 * Pixel.getRenderSize().width,
                              brushSize * 2 * Pixel.getRenderSize().height);
        }
    }
    
    // The class that contains all the information to update the viewport at 60 FPS
    private class Updater extends TimerTask {
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
            
            clock = 1;
            rand = new Random();
            mouseStatus = 0;
        }
        
        public void updateMouseStatus(int status) {
            mouseStatus = status;
        }
        
        public void debugClick(int status) {
            Substance[] debugs = Substance.getDebugSubstances();
            int index = 0;
            int altIndex = 0;
            for(int i = 0; i < debugs.length; i++) {
                if(debugs[i].equals(Substance.getCurrentlySelected())) {
                    index = i;
                }
            }
            for(int i = 0; i < debugs.length; i++) {
                if(debugs[i].equals(Substance.getAlternateSelected())) {
                    altIndex = i;
                }
            }
            
            if(status == 3) {
                Substance.setCurrentlySelected(index == debugs.length - 1 ? debugs[0] : debugs[index + 1]);
            } else if(status == 4) {
                Substance.setAlternateSelected(altIndex == debugs.length - 1 ? debugs[0] : debugs[altIndex + 1]);
            }
        }
        
        // Logic that executes each tick to run the update
        @Override
        public void run() {
            if(mouseStatus == 1) {
                Pixel[] pixels = getPixelsInBrush();
                if(pixels != null) {
                    for(Pixel p: pixels) {
                        if(p.getSubstance().equals(Substance.NONE)) {
                            p.setSubstance(Substance.getCurrentlySelected());
                        }
                    }
                }
            }
            if(mouseStatus == 2) {
                Pixel[] pixels = getPixelsInBrush();
                if(pixels != null) {
                    for(Pixel p: pixels) {
                        if(p.getSubstance().equals(Substance.NONE)) {
                            p.setSubstance(Substance.getAlternateSelected());
                        }
                    }
                }
            }
            
            for(int y = 0; y < grid.length; y++) {
                for(int x = 0; x < grid[y].length; x++) {
                    // Ensures that no single pixel of substance is calculated more than once during the iteration
                    if(grid[y][x].getClockSync() != clock) {
                        if(grid[y][x].containsSubstance()) {
                            Substance substance = grid[y][x].getSubstance();
                            Pixel[] neighbors = getNeighborPixels(x, y);
                            Substance[] touchingSubstances = getTouchingSubstances(neighbors, substance);
                            
                            if(touchingSubstances != null) {
                                SubstanceInteraction interaction;
                                for(int s = 0; s < touchingSubstances.length; s++) {
                                    if(touchingSubstances[s] != null) {
                                        interaction = shouldApplyInteraction(substance, touchingSubstances[s]);
                                        if(interaction != null) {
                                            interact(interaction, grid[y][x], neighbors[s]);
                                        }
                                    }
                                }
                                
                                if(substance.getState() != State.SOLID && shouldApplyDensity(substance, neighbors[0], neighbors[2])) {
                                    fallOnePixel(grid[y][x], neighbors[0], neighbors[2], true);
                                }
                            }
                            
                            // Different substance states should act differently
                            // Solids do not fall nor flow in any way, so no behavior is defined for them
                            if(substance.getState() == State.GAS) {
                                if(shouldApplyGas(substance)) {
                                    boolean direction = rand.nextBoolean();
                                    flowOnePixel(grid[y][x], direction ? neighbors[3] : neighbors[0], direction ? neighbors[1] : neighbors[2]);
                                }
                            } else if(substance.getState() == State.LIQUID) {
                                if(shouldApplyWeight(substance, neighbors[0], neighbors[2])) {
                                    fallOnePixel(grid[y][x], neighbors[0], neighbors[2], false);
                                }

                                if(shouldApplyViscosity(substance)) {
                                    flowOnePixel(grid[y][x], neighbors[3], neighbors[1]);
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
        
        // Checks that the time interval for applying the substance weight has been reached
        // Also checks that the pixel above/below is unoccupied and is within the viewport bounds
        private boolean shouldApplyWeight(Substance sub, Pixel top, Pixel bottom) {
            // Substance has no weight
            if(sub.getWeight() == 0) {
                return false;
            }
            // Substance is against the top/bottom edge of the viewport
            if((sub.getWeight() > 0 && bottom == null) || (sub.getWeight() < 0 && top == null)) {
                return false;
            }
            // Substance is against another substance
            if((sub.getWeight() > 0 ? bottom : top).containsSubstance()) {
                return false;
            }
            
            long mappedWeightInterval = Math.round(FRAME_CYCLE / Math.abs(sub.getWeight()));
            
            return mappedWeightInterval <= clock && clock % mappedWeightInterval == 0;
        }
        
        // Checks that the probability of a viscous flow occurrence is met
        // Also checks that the substance is not rigid (has perfect viscosity)
        private boolean shouldApplyViscosity(Substance sub) {
            if(sub.getViscosity() == 1) {
                return false;
            }
            
            Long mappedViscosityProb = Math.round((100 * Math.pow(sub.getViscosity(), 2)) + 1);
            
            return rand.nextInt(mappedViscosityProb.intValue()) == 0;
        }
        
        // Checks that the substance is beneath a substance with a higher density
        // Also checks that the time interval for applying the substance density has been reached
        private boolean shouldApplyDensity(Substance sub, Pixel top, Pixel bottom) {
            Pixel neighbor = sub.getWeight() >= 0 ? top : bottom;
            // No substances are above the current pixel, or the least dense substance is already on top
            // Also checks that the pixel to be swapped hasn't already been calculated
            if(neighbor == null || !neighbor.containsSubstance() || neighbor.getSubstance().getDensity() <= sub.getDensity() || neighbor.getClockSync() == clock) {
                return false;
            }
            // Gases cannot be trapped under liquids, and solids cannot be displaced
            if(neighbor.getSubstance().getState() != State.LIQUID) {
                return false;
            }
            
            long mappedDensityInterval = Math.round(FRAME_CYCLE / (sub.getDensity() - neighbor.getSubstance().getDensity()));
            
            return mappedDensityInterval <= clock && clock % mappedDensityInterval == 0;
        }
        
        // Gaseous flow is based solely on density
        private boolean shouldApplyGas(Substance sub) {
            Long mappedGasProb = Math.round((100 * Math.pow(sub.getDensity(), 1.5)) + 1);
            return rand.nextInt(mappedGasProb.intValue()) == 0;
        }
        
        // Checks that two touching substances have an interaction defined between them
        // Also checks that the time interval for applying the interaction has been reached
        // Returns the interaction object to be used in the interact() method if one should occur
        private SubstanceInteraction shouldApplyInteraction(Substance src, Substance rct) {
            SubstanceInteraction interaction = null;
            // Check source reaction list
            for(SubstanceInteraction i: src.getReactions()) {
                if(i.getReactant().equals(rct)) {
                    interaction = i;
                }
            }
            
            // The touching substances are not set to react
            if(interaction == null) {
                return null;
            }
            
            Long mappedInteractionProb = Math.round((50000 * Math.pow(1 - interaction.getVolatility(), 2)) + 1);
            
            return rand.nextInt(mappedInteractionProb.intValue()) == 0 ? interaction : null;
        }
        
        // Swaps the current Pixel with the one above/below it based on substance weight/density
        // Boolean mode: FALSE denotes weight, TRUE denotes density
        private void fallOnePixel(Pixel main, Pixel top, Pixel bottom, boolean mode) {
            Substance sub = main.getSubstance();
            
            Pixel neighbor;
            if(mode) {
                neighbor = sub.getWeight() >= 0 ? top : bottom;
            } else {
                neighbor = sub.getWeight() >= 0 ? bottom : top;
            }
            
            if(neighbor == null) {
                return;
            }
            
            // Applying the corresponding drop by flipping the pixels
            Point m = main.getGridLocation();
            Point n = neighbor.getGridLocation();
            
            grid[m.y][m.x] = neighbor;
            grid[n.y][n.x] = main;
            
            main.setGridLocation(n.x, n.y);
            neighbor.setGridLocation(m.x, m.y);
            
            setClockSyncs(main, neighbor);
        }
        
        // Randomly determines a direction to flow in a single axis based on the current pixel's available choices
        // Gases will randomly select an axis on which to flow, while liquids may only flow horizontally
        private void flowOnePixel(Pixel main, Pixel first, Pixel second) {
            Substance sub = main.getSubstance();
            Substance firstSub = first == null ? null : first.getSubstance();
            Substance secondSub = second == null ? null : second.getSubstance();
            
            boolean firstOccupied = firstSub == null || firstSub.equals(sub) || firstSub.getState() == State.SOLID;
            boolean secondOccupied = secondSub == null || secondSub.equals(sub) || secondSub.getState() == State.SOLID;
            
            Pixel neighbor;
            if(firstOccupied && secondOccupied) {
                return;
            } else if(!firstOccupied && secondOccupied) {
                neighbor = first;
            } else if(firstOccupied && !secondOccupied) {
                neighbor = second;
            } else {
                neighbor = !rand.nextBoolean() ? first : second;
            }
            
            if(neighbor == null) {
                return;
            }
            
            // Applying the corresponding flow by flipping the pixels
            Point m = main.getGridLocation();
            Point n = neighbor.getGridLocation();
            
            grid[m.y][m.x] = neighbor;
            grid[n.y][n.x] = main;
            
            main.setGridLocation(n.x, n.y);
            neighbor.setGridLocation(m.x, m.y);
            
            setClockSyncs(main, neighbor);
        }
        
        private void interact(SubstanceInteraction interaction, Pixel main, Pixel reactant) {
            switch(interaction.getSourceOutcome()) {
                case UNCHANGED ->  {
                }
                case CHANGED ->  {
                    main.setSubstance(interaction.getProduct());
                }
                case DESTROYED ->  {
                    main.setSubstance(Substance.NONE);
                }
            }
            
            switch(interaction.getReactantOutcome()) {
                case UNCHANGED ->  {
                }
                case CHANGED ->  {
                    reactant.setSubstance(interaction.getProduct());
                }
                case DESTROYED ->  {
                    reactant.setSubstance(Substance.NONE);
                }
            }
            
            setClockSyncs(main, reactant);
        }
        
        // Synchronizes the pixels involved in an interaction so they don't get recalculated in the same iteration
        private void setClockSyncs(Pixel p1, Pixel p2) {
            p1.setClockSync(clock);
            p2.setClockSync(clock);
        }
        
        // Indices in this array are mapped to the directions of the neighboring pixels
        // 0: Above the substance in question
        // 1: Right of the substance in question
        // 2: Below the substance in question
        // 3: Left the substance in question
        private Pixel[] getNeighborPixels(int gridX, int gridY) {
            Pixel[] array = new Pixel[4];
            
            array[0] = gridY - 1 >= 0 ? grid[gridY - 1][gridX] : null;
            array[1] = gridX + 1 < grid[gridY].length ? grid[gridY][gridX + 1] : null;
            array[2] = gridY + 1 < grid.length ? grid[gridY + 1][gridX] : null;
            array[3] = gridX - 1 >= 0 ? grid[gridY][gridX - 1] : null;
            
            return array;
        }
        
        // Get list of touching substances based on neighboring pixels
        private Substance[] getTouchingSubstances(Pixel[] neighbors, Substance sub) {
            Substance[] array = new Substance[4];
            
            array[0] = neighbors[0] != null && !neighbors[0].getSubstance().equals(sub) ? neighbors[0].getSubstance() : null;
            array[1] = neighbors[1] != null && !neighbors[1].getSubstance().equals(sub) ? neighbors[1].getSubstance() : null;
            array[2] = neighbors[2] != null && !neighbors[2].getSubstance().equals(sub) ? neighbors[2].getSubstance() : null;
            array[3] = neighbors[3] != null && !neighbors[3].getSubstance().equals(sub) ? neighbors[3].getSubstance() : null;
            
            return !Arrays.equals(array, new Substance[] {null, null, null, null}) ? array : null;
        }
        
        // Gets cursor coordinates in terms of Pixel grid indices
        // Builds circle of pixels with radius = brushSize
        private Pixel[] getPixelsInBrush() {
            Point mouse = getMousePosition();
            if(mouse == null) {
                return null;
            }
            
            List<Pixel> list = new ArrayList<>();
            Point pixel = new Point(Math.floorDiv(mouse.x, Pixel.getRenderSize().width), Math.floorDiv(mouse.y, Pixel.getRenderSize().height));
            
            int yDiff = -brushSize;
            do {
                int xDiff = (int) -Math.sqrt(Math.abs(Math.ceil(Math.pow(brushSize, 2) - Math.pow(yDiff, 2))));
                do {
                    if(pixel.x + xDiff >= 0 && pixel.x + xDiff < grid[pixel.y].length && pixel.y + yDiff >= 0 && pixel.y + yDiff < grid.length) {
                        list.add(grid[pixel.y + yDiff][pixel.x + xDiff]);
                    }
                    xDiff++;
                } while(xDiff <= (int) Math.sqrt(Math.abs(Math.ceil(Math.pow(brushSize, 2) - Math.pow(yDiff, 2)))));
                yDiff++;
            } while(yDiff <= brushSize);
            
            Pixel[] array = new Pixel[list.size()];
            return list.toArray(array);
        }
    }
    
    private class MouseListener extends MouseAdapter {
        public MouseListener() {
            super();
        }
        
        @Override
        public void mousePressed(MouseEvent e) {
            int status = 0;
            switch(e.getButton()) {
                case MouseEvent.BUTTON1 ->  {
                    status = 1;
                }
                case MouseEvent.BUTTON2 ->  {
                    resetViewport();
                }
                case MouseEvent.BUTTON3 ->  {
                    status = 2;
                }
                case 5 ->  {
                    updater.debugClick(3);
                }
                case 4 ->  {
                    updater.debugClick(4);
                }
            }
            
            updater.updateMouseStatus(status);
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {
            updater.updateMouseStatus(0);
        }
        
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            if(e.getPreciseWheelRotation() < 0) {
                brushSize = brushSize >= 20 ? 20 : brushSize + 1;
            } else {
                brushSize = brushSize <= 1 ? 1 : brushSize - 1;
            }
        }
    }
}
