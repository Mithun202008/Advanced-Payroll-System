import java.awt.*;
import java.sql.*;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

/**
 * ModernSettingsView manages organization profile, branding metadata,
 * statutory tax IDs, and position salary structure configurations.
 */
public class ModernSettingsView extends JPanel {
    private MainMenu parentMenu;

    private JTextField txtOrgName, txtOrgTagline, txtOrgAddr, txtOrgPhone, txtOrgEmail, txtTaxId, txtCurrency, txtPayCycle;
    private JButton btnSaveOrgSettings;

    // Position Tiers
    private JComboBox<String> cmbPositions;
    private JTextField txtNewPosName, txtBasicPay;
    private JCheckBox chkDa, chkHra, chkWa, chkGpf, chkIt, chkGis, chkPf, chkLic;
    private JTextField txtDaVal, txtHraVal, txtWaVal, txtGpfVal, txtItVal, txtGisVal, txtPfVal, txtLicVal;
    private JButton btnAddPos, btnUpdatePos, btnDeletePos;

    public ModernSettingsView(MainMenu parent) {
        this.parentMenu = parent;
        setLayout(new BorderLayout(14, 14));
        setBackground(ModernTheme.SLATE_50);
        setBorder(new EmptyBorder(16, 20, 20, 20));

        // Header Card
        add(createHeaderCard(), BorderLayout.NORTH);

        // Center Tabbed Card
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(ModernTheme.FONT_BOLD);
        tabbedPane.setBackground(Color.WHITE);

        tabbedPane.addTab("Organization Profile & Branding", new ImageIcon("images/Business.png"), createOrgProfileTab());
        tabbedPane.addTab("Position & Salary Tiers Configurator", new ImageIcon("images/setting.png"), createSalaryTiersTab());

        add(tabbedPane, BorderLayout.CENTER);

        loadOrgSettings();
        loadPositions();
    }

    private JPanel createHeaderCard() {
        JPanel card = ModernTheme.createCard();
        card.setLayout(new BorderLayout(15, 10));

        JPanel leftPanel = new JPanel(new GridLayout(2, 1, 0, 3));
        leftPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("System Configuration & Salary Structures");
        lblTitle.setFont(ModernTheme.FONT_TITLE);
        lblTitle.setForeground(ModernTheme.TEXT_MAIN);

        JLabel lblSub = new JLabel("Enterprise Identity, Statutory Tax IDs, Payroll Policies, and Role Compensation Tiers");
        lblSub.setFont(ModernTheme.FONT_REGULAR);
        lblSub.setForeground(ModernTheme.TEXT_MUTED);

        leftPanel.add(lblTitle);
        leftPanel.add(lblSub);
        card.add(leftPanel, BorderLayout.WEST);
        return card;
    }

    private JPanel createOrgProfileTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 25, 20, 25));

        JPanel formGrid = new JPanel(new GridLayout(4, 2, 20, 16));
        formGrid.setOpaque(false);

        formGrid.add(createInputGroup("Organization Legal Name:", txtOrgName = ModernTheme.createTextField(20)));
        formGrid.add(createInputGroup("Branding Tagline:", txtOrgTagline = ModernTheme.createTextField(20)));
        formGrid.add(createInputGroup("Registered Business Address:", txtOrgAddr = ModernTheme.createTextField(20)));
        formGrid.add(createInputGroup("Official Phone Number:", txtOrgPhone = ModernTheme.createTextField(20)));
        formGrid.add(createInputGroup("Payroll Contact Email:", txtOrgEmail = ModernTheme.createTextField(20)));
        formGrid.add(createInputGroup("Corporate Tax ID (GSTIN / EIN):", txtTaxId = ModernTheme.createTextField(20)));
        formGrid.add(createInputGroup("Disbursement Currency Symbol:", txtCurrency = ModernTheme.createTextField(20)));
        formGrid.add(createInputGroup("Standard Payroll Cycle:", txtPayCycle = ModernTheme.createTextField(20)));

        panel.add(formGrid, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnPanel.setOpaque(false);

        btnSaveOrgSettings = ModernTheme.createPrimaryButton("Save Organization Profile", new ImageIcon("images/save_all.png"));
        btnSaveOrgSettings.addActionListener(e -> saveOrgSettings());
        btnPanel.add(btnSaveOrgSettings);

        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createInputGroup(String label, JTextField tf) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(ModernTheme.FONT_BOLD);
        lbl.setForeground(ModernTheme.TEXT_MAIN);
        p.add(lbl, BorderLayout.NORTH);
        p.add(tf, BorderLayout.CENTER);
        return p;
    }

    private JPanel createSalaryTiersTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(16, 20, 16, 20));

        // Top: Position selector
        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 5));
        topRow.setOpaque(false);
        topRow.setBorder(new CompoundBorder(new TitledBorder("Position Tier Selection"), new EmptyBorder(5, 5, 5, 5)));

        topRow.add(new JLabel("Select Position:"));
        cmbPositions = new JComboBox<>();
        cmbPositions.setPreferredSize(new Dimension(180, 26));
        cmbPositions.addActionListener(e -> fillSalaryTierDetails((String) cmbPositions.getSelectedItem()));
        topRow.add(cmbPositions);

        topRow.add(Box.createHorizontalStrut(15));
        topRow.add(new JLabel("Position Name:"));
        txtNewPosName = ModernTheme.createTextField(14);
        topRow.add(txtNewPosName);

        topRow.add(Box.createHorizontalStrut(15));
        topRow.add(new JLabel("Base Basic Salary (Rs):"));
        txtBasicPay = ModernTheme.createTextField(10);
        topRow.add(txtBasicPay);

        panel.add(topRow, BorderLayout.NORTH);

        // Center: Allowances & Deductions
        JPanel midPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        midPanel.setOpaque(false);

        // Allowances
        JPanel pAllow = new JPanel(new GridLayout(3, 4, 10, 8));
        pAllow.setBorder(new CompoundBorder(new TitledBorder("Earnings & Allowances Structure"), new EmptyBorder(8, 10, 8, 10)));

        pAllow.add(new JLabel("Dearness Allowance (DA):"));
        chkDa = new JCheckBox("Percentage (%)");
        txtDaVal = ModernTheme.createTextField(8);
        pAllow.add(chkDa);
        pAllow.add(txtDaVal);
        pAllow.add(new JLabel("Value (Rs or %)"));

        pAllow.add(new JLabel("House Rent Allowance (HRA):"));
        chkHra = new JCheckBox("Percentage (%)");
        txtHraVal = ModernTheme.createTextField(8);
        pAllow.add(chkHra);
        pAllow.add(txtHraVal);
        pAllow.add(new JLabel("Value (Rs or %)"));

        pAllow.add(new JLabel("Washing Allowance (WA):"));
        chkWa = new JCheckBox("Percentage (%)");
        txtWaVal = ModernTheme.createTextField(8);
        pAllow.add(chkWa);
        pAllow.add(txtWaVal);
        pAllow.add(new JLabel("Value (Rs or %)"));

        midPanel.add(pAllow);

        // Deductions
        JPanel pDedu = new JPanel(new GridLayout(5, 4, 10, 6));
        pDedu.setBorder(new CompoundBorder(new TitledBorder("Statutory & Voluntary Deductions"), new EmptyBorder(8, 10, 8, 10)));

        pDedu.add(new JLabel("General Provident Fund (GPF):"));
        chkGpf = new JCheckBox("Percentage (%)");
        txtGpfVal = ModernTheme.createTextField(8);
        pDedu.add(chkGpf);
        pDedu.add(txtGpfVal);
        pDedu.add(new JLabel("Value (Rs or %)"));

        pDedu.add(new JLabel("Income Tax (IT):"));
        chkIt = new JCheckBox("Percentage (%)");
        txtItVal = ModernTheme.createTextField(8);
        pDedu.add(chkIt);
        pDedu.add(txtItVal);
        pDedu.add(new JLabel("Value (Rs or %)"));

        pDedu.add(new JLabel("Group Insurance (GIS):"));
        chkGis = new JCheckBox("Percentage (%)");
        txtGisVal = ModernTheme.createTextField(8);
        pDedu.add(chkGis);
        pDedu.add(txtGisVal);
        pDedu.add(new JLabel("Value (Rs or %)"));

        pDedu.add(new JLabel("Provident Fund (PF):"));
        chkPf = new JCheckBox("Percentage (%)");
        txtPfVal = ModernTheme.createTextField(8);
        pDedu.add(chkPf);
        pDedu.add(txtPfVal);
        pDedu.add(new JLabel("Value (Rs or %)"));

        pDedu.add(new JLabel("Life Insurance (LIC):"));
        chkLic = new JCheckBox("Percentage (%)");
        txtLicVal = ModernTheme.createTextField(8);
        pDedu.add(chkLic);
        pDedu.add(txtLicVal);
        pDedu.add(new JLabel("Value (Rs or %)"));

        midPanel.add(pDedu);
        panel.add(midPanel, BorderLayout.CENTER);

        // Bottom Tier Actions
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        actionRow.setOpaque(false);

        btnAddPos = ModernTheme.createSecondaryButton("Add As New Tier", new ImageIcon("images/add.gif"));
        btnAddPos.addActionListener(e -> saveSalaryTier(true));

        btnUpdatePos = ModernTheme.createPrimaryButton("Update Selected Tier", new ImageIcon("images/save_all.png"));
        btnUpdatePos.addActionListener(e -> saveSalaryTier(false));

        btnDeletePos = ModernTheme.createDangerButton("Delete Tier", new ImageIcon("images/delete.png"));
        btnDeletePos.addActionListener(e -> deleteSelectedTier());

        actionRow.add(btnAddPos);
        actionRow.add(btnUpdatePos);
        actionRow.add(btnDeletePos);

        panel.add(actionRow, BorderLayout.SOUTH);
        return panel;
    }

    private void loadOrgSettings() {
        Map<String, String> map = OrgSettingsService.getSettings();
        txtOrgName.setText(map.getOrDefault("ORG_NAME", "Global Enterprise Corp"));
        txtOrgTagline.setText(map.getOrDefault("ORG_TAGLINE", "Advanced Payroll & Workforce Intelligence"));
        txtOrgAddr.setText(map.getOrDefault("ORG_ADDRESS", "Tech Park, Outer Ring Road, Bangalore - 560103"));
        txtOrgPhone.setText(map.getOrDefault("ORG_PHONE", "+91 (080) 4123-8899"));
        txtOrgEmail.setText(map.getOrDefault("ORG_EMAIL", "payroll@enterprise.com"));
        txtTaxId.setText(map.getOrDefault("TAX_ID", "GSTIN29AAACG0561K1ZV"));
        txtCurrency.setText(map.getOrDefault("CURRENCY_SYMBOL", "Rs"));
        txtPayCycle.setText(map.getOrDefault("PAYROLL_CYCLE", "Monthly (Last Working Day)"));
    }

    private void saveOrgSettings() {
        if (!AuthService.canManageSettings()) {
            JOptionPane.showMessageDialog(this, "Permission Denied: Only Super Admin can modify organization parameters.", "Access Restricted", JOptionPane.WARNING_MESSAGE);
            return;
        }

        OrgSettingsService.set("ORG_NAME", txtOrgName.getText().trim());
        OrgSettingsService.set("ORG_TAGLINE", txtOrgTagline.getText().trim());
        OrgSettingsService.set("ORG_ADDRESS", txtOrgAddr.getText().trim());
        OrgSettingsService.set("ORG_PHONE", txtOrgPhone.getText().trim());
        OrgSettingsService.set("ORG_EMAIL", txtOrgEmail.getText().trim());
        OrgSettingsService.set("TAX_ID", txtTaxId.getText().trim());
        OrgSettingsService.set("CURRENCY_SYMBOL", txtCurrency.getText().trim());
        OrgSettingsService.set("PAYROLL_CYCLE", txtPayCycle.getText().trim());

        AuditService.log(AuthService.getCurrentUser(), "UPDATE_ORG_SETTINGS", "ORGANIZATION_SETTINGS", "Updated organization corporate parameters.");
        JOptionPane.showMessageDialog(this, "Organization profile updated successfully!", "Saved", JOptionPane.INFORMATION_MESSAGE);
    }

    private void loadPositions() {
        cmbPositions.removeAllItems();
        try (Connection conn = clsConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Category_Name FROM Settings ORDER BY Category_Name")) {
            while (rs.next()) {
                String name = rs.getString("Category_Name");
                if (name != null) cmbPositions.addItem(name.trim());
            }
        } catch (Exception ignored) {}

        if (cmbPositions.getItemCount() > 0) {
            cmbPositions.setSelectedIndex(0);
            fillSalaryTierDetails((String) cmbPositions.getSelectedItem());
        }
    }

    private void fillSalaryTierDetails(String positionName) {
        if (positionName == null) return;
        try (Connection conn = clsConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM Settings WHERE Category_Name = ?")) {
            ps.setString(1, positionName.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    txtNewPosName.setText(rs.getString("Category_Name"));
                    txtBasicPay.setText(String.valueOf(rs.getInt("Basic_Pay")));

                    chkDa.setSelected("true".equalsIgnoreCase(rs.getString("DA")));
                    txtDaVal.setText(String.valueOf(rs.getInt("DA_Allow")));

                    chkHra.setSelected("true".equalsIgnoreCase(rs.getString("HRA")));
                    txtHraVal.setText(String.valueOf(rs.getInt("HRA_Allow")));

                    chkWa.setSelected("true".equalsIgnoreCase(rs.getString("WA")));
                    txtWaVal.setText(String.valueOf(rs.getInt("WA_Allow")));

                    chkGpf.setSelected("true".equalsIgnoreCase(rs.getString("GPF")));
                    txtGpfVal.setText(String.valueOf(rs.getInt("GPF_Dedu")));

                    chkIt.setSelected("true".equalsIgnoreCase(rs.getString("IT")));
                    txtItVal.setText(String.valueOf(rs.getInt("IT_Dedu")));

                    chkGis.setSelected("true".equalsIgnoreCase(rs.getString("GIS")));
                    txtGisVal.setText(String.valueOf(rs.getInt("GIS_Dedu")));

                    chkPf.setSelected("true".equalsIgnoreCase(rs.getString("PF")));
                    txtPfVal.setText(String.valueOf(rs.getInt("PF_Dedu")));

                    chkLic.setSelected("true".equalsIgnoreCase(rs.getString("LIC")));
                    txtLicVal.setText(String.valueOf(rs.getInt("LIC_Dedu")));
                }
            }
        } catch (Exception ignored) {}
    }

    private void saveSalaryTier(boolean isNew) {
        if (!AuthService.canManageSettings()) {
            JOptionPane.showMessageDialog(this, "Only Super Administrator can modify salary structures.", "Permission Denied", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String name = txtNewPosName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Position Name is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int basic = 0, da = 0, hra = 0, wa = 0, gpf = 0, it = 0, gis = 0, pf = 0, lic = 0;
        try {
            basic = Integer.parseInt(txtBasicPay.getText().trim());
            da = Integer.parseInt(txtDaVal.getText().trim());
            hra = Integer.parseInt(txtHraVal.getText().trim());
            wa = Integer.parseInt(txtWaVal.getText().trim());
            gpf = Integer.parseInt(txtGpfVal.getText().trim());
            it = Integer.parseInt(txtItVal.getText().trim());
            gis = Integer.parseInt(txtGisVal.getText().trim());
            pf = Integer.parseInt(txtPfVal.getText().trim());
            lic = Integer.parseInt(txtLicVal.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "All salary values must be valid integer numbers.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = clsConnection.getConnection()) {
            if (isNew) {
                String sql = "INSERT INTO Settings (Category_Name, Basic_Pay, DA_Allow, HRA_Allow, WA_Allow, GPF_Dedu, IT_Dedu, GIS_Dedu, LIC_Dedu, PF_Dedu, DA, HRA, WA, GPF, IT, GIS, PF, LIC, Category_Type) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1)";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, name);
                    ps.setInt(2, basic);
                    ps.setInt(3, da);
                    ps.setInt(4, hra);
                    ps.setInt(5, wa);
                    ps.setInt(6, gpf);
                    ps.setInt(7, it);
                    ps.setInt(8, gis);
                    ps.setInt(9, lic);
                    ps.setInt(10, pf);
                    ps.setString(11, String.valueOf(chkDa.isSelected()));
                    ps.setString(12, String.valueOf(chkHra.isSelected()));
                    ps.setString(13, String.valueOf(chkWa.isSelected()));
                    ps.setString(14, String.valueOf(chkGpf.isSelected()));
                    ps.setString(15, String.valueOf(chkIt.isSelected()));
                    ps.setString(16, String.valueOf(chkGis.isSelected()));
                    ps.setString(17, String.valueOf(chkPf.isSelected()));
                    ps.setString(18, String.valueOf(chkLic.isSelected()));
                    ps.executeUpdate();
                }
                AuditService.log(AuthService.getCurrentUser(), "ADD_SALARY_TIER", "Settings", "Added new salary tier: " + name);
                JOptionPane.showMessageDialog(this, "Salary Tier '" + name + "' added successfully!", "Tier Added", JOptionPane.INFORMATION_MESSAGE);
            } else {
                String sql = "UPDATE Settings SET Basic_Pay = ?, DA_Allow = ?, HRA_Allow = ?, WA_Allow = ?, GPF_Dedu = ?, IT_Dedu = ?, GIS_Dedu = ?, LIC_Dedu = ?, PF_Dedu = ?, DA = ?, HRA = ?, WA = ?, GPF = ?, IT = ?, GIS = ?, PF = ?, LIC = ? WHERE Category_Name = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, basic);
                    ps.setInt(2, da);
                    ps.setInt(3, hra);
                    ps.setInt(4, wa);
                    ps.setInt(5, gpf);
                    ps.setInt(6, it);
                    ps.setInt(7, gis);
                    ps.setInt(8, lic);
                    ps.setInt(9, pf);
                    ps.setString(10, String.valueOf(chkDa.isSelected()));
                    ps.setString(11, String.valueOf(chkHra.isSelected()));
                    ps.setString(12, String.valueOf(chkWa.isSelected()));
                    ps.setString(13, String.valueOf(chkGpf.isSelected()));
                    ps.setString(14, String.valueOf(chkIt.isSelected()));
                    ps.setString(15, String.valueOf(chkGis.isSelected()));
                    ps.setString(16, String.valueOf(chkPf.isSelected()));
                    ps.setString(17, String.valueOf(chkLic.isSelected()));
                    ps.setString(18, name);
                    ps.executeUpdate();
                }
                AuditService.log(AuthService.getCurrentUser(), "UPDATE_SALARY_TIER", "Settings", "Updated salary tier: " + name);
                JOptionPane.showMessageDialog(this, "Salary Tier '" + name + "' updated successfully!", "Tier Updated", JOptionPane.INFORMATION_MESSAGE);
            }
            loadPositions();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving tier: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedTier() {
        if (!AuthService.canManageSettings()) {
            JOptionPane.showMessageDialog(this, "Only Super Administrator can delete salary tiers.", "Permission Denied", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String name = (String) cmbPositions.getSelectedItem();
        if (name == null) return;

        int reply = JOptionPane.showConfirmDialog(this, "Delete salary tier '" + name + "'?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (reply == JOptionPane.YES_OPTION) {
            try (Connection conn = clsConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("DELETE FROM Settings WHERE Category_Name = ?")) {
                ps.setString(1, name);
                ps.executeUpdate();
                AuditService.log(AuthService.getCurrentUser(), "DELETE_SALARY_TIER", "Settings", "Deleted salary tier: " + name);
                loadPositions();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting tier: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
