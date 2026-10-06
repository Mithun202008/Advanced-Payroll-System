import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * MainMenu provides the central application frame for the Enterprise Payroll System.
 * Incorporates modern collateral sidebar navigation, dynamic view swapping,
 * global search, notification center, and complete backward compatibility with legacy internal frames.
 */
public class MainMenu extends JFrame implements ActionListener {
    private String sMSGBOX_TITLE = "Enterprise Payroll Management System v3.0";

    // Modern Shell Components
    private ModernSidebar sidebar;
    private ModernHeaderBar headerBar;
    private JPanel contentContainer;
    private CardLayout cardLayout;

    // View Panels
    private ModernDashboard dashboardView;
    private ModernEmployeeDirectory employeeDirectoryView;
    private ModernAttendanceManager attendanceView;
    private ModernLeaveManager leaveView;
    private ModernPayrollProcessor payrollProcessorView;
    private ModernReportsCenter reportsView;
    private ModernUserManagement userManagementView;
    private ModernAuditLogsViewer auditLogsView;
    private ModernSettingsView settingsView;

    // Legacy Desktop Pane for internal modal frames
    private JDesktopPane desktop = new JDesktopPane();

    // Menu Bar & Items (Preserved for compatibility)
    private JMenuBar menubar = new JMenuBar();
    private JMenu menuFile = new JMenu("File");
    private JMenu menuEmployee = new JMenu("Employee");
    private JMenu menuTools = new JMenu("Tools");
    private JMenu menuReports = new JMenu("Reports");
    private JMenu menuHelp = new JMenu("Help");

    private JMenuItem itemExit = new JMenuItem("Quit");
    private JMenuItem itemDirectory = new JMenuItem("Employee Directory");
    private JMenuItem itemAdd = new JMenuItem("Add Employee");
    private JMenuItem itemEdit = new JMenuItem("Edit Employee");
    private JMenuItem itemDelete = new JMenuItem("Delete Employee");
    private JMenuItem itemSettings = new JMenuItem("Position & Salary Settings");
    private JMenuItem itemChangePass = new JMenuItem("Change Password");
    private JMenuItem itemWebPortal = new JMenuItem("Launch Web SaaS Portal");
    private JMenuItem itemCalculator = new JMenuItem("Calculator");
    private JMenuItem itemNotePad = new JMenuItem("NotePad");
    private JMenuItem itemEmprpt = new JMenuItem("Employee PaySlip");
    private JMenuItem itemAuthor = new JMenuItem("About System");
    private JMenuItem itemHelp = new JMenuItem("User Guide & Help");

    // Internal Frames (Preserved)
    private Addwindow FormAddwindow;
    private Editwindow FormEditwindow;
    private Deletewindow FormDeletewindow;
    private Settingswindow FormSettingswindow;
    private Emprptwindow FormEmprptwindow;
    private EmployeeListWindow FormEmployeeList;
    private Authorwindow FormAuthorwindow;
    private Helpwindow FormHelpwindow;
    private ChangePasswordWindow FormChangePassword;

    private clsSettings settings = new clsSettings();
    private static String sUser = "Admin";
    private static Date td = new Date();
    private static String sLogin = DateFormat.getDateTimeInstance().format(td);

    public MainMenu(String user, Date date) {
        super("Enterprise Payroll System [SaaS Edition v3.0]");
        sUser = (user != null && !user.trim().isEmpty()) ? user : "Admin";
        td = (date != null) ? date : new Date();
        sLogin = DateFormat.getDateTimeInstance().format(td);

        AuthService.setCurrentUser(sUser);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setIconImage(new ImageIcon("images/Business.png").getImage());

        // Initialize Master Layout: Sidebar (WEST), Header (NORTH), Content (CENTER)
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(ModernTheme.SLATE_50);

        sidebar = new ModernSidebar(this);
        headerBar = new ModernHeaderBar(this);

        rootPanel.add(sidebar, BorderLayout.WEST);

        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.setOpaque(false);
        mainArea.add(headerBar, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        contentContainer = new JPanel(cardLayout);
        contentContainer.setOpaque(false);

        // Initialize views
        dashboardView = new ModernDashboard(this);
        employeeDirectoryView = new ModernEmployeeDirectory(this);
        attendanceView = new ModernAttendanceManager(this);
        leaveView = new ModernLeaveManager(this);
        payrollProcessorView = new ModernPayrollProcessor(this);
        reportsView = new ModernReportsCenter(this);
        userManagementView = new ModernUserManagement(this);
        auditLogsView = new ModernAuditLogsViewer(this);
        settingsView = new ModernSettingsView(this);

        // Setup legacy desktop pane container
        desktop.setBackground(new Color(235, 240, 248));
        desktop.setAutoscrolls(true);
        desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);

        contentContainer.add(dashboardView, "DASHBOARD");
        contentContainer.add(employeeDirectoryView, "EMPLOYEES");
        contentContainer.add(attendanceView, "ATTENDANCE");
        contentContainer.add(leaveView, "LEAVE");
        contentContainer.add(payrollProcessorView, "PAYROLL_RUN");
        contentContainer.add(reportsView, "REPORTS");
        contentContainer.add(userManagementView, "USERS");
        contentContainer.add(auditLogsView, "AUDIT_LOGS");
        contentContainer.add(settingsView, "SETTINGS");
        contentContainer.add(desktop, "LEGACY_DESKTOP");

        mainArea.add(contentContainer, BorderLayout.CENTER);
        rootPanel.add(mainArea, BorderLayout.CENTER);

        getContentPane().add(rootPanel);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                UnloadWindow();
            }
        });

        setJMenuBar(CreateJMenuBar());

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width = Math.min(1280, screen.width - 40);
        int height = Math.min(840, screen.height - 60);
        setSize(width, height);
        setMinimumSize(new Dimension(1080, 680));
        setLocation((screen.width - width) / 2, (screen.height - height) / 2);

        // Default view: Dashboard
        navigateTo("DASHBOARD");
    }

    public void navigateTo(String key) {
        sidebar.setActive(key);
        switch (key) {
            case "DASHBOARD":
                headerBar.setBreadcrumb("Overview  /  Executive Dashboard");
                dashboardView.loadDashboardData();
                cardLayout.show(contentContainer, "DASHBOARD");
                break;
            case "EMPLOYEES":
                headerBar.setBreadcrumb("Workforce  /  Employee Directory");
                employeeDirectoryView.loadEmployees();
                cardLayout.show(contentContainer, "EMPLOYEES");
                break;
            case "ADD_EMP":
                openAddEmployee();
                break;
            case "ATTENDANCE":
                headerBar.setBreadcrumb("Workforce  /  Attendance Tracker");
                attendanceView.loadAttendanceForDate();
                cardLayout.show(contentContainer, "ATTENDANCE");
                break;
            case "LEAVE":
                headerBar.setBreadcrumb("Workforce  /  Leave & Time-Off Management");
                leaveView.loadLeaveRequests();
                cardLayout.show(contentContainer, "LEAVE");
                break;
            case "PAYROLL_RUN":
                headerBar.setBreadcrumb("Payroll  /  12-Step Processing Pipeline");
                cardLayout.show(contentContainer, "PAYROLL_RUN");
                break;
            case "SETTINGS":
                headerBar.setBreadcrumb("Administration  /  System Settings & Salary Tiers");
                cardLayout.show(contentContainer, "SETTINGS");
                break;
            case "PAYSLIP":
                openPayslip(null);
                break;
            case "REPORTS":
                headerBar.setBreadcrumb("Reports  /  Financial & Attendance Registers");
                reportsView.runSelectedReport();
                cardLayout.show(contentContainer, "REPORTS");
                break;
            case "USERS":
                headerBar.setBreadcrumb("Administration  /  Role-Based Access Control");
                userManagementView.loadUsers();
                cardLayout.show(contentContainer, "USERS");
                break;
            case "AUDIT_LOGS":
                headerBar.setBreadcrumb("System  /  Immutable Audit Logs");
                auditLogsView.loadAuditLogs();
                cardLayout.show(contentContainer, "AUDIT_LOGS");
                break;
            case "ORG_SETTINGS":
                headerBar.setBreadcrumb("Administration  /  Organization Profile");
                cardLayout.show(contentContainer, "SETTINGS");
                break;
            default:
                cardLayout.show(contentContainer, "DASHBOARD");
                break;
        }
        headerBar.updateNotificationBadge();
    }

    public void openEmployeeProfile(String empCode) {
        ModernEmployeeProfile profile = new ModernEmployeeProfile(this, empCode);
        contentContainer.add(profile, "PROFILE_" + empCode);
        headerBar.setBreadcrumb("Workforce  /  Employee Profile  /  " + empCode);
        cardLayout.show(contentContainer, "PROFILE_" + empCode);
    }

    public void openAttendance() {
        navigateTo("ATTENDANCE");
    }

    public void openLeaveManager() {
        navigateTo("LEAVE");
    }

    public void openPayrollRunner() {
        navigateTo("PAYROLL_RUN");
    }

    public void openReports() {
        navigateTo("REPORTS");
    }

    public void openAuditLogs() {
        navigateTo("AUDIT_LOGS");
    }

    public void openNotificationCenter() {
        ModernNotificationCenter dialog = new ModernNotificationCenter(this);
        dialog.setVisible(true);
        headerBar.updateNotificationBadge();
    }

    public void performGlobalSearch(String query) {
        navigateTo("EMPLOYEES");
        employeeDirectoryView.filterGlobally(query);
    }

    public void confirmLogout() {
        int reply = JOptionPane.showConfirmDialog(this, "Sign out of your active session?",
                "Sign Out", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (reply == JOptionPane.YES_OPTION) {
            AuditService.log(AuthService.getCurrentUser(), "USER_LOGOUT", "AUTH", "User signed out.");
            dispose();
            LoginFrame lf = new LoginFrame();
            lf.setVisible(true);
        }
    }

    // =========================================================================
    // BACKWARD COMPATIBLE METHODS & INTERNAL FRAME LOADERS
    // =========================================================================

    public void openEmployeeList() {
        navigateTo("EMPLOYEES");
    }

    public void openAddEmployee() {
        cardLayout.show(contentContainer, "LEGACY_DESKTOP");
        headerBar.setBreadcrumb("Workforce  /  Add Employee");
        if (FormAddwindow == null || FormAddwindow.isClosed()) {
            FormAddwindow = new Addwindow(this);
            loadForm("Add - Employee", FormAddwindow);
        } else {
            activateFrame(FormAddwindow);
        }
    }

    public void openEditEmployee(String empCode) {
        cardLayout.show(contentContainer, "LEGACY_DESKTOP");
        headerBar.setBreadcrumb("Workforce  /  Edit Employee");
        if (FormEditwindow == null || FormEditwindow.isClosed()) {
            FormEditwindow = new Editwindow(this);
            loadForm("Edit - Employee Record", FormEditwindow);
        } else {
            activateFrame(FormEditwindow);
        }
        if (empCode != null) {
            FormEditwindow.selectEmployee(empCode);
        }
    }

    public void openDeleteEmployee(String empCode) {
        cardLayout.show(contentContainer, "LEGACY_DESKTOP");
        headerBar.setBreadcrumb("Workforce  /  Delete Employee");
        if (FormDeletewindow == null || FormDeletewindow.isClosed()) {
            FormDeletewindow = new Deletewindow(this);
            loadForm("Delete - Employee Record", FormDeletewindow);
        } else {
            activateFrame(FormDeletewindow);
        }
    }

    public void openSettings() {
        navigateTo("SETTINGS");
    }

    public void openPayslip(String empCode) {
        cardLayout.show(contentContainer, "LEGACY_DESKTOP");
        headerBar.setBreadcrumb("Payroll  /  Generate Payslip");
        if (FormEmprptwindow == null || FormEmprptwindow.isClosed()) {
            FormEmprptwindow = new Emprptwindow(this);
            loadForm("Employee PaySlip Generator", FormEmprptwindow);
        } else {
            activateFrame(FormEmprptwindow);
        }
        if (empCode != null) {
            FormEmprptwindow.selectEmployeeCode(empCode);
        }
    }

    public void openAbout() {
        cardLayout.show(contentContainer, "LEGACY_DESKTOP");
        if (FormAuthorwindow == null || FormAuthorwindow.isClosed()) {
            FormAuthorwindow = new Authorwindow(this);
            loadForm("About - Advanced Payroll System", FormAuthorwindow);
        } else {
            activateFrame(FormAuthorwindow);
        }
    }

    public void openHelp() {
        cardLayout.show(contentContainer, "LEGACY_DESKTOP");
        if (FormHelpwindow == null || FormHelpwindow.isClosed()) {
            FormHelpwindow = new Helpwindow(this);
            loadForm("Payroll System - User Guide & Help", FormHelpwindow);
        } else {
            activateFrame(FormHelpwindow);
        }
    }

    public void openChangePassword() {
        cardLayout.show(contentContainer, "LEGACY_DESKTOP");
        if (FormChangePassword == null || FormChangePassword.isClosed()) {
            FormChangePassword = new ChangePasswordWindow(sUser);
            loadForm("Security - Change Administrator Password", FormChangePassword);
        } else {
            activateFrame(FormChangePassword);
        }
    }

    private void activateFrame(JInternalFrame frame) {
        try {
            frame.setVisible(true);
            frame.setIcon(false);
            frame.setSelected(true);
            frame.toFront();
        } catch (PropertyVetoException ignored) {}
    }

    protected void loadForm(String title, JInternalFrame clsForm) {
        if (!isLoaded(title)) {
            desktop.add(clsForm);
            clsForm.setVisible(true);
        }
        activateFrame(clsForm);
    }

    protected boolean isLoaded(String formTitle) {
        JInternalFrame[] frames = desktop.getAllFrames();
        for (JInternalFrame f : frames) {
            if (f.getTitle().equalsIgnoreCase(formTitle)) {
                activateFrame(f);
                return true;
            }
        }
        return false;
    }

    protected void runComponents(String sComponents) {
        try {
            new ProcessBuilder(sComponents).start();
        } catch (IOException evt) {
            JOptionPane.showMessageDialog(this, "Could not launch " + sComponents + ":\n" + evt.getMessage(),
                    "Execution Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    protected void UnloadWindow() {
        int reply = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit the Payroll System?",
                sMSGBOX_TITLE, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (reply == JOptionPane.YES_OPTION) {
            AuditService.log(AuthService.getCurrentUser(), "APP_QUIT", "SYSTEM", "Application closed.");
            setVisible(false);
            dispose();
            System.exit(0);
        }
    }

    public static void setlogin(String sUsername, Date sDate) {
        sUser = sUsername;
        td = sDate;
        AuthService.setCurrentUser(sUsername);
    }

    protected JMenuBar CreateJMenuBar() {
        menuFile.add(settings.setJMenuItem(itemExit, "Quit Application", "images/exit.png"));
        itemExit.addActionListener(this);

        menuEmployee.add(settings.setJMenuItem(itemDirectory, "Employee Directory", "images/employees.png"));
        menuEmployee.addSeparator();
        menuEmployee.add(settings.setJMenuItem(itemAdd, "Add Employee", "images/employee.png"));
        menuEmployee.add(settings.setJMenuItem(itemEdit, "Edit Employee", "images/edit.png"));
        menuEmployee.add(settings.setJMenuItem(itemDelete, "Delete Employee", "images/delete.png"));

        itemDirectory.addActionListener(this);
        itemAdd.addActionListener(this);
        itemEdit.addActionListener(this);
        itemDelete.addActionListener(this);

        menuTools.add(settings.setJMenuItem(itemSettings, "Position & Salary Settings", "images/setting.png"));
        menuTools.add(settings.setJMenuItem(itemChangePass, "Change Password", "images/Key.gif"));
        menuTools.addSeparator();
        menuTools.add(settings.setJMenuItem(itemWebPortal, "Launch Web SaaS Portal", "images/explorer.png"));
        menuTools.add(settings.setJMenuItem(itemCalculator, "Calculator", "images/calc.png"));
        menuTools.add(settings.setJMenuItem(itemNotePad, "NotePad", "images/notepad.png"));

        itemSettings.addActionListener(this);
        itemChangePass.addActionListener(this);
        itemWebPortal.addActionListener(this);
        itemCalculator.addActionListener(this);
        itemNotePad.addActionListener(this);

        menuReports.add(settings.setJMenuItem(itemEmprpt, "Generate Employee PaySlip", "images/emp_rpt.png"));
        itemEmprpt.addActionListener(this);

        menuHelp.add(settings.setJMenuItem(itemHelp, "User Guide & Documentation", "images/help.png"));
        menuHelp.add(settings.setJMenuItem(itemAuthor, "About System", "images/xp.png"));

        itemHelp.addActionListener(this);
        itemAuthor.addActionListener(this);

        menubar.add(settings.setJMenu(menuFile));
        menubar.add(settings.setJMenu(menuEmployee));
        menubar.add(settings.setJMenu(menuTools));
        menubar.add(settings.setJMenu(menuReports));
        menubar.add(settings.setJMenu(menuHelp));
        return menubar;
    }

    public void actionPerformed(ActionEvent event) {
        Object object = event.getSource();
        if (object == itemExit) {
            UnloadWindow();
        } else if (object == itemDirectory) {
            navigateTo("EMPLOYEES");
        } else if (object == itemAdd) {
            openAddEmployee();
        } else if (object == itemEdit) {
            openEditEmployee(null);
        } else if (object == itemDelete) {
            openDeleteEmployee(null);
        } else if (object == itemSettings) {
            navigateTo("SETTINGS");
        } else if (object == itemChangePass) {
            openChangePassword();
        } else if (object == itemWebPortal) {
            launchWebPortal();
        } else if (object == itemCalculator) {
            runComponents("calc.exe");
        } else if (object == itemNotePad) {
            runComponents("notepad.exe");
        } else if (object == itemEmprpt) {
            openPayslip(null);
        } else if (object == itemAuthor) {
            openAbout();
        } else if (object == itemHelp) {
            openHelp();
        }
    }

    public void launchWebPortal() {
        new Thread(() -> {
            try {
                PayrollWebServer.main(new String[0]);
            } catch (Exception ignored) {}
        }).start();

        try {
            Desktop.getDesktop().browse(new java.net.URI("http://localhost:8080"));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Web Portal is live at: http://localhost:8080\nOpen this address in your web browser.",
                    "Web Portal Live", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
