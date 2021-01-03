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

    public static void main(String[] args) {
        Substance.loadSavedSubstances();

        JFrame frame = new JFrame("Physics Toolbox");
        frame.getContentPane().setLayout(null);
        
        Viewport vp = Viewport.getInstance(WINDOW_SIZE.width, VIEWPORT_SIZE);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(WINDOW_SIZE);
        frame.setResizable(false);
        
        frame.add(vp);
        frame.add(UI.getSubstanceEditorComponent(WINDOW_SIZE, VIEWPORT_SIZE.width));
        frame.add(UI.getSubstanceMenuComponent(WINDOW_SIZE, VIEWPORT_SIZE));
        UI.initializeSelectedSubstances();

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Substance.saveSubstances();
            }
        });

        frame.pack();
        frame.setVisible(true);
    }
}
