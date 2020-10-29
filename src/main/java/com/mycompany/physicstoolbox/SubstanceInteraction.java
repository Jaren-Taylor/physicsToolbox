package com.mycompany.physicstoolbox;

public class SubstanceInteraction {
    
    public static SubstanceInteraction decayReaction(double volatility) {
        return new SubstanceInteraction(Substance.NONE, Substance.NONE, ReactionOutcome.CHANGED, ReactionOutcome.UNCHANGED, volatility);
    }
    
    private Substance reactant, product;
    private double volatility;
    private ReactionOutcome sourceOutcome, reactantOutcome;
    
    public SubstanceInteraction(Substance r, Substance p, ReactionOutcome src, ReactionOutcome rct, double v) {
        if(src == ReactionOutcome.UNCHANGED && rct == ReactionOutcome.UNCHANGED) {
            throw new IllegalArgumentException("Cannot define interaction where nothing happens.");
        }
        if(v < 0 || v > 1) {
            throw new IllegalArgumentException("Volatility must be between 0 and 1.");
        }
        
        reactant = r;
        product = p;
        volatility = v;
        sourceOutcome = src;
        reactantOutcome = rct;
    }
    
    public Substance getReactant() {
        return reactant;
    }
    
    public void setReactant(Substance r) {
        reactant = r;
    }
    
    public Substance getProduct() {
        return product;
    }
    
    public void setProduct(Substance p) {
        product = p;
    }
    
    public ReactionOutcome getSourceOutcome() {
        return sourceOutcome;
    }
    
    public void setSourceOutcome(ReactionOutcome o) {
        sourceOutcome = o;
    }
    
    public ReactionOutcome getReactantOutcome() {
        return reactantOutcome;
    }
    
    public void setReactantOutcome(ReactionOutcome o) {
        reactantOutcome = o;
    }
    
    public double getVolatility() {
        return volatility;
    }
    
    public void setVolatility(double v) {
        volatility = v;
    }
    
    public enum ReactionOutcome {
        UNCHANGED,
        CHANGED,
        DESTROYED
    }
}
