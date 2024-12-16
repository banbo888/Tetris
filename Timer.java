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

    // Format elapsed time as "MM:SS:mmm"
    public String getFormattedTime() {
        long elapsedTime = getElapsedTime();

        long minutes = (elapsedTime / 60000) % 60;   // 1 minute = 60,000 ms
        long seconds = (elapsedTime / 1000) % 60;    // 1 second = 1,000 ms
        long milliseconds = elapsedTime % 1000;

        return String.format("%02d:%02d:%03d", minutes, seconds, milliseconds);
    }
}
