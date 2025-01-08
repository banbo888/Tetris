import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Grid {
    public static final int ROWS = 20;
    public static final int COLS = 10;
    private int[][] grid;
    private Color[][] colors;
    private int linesCleared = 0;
    private int piecesPlaced = 0;
    private boolean[][] bombLocations;
    private static final double BOMB_SPAWN_CHANCE = 0.15; // 15% chance for eligible blocks
    private int consecutiveUnclearedLines = 0;
    private boolean lastClearFromBomb = false;
    private static final int BLOCK_SIZE = 30; // Matching Tetromino.BLOCK_SIZE
    public Grid() {
        grid = new int[ROWS][COLS];
        colors = new Color[ROWS][COLS];
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                grid[row][col] = 0;
                colors[row][col] = Color.BLACK;
            }
        }
        bombLocations = new boolean[ROWS][COLS];
    }

    public boolean wasLastClearFromBomb() {
        return lastClearFromBomb;
    }

    public boolean isBomb(int row, int col) {
        return bombLocations[row][col];
    }

    private void handleBombDetonation(int row) {
        // Clear the bomb's line
        clearLine(row);
        
        // Clear the line below if it exists
        if (row < ROWS - 1) {
            clearLine(row + 1);
        }
        
        // Remove all bomb flags from these rows
        Arrays.fill(bombLocations[row], false);
        if (row < ROWS - 1) {
            Arrays.fill(bombLocations[row + 1], false);
        }

        lastClearFromBomb = true;
        linesCleared += (row < ROWS - 1) ? 2 : 1; // Count both lines if applicable
    }

    private void clearLine(int row) {
        // Shift all rows above this one down
        for (int i = row; i > 0; i--) {
            grid[i] = grid[i - 1].clone();
            colors[i] = colors[i - 1].clone();
            bombLocations[i] = bombLocations[i - 1].clone();
        }
        
        // Clear the top row
        Arrays.fill(grid[0], 0);
        Arrays.fill(colors[0], Color.BLACK);
        Arrays.fill(bombLocations[0], false);
    }
    // In Grid class
    public void clearFullLines() {
        boolean bombCleared = false;
        int bombRow = -1;
        ArrayList<Integer> linesToClear = new ArrayList<>();
        
        // First pass: identify full lines and bombs
        for (int row = ROWS - 1; row >= 0; row--) {
            boolean isLineFull = true;
            boolean hasBomb = false;
            
            for (int col = 0; col < COLS; col++) {
                // Check if cell is empty (0 represents empty cell)
                if (grid[row][col] == 0) {
                    isLineFull = false;
                }
                if (bombLocations[row][col]) {
                    hasBomb = true;
                    bombRow = row;
                }
            }
            
            if (isLineFull) {
                linesToClear.add(row);
                if (hasBomb) {
                    bombCleared = true;
                    // Add the line below the bomb if it exists
                    if (row < ROWS - 1) {
                        linesToClear.add(row + 1);
                    }
                }
            }
        }
        
        // Sort lines to clear from bottom to top
        Collections.sort(linesToClear, Collections.reverseOrder());
        
        // Clear the lines and update the grid
        for (int row : linesToClear) {
            // Clear the line
            for (int col = 0; col < COLS; col++) {
                grid[row][col] = 0;  // Set to empty
                bombLocations[row][col] = false;
            }
            
            // Move all lines above down
            for (int r = row - 1; r >= 0; r--) {
                for (int col = 0; col < COLS; col++) {
                    grid[r + 1][col] = grid[r][col];
                    bombLocations[r + 1][col] = bombLocations[r][col];
                    grid[r][col] = 0;  // Set to empty
                    bombLocations[r][col] = false;
                }
            }
        }
        
        // Update lines cleared count
        linesCleared += linesToClear.size();
        lastClearFromBomb = bombCleared;
    }
public boolean checkCollision(Tetromino piece, int x, int y) {
    int[][] shape = piece.getCurrentRotation();
    
    // Check each cell of the piece
    for (int row = 0; row < shape.length; row++) {
        for (int col = 0; col < shape[row].length; col++) {
            if (shape[row][col] != 0) {
                int gridRow = y + row;
                int gridCol = x + col;
                
                // Check if position is out of bounds
                if (gridRow >= ROWS || gridCol < 0 || gridCol >= COLS) {
                    return true;
                }
                
                // Check if position is below the bottom
                if (gridRow < 0) {
                    return true;
                }
                
                // Check if the cell is already occupied (0 represents empty cell)
                if (grid[gridRow][gridCol] != 0) {
                    return true;
                }
            }
        }
    }
    return false;
}
    public void addPiece(Tetromino piece, int x, int y) {
        int[][] shape = piece.getCurrentRotation();
        boolean touchedBomb = false;
        int bombRow = -1;
        
        // First pass: check for bomb contact
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    int gridRow = y + row;
                    int gridCol = x + col;
                    
                    // Check adjacent cells for bombs
                    if (gridRow < ROWS - 1 && bombLocations[gridRow + 1][gridCol]) {
                        touchedBomb = true;
                        bombRow = gridRow + 1;
                    }
                    if (gridRow > 0 && bombLocations[gridRow - 1][gridCol]) {
                        touchedBomb = true;
                        bombRow = gridRow - 1;
                    }
                    if (gridCol < COLS - 1 && bombLocations[gridRow][gridCol + 1]) {
                        touchedBomb = true;
                        bombRow = gridRow;
                    }
                    if (gridCol > 0 && bombLocations[gridRow][gridCol - 1]) {
                        touchedBomb = true;
                        bombRow = gridRow;
                    }
                }
            }
        }
        
        // Handle bomb detonation if necessary
        if (touchedBomb && bombRow != -1) {
            handleBombDetonation(bombRow);
        } else {
            lastClearFromBomb = false;
        }
        
        // Second pass: add the piece normally
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    grid[y + row][x + col] = piece.getIndex() + 1;
                    colors[y + row][x + col] = piece.getColor();
                }
            }
        }
        
        piecesPlaced++;
        
        // Check for consecutive uncleared lines
        boolean anyLinesCleared = false;
        for (int row = 0; row < ROWS; row++) {
            if (isLineFull(row)) {
                anyLinesCleared = true;
                break;
            }
        }
        
        if (!anyLinesCleared) {
            consecutiveUnclearedLines++;
            if (consecutiveUnclearedLines >= 4) {
                trySpawnBomb();
            }
        } else {
            consecutiveUnclearedLines = 0;
        }
    }

    private void trySpawnBomb() {
        // Only spawn if there's no other bomb on screen
        boolean bombExists = false;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (bombLocations[row][col]) {
                    bombExists = true;
                    break;
                }
            }
        }
        
        if (!bombExists) {
            // Find eligible blocks (occupied spaces)
            ArrayList<Point> eligibleSpaces = new ArrayList<>();
            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLS; col++) {
                    if (grid[row][col] != 0) {
                        eligibleSpaces.add(new Point(col, row));
                    }
                }
            }
            
            if (!eligibleSpaces.isEmpty() && Math.random() < BOMB_SPAWN_CHANCE) {
                Point selected = eligibleSpaces.get((int)(Math.random() * eligibleSpaces.size()));
                bombLocations[selected.y][selected.x] = true;
            }
        }
    }

    private void drawBomb(Graphics g, int x, int y) {
        // Draw bomb body (black circle)
        g.setColor(Color.BLACK);
        g.fillOval(x + 2, y + 2, BLOCK_SIZE - 4, BLOCK_SIZE - 4);
        
        // Draw bomb fuse (brown)
        g.setColor(new Color(139, 69, 19));
        g.fillRect(x + BLOCK_SIZE/2 - 1, y, 2, 5);
        
        // Draw bomb highlight (white circle)
        g.setColor(Color.WHITE);
        g.fillOval(x + BLOCK_SIZE/4, y + BLOCK_SIZE/4, BLOCK_SIZE/6, BLOCK_SIZE/6);
    }

    private boolean isLineFull(int row) {
        for (int col = 0; col < COLS; col++) {
            if (grid[row][col] == 0) {
                return false;
            }
        }
        return true;
    }

    public int getLinesCleared(){
        return linesCleared;
    }

    public int getPiecesPlaced(){
        return piecesPlaced;
    }

    public void resetStats() {
        linesCleared = 0;
        piecesPlaced = 0;
        consecutiveUnclearedLines = 0;
        lastClearFromBomb = false;
        for (int row = 0; row < ROWS; row++) {
            Arrays.fill(bombLocations[row], false);
        }
    }

    public boolean isOccupied(int row, int col) {
        // Check if the row and column are within grid bounds
        if (row < 0 || row >= ROWS || col < 0 || col >= COLS) {
            return true; // Treat out-of-bounds as occupied to prevent piece movement
        }
        return grid[row][col] == 1;
    }

    public void draw(Graphics g, int offsetX, int offsetY) {
        // Draw regular blocks
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (grid[row][col] != 0) {
                    if (bombLocations[row][col]) {
                        drawBomb(g, offsetX + col * BLOCK_SIZE, offsetY + row * BLOCK_SIZE);
                    } else {
                        g.setColor(colors[row][col]);
                        g.fillRect(offsetX + col * BLOCK_SIZE, offsetY + row * BLOCK_SIZE,
                                 BLOCK_SIZE, BLOCK_SIZE);
                        g.setColor(Color.BLACK);
                        g.drawRect(offsetX + col * BLOCK_SIZE, offsetY + row * BLOCK_SIZE,
                                 BLOCK_SIZE, BLOCK_SIZE);
                    }
                }
            }
        }
    }
    
    
}
