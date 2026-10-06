import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Authorwindow extends JInternalFrame implements ActionListener {
    private JFrame JFParentFrame;
    private JButton btnClose;

    public Authorwindow(JFrame getParentFrame) {
        super("About - Advanced Payroll System", true, true, true, true);
        this.JFParentFrame = getParentFrame;
        setSize(480, 420);
        setFrameIcon(new ImageIcon("images/xp.png"));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Color.WHITE);

        // Header Panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        headerPanel.setBackground(Color.WHITE);
        JLabel iconLabel = new JLabel(new ImageIcon("images/Business.png"));
        JLabel titleLabel = new JLabel("Advanced Payroll System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(25, 60, 110));
        headerPanel.add(iconLabel);
        headerPanel.add(titleLabel);

        // Content Area
        JTextPane infoText = new JTextPane();
        infoText.setContentType("text/html");
        infoText.setEditable(false);
        infoText.setBackground(Color.WHITE);
        infoText.setText("<html><body style='font-family: Segoe UI, sans-serif; font-size: 11pt; color: #333333; margin: 10px;'>"
                + "<div style='text-align: center; border-bottom: 1px solid #e0e0e0; padding-bottom: 8px;'>"
                + "<b>Version:</b> 2.0 (Enhanced Edition)<br/>"
                + "<b>Environment:</b> Java Swing GUI + UCanAccess JDBC<br/>"
                + "<b>Database:</b> Microsoft Access (*.mdb)"
                + "</div>"
                + "<p style='margin-top: 12px;'><b>System Capabilities:</b></p>"
                + "<ul>"
                + "<li>Full Employee Lifecycle Management (Add, Edit, View, Delete)</li>"
                + "<li>Configurable Position & Salary Structure (Basic, DA, HRA, WA)</li>"
                + "<li>Automatic Statutory Deductions (GPF, IT, GIS, PF, LIC)</li>"
                + "<li>Interactive Searchable Employee Directory with CSV Export</li>"
                + "<li>Monthly Payslip Generation with Print & Save Support</li>"
                + "<li>Administrative Security with Credential Management</li>"
                + "</ul>"
                + "<p style='color: #666666; font-size: 10pt; text-align: center;'>"
                + "Original GEC Payroll Concept &bull; Modernized & Enhanced</p>"
                + "</body></html>");

        JScrollPane scrollPane = new JScrollPane(infoText);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        // Button Panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(Color.WHITE);
        btnClose = new JButton("Close");
        btnClose.setIcon(new ImageIcon("images/exit.png"));
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnClose.setFocusPainted(false);
        btnClose.addActionListener(this);
        btnPanel.add(btnClose);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        getContentPane().add(mainPanel);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnClose) {
            setVisible(false);
            dispose();
        }
    }
}
