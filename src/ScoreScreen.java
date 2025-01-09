import javax.swing.*;
import java.awt.*;

public class ScoreScreen extends JPanel {
    private JLabel timeLabel;
    private JButton againButton;
    private JButton menuButton;
    private JTextField usernameField;

    public ScoreScreen(GamePanel gamePanel, String result, String previousState, boolean isHighScore) {
        JPanel containerPanel, usernamePanel, buttonsPanel;
        JLabel titleLabel, usernameLabel;
        String title;

        SoundManager.playMusic("music/scorescreen.wav");

        setLayout(new GridBagLayout()); // Center everything
        setBackground(Color.BLACK);

        // Main container
        containerPanel = new JPanel();
        containerPanel.setLayout(new GridLayout(4,1,0, 10));
        containerPanel.setBackground(Color.BLACK);
        containerPanel.setPreferredSize(new Dimension(400, 250)); 

        // Title label
        if (previousState.equals("GAME_SPRINT")) {
            title = "FINAL TIME";
        } else {
            title = "SCORE";
        }

        titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 36));
        titleLabel.setForeground(Color.BLUE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Time label
        timeLabel = new JLabel(result);
        timeLabel.setFont(new Font("Monospaced", Font.BOLD, 48));
        timeLabel.setForeground(Color.RED);
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Username input panel
        usernamePanel = new JPanel();
        usernamePanel.setLayout(new BorderLayout());
        usernamePanel.setBackground(Color.BLACK);

        usernameLabel = new JLabel("ENTER USERNAME:");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        usernameLabel.setBorder(BorderFactory.createEmptyBorder(5,0, 15,0));

        usernameField = new JTextField();
        usernameField.setHorizontalAlignment(SwingConstants.CENTER);
        usernameField.setColumns(10); // Sets the width in terms of characters
        usernameField.setFont(new Font("Arial", Font.PLAIN, 25));        
        usernameField.setPreferredSize(new Dimension(200, 100));

        usernamePanel.add(usernameLabel, BorderLayout.NORTH);
        usernamePanel.add(usernameField, BorderLayout.CENTER);

        // Buttons panel
        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(1, 2, 20, 0)); // Horizontal layout with spacing
        buttonsPanel.setBackground(Color.BLACK);
        usernameLabel.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));


        // Create buttons
        againButton = createMenuButton("AGAIN");
        menuButton = createMenuButton("MENU");

        // Add buttons to panel
        buttonsPanel.add(againButton);
        buttonsPanel.add(menuButton);

        againButton.addActionListener(e -> {
            gamePanel.returntoGame();
            gamePanel.restartGame();
            if(isHighScore){
                gamePanel.saveHighScore(previousState, result, usernameField.getText());
            }
            if(previousState.equals("GAME_SPRINT")){
                SoundManager.playMusic("music/sprint.wav");
            } else if (previousState.equals("GAME_TIMETRIAL")) {
                SoundManager.playMusic("music/blitz.wav");
            } else if (previousState.equals("GAME_CHALLENGE")) {
                SoundManager.playMusic("music/challenge.wav");
            } else if (previousState.equals("GAME_PRACTICE")) {
                SoundManager.playMusic("music/practice.wav");
            }
        });
        menuButton.addActionListener(e -> {
            gamePanel.returntoMenu();
            if(isHighScore){
                gamePanel.saveHighScore(previousState, result, usernameField.getText());
            }
            SoundManager.playMusic("music/theme.wav");

        });

        // Add components to container panel
        containerPanel.add(titleLabel);
        containerPanel.add(timeLabel);
        if(isHighScore){
            containerPanel.add(usernamePanel);
        }
        containerPanel.add(buttonsPanel);


        // Add container to main panel
        add(containerPanel);
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setPreferredSize(new Dimension(150, 50));
        button.setOpaque(true);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        // Mouse hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.DARK_GRAY);
                button.setForeground(Color.WHITE);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE);
                button.setForeground(Color.BLACK);
            }

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.DARK_GRAY);
            }
        });

        return button;
    }

    public JButton getAgainButton() {
        return againButton;
    }

    public JButton getMenuButton() {
        return menuButton;
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public void resetButtonAppearance(JButton button) {
        button.setBackground(new Color(255, 255, 255, 200));
        button.setForeground(Color.BLACK);
        button.setFont(button.getFont().deriveFont(12f));
    }
}
