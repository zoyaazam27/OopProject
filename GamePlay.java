
package com.mycompany.brick;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import javax.swing.ImageIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GamePlay extends JPanel implements KeyListener, ActionListener {

    private boolean play = false;
    private int score = 0;
    private int totalBricks = 21;
    private Timer timer;
    private int delay = 8;

    // Player and knife positions
    private int playerX = 310;
    private int knifePosX = 120;
    private int knifePosY = 350;
    private int knifeXDir = -1;
    private int knifeYDir = -2;

    private MapGenerator map;

    // Knife image
    private ImageIcon knifeIcon;
    private Image knifeImage;

    // Timer variables
    private int elapsedSeconds = 0;
    private Timer gameClock;

    // Rotation angle for the knife
    private double knifeAngle = 0;

    public GamePlay() {
        map = new MapGenerator(3, 7);
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        // Load knife image and scale it if necessary
        knifeIcon = new ImageIcon(getClass().getResource("/com/mycompany/brick/knifee.jpg"));
        knifeImage = knifeIcon.getImage().getScaledInstance(80, 20, Image.SCALE_DEFAULT); // Adjust size of the knife image

        // Ball and paddle timer
        timer = new Timer(delay, this);
        timer.start();

        // Game clock timer (1-second interval)
        gameClock = new Timer(1000, e -> {
            if (play) elapsedSeconds++;
        });
        gameClock.start();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g); // Clear previous drawings

        // Background
        g.setColor(Color.black);
        g.fillRect(1, 1, 692, 592);

        // Draw map (bricks)
        map.draw((Graphics2D) g);

        // Borders
        g.setColor(Color.yellow);
        g.fillRect(0, 0, 3, 592);
        g.fillRect(0, 0, 692, 3);
        g.fillRect(691, 0, 3, 592);

        // Score display
        g.setColor(Color.white);
        g.setFont(new Font("serif", Font.BOLD, 25));
        g.drawString("Score: " + score, 590, 30);

        // Timer display
        g.setFont(new Font("serif", Font.BOLD, 20));
        g.drawString("Time: " + formatTime(elapsedSeconds), 20, 30);

        // Paddle
        g.setColor(Color.yellow);
        g.fillRect(playerX, 550, 100, 8);

        // Knife (instead of ball) with rotation
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(knifePosX + 40, knifePosY + 10);  // Move the knife to its position
        g2d.rotate(Math.toRadians(knifeAngle));  // Apply rotation
        g2d.drawImage(knifeImage, -40, -10, this); // Draw knife with rotation
        g2d.rotate(-Math.toRadians(knifeAngle)); // Reset rotation
        g2d.translate(-(knifePosX + 40), -(knifePosY + 10)); // Reset the translation

        // Game Over condition
        if (knifePosY > 570) {
            play = false;
            stopGameClock(); // Stop the game clock
            knifeXDir = 0;
            knifeYDir = 0;

            g.setColor(Color.red);
            g.setFont(new Font("serif", Font.BOLD, 30));
            g.drawString("Game Over! Your Score: " + score, 190, 300);

            g.setFont(new Font("serif", Font.BOLD, 25));
            g.drawString("Time Played: " + formatTime(elapsedSeconds), 230, 340); // Display elapsed time

            g.setFont(new Font("serif", Font.BOLD, 20));
            g.drawString("Press Enter to Restart", 230, 380);
        }

        // Victory condition
        if (totalBricks == 0) {
            play = false;
            stopGameClock(); // Stop the game clock
            knifeXDir = 0;
            knifeYDir = 0;

            g.setColor(Color.green);
            g.setFont(new Font("serif", Font.BOLD, 30));
            g.drawString("You Won! Your Score: " + score, 190, 300);

            g.setFont(new Font("serif", Font.BOLD, 25));
            g.drawString("Time Played: " + formatTime(elapsedSeconds), 230, 340); // Display elapsed time

            g.setFont(new Font("serif", Font.BOLD, 20));
            g.drawString("Press Enter to Restart", 230, 380);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        timer.start();
        if (play) {
            // Knife-paddle collision
            if (new Rectangle(knifePosX, knifePosY, 80, 20).intersects(new Rectangle(playerX, 550, 100, 8))) {
                knifeYDir = -knifeYDir;
                knifeAngle += 30;  // Rotate the knife by 30 degrees on each collision
                if (knifeAngle >= 360) {
                    knifeAngle = 0;  // Keep the angle within 0-360 degrees
                }
            }

            // Knife-brick collision
            for (int i = 0; i < map.map.length; i++) {
                for (int j = 0; j < map.map[0].length; j++) {
                    if (map.map[i][j] > 0) {
                        int brickX = j * map.bricksWidth + 80;
                        int brickY = i * map.bricksHeight + 50;
                        Rectangle brickRect = new Rectangle(brickX, brickY, map.bricksWidth, map.bricksHeight);

                        if (new Rectangle(knifePosX, knifePosY, 80, 20).intersects(brickRect)) {
                            map.setBricksValue(0, i, j);
                            totalBricks--;
                            score += 5;

                            if (knifePosX + 39 <= brickRect.x || knifePosX + 1 >= brickRect.x + map.bricksWidth) {
                                knifeXDir = -knifeXDir;
                            } else {
                                knifeYDir = -knifeYDir;
                            }
                            break;
                        }
                    }
                }
            }

            // Knife movement
            knifePosX += knifeXDir;
            knifePosY += knifeYDir;

            // Knife collision with walls
            if (knifePosX < 0 || knifePosX > 670) {
                knifeXDir = -knifeXDir;
            }
            if (knifePosY < 0) {
                knifeYDir = -knifeYDir;
            }
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            if (playerX >= 600) {
                playerX = 600;
            } else {
                moveRight();
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            if (playerX < 10) {
                playerX = 10;
            } else {
                moveLeft();
            }
        }

        // Make sure the game resets when Enter is pressed and the game is over (play == false)
        if (e.getKeyCode() == KeyEvent.VK_ENTER && !play) {
            resetGame();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    private void moveRight() {
        play = true;
        playerX += 20;
    }

    private void moveLeft() {
        play = true;
        playerX -= 20;
    }

    private void resetGame() {
        play = true;  // Start the game
        elapsedSeconds = 0; // Reset elapsed time
        startGameClock(); // Restart the game clock

        // Reset positions, scores, and map
        knifePosX = 120;
        knifePosY = 350;
        knifeXDir = -1;
        knifeYDir = -2;
        score = 0;
        playerX = 310;
        knifeAngle = 0; // Reset rotation
        map = new MapGenerator(3, 7);
        totalBricks = 21;

        repaint();
    }

    private void startGameClock() {
        gameClock.start();
    }

    private void stopGameClock() {
        gameClock.stop();
    }

    private String formatTime(int seconds) {
        int mins = seconds / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d", mins, secs);
    }
}
