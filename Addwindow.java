import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Addwindow extends JInternalFrame implements ActionListener {
    private JFrame JFParentFrame;
    private JPanel panel1, panel2;
    private JButton AddBtn, ResetBtn, ExitBtn, AutoCodeBtn;
    private JLabel LblEmp_Code, LblEmp_Name1, LblEmp_Name2, LblEmp_Desi, LblEmp_Add, LblEmp_No;
    private JTextField TxtEmp_Code, TxtEmp_Name1, TxtEmp_Name2, TxtEmp_Add, TxtEmp_No;
    private JComboBox<String> Emp_Type;

    private clsSettings settings = new clsSettings();

    public Addwindow(JFrame getParentFrame) {
        super("Add - Employee", true, true, true, true);
        this.JFParentFrame = getParentFrame;
        setSize(460, 420);
        setFrameIcon(new ImageIcon("images/employee.png"));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        panel1 = new JPanel(new GridLayout(6, 2, 10, 10));

        LblEmp_Code = new JLabel("Employee Code:");
        LblEmp_Code.setFont(new Font("Segoe UI", Font.BOLD, 12));
        LblEmp_Desi = new JLabel("Designation / Role:");
        LblEmp_Desi.setFont(new Font("Segoe UI", Font.BOLD, 12));
        LblEmp_Name1 = new JLabel("First Name:");
        LblEmp_Name1.setFont(new Font("Segoe UI", Font.BOLD, 12));
        LblEmp_Name2 = new JLabel("Last Name:");
        LblEmp_Name2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        LblEmp_Add = new JLabel("Residential Address:");
        LblEmp_Add.setFont(new Font("Segoe UI", Font.BOLD, 12));
        LblEmp_No = new JLabel("Contact Number:");
        LblEmp_No.setFont(new Font("Segoe UI", Font.BOLD, 12));

        TxtEmp_Code = new JTextField();
        JPanel codePanel = new JPanel(new BorderLayout(5, 0));
        codePanel.add(TxtEmp_Code, BorderLayout.CENTER);
        AutoCodeBtn = new JButton("Auto");
        AutoCodeBtn.setToolTipText("Generate next available Employee Code");
        AutoCodeBtn.addActionListener(this);
        codePanel.add(AutoCodeBtn, BorderLayout.EAST);

        Emp_Type = new JComboBox<>();
        Emp_Type.setEditable(false);
        loadDesignations(Emp_Type);

        TxtEmp_Name1 = new JTextField();
        TxtEmp_Name2 = new JTextField();
        TxtEmp_Add = new JTextField();
        TxtEmp_No = new JTextField();
        settings.Numvalidator(TxtEmp_No);

        panel1.add(LblEmp_Code);
        panel1.add(codePanel);

        panel1.add(LblEmp_Desi);
        panel1.add(Emp_Type);

        panel1.add(LblEmp_Name1);
        panel1.add(TxtEmp_Name1);

        panel1.add(LblEmp_Name2);
        panel1.add(TxtEmp_Name2);

        panel1.add(LblEmp_Add);
        panel1.add(TxtEmp_Add);

        panel1.add(LblEmp_No);
        panel1.add(TxtEmp_No);

        mainPanel.add(panel1, BorderLayout.CENTER);

        // Buttons
        panel2 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        AddBtn = new JButton("Save Employee");
        AddBtn.setIcon(new ImageIcon("images/save_all.png"));
        AddBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        AddBtn.addActionListener(this);

        ResetBtn = new JButton("Reset");
        ResetBtn.setIcon(new ImageIcon("images/reset.png"));
        ResetBtn.addActionListener(this);

        ExitBtn = new JButton("Exit");
        ExitBtn.setIcon(new ImageIcon("images/exit.png"));
        ExitBtn.addActionListener(this);

        panel2.add(AddBtn);
        panel2.add(ResetBtn);
        panel2.add(ExitBtn);

        mainPanel.add(panel2, BorderLayout.SOUTH);
        getContentPane().add(mainPanel);

        suggestAutoCode();
    }

    public void loadDesignations(JComboBox<String> cmb) {
        cmb.removeAllItems();
        try (Connection conn = clsConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Category_Name FROM Settings ORDER BY Category_Name")) {

            while (rs.next()) {
                String cat = rs.getString("Category_Name");
                if (cat != null && !cat.trim().isEmpty()) {
                    cmb.addItem(cat.trim());
                }
            }
        } catch (Exception ex) {
            System.err.println("Error loading designations: " + ex.getMessage());
        }

        // If Settings had no records, offer defaults
        if (cmb.getItemCount() == 0) {
            cmb.addItem("Programmer");
            cmb.addItem("Team Leader");
            cmb.addItem("CEO");
        }
    }

    private void suggestAutoCode() {
        try (Connection conn = clsConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Emp_Code FROM EMPLOYEE")) {

            int maxNum = 0;
            while (rs.next()) {
                String c = rs.getString("Emp_Code");
                if (c != null) {
                    c = c.replaceAll("[^0-9]", "");
                    if (!c.isEmpty()) {
                        try {
                            int n = Integer.parseInt(c);
                            if (n > maxNum) maxNum = n;
                        } catch (NumberFormatException ignored) {}
                    }
                }
            }
            TxtEmp_Code.setText(String.format("EMP%03d", maxNum + 1));
        } catch (Exception e) {
            TxtEmp_Code.setText("EMP001");
        }
    }

    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();

        if (source == AutoCodeBtn) {
            suggestAutoCode();
        } else if (source == AddBtn) {
            saveEmployee();
        } else if (source == ResetBtn) {
            resetRecord();
        } else if (source == ExitBtn) {
            setVisible(false);
            dispose();
        }
    }

    private void saveEmployee() {
        String empCode = TxtEmp_Code.getText().trim();
        String empName1 = TxtEmp_Name1.getText().trim();
        String empName2 = TxtEmp_Name2.getText().trim();
        String empDesi = (String) Emp_Type.getSelectedItem();
        String empAdd = TxtEmp_Add.getText().trim();
        String empNo = TxtEmp_No.getText().trim();

        if (empCode.isEmpty() || empName1.isEmpty() || empAdd.isEmpty() || empNo.isEmpty() || empDesi == null || empDesi.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all mandatory fields (Code, Name, Designation, Address, Contact No).",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = clsConnection.getConnection()) {
            // Check if duplicate code
            String checkSql = "SELECT Emp_Code FROM EMPLOYEE WHERE Emp_Code = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, empCode);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(this, "An employee with Code '" + empCode + "' already exists!",
                                "Duplicate Record", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }

            // Insert into EMPLOYEE with explicit columns
            String insertSql = "INSERT INTO EMPLOYEE (Emp_Code, Emp_Name1, Emp_name2, Emp_Add, Emp_No, Emp_Desi) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                pstmt.setString(1, empCode);
                pstmt.setString(2, empName1);
                pstmt.setString(3, empName2);
                pstmt.setString(4, empAdd);
                pstmt.setString(5, empNo);
                pstmt.setString(6, empDesi);

                int result = pstmt.executeUpdate();
                if (result == 1) {
                    JOptionPane.showMessageDialog(this, "Employee '" + empCode + "' (" + empName1 + " " + empName2 + ") added successfully!",
                            "Employee Added", JOptionPane.INFORMATION_MESSAGE);
                    resetRecord();
                    suggestAutoCode();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to insert record into database.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void resetRecord() {
        TxtEmp_Code.setText("");
        TxtEmp_Name1.setText("");
        TxtEmp_Name2.setText("");
        TxtEmp_Add.setText("");
        TxtEmp_No.setText("");
        if (Emp_Type.getItemCount() > 0) Emp_Type.setSelectedIndex(0);
    }
}
