//ICS Summative - Tetris by Richard Xiong & Eric Ma
//Beta Program Submission
//2025-01-09
//Grid Class - Manages the Tetris grid, including the background, tetrominoes, and line clearing mechanisms.

import java.awt.*;

public class Grid {
    public static final int ROWS = 20; // Number of rows in the grid
    public static final int COLS = 10; // Number of columns in the grid
    private int[][] grid; // 2D array to store grid data (0 for empty, 1 for filled)
    private Color[][] colors; // 2D array to store the colors of the grid cells
    public int linesCleared = 0; // Tracks the number of lines cleared
    public int piecesPlaced = 0; // Tracks the number of pieces placed

    public Grid() {
        // Initializes the grid and colors arrays, setting all cells to empty and black
        grid = new int[ROWS][COLS];
        colors = new Color[ROWS][COLS];
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                grid[row][col] = 0; // 0 means empty
                colors[row][col] = Color.BLACK; // Black represents an empty cell
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
        // Checks if the given piece collides with the grid at the specified position (x, y)
        int[][] shape = piece.getShape();
        int newX, newY;

        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] == 1) { // If part of the piece
                    newX = x + col;
                    newY = y + row;
    
                    // Ensure newX and newY are within grid bounds
                    if (newX < 0 || newX >= COLS || newY >= ROWS) {
                        return true; // Collision if outside grid bounds
                    }
    
                    // Check for collision with other placed pieces
                    if (newY >= 0 && grid[newY][newX] == 1) {
                        return true; // Collision if there's already a piece in the cell
                    }
                }
            }
        }
        return false; // No collision
    }

    public void clearFullLines() {
        // Clears any full lines in the grid
        boolean full;
        
        for (int row = 0; row < ROWS; row++) {
            full = true; // Assume the line is full
            for (int col = 0; col < COLS; col++) {
                if (grid[row][col] == 0) { // If there's any empty cell, the line is not full
                    full = false;
                    break;
                }
            }
            if (full) {
                // Shift all rows above this line down by one
                for (int i = row; i > 0; i--) {
                    grid[i] = grid[i - 1].clone(); // Copy the previous row
                    colors[i] = colors[i - 1].clone(); // Copy the colors of the previous row
                }
                linesCleared++; // Increment the lines cleared count
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
        // Clears a specific cell on the grid (sets it to empty and black)
        if (row >= 0 && row < ROWS && col >= 0 && col < COLS) { // Ensure within grid bounds
            grid[row][col] = 0; // Mark the cell as empty
            colors[row][col] = Color.BLACK; // Set the cell color to black
        }
    }

    public void clearSurroundingBlocks(Tetromino piece, int pieceX, int pieceY) {
        // Clears a one-block radius around the given piece
        int[][] shape = piece.getShape();
        
        // Calculate the area to clear (one block radius around the piece)
        for (int row = pieceY - 1; row <= pieceY + shape.length; row++) {
            for (int col = pieceX - 1; col <= pieceX + shape[0].length; col++) {
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
    public boolean isTopRowOccupied() {
        for (int col = 0; col < COLS; col++) {
            if (grid[0][col] != 0) { // Assuming 0 represents an empty cell
                return true; // Return true if any cell in the top row is occupied
            }
        }
        return false; // Return false if no cells in the top row are occupied
    }

    public void draw(Graphics g, int offsetX, int offsetY) {
        // Draws the entire grid, including the filled cells and their colors
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (grid[row][col] != 0) { // Only draw filled cells
                    g.setColor(colors[row][col]); // Set the color of the cell
                    g.fillRect(
                        offsetX + col * Tetromino.BLOCK_SIZE, // Calculate the x position for the block
                        offsetY + row * Tetromino.BLOCK_SIZE, // Calculate the y position for the block
                        Tetromino.BLOCK_SIZE, // Set the width and height of the block
                        Tetromino.BLOCK_SIZE
                    );
                    g.drawRect(
                        offsetX + col * Tetromino.BLOCK_SIZE, // Draw the border of the block
                        offsetY + row * Tetromino.BLOCK_SIZE, 
                        Tetromino.BLOCK_SIZE, 
                        Tetromino.BLOCK_SIZE
                    );
                }
            }
        }
    }
}
