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
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] == 1) {
                    int newX = x + col;
                    int newY = y + row;
    
                    // Only add the piece to the grid if it's within bounds or above the grid
                    if (newX >= 0 && newX < COLS) {
                        if (newY >= 0) {
                            // If the piece is above or within the grid, add it to the grid
                            grid[newY][newX] = 1;
                            colors[newY][newX] = piece.getColor();
                        } 
                        // No else needed: we simply don't add it to the grid if it's above, but it's allowed to hover.
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
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] == 1) {
                    int newX = x + col;
                    int newY = y + row;
    
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
        for (int row = 0; row < ROWS; row++) {
            boolean full = true;
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
