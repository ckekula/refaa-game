package client;

import java.awt.Graphics2D;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class TileManager {
    public Tile[] tile;
    public int[][] mapTileNum;

    public TileManager() {
        tile = new Tile[10];
        mapTileNum = new int[50][50]; // Match server's maxWorldCol/Row
        loadTiles();
        loadMap("/maps/world01.txt");
    }

    private void loadTiles() {
        try {
            tile[0] = new Tile(ImageIO.read(getClass().getResourceAsStream("/tiles/grass.png")));
            tile[1] = new Tile(ImageIO.read(getClass().getResourceAsStream("/tiles/wall.png")));
            tile[2] = new Tile(ImageIO.read(getClass().getResourceAsStream("/tiles/water.png")));
            tile[3] = new Tile(ImageIO.read(getClass().getResourceAsStream("/tiles/earth.png")));
            tile[4] = new Tile(ImageIO.read(getClass().getResourceAsStream("/tiles/tree.png")));
            tile[5] = new Tile(ImageIO.read(getClass().getResourceAsStream("/tiles/sand.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // loadMap() implementation similar to original single-player version
    // ...

    public void draw(Graphics2D g2, int playerWorldX, int playerWorldY, int screenWidth, int screenHeight, int tileSize) {
        // Camera-centered drawing logic from original game
        // ...
    }
}
