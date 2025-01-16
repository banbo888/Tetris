//ICS Summative - Tetris by Richard Xiong & Eric Ma
//Beta Program Submission
//2025-01-09
//GameInterface Class - Manages the display and updates of game-related information, including the current piece, held piece, upcoming pieces, game stats, action texts, and power-up notifications.

import java.awt.*;
import java.util.Queue;
import javax.swing.Timer;

public class GameInterface {
    private static final int BLOCK_SIZE = Tetromino.BLOCK_SIZE; // Block size from Tetromino class
    private int TOP_PANEL_HEIGHT; // Height of the top panel for game stats
    private int GRID_OFFSET_X; // Horizontal offset for grid positioning
    private Queue<Tetromino> pieceQueue; // Queue to hold upcoming tetromino pieces
    private Tetromino heldPiece; // The piece held by the player
    private Tetromino currentPiece; // The current piece on the game grid
    private int pieceX; // Current X position of the piece on the grid
    private int pieceY; // Current Y position of the piece on the grid
    private String gameState; // Current state of the game (e.g., challenge, sprint, etc.)
    private boolean isCountingDown; // Flag to track if the game is in countdown mode

    // Action text for displaying current game actions (e.g., combos, power-ups)
    private String actionText = "";
    private String tSpinText = "";
    private String pcText = "";
    private String backToBackText = "";
    private String comboString = "";

    // Power-up related fields
    private String powerUpText = ""; // Text for power-up notifications
    private boolean showPowerUpText = false; // Flag to show power-up text
    private long powerUpTextStartTime; // Start time of the power-up text
    private static final long POWER_UP_TEXT_DURATION = 3000; // Duration for showing power-up text (3 seconds)
    private String powerUpActiveText = ""; // Active power-up text (e.g., Slow Mode, Line Destroyer)
    private float powerUpFadeAlpha = 1.0f; // Fade effect alpha for power-up text
    private Timer powerUpFadeTimer; // Timer to control the fade effect of power-up text

    // Fade effect fields for action text
    private Timer fadeTimer; // Timer for action text fade effect
    private float fadeAlpha = 0.0f; // Alpha value for fading the action text
    private static final int FADE_INTERVAL = 75; // Interval for fade effect

    // Constructor to initialize grid offset and top panel height
    public GameInterface(int gridOffsetX, int topPanelHeight) {
        this.GRID_OFFSET_X = gridOffsetX;
        this.TOP_PANEL_HEIGHT = topPanelHeight;

        // Initialize the fade timer used for action text
        fadeTimer = new Timer(FADE_INTERVAL, e -> {
            fadeAlpha -= 0.02f; // Decrease alpha for fade effect
            if (fadeAlpha <= 0) {
                fadeAlpha = 0; // Ensure alpha doesn't go below 0
                actionText = ""; // Clear action text when fade completes
                ((Timer) e.getSource()).stop(); // Stop the fade timer
            }
        });
        fadeTimer.setRepeats(true); // Ensure the fade timer repeats

        // Initialize power-up fade timer
        powerUpFadeTimer = new Timer(FADE_INTERVAL, e -> {
            powerUpFadeAlpha -= 0.02f; // Decrease alpha for power-up fade effect
            if (powerUpFadeAlpha <= 0) {
                powerUpFadeAlpha = 0; // Ensure alpha doesn't go below 0
                showPowerUpText = false; // Hide power-up text when fade completes
                ((Timer) e.getSource()).stop(); // Stop the power-up fade timer
            }
        });
        powerUpFadeTimer.setRepeats(true); // Ensure the power-up fade timer repeats
    }

    // Method to trigger a power-up text display based on the power-up type
    public void triggerPowerUpText(int powerUp) {
        showPowerUpText = true; // Show power-up text
        powerUpTextStartTime = System.currentTimeMillis(); // Record the start time
        powerUpFadeAlpha = 1.0f; // Reset fade alpha to full opacity
        powerUpFadeTimer.restart(); // Restart the fade timer
        if (powerUp == 1) {
            powerUpActiveText = "SLOW MODE ACTIVATED!"; // Set text for slow mode power-up
        } else if (powerUp == 2) {
            powerUpActiveText = "LINE DESTROYER ACTIVATED!"; // Set text for line destroyer power-up
        }
    }

    // Method to update the power-up availability text
    public void powerUpAvailable(int powerUp) {
        if (powerUp == 0) {
            powerUpText = ""; // Clear power-up text if no power-up is available
        } else if (powerUp == 1) {
            powerUpText = "SLOW TIME:"; // Display text for slow time power-up
        } else if (powerUp == 2) {
            powerUpText = "DESTROY LINES:"; // Display text for line destroyer power-up
        }
    }

    // Method to draw game stats (time, lines left, score, etc.) on the screen
    public void drawStats(Graphics g, String timeElapsedFormatted, String timeRemaining, 
                          long timeElapsedMiliseconds, int piecesPlaced, int linesCleared, int score, int level, 
                          boolean powerUpAvailable, boolean powerUpUsed) {
        // Declare variables to manage displayed stats
        String timeString;
        String mainTime;
        String milliseconds;
        int linesRemaining;
        double piecePerSecond;
        double timeInSeconds;

        // Positioning variables
        int posX = 100;
        int posY = 625; // Position to align with held pieces
        FontMetrics fmMain, fmPiecesPlaced;
        int mainTimeWidth, piecesCounterWidth;

        // Handle countdown state based on game state
        if (!isCountingDown) {
            if (gameState.equals("GAME_SPRINT")) {
                timeString = timeElapsedFormatted; // Use formatted elapsed time for sprint
            } else if (gameState.equals("GAME_TIMETRIAL")) {
                timeString = timeRemaining; // Use remaining time for time trial
            } else {
                timeString = timeElapsedFormatted; // Use formatted elapsed time for other game states
            }
        } else {
            // If countdown is active, show default countdown values
            if (gameState.equals("GAME_SPRINT")) {
                timeString = "00:00.000"; // Show 0 for sprint mode
            } else if (gameState.equals("GAME_TIMETRIAL")) {
                timeString = "02:00.000"; // Show 2 minutes for time trial mode
            } else {
                timeString = "00:00.000"; // Show 0 for other game modes
            }
        }

        // Calculates lines remaining based on cleared lines
        if (linesCleared <= 40) {
            linesRemaining = 40 - linesCleared; // Lines left in sprint mode
        } else {
            linesRemaining = 0; // No lines remaining if cleared more than 40
        }

        // Calculates pieces placed per second
        timeInSeconds = timeElapsedMiliseconds / 1000.0;
        if (!isCountingDown) {
            piecePerSecond = piecesPlaced / timeInSeconds; // Pieces placed per second during active game
        } else {
            piecePerSecond = 0; // No pieces placed during countdown
        }
        
        // Split time string into main time and milliseconds parts
        mainTime = timeString.substring(0, timeString.lastIndexOf('.') + 1); // Extract main time part (e.g., "00:00.")
        milliseconds = timeString.substring(timeString.lastIndexOf('.') + 1); // Extract milliseconds part (e.g., "000")
    
        // Draw "TIME:" label
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 20)); // Smaller font for the label
        g.drawString("TIME:", posX, posY - 40);
    
        // Draw the main time part
        g.setFont(new Font("SansSerif", Font.BOLD, 35)); // Larger font for main time
        fmMain = g.getFontMetrics();
        mainTimeWidth = fmMain.stringWidth(mainTime); // Get width of main time string
        g.drawString(mainTime, posX, posY);
    
        // Draw the milliseconds part
        g.setFont(new Font("SansSerif", Font.PLAIN, 25)); // Smaller font for milliseconds
        g.drawString(milliseconds, posX + mainTimeWidth, posY); // Offset by the width of main time

        // Display power-up availability and usage instructions
        if (gameState.equals("GAME_CHALLENGE") && powerUpAvailable && !powerUpUsed) {
            g.setFont(new Font("SansSerif", Font.BOLD, 25));
            g.setColor(Color.GREEN);
            g.drawString(powerUpText, GRID_OFFSET_X + 300 + 30, posY - 40); // Display power-up text
            g.setFont(new Font("SansSerif", Font.PLAIN, 20));
            g.setColor(Color.WHITE);
            g.drawString("Press V to activate", GRID_OFFSET_X + 300 + 30, posY); // Display activation hint
        }

        // Draw other game stats (e.g., lines left, score, level) based on the game state
        if (gameState.equals("GAME_SPRINT")) {
            g.setFont(new Font("SansSerif", Font.BOLD, 20)); // Label font
            g.drawString("LINES LEFT:", posX, posY - 140);
            g.setFont(new Font("SansSerif", Font.BOLD, 35)); // Value font
            g.drawString(String.valueOf(linesRemaining), posX, posY - 95);
        } else if (gameState.equals("GAME_TIMETRIAL") || gameState.equals("GAME_CHALLENGE") || gameState.equals("GAME_PRACTICE")) {
            g.setFont(new Font("SansSerif", Font.BOLD, 20)); // Label font
            g.drawString("SCORE:", posX, posY - 140);
            g.setFont(new Font("SansSerif", Font.BOLD, 35)); // Value font
            g.drawString(String.valueOf(score), posX, posY - 95);
            if (gameState.equals("GAME_TIMETRIAL")) {
                g.setFont(new Font("SansSerif", Font.BOLD, 20)); // Label font
                g.drawString("LEVEL:", GRID_OFFSET_X + 300 + 30, posY - 40);
                g.setFont(new Font("SansSerif", Font.BOLD, 35)); // Value font
                g.drawString(String.valueOf(level), GRID_OFFSET_X + 300 + 30, posY); // Level counter
            }
        }

        // Draw pieces placed and pieces per second statistics
        g.setFont(new Font("SansSerif", Font.BOLD, 20)); // Label font
        g.drawString("PIECES:", posX, posY - 245);
        g.setFont(new Font("SansSerif", Font.BOLD, 35)); // Value font
        fmPiecesPlaced = g.getFontMetrics();
        piecesCounterWidth = fmPiecesPlaced.stringWidth(String.valueOf(piecesPlaced)); // Get width of pieces counter
        g.drawString(String.valueOf(piecesPlaced), posX, posY - 195); // Display pieces placed
        g.setFont(new Font("SansSerif", Font.PLAIN, 25)); // Font for pieces per second
        g.drawString(String.format("%.2f", piecePerSecond) + "/S", posX + piecesCounterWidth + 20, posY - 195); // Display pieces per second
    }

    public void drawQueue(Graphics g) {
        // Array declaration to hold the next pieces in the queue.
        Tetromino[] piecesArray; 
    
        // Set up the starting positions and spacing for drawing the queue
        int startX = GRID_OFFSET_X + 300 + 30; // Position for the right panel where the queue is drawn
        int startY = 120; // Initial Y position for the first piece
        int spacing = 85; // Vertical spacing between each queued piece
        
        // Draw the "NEXT" label indicating the upcoming pieces
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 25));
        g.drawString("NEXT", startX, startY - 20);
        
        // Convert the pieceQueue to an array and draw each piece in the queue
        piecesArray = pieceQueue.toArray(new Tetromino[0]);
        for (int i = 0; i < piecesArray.length; i++) {
            // For each piece in the queue, draw it at the calculated position
            piecesArray[i].draw(g, startX, startY + (i * spacing));
        }
    }
    
    public void updateState(Queue<Tetromino> pieceQueue, Tetromino heldPiece, 
                            Tetromino currentPiece, int pieceX, int pieceY, String gameState, boolean isCountingDown) {
        // Update the game state with the new pieces and position details.
        this.pieceQueue = pieceQueue;
        this.heldPiece = heldPiece;
        this.currentPiece = currentPiece;
        this.pieceX = pieceX;
        this.pieceY = pieceY;
        this.gameState = gameState;
        this.isCountingDown = isCountingDown;
    }
    
    public void drawHeldPiece(Graphics g) {
        // Defines the starting positions for drawing the held piece
        int startX = 100; // Position for the left side panel
        int startY = 120;
    
        // Draws the "HOLD" label to indicate the held piece area
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 25));
        g.drawString("HOLD (C)", startX, startY - 20);
    
        // If a piece is held, draw it at the specified location
        if (heldPiece != null) {
            heldPiece.draw(g, startX, startY);
        }
    }
    
    public void drawGhostPiece(Graphics g, Grid grid, int ghostVisibility) {        
        // Sets the initial Y position for the ghost piece to the current piece's position
        int ghostY = pieceY; 
        Graphics2D g2d = (Graphics2D) g.create();
        
        // Determine lowest valid position for the ghost piece by checking for collisions
        while (!grid.checkCollision(currentPiece, pieceX, ghostY + 1)) {
            ghostY++; // Move down until collision is detected
        }
        
        // Set opacity for the ghost piece (transparent to indicate a 'ghost' appearance)
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, ghostVisibility / 100.0f));
        
        // Draw the ghost piece at its calculated position
        currentPiece.draw(g2d, 
            GRID_OFFSET_X + pieceX * BLOCK_SIZE, 
            TOP_PANEL_HEIGHT + ghostY * BLOCK_SIZE
        );
        
        g2d.dispose();
    }
    
    public void triggerActionText(int linesCleared, boolean isTSpin, boolean isGridEmpty, int backToBackCounter, int comboCounter) {
        // Determine action text based on lines cleared
        switch (linesCleared) {
            case 1:
                actionText = "SINGLE";
                break;
            case 2:
                actionText = "DOUBLE";
                break;
            case 3:
                actionText = "TRIPLE";
                break;
            case 4:
                actionText = "QUAD";
                break;
            default:
                actionText = "";
                break;
        }
    
        // Handle T-spin text if T-spin was performed
        if (isTSpin) {
            tSpinText = "T-SPIN";
        } else {
            tSpinText = "";
        }
    
        // Handle All Clear text if the grid is empty
        if (isGridEmpty) {
            pcText = "ALL CLEAR!";
        } else {
            pcText = "";
        }
    
        // Handle back-to-back text for consecutive actions
        if (backToBackCounter == 2) {
            backToBackText = "B2B";
        } else if (backToBackCounter > 2) {
            backToBackText = "B2B x" + (backToBackCounter - 1);
        } else {
            backToBackText = "";
        }
        
        // Handles combo text for multiple consecutive line clears
        if (comboCounter >= 2) {
            comboString = String.valueOf(comboCounter - 1) + " COMBO";
        } else {
            comboString = "";
        }
    
        // If action text exists, reset fade and start the fade timer for a smooth transition
        if (!actionText.isEmpty()) {
            fadeAlpha = 1.0f; // Reset the fade effect
            fadeTimer.start(); // Start the fade timer to gradually reduce opacity
        }
    }
    
    public void drawActionText(Graphics g) {
        // Sets starting position for drawing the action text
        int posX = 100;
        int posY = 615;
        
        FontMetrics fmPowerUp;
        int powerUpWidth;
        
        Graphics2D g2dAction = (Graphics2D) g.create();
        Graphics2D g2dPowerUp = (Graphics2D) g.create();
    
        // Draw the regular action text (e.g., "SINGLE", "T-SPIN", etc.) with fading effect
        if (fadeAlpha > 0 && actionText != null && !actionText.isEmpty()) {
            g2dAction.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha));
            g2dAction.setFont(new Font("SansSerif", Font.BOLD, 35));
            g2dAction.setColor(Color.WHITE);
            g2dAction.drawString(actionText, posX, posY - 350);
            
            // Draw additional action details such as T-spin, All Clear, Back-to-Back, and Combo
            g2dAction.setFont(new Font("SansSerif", Font.BOLD, 25));
            g2dAction.setColor(Color.MAGENTA);
            g2dAction.drawString(tSpinText, posX, posY - 390);
            
            g2dAction.setFont(new Font("SansSerif", Font.BOLD, 45));
            g2dAction.setColor(new Color(255, 234, 0));
            g2dAction.drawString(pcText, posX + 220, posY - 360);
            
            g2dAction.setFont(new Font("SansSerif", Font.BOLD, 30));
            g2dAction.setColor(Color.CYAN);
            g2dAction.drawString(backToBackText, posX, posY - 310);
            
            g2dAction.setFont(new Font("SansSerif", Font.BOLD, 30));
            g2dAction.setColor(Color.YELLOW);
            g2dAction.drawString(comboString, posX, posY - 270);
            g2dAction.dispose();
        }
    
        // Draw power-up activation text if necessary
        if (showPowerUpText) {
            g2dPowerUp.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, powerUpFadeAlpha));
            g2dPowerUp.setFont(new Font("SansSerif", Font.BOLD, 40)); // Increased font size
            g2dPowerUp.setColor(new Color(0, 255, 255)); // Cyan color
            fmPowerUp = g2dPowerUp.getFontMetrics();
            powerUpWidth = fmPowerUp.stringWidth(powerUpActiveText);
        
            // Draw shadow for better visibility
            g2dPowerUp.setColor(Color.BLACK);
            g2dPowerUp.drawString(powerUpActiveText, 
                GRID_OFFSET_X + (GamePanel.GAME_WIDTH - powerUpWidth) / 2 + 2,
                posX + 52);
        
            // Draw the main text
            g2dPowerUp.setColor(new Color(0, 255, 255)); // Cyan color
            g2dPowerUp.drawString(powerUpActiveText, 
                GRID_OFFSET_X + (GamePanel.GAME_WIDTH - powerUpWidth) / 2,
                posX + 50);
            g2dPowerUp.dispose();
        
            // If the power-up duration has elapsed, hide the power-up text
            if (System.currentTimeMillis() - powerUpTextStartTime >= POWER_UP_TEXT_DURATION) {
                showPowerUpText = false;
            }
        }
    }
}    
