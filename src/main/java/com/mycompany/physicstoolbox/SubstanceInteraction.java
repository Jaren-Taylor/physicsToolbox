package com.mycompany.physicstoolbox;

public class SubstanceInteraction {
    private Substance reactant, product;
    private double reactionDelay;
    
    public SubstanceInteraction(Substance r, Substance p, double d) {
        reactant = r;
        product = p;
        reactionDelay = d;
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
    
    public double getDelay() {
        return reactionDelay;
    }
    
    public void setDelay(double d) {
        reactionDelay = d;
    }
}
