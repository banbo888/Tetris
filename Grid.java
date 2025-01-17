//ICS Summative - Tetris by Richard Xiong & Eric Ma
//Final Program Submission
//2025-01-16
//Grid Class - Manages the Tetris grid, including the background, tetrominoes, and line clearing mechanisms.

import java.awt.*;
import java.util.Random;

public class Grid {
    public static final int ROWS = 20; // Number of rows in the grid
    public static final int COLS = 10; // Number of columns in the grid
    private int[][] grid; // 2D array to store grid data (0 for empty, 1 for filled)
    private Color[][] colors; // 2D array to store the colors of the grid cells
    
    // Add a buffer zone above the grid for pieces that extend above
    private static final int BUFFER_ROWS = 4;
    private int[][] bufferGrid;
    private Color[][] bufferColors;
    
    public int linesCleared = 0; // Tracks the number of lines cleared
    public int piecesPlaced = 0; // Tracks the number of pieces placed


    public Grid() {
        // Initializes the grid and colors arrays, setting all cells to empty and black
        grid = new int[ROWS][COLS];
        colors = new Color[ROWS][COLS];
        bufferGrid = new int[BUFFER_ROWS][COLS];
        bufferColors = new Color[BUFFER_ROWS][COLS];
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                grid[row][col] = 0; // 0 means empty
                colors[row][col] = Color.BLACK; // Black represents an empty cell
            }
        }
        for (int row = 0; row < BUFFER_ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                bufferGrid[row][col] = 0;
                bufferColors[row][col] = Color.BLACK;
            }
        }
    }

    public void addPiece(Tetromino piece, int x, int y) {
        // Adds the given piece to the grid at the specified position (x, y)
        int[][] shape = piece.getShape(); // Gets the shape of the Tetromino
        int newX, newY;

        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] == 1) { // If the cell is part of the piece
                    newX = x + col;
                    newY = y + row;
    
                    // Only add the piece to the grid if it's within bounds or above the grid
                    if (newX >= 0 && newX < COLS) { // Ensure within horizontal bounds
                        if (newY >= 0) { // Ensure within vertical bounds (above the grid)
                            grid[newY][newX] = 1; // Mark the grid as filled
                            colors[newY][newX] = piece.getColor(); // Set the color of the piece
                        } else if (newY < 0 && -newY <= BUFFER_ROWS) {
                            // Add to buffer grid
                            bufferGrid[BUFFER_ROWS + newY][newX] = 1;
                            bufferColors[BUFFER_ROWS + newY][newX] = piece.getColor();
                        }
                    }
                }
            }
        }
    
        piecesPlaced++; // Increment the number of pieces placed
    }

    public void fillGrid(int row, int col, Color color) {
        // Fills the specified grid cell with the given color
        if (row >= 0 && row < ROWS && col >= 0 && col < COLS) { // Ensure within grid bounds
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
    
                    // Check horizontal bounds always
                    if (newX < 0 || newX >= COLS) {
                        return true;
                    }
                    
                    // Check bottom bound
                    if (newY >= ROWS) {
                        return true;
                    }
                    
                    // Check collision with existing pieces in main grid
                    if (newY >= 0 && grid[newY][newX] == 1) {
                        return true;
                    }
                    
                    // Check collision with pieces in buffer zone
                    if (newY < 0 && -newY <= BUFFER_ROWS) {
                        if (bufferGrid[BUFFER_ROWS + newY][newX] == 1) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public void clearFullLines() {
        // Create temporary grids including buffer
        int totalRows = ROWS + BUFFER_ROWS;
        int[][] tempGrid = new int[totalRows][COLS];
        Color[][] tempColors = new Color[totalRows][COLS];
        
        // Copy buffer and main grid into temp grids
        for (int row = 0; row < BUFFER_ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                tempGrid[row][col] = bufferGrid[row][col];
                tempColors[row][col] = bufferColors[row][col];
            }
        }
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                tempGrid[row + BUFFER_ROWS][col] = grid[row][col];
                tempColors[row + BUFFER_ROWS][col] = colors[row][col];
            }
        }
        
        // Find and clear full lines
        int writeRow = totalRows - 1;
        for (int readRow = totalRows - 1; readRow >= 0; readRow--) {
            boolean isLineFull = true;
            for (int col = 0; col < COLS; col++) {
                if (tempGrid[readRow][col] == 0) {
                    isLineFull = false;
                    break;
                }
            }
            
            if (!isLineFull) {
                if (writeRow != readRow) {
                    // Copy row to new position
                    for (int col = 0; col < COLS; col++) {
                        tempGrid[writeRow][col] = tempGrid[readRow][col];
                        tempColors[writeRow][col] = tempColors[readRow][col];
                    }
                }
                writeRow--;
            } else {
                linesCleared++;
            }
        }
        // Clear remaining rows
        while (writeRow >= 0) {
            for (int col = 0; col < COLS; col++) {
                tempGrid[writeRow][col] = 0;
                tempColors[writeRow][col] = Color.BLACK;
            }
            writeRow--;
        }
        
        // Copy back to buffer and main grid
        for (int row = 0; row < BUFFER_ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                bufferGrid[row][col] = tempGrid[row][col];
                bufferColors[row][col] = tempColors[row][col];
            }
        }
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                grid[row][col] = tempGrid[row + BUFFER_ROWS][col];
                colors[row][col] = tempColors[row + BUFFER_ROWS][col];
            }
        }
    }

    public int getLinesCleared(){
        // Returns the number of lines cleared
        return linesCleared;
    }

    public int getPiecesPlaced(){
        // Returns the number of pieces placed
        return piecesPlaced;
    }

    public void resetStats(){
        // Resets the statistics (lines cleared, pieces placed)
        linesCleared = 0;
        piecesPlaced = 0;
    }

    public boolean isOccupied(int row, int col) {
        // Checks if a specific cell is occupied
        if (row < 0 || row >= ROWS || col < 0 || col >= COLS) {
            return true; // Treat out-of-bounds as occupied to prevent piece movement
        }
        return grid[row][col] == 1; // Returns true if the cell is occupied
    }

    public void clearCell(int row, int col) {
        if (row < 0) {
            // Handle buffer grid
            bufferGrid[row + BUFFER_ROWS][col] = 0;
        } else {
            // Handle main grid
            grid[row][col] = 0;
        }
    }

    public void clearSurroundingBlocks(Tetromino piece, int pieceX, int pieceY) {
        // Clears a one-block radius around the given piece
        int[][] shape = piece.getShape();
        
        // Calculate the area to clear (one block radius around the piece)
        for (int row = pieceY - 5; row <= pieceY + shape.length; row++) {
            for (int col = pieceX - 5; col <= pieceX + shape[0].length; col++) {
                // Skip if outside grid bounds
                if (row < 0 || row >= ROWS || col < 0 || col >= COLS) {
                    continue;
                }
                // Clear all cells in the area, including the piece itself
                clearCell(row, col);
            }
        }
    }

    // Method to check if the top row has any occupied cells
    public boolean isRowOccupied(int row) {
        if (row < 0) {
            // Handle buffer grid
            for (int col = 0; col < COLS; col++) {
                if (bufferGrid[row + BUFFER_ROWS][col] != 0) { // Assuming 0 represents an empty cell
                    return true; // Return true if any cell in the buffer row is occupied
                }
            }
        } else {
            // Handle main grid
            for (int col = 0; col < COLS; col++) {
                if (grid[row][col] != 0) { // Assuming 0 represents an empty cell
                    return true; // Return true if any cell in the main grid row is occupied
                }
            }
        }
        return false; // Return false if no cells in the row are occupied
    }

    public void addCheese() {
        // Create a temporary grid to store current grid state
        int[][] tempGrid = new int[Grid.ROWS][Grid.COLS];
        Color[][] tempColors = new Color[Grid.ROWS][Grid.COLS];

        int rowsToShiftUp; // Variable for rows to shift up
    
        Random rand = new Random(); // Random num generator (random blocks of cheese)
        Color cheeseColor = Color.GRAY; // Use gray color for cheese blocks
    
        // Step 1: Find the highest occupied row
        int highestOccupiedRow = -1;
        for (int row = 0; row < Grid.ROWS; row++) {
            if (isRowOccupied(row)) {
                highestOccupiedRow = row;
            }
        }
    
        // Calculate how many rows to shift up
        rowsToShiftUp = Math.max(0, highestOccupiedRow - 15);
    
        //Shift existing grid contents up by rowsToShiftUp
        for (int row = 0; row < Grid.ROWS - rowsToShiftUp; row++) {
            for (int col = 0; col < Grid.COLS; col++) {
                tempGrid[row][col] = grid[row + rowsToShiftUp][col];
                tempColors[row][col] = colors[row + rowsToShiftUp][col];
            }
        }
    
        // Clear the rows above the shifted rows
        for (int row = Grid.ROWS - rowsToShiftUp; row < Grid.ROWS; row++) {
            for (int col = 0; col < Grid.COLS; col++) {
                tempGrid[row][col] = 0;
                tempColors[row][col] = Color.BLACK;
            }
        }
    
        // Add 10 lines of cheese at the bottom
        for (int row = Grid.ROWS - 10; row < Grid.ROWS; row++) {
            // For each line, choose a random hole position
            int holePosition = rand.nextInt(Grid.COLS);
    
            // Fill the row with blocks except for the hole
            for (int col = 0; col < Grid.COLS; col++) {
                if (col != holePosition) {
                    tempGrid[row][col] = 1;
                    tempColors[row][col] = cheeseColor;
                } else {
                    tempGrid[row][col] = 0;
                    tempColors[row][col] = Color.BLACK;
                }
            }
        }
    
        // Update the original grid with the temp grid
        for (int row = 0; row < Grid.ROWS; row++) {
            for (int col = 0; col < Grid.COLS; col++) {
                grid[row][col] = tempGrid[row][col];
                colors[row][col] = tempColors[row][col];
            }
        }
    }
    
    


    public void draw(Graphics g, int offsetX, int offsetY) {
        // Draw buffer grid blocks (if they exist)
        for (int row = 0; row < BUFFER_ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (bufferGrid[row][col] != 0) {
                    g.setColor(bufferColors[row][col]);
                    g.fillRect(
                        offsetX + col * Tetromino.BLOCK_SIZE, 
                        offsetY + (row - BUFFER_ROWS) * Tetromino.BLOCK_SIZE,
                        Tetromino.BLOCK_SIZE, 
                        Tetromino.BLOCK_SIZE
                    );
                    g.drawRect(
                        offsetX + col * Tetromino.BLOCK_SIZE, 
                        offsetY + (row - BUFFER_ROWS) * Tetromino.BLOCK_SIZE,
                        Tetromino.BLOCK_SIZE, 
                        Tetromino.BLOCK_SIZE
                    );
                }
            }
        }
        
        // Draw main grid blocks
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
