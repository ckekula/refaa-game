package server.tile;

import server.GameEngine;

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
        loadMap("/maps/world01.txt"); // load map
    }

    private void initializeTiles() {
        // Initialize tiles with collision properties
        tile[0] = new Tile(false); // Grass (no collision)
        tile[1] = new Tile(true); // Wall (collision)
        tile[2] = new Tile(true); // Water (collision)
        tile[3] = new Tile(false); // Earth (no collision)
        tile[4] = new Tile(true); // Tree (collision)
        tile[5] = new Tile(false); // Sand (no collision)
    }

    public void loadMap(String mapPath) {
        System.out.println("Loading map from: " + mapPath);
        try (InputStream is = getClass().getResourceAsStream(mapPath)) {
            assert is != null;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

                int row = 0;
                String line;

                while ((line = br.readLine()) != null && row < gp.maxWorldRow) {
                    String[] numbers = line.trim().split("\\s+");
                    for (int col = 0; col < Math.min(numbers.length, gp.maxWorldCol); col++) {
                        mapTileNum[col][row] = Integer.parseInt(numbers[col]);
                    }
                    row++;
                }
                System.out.println("Map loading complete.");
            }
        } catch (Exception e) {
            System.err.println("Error loading map: " + e.getMessage());
            e.printStackTrace();
        }
    }
}