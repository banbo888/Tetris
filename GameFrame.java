//ICS Summative - Tetris by Richard Xiong & Eric Ma
//Final Program Submission
//2025-01-16
//GameFrame Class - Creates the main window that will host the Tetris game panel, setting up the basic window properties and displaying the game.

import javax.swing.*;

public class GameFrame extends JFrame {
    // Game panel that contains the game logic and rendering
    GamePanel panel;

    public GameFrame() {
        // Create and add game panel
        panel = new GamePanel();
        this.add(panel);

        // Set up window properties
        this.setTitle("TETRIX");
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }
}
