package com.mycompany.physicstoolbox;

import com.mycompany.physicstoolbox.SubstanceInteraction.ReactionOutcome;
import java.awt.Color;

public class Substance {
    public static final Substance NONE = new Substance(null, null, 0, 0, 0, null, null);
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
    
    // TEMPORARY METHOD
    public static Substance[] getDebugSubstances() {
        Substance[] subs = new Substance[3];
        subs[0] = new Substance(new Color(255, 0, 0), "Red Stuff", 0.9, 0.75, 0.8, State.LIQUID, null);
        subs[1] = new Substance(new Color(0, 255, 0), "Green Stuff", 0.5, 1, 0.1, State.LIQUID, null);
        subs[2] = new Substance(new Color(0, 0, 255), "Blue Stuff", 0.1, 0.5, 1, State.LIQUID, null);
        
        subs[0].addReaction(new SubstanceInteraction(subs[2], subs[1], ReactionOutcome.CHANGED, ReactionOutcome.CHANGED, 0.5));
        subs[2].addReaction(new SubstanceInteraction(subs[0], subs[1], ReactionOutcome.CHANGED, ReactionOutcome.CHANGED, 0.5));
        
        return subs;
    }
    
    private Color color;      // Define using the RGB constructor only
    private String name;
    private double viscosity; // Range -> 0:1
    private double weight;    // Range -> -1:1
    private double density;   // Range -> 0:1
    private State state;
    private SubstanceInteraction[] reactions;
    
    public Substance(Color c, String n, double v, double w, double d, State s, SubstanceInteraction[] r) {
        if(v < 0 || v > 1) {
            throw new IllegalArgumentException("Viscosity must be between 0 and 1.");
        }
        if(w < -1 || w > 1) {
            throw new IllegalArgumentException("Weight must be between -1 and 1.");
        }
        if(d < 0 || d > 1) {
            throw new IllegalArgumentException("Density must be between 0 and 1.");
        }
        
        if(r == null) {
            r = new SubstanceInteraction[0];
        }
        
        color = c;
        name = n;
        viscosity = v;
        weight = w;
        density = d;
        state = s;
        reactions = r;
    }
    
    public Color getColor() {
        // If no color exists, return transparent instead of null.
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
        if(v < 0 || v > 1) {
            throw new IllegalArgumentException("Viscosity must be between 0 and 1.");
        }
        
        viscosity = v;
    }
    
    public double getWeight() {
        return weight;
    }
    
    public void setWeight(double w) {
        if(w < -1 || w > 1) {
            throw new IllegalArgumentException("Weight must be between -1 and 1.");
        }
        
        weight = w;
    }
    
    public double getDensity() {
        return density;
    }
    
    public void setDensity(double d) {
        if(d < 0 || d > 1) {
            throw new IllegalArgumentException("Density must be between 0 and 1.");
        }
        
        density = d;
    }
    
    public State getState() {
        return state;
    }
    
    public void setState(State s) {
        state = s;
    }
    
    public SubstanceInteraction[] getReactions() {
        return reactions;
    }
    
    public void addReaction(SubstanceInteraction si) {
        if(reactions == null) {
            reactions = new SubstanceInteraction[0];
        }
        
        for(SubstanceInteraction reaction: reactions) {
            if(reaction.getReactant().equals(si.getReactant())) {
                throw new IllegalArgumentException("Cannot define two different interactions with the same reactant.");
            }
        }
        
        SubstanceInteraction[] temp = new SubstanceInteraction[reactions.length + 1];
        
        System.arraycopy(reactions, 0, temp, 0, reactions.length);
        temp[reactions.length] = si;
        
        reactions = temp;
    }
    
    public void removeReaction(SubstanceInteraction si) {
        if(reactions.length == 0) {
            return;
        }
        
        SubstanceInteraction[] temp = new SubstanceInteraction[reactions.length - 1];
        
        int j = 0;
        for(SubstanceInteraction reaction : reactions) {
            if (!reaction.equals(si)) {
                temp[j] = reaction;
                j++;
            }
        }
        
        reactions = temp;
    }
    
    public enum State {
        SOLID,
        LIQUID,
        GAS
    }
}
