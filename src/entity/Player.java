package entity;

import main.GamePanel;
import main.KeyHandler;


public class Player extends Entity{
    GamePanel gp;
    KeyHandler keyh;

    public Player(GamePanel gp,KeyHandler keyh){
        this.gp = gp;
        this.keyh = keyh;
    }
}
