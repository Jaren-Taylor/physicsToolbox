package com.mycompany.physicstoolbox;

import java.awt.Dimension;
import java.awt.Point;

public class Pixel {
    private static Dimension renderSize;
    
    public static Dimension getRenderSize() {
        return renderSize;
    }
    
    public static void setRenderSize(int x, int y) {
        renderSize = new Dimension(x, y);
    }
    
    private Substance occupyingSubstance;
    private Point gridLocation;
    private int clockSync;
    
    public Pixel(Substance s, int x, int y) {
        occupyingSubstance = s;
        gridLocation = new Point(x, y);
        clockSync = 0;
    }
    
    public boolean containsSubstance() {
        return occupyingSubstance.getId() != -1;
    }
    
    public Substance getSubstance() {
        return occupyingSubstance;
    }
    
    public void setSubstance(Substance s) {
        occupyingSubstance = s;
    }
    
    public Point getGridLocation() {
        return gridLocation;
    }
    
    public void setGridLocation(int x, int y) {
        gridLocation = new Point(x, y);
    }
    
    public int getClockSync() {
        return clockSync;
    }
    
    public void setClockSync(int cs) {
        clockSync = cs;
    }
}
