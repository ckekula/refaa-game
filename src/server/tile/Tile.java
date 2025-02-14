package server.tile;

import java.awt.image.BufferedImage;

public class Tile {
    public boolean collision = false;

    //  for cases where no image is passed
    public Tile() {
        // Default constructor
    }

    public Tile(boolean collision) {
        this.collision = collision;
    }
}
