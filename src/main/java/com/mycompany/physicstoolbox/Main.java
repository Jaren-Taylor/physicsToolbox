package com.mycompany.physicstoolbox;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {
    private static final Dimension WINDOW_SIZE = new Dimension(1200, 800);
    private static final Dimension VIEWPORT_SIZE = new Dimension(800, 600);
    
    public static void main (String[] args) {
        // TEMPORARY CODE
        // Sets the currently selected substance to a generic, filler instance
        Substance.setCurrentlySelected(new Substance(new Color(255, 255, 255), "Generic", 0.5, 1, 0.5));
        
        JFrame frame = new JFrame("Physics Toolbox");
        Viewport vp = Viewport.getInstance(VIEWPORT_SIZE);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(WINDOW_SIZE);
        frame.add(vp);
        
        frame.pack();
        frame.getContentPane().setLayout(new FlowLayout(FlowLayout.RIGHT));
        frame.setVisible(true);
    }
}
