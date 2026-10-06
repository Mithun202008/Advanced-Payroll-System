import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * ModernNotificationCenter displays system announcements, payroll alerts,
 * and pending approval reminders.
 */
public class ModernNotificationCenter extends JDialog {
    private JTable notifTable;
    private DefaultTableModel tableModel;
    private JButton btnMarkRead, btnClose;

    public ModernNotificationCenter(JFrame parent) {
        super(parent, "Notification & Alert Center", true);
        setSize(580, 420);
        setLocationRelativeTo(parent);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBackground(Color.WHITE);
        root.setBorder(new EmptyBorder(16, 18, 16, 18));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel lblTitle = new JLabel("System Notifications & Workflow Alerts");
        lblTitle.setFont(ModernTheme.FONT_TITLE);
        lblTitle.setForeground(ModernTheme.TEXT_MAIN);
        header.add(lblTitle, BorderLayout.WEST);
        root.add(header, BorderLayout.NORTH);

        // Table
        String[] cols = {"Category", "Alert Title", "Timestamp", "Read"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        notifTable = new JTable(tableModel);
        ModernTheme.styleTable(notifTable);

        notifTable.getColumnModel().getColumn(0).setPreferredWidth(95);
        notifTable.getColumnModel().getColumn(1).setPreferredWidth(260);
        notifTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        notifTable.getColumnModel().getColumn(3).setPreferredWidth(60);

        notifTable.getColumnModel().getColumn(0).setCellRenderer((tbl, val, isSel, hasFoc, row, col) -> {
            String cat = val != null ? val.toString() : "SYSTEM";
            String type = "INFO";
            if ("PAYROLL".equalsIgnoreCase(cat)) type = "SUCCESS";
            else if ("LEAVE".equalsIgnoreCase(cat)) type = "WARNING";
            JLabel b = ModernTheme.createBadge(cat, type);
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 4));
            p.setOpaque(isSel);
            p.setBackground(isSel ? ModernTheme.PRIMARY_LIGHT : (row % 2 == 0 ? Color.WHITE : ModernTheme.SLATE_50));
            p.add(b);
            return p;
        });

        root.add(new JScrollPane(notifTable), BorderLayout.CENTER);

        // Bottom Actions
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        btnPanel.setOpaque(false);

        btnMarkRead = ModernTheme.createPrimaryButton("Mark All As Read", new ImageIcon("images/ok.png"));
        btnMarkRead.addActionListener(e -> {
            NotificationService.markAllAsRead();
            loadNotifications();
        });

        btnClose = ModernTheme.createSecondaryButton("Close", new ImageIcon("images/exit.png"));
        btnClose.addActionListener(e -> dispose());

        btnPanel.add(btnMarkRead);
        btnPanel.add(btnClose);
        root.add(btnPanel, BorderLayout.SOUTH);

        getContentPane().add(root);
        loadNotifications();
    }

    private void loadNotifications() {
        tableModel.setRowCount(0);
        List<NotificationService.NotificationItem> list = NotificationService.getAll();
        for (NotificationService.NotificationItem n : list) {
            tableModel.addRow(new Object[]{
                    n.category,
                    n.title + " - " + n.message,
                    n.createdAt,
                    n.isRead ? "Yes" : "NEW"
            });
        }
    }
}
