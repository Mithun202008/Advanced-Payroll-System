import java.awt.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * ModernReportsCenter provides dedicated reports:
 * 1. Monthly Payroll Summary Report
 * 2. Attendance Register Report
 * 3. Leave Utilization Report
 * 4. Statutory Deductions & Tax Register
 * With live query filters and 1-click CSV export.
 */
public class ModernReportsCenter extends JPanel {
    private MainMenu parentMenu;

    private JComboBox<String> cmbReportType;
    private JComboBox<String> cmbMonth;
    private JTextField txtYear;
    private JComboBox<String> cmbDepartment;

    private JTable reportTable;
    private DefaultTableModel tableModel;

    private JLabel lblReportSummary;
    private JButton btnRunReport, btnExportCsv;

    private static final String[] REPORT_TYPES = {
            "Monthly Payroll Summary Report",
            "Attendance Register Report",
            "Leave Utilization Report",
            "Tax & Statutory Deductions Register"
    };

    private static final String[] MONTHS = {
            "All Months", "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };

    public ModernReportsCenter(MainMenu parent) {
        this.parentMenu = parent;
        setLayout(new BorderLayout(14, 14));
        setBackground(ModernTheme.SLATE_50);
        setBorder(new EmptyBorder(16, 20, 20, 20));

        // Top Filter Card
        add(createTopFilterCard(), BorderLayout.NORTH);

        // Center Table Card
        JPanel centerCard = ModernTheme.createCard();
        centerCard.setLayout(new BorderLayout(0, 10));

        setupReportTable();
        JScrollPane scrollPane = new JScrollPane(reportTable);
        scrollPane.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, ModernTheme.SLATE_200));
        centerCard.add(scrollPane, BorderLayout.CENTER);

        // Bottom Export Bar
        centerCard.add(createBottomToolbar(), BorderLayout.SOUTH);
        add(centerCard, BorderLayout.CENTER);

        // Run default report
        runSelectedReport();
    }

    private JPanel createTopFilterCard() {
        JPanel card = ModernTheme.createCard();
        card.setLayout(new BorderLayout(15, 10));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 3));
        titlePanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Enterprise Analytics & Statutory Reporting");
        lblTitle.setFont(ModernTheme.FONT_TITLE);
        lblTitle.setForeground(ModernTheme.TEXT_MAIN);

        JLabel lblSub = new JLabel("Audit-Ready Financial Statements, Attendance Matrices, and Statutory Registers");
        lblSub.setFont(ModernTheme.FONT_REGULAR);
        lblSub.setForeground(ModernTheme.TEXT_MUTED);

        titlePanel.add(lblTitle);
        titlePanel.add(lblSub);
        card.add(titlePanel, BorderLayout.NORTH);

        // Filter controls
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        filterRow.setOpaque(false);

        filterRow.add(new JLabel("Report Type:"));
        cmbReportType = new JComboBox<>(REPORT_TYPES);
        cmbReportType.setPreferredSize(new Dimension(240, 26));
        cmbReportType.addActionListener(e -> runSelectedReport());
        filterRow.add(cmbReportType);

        filterRow.add(new JLabel("Month:"));
        cmbMonth = new JComboBox<>(MONTHS);
        cmbMonth.setSelectedIndex(Calendar.getInstance().get(Calendar.MONTH) + 1);
        filterRow.add(cmbMonth);

        filterRow.add(new JLabel("Year:"));
        txtYear = ModernTheme.createTextField(5);
        txtYear.setText(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        filterRow.add(txtYear);

        filterRow.add(new JLabel("Department:"));
        cmbDepartment = new JComboBox<>(new String[]{"All Departments", "Engineering", "Product & Engineering", "Executive Management"});
        filterRow.add(cmbDepartment);

        btnRunReport = ModernTheme.createPrimaryButton("Generate Report", new ImageIcon("images/search.png"));
        btnRunReport.addActionListener(e -> runSelectedReport());
        filterRow.add(btnRunReport);

        card.add(filterRow, BorderLayout.CENTER);
        return card;
    }

    private void setupReportTable() {
        tableModel = new DefaultTableModel();
        reportTable = new JTable(tableModel);
        ModernTheme.styleTable(reportTable);
    }

    private JPanel createBottomToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout(10, 5));
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(8, 5, 5, 5));

        lblReportSummary = new JLabel("Report ready: 0 rows generated");
        lblReportSummary.setFont(ModernTheme.FONT_BOLD);
        lblReportSummary.setForeground(ModernTheme.TEXT_MAIN);
        toolbar.add(lblReportSummary, BorderLayout.WEST);

        btnExportCsv = ModernTheme.createSecondaryButton("Export Report to CSV", new ImageIcon("images/save_all.png"));
        btnExportCsv.addActionListener(e -> exportReportCsv());
        toolbar.add(btnExportCsv, BorderLayout.EAST);

        return toolbar;
    }

    public void runSelectedReport() {
        int idx = cmbReportType.getSelectedIndex();
        if (idx == 0) generatePayrollSummaryReport();
        else if (idx == 1) generateAttendanceRegisterReport();
        else if (idx == 2) generateLeaveUtilizationReport();
        else if (idx == 3) generateStatutoryTaxReport();
    }

    private void generatePayrollSummaryReport() {
        String[] cols = {"Emp Code", "Personnel Name", "Role", "Department", "Basic Salary", "Allowances", "Deductions", "Net Pay", "Cycle"};
        tableModel.setDataVector(new Object[][]{}, cols);

        double sumBasic = 0, sumAllow = 0, sumDedu = 0, sumNet = 0;
        int rowCount = 0;

        try (Connection conn = clsConnection.getConnection()) {
            String sql = "SELECT e.Emp_Code, e.Emp_Name1, e.Emp_name2, e.Emp_Desi, p.Department, " +
                    "s.Basic_Pay, s.DA_Allow, s.HRA_Allow, s.WA_Allow, s.GPF_Dedu, s.IT_Dedu, s.GIS_Dedu, s.PF_Dedu, s.LIC_Dedu, " +
                    "s.DA, s.HRA, s.WA, s.GPF, s.IT, s.GIS, s.PF, s.LIC " +
                    "FROM (EMPLOYEE e LEFT JOIN EMPLOYEE_PROFILE p ON e.Emp_Code = p.Emp_Code) " +
                    "LEFT JOIN Settings s ON e.Emp_Desi = s.Category_Name ORDER BY e.Emp_Code";

            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {

                while (rs.next()) {
                    String code = rs.getString("Emp_Code");
                    String n1 = rs.getString("Emp_Name1");
                    String n2 = rs.getString("Emp_name2");
                    String desi = rs.getString("Emp_Desi");
                    String dept = rs.getString("Department");
                    if (dept == null) dept = "General";

                    double bp = rs.getInt("Basic_Pay");
                    boolean daPct = "true".equalsIgnoreCase(rs.getString("DA"));
                    boolean hraPct = "true".equalsIgnoreCase(rs.getString("HRA"));
                    boolean waPct = "true".equalsIgnoreCase(rs.getString("WA"));
                    double allw = (daPct ? bp * rs.getInt("DA_Allow") / 100.0 : rs.getInt("DA_Allow"))
                            + (hraPct ? bp * rs.getInt("HRA_Allow") / 100.0 : rs.getInt("HRA_Allow"))
                            + (waPct ? bp * rs.getInt("WA_Allow") / 100.0 : rs.getInt("WA_Allow"));

                    boolean gpfPct = "true".equalsIgnoreCase(rs.getString("GPF"));
                    boolean itPct = "true".equalsIgnoreCase(rs.getString("IT"));
                    boolean gisPct = "true".equalsIgnoreCase(rs.getString("GIS"));
                    boolean pfPct = "true".equalsIgnoreCase(rs.getString("PF"));
                    boolean licPct = "true".equalsIgnoreCase(rs.getString("LIC"));
                    double dedu = (gpfPct ? bp * rs.getInt("GPF_Dedu") / 100.0 : rs.getInt("GPF_Dedu"))
                            + (itPct ? bp * rs.getInt("IT_Dedu") / 100.0 : rs.getInt("IT_Dedu"))
                            + (gisPct ? bp * rs.getInt("GIS_Dedu") / 100.0 : rs.getInt("GIS_Dedu"))
                            + (pfPct ? bp * rs.getInt("PF_Dedu") / 100.0 : rs.getInt("PF_Dedu"))
                            + (licPct ? bp * rs.getInt("LIC_Dedu") / 100.0 : rs.getInt("LIC_Dedu"));

                    double net = (bp + allw) - dedu;

                    sumBasic += bp;
                    sumAllow += allw;
                    sumDedu += dedu;
                    sumNet += net;
                    rowCount++;

                    tableModel.addRow(new Object[]{
                            code, (n1 != null ? n1 : "") + " " + (n2 != null ? n2 : ""), desi, dept,
                            "Rs " + String.format("%.2f", bp),
                            "Rs " + String.format("%.2f", allw),
                            "Rs " + String.format("%.2f", dedu),
                            "Rs " + String.format("%.2f", net),
                            cmbMonth.getSelectedItem() + " " + txtYear.getText().trim()
                    });
                }
            }

            lblReportSummary.setText(String.format("Generated: %d Staff • Total Net Payout: Rs %,.2f", rowCount, sumNet));

        } catch (Exception ex) {
            lblReportSummary.setText("Error generating report: " + ex.getMessage());
        }
    }

    private void generateAttendanceRegisterReport() {
        String[] cols = {"Emp Code", "Personnel Name", "Role", "Present Shifts", "Late Shifts", "Absences", "Overtime (Hrs)", "Attendance %"};
        tableModel.setDataVector(new Object[][]{}, cols);

        try (Connection conn = clsConnection.getConnection()) {
            String sql = "SELECT e.Emp_Code, e.Emp_Name1, e.Emp_name2, e.Emp_Desi FROM EMPLOYEE e ORDER BY e.Emp_Code";
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {

                while (rs.next()) {
                    String code = rs.getString("Emp_Code");
                    String n1 = rs.getString("Emp_Name1");
                    String n2 = rs.getString("Emp_name2");
                    String desi = rs.getString("Emp_Desi");

                    // Query stats for this employee
                    int pres = 0, late = 0, abs = 0;
                    double ot = 0.0;
                    try (PreparedStatement ps = conn.prepareStatement("SELECT Status, Overtime_Hours FROM ATTENDANCE WHERE Emp_Code = ?")) {
                        ps.setString(1, code);
                        try (ResultSet attRs = ps.executeQuery()) {
                            while (attRs.next()) {
                                String s = attRs.getString("Status");
                                if ("Present".equalsIgnoreCase(s)) pres++;
                                else if ("Late".equalsIgnoreCase(s)) late++;
                                else if ("Absent".equalsIgnoreCase(s)) abs++;
                                ot += attRs.getDouble("Overtime_Hours");
                            }
                        }
                    }

                    int total = pres + late + abs;
                    int rate = total > 0 ? ((pres + late) * 100 / total) : 100;

                    tableModel.addRow(new Object[]{
                            code, (n1 != null ? n1 : "") + " " + (n2 != null ? n2 : ""), desi,
                            pres, late, abs, ot, rate + "%"
                    });
                }
            }
            lblReportSummary.setText("Attendance Register: " + tableModel.getRowCount() + " records analyzed");
        } catch (Exception ex) {
            lblReportSummary.setText("Error: " + ex.getMessage());
        }
    }

    private void generateLeaveUtilizationReport() {
        String[] cols = {"Emp Code", "Personnel Name", "Annual Quota", "Casual Leaves", "Sick Leaves", "Total Taken", "Remaining Balance", "Utilization %"};
        tableModel.setDataVector(new Object[][]{}, cols);

        try (Connection conn = clsConnection.getConnection()) {
            String sql = "SELECT e.Emp_Code, e.Emp_Name1, e.Emp_name2 FROM EMPLOYEE e ORDER BY e.Emp_Code";
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {

                while (rs.next()) {
                    String code = rs.getString("Emp_Code");
                    String n1 = rs.getString("Emp_Name1");
                    String n2 = rs.getString("Emp_name2");

                    double casual = 0, sick = 0;
                    try (PreparedStatement ps = conn.prepareStatement("SELECT Leave_Type, Days_Count FROM LEAVE_REQUESTS WHERE Emp_Code = ? AND Status = 'Approved'")) {
                        ps.setString(1, code);
                        try (ResultSet lRs = ps.executeQuery()) {
                            while (lRs.next()) {
                                String t = lRs.getString("Leave_Type");
                                double d = lRs.getDouble("Days_Count");
                                if (t != null && t.toLowerCase().contains("casual")) casual += d;
                                else if (t != null && t.toLowerCase().contains("sick")) sick += d;
                            }
                        }
                    }

                    double totalTaken = casual + sick;
                    double quota = 24.0;
                    double balance = Math.max(0.0, quota - totalTaken);
                    int util = (int) ((totalTaken / quota) * 100);

                    tableModel.addRow(new Object[]{
                            code, (n1 != null ? n1 : "") + " " + (n2 != null ? n2 : ""),
                            "24 Days", casual + " Days", sick + " Days", totalTaken + " Days", balance + " Days", util + "%"
                    });
                }
            }
            lblReportSummary.setText("Leave Register: " + tableModel.getRowCount() + " personnel analyzed");
        } catch (Exception ex) {
            lblReportSummary.setText("Error: " + ex.getMessage());
        }
    }

    private void generateStatutoryTaxReport() {
        String[] cols = {"Emp Code", "Personnel Name", "Role", "Basic Pay", "GPF (Rs)", "Income Tax (Rs)", "GIS (Rs)", "PF (Rs)", "LIC (Rs)", "Total Statutory"};
        tableModel.setDataVector(new Object[][]{}, cols);

        double totGpf = 0, totIt = 0, totGis = 0, totPf = 0, totLic = 0, grandDedu = 0;

        try (Connection conn = clsConnection.getConnection()) {
            String sql = "SELECT e.Emp_Code, e.Emp_Name1, e.Emp_name2, e.Emp_Desi, " +
                    "s.Basic_Pay, s.GPF_Dedu, s.IT_Dedu, s.GIS_Dedu, s.PF_Dedu, s.LIC_Dedu, " +
                    "s.GPF, s.IT, s.GIS, s.PF, s.LIC " +
                    "FROM EMPLOYEE e LEFT JOIN Settings s ON e.Emp_Desi = s.Category_Name ORDER BY e.Emp_Code";

            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {

                while (rs.next()) {
                    String code = rs.getString("Emp_Code");
                    String n1 = rs.getString("Emp_Name1");
                    String n2 = rs.getString("Emp_name2");
                    String desi = rs.getString("Emp_Desi");
                    double bp = rs.getInt("Basic_Pay");

                    boolean gpfPct = "true".equalsIgnoreCase(rs.getString("GPF"));
                    boolean itPct = "true".equalsIgnoreCase(rs.getString("IT"));
                    boolean gisPct = "true".equalsIgnoreCase(rs.getString("GIS"));
                    boolean pfPct = "true".equalsIgnoreCase(rs.getString("PF"));
                    boolean licPct = "true".equalsIgnoreCase(rs.getString("LIC"));

                    double gpf = gpfPct ? (bp * rs.getInt("GPF_Dedu") / 100.0) : rs.getInt("GPF_Dedu");
                    double it = itPct ? (bp * rs.getInt("IT_Dedu") / 100.0) : rs.getInt("IT_Dedu");
                    double gis = gisPct ? (bp * rs.getInt("GIS_Dedu") / 100.0) : rs.getInt("GIS_Dedu");
                    double pf = pfPct ? (bp * rs.getInt("PF_Dedu") / 100.0) : rs.getInt("PF_Dedu");
                    double lic = licPct ? (bp * rs.getInt("LIC_Dedu") / 100.0) : rs.getInt("LIC_Dedu");
                    double tot = gpf + it + gis + pf + lic;

                    totGpf += gpf;
                    totIt += it;
                    totGis += gis;
                    totPf += pf;
                    totLic += lic;
                    grandDedu += tot;

                    tableModel.addRow(new Object[]{
                            code, (n1 != null ? n1 : "") + " " + (n2 != null ? n2 : ""), desi,
                            "Rs " + String.format("%.2f", bp),
                            "Rs " + String.format("%.2f", gpf),
                            "Rs " + String.format("%.2f", it),
                            "Rs " + String.format("%.2f", gis),
                            "Rs " + String.format("%.2f", pf),
                            "Rs " + String.format("%.2f", lic),
                            "Rs " + String.format("%.2f", tot)
                    });
                }
            }
            lblReportSummary.setText(String.format("Statutory Compliance Total Deductions: Rs %,.2f (Tax: Rs %,.2f, PF: Rs %,.2f)", grandDedu, totIt, totPf));
        } catch (Exception ex) {
            lblReportSummary.setText("Error: " + ex.getMessage());
        }
    }

    private void exportReportCsv() {
        JFileChooser chooser = new JFileChooser();
        String safeName = ((String) cmbReportType.getSelectedItem()).replaceAll("[^a-zA-Z0-9]", "_") + ".csv";
        chooser.setSelectedFile(new File(safeName));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
                int colCount = tableModel.getColumnCount();
                for (int c = 0; c < colCount; c++) {
                    pw.print("\"" + tableModel.getColumnName(c) + "\"" + (c < colCount - 1 ? "," : ""));
                }
                pw.println();

                for (int r = 0; r < tableModel.getRowCount(); r++) {
                    for (int c = 0; c < colCount; c++) {
                        Object val = tableModel.getValueAt(r, c);
                        pw.print("\"" + (val != null ? val.toString() : "") + "\"" + (c < colCount - 1 ? "," : ""));
                    }
                    pw.println();
                }

                JOptionPane.showMessageDialog(this, "Report successfully exported to:\n" + f.getAbsolutePath(), "Export Complete", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error exporting CSV: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
