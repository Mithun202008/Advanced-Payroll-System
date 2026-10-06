import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * ModernUserManagement handles Role-Based Access Control (RBAC).
 * Supports Super Admin, HR Admin, Manager, and Employee roles,
 * user provisioning, status toggles, and password resets.
 */
public class ModernUserManagement extends JPanel {
    private MainMenu parentMenu;

    private JTable usersTable;
    private DefaultTableModel tableModel;
    private JButton btnAddUser, btnEditRole, btnResetPassword, btnToggleStatus, btnRefresh;

    private static final String[] ROLES = {"SUPER_ADMIN", "HR_ADMIN", "MANAGER", "EMPLOYEE"};

    public ModernUserManagement(MainMenu parent) {
        this.parentMenu = parent;
        setLayout(new BorderLayout(14, 14));
        setBackground(ModernTheme.SLATE_50);
        setBorder(new EmptyBorder(16, 20, 20, 20));

        // Header Card
        add(createHeaderCard(), BorderLayout.NORTH);

        // Center Table Card
        JPanel centerCard = ModernTheme.createCard();
        centerCard.setLayout(new BorderLayout(0, 10));

        setupUsersTable();
        JScrollPane scrollPane = new JScrollPane(usersTable);
        scrollPane.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, ModernTheme.SLATE_200));
        centerCard.add(scrollPane, BorderLayout.CENTER);

        // Bottom Actions
        centerCard.add(createBottomToolbar(), BorderLayout.SOUTH);
        add(centerCard, BorderLayout.CENTER);

        loadUsers();
    }

    private JPanel createHeaderCard() {
        JPanel card = ModernTheme.createCard();
        card.setLayout(new BorderLayout(15, 10));

        JPanel leftPanel = new JPanel(new GridLayout(2, 1, 0, 3));
        leftPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Role-Based Access Control & User Administration");
        lblTitle.setFont(ModernTheme.FONT_TITLE);
        lblTitle.setForeground(ModernTheme.TEXT_MAIN);

        JLabel lblSub = new JLabel("Manage User Accounts, Security Credentials, and Operational Role Assignments");
        lblSub.setFont(ModernTheme.FONT_REGULAR);
        lblSub.setForeground(ModernTheme.TEXT_MUTED);

        leftPanel.add(lblTitle);
        leftPanel.add(lblSub);
        card.add(leftPanel, BorderLayout.WEST);

        // Current user role badge
        JPanel rightBadge = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        rightBadge.setOpaque(false);
        rightBadge.add(new JLabel("Active User:"));
        rightBadge.add(ModernTheme.createBadge(AuthService.getCurrentUser() + " (" + AuthService.getCurrentRole().name() + ")", "SUCCESS"));
        card.add(rightBadge, BorderLayout.EAST);

        return card;
    }

    private void setupUsersTable() {
        String[] cols = {"Username", "Full Name", "Assigned Role", "Official Email", "Account Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        usersTable = new JTable(tableModel);
        ModernTheme.styleTable(usersTable);

        usersTable.getColumnModel().getColumn(2).setCellRenderer((tbl, val, isSel, hasFoc, row, col) -> {
            String role = val != null ? val.toString() : "EMPLOYEE";
            String type = "INFO";
            if ("SUPER_ADMIN".equalsIgnoreCase(role)) type = "DANGER";
            else if ("HR_ADMIN".equalsIgnoreCase(role)) type = "WARNING";
            else if ("MANAGER".equalsIgnoreCase(role)) type = "SUCCESS";

            JLabel b = ModernTheme.createBadge(role, type);
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 4));
            p.setOpaque(isSel);
            p.setBackground(isSel ? ModernTheme.PRIMARY_LIGHT : (row % 2 == 0 ? Color.WHITE : ModernTheme.SLATE_50));
            p.add(b);
            return p;
        });

        usersTable.getColumnModel().getColumn(4).setCellRenderer((tbl, val, isSel, hasFoc, row, col) -> {
            String status = val != null ? val.toString() : "Active";
            JLabel b = ModernTheme.createBadge(status, "Active".equalsIgnoreCase(status) ? "SUCCESS" : "DANGER");
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 4));
            p.setOpaque(isSel);
            p.setBackground(isSel ? ModernTheme.PRIMARY_LIGHT : (row % 2 == 0 ? Color.WHITE : ModernTheme.SLATE_50));
            p.add(b);
            return p;
        });
    }

    private JPanel createBottomToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout(10, 5));
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(8, 5, 5, 5));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        btnRefresh = ModernTheme.createSecondaryButton("Refresh", new ImageIcon("images/reload_Hover.png"));
        btnRefresh.addActionListener(e -> loadUsers());

        btnAddUser = ModernTheme.createSecondaryButton("Add User Account", new ImageIcon("images/employee.png"));
        btnAddUser.addActionListener(e -> showAddUserDialog());

        btnEditRole = ModernTheme.createSecondaryButton("Change Role", new ImageIcon("images/edit.png"));
        btnEditRole.addActionListener(e -> changeSelectedUserRole());

        btnResetPassword = ModernTheme.createSecondaryButton("Reset Password", new ImageIcon("images/Key.gif"));
        btnResetPassword.addActionListener(e -> resetSelectedUserPassword());

        btnToggleStatus = ModernTheme.createPrimaryButton("Toggle Active/Inactive", new ImageIcon("images/modify.png"));
        btnToggleStatus.addActionListener(e -> toggleSelectedUserStatus());

        actions.add(btnRefresh);
        actions.add(btnAddUser);
        actions.add(btnEditRole);
        actions.add(btnResetPassword);
        actions.add(btnToggleStatus);

        toolbar.add(actions, BorderLayout.EAST);
        return toolbar;
    }

    private String getSelectedUsername() {
        int row = usersTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a user from the table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        int modelRow = usersTable.convertRowIndexToModel(row);
        return (String) tableModel.getValueAt(modelRow, 0);
    }

    public void loadUsers() {
        tableModel.setRowCount(0);
        try (Connection conn = clsConnection.getConnection()) {
            String sql = "SELECT Username, User_Role, Full_Name, Email, Status FROM USER_ROLES ORDER BY Username";
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                            rs.getString("Username"),
                            rs.getString("Full_Name"),
                            rs.getString("User_Role"),
                            rs.getString("Email"),
                            rs.getString("Status")
                    });
                }
            }
        } catch (Exception ex) {
            System.err.println("Error loading users: " + ex.getMessage());
        }
    }

    private void showAddUserDialog() {
        if (!AuthService.canManageSettings()) {
            JOptionPane.showMessageDialog(this, "Only Super Administrator can provision new accounts.", "Permission Denied", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
        JTextField txtUName = ModernTheme.createTextField(12);
        JPasswordField txtPass = new JPasswordField(12);
        JTextField txtName = ModernTheme.createTextField(12);
        JTextField txtEmail = ModernTheme.createTextField(12);
        JComboBox<String> cmbRole = new JComboBox<>(ROLES);

        form.add(new JLabel("Username:"));
        form.add(txtUName);
        form.add(new JLabel("Initial Password:"));
        form.add(txtPass);
        form.add(new JLabel("Full Name:"));
        form.add(txtName);
        form.add(new JLabel("Email Address:"));
        form.add(txtEmail);
        form.add(new JLabel("Role:"));
        form.add(cmbRole);

        int opt = JOptionPane.showConfirmDialog(this, form, "Create System User", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opt == JOptionPane.OK_OPTION) {
            String u = txtUName.getText().trim();
            String p = new String(txtPass.getPassword()).trim();
            String n = txtName.getText().trim();
            String em = txtEmail.getText().trim();
            String r = (String) cmbRole.getSelectedItem();

            if (u.isEmpty() || p.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username and password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = clsConnection.getConnection()) {
                // Insert into Login
                try (PreparedStatement ps = conn.prepareStatement("INSERT INTO Login (username, password) VALUES (?, ?)")) {
                    ps.setString(1, u);
                    ps.setString(2, p);
                    ps.executeUpdate();
                }

                // Insert into USER_ROLES
                try (PreparedStatement ps = conn.prepareStatement("INSERT INTO USER_ROLES (Username, User_Role, Full_Name, Email, Status) VALUES (?, ?, ?, ?, 'Active')")) {
                    ps.setString(1, u);
                    ps.setString(2, r);
                    ps.setString(3, n);
                    ps.setString(4, em);
                    ps.executeUpdate();
                }

                AuditService.log(AuthService.getCurrentUser(), "CREATE_USER", "USER_ROLES", "Created user account: " + u + " with role: " + r);
                JOptionPane.showMessageDialog(this, "User account '" + u + "' created successfully!", "User Created", JOptionPane.INFORMATION_MESSAGE);
                loadUsers();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error creating user: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void changeSelectedUserRole() {
        if (!AuthService.canManageSettings()) {
            JOptionPane.showMessageDialog(this, "Only Super Administrator can alter user permissions.", "Permission Denied", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String uName = getSelectedUsername();
        if (uName == null) return;

        JComboBox<String> cmbRole = new JComboBox<>(ROLES);
        int opt = JOptionPane.showConfirmDialog(this, cmbRole, "Select New Role for " + uName, JOptionPane.OK_CANCEL_OPTION);
        if (opt == JOptionPane.OK_OPTION) {
            String newRole = (String) cmbRole.getSelectedItem();
            try (Connection conn = clsConnection.getConnection()) {
                try (PreparedStatement ps = conn.prepareStatement("UPDATE USER_ROLES SET User_Role = ? WHERE Username = ?")) {
                    ps.setString(1, newRole);
                    ps.setString(2, uName);
                    ps.executeUpdate();
                }
                AuditService.log(AuthService.getCurrentUser(), "CHANGE_ROLE", "USER_ROLES", "Changed role of " + uName + " to " + newRole);
                JOptionPane.showMessageDialog(this, "User " + uName + " role updated to " + newRole, "Role Updated", JOptionPane.INFORMATION_MESSAGE);
                loadUsers();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error updating role: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void resetSelectedUserPassword() {
        if (!AuthService.canManageSettings()) {
            JOptionPane.showMessageDialog(this, "Only Super Administrator can reset user passwords.", "Permission Denied", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String uName = getSelectedUsername();
        if (uName == null) return;

        JPasswordField pf = new JPasswordField();
        int opt = JOptionPane.showConfirmDialog(this, pf, "Enter New Password for " + uName, JOptionPane.OK_CANCEL_OPTION);
        if (opt == JOptionPane.OK_OPTION) {
            String newPass = new String(pf.getPassword()).trim();
            if (newPass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Password cannot be empty.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try (Connection conn = clsConnection.getConnection()) {
                try (PreparedStatement ps = conn.prepareStatement("UPDATE Login SET password = ? WHERE LCASE(username) = LCASE(?)")) {
                    ps.setString(1, newPass);
                    ps.setString(2, uName);
                    ps.executeUpdate();
                }
                AuditService.log(AuthService.getCurrentUser(), "RESET_PASSWORD", "Login", "Reset password for user: " + uName);
                JOptionPane.showMessageDialog(this, "Password for '" + uName + "' has been successfully reset!", "Password Reset", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error resetting password: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void toggleSelectedUserStatus() {
        if (!AuthService.canManageSettings()) {
            JOptionPane.showMessageDialog(this, "Only Super Administrator can toggle user status.", "Permission Denied", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String uName = getSelectedUsername();
        if (uName == null) return;

        try (Connection conn = clsConnection.getConnection()) {
            String current = "Active";
            try (PreparedStatement ps = conn.prepareStatement("SELECT Status FROM USER_ROLES WHERE Username = ?")) {
                ps.setString(1, uName);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) current = rs.getString(1);
                }
            }

            String newSt = "Active".equalsIgnoreCase(current) ? "Inactive" : "Active";
            try (PreparedStatement ps = conn.prepareStatement("UPDATE USER_ROLES SET Status = ? WHERE Username = ?")) {
                ps.setString(1, newSt);
                ps.setString(2, uName);
                ps.executeUpdate();
            }

            AuditService.log(AuthService.getCurrentUser(), "USER_STATUS", "USER_ROLES", "Toggled status of " + uName + " to " + newSt);
            loadUsers();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
