public class Timer {
    private long startTime;
    private long endTime;
    private boolean running;

    public Timer() {
        this.running = false;
    }

    // Start the timer
    public void start() {
        startTime = System.currentTimeMillis();
        running = true;
    }

    // Stop the timer
    public void stop() {
        endTime = System.currentTimeMillis();
        running = false;
    }

    // Reset the timer
    public void reset() {
        startTime = 0;
        endTime = 0;
        running = false;
    }

    // Get the elapsed time in milliseconds
    public long getElapsedTime() {
        if (running) {
            return System.currentTimeMillis() - startTime;
        } else {
            return endTime - startTime;
        }
    }

    // Get the time remaining from 2 minutes (used for Time-Trial mode)
    public String getTimeRemaining() {
        long elapsedTime = getElapsedTime();
        long remainingTime = 120000 - elapsedTime;

        // Clamp to 0 if time has run out
        remainingTime = Math.max(remainingTime, 0);

        long minutes = (remainingTime / 60000) % 60;
        long seconds = (remainingTime / 1000) % 60;
        long milliseconds = remainingTime % 1000;

        return String.format("%02d:%02d.%03d", minutes, seconds, milliseconds);
    }

    // Format elapsed time as "MM:SS:mmm"
    public String getFormattedTime() {
        long elapsedTime = getElapsedTime();

        long minutes = (elapsedTime / 60000) % 60;   // 1 minute = 60,000 ms
        long seconds = (elapsedTime / 1000) % 60;    // 1 second = 1,000 ms
        long milliseconds = elapsedTime % 1000;

        return String.format("%02d:%02d.%03d", minutes, seconds, milliseconds);
    }

    // Change string time to long time
    public static long parseTimeToMillis(String time) {
        String[] parts = time.split(":");
        int minutes = Integer.parseInt(parts[0]);
        int seconds = Integer.parseInt(parts[1]);
        int millis = Integer.parseInt(parts[2]);
        return (minutes * 60 * 1000L) + (seconds * 1000L) + millis;
    }
}
