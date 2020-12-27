package com.mycompany.physicstoolbox;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
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
        
        private JLabel titleLbl, nameLbl, colorLbl, viscosityLbl, weightLbl, densityLbl,
                       stateLbl, reactionsLbl, reactantLbl, productLbl;
        private ColorBox colorPanel, reactantPanel, productPanel;
        private JTextField nameInput;
        private JSlider rSlider, gSlider, bSlider, viscositySlider, weightSlider, densitySlider,
                        flammableSlider, decaySlider, volatilitySlider;
        private JRadioButton solidRadio, liquidRadio, gasRadio, sourceUnchangedRadio, sourceChangedRadio,
                             sourceDestroyedRadio, reactantUnchangedRadio, reactantChangedRadio, reactantDestroyedRadio;
        private ButtonGroup stateGroup, sourceOutcomeGroup, reactantOutcomeGroup;
        private JCheckBox flammableBox, decayBox;
        private JComboBox reactionsDropdown, reactantDropdown, productDropdown;
        private JButton resetBtn, saveBtn;
        
        private final int PADDING = 5;
        
        private final int TITLE_COMPONENT_HEIGHT = 30;
        private final int REACTANT_PRODUCT_LABEL_WIDTH = 60;
        private final int FLAMMABLE_DECAY_LABEL_WIDTH = 100;
        private final int COMPONENT_HEIGHT = 20;
        private final int COMPONENT_WIDTH;
        
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
            
            reactantLbl = new JLabel("Reactant");
            reactantLbl.setBounds(PADDING, TITLE_COMPONENT_HEIGHT + (27 * COMPONENT_HEIGHT) + PADDING, REACTANT_PRODUCT_LABEL_WIDTH, COMPONENT_HEIGHT);
            //reactantLbl.setFont(LABEL_FONT);
            
            productLbl = new JLabel("Product");
            productLbl.setBounds(PADDING, TITLE_COMPONENT_HEIGHT + (28 * COMPONENT_HEIGHT) + PADDING, REACTANT_PRODUCT_LABEL_WIDTH, COMPONENT_HEIGHT);
            //productLbl.setFont(LABEL_FONT);
            
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
            
            decaySlider = new JSlider(0, 100, 0);
            
            volatilitySlider = new JSlider(0, 100, 0);
            
            /* ---------- */
            
            solidRadio = new JRadioButton("Solid", false);
            solidRadio.setBounds(PADDING, TITLE_COMPONENT_HEIGHT + (19 * COMPONENT_HEIGHT) + PADDING, COMPONENT_WIDTH / 3, COMPONENT_HEIGHT);
            
            liquidRadio = new JRadioButton("Liquid", true);
            liquidRadio.setBounds(PADDING + (COMPONENT_WIDTH / 3), TITLE_COMPONENT_HEIGHT + (19 * COMPONENT_HEIGHT) + PADDING, COMPONENT_WIDTH / 3, COMPONENT_HEIGHT);
            
            gasRadio = new JRadioButton("Gas", false);
            gasRadio.setBounds(PADDING + ((2 * COMPONENT_WIDTH) / 3), TITLE_COMPONENT_HEIGHT + (19 * COMPONENT_HEIGHT) + PADDING, COMPONENT_WIDTH / 3, COMPONENT_HEIGHT);
            
            sourceUnchangedRadio = new JRadioButton("Don't Change", false);
            
            sourceChangedRadio = new JRadioButton("Change", true);
            
            sourceDestroyedRadio = new JRadioButton("Destroy", false);
            
            reactantUnchangedRadio = new JRadioButton("Don't Change", false);
            
            reactantChangedRadio = new JRadioButton("Change", true);
            
            reactantDestroyedRadio = new JRadioButton("Destroy", false);
            
            /* ---------- */
            
            stateGroup = new ButtonGroup();
            sourceOutcomeGroup = new ButtonGroup();
            reactantOutcomeGroup = new ButtonGroup();
            
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
            
            sourceOutcomeGroup.add(sourceUnchangedRadio);
            sourceOutcomeGroup.add(sourceChangedRadio);
            sourceOutcomeGroup.add(sourceDestroyedRadio);
            reactantOutcomeGroup.add(sourceUnchangedRadio);
            reactantOutcomeGroup.add(sourceChangedRadio);
            reactantOutcomeGroup.add(sourceDestroyedRadio);
            add(sourceUnchangedRadio);
            add(sourceChangedRadio);
            add(sourceDestroyedRadio);
            add(reactantUnchangedRadio);
            add(reactantChangedRadio);
            add(reactantDestroyedRadio);
            
            add(decayBox);
            add(flammableBox);
            
            add(reactionsDropdown);
            add(reactantDropdown);
            add(productDropdown);
            
            add(resetBtn);
            add(saveBtn);
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
        
        public Color getColor() {
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
