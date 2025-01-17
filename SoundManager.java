//ICS Summative - Tetris by Richard Xiong & Eric Ma
//Final Program Submission
//2025-01-16
//SoundManager Class - manages the playback of background music and sound effects, including controlling their volume levels and handling audio resources.

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundManager {
    public static Clip musicClip; // Clip for background music
    private static float musicVolume; // Volume for music (1.0 = 100%)
    private static float sfxVolume; // Volume for sound effects (1.0 = 100%)

    // Constructor to initialize the volume levels for music and sound effects
    public SoundManager() {
        musicVolume = 7.5f; // Initial default music volume (on a scale of 0-10)
        sfxVolume = 7.5f;   // Initial default sound effect volume (on a scale of 0-10)
    }

    // Method to play background music
    public static void playMusic(String track) {
        File audioFile; // Represents the audio file to be played
        AudioInputStream audioStream; // Input stream for the audio file

        try {
            // Stop and close the current music clip if it's already running
            if (musicClip != null) {
                if (musicClip.isRunning()) {
                    musicClip.stop(); // Stop the clip if it's running
                }
                musicClip.close(); // Close the clip to release resources
                musicClip = null; // Set the clip to null
            }
        } catch (Exception e) {
            // Ignore any exceptions during cleanup
        }

        try {
            audioFile = new File(track); // Create a file object for the audio track
            audioStream = AudioSystem.getAudioInputStream(audioFile); // Get an audio stream from the file
            musicClip = AudioSystem.getClip(); // Get a clip for the audio
            musicClip.open(audioStream); // Open the clip for playback
            applyVolume(musicClip, musicVolume); // Apply the desired volume level to the music clip
            musicClip.loop(Clip.LOOP_CONTINUOUSLY); // Loop the music indefinitely
            musicClip.start(); // Start playing the music
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error playing the audio file: " + e.getMessage()); // Handle errors during music playback
        }
    }

    // Method to play sound effects
    public static void playSound(String file) {
        File soundFile; // Represents the sound effect file
        AudioInputStream audioInput; // Input stream for the sound file
        Clip soundClip; // Clip to play the sound effect

        try {
            soundFile = new File(file); // Create a file object for the sound effect
            audioInput = AudioSystem.getAudioInputStream(soundFile); // Get an audio stream for the sound
            soundClip = AudioSystem.getClip(); // Get a clip for the sound
            soundClip.open(audioInput); // Open the clip for playback
            // Apply the sound effect volume
            applyVolume(soundClip, sfxVolume);
            soundClip.start(); // Start playing the sound effect
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Error playing sound file:" + e.getMessage()); // Handle errors during sound effect playback
        }
    }

    // Method to apply volume to a given clip
    private static void applyVolume(Clip clip, float volume) {
        FloatControl gainControl; // Control to adjust the volume of the clip
        float min, max, dB; // Minimum, maximum, and dB values for volume adjustment

        if (clip == null || !clip.isOpen()) {
            return; // Return early if the clip is invalid or not open
        }
        try {
            gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN); // Get the volume control for the clip
            min = gainControl.getMinimum(); // Get the minimum volume value
            max = gainControl.getMaximum(); // Get the maximum volume value
            dB = (float) (20.0 * Math.log10(volume)); // Convert volume to dB scale
            dB = Math.max(min, Math.min(dB, max)); // Clamp the dB value within the allowable range
            gainControl.setValue(dB); // Set the volume level for the clip
        } catch (IllegalArgumentException e) {
            System.err.println("Volume control not supported: " + e.getMessage()); // Handle errors if volume control is not available
        }
    }

    // Method to set the music volume
    public static void setMusicVolume(float newVolume) {
        if (newVolume < 0.0f || newVolume > 1.0f) {
            throw new IllegalArgumentException("Volume must be between 0.0 and 1.0"); // Ensure the volume is within a valid range
        }
        musicVolume = newVolume; // Set the new music volume
        if (musicClip != null) {
            applyVolume(musicClip, musicVolume); // Update the volume for the current music clip
        }
    }

    // Method to set the sound effect volume
    public static void setSfxVolume(float newVolume) {
        if (newVolume < 0.0f || newVolume > 1.0f) {
            throw new IllegalArgumentException("Volume must be between 0.0 and 1.0"); // Ensure the volume is within a valid range
        }
        sfxVolume = newVolume; // Set the new sound effect volume
    }

    // Method to get the current music volume
    public static float getMusicVolume() {
        return musicVolume; // Return the current music volume
    }

    // Method to get the current sound effect volume
    public static float getSfxVolume() {
        return sfxVolume; // Return the current sound effect volume
    }
}
