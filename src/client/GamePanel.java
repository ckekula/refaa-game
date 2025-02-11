package client;

import entity.Player;
import object.SuperObject;
import server.GameState;
import server.KeyState;
import tile.TileManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class GamePanel extends JPanel {
    private GameState gameState;
    private final ObjectOutputStream out;
    private final KeyState keyState = new KeyState();
    private final TileManager tileM;
    private final Player playerForRendering;

    // Screen settings
    final int tileSize = 48;
    final int maxScreenCol = 16;
    final int maxScreenRow = 12;
    final int screenWidth = tileSize * maxScreenCol;
    final int screenHeight = tileSize * maxScreenRow;

    public GamePanel(ObjectOutputStream out) {
        this.out = out;
        tileM = new TileManager(this);
        playerForRendering = new Player();
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
        tileM.draw(g2);

        // Draw objects
        for (int i = 0; i < gameState.objects.length; i++) {
            if (gameState.objects[i] != null) {
                SuperObject obj = new SuperObject();
                obj.worldX = gameState.objects[i].worldX;
                obj.worldY = gameState.objects[i].worldY;
                obj.name = gameState.objects[i].name;
                obj.image = getObjectImage(obj.name);
                obj.draw(g2, this);
            }
        }

        // Draw player
        playerForRendering.worldX = gameState.playerWorldX;
        playerForRendering.worldY = gameState.playerWorldY;
        playerForRendering.direction = gameState.playerDirection;
        playerForRendering.spriteNum = gameState.playerSpriteNum;
        BufferedImage image = getPlayerImage();
        int screenX = screenWidth/2 - (tileSize/2);
        int screenY = screenHeight/2 - (tileSize/2);
        g2.drawImage(image, screenX, screenY, tileSize, tileSize, null);

        // Draw UI
        UI ui = new UI(this);
        ui.hasKey = gameState.hasKey;
        ui.message = gameState.message;
        ui.messageOn = gameState.messageOn;
        ui.playTime = gameState.playTime;
        ui.gameFinished = gameState.gameFinished;
        ui.draw(g2);

        g2.dispose();
    }

    private BufferedImage getPlayerImage() {
        // Implement based on direction and spriteNum
        return new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB);
    }

    private BufferedImage getObjectImage(String name) {
        // Implement based on object name
        return new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB);
    }
}