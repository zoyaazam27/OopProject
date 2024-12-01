package com.mycompany.brick;

import com.mycompany.brick.MapGenerator;

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
    private boolean welcomeScreen = true;
    private int score = 0;
    private int totalBricks = 21;
    private Timer timer;
    private int delay = 8;

    private int playerX = 310;
    private int knifePosX = 120;
    private int knifePosY = 350;
    private int knifeXDir = -1;
    private int knifeYDir = -2;

    private MapGenerator map;

    private ImageIcon knifeIcon;
    private Image knifeImage;

    private ImageIcon backgroundIcon;
    private Image backgroundImage;

    private int elapsedSeconds = 0;
    private Timer gameClock;

    private double knifeAngle = 0;

    public GamePlay() {
        map = new MapGenerator(3, 7);
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        knifeIcon = new ImageIcon(getClass().getResource("/com/mycompany/brick/knifee.jpg"));
        knifeImage = knifeIcon.getImage().getScaledInstance(100, 30, Image.SCALE_DEFAULT);

        backgroundIcon = new ImageIcon(getClass().getResource("/com/mycompany/brick/b.jpg"));
        backgroundImage = backgroundIcon.getImage();

        timer = new Timer(delay, this);
        timer.start();

        gameClock = new Timer(1000, e -> {
            if (play) elapsedSeconds++;
        });
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        if (welcomeScreen) {
            // Draw the welcome screen
            g.drawImage(backgroundImage, 0, 0, panelWidth, panelHeight, null);

            g.setColor(Color.black);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("Welcome to Slicing Spree!", 60, 200);

            g.setFont(new Font("serif", Font.BOLD, 30));
            g.drawString("Press ENTER to start", 200, 300);
            return;
        }

        // Adjust for a fixed game area (no extra space at the bottom)
        g.setColor(Color.black);
        g.fillRect(0, 0, panelWidth, panelHeight); // Full game area without any extra space

        // Draw map (bricks)
        map.draw((Graphics2D) g);

        // Borders (only left, top, and right borders)
        g.setColor(Color.yellow);
        g.fillRect(0, 0, 3, panelHeight); // Left border
        g.fillRect(0, 0, panelWidth, 3); // Top border
        g.fillRect(panelWidth - 3, 0, 3, panelHeight); // Right border

        // Score display
        g.setColor(Color.white);
        g.setFont(new Font("serif", Font.BOLD, 25));
        g.drawString("Score: " + score, panelWidth - 150, 30);

        // Timer display
        g.setFont(new Font("serif", Font.BOLD, 20));
        g.drawString("Time: " + formatTime(elapsedSeconds), 20, 30);

        // Paddle (fixed position at the bottom)
        g.setColor(new Color(139, 0, 0));  // Dark red color
        g.fillRect(playerX, panelHeight - 50, 100, 8); // Paddle at the very bottom

        // Knife (with rotation)
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(knifePosX + 40, knifePosY + 10);
        g2d.rotate(Math.toRadians(knifeAngle));
        g2d.drawImage(knifeImage, -40, -10, this);
        g2d.rotate(-Math.toRadians(knifeAngle));
        g2d.translate(-(knifePosX + 40), -(knifePosY + 10));

        // Game Over condition
        if (knifePosY > panelHeight - 10) {
            play = false;
            stopGameClock();
            knifeXDir = 0;
            knifeYDir = 0;

            g.setColor(Color.red);
            g.setFont(new Font("serif", Font.BOLD, 30));
            g.drawString("Game Over! Your Score: " + score, 190, 300);

            g.setFont(new Font("serif", Font.BOLD, 25));
            g.drawString("Time Played: " + formatTime(elapsedSeconds), 230, 340);

            g.setFont(new Font("serif", Font.BOLD, 20));
            g.drawString("Press Enter to Restart", 230, 380);
        }

        // Victory condition
        if (totalBricks == 0) {
            play = false;
            stopGameClock();
            knifeXDir = 0;
            knifeYDir = 0;

            g.setColor(Color.green);
            g.setFont(new Font("serif", Font.BOLD, 30));
            g.drawString("You Won! Your Score: " + score, 190, 300);

            g.setFont(new Font("serif", Font.BOLD, 25));
            g.drawString("Time Played: " + formatTime(elapsedSeconds), 230, 340);

            g.setFont(new Font("serif", Font.BOLD, 20));
            g.drawString("Press Enter to Restart", 230, 380);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        timer.start();
        if (play) {
            if (new Rectangle(knifePosX, knifePosY, 80, 20).intersects(new Rectangle(playerX, getHeight() - 50, 100, 8))) {
                knifeYDir = -knifeYDir;
                knifeAngle += 30;
                if (knifeAngle >= 360) knifeAngle = 0;
            }

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

            knifePosX += knifeXDir;
            knifePosY += knifeYDir;

            // Prevent the knife from going out of bounds (left and right walls)
            if (knifePosX < 0) {
                knifePosX = 0;  // Keep knife within the left boundary
                knifeXDir = -knifeXDir;  // Change direction
            }
            if (knifePosX + 80 > getWidth()) {
                knifePosX = getWidth() - 80;  // Keep knife within the right boundary
                knifeXDir = -knifeXDir;  // Change direction
            }
            // Prevent the knife from going out of bounds at the top
            if (knifePosY < 0) {
                knifePosY = 0;
                knifeYDir = -knifeYDir;  // Change direction
            }
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            if (playerX >= getWidth() - 100) {
                playerX = getWidth() - 100; // Keep paddle within the right boundary
            } else {
                moveRight();
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            if (playerX < 10) {
                playerX = 10; // Keep paddle within the left boundary
            } else {
                moveLeft();
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (welcomeScreen) {
                welcomeScreen = false;
                repaint();
            } else if (!play) {
                resetGame();
            }
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
        play = true;
        welcomeScreen = false;
        knifePosX = 120;
        knifePosY = 350;
        knifeXDir = -1;
        knifeYDir = -2;
        score = 0;
        totalBricks = 21;
        map = new MapGenerator(3, 7);
        elapsedSeconds = 0;
        gameClock.restart();
        repaint();
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

    private void stopGameClock() {
        gameClock.stop();
    }
}

