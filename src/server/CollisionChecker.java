package server;

import server.entity.Entity;

public class CollisionChecker {
    private final GameEngine ge;

    public CollisionChecker(GameEngine ge) {
        this.ge = ge;
    }

    public void checkTile(Entity entity) {
        int entityLeftCol = (entity.worldX + entity.solidArea.x) / ge.tileSize;
        int entityRightCol = (entity.worldX + entity.solidArea.x + entity.solidArea.width) / ge.tileSize;
        int entityTopRow = (entity.worldY + entity.solidArea.y) / ge.tileSize;
        int entityBottomRow = (entity.worldY + entity.solidArea.y + entity.solidArea.height) / ge.tileSize;

        switch (entity.direction) {
            case "up":
                entityTopRow = (entity.worldY + entity.solidArea.y - entity.speed) / ge.tileSize;
                if (ge.tileM.tile[ge.tileM.mapTileNum[entityLeftCol][entityTopRow]].collision ||
                        ge.tileM.tile[ge.tileM.mapTileNum[entityRightCol][entityTopRow]].collision) {
                    entity.collisionOn = true;
                }
                break;
            case "down":
                entityBottomRow = (entity.worldY + entity.solidArea.y + entity.speed) / ge.tileSize;
                if (ge.tileM.tile[ge.tileM.mapTileNum[entityLeftCol][entityBottomRow]].collision ||
                        ge.tileM.tile[ge.tileM.mapTileNum[entityRightCol][entityBottomRow]].collision) {
                    entity.collisionOn = true;
                }
                break;
            case "left":
                entityLeftCol = (entity.worldX + entity.solidArea.x - entity.speed) / ge.tileSize;
                if (ge.tileM.tile[ge.tileM.mapTileNum[entityLeftCol][entityTopRow]].collision ||
                        ge.tileM.tile[ge.tileM.mapTileNum[entityLeftCol][entityBottomRow]].collision) {
                    entity.collisionOn = true;
                }
                break;
            case "right":
                entityRightCol = (entity.worldX + entity.solidArea.x + entity.speed) / ge.tileSize;
                if (ge.tileM.tile[ge.tileM.mapTileNum[entityRightCol][entityTopRow]].collision ||
                        ge.tileM.tile[ge.tileM.mapTileNum[entityRightCol][entityBottomRow]].collision) {
                    entity.collisionOn = true;
                }
                break;
        }
    }

    public int checkObject(Entity entity, boolean isPlayer) {
        for(int i = 0; i < ge.obj.length; i++) {
            if(ge.obj[i] != null) {
                // Simplified collision check logic
                int objLeft = ge.obj[i].worldX + ge.obj[i].solidAreaDefaultX;
                int objRight = objLeft + 48;
                int objTop = ge.obj[i].worldY + ge.obj[i].solidAreaDefaultY;
                int objBottom = objTop + 48;

                int entityLeft = entity.worldX + entity.solidArea.x;
                int entityRight = entityLeft + entity.solidArea.width;
                int entityTop = entity.worldY + entity.solidArea.y;
                int entityBottom = entityTop + entity.solidArea.height;

                if(entityRight > objLeft && entityLeft < objRight &&
                        entityBottom > objTop && entityTop < objBottom) {
                    return i;
                }
            }
        }
        return 999;
    }
}