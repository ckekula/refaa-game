package server.state;

import java.io.Serializable;
import java.util.List;

public class GameState implements Serializable {
    public List<PlayerState> players;
    public ObjectState[] objects;
    public int[][] mapTileNum;
    public String message;
    public boolean messageOn;
    public double playTime;
    public boolean gameFinished;

    public int getPlayerWorldX(int playerIndex) {
        return players.get(playerIndex).worldX;
    }

    public int getPlayerWorldY(int playerIndex) {
        return players.get(playerIndex).worldY;
    }

    public String getPlayerDirection() {
        return players.isEmpty() ? "down" : players.get(0).direction;
    }

    public int getPlayerSpriteNum() {
        return players.isEmpty() ? 1 : players.get(0).spriteNum;
    }

    public int getHasKey() {
        return players.isEmpty() ? 0 : players.get(0).hasKey;
    }
}