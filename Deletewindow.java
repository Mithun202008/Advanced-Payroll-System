import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Deletewindow extends JInternalFrame implements ActionListener {
    private JFrame JFParentFrame;
    private JPanel panel1, panel2;
    private JButton FindBtn, DeleteBtn, ExitBtn;
    private JLabel LblEmp_Code, LblEmp_Name1, LblEmp_Name2, LblEmp_Desi, LblEmp_Add, LblEmp_No;
    private JComboBox<String> EmpCodeCombo;
    private JTextField TxtEmp_Name1, TxtEmp_Name2, TxtEmp_Desi, TxtEmp_Add, TxtEmp_No;

    public Deletewindow(JFrame getParentFrame) {
        super("Delete - Employee Record", true, true, true, true);
        this.JFParentFrame = getParentFrame;
        setSize(480, 420);
        setFrameIcon(new ImageIcon("images/delete.png"));
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

        // Code selector panel
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

        TxtEmp_Desi = new JTextField();
        TxtEmp_Desi.setEditable(false);

        TxtEmp_Name1 = new JTextField();
        TxtEmp_Name1.setEditable(false);

        TxtEmp_Name2 = new JTextField();
        TxtEmp_Name2.setEditable(false);

        TxtEmp_Add = new JTextField();
        TxtEmp_Add.setEditable(false);

        TxtEmp_No = new JTextField();
        TxtEmp_No.setEditable(false);

        panel1.add(LblEmp_Code);
        panel1.add(codePanel);

        panel1.add(LblEmp_Desi);
        panel1.add(TxtEmp_Desi);

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
        DeleteBtn = new JButton("Delete Employee");
        DeleteBtn.setIcon(new ImageIcon("images/delete.png"));
        DeleteBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        DeleteBtn.setForeground(new Color(180, 20, 20));
        DeleteBtn.addActionListener(this);

        ExitBtn = new JButton("Exit");
        ExitBtn.setIcon(new ImageIcon("images/exit.png"));
        ExitBtn.addActionListener(this);

        panel2.add(DeleteBtn);
        panel2.add(ExitBtn);

        mainPanel.add(panel2, BorderLayout.SOUTH);
        getContentPane().add(mainPanel);

        if (EmpCodeCombo.getItemCount() > 0) {
            String first = (String) EmpCodeCombo.getItemAt(0);
            if (first != null && !first.trim().isEmpty()) {
                loadEmployeeDetails(first.trim());
            }
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

    private void loadEmployeeDetails(String code) {
        if (code == null || code.trim().isEmpty()) return;

        try (Connection conn = clsConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT Emp_Name1, Emp_name2, Emp_Desi, Emp_Add, Emp_No FROM EMPLOYEE WHERE Emp_Code = ?")) {

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
                    TxtEmp_Desi.setText(desi != null ? desi.trim() : "");
                    TxtEmp_Add.setText(add != null ? add.trim() : "");
                    TxtEmp_No.setText(no != null ? no.trim() : "");
                } else {
                    resetFields();
                    JOptionPane.showMessageDialog(this, "No employee found with code '" + code + "'.", "Not Found", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error fetching employee: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetFields() {
        TxtEmp_Name1.setText("");
        TxtEmp_Name2.setText("");
        TxtEmp_Desi.setText("");
        TxtEmp_Add.setText("");
        TxtEmp_No.setText("");
    }

    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();

        if (source == FindBtn) {
            String code = (String) EmpCodeCombo.getSelectedItem();
            if (code != null && !code.trim().isEmpty()) {
                loadEmployeeDetails(code.trim());
            } else {
                JOptionPane.showMessageDialog(this, "Please select or enter an Employee Code.", "Code Required", JOptionPane.WARNING_MESSAGE);
            }
        } else if (source == DeleteBtn) {
            deleteEmployee();
        } else if (source == ExitBtn) {
            setVisible(false);
            dispose();
        }
    }

    private void deleteEmployee() {
        String empCode = (String) EmpCodeCombo.getSelectedItem();
        if (empCode == null || empCode.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select an Employee Code to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        empCode = empCode.trim();

        String name = TxtEmp_Name1.getText() + " " + TxtEmp_Name2.getText();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to permanently delete Employee:\nCode: " + empCode + "\nName: " + name.trim() + "?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = clsConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM EMPLOYEE WHERE Emp_Code = ?")) {

                pstmt.setString(1, empCode);
                int result = pstmt.executeUpdate();
                if (result == 1) {
                    JOptionPane.showMessageDialog(this, "Employee '" + empCode + "' deleted successfully!", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                    resetFields();
                    loadEmployeeCodes();
                } else {
                    JOptionPane.showMessageDialog(this, "Employee code '" + empCode + "' not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting employee: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
}
