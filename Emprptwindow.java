import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class Emprptwindow extends JInternalFrame implements ActionListener {
    private JFrame JFParentFrame;
    private JComboBox<String> EmpCombo;
    private JComboBox<String> MonthCombo;
    private JTextField TxtYear;
    private JButton GenerateBtn, PrintBtn, ExitBtn;

    private JLabel LblEmp_Name, LblEmp_Desi, LblDate, LblSalary_Slip;
    private JTextField TxtDate, TxtEmp_Name, TxtSalary_Month, TxtEmp_Desi, TxtBasic_Pay;
    private JTextField TxtDA, TxtHRA, TxtWA, TxtGPF, TxtIT, TxtGIS, TxtPF, TxtLIC;
    private JTextField TxtTot_Allowance, TxtTot_Deduction, TxtNet_Salary;

    private String sEmp_Code = "";
    private String sEmp_Name1 = "";
    private String sEmp_Name2 = "";
    private String sEmp_Desi = "";
    private String sDate = "";
    private String selectedMonth = "January";
    private String selectedYear = "";

    private double vBasic_Pay, vDA, vHRA, vWA, vGPF, vIT, vGIS, vPF, vLIC;
    private double totalAllowances, totalDeductions, netSalary;

    private static final String[] MONTH_NAMES = {
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    };

    public Emprptwindow(JFrame getParentFrame) {
        super("Employee PaySlip Generator", true, true, true, true);
        this.JFParentFrame = getParentFrame;
        setSize(680, 680);
        setFrameIcon(new ImageIcon("images/emp_rpt.png"));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        sDate = DateFormat.getDateInstance(DateFormat.MEDIUM).format(new Date());
        Calendar cal = Calendar.getInstance();
        int curMonth = cal.get(Calendar.MONTH);
        int curYear = cal.get(Calendar.YEAR);
        selectedYear = String.valueOf(curYear);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(12, 15, 12, 15));

        // Top Filter Panel (Employee, Month, Year, Generate)
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBorder(new CompoundBorder(new TitledBorder("Pay Period & Employee Selection"), new EmptyBorder(5, 5, 5, 5)));

        filterPanel.add(new JLabel("Employee:"));
        EmpCombo = new JComboBox<>();
        EmpCombo.setPreferredSize(new Dimension(160, 25));
        loadEmployeeDropdown();
        filterPanel.add(EmpCombo);

        filterPanel.add(new JLabel("Month:"));
        MonthCombo = new JComboBox<>(MONTH_NAMES);
        MonthCombo.setSelectedIndex(curMonth);
        selectedMonth = MONTH_NAMES[curMonth];
        MonthCombo.addActionListener(e -> {
            selectedMonth = (String) MonthCombo.getSelectedItem();
        });
        filterPanel.add(MonthCombo);

        filterPanel.add(new JLabel("Year:"));
        TxtYear = new JTextField(selectedYear, 5);
        filterPanel.add(TxtYear);

        GenerateBtn = new JButton("Generate");
        GenerateBtn.setIcon(new ImageIcon("images/search.png"));
        GenerateBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        GenerateBtn.addActionListener(this);
        filterPanel.add(GenerateBtn);

        mainPanel.add(filterPanel, BorderLayout.NORTH);

        // Center Payslip Preview Form
        JPanel slipPanel = new JPanel();
        slipPanel.setLayout(new BoxLayout(slipPanel, BoxLayout.Y_AXIS));
        slipPanel.setBorder(new CompoundBorder(new TitledBorder("Generated Salary Slip Details"), new EmptyBorder(10, 10, 10, 10)));

        // Employee Info Row
        JPanel pEmp = new JPanel(new GridLayout(2, 4, 10, 5));
        pEmp.add(new JLabel("Employee Name:"));
        TxtEmp_Name = new JTextField();
        TxtEmp_Name.setEditable(false);
        pEmp.add(TxtEmp_Name);

        pEmp.add(new JLabel("Designation:"));
        TxtEmp_Desi = new JTextField();
        TxtEmp_Desi.setEditable(false);
        pEmp.add(TxtEmp_Desi);

        pEmp.add(new JLabel("Pay Period:"));
        TxtSalary_Month = new JTextField();
        TxtSalary_Month.setEditable(false);
        pEmp.add(TxtSalary_Month);

        pEmp.add(new JLabel("Basic Salary:"));
        TxtBasic_Pay = new JTextField();
        TxtBasic_Pay.setEditable(false);
        pEmp.add(TxtBasic_Pay);

        slipPanel.add(pEmp);
        slipPanel.add(Box.createVerticalStrut(10));

        // Allowances Panel
        JPanel pAllow = new JPanel(new GridLayout(2, 4, 10, 5));
        pAllow.setBorder(new TitledBorder("Earnings & Allowances (Rs)"));
        pAllow.add(new JLabel("DA:"));
        TxtDA = new JTextField();
        TxtDA.setEditable(false);
        pAllow.add(TxtDA);

        pAllow.add(new JLabel("HRA:"));
        TxtHRA = new JTextField();
        TxtHRA.setEditable(false);
        pAllow.add(TxtHRA);

        pAllow.add(new JLabel("WA:"));
        TxtWA = new JTextField();
        TxtWA.setEditable(false);
        pAllow.add(TxtWA);

        pAllow.add(new JLabel("Total Allowances:"));
        TxtTot_Allowance = new JTextField();
        TxtTot_Allowance.setEditable(false);
        TxtTot_Allowance.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pAllow.add(TxtTot_Allowance);

        slipPanel.add(pAllow);
        slipPanel.add(Box.createVerticalStrut(10));

        // Deductions Panel
        JPanel pDedu = new JPanel(new GridLayout(3, 4, 10, 5));
        pDedu.setBorder(new TitledBorder("Statutory & Other Deductions (Rs)"));
        pDedu.add(new JLabel("GPF:"));
        TxtGPF = new JTextField();
        TxtGPF.setEditable(false);
        pDedu.add(TxtGPF);

        pDedu.add(new JLabel("Income Tax (IT):"));
        TxtIT = new JTextField();
        TxtIT.setEditable(false);
        pDedu.add(TxtIT);

        pDedu.add(new JLabel("GIS:"));
        TxtGIS = new JTextField();
        TxtGIS.setEditable(false);
        pDedu.add(TxtGIS);

        pDedu.add(new JLabel("PF:"));
        TxtPF = new JTextField();
        TxtPF.setEditable(false);
        pDedu.add(TxtPF);

        pDedu.add(new JLabel("LIC:"));
        TxtLIC = new JTextField();
        TxtLIC.setEditable(false);
        pDedu.add(TxtLIC);

        pDedu.add(new JLabel("Total Deductions:"));
        TxtTot_Deduction = new JTextField();
        TxtTot_Deduction.setEditable(false);
        TxtTot_Deduction.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pDedu.add(TxtTot_Deduction);

        slipPanel.add(pDedu);
        slipPanel.add(Box.createVerticalStrut(10));

        // Net Salary Row
        JPanel pNet = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        JLabel lblNet = new JLabel("NET SALARY PAYABLE:");
        lblNet.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNet.setForeground(new Color(20, 100, 40));
        pNet.add(lblNet);

        TxtNet_Salary = new JTextField(12);
        TxtNet_Salary.setEditable(false);
        TxtNet_Salary.setFont(new Font("Segoe UI", Font.BOLD, 14));
        TxtNet_Salary.setForeground(new Color(20, 100, 40));
        pNet.add(TxtNet_Salary);

        slipPanel.add(pNet);
        mainPanel.add(new JScrollPane(slipPanel), BorderLayout.CENTER);

        // Action Buttons at Bottom
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        PrintBtn = new JButton("Print / Preview Payslip");
        PrintBtn.setIcon(new ImageIcon("images/print.png"));
        PrintBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        PrintBtn.setEnabled(false);
        PrintBtn.addActionListener(this);

        ExitBtn = new JButton("Exit");
        ExitBtn.setIcon(new ImageIcon("images/exit.png"));
        ExitBtn.addActionListener(this);

        btnPanel.add(PrintBtn);
        btnPanel.add(ExitBtn);

        mainPanel.add(btnPanel, BorderLayout.SOUTH);
        getContentPane().add(mainPanel);
    }

    public void selectEmployeeCode(String code) {
        if (code == null) return;
        for (int i = 0; i < EmpCombo.getItemCount(); i++) {
            String item = EmpCombo.getItemAt(i);
            if (item != null && item.startsWith(code)) {
                EmpCombo.setSelectedIndex(i);
                break;
            }
        }
        generatePayslip();
    }

    private void loadEmployeeDropdown() {
        EmpCombo.removeAllItems();
        try (Connection conn = clsConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Emp_Code, Emp_Name1, Emp_name2 FROM EMPLOYEE ORDER BY Emp_Code")) {

            while (rs.next()) {
                String code = rs.getString("Emp_Code");
                String n1 = rs.getString("Emp_Name1");
                String n2 = rs.getString("Emp_name2");
                EmpCombo.addItem(code + " - " + (n1 != null ? n1 : "") + " " + (n2 != null ? n2 : ""));
            }
        } catch (Exception ex) {
            System.err.println("Error loading employees for payslip: " + ex.getMessage());
        }
    }

    public void actionPerformed(ActionEvent event) {
        Object src = event.getSource();

        if (src == GenerateBtn) {
            generatePayslip();
        } else if (src == PrintBtn) {
            openPrintWindow();
        } else if (src == ExitBtn) {
            setVisible(false);
            dispose();
        }
    }

    private void generatePayslip() {
        String empSelection = (String) EmpCombo.getSelectedItem();
        if (empSelection == null || empSelection.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select an employee first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        sEmp_Code = empSelection.split(" - ")[0].trim();
        selectedMonth = (String) MonthCombo.getSelectedItem();
        selectedYear = TxtYear.getText().trim();

        if (selectedYear.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid Year.", "Year Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Fetch employee details
        try (Connection conn = clsConnection.getConnection()) {
            String empSql = "SELECT Emp_Name1, Emp_name2, Emp_Desi FROM EMPLOYEE WHERE Emp_Code = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(empSql)) {
                pstmt.setString(1, sEmp_Code);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        sEmp_Name1 = rs.getString("Emp_Name1");
                        sEmp_Name2 = rs.getString("Emp_name2");
                        sEmp_Desi = rs.getString("Emp_Desi");
                    } else {
                        JOptionPane.showMessageDialog(this, "Employee " + sEmp_Code + " not found in database.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }

            if (sEmp_Desi == null || sEmp_Desi.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Employee " + sEmp_Code + " has no designation configured.", "Missing Designation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Fetch position salary structure from Settings
            String setSql = "SELECT * FROM Settings WHERE Category_Name = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(setSql)) {
                pstmt.setString(1, sEmp_Desi.trim());
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        vBasic_Pay = rs.getInt("Basic_Pay");

                        int daVal = rs.getInt("DA_Allow");
                        int hraVal = rs.getInt("HRA_Allow");
                        int waVal = rs.getInt("WA_Allow");

                        int gpfVal = rs.getInt("GPF_Dedu");
                        int itVal = rs.getInt("IT_Dedu");
                        int gisVal = rs.getInt("GIS_Dedu");
                        int pfVal = rs.getInt("PF_Dedu");
                        int licVal = rs.getInt("LIC_Dedu");

                        boolean daIsPct = "true".equalsIgnoreCase(rs.getString("DA"));
                        boolean hraIsPct = "true".equalsIgnoreCase(rs.getString("HRA"));
                        boolean waIsPct = "true".equalsIgnoreCase(rs.getString("WA"));

                        boolean gpfIsPct = "true".equalsIgnoreCase(rs.getString("GPF"));
                        boolean itIsPct = "true".equalsIgnoreCase(rs.getString("IT"));
                        boolean gisIsPct = "true".equalsIgnoreCase(rs.getString("GIS"));
                        boolean pfIsPct = "true".equalsIgnoreCase(rs.getString("PF"));
                        boolean licIsPct = "true".equalsIgnoreCase(rs.getString("LIC"));

                        // Calculate Allowances
                        vDA = daIsPct ? (vBasic_Pay * daVal / 100.0) : daVal;
                        vHRA = hraIsPct ? (vBasic_Pay * hraVal / 100.0) : hraVal;
                        vWA = waIsPct ? (vBasic_Pay * waVal / 100.0) : waVal;
                        totalAllowances = vDA + vHRA + vWA;

                        // Calculate Deductions
                        vGPF = gpfIsPct ? (vBasic_Pay * gpfVal / 100.0) : gpfVal;
                        vIT = itIsPct ? (vBasic_Pay * itVal / 100.0) : itVal;
                        vGIS = gisIsPct ? (vBasic_Pay * gisVal / 100.0) : gisVal;
                        vPF = pfIsPct ? (vBasic_Pay * pfVal / 100.0) : pfVal;
                        vLIC = licIsPct ? (vBasic_Pay * licVal / 100.0) : licVal;
                        totalDeductions = vGPF + vIT + vGIS + vPF + vLIC;

                        // Net Salary
                        netSalary = (vBasic_Pay + totalAllowances) - totalDeductions;

                        // Fill Form Fields
                        TxtEmp_Name.setText((sEmp_Name1 != null ? sEmp_Name1 : "") + " " + (sEmp_Name2 != null ? sEmp_Name2 : ""));
                        TxtEmp_Desi.setText(sEmp_Desi);
                        TxtSalary_Month.setText(selectedMonth + ", " + selectedYear);
                        TxtBasic_Pay.setText(String.format("%.2f", vBasic_Pay));

                        TxtDA.setText(String.format("%.2f", vDA));
                        TxtHRA.setText(String.format("%.2f", vHRA));
                        TxtWA.setText(String.format("%.2f", vWA));
                        TxtTot_Allowance.setText(String.format("%.2f", totalAllowances));

                        TxtGPF.setText(String.format("%.2f", vGPF));
                        TxtIT.setText(String.format("%.2f", vIT));
                        TxtGIS.setText(String.format("%.2f", vGIS));
                        TxtPF.setText(String.format("%.2f", vPF));
                        TxtLIC.setText(String.format("%.2f", vLIC));
                        TxtTot_Deduction.setText(String.format("%.2f", totalDeductions));

                        TxtNet_Salary.setText(String.format("%.2f", netSalary));

                        PrintBtn.setEnabled(true);
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "No salary tier found in Settings for designation '" + sEmp_Desi + "'.\nPlease add this position under Tools -> Settings first.",
                                "Position Not Configured", JOptionPane.WARNING_MESSAGE);
                        PrintBtn.setEnabled(false);
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error generating payslip: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void openPrintWindow() {
        printwindow prn = new printwindow();
        prn.fill_data(
                sEmp_Name1, sEmp_Name2, selectedMonth, selectedYear, sEmp_Code, sDate,
                sEmp_Desi, String.format("%.2f", vBasic_Pay),
                String.format("%.2f", vDA), String.format("%.2f", vHRA), String.format("%.2f", vWA),
                String.format("%.2f", vGPF), String.format("%.2f", vIT), String.format("%.2f", vGIS),
                String.format("%.2f", vPF), String.format("%.2f", vLIC),
                String.format("%.2f", totalAllowances), String.format("%.2f", totalDeductions),
                String.format("%.2f", netSalary)
        );
        prn.setLocationRelativeTo(this);
        prn.setVisible(true);
    }
}
