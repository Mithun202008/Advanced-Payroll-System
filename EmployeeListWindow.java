import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class EmployeeListWindow extends JInternalFrame implements ActionListener {
    private MainMenu parentMenu;
    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JTextField searchField;
    private JLabel countLabel;
    private JButton btnRefresh, btnAdd, btnEdit, btnDelete, btnPayslip, btnExport, btnClose;

    public EmployeeListWindow(MainMenu parent) {
        super("Employee Directory & Records", true, true, true, true);
        this.parentMenu = parent;
        setSize(850, 520);
        setFrameIcon(new ImageIcon("images/employees.png"));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Top Search and Control Panel
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        searchPanel.add(new JLabel("Search Personnel:"));
        searchField = new JTextField(22);
        searchField.setToolTipText("Filter by employee code, name, designation, or address");
        searchPanel.add(searchField);

        countLabel = new JLabel("Total Employees: 0");
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        countLabel.setForeground(new Color(40, 80, 140));
        searchPanel.add(Box.createHorizontalStrut(15));
        searchPanel.add(countLabel);

        topPanel.add(searchPanel, BorderLayout.WEST);

        // Actions toolbar
        JPanel actionToolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        btnRefresh = createStyledButton("Refresh", "images/reload_Hover.png");
        btnAdd = createStyledButton("Add New", "images/employee.png");
        btnEdit = createStyledButton("Edit", "images/edit.png");
        btnDelete = createStyledButton("Delete", "images/delete.png");
        btnPayslip = createStyledButton("Payslip", "images/emp_rpt.png");
        btnExport = createStyledButton("Export CSV", "images/save_all.png");

        actionToolbar.add(btnRefresh);
        actionToolbar.add(btnAdd);
        actionToolbar.add(btnEdit);
        actionToolbar.add(btnDelete);
        actionToolbar.add(btnPayslip);
        actionToolbar.add(btnExport);

        topPanel.add(actionToolbar, BorderLayout.EAST);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Table Setup
        String[] columnNames = {"Emp Code", "First Name", "Last Name", "Designation", "Address", "Contact No"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        employeeTable = new JTable(tableModel);
        employeeTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        employeeTable.setRowHeight(24);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        employeeTable.getTableHeader().setBackground(new Color(240, 243, 248));
        employeeTable.setShowGrid(true);
        employeeTable.setGridColor(new Color(230, 230, 230));

        // Center alignment for Code and Contact No
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        employeeTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        employeeTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        employeeTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        employeeTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        employeeTable.getColumnModel().getColumn(3).setPreferredWidth(140);
        employeeTable.getColumnModel().getColumn(4).setPreferredWidth(180);
        employeeTable.getColumnModel().getColumn(5).setPreferredWidth(110);

        rowSorter = new TableRowSorter<>(tableModel);
        employeeTable.setRowSorter(rowSorter);

        // Real-time search filter
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilter(); }
            public void removeUpdate(DocumentEvent e) { applyFilter(); }
            public void changedUpdate(DocumentEvent e) { applyFilter(); }
            private void applyFilter() {
                String text = searchField.getText().trim();
                if (text.isEmpty()) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
                updateCount();
            }
        });

        // Double click row to view/edit
        employeeTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelectedEmployee();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(employeeTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom status / close panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnClose = new JButton("Close");
        btnClose.setIcon(new ImageIcon("images/exit.png"));
        btnClose.addActionListener(this);
        bottomPanel.add(btnClose);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        getContentPane().add(mainPanel);

        loadEmployees();
    }

    private JButton createStyledButton(String text, String iconPath) {
        JButton btn = new JButton(text);
        File f = new File(iconPath);
        if (f.exists()) {
            btn.setIcon(new ImageIcon(iconPath));
        }
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setFocusPainted(false);
        btn.addActionListener(this);
        return btn;
    }

    public void loadEmployees() {
        tableModel.setRowCount(0);
        try (Connection conn = clsConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Emp_Code, Emp_Name1, Emp_name2, Emp_Desi, Emp_Add, Emp_No FROM EMPLOYEE ORDER BY Emp_Code")) {

            while (rs.next()) {
                String code = rs.getString("Emp_Code");
                String name1 = rs.getString("Emp_Name1");
                String name2 = rs.getString("Emp_name2");
                String desi = rs.getString("Emp_Desi");
                String add = rs.getString("Emp_Add");
                String no = rs.getString("Emp_No");

                tableModel.addRow(new Object[]{
                    code != null ? code.trim() : "",
                    name1 != null ? name1.trim() : "",
                    name2 != null ? name2.trim() : "",
                    desi != null ? desi.trim() : "",
                    add != null ? add.trim() : "",
                    no != null ? no.trim() : ""
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading employees: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        updateCount();
    }

    private void updateCount() {
        int count = employeeTable.getRowCount();
        countLabel.setText("Total Employees: " + count);
    }

    private String getSelectedEmpCode() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee row first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        int modelRow = employeeTable.convertRowIndexToModel(selectedRow);
        return (String) tableModel.getValueAt(modelRow, 0);
    }

    private void editSelectedEmployee() {
        String empCode = getSelectedEmpCode();
        if (empCode != null && parentMenu != null) {
            parentMenu.openEditEmployee(empCode);
        }
    }

    private void deleteSelectedEmployee() {
        String empCode = getSelectedEmpCode();
        if (empCode == null) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to permanently delete Employee '" + empCode + "'?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = clsConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM EMPLOYEE WHERE Emp_Code = ?")) {
                pstmt.setString(1, empCode);
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Employee " + empCode + " deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadEmployees();
                } else {
                    JOptionPane.showMessageDialog(this, "Could not delete employee record.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting employee: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportToCSV() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No employee records to export.", "Empty Table", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Employee Records to CSV");
        fileChooser.setSelectedFile(new File("Employees_Export.csv"));
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (PrintWriter pw = new PrintWriter(new FileWriter(fileToSave))) {
                // Header
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    pw.print("\"" + tableModel.getColumnName(i) + "\"" + (i == tableModel.getColumnCount() - 1 ? "" : ","));
                }
                pw.println();

                // Rows
                for (int r = 0; r < tableModel.getRowCount(); r++) {
                    for (int c = 0; c < tableModel.getColumnCount(); c++) {
                        Object val = tableModel.getValueAt(r, c);
                        pw.print("\"" + (val != null ? val.toString().replace("\"", "\"\"") : "") + "\"" + (c == tableModel.getColumnCount() - 1 ? "" : ","));
                    }
                    pw.println();
                }

                JOptionPane.showMessageDialog(this, "Exported successfully to:\n" + fileToSave.getAbsolutePath(), "Export Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to export CSV: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == btnRefresh) {
            loadEmployees();
        } else if (src == btnAdd) {
            if (parentMenu != null) parentMenu.openAddEmployee();
        } else if (src == btnEdit) {
            editSelectedEmployee();
        } else if (src == btnDelete) {
            deleteSelectedEmployee();
        } else if (src == btnPayslip) {
            String empCode = getSelectedEmpCode();
            if (empCode != null && parentMenu != null) {
                parentMenu.openPayslip(empCode);
            }
        } else if (src == btnExport) {
            exportToCSV();
        } else if (src == btnClose) {
            setVisible(false);
            dispose();
        }
    }
}
