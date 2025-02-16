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

    private final GameEngine gameEngine = new GameEngine(); // initialize game engine

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        this.gameEngine.startGameThread(); // start the game thread
        this.player = new Player(gameEngine);
        gameEngine.addPlayer(player, new KeyState()); // add new player
    }

    public void run() {
        try {
            // setup ObjectOutputStream / ObjectInputStream to communicate with client.
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());

            // Send initial game state
            synchronized (gameEngine) {
                GameState initialState = gameEngine.getCurrentGameState();
                out.writeObject(initialState);
                out.flush();
                System.out.println("[Server] Sent initial GameState to client");
            }

            // New thread for game state update
            // continuously send current GameState to the client at 60 fps
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

            // Listen for key inputs by the client
            while (!clientSocket.isClosed()) {
                try {
                    KeyState keyState = (KeyState) in.readObject();
                    System.out.println("Received key state from client: " + keyState);
                    synchronized (gameEngine) {
                        int playerIndex = gameEngine.getPlayers().indexOf(player);
                        if (playerIndex != -1) {
                            gameEngine.updateKeyState(playerIndex, keyState); // update player state in gameEngine
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
        } finally { // if client disconnects
            synchronized (gameEngine) {
                gameEngine.removePlayer(player); // remove player from gameEngine
                System.out.println("Removed player from game engine.");
            }
            try {
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
                if (clientSocket != null)
                    clientSocket.close(); // close the socket
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}