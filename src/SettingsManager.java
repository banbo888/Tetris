public class SettingsManager {
    private int arr; // Auto-repeat rate
    private int das; // Delayed auto shift
    private int sdf; // Soft drop factor
    private int musicVolume;
    private int sfxVolume;
    private boolean audioEnabled;
    private int gridVisibility;
    private int ghostVisibility;
    private boolean actionTextOn;
    SoundManager sound;

    public SettingsManager() {
        sound = new SoundManager();
        // Default settings values
        this.arr = 1;
        this.das = 4;
        this.sdf = 1000;
        this.musicVolume = 0;
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

    public void setAudioEnabled(boolean on) {
        if(on){
            audioEnabled = true;
        }
        else{
            audioEnabled = false;
        }
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
