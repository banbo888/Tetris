//ICS Summative - Tetris by Richard Xiong & Eric Ma
//Beta Program Submission
//2025-01-09
//Timer Class - Provides functionality to start, stop, reset, and measure elapsed time, as well as format the elapsed time and calculate the remaining time.

public class Timer {
    private long startTime;  // Variable to store the start time in milliseconds
    private long endTime;    // Variable to store the end time in milliseconds
    private boolean running; // Flag to check if the timer is currently running

    // Constructor to initialize the timer, setting the 'running' flag to false
    public Timer() {
        this.running = false;
    }

    // Start the timer by setting the start time to the current system time
    public void start() {
        startTime = System.currentTimeMillis();  // Get current system time in milliseconds
        running = true;  // Set the running flag to true to indicate the timer is active
    }

    // Stop the timer by setting the end time to the current system time
    public void stop() {
        endTime = System.currentTimeMillis();  // Get the current system time when the timer is stopped
        running = false;  // Set the running flag to false to indicate the timer is inactive
    }

    // Reset the timer, clearing both start and end times and setting the running flag to false
    public void reset() {
        startTime = 0;  // Reset start time
        endTime = 0;    // Reset end time
        running = false; // Set running flag to false
    }

    // Get the elapsed time in milliseconds
    public long getElapsedTime() {
        // If the timer is running, return the time difference between now and the start time
        if (running) {
            return System.currentTimeMillis() - startTime;
        } else {
            return endTime - startTime;  // Otherwise, return the difference between start and end times
        }
    }

    // Get the time remaining from a 2-minute countdown (Time-Trial mode)
    public String getTimeRemaining() {
        long elapsedTime, remainingTime, minutes, seconds, milliseconds;

        elapsedTime = getElapsedTime();  // Get the elapsed time
        remainingTime = 120000 - elapsedTime; // Calculate the remaining time (2 minutes = 120000 ms)

        // Ensure remaining time is not negative by clamping it to 0
        remainingTime = Math.max(remainingTime, 0);

        // Convert remaining time into minutes, seconds, and milliseconds
        minutes = (remainingTime / 60000) % 60;  // Calculate minutes
        seconds = (remainingTime / 1000) % 60;  // Calculate seconds
        milliseconds = remainingTime % 1000;   // Calculate milliseconds

        // Return the time remaining in MM:SS.mmm format
        return String.format("%02d:%02d.%03d", minutes, seconds, milliseconds);
    }

    // Format elapsed time as "MM:SS.mmm" for easy display
    public String getFormattedTime() {
        long elapsedTime, minutes, seconds, milliseconds;

        elapsedTime = getElapsedTime();  // Get the elapsed time

        minutes = (elapsedTime / 60000) % 60;   // Convert to minutes
        seconds = (elapsedTime / 1000) % 60;    // Convert to seconds
        milliseconds = elapsedTime % 1000;      // Get the milliseconds

        // Return the formatted time in MM:SS.mmm format
        return String.format("%02d:%02d.%03d", minutes, seconds, milliseconds);
    }

    // Static method to convert time in "MM:SS.mmm" format to milliseconds
    public static long parseTimeToMillis(String time) {
        String[] parts; // Array to hold parts of the time string
        int minutes, seconds, millis;

        // Split the time string into minutes, seconds, and milliseconds
        parts = time.split(":");
        minutes = Integer.parseInt(parts[0]);  // Extract minutes
        seconds = Integer.parseInt(parts[1]);  // Extract seconds
        millis = Integer.parseInt(parts[2]);   // Extract milliseconds

        // Convert the time to milliseconds and return the result
        return (minutes * 60 * 1000L) + (seconds * 1000L) + millis;
    }
}
