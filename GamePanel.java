import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Queue;
import javax.swing.*;

public class GamePanel extends JPanel implements Runnable, KeyListener {
    public static final int GAME_WIDTH = 300;  // Width of main grid
    private static final int TOP_PANEL_HEIGHT = 50; // Adjust as needed
    private static final int BOTTOM_PANEL_HEIGHT = 25; // Adjust as needed 
    public static final int GAME_HEIGHT = 600 + TOP_PANEL_HEIGHT + BOTTOM_PANEL_HEIGHT; // Original height plus new panels
    private static final int BLOCK_SIZE = Tetromino.BLOCK_SIZE;
    private static final int GRID_ROWS = GAME_HEIGHT / BLOCK_SIZE;
    private static final int GRID_COLS = GAME_WIDTH / BLOCK_SIZE;
    private static final int SIDE_PANEL_WIDTH = 300; // Width of side panels
    private static final int GRID_OFFSET_X = SIDE_PANEL_WIDTH; // Offset for grid drawing and calculations
    
    //Softdrop 
    private int lockDelayFrames = 60;  // ~0.5 seconds at 60 FPS
    private int currentLockDelayFrames = 0;
    private int movementCounter = 0;
    private static final int MAX_MOVEMENTS = 30;
    private boolean isLockDelayActive = false;
    //DAS AND ARR
    private static final int DAS_DELAY = 4; // Delay before auto-shift starts (in frames)
    private static final int ARR_DELAY = 1; // Auto-repeat rate (in frames)
    private int dasCharge = 0;
    private boolean leftKeyPressed = false;
    private boolean rightKeyPressed = false;
    private int lastKeyPressed = 0; // 0: None, -1: Left, 1: Right

    Thread gameThread;
    Tetromino currentPiece;
    Tetromino heldPiece;
    Grid grid;
    PieceBagGenerator bagGenerator;
    GameState currentState = GameState.MAIN_MENU; // Current state of the game
    Menu menu;
    Timer gameTimer;
    GameInterface gameInterface;

    private Queue<Tetromino> pieceQueue;
    private int pieceX = GRID_COLS / 2 - 2;
    private int pieceY = 0;
    private boolean softDropActive = false;
    private boolean canHold = true;

    //Countdown timer
    private boolean isCountingdown = false;
    private long countdownStartTime;
    private static final String[] COUNTDOWN_TEXT = {"GO!", "SET", "READY"};


    // Enum for game states
    public enum GameState {
        MAIN_MENU, // Main menu state
        GAME,      // Active game state
        SCORE_SCREEN, // Win screen state
        LOSE_SCREEN,//Lose screen

    }
    
    public GamePanel() {
        menu = new Menu(this);
        grid = new Grid();
        gameTimer = new Timer();
        pieceQueue = new LinkedList<>();
        bagGenerator = new PieceBagGenerator();
        gameInterface = new GameInterface(GRID_OFFSET_X, TOP_PANEL_HEIGHT);
        initializePieceQueue();

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
    }

    public void startGame() {
        currentState = GameState.GAME;
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


    private void restartGame() {
        currentState = GameState.GAME;
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
        
        // Get first piece from queue
        currentPiece = pieceQueue.poll();
        spawnNewPiece();
        
        //Restart stats
        grid.resetStats();
        gameTimer.reset();  // Reset the timer
        gameTimer.start();  // Restart the timer

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


    public void paint(Graphics g) {
        super.paint(g);
        if (currentState == GameState.MAIN_MENU) {
            menu.setVisible(true);
        } else {
            menu.setVisible(false);
            
            // Draw top panel
            g.setColor(Color.BLACK);
            g.fillRect(GRID_OFFSET_X, 0, GAME_WIDTH, TOP_PANEL_HEIGHT);
            
            // Draw bottom panel
            g.setColor(Color.BLACK);
            g.fillRect(GRID_OFFSET_X, GAME_HEIGHT - BOTTOM_PANEL_HEIGHT, GAME_WIDTH, BOTTOM_PANEL_HEIGHT);
            
            // Adjust grid drawing offset
            int gridDrawOffset = TOP_PANEL_HEIGHT;
            
            // Draw game grid
            g.setColor(Color.LIGHT_GRAY);
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
            
            // You'll need to update other drawing methods similarly
            gameInterface.updateState(pieceQueue, heldPiece, currentPiece, pieceX, pieceY, currentState.toString(), isCountingdown);
            gameInterface.drawQueue(g);
            gameInterface.drawHeldPiece(g);
            gameInterface.drawGhostPiece(g, grid);
            gameInterface.drawStats(g, gameTimer.getFormattedTime(), grid.getPiecesPlaced(), grid.getLinesCleared());

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
            if (dasCharge >= DAS_DELAY) {
                if (dasCharge % ARR_DELAY == 0) {
                    movePieceHorizontally(-1);
                }
            }
            dasCharge++;
        } else if (lastKeyPressed == 1 && rightKeyPressed) { // Move right if last key was right
            if (dasCharge >= DAS_DELAY) {
                if (dasCharge % ARR_DELAY == 0) {
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
        int adjustedDropInterval;

        long lastDropTime = System.nanoTime();
        int dropInterval = 500000000; // 500ms in nanoseconds
        
        while (true) {
            now = System.nanoTime();
            elapsedTime = now - lastDropTime; // Calculate time since last drop
            delta += (now - lastTime) / ns;
            lastTime = now;
            
            if (delta >= 1) {
                handleDAS();
                repaint();
                delta--;
            }        


            if (softDropActive) {
                adjustedDropInterval = dropInterval / 50; // If soft drop is active, drop faster
            } else {
                adjustedDropInterval = dropInterval; // If soft drop is not active, use normal interval
            }
            
            if (!isCountingdown && elapsedTime > adjustedDropInterval) {
                updateGame(); // Call the game update function
                lastDropTime = now; // Update the last drop time to the current time
            }
        }

    }

    
    private void updateGame() {
        // Check for game over condition first
        if (isGameOver() && currentState != GameState.MAIN_MENU) {
            currentState = GameState.LOSE_SCREEN;
            gameTimer.stop();
            Object[] options = {"Return to Main Menu", "Try Again"};
            int choice = JOptionPane.showOptionDialog(this,
                "Game Over!",
                "Game Over",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
            
            if (choice == JOptionPane.YES_OPTION) {
                currentState = GameState.MAIN_MENU;
                menu.resetToMainMenu(null);
                return;
            } else if (choice == JOptionPane.NO_OPTION) {
                restartGame();
                return;
            }            
        }
    
        if (!checkCollision(pieceX, pieceY + 1) && currentState != GameState.MAIN_MENU) {
            pieceY++;
            currentLockDelayFrames = 0;
            isLockDelayActive = false;
            movementCounter = 0;
        } else {
            // Piece has landed
            if (!isLockDelayActive) {
                isLockDelayActive = true;
                currentLockDelayFrames = 0;
            }
    
            currentLockDelayFrames++;
    
            // Lock the piece after lock delay or max movements
            if (currentLockDelayFrames >= lockDelayFrames || movementCounter >= MAX_MOVEMENTS) {
                grid.addPiece(currentPiece, pieceX, pieceY);
                grid.clearFullLines();
                spawnNewPiece();
            }
        }
    
        if(grid.getLinesCleared() >= 40 && currentState == GameState.GAME){
            currentState = GameState.SCORE_SCREEN;
            gameTimer.stop();
            System.out.println(gameTimer.getFormattedTime());
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
    }

    private boolean isGameOver() {
        // Check if the new piece collides at spawn position
        if (checkCollision(pieceX, pieceY)) {
            return true;
        }
        
        // Additionally check if any blocks are in the top row (optional but recommended)
        for (int x = 0; x < GRID_COLS; x++) {
            if (grid.isOccupied(0, x)) {
                return true;
            }
        }
        
        return false;
    }

    private void hardDrop() {
        while (!checkCollision(pieceX, pieceY + 1)) {
            pieceY++;
        }
        // Immediately lock the piece without delay
        grid.addPiece(currentPiece, pieceX, pieceY);
        grid.clearFullLines();
        spawnNewPiece();
    }

    private void movePieceHorizontally(int direction) {
        if (!checkCollision(pieceX + direction, pieceY)) {
            pieceX += direction;
            if (isLockDelayActive) {
                currentLockDelayFrames = 0;
                movementCounter++;
            }
        }
    }

    private void findGridsOccupied(){
        for (int row = 0; row < 20; row++) {
            for (int col = 0; col < 10; col++) {
                if (grid.isOccupied(row, col)) {
                    System.out.println("Occupied cell at [" + row + ", " + col + "]");
                }
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
            currentLockDelayFrames = 0;
            movementCounter = 0;  // Reset movement counter to allow for more movements
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
    

    public void keyPressed(KeyEvent e) {
        boolean pieceWasMoved = false;

        switch(e.getKeyCode()) {
            case KeyEvent.VK_R:
                restartGame();
                break;
            
            case KeyEvent.VK_ESCAPE:
                // Show confirmation popup
                int response = JOptionPane.showConfirmDialog(
                    null,
                    "Do you want to return to the main menu?",
                    "Confirm Exit",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );
                
                // Check the user's choice
                if (response == JOptionPane.YES_OPTION) {
                    currentState = GameState.MAIN_MENU;
                    menu.resetToMainMenu(null);
                }
                break;
        }



        if (!isCountingdown && currentState != GameState.MAIN_MENU) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    leftKeyPressed = true;
                    lastKeyPressed = -1; // Left arrow is now the last key pressed
                    dasCharge = 0;
                    movePieceHorizontally(-1);
                    break;
        
                case KeyEvent.VK_RIGHT:
                    rightKeyPressed = true;
                    lastKeyPressed = 1; // Right arrow is now the last key pressed
                    dasCharge = 0;
                    movePieceHorizontally(1);
                    break;
        
                case KeyEvent.VK_SHIFT:
                    softDropActive = true;
                    break;
        
                case KeyEvent.VK_UP:
                    currentPiece.rotateCW();
                    if (!performWallKick()) {
                        currentPiece.rotateCCW();
                    }
                    break;
        
                case KeyEvent.VK_DOWN:
                    currentPiece.rotateCCW();
                    if (!performWallKick()) {
                        currentPiece.rotateCW();
                    }
                    break;
        
                case KeyEvent.VK_X:
                    currentPiece.rotateFlip();
                    if (checkCollision(pieceX, pieceY)) {
                        if(tryWallKick(-1, 0)) return;
                        if(tryWallKick(1, 0)) return;
                        currentPiece.rotateFlip(); // Undo if invalid
                    } else {
                        pieceWasMoved = true;
                    }
                    break;
        
                case KeyEvent.VK_SPACE:
                    hardDrop();
                    break;
        
                case KeyEvent.VK_C:
                    holdPiece();
                    break;

                case KeyEvent.VK_T: // 'T' key for testing
                    System.out.println("Total rows: " + 20);
                    System.out.println("Total columns: " + 10);
                    
                    findGridsOccupied();
                    int rotationState = currentPiece.getRotationState();
                    System.out.println("Current rotation state: " + rotationState);
                    
                    break;
            }
        
            // Reset lock delay and increment movement counter if piece was moved or rotated
            if (pieceWasMoved && isLockDelayActive) {
                currentLockDelayFrames = 0;
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

}
