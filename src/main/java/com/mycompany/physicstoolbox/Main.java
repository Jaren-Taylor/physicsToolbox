package com.mycompany.physicstoolbox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class Main extends JFrame {

    private static final Dimension WINDOW_SIZE = new Dimension(1200, 800);
    private static final Dimension VIEWPORT_SIZE = new Dimension(800, 600);
    private static JButton substanceItem;

    public static Substance[] allSubstances = new Substance[0];

    static class SubstanceMenu extends JPanel implements ActionListener {

        public JLabel menu = new JLabel("Placeholder");
        public static Substance newMenuItem;
        private final Timer TIMER = new Timer(1000, this);

        private final int TIMER_SPEED = 5;

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == TIMER) {
                if(newMenuItem != null){
                addSubstanceToMenu(newMenuItem);
                String name = newMenuItem.getName();
                substanceItem = new JButton(name);
                substanceItem.setBackground(newMenuItem.getColor());
                substanceItem.setMaximumSize(substanceItem.getPreferredSize());
                this.add(substanceItem);
                substanceItem.addActionListener(this);
                this.revalidate();
                this.repaint();
                newMenuItem = null;
                }
            } else {
                String name = ((JButton) e.getSource()).getText();

                Substance.setCurrentlySelected(substanceByName(name));

            }

        }

        public SubstanceMenu() {
            setPreferredSize(new Dimension(800, 140));
            setBorder(BorderFactory.createLineBorder(Color.yellow));
            setLayout(new GridLayout(5, 4));

            for (Substance sub : allSubstances) {
                String name = sub.getName();
                substanceItem = new JButton(name);
                substanceItem.setBackground(substanceByName(name).getColor());
                substanceItem.setMaximumSize(substanceItem.getPreferredSize());
                add(substanceItem);
                substanceItem.addActionListener(this);
            }
            TIMER.start();
            
        }

        public static Substance substanceByName(String name) {
            for (Substance sub : allSubstances) {
                if (sub.getName().equalsIgnoreCase(name)) {
                    return sub;
                }
            }
            System.out.println("No substance found, the selected substance will remain the same.");
            return Substance.getCurrentlySelected();
        }
    }

    public static void main(String[] args) {
        Substance.loadSavedSubstances();

        allSubstances = Substance.getSavedSubstances();

        Substance.setCurrentlySelected(allSubstances[0]);
        Substance.setAlternateSelected(allSubstances[1]);

        JFrame frame = new JFrame("Physics Toolbox");
        UI ui = new UI();
        Viewport vp = Viewport.getInstance(VIEWPORT_SIZE);
        SubstanceMenu sm = new SubstanceMenu();
        JPanel vpContainer = new JPanel(new BorderLayout());
        JPanel uiContainer = new JPanel();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(WINDOW_SIZE);
        frame.setResizable(false);
        uiContainer.add(ui);
        frame.add(uiContainer, BorderLayout.LINE_START);
        vpContainer.add(vp, BorderLayout.PAGE_START);
        vpContainer.add(sm, BorderLayout.PAGE_END);
        frame.add(vpContainer, BorderLayout.LINE_END);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Substance.saveSubstances();
            }
        });

        frame.pack();
        frame.getContentPane().setLayout(new BorderLayout());
        frame.setVisible(true);
    }

    public static void addSubstanceToMenu(Substance sub) {
        List<Substance> subsAsList = new ArrayList<>(Arrays.asList(allSubstances));
        subsAsList.add(sub);

        Substance[] subsAsArray = new Substance[subsAsList.size()];
        allSubstances = subsAsList.toArray(subsAsArray);
    }

    public static Substance[] getSubstances() {
        return allSubstances;
    }
}
