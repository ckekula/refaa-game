package client.tile;

import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import javax.imageio.ImageIO;

public class TileManager {
    public Tile[] tile;
    public int[][] mapTileNum;

    public TileManager() {
        tile = new Tile[10];
        mapTileNum = new int[50][50];
        loadTiles();
        loadMap("/maps/world01.txt");
    }

    private void loadTiles() {
        try {
            tile[0] = new Tile(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/tiles/grass.png"))));
            tile[1] = new Tile(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/tiles/wall.png"))));
            tile[2] = new Tile(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/tiles/water.png"))));
            tile[3] = new Tile(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/tiles/earth.png"))));
            tile[4] = new Tile(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/tiles/tree.png"))));
            tile[5] = new Tile(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/tiles/sand.png"))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadMap(String mapPath) {
        try (InputStream is = getClass().getResourceAsStream(mapPath);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            int row = 0;
            while ((line = br.readLine()) != null && row < mapTileNum.length) {
                String[] numbers = line.split(" ");
                for (int col = 0; col < numbers.length && col < mapTileNum[row].length; col++) {
                    mapTileNum[col][row] = Integer.parseInt(numbers[col]);
                }
                row++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2, int playerWorldX, int playerWorldY, int screenWidth, int screenHeight, int tileSize) {
        int screenCenterX = screenWidth / 2;
        int screenCenterY = screenHeight / 2;

        for (int row = 0; row < mapTileNum.length; row++) {
            for (int col = 0; col < mapTileNum[row].length; col++) {
                int worldX = col * tileSize;
                int worldY = row * tileSize;
                int screenX = worldX - playerWorldX + screenCenterX;
                int screenY = worldY - playerWorldY + screenCenterY;

                if (screenX + tileSize > 0 && screenX < screenWidth &&
                        screenY + tileSize > 0 && screenY < screenHeight) {
                    int tileNum = mapTileNum[col][row];
                    g2.drawImage(tile[tileNum].image, screenX, screenY, tileSize, tileSize, null);
                }
            }
        }
    }
}
