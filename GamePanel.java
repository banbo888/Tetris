import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Queue;
import javax.swing.*;

public class GamePanel extends JPanel implements Runnable, KeyListener {
    public static final int GAME_WIDTH = 300;  // Width of main grid
    private static final int TOP_PANEL_HEIGHT = 50; // Adjust as needed
    private static final int BOTTOM_PANEL_HEIGHT = 50; // Adjust as needed 
    public static final int GAME_HEIGHT = 600 + TOP_PANEL_HEIGHT + BOTTOM_PANEL_HEIGHT; // Original height plus new panels
    private static final int BLOCK_SIZE = Tetromino.BLOCK_SIZE;
    private static final int GRID_ROWS = GAME_HEIGHT / BLOCK_SIZE;
    private static final int GRID_COLS = GAME_WIDTH / BLOCK_SIZE;
    private static final int SIDE_PANEL_WIDTH = 300; // Width of side panels
    private static final int GRID_OFFSET_X = SIDE_PANEL_WIDTH; // Offset for grid drawing and calculations
    
    //Softdrop 
    private int lockDelayTime;
    private int currentLockDelayTime = 0;
    private int movementCounter = 0;
    private static final int MAX_MOVEMENTS = 20;
    private boolean isLockDelayActive = false;

    //Handling settings (Delayed Auto Shift, Auto Repeat Rate, Soft Drop Factor)
    private int dasDelay; // Delay before auto-shift starts (in frames, 1-20)
    private int arrDelay; // Auto-repeat rate (in frames, 1-5)
    private int softdropFactor; // Soft drop speed factor (1-100)
    private int dasCharge = 0;
    private boolean leftKeyPressed = false;
    private boolean rightKeyPressed = false;
    private int lastKeyPressed = 0; // 0: None, -1: Left, 1: Right

    //Video and Audio settings
    private boolean audioEnabled;
    private int musicVolume;
    private int sfxVolume;
    private int gridVisibility;
    private int ghostVisibility;
    private boolean actionTextOn;

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

    private Queue<Tetromino> pieceQueue;
    private int pieceX = GRID_COLS / 2 - 2;
    private int pieceY = 0;
    private boolean softDropActive = false;
    private boolean canHold = true;

    //Countdown timer
    private boolean isCountingdown = false;
    private long countdownStartTime;
    private static final String[] COUNTDOWN_TEXT = {"GO!", "SET", "READY"};

    // Line clear logic (stores line clears, back to back, combo, and Tspin stats)
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
    
    public GamePanel() {
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
        spawnNewPiece();

        this.setFocusable(true);
        this.addKeyListener(this);
        this.setPreferredSize(new Dimension(
            GAME_WIDTH + (2 * SIDE_PANEL_WIDTH), 
            GAME_HEIGHT
        ));        
        this.setBackground(Color.BLACK); 
        this.setLayout(new BorderLayout());
        add(menu, BorderLayout.CENTER);


        gameThread = new Thread(this);
        gameThread.start();

        // Action Listeners for Pause Screen
        pauseScreen.getResumeButton().addActionListener(e -> {
            currentState = previousState; // resume the game
            remove(pauseScreen);
            revalidate();
            repaint();
        });

        pauseScreen.getMainMenuButton().addActionListener(e -> {
            currentState = GameState.MENU;
            menu.resetToMainMenu(null);
            remove(pauseScreen);
            revalidate();
            repaint();
        });

        // Action Listseners for Lose Screen
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
            menu.resetToMainMenu(null);
            remove(loseScreen);
            revalidate();
            repaint();
            loseScreen.resetButtonAppearance(loseScreen.getMainMenuButton());
        });
    }
    
    public void startGame(int mode) {
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

    private void startCountdown() {
        isCountingdown = true;
        countdownStartTime = System.currentTimeMillis() - 1000; // Set to current time directly
        repaint();
    }

    private void drawCountdown(Graphics g) {
        Font originalFont = g.getFont();
        Font countdownFont = new Font("Arial", Font.BOLD, 72);
        g.setFont(countdownFont);
        
        // Calculate time elapsed and current display
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - countdownStartTime;
        int displayIndex = (int) Math.max(0, Math.min(3, 3 - (elapsedTime / 1000)));
        
        // Only draw if within valid range (including GO!)
        if (displayIndex >= 0 && displayIndex < COUNTDOWN_TEXT.length) {
            // Draw semi-transparent background
            g.setColor(new Color(0, 0, 0, 128));
            g.fillRect(0, 0, getWidth(), getHeight());
            
            // Draw countdown text
            String text = COUNTDOWN_TEXT[displayIndex];
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getHeight();
            
            g.setColor(Color.WHITE);
            g.drawString(text, 
                (getWidth() - textWidth) / 2,
                (getHeight() - textHeight) / 2 + fm.getAscent()
            );
        }
        
        g.setFont(originalFont);
        
        // Check if countdown is complete
        gameTimer.stop();
        if (elapsedTime >= 4000) { // 4 seconds total duration
            isCountingdown = false;
            gameTimer.start(); // Start the game timer
        }
    }


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
        powerUpAvailable = false;
        powerUpUsed = false;
        slowTimeActive = false;

        // Repaint to update the display
        repaint();
    }

    private void initializePieceQueue() {

        //Cycle through first bag
        for(int i = 0; i < 6; i++){
            bagGenerator.getNextPiece();
        }

        for (int i = 0; i < 6; i++) { // Add all 7 pieces to the queue initially
            pieceQueue.add(bagGenerator.getNextPiece());
        }
    }
    
    private void addPieceToQueue() {
        pieceQueue.add(bagGenerator.getNextPiece());
    }

    public void updateSettings(SettingsManager newSettings) {
        this.settings = newSettings;
        arrDelay = settings.getArr();
        dasDelay = settings.getDas();
        softdropFactor = settings.getSdf();
        audioEnabled = settings.isAudioEnabled();
        musicVolume = settings.getMusicVolume();
        sfxVolume = settings.getSfxVolume();
        gridVisibility = settings.getGridVisibility();
        ghostVisibility = settings.getGhostVisibility();
        actionTextOn = settings.isActionTextOn();
    }

    public void paint(Graphics g) {
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
            int gridDrawOffset = TOP_PANEL_HEIGHT;
            
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
            currentState.toString(), isCountingdown, powerUpAvailable);
        
            gameInterface.drawQueue(g);
            gameInterface.drawHeldPiece(g);
            gameInterface.drawGhostPiece(g, grid, ghostVisibility);
            gameInterface.drawStats(g, gameTimer.getFormattedTime(), gameTimer.getTimeRemaining(), 
            gameTimer.getElapsedTime(), grid.getPiecesPlaced(), grid.getLinesCleared(), 
            score, level);
            
            if(actionTextOn){
                gameInterface.drawActionText(g);
            }
            if (isCountingdown) {
                drawCountdown(g);
            }
        }
    }


    private void holdPiece() {
        if (!canHold) return; // Can only hold once per piece
    
        if (heldPiece == null) {
            // First time holding a piece
            currentPiece.resetRotation(); // Reset rotation before holding
            heldPiece = currentPiece;
            spawnNewPiece();
        } else {
            // Swap current piece with held piece
            Tetromino temp = currentPiece;
            currentPiece = heldPiece;
            currentPiece.resetRotation(); // Reset rotation for the swapped-in piece
            heldPiece = temp;
            heldPiece.resetRotation(); // Reset rotation for the piece being held
            
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
        }

    }

    
    private void updateGame() {
        updateSettings(settings);
    
        if (!checkCollision(pieceX, pieceY + 1) && currentState != GameState.MENU && currentState != GameState.SCORE_SCREEN && currentState != GameState.LOSE_SCREEN) {
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
                updateLinesCleared(isTSpin()); 
                spawnNewPiece();
            }
        }
    }

    private boolean powerUpUsed = false;
    private boolean powerUpAvailable = false;
    private boolean slowTimeActive = false;
    private long slowTimeStarted = 0;
    private static final long SLOW_TIME_DURATION = 3000; // 3 seconds in milliseconds

    private void gameCondition() {
        if (grid.getLinesCleared() >= 2 && currentState == GameState.GAME_SPRINT) {
            currentState = GameState.SCORE_SCREEN;
            result = gameTimer.getFormattedTime();
            gameTimer.stop();
            remove(scoreScreen);
            scoreScreen = new ScoreScreen(this, result, previousState.toString(), scoreManager.isHighScore(result, previousState.toString()));
            add(scoreScreen, BorderLayout.CENTER);
            scoreScreen.setVisible(true);
            revalidate();
        }

        if (gameTimer.getElapsedTime() >= 1000 && currentState == GameState.GAME_TIMETRIAL) {
            currentState = GameState.SCORE_SCREEN;
            result = String.format("%,d", score);
            gameTimer.stop();
            remove(scoreScreen);
            scoreScreen = new ScoreScreen(this, result, previousState.toString(), scoreManager.isHighScore(result, previousState.toString()));
            add(scoreScreen, BorderLayout.CENTER);
            scoreScreen.setVisible(true);
            revalidate();
        }

        if (currentState == GameState.GAME_CHALLENGE) {
            // Calculate difficulty based on time elapsed
            long elapsedSeconds = gameTimer.getElapsedTime() / 1000;
            gravityFactor = Math.min(120.919, 1 + (elapsedSeconds / 10.0)); // Increases by 0.1 every 1 second, caps at max speed

            // Check for power-up availability
            if (grid.getLinesCleared() > 0 && grid.getLinesCleared() % 10 == 0) {
                powerUpAvailable = true;
            }

            // Handle slow time power-up
            if (slowTimeActive) {
                if (System.currentTimeMillis() - slowTimeStarted >= SLOW_TIME_DURATION) {
                    slowTimeActive = false;
                } else {
                    gravityFactor *= 0.5; // Slow down pieces to half speed
                }
            }
        }
    }

    public void saveHighScore(String previousState, String result){
        scoreManager.saveScore(previousState, result);
    }

    // Add this method to handle power-up activation
    public void activateSlowTime() {
        if (powerUpAvailable && !slowTimeActive && !powerUpUsed && currentState == GameState.GAME_CHALLENGE) {
            gameInterface.triggerPowerUpText();
            slowTimeActive = true;
            powerUpAvailable = false;
            powerUpUsed = true; // Mark the power-up as used
            slowTimeStarted = System.currentTimeMillis();
        }
    }

    private boolean checkCollision(int x, int y) {
        return grid.checkCollision(currentPiece, x, y);
    }

    private void spawnNewPiece() {
        currentPiece = pieceQueue.poll();
        addPieceToQueue();
        
        // Reset piece position to spawn position
        pieceX = (GRID_COLS - currentPiece.getShapeWidth()) / 2;
        pieceY = 0;
        canHold = true;

        // Reset movement counter, harddrop distance, and softdrop distance
        movementCounter = 0;

        // Check if game can spawn piece (if not, game over)
        if (checkCollision(pieceX, pieceY) && currentState != GameState.MENU && currentState != GameState.SCORE_SCREEN && currentState != GameState.LOSE_SCREEN && currentState != GameState.PAUSE) {

            currentState = GameState.LOSE_SCREEN;
            
            add(loseScreen, BorderLayout.CENTER);

            revalidate();
            repaint();
        }
    }

    private void hardDrop() {
        while (!checkCollision(pieceX, pieceY + 1)) {
            pieceY++;
            score++;
        }
        grid.addPiece(currentPiece, pieceX, pieceY);
        updateLinesCleared(isTSpin()); // Centralized update for lines cleared
        spawnNewPiece();
    }

    private void updateLinesCleared(boolean isTspin) {
        int previousLinesCleared = grid.getLinesCleared();
        grid.clearFullLines(); // Clear lines in the grid
        controlGravity(); // Track levels
        currentLinesCleared = grid.getLinesCleared() - previousLinesCleared;

        //Track back to backs
        if(isTspin || currentLinesCleared == 4){
            backToBackCounter++;
        }
        else if (currentLinesCleared > 0){
            backToBackCounter = 0;
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

    private void controlGravity(){
        int linesCleared = grid.getLinesCleared();

        if(currentState != GameState.GAME_TIMETRIAL){
            gravityFactor = 1;
        }
        else if(currentState == GameState.GAME_TIMETRIAL){
            if(linesCleared < 3){
                level = 1;
                gravityFactor = 1;
            }
            else if(linesCleared < 8){
                level = 2;
                gravityFactor = 1.555;
            }
            else if(linesCleared < 15){
                level = 3;
                gravityFactor = 2.475;
            }
            else if(linesCleared < 24){
                level = 4;
                gravityFactor = 4.0161;
            }
            else if(linesCleared < 35){
                level = 5;
                gravityFactor = 6.667;
            }
            else if(linesCleared < 48){
                level = 6;
                gravityFactor = 11.367;
            }
            else if(linesCleared < 63){
                level = 7;
                gravityFactor = 19.802;
            }
            else if(linesCleared < 80){
                level = 8;
                gravityFactor = 35.336;
            }
            else if(linesCleared < 99){
                level = 9;
                gravityFactor = 64.516;
            }
            else {
                level = 10;
                gravityFactor = 120.919;
            }
        }
    }

    private void calculateScore(boolean isTspin){
        double backToBackMultiplier;

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

        if(isGridEmpty()){
            score += 3500 * level;
        }

        if(comboCounter > 1){
            score += level * (50 * (comboCounter-1));
        }

        score += 2 * harddropDistance; //2 points per cell dropped
    }

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

    private void movePieceHorizontally(int direction) {
        if (!checkCollision(pieceX + direction, pieceY)) {
            pieceX += direction;
            if (isLockDelayActive) {
                currentLockDelayTime = 0;
                movementCounter++;
            }
        }
    }

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

    private boolean performWallKick() {
    
        int currentState = currentPiece.getRotationState();
        int previousState = currentPiece.getPreviousRotation();
        
        // Determine which wall kick data to use for both CW and CCW rotations
        int kickIndex;
        
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
            int deltaX = Tetromino.JLSTZ_WALLKICKS[kickIndex][i][0];
            int deltaY = Tetromino.JLSTZ_WALLKICKS[kickIndex][i][1];
            if (tryWallKick(deltaX, deltaY)) {
                return true;
            }
        }

        //I piece has different kick tests
        if (currentPiece.getShapeWidth() == 4) {
            for (int i = 0; i < Tetromino.I_WALLKICKS[kickIndex].length; i++) {
                int deltaX = Tetromino.I_WALLKICKS[kickIndex][i][0];
                int deltaY = Tetromino.I_WALLKICKS[kickIndex][i][1];
                if (tryWallKick(deltaX, deltaY)) {
                    return true;
                }
            }
        }
        
        return false;
    }    

    public boolean isTSpin(){
        if(currentPiece.getIndex() != 1 || !lastKeyValidRotation){
            return false;
        }

        if(!isLockDelayActive){
            return false;
        }

        int[][] corners = {
            {pieceY, pieceX},           // Top-left
            {pieceY, pieceX + 2},       // Top-right
            {pieceY + 2, pieceX},       // Bottom-left
            {pieceY + 2, pieceX + 2}    // Bottom-right
        };

        // Count occupied corners
        int occupiedCorners = 0;
        for (int i = 0; i < corners.length; i++) {
            int row = corners[i][0];
            int col = corners[i][1];

            // Check if corner is outside grid bounds
            boolean isOutOfBounds = row >= Grid.ROWS || col >= Grid.COLS || col < 0;

            // Check if corner is occupied by a block
            boolean isOccupied = false;
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

    public void displayHighscores(){
        System.out.println("Top 10 High Scores:");
        for (int i = 1; i < 11; i++) {
            System.out.println((i + 1) + ". " + scoreManager.getScore("GAME_SPRINT", i));
        }
    }

    public void keyPressed(KeyEvent e) {
        boolean pieceWasMoved = false;

        if(currentState != GameState.MENU){
            switch(e.getKeyCode()) {
                case KeyEvent.VK_R:
                    restartGame();
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

        if (!isCountingdown && currentState != GameState.MENU) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    leftKeyPressed = true;
                    lastKeyPressed = -1; // Left arrow is now the last key pressed
                    dasCharge = 0;
                    movePieceHorizontally(-1);
                    pieceWasMoved = true;
                    lastKeyValidRotation = false;
                    break;
        
                case KeyEvent.VK_RIGHT:
                    rightKeyPressed = true;
                    lastKeyPressed = 1; // Right arrow is now the last key pressed
                    dasCharge = 0;
                    movePieceHorizontally(1);
                    pieceWasMoved = true;
                    lastKeyValidRotation = false;
                    break;
        
                case KeyEvent.VK_SHIFT:
                    softDropActive = true;
                    break;
        
                case KeyEvent.VK_UP:
                    currentPiece.rotateCW();
                    pieceWasMoved = true;
                    movementCounter++;
                    lastKeyValidRotation = true;
                    if (!performWallKick()) {
                        currentPiece.rotateCCW();
                        lastKeyValidRotation = false;
                    }
                    
                    break;
        
                case KeyEvent.VK_DOWN:
                    currentPiece.rotateCCW();
                    pieceWasMoved = true;
                    movementCounter++;
                    lastKeyValidRotation = true;

                    if (!performWallKick()) {
                        currentPiece.rotateCW();
                        lastKeyValidRotation = false;
                    }
                    break;
        
                case KeyEvent.VK_X:
                    currentPiece.rotateFlip();
                    lastKeyValidRotation = false;
                    pieceWasMoved = true;
                    if (checkCollision(pieceX, pieceY)) {
                        if(tryWallKick(-1, 0)) return;
                        if(tryWallKick(1, 0)) return;
                        currentPiece.rotateFlip(); // Undo if invalid
                    }
                    break;
        
                case KeyEvent.VK_SPACE:
                    hardDrop();
                    break;
                case KeyEvent.VK_V:
                    activateSlowTime();
                    break;
                case KeyEvent.VK_C:
                    holdPiece();
                    lastKeyValidRotation = false;
                    break;

                case KeyEvent.VK_T: // 'T' key for testing
                    System.out.println("Total rows: " + 20);
                    System.out.println("Total columns: " + 10);
                    
                    int rotationState = currentPiece.getRotationState();
                    System.out.println("Current rotation state: " + rotationState);
                    
                    // Example usage of getIndex to check the type of the current piece
                    int currentPieceIndex = currentPiece.getIndex();
                    switch (currentPieceIndex) {
                        case 0:
                            System.out.println("Current piece is I-piece");
                            break;
                        case 1:
                            System.out.println("Current piece is T-piece");
                            break;
                        case 2:
                            System.out.println("Current piece is O-piece");
                            break;
                        case 3:
                            System.out.println("Current piece is Z-piece");
                            break;
                        case 4:
                            System.out.println("Current piece is S-piece");
                            break;
                        case 5:
                            System.out.println("Current piece is J-piece");
                            break;
                        case 6:
                            System.out.println("Current piece is L-piece");
                            break;
                        default:
                            System.out.println("Unknown piece");
                            break;
                    }

                    int[][] corners = {
                        {pieceY, pieceX},           // Top-left
                        {pieceY, pieceX + 2},       // Top-right
                        {pieceY + 2, pieceX},       // Bottom-left
                        {pieceY + 2, pieceX + 2}    // Bottom-right
                    };

                    for (int i = 0; i < corners.length; i++) {
                        int row = corners[i][0];
                        int col = corners[i][1];
                        System.out.println("Corner " + i + " at [" + row + ", " + col + "]");
                    }

                    System.out.println("Handing Settings: " + dasDelay + " " + arrDelay + " " + softdropFactor + " " + audioEnabled + " " + musicVolume + " " + sfxVolume + " " + gridVisibility + " " + ghostVisibility + " " + actionTextOn);

            }
        
            // Reset lock delay if piece was moved or rotated until max movement reached
            if (pieceWasMoved && isLockDelayActive && movementCounter < MAX_MOVEMENTS) {
                currentLockDelayTime = 0;
                movementCounter++;
            }
        
            repaint();
        }
    }

    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                leftKeyPressed = false;
                if (lastKeyPressed == -1) {
                    lastKeyPressed = rightKeyPressed ? 1 : 0;
                }
                dasCharge = 0;
                break;
    
            case KeyEvent.VK_RIGHT:
                rightKeyPressed = false;
                if (lastKeyPressed == 1) {
                    lastKeyPressed = leftKeyPressed ? -1 : 0;
                }
                dasCharge = 0;
                break;
    
            case KeyEvent.VK_SHIFT:
                softDropActive = false;
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

    public void returntoMenu(){
        currentState = GameState.MENU;
        menu.resetToMainMenu(null);
        remove(scoreScreen);
        revalidate();
        repaint();
        scoreScreen.resetButtonAppearance(scoreScreen.getMenuButton());
    }
    
}
