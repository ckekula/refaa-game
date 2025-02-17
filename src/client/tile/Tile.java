package client.tile;

import java.awt.image.BufferedImage;

public class Tile {

    public BufferedImage image;
    public boolean collision = false;

    public Tile() {
        // Default constructor for cases where no image is passed
    }

    public Tile (BufferedImage image) {
        this.image = image;
    }
}
