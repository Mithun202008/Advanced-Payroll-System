import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * ModernSidebar provides structured SaaS collateral navigation with categorized sections,
 * active indicator pills, smooth hover animations, and bottom user profile card.
 */
public class ModernSidebar extends JPanel {
    private MainMenu parentMenu;
    private List<NavButton> navButtons = new ArrayList<>();
    private String activeKey = "DASHBOARD";

    public ModernSidebar(MainMenu parent) {
        this.parentMenu = parent;
        setPreferredSize(new Dimension(245, 0));
        setBackground(ModernTheme.SLATE_900);
        setLayout(new BorderLayout());

        // Header Panel: Logo & Product Title
        JPanel brandPanel = new JPanel(new BorderLayout(10, 5));
        brandPanel.setOpaque(false);
        brandPanel.setBorder(new EmptyBorder(20, 18, 16, 18));

        JLabel logoLabel = new JLabel(new ImageIcon("images/Business.png"));
        brandPanel.add(logoLabel, BorderLayout.WEST);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("ENTERPRISE PAYROLL");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLabel.setForeground(Color.WHITE);

        JLabel subTitleLabel = new JLabel("SaaS Edition • v3.0");
        subTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        subTitleLabel.setForeground(ModernTheme.SLATE_400);

        textPanel.add(titleLabel);
        textPanel.add(subTitleLabel);
        brandPanel.add(textPanel, BorderLayout.CENTER);

        add(brandPanel, BorderLayout.NORTH);

        // Center: Scrollable navigation menu
        JPanel menuContainer = new JPanel();
        menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));
        menuContainer.setOpaque(false);
        menuContainer.setBorder(new EmptyBorder(5, 12, 10, 12));

        // Sections:
        addSectionHeader(menuContainer, "OVERVIEW");
        addNavItem(menuContainer, "Dashboard", "images/Home.gif", "DASHBOARD");

        addSectionHeader(menuContainer, "WORKFORCE");
        addNavItem(menuContainer, "Employee Directory", "images/employees.png", "EMPLOYEES");
        addNavItem(menuContainer, "Add Employee", "images/employee.png", "ADD_EMP");
        addNavItem(menuContainer, "Attendance Tracker", "images/Attendance.png", "ATTENDANCE");
        addNavItem(menuContainer, "Leave Management", "images/Advances.png", "LEAVE");

        addSectionHeader(menuContainer, "PAYROLL");
        addNavItem(menuContainer, "Process Payroll Run", "images/payments.png", "PAYROLL_RUN");
        addNavItem(menuContainer, "Salary Structure", "images/setting.png", "SETTINGS");
        addNavItem(menuContainer, "Employee Payslip", "images/emp_rpt.png", "PAYSLIP");

        addSectionHeader(menuContainer, "REPORTS");
        addNavItem(menuContainer, "Analytics & Reports", "images/prints.png", "REPORTS");

        addSectionHeader(menuContainer, "ADMINISTRATION");
        addNavItem(menuContainer, "User Roles & RBAC", "images/users.png", "USERS");
        addNavItem(menuContainer, "System Audit Logs", "images/Log Details.png", "AUDIT_LOGS");
        addNavItem(menuContainer, "Organization Settings", "images/Business.png", "ORG_SETTINGS");

        JScrollPane menuScroll = new JScrollPane(menuContainer);
        menuScroll.setBorder(null);
        menuScroll.setOpaque(false);
        menuScroll.getViewport().setOpaque(false);
        menuScroll.getVerticalScrollBar().setUnitIncrement(14);
        menuScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        add(menuScroll, BorderLayout.CENTER);

        // Bottom: User Profile Card & Logout
        add(createUserProfileCard(), BorderLayout.SOUTH);
    }

    private void addSectionHeader(JPanel container, String text) {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        headerPanel.setOpaque(false);
        headerPanel.setMaximumSize(new Dimension(300, 26));

        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(ModernTheme.SLATE_500);
        headerPanel.add(lbl);

        container.add(Box.createVerticalStrut(8));
        container.add(headerPanel);
    }

    private void addNavItem(JPanel container, String label, String iconPath, String key) {
        NavButton btn = new NavButton(label, new ImageIcon(iconPath), key);
        navButtons.add(btn);
        container.add(btn);
        container.add(Box.createVerticalStrut(2));
    }

    public void setActive(String key) {
        this.activeKey = key;
        for (NavButton b : navButtons) {
            b.repaint();
        }
    }

    private JPanel createUserProfileCard() {
        JPanel card = new JPanel(new BorderLayout(8, 0));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, ModernTheme.SLATE_800),
                new EmptyBorder(12, 14, 12, 14)
        ));

        // User initial avatar
        JLabel avatar = new JLabel(AuthService.getCurrentUser().substring(0, 1).toUpperCase(), JLabel.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ModernTheme.PRIMARY);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avatar.setPreferredSize(new Dimension(32, 32));
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        avatar.setForeground(Color.WHITE);
        card.add(avatar, BorderLayout.WEST);

        // Name and Role
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        infoPanel.setOpaque(false);

        JLabel lblName = new JLabel(AuthService.getCurrentUser());
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblName.setForeground(Color.WHITE);

        JLabel lblRole = new JLabel(AuthService.getCurrentRole().name());
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblRole.setForeground(ModernTheme.SUCCESS);

        infoPanel.add(lblName);
        infoPanel.add(lblRole);
        card.add(infoPanel, BorderLayout.CENTER);

        // Sign out button
        JButton btnLogout = new JButton(new ImageIcon("images/exit.png"));
        btnLogout.setToolTipText("Sign Out");
        btnLogout.setBorderPainted(false);
        btnLogout.setContentAreaFilled(false);
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> parentMenu.confirmLogout());
        card.add(btnLogout, BorderLayout.EAST);

        return card;
    }

    private class NavButton extends JButton {
        private String key;
        private boolean hover = false;

        public NavButton(String text, Icon icon, String key) {
            super(text);
            this.key = key;
            if (icon != null) setIcon(icon);
            setFont(new Font("Segoe UI", Font.PLAIN, 12));
            setForeground(ModernTheme.SLATE_300);
            setHorizontalAlignment(SwingConstants.LEFT);
            setIconTextGap(10);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setMaximumSize(new Dimension(230, 36));
            setPreferredSize(new Dimension(220, 36));
            setBorder(new EmptyBorder(6, 12, 6, 12));

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            });

            addActionListener(e -> {
                setActive(key);
                parentMenu.navigateTo(key);
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            boolean isActive = key.equalsIgnoreCase(activeKey);
            if (isActive) {
                g2.setColor(ModernTheme.PRIMARY);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                setForeground(Color.WHITE);
            } else if (hover) {
                g2.setColor(ModernTheme.SLATE_800);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                setForeground(Color.WHITE);
            } else {
                setForeground(ModernTheme.SLATE_300);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
