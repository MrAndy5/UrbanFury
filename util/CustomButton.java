package util;

import javax.swing.*;
import java.awt.*;

/**
 * The CustomButton class extends JButton to create a custom-styled button component.
 */
public class CustomButton extends JButton {
    private static final int CORNER_RADIUS = 20;

    /**
     * Constructs a CustomButton with the specified text.
     *
     * @param text The text to be displayed on the button.
     */
    public CustomButton(String text) {
        super(text);
        setOpaque(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(Color.WHITE);
        setBackground(Color.BLACK);
        setFont(new Font("Arial", Font.BOLD, 20));
        setPreferredSize(new Dimension(200, 50));
    }

    /**
     * Custom painting method to paint the button background, border, and text.
     *
     * @param g The Graphics object used for painting.
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Paint background
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS);

        // Paint border (optional)
        g2.setColor(getForeground());
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS);

        // Paint text
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(getText());
        int textHeight = fm.getAscent();
        int x = (getWidth() - textWidth) / 2;
        int y = (getHeight() + textHeight) / 2 - 3;
        g2.drawString(getText(), x, y);

        g2.dispose();
        super.paintComponent(g);
    }

    /**
     * Returns the preferred size of the button.
     *
     * @return The preferred size of the button.
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(200, 50);
    }
}


