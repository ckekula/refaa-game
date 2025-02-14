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

    private final GameEngine sharedGameEngine = new GameEngine();

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        this.player = new Player(sharedGameEngine);
        sharedGameEngine.addPlayer(player, new KeyState());
    }

    public void run() {
        try {
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());

            // Send initial game state
            synchronized (sharedGameEngine) {
                out.writeObject(sharedGameEngine.getCurrentGameState());
            }
            out.flush();

            // Game state update thread
            new Thread(() -> {
                try {
                    while (!clientSocket.isClosed()) {
                        GameState state;
                        synchronized (sharedGameEngine) {
                            state = sharedGameEngine.getCurrentGameState();
                        }
                        out.writeObject(state);
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
                    synchronized (sharedGameEngine) {
                        int playerIndex = sharedGameEngine.getPlayers().indexOf(player);
                        if (playerIndex != -1) {
                            sharedGameEngine.updateKeyState(playerIndex, keyState);
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
            synchronized (sharedGameEngine) {
                sharedGameEngine.removePlayer(player);
            }
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (clientSocket != null) clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}