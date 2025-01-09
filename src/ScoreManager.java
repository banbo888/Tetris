import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ScoreManager {
    private static final String SPRINT_SCORES_FILE = "sprint_scores.txt";
    private static final String TIME_TRIAL_SCORES_FILE = "timetrial_scores.txt";
    private static final String CHALLENGE_SCORES_FILE = "challenge_scores.txt";
    private static final int MAX_SCORES = 10;

    // Sorting variables
    private String[] aParts, bParts;
    private long aMillis, bMillis;
    private int scoreA, scoreB;


    public void saveScore(String gameMode, String score, String username) {
        String fileName;
        List<String> scores = new ArrayList<>();
        // Data variables
        String timestamp, entry;


        fileName = getFileNameForMode(gameMode);
        scores = loadScores(fileName);

        // Set username if empty
        if (username.equals("")){
            username = "Anonymous";
        }

        timestamp = new SimpleDateFormat("yyyy-MM-dd").format(new Date()); // Get current date and time
        entry = score + "," + username + "," + timestamp; // Put together the different data

        scores.add(entry);

        Collections.sort(scores, (a, b) -> {
            aParts = a.split(",");
            bParts = b.split(",");
            
            if (gameMode.equals("GAME_SPRINT")) {
                aMillis = parseTimeToMillis(aParts[0]);
                bMillis = parseTimeToMillis(bParts[0]);
                return Long.compare(aMillis, bMillis);
            } else {
                scoreA = Integer.parseInt(aParts[0].replace(",", ""));
                scoreB = Integer.parseInt(bParts[0].replace(",", ""));
                return Integer.compare(scoreB, scoreA);
            }
        });

        if (scores.size() > MAX_SCORES) {
            scores = scores.subList(0, MAX_SCORES);
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            for (String s : scores) {
                writer.println(s);
            }
        } catch (IOException e) {
            System.err.println("Error saving scores: " + e.getMessage());
        }
    }

    public List<String> getScores(String gameMode) {
        return loadScores(getFileNameForMode(gameMode));
    }

    public String getEntryComponent(String gameMode, int nthScore, String component) {
        List<String> scores = new ArrayList<>();
        String entry;
        String [] parts;
        
        scores = getScores(gameMode);
        if (scores.isEmpty() || nthScore < 1 || nthScore > scores.size()) {
            return "---";
        }

        entry = scores.get(nthScore - 1);
        parts = entry.split(",");

        switch (component.toLowerCase()) {
            case "score":
                return parts[0];
            case "username":
                return parts[1];
            case "date":
                return parts[2];
            default:
                throw new IllegalArgumentException("Invalid component: " + component);
        }
    }

    public boolean isHighScore(String result, String previousState) {
        List<String> scores = new ArrayList<>();
        String worstScore;
        int newScoreValue, worstScoreValue;

        scores = getScores(previousState);
        if (scores.size() < MAX_SCORES) {
            return true;
        }
        worstScore = scores.get(scores.size() - 1).split(",")[0];

        if (previousState.equals("GAME_SPRINT")) {
            return parseTimeToMillis(result) < parseTimeToMillis(worstScore);
        } else {
            newScoreValue = Integer.parseInt(result.replace(",", ""));
            worstScoreValue = Integer.parseInt(worstScore.replace(",", ""));
            return newScoreValue > worstScoreValue;
        }
    }

    private List<String> loadScores(String fileName) {
        List<String> scores = new ArrayList<>();
        String line;

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            while ((line = reader.readLine()) != null) {
                scores.add(line.trim());
            }
        } catch (IOException e) {
            return scores;
        }
        return scores;
    }

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
        int minutes, seconds, millis;
        String[] parts, secondParts;

        parts = time.split(":");
        minutes = Integer.parseInt(parts[0]);
        secondParts = parts[1].split("\\.");
        seconds = Integer.parseInt(secondParts[0]);
        millis = Integer.parseInt(secondParts[1]);

        return (minutes * 60 * 1000L) + (seconds * 1000L) + millis;
    }
}
