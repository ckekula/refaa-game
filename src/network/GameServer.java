package network;

import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;
import entity.NetworkPlayer;
import main.GamePanel;

public class GameServer {
    private ServerSocket serverSocket;
    private static final int PORT = 5000;
    private GamePanel gp;
    private Map<String, NetworkPlayer> players = new ConcurrentHashMap<>();
    private Map<String, PrintWriter> clientOutputs = new ConcurrentHashMap<>();
    private Gson gson = new Gson();

    public GameServer(GamePanel gp) {
        this.gp = gp;
        try {
            serverSocket = new ServerSocket(PORT);
            new Thread(this::acceptConnections).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void acceptConnections() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                String playerId = UUID.randomUUID().toString();
                new Thread(new ClientHandler(clientSocket, playerId)).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ClientHandler implements Runnable {
        private Socket socket;
        private String playerId;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket socket, String playerId) {
            this.socket = socket;
            this.playerId = playerId;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                clientOutputs.put(playerId, out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                // Send initial player ID to client
                PlayerData initialData = new PlayerData(playerId, true);
                out.println(gson.toJson(initialData));

                // Create and add player to GamePanel
                NetworkPlayer newPlayer = new NetworkPlayer(gp, null, playerId, false);
                players.put(playerId, newPlayer);
                gp.addPlayer(playerId, false);

                // Notify all clients about the new player
                broadcastNewPlayer(playerId);

                // Handle incoming messages
                String input;
                while ((input = in.readLine()) != null) {
                    PlayerData data = gson.fromJson(input, PlayerData.class);
                    updatePlayerState(data);
                    broadcastPlayerState(data);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // Remove player when disconnected
                players.remove(playerId);
                clientOutputs.remove(playerId);
                gp.removePlayer(playerId);
                broadcastPlayerDisconnect(playerId);
            }
        }
    }

    private void updatePlayerState(PlayerData data) {
        NetworkPlayer player = players.get(data.getPlayerId());
        if (player != null) {
            player.updateFromNetwork(data.getWorldX(), data.getWorldY(),
                    data.getDirection(), data.getSpriteNum());
            gp.updatePlayerState(data);
        }
    }

    private void broadcastNewPlayer(String playerId) {
        PlayerData newPlayerData = new PlayerData(playerId, false);
        String message = gson.toJson(newPlayerData);
        broadcastMessage(message);
    }

    private void broadcastPlayerState(PlayerData data) {
        String message = gson.toJson(data);
        broadcastMessage(message);
    }

    private void broadcastPlayerDisconnect(String playerId) {
        PlayerData disconnectData = new PlayerData(playerId, true);
        String message = gson.toJson(disconnectData);
        broadcastMessage(message);
    }

    private void broadcastMessage(String message) {
        for (PrintWriter out : clientOutputs.values()) {
            out.println(message);
        }
    }
}
