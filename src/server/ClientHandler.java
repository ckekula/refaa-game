package server;

import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread {
    private Socket clientSocket;
    private GameEngine gameEngine;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        this.gameEngine = new GameEngine();
        gameEngine.startGameThread();
    }

    public void run() {
        try {
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());

            // Send initial game state
            out.writeObject(gameEngine.getCurrentGameState());
            out.flush();

            // Game state update thread
            new Thread(() -> {
                try {
                    while (true) {
                        GameState state = gameEngine.getCurrentGameState();
                        out.writeObject(state);
                        out.flush();
                        Thread.sleep(1000 / 60);
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

            // Handle key inputs
            while (true) {
                KeyState keyState = (KeyState) in.readObject();
                gameEngine.updateKeyState(keyState);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}