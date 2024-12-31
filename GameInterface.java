import java.awt.*;
import java.util.Queue;

public class GameInterface {
    private static final int BLOCK_SIZE = Tetromino.BLOCK_SIZE;
    private int TOP_PANEL_HEIGHT;
    private int GRID_OFFSET_X;
    private Queue<Tetromino> pieceQueue;
    private Tetromino heldPiece;
    private Tetromino currentPiece;
    private int pieceX;
    private int pieceY;
    private String gameState;
    private boolean isCountingDown;

    public GameInterface(int gridOffsetX, int topPanelHeight) {
        this.GRID_OFFSET_X = gridOffsetX;
        this.TOP_PANEL_HEIGHT = topPanelHeight;
    }

    public void updateState(Queue<Tetromino> pieceQueue, Tetromino heldPiece, Tetromino currentPiece, int pieceX, int pieceY, String gameState, boolean isCountingDown) {
        this.pieceQueue = pieceQueue;
        this.heldPiece = heldPiece;
        this.currentPiece = currentPiece;
        this.pieceX = pieceX;
        this.pieceY = pieceY;
        this.gameState = gameState;
        this.isCountingDown = isCountingDown;
    }

    public void drawStats(Graphics g, String timeElapsed, int piecesPlaced, int linesCleared) {
        String timeString;
        String mainTime;
        String milliseconds;
        int linesRemaining;
        double piecePerSecond;
        double timeInSeconds;
        int posX = 100;
        int posY = 625; // Align with held pieces

        if(!isCountingDown){
            timeString = timeElapsed; // Get current time
        }
        else{
            timeString = "00:00.000"; // Display 0 if game did not start
        }

        // Get lines remaining
        if(linesCleared <= 40){
            linesRemaining = 40 - linesCleared;
        }
        else{
            linesRemaining = 0;
        }

        // Calculate pieces placed per second
        timeInSeconds = convertTimeStringToSeconds(timeString);
        if(!isCountingDown){
            piecePerSecond = piecesPlaced / timeInSeconds;
        }
        else{
            piecePerSecond = 0;
        }

        
        // Split the time string into two parts: main time and milliseconds
        mainTime = timeString.substring(0, timeString.lastIndexOf('.') + 1); // "00:00."
        milliseconds = timeString.substring(timeString.lastIndexOf('.') + 1); // "000"
    
        // Draw "TIME:" label
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 25)); // Smaller font for the label
        g.drawString("TIME:", posX, posY - 50);
    
        // Draw the main time part
        g.setFont(new Font("SansSerif", Font.BOLD, 40)); // Larger font for main time
        FontMetrics fmMain = g.getFontMetrics();
        int mainTimeWidth = fmMain.stringWidth(mainTime); // Get width of main time string
        g.drawString(mainTime, posX, posY);
    
        // Draw the milliseconds part
        g.setFont(new Font("SansSerif", Font.PLAIN, 30)); // Smaller font for milliseconds
        g.drawString(milliseconds, posX + mainTimeWidth, posY); // Offset by the width of main time

        // Draw "LINES LEFT:" label
        g.setFont(new Font("SansSerif", Font.BOLD, 25)); // Smaller font for the label
        g.drawString("LINES LEFT:", posX, posY - 150);

        // Draw lines remaining counter
        g.setFont(new Font("SansSerif", Font.BOLD, 40)); // Larger font for main time
        g.drawString(String.valueOf(linesRemaining), posX, posY - 100);

        //Draw "PIECES PLACED:" label
        g.setFont(new Font("SansSerif", Font.BOLD, 25)); // Smaller font for the label
        g.drawString("PIECES:", posX, posY - 250);

        //Draw pieces placed counter
        g.setFont(new Font("SansSerif", Font.BOLD, 40)); // Larger font for main time
        FontMetrics fmPiecesPlaced = g.getFontMetrics();
        int piecesCounterWidth = fmPiecesPlaced.stringWidth(String.valueOf(piecesPlaced)); // Get width of main time string
        g.drawString(String.valueOf(piecesPlaced), posX, posY - 200);

        // Draw pieces placed per second
        g.setFont(new Font("SansSerif", Font.PLAIN, 30)); // Smaller font for PPS
        g.drawString(String.format("%.2f", piecePerSecond) + "/S", posX + piecesCounterWidth + 20, posY - 200);
        
    }

    public void drawQueue(Graphics g) {
        Tetromino[] piecesArray;
        int startX = GRID_OFFSET_X + 300 + 30; // Right side panel
        int startY = 120;              // Start Y position for the first piece in the queue
        int spacing = 100;            // Vertical spacing between queued pieces
    
        // Draw the "Next Pieces" label
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 25));
        g.drawString("NEXT", startX, startY - 20);
    
        // Draw the next pieces in the queue
        piecesArray = pieceQueue.toArray(new Tetromino[0]);
        for (int i = 0; i < piecesArray.length; i++) {
            piecesArray[i].draw(g, startX, startY + (i * spacing));
        }
    }

    public void drawHeldPiece(Graphics g) {
        int startX = 100; // Left side panel
        int startY = 120;

        // Draw "Held Piece" label
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 25));
        g.drawString("HOLD", startX, startY - 20);

        // Draw the held piece (if any)
        if (heldPiece != null) {
            heldPiece.draw(g, startX, startY);
        }
    }

    public void drawGhostPiece(Graphics g, Grid grid) {
        int ghostY = pieceY;
        
        // Find the lowest valid position for the ghost piece
        while (!grid.checkCollision(currentPiece, pieceX, ghostY + 1)) {
            ghostY++;
        }
        
        // Draw the ghost piece with reduced opacity
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        
        currentPiece.draw(g2d, 
            GRID_OFFSET_X + pieceX * BLOCK_SIZE, 
            TOP_PANEL_HEIGHT + ghostY * BLOCK_SIZE
        );
        
        g2d.dispose();
    }

    private double convertTimeStringToSeconds(String timeString) {
        String[] parts = timeString.split("[:.]");
        int minutes = Integer.parseInt(parts[0]);
        int seconds = Integer.parseInt(parts[1]);
        int milliseconds = Integer.parseInt(parts[2]);

        return minutes * 60 + seconds + milliseconds / 1000.0;
    }

}
