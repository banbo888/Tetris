import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PieceBagGenerator {
    private List<Integer> bag = new ArrayList<>();
    private Random random = new Random();

    private static final Color[] COLORS = {
        Color.CYAN,    // I
        Color.MAGENTA, // T
        Color.YELLOW,  // O
        Color.RED,     // Z
        Color.GREEN,   // S
        Color.BLUE,    // L
        Color.ORANGE   // J
    };

    public PieceBagGenerator() {
        refillBag();
    }

    public void refillBag() {
        bag.clear();
        // Add the 7 unique pieces (0 to 6) to the bag
        for (int i = 0; i < 7; i++) {
            bag.add(i);
        }
        // Shuffle the bag so the order is random
        Collections.shuffle(bag, random);
    }

    public Tetromino getNextPiece() {
        if (bag.isEmpty()) {
            refillBag();
        }
        int index = bag.remove(0);
        return new Tetromino(index, COLORS[index]);
    }
}