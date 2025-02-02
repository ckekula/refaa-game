package network;

public class PlayerData {
    private String playerId;
    private int worldX;
    private int worldY;
    private String direction;
    private int spriteNum;
    private boolean isNewPlayer;
    private boolean isDisconnecting;

    public PlayerData(String playerId, boolean isNewPlayer) {
        this.playerId = playerId;
        this.isNewPlayer = isNewPlayer;
    }

    // Constructor for player state updates
    public PlayerData(String playerId, int worldX, int worldY, String direction, int spriteNum) {
        this.playerId = playerId;
        this.worldX = worldX;
        this.worldY = worldY;
        this.direction = direction;
        this.spriteNum = spriteNum;
        this.isNewPlayer = false;
    }

    // Getters and setters
    public String getPlayerId() { return playerId; }
    public int getWorldX() { return worldX; }
    public int getWorldY() { return worldY; }
    public String getDirection() { return direction; }
    public int getSpriteNum() { return spriteNum; }
    public boolean isNewPlayer() { return isNewPlayer; }
    public boolean isDisconnecting() { return isDisconnecting; }

    public void setDisconnecting(boolean disconnecting) {
        isDisconnecting = disconnecting;
    }
}