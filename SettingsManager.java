//ICS Summative - Tetris by Richard Xiong & Eric Ma
//Beta Program Submission
//2025-01-09
//SettingsManager Class - Manages game settings such as auto-repeat rate, delayed auto shift, soft drop speed, audio volumes, grid and ghost piece visibility, and action text visibility, providing getter and setter methods for each setting.

public class SettingsManager {
    private int arr; // Auto-repeat rate (Controls how fast input is repeated when a key is held down)
    private int das; // Delayed auto shift (Controls the time before a block automatically shifts)
    private int sdf; // Soft drop factor (Controls the speed of soft dropping a block)
    private int musicVolume; // Volume for music
    private int sfxVolume; // Volume for sound effects
    private boolean audioEnabled; // Whether audio is enabled
    private int gridVisibility; // Controls how visible the grid is (e.g., 0 - invisible, 100 - fully visible)
    private int ghostVisibility; // Controls the visibility of the ghost piece (e.g., 0 - invisible, 100 - fully visible)
    private boolean actionTextOn; // Whether action text (such as key mappings) is visible or not
    SoundManager sound; // SoundManager instance to manage sound settings

    // Constructor to initialize default settings
    public SettingsManager() {
        sound = new SoundManager(); // Initialize sound manager for handling sound settings
        // Default settings values
        this.arr = 1; // Default auto-repeat rate
        this.das = 4; // Default delayed auto shift value
        this.sdf = 1000; // Default soft drop factor value
        this.musicVolume = 50; // Default music volume (out of 100)
        this.sfxVolume = 50; // Default sound effect volume (out of 100)
        this.audioEnabled = true; // Default is audio enabled
        this.gridVisibility = 50; // Default grid visibility (50% visible)
        this.ghostVisibility = 50; // Default ghost piece visibility (50% visible)
        this.actionTextOn = true; // Default is that action text is shown
    }

    // Getter and setter methods for each setting

    // Get auto-repeat rate
    public int getArr() {
        return arr;
    }

    // Set auto-repeat rate
    public void setArr(int arr) {
        this.arr = arr;
    }

    // Get delayed auto shift value
    public int getDas() {
        return das;
    }

    // Set delayed auto shift value
    public void setDas(int das) {
        this.das = das;
    }

    // Get soft drop factor
    public int getSdf() {
        return sdf;
    }

    // Set soft drop factor
    public void setSdf(int sdf) {
        this.sdf = sdf;
    }

    // Get music volume
    public int getMusicVolume() {
        return musicVolume;
    }

    // Set music volume
    public void setMusicVolume(int musicVolume) {
        this.musicVolume = musicVolume;
    }

    // Get sound effect volume
    public int getSfxVolume() {
        return sfxVolume;
    }

    // Set sound effect volume
    public void setSfxVolume(int sfxVolume) {
        this.sfxVolume = sfxVolume;
    }

    // Check if audio is enabled
    public boolean isAudioEnabled() {
        return audioEnabled;
    }

    // Set audio enabled/disabled
    public void setAudioEnabled(boolean on) {
        if (on) {
            audioEnabled = true; // Enable audio if 'on' is true
        } else {
            audioEnabled = false; // Disable audio if 'on' is false
        }
    }

    // Get grid visibility
    public int getGridVisibility() {
        return gridVisibility;
    }

    // Set grid visibility
    public void setGridVisibility(int gridVisibility) {
        this.gridVisibility = gridVisibility;
    }

    // Get ghost visibility
    public int getGhostVisibility() {
        return ghostVisibility;
    }

    // Set ghost visibility
    public void setGhostVisibility(int ghostVisibility) {
        this.ghostVisibility = ghostVisibility;
    }

    // Check if action text is displayed
    public boolean isActionTextOn() {
        return actionTextOn;
    }

    // Set whether action text is on or off
    public void setActionTextOn(boolean actionTextOn) {
        this.actionTextOn = actionTextOn;
    }
}
