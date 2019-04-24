package com.zetcode;

import java.awt.EventQueue;
import javax.swing.JFrame;

//Snake貪食蛇主要類別，繼承自Java視窗類別JFrame
public class Snake extends JFrame {

	//建構子，建立此物件
    public Snake() {
        
        initUI();
    }

    private void initUI() {
        
    	//在視窗中放入繼承自JPanel的Board物件，繪圖和遊戲邏輯都放在Board裡
        add(new Board());
               
        setResizable(false);
        pack();
        
        setTitle("Snake");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    /*
     * 啟動程式
     * 必須public讓全域可見
     * 必須static可以獨立執行不需要依賴物件
     * 沒有回傳直所以是void
     * 必須要有接收參數String[] args的接口
     */
    public static void main(String[] args) {
        
        EventQueue.invokeLater(() -> {
        	//創建一個Snake貪食蛇物件，並顯示視窗
            JFrame ex = new Snake();
            ex.setVisible(true);
        });
    }
}
