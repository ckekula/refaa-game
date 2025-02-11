package client;

import java.awt.Graphics2D;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class SuperObject {
    public BufferedImage image;
    public int worldX, worldY;

    public void loadImage(String path) {
        try {
            image = ImageIO.read(getClass().getResourceAsStream(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2, int screenX, int screenY, int tileSize) {
        g2.drawImage(image, screenX, screenY, tileSize, tileSize, null);
    }
}