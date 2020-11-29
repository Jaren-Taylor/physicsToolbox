package com.mycompany.physicstoolbox;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import com.mycompany.physicstoolbox.Main.SubstanceMenu;

/**
 *
 * @author Jaren Taylor
 */
public class UI extends JPanel implements ItemListener, ActionListener {

    public static final Dimension EDITOR_SIZE = new Dimension(370, 750);

    public static JLabel title = new JLabel("Substance Editor");
    public static JLabel name = new JLabel("Name");
    public static JLabel cl = new JLabel("Color");
    public static JLabel viscosity = new JLabel("Viscosity");
    public static JLabel weight = new JLabel("Weight");
    public static JLabel density = new JLabel("Density");
    public static JLabel state = new JLabel("State");
    public static JLabel reactivity = new JLabel("Reactivity");
    public static JLabel rpRelation = new JLabel("Reaction --> Product");
    public static JLabel reactant = new JLabel("Reactant");
    public static JLabel product = new JLabel("Product");
    public static JLabel volatilityL = new JLabel("Volatility");
    public static JRadioButton solid = new JRadioButton("Solid");
    public static JRadioButton liquid = new JRadioButton("Liquid");
    public static JRadioButton gas = new JRadioButton("Gas");
    public static JCheckBoxMenuItem decays = new JCheckBoxMenuItem("Decay");
    public static JCheckBoxMenuItem flammable = new JCheckBoxMenuItem("Flammable");

    public ButtonGroup bg = new ButtonGroup();
    public static Substance[] substances = Main.getSubstances();

    public static Double vis;
    public static Double wei;
    public static Double den;

    public static Double decay;
    public static Double flammability;
    public static Double volat;
    public static Substance.State substanceState;
    public static ArrayList<SubstanceInteraction> interactionList = new ArrayList<>();

    public static JTextField nameInput = new JTextField(10);
    JSlider v = new JSlider(0, 100);
    JSlider w = new JSlider(-100, 100);
    JSlider d = new JSlider(0, 100);
    JSlider dec = new JSlider(0, 100);
    JSlider flam = new JSlider(0, 100);
    JSlider volatility = new JSlider(0, 100);

    //Components
    public Rect rect = new Rect();
    public Reactivity decayComponent = new Reactivity(decays, dec);
    public Reactivity flammabilityComponent = new Reactivity(flammable, flam);
    public ReactivityRect reactantComponent = new ReactivityRect(reactant);
    public ReactivityRect productComponent = new ReactivityRect(product);

    public JPanel statePanel = new JPanel();
    public JPanel decayPanel = new JPanel();
    public JPanel flammabiltyPanel = new JPanel();
    public JPanel reactantPanel = new JPanel();
    public JPanel productPanel = new JPanel();
    public JButton create = new JButton("Create");
    public JButton newReaction = new JButton("Add Reaction");

    public UI() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(EDITOR_SIZE);
        setBorder(BorderFactory.createLineBorder(java.awt.Color.RED));

        statePanel.add(solid);
        statePanel.add(liquid);
        statePanel.add(gas);
        statePanel.setMaximumSize(statePanel.getPreferredSize());

        decayPanel.add(decayComponent);
        decayPanel.setMaximumSize(decayPanel.getPreferredSize());

        flammabiltyPanel.add(flammabilityComponent);
        flammabiltyPanel.setMaximumSize(flammabiltyPanel.getPreferredSize());

        reactantPanel.add(reactantComponent);
        reactantPanel.setMaximumSize(reactantPanel.getPreferredSize());

        productPanel.add(productComponent);
        productPanel.setMaximumSize(reactantPanel.getPreferredSize());

        title.setFont(
                new Font("Times New Roman", Font.PLAIN, 25));
        nameInput.setMaximumSize(nameInput.getPreferredSize());
        create.setMaximumSize(create.getPreferredSize());

        add(title);
        add(name);
        add(nameInput);
        add(cl);
        add(rect);
        add(viscosity);
        add(v);
        add(weight);
        add(w);
        add(density);
        add(d);
        add(state);
        add(statePanel);
        add(reactivity);
        add(decayPanel);
        add(flammabiltyPanel);
        add(rpRelation);
        add(reactantPanel);
        add(productPanel);
        add(volatilityL); //label
        add(volatility); //slider
        add(newReaction);
        add(create);


        rect.setAlignmentX(Component.LEFT_ALIGNMENT);
        v.setAlignmentX(Component.LEFT_ALIGNMENT);
        w.setAlignmentX(Component.LEFT_ALIGNMENT);
        d.setAlignmentX(Component.LEFT_ALIGNMENT);
        volatility.setAlignmentX(Component.LEFT_ALIGNMENT);
        statePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        decayPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        flammabiltyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        reactantPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        productPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        solid.addItemListener(
                this);
        liquid.addItemListener(
                this);
        gas.addItemListener(
                this);
        create.addActionListener(
                this);
        newReaction.addActionListener(this);
        bg.add(gas);
        bg.add(solid);
        bg.add(liquid);
    }

    public void createSubstance() {
        Substance newSub = new Substance(
                Substance.getSavedSubstances().length,
                this.rect.rc,
                this.nameInput.getText(),
                Double.valueOf(v.getValue())/100,
                Double.valueOf(w.getValue())/100,
                Double.valueOf(d.getValue())/100,
                this.substanceState);
        
        newSub.toggleDecayReaction(decays.getState(), Double.valueOf(dec.getValue())/100);

        newSub.toggleFlammableReaction(flammable.getState(), Double.valueOf(flam.getValue())/100);

        for (SubstanceInteraction SI : interactionList) {
            newSub.addReaction(SI);

        }
        interactionList.clear();
        Substance.addCustomSubstance(newSub);
        Main.addSubstanceToMenu(newSub);

//        newSub.addReaction(
//                new SubstanceInteraction(Main.SubstanceMenu.substanceByName(
//                        reactantComponent.dropBox.getName()),
//                        Main.SubstanceMenu.substanceByName(productComponent.dropBox.getSelectedItem().toString()),
//                        SubstanceInteraction.ReactionOutcome.CHANGED,
//                        SubstanceInteraction.ReactionOutcome.UNCHANGED,
//                        this.volat
//                ));
        Main.SubstanceMenu.substanceAdded = true;
    }

    public void newReaction() {
        
        try {
            System.out.println(
                    reactantComponent.dropBox.getSelectedItem().toString());
            System.out.println(
                    productComponent.dropBox.getSelectedItem().toString());

            SubstanceInteraction newReaction = new SubstanceInteraction(Main.SubstanceMenu.substanceByName(
                    reactantComponent.dropBox.getSelectedItem().toString()),
                    Main.SubstanceMenu.substanceByName(productComponent.dropBox.getSelectedItem().toString()),
                    SubstanceInteraction.ReactionOutcome.CHANGED,
                    SubstanceInteraction.ReactionOutcome.UNCHANGED,
                    Double.valueOf( volatility.getValue())/100);
            interactionList.add(newReaction);
            JOptionPane.showMessageDialog(this, "Substance Interaction saved, to add another change the sliders and click Add Reaction.");
        } catch (NullPointerException n) {
            JOptionPane.showMessageDialog(this, "Some sliders were not initialized, initialize them and retry adding your reaction");
        }
    }

    //buggy, we need a way to deselect the buttons whenever we click them, but we only want to deselect the buttons that weren't just clicked. will use action listeners instead.
    @Override
    public void itemStateChanged(ItemEvent e) {
        String type = e.getSource().toString().split("text=")[1].split("]")[0];

        switch (type) {
            case "Gas":
                bg.clearSelection();
                this.substanceState = Substance.State.GAS;
                break;

            case "Solid":
                bg.clearSelection();
                this.substanceState = Substance.State.SOLID;
                break;

            case "Liquid":
                bg.clearSelection();
                this.substanceState = Substance.State.LIQUID;
                break;

        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String name = ((JButton) e.getSource()).getText();
        switch (name) {
            case "Create":
                createSubstance();
                break;

            case "Add Reaction":
                newReaction();
                break;

        }
    }

    class Rect extends JPanel implements ChangeListener {

        JSlider r = new JSlider(0, 255);
        JSlider g = new JSlider(0, 255);
        JSlider b = new JSlider(0, 255);
        Color rc;

        public Rect() {
            setMaximumSize(new Dimension(350, 100));
            add(r);
            add(g);
            add(b);
            r.addChangeListener(this);
            g.addChangeListener(this);
            b.addChangeListener(this);

        }

        @Override
        public void stateChanged(ChangeEvent e) {
            rc = new Color(r.getValue(), this.g.getValue(), b.getValue());
            repaint();
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawRect(0, 10, 50, 50);
            g.setColor(rc); //set color based on RGB sliders
            g.fillRect(0, 10, 50, 50);
        }

        public Color getColor() {
            return this.rc;
        }
    }

    class ReactivityRect extends JPanel implements ItemListener {

        Substance selectedSub;
        JComboBox dropBox;
        

        public ReactivityRect(JLabel type) {
            add(Box.createHorizontalStrut(5));
            add(type);
            dropBox = new JComboBox(initSubs(substances, type));
            add(dropBox);

            dropBox.addItemListener(this);

        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawRect(0, 10, 10, 10);
            if (this.selectedSub != null) {
                g.setColor(this.selectedSub.getColor());
            } else {
                g.setColor(Color.BLACK);
            }
            g.fillRect(0, 10, 10, 10);
        }

        public String[] initSubs(Substance[] substances, JLabel type) {
            ArrayList<String> subs = new ArrayList<>();

            for (Substance sub : substances) {
                if( !(type.getText().equalsIgnoreCase("reactant") && 
                        (sub.getName().equalsIgnoreCase("Wall") || sub.getName().equalsIgnoreCase("Fire"))))
                    
                subs.add(sub.getName());
            }

            String[] newSubs = subs.toArray(new String[0]);
            return newSubs;
        }

        public void itemStateChanged(ItemEvent e) {
            // if the state combobox is changed 
            if (e.getSource() == dropBox) {
                String substanceName = dropBox.getSelectedItem().toString();

                for (Substance sub : substances) {
                    if (sub.getName().equalsIgnoreCase(substanceName)) {
                        this.selectedSub = sub;
                        this.repaint();
                    }
                }

            }
        }
    }

    class Reactivity extends JPanel {

        public Reactivity(JCheckBoxMenuItem item, JSlider slider) {
            add(item);
            add(slider);
        }

    }

}
