import java.awt.*;

public class Grid {
    public static final int ROWS = 20;
    public static final int COLS = 10;
    private int[][] grid;
    private Color[][] colors;
    public int linesCleared = 0;
    public int piecesPlaced = 0;

    public Grid() {
        grid = new int[ROWS][COLS];
        colors = new Color[ROWS][COLS];
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                grid[row][col] = 0;
                colors[row][col] = Color.BLACK;
            }
        }
    }

    public void addPiece(Tetromino piece, int x, int y) {
        int[][] shape = piece.getShape();
        int newX, newY;

        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] == 1) {
                    newX = x + col;
                    newY = y + row;
    
                    // Only add the piece to the grid if it's within bounds or above the grid
                    if (newX >= 0 && newX < COLS) {
                        if (newY >= 0) {
                            // If the piece is above or within the grid, add it to the grid
                            grid[newY][newX] = 1;
                            colors[newY][newX] = piece.getColor();
                        } 
                    }
                }
            }
        }
    
        piecesPlaced++;
    }

    public void fillGrid(int row, int col, Color color) {
        if (row >= 0 && row < ROWS && col >= 0 && col < COLS) {
            grid[row][col] = 1; // Mark the cell as filled
            colors[row][col] = color; // Set the color of the cell
        }
    }
    
    public boolean checkCollision(Tetromino piece, int x, int y) {
        int[][] shape = piece.getShape();
        int newX, newY;

        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] == 1) {
                    newX = x + col;
                    newY = y + row;
    
                    // Ensure newY is within grid bounds
                    if (newX < 0 || newX >= COLS || newY >= ROWS) {
                        return true;
                    }
    
                    // Check for collision with occupied spaces
                    if (newY >= 0 && grid[newY][newX] == 1) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void clearFullLines() {
        boolean full;
        
        for (int row = 0; row < ROWS; row++) {
            full = true;
            for (int col = 0; col < COLS; col++) {
                if (grid[row][col] == 0) {
                    full = false;
                    break;
                }
            }
            if (full) {
                for (int i = row; i > 0; i--) {
                    grid[i] = grid[i - 1].clone();
                    colors[i] = colors[i - 1].clone();
                }
                linesCleared++;
            };
        }
    }

    public int getLinesCleared(){
        return linesCleared;
    }

    public int getPiecesPlaced(){
        return piecesPlaced;
    }

    public void resetStats(){
        linesCleared = 0;
        piecesPlaced = 0;
    }

    public boolean isOccupied(int row, int col) {
        // Check if the row and column are within grid bounds
        if (row < 0 || row >= ROWS || col < 0 || col >= COLS) {
            return true; // Treat out-of-bounds as occupied to prevent piece movement
        }
        return grid[row][col] == 1;
    }

    // Add these methods to your Grid class
public void clearCell(int row, int col) {
    if (row >= 0 && row < ROWS && col >= 0 && col < COLS) {
        grid[row][col] = 0;
        colors[row][col] = Color.BLACK;
    }
}

public void clearSurroundingBlocks(Tetromino piece, int pieceX, int pieceY) {
    int[][] shape = piece.getShape();
    
    // Calculate the area to clear (one block radius around the piece)
    for (int row = pieceY - 1; row <= pieceY + shape.length; row++) {
        for (int col = pieceX - 1; col <= pieceX + shape[0].length; col++) {
            // Skip if outside grid bounds
            if (row < 0 || row >= ROWS || col < 0 || col >= COLS) {
                continue;
            }
            
            // Clear all cells in the area, including the piece
            clearCell(row, col);
        }
    }
}

    public void draw(Graphics g, int offsetX, int offsetY) {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (grid[row][col] != 0) {
                    g.setColor(colors[row][col]);
                    g.fillRect(
                        offsetX + col * Tetromino.BLOCK_SIZE, 
                        offsetY + row * Tetromino.BLOCK_SIZE,
                        Tetromino.BLOCK_SIZE, 
                        Tetromino.BLOCK_SIZE
                    );
                    g.drawRect(
                        offsetX + col * Tetromino.BLOCK_SIZE, 
                        offsetY + row * Tetromino.BLOCK_SIZE,
                        Tetromino.BLOCK_SIZE, 
                        Tetromino.BLOCK_SIZE
                    );
                }
            }
        }
    }

    
}
