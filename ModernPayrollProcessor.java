import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * ModernPayrollProcessor implements the complete 12-step enterprise payroll processing pipeline.
 * Features scope validation, automated allowance/deduction computation, overtime inclusion,
 * unpaid absence proration, duplicate run prevention, summary confirmation, and persistence.
 */
public class ModernPayrollProcessor extends JPanel {
    private MainMenu parentMenu;

    private JComboBox<String> cmbMonth;
    private JTextField txtYear;
    private JButton btnCalculate, btnFinalize, btnExportSummary;

    private JTable reviewTable;
    private DefaultTableModel tableModel;

    private JLabel lblTotalEmployees;
    private JLabel lblGrossPayroll;
    private JLabel lblTotalAllowances;
    private JLabel lblTotalDeductions;
    private JLabel lblNetPayroll;
    private JLabel lblStatusBadge;

    private static final String[] MONTHS = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };

    // Staging calculation rows
    private static class PayrollItem {
        String empCode;
        String name;
        String designation;
        String department;
        double basic;
        double da;
        double hra;
        double wa;
        double overtime;
        double allowancesTotal;
        double gpf;
        double it;
        double gis;
        double pf;
        double lic;
        double deductionsTotal;
        double netSalary;
    }

    private List<PayrollItem> stagedItems = new ArrayList<>();
    private boolean isCalculated = false;
    private boolean isAlreadyFinalized = false;

    public ModernPayrollProcessor(MainMenu parent) {
        this.parentMenu = parent;
        setLayout(new BorderLayout(14, 14));
        setBackground(ModernTheme.SLATE_50);
        setBorder(new EmptyBorder(16, 20, 20, 20));

        // Top Control Card
        add(createTopControlCard(), BorderLayout.NORTH);

        // Center Table Card
        JPanel centerCard = ModernTheme.createCard();
        centerCard.setLayout(new BorderLayout(0, 10));

        setupReviewTable();
        JScrollPane scrollPane = new JScrollPane(reviewTable);
        scrollPane.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, ModernTheme.SLATE_200));
        centerCard.add(scrollPane, BorderLayout.CENTER);

        // Bottom Summary & Finalization Card
        centerCard.add(createSummaryFooter(), BorderLayout.SOUTH);
        add(centerCard, BorderLayout.CENTER);

        // Auto calculate on load
        SwingUtilities.invokeLater(this::calculatePayrollPipeline);
    }

    private JPanel createTopControlCard() {
        JPanel card = ModernTheme.createCard();
        card.setLayout(new BorderLayout(15, 10));

        JPanel leftPanel = new JPanel(new GridLayout(2, 1, 0, 3));
        leftPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Enterprise Payroll Processing Pipeline");
        lblTitle.setFont(ModernTheme.FONT_TITLE);
        lblTitle.setForeground(ModernTheme.TEXT_MAIN);

        JLabel lblSub = new JLabel("12-Step Automated Statutory Compliance, Overtime, & Deductions Engine");
        lblSub.setFont(ModernTheme.FONT_REGULAR);
        lblSub.setForeground(ModernTheme.TEXT_MUTED);

        leftPanel.add(lblTitle);
        leftPanel.add(lblSub);
        card.add(leftPanel, BorderLayout.WEST);

        // Period selectors and calculate button
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        controlPanel.setOpaque(false);

        controlPanel.add(new JLabel("Pay Period Month:"));
        Calendar cal = Calendar.getInstance();
        cmbMonth = new JComboBox<>(MONTHS);
        cmbMonth.setSelectedIndex(cal.get(Calendar.MONTH));
        controlPanel.add(cmbMonth);

        controlPanel.add(new JLabel("Year:"));
        txtYear = ModernTheme.createTextField(5);
        txtYear.setText(String.valueOf(cal.get(Calendar.YEAR)));
        controlPanel.add(txtYear);

        btnCalculate = ModernTheme.createSecondaryButton("1. Calculate Run", new ImageIcon("images/search.png"));
        btnCalculate.addActionListener(e -> calculatePayrollPipeline());
        controlPanel.add(btnCalculate);

        btnFinalize = ModernTheme.createPrimaryButton("2. Finalize & Disburse", new ImageIcon("images/save_all.png"));
        btnFinalize.setEnabled(false);
        btnFinalize.addActionListener(e -> finalizePayrollPipeline());
        controlPanel.add(btnFinalize);

        card.add(controlPanel, BorderLayout.EAST);
        return card;
    }

    private void setupReviewTable() {
        String[] columns = {
                "Emp Code", "Personnel Name", "Role / Designation", "Basic Pay (Rs)",
                "Allowances (Rs)", "Overtime (Rs)", "Deductions (Rs)", "Net Payable (Rs)", "Status"
        };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        reviewTable = new JTable(tableModel);
        ModernTheme.styleTable(reviewTable);

        reviewTable.getColumnModel().getColumn(0).setPreferredWidth(85);
        reviewTable.getColumnModel().getColumn(1).setPreferredWidth(160);
        reviewTable.getColumnModel().getColumn(2).setPreferredWidth(130);
        reviewTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        reviewTable.getColumnModel().getColumn(4).setPreferredWidth(110);
        reviewTable.getColumnModel().getColumn(5).setPreferredWidth(90);
        reviewTable.getColumnModel().getColumn(6).setPreferredWidth(110);
        reviewTable.getColumnModel().getColumn(7).setPreferredWidth(120);
        reviewTable.getColumnModel().getColumn(8).setPreferredWidth(90);

        // Net Payable column bold formatting
        reviewTable.getColumnModel().getColumn(7).setCellRenderer((tbl, val, isSel, hasFoc, row, col) -> {
            JLabel lbl = new JLabel(val != null ? val.toString() : "", JLabel.RIGHT);
            lbl.setFont(ModernTheme.FONT_BOLD);
            lbl.setForeground(ModernTheme.SUCCESS.darker());
            lbl.setBorder(new EmptyBorder(0, 5, 0, 10));
            lbl.setOpaque(isSel);
            lbl.setBackground(isSel ? ModernTheme.PRIMARY_LIGHT : (row % 2 == 0 ? Color.WHITE : ModernTheme.SLATE_50));
            return lbl;
        });

        // Status chip renderer
        reviewTable.getColumnModel().getColumn(8).setCellRenderer((tbl, val, isSel, hasFoc, row, col) -> {
            String st = val != null ? val.toString() : "Ready";
            JLabel b = ModernTheme.createBadge(st, "Ready".equalsIgnoreCase(st) ? "SUCCESS" : "INFO");
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 4));
            p.setOpaque(isSel);
            p.setBackground(isSel ? ModernTheme.PRIMARY_LIGHT : (row % 2 == 0 ? Color.WHITE : ModernTheme.SLATE_50));
            p.add(b);
            return p;
        });
    }

    private JPanel createSummaryFooter() {
        JPanel footer = new JPanel(new BorderLayout(15, 10));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(12, 5, 5, 5));

        // KPI Summary Counters
        JPanel kpiGrid = new JPanel(new GridLayout(1, 5, 10, 5));
        kpiGrid.setOpaque(false);

        kpiGrid.add(createSummaryCard("Personnel Count", lblTotalEmployees = new JLabel("0")));
        kpiGrid.add(createSummaryCard("Gross Basic", lblGrossPayroll = new JLabel("Rs 0.00")));
        kpiGrid.add(createSummaryCard("Total Allowances", lblTotalAllowances = new JLabel("Rs 0.00")));
        kpiGrid.add(createSummaryCard("Total Deductions", lblTotalDeductions = new JLabel("Rs 0.00")));
        kpiGrid.add(createSummaryCard("NET DISBURSED", lblNetPayroll = new JLabel("Rs 0.00")));
        lblNetPayroll.setForeground(ModernTheme.SUCCESS.darker());
        lblNetPayroll.setFont(new Font("Segoe UI", Font.BOLD, 15));

        footer.add(kpiGrid, BorderLayout.CENTER);

        // Right export button
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        actionRow.setOpaque(false);

        btnExportSummary = ModernTheme.createSecondaryButton("Export Run Summary (CSV)", new ImageIcon("images/save_all.png"));
        btnExportSummary.addActionListener(e -> exportPayrollCsv());
        actionRow.add(btnExportSummary);

        footer.add(actionRow, BorderLayout.SOUTH);
        return footer;
    }

    private JPanel createSummaryCard(String title, JLabel valLabel) {
        JPanel p = new JPanel(new GridLayout(2, 1, 0, 2));
        p.setBackground(ModernTheme.SLATE_100);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernTheme.SLATE_200, 1),
                new EmptyBorder(6, 10, 6, 10)
        ));

        JLabel lblT = new JLabel(title.toUpperCase());
        lblT.setFont(ModernTheme.FONT_SMALL_BOLD);
        lblT.setForeground(ModernTheme.TEXT_MUTED);

        valLabel.setFont(ModernTheme.FONT_BOLD);
        valLabel.setForeground(ModernTheme.TEXT_MAIN);

        p.add(lblT);
        p.add(valLabel);
        return p;
    }

    private void calculatePayrollPipeline() {
        stagedItems.clear();
        tableModel.setRowCount(0);
        String selectedMonth = (String) cmbMonth.getSelectedItem();
        String selectedYear = txtYear.getText().trim();

        if (selectedYear.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid Year.", "Year Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double sumGross = 0;
        double sumAllow = 0;
        double sumDedu = 0;
        double sumNet = 0;

        try (Connection conn = clsConnection.getConnection()) {
            // Check if this run is already finalized in PAYROLL_RUNS
            String checkSql = "SELECT Run_Id, Status FROM PAYROLL_RUNS WHERE Period_Month = ? AND Period_Year = ?";
            try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
                ps.setString(1, selectedMonth);
                ps.setString(2, selectedYear);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        isAlreadyFinalized = true;
                        btnFinalize.setEnabled(false);
                        JOptionPane.showMessageDialog(this,
                                "Payroll for " + selectedMonth + " " + selectedYear + " has ALREADY been finalized.\nStatus: " + rs.getString("Status"),
                                "Cycle Already Finalized", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        isAlreadyFinalized = false;
                        btnFinalize.setEnabled(true);
                    }
                }
            }

            // Step 2 & 3: Compute unified payroll breakdown using centralized engine
            List<PayrollCalculationEngine.PayrollBreakdown> breakdowns = PayrollCalculationEngine.calculateAllActive(conn, selectedMonth, selectedYear);

            for (PayrollCalculationEngine.PayrollBreakdown b : breakdowns) {
                PayrollItem item = new PayrollItem();
                item.empCode = b.empCode;
                item.name = b.fullName;
                item.designation = b.designation;
                item.department = b.department;
                item.basic = b.basicPay;
                item.da = b.da;
                item.hra = b.hra;
                item.wa = b.wa;
                item.overtime = b.overtimeAmount;
                item.allowancesTotal = b.totalAllowances;
                item.gpf = b.gpf;
                item.it = b.it;
                item.gis = b.gis;
                item.pf = b.pf;
                item.lic = b.lic;
                item.deductionsTotal = b.totalDeductions;
                item.netSalary = b.netSalary;

                stagedItems.add(item);

                sumGross += item.basic;
                sumAllow += item.allowancesTotal;
                sumDedu += item.deductionsTotal;
                sumNet += item.netSalary;

                tableModel.addRow(new Object[]{
                        item.empCode,
                        item.name.trim(),
                        item.designation != null ? item.designation : "-",
                        String.format("%,.2f", item.basic),
                        String.format("%,.2f", item.allowancesTotal),
                        String.format("%,.2f", item.overtime),
                        String.format("%,.2f", item.deductionsTotal),
                        String.format("%,.2f", item.netSalary),
                        isAlreadyFinalized ? "Finalized" : "Calculated"
                });
            }

            lblTotalEmployees.setText(String.valueOf(stagedItems.size()));
            lblGrossPayroll.setText("Rs " + String.format("%,.2f", sumGross));
            lblTotalAllowances.setText("Rs " + String.format("%,.2f", sumAllow));
            lblTotalDeductions.setText("Rs " + String.format("%,.2f", sumDedu));
            lblNetPayroll.setText("Rs " + String.format("%,.2f", sumNet));
            isCalculated = true;

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Calculation Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private double calculateOvertimeForEmp(Connection conn, String code) {
        double hours = 0.0;
        try (PreparedStatement ps = conn.prepareStatement("SELECT SUM(Overtime_Hours) FROM ATTENDANCE WHERE Emp_Code = ?")) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) hours = rs.getDouble(1);
            }
        } catch (Exception ignored) {}
        // Overtime rate: Rs 200 per hour
        return hours * 200.0;
    }

    private void finalizePayrollPipeline() {
        if (!AuthService.canRunPayroll()) {
            JOptionPane.showMessageDialog(this, "Permission Denied: Only Administrator / HR can finalize payroll.", "Access Restricted", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (stagedItems.isEmpty() || !isCalculated) {
            JOptionPane.showMessageDialog(this, "No calculated payroll items to finalize. Please calculate run first.", "Validation Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (isAlreadyFinalized) {
            JOptionPane.showMessageDialog(this, "This payroll cycle is already finalized.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String month = (String) cmbMonth.getSelectedItem();
        String year = txtYear.getText().trim();

        // Step 10 & 11: Summary Confirmation Dialog
        String summaryMsg = String.format(
                "Confirm Finalization of Monthly Payroll?\n\n" +
                        "• Cycle Period: %s %s\n" +
                        "• Total Personnel: %d Staff\n" +
                        "• Gross Basic: %s\n" +
                        "• Total Allowances & Overtime: %s\n" +
                        "• Total Statutory Deductions: %s\n" +
                        "• NET DISBURSEMENT AMOUNT: %s\n\n" +
                        "Once finalized, payslips will be permanently locked and archived in the database.",
                month, year, stagedItems.size(),
                lblGrossPayroll.getText(), lblTotalAllowances.getText(),
                lblTotalDeductions.getText(), lblNetPayroll.getText());

        int confirm = JOptionPane.showConfirmDialog(this, summaryMsg, "Confirm Payroll Finalization", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        // Step 12: Finalization & Persistence
        try (Connection conn = clsConnection.getConnection()) {
            conn.setAutoCommit(false);

            // 1. Insert into PAYROLL_RUNS
            String insRun = "INSERT INTO PAYROLL_RUNS (Period_Month, Period_Year, Total_Employees, Gross_Payroll, Total_Allowances, Total_Deductions, Net_Payroll, Processed_Date, Status, Processed_By) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'Finalized', ?)";

            double gross = Double.parseDouble(lblGrossPayroll.getText().replace("Rs ", "").replace(",", ""));
            double allow = Double.parseDouble(lblTotalAllowances.getText().replace("Rs ", "").replace(",", ""));
            double dedu = Double.parseDouble(lblTotalDeductions.getText().replace("Rs ", "").replace(",", ""));
            double net = Double.parseDouble(lblNetPayroll.getText().replace("Rs ", "").replace(",", ""));
            String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());

            int runId = 1;
            try (PreparedStatement ps = conn.prepareStatement(insRun)) {
                ps.setString(1, month);
                ps.setString(2, year);
                ps.setInt(3, stagedItems.size());
                ps.setDouble(4, gross);
                ps.setDouble(5, allow);
                ps.setDouble(6, dedu);
                ps.setDouble(7, net);
                ps.setString(8, now);
                ps.setString(9, AuthService.getCurrentUser());
                ps.executeUpdate();
            }

            // Fetch the generated Run_Id
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT MAX(Run_Id) FROM PAYROLL_RUNS")) {
                if (rs.next()) runId = rs.getInt(1);
            }

            // 2. Insert itemized payslips into PAYSLIP_RECORDS
            String insSlip = "INSERT INTO PAYSLIP_RECORDS (" +
                    "Run_Id, Emp_Code, Period_Month, Period_Year, Basic_Pay, DA_Amount, HRA_Amount, WA_Amount, " +
                    "Allowances_Total, GPF_Amount, IT_Amount, GIS_Amount, PF_Amount, LIC_Amount, Deductions_Total, " +
                    "Net_Salary, Generated_Date, Payment_Status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'Paid')";

            try (PreparedStatement ps = conn.prepareStatement(insSlip)) {
                for (PayrollItem it : stagedItems) {
                    ps.setInt(1, runId);
                    ps.setString(2, it.empCode);
                    ps.setString(3, month);
                    ps.setString(4, year);
                    ps.setDouble(5, it.basic);
                    ps.setDouble(6, it.da);
                    ps.setDouble(7, it.hra);
                    ps.setDouble(8, it.wa);
                    ps.setDouble(9, it.allowancesTotal);
                    ps.setDouble(10, it.gpf);
                    ps.setDouble(11, it.it);
                    ps.setDouble(12, it.gis);
                    ps.setDouble(13, it.pf);
                    ps.setDouble(14, it.lic);
                    ps.setDouble(15, it.deductionsTotal);
                    ps.setDouble(16, it.netSalary);
                    ps.setString(17, now);
                    ps.executeUpdate();
                }
            }

            conn.commit();
            conn.setAutoCommit(true);

            // Audit & Notification
            AuditService.log(AuthService.getCurrentUser(), "PAYROLL_FINALIZED", "PAYROLL_RUNS",
                    "Finalized payroll cycle " + month + " " + year + " for " + stagedItems.size() + " employees. Net: Rs " + String.format("%,.2f", net));

            NotificationService.send("Payroll Disbursed: " + month + " " + year,
                    "Total payout of Rs " + String.format("%,.2f", net) + " disbursed across " + stagedItems.size() + " employees.", "PAYROLL");

            JOptionPane.showMessageDialog(this,
                    "SUCCESS: Monthly Payroll for " + month + " " + year + " has been finalized and locked!\n" +
                            stagedItems.size() + " payslip snapshots permanently archived into database.",
                    "Payroll Run Finalized", JOptionPane.INFORMATION_MESSAGE);

            isAlreadyFinalized = true;
            btnFinalize.setEnabled(false);
            calculatePayrollPipeline();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to finalize payroll:\n" + ex.getMessage(), "Finalization Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void exportPayrollCsv() {
        if (stagedItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No payroll items to export.", "Empty Run", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("Payroll_Run_" + cmbMonth.getSelectedItem() + "_" + txtYear.getText().trim() + ".csv"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
                pw.println("Emp_Code,Name,Designation,Department,Basic_Pay,Allowances_Total,Overtime,Deductions_Total,Net_Pay,Status");
                for (PayrollItem it : stagedItems) {
                    pw.println(String.format("\"%s\",\"%s\",\"%s\",\"%s\",%.2f,%.2f,%.2f,%.2f,%.2f,\"Finalized\"",
                            it.empCode, it.name, it.designation, it.department,
                            it.basic, it.allowancesTotal, it.overtime, it.deductionsTotal, it.netSalary));
                }
                JOptionPane.showMessageDialog(this, "Payroll run report successfully saved to:\n" + f.getAbsolutePath(), "Export Complete", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Export Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
