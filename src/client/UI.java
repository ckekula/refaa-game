package client;

import object.OBJ_Key;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

public class UI {
    main.GamePanel gp;
    private BufferedImage keyImage;
    Font arial_40,arial_80B;
    public String message = "";
    public boolean messageOn = false;
    public double playTime = 0;
    public boolean gameFinished = false;
    int messageCounter = 0;
    DecimalFormat dFormat = new DecimalFormat("#0.00");

    public UI (GamePanel gp){

        arial_40 = new Font("Arial",Font.BOLD,40);
        arial_80B = new Font("Arial",Font.BOLD,80);
        OBJ_Key key = new OBJ_Key();
        keyImage = key.image;
    }

    public void draw(Graphics2D g2, int screenWidth, int screenHeight, int tileSize, int hasKey) {

        if(gameFinished == true){

            g2.setFont(arial_40);
            g2.setColor(Color.white);

            String text;
            int textLength;
            int x;
            int y;

            text = "You found the treasure!";
            textLength = (int)g2.getFontMetrics().getStringBounds(text,g2).getWidth();
            x = gp.screenWidth/2 - textLength/2;
            y = gp.screenHeight/2 - (gp.tileSize*3);
            g2.drawString(text,x,y);

            text = "You time is :"+dFormat.format(playTime) + "!";
            textLength = (int)g2.getFontMetrics().getStringBounds(text,g2).getWidth();
            x = gp.screenWidth/2 - textLength/2;
            y = gp.screenHeight/2 - (gp.tileSize*4);
            g2.drawString(text,x,y);

            g2.setFont(arial_80B);
            g2.setColor(Color.yellow);
            text = "Congratulations!";
            textLength = (int)g2.getFontMetrics().getStringBounds(text,g2).getWidth();
            x = gp.screenWidth/2 - textLength/2;
            y = gp.screenHeight/2 + (gp.tileSize*2);
            g2.drawString(text,x,y);

            gp.gameThread = null;
        }
        else {
            g2.setFont(arial_40);
            g2.setColor(Color.white);
            g2.drawImage(keyImage, gp.tileSize / 2, gp.tileSize / 2, gp.tileSize, gp.tileSize, null);
            g2.drawString("Key= " + gp.player.hasKey, 74, 65);

            // TIME
            playTime += (double) 1 / 6;
            g2.drawString("Time: " + dFormat.format(playTime), gp.tileSize * 11, 65);

            // MESSAGE
            if (messageOn == true) {

                g2.setFont(g2.getFont().deriveFont(30F));
                g2.drawString(message, gp.tileSize / 2, gp.tileSize * 5);

                messageCounter++;

                if (messageCounter > 120) { // 2 second
                    messageCounter = 0;
                    messageOn = false;
                }
            }
        }
    }
}