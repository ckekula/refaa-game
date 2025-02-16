package client;

import client.tile.TileManager;
import server.state.GameState;
import server.state.KeyState;
import server.state.PlayerState;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GamePanel extends JPanel {
    private GameState gameState;
    private final ObjectOutputStream out;
    private final KeyState keyState = new KeyState();
    private final TileManager tileM;
    private final Map<String, BufferedImage> objectImages = new HashMap<>();
    private final Sound sound = new Sound();
    private boolean playingBGM = false;

    // Screen settings
    final int tileSize = 48;
    final int maxScreenCol = 16;
    final int maxScreenRow = 12;
    final int screenWidth = tileSize * maxScreenCol;
    final int screenHeight = tileSize * maxScreenRow;

    public GamePanel(ObjectOutputStream out) {
        this.out = out;
        tileM = new TileManager();
        setPreferredSize(new Dimension(screenWidth, screenHeight));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        setupKeyListener();
        preloadObjectImages();
        playMusic(0);
    }

    private void preloadObjectImages() {
        String[] objects = { "Key", "Door", "Chest", "Boots" };
        for (String obj : objects) {
            objectImages.put(obj, loadObjectImage(obj));
        }
    }

    private void setupKeyListener() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKey(e.getKeyCode(), true);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                handleKey(e.getKeyCode(), false);
            }
        });
        setFocusable(true);
    }

    private void handleKey(int keyCode, boolean pressed) {
        try {
            switch (keyCode) {
                case KeyEvent.VK_W -> keyState.upPressed = pressed;
                case KeyEvent.VK_S -> keyState.downPressed = pressed;
                case KeyEvent.VK_A -> keyState.leftPressed = pressed;
                case KeyEvent.VK_D -> keyState.rightPressed = pressed;
            }
            out.writeObject(keyState);
            out.reset(); // Clear the object cache
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playMusic(int i) {
        if (!playingBGM) {
            sound.setFile(i);
            sound.play();
            sound.loop();
            playingBGM = true;
        }
    }

    private void stopMusic() {
        playingBGM = false;
        sound.stop();
    }

    private void playSE(int i) {
        sound.setFile(i);
        sound.play();
    }

    public void updateGameState(GameState newState) {
        GameState oldState = this.gameState;
        this.gameState = newState;

        if (newState.mapTileNum != null) {
            tileM.setMapTileNum(newState.mapTileNum);
        }

        // Play sound effects based on state changes
        if (oldState != null) {
            // Key collected
            if (newState.getHasKey() > oldState.getHasKey()) {
                playSE(1); // coin sound
            }
            // Door opened
            if (oldState.objects != null && newState.objects != null) {
                for (int i = 0; i < oldState.objects.length; i++) {
                    if (oldState.objects[i] != null &&
                            newState.objects[i] == null &&
                            "Door".equals(oldState.objects[i].name)) {
                        playSE(3); // unlock sound
                    }
                }
            }
            // Boots collected
            if (oldState.objects != null && newState.objects != null) {
                for (int i = 0; i < oldState.objects.length; i++) {
                    if (oldState.objects[i] != null &&
                            newState.objects[i] == null &&
                            "Boots".equals(oldState.objects[i].name)) {
                        playSE(2); // powerup sound
                    }
                }
            }
            // Game finished
            if (!oldState.gameFinished && newState.gameFinished) {
                stopMusic();
                playSE(4); // fanfare sound
            }
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (gameState == null) {
            System.out.println("[GamePanel] GameState is null, skipping paintComponent");
            return;
        }

        Graphics2D g2 = (Graphics2D) g;

        // Draw tiles
        System.out.println("[GamePanel] Painting tiles");
        paintTiles(g2);

        // Draw objects
        System.out.println("[GamePanel] Painting objects");
        for (int i = 0; i < gameState.objects.length; i++) {
            if (gameState.objects[i] != null) {
                BufferedImage image = getObjectImage(gameState.objects[i].name);
                int screenX = gameState.objects[i].worldX - gameState.getPlayerWorldX(0) + screenWidth / 2
                        - (tileSize / 2);
                int screenY = gameState.objects[i].worldY - gameState.getPlayerWorldY(0) + screenHeight / 2
                        - (tileSize / 2);
                g2.drawImage(image, screenX, screenY, tileSize, tileSize, null);
            }
        }

        // Draw players
        System.out.println("[GamePanel] Painting players");
        if (!gameState.players.isEmpty()) {
            PlayerState mainPlayer = gameState.players.get(0);
            BufferedImage image = getPlayerImage(mainPlayer);
            int screenX = screenWidth / 2 - (tileSize / 2);
            int screenY = screenHeight / 2 - (tileSize / 2);
            g2.drawImage(image, screenX, screenY, tileSize, tileSize, null);
        }

        // Draw UI
        System.out.println("[GamePanel] Painting UI");
        UI ui = new UI();
        ui.hasKey = gameState.getHasKey();
        ui.message = gameState.message;
        ui.messageOn = gameState.messageOn;
        ui.playTime = gameState.playTime;
        ui.gameFinished = gameState.gameFinished;
        ui.draw(g2, screenWidth, screenHeight, tileSize, ui.hasKey);

        g2.dispose();
    }

    private void paintTiles(Graphics2D g2) {
        if (gameState == null || gameState.mapTileNum == null || tileM == null) {
            System.out.println("Missing data for tile rendering:");
            System.out.println("gameState: " + (gameState == null ? "null" : "present"));
            System.out.println("mapTileNum: " + (gameState == null ? "n/a" : (gameState.mapTileNum == null ? "null" : "present")));
            System.out.println("tileM: " + (tileM == null ? "null" : "present"));
            return;
        }

        int playerWorldX = gameState.getPlayerWorldX(0);
        int playerWorldY = gameState.getPlayerWorldY(0);

        for (int worldCol = 0; worldCol < gameState.mapTileNum.length; worldCol++) {
            for (int worldRow = 0; worldRow < gameState.mapTileNum[worldCol].length; worldRow++) {
                int worldX = worldCol * tileSize;
                int worldY = worldRow * tileSize;
                int screenX = worldX - playerWorldX + screenWidth / 2 - (tileSize / 2);
                int screenY = worldY - playerWorldY + screenHeight / 2 - (tileSize / 2);

                // Only render tiles that are visible on screen (with some margin)
                if (screenX + tileSize > -tileSize && 
                    screenX < screenWidth + tileSize && 
                    screenY + tileSize > -tileSize && 
                    screenY < screenHeight + tileSize) {
                    
                    int tileNum = gameState.mapTileNum[worldCol][worldRow];
                    if (tileNum >= 0 && tileNum < tileM.tile.length && tileM.tile[tileNum] != null) {
                        g2.drawImage(tileM.tile[tileNum].image, screenX, screenY, tileSize, tileSize, null);
                    }
                }
            }
        }
    }

    private BufferedImage getPlayerImage(PlayerState state) {
        // Load player images if not already loaded
        if (playerImages.isEmpty()) {
            loadPlayerImages();
        }

        // Determine the correct image based on direction and spriteNum
        String key = state.direction + "_" + state.spriteNum;
        return playerImages.getOrDefault(key, playerImages.get("down_1")); // Default to down_1 if not found
    }

    // Cache for player images
    private final Map<String, BufferedImage> playerImages = new HashMap<>();

    private void loadPlayerImages() {
        try {
            // Load all player sprites
            playerImages.put("up_1",
                    ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/boy_up_1.png"))));
            playerImages.put("up_2",
                    ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/boy_up_2.png"))));
            playerImages.put("down_1",
                    ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/boy_down_1.png"))));
            playerImages.put("down_2",
                    ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/boy_down_2.png"))));
            playerImages.put("left_1",
                    ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/boy_left_1.png"))));
            playerImages.put("left_2",
                    ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/boy_left_2.png"))));
            playerImages.put("right_1",
                    ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/boy_right_1.png"))));
            playerImages.put("right_2",
                    ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/boy_right_2.png"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BufferedImage getObjectImage(String name) {
        return objectImages.computeIfAbsent(name, k -> {
            client.SuperObject obj = new client.SuperObject();
            obj.loadImage("/objects/" + name.toLowerCase() + ".png");
            return obj.image;
        });
    }

    private BufferedImage loadObjectImage(String name) {
        String imagePath = "/objects/" + name.toLowerCase() + ".png";
        System.out.println("Loading image from path: " + imagePath); // Debugging

        try {
            InputStream inputStream = getClass().getResourceAsStream(imagePath);
            if (inputStream == null) {
                System.err.println("Resource not found: " + imagePath);
                return null; // Use placeholder
            }
            BufferedImage image = ImageIO.read(inputStream);
            if (image != null) {
                return image;
            } else {
                System.err.println("Failed to load image for object: " + name);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null; // Use placeholder
    }
}