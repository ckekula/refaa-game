package entity;

import main.GamePanel;
import main.KeyHandler;

public class NetworkPlayer extends Player {
    private String playerId;
    private boolean isLocalPlayer;

    public NetworkPlayer(GamePanel gp, KeyHandler keyH, String playerId, boolean isLocalPlayer) {
        super(gp, keyH);
        this.playerId = playerId;
        this.isLocalPlayer = isLocalPlayer;
    }

    @Override
    public void update() {
        if (isLocalPlayer) {
            super.update();

            // Only send state if there was actual movement
            if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed) {
                gp.gameClient.sendPlayerState(worldX, worldY, direction, spriteNum);
            }
        }
    }

    public void updateFromNetwork(int newWorldX, int newWorldY, String direction, int spriteNum) {
        if (!isLocalPlayer) {
            this.worldX = newWorldX;
            this.worldY = newWorldY;
            this.direction = direction;
            this.spriteNum = spriteNum;
        }
    }

    public String getPlayerId() {
        return playerId;
    }
}