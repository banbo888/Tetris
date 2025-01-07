import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ScoreManager {
    private static final String SPRINT_SCORES_FILE = "sprint_scores.txt";
    private static final String TIME_TRIAL_SCORES_FILE = "timetrial_scores.txt";
    private static final String CHALLENGE_SCORES_FILE = "challenge_scores.txt";
    private static final int MAX_SCORES = 10;

    public void saveScore(String gameMode, String score, String username) {
        String fileName = getFileNameForMode(gameMode);
        List<String> scores = loadScores(fileName);

        // Get current date and time
        String timestamp = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String entry = score + "," + username + "," + timestamp;

        scores.add(entry);

        Collections.sort(scores, (a, b) -> {
            String[] aParts = a.split(",");
            String[] bParts = b.split(",");
            
            if (gameMode.equals("GAME_SPRINT")) {
                long aMillis = parseTimeToMillis(aParts[0]);
                long bMillis = parseTimeToMillis(bParts[0]);
                return Long.compare(aMillis, bMillis);
            } else {
                int scoreA = Integer.parseInt(aParts[0].replace(",", ""));
                int scoreB = Integer.parseInt(bParts[0].replace(",", ""));
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
        List<String> scores = getScores(gameMode);
        if (scores.isEmpty() || nthScore < 1 || nthScore > scores.size()) {
            return "---";
        }

        String entry = scores.get(nthScore - 1);
        String[] parts = entry.split(",");

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
        List<String> scores = getScores(previousState);
        if (scores.size() < MAX_SCORES) {
            return true;
        }
        String worstScore = scores.get(scores.size() - 1).split(",")[0];

        if (previousState.equals("GAME_SPRINT")) {
            return parseTimeToMillis(result) < parseTimeToMillis(worstScore);
        } else {
            int newScoreValue = Integer.parseInt(result.replace(",", ""));
            int worstScoreValue = Integer.parseInt(worstScore.replace(",", ""));
            return newScoreValue > worstScoreValue;
        }
    }

    private List<String> loadScores(String fileName) {
        List<String> scores = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
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
        String[] parts = time.split(":");
        int minutes = Integer.parseInt(parts[0]);
        String[] secondParts = parts[1].split("\\.");
        int seconds = Integer.parseInt(secondParts[0]);
        int millis = Integer.parseInt(secondParts[1]);

        return (minutes * 60 * 1000L) + (seconds * 1000L) + millis;
    }
}
