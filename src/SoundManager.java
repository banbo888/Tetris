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

    public SoundManager() {
        musicVolume = 7.5f;
        sfxVolume = 7.5f;
    }

    // Method to play background music
    public static void playMusic(String track) {
        File audioFile;
        AudioInputStream audioStream;

        try {
            if (musicClip != null) {
                if (musicClip.isRunning()) {
                    musicClip.stop();
                }
                musicClip.close();
                musicClip = null;
            }
        } catch (Exception e) {}

    
        try {
            audioFile = new File(track);
            audioStream = AudioSystem.getAudioInputStream(audioFile);
            musicClip = AudioSystem.getClip();
            musicClip.open(audioStream); // Open the clip before setting the volume
            applyVolume(musicClip, musicVolume); // Set the volume now that the clip is open
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
            musicClip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error playing the audio file: " + e.getMessage());
        }
    }

    // Method to play sound effect
    public static void playSound(String file) {
        File soundFile;
        AudioInputStream audioInput;
        Clip soundClip;

        try {
            soundFile = new File(file);
            audioInput = AudioSystem.getAudioInputStream(soundFile);
            soundClip = AudioSystem.getClip();
            soundClip.open(audioInput);
            // Apply SFX volume
            applyVolume(soundClip, sfxVolume);
            soundClip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Error playing sound file:" + e.getMessage());
        }
    }

    // Method to apply volume to a given clip
    private static void applyVolume(Clip clip, float volume) {
        FloatControl gainControl;
        float min, max, dB;

        if (clip == null || !clip.isOpen()) {
            return;
        }
        try {
            gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            min = gainControl.getMinimum();
            max = gainControl.getMaximum();
            dB = (float) (20.0 * Math.log10(volume));
            dB = Math.max(min, Math.min(dB, max)); // Clamp the dB value within the allowable range
            gainControl.setValue(dB);
        } catch (IllegalArgumentException e) {
            System.err.println("Volume control not supported: " + e.getMessage());
        }
    }
    
    // Method to set the music volume
    public static void setMusicVolume(float newVolume) {
        if (newVolume < 0.0f || newVolume > 1.0f) {
            throw new IllegalArgumentException("Volume must be between 0.0 and 1.0");
        }
        musicVolume = newVolume;
        if (musicClip != null) {
            applyVolume(musicClip, musicVolume); // Update the volume for the current music
        }
    }

    // Method to set the SFX volume
    public static void setSfxVolume(float newVolume) {
        if (newVolume < 0.0f || newVolume > 1.0f) {
            throw new IllegalArgumentException("Volume must be between 0.0 and 1.0");
        }
        sfxVolume = newVolume;
    }

    // Method to get the current music volume
    public static float getMusicVolume() {
        return musicVolume;
    }

    // Method to get the current SFX volume
    public static float getSfxVolume() {
        return sfxVolume;
    }
}
