package com.mycompany.physicstoolbox;

import com.mycompany.physicstoolbox.SubstanceInteraction.ReactionOutcome;
import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
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
    private static int NUM_OF_SAMPLE_SUBS;
    
    public static void loadSavedSubstances() {
        savedSubs.clear();
        
        Substance[] sampleSubs = new Substance[] {
            new Substance(new Color(180, 180, 180), "Wall", 1, 1, 1, State.SOLID),
            new Substance(new Color(0, 50, 255), "Water", 0.2, 0.75, 0.5, State.LIQUID),
            new Substance(new Color(255, 255, 160), "Sand", 1, 0.9, 0.7, State.LIQUID),
            new Substance(new Color(128, 128, 128), "Stone", 1, 1, 0.99, State.LIQUID),
            new Substance(new Color(255, 255, 255), "Salt", 1, 0.6, 0.5, State.LIQUID),
            new Substance(new Color(100, 160, 255), "Salt Water", 0.25, 0.8, 0.6, State.LIQUID),
            new Substance(new Color(170, 80, 50), "Oil", 0, 0.75, 0.3, State.LIQUID),
            new Substance(new Color(255, 125, 0), "Lava", 0.9, 1, 1, State.LIQUID),
            new Substance(new Color(75, 75, 75), "Metal", 0.5, 1, 1, State.SOLID),
            new Substance(new Color(255, 50, 50), "Fire", 0.1, -0.75, 0, State.LIQUID)
        };
        
        NUM_OF_SAMPLE_SUBS = sampleSubs.length;
        
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
            
            List<Substance> allSubs = new ArrayList<>(Arrays.asList(sampleSubs));
            allSubs.addAll(subList);
            
            // Add the substances individually to set their proper IDs
            for(Substance sub: allSubs) {
                addCustomSubstance(sub);
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
            
        } catch(FileNotFoundException fnfe) {
            System.out.println("LOAD ERROR: Could not find SavedSubstances.json.");
            
            for(Substance sub: sampleSubs) {
                addCustomSubstance(sub);
            }
        } catch(Exception e) {
            System.out.println("LOAD ERROR: SavedSubstances.json is corrupted.");
            e.printStackTrace();
            
            for(Substance sub: sampleSubs) {
                addCustomSubstance(sub);
            }
        }
    }
    
    public static void saveSubstances() {
        JSONArray json = new JSONArray();
        JSONObject subJsonObj;
        Substance savedSub;
        
        // Don't write sample substances to the JSON
        for(int i = NUM_OF_SAMPLE_SUBS; i < savedSubs.size(); i++) {
            subJsonObj = new JSONObject();
            savedSub = savedSubs.get(i);
            
            JSONObject colorJsonObj = new JSONObject();
            colorJsonObj.put("r", savedSub.getColor().getRed());
            colorJsonObj.put("g", savedSub.getColor().getGreen());
            colorJsonObj.put("b", savedSub.getColor().getBlue());
            
            subJsonObj.put("color", colorJsonObj);
            subJsonObj.put("name", savedSub.getName());
            subJsonObj.put("viscosity", savedSub.getViscosity());
            subJsonObj.put("weight", savedSub.getWeight());
            subJsonObj.put("density", savedSub.getDensity());
            
            int sta = savedSub.getState() == State.SOLID ? 1 : savedSub.getState() == State.LIQUID ? 2 : 3;
            subJsonObj.put("state", sta);
            
            JSONArray reactionsJson = new JSONArray();
            JSONObject reactionJsonObj;
            for(SubstanceInteraction reaction: savedSub.getReactions()) {
                reactionJsonObj = new JSONObject();
                
                reactionJsonObj.put("reactantId", reaction.getReactant().getId());
                reactionJsonObj.put("productId", reaction.getProduct().getId());
                
                int sourceOutcome = reaction.getSourceOutcome() == ReactionOutcome.UNCHANGED ? 1 : reaction.getSourceOutcome() == ReactionOutcome.CHANGED ? 2 : 3;
                int reactantOutcome = reaction.getReactantOutcome() == ReactionOutcome.UNCHANGED ? 1 : reaction.getReactantOutcome() == ReactionOutcome.CHANGED ? 2 : 3;
                reactionJsonObj.put("sourceOutcome", sourceOutcome);
                reactionJsonObj.put("reactantOutcome", reactantOutcome);
                
                reactionJsonObj.put("volatility", reaction.getVolatility());
                
                reactionsJson.add(reactionJsonObj);
            }
            
            subJsonObj.put("reactions", reactionsJson);
            
            json.add(subJsonObj);
        }
        
        try {
            PrintWriter writer = new PrintWriter("SavedSubstances.json");
            writer.write(json.toJSONString());
            
            writer.flush();
            writer.close();
            
        } catch(FileNotFoundException fnfe) {
            System.out.println("SAVE ERROR: Could not find SavedSubstances.json.");
        } catch(Exception e) {
            System.out.println("SAVE ERROR OCCURRED.");
            e.printStackTrace();
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
    
    public static void addCustomSubstance(Substance sub) {
        sub.setId(savedSubs.size());
        savedSubs.add(sub);
    }
    
    public static void editCustomSubstance(int id, Substance newSub) {
        if(id < NUM_OF_SAMPLE_SUBS) {
            throw new UnsupportedOperationException("Cannot edit sample substances.");
        }
        
        newSub.setId(savedSubs.get(id).getId());
        savedSubs.set(id, newSub);
    }
    
    public static void removeCustomSubstance(Substance sub) {
        if(savedSubs.indexOf(sub) < NUM_OF_SAMPLE_SUBS) {
            throw new UnsupportedOperationException("Cannot remove sample substances.");
        }
        
        // Decrementing the IDs of the substances that follow
        for(int i = savedSubs.indexOf(sub) + 1; i < savedSubs.size(); i++) {
            Substance s = savedSubs.get(i);
            s.setId(s.getId() - 1);
        }
        
        savedSubs.remove(sub);
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
        if(c == null && n == null && s == null) {
            id = -1;
        }
    }
    
    public int getId() {
        return id;
    }
    
    // Setter for the ID is private, since ID is hidden from the user
    private void setId(int i) {
        if(this.equals(Substance.NONE)) {
            throw new UnsupportedOperationException("Cannot change the ID of Substance.NONE.");
        }
        
        id = i;
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
