package server;

import java.io.Serializable;

public class ObjectState implements Serializable {
    public int worldX;
    public int worldY;
    public String name;

    public ObjectState(int worldX, int worldY, String name) {
        this.worldX = worldX;
        this.worldY = worldY;
        this.name = name;
    }
}