/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.physicstoolbox;
import javax.swing.*;
import java.awt.*;
/**
 *
 * @author TeaRz
 */
public class Main extends JFrame {

    
    public static void main (String[] args){
        JFrame frame = new JFrame("Physics Toolbox");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(800, 800));
        JPanel jp = new JPanel();
        JLabel label = new JLabel("Experiment Pane");
        jp.add(label);
        jp.setBackground(Color.BLACK);
        jp.setPreferredSize(new Dimension(400, 400));
        frame.add(jp);
        frame.pack();
        frame.getContentPane().setLayout(new FlowLayout(FlowLayout.RIGHT));
        frame.setVisible(true);
    }
}
