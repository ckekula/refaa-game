package main;

import entity.NetworkPlayer;
import entity.Player;
import network.GameClient;
import network.GameServer;
import network.PlayerData;
import object.SuperObject;
import tile.TileManager;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GamePanel extends JPanel implements Runnable {

    // SCREEN SETTINGS
    final int originalTileSize = 16; // 16x16 tile
    final int scale = 3;
    public final int tileSize = originalTileSize * scale; // 48x48 tile
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol; // 768 pixels
    public final int screenHeight = tileSize * maxScreenRow; // 576 pixels

    // WORLD SETTINGS
    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;

    // FPS
    int FPS = 60;

    // SYSTEM
    TileManager tileM = new TileManager(this);
    KeyHandler keyH = new KeyHandler();
    Sound music = new Sound();
    Sound se = new Sound();
    public CollisionChecker cChecker = new CollisionChecker(this);
    public AssetSetter aSetter = new AssetSetter(this);
    public UI ui = new UI(this);
    Thread gameThread;

    // ENTITY AND OBJECT
    public Player player;
    public SuperObject obj[] = new SuperObject[10];

    // Multiplayer
    private Map<String, NetworkPlayer> players = new ConcurrentHashMap<>();
    public GameClient gameClient;
    private String localPlayerId;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
    }

    public void setupGame() {
        aSetter.setObject();
        playMusic(0);
    }

    public void setupMultiplayer(boolean isHost) {
        setupGame(); // setup basic game objects

        if(isHost) {
            new GameServer(this);
            try {
                Thread.sleep(1000);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }

        gameClient = new GameClient(this);
        gameClient.connect("localhost", 5000);

        // Wait a short time for initial connection
        try {
            Thread.sleep(500);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }

        startGameThread();
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start(); // Start the thread
    }

    @Override
    public void run() {
        double drawInterval = (double) 1000000000 / FPS; // 0.01666 seconds
        double nextDrawTime = System.nanoTime() + drawInterval;

        while (gameThread != null) {
            update();
            repaint();

            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime = remainingTime / 1000000;

                if (remainingTime < 0) {
                    remainingTime = 0;
                }
                Thread.sleep((long) remainingTime);

                nextDrawTime += drawInterval;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void update() {
        // Update local player
        if (player != null) {
            player.update();
        }

        // Update remote players
        for (NetworkPlayer netPlayer : players.values()) {
            netPlayer.update();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Draw Tiles
        tileM.draw(g2);

        // Draw Objects
        for(SuperObject obj : obj) {
            if(obj != null) {
                obj.draw(g2, this);
            }
        }

        // Draw all players if initialized
        if(!players.isEmpty()) {
            for(NetworkPlayer netPlayer : players.values()) {
                netPlayer.draw(g2);
            }
        }

        // Draw UI
        ui.draw(g2);

        g2.dispose();
    }

    public void addPlayer(String playerId, boolean isLocal) {
        NetworkPlayer newPlayer = new NetworkPlayer(this, isLocal ? keyH : null, playerId, isLocal);
        players.put(playerId, newPlayer);

        if (isLocal) {
            localPlayerId = playerId;
            player = newPlayer; // Assign local player reference
            System.out.println("Local player added with ID: " + playerId); // Debug log
        }
    }

    public void removePlayer(String playerId) {
        players.remove(playerId);
    }

    public void updatePlayerState(PlayerData data) {
        NetworkPlayer player = players.get(data.getPlayerId());
        if (player != null) {
            player.updateFromNetwork(data.getWorldX(), data.getWorldY(),
                    data.getDirection(), data.getSpriteNum());
        }
    }

    public void playMusic(int i) {
        music.setFile(i);
        music.play();
        music.loop();
    }

    public void stopMusic() {
        music.stop();
    }

    public void playSE(int i) {
        se.setFile(i);
        se.play();
    }
}
