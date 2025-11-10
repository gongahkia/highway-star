package highwaystar.ui.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StatsCard extends JPanel {
    private JLabel titleLabel;
    private JLabel valueLabel;
    private JLabel iconLabel;

    public StatsCard(String title, String value, String icon) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            new EmptyBorder(15, 15, 15, 15)
        ));
        setBackground(Color.WHITE);

        // Icon
        iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        add(iconLabel, BorderLayout.WEST);

        // Content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);

        titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        titleLabel.setForeground(new Color(100, 100, 100));
        contentPanel.add(titleLabel, BorderLayout.NORTH);

        valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        contentPanel.add(valueLabel, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    public void setIcon(String icon) {
        iconLabel.setText(icon);
    }
}
