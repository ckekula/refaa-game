package client;

import server.state.GameState;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        String serverAddress = "localhost";
        int port = 12345;

        try {
            Socket socket = new Socket(serverAddress, port);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            // Wait for initial GameState
            GameState initialState = (GameState) in.readObject();
            System.out.println("[Client] Received initial GameState");

            JFrame window = new JFrame();
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setResizable(false);
            window.setTitle("REFAA Client");

            GamePanel gamePanel = new GamePanel(out);
            gamePanel.updateGameState(initialState);
            window.add(gamePanel);
            window.pack();
            window.setLocationRelativeTo(null);
            window.setVisible(true);

            // Thread to receive game state updates from the server
            new Thread(() -> {
                try {
                    while (true) {
                        GameState state = (GameState) in.readObject();
                        gamePanel.updateGameState(state);
                        gamePanel.repaint();
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}