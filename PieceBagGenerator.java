//ICS Summative - Tetris by Richard Xiong & Eric Ma
//Beta Program Submission
//2025-01-09
//PieceBagGenerator Class - Randomly generates a piece that goes into the piece queue during the game.

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PieceBagGenerator {
    private List<Integer> bag = new ArrayList<>(); // List to hold the piece identifiers (0 to 6)
    private Random random = new Random(); // Random object for shuffling the bag
    private static final Color[] COLORS = {
        Color.CYAN,    // I piece
        Color.MAGENTA, // T piece
        Color.YELLOW,  // O piece
        Color.RED,     // Z piece
        Color.GREEN,   // S piece
        Color.BLUE,    // L piece
        Color.ORANGE   // J piece
    };

    // Constructor that initializes the piece bag with a shuffled set of pieces
    public PieceBagGenerator() {
        refillBag(); // Call refillBag to initialize the bag with a shuffled set of pieces
    }

    // Method to refill the bag with pieces (0-6), then shuffle them to randomize the order
    public void refillBag() {
        bag.clear(); // Clear any previous pieces in the bag

        // Add the 7 unique pieces (0 to 6) to the bag
        for (int i = 0; i < 7; i++) {
            bag.add(i);
        }

        // Shuffle the bag so the order of pieces is random each time
        Collections.shuffle(bag, random);
    }

    // Method to get the next piece from the bag
    public Tetromino getNextPiece() {
        int index;

        // If the bag is empty, refill it with a new set of pieces
        if (bag.isEmpty()) {
            refillBag();
        }

        // Remove and get the first piece from the bag
        index = bag.remove(0);

        // Return a new Tetromino object based on the index and its corresponding color
        return new Tetromino(index, COLORS[index]);
    }
}
