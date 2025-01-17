//ICS Summative - Tetris by Richard Xiong & Eric Ma
//Final Program Submission
//2025-01-16
//ExitScreen Class - Displays an exit menu with options to resume the game or return to the main menu, providing a user interface for exiting or continuing gameplay.

import javax.swing.*;
import java.awt.*;

public class ExitScreen extends JPanel {
    private JButton resumeBotton; // Button to resume the game
    private JButton mainMenuButton; // Button to return to the main menu

    // Constructor for the ExitScreen class
    public ExitScreen(GamePanel gamePanel, String label, String button1, String button2, String type) {
        // Variable declaration for UI components
        JPanel containerPanel; 
        JPanel titlePanel;
        JPanel buttonsPanel;
        JLabel exitLabel;

        setLayout(new GridBagLayout()); // Use GridBagLayout to center the container on the screen
        setBackground(Color.BLACK); // Set the background color to black

        // Main container panel to hold all sub-panels
        containerPanel = new JPanel();
        containerPanel.setLayout(new BorderLayout()); // Use BorderLayout for organizing sub-panels
        containerPanel.setBackground(new Color(0, 0, 0, 120)); // Semi-transparent black background
        containerPanel.setPreferredSize(new Dimension(300, 150)); // Set a fixed size for the container

        // Title Panel for the label
        titlePanel = new JPanel();
        titlePanel.setBackground(new Color(0, 0, 0, 0)); // Fully transparent background
        exitLabel = new JLabel(label); // Create a label with the provided text
        exitLabel.setFont(new Font("Monospaced", Font.BOLD, 24)); // Set font to Monospaced, bold, size 24
        exitLabel.setForeground(Color.WHITE); // Set text color to white
        exitLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0)); // Add padding around the label
        titlePanel.add(exitLabel); // Add the label to the title panel

        // Buttons Panel for holding the buttons
        buttonsPanel = new JPanel();
        buttonsPanel.setBackground(new Color(0, 0, 0, 0)); // Fully transparent background
        buttonsPanel.setLayout(new GridLayout(2, 1, 5, 5)); // Use GridLayout to stack buttons vertically with spacing

        // Create buttons with provided text
        resumeBotton = createMenuButton(button1); // Button for resuming the game
        mainMenuButton = createMenuButton(button2); // Button for returning to the main menu

        // Add buttons to the buttons panel
        buttonsPanel.add(resumeBotton);
        buttonsPanel.add(mainMenuButton);

        // Add padding around the buttons panel
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Add sub-panels to the container panel
        containerPanel.add(titlePanel, BorderLayout.NORTH); // Add title panel to the top
        containerPanel.add(buttonsPanel, BorderLayout.CENTER); // Add buttons panel to the center

        // Add the container panel to the main panel
        add(containerPanel);
    }

    // Helper method to create a button with specific styles
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text); // Create a button with the given text

        button.setFocusPainted(false); // Disable the focus border on click
        button.setFont(new Font("Arial", Font.BOLD, 12)); // Set font to Arial, bold, size 12
        button.setBackground(new Color(255, 255, 255, 200)); // Set semi-transparent white background
        button.setForeground(Color.BLACK); // Set text color to black
        button.setPreferredSize(new Dimension(100, 25)); // Set fixed button size
        button.setMaximumSize(new Dimension(100, 25)); // Set maximum size for layout constraints
        button.setOpaque(true); // Make the button opaque
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1)); // Add a white border

        // Add hover effects for the button
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                // Change background and text color on hover
                button.setBackground(new Color(169, 169, 169, 200)); // Semi-transparent gray
                button.setForeground(Color.WHITE); // White text color
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                // Reset background and text color when not hovering
                button.setBackground(new Color(255, 255, 255, 200)); // Semi-transparent white
                button.setForeground(Color.BLACK); // Black text color
            }

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                // Change background when the button is clicked and released
                button.setBackground(new Color(169, 169, 169, 200)); // Semi-transparent gray
            }
        });

        return button; // Return the styled button
    }

    // Getter for the resume button
    public JButton getResumeButton() {
        return resumeBotton;
    }

    // Getter for the main menu button
    public JButton getMainMenuButton() {
        return mainMenuButton;
    }

    // Method to reset a button's appearance to its default state
    public void resetButtonAppearance(JButton button) {
        button.setBackground(new Color(255, 255, 255, 200)); // Set background to semi-transparent white
        button.setForeground(Color.BLACK); // Set text color to black
        button.setFont(button.getFont().deriveFont(12f)); // Reset font size to 12
    }
}
