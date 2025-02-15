package server;

import server.entity.Player;
import server.object.SuperObject;
import server.state.GameState;
import server.state.KeyState;
import server.state.ObjectState;
import server.state.PlayerState;
import server.tile.TileManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameEngine implements Runnable {
    private final List<Player> players = Collections.synchronizedList(new ArrayList<>());
    private final List<KeyState> keyStates = Collections.synchronizedList(new ArrayList<>());

    // Game settings
    public final int tileSize = 48;
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;
    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;

    // Game components
    public static TileManager tileM;
    public static CollisionChecker cChecker;
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
        AssetSetter assetSetter = new AssetSetter(this);
        assetSetter.setObject();
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
                if (remainingTime < 0)
                    remainingTime = 0;
                Thread.sleep((long) remainingTime);
                nextDrawTime += drawInterval;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void addPlayer(Player player, KeyState keyState) {
        players.add(player);
        keyStates.add(keyState);
    }

    public synchronized void removePlayer(Player player) {
        int index = players.indexOf(player);
        if (index != -1) {
            players.remove(index);
            keyStates.remove(index);
        }
    }

    public synchronized void update() {
        for (int i = 0; i < players.size(); i++) {
            players.get(i).update(keyStates.get(i));
        }
        playTime += 1.0 / 60;

        if (gameFinished) {
            gameThread = null;
        }
    }

    public synchronized GameState getCurrentGameState() {
        GameState state = new GameState();
        state.players = new ArrayList<>();
        state.mapTileNum = tileM.mapTileNum;
        for (Player player : players) {
            PlayerState playerState = new PlayerState();
            playerState.worldX = player.worldX;
            playerState.worldY = player.worldY;
            playerState.direction = player.direction;
            playerState.spriteNum = player.spriteNum;
            playerState.hasKey = player.hasKey;
            state.players.add(playerState);
        }
        state.objects = new ObjectState[obj.length];
        for (int i = 0; i < obj.length; i++) {
            if (obj[i] != null) {
                state.objects[i] = new ObjectState(obj[i].worldX, obj[i].worldY, obj[i].name);
            }
        }
        state.message = message;
        state.messageOn = messageOn;
        state.playTime = playTime;
        state.gameFinished = gameFinished;
        System.out.println("Setting game state to: " + state);
        return state;
    }

    public void updateKeyState(int playerIndex, KeyState keyState) {
        keyStates.set(playerIndex, keyState);
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public int getTileSize() {
        return tileSize;
    }

    public List<Player> getPlayers() {
        return players;
    }
}