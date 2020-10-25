package com.mycompany.physicstoolbox;

import java.awt.Dimension;

public class Pixel {
    private static Dimension renderSize;
    
    public static Dimension getRenderSize() {
        return renderSize;
    }
    
    public static void setRenderSize(int x, int y) {
        renderSize = new Dimension(x, y);
    }
    
    private Substance occupyingSubstance;
    private Dimension gridLocation;
    private int clockSync;
    
    public Pixel(Substance s, int x, int y) {
        occupyingSubstance = s;
        gridLocation = new Dimension(x, y);
        clockSync = 0;
    }
    
    public boolean containsSubstance() {
        return !occupyingSubstance.equals(Substance.NONE);
    }
    
    public Substance getSubstance() {
        return occupyingSubstance;
    }
    
    public void setSubstance(Substance s) {
        occupyingSubstance = s;
    }
    
    public Dimension getGridLocation() {
        return gridLocation;
    }
    
    public void setGridLocation(int x, int y) {
        gridLocation = new Dimension(x, y);
    }
    
    public int getClockSync() {
        return clockSync;
    }
    
    public void setClockSync(int cs) {
        clockSync = cs;
    }
}
