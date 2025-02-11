package server;

import main.GameEngine;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TileManager {
    public Tile[] tile;
    public int[][] mapTileNum;
    private final GameEngine gp;

    public TileManager(GameEngine gp) {
        this.gp = gp;
        tile = new Tile[10];
        mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];
        initializeTiles();
        loadMap("/maps/world01.txt");
    }

    private void initializeTiles() {
        // Set collision properties only (no image loading)
        tile[0] = new Tile(); // Grass
        tile[1] = new Tile(); tile[1].collision = true; // Wall
        tile[2] = new Tile(); tile[2].collision = true; // Water
        tile[3] = new Tile(); // Earth
        tile[4] = new Tile(); tile[4].collision = true; // Tree
        tile[5] = new Tile(); // Sand
    }

    public void loadMap(String mapPath) {
        try(InputStream is = getClass().getResourceAsStream(mapPath);
            BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            int col = 0;
            int row = 0;

            while(row < gp.maxWorldRow) {
                String line = br.readLine();
                String[] numbers = line.split(" ");

                for(col = 0; col < gp.maxWorldCol; col++) {
                    mapTileNum[col][row] = Integer.parseInt(numbers[col]);
                }
                row++;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}