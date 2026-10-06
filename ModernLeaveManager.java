import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 * ModernLeaveManager provides leave workflow management:
 * Leave request submission, Manager/Admin approval and rejection with remarks,
 * quota balance tracking, status chip rendering, and notifications.
 */
public class ModernLeaveManager extends JPanel {
    private MainMenu parentMenu;

    private JTable leaveTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JComboBox<String> cmbStatusFilter;

    private JLabel lblPendingCount, lblApprovedCount, lblRejectedCount;
    private JButton btnApplyLeave, btnApprove, btnReject, btnRefresh;

    public ModernLeaveManager(MainMenu parent) {
        this.parentMenu = parent;
        setLayout(new BorderLayout(14, 14));
        setBackground(ModernTheme.SLATE_50);
        setBorder(new EmptyBorder(16, 20, 20, 20));

        // Top Control Card
        add(createTopControlCard(), BorderLayout.NORTH);

        // Center Table Card
        JPanel centerCard = ModernTheme.createCard();
        centerCard.setLayout(new BorderLayout(0, 10));

        setupLeaveTable();
        JScrollPane scrollPane = new JScrollPane(leaveTable);
        scrollPane.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, ModernTheme.SLATE_200));
        centerCard.add(scrollPane, BorderLayout.CENTER);

        // Bottom Actions Toolbar
        centerCard.add(createBottomToolbar(), BorderLayout.SOUTH);
        add(centerCard, BorderLayout.CENTER);

        loadLeaveRequests();
    }

    private JPanel createTopControlCard() {
        JPanel card = ModernTheme.createCard();
        card.setLayout(new BorderLayout(15, 10));

        JPanel leftPanel = new JPanel(new GridLayout(2, 1, 0, 3));
        leftPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Leave Management & Time-Off Approvals");
        lblTitle.setFont(ModernTheme.FONT_TITLE);
        lblTitle.setForeground(ModernTheme.TEXT_MAIN);

        JLabel lblSub = new JLabel("Employee Leave Requests, HR Approval Queue, and Balance Quota Tracking");
        lblSub.setFont(ModernTheme.FONT_REGULAR);
        lblSub.setForeground(ModernTheme.TEXT_MUTED);

        leftPanel.add(lblTitle);
        leftPanel.add(lblSub);
        card.add(leftPanel, BorderLayout.WEST);

        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        actionPanel.setOpaque(false);

        actionPanel.add(new JLabel("Status Filter:"));
        cmbStatusFilter = new JComboBox<>(new String[]{"All Statuses", "Pending", "Approved", "Rejected"});
        cmbStatusFilter.addActionListener(e -> applyFilter());
        actionPanel.add(cmbStatusFilter);

        btnRefresh = ModernTheme.createSecondaryButton("Refresh", new ImageIcon("images/reload_Hover.png"));
        btnRefresh.addActionListener(e -> loadLeaveRequests());
        actionPanel.add(btnRefresh);

        btnApplyLeave = ModernTheme.createPrimaryButton("Request Leave", new ImageIcon("images/Advances.png"));
        btnApplyLeave.addActionListener(e -> showApplyLeaveDialog());
        actionPanel.add(btnApplyLeave);

        card.add(actionPanel, BorderLayout.EAST);
        return card;
    }

    private void setupLeaveTable() {
        String[] cols = {"ID", "Emp Code", "Personnel Name", "Leave Type", "Start Date", "End Date", "Days", "Reason", "Status", "Reviewed By"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        leaveTable = new JTable(tableModel);
        ModernTheme.styleTable(leaveTable);

        rowSorter = new TableRowSorter<>(tableModel);
        leaveTable.setRowSorter(rowSorter);

        leaveTable.getColumnModel().getColumn(0).setPreferredWidth(45);
        leaveTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        leaveTable.getColumnModel().getColumn(2).setPreferredWidth(140);
        leaveTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        leaveTable.getColumnModel().getColumn(4).setPreferredWidth(90);
        leaveTable.getColumnModel().getColumn(5).setPreferredWidth(90);
        leaveTable.getColumnModel().getColumn(6).setPreferredWidth(55);
        leaveTable.getColumnModel().getColumn(7).setPreferredWidth(180);
        leaveTable.getColumnModel().getColumn(8).setPreferredWidth(95);
        leaveTable.getColumnModel().getColumn(9).setPreferredWidth(110);

        // Status badge renderer for column 8
        leaveTable.getColumnModel().getColumn(8).setCellRenderer((tbl, val, isSel, hasFoc, row, col) -> {
            String st = val != null ? val.toString() : "Pending";
            String badgeType = "Approved".equalsIgnoreCase(st) ? "SUCCESS" : ("Rejected".equalsIgnoreCase(st) ? "DANGER" : "WARNING");
            JLabel badge = ModernTheme.createBadge(st, badgeType);
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 4));
            p.setOpaque(isSel);
            p.setBackground(isSel ? ModernTheme.PRIMARY_LIGHT : (row % 2 == 0 ? Color.WHITE : ModernTheme.SLATE_50));
            p.add(badge);
            return p;
        });
    }

    private JPanel createBottomToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout(10, 5));
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(10, 5, 5, 5));

        // KPI Counter Pills
        JPanel kpiRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        kpiRow.setOpaque(false);

        lblPendingCount = new JLabel("Pending Review: 0");
        lblPendingCount.setFont(ModernTheme.FONT_BOLD);
        lblPendingCount.setForeground(ModernTheme.WARNING.darker());

        lblApprovedCount = new JLabel("Approved: 0");
        lblApprovedCount.setFont(ModernTheme.FONT_BOLD);
        lblApprovedCount.setForeground(ModernTheme.SUCCESS.darker());

        lblRejectedCount = new JLabel("Rejected: 0");
        lblRejectedCount.setFont(ModernTheme.FONT_BOLD);
        lblRejectedCount.setForeground(ModernTheme.DANGER);

        kpiRow.add(lblPendingCount);
        kpiRow.add(lblApprovedCount);
        kpiRow.add(lblRejectedCount);
        toolbar.add(kpiRow, BorderLayout.WEST);

        // Approve / Reject buttons
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        btnApprove = ModernTheme.createPrimaryButton("Approve Selected", new ImageIcon("images/ok.png"));
        btnApprove.addActionListener(e -> updateSelectedLeaveStatus("Approved"));

        btnReject = ModernTheme.createDangerButton("Reject Selected", new ImageIcon("images/delete.png"));
        btnReject.addActionListener(e -> updateSelectedLeaveStatus("Rejected"));

        actions.add(btnApprove);
        actions.add(btnReject);
        toolbar.add(actions, BorderLayout.EAST);

        return toolbar;
    }

    private int getSelectedLeaveId() {
        int row = leaveTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a leave request row first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return -1;
        }
        int modelRow = leaveTable.convertRowIndexToModel(row);
        return (Integer) tableModel.getValueAt(modelRow, 0);
    }

    public void loadLeaveRequests() {
        tableModel.setRowCount(0);
        int pending = 0, approved = 0, rejected = 0;

        try (Connection conn = clsConnection.getConnection()) {
            String sql = "SELECT l.Leave_Id, l.Emp_Code, e.Emp_Name1, e.Emp_name2, l.Leave_Type, " +
                    "l.Start_Date, l.End_Date, l.Days_Count, l.Reason, l.Status, l.Reviewed_By " +
                    "FROM LEAVE_REQUESTS l LEFT JOIN EMPLOYEE e ON l.Emp_Code = e.Emp_Code " +
                    "ORDER BY l.Leave_Id DESC";

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    int id = rs.getInt("Leave_Id");
                    String code = rs.getString("Emp_Code");
                    String n1 = rs.getString("Emp_Name1");
                    String n2 = rs.getString("Emp_name2");
                    String name = (n1 != null ? n1 : "") + " " + (n2 != null ? n2 : "");
                    String type = rs.getString("Leave_Type");
                    String start = rs.getString("Start_Date");
                    String end = rs.getString("End_Date");
                    double days = rs.getDouble("Days_Count");
                    String reason = rs.getString("Reason");
                    String status = rs.getString("Status");
                    String reviewer = rs.getString("Reviewed_By");

                    if ("Approved".equalsIgnoreCase(status)) approved++;
                    else if ("Rejected".equalsIgnoreCase(status)) rejected++;
                    else pending++;

                    tableModel.addRow(new Object[]{
                            id, code, name.trim(), type, start, end, days, reason, status, reviewer != null ? reviewer : "Pending"
                    });
                }
            }

            lblPendingCount.setText("Pending Review: " + pending);
            lblApprovedCount.setText("Approved: " + approved);
            lblRejectedCount.setText("Rejected: " + rejected);
            applyFilter();

        } catch (Exception ex) {
            System.err.println("Error loading leave requests: " + ex.getMessage());
        }
    }

    private void applyFilter() {
        String filter = (String) cmbStatusFilter.getSelectedItem();
        if (filter == null || filter.startsWith("All")) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)^" + filter + "$", 8));
        }
    }

    private void updateSelectedLeaveStatus(String newStatus) {
        if (!AuthService.canApproveLeave()) {
            JOptionPane.showMessageDialog(this, "Permission Denied: Only Administrator / HR / Manager can approve or reject leaves.", "Access Restricted", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int leaveId = getSelectedLeaveId();
        if (leaveId < 0) return;

        String remarks = JOptionPane.showInputDialog(this, "Enter approval / review remarks:", newStatus + " Remarks", JOptionPane.PLAIN_MESSAGE);
        if (remarks == null) return; // User cancelled

        try (Connection conn = clsConnection.getConnection()) {
            String sql = "UPDATE LEAVE_REQUESTS SET Status = ?, Reviewed_By = ?, Review_Notes = ? WHERE Leave_Id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, newStatus);
                pstmt.setString(2, AuthService.getCurrentUser());
                pstmt.setString(3, remarks);
                pstmt.setInt(4, leaveId);
                pstmt.executeUpdate();
            }

            AuditService.log(AuthService.getCurrentUser(), "LEAVE_STATUS_UPDATE", "LEAVE_REQUESTS",
                    "Leave Request #" + leaveId + " marked as " + newStatus + ". Remarks: " + remarks);

            NotificationService.send("Leave Request " + newStatus,
                    "Leave Request #" + leaveId + " has been marked as " + newStatus + " by " + AuthService.getCurrentUser(), "LEAVE");

            JOptionPane.showMessageDialog(this, "Leave Request #" + leaveId + " updated to " + newStatus + ".", "Status Updated", JOptionPane.INFORMATION_MESSAGE);
            loadLeaveRequests();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating leave request: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showApplyLeaveDialog() {
        JPanel form = new JPanel(new GridLayout(6, 2, 10, 10));

        JComboBox<String> cmbEmp = new JComboBox<>();
        loadEmployeeCombo(cmbEmp);

        JComboBox<String> cmbType = new JComboBox<>(new String[]{
                "Casual Leave", "Sick Leave", "Paid Annual Leave", "Maternity / Paternity", "Unpaid Leave"
        });

        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        JTextField txtStart = ModernTheme.createTextField(10);
        txtStart.setText(today);

        JTextField txtEnd = ModernTheme.createTextField(10);
        txtEnd.setText(today);

        JTextField txtDays = ModernTheme.createTextField(5);
        txtDays.setText("1.0");

        JTextField txtReason = ModernTheme.createTextField(15);
        txtReason.setText("Personal reasons");

        form.add(new JLabel("Employee:"));
        form.add(cmbEmp);
        form.add(new JLabel("Leave Type:"));
        form.add(cmbType);
        form.add(new JLabel("Start Date (YYYY-MM-DD):"));
        form.add(txtStart);
        form.add(new JLabel("End Date (YYYY-MM-DD):"));
        form.add(txtEnd);
        form.add(new JLabel("Number of Days:"));
        form.add(txtDays);
        form.add(new JLabel("Reason:"));
        form.add(txtReason);

        int opt = JOptionPane.showConfirmDialog(this, form, "Submit Time-Off Request", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opt == JOptionPane.OK_OPTION) {
            String empSel = (String) cmbEmp.getSelectedItem();
            if (empSel == null || empSel.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select an employee.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String code = empSel.split(" - ")[0].trim();
            String type = (String) cmbType.getSelectedItem();
            String start = txtStart.getText().trim();
            String end = txtEnd.getText().trim();
            double days = 1.0;
            try { days = Double.parseDouble(txtDays.getText().trim()); } catch (Exception ignored) {}
            String reason = txtReason.getText().trim();
            String appliedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());

            try (Connection conn = clsConnection.getConnection()) {
                String sql = "INSERT INTO LEAVE_REQUESTS (Emp_Code, Leave_Type, Start_Date, End_Date, Days_Count, Reason, Status, Applied_Date) " +
                        "VALUES (?, ?, ?, ?, ?, ?, 'Pending', ?)";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, code);
                    ps.setString(2, type);
                    ps.setString(3, start);
                    ps.setString(4, end);
                    ps.setDouble(5, days);
                    ps.setString(6, reason);
                    ps.setString(7, appliedDate);
                    ps.executeUpdate();
                }

                AuditService.log(AuthService.getCurrentUser(), "SUBMIT_LEAVE", "LEAVE_REQUESTS",
                        "Submitted " + days + " days " + type + " for employee " + code);

                NotificationService.send("New Leave Request: " + code,
                        "Employee " + code + " applied for " + days + " days " + type + ".", "LEAVE");

                JOptionPane.showMessageDialog(this, "Leave request submitted successfully!\nAwaiting HR/Manager review.", "Request Submitted", JOptionPane.INFORMATION_MESSAGE);
                loadLeaveRequests();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error submitting leave: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadEmployeeCombo(JComboBox<String> cmb) {
        cmb.removeAllItems();
        try (Connection conn = clsConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT Emp_Code, Emp_Name1, Emp_name2 FROM EMPLOYEE ORDER BY Emp_Code")) {
            while (rs.next()) {
                String c = rs.getString("Emp_Code");
                String n1 = rs.getString("Emp_Name1");
                String n2 = rs.getString("Emp_name2");
                cmb.addItem(c + " - " + (n1 != null ? n1 : "") + " " + (n2 != null ? n2 : ""));
            }
        } catch (Exception ignored) {}
    }
}
