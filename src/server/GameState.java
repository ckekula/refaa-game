package server;

import java.io.Serializable;

public class GameState implements Serializable {
    public int playerWorldX;
    public int playerWorldY;
    public String playerDirection;
    public int playerSpriteNum;
    public int hasKey;
    public String message;
    public boolean messageOn;
    public double playTime;
    public boolean gameFinished;
    public ObjectState[] objects;
}