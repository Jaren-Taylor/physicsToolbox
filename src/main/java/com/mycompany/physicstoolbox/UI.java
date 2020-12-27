package com.mycompany.physicstoolbox;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import javax.swing.border.BevelBorder;

public abstract class UI {
    
    public static SubstanceEditor getSubstanceEditorComponent(Dimension frameDim, int viewportWidth) {
        return new SubstanceEditor(frameDim, viewportWidth);
    }
    
    public static SubstanceMenu getSubstanceMenuComponent() {
        return new SubstanceMenu();
    }
    
    private static class SubstanceEditor extends JPanel {
        
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
        private JButton deleteReactionBtn, addReactionBtn, resetBtn, saveBtn;
        
        private List<Substance> substanceExclusions = new ArrayList<>();
        
        private final int PADDING = 5;
        
        private final int TITLE_COMPONENT_HEIGHT = 30;
        private final int COMPONENT_HEIGHT = 18;
        private final int COMPONENT_WIDTH;
        
        private final int REACTANT_PRODUCT_LABEL_WIDTH = 60;
        private final int FLAMMABLE_DECAY_LABEL_WIDTH = 90;
        private final int SOURCE_REACTANT_LABEL_WIDTH = 60;
        private final int VOLATILITY_LABEL_WIDTH = 60;
        
        private final Font TITLE_FONT = new Font("Bahnschrift", Font.PLAIN, 30);
        private final Font LABEL_FONT = new Font("Bahnschrift", Font.PLAIN, 16);
        
        private SubstanceEditor(Dimension frameDim, int viewportWidth) {
            super();
            
            setLayout(null);
            setBounds(5, 5, frameDim.width - (viewportWidth + 30), frameDim.height - 50);
            
            COMPONENT_WIDTH = getWidth() - (2 * PADDING);
            
            setBorder(BorderFactory.createEtchedBorder());
            
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
            
            /* ---------- */
            
            rSlider = new JSlider(0, 255, 128);
            rSlider.setBounds(colorPanel.getWidth() + PADDING, TITLE_COMPONENT_HEIGHT + (5 * COMPONENT_HEIGHT) + PADDING, COMPONENT_WIDTH - colorPanel.getWidth(), COMPONENT_HEIGHT);
            
            gSlider = new JSlider(0, 255, 128);
            gSlider.setBounds(colorPanel.getWidth() + PADDING, TITLE_COMPONENT_HEIGHT + (6 * COMPONENT_HEIGHT) + PADDING, COMPONENT_WIDTH - colorPanel.getWidth(), COMPONENT_HEIGHT);
            
            bSlider = new JSlider(0, 255, 128);
            bSlider.setBounds(colorPanel.getWidth() + PADDING, TITLE_COMPONENT_HEIGHT + (7 * COMPONENT_HEIGHT) + PADDING, COMPONENT_WIDTH - colorPanel.getWidth(), COMPONENT_HEIGHT);
            
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
            flammableSlider.setBounds(FLAMMABLE_DECAY_LABEL_WIDTH + (2 * PADDING), TITLE_COMPONENT_HEIGHT + (24 * COMPONENT_HEIGHT) + PADDING, COMPONENT_WIDTH - (FLAMMABLE_DECAY_LABEL_WIDTH + PADDING), COMPONENT_HEIGHT);
            
            decaySlider = new JSlider(0, 100, 0);
            decaySlider.setBounds(FLAMMABLE_DECAY_LABEL_WIDTH + (2 * PADDING), TITLE_COMPONENT_HEIGHT + (25 * COMPONENT_HEIGHT) + PADDING, COMPONENT_WIDTH - (FLAMMABLE_DECAY_LABEL_WIDTH + PADDING), COMPONENT_HEIGHT);
            
            volatilitySlider = new JSlider(0, 100, 0);
            volatilitySlider.setBounds(VOLATILITY_LABEL_WIDTH + (2 * PADDING), TITLE_COMPONENT_HEIGHT + (33 * COMPONENT_HEIGHT) + PADDING, COMPONENT_WIDTH - (VOLATILITY_LABEL_WIDTH + PADDING), COMPONENT_HEIGHT);
            
            /* ---------- */
            
            solidRadio = new JRadioButton("Solid", false);
            solidRadio.setBounds(PADDING, TITLE_COMPONENT_HEIGHT + (19 * COMPONENT_HEIGHT) + PADDING, COMPONENT_WIDTH / 3, COMPONENT_HEIGHT);
            
            liquidRadio = new JRadioButton("Liquid", true);
            liquidRadio.setBounds(PADDING + (COMPONENT_WIDTH / 3), TITLE_COMPONENT_HEIGHT + (19 * COMPONENT_HEIGHT) + PADDING, COMPONENT_WIDTH / 3, COMPONENT_HEIGHT);
            
            gasRadio = new JRadioButton("Gas", false);
            gasRadio.setBounds(PADDING + ((2 * COMPONENT_WIDTH) / 3), TITLE_COMPONENT_HEIGHT + (19 * COMPONENT_HEIGHT) + PADDING, COMPONENT_WIDTH / 3, COMPONENT_HEIGHT);
            
            srcUnchangedRadio = new JRadioButton("No Change", false);
            srcUnchangedRadio.setBounds(SOURCE_REACTANT_LABEL_WIDTH + (2 * PADDING), TITLE_COMPONENT_HEIGHT + (30 * COMPONENT_HEIGHT) + PADDING, (COMPONENT_WIDTH - SOURCE_REACTANT_LABEL_WIDTH) / 3, COMPONENT_HEIGHT);
            
            srcChangedRadio = new JRadioButton("Change", true);
            srcChangedRadio.setBounds(SOURCE_REACTANT_LABEL_WIDTH + ((COMPONENT_WIDTH - SOURCE_REACTANT_LABEL_WIDTH) / 3) + (2 * PADDING), TITLE_COMPONENT_HEIGHT + (30 * COMPONENT_HEIGHT) + PADDING, (COMPONENT_WIDTH - SOURCE_REACTANT_LABEL_WIDTH) / 3, COMPONENT_HEIGHT);
            
            srcDestroyedRadio = new JRadioButton("Destroy", false);
            srcDestroyedRadio.setBounds(SOURCE_REACTANT_LABEL_WIDTH + ((2 * (COMPONENT_WIDTH - SOURCE_REACTANT_LABEL_WIDTH)) / 3) + (2 * PADDING), TITLE_COMPONENT_HEIGHT + (30 * COMPONENT_HEIGHT) + PADDING, ((COMPONENT_WIDTH - SOURCE_REACTANT_LABEL_WIDTH) / 3) - (4 * PADDING), COMPONENT_HEIGHT);
            
            rctUnchangedRadio = new JRadioButton("No Change", false);
            rctUnchangedRadio.setBounds(SOURCE_REACTANT_LABEL_WIDTH + (2 * PADDING), TITLE_COMPONENT_HEIGHT + (31 * COMPONENT_HEIGHT) + PADDING, (COMPONENT_WIDTH - SOURCE_REACTANT_LABEL_WIDTH) / 3, COMPONENT_HEIGHT);
            
            rctChangedRadio = new JRadioButton("Change", true);
            rctChangedRadio.setBounds(SOURCE_REACTANT_LABEL_WIDTH + ((COMPONENT_WIDTH - SOURCE_REACTANT_LABEL_WIDTH) / 3) + (2 * PADDING), TITLE_COMPONENT_HEIGHT + (31 * COMPONENT_HEIGHT) + PADDING, (COMPONENT_WIDTH - SOURCE_REACTANT_LABEL_WIDTH) / 3, COMPONENT_HEIGHT);
            
            rctDestroyedRadio = new JRadioButton("Destroy", false);
            rctDestroyedRadio.setBounds(SOURCE_REACTANT_LABEL_WIDTH + ((2 * (COMPONENT_WIDTH - SOURCE_REACTANT_LABEL_WIDTH)) / 3) + (2 * PADDING), TITLE_COMPONENT_HEIGHT + (31 * COMPONENT_HEIGHT) + PADDING, ((COMPONENT_WIDTH - SOURCE_REACTANT_LABEL_WIDTH) / 3) - (4 * PADDING), COMPONENT_HEIGHT);
            
            /* ---------- */
            
            stateGroup = new ButtonGroup();
            srcOutcomeGroup = new ButtonGroup();
            rctOutcomeGroup = new ButtonGroup();
            
            /* ---------- */
            
            flammableBox = new JCheckBox("Flammable");
            flammableBox.setBounds(PADDING, TITLE_COMPONENT_HEIGHT + (24 * COMPONENT_HEIGHT) + PADDING, FLAMMABLE_DECAY_LABEL_WIDTH, COMPONENT_HEIGHT);
            
            decayBox = new JCheckBox("Decays");
            decayBox.setBounds(PADDING, TITLE_COMPONENT_HEIGHT + (25 * COMPONENT_HEIGHT) + PADDING, FLAMMABLE_DECAY_LABEL_WIDTH, COMPONENT_HEIGHT);
            
            /* ---------- */
            
            reactionsDropdown = new JComboBox(new SubstanceInteraction[] { SubstanceInteraction.CREATE_NEW });
            reactionsDropdown.setBounds(PADDING, TITLE_COMPONENT_HEIGHT + (22 * COMPONENT_HEIGHT) + PADDING, COMPONENT_WIDTH, COMPONENT_HEIGHT);
            
            Substance[] reactantList = Substance.getSavedSubstances();
            reactantList[0] = Substance.NONE; // Substance.WALL cannot be a reactant, but Substance.NONE can
            
            reactantDropdown = new JComboBox(reactantList);
            reactantDropdown.setBounds(REACTANT_PRODUCT_LABEL_WIDTH + reactantPanel.getWidth() + (3 * PADDING), TITLE_COMPONENT_HEIGHT + (27 * COMPONENT_HEIGHT) + PADDING, COMPONENT_WIDTH - (REACTANT_PRODUCT_LABEL_WIDTH + reactantPanel.getWidth() + (2 * PADDING)), COMPONENT_HEIGHT);
            
            productDropdown = new JComboBox(Substance.getSavedSubstances()); // Substance.NONE cannot be a product, but Substance.WALL can
            productDropdown.setBounds(REACTANT_PRODUCT_LABEL_WIDTH + productPanel.getWidth() + (3 * PADDING), TITLE_COMPONENT_HEIGHT + (28 * COMPONENT_HEIGHT) + PADDING, COMPONENT_WIDTH - (REACTANT_PRODUCT_LABEL_WIDTH + productPanel.getWidth() + (2 * PADDING)), COMPONENT_HEIGHT);
            
            reactantPanel.setFillColor(((Substance) reactantDropdown.getSelectedItem()).getColor());
            productPanel.setFillColor(((Substance) productDropdown.getSelectedItem()).getColor());
            
            /* ---------- */
            
            deleteReactionBtn = new JButton("Delete Reaction");
            addReactionBtn = new JButton("Add Reaction");
            resetBtn = new JButton("Reset");
            saveBtn = new JButton("Save");
            
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
            rctOutcomeGroup.add(srcUnchangedRadio);
            rctOutcomeGroup.add(srcChangedRadio);
            rctOutcomeGroup.add(srcDestroyedRadio);
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
            add(addReactionBtn);
            add(resetBtn);
            add(saveBtn);
        }
        
        private Substance[] getSubsWithExclusions(Substance[] exclusions) {
            List<Substance> subs = new ArrayList<>();
            List<Substance> allExclusions = new ArrayList<>(substanceExclusions);
            
            subs.add(Substance.NONE);
            subs.addAll(Arrays.asList(Substance.getSavedSubstances()));
            
            allExclusions.addAll(Arrays.asList(exclusions));
            
            for(Substance sub: subs) {
                for(Substance s: allExclusions) {
                    if(sub.getId() == s.getId()) {
                        subs.remove(sub);
                    }
                }
            }
            
            Substance[] finalSubs = new Substance[subs.size()];
            return subs.toArray(finalSubs);
        }
    }
    
    private static class SubstanceMenu extends JPanel {
        
        public SubstanceMenu() {
            super();
            
            
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
}
