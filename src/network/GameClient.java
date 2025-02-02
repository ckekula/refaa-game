package network;

import com.google.gson.Gson;
import main.GamePanel;

import java.io.*;
import java.net.*;

public class GameClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private GamePanel gp;
    private Gson gson;
    private String playerId;
    private volatile boolean running;

    public GameClient(GamePanel gp) {
        this.gp = gp;
        this.gson = new Gson();
    }

    public void connect(String host, int port) {
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            running = true;

            // Start listening for server messages
            new Thread(this::receiveMessages).start();

        } catch (IOException e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void receiveMessages() {
        try {
            String message;
            while (running && (message = in.readLine()) != null) {
                PlayerData data = gson.fromJson(message, PlayerData.class);
                handlePlayerData(data);
            }
        } catch (IOException e) {
            System.err.println("Error receiving messages: " + e.getMessage());
            disconnect();
        }
    }

    private void handlePlayerData(PlayerData data) {
        if(data.isNewPlayer()) {
            if(playerId == null) {
                // This is our player ID from server
                playerId = data.getPlayerId();
                gp.addPlayer(playerId, true);
                // Signal that player is ready
                sendPlayerState(0, 0, "down", 1); // Send initial state
            } else {
                // New player joined
                gp.addPlayer(data.getPlayerId(), false);
            }
        } else if(data.isDisconnecting()) {
            gp.removePlayer(data.getPlayerId());
        } else {
            gp.updatePlayerState(data);
        }
    }

    public void sendPlayerState(int worldX, int worldY, String direction, int spriteNum) {
        if (playerId != null && out != null) {
            PlayerData data = new PlayerData(playerId, worldX, worldY, direction, spriteNum);
            out.println(gson.toJson(data));
        }
    }

    public void disconnect() {
        running = false;
        try {
            if (out != null) {
                PlayerData data = new PlayerData(playerId, false);
                data.setDisconnecting(true);
                out.println(gson.toJson(data));
                out.close();
            }
            if (in != null) in.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getPlayerId() {
        return playerId;
    }
}