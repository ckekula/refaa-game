package server;

import server.object.SuperObject;
import server.state.KeyState;

import java.awt.*;

public class Player extends Entity {
    public int hasKey = 0;
    private final GameEngine gameEngine;

    public Player(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
        solidArea = new Rectangle(8, 16, 32, 32);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        setDefaultValues();
    }

    private void setDefaultValues() {
        worldX = 23 * 48;
        worldY = 21 * 48;
        speed = 4;
        direction = "down";
    }

    public void update(KeyState keyState) {
        if (keyState.upPressed || keyState.downPressed ||
                keyState.leftPressed || keyState.rightPressed) {

            // Update direction based on key input
            if (keyState.upPressed) {
                direction = "up";
            } else if (keyState.downPressed) {
                direction = "down";
            } else if (keyState.leftPressed) {
                direction = "left";
            } else if (keyState.rightPressed) {
                direction = "right";
            }

            // Check collisions using the server's GameEngine
            collisionOn = false;
            gameEngine.cChecker.checkTile(this);

            // Check object collisions and process pickups
            int objIndex = gameEngine.cChecker.checkObject(this, true);
            pickUpObject(objIndex);

            // Move the player if no collision occurred
            if (!collisionOn) {
                switch (direction) {
                    case "up":    worldY -= speed; break;
                    case "down":  worldY += speed; break;
                    case "left":  worldX -= speed; break;
                    case "right": worldX += speed; break;
                }
            }

            // Update animation
            spriteCounter++;
            if (spriteCounter > 12) {
                spriteNum = (spriteNum == 1) ? 2 : 1;
                spriteCounter = 0;
            }
        }
    }

    private void pickUpObject(int i) {
        if (i != 999) {
            synchronized (gameEngine) {
                SuperObject obj = gameEngine.obj[i];
                switch (obj.name) {
                    case "Key":
                        hasKey++;
                        gameEngine.obj[i] = null;
                        gameEngine.message = "You got a key!";
                        gameEngine.messageOn = true;
                        break;
                    case "Door":
                        if (hasKey > 0) {
                            hasKey--;
                            gameEngine.obj[i] = null;
                            gameEngine.message = "You opened the door!";
                            gameEngine.messageOn = true;
                        } else {
                            gameEngine.message = "You need a key!";
                            gameEngine.messageOn = true;
                        }
                        break;
                    case "Boots":
                        speed += 2;
                        gameEngine.obj[i] = null;
                        gameEngine.message = "Speed up!";
                        gameEngine.messageOn = true;
                        break;
                    case "Chest":
                        gameEngine.gameFinished = true;
                        break;
                }
            }
        }
    }
}
