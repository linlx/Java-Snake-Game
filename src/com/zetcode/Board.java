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

	//蛇頭、蛇身、蘋果的 「基本大小」
    private final int DOT_SIZE = 20;
    
    //視窗寬度為幾個「基本大小」
    private final int DOT_W = 20;
    
    //視窗高度為幾個「基本大小」
    private final int DOT_H = 20;
    
    private final int B_WIDTH = DOT_SIZE*DOT_W;
    private final int B_HEIGHT = DOT_SIZE*DOT_H;
    private final int ALL_DOTS = DOT_W*DOT_H;
    
    //蘋果亂數位置，落點為0~(DOT_W-1)，避免畫到視窗畫布之外
    private final int RAND_POS = DOT_W-1;
    
    //遊戲速度，畫面刷新速度單位是ms，1000ms = 1sec
    private final int DELAY = 150;

    //蛇身體在畫布上的位置，(x,y)儲存內容是像素位置
    private final int snake_x[] = new int[ALL_DOTS];
    private final int snake_y[] = new int[ALL_DOTS];

    //蛇身初始長度
    private int body=3;
    
    //蘋果位置(x,y)
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

    //載入圖片
    private void loadImages() {

        ImageIcon iid = new ImageIcon("src/resources/dot.png");
        ball = getScaledImage(iid.getImage(),DOT_SIZE,DOT_SIZE);

        ImageIcon iia = new ImageIcon("src/resources/apple.png");
        apple = getScaledImage(iia.getImage(),DOT_SIZE,DOT_SIZE);

        ImageIcon iih = new ImageIcon("src/resources/head.png");
        head = getScaledImage(iih.getImage(),DOT_SIZE,DOT_SIZE);
    }
    
    //調整圖片大小
    private Image getScaledImage(Image srcImg, int w, int h){
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();

        return resizedImg;
    }

    /*
     * 初始化遊戲
     * 畫布坐標系(x,y)簡介如下，起點為左上角
     * (0,0),(1,0),(2,0),(3,0)
     * (0,1),(1,1),(2,1),(3,1)
     * (0,2),(1,2),(2,2),(3,2)
     * (0,3),(1,3),(2,3),(3,3)
     */
    private void initGame() {

    	//蛇在畫面中央
        for (int z = 0; z < body; z++) {
        	snake_x[z] = (int)(B_WIDTH/2)-(z*DOT_SIZE);
        	snake_y[z] = (int)(B_HEIGHT/2);
        	
        	System.out.print("snake("+z+","+z+"):"+snake_x[z]+","+snake_y[z]+"\n");
        }
        
        //亂數設定蘋果位置
        locateApple();

        //畫面刷新，每個 DELAY 時間後執行 ActionListener.actionPerformed()
        timer = new Timer(DELAY, this);
        timer.start();
    }

    //實作JPanel
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
    }
    
    //視窗畫布繪圖
    private void doDrawing(Graphics g) {
        
        if (inGame) {

        	//畫蘋果位置
            g.drawImage(apple, apple_x, apple_y, this);

            for (int z = 0; z < body; z++) {
                if (z == 0) {
                	//畫蛇頭
                    g.drawImage(head, snake_x[z], snake_y[z], this);
                } else {
                	//畫蛇身
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

    	//蛇頭位置=蘋果位置
        if ((snake_x[0] == apple_x) && (snake_y[0] == apple_y)) {

        	//蛇身+1
        	body++;
        	//重新亂數給蘋果位置
            locateApple();
        }
    }

    private void move() {

    	//蛇身每一格複製到前一格身體位置
        for (int z = body; z > 0; z--) {
        	snake_x[z] = snake_x[(z - 1)];
        	snake_y[z] = snake_y[(z - 1)];
        }

        //蛇頭向左一格，蛇身x-1格
        if (leftDirection) {
        	snake_x[0] -= DOT_SIZE;
        }

        //蛇頭向右一格，蛇身x+1格
        if (rightDirection) {
        	snake_x[0] += DOT_SIZE;
        }

        //蛇頭向上一格，蛇身y-1格
        if (upDirection) {
        	snake_y[0] -= DOT_SIZE;
        }

        //蛇頭向下一格，蛇身y+1格
        if (downDirection) {
        	snake_y[0] += DOT_SIZE;
        }
    }

    //檢查碰撞
    private void checkCollision() {

    	//檢查蛇頭與蛇身是否在同一位置
        for (int z = body; z > 0; z--) {

            if ((snake_x[0] == snake_x[z]) && (snake_y[0] == snake_y[z])) {
                inGame = false;
            }
        }

        //檢查蛇頭碰到高度下邊界
        if (snake_y[0] >= B_HEIGHT) {
            inGame = false;
        }

        //檢查蛇頭碰到高度上邊界
        if (snake_y[0] < 0) {
            inGame = false;
        }

        //檢查蛇頭碰到寬度右邊界
        if (snake_x[0] >= B_WIDTH) {
            inGame = false;
        }

        //檢查蛇頭碰到寬度左邊界
        if (snake_x[0] < 0) {
            inGame = false;
        }
        
        //如果狀態為結束遊戲，停止刷新計時器
        if (!inGame) {
            timer.stop();
        }
    }

    private void locateApple() {

    	//Math.random()亂數範圍在0.0~1.0之間
        int r = (int) (Math.random() * RAND_POS);
        apple_x = ((r * DOT_SIZE));

        r = (int) (Math.random() * RAND_POS);
        apple_y = ((r * DOT_SIZE));
    }

    //實作ActionListener
    @Override
    public void actionPerformed(ActionEvent e) {

        if (inGame) {

            checkApple();
            checkCollision();
            move();
        }

        repaint();
    }

    //按鍵事件，改變蛇動向上、下、左、右狀態機
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
