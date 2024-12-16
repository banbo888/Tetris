
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Queue;
import javax.swing.*;

public class GamePanel extends JPanel implements Runnable, KeyListener {
    public static final int GAME_WIDTH = 300;  // Width of main grid
    public static final int GAME_HEIGHT = 600; // Height of grid
    private static final int BLOCK_SIZE = Tetromino.BLOCK_SIZE;
    private static final int GRID_ROWS = GAME_HEIGHT / BLOCK_SIZE;
    private static final int GRID_COLS = GAME_WIDTH / BLOCK_SIZE;
    private static final int SIDE_PANEL_WIDTH = 150; // Width of side panels
    private static final int GRID_OFFSET_X = SIDE_PANEL_WIDTH; // Offset for grid drawing and calculations
    //Softdrop 
    private int lockDelayFrames = 30;  // ~0.5 seconds at 60 FPS
    private int currentLockDelayFrames = 0;
    private int movementCounter = 0;
    private static final int MAX_MOVEMENTS = 15;
    private boolean isLockDelayActive = false;
    //DAS AND ARR
    private static final int DAS_DELAY = 5; // Delay before auto-shift starts (in frames)
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

    private Queue<Tetromino> pieceQueue;
    private int pieceX = GRID_COLS / 2 - 2;
    private int pieceY = 0;
    private boolean softDropActive = false;
    private boolean canHold = true;


    // Enum for game states
    public enum GameState {
        MAIN_MENU, // Main menu state
        GAME_SPRINT,      // Active game state
        GAME_TIMETRIAL,
        GAME_PRACTICE,
        GAME_CHALLENGE,
        SCORE_SCREEN, // Win screen state
        LOSE_SCREEN,//Lose screen

    }
    
    public GamePanel() {
        menu = new Menu(this);
        grid = new Grid();
        gameTimer = new Timer();
        pieceQueue = new LinkedList<>();
        bagGenerator = new PieceBagGenerator();
        initializePieceQueue();

        currentPiece = pieceQueue.poll();
        spawnNewPiece();

        this.setFocusable(true);
        this.addKeyListener(this);
        this.setPreferredSize(new Dimension(GAME_WIDTH + (2 * SIDE_PANEL_WIDTH), GAME_HEIGHT));
        this.setBackground(Color.BLACK); 
        this.setLayout(new BorderLayout());
        add(menu, BorderLayout.CENTER);


        gameThread = new Thread(this);
        gameThread.start();
    }

    public void startGame() {
        currentState = GameState.GAME_SPRINT;
        restartGame();
        gameTimer.start(); // Start the timer
        repaint();
        
    }


    private void restartGame() {
        currentState = GameState.GAME_SPRINT;

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
    

    public void returnToMainMenu (){
        currentState = GameState.MAIN_MENU;
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
            menu.setVisible(true); // Show menu
        } else {
            menu.setVisible(false); // Hide menu when the game starts
    
            // Draw game grid
            g.setColor(Color.LIGHT_GRAY);
            for (int x = GRID_OFFSET_X; x <= GRID_OFFSET_X + GAME_WIDTH; x += BLOCK_SIZE) {
                g.drawLine(x, 0, x, GAME_HEIGHT);
            }
            for (int y = 0; y <= GAME_HEIGHT; y += BLOCK_SIZE) {
                g.drawLine(GRID_OFFSET_X, y, GRID_OFFSET_X + GAME_WIDTH, y);
            }
    
            grid.draw(g, GRID_OFFSET_X);
            currentPiece.draw(g, GRID_OFFSET_X + pieceX * BLOCK_SIZE, pieceY * BLOCK_SIZE);
            drawQueue(g);
            drawHeldPiece(g);
            drawGhostPiece(g);
        }
    }


    private void drawQueue(Graphics g) {
        int startX = GAME_WIDTH + GRID_OFFSET_X + 20; // Right side panel
        int startY = 50;              // Start Y position for the first piece in the queue
        int spacing = 100;            // Vertical spacing between queued pieces
    
        // Draw the "Next Pieces" label
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 20));
        g.drawString("Next Pieces", startX, startY - 20);
    
        // Draw the next pieces in the queue
        int i = 0;
        for (Tetromino piece : pieceQueue) {
            piece.draw(g, startX, startY + (i * spacing)); // Draw each queued piece
            i++;
        }
    }

    private void drawHeldPiece(Graphics g) {
        int startX = 20; // Left side panel
        int startY = 50;

        // Draw "Held Piece" label
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 20));
        g.drawString("Held Piece", startX, startY - 20);

        // Draw the held piece (if any)
        if (heldPiece != null) {
            heldPiece.draw(g, startX, startY);
        }
    }

    private void holdPiece() {
        if (!canHold) return; // Can only hold once per piece

        if (heldPiece == null) {
            // First time holding a piece
            heldPiece = currentPiece;
            spawnNewPiece();
        } else {
            // Swap current piece with held piece
            Tetromino temp = currentPiece;
            currentPiece = heldPiece;
            heldPiece = temp;
            
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

    private void drawGhostPiece(Graphics g) {
        int ghostY = pieceY;
        
        // Find the lowest valid position for the ghost piece
        while (!checkCollision(pieceX, ghostY + 1)) {
            ghostY++;
        }
        
        // Draw the ghost piece with reduced opacity
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
        
        currentPiece.draw(g2d, 
            GRID_OFFSET_X + pieceX * Tetromino.BLOCK_SIZE, 
            ghostY * Tetromino.BLOCK_SIZE
        );
        
        g2d.dispose();
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
            
            if (elapsedTime > adjustedDropInterval) {
                updateGame(); // Call the game update function
                lastDropTime = now; // Update the last drop time to the current time
            }
        }

    }

    
    private void updateGame() {
        if (!checkCollision(pieceX, pieceY + 1)) {
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


        if(grid.getLinesCleared() >= 10 && currentState == GameState.GAME_SPRINT){
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
        
        // Adjust initial piece position based on piece shape
        int pieceWidth = currentPiece.getShape()[0].length;
    
        // Center the piece on the board
        pieceX = (GRID_COLS - pieceWidth) / 2;  // Ensures proper centering
        pieceY = 0;
        canHold = true;
    
        // Check for collision right after spawning
        if (checkCollision(pieceX, pieceY)) {
            JOptionPane.showMessageDialog(this, "Game Over!");
            gameTimer.stop(); // Stop the timer
            System.exit(0);
        }
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

    public void keyPressed(KeyEvent e) {
        boolean pieceWasMoved = false;
    
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
                if (checkCollision(pieceX, pieceY)) {
                    currentPiece.rotateCCW(); // Undo if invalid
                } else {
                    pieceWasMoved = true;
                }
                break;
    
            case KeyEvent.VK_DOWN:
                currentPiece.rotateCCW();
                if (checkCollision(pieceX, pieceY)) {
                    currentPiece.rotateCW(); // Undo if invalid
                } else {
                    pieceWasMoved = true;
                }
                break;
    
            case KeyEvent.VK_X:
                currentPiece.rotateFlip();
                if (checkCollision(pieceX, pieceY)) {
                    currentPiece.rotateCW(); // Undo if invalid
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
                    returnToMainMenu(); // Call method to return to main menu
                    menu.resetToMainMenu(null);
                }
                break;
        }
    
        // Reset lock delay and increment movement counter if piece was moved or rotated
        if (pieceWasMoved && isLockDelayActive) {
            currentLockDelayFrames = 0;
            movementCounter++;
        }
    
        repaint();
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