import java.awt.*;
import java.util.Queue;
import javax.swing.Timer;

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

    //Action text
    private String actionText = "";
    private String tSpinText = "";
    private String pcText = "";
    private String backToBackText = "";
    private String comboString = "";

    // Add fields for fade effect
    private Timer fadeTimer;
    private float fadeAlpha = 0.0f;
    private static final int FADE_INTERVAL = 75;

    public GameInterface(int gridOffsetX, int topPanelHeight) {
        this.GRID_OFFSET_X = gridOffsetX;
        this.TOP_PANEL_HEIGHT = topPanelHeight;

        //Initialize the fade timer
        fadeTimer = new Timer(FADE_INTERVAL, e -> {
            fadeAlpha -= 0.02f;
            if (fadeAlpha <= 0) {
                fadeAlpha = 0;
                actionText = "";
                ((Timer) e.getSource()).stop(); // Stop the timer when fadeAlpha is 0
            }
        });
        fadeTimer.setRepeats(true);
        
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

    public void drawStats(Graphics g, String timeElapsedFormatted, String timeRemaining, long timeElapsedMiliseconds, int piecesPlaced, int linesCleared) {
        String timeString;
        String mainTime;
        String milliseconds;
        int linesRemaining;
        double piecePerSecond;
        double timeInSeconds;
        int posX = 100;
        int posY = 625; // Align with held pieces

        if(!isCountingDown){
            if(gameState.equals("GAME_SPRINT")){
                timeString = timeElapsedFormatted; // Get current time
            }
            else if(gameState.equals("GAME_TIMETRIAL")){
                timeString = timeRemaining; // Get time remaining
            }
            else{
                timeString = timeElapsedFormatted; // Get current time
            }
        }
        else{
            if(gameState.equals("GAME_SPRINT")){
                timeString = "00:00.000"; // Display 0 if game did not start
            }
            else if(gameState.equals("GAME_TIMETRIAL")){
                timeString = "02:00.000"; // Display 2 minutes if game did not start (time remaining)
            }
            else{
            timeString = "00:00.000"; // Display 0 if game did not start
            }
        }

        // Get lines remaining
        if(linesCleared <= 40){
            linesRemaining = 40 - linesCleared;
        }
        else{
            linesRemaining = 0;
        }

        // Calculate pieces placed per second
        timeInSeconds = timeElapsedMiliseconds / 1000.0;
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
        g.setFont(new Font("SansSerif", Font.BOLD, 20)); // Smaller font for the label
        g.drawString("TIME:", posX, posY - 40);
    
        // Draw the main time part
        g.setFont(new Font("SansSerif", Font.BOLD, 35)); // Larger font for main time
        FontMetrics fmMain = g.getFontMetrics();
        int mainTimeWidth = fmMain.stringWidth(mainTime); // Get width of main time string
        g.drawString(mainTime, posX, posY);
    
        // Draw the milliseconds part
        g.setFont(new Font("SansSerif", Font.PLAIN, 25)); // Smaller font for milliseconds
        g.drawString(milliseconds, posX + mainTimeWidth, posY); // Offset by the width of main time

        // Draw "LINES LEFT:" label
        g.setFont(new Font("SansSerif", Font.BOLD, 20)); // Smaller font for the label
        g.drawString("LINES LEFT:", posX, posY - 140);

        // Draw lines remaining counter
        g.setFont(new Font("SansSerif", Font.BOLD, 35)); // Larger font for main time
        g.drawString(String.valueOf(linesRemaining), posX, posY - 95);

        //Draw "PIECES PLACED:" label
        g.setFont(new Font("SansSerif", Font.BOLD, 20)); // Smaller font for the label
        g.drawString("PIECES:", posX, posY - 245);

        //Draw pieces placed counter
        g.setFont(new Font("SansSerif", Font.BOLD, 35)); // Larger font for main time
        FontMetrics fmPiecesPlaced = g.getFontMetrics();
        int piecesCounterWidth = fmPiecesPlaced.stringWidth(String.valueOf(piecesPlaced)); // Get width of main time string
        g.drawString(String.valueOf(piecesPlaced), posX, posY - 195);

        // Draw pieces placed per second
        g.setFont(new Font("SansSerif", Font.PLAIN, 25)); // Smaller font for PPS
        g.drawString(String.format("%.2f", piecePerSecond) + "/S", posX + piecesCounterWidth + 20, posY - 195);
        
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

    public void drawGhostPiece(Graphics g, Grid grid, int ghostVisibility) {
        int ghostY = pieceY;
        
        // Find the lowest valid position for the ghost piece
        while (!grid.checkCollision(currentPiece, pieceX, ghostY + 1)) {
            ghostY++;
        }
        
        // Draw the ghost piece with reduced opacity
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, ghostVisibility/100.0f));
        
        currentPiece.draw(g2d, 
            GRID_OFFSET_X + pieceX * BLOCK_SIZE, 
            TOP_PANEL_HEIGHT + ghostY * BLOCK_SIZE
        );
        
        g2d.dispose();
    }

    public void triggerActionText(int linesCleared, boolean isTSpin, boolean isGridEmpty, int backToBackCounter, int comboCounter) {
        // Determine new action text 

        switch (linesCleared){
            case 1:
                actionText = "SINGLE";
                break;
            case 2:
                actionText = "DOUBLE";
                break;
            case 3:
                actionText = "TRIPLE";
                break;
            case 4:
                actionText = "QUAD";
                break;
            default:
                actionText = "";
                break;
        }

        //Tspin action text
        if (isTSpin){
            tSpinText = "T-SPIN";
        }
        else{
            tSpinText = "";
        }

        //All clear action text
        if(isGridEmpty){
            pcText = "ALL CLEAR!";
        }
        else {
            pcText = "";
        }

        //Back to back action text
        if(backToBackCounter == 2){
            backToBackText = "B2B";
        }
        else if(backToBackCounter > 2){
            backToBackText = "B2B x" + (backToBackCounter - 1);
        }
        else{
            backToBackText = "";
        }
    
        //Combo action text
        if(comboCounter >= 2){
            comboString = String.valueOf(comboCounter - 1) + " COMBO";
        }
        else{
            comboString = "";
        }

        if (!actionText.isEmpty()) {
            fadeAlpha = 1.0f; // Reset fade effect
            fadeTimer.start(); // Start fade timer
        }
    }

    public void drawActionText(Graphics g) {
        int posX = 100;
        int posY = 615;
    
        // Only draw if fadeAlpha is greater than 0
        if (fadeAlpha > 0 && actionText != null && !actionText.isEmpty()) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha));
            g2d.setFont(new Font("SansSerif", Font.BOLD, 35));
            g2d.setColor(Color.WHITE);
            g2d.drawString(actionText, posX, posY - 350);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 25));
            g2d.setColor(Color.MAGENTA);
            g2d.drawString(tSpinText, posX, posY - 390);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 45));
            g2d.setColor(new Color(255, 234,0));
            g2d.drawString(pcText, posX + 220, posY - 360);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 30));
            g2d.setColor(Color.CYAN);
            g2d.drawString(backToBackText, posX, posY - 310);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 30));
            g2d.setColor(Color.YELLOW);
            g2d.drawString(comboString, posX, posY - 270);

            
            g2d.dispose();
        }
    }
}
