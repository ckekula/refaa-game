package server;

import main.AssetSetter;
import main.CollisionChecker;

public class GameEngine implements Runnable {
    // Game settings
    public final int tileSize = 48;
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;
    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;

    // Game components
    public TileManager tileM;
    public CollisionChecker cChecker;
    public AssetSetter aSetter;
    public Player player;
    public SuperObject[] obj = new SuperObject[10];
    public KeyState keyState = new KeyState();
    private Thread gameThread;
    public int FPS = 60;

    // Game state
    public String message = " ";
    public boolean messageOn = false;
    public double playTime = 0;
    public boolean gameFinished = false;

    public GameEngine() {
        tileM = new TileManager(this);
        cChecker = new CollisionChecker(this);
        aSetter = new AssetSetter(this);
        player = new Player(this);
        aSetter.setObject();
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000.0 / FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;

        while (gameThread != null) {
            update();

            try {
                double remainingTime = (nextDrawTime - System.nanoTime()) / 1000000;
                if (remainingTime < 0) remainingTime = 0;
                Thread.sleep((long) remainingTime);
                nextDrawTime += drawInterval;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void update() {
        player.update(keyState);
        playTime += 1.0 / 60;

        // Check game completion
        if (gameFinished) {
            gameThread = null;
        }
    }

    public GameState getCurrentGameState() {
        GameState state = new GameState();
        state.playerWorldX = player.worldX;
        state.playerWorldY = player.worldY;
        state.playerDirection = player.direction;
        state.playerSpriteNum = player.spriteNum;
        state.hasKey = player.hasKey;
        state.message = message;
        state.messageOn = messageOn;
        state.playTime = playTime;
        state.gameFinished = gameFinished;
        state.objects = new ObjectState[obj.length];
        for (int i = 0; i < obj.length; i++) {
            if (obj[i] != null) {
                state.objects[i] = new ObjectState(obj[i].worldX, obj[i].worldY, obj[i].name);
            }
        }
        return state;
    }

    public void updateKeyState(KeyState keyState) {
        this.keyState = keyState;
    }
}