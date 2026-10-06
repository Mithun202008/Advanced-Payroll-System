import java.awt.*;
import java.sql.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * ModernEmployeeProfile provides an executive 360-degree employee view
 * across 5 tabs: Overview & Statutory, Salary Structure, Attendance History,
 * Leave Balance, and Generated Payslips Archive.
 */
public class ModernEmployeeProfile extends JPanel {
    private MainMenu parentMenu;
    private String empCode;

    // Header info labels
    private JLabel lblAvatar, lblFullName, lblRole, lblDept, lblStatusBadge;
    private JLabel lblEmail, lblPhone, lblAddress;

    // Tabbed Pane
    private JTabbedPane tabbedPane;

    public ModernEmployeeProfile(MainMenu parent, String employeeCode) {
        this.parentMenu = parent;
        this.empCode = employeeCode;

        setLayout(new BorderLayout(14, 14));
        setBackground(ModernTheme.SLATE_50);
        setBorder(new EmptyBorder(16, 20, 20, 20));

        // Top Header Banner Card
        add(createProfileHeaderCard(), BorderLayout.NORTH);

        // Center: Tabbed Panels
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(ModernTheme.FONT_BOLD);
        tabbedPane.setBackground(Color.WHITE);

        tabbedPane.addTab("Personal & Banking", new ImageIcon("images/employee.png"), createPersonalAndBankingTab());
        tabbedPane.addTab("Salary Structure", new ImageIcon("images/setting.png"), createSalaryStructureTab());
        tabbedPane.addTab("Attendance History", new ImageIcon("images/Attendance.png"), createAttendanceHistoryTab());
        tabbedPane.addTab("Leave Balance", new ImageIcon("images/Advances.png"), createLeaveHistoryTab());
        tabbedPane.addTab("Payslips Archive", new ImageIcon("images/emp_rpt.png"), createPayslipArchiveTab());

        add(tabbedPane, BorderLayout.CENTER);

        loadProfileDetails();
    }

    private JPanel createProfileHeaderCard() {
        JPanel card = ModernTheme.createCard();
        card.setLayout(new BorderLayout(15, 10));

        // Left: Avatar + Details
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setOpaque(false);

        lblAvatar = new JLabel("EM", JLabel.CENTER) {
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
        lblAvatar.setPreferredSize(new Dimension(54, 54));
        lblAvatar.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblAvatar.setForeground(Color.WHITE);
        leftPanel.add(lblAvatar);

        JPanel details = new JPanel(new GridLayout(3, 1, 0, 3));
        details.setOpaque(false);

        JPanel nameRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        nameRow.setOpaque(false);
        lblFullName = new JLabel("Loading Employee...");
        lblFullName.setFont(ModernTheme.FONT_TITLE);
        lblFullName.setForeground(ModernTheme.TEXT_MAIN);
        lblStatusBadge = ModernTheme.createBadge("Active", "SUCCESS");
        nameRow.add(lblFullName);
        nameRow.add(lblStatusBadge);

        JPanel subRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        subRow.setOpaque(false);
        lblRole = new JLabel("Role: -");
        lblRole.setFont(ModernTheme.FONT_BOLD);
        lblRole.setForeground(ModernTheme.TEXT_MUTED);
        lblDept = new JLabel("Department: -");
        lblDept.setFont(ModernTheme.FONT_REGULAR);
        lblDept.setForeground(ModernTheme.TEXT_MUTED);
        subRow.add(lblRole);
        subRow.add(lblDept);

        JPanel contactRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        contactRow.setOpaque(false);
        lblEmail = new JLabel("Email: -");
        lblEmail.setFont(ModernTheme.FONT_SMALL);
        lblEmail.setForeground(ModernTheme.TEXT_MUTED);
        lblPhone = new JLabel("Phone: -");
        lblPhone.setFont(ModernTheme.FONT_SMALL);
        lblPhone.setForeground(ModernTheme.TEXT_MUTED);
        lblAddress = new JLabel("Location: -");
        lblAddress.setFont(ModernTheme.FONT_SMALL);
        lblAddress.setForeground(ModernTheme.TEXT_MUTED);
        contactRow.add(lblEmail);
        contactRow.add(lblPhone);
        contactRow.add(lblAddress);

        details.add(nameRow);
        details.add(subRow);
        details.add(contactRow);
        leftPanel.add(details);

        card.add(leftPanel, BorderLayout.CENTER);

        // Right: Navigation / Quick Action Buttons
        JPanel actionBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 10));
        actionBtns.setOpaque(false);

        JButton btnBack = ModernTheme.createSecondaryButton("Back to Directory", new ImageIcon("images/1leftarrow.png"));
        btnBack.addActionListener(e -> parentMenu.openEmployeeList());

        JButton btnPayslip = ModernTheme.createSecondaryButton("Generate PaySlip", new ImageIcon("images/emp_rpt.png"));
        btnPayslip.addActionListener(e -> parentMenu.openPayslip(empCode));

        JButton btnEdit = ModernTheme.createPrimaryButton("Edit Record", new ImageIcon("images/edit.png"));
        btnEdit.addActionListener(e -> parentMenu.openEditEmployee(empCode));

        actionBtns.add(btnBack);
        actionBtns.add(btnPayslip);
        actionBtns.add(btnEdit);

        card.add(actionBtns, BorderLayout.EAST);
        return card;
    }

    private JPanel createPersonalAndBankingTab() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 14, 14));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(16, 18, 16, 18));

        // Column 1: Personal & Employment Details
        JPanel col1 = new JPanel();
        col1.setLayout(new BoxLayout(col1, BoxLayout.Y_AXIS));
        col1.setOpaque(false);

        JLabel h1 = new JLabel("Employment Information");
        h1.setFont(ModernTheme.FONT_SECTION);
        h1.setForeground(ModernTheme.PRIMARY);
        col1.add(h1);
        col1.add(Box.createVerticalStrut(10));

        col1.add(createFieldRow("Employee ID:", txtEmpId = createROField()));
        col1.add(createFieldRow("First Name:", txtFirst = createROField()));
        col1.add(createFieldRow("Last Name:", txtLast = createROField()));
        col1.add(createFieldRow("Designation:", txtDesi = createROField()));
        col1.add(createFieldRow("Department:", txtDept = createROField()));
        col1.add(createFieldRow("Employment Type:", txtEmpType = createROField()));
        col1.add(createFieldRow("Date of Joining:", txtJoinDate = createROField()));

        panel.add(col1);

        // Column 2: Banking & Statutory
        JPanel col2 = new JPanel();
        col2.setLayout(new BoxLayout(col2, BoxLayout.Y_AXIS));
        col2.setOpaque(false);

        JLabel h2 = new JLabel("Statutory & Banking Details");
        h2.setFont(ModernTheme.FONT_SECTION);
        h2.setForeground(ModernTheme.PRIMARY);
        col2.add(h2);
        col2.add(Box.createVerticalStrut(10));

        col2.add(createFieldRow("Bank Name:", txtBankName = createROField()));
        col2.add(createFieldRow("Account Number:", txtBankAcc = createROField()));
        col2.add(createFieldRow("PAN / Tax Number:", txtPan = createROField()));
        col2.add(createFieldRow("Contact Number:", txtPhone = createROField()));
        col2.add(createFieldRow("Official Email:", txtEmail = createROField()));
        col2.add(createFieldRow("Residential Address:", txtAddress = createROField()));

        panel.add(col2);
        return panel;
    }

    private JTextField txtEmpId, txtFirst, txtLast, txtDesi, txtDept, txtEmpType, txtJoinDate;
    private JTextField txtBankName, txtBankAcc, txtPan, txtPhone, txtEmail, txtAddress;

    private JTextField createROField() {
        JTextField tf = new JTextField();
        tf.setEditable(false);
        tf.setFont(ModernTheme.FONT_REGULAR);
        tf.setBackground(ModernTheme.SLATE_100);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernTheme.SLATE_200, 1),
                new EmptyBorder(5, 8, 5, 8)
        ));
        return tf;
    }

    private JPanel createFieldRow(String label, JTextField field) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(800, 36));
        row.setBorder(new EmptyBorder(3, 0, 3, 0));

        JLabel lbl = new JLabel(label);
        lbl.setFont(ModernTheme.FONT_BOLD);
        lbl.setPreferredSize(new Dimension(140, 24));
        lbl.setForeground(ModernTheme.TEXT_MAIN);

        row.add(lbl, BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);
        return row;
    }

    private JPanel createSalaryStructureTab() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 14, 14));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(16, 18, 16, 18));

        // Allowances
        JPanel pAllow = new JPanel();
        pAllow.setLayout(new BoxLayout(pAllow, BoxLayout.Y_AXIS));
        pAllow.setOpaque(false);

        JLabel h1 = new JLabel("Base Salary & Earnings");
        h1.setFont(ModernTheme.FONT_SECTION);
        h1.setForeground(ModernTheme.SUCCESS);
        pAllow.add(h1);
        pAllow.add(Box.createVerticalStrut(10));

        pAllow.add(createFieldRow("Basic Pay (Rs):", txtBasicPay = createROField()));
        pAllow.add(createFieldRow("Dearness Allowance (DA):", txtDa = createROField()));
        pAllow.add(createFieldRow("House Rent Allowance (HRA):", txtHra = createROField()));
        pAllow.add(createFieldRow("Washing Allowance (WA):", txtWa = createROField()));
        pAllow.add(createFieldRow("Total Allowances:", txtTotAllow = createROField()));
        panel.add(pAllow);

        // Deductions
        JPanel pDedu = new JPanel();
        pDedu.setLayout(new BoxLayout(pDedu, BoxLayout.Y_AXIS));
        pDedu.setOpaque(false);

        JLabel h2 = new JLabel("Statutory Deductions");
        h2.setFont(ModernTheme.FONT_SECTION);
        h2.setForeground(ModernTheme.DANGER);
        pDedu.add(h2);
        pDedu.add(Box.createVerticalStrut(10));

        pDedu.add(createFieldRow("GPF Deduction:", txtGpf = createROField()));
        pDedu.add(createFieldRow("Income Tax (IT):", txtIt = createROField()));
        pDedu.add(createFieldRow("GIS Deduction:", txtGis = createROField()));
        pDedu.add(createFieldRow("Provident Fund (PF):", txtPf = createROField()));
        pDedu.add(createFieldRow("LIC Deduction:", txtLic = createROField()));
        pDedu.add(createFieldRow("Total Deductions:", txtTotDedu = createROField()));
        pDedu.add(createFieldRow("ESTIMATED NET PAY:", txtNetPay = createROField()));
        txtNetPay.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtNetPay.setForeground(ModernTheme.SUCCESS.darker());

        panel.add(pDedu);
        return panel;
    }

    private JTextField txtBasicPay, txtDa, txtHra, txtWa, txtTotAllow;
    private JTextField txtGpf, txtIt, txtGis, txtPf, txtLic, txtTotDedu, txtNetPay;

    private JPanel createAttendanceHistoryTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        String[] cols = {"Date", "Status", "Check In", "Check Out", "Overtime (Hrs)", "Remarks"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable tbl = new JTable(model);
        ModernTheme.styleTable(tbl);

        // Load attendance
        try (Connection conn = clsConnection.getConnection()) {
            String sql = "SELECT Att_Date, Status, Check_In, Check_Out, Overtime_Hours, Notes FROM ATTENDANCE WHERE Emp_Code = ? ORDER BY Att_Date DESC";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, empCode);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        model.addRow(new Object[]{
                                rs.getString("Att_Date"),
                                rs.getString("Status"),
                                rs.getString("Check_In"),
                                rs.getString("Check_Out"),
                                rs.getDouble("Overtime_Hours"),
                                rs.getString("Notes")
                        });
                    }
                }
            }
        } catch (Exception ignored) {}

        panel.add(new JScrollPane(tbl), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createLeaveHistoryTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Top summary cards
        JPanel summaryRow = new JPanel(new GridLayout(1, 3, 10, 0));
        summaryRow.setOpaque(false);
        summaryRow.add(new KpiCard("Total Entitlement", "24 Days", "Annual paid leave quota", ModernTheme.INFO, null));
        summaryRow.add(new KpiCard("Used Leave", "3 Days", "Approved leaves taken", ModernTheme.WARNING, null));
        summaryRow.add(new KpiCard("Remaining Balance", "21 Days", "Available to request", ModernTheme.SUCCESS, null));
        panel.add(summaryRow, BorderLayout.NORTH);

        String[] cols = {"Leave Type", "Start Date", "End Date", "Days", "Reason", "Status", "Reviewed By"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable tbl = new JTable(model);
        ModernTheme.styleTable(tbl);

        try (Connection conn = clsConnection.getConnection()) {
            String sql = "SELECT Leave_Type, Start_Date, End_Date, Days_Count, Reason, Status, Reviewed_By FROM LEAVE_REQUESTS WHERE Emp_Code = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, empCode);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        model.addRow(new Object[]{
                                rs.getString("Leave_Type"),
                                rs.getString("Start_Date"),
                                rs.getString("End_Date"),
                                rs.getDouble("Days_Count"),
                                rs.getString("Reason"),
                                rs.getString("Status"),
                                rs.getString("Reviewed_By")
                        });
                    }
                }
            }
        } catch (Exception ignored) {}

        panel.add(new JScrollPane(tbl), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPayslipArchiveTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        String[] cols = {"Period Month", "Period Year", "Basic Pay", "Total Allowances", "Total Deductions", "Net Salary", "Payment Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable tbl = new JTable(model);
        ModernTheme.styleTable(tbl);

        try (Connection conn = clsConnection.getConnection()) {
            String sql = "SELECT Period_Month, Period_Year, Basic_Pay, Allowances_Total, Deductions_Total, Net_Salary, Payment_Status FROM PAYSLIP_RECORDS WHERE Emp_Code = ? ORDER BY Slip_Id DESC";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, empCode);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        model.addRow(new Object[]{
                                rs.getString("Period_Month"),
                                rs.getString("Period_Year"),
                                "Rs " + String.format("%.2f", rs.getDouble("Basic_Pay")),
                                "Rs " + String.format("%.2f", rs.getDouble("Allowances_Total")),
                                "Rs " + String.format("%.2f", rs.getDouble("Deductions_Total")),
                                "Rs " + String.format("%.2f", rs.getDouble("Net_Salary")),
                                rs.getString("Payment_Status")
                        });
                    }
                }
            }
        } catch (Exception ignored) {}

        if (model.getRowCount() == 0) {
            // Offer to generate current payslip
            JPanel emptyPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));
            emptyPanel.setOpaque(false);
            JLabel lblMsg = new JLabel("No historical payroll run slips archived yet for this employee.");
            lblMsg.setFont(ModernTheme.FONT_REGULAR);
            JButton btnGen = ModernTheme.createPrimaryButton("Generate Current Month PaySlip", new ImageIcon("images/emp_rpt.png"));
            btnGen.addActionListener(e -> parentMenu.openPayslip(empCode));
            emptyPanel.add(lblMsg);
            emptyPanel.add(btnGen);
            panel.add(emptyPanel, BorderLayout.NORTH);
        }

        panel.add(new JScrollPane(tbl), BorderLayout.CENTER);
        return panel;
    }

    private void loadProfileDetails() {
        try (Connection conn = clsConnection.getConnection()) {
            String sql = "SELECT e.Emp_Code, e.Emp_Name1, e.Emp_name2, e.Emp_Desi, e.Emp_Add, e.Emp_No, " +
                    "p.Department, p.Status, p.Email, p.Joining_Date, p.Employment_Type, p.Bank_Account, p.Bank_Name, p.PAN_Tax_No " +
                    "FROM EMPLOYEE e LEFT JOIN EMPLOYEE_PROFILE p ON e.Emp_Code = p.Emp_Code WHERE e.Emp_Code = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, empCode);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String n1 = rs.getString("Emp_Name1");
                        String n2 = rs.getString("Emp_name2");
                        String fullName = (n1 != null ? n1 : "") + " " + (n2 != null ? n2 : "");
                        String desi = rs.getString("Emp_Desi");
                        String dept = rs.getString("Department");
                        String email = rs.getString("Email");
                        String phone = rs.getString("Emp_No");
                        String addr = rs.getString("Emp_Add");
                        String status = rs.getString("Status");
                        if (status == null) status = "Active";

                        lblFullName.setText(fullName.trim());
                        lblRole.setText("Role: " + (desi != null ? desi : "-"));
                        lblDept.setText("Department: " + (dept != null ? dept : "General"));
                        lblEmail.setText("Email: " + (email != null ? email : "-"));
                        lblPhone.setText("Phone: " + (phone != null ? phone : "-"));
                        lblAddress.setText("Location: " + (addr != null ? addr : "-"));
                        lblAvatar.setText(fullName.trim().isEmpty() ? "EM" : fullName.trim().substring(0, 1).toUpperCase());

                        txtEmpId.setText(empCode);
                        txtFirst.setText(n1 != null ? n1 : "");
                        txtLast.setText(n2 != null ? n2 : "");
                        txtDesi.setText(desi != null ? desi : "");
                        txtDept.setText(dept != null ? dept : "");
                        txtEmpType.setText(rs.getString("Employment_Type") != null ? rs.getString("Employment_Type") : "Full-Time");
                        txtJoinDate.setText(rs.getString("Joining_Date") != null ? rs.getString("Joining_Date") : "2023-01-15");

                        txtBankName.setText(rs.getString("Bank_Name") != null ? rs.getString("Bank_Name") : "HDFC Bank");
                        txtBankAcc.setText(rs.getString("Bank_Account") != null ? rs.getString("Bank_Account") : "910022334455");
                        txtPan.setText(rs.getString("PAN_Tax_No") != null ? rs.getString("PAN_Tax_No") : "ABCDE1234F");
                        txtPhone.setText(phone != null ? phone : "");
                        txtEmail.setText(email != null ? email : "");
                        txtAddress.setText(addr != null ? addr : "");

                        // Load salary breakdown from Settings
                        if (desi != null) {
                            loadSalaryFromSettings(conn, desi.trim());
                        }
                    }
                }
            }
        } catch (Exception ex) {
            System.err.println("Error loading profile: " + ex.getMessage());
        }
    }

    private void loadSalaryFromSettings(Connection conn, String designation) {
        String sql = "SELECT * FROM Settings WHERE Category_Name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, designation);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double bp = rs.getInt("Basic_Pay");
                    boolean daPct = "true".equalsIgnoreCase(rs.getString("DA"));
                    boolean hraPct = "true".equalsIgnoreCase(rs.getString("HRA"));
                    boolean waPct = "true".equalsIgnoreCase(rs.getString("WA"));

                    double da = daPct ? (bp * rs.getInt("DA_Allow") / 100.0) : rs.getInt("DA_Allow");
                    double hra = hraPct ? (bp * rs.getInt("HRA_Allow") / 100.0) : rs.getInt("HRA_Allow");
                    double wa = waPct ? (bp * rs.getInt("WA_Allow") / 100.0) : rs.getInt("WA_Allow");
                    double totAllow = da + hra + wa;

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
                    double totDedu = gpf + it + gis + pf + lic;
                    double net = (bp + totAllow) - totDedu;

                    txtBasicPay.setText("Rs " + String.format("%.2f", bp));
                    txtDa.setText("Rs " + String.format("%.2f", da) + (daPct ? " (" + rs.getInt("DA_Allow") + "%)" : ""));
                    txtHra.setText("Rs " + String.format("%.2f", hra) + (hraPct ? " (" + rs.getInt("HRA_Allow") + "%)" : ""));
                    txtWa.setText("Rs " + String.format("%.2f", wa) + (waPct ? " (" + rs.getInt("WA_Allow") + "%)" : ""));
                    txtTotAllow.setText("Rs " + String.format("%.2f", totAllow));

                    txtGpf.setText("Rs " + String.format("%.2f", gpf) + (gpfPct ? " (" + rs.getInt("GPF_Dedu") + "%)" : ""));
                    txtIt.setText("Rs " + String.format("%.2f", it) + (itPct ? " (" + rs.getInt("IT_Dedu") + "%)" : ""));
                    txtGis.setText("Rs " + String.format("%.2f", gis) + (gisPct ? " (" + rs.getInt("GIS_Dedu") + "%)" : ""));
                    txtPf.setText("Rs " + String.format("%.2f", pf) + (pfPct ? " (" + rs.getInt("PF_Dedu") + "%)" : ""));
                    txtLic.setText("Rs " + String.format("%.2f", lic) + (licPct ? " (" + rs.getInt("LIC_Dedu") + "%)" : ""));
                    txtTotDedu.setText("Rs " + String.format("%.2f", totDedu));
                    txtNetPay.setText("Rs " + String.format("%.2f", net));
                }
            }
        } catch (Exception ex) {
            System.err.println("Error loading salary structure: " + ex.getMessage());
        }
    }
}
