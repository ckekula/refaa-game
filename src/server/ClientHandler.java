package server;

import server.entity.Player;
import server.state.GameState;
import server.state.KeyState;

import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread {
    private final Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private final Player player;

    private GameEngine gameEngine = new GameEngine();

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        this.gameEngine = new GameEngine();
        this.gameEngine.startGameThread();
        this.player = new Player(gameEngine);
        gameEngine.addPlayer(player, new KeyState());
    }

    public void run() {
        try {
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());

            // Send initial game state
            synchronized (gameEngine) {
                GameState initialState = gameEngine.getCurrentGameState();
                out.writeObject(initialState);
                out.flush();
                System.out.println("[Server] Sent initial GameState to client");
            }

            // Game state update thread
            new Thread(() -> {
                try {
                    while (!clientSocket.isClosed()) {
                        GameState state;
                        synchronized (gameEngine) {
                            state = gameEngine.getCurrentGameState();
                        }
                        System.out.println("Sending game state to client.");
                        out.writeObject(state);
                        out.reset(); // Clear Object cache
                        out.flush();
                        Thread.sleep(1000 / 60);
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

            // Handle key inputs
            while (!clientSocket.isClosed()) {
                try {
                    KeyState keyState = (KeyState) in.readObject();
                    System.out.println("Received key state from client: " + keyState);
                    synchronized (gameEngine) {
                        int playerIndex = gameEngine.getPlayers().indexOf(player);
                        if (playerIndex != -1) {
                            gameEngine.updateKeyState(playerIndex, keyState);
                            System.out.println("Updated key state of player " + playerIndex + ": " + keyState);
                        }
                    }
                } catch (EOFException e) {
                    System.out.println("Client disconnected.");
                    break;
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            synchronized (gameEngine) {
                gameEngine.removePlayer(player);
                System.out.println("Removed player from game engine.");
            }
            try {
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
                if (clientSocket != null)
                    clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}