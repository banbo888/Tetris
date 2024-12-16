
import java.awt.*;

public class Tetromino {
    public static final int BLOCK_SIZE = 30; // Size of each block
    private int[][] shape;
    private Color color;
    private int [][][] rotations;
    private int rotation = 0; // Current rotation state

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
    
    private static final int[][][] JLSTZ_WALL_KICKS = {
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

    public Tetromino(int index, Color color) {
        this.rotations = TETROMINOS[index];
        this.color = color;
    }

    // Rotate clockwise
    public void rotateCW() {
        rotation = (rotation + 1) % 4; // Cycle through 0, 1, 2, 3
    }

    // Rotate counter-clockwise
    public void rotateCCW() {
        rotation = (rotation + 3) % 4; // Equivalent to subtracting 1, but wraps around
    }

    public void rotateFlip(){
        rotation = (rotation + 2) % 4;
    }

    // Get the current shape based on rotation
    public int[][] getShape() {
        return rotations[rotation];
    }

    public int getShapeWidth() {
        return getShape()[0].length;
    }

    public Color getColor(){
        return color;
    }

    // Draw the tetromino
    public void draw(Graphics g, int x, int y) {
        int[][] shape = getShape();
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] == 1) {
                    g.setColor(color);
                    g.fillRect(x + col * BLOCK_SIZE, y + row * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                    g.setColor(Color.BLACK);
                    g.drawRect(x + col * BLOCK_SIZE, y + row * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                }
            }
        }
    }
}