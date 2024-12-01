package com.mycompany.brick;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.Random;
import javax.swing.ImageIcon;

public class MapGenerator {
    public int[][] map;
    public int bricksWidth;
    public int bricksHeight;

    // Load fruit images
    private Image appleImage;
    private Image grapeImage;
    private Image orangeImage;

    // Keep track of which fruit is at each position
    private int[][] fruitTypes; // 1 for apple, 2 for grape, 3 for orange

    public MapGenerator(int row, int col) {
        map = new int[row][col];
        fruitTypes = new int[row][col];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                map[i][j] = 1; // Initially, a fruit is present

                // Randomly assign a fruit type
                fruitTypes[i][j] = new Random().nextInt(3) + 1; // 1, 2, or 3
            }
        }

        bricksWidth = 540 / col; // Width of the fruit (or space for the fruit)
        bricksHeight = 230 / row; // Height of the fruit (or space for the fruit)

        // Load fruit images from the resources
        appleImage = new ImageIcon(getClass().getResource("/com/mycompany/brick/apple.jpg")).getImage();
        grapeImage = new ImageIcon(getClass().getResource("/com/mycompany/brick/grape.jpg")).getImage();
        orangeImage = new ImageIcon(getClass().getResource("/com/mycompany/brick/orange.jpg")).getImage();
    }

    // Draw the fruits
    public void draw(Graphics2D g) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] > 0) {
                    int x = j * bricksWidth + 80;
                    int y = i * bricksHeight + 50;

                    // Determine which fruit to draw based on the fruit type
                    switch (fruitTypes[i][j]) {
                        case 1: // Apple
                            g.drawImage(appleImage, x, y, bricksWidth, bricksHeight, null);
                            break;
                        case 2: // Grape
                            g.drawImage(grapeImage, x, y, bricksWidth, bricksHeight, null);
                            break;
                        case 3: // Orange
                            g.drawImage(orangeImage, x, y, bricksWidth, bricksHeight, null);
                            break;
                    }

                    // Optional: Draw the border around the fruit
                    g.setStroke(new BasicStroke(3));
                    g.setColor(Color.BLACK);
                    g.drawRect(x, y, bricksWidth, bricksHeight); // Border for the fruit
                }
            }
        }
    }

    // Set the fruit's value to 0 (removes the fruit)
    public void setBricksValue(int value, int row, int col) {
        map[row][col] = value;
    }
}
