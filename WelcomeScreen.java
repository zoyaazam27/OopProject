////package com.mycompany.brick;
////
////import javax.swing.*;
////import java.awt.*;
////import java.awt.event.KeyAdapter;
////import java.awt.event.KeyEvent;
////
////public class WelcomeScreen extends JPanel {
////    private boolean startGame = false;
////
////    public WelcomeScreen() {
////        addKeyListener(new KeyAdapter() {
////            @Override
////            public void keyPressed(KeyEvent e) {
////                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
////                    startGame = true;
////                    JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(WelcomeScreen.this);
////                    parentFrame.getContentPane().removeAll();
////                    parentFrame.add(new GamePlay());
////                    parentFrame.revalidate();
////                    parentFrame.repaint();
////                }
////            }
////        });
////        setFocusable(true);
////        setFocusTraversalKeysEnabled(false);
////    }
////
////    @Override
////    protected void paintComponent(Graphics g) {
////        super.paintComponent(g);
////
////        // Draw welcome message
////        g.setColor(Color.BLACK);
////        g.fillRect(0, 0, getWidth(), getHeight());
////
////        g.setColor(Color.WHITE);
////        g.setFont(new Font("Serif", Font.BOLD, 30));
////        g.drawString("Welcome to Brick Breaker!", 150, 200);
////
////        g.setFont(new Font("Serif", Font.PLAIN, 20));
////        g.drawString("Press ENTER to Start", 220, 250);
////    }
////}
//package com.mycompany.brick;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.KeyAdapter;
//import java.awt.event.KeyEvent;
//
//public class WelcomeScreen extends JPanel {
//    private boolean startGame = false;
//
//    public WelcomeScreen() {
//        addKeyListener(new KeyAdapter() {
//            @Override
//            public void keyPressed(KeyEvent e) {
//                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
//                    startGame = true;
//                    JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(WelcomeScreen.this);
//                    parentFrame.getContentPane().removeAll();
//                    parentFrame.add(new GamePlay());
//                    parentFrame.revalidate();
//                    parentFrame.repaint();
//                }
//            }
//        });
//        setFocusable(true);
//        setFocusTraversalKeysEnabled(false);
//    }
//
//    @Override
//    protected void paintComponent(Graphics g) {
//        super.paintComponent(g);
//
//        Graphics2D g2d = (Graphics2D) g;
//
//        // Gradient background
//        Color color1 = new Color(70, 130, 180); // Steel blue
//        Color color2 = new Color(255, 140, 0);  // Dark orange
//        GradientPaint gradient = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
//        g2d.setPaint(gradient);
//        g2d.fillRect(0, 0, getWidth(), getHeight());
//
//        // Game title
//        g.setColor(Color.YELLOW);
//        g.setFont(new Font("Impact", Font.BOLD, 50));
//        g.drawString("SLICING SPREE!", 160, 150);
//
//        // Subheading
//        g.setColor(Color.WHITE);
//        g.setFont(new Font("Serif", Font.ITALIC, 20));
//        g.drawString("An Exciting Ball & Paddle Game", 210, 190);
//
//        // Instructions
//        g.setFont(new Font("Serif", Font.BOLD, 25));
//        g.setColor(Color.CYAN);
//        g.drawString("Press ENTER to Start", 230, 300);
//
//        // Add some decorations (stars or circles for design)
//        g.setColor(new Color(255, 255, 255, 120)); // Semi-transparent white
//        for (int i = 0; i < 20; i++) {
//            int x = (int) (Math.random() * getWidth());
//            int y = (int) (Math.random() * getHeight());
//            int size = (int) (Math.random() * 15 + 5);
//            g.fillOval(x, y, size, size);
//        }
//
//        // Footer
//        g.setFont(new Font("Monospaced", Font.PLAIN, 15));
//        g.setColor(Color.WHITE);
//        g.drawString("Created by Your Name © 2024", 230, getHeight() - 20);
//    }
//}
package com.mycompany.brick;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

public class WelcomeScreen extends JPanel {
    private boolean startGame = false;
    private Image backgroundImage;

    public WelcomeScreen() {
        // Load the background image
        loadImage();

        // Set up key listener for starting the game
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    startGame = true;
                    JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(WelcomeScreen.this);
                    parentFrame.getContentPane().removeAll();
                    parentFrame.add(new GamePlay());
                    parentFrame.revalidate();
                    parentFrame.repaint();
                }
            }
        });
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
    }

    private void loadImage() {
        try {
            // Load the image from the resource path
            backgroundImage = new ImageIcon(getClass().getResource("C:\\Users\\Lenovo\\OneDrive\\Desktop\\OOP\\src\\com\\mycompany")).getImage();
        } catch (Exception e) {
            System.out.println("Error loading background image: " + e.getMessage());
        }

        // Fallback: Use placeholder image if all else fails
        if (backgroundImage == null) {
            System.out.println("Using placeholder image...");
            backgroundImage = new ImageIcon(Toolkit.getDefaultToolkit().getImage("https://via.placeholder.com/800x600")).getImage();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the background image
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            // If image not loaded, fill with a solid color
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        // Game title
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Impact", Font.BOLD, 50));
        g.drawString("SLICING SPREE!", 160, 150);

        // Subheading
        g.setColor(Color.WHITE);
        g.setFont(new Font("Serif", Font.ITALIC, 20));
        g.drawString("An Exciting Ball & Paddle Game", 210, 190);

        // Instructions
        g.setFont(new Font("Serif", Font.BOLD, 25));
        g.setColor(Color.CYAN);
        g.drawString("Press ENTER to Start", 230, 300);

        // Footer
        g.setFont(new Font("Monospaced", Font.PLAIN, 15));
        g.setColor(Color.WHITE);
        g.drawString("Created by Your Name © 2024", 230, getHeight() - 20);
    }
}