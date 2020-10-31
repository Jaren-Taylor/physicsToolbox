package com.mycompany.physicstoolbox;

import com.mycompany.physicstoolbox.SubstanceInteraction.ReactionOutcome;
import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Substance {
    public static final Substance NONE = new Substance(null, null, 0, 0, 0, null);
    private static Substance[] selected = new Substance[2];
    
    private static List<Substance> savedSubs = new ArrayList<>();
    
    public static void loadSavedSubstances() {
        savedSubs.clear();
        
        Substance[] sampleSubs = new Substance[] {
            new Substance(new Color(180, 180, 180), "Wall", 1, 1, 1, State.SOLID),
            new Substance(new Color(0, 50, 255), "Water", 0.2, 0.75, 0.5, State.LIQUID),
            new Substance(new Color(255, 255, 160), "Sand", 1, 0.9, 0.7, State.LIQUID),
            new Substance(new Color(128, 128, 128), "Stone", 1, 1, 0.99, State.LIQUID),
            new Substance(new Color(255, 255, 255), "Salt", 1, 0.6, 0.5, State.LIQUID),
            new Substance(new Color(100, 160, 255), "Salt Water", 0.25, 0.8, 0.5, State.LIQUID),
            new Substance(new Color(170, 80, 50), "Oil", 0, 0.75, 0.3, State.LIQUID),
            new Substance(new Color(255, 125, 0), "Lava", 0.9, 1, 1, State.LIQUID),
            new Substance(new Color(75, 75, 75), "Metal", 0.5, 1, 1, State.SOLID),
            new Substance(new Color(255, 50, 50), "Fire", 0.1, -0.75, 0, State.LIQUID)
        };
        
        // Set sample substance interactions
        sampleSubs[1].addReaction(new SubstanceInteraction(sampleSubs[4], sampleSubs[5], ReactionOutcome.CHANGED, ReactionOutcome.DESTROYED, 0.8));
        sampleSubs[1].addReaction(new SubstanceInteraction(sampleSubs[7], sampleSubs[3], ReactionOutcome.DESTROYED, ReactionOutcome.CHANGED, 0.8));
        sampleSubs[1].addReaction(new SubstanceInteraction(sampleSubs[8], sampleSubs[2], ReactionOutcome.UNCHANGED, ReactionOutcome.CHANGED, 0.3));
        sampleSubs[1].addReaction(new SubstanceInteraction(sampleSubs[9], sampleSubs[9], ReactionOutcome.DESTROYED, ReactionOutcome.DESTROYED, 0.6));
        sampleSubs[2].addReaction(new SubstanceInteraction(sampleSubs[9], sampleSubs[9], ReactionOutcome.UNCHANGED, ReactionOutcome.DESTROYED, 0.9));
        sampleSubs[4].addReaction(new SubstanceInteraction(sampleSubs[7], sampleSubs[9], ReactionOutcome.CHANGED, ReactionOutcome.UNCHANGED, 0.85));
        sampleSubs[4].addReaction(new SubstanceInteraction(sampleSubs[9], sampleSubs[9], ReactionOutcome.CHANGED, ReactionOutcome.UNCHANGED, 0.7));
        sampleSubs[5].addReaction(new SubstanceInteraction(sampleSubs[7], sampleSubs[3], ReactionOutcome.DESTROYED, ReactionOutcome.CHANGED, 0.85));
        sampleSubs[5].addReaction(new SubstanceInteraction(sampleSubs[8], sampleSubs[2], ReactionOutcome.UNCHANGED, ReactionOutcome.CHANGED, 0.5));
        sampleSubs[5].addReaction(new SubstanceInteraction(sampleSubs[9], sampleSubs[4], ReactionOutcome.CHANGED, ReactionOutcome.DESTROYED, 0.6));
        sampleSubs[6].addReaction(new SubstanceInteraction(sampleSubs[7], sampleSubs[9], ReactionOutcome.CHANGED, ReactionOutcome.UNCHANGED, 0.98));
        sampleSubs[6].addReaction(new SubstanceInteraction(sampleSubs[9], sampleSubs[9], ReactionOutcome.CHANGED, ReactionOutcome.UNCHANGED, 0.95));
        sampleSubs[7].addReaction(new SubstanceInteraction(Substance.NONE, sampleSubs[3], ReactionOutcome.CHANGED, ReactionOutcome.UNCHANGED, 0.4));
        sampleSubs[8].addReaction(new SubstanceInteraction(sampleSubs[7], sampleSubs[7], ReactionOutcome.CHANGED, ReactionOutcome.UNCHANGED, 0.6));
        sampleSubs[9].addReaction(SubstanceInteraction.decayReaction(0.9));
        
        
        try {
            Iterator json = ((JSONArray) new JSONParser().parse(new FileReader("SavedSubstances.json"))).iterator();
            
            List<Substance> subList = new ArrayList<>();
            List<JSONObject[]> reactionList = new ArrayList<>();
            while(json.hasNext()) {
                JSONObject sub = (JSONObject) json.next();
                JSONObject col = (JSONObject) sub.get("color");
                int sta = ((Long) sub.get("state")).intValue();
                
                Color color = new Color(((Long) col.get("r")).intValue(), ((Long) col.get("g")).intValue(), ((Long) col.get("b")).intValue());
                String name = (String) sub.get("name");
                double viscosity = ((Number) sub.get("viscosity")).doubleValue();
                double weight = ((Number) sub.get("weight")).doubleValue();
                double density = ((Number) sub.get("density")).doubleValue();
                State state = sta == 1 ? State.SOLID : sta == 2 ? State.LIQUID : State.GAS;
                
                // Load substance interactions into a list to be applied in the next step
                // Interactions cannot be applied in this step, because the reactants/products may not have been initialized yet
                Iterator reactionsJson = ((JSONArray) sub.get("reactions")).iterator();
                List<JSONObject> reactionObjs = new ArrayList<>();
                while(reactionsJson.hasNext()) {
                    reactionObjs.add((JSONObject) reactionsJson.next());
                }
                
                JSONObject[] reactionObjsArray = new JSONObject[reactionObjs.size()];
                reactionList.add(reactionObjs.toArray(reactionObjsArray));
                
                subList.add(new Substance(color, name, viscosity, weight, density, state));
            }
            
            // Set all custom substance interactions
            for(Substance s: subList) {
                for(JSONObject obj: reactionList.get(subList.indexOf(s))) {
                    int reactantId = ((Long) obj.get("reactantId")).intValue();
                    int productId = ((Long) obj.get("productId")).intValue();
                    int srcOutcome = ((Long) obj.get("sourceOutcome")).intValue();
                    int rctOutcome = ((Long) obj.get("reactantOutcome")).intValue();
                    double volatility = ((Number) obj.get("volatility")).doubleValue();
                    
                    ReactionOutcome sourceOutcome = srcOutcome == 1 ? ReactionOutcome.UNCHANGED : srcOutcome == 2 ? ReactionOutcome.CHANGED : ReactionOutcome.DESTROYED;
                    ReactionOutcome reactantOutcome = rctOutcome == 1 ? ReactionOutcome.UNCHANGED : rctOutcome == 2 ? ReactionOutcome.CHANGED : ReactionOutcome.DESTROYED;
                    
                    s.addReaction(new SubstanceInteraction(Substance.getSubstanceById(reactantId), Substance.getSubstanceById(productId), sourceOutcome, reactantOutcome, volatility));
                }
            }
            
            List<Substance> allSubs = new ArrayList<>(Arrays.asList(sampleSubs));
            allSubs.addAll(subList);
            savedSubs = allSubs;
            
        } catch(FileNotFoundException fnfe) {
            System.out.println("Could not find SavedSubstances.json.");
            savedSubs = Arrays.asList(sampleSubs);
        } catch(Exception e) {
            System.out.println("SavedSubstances.json is corrupted.");
            savedSubs = Arrays.asList(sampleSubs);
        }
    }
    
    public static Substance[] getSavedSubstances() {
        Substance[] array = new Substance[savedSubs.size()];
        return savedSubs.toArray(array);
    }
    
    public static Substance getSubstanceById(int id) {
        for(Substance sub: savedSubs) {
            if(sub.getId() == id) {
                return sub;
            }
        }
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
    
    // Fail-safe for initializing a substance with an already-existing ID
    private static int getNewId() {
        int id = 0;
        for(Substance sub: savedSubs) {
            if(sub.getId() > id) {
                id = sub.getId();
            }
        }
        return id;
    }
    
    private int id;           // Used to identify reactants/products in the JSON
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
        
        // Assign an ID of -1 to Substance.NONE
        // Do not add Substance.NONE to the savedSubs list
        if(c == null && n == null && s == null) {
            id = -1;
        } else {
            id = Substance.getSubstanceById(savedSubs.size()) == null ? savedSubs.size() : Substance.getNewId();
            savedSubs.add(this);
        }
    }
    
    public int getId() {
        return id;
    }
    
    // No setter for ID, since ID is hidden from the user
    
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
        
        if(si.getReactant().getId() != id && reactsWith(si.getReactant().getId())) {
            throw new IllegalArgumentException("Cannot define two different interactions with the same reactant.");
        }
        
        List<SubstanceInteraction> asList = new ArrayList<>(Arrays.asList(reactions));
        asList.add(si);
        
        SubstanceInteraction[] asArray = new SubstanceInteraction[asList.size()];
        reactions = asList.toArray(asArray);
        
        if(si.getReactant().getId() != id && si.getReactant().getId() != -1 && !si.getReactant().reactsWith(id)) {
            si.getReactant().addReaction(new SubstanceInteraction(this, si.getProduct(), si.getReactantOutcome(), si.getSourceOutcome(), si.getVolatility()));
        }
    }
    
    public void removeReaction(SubstanceInteraction si) {
        if(reactions == null || reactions.length == 0) {
            return;
        }
        
        List<SubstanceInteraction> asList = new ArrayList<>(Arrays.asList(reactions));
        
        for(SubstanceInteraction reaction : asList) {
            if (reaction.equals(si)) {
                asList.remove(reaction);
            }
        }
        
        SubstanceInteraction[] asArray = new SubstanceInteraction[asList.size()];
        reactions = asList.toArray(asArray);
        
        if(si.getReactant().getId() != -1 && si.getReactant().getId() != id) {
            si.getReactant().removeReaction(new SubstanceInteraction(this, si.getProduct(), si.getReactantOutcome(), si.getSourceOutcome(), si.getVolatility()));
        }
    }
    
    public void resetReactions() {
        for(SubstanceInteraction reaction: reactions) {
            reaction.getReactant().removeReaction(new SubstanceInteraction(this, reaction.getProduct(), reaction.getReactantOutcome(), reaction.getSourceOutcome(), reaction.getVolatility()));
        }
        
        reactions = new SubstanceInteraction[0];
    }
    
    public boolean reactsWith(int subId) {
        if(reactions == null || reactions.length == 0) {
            return false;
        }
        for(SubstanceInteraction reaction: reactions) {
            if(reaction.getReactant().getId() == subId) {
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
