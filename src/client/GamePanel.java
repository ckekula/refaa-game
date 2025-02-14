package client;

import client.entity.Player;
import client.tile.TileManager;
import server.state.GameState;
import server.state.KeyState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

public class GamePanel extends JPanel {
    private GameState gameState;
    private final ObjectOutputStream out;
    private final KeyState keyState = new KeyState();
    private final TileManager tileM;
    private final Map<String, BufferedImage> objectImages = new HashMap<>();

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
        switch (keyCode) {
            case KeyEvent.VK_W -> keyState.upPressed = pressed;
            case KeyEvent.VK_S -> keyState.downPressed = pressed;
            case KeyEvent.VK_A -> keyState.leftPressed = pressed;
            case KeyEvent.VK_D -> keyState.rightPressed = pressed;
        }
        try {
            out.writeObject(keyState);
            out.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void updateGameState(GameState state) {
        this.gameState = state;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (gameState == null) return;

        Graphics2D g2 = (Graphics2D) g;

        // Draw tiles
        tileM.draw(g2, gameState.getPlayerWorldX(0), gameState.getPlayerWorldY(0), screenWidth, screenHeight, tileSize);

        // Draw objects
        for (int i = 0; i < gameState.objects.length; i++) {
            if (gameState.objects[i] != null) {
                BufferedImage image = getObjectImage(gameState.objects[i].name);
                int screenX = gameState.objects[i].worldX - gameState.getPlayerWorldX(0) + screenWidth / 2 - (tileSize / 2);
                int screenY = gameState.objects[i].worldY - gameState.getPlayerWorldY(0) + screenHeight / 2 - (tileSize / 2);
                g2.drawImage(image, screenX, screenY, tileSize, tileSize, null);
            }
        }

        // Draw players
        for (int i = 0; i < gameState.players.size(); i++) {
            Player player = new Player();
            player.worldX = gameState.players.get(i).worldX;
            player.worldY = gameState.players.get(i).worldY;
            player.direction = gameState.players.get(i).direction;
            player.spriteNum = gameState.players.get(i).spriteNum;
            BufferedImage image = player.getPlayerImage();
            int screenX = screenWidth / 2 - (tileSize / 2);
            int screenY = screenHeight / 2 - (tileSize / 2);
            g2.drawImage(image, screenX, screenY, tileSize, tileSize, null);
        }

        // Draw UI
        UI ui = new UI();
        ui.hasKey = gameState.getHasKey();
        ui.message = gameState.message;
        ui.messageOn = gameState.messageOn;
        ui.playTime = gameState.playTime;
        ui.gameFinished = gameState.gameFinished;
        ui.draw(g2, screenWidth, screenHeight, tileSize, ui.hasKey);

        g2.dispose();
    }

    private BufferedImage getObjectImage(String name) {
        return objectImages.computeIfAbsent(name, k -> {
            SuperObject obj = new SuperObject();
            obj.loadImage("/objects/" + name.toLowerCase() + ".png");
            return obj.image;
        });
    }
}