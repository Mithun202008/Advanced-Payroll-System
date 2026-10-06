import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class Settingswindow extends JInternalFrame implements ActionListener, ItemListener {
    private JFrame JFParentFrame;
    private JComboBox<String> Cat_Name;
    private JTextField TxtCategory_Name, TxtBasic;
    private JTextField TxtDA1, TxtHRA1, TxtWA1;
    private JTextField TxtGPF1, TxtIT1, TxtGIS1, TxtPF1, TxtLIC1;
    private JLabel lblDAUnit, lblHRAUnit, lblWAUnit;
    private JLabel lblGPFUnit, lblITUnit, lblGISUnit, lblPFUnit, lblLICUnit;
    private JCheckBox chDA, chHRA, chWA, chGPF, chIT, chGIS, chPF, chLIC;
    private JButton AddBtn, ChangeBtn, DeleteBtn, ExitBtn;

    private clsSettings settings = new clsSettings();

    public Settingswindow(JFrame getParentFrame) {
        super("Employee Position & Salary Settings", true, true, true, true);
        this.JFParentFrame = getParentFrame;
        setSize(720, 680);
        setFrameIcon(new ImageIcon("images/setting.png"));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(12, 15, 12, 15));

        // Top Panel: Position Selector and Basic Salary
        JPanel topPanel = new JPanel(new GridLayout(2, 1, 8, 8));
        topPanel.setBorder(new CompoundBorder(new TitledBorder("Position / Category Details"), new EmptyBorder(5, 10, 5, 10)));

        JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        p1.add(new JLabel("Select Position:"));
        Cat_Name = new JComboBox<>();
        Cat_Name.setPreferredSize(new Dimension(180, 25));
        Cat_Name.addActionListener(this);
        p1.add(Cat_Name);

        p1.add(Box.createHorizontalStrut(15));
        p1.add(new JLabel("New Position Name:"));
        TxtCategory_Name = new JTextField(15);
        p1.add(TxtCategory_Name);
        topPanel.add(p1);

        JPanel p2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        p2.add(new JLabel("Base Basic Salary (Rs):"));
        TxtBasic = new JTextField(12);
        settings.Numvalidator(TxtBasic);
        p2.add(TxtBasic);
        topPanel.add(p2);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Center Panel: Allowances & Deductions
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 10, 10));

        // Allowances Panel
        JPanel allowPanel = new JPanel(new GridLayout(3, 4, 10, 6));
        allowPanel.setBorder(new CompoundBorder(new TitledBorder("Allowances (Earnings)"), new EmptyBorder(5, 10, 5, 10)));

        chDA = new JCheckBox("DA (%)");
        chDA.addItemListener(this);
        TxtDA1 = new JTextField();
        settings.Numvalidator(TxtDA1);
        lblDAUnit = new JLabel("Fixed Rs");
        allowPanel.add(new JLabel("Dearness Allowance (DA):"));
        allowPanel.add(chDA);
        allowPanel.add(TxtDA1);
        allowPanel.add(lblDAUnit);

        chHRA = new JCheckBox("HRA (%)");
        chHRA.addItemListener(this);
        TxtHRA1 = new JTextField();
        settings.Numvalidator(TxtHRA1);
        lblHRAUnit = new JLabel("Fixed Rs");
        allowPanel.add(new JLabel("House Rent Allowance (HRA):"));
        allowPanel.add(chHRA);
        allowPanel.add(TxtHRA1);
        allowPanel.add(lblHRAUnit);

        chWA = new JCheckBox("WA (%)");
        chWA.addItemListener(this);
        TxtWA1 = new JTextField();
        settings.Numvalidator(TxtWA1);
        lblWAUnit = new JLabel("Fixed Rs");
        allowPanel.add(new JLabel("Washing/Water Allowance (WA):"));
        allowPanel.add(chWA);
        allowPanel.add(TxtWA1);
        allowPanel.add(lblWAUnit);

        centerPanel.add(allowPanel);

        // Deductions Panel
        JPanel deduPanel = new JPanel(new GridLayout(5, 4, 10, 6));
        deduPanel.setBorder(new CompoundBorder(new TitledBorder("Deductions"), new EmptyBorder(5, 10, 5, 10)));

        chGPF = new JCheckBox("GPF (%)");
        chGPF.addItemListener(this);
        TxtGPF1 = new JTextField();
        settings.Numvalidator(TxtGPF1);
        lblGPFUnit = new JLabel("Fixed Rs");
        deduPanel.add(new JLabel("General Provident Fund (GPF):"));
        deduPanel.add(chGPF);
        deduPanel.add(TxtGPF1);
        deduPanel.add(lblGPFUnit);

        chIT = new JCheckBox("IT (%)");
        chIT.addItemListener(this);
        TxtIT1 = new JTextField();
        settings.Numvalidator(TxtIT1);
        lblITUnit = new JLabel("Fixed Rs");
        deduPanel.add(new JLabel("Income Tax (IT):"));
        deduPanel.add(chIT);
        deduPanel.add(TxtIT1);
        deduPanel.add(lblITUnit);

        chGIS = new JCheckBox("GIS (%)");
        chGIS.addItemListener(this);
        TxtGIS1 = new JTextField();
        settings.Numvalidator(TxtGIS1);
        lblGISUnit = new JLabel("Fixed Rs");
        deduPanel.add(new JLabel("Group Insurance Scheme (GIS):"));
        deduPanel.add(chGIS);
        deduPanel.add(TxtGIS1);
        deduPanel.add(lblGISUnit);

        chPF = new JCheckBox("PF (%)");
        chPF.addItemListener(this);
        TxtPF1 = new JTextField();
        settings.Numvalidator(TxtPF1);
        lblPFUnit = new JLabel("Fixed Rs");
        deduPanel.add(new JLabel("Provident Fund (PF):"));
        deduPanel.add(chPF);
        deduPanel.add(TxtPF1);
        deduPanel.add(lblPFUnit);

        chLIC = new JCheckBox("LIC (%)");
        chLIC.addItemListener(this);
        TxtLIC1 = new JTextField();
        settings.Numvalidator(TxtLIC1);
        lblLICUnit = new JLabel("Fixed Rs");
        deduPanel.add(new JLabel("Life Insurance (LIC):"));
        deduPanel.add(chLIC);
        deduPanel.add(TxtLIC1);
        deduPanel.add(lblLICUnit);

        centerPanel.add(deduPanel);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom Button Toolbar
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        AddBtn = new JButton("Add As New");
        AddBtn.setIcon(new ImageIcon("images/add.gif"));
        AddBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        AddBtn.addActionListener(this);

        ChangeBtn = new JButton("Update Selected");
        ChangeBtn.setIcon(new ImageIcon("images/save_all.png"));
        ChangeBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        ChangeBtn.addActionListener(this);

        DeleteBtn = new JButton("Delete Selected");
        DeleteBtn.setIcon(new ImageIcon("images/delete.png"));
        DeleteBtn.addActionListener(this);

        ExitBtn = new JButton("Exit");
        ExitBtn.setIcon(new ImageIcon("images/exit.png"));
        ExitBtn.addActionListener(this);

        btnPanel.add(AddBtn);
        btnPanel.add(ChangeBtn);
        btnPanel.add(DeleteBtn);
        btnPanel.add(ExitBtn);

        mainPanel.add(btnPanel, BorderLayout.SOUTH);
        getContentPane().add(mainPanel);

        loadPositions();
    }

    public void loadPositions() {
        Cat_Name.removeActionListener(this);
        Cat_Name.removeAllItems();
        try (Connection conn = clsConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Category_Name FROM Settings ORDER BY Category_Name")) {

            while (rs.next()) {
                String name = rs.getString("Category_Name");
                if (name != null && !name.trim().isEmpty()) {
                    Cat_Name.addItem(name.trim());
                }
            }
        } catch (Exception ex) {
            System.err.println("Error loading settings categories: " + ex.getMessage());
        }

        Cat_Name.addActionListener(this);
        if (Cat_Name.getItemCount() > 0) {
            Cat_Name.setSelectedIndex(0);
            fillForm((String) Cat_Name.getSelectedItem());
        }
    }

    private void fillForm(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) return;

        try (Connection conn = clsConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Settings WHERE Category_Name = ?")) {

            pstmt.setString(1, categoryName.trim());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    TxtCategory_Name.setText(rs.getString("Category_Name"));
                    TxtBasic.setText(String.valueOf(rs.getInt("Basic_Pay")));

                    TxtDA1.setText(String.valueOf(rs.getInt("DA_Allow")));
                    TxtHRA1.setText(String.valueOf(rs.getInt("HRA_Allow")));
                    TxtWA1.setText(String.valueOf(rs.getInt("WA_Allow")));

                    TxtGPF1.setText(String.valueOf(rs.getInt("GPF_Dedu")));
                    TxtIT1.setText(String.valueOf(rs.getInt("IT_Dedu")));
                    TxtGIS1.setText(String.valueOf(rs.getInt("GIS_Dedu")));
                    TxtPF1.setText(String.valueOf(rs.getInt("PF_Dedu")));
                    TxtLIC1.setText(String.valueOf(rs.getInt("LIC_Dedu")));

                    setCheck(chDA, lblDAUnit, rs.getString("DA"));
                    setCheck(chHRA, lblHRAUnit, rs.getString("HRA"));
                    setCheck(chWA, lblWAUnit, rs.getString("WA"));
                    setCheck(chGPF, lblGPFUnit, rs.getString("GPF"));
                    setCheck(chIT, lblITUnit, rs.getString("IT"));
                    setCheck(chGIS, lblGISUnit, rs.getString("GIS"));
                    setCheck(chPF, lblPFUnit, rs.getString("PF"));
                    setCheck(chLIC, lblLICUnit, rs.getString("LIC"));
                }
            }
        } catch (Exception ex) {
            System.err.println("Error reading settings for " + categoryName + ": " + ex.getMessage());
        }
    }

    private void setCheck(JCheckBox cb, JLabel lbl, String isPercent) {
        boolean boolVal = "true".equalsIgnoreCase(isPercent);
        cb.setSelected(boolVal);
        lbl.setText(boolVal ? "% of Basic" : "Fixed Rs");
    }

    public void itemStateChanged(ItemEvent e) {
        Object item = e.getSource();
        boolean selected = (e.getStateChange() == ItemEvent.SELECTED);
        String labelText = selected ? "% of Basic" : "Fixed Rs";

        if (item == chDA) lblDAUnit.setText(labelText);
        else if (item == chHRA) lblHRAUnit.setText(labelText);
        else if (item == chWA) lblWAUnit.setText(labelText);
        else if (item == chGPF) lblGPFUnit.setText(labelText);
        else if (item == chIT) lblITUnit.setText(labelText);
        else if (item == chGIS) lblGISUnit.setText(labelText);
        else if (item == chPF) lblPFUnit.setText(labelText);
        else if (item == chLIC) lblLICUnit.setText(labelText);
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == Cat_Name) {
            String selected = (String) Cat_Name.getSelectedItem();
            if (selected != null) fillForm(selected);
        } else if (src == AddBtn) {
            addRecord();
        } else if (src == ChangeBtn) {
            editRecord();
        } else if (src == DeleteBtn) {
            deleteRecord();
        } else if (src == ExitBtn) {
            setVisible(false);
            dispose();
        }
    }

    private int parseSafeInt(JTextField tf, String fieldName) {
        String val = tf.getText().trim();
        if (val.isEmpty()) return 0;
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for " + fieldName, "Invalid Input", JOptionPane.WARNING_MESSAGE);
            throw ex;
        }
    }

    private void addRecord() {
        String catName = TxtCategory_Name.getText().trim();
        if (catName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a name in 'New Position Name'.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int basic = parseSafeInt(TxtBasic, "Basic Salary");
            int da = parseSafeInt(TxtDA1, "DA");
            int hra = parseSafeInt(TxtHRA1, "HRA");
            int wa = parseSafeInt(TxtWA1, "WA");
            int gpf = parseSafeInt(TxtGPF1, "GPF");
            int it = parseSafeInt(TxtIT1, "IT");
            int gis = parseSafeInt(TxtGIS1, "GIS");
            int pf = parseSafeInt(TxtPF1, "PF");
            int lic = parseSafeInt(TxtLIC1, "LIC");

            try (Connection conn = clsConnection.getConnection()) {
                // Check if exists
                String checkSql = "SELECT Category_Name FROM Settings WHERE Category_Name = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                    checkStmt.setString(1, catName);
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (rs.next()) {
                            JOptionPane.showMessageDialog(this, "Position '" + catName + "' already exists! Use 'Update Selected' to modify it.", "Duplicate", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                    }
                }

                // Get next Category_Type ID
                int nextType = 1;
                try (Statement s = conn.createStatement();
                     ResultSet rs = s.executeQuery("SELECT MAX(Category_Type) AS max_id FROM Settings")) {
                    if (rs.next()) {
                        nextType = rs.getInt("max_id") + 1;
                    }
                }

                String insertSql = "INSERT INTO Settings (Category_Name, Basic_Pay, DA_Allow, HRA_Allow, WA_Allow, "
                        + "GPF_Dedu, IT_Dedu, GIS_Dedu, LIC_Dedu, PF_Dedu, "
                        + "DA, HRA, WA, GPF, IT, GIS, PF, LIC, Category_Type) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    pstmt.setString(1, catName);
                    pstmt.setInt(2, basic);
                    pstmt.setInt(3, da);
                    pstmt.setInt(4, hra);
                    pstmt.setInt(5, wa);
                    pstmt.setInt(6, gpf);
                    pstmt.setInt(7, it);
                    pstmt.setInt(8, gis);
                    pstmt.setInt(9, lic);
                    pstmt.setInt(10, pf);

                    pstmt.setString(11, chDA.isSelected() ? "true" : "false");
                    pstmt.setString(12, chHRA.isSelected() ? "true" : "false");
                    pstmt.setString(13, chWA.isSelected() ? "true" : "false");
                    pstmt.setString(14, chGPF.isSelected() ? "true" : "false");
                    pstmt.setString(15, chIT.isSelected() ? "true" : "false");
                    pstmt.setString(16, chGIS.isSelected() ? "true" : "false");
                    pstmt.setString(17, chPF.isSelected() ? "true" : "false");
                    pstmt.setString(18, chLIC.isSelected() ? "true" : "false");
                    pstmt.setInt(19, nextType);

                    int rows = pstmt.executeUpdate();
                    if (rows == 1) {
                        JOptionPane.showMessageDialog(this, "Position '" + catName + "' created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadPositions();
                        Cat_Name.setSelectedItem(catName);
                    }
                }
            }
        } catch (NumberFormatException ignored) {
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding position: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editRecord() {
        String catName = (String) Cat_Name.getSelectedItem();
        if (catName == null || catName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a position to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int basic = parseSafeInt(TxtBasic, "Basic Salary");
            int da = parseSafeInt(TxtDA1, "DA");
            int hra = parseSafeInt(TxtHRA1, "HRA");
            int wa = parseSafeInt(TxtWA1, "WA");
            int gpf = parseSafeInt(TxtGPF1, "GPF");
            int it = parseSafeInt(TxtIT1, "IT");
            int gis = parseSafeInt(TxtGIS1, "GIS");
            int pf = parseSafeInt(TxtPF1, "PF");
            int lic = parseSafeInt(TxtLIC1, "LIC");

            try (Connection conn = clsConnection.getConnection()) {
                String updateSql = "UPDATE Settings SET Basic_Pay = ?, DA_Allow = ?, HRA_Allow = ?, WA_Allow = ?, "
                        + "GPF_Dedu = ?, IT_Dedu = ?, GIS_Dedu = ?, LIC_Dedu = ?, PF_Dedu = ?, "
                        + "DA = ?, HRA = ?, WA = ?, GPF = ?, IT = ?, GIS = ?, PF = ?, LIC = ? "
                        + "WHERE Category_Name = ?";

                try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                    pstmt.setInt(1, basic);
                    pstmt.setInt(2, da);
                    pstmt.setInt(3, hra);
                    pstmt.setInt(4, wa);
                    pstmt.setInt(5, gpf);
                    pstmt.setInt(6, it);
                    pstmt.setInt(7, gis);
                    pstmt.setInt(8, lic);
                    pstmt.setInt(9, pf);

                    pstmt.setString(10, chDA.isSelected() ? "true" : "false");
                    pstmt.setString(11, chHRA.isSelected() ? "true" : "false");
                    pstmt.setString(12, chWA.isSelected() ? "true" : "false");
                    pstmt.setString(13, chGPF.isSelected() ? "true" : "false");
                    pstmt.setString(14, chIT.isSelected() ? "true" : "false");
                    pstmt.setString(15, chGIS.isSelected() ? "true" : "false");
                    pstmt.setString(16, chPF.isSelected() ? "true" : "false");
                    pstmt.setString(17, chLIC.isSelected() ? "true" : "false");
                    pstmt.setString(18, catName);

                    int rows = pstmt.executeUpdate();
                    if (rows == 1) {
                        JOptionPane.showMessageDialog(this, "Position '" + catName + "' updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Could not update position.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (NumberFormatException ignored) {
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating position: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteRecord() {
        String catName = (String) Cat_Name.getSelectedItem();
        if (catName == null || catName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a position to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete position '" + catName + "'?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = clsConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Settings WHERE Category_Name = ?")) {
                pstmt.setString(1, catName);
                int rows = pstmt.executeUpdate();
                if (rows == 1) {
                    JOptionPane.showMessageDialog(this, "Position '" + catName + "' deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadPositions();
                } else {
                    JOptionPane.showMessageDialog(this, "Position not found in database.", "Warning", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting position: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
