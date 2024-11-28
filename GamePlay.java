package com.mycompany.brick;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.*;
import javax.swing.ImageIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

public class GamePlay extends JPanel implements KeyListener, ActionListener {

    private final Image backgroundImage;
    private boolean play = false;
    private int score = 0;
    private int totalBricks = 21;
    private Timer timer;
    private int delay = 8;

    private int playerX = 310; // Paddle position
    private int paddleWidth = 100; // Dynamic paddle width

    private int knifePosX = 120;
    private int knifePosY = 350;
    private int knifeXDir = -1;
    private int knifeYDir = -2;

    private int lives = 3; // Player lives
    private boolean doublePointsActive = false;

    private MapGenerator map;
    private ImageIcon knifeIcon;
    private Image knifeImage;
    private Random random = new Random();

    private int elapsedSeconds = 0; // Time tracker
    private Timer gameClock;

    private enum GameState {WELCOME, PLAYING, GAME_OVER}
    private GameState gameState = GameState.WELCOME;

    public GamePlay() {
        map = new MapGenerator(3, 7);
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        knifeIcon = new ImageIcon(getClass().getResource("/com/mycompany/brick/knifee.jpg"));
        knifeImage = knifeIcon.getImage().getScaledInstance(80, 20, Image.SCALE_DEFAULT);
        backgroundImage = new ImageIcon(getClass().getResource("/com/mycompany/brick/background.jpg")).getImage();

        timer = new Timer(delay, this);
        timer.start();

        gameClock = new Timer(1000, e -> {
            if (play) elapsedSeconds++;
        });
        gameClock.start();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        // Background
        g.setColor(Color.black);
        g.fillRect(1, 1, 692, 592);

        if (gameState == GameState.WELCOME) {
            showWelcomeScreen(g);
        } else if (gameState == GameState.PLAYING) {
            // Map and borders
            map.draw((Graphics2D) g);
            g.setColor(Color.yellow);
            g.fillRect(0, 0, 3, 592);
            g.fillRect(0, 0, 692, 3);
            g.fillRect(691, 0, 3, 592);

            // Score and lives
            g.setColor(Color.white);
            g.setFont(new Font("serif", Font.BOLD, 25));
            g.drawString("Score: " + score, 540, 30);
            g.drawString("Lives: " + lives, 20, 30);
            g.drawString("Time: " + formatTime(elapsedSeconds), 280, 30);

            // Paddle
            g.setColor(Color.yellow);
            g.fillRect(playerX, 550, paddleWidth, 8);

            // Knife
            drawKnife(g);

            // Game Over
            if (lives <= 0) {
                gameState = GameState.GAME_OVER;
                endGame(g, "Game Over! Your Score: " + score);
            }

            // Victory
            if (totalBricks == 0) {
                gameState = GameState.GAME_OVER;
                endGame(g, "You Won! Your Score: " + score);
            }
        } else if (gameState == GameState.GAME_OVER) {
            // Game Over screen
            endGame(g, "Game Over! Your Score: " + score);
        }
    }

    private void showWelcomeScreen(Graphics g) {
        g.setColor(Color.white);
        g.setFont(new Font("serif", Font.BOLD, 40));
        g.drawString("Welcome to the Slicing Spree!", 140, 250);

        g.setFont(new Font("serif", Font.BOLD, 30));
        g.drawString("Press Enter to Start", 230, 300);
    }

    private void drawKnife(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(knifeImage, knifePosX, knifePosY, this);

        // Trail effect
        g2d.setColor(new Color(255, 255, 255, 80)); // Semi-transparent white
        g2d.fillOval(knifePosX + 20, knifePosY + 10, 40, 40);
    }

    private void endGame(Graphics g, String message) {
        play = false;
        stopGameClock();
        knifeXDir = 0;
        knifeYDir = 0;

        g.setColor(Color.red);
        g.setFont(new Font("serif", Font.BOLD, 30));
        g.drawString(message, 190, 300);

        g.setFont(new Font("serif", Font.BOLD, 25));
        g.drawString("Time Played: " + formatTime(elapsedSeconds), 230, 340);

        g.setFont(new Font("serif", Font.BOLD, 20));
        g.drawString("Press Enter to Restart", 230, 380);
    }

    private void stopGameClock() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        timer.start();
        if (play) {
            knifePosX += knifeXDir;
            knifePosY += knifeYDir;

            // Bounce off walls
            if (knifePosX < 0 || knifePosX > 670) knifeXDir = -knifeXDir;
            if (knifePosY < 0) knifeYDir = -knifeYDir;

            // Paddle collision
            if (new Rectangle(knifePosX, knifePosY, 80, 20).intersects(new Rectangle(playerX, 550, paddleWidth, 8))) {
                knifeYDir = -knifeYDir;
                score += (doublePointsActive ? 10 : 5); // Double points logic
            }

            // Ball missed
            if (knifePosY > 570) {
                lives--;
                resetKnife();
            }

            // Brick collision
            checkBrickCollision();

            // Difficulty scaling
            if (elapsedSeconds % 20 == 0 && elapsedSeconds > 0) {
                knifeXDir *= 1.1;
                knifeYDir *= 1.1;
            }
        }
        repaint();
    }

    private void checkBrickCollision() {
        for (int i = 0; i < map.map.length; i++) {
            for (int j = 0; j < map.map[0].length; j++) {
                if (map.map[i][j] > 0) {
                    int brickX = j * map.bricksWidth + 80;
                    int brickY = i * map.bricksHeight + 50;
                    Rectangle brickRect = new Rectangle(brickX, brickY, map.bricksWidth, map.bricksHeight);

                    if (new Rectangle(knifePosX, knifePosY, 80, 20).intersects(brickRect)) {
                        map.setBricksValue(map.map[i][j] - 1, i, j);
                        score += (doublePointsActive ? 20 : 10); // Double points logic
                        totalBricks--;

                        // Random power-up chance
                        if (random.nextInt(10) == 0) spawnPowerUp(brickX, brickY);

                        knifeYDir = -knifeYDir;
                        break;
                    }
                }
            }
        }
    }

    private void spawnPowerUp(int x, int y) {
        int powerUpType = random.nextInt(3);
        switch (powerUpType) {
            case 0: // Extra Life
                lives++;
                break;
            case 1: // Paddle Expansion
                paddleWidth += 50;
                break;
            case 2: // Double Points
                doublePointsActive = true;
                Timer doublePointsTimer = new Timer(5000, e -> doublePointsActive = false); // 5 seconds
                doublePointsTimer.setRepeats(false);
                doublePointsTimer.start();
                break;
        }
    }

    private void resetKnife() {
        knifePosX = 120;
        knifePosY = 350;
        knifeXDir = -1;
        knifeYDir = -2;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT && playerX < 600) moveRight();
        if (e.getKeyCode() == KeyEvent.VK_LEFT && playerX > 10) moveLeft();
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (gameState == GameState.WELCOME) {
                gameState = GameState.PLAYING;
                resetGame();
            } else if (gameState == GameState.GAME_OVER) {
                gameState = GameState.PLAYING;
                resetGame();
            }
        }
    }

    private void resetGame() {
        totalBricks = 21;
        score = 0;
        lives = 3;
        elapsedSeconds = 0;
        map = new MapGenerator(3, 7);
        resetKnife();
        play = true;
        repaint();
    }

    private void moveRight() {
        if (playerX >= 600) return;
        playerX += 20;
    }

    private void moveLeft() {
        if (playerX <= 10) return;
        playerX -= 20;
    }

    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        seconds %= 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
