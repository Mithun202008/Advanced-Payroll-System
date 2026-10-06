import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * ModernAttendanceManager provides daily attendance logging, status toggles,
 * overtime capture, batch check-in, and monthly attendance registers.
 */
public class ModernAttendanceManager extends JPanel {
    private MainMenu parentMenu;

    private JTextField txtDate;
    private JComboBox<String> cmbDeptFilter;
    private JTable attendanceTable;
    private DefaultTableModel tableModel;

    private JLabel lblPresentCount, lblLateCount, lblAbsentCount, lblAttendanceRate;
    private JButton btnSaveAll, btnMarkAllPresent, btnRefresh, btnExportCsv;

    private static final String[] STATUS_OPTIONS = {"Present", "Late", "Half Day", "Absent", "On Leave"};

    public ModernAttendanceManager(MainMenu parent) {
        this.parentMenu = parent;
        setLayout(new BorderLayout(14, 14));
        setBackground(ModernTheme.SLATE_50);
        setBorder(new EmptyBorder(16, 20, 20, 20));

        // Top Control Card
        add(createTopControlCard(), BorderLayout.NORTH);

        // Center Table Card
        JPanel centerCard = ModernTheme.createCard();
        centerCard.setLayout(new BorderLayout(0, 10));

        setupAttendanceTable();
        JScrollPane scrollPane = new JScrollPane(attendanceTable);
        scrollPane.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, ModernTheme.SLATE_200));
        centerCard.add(scrollPane, BorderLayout.CENTER);

        // Summary Counters at bottom of table
        centerCard.add(createSummaryFooter(), BorderLayout.SOUTH);
        add(centerCard, BorderLayout.CENTER);

        loadAttendanceForDate();
    }

    private JPanel createTopControlCard() {
        JPanel card = ModernTheme.createCard();
        card.setLayout(new BorderLayout(15, 10));

        JPanel leftPanel = new JPanel(new GridLayout(2, 1, 0, 3));
        leftPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Personnel Attendance & Shift Logging");
        lblTitle.setFont(ModernTheme.FONT_TITLE);
        lblTitle.setForeground(ModernTheme.TEXT_MAIN);

        JLabel lblSub = new JLabel("Daily Check-In, Shift Tracking, Overtime Capture, and Telemetry");
        lblSub.setFont(ModernTheme.FONT_REGULAR);
        lblSub.setForeground(ModernTheme.TEXT_MUTED);

        leftPanel.add(lblTitle);
        leftPanel.add(lblSub);
        card.add(leftPanel, BorderLayout.WEST);

        // Date selector and Action buttons
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        controlPanel.setOpaque(false);

        controlPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
        txtDate = ModernTheme.createTextField(10);
        txtDate.setText(today);
        controlPanel.add(txtDate);

        btnRefresh = ModernTheme.createSecondaryButton("Load Date", new ImageIcon("images/search.png"));
        btnRefresh.addActionListener(e -> loadAttendanceForDate());
        controlPanel.add(btnRefresh);

        btnMarkAllPresent = ModernTheme.createSecondaryButton("Mark All Present", new ImageIcon("images/ok.png"));
        btnMarkAllPresent.addActionListener(e -> markAllPresent());
        controlPanel.add(btnMarkAllPresent);

        btnSaveAll = ModernTheme.createPrimaryButton("Save Attendance", new ImageIcon("images/save_all.png"));
        btnSaveAll.addActionListener(e -> saveAttendanceToDatabase());
        controlPanel.add(btnSaveAll);

        card.add(controlPanel, BorderLayout.EAST);
        return card;
    }

    private void setupAttendanceTable() {
        String[] cols = {"Emp Code", "Personnel Name", "Role / Designation", "Status", "In Time", "Out Time", "Overtime (Hrs)", "Remarks"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                // Status, In Time, Out Time, Overtime, and Remarks are editable
                return col >= 3;
            }
        };

        attendanceTable = new JTable(tableModel);
        ModernTheme.styleTable(attendanceTable);

        attendanceTable.getColumnModel().getColumn(0).setPreferredWidth(85);
        attendanceTable.getColumnModel().getColumn(1).setPreferredWidth(160);
        attendanceTable.getColumnModel().getColumn(2).setPreferredWidth(140);
        attendanceTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        attendanceTable.getColumnModel().getColumn(4).setPreferredWidth(85);
        attendanceTable.getColumnModel().getColumn(5).setPreferredWidth(85);
        attendanceTable.getColumnModel().getColumn(6).setPreferredWidth(95);
        attendanceTable.getColumnModel().getColumn(7).setPreferredWidth(160);

        // Dropdown editor for Status column
        JComboBox<String> cmbStatus = new JComboBox<>(STATUS_OPTIONS);
        attendanceTable.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(cmbStatus));

        // Status renderer with color coding
        attendanceTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val, boolean isSel, boolean hasFoc, int row, int col) {
                String st = val != null ? val.toString() : "Present";
                String type = "SUCCESS";
                if ("Late".equalsIgnoreCase(st)) type = "WARNING";
                else if ("Absent".equalsIgnoreCase(st)) type = "DANGER";
                else if ("Half Day".equalsIgnoreCase(st) || "On Leave".equalsIgnoreCase(st)) type = "INFO";

                JLabel b = ModernTheme.createBadge(st, type);
                JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 4));
                p.setOpaque(isSel);
                p.setBackground(isSel ? ModernTheme.PRIMARY_LIGHT : (row % 2 == 0 ? Color.WHITE : ModernTheme.SLATE_50));
                p.add(b);
                return p;
            }
        });
    }

    private JPanel createSummaryFooter() {
        JPanel footer = new JPanel(new BorderLayout(15, 10));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(10, 5, 5, 5));

        JPanel kpiRow = new JPanel(new GridLayout(1, 4, 12, 5));
        kpiRow.setOpaque(false);

        kpiRow.add(createMiniCard("Present Today", lblPresentCount = new JLabel("0")));
        kpiRow.add(createMiniCard("Late Arrivals", lblLateCount = new JLabel("0")));
        kpiRow.add(createMiniCard("Absences", lblAbsentCount = new JLabel("0")));
        kpiRow.add(createMiniCard("Attendance Rate", lblAttendanceRate = new JLabel("0%")));
        lblAttendanceRate.setForeground(ModernTheme.SUCCESS.darker());

        footer.add(kpiRow, BorderLayout.CENTER);

        JPanel rightExport = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        rightExport.setOpaque(false);
        btnExportCsv = ModernTheme.createSecondaryButton("Export Attendance Register (CSV)", new ImageIcon("images/save_all.png"));
        btnExportCsv.addActionListener(e -> exportAttendanceCsv());
        rightExport.add(btnExportCsv);

        footer.add(rightExport, BorderLayout.SOUTH);
        return footer;
    }

    private JPanel createMiniCard(String title, JLabel val) {
        JPanel p = new JPanel(new GridLayout(2, 1, 0, 2));
        p.setBackground(ModernTheme.SLATE_100);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernTheme.SLATE_200, 1),
                new EmptyBorder(6, 10, 6, 10)
        ));

        JLabel lblT = new JLabel(title.toUpperCase());
        lblT.setFont(ModernTheme.FONT_SMALL_BOLD);
        lblT.setForeground(ModernTheme.TEXT_MUTED);

        val.setFont(ModernTheme.FONT_BOLD);
        val.setForeground(ModernTheme.TEXT_MAIN);

        p.add(lblT);
        p.add(val);
        return p;
    }

    public void loadAttendanceForDate() {
        String targetDate = txtDate.getText().trim();
        if (targetDate.isEmpty()) {
            targetDate = new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
            txtDate.setText(targetDate);
        }

        tableModel.setRowCount(0);
        int pres = 0, late = 0, abs = 0, leave = 0;

        try (Connection conn = clsConnection.getConnection()) {
            // Load existing attendance for this date or default from active employees
            String sql = "SELECT e.Emp_Code, e.Emp_Name1, e.Emp_name2, e.Emp_Desi, " +
                    "a.Status, a.Check_In, a.Check_Out, a.Overtime_Hours, a.Notes " +
                    "FROM EMPLOYEE e LEFT JOIN ATTENDANCE a ON (e.Emp_Code = a.Emp_Code AND a.Att_Date = ?) " +
                    "ORDER BY e.Emp_Code";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, targetDate);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String code = rs.getString("Emp_Code");
                        String n1 = rs.getString("Emp_Name1");
                        String n2 = rs.getString("Emp_name2");
                        String name = (n1 != null ? n1 : "") + " " + (n2 != null ? n2 : "");
                        String desi = rs.getString("Emp_Desi");

                        String status = rs.getString("Status");
                        if (status == null || status.trim().isEmpty()) status = "Present";

                        String inTime = rs.getString("Check_In");
                        if (inTime == null || inTime.trim().isEmpty()) inTime = "09:00";

                        String outTime = rs.getString("Check_Out");
                        if (outTime == null || outTime.trim().isEmpty()) outTime = "18:00";

                        double ot = rs.getDouble("Overtime_Hours");
                        String notes = rs.getString("Notes");
                        if (notes == null) notes = "Regular Shift";

                        if ("Present".equalsIgnoreCase(status)) pres++;
                        else if ("Late".equalsIgnoreCase(status)) late++;
                        else if ("Absent".equalsIgnoreCase(status)) abs++;
                        else leave++;

                        tableModel.addRow(new Object[]{
                                code, name.trim(), desi != null ? desi : "", status, inTime, outTime, ot, notes
                        });
                    }
                }
            }

            lblPresentCount.setText(String.valueOf(pres));
            lblLateCount.setText(String.valueOf(late));
            lblAbsentCount.setText(String.valueOf(abs));
            int total = pres + late + abs + leave;
            int rate = total > 0 ? ((pres + late) * 100 / total) : 100;
            lblAttendanceRate.setText(rate + "%");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading attendance: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void markAllPresent() {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            tableModel.setValueAt("Present", i, 3);
            tableModel.setValueAt("09:00", i, 4);
            tableModel.setValueAt("18:00", i, 5);
        }
        recalculateCounters();
    }

    private void recalculateCounters() {
        int pres = 0, late = 0, abs = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String st = (String) tableModel.getValueAt(i, 3);
            if ("Present".equalsIgnoreCase(st)) pres++;
            else if ("Late".equalsIgnoreCase(st)) late++;
            else if ("Absent".equalsIgnoreCase(st)) abs++;
        }
        lblPresentCount.setText(String.valueOf(pres));
        lblLateCount.setText(String.valueOf(late));
        lblAbsentCount.setText(String.valueOf(abs));
        int total = tableModel.getRowCount();
        int rate = total > 0 ? ((pres + late) * 100 / total) : 100;
        lblAttendanceRate.setText(rate + "%");
    }

    private void saveAttendanceToDatabase() {
        String targetDate = txtDate.getText().trim();
        if (targetDate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid Date.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = clsConnection.getConnection()) {
            conn.setAutoCommit(false);

            // Delete old records for this date
            try (PreparedStatement del = conn.prepareStatement("DELETE FROM ATTENDANCE WHERE Att_Date = ?")) {
                del.setString(1, targetDate);
                del.executeUpdate();
            }

            // Insert updated rows
            String ins = "INSERT INTO ATTENDANCE (Emp_Code, Att_Date, Status, Check_In, Check_Out, Overtime_Hours, Notes) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(ins)) {
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    String code = (String) tableModel.getValueAt(i, 0);
                    String status = (String) tableModel.getValueAt(i, 3);
                    String inTime = (String) tableModel.getValueAt(i, 4);
                    String outTime = (String) tableModel.getValueAt(i, 5);
                    Object otObj = tableModel.getValueAt(i, 6);
                    double ot = 0.0;
                    if (otObj != null) {
                        try { ot = Double.parseDouble(otObj.toString()); } catch (Exception ignored) {}
                    }
                    String notes = (String) tableModel.getValueAt(i, 7);

                    ps.setString(1, code);
                    ps.setString(2, targetDate);
                    ps.setString(3, status != null ? status : "Present");
                    ps.setString(4, inTime != null ? inTime : "09:00");
                    ps.setString(5, outTime != null ? outTime : "18:00");
                    ps.setDouble(6, ot);
                    ps.setString(7, notes != null ? notes : "");
                    ps.executeUpdate();
                }
            }

            conn.commit();
            conn.setAutoCommit(true);

            AuditService.log(AuthService.getCurrentUser(), "ATTENDANCE_SAVED", "ATTENDANCE",
                    "Saved attendance for " + tableModel.getRowCount() + " employees on " + targetDate);

            JOptionPane.showMessageDialog(this,
                    "Attendance for " + targetDate + " successfully saved into the database!",
                    "Attendance Saved", JOptionPane.INFORMATION_MESSAGE);

            recalculateCounters();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to save attendance:\n" + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportAttendanceCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("Attendance_Register_" + txtDate.getText().trim() + ".csv"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
                pw.println("Emp_Code,Personnel_Name,Designation,Status,Check_In,Check_Out,Overtime_Hours,Remarks");
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    pw.println(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",%s,\"%s\"",
                            tableModel.getValueAt(i, 0),
                            tableModel.getValueAt(i, 1),
                            tableModel.getValueAt(i, 2),
                            tableModel.getValueAt(i, 3),
                            tableModel.getValueAt(i, 4),
                            tableModel.getValueAt(i, 5),
                            tableModel.getValueAt(i, 6),
                            tableModel.getValueAt(i, 7)));
                }
                JOptionPane.showMessageDialog(this, "Attendance register saved to:\n" + f.getAbsolutePath(), "Export Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error exporting CSV: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
