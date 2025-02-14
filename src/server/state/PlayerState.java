package server.state;

import java.io.Serializable;

public class PlayerState implements Serializable {
    public int worldX;
    public int worldY;
    public String direction;
    public int spriteNum;
    public int hasKey;
}
