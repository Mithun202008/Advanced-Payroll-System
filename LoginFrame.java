import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Modern LoginFrame provides enterprise authentication with role detection,
 * password recovery, and modern SaaS card styling.
 */
public class LoginFrame extends JFrame implements ActionListener {
    private JTextField userTxt;
    private JPasswordField passwordTxt;
    private JCheckBox chkShowPassword;
    private JButton loginBtn;
    private JButton resetBtn;
    private JButton exitBtn;

    public LoginFrame() {
        super("Enterprise Payroll System - Authentication");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Ensure database migrations and tables exist on startup
        try {
            DatabaseMigration.runMigrations();
        } catch (Exception ignored) {}

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setIconImage(new ImageIcon("images/Business.png").getImage());

        JPanel rootPanel = new JPanel(new BorderLayout(15, 15));
        rootPanel.setBorder(new EmptyBorder(26, 32, 26, 32));
        rootPanel.setBackground(ModernTheme.SLATE_50);

        JPanel card = ModernTheme.createCard();
        card.setLayout(new BorderLayout(15, 15));
        card.setBorder(new EmptyBorder(22, 26, 22, 26));

        // Header Section
        JPanel headerPanel = new JPanel(new BorderLayout(14, 5));
        headerPanel.setOpaque(false);

        JLabel logoLabel = new JLabel(new ImageIcon("images/Business.png"));
        headerPanel.add(logoLabel, BorderLayout.WEST);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 2));
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("ENTERPRISE PAYROLL SYSTEM");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titleLabel.setForeground(ModernTheme.PRIMARY);

        JLabel subTitleLabel = new JLabel("SaaS Edition v3.0 • Sign in with your security credentials");
        subTitleLabel.setFont(ModernTheme.FONT_REGULAR);
        subTitleLabel.setForeground(ModernTheme.TEXT_MUTED);

        titlePanel.add(titleLabel);
        titlePanel.add(subTitleLabel);
        headerPanel.add(titlePanel, BorderLayout.CENTER);

        card.add(headerPanel, BorderLayout.NORTH);

        // Credentials Input Section
        JPanel formContainer = new JPanel(new BorderLayout(5, 10));
        formContainer.setOpaque(false);

        JPanel formGrid = new JPanel(new GridLayout(2, 2, 12, 14));
        formGrid.setOpaque(false);
        formGrid.setBorder(new EmptyBorder(12, 5, 5, 5));

        JLabel userLbl = new JLabel("Username / ID:");
        userLbl.setFont(ModernTheme.FONT_BOLD);
        userLbl.setForeground(ModernTheme.TEXT_MAIN);
        userTxt = ModernTheme.createTextField(15);
        userTxt.setText("admin");

        JLabel passwordLbl = new JLabel("Security Password:");
        passwordLbl.setFont(ModernTheme.FONT_BOLD);
        passwordLbl.setForeground(ModernTheme.TEXT_MAIN);
        passwordTxt = new JPasswordField(15);
        passwordTxt.setText("admin");
        passwordTxt.setFont(ModernTheme.FONT_REGULAR);
        passwordTxt.setBorder(BorderFactory.createCompoundBorder(
                new ModernTheme.RoundedBorder(ModernTheme.SLATE_300, 6),
                new EmptyBorder(6, 10, 6, 10)
        ));

        // Enter key to submit
        KeyAdapter enterSubmit = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        };
        userTxt.addKeyListener(enterSubmit);
        passwordTxt.addKeyListener(enterSubmit);

        formGrid.add(userLbl);
        formGrid.add(userTxt);
        formGrid.add(passwordLbl);
        formGrid.add(passwordTxt);
        formContainer.add(formGrid, BorderLayout.CENTER);

        // Options Row (Show Password & Reset Link)
        JPanel optionsPanel = new JPanel(new BorderLayout());
        optionsPanel.setOpaque(false);
        optionsPanel.setBorder(new EmptyBorder(4, 5, 10, 5));

        chkShowPassword = new JCheckBox("Show Password");
        chkShowPassword.setOpaque(false);
        chkShowPassword.setFont(ModernTheme.FONT_SMALL);
        chkShowPassword.setFocusPainted(false);
        chkShowPassword.addActionListener(e -> {
            if (chkShowPassword.isSelected()) {
                passwordTxt.setEchoChar((char) 0);
            } else {
                passwordTxt.setEchoChar('•');
            }
        });
        optionsPanel.add(chkShowPassword, BorderLayout.WEST);

        resetBtn = new JButton("Forgot Password?");
        resetBtn.setBorderPainted(false);
        resetBtn.setContentAreaFilled(false);
        resetBtn.setFocusPainted(false);
        resetBtn.setFont(ModernTheme.FONT_SMALL);
        resetBtn.setForeground(ModernTheme.PRIMARY);
        resetBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        resetBtn.addActionListener(this);
        optionsPanel.add(resetBtn, BorderLayout.EAST);

        formContainer.add(optionsPanel, BorderLayout.SOUTH);
        card.add(formContainer, BorderLayout.CENTER);

        // Action Buttons at Bottom
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        btnPanel.setOpaque(false);

        loginBtn = ModernTheme.createPrimaryButton("Sign In to Portal", new ImageIcon("images/Key.gif"));
        loginBtn.addActionListener(this);

        exitBtn = ModernTheme.createSecondaryButton("Exit Application", new ImageIcon("images/exit.png"));
        exitBtn.addActionListener(this);

        btnPanel.add(loginBtn);
        btnPanel.add(exitBtn);
        card.add(btnPanel, BorderLayout.SOUTH);

        rootPanel.add(card, BorderLayout.CENTER);
        getContentPane().add(rootPanel);
        pack();

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screen.width - getWidth()) / 2, (screen.height - getHeight()) / 2);
    }

    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();
        if (source == loginBtn) {
            performLogin();
        } else if (source == resetBtn) {
            showPasswordResetDialog();
        } else if (source == exitBtn) {
            System.exit(0);
        }
    }

    public void performLogin() {
        String username = userTxt.getText().trim();
        String password = new String(passwordTxt.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both Username and Password.",
                    "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = clsConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT username, password FROM Login WHERE LCASE(username) = LCASE(?) AND password = ?")) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String authenticatedUser = rs.getString("username");
                    AuthService.setCurrentUser(authenticatedUser);
                    AuditService.log(authenticatedUser, "USER_LOGIN", "AUTH", "Successful authentication from console");

                    dispose();
                    MainMenu mm = new MainMenu(authenticatedUser, new Date());
                    mm.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Invalid credentials.\nIf you have forgotten your password, click 'Forgot Password?'.",
                            "Authentication Failed", JOptionPane.ERROR_MESSAGE);
                    passwordTxt.setText("");
                    passwordTxt.requestFocus();
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database Connection Error:\n" + ex.getMessage(),
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void showPasswordResetDialog() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 8, 8));
        JTextField userField = new JTextField(userTxt.getText().trim());
        JPasswordField newPassField = new JPasswordField();
        JPasswordField confirmPassField = new JPasswordField();

        panel.add(new JLabel("Username to Reset:"));
        panel.add(userField);
        panel.add(new JLabel("New Password:"));
        panel.add(newPassField);
        panel.add(new JLabel("Confirm Password:"));
        panel.add(confirmPassField);

        int option = JOptionPane.showConfirmDialog(this, panel,
                "Reset Account Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String uName = userField.getText().trim();
            String newPass = new String(newPassField.getPassword()).trim();
            String confPass = new String(confirmPassField.getPassword()).trim();

            if (uName.isEmpty() || newPass.isEmpty() || confPass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required to reset the password.",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!newPass.equals(confPass)) {
                JOptionPane.showMessageDialog(this, "The new passwords do not match.",
                        "Mismatch Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = clsConnection.getConnection()) {
                String checkSql = "SELECT username FROM Login WHERE LCASE(username) = LCASE(?)";
                boolean userExists = false;
                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                    checkStmt.setString(1, uName);
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        userExists = rs.next();
                    }
                }

                if (userExists) {
                    String updateSql = "UPDATE Login SET password = ? WHERE LCASE(username) = LCASE(?)";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setString(1, newPass);
                        updateStmt.setString(2, uName);
                        updateStmt.executeUpdate();
                    }
                } else {
                    String insertSql = "INSERT INTO Login (username, password) VALUES (?, ?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setString(1, uName);
                        insertStmt.setString(2, newPass);
                        insertStmt.executeUpdate();
                    }
                }

                AuditService.log(uName, "PASSWORD_RESET", "Login", "Password reset successfully via auth screen");
                JOptionPane.showMessageDialog(this,
                        "Password for '" + uName + "' has been successfully updated!\nYou can now sign in with your new password.",
                        "Password Reset Successful", JOptionPane.INFORMATION_MESSAGE);

                userTxt.setText(uName);
                passwordTxt.setText(newPass);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error resetting password: " + ex.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static LoginFrame instance;

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        System.setProperty("sun.java2d.uiScale.enabled", "true");

        SwingUtilities.invokeLater(() -> {
            instance = new LoginFrame();
            instance.setVisible(true);
            instance.toFront();
            instance.requestFocus();
        });
    }
}
