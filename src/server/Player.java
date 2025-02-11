package server;

import java.awt.*;

public class Player extends Entity {
    public int hasKey = 0;
    private final GameEngine gp;

    public Player(GameEngine gp) {
        this.gp = gp;
        solidArea = new Rectangle(8, 16, 32, 32);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        setDefaultValues();
    }

    private void setDefaultValues() {
        worldX = gp.tileSize * 23;
        worldY = gp.tileSize * 21;
        speed = 4;
        direction = "down";
    }

    public void update(KeyState keyState) {
        if(keyState.upPressed || keyState.downPressed ||
                keyState.leftPressed || keyState.rightPressed) {

            // Update direction
            if(keyState.upPressed) direction = "up";
            else if(keyState.downPressed) direction = "down";
            else if(keyState.leftPressed) direction = "left";
            else if(keyState.rightPressed) direction = "right";

            // Check collisions
            collisionOn = false;
            gp.cChecker.checkTile(this);

            // Check object collisions
            int objIndex = gp.cChecker.checkObject(this, true);
            pickUpObject(objIndex);

            // Move player if no collision
            if(!collisionOn) {
                switch(direction) {
                    case "up": worldY -= speed; break;
                    case "down": worldY += speed; break;
                    case "left": worldX -= speed; break;
                    case "right": worldX += speed; break;
                }
            }

            // Update animation
            spriteCounter++;
            if(spriteCounter > 12) {
                spriteNum = (spriteNum == 1) ? 2 : 1;
                spriteCounter = 0;
            }
        }
    }

    private void pickUpObject(int i) {
        if(i != 999) {
            SuperObject obj = gp.obj[i];
            switch(obj.name) {
                case "Key":
                    hasKey++;
                    gp.obj[i] = null;
                    gp.message = "You got a key!";
                    gp.messageOn = true;
                    break;
                case "Door":
                    if(hasKey > 0) {
                        hasKey--;
                        gp.obj[i] = null;
                        gp.message = "You opened the door!";
                        gp.messageOn = true;
                    } else {
                        gp.message = "You need a key!";
                        gp.messageOn = true;
                    }
                    break;
                case "Boots":
                    speed += 2;
                    gp.obj[i] = null;
                    gp.message = "Speed up!";
                    gp.messageOn = true;
                    break;
                case "Chest":
                    gp.gameFinished = true;
                    break;
            }
        }
    }
}