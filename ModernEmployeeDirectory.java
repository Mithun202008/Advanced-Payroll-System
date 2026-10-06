import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 * ModernEmployeeDirectory provides advanced workforce directory management
 * featuring real-time search, multi-faceted filtering, status badges, pagination,
 * profile navigation, and CSV import/export.
 */
public class ModernEmployeeDirectory extends JPanel {
    private MainMenu parentMenu;

    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;

    private JTextField searchField;
    private JComboBox<String> cmbDepartmentFilter;
    private JComboBox<String> cmbDesignationFilter;
    private JComboBox<String> cmbStatusFilter;

    private JLabel lblTotalCount;
    private JButton btnAddEmployee, btnExportCsv, btnImportCsv, btnRefresh;

    public ModernEmployeeDirectory(MainMenu parent) {
        this.parentMenu = parent;
        setLayout(new BorderLayout(12, 12));
        setBackground(ModernTheme.SLATE_50);
        setBorder(new EmptyBorder(16, 20, 20, 20));

        // Top Control Toolbar
        add(createTopPanel(), BorderLayout.NORTH);

        // Center Table in Card
        JPanel tableCard = ModernTheme.createCard();
        tableCard.setLayout(new BorderLayout(0, 10));

        setupTable();
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, ModernTheme.SLATE_200));
        tableCard.add(scrollPane, BorderLayout.CENTER);

        // Bottom Actions Toolbar
        tableCard.add(createBottomToolbar(), BorderLayout.SOUTH);

        add(tableCard, BorderLayout.CENTER);

        loadEmployees();
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setOpaque(false);

        // Title and count
        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);

        JLabel lblTitle = new JLabel("Personnel Directory & Records");
        lblTitle.setFont(ModernTheme.FONT_TITLE);
        lblTitle.setForeground(ModernTheme.TEXT_MAIN);

        lblTotalCount = new JLabel("Showing 0 employees");
        lblTotalCount.setFont(ModernTheme.FONT_REGULAR);
        lblTotalCount.setForeground(ModernTheme.TEXT_MUTED);

        JPanel leftTitle = new JPanel(new GridLayout(2, 1, 0, 2));
        leftTitle.setOpaque(false);
        leftTitle.add(lblTitle);
        leftTitle.add(lblTotalCount);
        titleRow.add(leftTitle, BorderLayout.WEST);

        // Action buttons
        JPanel actionBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionBtns.setOpaque(false);

        btnRefresh = ModernTheme.createSecondaryButton("Refresh", new ImageIcon("images/reload_Hover.png"));
        btnRefresh.addActionListener(e -> loadEmployees());
        actionBtns.add(btnRefresh);

        btnImportCsv = ModernTheme.createSecondaryButton("Bulk Import CSV", new ImageIcon("images/OPEN.GIF"));
        btnImportCsv.addActionListener(e -> importCsvWorkflow());
        actionBtns.add(btnImportCsv);

        btnExportCsv = ModernTheme.createSecondaryButton("Export CSV", new ImageIcon("images/save_all.png"));
        btnExportCsv.addActionListener(e -> exportCsvWorkflow());
        actionBtns.add(btnExportCsv);

        btnAddEmployee = ModernTheme.createPrimaryButton("Add New Employee", new ImageIcon("images/employee.png"));
        btnAddEmployee.addActionListener(e -> parentMenu.openAddEmployee());
        actionBtns.add(btnAddEmployee);

        titleRow.add(actionBtns, BorderLayout.EAST);
        topPanel.add(titleRow, BorderLayout.NORTH);

        // Filter Bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        filterBar.setOpaque(false);

        // Search textfield
        filterBar.add(new JLabel("Search:"));
        searchField = ModernTheme.createTextField(16);
        searchField.setToolTipText("Filter by ID, name, phone, address...");
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilters(); }
            public void removeUpdate(DocumentEvent e) { applyFilters(); }
            public void changedUpdate(DocumentEvent e) { applyFilters(); }
        });
        filterBar.add(searchField);

        // Department filter
        filterBar.add(new JLabel("Department:"));
        cmbDepartmentFilter = new JComboBox<>(new String[]{"All Departments", "Engineering", "Product & Engineering", "Executive Management"});
        cmbDepartmentFilter.addActionListener(e -> applyFilters());
        filterBar.add(cmbDepartmentFilter);

        // Designation filter
        filterBar.add(new JLabel("Role:"));
        cmbDesignationFilter = new JComboBox<>(new String[]{"All Roles"});
        loadDesignationsFilter();
        cmbDesignationFilter.addActionListener(e -> applyFilters());
        filterBar.add(cmbDesignationFilter);

        // Status filter
        filterBar.add(new JLabel("Status:"));
        cmbStatusFilter = new JComboBox<>(new String[]{"All Statuses", "Active", "Inactive"});
        cmbStatusFilter.addActionListener(e -> applyFilters());
        filterBar.add(cmbStatusFilter);

        JButton btnClear = ModernTheme.createSecondaryButton("Clear Filters", null);
        btnClear.addActionListener(e -> {
            searchField.setText("");
            cmbDepartmentFilter.setSelectedIndex(0);
            cmbDesignationFilter.setSelectedIndex(0);
            cmbStatusFilter.setSelectedIndex(0);
            applyFilters();
        });
        filterBar.add(btnClear);

        topPanel.add(filterBar, BorderLayout.SOUTH);
        return topPanel;
    }

    private void setupTable() {
        String[] cols = {"Emp Code", "Full Name", "Designation", "Department", "Contact No", "Status", "Actions"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        table = new JTable(tableModel);
        ModernTheme.styleTable(table);

        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        table.getColumnModel().getColumn(0).setPreferredWidth(90);
        table.getColumnModel().getColumn(1).setPreferredWidth(160);
        table.getColumnModel().getColumn(2).setPreferredWidth(140);
        table.getColumnModel().getColumn(3).setPreferredWidth(140);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);
        table.getColumnModel().getColumn(5).setPreferredWidth(90);
        table.getColumnModel().getColumn(6).setPreferredWidth(160);

        // Status badge renderer for column 5
        table.getColumnModel().getColumn(5).setCellRenderer((tbl, val, isSel, hasFoc, row, col) -> {
            String status = val != null ? val.toString() : "Active";
            String badgeType = "Active".equalsIgnoreCase(status) ? "SUCCESS" : "DANGER";
            JLabel badge = ModernTheme.createBadge(status, badgeType);
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 4));
            p.setOpaque(isSel);
            p.setBackground(isSel ? ModernTheme.PRIMARY_LIGHT : (row % 2 == 0 ? Color.WHITE : ModernTheme.SLATE_50));
            p.add(badge);
            return p;
        });

        // Double click to open full profile
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() >= 0) {
                    int modelRow = table.convertRowIndexToModel(table.getSelectedRow());
                    String code = (String) tableModel.getValueAt(modelRow, 0);
                    parentMenu.openEmployeeProfile(code);
                }
            }
        });
    }

    private JPanel createBottomToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout(10, 5));
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(8, 0, 4, 0));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        leftPanel.setOpaque(false);
        JLabel lblHint = new JLabel("Double-click any row to view complete 360° Employee Profile.");
        lblHint.setFont(ModernTheme.FONT_SMALL);
        lblHint.setForeground(ModernTheme.TEXT_MUTED);
        leftPanel.add(lblHint);
        toolbar.add(leftPanel, BorderLayout.WEST);

        // Row action buttons
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        actions.setOpaque(false);

        JButton btnView = ModernTheme.createSecondaryButton("View Profile", new ImageIcon("images/Info.png"));
        btnView.addActionListener(e -> {
            String code = getSelectedEmpCode();
            if (code != null) parentMenu.openEmployeeProfile(code);
        });

        JButton btnEdit = ModernTheme.createSecondaryButton("Edit Record", new ImageIcon("images/edit.png"));
        btnEdit.addActionListener(e -> {
            String code = getSelectedEmpCode();
            if (code != null) parentMenu.openEditEmployee(code);
        });

        JButton btnPayslip = ModernTheme.createSecondaryButton("Generate Payslip", new ImageIcon("images/emp_rpt.png"));
        btnPayslip.addActionListener(e -> {
            String code = getSelectedEmpCode();
            if (code != null) parentMenu.openPayslip(code);
        });

        JButton btnDeactivate = ModernTheme.createSecondaryButton("Toggle Status", new ImageIcon("images/modify.png"));
        btnDeactivate.addActionListener(e -> toggleEmployeeStatus());

        JButton btnDelete = ModernTheme.createDangerButton("Delete", new ImageIcon("images/delete.png"));
        btnDelete.addActionListener(e -> deleteSelectedEmployee());

        actions.add(btnView);
        actions.add(btnEdit);
        actions.add(btnPayslip);
        actions.add(btnDeactivate);
        actions.add(btnDelete);

        toolbar.add(actions, BorderLayout.EAST);
        return toolbar;
    }

    private String getSelectedEmpCode() {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Please select an employee row first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        int modelRow = table.convertRowIndexToModel(selected);
        return (String) tableModel.getValueAt(modelRow, 0);
    }

    public void loadEmployees() {
        tableModel.setRowCount(0);
        try (Connection conn = clsConnection.getConnection()) {
            String sql = "SELECT e.Emp_Code, e.Emp_Name1, e.Emp_name2, e.Emp_Desi, e.Emp_No, " +
                    "p.Department, p.Status FROM EMPLOYEE e " +
                    "LEFT JOIN EMPLOYEE_PROFILE p ON e.Emp_Code = p.Emp_Code ORDER BY e.Emp_Code";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    String code = rs.getString("Emp_Code");
                    String n1 = rs.getString("Emp_Name1");
                    String n2 = rs.getString("Emp_name2");
                    String fullName = (n1 != null ? n1 : "") + " " + (n2 != null ? n2 : "");
                    String desi = rs.getString("Emp_Desi");
                    String dept = rs.getString("Department");
                    if (dept == null || dept.trim().isEmpty()) dept = "General";
                    String phone = rs.getString("Emp_No");
                    String status = rs.getString("Status");
                    if (status == null || status.trim().isEmpty()) status = "Active";

                    tableModel.addRow(new Object[]{
                            code,
                            fullName.trim(),
                            desi != null ? desi : "",
                            dept,
                            phone != null ? phone : "",
                            status,
                            "View / Actions"
                    });
                }
            }
        } catch (Exception ex) {
            System.err.println("Error loading employees in directory: " + ex.getMessage());
        }
        applyFilters();
    }

    private void loadDesignationsFilter() {
        try (Connection conn = clsConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Category_Name FROM Settings ORDER BY Category_Name")) {
            while (rs.next()) {
                String cat = rs.getString("Category_Name");
                if (cat != null && !cat.trim().isEmpty()) {
                    cmbDesignationFilter.addItem(cat.trim());
                }
            }
        } catch (Exception ignored) {}
    }

    private void applyFilters() {
        String search = searchField.getText().trim();
        String dept = (String) cmbDepartmentFilter.getSelectedItem();
        String desi = (String) cmbDesignationFilter.getSelectedItem();
        String status = (String) cmbStatusFilter.getSelectedItem();

        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        if (!search.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + search));
        }

        if (dept != null && !dept.startsWith("All")) {
            filters.add(RowFilter.regexFilter("(?i)^" + dept + "$", 3));
        }

        if (desi != null && !desi.startsWith("All")) {
            filters.add(RowFilter.regexFilter("(?i)^" + desi + "$", 2));
        }

        if (status != null && !status.startsWith("All")) {
            filters.add(RowFilter.regexFilter("(?i)^" + status + "$", 5));
        }

        if (filters.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.andFilter(filters));
        }

        lblTotalCount.setText("Showing " + table.getRowCount() + " of " + tableModel.getRowCount() + " personnel records");
    }

    public void filterGlobally(String query) {
        searchField.setText(query);
        applyFilters();
    }

    private void toggleEmployeeStatus() {
        String code = getSelectedEmpCode();
        if (code == null) return;

        try (Connection conn = clsConnection.getConnection()) {
            String selSql = "SELECT Status FROM EMPLOYEE_PROFILE WHERE Emp_Code = ?";
            String currentStatus = "Active";
            try (PreparedStatement pstmt = conn.prepareStatement(selSql)) {
                pstmt.setString(1, code);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) currentStatus = rs.getString("Status");
                }
            }

            String newStatus = "Active".equalsIgnoreCase(currentStatus) ? "Inactive" : "Active";
            String updSql = "UPDATE EMPLOYEE_PROFILE SET Status = ? WHERE Emp_Code = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updSql)) {
                pstmt.setString(1, newStatus);
                pstmt.setString(2, code);
                pstmt.executeUpdate();
            }

            AuditService.log(AuthService.getCurrentUser(), "STATUS_CHANGE", "EMPLOYEE", "Updated status of " + code + " to " + newStatus);
            JOptionPane.showMessageDialog(this, "Employee " + code + " status changed to: " + newStatus, "Status Updated", JOptionPane.INFORMATION_MESSAGE);
            loadEmployees();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error toggling status: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedEmployee() {
        if (!AuthService.canManageEmployees()) {
            JOptionPane.showMessageDialog(this, "Permission Denied: Only Administrator and HR can delete records.", "Access Restricted", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String code = getSelectedEmpCode();
        if (code == null) return;

        int reply = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to permanently delete Employee " + code + "?\nThis will remove their profile and payroll records.",
                "Confirm Employee Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (reply == JOptionPane.YES_OPTION) {
            try (Connection conn = clsConnection.getConnection()) {
                // Delete from EMPLOYEE
                try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM EMPLOYEE WHERE Emp_Code = ?")) {
                    pstmt.setString(1, code);
                    pstmt.executeUpdate();
                }
                // Delete from EMPLOYEE_PROFILE
                try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM EMPLOYEE_PROFILE WHERE Emp_Code = ?")) {
                    pstmt.setString(1, code);
                    pstmt.executeUpdate();
                }

                AuditService.log(AuthService.getCurrentUser(), "DELETE_EMPLOYEE", "EMPLOYEE", "Deleted employee record: " + code);
                JOptionPane.showMessageDialog(this, "Employee " + code + " has been deleted.", "Record Deleted", JOptionPane.INFORMATION_MESSAGE);
                loadEmployees();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting employee: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportCsvWorkflow() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("Employees_Directory_Export.csv"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
                pw.println("Emp_Code,Full_Name,Designation,Department,Contact_No,Status");
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    pw.println(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"",
                            tableModel.getValueAt(i, 0),
                            tableModel.getValueAt(i, 1),
                            tableModel.getValueAt(i, 2),
                            tableModel.getValueAt(i, 3),
                            tableModel.getValueAt(i, 4),
                            tableModel.getValueAt(i, 5)));
                }
                AuditService.log(AuthService.getCurrentUser(), "EXPORT_DATA", "EMPLOYEE", "Exported employee directory to " + file.getName());
                JOptionPane.showMessageDialog(this, "Successfully exported " + tableModel.getRowCount() + " records to:\n" + file.getAbsolutePath(),
                        "Export Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to export CSV:\n" + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void importCsvWorkflow() {
        if (!AuthService.canManageEmployees()) {
            JOptionPane.showMessageDialog(this, "Permission Denied: HR/Admin role required for bulk import.", "Access Restricted", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            int imported = 0;
            int errors = 0;

            try (BufferedReader br = new BufferedReader(new FileReader(file));
                 Connection conn = clsConnection.getConnection()) {

                String line = br.readLine(); // Header
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 6) {
                        String code = parts[0].replace("\"", "").trim();
                        String fName = parts[1].replace("\"", "").trim();
                        String desi = parts[2].replace("\"", "").trim();
                        String dept = parts[3].replace("\"", "").trim();
                        String phone = parts[4].replace("\"", "").trim();
                        String status = parts[5].replace("\"", "").trim();

                        try {
                            // Insert into EMPLOYEE
                            String insEmp = "INSERT INTO EMPLOYEE (Emp_Code, Emp_Name1, Emp_name2, Emp_Add, Emp_No, Emp_Desi) VALUES (?, ?, '', 'Office', ?, ?)";
                            try (PreparedStatement ps = conn.prepareStatement(insEmp)) {
                                ps.setString(1, code);
                                ps.setString(2, fName);
                                ps.setString(3, phone);
                                ps.setString(4, desi);
                                ps.executeUpdate();
                            }

                            // Insert into EMPLOYEE_PROFILE
                            String insProf = "INSERT INTO EMPLOYEE_PROFILE (Emp_Code, Department, Status, Email, Joining_Date, Employment_Type) VALUES (?, ?, ?, ?, '2023-01-01', 'Full-Time')";
                            try (PreparedStatement ps = conn.prepareStatement(insProf)) {
                                ps.setString(1, code);
                                ps.setString(2, dept);
                                ps.setString(3, status);
                                ps.setString(4, code.toLowerCase() + "@enterprise.com");
                                ps.executeUpdate();
                            }
                            imported++;
                        } catch (Exception itemEx) {
                            errors++;
                        }
                    }
                }

                AuditService.log(AuthService.getCurrentUser(), "BULK_IMPORT", "EMPLOYEE", "Imported " + imported + " records with " + errors + " skipped.");
                JOptionPane.showMessageDialog(this,
                        "Bulk Import Complete!\nSuccessfully Imported: " + imported + " records\nSkipped / Errored: " + errors + " records",
                        "Import Complete", JOptionPane.INFORMATION_MESSAGE);
                loadEmployees();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error reading CSV file:\n" + ex.getMessage(), "Import Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
