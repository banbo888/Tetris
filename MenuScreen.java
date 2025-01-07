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
        instructionsButton.addActionListener(e -> JOptionPane.showMessageDialog(null, "Instructions feature coming soon!"));

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

        return button;
    }

    private void showGameModeMenu(GamePanel gamePanel) {
        removeAll(); // Remove existing components
        setLayout(new BorderLayout());

        // Title Panel
        titlePanel = new JPanel();
        titlePanel.setBackground(Color.BLACK);
        JLabel modeTitle = new JLabel("<html><span style='color:blue'>Game </span><span style='color:red'>Mode</span></html>");
        modeTitle.setFont(new Font("Monospaced", Font.BOLD, 60));
        modeTitle.setForeground(Color.WHITE);
        
        // Add border to shift the title down
        modeTitle.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));
        
        titlePanel.add(modeTitle);
        add(titlePanel, BorderLayout.NORTH);

        // Buttons Panel for Game Modes
        JPanel modeButtonsPanel = new JPanel();
        modeButtonsPanel.setBackground(Color.BLACK);
        modeButtonsPanel.setLayout(new GridLayout(2, 2, 10, 10)); // 2 rows, 2 columns

        JButton sprintModeButton = createMenuButton("SPRINT");
        JButton timeTrialModeButton = createMenuButton("TIME TRIAL");
        JButton practiceModeButton = createMenuButton("PRACTICE");
        JButton challengeModeButton = createMenuButton("CHALLENGE");
        JButton backButton = createMenuButton("BACK");

        // Add action listeners for game modes
        sprintModeButton.addActionListener(e -> gamePanel.startGame(1));
        timeTrialModeButton.addActionListener(e -> gamePanel.startGame(2));
        practiceModeButton.addActionListener(e -> gamePanel.startGame(3));
        challengeModeButton.addActionListener(e -> gamePanel.startGame(4));
        backButton.addActionListener(e -> resetToMainMenu(gamePanel));

        modeButtonsPanel.add(sprintModeButton);
        modeButtonsPanel.add(timeTrialModeButton);
        modeButtonsPanel.add(practiceModeButton);
        modeButtonsPanel.add(challengeModeButton);

        // Padding
        modeButtonsPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 10, 100));
        add(modeButtonsPanel, BorderLayout.CENTER);

        // Back button panel
        JPanel backButtonPanel = new JPanel();
        backButtonPanel.setBackground(Color.BLACK);
        backButtonPanel.setLayout(new BorderLayout());
        backButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 100, 50, 100));
        backButtonPanel.add(backButton, BorderLayout.CENTER);
        add(backButtonPanel, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    private void showSettingsMenu(GamePanel gamePanel) {
        removeAll();
        setLayout(new BorderLayout());
    
        // Title Panel
        titlePanel = new JPanel();
        titlePanel.setBackground(Color.BLACK);
        JLabel settingsTitle = new JLabel("SETTINGS");
        settingsTitle.setFont(new Font("Monospaced", Font.BOLD, 40));
        settingsTitle.setForeground(Color.WHITE);
        settingsTitle.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        titlePanel.add(settingsTitle);
        add(titlePanel, BorderLayout.NORTH);
    
        // Main Settings Panel
        JPanel settingsPanel = new JPanel();
        settingsPanel.setBackground(Color.BLACK);
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        settingsPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));
    
        // Handling Settings
        JPanel handlingPanel = createSettingSection("Handling");
        JTextField arrField = createNumberField(settings.getArr(), "Automatic Repeat Rate: the speed at which tetrominoes move when holding down movement keys (in frames)", 0, 5);
        JTextField dasField = createNumberField(settings.getDas(), "Delayed Auto Shift: the delay before tetrominoes start moving when holding down movement keys (in frames)", 1, 20);
        JTextField sdfField = createNumberField(settings.getSdf(), "Soft Drop Factor: the speed at which tetrominoes fall when soft dropping", 5, 100);
        
        handlingPanel.add(createSettingRow("ARR (1-5)   ", arrField));
        handlingPanel.add(createSettingRow("DAS (1-20)  ", dasField));
        handlingPanel.add(createSettingRow("SDF (5-1000)", sdfField));
        settingsPanel.add(handlingPanel);
    
        // Audio Settings (keeping sliders for these)
        JPanel audioPanel = createSettingSection("Audio");
        JSlider musicSlider = createSlider("Music", 0, 100, settings.getMusicVolume(), "Music Volume");
        JSlider sfxSlider = createSlider("SFX", 0, 100, settings.getSfxVolume(), "Sound Effects Volume");
        JCheckBox audioToggle = new JCheckBox("On/Off");
        audioToggle.setForeground(Color.WHITE);
        audioToggle.setBackground(Color.BLACK);
        audioToggle.setSelected(settings.isAudioEnabled());
        audioPanel.add(createSettingRow("Music", musicSlider));
        audioPanel.add(createSettingRow("SFX  ", sfxSlider));
        audioPanel.add(createSettingRow("Audio:", audioToggle));
        settingsPanel.add(audioPanel);
    
        // Video Settings (keeping sliders for these)
        JPanel videoPanel = createSettingSection("Video");
        JSlider gridSlider = createSlider("Grid Visibility", 0, 100, settings.getGridVisibility(), "Visibility of the game grid");
        JSlider ghostSlider = createSlider("Ghost Piece Visibility", 0, 100, settings.getGhostVisibility(), "Opacity of the ghost piece");
        JCheckBox actionTextToggle = new JCheckBox("On/Off");
        actionTextToggle.setForeground(Color.WHITE);
        actionTextToggle.setBackground(Color.BLACK);
        actionTextToggle.setSelected(settings.isActionTextOn());
        videoPanel.add(createSettingRow("Grid       ", gridSlider));
        videoPanel.add(createSettingRow("Ghost Piece", ghostSlider));
        videoPanel.add(createSettingRow("Action Text:", actionTextToggle));
        settingsPanel.add(videoPanel);
    
        add(settingsPanel, BorderLayout.CENTER);
    
        // Back button
        JPanel backButtonPanel = new JPanel();
        backButtonPanel.setBackground(Color.BLACK);
        backButtonPanel.setLayout(new BorderLayout());
        backButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 30, 50)); 
        JButton backButton = createMenuButton("SAVE & GO BACK");
        backButton.addActionListener(e -> {
            updateGamePanelSettings(gamePanel); // Update settings before going back
            resetToMainMenu(gamePanel);

            //Save settings when pressed:
            try {
                int value = Integer.parseInt(arrField.getText());
                value = Math.max(1, Math.min(5, value));
                settings.setArr(value);
                arrField.setText(String.valueOf(value));
            } catch (NumberFormatException ex) {
                arrField.setText(String.valueOf(settings.getArr()));
            }

            try {
                int value = Integer.parseInt(dasField.getText());
                value = Math.max(1, Math.min(20, value));
                settings.setDas(value);
                dasField.setText(String.valueOf(value));
            } catch (NumberFormatException ex) {
                dasField.setText(String.valueOf(settings.getDas()));
            }

            try {
                int value = Integer.parseInt(sdfField.getText());
                value = Math.max(5, Math.min(1000, value));
                settings.setSdf(value);
                sdfField.setText(String.valueOf(value));
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
        JTextField field = new JTextField(String.valueOf(defaultValue), 5);
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
        JSlider slider = new JSlider(JSlider.HORIZONTAL, min, max, defaultValue);
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
        row.setLayout(new BorderLayout(5, 0)); 
        row.setBackground(Color.BLACK);
        JLabel nameLabel = new JLabel(label);
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
        removeAll();
        setLayout(new BorderLayout());
    
        // Title Panel
        titlePanel = new JPanel();
        titlePanel.setBackground(Color.BLACK);
        JLabel highscoresTitle = new JLabel("<html><span style='color:blue'>High</span><span style='color:red'>scores</span></html>");
        highscoresTitle.setFont(new Font("Monospaced", Font.BOLD, 60));
        highscoresTitle.setForeground(Color.WHITE);
        highscoresTitle.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        titlePanel.add(highscoresTitle);
        add(titlePanel, BorderLayout.NORTH);
    
        // Mode Toggle Panel
        JPanel togglePanel = new JPanel();
        togglePanel.setBackground(Color.BLACK);
        JButton blitzButton = createMenuButton("BLITZ");
        JButton sprintButton = createMenuButton("SPRINT");
        
        // Use CardLayout for switching between score panels
        CardLayout cardLayout = new CardLayout();
        JPanel scoresContainer = new JPanel(cardLayout);
        scoresContainer.setBackground(Color.BLACK);
        
        JPanel blitzScores = createScorePanel("BLITZ");
        JPanel sprintScores = createScorePanel("SPRINT");
        
        scoresContainer.add(blitzScores, "BLITZ");
        scoresContainer.add(sprintScores, "SPRINT");
    
        // Toggle button listeners
        blitzButton.addActionListener(e -> {
            cardLayout.show(scoresContainer, "BLITZ");
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
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.BLACK);
        contentPanel.add(togglePanel, BorderLayout.NORTH);
        contentPanel.add(scoresContainer, BorderLayout.CENTER);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 100, 15, 100));
        add(contentPanel, BorderLayout.CENTER);
    
        // Back button
        JPanel backButtonPanel = new JPanel();
        backButtonPanel.setBackground(Color.BLACK);
        backButtonPanel.setLayout(new BorderLayout());
        backButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 100, 30, 100));
        
        JButton backButton = createMenuButton("BACK");
        backButton.addActionListener(e -> resetToMainMenu(gamePanel));
        backButtonPanel.add(backButton, BorderLayout.CENTER);
        add(backButtonPanel, BorderLayout.SOUTH);
    
        revalidate();
        repaint();
    }
    
    private JPanel createScorePanel(String mode) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.BLACK);
        
        // Create headers with 4 columns now
        JPanel headerPanel = new JPanel(new GridLayout(1, 4));
        headerPanel.setBackground(Color.BLACK);
        
        JLabel rankHeader = new JLabel("Rank");
        JLabel usernameHeader = new JLabel("Username");  // New column
        JLabel scoreHeader = new JLabel("Score");
        JLabel dateHeader = new JLabel("Date");
        
        for (JLabel header : new JLabel[]{rankHeader, usernameHeader, scoreHeader, dateHeader}) {
            header.setFont(new Font("Monospaced", Font.BOLD, 20));
            header.setForeground(Color.WHITE);
            header.setHorizontalAlignment(JLabel.CENTER);
            headerPanel.add(header);
        }
        
        panel.add(headerPanel);
        panel.add(Box.createVerticalStrut(10));
        
        // Add 10 scores with usernames
        String[][] dummyScores = {
            {"1", "Player1", "1000", "2024-01-07"},
            {"2", "Player2", "950", "2024-01-06"},
            {"3", "Player3", "900", "2024-01-05"},
            {"4", "Player4", "850", "2024-01-04"},
            {"5", "Player5", "800", "2024-01-03"},
            {"6", "Player6", "750", "2024-01-02"},
            {"7", "Player7", "700", "2024-01-01"},
            {"8", "Player8", "650", "2023-12-31"},
            {"9", "Player9", "600", "2023-12-30"},
            {"10", "Player10", "550", "2023-12-29"}
        };
        
        for (String[] score : dummyScores) {
            JPanel scoreRow = new JPanel(new GridLayout(1, 4));  // Changed to 4 columns
            scoreRow.setBackground(Color.BLACK);
            
            for (String value : score) {
                JLabel label = new JLabel(value);
                label.setFont(new Font("Monospaced", Font.PLAIN, 16));
                label.setForeground(Color.WHITE);
                label.setHorizontalAlignment(JLabel.CENTER);
                scoreRow.add(label);
            }
            
            panel.add(scoreRow);
            panel.add(Box.createVerticalStrut(5));
        }
        
        return panel;
    }
}
