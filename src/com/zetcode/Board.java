package com.zetcode;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements ActionListener {

    private final int DOT_SIZE = 20;
    
    private final int DOT_W = 20;
    
    private final int DOT_H = 20;
    
    private final int B_WIDTH = DOT_SIZE*DOT_W;
    private final int B_HEIGHT = DOT_SIZE*DOT_H;
    private final int ALL_DOTS = DOT_W*DOT_H;
    
    private final int RAND_POS = DOT_W-1;
    
    private final int DELAY = 150;

    private final int snake_x[] = new int[ALL_DOTS];
    private final int snake_y[] = new int[ALL_DOTS];

    private int body=3;
    
    private int apple_x;
    private int apple_y;

    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;
    private boolean inGame = true;

    private Timer timer;
    private Image ball;
    private Image apple;
    private Image head;

    public Board() {
        
        initBoard();
    }
    
    private void initBoard() {

        addKeyListener(new TAdapter());
        setBackground(Color.black);
        setFocusable(true);

        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        loadImages();
        initGame();
    }

    private void loadImages() {

        ImageIcon iid = new ImageIcon("src/resources/dot.png");
        ball = getScaledImage(iid.getImage(),DOT_SIZE,DOT_SIZE);

        ImageIcon iia = new ImageIcon("src/resources/apple.png");
        apple = getScaledImage(iia.getImage(),DOT_SIZE,DOT_SIZE);

        ImageIcon iih = new ImageIcon("src/resources/head.png");
        head = getScaledImage(iih.getImage(),DOT_SIZE,DOT_SIZE);
    }
    
    private Image getScaledImage(Image srcImg, int w, int h){
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();

        return resizedImg;
    }

    private void initGame() {

        for (int z = 0; z < body; z++) {
        	snake_x[z] = (int)(B_WIDTH/2)-(z*DOT_SIZE);
        	snake_y[z] = (int)(B_HEIGHT/2);
        	
        	System.out.print("snake("+z+","+z+"):"+snake_x[z]+","+snake_y[z]+"\n");
        }
        
        locateApple();

        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
    }
    
    private void doDrawing(Graphics g) {
        
        if (inGame) {

            g.drawImage(apple, apple_x, apple_y, this);

            for (int z = 0; z < body; z++) {
                if (z == 0) {
                    g.drawImage(head, snake_x[z], snake_y[z], this);
                } else {
                    g.drawImage(ball, snake_x[z], snake_y[z], this);
                }
            }

            Toolkit.getDefaultToolkit().sync();

        } else {

            gameOver(g);
        }        
    }

    private void gameOver(Graphics g) {
        
        String msg = "Game Over";
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr = getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 2);
    }

    private void checkApple() {

        if ((snake_x[0] == apple_x) && (snake_y[0] == apple_y)) {

        	body++;
          locateApple();
        }
    }

    private void move() {

        for (int z = body; z > 0; z--) {
        	snake_x[z] = snake_x[(z - 1)];
        	snake_y[z] = snake_y[(z - 1)];
        }

        if (leftDirection) {
        	snake_x[0] -= DOT_SIZE;
        }

        if (rightDirection) {
        	snake_x[0] += DOT_SIZE;
        }

        if (upDirection) {
        	snake_y[0] -= DOT_SIZE;
        }

        if (downDirection) {
        	snake_y[0] += DOT_SIZE;
        }
    }

    private void checkCollision() {

        for (int z = body; z > 0; z--) {

            if ((snake_x[0] == snake_x[z]) && (snake_y[0] == snake_y[z])) {
                inGame = false;
            }
        }

        if (snake_y[0] >= B_HEIGHT) {
            inGame = false;
        }

        if (snake_y[0] < 0) {
            inGame = false;
        }

        if (snake_x[0] >= B_WIDTH) {
            inGame = false;
        }

        if (snake_x[0] < 0) {
            inGame = false;
        }
        
        if (!inGame) {
            timer.stop();
        }
    }

    private void locateApple() {

        int r = (int) (Math.random() * RAND_POS);
        apple_x = ((r * DOT_SIZE));

        r = (int) (Math.random() * RAND_POS);
        apple_y = ((r * DOT_SIZE));
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (inGame) {

            checkApple();
            checkCollision();
            move();
        }

        repaint();
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if ((key == KeyEvent.VK_LEFT) && (!rightDirection)) {
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_RIGHT) && (!leftDirection)) {
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_UP) && (!downDirection)) {
                upDirection = true;
                rightDirection = false;
                leftDirection = false;
            }

            if ((key == KeyEvent.VK_DOWN) && (!upDirection)) {
                downDirection = true;
                rightDirection = false;
                leftDirection = false;
            }
        }
    }
}
