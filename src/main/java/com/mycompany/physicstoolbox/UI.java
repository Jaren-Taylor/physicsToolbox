package com.mycompany.physicstoolbox;

import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.*;

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
        private JPanel colorPanel, reactantPanel, productPanel;
        private JTextField nameInput;
        private JSlider rSlider, gSlider, bSlider, viscositySlider, weightSlider, densitySlider,
                        decaySlider, flammableSlider, volatilitySlider;
        private JRadioButton solidRadio, liquidRadio, gasRadio;
        private ButtonGroup stateGroup;
        private JCheckBox decayBox, flammableBox;
        private JComboBox reactionsDropdown, reactantDropdown, productDropdown;
        private JButton resetBtn, saveBtn;
        
        private SubstanceEditor(Dimension frameDim, int viewportWidth) {
            super();
            
            setLayout(new FlowLayout(FlowLayout.LEFT));
            setBounds(5, 5, frameDim.width - (viewportWidth + 25), frameDim.height - 20);
            
            // Initializing UI components
            titleLbl = new JLabel("Substance Editor");
            nameLbl = new JLabel("Name");
            colorLbl = new JLabel("Color");
            viscosityLbl = new JLabel("Viscosity");
            weightLbl = new JLabel("Weight");
            densityLbl = new JLabel("Density");
            stateLbl = new JLabel("State");
            reactionsLbl = new JLabel("Reactions");
            reactantLbl = new JLabel("Reactant");
            productLbl = new JLabel("Product");
            
            colorPanel = new JPanel();
            reactantPanel = new JPanel();
            productPanel = new JPanel();
            
            nameInput = new JTextField();
            
            rSlider = new JSlider();
            gSlider = new JSlider();
            bSlider = new JSlider();
            viscositySlider = new JSlider();
            weightSlider = new JSlider();
            densitySlider = new JSlider();
            decaySlider = new JSlider();
            flammableSlider = new JSlider();
            volatilitySlider = new JSlider();
            
            solidRadio = new JRadioButton();
            liquidRadio = new JRadioButton();
            gasRadio = new JRadioButton();
            
            stateGroup = new ButtonGroup();
            
            decayBox = new JCheckBox();
            flammableBox = new JCheckBox();
            
            reactionsDropdown = new JComboBox();
            reactantDropdown = new JComboBox();
            productDropdown = new JComboBox();
            
            resetBtn = new JButton("Reset");
            saveBtn = new JButton("Save");
            
            // Setting component configuration
            
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
}
