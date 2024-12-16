import javax.swing.*;
import java.awt.*;

public class Menu extends JPanel {

    JButton playButton;
    JButton highScoresButton;
    JButton settingsButton;
    JButton instructionsButton;
    JPanel titlePanel;
    JPanel buttonsPanel;
    JLabel titleLabel;

    public Menu(GamePanel gamePanel) {
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        // Title Panel
        titlePanel = new JPanel();
        titlePanel.setBackground(Color.BLACK);
        titleLabel = createTitleLabel();
        titleLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 0,0));
        titlePanel.add(titleLabel);

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
        playButton.addActionListener(_ -> showGameModeMenu(gamePanel));

        highScoresButton.addActionListener(_ -> JOptionPane.showMessageDialog(null, "High Scores feature coming soon!"));
        settingsButton.addActionListener(_ -> JOptionPane.showMessageDialog(null, "Settings feature coming soon!"));
        instructionsButton.addActionListener(_ -> JOptionPane.showMessageDialog(null, "Instructions feature coming soon!"));

        // Add buttons to panel
        buttonsPanel.add(playButton);
        buttonsPanel.add(highScoresButton);
        buttonsPanel.add(settingsButton);
        buttonsPanel.add(instructionsButton);

        // Padding for buttons
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        // Add panels
        add(titlePanel, BorderLayout.NORTH);
        add(buttonsPanel, BorderLayout.CENTER);
    }

    private JLabel createTitleLabel() {
        JLabel label = new JLabel("<html><span style='color:blue'>Tetri</span><span style='color:red'>X</span></html>");
        label.setFont(new Font("Monospaced", Font.BOLD, 60));
        label.setForeground(Color.WHITE);
        return label;
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

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
        JLabel modeTitle = new JLabel("<html><span style='color:blue'>Select </span><span style='color:red'>Mode</span></html>");
        modeTitle.setFont(new Font("Monospaced", Font.BOLD, 48));
        modeTitle.setForeground(Color.WHITE);
        titlePanel.add(modeTitle);
        add(titlePanel, BorderLayout.NORTH);

        // Buttons Panel for Game Modes
        JPanel modeButtonsPanel = new JPanel();
        modeButtonsPanel.setBackground(Color.BLACK);
        modeButtonsPanel.setLayout(new GridLayout(3, 1, 10, 10));

        JButton classicModeButton = createMenuButton("CLASSIC MODE");
        JButton timeAttackModeButton = createMenuButton("TIME ATTACK");
        JButton backButton = createMenuButton("BACK");

        // Add action listeners for game modes
        classicModeButton.addActionListener(_ -> gamePanel.startGame());
        timeAttackModeButton.addActionListener(_ -> gamePanel.startGame());
        backButton.addActionListener(_ -> resetToMainMenu(gamePanel));

        modeButtonsPanel.add(classicModeButton);
        modeButtonsPanel.add(timeAttackModeButton);
        modeButtonsPanel.add(backButton);

        // Padding
        modeButtonsPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        add(modeButtonsPanel, BorderLayout.CENTER);

        revalidate();
        repaint();
    }
    
    public void resetToMainMenu(GamePanel gamePanel) {
        // Remove all current components
        removeAll();
    
        // Reset layout
        setLayout(new BorderLayout());
    
        // Reuse existing components instead of creating new ones
        titleLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));
        
        // Clear existing panels and re-add original buttons
        buttonsPanel.removeAll();
        buttonsPanel.add(playButton);
        buttonsPanel.add(highScoresButton);
        buttonsPanel.add(settingsButton);
        buttonsPanel.add(instructionsButton);
    
        // Add panels back
        add(titlePanel, BorderLayout.NORTH);
        add(buttonsPanel, BorderLayout.CENTER);
    
        // Ensure layout is updated
        revalidate();
        repaint();
    }
}
