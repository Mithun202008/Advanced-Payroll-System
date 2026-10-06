import java.awt.*;
import java.io.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 * ModernAuditLogsViewer displays system audit trail:
 * Who performed what action, when, on which entity, and metadata details.
 */
public class ModernAuditLogsViewer extends JPanel {
    private MainMenu parentMenu;

    private JTable auditTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JTextField txtSearch;
    private JComboBox<String> cmbActionFilter;
    private JButton btnRefresh, btnExportCsv;

    public ModernAuditLogsViewer(MainMenu parent) {
        this.parentMenu = parent;
        setLayout(new BorderLayout(14, 14));
        setBackground(ModernTheme.SLATE_50);
        setBorder(new EmptyBorder(16, 20, 20, 20));

        // Header Card
        add(createHeaderCard(), BorderLayout.NORTH);

        // Center Table Card
        JPanel centerCard = ModernTheme.createCard();
        centerCard.setLayout(new BorderLayout(0, 10));

        setupAuditTable();
        JScrollPane scrollPane = new JScrollPane(auditTable);
        scrollPane.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, ModernTheme.SLATE_200));
        centerCard.add(scrollPane, BorderLayout.CENTER);

        // Bottom toolbar
        centerCard.add(createBottomToolbar(), BorderLayout.SOUTH);
        add(centerCard, BorderLayout.CENTER);

        loadAuditLogs();
    }

    private JPanel createHeaderCard() {
        JPanel card = ModernTheme.createCard();
        card.setLayout(new BorderLayout(15, 10));

        JPanel leftPanel = new JPanel(new GridLayout(2, 1, 0, 3));
        leftPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("System Audit Trail & Security Activity Logs");
        lblTitle.setFont(ModernTheme.FONT_TITLE);
        lblTitle.setForeground(ModernTheme.TEXT_MAIN);

        JLabel lblSub = new JLabel("Immutable Audit Log of System Actions, Financial Operations, and Staff Updates");
        lblSub.setFont(ModernTheme.FONT_REGULAR);
        lblSub.setForeground(ModernTheme.TEXT_MUTED);

        leftPanel.add(lblTitle);
        leftPanel.add(lblSub);
        card.add(leftPanel, BorderLayout.WEST);

        // Search & Filters
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        filterPanel.setOpaque(false);

        filterPanel.add(new JLabel("Search Logs:"));
        txtSearch = ModernTheme.createTextField(14);
        txtSearch.addActionListener(e -> applyFilter());
        filterPanel.add(txtSearch);

        filterPanel.add(new JLabel("Action:"));
        cmbActionFilter = new JComboBox<>(new String[]{"All Actions", "PAYROLL_FINALIZED", "CREATE_EMPLOYEE", "DELETE_EMPLOYEE", "STATUS_CHANGE", "LEAVE_STATUS_UPDATE", "ATTENDANCE_SAVED"});
        cmbActionFilter.addActionListener(e -> applyFilter());
        filterPanel.add(cmbActionFilter);

        btnRefresh = ModernTheme.createSecondaryButton("Refresh", new ImageIcon("images/reload_Hover.png"));
        btnRefresh.addActionListener(e -> loadAuditLogs());
        filterPanel.add(btnRefresh);

        card.add(filterPanel, BorderLayout.EAST);
        return card;
    }

    private void setupAuditTable() {
        String[] cols = {"Log ID", "Timestamp", "User", "Action Type", "Target Entity", "Activity Details"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        auditTable = new JTable(tableModel);
        ModernTheme.styleTable(auditTable);

        rowSorter = new TableRowSorter<>(tableModel);
        auditTable.setRowSorter(rowSorter);

        auditTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        auditTable.getColumnModel().getColumn(1).setPreferredWidth(140);
        auditTable.getColumnModel().getColumn(2).setPreferredWidth(90);
        auditTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        auditTable.getColumnModel().getColumn(4).setPreferredWidth(110);
        auditTable.getColumnModel().getColumn(5).setPreferredWidth(350);

        // Action badge renderer
        auditTable.getColumnModel().getColumn(3).setCellRenderer((tbl, val, isSel, hasFoc, row, col) -> {
            String act = val != null ? val.toString() : "";
            String type = "INFO";
            if (act.contains("FINALIZED") || act.contains("SAVED")) type = "SUCCESS";
            else if (act.contains("DELETE") || act.contains("RESET")) type = "DANGER";
            else if (act.contains("UPDATE") || act.contains("STATUS")) type = "WARNING";

            JLabel b = ModernTheme.createBadge(act, type);
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

        JLabel lblCount = new JLabel("Audit entries recorded in database.");
        lblCount.setFont(ModernTheme.FONT_SMALL);
        lblCount.setForeground(ModernTheme.TEXT_MUTED);
        toolbar.add(lblCount, BorderLayout.WEST);

        btnExportCsv = ModernTheme.createSecondaryButton("Export Audit Trail (CSV)", new ImageIcon("images/save_all.png"));
        btnExportCsv.addActionListener(e -> exportAuditCsv());
        toolbar.add(btnExportCsv, BorderLayout.EAST);

        return toolbar;
    }

    public void loadAuditLogs() {
        tableModel.setRowCount(0);
        try (Connection conn = clsConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Log_Id, Log_Timestamp, Username, Action_Type, Target_Entity, Details FROM AUDIT_LOGS ORDER BY Log_Id DESC")) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("Log_Id"),
                        rs.getString("Log_Timestamp"),
                        rs.getString("Username"),
                        rs.getString("Action_Type"),
                        rs.getString("Target_Entity"),
                        rs.getString("Details")
                });
            }
        } catch (Exception ex) {
            System.err.println("Error loading audit logs: " + ex.getMessage());
        }
    }

    private void applyFilter() {
        String search = txtSearch.getText().trim();
        String action = (String) cmbActionFilter.getSelectedItem();

        if (search.isEmpty() && (action == null || action.startsWith("All"))) {
            rowSorter.setRowFilter(null);
        } else if (!search.isEmpty() && (action == null || action.startsWith("All"))) {
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + search));
        } else if (search.isEmpty() && !action.startsWith("All")) {
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)^" + action + "$", 3));
        } else {
            java.util.List<RowFilter<Object, Object>> list = new java.util.ArrayList<>();
            list.add(RowFilter.regexFilter("(?i)" + search));
            list.add(RowFilter.regexFilter("(?i)^" + action + "$", 3));
            rowSorter.setRowFilter(RowFilter.andFilter(list));
        }
    }

    private void exportAuditCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("System_Audit_Trail_Export.csv"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
                pw.println("Log_Id,Timestamp,Username,Action_Type,Target_Entity,Details");
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    pw.println(String.format("%s,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"",
                            tableModel.getValueAt(i, 0),
                            tableModel.getValueAt(i, 1),
                            tableModel.getValueAt(i, 2),
                            tableModel.getValueAt(i, 3),
                            tableModel.getValueAt(i, 4),
                            tableModel.getValueAt(i, 5)));
                }
                JOptionPane.showMessageDialog(this, "Audit logs exported to:\n" + f.getAbsolutePath(), "Export Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error exporting CSV: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
