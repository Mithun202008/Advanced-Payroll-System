import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ChangePasswordWindow extends JInternalFrame implements ActionListener {
    private String currentUser;
    private JPasswordField txtCurrentPass;
    private JPasswordField txtNewPass;
    private JPasswordField txtConfirmPass;
    private JButton btnSave, btnCancel;

    public ChangePasswordWindow(String username) {
        super("Security - Change Administrator Password", true, true, true, true);
        this.currentUser = username != null && !username.trim().isEmpty() ? username : "Pavan";
        setSize(420, 260);
        setFrameIcon(new ImageIcon("images/Key.gif"));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.add(new JLabel("Current User:"));
        JLabel lblUser = new JLabel(this.currentUser);
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(lblUser);

        formPanel.add(new JLabel("Current Password:"));
        txtCurrentPass = new JPasswordField(15);
        formPanel.add(txtCurrentPass);

        formPanel.add(new JLabel("New Password:"));
        txtNewPass = new JPasswordField(15);
        formPanel.add(txtNewPass);

        formPanel.add(new JLabel("Confirm Password:"));
        txtConfirmPass = new JPasswordField(15);
        formPanel.add(txtConfirmPass);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        btnSave = new JButton("Update Password");
        btnSave.setIcon(new ImageIcon("images/ok.png"));
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSave.addActionListener(this);

        btnCancel = new JButton("Cancel");
        btnCancel.setIcon(new ImageIcon("images/exit.png"));
        btnCancel.addActionListener(this);

        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        getContentPane().add(mainPanel);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnCancel) {
            setVisible(false);
            dispose();
            return;
        }

        if (e.getSource() == btnSave) {
            String curPass = new String(txtCurrentPass.getPassword()).trim();
            String newPass = new String(txtNewPass.getPassword()).trim();
            String confPass = new String(txtConfirmPass.getPassword()).trim();

            if (curPass.isEmpty() || newPass.isEmpty() || confPass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all password fields.", "Validation Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!newPass.equals(confPass)) {
                JOptionPane.showMessageDialog(this, "New Password and Confirm Password do not match.", "Password Mismatch", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = clsConnection.getConnection()) {
                // Verify current password
                String checkSql = "SELECT password FROM Login WHERE username = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                    checkStmt.setString(1, currentUser);
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (!rs.next() || !rs.getString("password").trim().equals(curPass)) {
                            JOptionPane.showMessageDialog(this, "Current password is incorrect.", "Authentication Failed", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                }

                // Update password
                String updateSql = "UPDATE Login SET password = ? WHERE username = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setString(1, newPass);
                    updateStmt.setString(2, currentUser);
                    int affected = updateStmt.executeUpdate();
                    if (affected > 0) {
                        JOptionPane.showMessageDialog(this, "Password updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        setVisible(false);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to update password.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
