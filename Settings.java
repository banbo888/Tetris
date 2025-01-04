public class Settings {
    private int arr; // Auto-repeat rate
    private int das; // Delayed auto shift
    private int sdf; // Soft drop factor
    private int musicVolume;
    private int sfxVolume;
    private boolean audioEnabled;
    private int gridVisibility;
    private int ghostVisibility;
    private boolean actionTextOn;

    public Settings() {
        // Default settings values
        this.arr = 1;
        this.das = 5;
        this.sdf = 50;
        this.musicVolume = 75;
        this.sfxVolume = 75;
        this.audioEnabled = true;
        this.gridVisibility = 50;
        this.ghostVisibility = 50;
        this.actionTextOn = true;
    }

    // Getters and setters for each setting
    public int getArr() {
        return arr;
    }

    public void setArr(int arr) {
        this.arr = arr;
    }

    public int getDas() {
        return das;
    }

    public void setDas(int das) {
        this.das = das;
    }

    public int getSdf() {
        return sdf;
    }

    public void setSdf(int sdf) {
        this.sdf = sdf;
    }

    public int getMusicVolume() {
        return musicVolume;
    }

    public void setMusicVolume(int musicVolume) {
        this.musicVolume = musicVolume;
    }

    public int getSfxVolume() {
        return sfxVolume;
    }

    public void setSfxVolume(int sfxVolume) {
        this.sfxVolume = sfxVolume;
    }

    public boolean isAudioEnabled() {
        return audioEnabled;
    }

    public void setAudioEnabled(boolean audioEnabled) {
        this.audioEnabled = audioEnabled;
    }

    public int getGridVisibility() {
        return gridVisibility;
    }

    public void setGridVisibility(int gridVisibility) {
        this.gridVisibility = gridVisibility;
    }

    public int getGhostVisibility() {
        return ghostVisibility;
    }

    public void setGhostVisibility(int ghostVisibility) {
        this.ghostVisibility = ghostVisibility;
    }

    public boolean isActionTextOn() {
        return actionTextOn;
    }

    public void setActionTextOn(boolean actionTextOn) {
        this.actionTextOn = actionTextOn;
    }
}
