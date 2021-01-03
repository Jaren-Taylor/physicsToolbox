package com.mycompany.physicstoolbox;

import com.mycompany.physicstoolbox.Substance.State;
import com.mycompany.physicstoolbox.SubstanceInteraction.ReactionOutcome;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public abstract class UI {
    
    private static SubstanceEditor editor;
    private static SubstanceMenu menu;
    
    public static SubstanceEditor getSubstanceEditorComponent(Dimension frameDim, int viewportWidth) {
        editor = new SubstanceEditor(frameDim, viewportWidth);
        return editor;
    }
    
    public static SubstanceMenu getSubstanceMenuComponent(Dimension frameDim, Dimension viewportDim) {
        menu = new SubstanceMenu(frameDim, viewportDim);
        return menu;
    }
    
    public static void setEditorSubstance(Substance sub) {
        editor.setSelectedSubstance(sub);
    }
    
    public static void setMenuOptions(Substance[] subs) {
        menu.updateOptions(subs);
    }
    
    public static void initializeSelectedSubstances() {
        Substance.setCurrentlySelected(Substance.WALL);
        Substance.setCurrentlySelected(Substance.CREATE_NEW);
        
        Substance.setAlternateSelected(Substance.WATER);
    }
    
    private static class SubstanceEditor extends JPanel {
        
        private ActionHandler handler = new ActionHandler(); 
        
        private JLabel titleLbl, nameLbl, colorLbl, viscosityLbl, weightLbl, densityLbl, stateLbl, reactionsLbl,
                       reactantLbl, productLbl, srcOutcomeLbl, rctOutcomeLbl, volatilityLbl;
        private ColorBox colorPanel, reactantPanel, productPanel;
        private JTextField nameInput;
        private JSlider rSlider, gSlider, bSlider, viscositySlider, weightSlider, densitySlider,
                        flammableSlider, decaySlider, volatilitySlider;
        private JRadioButton solidRadio, liquidRadio, gasRadio, srcUnchangedRadio, srcChangedRadio,
                             srcDestroyedRadio, rctUnchangedRadio, rctChangedRadio, rctDestroyedRadio;
        private ButtonGroup stateGroup, srcOutcomeGroup, rctOutcomeGroup;
        private JCheckBox flammableBox, decayBox;
        private JComboBox reactionsDropdown, reactantDropdown, productDropdown;
        private JButton deleteReactionBtn, saveReactionBtn, resetBtn, saveBtn, deleteBtn;
        
        private Substance selectedSubstance = Substance.CREATE_NEW;
        private List<Substance> substanceExclusions = new ArrayList<>();
        private List<SubstanceInteraction> substanceInteractions = new ArrayList<>();
        
        private final int PADDING = 6;
        
        private final int TITLE_COMPONENT_HEIGHT = 30;
        private final int COMPONENT_HEIGHT = 18;
        private final int COMPONENT_WIDTH;
        
        private final int REACTANT_PRODUCT_LABEL_WIDTH = 60;
        private final int FLAMMABLE_DECAY_LABEL_WIDTH = 90;
        private final int SOURCE_REACTANT_LABEL_WIDTH = 60;
        private final int VOLATILITY_LABEL_WIDTH = 60;
        private final int RESET_BUTTON_WIDTH = 80;
        private final int DELETE_ADD_BUTTON_WIDTH = 130;
        private final int DELETE_SAVE_BUTTON_HEIGHT = 40;
        
        private final Font TITLE_FONT = new Font("Bahnschrift", Font.PLAIN, 30);
        private final Font LABEL_FONT = new Font("Bahnschrift", Font.PLAIN, 16);
        private final Font BUTTON_FONT = new Font("Bahnschrift", Font.BOLD, 20);
        
        private SubstanceEditor(Dimension frameDim, int viewportWidth) {
            super();
            
            setLayout(null);
            setBounds(5, 5, frameDim.width - (viewportWidth + 30), frameDim.height - 50);
            setBorder(BorderFactory.createEtchedBorder());
            
            COMPONENT_WIDTH = getWidth() - (2 * PADDING);
            
            // Initializing UI components
            titleLbl = new JLabel("Substance Editor");
            titleLbl.setBounds(2 * PADDING, 2 * PADDING, COMPONENT_WIDTH, TITLE_COMPONENT_HEIGHT);
            titleLbl.setFont(TITLE_FONT);
            
            nameLbl = new JLabel("Name");
            nameLbl.setBounds(PADDING, TITLE_COMPONENT_HEIGHT + COMPONENT_HEIGHT, COMPONENT_WIDTH, COMPONENT_HEIGHT);
            nameLbl.setFont(LABEL_FONT);
            
            colorLbl = new JLabel("Color");
            colorLbl.setBounds(PADDING, TITLE_COMPONENT_HEIGHT + (4 * COMPONENT_HEIGHT) + PADDING, COMPONENT_WIDTH, COMPONENT_HEIGHT);
            colorLbl.setFont(LABEL_FONT);
            
            viscosityLbl = new JLabel("Viscosity");
            viscosityLbl.setBounds(PADDING, TITLE_COMPONENT_HEIGHT + (9 * COMPONENT_HEIGHT) + PADDING, COMPONENT_WIDTH, COMPONENT_HEIGHT);
            viscosityLbl.setFont(LABEL_FONT);
            
            weightLbl = new JLabel("Weight");
            weightLbl.setBounds(PADDING, TITLE_COMPONENT_HEIGHT + (12 * COMPONENT_HEIGHT) + PADDING, COMPONENT_WIDTH, COMPONENT_HEIGHT);
            weightLbl.setFont(LABEL_FONT);
            
            densityLbl = new JLabel("Density");
            densityLbl.setBounds(PADDING, TITLE_COMPONENT_HEIGHT + (15 * COMPONENT_HEIGHT) + PADDING, COMPONENT_WIDTH, COMPONENT_HEIGHT);
            densityLbl.setFont(LABEL_FONT);
            
            stateLbl = new JLabel("State");
            stateLbl.setBounds(PADDING, TITLE_COMPONENT_HEIGHT + (18 * COMPONENT_HEIGHT) + PADDING, COMPONENT_WIDTH, COMPONENT_HEIGHT);
            stateLbl.setFont(LABEL_FONT);
            
            reactionsLbl = new JLabel("Reactions");
            reactionsLbl.setBounds(PADDING, TITLE_COMPONENT_HEIGHT + (21 * COMPONENT_HEIGHT) + PADDING, COMPONENT_WIDTH, COMPONENT_HEIGHT);
            reactionsLbl.setFont(LABEL_FONT);
            
            reactantLbl = new JLabel("Reactant:");
            reactantLbl.setBounds(PADDING, TITLE_COMPONENT_HEIGHT + (27 * COMPONENT_HEIGHT) + PADDING, REACTANT_PRODUCT_LABEL_WIDTH, COMPONENT_HEIGHT);
            
            productLbl = new JLabel("Product:");
            productLbl.setBounds(PADDING, TITLE_COMPONENT_HEIGHT + (28 * COMPONENT_HEIGHT) + PADDING, REACTANT_PRODUCT_LABEL_WIDTH, COMPONENT_HEIGHT);
            
            srcOutcomeLbl = new JLabel("Source:");
            srcOutcomeLbl.setBounds(PADDING, TITLE_COMPONENT_HEIGHT + (30 * COMPONENT_HEIGHT) + PADDING, SOURCE_REACTANT_LABEL_WIDTH, COMPONENT_HEIGHT);
            
            rctOutcomeLbl = new JLabel("Reactant:");
            rctOutcomeLbl.setBounds(PADDING, TITLE_COMPONENT_HEIGHT + (31 * COMPONENT_HEIGHT) + PADDING, SOURCE_REACTANT_LABEL_WIDTH, COMPONENT_HEIGHT);
            
            volatilityLbl = new JLabel("Volatility:");
            volatilityLbl.setBounds(PADDING, TITLE_COMPONENT_HEIGHT + (33 * COMPONENT_HEIGHT) + PADDING, VOLATILITY_LABEL_WIDTH, COMPONENT_HEIGHT);
            
            /* ---------- */
            
            colorPanel = new ColorBox(new Color(128, 128, 128));
            colorPanel.setBounds(PADDING, TITLE_COMPONENT_HEIGHT + (5 * COMPONENT_HEIGHT) + PADDING, 3 * COMPONENT_HEIGHT, 3 * COMPONENT_HEIGHT);
            
            reactantPanel = new ColorBox(new Color(128, 128, 128));
            reactantPanel.setBounds(REACTANT_PRODUCT_LABEL_WIDTH + (2 * PADDING), TITLE_COMPONENT_HEIGHT + (27 * COMPONENT_HEIGHT) + PADDING, COMPONENT_HEIGHT, COMPONENT_HEIGHT);
            
            productPanel = new ColorBox(new Color(128, 128, 128));
            productPanel.setBounds(REACTANT_PRODUCT_LABEL_WIDTH + (2 * PADDING), TITLE_COMPONENT_HEIGHT + (28 * COMPONENT_HEIGHT) + PADDING, COMPONENT_HEIGHT, COMPONENT_HEIGHT);
            
            /* ---------- */
            
            nameInput = new JTextField();
            nameInput.setBounds(PADDING, TITLE_COMPONENT_HEIGHT + (2 * COMPONENT_HEIGHT) + PADDING, COMPONENT_WIDTH, COMPONENT_HEIGHT);
            nameInput.getDocument().addDocumentListener(handler);
            
            /* ---------- */
            
            rSlider = new JSlider(0, 255, 128);
            rSlider.setBounds(colorPanel.getWidth() + PADDING, TITLE_COMPONENT_HEIGHT + (5 * COMPONENT_HEIGHT) + PADDING, COMPONENT_WIDTH - colorPanel.getWidth(), COMPONENT_HEIGHT);
            rSlider.addChangeListener(handler);
            
            gSlider = new JSlider(0, 255, 128);
            gSlider.setBounds(colorPanel.getWidth() + PADDING, TITLE_COMPONENT_HEIGHT + (6 * COMPONENT_HEIGHT) + PADDING, COMPONENT_WIDTH - colorPanel.getWidth(), COMPONENT_HEIGHT);
            gSlider.addChangeListener(handler);
            
            bSlider = new JSlider(0, 255, 128);
            bSlider.setBounds(colorPanel.getWidth() + PADDING, TITLE_COMPONENT_HEIGHT + (7 * COMPONENT_HEIGHT) + PADDING, COMPONENT_WIDTH - colorPanel.getWidth(), COMPONENT_HEIGHT);
            bSlider.addChangeListener(handler);
            
            viscositySlider = new JSlider(0, 100, 0);
            viscositySlider.setBounds(PADDING, TITLE_COMPONENT_HEIGHT + (10 * COMPONENT_HEIGHT) + PADDING, COMPONENT_WIDTH, COMPONENT_HEIGHT + 10);
            viscositySlider.setMajorTickSpacing(100);
            viscositySlider.setPaintTicks(true);
            
            weightSlider = new JSlider(-100, 100, 0);
            weightSlider.setBounds(PADDING, TITLE_COMPONENT_HEIGHT + (13 * COMPONENT_HEIGHT) + PADDING, COMPONENT_WIDTH, COMPONENT_HEIGHT + 10);
            weightSlider.setMajorTickSpacing(100);
            weightSlider.setPaintTicks(true);
            
            densitySlider = new JSlider(0, 100, 0);
            densitySlider.setBounds(PADDING, TITLE_COMPONENT_HEIGHT + (16 * COMPONENT_HEIGHT) + PADDING, COMPONENT_WIDTH, COMPONENT_HEIGHT + 10);
            densitySlider.setMajorTickSpacing(100);
            densitySlider.setPaintTicks(true);
            
            flammableSlider = new JSlider(0, 100, 0);
            flammableSlider.setBounds(FLAMMABLE_DECAY_LABEL_WIDTH + (2 * PADDING), TITLE_COMPONENT_HEIGHT + (22 * COMPONENT_HEIGHT) + PADDING, COMPONENT_WIDTH - (FLAMMABLE_DECAY_LABEL_WIDTH + PADDING), COMPONENT_HEIGHT);
            flammableSlider.setEnabled(false);
            
            decaySlider = new JSlider(0, 100, 0);
            decaySlider.setBounds(FLAMMABLE_DECAY_LABEL_WIDTH + (2 * PADDING), TITLE_COMPONENT_HEIGHT + (23 * COMPONENT_HEIGHT) + PADDING, COMPONENT_WIDTH - (FLAMMABLE_DECAY_LABEL_WIDTH + PADDING), COMPONENT_HEIGHT);
            decaySlider.setEnabled(false);
            
            volatilitySlider = new JSlider(0, 100, 0);
            volatilitySlider.setBounds(VOLATILITY_LABEL_WIDTH + (2 * PADDING), TITLE_COMPONENT_HEIGHT + (33 * COMPONENT_HEIGHT) + PADDING, COMPONENT_WIDTH - (VOLATILITY_LABEL_WIDTH + PADDING), COMPONENT_HEIGHT);
            
            /* ---------- */
            
            solidRadio = new JRadioButton("Solid", false);
            solidRadio.setBounds(PADDING, TITLE_COMPONENT_HEIGHT + (19 * COMPONENT_HEIGHT) + PADDING, COMPONENT_WIDTH / 3, COMPONENT_HEIGHT);
            solidRadio.addActionListener(handler);
            
            liquidRadio = new JRadioButton("Liquid", true);
            liquidRadio.setBounds(PADDING + (COMPONENT_WIDTH / 3), TITLE_COMPONENT_HEIGHT + (19 * COMPONENT_HEIGHT) + PADDING, COMPONENT_WIDTH / 3, COMPONENT_HEIGHT);
            liquidRadio.addActionListener(handler);
            
            gasRadio = new JRadioButton("Gas", false);
            gasRadio.setBounds(PADDING + ((2 * COMPONENT_WIDTH) / 3), TITLE_COMPONENT_HEIGHT + (19 * COMPONENT_HEIGHT) + PADDING, COMPONENT_WIDTH / 3, COMPONENT_HEIGHT);
            gasRadio.addActionListener(handler);
            
            srcUnchangedRadio = new JRadioButton("No Change", false);
            srcUnchangedRadio.setBounds(SOURCE_REACTANT_LABEL_WIDTH + (2 * PADDING), TITLE_COMPONENT_HEIGHT + (30 * COMPONENT_HEIGHT) + PADDING, (COMPONENT_WIDTH - SOURCE_REACTANT_LABEL_WIDTH) / 3, COMPONENT_HEIGHT);
            srcUnchangedRadio.addActionListener(handler);
            
            srcChangedRadio = new JRadioButton("Change", true);
            srcChangedRadio.setBounds(SOURCE_REACTANT_LABEL_WIDTH + ((COMPONENT_WIDTH - SOURCE_REACTANT_LABEL_WIDTH) / 3) + (2 * PADDING), TITLE_COMPONENT_HEIGHT + (30 * COMPONENT_HEIGHT) + PADDING, (COMPONENT_WIDTH - SOURCE_REACTANT_LABEL_WIDTH) / 3, COMPONENT_HEIGHT);
            srcChangedRadio.addActionListener(handler);
            
            srcDestroyedRadio = new JRadioButton("Destroy", false);
            srcDestroyedRadio.setBounds(SOURCE_REACTANT_LABEL_WIDTH + ((2 * (COMPONENT_WIDTH - SOURCE_REACTANT_LABEL_WIDTH)) / 3) + (2 * PADDING), TITLE_COMPONENT_HEIGHT + (30 * COMPONENT_HEIGHT) + PADDING, ((COMPONENT_WIDTH - SOURCE_REACTANT_LABEL_WIDTH) / 3) - (4 * PADDING), COMPONENT_HEIGHT);
            srcDestroyedRadio.addActionListener(handler);
            
            rctUnchangedRadio = new JRadioButton("No Change", false);
            rctUnchangedRadio.setBounds(SOURCE_REACTANT_LABEL_WIDTH + (2 * PADDING), TITLE_COMPONENT_HEIGHT + (31 * COMPONENT_HEIGHT) + PADDING, (COMPONENT_WIDTH - SOURCE_REACTANT_LABEL_WIDTH) / 3, COMPONENT_HEIGHT);
            rctUnchangedRadio.addActionListener(handler);
            
            rctChangedRadio = new JRadioButton("Change", true);
            rctChangedRadio.setBounds(SOURCE_REACTANT_LABEL_WIDTH + ((COMPONENT_WIDTH - SOURCE_REACTANT_LABEL_WIDTH) / 3) + (2 * PADDING), TITLE_COMPONENT_HEIGHT + (31 * COMPONENT_HEIGHT) + PADDING, (COMPONENT_WIDTH - SOURCE_REACTANT_LABEL_WIDTH) / 3, COMPONENT_HEIGHT);
            rctChangedRadio.addActionListener(handler);
            
            rctDestroyedRadio = new JRadioButton("Destroy", false);
            rctDestroyedRadio.setBounds(SOURCE_REACTANT_LABEL_WIDTH + ((2 * (COMPONENT_WIDTH - SOURCE_REACTANT_LABEL_WIDTH)) / 3) + (2 * PADDING), TITLE_COMPONENT_HEIGHT + (31 * COMPONENT_HEIGHT) + PADDING, ((COMPONENT_WIDTH - SOURCE_REACTANT_LABEL_WIDTH) / 3) - (4 * PADDING), COMPONENT_HEIGHT);
            rctDestroyedRadio.addActionListener(handler);
            
            /* ---------- */
            
            stateGroup = new ButtonGroup();
            srcOutcomeGroup = new ButtonGroup();
            rctOutcomeGroup = new ButtonGroup();
            
            /* ---------- */
            
            flammableBox = new JCheckBox("Flammable");
            flammableBox.setBounds(PADDING, TITLE_COMPONENT_HEIGHT + (22 * COMPONENT_HEIGHT) + PADDING, FLAMMABLE_DECAY_LABEL_WIDTH, COMPONENT_HEIGHT);
            flammableBox.addActionListener(handler);
            
            decayBox = new JCheckBox("Decays");
            decayBox.setBounds(PADDING, TITLE_COMPONENT_HEIGHT + (23 * COMPONENT_HEIGHT) + PADDING, FLAMMABLE_DECAY_LABEL_WIDTH, COMPONENT_HEIGHT);
            decayBox.addActionListener(handler);
            
            /* ---------- */
            
            substanceInteractions.add(SubstanceInteraction.CREATE_NEW);
            
            reactionsDropdown = new JComboBox(getSubstanceInteractions());
            reactionsDropdown.setBounds(PADDING, TITLE_COMPONENT_HEIGHT + (25 * COMPONENT_HEIGHT) + PADDING, COMPONENT_WIDTH, COMPONENT_HEIGHT);
            reactionsDropdown.addActionListener(handler);
            
            reactantDropdown = new JComboBox(getSubsWithExclusions(true));
            reactantDropdown.setBounds(REACTANT_PRODUCT_LABEL_WIDTH + reactantPanel.getWidth() + (3 * PADDING), TITLE_COMPONENT_HEIGHT + (27 * COMPONENT_HEIGHT) + PADDING, COMPONENT_WIDTH - (REACTANT_PRODUCT_LABEL_WIDTH + reactantPanel.getWidth() + (2 * PADDING)), COMPONENT_HEIGHT);
            reactantDropdown.addActionListener(handler);
            
            productDropdown = new JComboBox(Substance.getSavedSubstances()); // Substance.NONE cannot be a product
            productDropdown.setBounds(REACTANT_PRODUCT_LABEL_WIDTH + productPanel.getWidth() + (3 * PADDING), TITLE_COMPONENT_HEIGHT + (28 * COMPONENT_HEIGHT) + PADDING, COMPONENT_WIDTH - (REACTANT_PRODUCT_LABEL_WIDTH + productPanel.getWidth() + (2 * PADDING)), COMPONENT_HEIGHT);
            productDropdown.addActionListener(handler);
            
            reactantPanel.setFillColor(((Substance) reactantDropdown.getSelectedItem()).getColor());
            productPanel.setFillColor(((Substance) productDropdown.getSelectedItem()).getColor());
            
            /* ---------- */
            
            deleteReactionBtn = new JButton("Delete Reaction");
            deleteReactionBtn.setBounds(getWidth() - ((2 * DELETE_ADD_BUTTON_WIDTH) + (2 * PADDING)), TITLE_COMPONENT_HEIGHT + (35 * COMPONENT_HEIGHT) + PADDING, DELETE_ADD_BUTTON_WIDTH, COMPONENT_HEIGHT);
            deleteReactionBtn.addActionListener(handler);
            deleteReactionBtn.setEnabled(false);
            
            saveReactionBtn = new JButton("Save Reaction");
            saveReactionBtn.setBounds(getWidth() - (DELETE_ADD_BUTTON_WIDTH + PADDING), TITLE_COMPONENT_HEIGHT + (35 * COMPONENT_HEIGHT) + PADDING, DELETE_ADD_BUTTON_WIDTH, COMPONENT_HEIGHT);
            saveReactionBtn.addActionListener(handler);
            
            resetBtn = new JButton("Reset");
            resetBtn.setBounds(getWidth() - RESET_BUTTON_WIDTH - PADDING, PADDING, RESET_BUTTON_WIDTH, COMPONENT_HEIGHT);
            resetBtn.addActionListener(handler);
            
            saveBtn = new JButton("Save");
            saveBtn.setBounds((COMPONENT_WIDTH / 2) + PADDING, getHeight() - (DELETE_SAVE_BUTTON_HEIGHT + PADDING), (COMPONENT_WIDTH - PADDING) / 2, DELETE_SAVE_BUTTON_HEIGHT);
            saveBtn.setFont(BUTTON_FONT);
            saveBtn.addActionListener(handler);
            
            deleteBtn = new JButton("Delete");
            deleteBtn.setBounds(PADDING, getHeight() - (DELETE_SAVE_BUTTON_HEIGHT + PADDING), (COMPONENT_WIDTH - PADDING) / 2, DELETE_SAVE_BUTTON_HEIGHT);
            deleteBtn.setFont(BUTTON_FONT);
            deleteBtn.addActionListener(handler);
            
            // Adding components to panel
            add(titleLbl);
            add(nameLbl);
            add(colorLbl);
            add(viscosityLbl);
            add(weightLbl);
            add(densityLbl);
            add(stateLbl);
            add(reactionsLbl);
            add(reactantLbl);
            add(productLbl);
            add(srcOutcomeLbl);
            add(rctOutcomeLbl);
            add(volatilityLbl);
            
            add(colorPanel);
            add(reactantPanel);
            add(productPanel);
            
            add(nameInput);
            
            add(rSlider);
            add(gSlider);
            add(bSlider);
            add(viscositySlider);
            add(weightSlider);
            add(densitySlider);
            add(decaySlider);
            add(flammableSlider);
            add(volatilitySlider);
            
            stateGroup.add(solidRadio);
            stateGroup.add(liquidRadio);
            stateGroup.add(gasRadio);
            add(solidRadio);
            add(liquidRadio);
            add(gasRadio);
            
            srcOutcomeGroup.add(srcUnchangedRadio);
            srcOutcomeGroup.add(srcChangedRadio);
            srcOutcomeGroup.add(srcDestroyedRadio);
            rctOutcomeGroup.add(rctUnchangedRadio);
            rctOutcomeGroup.add(rctChangedRadio);
            rctOutcomeGroup.add(rctDestroyedRadio);
            add(srcUnchangedRadio);
            add(srcChangedRadio);
            add(srcDestroyedRadio);
            add(rctUnchangedRadio);
            add(rctChangedRadio);
            add(rctDestroyedRadio);
            
            add(decayBox);
            add(flammableBox);
            
            add(reactionsDropdown);
            add(reactantDropdown);
            add(productDropdown);
            
            add(deleteReactionBtn);
            add(saveReactionBtn);
            add(resetBtn);
            add(saveBtn);
            add(deleteBtn);
        }
        
        public void setSelectedSubstance(Substance selected) {
            selectedSubstance = selected == null ? Substance.CREATE_NEW : selected;
            
            resetFullEditor();
            setFullEditorDisabled(selected.isSampleSubstance());
        }
        
        private Substance[] getSubsWithExclusions(boolean exclude) {
            List<Substance> subs = new ArrayList<>();
            
            subs.add(Substance.NONE);
            subs.addAll(Arrays.asList(Substance.getSavedSubstances()));
            
            if(exclude) {
                subs.remove(Substance.WALL); // Substance.WALL cannot be a reactant
                
                for(Substance sub : substanceExclusions) {
                    if(subs.contains(sub) && !sub.equals(((SubstanceInteraction) reactionsDropdown.getSelectedItem()).getReactant())) {
                        subs.remove(sub);
                    }
                }
            }
            
            Substance[] finalSubs = new Substance[subs.size()];
            return subs.toArray(finalSubs);
        }
        
        private SubstanceInteraction[] getSubstanceInteractions() {
            SubstanceInteraction[] array = new SubstanceInteraction[substanceInteractions.size()];
            return substanceInteractions.toArray(array);
        }
        
        private void saveSubstance() {
            String name = nameInput.getText().trim();
            Color color = new Color(rSlider.getValue(), gSlider.getValue(), bSlider.getValue());
            double viscosity = (double) viscositySlider.getValue() / 100.0;
            double weight = (double) weightSlider.getValue() / 100.0;
            double density = (double) densitySlider.getValue() / 100.0;
            State state = solidRadio.isSelected() ? State.SOLID : liquidRadio.isSelected() ? State.LIQUID : State.GAS;
            
            SubstanceInteraction[] reactions = new SubstanceInteraction[substanceInteractions.size()];
            reactions = substanceInteractions.toArray(reactions);
            
            Substance newSub = new Substance(color, name, viscosity, weight, density, state, false);
            
            if(flammableBox.isSelected()) {
                newSub.addReaction(new SubstanceInteraction(Substance.FIRE, Substance.FIRE, ReactionOutcome.CHANGED, ReactionOutcome.UNCHANGED, (double) flammableSlider.getValue() / 100.0));
            }
            if(decayBox.isSelected()) {
                newSub.addReaction(new SubstanceInteraction(Substance.NONE, Substance.NONE, ReactionOutcome.CHANGED, ReactionOutcome.UNCHANGED, (double) decaySlider.getValue() / 100.0));
                newSub.addReaction(new SubstanceInteraction(newSub, Substance.NONE, ReactionOutcome.CHANGED, ReactionOutcome.UNCHANGED, (double) decaySlider.getValue() / 100.0));
            }
            
            for(SubstanceInteraction si: substanceInteractions) {
                if(!si.equals(SubstanceInteraction.CREATE_NEW)) {
                    newSub.addReaction(si);
                }
            }
            
            if(selectedSubstance.equals(Substance.CREATE_NEW)) {
                Substance.addCustomSubstance(newSub);
            } else {
                Substance.editCustomSubstance(selectedSubstance.getId(), newSub);
            }
            
            setSelectedSubstance(Substance.CREATE_NEW);
        }
        
        private void deleteSubstance() {
            Substance.removeCustomSubstance(selectedSubstance);
            setSelectedSubstance(Substance.CREATE_NEW);
        }
        
        private void saveReaction(SubstanceInteraction interaction) {
            Substance reactant = (Substance) reactantDropdown.getSelectedItem();
            Substance product = (Substance) productDropdown.getSelectedItem();
            ReactionOutcome sourceOutcome = srcUnchangedRadio.isSelected() ? ReactionOutcome.UNCHANGED : srcChangedRadio.isSelected() ? ReactionOutcome.CHANGED : ReactionOutcome.DESTROYED;
            ReactionOutcome reactantOutcome = rctUnchangedRadio.isSelected() ? ReactionOutcome.UNCHANGED : rctChangedRadio.isSelected() ? ReactionOutcome.CHANGED : ReactionOutcome.DESTROYED;
            double volatility = (double) volatilitySlider.getValue() / 100.0;
            
            SubstanceInteraction newReaction = new SubstanceInteraction(reactant, product, sourceOutcome, reactantOutcome, volatility);
            
            if(interaction.equals(SubstanceInteraction.CREATE_NEW)) {
                substanceInteractions.add(newReaction);
            } else {
                substanceInteractions.set(substanceInteractions.indexOf(interaction), newReaction);
            }
            
            reactionsDropdown.setModel(new DefaultComboBoxModel(getSubstanceInteractions()));
            
            boolean containsFire = false;
            boolean containsVoid = false;
            for(SubstanceInteraction si: substanceInteractions) {
                if(si.equals(SubstanceInteraction.CREATE_NEW)) {
                    continue;
                }
                
                if(si.getReactant().equals(Substance.FIRE)) {
                    containsFire = true;
                } else if(si.getReactant().equals(Substance.NONE)) {
                    containsVoid = true;
                }
            }
            
            setFlammableBoxDisabled(containsFire);
            setDecayBoxDisabled(containsVoid);
            
            substanceExclusions.add(reactant);
            
            resetReactionsComponent(SubstanceInteraction.CREATE_NEW);
        }
        
        private void deleteReaction(SubstanceInteraction interaction) {
            substanceInteractions.remove(interaction);
            
            boolean containsFire = false;
            boolean containsVoid = false;
            for(SubstanceInteraction si: substanceInteractions) {
                if(si.equals(SubstanceInteraction.CREATE_NEW)) {
                    continue;
                }
                
                if(si.getReactant().equals(Substance.FIRE)) {
                    containsFire = true;
                } else if(si.getReactant().equals(Substance.NONE)) {
                    containsVoid = true;
                }
            }
            
            setFlammableBoxDisabled(containsFire);
            setDecayBoxDisabled(containsVoid);
            
            substanceExclusions.remove(interaction.getReactant());
            
            reactionsDropdown.setSelectedIndex(0);
            resetReactionsComponent((SubstanceInteraction) reactionsDropdown.getSelectedItem());
            
            reactionsDropdown.setModel(new DefaultComboBoxModel(getSubstanceInteractions()));
        }
        
        private void resetFullEditor() {
            if(selectedSubstance.equals(Substance.CREATE_NEW)) {
                substanceExclusions = new ArrayList<>();
                
                nameInput.setText("");
                
                rSlider.setValue(128);
                gSlider.setValue(128);
                bSlider.setValue(128);
                colorPanel.setFillColor(new Color(128, 128, 128));
                
                viscositySlider.setValue(0);
                weightSlider.setValue(0);
                densitySlider.setValue(0);
                
                solidRadio.setSelected(false);
                liquidRadio.setSelected(true);
                gasRadio.setSelected(false);
                
                substanceInteractions = new ArrayList<>();
                substanceInteractions.add(SubstanceInteraction.CREATE_NEW);
                
                flammableBox.setSelected(false);
                flammableSlider.setValue(0);
                decayBox.setSelected(false);
                decaySlider.setValue(0);
                
                reactionsDropdown.setSelectedIndex(0);
                reactionsDropdown.setModel(new DefaultComboBoxModel(getSubstanceInteractions()));
                resetReactionsComponent((SubstanceInteraction) reactionsDropdown.getSelectedItem());
            } else {
                substanceExclusions = new ArrayList<>();
                
                nameInput.setText(selectedSubstance.getName());
                
                Color subColor = selectedSubstance.getColor();
                rSlider.setValue(subColor.getRed());
                gSlider.setValue(subColor.getGreen());
                bSlider.setValue(subColor.getBlue());
                colorPanel.setFillColor(subColor);
                
                viscositySlider.setValue((int) (selectedSubstance.getViscosity() * 100));
                weightSlider.setValue((int) (selectedSubstance.getWeight() * 100));
                densitySlider.setValue((int) (selectedSubstance.getDensity() * 100));
                
                State subState = selectedSubstance.getState();
                solidRadio.setSelected(subState == State.SOLID);
                liquidRadio.setSelected(subState == State.LIQUID);
                gasRadio.setSelected(subState == State.GAS);
                
                SubstanceInteraction[] subInteractions = selectedSubstance.getReactions();
                SubstanceInteraction flammableReaction = null;
                SubstanceInteraction decayVoidReaction = null;
                SubstanceInteraction decaySelfReaction = null;
                
                for(SubstanceInteraction si: subInteractions) {
                    substanceExclusions.add(si.getReactant());
                    
                    if(si.getReactant().equals(Substance.FIRE) && si.getProduct().equals(Substance.FIRE)) {
                        flammableReaction = si;
                        substanceExclusions.remove(Substance.FIRE);
                    }
                    if(si.getReactant().equals(Substance.NONE) && si.getProduct().equals(Substance.NONE)) {
                        decayVoidReaction = si;
                    }
                    if(si.getReactant().equals(selectedSubstance) && si.getProduct().equals(Substance.NONE)) {
                        decaySelfReaction = si;
                    }
                }
                
                substanceInteractions = new ArrayList<>();
                substanceInteractions.add(SubstanceInteraction.CREATE_NEW);
                substanceInteractions.addAll(Arrays.asList(subInteractions));
                
                if(flammableReaction != null) {
                    substanceInteractions.remove(flammableReaction);
                }
                if(decayVoidReaction != null && decaySelfReaction != null) {
                    substanceInteractions.remove(decayVoidReaction);
                    substanceInteractions.remove(decaySelfReaction);
                    substanceExclusions.remove(Substance.NONE);
                }
                
                flammableBox.setSelected(flammableReaction != null);
                flammableSlider.setValue(flammableReaction == null ? 0 : (int) (flammableReaction.getVolatility() * 100));
                decayBox.setSelected(decayVoidReaction != null && decaySelfReaction != null);
                decaySlider.setValue(decayVoidReaction == null || decaySelfReaction == null ? 0 : (int) (decayVoidReaction.getVolatility() * 100));
                
                reactionsDropdown.setSelectedIndex(0);
                reactionsDropdown.setModel(new DefaultComboBoxModel(getSubstanceInteractions()));
                resetReactionsComponent((SubstanceInteraction) reactionsDropdown.getSelectedItem());
            }
            
            setFullEditorDisabled(false);
        }
        
        private void resetReactionsComponent(SubstanceInteraction selected) {
            reactantDropdown.setModel(new DefaultComboBoxModel(getSubsWithExclusions(true)));
            productDropdown.setModel(new DefaultComboBoxModel(Substance.getSavedSubstances()));
            
            if(selected == SubstanceInteraction.CREATE_NEW) {
                reactionsDropdown.setSelectedIndex(0);
                reactantDropdown.setSelectedIndex(0);
                productDropdown.setSelectedIndex(0);
                
                srcUnchangedRadio.setSelected(false);
                srcChangedRadio.setSelected(true);
                srcDestroyedRadio.setSelected(false);
                
                rctUnchangedRadio.setSelected(false);
                rctChangedRadio.setSelected(true);
                rctDestroyedRadio.setSelected(false);
                
                setSourceUnchangedDisabled(false);
                setReactantUnchangedDisabled(false);
                
                volatilitySlider.setValue(0);
            } else {
                reactantDropdown.setSelectedItem(selected.getReactant());
                productDropdown.setSelectedItem(selected.getProduct());
                
                srcUnchangedRadio.setSelected(selected.getSourceOutcome() == ReactionOutcome.UNCHANGED);
                srcChangedRadio.setSelected(selected.getSourceOutcome() == ReactionOutcome.CHANGED);
                srcDestroyedRadio.setSelected(selected.getSourceOutcome() == ReactionOutcome.DESTROYED);
                
                rctUnchangedRadio.setSelected(selected.getReactantOutcome() == ReactionOutcome.UNCHANGED);
                rctChangedRadio.setSelected(selected.getReactantOutcome() == ReactionOutcome.CHANGED);
                rctDestroyedRadio.setSelected(selected.getReactantOutcome() == ReactionOutcome.DESTROYED);
                
                setSourceUnchangedDisabled(rctUnchangedRadio.isSelected());
                setReactantUnchangedDisabled(srcUnchangedRadio.isSelected());
                
                volatilitySlider.setValue((int) (selected.getVolatility() * 100));
            }
        }
        
        private void setFullEditorDisabled(boolean set) {
            titleLbl.setForeground(set ? Color.LIGHT_GRAY : Color.BLACK);
            
            setResetDisabled(set);
            setNameDisabled(set);
            setColorDisabled(set);
            setStateDisabled(set);
            reactionsLbl.setForeground(set ? Color.LIGHT_GRAY : Color.BLACK);
            setReactionsDropdownDisabled(set);
            setReactantDropdownDisabled(set);
            setProductDropdownDisabled(set);
            setSourceUnchangedDisabled(set);
            setSourceChangedDisabled(set);
            setSourceDestroyedDisabled(set);
            setReactantUnchangedDisabled(set);
            setVolatilityDisabled(set);
            
            if(set) {
                setViscosityDisabled(true);
                setWeightDisabled(true);
                setDensityDisabled(true);
                setFlammableBoxDisabled(true);
                setDecayBoxDisabled(true);
                setReactantChangedDisabled(true);
                setReactantDestroyedDisabled(true);
                setDeleteReactionDisabled(true);
                setSaveReactionDisabled(true);
                setDeleteDisabled(true);
                setSaveDisabled(true);
            } else {
                setViscosityDisabled(!liquidRadio.isSelected());
                setWeightDisabled(!liquidRadio.isSelected());
                setDensityDisabled(solidRadio.isSelected());
                
                boolean containsFire = false;
                boolean containsVoid = false;
                for(SubstanceInteraction si : substanceInteractions) {
                    if(si.equals(SubstanceInteraction.CREATE_NEW)) {
                        continue;
                    }

                    if(si.getReactant().equals(Substance.FIRE) && !si.getProduct().equals(Substance.FIRE)) {
                        containsFire = true;
                    } else if(si.getReactant().equals(Substance.NONE) && !si.getProduct().equals(Substance.NONE)) {
                        containsVoid = true;
                    }
                }
                
                setFlammableBoxDisabled(containsFire);
                setDecayBoxDisabled(containsVoid);
                
                if(((Substance) reactantDropdown.getSelectedItem()).equals(Substance.NONE)) {
                    rctUnchangedRadio.setSelected(true);
                    rctChangedRadio.setSelected(false);
                    rctDestroyedRadio.setSelected(false);
                    
                    setSourceUnchangedDisabled(true);
                    setReactantChangedDisabled(true);
                    setReactantDestroyedDisabled(true);
                } else {
                    setReactantChangedDisabled(false);
                    setReactantDestroyedDisabled(false);
                }
                
                setDeleteReactionDisabled(((SubstanceInteraction) reactionsDropdown.getSelectedItem()).equals(SubstanceInteraction.CREATE_NEW));
                setSaveReactionDisabled(reactantDropdown.getModel().getElementAt(0) == null);
                setDeleteDisabled(selectedSubstance.equals(Substance.CREATE_NEW) || selectedSubstance.isSampleSubstance());
                
                String subName = nameInput.getText();
                Substance[] subs = getSubsWithExclusions(false);

                boolean matchingNameFound = false;
                for(Substance sub : subs) {
                    if(subName.trim().equals("") || subName.toLowerCase().trim().equals("[create new]") || (!sub.equals(selectedSubstance) && sub.getName().toLowerCase().equals(subName.trim().toLowerCase()))) {
                        matchingNameFound = true;
                    }
                }
                
                setSaveDisabled(matchingNameFound);
            }
        }
        
        private void setResetDisabled(boolean set) {
            resetBtn.setEnabled(!set);
        }
        
        private void setNameDisabled(boolean set) {
            nameInput.setEnabled(!set);
            nameLbl.setForeground(set ? Color.LIGHT_GRAY : Color.BLACK);
        }
        
        private void setColorDisabled(boolean set) {
            rSlider.setEnabled(!set);
            gSlider.setEnabled(!set);
            bSlider.setEnabled(!set);
            colorLbl.setForeground(set ? Color.LIGHT_GRAY : Color.BLACK);
        }
        
        private void setViscosityDisabled(boolean set) {
            viscositySlider.setEnabled(!set);
            viscosityLbl.setForeground(set ? Color.LIGHT_GRAY : Color.BLACK);
        }
        
        private void setWeightDisabled(boolean set) {
            weightSlider.setEnabled(!set);
            weightLbl.setForeground(set ? Color.LIGHT_GRAY : Color.BLACK);
        }
        
        private void setDensityDisabled(boolean set) {
            densitySlider.setEnabled(!set);
            densityLbl.setForeground(set ? Color.LIGHT_GRAY : Color.BLACK);
        }
        
        private void setStateDisabled(boolean set) {
            solidRadio.setEnabled(!set);
            liquidRadio.setEnabled(!set);
            gasRadio.setEnabled(!set);
            stateLbl.setForeground(set ? Color.LIGHT_GRAY : Color.BLACK);
        }
        
        private void setFlammableBoxDisabled(boolean set) {
            if(set) {
                flammableBox.setSelected(false);
            }
            
            flammableBox.setEnabled(!set);
            flammableSlider.setEnabled(flammableBox.isSelected());
        }
        
        private void setFlammableSliderDisabled(boolean set) {
            flammableSlider.setEnabled(!set);
        }
        
        private void setDecayBoxDisabled(boolean set) {
            if(set) {
                decayBox.setSelected(false);
            }
            
            decayBox.setEnabled(!set);
            decaySlider.setEnabled(decayBox.isSelected());
        }
        
        private void setDecaySliderDisabled(boolean set) {
            decaySlider.setEnabled(!set);
        }
        
        private void setReactionsDropdownDisabled(boolean set) {
            reactionsDropdown.setEnabled(!set);
        }
        
        private void setReactantDropdownDisabled(boolean set) {
            reactantDropdown.setEnabled(!set);
            reactantLbl.setForeground(set ? Color.LIGHT_GRAY : Color.BLACK);
        }
        
        private void setProductDropdownDisabled(boolean set) {
            productDropdown.setEnabled(!set);
            productLbl.setForeground(set ? Color.LIGHT_GRAY : Color.BLACK);
        }
        
        private void setSourceUnchangedDisabled(boolean set) {
            srcUnchangedRadio.setEnabled(!set);
            if(set && srcUnchangedRadio.isSelected()) {
                srcUnchangedRadio.setSelected(false);
                srcChangedRadio.setSelected(true);
            }
            
            boolean wholeComponentDisabled = !srcUnchangedRadio.isEnabled() && !srcChangedRadio.isEnabled() && !srcDestroyedRadio.isEnabled();
            srcOutcomeLbl.setForeground(wholeComponentDisabled ? Color.LIGHT_GRAY : Color.BLACK);
        }
        
        private void setSourceChangedDisabled(boolean set) {
            srcChangedRadio.setEnabled(!set);
            
            boolean wholeComponentDisabled = !srcUnchangedRadio.isEnabled() && !srcChangedRadio.isEnabled() && !srcDestroyedRadio.isEnabled();
            srcOutcomeLbl.setForeground(wholeComponentDisabled ? Color.LIGHT_GRAY : Color.BLACK);
        }
        
        private void setSourceDestroyedDisabled(boolean set) {
            srcDestroyedRadio.setEnabled(!set);
            
            boolean wholeComponentDisabled = !srcUnchangedRadio.isEnabled() && !srcChangedRadio.isEnabled() && !srcDestroyedRadio.isEnabled();
            srcOutcomeLbl.setForeground(wholeComponentDisabled ? Color.LIGHT_GRAY : Color.BLACK);
        }
        
        private void setReactantUnchangedDisabled(boolean set) {
            rctUnchangedRadio.setEnabled(!set);
            
            boolean wholeComponentDisabled = !rctUnchangedRadio.isEnabled() && !rctChangedRadio.isEnabled() && !rctDestroyedRadio.isEnabled();
            rctOutcomeLbl.setForeground(wholeComponentDisabled ? Color.LIGHT_GRAY : Color.BLACK);
        }
        
        private void setReactantChangedDisabled(boolean set) {
            rctChangedRadio.setEnabled(!set);
            
            boolean wholeComponentDisabled = !rctUnchangedRadio.isEnabled() && !rctChangedRadio.isEnabled() && !rctDestroyedRadio.isEnabled();
            rctOutcomeLbl.setForeground(wholeComponentDisabled ? Color.LIGHT_GRAY : Color.BLACK);
        }
        
        private void setReactantDestroyedDisabled(boolean set) {
            rctDestroyedRadio.setEnabled(!set);
            
            boolean wholeComponentDisabled = !rctUnchangedRadio.isEnabled() && !rctChangedRadio.isEnabled() && !rctDestroyedRadio.isEnabled();
            rctOutcomeLbl.setForeground(wholeComponentDisabled ? Color.LIGHT_GRAY : Color.BLACK);
        }
        
        private void setVolatilityDisabled(boolean set) {
            volatilitySlider.setEnabled(!set);
            volatilityLbl.setForeground(set ? Color.LIGHT_GRAY : Color.BLACK);
        }
        
        private void setDeleteReactionDisabled(boolean set) {
            deleteReactionBtn.setEnabled(!set);
        }
        
        private void setSaveReactionDisabled(boolean set) {
            saveReactionBtn.setEnabled(!set);
        }
        
        private void setDeleteDisabled(boolean set) {
            deleteBtn.setEnabled(!set);
        }
        
        private void setSaveDisabled(boolean set) {
            saveBtn.setEnabled(!set);
        }
        
        private class ActionHandler implements ChangeListener, ActionListener, DocumentListener {

            @Override
            public void stateChanged(ChangeEvent event) {
                Object src = event.getSource();
                
                if(src == rSlider || src == gSlider || src == bSlider) {
                    colorPanel.setFillColor(new Color(rSlider.getValue(), gSlider.getValue(), bSlider.getValue()));
                }
            }
            
            @Override
            public void actionPerformed(ActionEvent event) {
                Object src = event.getSource();
                
                if(src == solidRadio || src == liquidRadio || src == gasRadio) {
                    setViscosityDisabled(!liquidRadio.isSelected());
                    setWeightDisabled(!liquidRadio.isSelected());
                    setDensityDisabled(solidRadio.isSelected());
                } else if(src == reactionsDropdown) {
                    setDeleteReactionDisabled(((SubstanceInteraction) reactionsDropdown.getSelectedItem()).equals(SubstanceInteraction.CREATE_NEW));
                    resetReactionsComponent((SubstanceInteraction) reactionsDropdown.getSelectedItem());
                } else if(src == flammableBox || src == decayBox) {
                    setFlammableSliderDisabled(!flammableBox.isSelected());
                    setDecaySliderDisabled(!decayBox.isSelected());
                } else if(src == reactantDropdown) {
                    Substance reactant = (Substance) reactantDropdown.getSelectedItem();
                    reactantPanel.setFillColor(reactant == null ? new Color(0, 0, 0, 0) : reactant.getColor());
                    
                    if(reactant.equals(Substance.NONE)) {
                        rctUnchangedRadio.setSelected(true);
                        rctChangedRadio.setSelected(false);
                        rctDestroyedRadio.setSelected(false);
                        
                        if(srcUnchangedRadio.isSelected()) {
                            srcUnchangedRadio.setSelected(false);
                            srcChangedRadio.setSelected(true);
                        }
                        
                        setSourceUnchangedDisabled(true);
                        setReactantChangedDisabled(true);
                        setReactantDestroyedDisabled(true);
                    } else {
                        setReactantChangedDisabled(false);
                        setReactantDestroyedDisabled(false);
                    }
                } else if(src == productDropdown) {
                    Substance product = (Substance) productDropdown.getSelectedItem();
                    productPanel.setFillColor(product == null ? new Color(0, 0, 0, 0) : product.getColor());
                } else if(src == srcUnchangedRadio || src == srcChangedRadio || src == srcDestroyedRadio || src == rctUnchangedRadio || src == rctChangedRadio || src == rctDestroyedRadio) {
                    setSourceUnchangedDisabled(rctUnchangedRadio.isSelected());
                    setReactantUnchangedDisabled(srcUnchangedRadio.isSelected());
                } else if(src == deleteReactionBtn) {
                    deleteReaction((SubstanceInteraction) reactionsDropdown.getSelectedItem());
                } else if(src == saveReactionBtn) {
                    saveReaction((SubstanceInteraction) reactionsDropdown.getSelectedItem());
                } else if(src == resetBtn) {
                    resetFullEditor();
                } else if(src == saveBtn) {
                    saveSubstance();
                } else if(src == deleteBtn) {
                    deleteSubstance();
                }
            }
            
            @Override
            public void changedUpdate(DocumentEvent event) {
                checkText();
            }
            
            @Override
            public void removeUpdate(DocumentEvent event) {
                checkText();
            }
            
            @Override
            public void insertUpdate(DocumentEvent event) {
                checkText();
            }
            
            private void checkText() {
                String subName = nameInput.getText();
                Substance[] subs = getSubsWithExclusions(false);

                boolean matchingNameFound = false;
                for(Substance sub : subs) {
                    if(subName.trim().equals("") || subName.toLowerCase().trim().equals("[create new]") || (!sub.equals(selectedSubstance) && sub.getName().toLowerCase().equals(subName.trim().toLowerCase()))) {
                        matchingNameFound = true;
                        setSaveDisabled(true);
                    }
                }

                if(!matchingNameFound) {
                    setSaveDisabled(false);
                }
            }
        }
    }
    
    private static class SubstanceMenu extends JPanel {
        
        private PressHandler handler = new PressHandler();
        
        private List<SubstanceButton> options = new ArrayList<>();
        
        private final int ROW_NUMBER = 6;
        private final int COLUMN_NUMBER = 6;
        private final int H_GAP = 2;
        private final int V_GAP = 2;
        
        public SubstanceMenu(Dimension frameDim, Dimension viewportDim) {
            super();
            
            setLayout(new GridLayout(ROW_NUMBER, COLUMN_NUMBER, H_GAP, V_GAP));
            setBounds(frameDim.width - (viewportDim.width + 20), viewportDim.height + 10, viewportDim.width, frameDim.height - (viewportDim.height + 55));
            setBorder(BorderFactory.createEtchedBorder());
            
            options.add(new SubstanceButton(Substance.CREATE_NEW, handler, handler));
            for(Substance sub: Substance.getSavedSubstances()) {
                options.add(new SubstanceButton(sub, handler, handler));
            }
            
            for(SubstanceButton button: options) {
                add(button);
            }
        }
        
        public void updateOptions(Substance[] subs) {
            List<Substance> substances = new ArrayList<>();
            substances.add(Substance.CREATE_NEW);
            substances.addAll(Arrays.asList(subs));
            
            options = new ArrayList<>();
            for(Substance sub: substances) {
                options.add(new SubstanceButton(sub, handler, handler));
            }
            
            removeAll();
            
            for(SubstanceButton button: options) {
                add(button);
            }
            
            repaint();
        }
        
        public void refreshButtons() {
            for(SubstanceButton button: options) {
                button.repaint();
            }
        }
        
        private class PressHandler extends MouseAdapter implements ActionListener {
            
            private boolean rightClickHeld = false;
            
            @Override
            public void actionPerformed(ActionEvent event) {
                Substance.setCurrentlySelected(((SubstanceButton) event.getSource()).getSubstance());
                refreshButtons();
            }
            
            @Override
            public void mousePressed(MouseEvent event) {
                if(event.getButton() == MouseEvent.BUTTON3) {
                    rightClickHeld = true;
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent event) {
                if(event.getButton() == MouseEvent.BUTTON3 && rightClickHeld) {
                    rightClickHeld = false;
                    
                    Substance.setAlternateSelected(((SubstanceButton) event.getSource()).getSubstance());
                    refreshButtons();
                }
            }
        }
    }
    
    protected static class ColorBox extends JPanel {
        
        private Color boxColor;
        
        public ColorBox(Color color) {
            super();
            
            if(color == null) {
                throw new IllegalArgumentException("Cannot initialize UI color box with null color.");
            }
            
            boxColor = color;
            setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        }
        
        public Color getFillColor() {
            return boxColor;
        }
        
        public void setFillColor(Color color) {
            if(color == null) {
                throw new IllegalArgumentException("Cannot set null color in UI color box.");
            }
            
            boxColor = color;
            repaint();
        }
        
        @Override
        public void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            
            graphics.setColor(boxColor == null ? new Color(128, 128, 128) : boxColor);
            graphics.fillRect(0, 0, getWidth(), getHeight());
        }
    }
    
    protected static class SubstanceButton extends JButton {
        
        private final Substance substance;
        
        public SubstanceButton(Substance sub, ActionListener pressListener, MouseAdapter rightPressListener) {
            super(sub.getName());
            
            substance = sub;
            
            Color subColor = sub.equals(Substance.CREATE_NEW) ? Color.WHITE : sub.getColor();
            setBackground(subColor);
            setForeground((subColor.getRed() + subColor.getGreen() + subColor.getBlue()) >= 384 ? Color.BLACK : Color.WHITE);
            
            addActionListener(pressListener);
            addMouseListener(rightPressListener);
        }
        
        public Substance getSubstance() {
            return substance;
        }
        
        @Override
        public void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            
            if(Substance.getCurrentlySelected().equals(substance)) {
                graphics.setColor(Color.RED);
                graphics.fillOval(5, 5, getHeight() - 10, getHeight() - 10);
                graphics.setColor(Color.BLACK);
                graphics.drawOval(5, 5, getHeight() - 10, getHeight() - 10);
                
                if(Substance.getAlternateSelected().equals(substance)) {
                    graphics.setColor(Color.BLUE);
                    graphics.fillOval(getHeight(), 5, getHeight() - 10, getHeight() - 10);
                    graphics.setColor(Color.BLACK);
                    graphics.drawOval(getHeight(), 5, getHeight() - 10, getHeight() - 10);
                }
            } else if(Substance.getAlternateSelected().equals(substance)) {
                graphics.setColor(Color.BLUE);
                graphics.fillOval(5, 5, getHeight() - 10, getHeight() - 10);
                graphics.setColor(Color.BLACK);
                graphics.drawOval(5, 5, getHeight() - 10, getHeight() - 10);
            }
        }
    }
}
