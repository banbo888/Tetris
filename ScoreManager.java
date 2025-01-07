import java.io.*;
import java.util.*;

public class ScoreManager {
    // File names for storing scores of different game modes
    private static final String SPRINT_SCORES_FILE = "sprint_scores.txt";
    private static final String TIME_TRIAL_SCORES_FILE = "timetrial_scores.txt";
    private static final String CHALLENGE_SCORES_FILE = "challenge_scores.txt";
    private static final int MAX_SCORES = 10;  // Maximum number of scores to keep in the file

    // Save a new score for the specified game mode
    public void saveScore(String gameMode, String score) {
        // Determine the appropriate file for the given game mode
        String fileName = getFileNameForMode(gameMode);
        // Load existing scores from the file
        List<String> scores = loadScores(fileName);

        // Add the new score to the list
        scores.add(score);

        // Sort scores: ascending for Sprint (faster times are better), descending for others
        Collections.sort(scores, (a, b) -> {
            if (gameMode.equals("GAME_SPRINT")) {
                long aMillis = parseTimeToMillis(a);
                long bMillis = parseTimeToMillis(b);
                return Long.compare(aMillis, bMillis);
            } else {
                // Remove commas before parsing
                int scoreA = Integer.parseInt(a.replace(",", ""));
                int scoreB = Integer.parseInt(b.replace(",", ""));
                return Integer.compare(scoreB, scoreA);
            }
        });

        // Keep only the top MAX_SCORES scores
        if (scores.size() > MAX_SCORES) {
            scores = scores.subList(0, MAX_SCORES);
        }

        // Write the updated scores back to the file
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            for (String s : scores) {
                writer.println(s);  // Write each score on a new line
            }
        } catch (IOException e) {
            System.err.println("Error saving scores: " + e.getMessage());  // Handle file writing errors
        }
    }

    // Load all scores for the specified game mode
    public List<String> getScores(String gameMode) {
        // Load scores from the file corresponding to the game mode
        return loadScores(getFileNameForMode(gameMode));
    }

    // Get the nth score for the specified game mode
    public String getScore(String gameMode, int nthScore) {
        // Retrieve the list of scores for the given game mode
        List<String> scores = getScores(gameMode);
        
        // Check if the requested position is valid (1-based indexing)
        if (scores.isEmpty() || nthScore < 1 || nthScore > scores.size()) {
            return "---"; // Return placeholder if position doesn't exist
        }
        
        // Return the score at the requested position (converting from 1-based to 0-based indexing)
        return scores.get(nthScore - 1);
    }

    // Check if score is top 10
    public boolean isHighScore(String result, String previousState) {
        List<String> scores = getScores(previousState);

        // If less than 10 scores, score is automatically top 10
        if (scores.size() < MAX_SCORES) {
            return true;
        }

        // Get the worst score in top 10 (last element)
        String worstScore = scores.get(scores.size() - 1);
        
        // Compare based on game mode
        if (previousState.equals("GAME_SPRINT")) {
            // For sprint, lower times are better
            return parseTimeToMillis(result) < parseTimeToMillis(worstScore);
        } else {
            // For other modes, higher scores are better
            int newScoreValue = Integer.parseInt(result.replace(",", ""));
            int worstScoreValue = Integer.parseInt(worstScore.replace(",", ""));
            return newScoreValue > worstScoreValue;
        }
    }

    // Helper method to load scores from a file
    private List<String> loadScores(String fileName) {
        List<String> scores = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                scores.add(line.trim());
            }
        } catch (IOException e) {
            // File doesn't exist or an error occurred - return an empty list
            return scores;
        }
        return scores;
    }

    // Helper method to get the appropriate file name for each game mode
    private String getFileNameForMode(String gameMode) {
        switch (gameMode) {
            case "GAME_SPRINT":
                return SPRINT_SCORES_FILE;
            case "GAME_TIMETRIAL":
                return TIME_TRIAL_SCORES_FILE;
            case "GAME_CHALLENGE":
                return CHALLENGE_SCORES_FILE;
            default:
                throw new IllegalArgumentException("Invalid game mode: " + gameMode);
        }
    }

    public long parseTimeToMillis(String time) {
        String[] parts = time.split(":");
        int minutes = Integer.parseInt(parts[0]);
        // For the seconds and milliseconds part, we need to handle decimals
        String[] secondParts = parts[1].split("\\.");
        int seconds = Integer.parseInt(secondParts[0]);
        int millis = Integer.parseInt(secondParts[1]);
        
        return (minutes * 60 * 1000L) + (seconds * 1000L) + millis;
    }
}
