package client;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Objects;
import javax.imageio.ImageIO;

public class SuperObject {
    public BufferedImage image;
    public int worldX, worldY;
    public String name;
    public boolean collision = false;

    public void loadImage(String path) {
        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(path)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2, int playerWorldX, int playerWorldY, int screenWidth, int screenHeight, int tileSize) {
        int screenX = worldX - playerWorldX + screenWidth / 2 - (tileSize / 2);
        int screenY = worldY - playerWorldY + screenHeight / 2 - (tileSize / 2);
        g2.drawImage(image, screenX, screenY, tileSize, tileSize, null);
    }
}