//ICS Summative - Tetris by Richard Xiong & Eric Ma
//Beta Program Submission
//2025-01-09
//Tetromino Class - Manages the 7 possible tetrominoes (i.e. color, rotations, width, length, pixel size).

import java.awt.*;

public class Tetromino {
    public static final int BLOCK_SIZE = 30; // Size of each block
    private Color color; // Piece color
    private int [][][] rotations; // All possible rotations of shape
    private int rotation = 0; // Current rotation state
    private int previousRotation = 0; //Previous rotation state
    private int index; // Add an index field to store the type of piece
    
    // Array that stores every rotation configuration of each piece
    public static final int[][][][] TETROMINOS = { 
        { // I-piece
            {{0, 0, 0, 0}, {1, 1, 1, 1}, {0, 0, 0, 0}, {0, 0, 0, 0}}, // 0°
            {{0, 0, 1, 0}, {0, 0, 1, 0}, {0, 0, 1, 0}, {0, 0, 1, 0}}, // 90°
            {{0, 0, 0, 0}, {0, 0, 0, 0}, {1, 1, 1, 1}, {0, 0, 0, 0}}, // 180°
            {{0, 1, 0, 0}, {0, 1, 0, 0}, {0, 1, 0, 0}, {0, 1, 0, 0}}  // 270°
        },
        { // T-piece
            {{0, 1, 0}, {1, 1, 1}, {0, 0, 0}}, // 0°
            {{0, 1, 0}, {0, 1, 1}, {0, 1, 0}}, // 90°
            {{0, 0, 0}, {1, 1, 1}, {0, 1, 0}}, // 180°
            {{0, 1, 0}, {1, 1, 0}, {0, 1, 0}}  // 270°
        },
        { // O-piece (No rotation)
            {{1, 1}, {1, 1}}, // 0°
            {{1, 1}, {1, 1}}, // 90°
            {{1, 1}, {1, 1}}, // 180°
            {{1, 1}, {1, 1}}  // 270°
        },
        { // Z-piece
            {{1, 1, 0}, {0, 1, 1}, {0, 0, 0}}, // 0°
            {{0, 0, 1}, {0, 1, 1}, {0, 1, 0}}, // 90°
            {{0, 0, 0}, {1, 1, 0}, {0, 1, 1}}, // 180°
            {{0, 1, 0}, {1, 1, 0}, {1, 0, 0}}  // 270°
        },
        { // S-piece
            {{0, 1, 1}, {1, 1, 0}, {0, 0, 0}}, // 0°
            {{0, 1, 0}, {0, 1, 1}, {0, 0, 1}}, // 90°
            {{0, 0, 0}, {0, 1, 1}, {1, 1, 0}}, // 180°
            {{1, 0, 0}, {1, 1, 0}, {0, 1, 0}}  // 270°
        },
        { // L-piece
            {{1, 0, 0}, {1, 1, 1}, {0, 0, 0}}, // 0°
            {{0, 1, 1}, {0, 1, 0}, {0, 1, 0}}, // 90°
            {{0, 0, 0}, {1, 1, 1}, {0, 0, 1}}, // 180°
            {{0, 1, 0}, {0, 1, 0}, {1, 1, 0}}  // 270°
        },
        { // J-piece
            {{0, 0, 1}, {1, 1, 1}, {0, 0, 0}}, // 0°
            {{0, 1, 0}, {0, 1, 0}, {0, 1, 1}}, // 90°
            {{0, 0, 0}, {1, 1, 1}, {1, 0, 0}}, // 180°
            {{1, 1, 0}, {0, 1, 0}, {0, 1, 0}}  // 270°
        }
    };

    // Array that stores every test for wall kicks 
    public static final int[][][] JLSTZ_WALLKICKS = {
        // 0 -> R
        {{0, 0}, {-1, 0}, {-1, 1}, {0, -2}, {-1, -2}},
        // R -> 0
        {{0, 0}, {1, 0}, {1, -1}, {0, 2}, {1, 2}},
        // R -> 2
        {{0, 0}, {1, 0}, {1, -1}, {0, 2}, {1, 2}},
        // 2 -> R
        {{0, 0}, {-1, 0}, {-1, 1}, {0, -2}, {-1, -2}},
        // 2 -> L
        {{0, 0}, {1, 0}, {1, 1}, {0, -2}, {1, -2}},
        // L -> 2
        {{0, 0}, {-1, 0}, {-1, -1}, {0, 2}, {-1, 2}},
        // L -> 0
        {{0, 0}, {-1, 0}, {-1, -1}, {0, 2}, {-1, 2}},
        // 0 -> L
        {{0, 0}, {1, 0}, {1, 1}, {0, -2}, {1, -2}}
    };

    // Array that stores every test for wall kicks for I pieces (different logic from other pieces)
    public static final int[][][] I_WALLKICKS = {
        // 0 -> R
        {{0, 0}, {-2, 0}, {1, 0}, {-2, -1}, {1, 2}},
        // R -> 0
        {{0, 0}, {2, 0}, {-1, 0}, {2, 1}, {-1, -2}},
        // R -> 2
        {{0, 0}, {-1, 0}, {2, 0}, {-1, 2}, {2, -1}},
        // 2 -> R
        {{0, 0}, {2, 0}, {-1, 0}, {2, 1}, {-1, -2}},
        // 2 -> L
        {{0, 0}, {1, 0}, {-2, 0}, {1, -2}, {-2, 1}},
        // L -> 2
        {{0, 0}, {2, 0}, {-1, 0}, {2, 1}, {-1, -2}},
        // L -> 0
        {{0, 0}, {-2, 0}, {1, 0}, {-2, -1}, {1, 2}},
        // 0 -> L
        {{0, 0}, {1, 0}, {-2, 0}, {1, -2}, {-2, 1}}
    };


    public Tetromino(int index, Color color) {
        this.index = index; // Initialize the index field
        this.rotations = TETROMINOS[index];
        this.color = color;
    }

    // Rotate clockwise
    public void rotateCW() {
        previousRotation = rotation;
        rotation = (rotation + 1) % 4; // Cycle through 0, 1, 2, 3
        
    }

    // Rotate counter-clockwise
    public void rotateCCW() {
        previousRotation = rotation;
        rotation = (rotation + 3) % 4; // Equivalent to subtracting 1, but wraps around
    }

    public void rotateFlip(){
        rotation = (rotation + 2) % 4;
    }

    public void resetRotation() {
        rotation = 0; // Reset to default rotation
    }

    // Get the current shape based on rotation
    public int[][] getShape() {
        return rotations[rotation];
    }

    // Get width of shape
    public int getShapeWidth() {
        return getShape()[0].length;
    }

    // Get color of shape
    public Color getColor(){
        return color;
    }

    // Get the current rotation state
    public int getRotationState() {
        return rotation;
    }

    // Get values of current rotation from array
    public int[][] getCurrentRotation() {
        return rotations[rotation];
    }

    // Get previous Rotation 
    public int getPreviousRotation(){
        return previousRotation;
    }

    // Add a method to get the index of the piece
    public int getIndex() {
        return index;
    }

    // Draw the tetromino
    public void draw(Graphics g, int x, int y) {
        int[][] shape = getShape();
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] == 1) {
                    g.setColor(color);
                    g.fillRect(x + col * BLOCK_SIZE, y + row * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                    g.drawRect(x + col * BLOCK_SIZE, y + row * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                }
            }
        }
    }
}
