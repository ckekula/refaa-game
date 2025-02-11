package server;

public class CollisionChecker {
    private final GameEngine gp;

    public CollisionChecker(GameEngine gp) {
        this.gp = gp;
    }

    public void checkTile(Entity entity) {
        int entityLeftCol = (entity.worldX + entity.solidArea.x) / gp.tileSize;
        int entityRightCol = (entity.worldX + entity.solidArea.x + entity.solidArea.width) / gp.tileSize;
        int entityTopRow = (entity.worldY + entity.solidArea.y) / gp.tileSize;
        int entityBottomRow = (entity.worldY + entity.solidArea.y + entity.solidArea.height) / gp.tileSize;

        switch(entity.direction) {
            case "up":
                entityTopRow = (entity.worldY + entity.solidArea.y - entity.speed) / gp.tileSize;
                if(gp.tileM.tile[gp.tileM.mapTileNum[entityLeftCol][entityTopRow]].collision ||
                        gp.tileM.tile[gp.tileM.mapTileNum[entityRightCol][entityTopRow]].collision) {
                    entity.collisionOn = true;
                }
                break;
            // Similar checks for other directions...
        }
    }

    public int checkObject(Entity entity, boolean isPlayer) {
        for(int i = 0; i < gp.obj.length; i++) {
            if(gp.obj[i] != null) {
                // Simplified collision check logic
                int objLeft = gp.obj[i].worldX + gp.obj[i].solidAreaDefaultX;
                int objRight = objLeft + 48;
                int objTop = gp.obj[i].worldY + gp.obj[i].solidAreaDefaultY;
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