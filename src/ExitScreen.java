import javax.swing.*;
import java.awt.*;

public class ExitScreen extends JPanel {
    private JButton resumeBotton;
    private JButton mainMenuButton;

    public ExitScreen(GamePanel gamePanel, String label, String button1, String button2, String type) {
        // Variable declaration
        JPanel containerPanel;

        JPanel titlePanel;
        JPanel buttonsPanel;
        JLabel exitLabel;

        setLayout(new GridBagLayout()); // GridBagLayout for centering
        setBackground(Color.BLACK); 

        // Main container panel to hold everything
        containerPanel = new JPanel();
        containerPanel.setLayout(new BorderLayout());
        containerPanel.setBackground(new Color(0, 0, 0, 120));
        containerPanel.setPreferredSize(new Dimension(300, 150)); // Smaller fixed size on screen

        // Title Panel
        titlePanel = new JPanel();
        titlePanel.setBackground(new Color(0, 0, 0, 0)); // Fully transparent
        exitLabel = new JLabel(label);
        exitLabel.setFont(new Font("Monospaced", Font.BOLD, 24)); // Smaller font
        exitLabel.setForeground(Color.WHITE);
        exitLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0)); // Smaller padding
        titlePanel.add(exitLabel);

        // Buttons Panel
        buttonsPanel = new JPanel();
        buttonsPanel.setBackground(new Color(0, 0, 0, 0)); // Fully transparent
        buttonsPanel.setLayout(new GridLayout(2, 1, 5, 5)); // Vertical layout with small gap

        // Create Buttons
        resumeBotton = createMenuButton(button1);
        mainMenuButton = createMenuButton(button2);
        
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

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);

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

    public JButton getResumeButton() {
        return resumeBotton;
    }

    public JButton getMainMenuButton() {
        return mainMenuButton;
    }

    public void resetButtonAppearance(JButton button) {
        button.setBackground(new Color(255, 255, 255, 200));
        button.setForeground(Color.BLACK);
        button.setFont(button.getFont().deriveFont(12f));
    }
}
