//ICS Summative - Tetris by Richard Xiong & Eric Ma
//Final Program Submission
//2025-01-16
//PauseScreen Class - Displays a pause menu with options to resume the game or return to the main menu, providing a user interface for pausing and resuming gameplay.

import javax.swing.*;
import java.awt.*;

public class PauseScreen extends JPanel {
    public JButton resumeBotton;
    public JButton mainMenuButton;
    private JPanel titlePanel;
    private JPanel buttonsPanel;
    private JLabel pausedLabel;

    public PauseScreen(GamePanel gamePanel) {
        JPanel containerPanel;

        setLayout(new GridBagLayout()); // Changed to GridBagLayout for better centering
        setBackground(Color.BLACK); // More translucent background

        // Main container panel to hold everything
        containerPanel = new JPanel();
        containerPanel.setLayout(new BorderLayout());
        containerPanel.setBackground(new Color(0, 0, 0, 120)); // Semi-transparent container
        containerPanel.setPreferredSize(new Dimension(200, 150)); // Smaller fixed size

        // Title Panel
        titlePanel = new JPanel();
        titlePanel.setBackground(new Color(0, 0, 0, 0)); // Fully transparent
        pausedLabel = new JLabel("EXIT TO MENU?");
        pausedLabel.setFont(new Font("Monospaced", Font.BOLD, 24)); // Smaller font
        pausedLabel.setForeground(Color.WHITE);
        pausedLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0)); // Smaller padding
        titlePanel.add(pausedLabel);

        // Buttons Panel
        buttonsPanel = new JPanel();
        buttonsPanel.setBackground(new Color(0, 0, 0, 0)); // Fully transparent
        buttonsPanel.setLayout(new GridLayout(2, 1, 5, 5)); // Vertical layout with small gap

        // Create Buttons
        resumeBotton = createMenuButton("RESUME");
        mainMenuButton = createMenuButton("RETURN TO MENU");

        // Add buttons to panel
        buttonsPanel.add(resumeBotton);
        buttonsPanel.add(mainMenuButton);

        // Smaller padding for buttons panel
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Add panels to container
        containerPanel.add(titlePanel, BorderLayout.NORTH);
        containerPanel.add(buttonsPanel, BorderLayout.CENTER);

        // Add container to main panel
        add(containerPanel);
    }

    // Method to create button
    private JButton createMenuButton(String text) {
        JButton button;
        
        button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12)); // Smaller font
        button.setBackground(new Color(255, 255, 255, 200)); // Semi-transparent white
        button.setForeground(Color.BLACK);
        button.setPreferredSize(new Dimension(100, 25)); // Fixed small size
        button.setMaximumSize(new Dimension(100, 25));
        button.setOpaque(true);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));

        // Mouse hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(169, 169, 169, 200)); // Semi-transparent gray
                button.setForeground(Color.WHITE);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 255, 255, 200));
                button.setForeground(Color.BLACK);
            }

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(169, 169, 169, 200));
            }
        });

        return button;
    }

    // Get Resume and MainMenu buttons for use in GamePanel
    public JButton getResumeButton() {
        return resumeBotton;
    }

    public JButton getMainMenuButton() {
        return mainMenuButton;
    }

    // Reset button appearance
    public void resetButtonAppearance(JButton button) {
        button.setBackground(new Color(255, 255, 255, 200));
        button.setForeground(Color.BLACK);
        button.setFont(button.getFont().deriveFont(12f));
    }
}
