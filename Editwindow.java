import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Editwindow extends JInternalFrame implements ActionListener {
    private JFrame JFParentFrame;
    private JPanel panel1, panel2;
    private JButton FindBtn, EditBtn, ExitBtn, RefreshListBtn;
    private JLabel LblEmp_Code, LblEmp_Name1, LblEmp_Name2, LblEmp_Desi, LblEmp_Add, LblEmp_No;
    private JComboBox<String> EmpCodeCombo;
    private JTextField TxtEmp_Code, TxtEmp_Name1, TxtEmp_Name2, TxtEmp_Add, TxtEmp_No;
    private JComboBox<String> Emp_Type;

    private clsSettings settings = new clsSettings();

    public Editwindow(JFrame getParentFrame) {
        super("Edit - Employee Record", true, true, true, true);
        this.JFParentFrame = getParentFrame;
        setSize(480, 440);
        setFrameIcon(new ImageIcon("images/edit.png"));
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

        // Employee Code Selection Panel (Combo + Find)
        JPanel codePanel = new JPanel(new BorderLayout(5, 0));
        EmpCodeCombo = new JComboBox<>();
        EmpCodeCombo.setEditable(true);
        loadEmployeeCodes();
        EmpCodeCombo.addActionListener(e -> {
            if ("comboBoxChanged".equals(e.getActionCommand())) {
                String selected = (String) EmpCodeCombo.getSelectedItem();
                if (selected != null && !selected.trim().isEmpty()) {
                    loadEmployeeDetails(selected.trim());
                }
            }
        });
        codePanel.add(EmpCodeCombo, BorderLayout.CENTER);

        FindBtn = new JButton("Load");
        FindBtn.setIcon(new ImageIcon("images/search.png"));
        FindBtn.addActionListener(this);
        codePanel.add(FindBtn, BorderLayout.EAST);

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
        EditBtn = new JButton("Update Employee");
        EditBtn.setIcon(new ImageIcon("images/save_all.png"));
        EditBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        EditBtn.addActionListener(this);

        ExitBtn = new JButton("Exit");
        ExitBtn.setIcon(new ImageIcon("images/exit.png"));
        ExitBtn.addActionListener(this);

        panel2.add(EditBtn);
        panel2.add(ExitBtn);

        mainPanel.add(panel2, BorderLayout.SOUTH);
        getContentPane().add(mainPanel);

        // Auto-load first employee if present
        if (EmpCodeCombo.getItemCount() > 0) {
            String firstCode = (String) EmpCodeCombo.getItemAt(0);
            if (firstCode != null && !firstCode.trim().isEmpty()) {
                loadEmployeeDetails(firstCode.trim());
            }
        }
    }

    public void selectEmployee(String code) {
        if (code != null) {
            EmpCodeCombo.setSelectedItem(code);
            loadEmployeeDetails(code);
        }
    }

    private void loadEmployeeCodes() {
        EmpCodeCombo.removeAllItems();
        try (Connection conn = clsConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Emp_Code FROM EMPLOYEE ORDER BY Emp_Code")) {

            while (rs.next()) {
                String c = rs.getString("Emp_Code");
                if (c != null && !c.trim().isEmpty()) {
                    EmpCodeCombo.addItem(c.trim());
                }
            }
        } catch (Exception ex) {
            System.err.println("Error loading employee codes: " + ex.getMessage());
        }
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

        if (cmb.getItemCount() == 0) {
            cmb.addItem("Programmer");
            cmb.addItem("Team Leader");
            cmb.addItem("CEO");
        }
    }

    private void loadEmployeeDetails(String code) {
        if (code == null || code.trim().isEmpty()) return;

        try (Connection conn = clsConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT Emp_Name1, Emp_name2, Emp_Desi, Emp_Add, Emp_No FROM EMPLOYEE WHERE Emp_Code = ?")) {

            pstmt.setString(1, code.trim());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String name1 = rs.getString("Emp_Name1");
                    String name2 = rs.getString("Emp_name2");
                    String desi = rs.getString("Emp_Desi");
                    String add = rs.getString("Emp_Add");
                    String no = rs.getString("Emp_No");

                    TxtEmp_Name1.setText(name1 != null ? name1.trim() : "");
                    TxtEmp_Name2.setText(name2 != null ? name2.trim() : "");
                    TxtEmp_Add.setText(add != null ? add.trim() : "");
                    TxtEmp_No.setText(no != null ? no.trim() : "");

                    if (desi != null && !desi.trim().isEmpty()) {
                        Emp_Type.setSelectedItem(desi.trim());
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "No employee found with code '" + code + "'.", "Not Found", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error fetching employee: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();

        if (source == FindBtn) {
            String code = (String) EmpCodeCombo.getSelectedItem();
            if (code != null && !code.trim().isEmpty()) {
                loadEmployeeDetails(code.trim());
            } else {
                JOptionPane.showMessageDialog(this, "Please select or type an Employee Code.", "Code Required", JOptionPane.WARNING_MESSAGE);
            }
        } else if (source == EditBtn) {
            updateEmployee();
        } else if (source == ExitBtn) {
            setVisible(false);
            dispose();
        }
    }

    private void updateEmployee() {
        String empCode = (String) EmpCodeCombo.getSelectedItem();
        if (empCode == null || empCode.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select an Employee Code to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        empCode = empCode.trim();

        String empName1 = TxtEmp_Name1.getText().trim();
        String empName2 = TxtEmp_Name2.getText().trim();
        String empDesi = (String) Emp_Type.getSelectedItem();
        String empAdd = TxtEmp_Add.getText().trim();
        String empNo = TxtEmp_No.getText().trim();

        if (empName1.isEmpty() || empAdd.isEmpty() || empNo.isEmpty() || empDesi == null || empDesi.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields (Name, Designation, Address, Contact No) are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = clsConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE EMPLOYEE SET Emp_Name1 = ?, Emp_name2 = ?, Emp_Desi = ?, Emp_Add = ?, Emp_No = ? WHERE Emp_Code = ?")) {

            pstmt.setString(1, empName1);
            pstmt.setString(2, empName2);
            pstmt.setString(3, empDesi);
            pstmt.setString(4, empAdd);
            pstmt.setString(5, empNo);
            pstmt.setString(6, empCode);

            int result = pstmt.executeUpdate();
            if (result == 1) {
                JOptionPane.showMessageDialog(this, "Employee '" + empCode + "' updated successfully!", "Update Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No record was updated. Employee code might not exist.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating employee: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
