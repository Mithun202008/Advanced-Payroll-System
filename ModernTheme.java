import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * ModernTheme provides a unified SaaS design system inspired by Linear, Stripe, and modern enterprise software.
 * Defines typography, curated color palettes, custom rounded borders, button renderers, and table styles.
 */
public class ModernTheme {

    // Typography
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_SECTION = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_SMALL_BOLD = new Font("Segoe UI", Font.BOLD, 11);
    public static final Font FONT_MONO = new Font("Consolas", Font.PLAIN, 12);

    // Primary Colors (SaaS Indigo/Navy)
    public static final Color PRIMARY = new Color(79, 70, 229);        // Indigo 600
    public static final Color PRIMARY_HOVER = new Color(67, 56, 202);  // Indigo 700
    public static final Color PRIMARY_LIGHT = new Color(238, 242, 255);// Indigo 50
    public static final Color PRIMARY_BORDER = new Color(199, 210, 254);// Indigo 200

    // Slate / Dark Neutral
    public static final Color SLATE_900 = new Color(15, 23, 42);       // Very dark slate
    public static final Color SLATE_800 = new Color(30, 41, 59);       // Sidebar bg
    public static final Color SLATE_700 = new Color(51, 65, 85);
    public static final Color SLATE_600 = new Color(71, 85, 105);
    public static final Color SLATE_500 = new Color(100, 116, 139);    // Text muted
    public static final Color SLATE_400 = new Color(148, 163, 184);
    public static final Color SLATE_300 = new Color(203, 213, 225);
    public static final Color SLATE_200 = new Color(226, 232, 240);    // Borders
    public static final Color SLATE_100 = new Color(241, 245, 249);    // Header bg
    public static final Color SLATE_50 = new Color(248, 250, 252);     // Canvas bg

    // Semantic Colors
    public static final Color SUCCESS = new Color(16, 185, 129);       // Emerald 500
    public static final Color SUCCESS_LIGHT = new Color(236, 253, 245);// Emerald 50
    public static final Color SUCCESS_BORDER = new Color(167, 243, 208);

    public static final Color WARNING = new Color(245, 158, 11);       // Amber 500
    public static final Color WARNING_LIGHT = new Color(254, 243, 199);// Amber 50
    public static final Color WARNING_BORDER = new Color(253, 230, 138);

    public static final Color DANGER = new Color(239, 68, 68);         // Rose 500
    public static final Color DANGER_LIGHT = new Color(254, 242, 242); // Rose 50
    public static final Color DANGER_BORDER = new Color(254, 202, 202);

    public static final Color INFO = new Color(14, 165, 233);          // Sky 500
    public static final Color INFO_LIGHT = new Color(240, 249, 255);   // Sky 50
    public static final Color INFO_BORDER = new Color(186, 230, 253);

    public static final Color CARD_BG = Color.WHITE;
    public static final Color TEXT_MAIN = SLATE_900;
    public static final Color TEXT_MUTED = SLATE_500;

    /**
     * Creates a styled modern card panel with rounded corners and subtle border.
     */
    public static JPanel createCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fill(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, 12, 12));
                g2.setColor(SLATE_200);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, 12, 12));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(16, 18, 16, 18));
        return card;
    }

    /**
     * Creates a styled flat primary button with hover and active states.
     */
    public static JButton createPrimaryButton(String text, Icon icon) {
        JButton btn = new JButton(text);
        if (icon != null) btn.setIcon(icon);
        btn.setFont(FONT_BOLD);
        btn.setForeground(Color.WHITE);
        btn.setBackground(PRIMARY);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));

        btn.addMouseListener(new MouseAdapter() {
            private boolean hover = false;
            private boolean pressed = false;

            public void mouseEntered(MouseEvent e) { hover = true; btn.repaint(); }
            public void mouseExited(MouseEvent e) { hover = false; btn.repaint(); }
            public void mousePressed(MouseEvent e) { pressed = true; btn.repaint(); }
            public void mouseReleased(MouseEvent e) { pressed = false; btn.repaint(); }
        });

        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                AbstractButton b = (AbstractButton) c;
                ButtonModel model = b.getModel();
                if (model.isPressed()) {
                    g2.setColor(PRIMARY_HOVER.darker());
                } else if (model.isRollover()) {
                    g2.setColor(PRIMARY_HOVER);
                } else if (!b.isEnabled()) {
                    g2.setColor(SLATE_300);
                } else {
                    g2.setColor(PRIMARY);
                }
                g2.fill(new RoundRectangle2D.Float(0, 0, c.getWidth(), c.getHeight(), 8, 8));
                g2.dispose();
                super.paint(g, c);
            }
        });

        return btn;
    }

    /**
     * Creates a secondary/ghost button.
     */
    public static JButton createSecondaryButton(String text, Icon icon) {
        JButton btn = new JButton(text);
        if (icon != null) btn.setIcon(icon);
        btn.setFont(FONT_BOLD);
        btn.setForeground(SLATE_700);
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 14, 8, 14));

        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                AbstractButton b = (AbstractButton) c;
                ButtonModel model = b.getModel();
                if (model.isPressed()) {
                    g2.setColor(SLATE_200);
                } else if (model.isRollover()) {
                    g2.setColor(SLATE_100);
                } else {
                    g2.setColor(Color.WHITE);
                }
                g2.fill(new RoundRectangle2D.Float(0, 0, c.getWidth(), c.getHeight(), 8, 8));
                g2.setColor(SLATE_300);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, c.getWidth() - 1, c.getHeight() - 1, 8, 8));
                g2.dispose();
                super.paint(g, c);
            }
        });

        return btn;
    }

    /**
     * Creates a danger/delete button.
     */
    public static JButton createDangerButton(String text, Icon icon) {
        JButton btn = new JButton(text);
        if (icon != null) btn.setIcon(icon);
        btn.setFont(FONT_BOLD);
        btn.setForeground(DANGER);
        btn.setBackground(DANGER_LIGHT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 14, 8, 14));

        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                AbstractButton b = (AbstractButton) c;
                ButtonModel model = b.getModel();
                if (model.isPressed()) {
                    g2.setColor(DANGER);
                    b.setForeground(Color.WHITE);
                } else if (model.isRollover()) {
                    g2.setColor(DANGER_BORDER);
                    b.setForeground(DANGER);
                } else {
                    g2.setColor(DANGER_LIGHT);
                    b.setForeground(DANGER);
                }
                g2.fill(new RoundRectangle2D.Float(0, 0, c.getWidth(), c.getHeight(), 8, 8));
                g2.setColor(DANGER_BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, c.getWidth() - 1, c.getHeight() - 1, 8, 8));
                g2.dispose();
                super.paint(g, c);
            }
        });

        return btn;
    }

    /**
     * Creates a modern textfield with rounded border and focus glow.
     */
    public static JTextField createTextField(int columns) {
        JTextField tf = new JTextField(columns);
        tf.setFont(FONT_REGULAR);
        tf.setForeground(TEXT_MAIN);
        tf.setCaretColor(PRIMARY);
        tf.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(SLATE_300, 6),
                new EmptyBorder(6, 10, 6, 10)
        ));
        tf.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                        new RoundedBorder(PRIMARY, 6),
                        new EmptyBorder(6, 10, 6, 10)
                ));
            }
            public void focusLost(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                        new RoundedBorder(SLATE_300, 6),
                        new EmptyBorder(6, 10, 6, 10)
                ));
            }
        });
        return tf;
    }

    /**
     * Styles a standard JTable into a sleek SaaS tabular data grid.
     */
    public static void styleTable(JTable table) {
        table.setFont(FONT_REGULAR);
        table.setRowHeight(36);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(PRIMARY_LIGHT);
        table.setSelectionForeground(PRIMARY_HOVER);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setBackground(SLATE_100);
        header.setForeground(SLATE_700);
        header.setPreferredSize(new Dimension(0, 38));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, SLATE_200));

        // Alternate row colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(tbl, val, isSelected, hasFocus, row, col);
                setBorder(new EmptyBorder(0, 12, 0, 12));
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : SLATE_50);
                    c.setForeground(TEXT_MAIN);
                } else {
                    c.setBackground(PRIMARY_LIGHT);
                    c.setForeground(PRIMARY_HOVER);
                }
                return c;
            }
        });
    }

    /**
     * Creates a pill status badge (e.g. Active, Pending, Approved, Leave).
     */
    public static JLabel createBadge(String text, String type) {
        JLabel badge = new JLabel(text, JLabel.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg, border;
                if ("SUCCESS".equalsIgnoreCase(type)) { bg = SUCCESS_LIGHT; border = SUCCESS_BORDER; }
                else if ("WARNING".equalsIgnoreCase(type)) { bg = WARNING_LIGHT; border = WARNING_BORDER; }
                else if ("DANGER".equalsIgnoreCase(type)) { bg = DANGER_LIGHT; border = DANGER_BORDER; }
                else if ("INFO".equalsIgnoreCase(type)) { bg = INFO_LIGHT; border = INFO_BORDER; }
                else { bg = SLATE_100; border = SLATE_300; }

                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(border);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setFont(FONT_SMALL_BOLD);
        if ("SUCCESS".equalsIgnoreCase(type)) badge.setForeground(SUCCESS.darker());
        else if ("WARNING".equalsIgnoreCase(type)) badge.setForeground(WARNING.darker());
        else if ("DANGER".equalsIgnoreCase(type)) badge.setForeground(DANGER);
        else if ("INFO".equalsIgnoreCase(type)) badge.setForeground(INFO.darker());
        else badge.setForeground(SLATE_600);

        badge.setOpaque(false);
        badge.setBorder(new EmptyBorder(3, 10, 3, 10));
        return badge;
    }

    /**
     * Helper border class for rounded components.
     */
    public static class RoundedBorder extends AbstractBorder {
        private final Color color;
        private final int radius;

        public RoundedBorder(Color color, int radius) {
            this.color = color;
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1f));
            g2.draw(new RoundRectangle2D.Float(x + 0.5f, y + 0.5f, width - 1, height - 1, radius, radius));
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(4, 8, 4, 8);
        }
    }
}
