//ICS Summative - Tetris by Richard Xiong & Eric Ma
//Beta Program Submission
//2025-01-09
//Menu Class - Manages the Menu, which allows users to start the game, see high scores, manage setings, and view instructions.

import javax.swing.*;
import javax.swing.border.TitledBorder;

import java.awt.*;

public class MenuScreen extends JPanel {

    JButton playButton;
    JButton highScoresButton;
    JButton settingsButton;
    JButton instructionsButton;
    JPanel titlePanel;
    JPanel buttonsPanel;
    JLabel tetriLabel;
    JLabel xLabel;
    SettingsManager settings;
    int arr, das, sdf;

    public MenuScreen(GamePanel gamePanel) {
        settings = new SettingsManager();
        
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        // Title Panel
        titlePanel = new JPanel();
        titlePanel.setBackground(Color.BLACK);
        tetriLabel = new JLabel("Tetri");
        tetriLabel.setFont(new Font("Monospaced", Font.BOLD, 70));
        tetriLabel.setForeground(Color.BLUE);
        tetriLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 0,0));

        xLabel = new JLabel("X");
        xLabel.setFont(new Font("Monospaced", Font.BOLD, 80));
        xLabel.setForeground(Color.RED);
        xLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 0,0));

        titlePanel.add(tetriLabel);
        titlePanel.add(xLabel);


        // Buttons Panel
        buttonsPanel = new JPanel();
        buttonsPanel.setBackground(Color.BLACK);
        buttonsPanel.setLayout(new GridLayout(4, 1, 10, 10));

        // Buttons
        playButton = createMenuButton("PLAY");
        highScoresButton = createMenuButton("HIGHSCORES");
        settingsButton = createMenuButton("SETTINGS");
        instructionsButton = createMenuButton("INSTRUCTIONS");

        // Add Action Listeners
        playButton.addActionListener(e -> showGameModeMenu(gamePanel));
        highScoresButton.addActionListener(e -> showHighscoresScreen(gamePanel));
        settingsButton.addActionListener(e -> showSettingsMenu(gamePanel));
        instructionsButton.addActionListener(e -> showInstructionsScreen(gamePanel));

        // Add buttons to panel
        buttonsPanel.add(playButton);
        buttonsPanel.add(highScoresButton);
        buttonsPanel.add(settingsButton);
        buttonsPanel.add(instructionsButton);

        // Padding for buttons
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        // Add panels
        add(titlePanel, BorderLayout.NORTH);
        add(buttonsPanel, BorderLayout.CENTER);
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        Dimension buttonSize = new Dimension(300, 70);  // Adjust these values as needed

        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setOpaque(true);
        button.setPreferredSize(buttonSize);
        button.setMinimumSize(buttonSize);
        button.setMaximumSize(buttonSize);
        button.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        

        // Mouse hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.DARK_GRAY);
                button.setForeground(Color.WHITE);
                button.setFont(button.getFont().deriveFont(28f));
                SoundManager.playSound("sfx/menuhover.wav");
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE);
                button.setForeground(Color.BLACK);
                button.setFont(button.getFont().deriveFont(24f));
            }

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.DARK_GRAY);
            }
        });

        button.addActionListener(e -> SoundManager.playSound("sfx/menuhit1.wav"));

        return button;
    }

    private void showGameModeMenu(GamePanel gamePanel) {
        JLabel modeTitle;
        JPanel modeButtonsPanel;
        JButton sprintModeButton, timeTrialModeButton, practiceModeButton, challengeModeButton, backButton;
        JPanel backButtonPanel;

        removeAll(); // Remove existing components
        setLayout(new BorderLayout());

        // Title Panel
        titlePanel = new JPanel();
        titlePanel.setBackground(Color.BLACK);
        modeTitle = new JLabel("<html><span style='color:blue'>Game </span><span style='color:red'>Mode</span></html>");
        modeTitle.setFont(new Font("Monospaced", Font.BOLD, 60));
        modeTitle.setForeground(Color.WHITE);
        
        // Add border to shift the title down
        modeTitle.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));
        
        titlePanel.add(modeTitle);
        add(titlePanel, BorderLayout.NORTH);

        // Buttons Panel for Game Modes
        modeButtonsPanel = new JPanel();
        modeButtonsPanel.setBackground(Color.BLACK);
        modeButtonsPanel.setLayout(new GridLayout(2, 2, 10, 10)); // 2 rows, 2 columns

        sprintModeButton = createMenuButton("SPRINT");
        timeTrialModeButton = createMenuButton("TIME TRIAL");
        practiceModeButton = createMenuButton("PRACTICE");
        challengeModeButton = createMenuButton("CHALLENGE");
        backButton = createMenuButton("BACK");

        // Add action listeners for game modes
        sprintModeButton.addActionListener(e -> {
            gamePanel.startGame(1);
            SoundManager.playSound("sfx/menuconfirm.wav");
            SoundManager.playMusic("music/sprint.wav");
        });
        timeTrialModeButton.addActionListener(e -> {
            gamePanel.startGame(2);
            SoundManager.playSound("sfx/menuconfirm.wav");
            SoundManager.playMusic("music/blitz.wav");
        });
        practiceModeButton.addActionListener(e -> {
            gamePanel.startGame(3);
            SoundManager.playSound("sfx/menuconfirm.wav");
            SoundManager.playMusic("music/practice.wav");
        });
        challengeModeButton.addActionListener(e -> {
            gamePanel.startGame(4);
            SoundManager.playSound("sfx/menuconfirm.wav");
            SoundManager.playMusic("music/challenge.wav");
        });
        backButton.addActionListener(e -> {
            resetToMainMenu(gamePanel);
            SoundManager.playSound("sfx/menuback.wav");
        });

        modeButtonsPanel.add(sprintModeButton);
        modeButtonsPanel.add(timeTrialModeButton);
        modeButtonsPanel.add(practiceModeButton);
        modeButtonsPanel.add(challengeModeButton);

        // Padding
        modeButtonsPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 10, 100));
        add(modeButtonsPanel, BorderLayout.CENTER);

        // Back button panel
        backButtonPanel = new JPanel();
        backButtonPanel.setBackground(Color.BLACK);
        backButtonPanel.setLayout(new BorderLayout());
        backButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 100, 50, 100));
        backButtonPanel.add(backButton, BorderLayout.CENTER);
        add(backButtonPanel, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    private void showSettingsMenu(GamePanel gamePanel) {
        JLabel settingsTitle;
        JPanel settingsPanel, handlingPanel, audioPanel, videoPanel, backButtonPanel;
        JTextField arrField, dasField, sdfField;
        JSlider musicSlider, sfxSlider, gridSlider, ghostSlider;
        JCheckBox audioToggle, actionTextToggle;
        JButton backButton;

        removeAll();
        setLayout(new BorderLayout());
    
        // Title Panel
        titlePanel = new JPanel();
        titlePanel.setBackground(Color.BLACK);
        settingsTitle = new JLabel("SETTINGS");
        settingsTitle.setFont(new Font("Monospaced", Font.BOLD, 40));
        settingsTitle.setForeground(Color.WHITE);
        settingsTitle.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        titlePanel.add(settingsTitle);
        add(titlePanel, BorderLayout.NORTH);
    
        // Main Settings Panel
        settingsPanel = new JPanel();
        settingsPanel.setBackground(Color.BLACK);
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        settingsPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));
    
        // Handling Settings
        handlingPanel = createSettingSection("Handling");
        arrField = createNumberField(settings.getArr(), "Automatic Repeat Rate: the speed at which tetrominoes move when holding down movement keys (in frames)", 0, 5);
        dasField = createNumberField(settings.getDas(), "Delayed Auto Shift: the delay before tetrominoes start moving when holding down movement keys (in frames)", 1, 20);
        sdfField = createNumberField(settings.getSdf(), "Soft Drop Factor: the speed at which tetrominoes fall when soft dropping", 5, 100);
        
        handlingPanel.add(createSettingRow("ARR (1-5)   ", arrField));
        handlingPanel.add(createSettingRow("DAS (1-20)  ", dasField));
        handlingPanel.add(createSettingRow("SDF (5-1000)", sdfField));
        settingsPanel.add(handlingPanel);
    
        // Audio Settings
        audioPanel = createSettingSection("Audio");
        musicSlider = createSlider("Music", 0, 100, settings.getMusicVolume(), "Music Volume");
        sfxSlider = createSlider("SFX", 0, 100, settings.getSfxVolume(), "Sound Effects Volume");
        audioToggle = new JCheckBox("On/Off");
        audioToggle.setForeground(Color.WHITE);
        audioToggle.setBackground(Color.BLACK);
        audioToggle.setSelected(settings.isAudioEnabled());;
        
        // Single action listener that updates settings immediately
        audioToggle.addActionListener(e -> {
            settings.setAudioEnabled(audioToggle.isSelected());
            updateGamePanelSettings(gamePanel);
        });

        musicSlider.addChangeListener(e -> {
            settings.setMusicVolume(musicSlider.getValue());
            updateGamePanelSettings(gamePanel);
        });

        audioPanel.add(createSettingRow("Music", musicSlider));
        audioPanel.add(createSettingRow("SFX  ", sfxSlider));
        audioPanel.add(createSettingRow("Audio:", audioToggle));
        settingsPanel.add(audioPanel);
    
        // Video Settings (keeping sliders for these)
        videoPanel = createSettingSection("Video");
        gridSlider = createSlider("Grid Visibility", 0, 100, settings.getGridVisibility(), "Visibility of the game grid");
        ghostSlider = createSlider("Ghost Piece Visibility", 0, 100, settings.getGhostVisibility(), "Opacity of the ghost piece");
        actionTextToggle = new JCheckBox("On/Off");
        actionTextToggle.setForeground(Color.WHITE);
        actionTextToggle.setBackground(Color.BLACK);
        actionTextToggle.setSelected(settings.isActionTextOn());
        videoPanel.add(createSettingRow("Grid       ", gridSlider));
        videoPanel.add(createSettingRow("Ghost Piece", ghostSlider));
        videoPanel.add(createSettingRow("Action Text:", actionTextToggle));
        settingsPanel.add(videoPanel);
    
        add(settingsPanel, BorderLayout.CENTER);
    
        // Back button
        backButtonPanel = new JPanel();
        backButtonPanel.setBackground(Color.BLACK);
        backButtonPanel.setLayout(new BorderLayout());
        backButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 30, 50)); 
        backButton = createMenuButton("SAVE & GO BACK");
        backButton.addActionListener(e -> {
            updateGamePanelSettings(gamePanel); // Update settings before going back
            resetToMainMenu(gamePanel);

            //Save settings when pressed:
            try {                
                arr = Integer.parseInt(arrField.getText());
                arr = Math.max(1, Math.min(5, arr));
                settings.setArr(arr);
                arrField.setText(String.valueOf(arr));
            } catch (NumberFormatException ex) {
                arrField.setText(String.valueOf(settings.getArr()));
            }

            try {
                das = Integer.parseInt(dasField.getText());
                das = Math.max(1, Math.min(20, das));
                settings.setDas(das);
                dasField.setText(String.valueOf(das));
            } catch (NumberFormatException ex) {
                dasField.setText(String.valueOf(settings.getDas()));
            }

            try {
                sdf = Integer.parseInt(sdfField.getText());
                sdf = Math.max(5, Math.min(1000, sdf));
                settings.setSdf(sdf);
                sdfField.setText(String.valueOf(sdf));
            } catch (NumberFormatException ex) {
                sdfField.setText(String.valueOf(settings.getSdf()));
            }
        });

        backButtonPanel.add(backButton, BorderLayout.CENTER);
        add(backButtonPanel, BorderLayout.SOUTH);
    
        revalidate();
        repaint();

        // Add listeners for audio settings
        musicSlider.addChangeListener(e -> settings.setMusicVolume(musicSlider.getValue()));
        sfxSlider.addChangeListener(e -> settings.setSfxVolume(sfxSlider.getValue()));
        audioToggle.addActionListener(e -> settings.setAudioEnabled(audioToggle.isSelected()));

        // Add listeners for video settings
        gridSlider.addChangeListener(e -> settings.setGridVisibility(gridSlider.getValue()));
        ghostSlider.addChangeListener(e -> settings.setGhostVisibility(ghostSlider.getValue()));
        actionTextToggle.addActionListener(e -> settings.setActionTextOn(actionTextToggle.isSelected()));
    }
    
    // Helper method to create a number input field with validation
    private JTextField createNumberField(int defaultValue, String tooltip, int min, int max) {
        JTextField field;
        
        field = new JTextField(String.valueOf(defaultValue), 5);
        field.setToolTipText(tooltip);
        field.setBackground(Color.WHITE);
        field.setForeground(Color.BLACK);
        field.setFont(new Font("Monospaced", Font.PLAIN, 14));
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setPreferredSize(new Dimension(200, 30)); // Set consistent width and height
        return field;
    }
    
    // Helper method to create slider
    private JSlider createSlider(String name, int min, int max, int defaultValue, String desc) {
        JSlider 
        
        slider = new JSlider(JSlider.HORIZONTAL, min, max, defaultValue);
        slider.setToolTipText(desc);
        slider.setBackground(Color.BLACK);
        slider.setForeground(Color.WHITE);
        slider.setPaintTicks(true);
        slider.setPaintTrack(true);
        slider.setPaintLabels(true);
        slider.setMajorTickSpacing(25);
        slider.setMinorTickSpacing(2); // Add minor ticks for better visibility
        slider.setFont(new Font("Monospaced", Font.PLAIN, 10)); // Reduced font size for labels
        slider.setPreferredSize(new Dimension(200, 40)); // Reduced size for better visibility
        return slider;
    }
    
    // Helper method to create a section panel
    private JPanel createSettingSection(String title) {
        JPanel sectionPanel = new JPanel();

        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
        sectionPanel.setBackground(Color.BLACK);
        sectionPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE),
            title,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Monospaced", Font.BOLD, 16),
            Color.WHITE
        ));
        return sectionPanel;
    }
    
    // Helper method to create a row for a setting
    private JPanel createSettingRow(String label, JComponent control) {
        JPanel row = new JPanel();
        JLabel nameLabel = new JLabel(label);
        
        row.setLayout(new BorderLayout(5, 0)); 
        row.setBackground(Color.BLACK);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));
        row.add(nameLabel, BorderLayout.WEST);
        row.add(control, BorderLayout.CENTER);
        row.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3)); 
        return row;
    }

    private void updateGamePanelSettings(GamePanel gamePanel) {
        gamePanel.updateSettings(this.settings); // Update settings in GamePanel
    }
        
    private void resetButtonAppearance(JButton button) {
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setFont(button.getFont().deriveFont(24f));
    }

    public void resetToMainMenu(GamePanel gamePanel) {
        // Remove all current components
        removeAll();
    
        // Reset layout
        setLayout(new BorderLayout());
    
        // Restore the original Tetrix title
        titlePanel = new JPanel();
        titlePanel.setBackground(Color.BLACK);
        tetriLabel = new JLabel("Tetri");
        tetriLabel.setFont(new Font("Monospaced", Font.BOLD, 70));
        tetriLabel.setForeground(Color.BLUE);
        tetriLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 0,0));

        xLabel = new JLabel("X");
        xLabel.setFont(new Font("Monospaced", Font.BOLD, 70));
        xLabel.setForeground(Color.RED);
        xLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 0,0));
        
        // Clear existing panels and re-add original buttons
        buttonsPanel.removeAll();
        
        // Reset each button's appearance
        resetButtonAppearance(playButton);
        resetButtonAppearance(highScoresButton);
        resetButtonAppearance(settingsButton);
        resetButtonAppearance(instructionsButton);
    
        buttonsPanel.add(playButton);
        buttonsPanel.add(highScoresButton);
        buttonsPanel.add(settingsButton);
        buttonsPanel.add(instructionsButton);
    
        // Clear and reset the title panel
        titlePanel.removeAll();
        titlePanel.add(tetriLabel);
        titlePanel.add(xLabel);
    
        // Add panels back
        add(titlePanel, BorderLayout.NORTH);
        add(buttonsPanel, BorderLayout.CENTER);
    
        // Ensure layout is updated
        revalidate();
        repaint();
    }

    private void showHighscoresScreen(GamePanel gamePanel) {
        JLabel highscoresTitle;
        JPanel togglePanel, scoresContainer, timetrialScores, sprintScores, displayScoresPanel, backButtonPanel;
        JButton blitzButton, sprintButton, backButton;
        CardLayout cardLayout = new CardLayout();


        removeAll();
        setLayout(new BorderLayout());
    
        // Title Panel
        titlePanel = new JPanel();
        titlePanel.setBackground(Color.BLACK);
        highscoresTitle = new JLabel("<html><span style='color:blue'>High</span><span style='color:red'>scores</span></html>");
        highscoresTitle.setFont(new Font("Monospaced", Font.BOLD, 60));
        highscoresTitle.setForeground(Color.WHITE);
        highscoresTitle.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        titlePanel.add(highscoresTitle);
        add(titlePanel, BorderLayout.NORTH);
    
        // Mode Toggle Panel
        togglePanel = new JPanel();
        togglePanel.setBackground(Color.BLACK);
        blitzButton = createMenuButton("TIME TRIAL");
        sprintButton = createMenuButton("SPRINT");
        
        // Use CardLayout for switching between score panels
        scoresContainer = new JPanel(cardLayout);
        scoresContainer.setBackground(Color.BLACK);
        
        timetrialScores = createScorePanel("GAME_TIMETRIAL");
        sprintScores = createScorePanel("GAME_SPRINT");
        
        scoresContainer.add(timetrialScores, "TIME TRIAL");
        scoresContainer.add(sprintScores, "SPRINT");
    
        // Toggle button listeners
        blitzButton.addActionListener(e -> {
            cardLayout.show(scoresContainer, "TIME TRIAl");
            blitzButton.setBackground(new Color(100, 100, 255));
            sprintButton.setBackground(Color.GRAY);
        });
        
        sprintButton.addActionListener(e -> {
            cardLayout.show(scoresContainer, "SPRINT");
            sprintButton.setBackground(new Color(100, 100, 255));
            blitzButton.setBackground(Color.GRAY);
        });
    
        togglePanel.add(blitzButton);
        togglePanel.add(sprintButton);
        
        // Main content panel
        displayScoresPanel = new JPanel(new BorderLayout());
        displayScoresPanel.setBackground(Color.BLACK);
        displayScoresPanel.add(togglePanel, BorderLayout.NORTH);
        displayScoresPanel.add(scoresContainer, BorderLayout.CENTER);
        displayScoresPanel.setBorder(BorderFactory.createEmptyBorder(15, 100, 15, 100));
        add(displayScoresPanel, BorderLayout.CENTER);
    
        // Back button
        backButtonPanel = new JPanel();
        backButtonPanel.setBackground(Color.BLACK);
        backButtonPanel.setLayout(new BorderLayout());
        backButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 100, 30, 100));
        
        backButton = createMenuButton("BACK");
        backButton.addActionListener(e -> resetToMainMenu(gamePanel));
        backButtonPanel.add(backButton, BorderLayout.CENTER);
        add(backButtonPanel, BorderLayout.SOUTH);
    
        revalidate();
        repaint();
    }
    
    private JPanel createScorePanel(String mode) {
        // Variable declaration
        ScoreManager scoreManager = new ScoreManager();
        JPanel panel, headerPanel, scoreRow;
        JLabel rankHeader, usernameHeader, scoreHeader, dateHeader, scoreLabel;

        // Score data arrays
        String[][] sprintHighscoreData = new String[10][4];
        String[][] timetrialHighscoreData = new String[10][4];
        String[][] selectedData;
        String[] score;
        
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.BLACK);
        
        // Create headers with 4 columns
        headerPanel = new JPanel(new GridLayout(1, 4));
        headerPanel.setBackground(Color.BLACK);
        
        rankHeader = new JLabel("Rank");
        usernameHeader = new JLabel("Username"); // New column
        scoreHeader = new JLabel("Score");
        dateHeader = new JLabel("Date");
        
        rankHeader.setFont(new Font("Monospaced", Font.BOLD, 20));
        rankHeader.setForeground(Color.WHITE);
        rankHeader.setHorizontalAlignment(JLabel.CENTER);
        headerPanel.add(rankHeader);
    
        usernameHeader.setFont(new Font("Monospaced", Font.BOLD, 20));
        usernameHeader.setForeground(Color.WHITE);
        usernameHeader.setHorizontalAlignment(JLabel.CENTER);
        headerPanel.add(usernameHeader);
    
        scoreHeader.setFont(new Font("Monospaced", Font.BOLD, 20));
        scoreHeader.setForeground(Color.WHITE);
        scoreHeader.setHorizontalAlignment(JLabel.CENTER);
        headerPanel.add(scoreHeader);
    
        dateHeader.setFont(new Font("Monospaced", Font.BOLD, 20));
        dateHeader.setForeground(Color.WHITE);
        dateHeader.setHorizontalAlignment(JLabel.CENTER);
        headerPanel.add(dateHeader);
        
        panel.add(headerPanel);
        panel.add(Box.createVerticalStrut(10));
        
        // Fill data arrays with data from ScoreManager
        for (int i = 0; i < sprintHighscoreData.length; i++) {
            for (int j = 0; j < sprintHighscoreData[i].length; j++) {
                if (j == 0) {
                    sprintHighscoreData[i][j] = String.valueOf(i + 1);
                    timetrialHighscoreData[i][j] = String.valueOf(i + 1); // Rank
                } else if (j == 1) {
                    sprintHighscoreData[i][j] = scoreManager.getEntryComponent("GAME_SPRINT", i + 1, "username");
                    timetrialHighscoreData[i][j] = scoreManager.getEntryComponent("GAME_TIMETRIAL", i + 1, "username");
                } else if (j == 2) {
                    sprintHighscoreData[i][j] = scoreManager.getEntryComponent("GAME_SPRINT", i + 1, "score");
                    timetrialHighscoreData[i][j] = scoreManager.getEntryComponent("GAME_TIMETRIAL", i + 1, "score");
                } else if (j == 3) {
                    sprintHighscoreData[i][j] = scoreManager.getEntryComponent("GAME_SPRINT", i + 1, "date");
                    timetrialHighscoreData[i][j] = scoreManager.getEntryComponent("GAME_TIMETRIAL", i + 1, "date");
                }
            }
        }
        
        // Select the appropriate dataset based on the mode
        if ("GAME_SPRINT".equals(mode)) {
            selectedData = sprintHighscoreData;
        } else if ("GAME_TIMETRIAL".equals(mode)) {
            selectedData = timetrialHighscoreData;
        } else {
            throw new IllegalArgumentException("Invalid game mode: " + mode);
        }
        
        // Add the rows to the panel
        for (int i = 0; i < selectedData.length; i++) {
            score = selectedData[i];
            scoreRow = new JPanel(new GridLayout(1, 4)); // 4 columns
            scoreRow.setBackground(Color.BLACK);
            
            for (int j = 0; j < score.length; j++) {
                scoreLabel = new JLabel(score[j]);
                scoreLabel.setFont(new Font("Monospaced", Font.PLAIN, 16));
                scoreLabel.setForeground(Color.WHITE);
                scoreLabel.setHorizontalAlignment(JLabel.CENTER);
                scoreRow.add(scoreLabel);
            }
            
            panel.add(scoreRow);
            panel.add(Box.createVerticalStrut(5));
        }
        
        return panel;
    }    

    private void showInstructionsScreen(GamePanel gamePanel) {
        JLabel instructionsTitle;
        JPanel contentPanel, backButtonPanel;
        JButton backButton;
        JTextPane instructionsText;
        JScrollPane scrollPane;
        String instructions;

        removeAll();
        setLayout(new BorderLayout());

        // Title Panel
        titlePanel = new JPanel();
        titlePanel.setBackground(Color.BLACK);
        instructionsTitle = new JLabel("INSTRUCTIONS");
        instructionsTitle.setFont(new Font("Monospaced", Font.BOLD, 60));
        instructionsTitle.setForeground(Color.WHITE);
        instructionsTitle.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        titlePanel.add(instructionsTitle);
        add(titlePanel, BorderLayout.NORTH);

        // Content Panel
        contentPanel = new JPanel();
        contentPanel.setBackground(Color.BLACK);
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // Instructions Text
        instructionsText = new JTextPane();
        instructionsText.setEditable(false);
        instructionsText.setBackground(Color.BLACK);
        instructionsText.setForeground(Color.WHITE);
        instructionsText.setFont(new Font("Monospaced", Font.PLAIN, 16));
        
        // Set the instructions content
        instructions = """
        1) SPRINT MODE
               - Clear 40 lines as quickly as possible.
               - Timer tracks how long it takes you to clear 40 lines.
               - Final time is recorded.

        2) TIME TRIAL MODE
            - Get the highest score possible in 2 minutes.
            - Levels increase the falling speed as you clear lines.
            - T-spins and 4-line clears reward higher points.

        3) PRACTICE MODE
            - No timer, no score.
            - Perfect for experimenting with piece placements and strategies.

        4) CHALLENGE MODE
            - Special tasks like clearing lines in limited moves, or specific line clears.
            - Power-Ups include:
                * Slow Down Time: temporarily slows falling speed.
                * Line Destroyer: removes blocks around your placed piece.

        CONTROLS
            - Left Arrow (←): Move piece left
            - Right Arrow (→): Move piece right
            - Up Arrow (↑): Rotate piece clockwise
            - Down Arrow (↓): Rotate piece counterclockwise
            - X: Flip piece 180°
            - SHIFT: Soft drop
            - SPACE: Hard drop (instantly place piece)
            - C: Hold piece
            - V: Activate stored power-up (Challenge Mode)
            - R: Restart (in-game)
            - ESC: Pause/unpause

        SETTINGS
            - DAS (Delayed Auto-Shift), ARR (Auto-Repeat Rate), SDF (Soft Drop Factor)
            - Audio: Music & SFX volume, or toggle off
            - Grid & Ghost visibility
            - Action Text: show/hide special move notifications

        TIPS
            - Hold pieces strategically to set up combos or T-spins.
            - Watch the next queue to plan ahead.
            - Power-ups (Challenge Mode) can be game-changers.
            - Practice Mode is a great way to learn without pressure.
        """;
        
        instructionsText.setText(instructions);

        // Add scroll capability
        scrollPane = new JScrollPane(instructionsText);
        scrollPane.setBackground(Color.BLACK);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);

        // Back button
        backButtonPanel = new JPanel();
        backButtonPanel.setBackground(Color.BLACK);
        backButtonPanel.setLayout(new BorderLayout());
        backButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 100, 30, 100));
        
        backButton = createMenuButton("BACK");
        backButton.addActionListener(e -> {
            resetToMainMenu(gamePanel);
            SoundManager.playSound("sfx/menuback.wav");
        });
        backButtonPanel.add(backButton, BorderLayout.CENTER);
        add(backButtonPanel, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }
}
