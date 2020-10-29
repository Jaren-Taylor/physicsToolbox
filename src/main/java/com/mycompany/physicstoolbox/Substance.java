package com.mycompany.physicstoolbox;

import com.mycompany.physicstoolbox.SubstanceInteraction.ReactionOutcome;
import java.awt.Color;

public class Substance {
    public static final Substance NONE = new Substance(null, null, 0, 0, 0, null);
    private static Substance[] selected = new Substance[2];
    
    // TEMPORARY FIELD
    private static Substance[] debugSubs;
    
    public static Substance[] loadSavedSubstances() {
        return null;
    }
    
    public static Substance getCurrentlySelected() {
        return selected[0];
    }
    
    public static void setCurrentlySelected(Substance s) {
        selected[0] = s;
    }
    
    public static Substance getAlternateSelected() {
        return selected[1];
    }
    
    public static void setAlternateSelected(Substance s) {
        selected[1] = s;
    }
    
    // TEMPORARY METHOD
    public static Substance[] getDebugSubstances() {
        return debugSubs;
    }
    
    // TEMPORARY METHOD
    public static void initializeDebugSubstances() {
        Substance[] subs = new Substance[7];
        subs[0] = new Substance(new Color(255, 100, 0), "Fire", 0.1, -0.75, 0, State.LIQUID);
        subs[1] = new Substance(new Color(255, 0, 0), "Red Stuff", 0.9, 0.75, 0.8, State.LIQUID);
        subs[2] = new Substance(new Color(0, 255, 0), "Green Stuff", 0.5, -1, 0.7, State.LIQUID);
        subs[3] = new Substance(new Color(0, 0, 255), "Blue Stuff", 0.1, 0.5, 0.1, State.LIQUID);
        subs[4] = new Substance(new Color(255, 255, 0), "Yellow Stuff", 0.5, 0.5, 1, State.GAS);
        subs[5] = new Substance(new Color(200, 50, 255), "Purple Stuff", 0, -0.6, 0.2, State.GAS);
        subs[6] = new Substance(new Color(128, 128, 128), "Gray Stuff", 0.1, 0.5, 0.1, State.SOLID);
        
        subs[0].addReaction(SubstanceInteraction.decayReaction(0.9));
        subs[1].addReaction(new SubstanceInteraction(subs[3], subs[2], ReactionOutcome.CHANGED, ReactionOutcome.CHANGED, 0.3));
        subs[3].addReaction(new SubstanceInteraction(subs[6], subs[6], ReactionOutcome.CHANGED, ReactionOutcome.UNCHANGED, 1));
        subs[4].addReaction(new SubstanceInteraction(subs[2], subs[3], ReactionOutcome.DESTROYED, ReactionOutcome.CHANGED, 0.8));
        subs[5].addReaction(new SubstanceInteraction(subs[6], subs[3], ReactionOutcome.CHANGED, ReactionOutcome.UNCHANGED, 0.75));
        
        debugSubs = subs;
    }
    
    private Color color;      // Define using the RGB constructor only
    private String name;
    private double viscosity; // Range -> 0:1
    private double weight;    // Range -> -1:1
    private double density;   // Range -> 0:1
    private State state;
    private SubstanceInteraction[] reactions;
    
    public Substance(Color c, String n, double v, double w, double d, State s) {
        if(v < 0 || v > 1) {
            throw new IllegalArgumentException("Viscosity must be between 0 and 1.");
        }
        if(w < -1 || w > 1) {
            throw new IllegalArgumentException("Weight must be between -1 and 1.");
        }
        if(d < 0 || d > 1) {
            throw new IllegalArgumentException("Density must be between 0 and 1.");
        }
        
        color = c;
        name = n;
        viscosity = s == State.SOLID ? 1 : s == State.GAS ? 0 : v;
        weight = s == State.SOLID || s == State.GAS ? 0 : w;
        density = d;
        state = s;
        reactions = new SubstanceInteraction[0];
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
        if(reactions == null) {
            return new SubstanceInteraction[0];
        }
        
        return reactions;
    }
    
    public void addReaction(SubstanceInteraction si) {
        if(reactions == null) {
            reactions = new SubstanceInteraction[0];
        }
        
        if(reactsWith(si.getReactant())) {
            throw new IllegalArgumentException("Cannot define two different interactions with the same reactant.");
        }
        
        SubstanceInteraction[] temp = new SubstanceInteraction[reactions.length + 1];
        
        System.arraycopy(reactions, 0, temp, 0, reactions.length);
        temp[reactions.length] = si;
        
        reactions = temp;
        
        if(!si.getReactant().equals(this) && !si.getReactant().equals(Substance.NONE) && !si.getReactant().reactsWith(this)) {
            si.getReactant().addReaction(new SubstanceInteraction(this, si.getProduct(), si.getReactantOutcome(), si.getSourceOutcome(), si.getVolatility()));
        }
    }
    
    public void removeReaction(SubstanceInteraction si) {
        if(reactions == null || reactions.length == 0) {
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
        
        if(!si.getReactant().equals(Substance.NONE)) {
            si.getReactant().removeReaction(new SubstanceInteraction(this, si.getProduct(), si.getReactantOutcome(), si.getSourceOutcome(), si.getVolatility()));
        }
    }
    
    public void resetReactions() {
        for(SubstanceInteraction reaction: reactions) {
            reaction.getReactant().removeReaction(new SubstanceInteraction(this, reaction.getProduct(), reaction.getReactantOutcome(), reaction.getSourceOutcome(), reaction.getVolatility()));
        }
        
        reactions = new SubstanceInteraction[0];
    }
    
    public boolean reactsWith(Substance sub) {
        if(reactions == null || reactions.length == 0) {
            return false;
        }
        for(SubstanceInteraction reaction: reactions) {
            if(reaction.getReactant().equals(sub)) {
                return true;
            }
        }
        return false;
    }
    
    public enum State {
        SOLID,
        LIQUID,
        GAS
    }
}
