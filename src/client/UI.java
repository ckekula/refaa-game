package client;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.Objects;
import javax.imageio.ImageIO;

public class UI {
    private BufferedImage keyImage;
    public String message = "";
    public boolean messageOn = false;
    public int hasKey;
    public double playTime = 0;
    public boolean gameFinished = false;
    private int messageCounter = 0;
    private final Font arial_40 = new Font("Arial", Font.BOLD, 40);
    private final Font arial_80B = new Font("Arial", Font.BOLD, 80);
    private final DecimalFormat dFormat = new DecimalFormat("#0.00");

    public UI() {
        try {
            keyImage = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/objects/key.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2, int screenWidth, int screenHeight, int tileSize, int hasKey) {
        System.out.println("[UI] Starting draw method");

        if (gameFinished) {
            System.out.println("[UI] Game finished, drawing end screen");
            g2.setFont(arial_40);
            g2.setColor(Color.white);

            String text;
            int textLength;
            int x;
            int y;

            text = "You found the treasure!";
            textLength = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
            x = screenWidth / 2 - textLength / 2;
            y = screenHeight / 2 - (tileSize * 3);
            g2.drawString(text, x, y);

            text = "Your time is: " + dFormat.format(playTime) + "!";
            textLength = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
            x = screenWidth / 2 - textLength / 2;
            y = screenHeight / 2 - (tileSize * 4);
            g2.drawString(text, x, y);

            g2.setFont(arial_80B);
            g2.setColor(Color.yellow);
            text = "Congratulations!";
            textLength = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
            x = screenWidth / 2 - textLength / 2;
            y = screenHeight / 2 + (tileSize * 2);
            g2.drawString(text, x, y);
        } else {
            System.out.println("[UI] Game not finished, drawing UI elements");
            g2.setFont(arial_40);
            g2.setColor(Color.white);
            g2.drawImage(keyImage, tileSize / 2, tileSize / 2, tileSize, tileSize, null);
            g2.drawString("Key= " + hasKey, 74, 65);

            // TIME
            System.out.println("[UI] Drawing time: " + dFormat.format(playTime));
            g2.drawString("Time: " + dFormat.format(playTime), tileSize * 11, 65);

            // MESSAGE
            if (messageOn) {
                System.out.println("[UI] Message on, drawing message: " + message);
                g2.setFont(g2.getFont().deriveFont(30F));
                g2.drawString(message, tileSize / 2, tileSize * 5);

                messageCounter++;
                if (messageCounter > 120) { // 2 seconds
                    System.out.println("[UI] Message duration exceeded, turning off message");
                    messageCounter = 0;
                    messageOn = false;
                }
            }
        }

        System.out.println("[UI] Finished draw method");
    }
}