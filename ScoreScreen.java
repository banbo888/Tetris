import javax.swing.*;
import java.awt.*;

public class ScoreScreen extends JPanel {
    private JLabel timeLabel;
    private JButton againButton;
    private JButton menuButton;

    public ScoreScreen(GamePanel gamePanel, String result) {
        setLayout(new GridBagLayout()); // Center everything
        setBackground(Color.BLACK);

        // Main container
        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new BorderLayout());
        containerPanel.setBackground(Color.BLACK);
        containerPanel.setPreferredSize(new Dimension(400, 200));

        // Title label
        JLabel titleLabel = new JLabel("FINAL TIME");
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 36));
        titleLabel.setForeground(Color.BLUE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Time label
        timeLabel = new JLabel(result);
        timeLabel.setFont(new Font("Monospaced", Font.BOLD, 48));
        timeLabel.setForeground(Color.RED);
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Buttons panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(1, 2, 20, 0)); // Horizontal layout with spacing
        buttonsPanel.setBackground(Color.BLACK);

        // Create buttons
        againButton = createMenuButton("AGAIN");
        menuButton = createMenuButton("MENU");

        // Add buttons to panel
        buttonsPanel.add(againButton);
        buttonsPanel.add(menuButton);

        againButton.addActionListener(e -> {
            gamePanel.returntoGame();
        });
        menuButton.addActionListener(e -> {
            gamePanel.returntoMenu();
        });
        

        // Add components to container panel
        containerPanel.add(titleLabel, BorderLayout.NORTH);
        containerPanel.add(timeLabel, BorderLayout.CENTER);
        containerPanel.add(buttonsPanel, BorderLayout.SOUTH);

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

    public JButton getAgainButton() {
        return againButton;
    }

    public JButton getMenuButton() {
        return menuButton;
    }

    public void resetButtonAppearance(JButton button) {
        button.setBackground(new Color(255, 255, 255, 200));
        button.setForeground(Color.BLACK);
        button.setFont(button.getFont().deriveFont(12f));
    }
}
