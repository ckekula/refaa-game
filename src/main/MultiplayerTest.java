package main;

import javax.swing.*;
import java.awt.*;

public class MultiplayerTest {
    public static void main(String[] args) {
        // Create first window (Host)
        JFrame window1 = new JFrame();
        window1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window1.setResizable(false);
        window1.setTitle("REFAA game - Player 1 (Host)");

        GamePanel gamePanel1 = new GamePanel();
        window1.add(gamePanel1);
        window1.pack();
        window1.setLocation(100, 100); // Position first window
        window1.setVisible(true);

        // Request focus for the first window
        gamePanel1.requestFocusInWindow();

        // Create second window (Client)
        JFrame window2 = new JFrame();
        window2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window2.setResizable(false);
        window2.setTitle("REFAA game - Player 2");

        GamePanel gamePanel2 = new GamePanel();
        window2.add(gamePanel2);
        window2.pack();
        window2.setLocation(900, 100); // Position second window
        window2.setVisible(true);

        // Request focus for the second window
        gamePanel2.requestFocusInWindow();

        // Add buttons for host/join
        JButton hostButton = new JButton("Host Game");
        JButton joinButton = new JButton("Join Game");

        hostButton.addActionListener(e -> {
            gamePanel1.setupMultiplayer(true);
            gamePanel1.requestFocusInWindow();
        });

        joinButton.addActionListener(e -> {
            gamePanel2.setupMultiplayer(false);
            gamePanel2.requestFocusInWindow();
        });

        // Add control panels
        JPanel controls1 = new JPanel();
        controls1.add(hostButton);
        window1.add(controls1, BorderLayout.SOUTH);

        JPanel controls2 = new JPanel();
        controls2.add(joinButton);
        window2.add(controls2, BorderLayout.SOUTH);
    }
}