//ICS Summative - Tetris by Richard Xiong & Eric Ma
//Final Program Submission
//2025-01-16
//GamePanel Class - Manages all of the logic in the game (i.e. the loop, the thread, as well as general mechanisms of tetris like the menu, game phase, etc.)

import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.*;

public class GamePanel extends JPanel implements Runnable, KeyListener {

    //Variable Declaration
    
    public static final int GAME_WIDTH = 300;  // Width of main grid
    private static final int TOP_PANEL_HEIGHT = 100; // Adjust as needed
    private static final int BOTTOM_PANEL_HEIGHT = 50; // Adjust as needed 
    public static final int GAME_HEIGHT = 600 + TOP_PANEL_HEIGHT + BOTTOM_PANEL_HEIGHT; // Original height plus new panels
    private static final int BLOCK_SIZE = Tetromino.BLOCK_SIZE;
    private static final int GRID_COLS = GAME_WIDTH / BLOCK_SIZE;
    private static final int SIDE_PANEL_WIDTH = 300; // Width of side panels
    private static final int GRID_OFFSET_X = SIDE_PANEL_WIDTH; // Offset for grid drawing and calculations

    // Game Piece variables
    private Queue<Tetromino> pieceQueue;
    private int pieceX = GRID_COLS / 2 - 2;
    private int pieceY = 0;
    private boolean softDropActive = false;
    private boolean canHold = true;

    // Softdrop variables
    private int lockDelayTime;
    private int currentLockDelayTime = 0;
    private int movementCounter = 0;
    private static final int MAX_MOVEMENTS = 20;
    private boolean isLockDelayActive = false;

    // Handling settings variables (Delayed Auto Shift, Auto Repeat Rate, Soft Drop Factor) 
    private int dasDelay; // Delay before auto-shift starts (in frames, 1-20)
    private int arrDelay; // Auto-repeat rate (in frames, 1-5)
    private int softdropFactor; // Soft drop speed factor (1-100)
    private int dasCharge = 0;
    private boolean leftKeyPressed = false;
    private boolean rightKeyPressed = false;
    private int lastKeyPressed = 0; // 0: None, -1: Left, 1: Right

    // Video and Audio settings variables
    private boolean audioEnabled;
    private int gridVisibility;
    private int ghostVisibility;
    private boolean actionTextOn;

    // Countdown timer variables
    private boolean isCountingdown = false;
    private long countdownStartTime;
    private int lastCountdownIndex = -1;
    private static final String[] COUNTDOWN_TEXT = {"GO!", "SET", "READY"};

    // Line clear logic variable (stores line clears, back to back, combo, and Tspin stats)
    private int currentLinesCleared = 0; // Check how many lines were cleared in a move
    private int backToBackCounter = 0; // Check for back-to-back (quads and Tspins)
    private int comboCounter = 0;
    private boolean lastKeyValidRotation = false; // Check if the last key pressed was a rotation
    private int score = 0;
    private int harddropDistance = 0;
    private String result = "";
    
    //Gravity logic
    private int level;
    private double gravityFactor;

    // Score system
    private boolean isHighScore;

    // Powerups System
    private boolean powerUpUsed = false;
    private boolean powerUpAvailable = false;
    private boolean powerUpStored = false;
    private boolean slowTimeActive = false;
    private boolean lineDestroyerActive = false;
    private long slowTimeStarted = 0;
    private boolean slowTimeAvailable = false;
    private boolean lineDestroyerAvailable = false;
    private static final long SLOW_TIME_DURATION = 10000; // 3 seconds in milliseconds
    private long lastPowerUpTime;
    private static final long POWER_UP_INTERVAL = 15000;

    Thread gameThread;
    Tetromino currentPiece;
    Tetromino heldPiece;
    Grid grid;
    PieceBagGenerator bagGenerator;
    GameState currentState = GameState.MENU; // Current state of the game
    GameState previousState;
    MenuScreen menu;
    ExitScreen pauseScreen;
    ExitScreen loseScreen;
    ScoreScreen scoreScreen;
    Timer gameTimer;
    GameInterface gameInterface;
    SettingsManager settings;
    ScoreManager scoreManager;
    SoundManager sound;


    // Enum for game states
    public enum GameState {
        MENU, // Main menu state
        GAME_SPRINT,      // Active game state (Sprint)
        GAME_TIMETRIAL,  // Active game state (Time Trial)
        GAME_PRACTICE,  // Active game state (Practice)
        GAME_CHALLENGE, // Active game state (Challenge)
        SCORE_SCREEN, // Win screen state
        LOSE_SCREEN, // Lose screen state
        PAUSE       // Pause state

    }

    //GamePanel Constructor
    public GamePanel() {
        // Initiatialize elements
        menu = new MenuScreen(this);
        pauseScreen = new ExitScreen(this, "EXIT TO MAIN MENU?", "RESUME", "EXIT TO MAIN MENU", "PAUSE");
        loseScreen = new ExitScreen(this, "GAME OVER", "TRY AGAIN", "EXIT TO MAIN MENU", "LOSE");
        scoreManager = new ScoreManager();
        scoreScreen = new ScoreScreen(this, result, currentState.toString(), isHighScore);
        grid = new Grid();
        settings = new SettingsManager();
        gameTimer = new Timer();
        pieceQueue = new LinkedList<>();
        bagGenerator = new PieceBagGenerator();
        gameInterface = new GameInterface(GRID_OFFSET_X, TOP_PANEL_HEIGHT);
        initializePieceQueue();
        updateSettings(settings);

        currentPiece = pieceQueue.poll();

        this.setFocusable(true);
        this.addKeyListener(this);
        this.setPreferredSize(new Dimension(
            GAME_WIDTH + (2 * SIDE_PANEL_WIDTH), 
            GAME_HEIGHT
        ));        
        this.setBackground(Color.BLACK); 
        this.setDoubleBuffered(true);
        this.setLayout(new BorderLayout());
        add(menu, BorderLayout.CENTER);


        gameThread = new Thread(this);
        gameThread.start();
        
        SoundManager.playMusic("music/theme.wav"); // Play background music

        // Action Listeners for Pause Screen
        pauseScreen.getResumeButton().addActionListener(e -> {
            currentState = previousState; // resume the game
            remove(pauseScreen);
            revalidate();
            repaint();
        });

        pauseScreen.getMainMenuButton().addActionListener(e -> {
            currentState = GameState.MENU;
            SoundManager.playMusic("music/theme.wav");

            menu.resetToMainMenu(null);
            remove(pauseScreen);
            revalidate();
            repaint();
        });

        // Action Listeners for Lose Screen
        loseScreen.getResumeButton().addActionListener(e -> {
            currentState = previousState;
            restartGame();
            remove(loseScreen);
            revalidate();
            repaint();
            loseScreen.resetButtonAppearance(loseScreen.getMainMenuButton());
        });

        loseScreen.getMainMenuButton().addActionListener(e -> {
            currentState = GameState.MENU;
            SoundManager.playMusic("music/theme.wav");

            menu.resetToMainMenu(null);
            remove(loseScreen);
            revalidate();
            repaint();
            loseScreen.resetButtonAppearance(loseScreen.getMainMenuButton());
        });
    }

    //Method to start game
    public void startGame(int mode) {
        //Handling 4 Gamemodes
        switch (mode) {
            case 1:
                currentState = GameState.GAME_SPRINT;
                break;
            case 2:
                currentState = GameState.GAME_TIMETRIAL;
                break;
            case 3:
                currentState = GameState.GAME_PRACTICE;
                break;
            case 4:
                currentState = GameState.GAME_CHALLENGE;
                break;
        }
        restartGame();
        repaint();
        
    }

    //Method to start countdown (before user can play)
    private void startCountdown() {
        isCountingdown = true;
        countdownStartTime = System.currentTimeMillis() - 1000; // Set to current time directly
        repaint();
    }

    //Method to draw countdown via Graphics
    private void drawCountdown(Graphics g) {
        long currentTime, elapsedTime;
        int displayIndex;
        String countdownText;

        // Formatting variables
        FontMetrics fmCountdown;
        Font originalFont, countdownFont;
        int textWidth, textHeight;

        originalFont = g.getFont();
        countdownFont = new Font("Arial", Font.BOLD, 72);
        g.setFont(countdownFont);
        
        // Calculate time elapsed and current display
        currentTime = System.currentTimeMillis();
        elapsedTime = currentTime - countdownStartTime;
        displayIndex = (int) Math.max(0, Math.min(3, 3 - (elapsedTime / 1000)));
        
        // Only draw if within valid range (including GO!)
        if (displayIndex >= 0 && displayIndex < COUNTDOWN_TEXT.length) {
            // Draw semi-transparent background
            g.setColor(new Color(0, 0, 0, 128));
            g.fillRect(0, 0, getWidth(), getHeight());
            
            // Draw countdown text
            countdownText = COUNTDOWN_TEXT[displayIndex];

            fmCountdown = g.getFontMetrics();
            textWidth = fmCountdown.stringWidth(countdownText);
            textHeight = fmCountdown.getHeight();
            
            g.setColor(Color.WHITE);
            g.drawString(countdownText, 
                (getWidth() - textWidth) / 2,
                (getHeight() - textHeight) / 2 + fmCountdown.getAscent()
            );
        }

        // Play sound effect for countdown
        if (displayIndex != lastCountdownIndex) {
            lastCountdownIndex = displayIndex;
            switch (displayIndex) {
                case 0:
                    SoundManager.playSound("sfx/go.wav");
                    break;
                case 1:
                    SoundManager.playSound("sfx/readyset.wav");
                    break;
                case 2:
                    SoundManager.playSound("sfx/readyset.wav");
                    break;
                case 3:
                    SoundManager.playSound("sfx/readyset.wav");
                    break;
            }
        }

        g.setFont(originalFont);
        
        // Check if countdown is complete
        gameTimer.stop();
        if (elapsedTime >= 4000) { // 4 seconds total duration
            isCountingdown = false;
            gameTimer.start(); // Start the game timer
        }
    }

    //Method to restart game once game over or task complete
    public void restartGame() {
        startCountdown();

        // Reset grid
        grid = new Grid();

        // Reset piece queue and generator
        pieceQueue.clear();
        bagGenerator = new PieceBagGenerator(); // Reset the bag generator
        initializePieceQueue();
        
        // Reset held piece
        heldPiece = null;
        
        // Reset piece position
        pieceX = GRID_COLS / 2 - 2;
        pieceY = 0;
        
        // Reset hold ability
        canHold = true;

        //Reset keys pressed
        lastKeyPressed = 0;
        
        // Get first piece from queue
        currentPiece = pieceQueue.poll();
        spawnNewPiece();
        
        // Restart stats
        grid.resetStats();
        score = 0;
        level = 0;
        gravityFactor = 1;
        backToBackCounter = 0;
        gameTimer.reset();  // Reset the timer
        gameTimer.start();  // Restart the timer

        // Store game mode
        previousState = currentState;

        //Store powerup stats
        powerUpAvailable = false;
        powerUpUsed = false;
        powerUpStored = false;
        slowTimeActive = false;
        lastPowerUpTime = System.currentTimeMillis(); // Reset the power-up timer when game restarts
        // Repaint to update the display
        repaint();
    }
    //Method to initialize piece queue (subsequent pieces the user will place)
    private void initializePieceQueue() {
        //Cycle through first bag
        for(int i = 0; i < 6; i++){
            bagGenerator.getNextPiece();
        }

        for (int i = 0; i < 6; i++) { // Add all 7 pieces to the queue initially
            pieceQueue.add(bagGenerator.getNextPiece());
        }
    }
    //Method to add piece to the queue randomly via bagGenerator
    private void addPieceToQueue() {
        pieceQueue.add(bagGenerator.getNextPiece());
    }

    //Method to update settings of the game/controls
    public void updateSettings(SettingsManager newSettings) {
        this.settings = newSettings;
        arrDelay = settings.getArr();
        dasDelay = settings.getDas();
        softdropFactor = settings.getSdf();
        audioEnabled = settings.isAudioEnabled();
        gridVisibility = settings.getGridVisibility();
        ghostVisibility = settings.getGhostVisibility();
        actionTextOn = settings.isActionTextOn();
    }

    //Paint method
    public void paint(Graphics g) {
        int gridDrawOffset;

        super.paint(g);
        if (currentState == GameState.MENU) {
            menu.setVisible(true);
            pauseScreen.setVisible(false);
            loseScreen.setVisible(false);
            scoreScreen.setVisible(false);
        } 
        else if (currentState == GameState.PAUSE) {
            pauseScreen.setVisible(true);
            menu.setVisible(false);
            loseScreen.setVisible(false);
            scoreScreen.setVisible(false);        
        }
        else if (currentState == GameState.LOSE_SCREEN){
            loseScreen.setVisible(true);
            menu.setVisible(false);
            pauseScreen.setVisible(false);
            scoreScreen.setVisible(false);        
        }
        else if (currentState == GameState.SCORE_SCREEN){
            scoreScreen.setVisible(true);
            menu.setVisible(false);
            pauseScreen.setVisible(false);
            loseScreen.setVisible(false);
        }
        else { 
            menu.setVisible(false);
            pauseScreen.setVisible(false);
            loseScreen.setVisible(false);
            scoreScreen.setVisible(false);     

            // Draw top panel
            g.setColor(Color.BLACK);
            g.fillRect(GRID_OFFSET_X, 0, GAME_WIDTH, TOP_PANEL_HEIGHT);
            
            // Draw bottom panel
            g.setColor(Color.BLACK);
            g.fillRect(GRID_OFFSET_X, GAME_HEIGHT - BOTTOM_PANEL_HEIGHT, GAME_WIDTH, BOTTOM_PANEL_HEIGHT);
            
            // Adjust grid drawing offset
            gridDrawOffset = TOP_PANEL_HEIGHT;
            
            // Draw game grid with less visible lines
            g.setColor(new Color(211, 211, 211, (int) (255 * (gridVisibility/100.0)))); // White with opactiy determined by user
            for (int x = GRID_OFFSET_X; x <= GRID_OFFSET_X + GAME_WIDTH; x += BLOCK_SIZE) {
                g.drawLine(x, gridDrawOffset, x, GAME_HEIGHT - BOTTOM_PANEL_HEIGHT);
            }
            for (int y = gridDrawOffset; y <= GAME_HEIGHT - BOTTOM_PANEL_HEIGHT; y += BLOCK_SIZE) {
                g.drawLine(GRID_OFFSET_X, y, GRID_OFFSET_X + GAME_WIDTH, y);
            }
    
            // Adjust grid and piece drawing to account for top panel
            grid.draw(g, GRID_OFFSET_X, gridDrawOffset);
            currentPiece.draw(g, 
                GRID_OFFSET_X + pieceX * BLOCK_SIZE, 
                gridDrawOffset + pieceY * BLOCK_SIZE
            );
            
            // Game Interface
            gameInterface.updateState(pieceQueue, heldPiece, currentPiece, pieceX, pieceY, 
            currentState.toString(), isCountingdown);
        
            gameInterface.drawQueue(g);
            gameInterface.drawHeldPiece(g);
            gameInterface.drawGhostPiece(g, grid, ghostVisibility);
            gameInterface.drawStats(g, gameTimer.getFormattedTime(), gameTimer.getTimeRemaining(), 
            gameTimer.getElapsedTime(), grid.getPiecesPlaced(), grid.getLinesCleared(), 
            score, level, powerUpAvailable, powerUpUsed);
            
            // Draw action text
            if(actionTextOn){
                gameInterface.drawActionText(g);
            }

            // Draw countdown
            if (isCountingdown) {
                drawCountdown(g);
            }
        }
    }

    //Method to take current piece user has and holds it, giving user flexibility in gameplay
    private void holdPiece() {
        Tetromino temp; // temp Tetromino object to swap piece

        if (!canHold) return; // Can only hold once per piece
    
        if (heldPiece == null) {
            // First time holding a piece
            currentPiece.resetRotation(); // Reset rotation before holding
            heldPiece = currentPiece;
            spawnNewPiece();
        } else {
            // Swap current piece with held piece
            temp = currentPiece;
            currentPiece = heldPiece;
            currentPiece.resetRotation(); // Reset rotation for the swapped-in piece
            heldPiece = temp;
            heldPiece.resetRotation(); // Reset rotation for the piece being held
            
            SoundManager.playSound("sfx/hold.wav");
            
            // Reset piece position
            pieceX = GRID_COLS / 2 - 2;
            pieceY = 0;
    
            // Check if the swapped piece causes a collision
            if (checkCollision(pieceX, pieceY)) {
                // If collision occurs, revert the swap and spawn a new piece
                currentPiece = heldPiece;
                heldPiece = null;
                spawnNewPiece();
                return;
            }
        }
    
        // Prevent multiple holds
        canHold = false;
        repaint();
    }
    
    //Method to handle DAS setting
    private void handleDAS() {
        if (lastKeyPressed == -1 && leftKeyPressed) { // Move left if last key was left
            if (dasCharge >= dasDelay) {
                if (dasCharge % arrDelay == 0) {
                    movePieceHorizontally(-1);
                }
            }
            dasCharge++;
        } else if (lastKeyPressed == 1 && rightKeyPressed) { // Move right if last key was right
            if (dasCharge >= dasDelay) {
                if (dasCharge % arrDelay == 0) {
                    movePieceHorizontally(1);
                }
            }
            dasCharge++;
        } else {
            dasCharge = 0; // Reset DAS charge if no keys are pressed
        }
    }

    //Run method
    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long now;
        long elapsedTime;
        double adjustedDropInterval;

        long lastDropTime = System.nanoTime();
        double dropInterval = 1000000000; // gravity interval, can be sped up in different modes
        
        while (true) {
            now = System.nanoTime();
            elapsedTime = now - lastDropTime; // Calculate time since last drop
            delta += (now - lastTime) / ns;
            lastTime = now;
            
            if (delta >= 1) {
                handleDAS();
                gameCondition();
                repaint();
                delta--;
            }        

            if (softDropActive) {
                adjustedDropInterval = 500000000 / softdropFactor; // If soft drop is active, drop faster
                lockDelayTime = softdropFactor; // Increase lock delay for soft drop to be equal to without soft drop
            } else {
                adjustedDropInterval = dropInterval / gravityFactor; // If soft drop is not active, use normal interval
                lockDelayTime = (int)gravityFactor;
            }
            
            if (!isCountingdown && elapsedTime > adjustedDropInterval) {
                updateGame(); // Call the game update function
                lastDropTime = now; // Update the last drop time to the current time
            }

            updateAudioSettings();
            
        }

    }

    //Method to handle and update all audio and sound settings
    private void updateAudioSettings() {
        if (!audioEnabled) {
            SoundManager.setMusicVolume(0);
            SoundManager.setSfxVolume(0);
        } else {
            SoundManager.setMusicVolume(settings.getMusicVolume() / 100.0f);
            SoundManager.setSfxVolume(settings.getSfxVolume() / 100.0f);
        }
    }

    //Method to update the game
    private void updateGame() {
        updateSettings(settings);

        if(isInGame()){
            // Drop tetrominoes
            if (!checkCollision(pieceX, pieceY + 1) && isInGame()) {
                pieceY++;
                // Add 1 point per cell soft dropped
                if(softDropActive){
                    score++; 
                }
                currentLockDelayTime = 0;
                isLockDelayActive = false;
                movementCounter = 0;
            } else {
                // Piece has landed
                if (!isLockDelayActive) {
                    isLockDelayActive = true;
                    currentLockDelayTime = 0;
                }
        
                currentLockDelayTime++;
        
                // Lock the piece after lock delay or max movements
                if (currentLockDelayTime >= lockDelayTime || movementCounter >= MAX_MOVEMENTS) {
    
                    grid.addPiece(currentPiece, pieceX, pieceY);

                    if (lineDestroyerActive) {
                        grid.clearSurroundingBlocks(currentPiece, pieceX, pieceY);
                        lineDestroyerActive = false;  // Deactivate the power-up after use
                        
                        // Check for any lines that might have been cleared
                        grid.clearFullLines();
                    }

                    updateLinesCleared(isTSpin()); 
                    spawnNewPiece();
                }
            }
        }    

    }

    //Method to handle various conditions the game may encounter
    private void gameCondition() {
        // Check if user has cleared 40 lines
        if (grid.getLinesCleared() >= 40 && currentState.equals(GameState.GAME_SPRINT)) {
            currentState = GameState.SCORE_SCREEN;
            result = gameTimer.getFormattedTime(); // Get time user took to clear 40 lines
            gameTimer.stop(); // Stop timer
            // Draw score screen
            remove(scoreScreen);
            scoreScreen = new ScoreScreen(this, result, previousState.toString(), scoreManager.isHighScore(result, previousState.toString()));
            add(scoreScreen, BorderLayout.CENTER);
            scoreScreen.setVisible(true);
            revalidate();
        }

        // Check if 2 minutes timer is up
        if (gameTimer.getElapsedTime() >= 120000 && currentState.equals(GameState.GAME_TIMETRIAL)) {
            currentState = GameState.SCORE_SCREEN;
            result = String.format("%,d", score); // Get score as a string
            gameTimer.stop(); // Stop timer
            // Draw score screen
            remove(scoreScreen);
            scoreScreen = new ScoreScreen(this, result, previousState.toString(), scoreManager.isHighScore(result, previousState.toString()));
            add(scoreScreen, BorderLayout.CENTER);
            scoreScreen.setVisible(true);
            revalidate();
        }

        // Check for power up availability
        if (currentState.equals(GameState.GAME_CHALLENGE)) {
            long elapsedSeconds;
            System.out.println(powerUpAvailable);
            int powerUp;
            long currentTime;

            // Calculate difficulty based on time elapsed
            elapsedSeconds = gameTimer.getElapsedTime() / 1000;
            gravityFactor = Math.min(120.919, 1 + (elapsedSeconds / 10.0));
        
            // Check for power-up availability based on time interval
            currentTime = System.currentTimeMillis();
            if (currentTime - lastPowerUpTime >= POWER_UP_INTERVAL) {
                // Reset power up stats
                powerUpUsed = false;
                powerUpStored = false; 

                // Give user a random powerup
                powerUp = (int)(Math.random() * 3) + 1; // Get random number between 1 and 3

                if(powerUp == 1 || powerUp == 2){ // Store line destroyer/slow time
                    storePowerUps(powerUp); // Store powerup
                    gameInterface.powerUpAvailable(powerUp); // Draw powerup
                    lastPowerUpTime = currentTime;
                    if(powerUp == 1){
                        lineDestroyerAvailable = false; // Override previous powerup
                    }
                    if(powerUp == 2){
                        slowTimeAvailable = false; // Override previous powerup
                    }
                }
                else if(powerUp == 3){ // Trigger cheese attack
                    grid.addCheese(); // Add cheese
                    lastPowerUpTime = currentTime; // Set power up variables
                    powerUpAvailable = false;
                    slowTimeAvailable = false;
                    lineDestroyerAvailable = false;
                    powerUpUsed = true;
                    gameInterface.triggerPowerUpText(powerUp); // Trigger powerup text
                }
            }
        
            // Handle slow time power-up
            if (slowTimeActive) {
                if (System.currentTimeMillis() - slowTimeStarted >= SLOW_TIME_DURATION) {
                    slowTimeActive = false;
                } else {
                    gravityFactor = 1;
                }
            }
        }
        
    }

    //Method to store powerups (Slow down time, Line Destroyer)
    public void storePowerUps(int powerup) {
        if (!powerUpStored) {
            if (powerup == 1) {
                slowTimeAvailable = true;
            } else if (powerup == 2) {
                lineDestroyerAvailable = true;
            }
    
            if (slowTimeAvailable || lineDestroyerAvailable) {
                powerUpAvailable = true;
            }
    
            powerUpStored = true; // Set flag to indicate the power-up has been stored
        }
    }

    //Method to activate powerups
    public void activatePowerUp(int powerup) {
        if (powerUpAvailable && !powerUpUsed && currentState == GameState.GAME_CHALLENGE) {
            switch (powerup) {
                case 1:
                    slowTimeActive = true;
                    slowTimeStarted = System.currentTimeMillis();
                    gameInterface.triggerPowerUpText(1);
                    break;
                case 2:
                    lineDestroyerActive = true;
                    gameInterface.triggerPowerUpText(2);
                    break;
            }
            // Set power up variables
            powerUpAvailable = false;
            lineDestroyerAvailable = false;
            slowTimeAvailable = false;
            powerUpUsed = true;
        }
    }

    //Method to save high scores
    public void saveHighScore(String previousState, String result, String username){
        scoreManager.saveScore(previousState, result, username);
    }

    //Method to check for any collisions
    private boolean checkCollision(int x, int y) {
        return grid.checkCollision(currentPiece, x, y);
    }

    //Method to spawn new piece into the game
    private void spawnNewPiece() {
        currentPiece = pieceQueue.poll();
        addPieceToQueue();
        
        // Reset piece position to spawn position
        pieceX = (GRID_COLS - currentPiece.getShapeWidth()) / 2;
        pieceY = -2;
        canHold = true;

        // Reset movement counter, harddrop distance, and softdrop distance
        movementCounter = 0;

        // Check collision and game state
        if (checkCollision(pieceX, pieceY) && isInGame()) {
            // Check if the spawn buffer is occupied
            if (grid.isRowOccupied(-1)) {
                // Ensure the main grid rows are checked before clearing
                if (!grid.isRowOccupied(1)) {
                    // Clear spawn buffer rows to avoid stuck pieces
                    for (int i = 0; i < 10; i++) {
                        grid.clearCell(-1, i);
                        grid.clearCell(-2, i);
                        grid.clearCell(-3, i);
                    }
                }
            }

            // If clearing fails or conditions persist, handle top-out
            if (grid.isRowOccupied(1)) {
                currentState = GameState.LOSE_SCREEN;
                SoundManager.playSound("sfx/topout.wav");
                add(loseScreen, BorderLayout.CENTER);
                revalidate();
                repaint();
            }
        }

    }

    //Method to immediately drop the piece to the bottom of the screen when the user presses space
    private void hardDrop() {
        // Move piece until it collides with something
        while (!checkCollision(pieceX, pieceY + 1)) {
            pieceY++;
            score++;
        }
        SoundManager.playSound("sfx/harddrop.wav");
        grid.addPiece(currentPiece, pieceX, pieceY); // Add dropped piece to grid

        if (lineDestroyerActive) {
            grid.clearSurroundingBlocks(currentPiece, pieceX, pieceY);
            lineDestroyerActive = false;  // Deactivate the power-up after use
            
            // Check for any lines that might have been cleared
            grid.clearFullLines();
        }

        updateLinesCleared(isTSpin()); // Centralized update for lines cleared
        spawnNewPiece();
    }

    //Method to update the current count of lines cleared
    private void updateLinesCleared(boolean isTspin) {
        int previousLinesCleared = grid.getLinesCleared();
        
        if(!isInGame()){
            return;
        }

        grid.clearFullLines(); // Clear lines in the grid
        controlGravity(); // Track levels
        currentLinesCleared = grid.getLinesCleared() - previousLinesCleared;

        //Track back to backs
        if(currentLinesCleared == 4){
            backToBackCounter++;
            if(backToBackCounter > 1){
                SoundManager.playSound("sfx/clearbtb.wav");
            }
            else{
                SoundManager.playSound("sfx/clearquad.wav");
            }
        }
        else if(isTspin){
            if(currentLinesCleared > 0){
                backToBackCounter++;
                if(backToBackCounter > 1){
                    SoundManager.playSound("sfx/clearquad.wav");
                }
                else{
                    SoundManager.playSound("sfx/clearquad.wav");
                }
            }

        }
        else if (currentLinesCleared > 0){
            backToBackCounter = 0;
            SoundManager.playSound("sfx/clearline.wav");
        }

        // Track combos
        if(currentLinesCleared > 0){
            comboCounter++;
        }
        else{
            comboCounter = 0;
        }

        // Calculate Score
        calculateScore(isTspin);

        // Trigger action text
        if(currentLinesCleared > 0){ // Only trigger if lines were cleared
            gameInterface.triggerActionText(currentLinesCleared, isTspin, isGridEmpty(), backToBackCounter, comboCounter);
        }
    }

    //Method to control gravity (how fast the block is falling)
    private void controlGravity(){
        int linesCleared = grid.getLinesCleared();
        int previousLevel = level; // Store the previous level

        // Arrays of levels and corresponding gravity factors
        int[] levelThresholds = {3, 8, 15, 24, 35, 48, 63, 80, 99};
        double[] gravityFactors = {1, 1.555, 2.475, 4.0161, 6.667, 11.367, 19.802, 35.336, 64.516, 120.919};
    
        if (currentState != GameState.GAME_TIMETRIAL) {
            gravityFactor = 1;
            return;
        }
    
        // Find the appropriate level and gravity factor
        for (int i = 0; i < levelThresholds.length; i++) {
            if (linesCleared < levelThresholds[i]) {
                level = i + 1;
                gravityFactor = gravityFactors[i];
                
                // Check for level up here
                if (level > previousLevel) {
                    SoundManager.playSound("sfx/levelUp.wav");
                }
                return;
            }
        }
    
        // If linesCleared is 99 or more, set the highest level and gravity factor
        if (linesCleared >= 99) {
            level = 10;
            gravityFactor = gravityFactors[gravityFactors.length - 1];
            
            // Check for level up here too
            if (level > previousLevel) {
                SoundManager.playSound("sfx/levelUp.wav");
            }
        }
    }

    //Method to calculate current score of the round the user is playing
    private void calculateScore(boolean isTspin){
        double backToBackMultiplier;

        // Add score based on b2b
        if(backToBackCounter > 1){
            backToBackMultiplier = 1.5;
        }
        else{
            backToBackMultiplier = 1;
        }

        // Calculate score
        if(currentLinesCleared == 0){
            if(isTspin){
                score += 400 * level;
            }
            else{
                score += 0;
            }
        }
        else if(currentLinesCleared == 1){
            if(isTspin){
                score+= 800 * level * backToBackMultiplier;
            }
            else{
                score += 100 * level;
            }
        }
        else if(currentLinesCleared == 2){
            if(isTspin){
                score += 1200 * level * backToBackMultiplier;
            }
            else{
                score += 300 * level;
            }
        }
        else if(currentLinesCleared == 3){
            if(isTspin){
                score += 1600 * level * backToBackMultiplier;
            }
            else{
                score += 500 * level;
            }
        }
        else if(currentLinesCleared == 4){
            score += 800 * level * backToBackMultiplier;
        }

        // All clear
        if(isGridEmpty()){
            score += 3500 * level;
            SoundManager.playSound("sfx/clearspin.wav");
        }

        if(comboCounter > 1){
            score += level * (50 * (comboCounter-1));
        }

        score += 2 * harddropDistance; //2 points per cell dropped
    }

    //Method to check if the Grid is empty or not (for all clears)
    private boolean isGridEmpty(){
        for(int row = 0; row < Grid.ROWS; row++){
            for(int col = 0; col < Grid.COLS; col++){
                if(grid.isOccupied(row, col)){
                    return false;
                }
            }
        }
        return true;
    }

    //Method to allow the user to move piece left and right
    private void movePieceHorizontally(int direction) {
        if (!checkCollision(pieceX + direction, pieceY)) {
            pieceX += direction;

            // Reset lock delay time when moving piece
            if (isLockDelayActive) {
                currentLockDelayTime = 0;
                movementCounter++;
            }
        }
    }

    //Method to allow users to spin a piece even when it is touching the left/right wall
    public boolean tryWallKick(int deltaX, int deltaY) {
        // Store original position
        int originalX = pieceX;
        int originalY = pieceY;
        
        // Try the kick
        pieceX += deltaX;
        pieceY -= deltaY;  // Invert deltaY because Tetris grid Y increases downward
        
        // Check if the new position is valid using collision detection
        if (checkCollision(pieceX, pieceY)) {
            // Revert to original position if invalid
            pieceX = originalX;
            pieceY = originalY;
            return false;
        }
        
        // Reset lock delay and movement counter on successful wall kick
        if (isLockDelayActive) {
            currentLockDelayTime = 0;
            movementCounter = 0;
        }
        
        return true;
    }

    //Method to perform a wall kick
    private boolean performWallKick() {
    
        int currentState = currentPiece.getRotationState();
        int previousState = currentPiece.getPreviousRotation();
        
        // Determine which wall kick data to use for both CW and CCW rotations
        int kickIndex;
        int deltaX, deltaY;
        
        // For clockwise rotations
        if (currentState == 1 && previousState == 0) kickIndex = 0;        // 0 -> R
        else if (currentState == 2 && previousState == 1) kickIndex = 2;   // R -> 2
        else if (currentState == 3 && previousState == 2) kickIndex = 4;   // 2 -> L
        else if (currentState == 0 && previousState == 3) kickIndex = 6;   // L -> 0
        // For counter-clockwise rotations
        else if (currentState == 3 && previousState == 0) kickIndex = 7;   // 0 -> L
        else if (currentState == 2 && previousState == 3) kickIndex = 5;   // L -> 2
        else if (currentState == 1 && previousState == 2) kickIndex = 3;   // 2 -> R
        else if (currentState == 0 && previousState == 1) kickIndex = 1;   // R -> 0
        else return false;
    
        // Try each wall kick test
        for (int i = 0; i < Tetromino.JLSTZ_WALLKICKS[kickIndex].length; i++) {
            deltaX = Tetromino.JLSTZ_WALLKICKS[kickIndex][i][0];
            deltaY = Tetromino.JLSTZ_WALLKICKS[kickIndex][i][1];
            if (tryWallKick(deltaX, deltaY)) {
                return true;
            }
        }

        //I piece has different kick tests
        if (currentPiece.getShapeWidth() == 4) {
            for (int i = 0; i < Tetromino.I_WALLKICKS[kickIndex].length; i++) {
                deltaX = Tetromino.I_WALLKICKS[kickIndex][i][0];
                deltaY = Tetromino.I_WALLKICKS[kickIndex][i][1];
                if (tryWallKick(deltaX, deltaY)) {
                    return true;
                }
            }
        }
        
        return false;
    }    
    
    //Method to check if a line clear is a T-Spin
    public boolean isTSpin(){
        // Corner check variables
        int row, col;
        boolean isOutOfBounds, isOccupied;
        int occupiedCorners = 0;

        // Array with the coordinates of the corners of a T-piece
        int[][] corners = {
            {pieceY, pieceX},           // Top-left
            {pieceY, pieceX + 2},       // Top-right
            {pieceY + 2, pieceX},       // Bottom-left
            {pieceY + 2, pieceX + 2}    // Bottom-right
        };

        if(currentPiece.getIndex() != 1 || !lastKeyValidRotation){
            return false;
        }

        if(!isLockDelayActive){
            return false;
        }

        // Count occupied corners
        for (int i = 0; i < corners.length; i++) {
            row = corners[i][0];
            col = corners[i][1];

            // Check if corner is outside grid bounds
            isOutOfBounds = row >= Grid.ROWS || col >= Grid.COLS || col < 0;

            // Check if corner is occupied by a block
            isOccupied = false;
            if (row >= 0) {  // Only check grid occupation if row is valid
                isOccupied = grid.isOccupied(row, col);
            }

            // If either condition is true, count it as an occupied corner
            if (isOutOfBounds || isOccupied) {
                occupiedCorners++;
            }
        }

        return occupiedCorners >= 3;
    }

    //KeyPressed method to check for keyboard pressing (input)
    public void keyPressed(KeyEvent e) {
        boolean pieceWasMoved = false;

        if(currentState != GameState.MENU && currentState != GameState.LOSE_SCREEN && currentState != GameState.SCORE_SCREEN){
            switch(e.getKeyCode()) {
                case KeyEvent.VK_R:
                    restartGame();
                    SoundManager.playSound("sfx/redo.wav");
                    break;
                
                case KeyEvent.VK_ESCAPE:
                    if (currentState != GameState.MENU) {
                        if (currentState == GameState.PAUSE) {
                            // Unpause
                            remove(pauseScreen);
                            currentState = previousState;
                        } else {
                            // Pause
                            currentState = GameState.PAUSE;
                            add(pauseScreen, BorderLayout.CENTER);
                            pauseScreen.resetButtonAppearance(pauseScreen.getMainMenuButton());
                            pauseScreen.resetButtonAppearance(pauseScreen.getResumeButton());
                        }
                        revalidate();
                        repaint();
                    }
                    break;
            }
        }

        if (!isCountingdown && isInGame()) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT: // Move piece left
                    leftKeyPressed = true;
                    lastKeyPressed = -1; // Left arrow is now the last key pressed
                    dasCharge = 0; // Reset das charge when moving
                    movePieceHorizontally(-1); // Move piece left
                    pieceWasMoved = true;
                    lastKeyValidRotation = false; // Last move was not a rotation (for Tspin checks)
                    break;
        
                case KeyEvent.VK_RIGHT: // Move piece right
                    rightKeyPressed = true;
                    lastKeyPressed = 1; // Right arrow is now the last key pressed
                    dasCharge = 0; // Reset das charge when moving
                    movePieceHorizontally(1);
                    pieceWasMoved = true; 
                    lastKeyValidRotation = false; // Last move was not a rotation (for Tspin checks)
                    break;
        
                case KeyEvent.VK_SHIFT: // Soft drop peice
                    softDropActive = true;
                    break;
        
                case KeyEvent.VK_UP: // Rotate piece clockwise
                    currentPiece.rotateCW();
                    pieceWasMoved = true;
                    movementCounter++;
                    lastKeyValidRotation = true; // Last move was a rotation

                    // Detect if user is attempting Tspin
                    if(isTSpin()){
                        SoundManager.playSound("sfx/spin.wav");
                    }

                    // Check if can rotate (reverse if can't)
                    if (!performWallKick()) {
                        currentPiece.rotateCCW();
                        lastKeyValidRotation = false;
                    }
                    
                    break;
        
                case KeyEvent.VK_DOWN: // Rotate piece counterclockwise
                    currentPiece.rotateCCW();
                    pieceWasMoved = true;
                    movementCounter++;
                    lastKeyValidRotation = true; // Last move was a rotation

                    // Detect if user is attempting Tspin
                    if(isTSpin()){
                        SoundManager.playSound("sfx/spin.wav");
                    }

                    // Check if can rotate (reverse if can't)
                    if (!performWallKick()) {
                        currentPiece.rotateCW();
                        lastKeyValidRotation = false;
                    }
                    break;
        
                case KeyEvent.VK_X: // Rotate piece 180°
                    currentPiece.rotateFlip();
                    lastKeyValidRotation = false; // T-spins can't be from 180° spins
                    pieceWasMoved = true;

                    // Specific wall kick logic for 180° rotation
                    if (checkCollision(pieceX, pieceY)) {
                        if(tryWallKick(-1, 0)) return;
                        if(tryWallKick(1, 0)) return;
                        currentPiece.rotateFlip(); // Undo if invalid
                    }
                    break;
        
                case KeyEvent.VK_SPACE: // Harddrop piece
                    hardDrop();
                    break;

                case KeyEvent.VK_V: // Use powerup
                    if(powerUpAvailable){ // Check if there is powerup available
                        if(slowTimeAvailable){
                            activatePowerUp(1); // Activate slow time
                        }
                        else if(lineDestroyerAvailable){
                            activatePowerUp(2); // Activate line destroyer

                        }

                    }
                    break;

                case KeyEvent.VK_C: // Hold piece
                    holdPiece();
                    lastKeyValidRotation = false;
                    break;
            
                }
        
            // Reset lock delay if piece was moved or rotated until max movement reached
            if (pieceWasMoved && isLockDelayActive && movementCounter < MAX_MOVEMENTS) {
                currentLockDelayTime = 0;
                movementCounter++;
            }
        
            repaint();
        }
    }

    //Key Released method to check for when a key is released
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT: // Movement logic
                leftKeyPressed = false;
                if (lastKeyPressed == -1) {
                    if (rightKeyPressed) {
                        lastKeyPressed = 1;
                    } else {
                        lastKeyPressed = 0;
                    }
                }
                dasCharge = 0;
                break;
    
            case KeyEvent.VK_RIGHT: // Movement logic
                rightKeyPressed = false;
                if (lastKeyPressed == 1) {
                    if (leftKeyPressed) {
                        lastKeyPressed = -1;
                    } else {
                        lastKeyPressed = 0;
                    }                
                }
                dasCharge = 0;
                break;
    
            case KeyEvent.VK_SHIFT:
                softDropActive = false; // Stop soft drop if not holding
                break;
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {}

    // Methods for Score Screen Buttons
    public void returntoGame() {
        currentState = previousState;
        remove(scoreScreen);
        restartGame();
        revalidate();
        repaint();
        scoreScreen.resetButtonAppearance(scoreScreen.getAgainButton());
    }

    //Method to return to main menu
    public void returntoMenu(){
        currentState = GameState.MENU;
        menu.resetToMainMenu(null);
        remove(scoreScreen);
        revalidate();
        repaint();
        scoreScreen.resetButtonAppearance(scoreScreen.getMenuButton());
    }

    //Method to check if user is currently in game
    public boolean isInGame(){
        if (currentState == GameState.GAME_CHALLENGE || currentState == GameState.GAME_SPRINT || currentState == GameState.GAME_TIMETRIAL || currentState == GameState.GAME_PRACTICE){
            return true;
        }
        else return false;
    }
}
