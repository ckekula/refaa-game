package main;

import javax.swing.*;

public class TestLauncher {
    public static void main(String[] args) {
        // Launch the multiplayer test in a separate thread to avoid blocking
        SwingUtilities.invokeLater(() -> {
            MultiplayerTest.main(args);
        });
    }
}