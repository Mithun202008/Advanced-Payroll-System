import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * ModernHeaderBar provides the top application bar with breadcrumbs,
 * search input, notification bell with unread badge, and telemetry status.
 */
public class ModernHeaderBar extends JPanel {
    private MainMenu parentMenu;
    private JLabel lblBreadcrumb;
    private JTextField txtGlobalSearch;
    private JButton btnNotification;
    private JLabel lblNotificationBadge;

    public ModernHeaderBar(MainMenu parent) {
        this.parentMenu = parent;
        setPreferredSize(new Dimension(0, 56));
        setBackground(Color.WHITE);
        setLayout(new BorderLayout(15, 0));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, ModernTheme.SLATE_200),
                new EmptyBorder(8, 20, 8, 20)
        ));

        // Left: Breadcrumb / Section Header
        lblBreadcrumb = new JLabel("Overview  /  Dashboard");
        lblBreadcrumb.setFont(ModernTheme.FONT_BOLD);
        lblBreadcrumb.setForeground(ModernTheme.TEXT_MAIN);
        add(lblBreadcrumb, BorderLayout.WEST);

        // Center: Global Quick Search
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        searchPanel.setOpaque(false);

        txtGlobalSearch = ModernTheme.createTextField(24);
        txtGlobalSearch.setToolTipText("Global Search employees, codes, designations, payslips...");
        txtGlobalSearch.addActionListener(e -> {
            String q = txtGlobalSearch.getText().trim();
            if (!q.isEmpty()) {
                parentMenu.performGlobalSearch(q);
            }
        });
        searchPanel.add(new JLabel(new ImageIcon("images/search.png")));
        searchPanel.add(txtGlobalSearch);
        add(searchPanel, BorderLayout.CENTER);

        // Right: Date & Notification Bell & Status
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 2));
        rightPanel.setOpaque(false);

        // Live Date & Time
        JLabel lblTime = new JLabel(new SimpleDateFormat("MMM dd, yyyy").format(new Date()));
        lblTime.setFont(ModernTheme.FONT_SMALL);
        lblTime.setForeground(ModernTheme.TEXT_MUTED);
        rightPanel.add(lblTime);

        // DB Status Indicator
        JLabel lblDbStatus = new JLabel("  Live DB  ", JLabel.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ModernTheme.SUCCESS_LIGHT);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(ModernTheme.SUCCESS_BORDER);
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblDbStatus.setFont(ModernTheme.FONT_SMALL_BOLD);
        lblDbStatus.setForeground(ModernTheme.SUCCESS.darker());
        lblDbStatus.setBorder(new EmptyBorder(3, 8, 3, 8));
        rightPanel.add(lblDbStatus);

        // Notification Bell Button
        JPanel notifContainer = new JPanel(new BorderLayout());
        notifContainer.setOpaque(false);

        btnNotification = new JButton(new ImageIcon("images/Info.png"));
        btnNotification.setToolTipText("Notifications & Alerts");
        btnNotification.setBorderPainted(false);
        btnNotification.setContentAreaFilled(false);
        btnNotification.setFocusPainted(false);
        btnNotification.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNotification.addActionListener(e -> parentMenu.openNotificationCenter());
        notifContainer.add(btnNotification, BorderLayout.CENTER);

        int unread = NotificationService.getUnreadCount();
        lblNotificationBadge = new JLabel(unread > 0 ? " " + unread + " " : "") {
            @Override
            protected void paintComponent(Graphics g) {
                if (getText().trim().isEmpty()) return;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ModernTheme.DANGER);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblNotificationBadge.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lblNotificationBadge.setForeground(Color.WHITE);
        lblNotificationBadge.setHorizontalAlignment(SwingConstants.CENTER);
        lblNotificationBadge.setVisible(unread > 0);
        notifContainer.add(lblNotificationBadge, BorderLayout.NORTH);

        rightPanel.add(notifContainer);
        add(rightPanel, BorderLayout.EAST);
    }

    public void setBreadcrumb(String text) {
        lblBreadcrumb.setText(text);
    }

    public void updateNotificationBadge() {
        int unread = NotificationService.getUnreadCount();
        lblNotificationBadge.setText(unread > 0 ? String.valueOf(unread) : "");
        lblNotificationBadge.setVisible(unread > 0);
        lblNotificationBadge.repaint();
    }
}
