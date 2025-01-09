//ICS Summative - Tetris by Richard Xiong & Eric Ma
//Beta Program Submission
//2025-01-09
//ScoreManager Class - Handles saving, loading, and sorting high scores for different game modes, storing them in separate files and maintaining a maximum number of high scores.

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ScoreManager {
    private static final String SPRINT_SCORES_FILE = "sprint_scores.txt"; // File for sprint game mode scores
    private static final String TIME_TRIAL_SCORES_FILE = "timetrial_scores.txt"; // File for time trial game mode scores
    private static final String CHALLENGE_SCORES_FILE = "challenge_scores.txt"; // File for challenge game mode scores
    private static final int MAX_SCORES = 10; // Max number of high scores to store

    // Sorting variables for comparing scores
    private String[] aParts, bParts;
    private long aMillis, bMillis;
    private int scoreA, scoreB;

    // Method to save a new score for a specific game mode
    public void saveScore(String gameMode, String score, String username) {
        String fileName;
        List<String> scores = new ArrayList<>();
        // Data variables
        String timestamp, entry;

        fileName = getFileNameForMode(gameMode); // Get the appropriate file name based on game mode
        scores = loadScores(fileName); // Load current scores from the file

        // If the username is empty, set it to "Anonymous"
        if (username.equals("")){
            username = "Anonymous";
        }

        timestamp = new SimpleDateFormat("yyyy-MM-dd").format(new Date()); // Get the current date
        entry = score + "," + username + "," + timestamp; // Combine the score, username, and timestamp into a single entry

        scores.add(entry); // Add the new score entry

        // Sort the scores list based on the game mode (either by time or score)
        Collections.sort(scores, (a, b) -> {
            aParts = a.split(",");
            bParts = b.split(",");

            if (gameMode.equals("GAME_SPRINT")) {
                // For sprint game mode, compare by time (in milliseconds)
                aMillis = parseTimeToMillis(aParts[0]);
                bMillis = parseTimeToMillis(bParts[0]);
                return Long.compare(aMillis, bMillis); // Return comparison based on time
            } else {
                // For other game modes, compare by score (higher is better)
                scoreA = Integer.parseInt(aParts[0].replace(",", ""));
                scoreB = Integer.parseInt(bParts[0].replace(",", ""));
                return Integer.compare(scoreB, scoreA); // Return comparison based on score
            }
        });

        // Ensure only the top MAX_SCORES scores are kept
        if (scores.size() > MAX_SCORES) {
            scores = scores.subList(0, MAX_SCORES); // Trim the list to the top scores
        }

        // Write the updated scores back to the file
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            for (String s : scores) {
                writer.println(s); // Write each score entry
            }
        } catch (IOException e) {
            System.err.println("Error saving scores: " + e.getMessage()); // Handle any errors during file writing
        }
    }

    // Method to get the list of scores for a specific game mode
    public List<String> getScores(String gameMode) {
        return loadScores(getFileNameForMode(gameMode)); // Load the scores from the appropriate file
    }

    // Method to get a specific component (score, username, date) of the nth score entry
    public String getEntryComponent(String gameMode, int nthScore, String component) {
        List<String> scores = new ArrayList<>();
        String entry;
        String [] parts;

        scores = getScores(gameMode); // Get the scores for the game mode
        if (scores.isEmpty() || nthScore < 1 || nthScore > scores.size()) {
            return "---"; // Return "---" if the score list is empty or nthScore is out of bounds
        }

        entry = scores.get(nthScore - 1); // Get the nth score entry
        parts = entry.split(","); // Split the entry into its components (score, username, date)

        switch (component.toLowerCase()) {
            case "score":
                return parts[0]; // Return the score part of the entry
            case "username":
                return parts[1]; // Return the username part of the entry
            case "date":
                return parts[2]; // Return the date part of the entry
            default:
                throw new IllegalArgumentException("Invalid component: " + component); // Handle invalid component requests
        }
    }

    // Method to check if a score qualifies as a high score
    public boolean isHighScore(String result, String previousState) {
        List<String> scores = new ArrayList<>();
        String worstScore;
        int newScoreValue, worstScoreValue;

        scores = getScores(previousState); // Get the current scores for the game mode
        if (scores.size() < MAX_SCORES) {
            return true; // If there are fewer than MAX_SCORES, the new score qualifies
        }
        worstScore = scores.get(scores.size() - 1).split(",")[0]; // Get the worst (lowest) score

        if (previousState.equals("GAME_SPRINT")) {
            // For sprint game mode, compare by time (lower time is better)
            return parseTimeToMillis(result) < parseTimeToMillis(worstScore); 
        } else {
            // For other game modes, compare by score (higher score is better)
            newScoreValue = Integer.parseInt(result.replace(",", ""));
            worstScoreValue = Integer.parseInt(worstScore.replace(",", ""));
            return newScoreValue > worstScoreValue;
        }
    }

    // Method to load scores from a file
    private List<String> loadScores(String fileName) {
        List<String> scores = new ArrayList<>();
        String line;

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            while ((line = reader.readLine()) != null) {
                scores.add(line.trim()); // Read each line (score entry) and add to the list
            }
        } catch (IOException e) {
            return scores; // If there's an error reading the file, return the empty list
        }
        return scores; // Return the list of scores
    }

    // Method to get the file name based on the game mode
    private String getFileNameForMode(String gameMode) {
        switch (gameMode) {
            case "GAME_SPRINT":
                return SPRINT_SCORES_FILE;
            case "GAME_TIMETRIAL":
                return TIME_TRIAL_SCORES_FILE;
            case "GAME_CHALLENGE":
                return CHALLENGE_SCORES_FILE;
            default:
                throw new IllegalArgumentException("Invalid game mode: " + gameMode); // Handle invalid game mode requests
        }
    }

    // Method to convert a time string in the format "MM:SS.MMM" to milliseconds
    public long parseTimeToMillis(String time) {
        int minutes, seconds, millis;
        String[] parts, secondParts;

        parts = time.split(":"); // Split time into minutes and seconds
        minutes = Integer.parseInt(parts[0]);
        secondParts = parts[1].split("\\."); // Split seconds into seconds and milliseconds
        seconds = Integer.parseInt(secondParts[0]);
        millis = Integer.parseInt(secondParts[1]);

        // Return the total time in milliseconds
        return (minutes * 60 * 1000L) + (seconds * 1000L) + millis;
    }
}
