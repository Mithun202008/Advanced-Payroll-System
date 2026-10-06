import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Modern SaaS KPI Metric Card displaying title, large formatted number, icon, and contextual trend.
 */
public class KpiCard extends JPanel {
    private JLabel lblTitle;
    private JLabel lblValue;
    private JLabel lblTrend;
    private Icon icon;
    private Color accentColor;

    public KpiCard(String title, String initialValue, String trendText, Color accentColor, Icon icon) {
        this.accentColor = accentColor != null ? accentColor : ModernTheme.PRIMARY;
        this.icon = icon;

        setLayout(new BorderLayout(10, 8));
        setOpaque(false);
        setBorder(new EmptyBorder(14, 16, 14, 16));

        // Top Row: Title + Icon Badge
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        lblTitle = new JLabel(title.toUpperCase());
        lblTitle.setFont(ModernTheme.FONT_SMALL_BOLD);
        lblTitle.setForeground(ModernTheme.TEXT_MUTED);
        topRow.add(lblTitle, BorderLayout.WEST);

        if (icon != null) {
            JLabel iconLabel = new JLabel(icon);
            topRow.add(iconLabel, BorderLayout.EAST);
        }
        add(topRow, BorderLayout.NORTH);

        // Center: Big bold stat value
        lblValue = new JLabel(initialValue);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblValue.setForeground(ModernTheme.TEXT_MAIN);
        add(lblValue, BorderLayout.CENTER);

        // Bottom: Trend / context subtitle
        lblTrend = new JLabel(trendText);
        lblTrend.setFont(ModernTheme.FONT_SMALL);
        lblTrend.setForeground(ModernTheme.TEXT_MUTED);
        add(lblTrend, BorderLayout.SOUTH);
    }

    public void setValue(String value) {
        lblValue.setText(value);
    }

    public void setTrend(String trend) {
        lblTrend.setText(trend);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Card surface
        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, 10, 10));

        // Subtle border
        g2.setColor(ModernTheme.SLATE_200);
        g2.setStroke(new BasicStroke(1f));
        g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, 10, 10));

        // Accent indicator bar on left
        g2.setColor(accentColor);
        g2.fill(new RoundRectangle2D.Float(1, 4, 4, getHeight() - 8, 3, 3));

        g2.dispose();
        super.paintComponent(g);
    }
}
