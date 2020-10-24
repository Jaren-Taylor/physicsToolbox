package com.mycompany.physicstoolbox;

import java.awt.Color;

public class Substance {
    public static final Substance NONE = new Substance(null, null, 0, 0, 0, null);
    private static Substance currentlySelected;
    
    public static Substance[] getSavedSubstances() {
        return null;
    }
    
    public static Substance getCurrentlySelected() {
        return currentlySelected;
    }
    
    public static void setCurrentlySelected(Substance s) {
        currentlySelected = s;
    }
    
    private Color color;
    private String name;
    private double viscosity;
    private double weight;
    private double density;
    private State state;
    
    public Substance(Color c, String n, double v, double w, double d, State s) {
        color = c;
        name = n;
        viscosity = v;
        weight = w;
        density = d;
        state = s;
    }
    
    public Color getColor() {
        // If no color exists, return perfect transparency instead of null.
        if(color == null) {
            return new Color(0, 0, 0, 0);
        }
        return color;
    }
    
    public void setColor(int r, int g, int b) {
        color = new Color(r, g, b);
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String n) {
        name = n;
    }
    
    public double getViscosity() {
        return viscosity;
    }
    
    public void setViscosity(double v) {
        viscosity = v;
    }
    
    public double getWeight() {
        return weight;
    }
    
    public void setWeight(double w) {
        weight = w;
    }
    
    public double getDensity() {
        return density;
    }
    
    public void setDensity(double d) {
        density = d;
    }
    
    public State getState() {
        return state;
    }
    
    public void setState(State s) {
        state = s;
    }
    
    public enum State {
        SOLID,
        LIQUID,
        GAS
    }
}
