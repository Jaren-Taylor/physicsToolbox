package com.mycompany.physicstoolbox;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 *
 * @author Jaren Taylor
 */
public class UI extends JPanel implements ChangeListener, ItemListener {

    public static final Dimension EDITOR_SIZE = new Dimension(370, 750);

    public static JLabel title = new JLabel("Substance Editor");
    public static JLabel name = new JLabel("Name");
    public static JLabel viscosity = new JLabel("Viscosity");
    public static JLabel weight = new JLabel("Weight");
    public static JLabel density = new JLabel("Density");
    public static JLabel state = new JLabel("State");
    public static JRadioButton solid = new JRadioButton("Solid");
    public static JRadioButton liquid = new JRadioButton("Liquid");
    public static JRadioButton gas = new JRadioButton("Gas");
    public ButtonGroup bg = new ButtonGroup();

    public static Double vis;
    public static Double wei;
    public static Double den;
    public static Substance.State substanceState;

    public static JTextField nameInput = new JTextField(10);
    JSlider v = new JSlider(0, 10);
    JSlider w = new JSlider(0, 10);
    JSlider d = new JSlider(0, 10);

    public JLabel cl = new JLabel("Color");

    public UI() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(EDITOR_SIZE);
        setBorder(BorderFactory.createLineBorder(java.awt.Color.RED));

        Rect rect = new Rect();
        JPanel statePanel = new JPanel();
        statePanel.add(solid);
        statePanel.add(liquid);
        statePanel.add(gas);
        statePanel.setMaximumSize(statePanel.getPreferredSize());

        title.setFont(new Font("Times New Roman", Font.PLAIN, 25));
        nameInput.setMaximumSize(nameInput.getPreferredSize());

        add(title);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(name);
        add(nameInput);
        add(Box.createRigidArea(new Dimension(0, 10)));
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

        v.addChangeListener(this);
        w.addChangeListener(this);
        d.addChangeListener(this);

        rect.setAlignmentX(Component.LEFT_ALIGNMENT);
        v.setAlignmentX(Component.LEFT_ALIGNMENT);
        w.setAlignmentX(Component.LEFT_ALIGNMENT);
        d.setAlignmentX(Component.LEFT_ALIGNMENT);
        statePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        solid.addItemListener(this);
        liquid.addItemListener(this);
        gas.addItemListener(this);
        bg.add(gas);
        bg.add(solid);
        bg.add(liquid);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        this.vis = Double.valueOf(v.getValue()) / 10;
        this.wei = Double.valueOf(w.getValue()) / 10;
        this.den = Double.valueOf(d.getValue()) / 10;
    }

    //buggy, we need a way to deselect the buttons whenever we click them, but we only want to deselect the buttons that weren't just clicked. will use action listeners instead.
    @Override
    public void itemStateChanged(ItemEvent e) {
        String[] button = e.getItem().toString().split(",text=");
        String currSelected = button[button.length-1].toString().substring(0, button[1].length() - 1);
        
  
        
       switch (currSelected){
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
       System.out.println(currSelected);
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
    }

    class Reactivity extends JPanel {

    }

}
