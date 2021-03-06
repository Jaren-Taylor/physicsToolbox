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

    public static final Substance CREATE_NEW = new Substance(null, "[Create New]", 0, 0, 0, null, false);
    public static final Substance NONE = new Substance(null, "Void", 0, 0, 0, null, true);

    private static Substance[] selected = new Substance[2];
    private static List<Substance> savedSubs = new ArrayList<>();
    private static int NUM_OF_SAMPLE_SUBS;

    public static final Substance WALL = new Substance(new Color(180, 180, 180), "Wall", 1, 1, 1, State.SOLID, true);
    public static final Substance WATER = new Substance(new Color(0, 50, 255), "Water", 0.2, 0.75, 0.5, State.LIQUID, true);
    public static final Substance SAND = new Substance(new Color(255, 255, 160), "Sand", 1, 0.9, 0.7, State.LIQUID, true);
    public static final Substance STONE = new Substance(new Color(128, 128, 128), "Stone", 1, 1, 0.99, State.LIQUID, true);
    public static final Substance SALT = new Substance(new Color(255, 255, 255), "Salt", 1, 0.6, 0.5, State.LIQUID, true);
    public static final Substance SALT_WATER = new Substance(new Color(100, 160, 255), "Salt Water", 0.25, 0.8, 0.6, State.LIQUID, true);
    public static final Substance OIL = new Substance(new Color(170, 80, 50), "Oil", 0, 0.75, 0.3, State.LIQUID, true);
    public static final Substance LAVA = new Substance(new Color(255, 125, 0), "Lava", 0.9, 1, 1, State.LIQUID, true);
    public static final Substance METAL = new Substance(new Color(75, 75, 75), "Metal", 0.5, 1, 1, State.SOLID, true);
    public static final Substance FIRE = new Substance(new Color(255, 50, 50), "Fire", 0.1, -0.75, 0, State.LIQUID, true);
    public static final Substance PLANT = new Substance(new Color(50, 225, 50), "Plant", 1, 0.5, 1, State.SOLID, true);

    public static void loadSavedSubstances() {
        CREATE_NEW.setId(-2);
        NONE.setId(-1);
        
        savedSubs.clear();

        Substance[] sampleSubs = new Substance[] { WALL, WATER, SAND, STONE, SALT, SALT_WATER, OIL, LAVA, METAL, FIRE, PLANT };
        
        int sampleId = 0;
        for(Substance sample: sampleSubs) {
            sample.setId(sampleId);
            sampleId++;
        }

        NUM_OF_SAMPLE_SUBS = sampleSubs.length;

        // Set sample substance interactions
        WATER.addReaction(new SubstanceInteraction(SALT, SALT_WATER, ReactionOutcome.CHANGED, ReactionOutcome.DESTROYED, 0.95));
        WATER.addReaction(new SubstanceInteraction(LAVA, STONE, ReactionOutcome.DESTROYED, ReactionOutcome.CHANGED, 0.85));
        WATER.addReaction(new SubstanceInteraction(METAL, SAND, ReactionOutcome.UNCHANGED, ReactionOutcome.CHANGED, 0.3));
        WATER.addReaction(new SubstanceInteraction(FIRE, FIRE, ReactionOutcome.DESTROYED, ReactionOutcome.DESTROYED, 0.6));
        WATER.addReaction(new SubstanceInteraction(PLANT, PLANT, ReactionOutcome.CHANGED, ReactionOutcome.UNCHANGED, 0.65));
        SAND.addReaction(new SubstanceInteraction(FIRE, FIRE, ReactionOutcome.UNCHANGED, ReactionOutcome.DESTROYED, 0.9));
        SALT.addReaction(new SubstanceInteraction(LAVA, FIRE, ReactionOutcome.CHANGED, ReactionOutcome.UNCHANGED, 0.85));
        SALT.addReaction(new SubstanceInteraction(PLANT, NONE, ReactionOutcome.UNCHANGED, ReactionOutcome.DESTROYED, 0.35));
        SALT.addReaction(new SubstanceInteraction(FIRE, FIRE, ReactionOutcome.CHANGED, ReactionOutcome.UNCHANGED, 0.85));
        SALT_WATER.addReaction(new SubstanceInteraction(LAVA, STONE, ReactionOutcome.DESTROYED, ReactionOutcome.CHANGED, 0.85));
        SALT_WATER.addReaction(new SubstanceInteraction(METAL, SAND, ReactionOutcome.UNCHANGED, ReactionOutcome.CHANGED, 0.5));
        SALT_WATER.addReaction(new SubstanceInteraction(FIRE, SALT, ReactionOutcome.CHANGED, ReactionOutcome.DESTROYED, 0.6));
        OIL.addReaction(new SubstanceInteraction(LAVA, FIRE, ReactionOutcome.CHANGED, ReactionOutcome.UNCHANGED, 0.95));
        OIL.addReaction(new SubstanceInteraction(FIRE, FIRE, ReactionOutcome.CHANGED, ReactionOutcome.UNCHANGED, 0.95));
        LAVA.addReaction(new SubstanceInteraction(NONE, STONE, ReactionOutcome.CHANGED, ReactionOutcome.UNCHANGED, 0.6));
        LAVA.addReaction(new SubstanceInteraction(LAVA, STONE, ReactionOutcome.CHANGED, ReactionOutcome.UNCHANGED, 0.25));
        METAL.addReaction(new SubstanceInteraction(LAVA, LAVA, ReactionOutcome.CHANGED, ReactionOutcome.UNCHANGED, 0.6));
        FIRE.addReaction(new SubstanceInteraction(NONE, NONE, ReactionOutcome.CHANGED, ReactionOutcome.UNCHANGED, 0.9));
        FIRE.addReaction(new SubstanceInteraction(FIRE, NONE, ReactionOutcome.CHANGED, ReactionOutcome.UNCHANGED, 0.9));
        PLANT.addReaction(new SubstanceInteraction(NONE, PLANT, ReactionOutcome.UNCHANGED, ReactionOutcome.CHANGED, 0.3));
        PLANT.addReaction(new SubstanceInteraction(LAVA, FIRE, ReactionOutcome.CHANGED, ReactionOutcome.UNCHANGED, 0.95));
        PLANT.addReaction(new SubstanceInteraction(FIRE, FIRE, ReactionOutcome.CHANGED, ReactionOutcome.UNCHANGED, 0.95));

        try {
            Iterator json = ((JSONArray) new JSONParser().parse(new FileReader("SavedSubstances.json"))).iterator();

            List<Substance> subList = new ArrayList<>();
            List<JSONObject[]> reactionList = new ArrayList<>();
            int id = sampleSubs.length;
            while (json.hasNext()) {
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
                while (reactionsJson.hasNext()) {
                    reactionObjs.add((JSONObject) reactionsJson.next());
                }

                JSONObject[] reactionObjsArray = new JSONObject[reactionObjs.size()];
                reactionList.add(reactionObjs.toArray(reactionObjsArray));
                
                Substance newSub = new Substance(color, name, viscosity, weight, density, state, false);
                newSub.setId(id);

                subList.add(newSub);
                id++;
            }

            List<Substance> allSubs = new ArrayList<>(Arrays.asList(sampleSubs));
            allSubs.addAll(subList);
            savedSubs.addAll(allSubs);
            
            // Set all custom substance interactions
            for (Substance s : subList) {
                for (JSONObject obj : reactionList.get(subList.indexOf(s))) {
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

        } catch (FileNotFoundException fnfe) {
            System.out.println("LOAD ERROR: Could not find SavedSubstances.json.");
            
            savedSubs.clear();
            savedSubs.addAll(new ArrayList<>(Arrays.asList(sampleSubs)));
        } catch (Exception e) {
            System.out.println("LOAD ERROR: SavedSubstances.json is corrupted.");
            e.printStackTrace();
            
            savedSubs.clear();
            savedSubs.addAll(new ArrayList<>(Arrays.asList(sampleSubs)));
        }
    }

    public static void saveSubstances() {
        JSONArray json = new JSONArray();
        JSONObject subJsonObj;
        Substance savedSub;

        // Don't write sample substances to the JSON
        for (int i = NUM_OF_SAMPLE_SUBS; i < savedSubs.size(); i++) {
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
                    for (SubstanceInteraction reaction : savedSub.getReactions()) {
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

            } catch (FileNotFoundException fnfe) {
                System.out.println("SAVE ERROR: Could not find SavedSubstances.json.");
            } catch (Exception e) {
                System.out.println("SAVE ERROR OCCURRED.");
                e.printStackTrace();
            }
        }
    

    public static Substance[] getSavedSubstances() {
        Substance[] array = new Substance[savedSubs.size()];
        return savedSubs.toArray(array);
    }

    public static Substance getSubstanceById(int id) {
        if(id == -2) {
            return Substance.CREATE_NEW;
        }
        if(id == -1) {
            return Substance.NONE;
        }
        
        for(Substance sub : savedSubs) {
            if(sub.getId() == id) {
                return sub;
            }
        }
        
        return null;
    }

    public static void addCustomSubstance(Substance newSub) {
        newSub.setId(savedSubs.size());
        savedSubs.add(newSub);
        
        UI.setMenuOptions(getSavedSubstances());
    }

    public static void editCustomSubstance(int id, Substance newSub) {
        if(getSubstanceById(id).isSampleSub) {
            throw new UnsupportedOperationException("Cannot edit sample substances.");
        }
        
        newSub.setId(id);
        savedSubs.set(id, newSub);
        
        if(getCurrentlySelected().id == id) {
            setCurrentlySelected(newSub);
        }
        if(getAlternateSelected().id == id) {
            setAlternateSelected(newSub);
        }
        
        UI.setMenuOptions(getSavedSubstances());
    }

    public static void removeCustomSubstance(Substance sub) {
        if(sub.equals(Substance.CREATE_NEW)) {
            throw new UnsupportedOperationException("Cannot remove [Create New].");
        }
        if(sub.isSampleSub) {
            throw new UnsupportedOperationException("Cannot remove sample substances.");
        }

        // Removing reactions and decrementing the IDs of the substances that follow
        for(int i = 0; i < savedSubs.size(); i++) {
            Substance s = savedSubs.get(i);
            
            for(SubstanceInteraction si: s.getReactions()) {
                if(si.getReactant().equals(sub) || si.getProduct().equals(sub)) {
                    s.removeReaction(si);
                }
            }
            
            if(i > savedSubs.indexOf(sub)) {
                s.setId(s.getId() - 1);
            }
        }
        
        if(Substance.getCurrentlySelected().equals(sub)) {
            Substance.setCurrentlySelected(WALL);
        }
        if(Substance.getAlternateSelected().equals(sub)) {
            Substance.setAlternateSelected(WALL);
        }

        savedSubs.remove(sub);
        
        UI.setMenuOptions(getSavedSubstances());
    }

    public static Substance getCurrentlySelected() {
        return selected[0];
    }

    public static void setCurrentlySelected(Substance s) {
        if(!s.equals(CREATE_NEW) && !s.equals(NONE)) {
            selected[0] = s;
        }
        
        UI.setEditorSubstance(s);
    }

    public static Substance getAlternateSelected() {
        return selected[1];
    }

    public static void setAlternateSelected(Substance s) {
        if(!s.equals(CREATE_NEW) && !s.equals(NONE)) {
            selected[1] = s;
        }
    }

    private int id;      // Used to identify reactants/products in the JSON
    private Color color;      // Define using the RGB constructor only
    private String name;
    private double viscosity; // Range -> 0:1
    private double weight;    // Range -> -1:1
    private double density;   // Range -> 0:1
    private State state;
    private SubstanceInteraction[] reactions;
    
    private boolean isSampleSub;

    public Substance(Color c, String n, double v, double w, double d, State s, boolean isSample) {
        if (v < 0 || v > 1) {
            throw new IllegalArgumentException("Viscosity must be between 0 and 1.");
        }
        if (w < -1 || w > 1) {
            throw new IllegalArgumentException("Weight must be between -1 and 1.");
        }
        if (d < 0 || d > 1) {
            throw new IllegalArgumentException("Density must be between 0 and 1.");
        }
        
        color = c;
        name = n;
        viscosity = s == State.SOLID ? 1 : s == State.GAS ? 0 : v;
        weight = s == State.SOLID || s == State.GAS ? 0 : w;
        density = d;
        state = s;
        reactions = new SubstanceInteraction[0];
        
        isSampleSub = isSample;
    }

    public int getId() {
        return id;
    }

    public void setId(int i) {
        if(i != -2 && this.equals(CREATE_NEW)) {
            throw new UnsupportedOperationException("Cannot change the ID of CREATE_NEW.");
        }
        if(i != -1 && this.equals(NONE)) {
            throw new UnsupportedOperationException("Cannot change the ID of NONE.");
        }
        
        id = i;
    }

    public Color getColor() {
        // If this is Substance.NONE, return the background color
        if (this.equals(Substance.NONE)) {
            return Viewport.getInstance(0, null).getBackgroundColor();
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
        if (v < 0 || v > 1) {
            throw new IllegalArgumentException("Viscosity must be between 0 and 1.");
        }

        viscosity = v;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double w) {
        if (w < -1 || w > 1) {
            throw new IllegalArgumentException("Weight must be between -1 and 1.");
        }

        weight = w;
    }

    public double getDensity() {
        return density;
    }

    public void setDensity(double d) {
        if (d < 0 || d > 1) {
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
    
    public boolean isSampleSubstance() {
        return isSampleSub;
    }

    public SubstanceInteraction[] getReactions() {
        if (reactions == null) {
            return new SubstanceInteraction[0];
        }

        return reactions;
    }

    public void addReaction(SubstanceInteraction si) {
        if (reactions == null) {
            reactions = new SubstanceInteraction[0];
        }

        if (si.getReactant().getId() != id && reactsWith(si.getReactant().getId())) {
            throw new IllegalArgumentException("Cannot define two different interactions with the same reactant.");
        }

        List<SubstanceInteraction> asList = new ArrayList<>(Arrays.asList(reactions));
        asList.add(si);

        SubstanceInteraction[] asArray = new SubstanceInteraction[asList.size()];
        reactions = asList.toArray(asArray);

        if (si.getReactant().getId() != id && si.getReactant().getId() != -1 && !si.getReactant().reactsWith(id)) {
            si.getReactant().addReaction(new SubstanceInteraction(this, si.getProduct(), si.getReactantOutcome(), si.getSourceOutcome(), si.getVolatility()));
        }
    }
    
    public void removeReaction(SubstanceInteraction si) {
        if (reactions == null || reactions.length == 0) {
            return;
        }

        List<SubstanceInteraction> asList = new ArrayList<>(Arrays.asList(reactions));
        asList.remove(si);

        SubstanceInteraction[] asArray = new SubstanceInteraction[asList.size()];
        reactions = asList.toArray(asArray);
    }

    public boolean reactsWith(int subId) {
        if (reactions == null || reactions.length == 0) {
            return false;
        }
        for (SubstanceInteraction reaction : reactions) {
            if (reaction.getReactant().getId() == subId) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        return name == null ? "[NoName]" : name;
    }

    public enum State {
        SOLID,
        LIQUID,
        GAS
    }
}
