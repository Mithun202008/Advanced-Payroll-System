import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Helpwindow extends JInternalFrame implements ActionListener {
    private JFrame JFParentFrame;
    private JButton btnClose;

    public Helpwindow(JFrame getParentFrame) {
        super("Payroll System - User Guide & Help", true, true, true, true);
        this.JFParentFrame = getParentFrame;
        setSize(650, 520);
        setFrameIcon(new ImageIcon("images/help.png"));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(12, 12, 12, 12));
        mainPanel.setBackground(Color.WHITE);

        // Header Panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        headerPanel.setBackground(new Color(245, 248, 252));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 220, 240)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        JLabel iconLabel = new JLabel(new ImageIcon("images/help.png"));
        JLabel titleLabel = new JLabel("System Documentation & How-To Guide");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(20, 50, 100));
        headerPanel.add(iconLabel);
        headerPanel.add(titleLabel);

        // Guide text
        JTextPane helpContent = new JTextPane();
        helpContent.setContentType("text/html");
        helpContent.setEditable(false);
        helpContent.setCaretPosition(0);
        helpContent.setText("<html><body style='font-family: Segoe UI, sans-serif; font-size: 11pt; color: #222222; margin: 8px;'>"
                + "<h3 style='color: #0b5394;'>1. Getting Started & Login</h3>"
                + "<p>When launching the application, authenticate using the administrator credentials:</p>"
                + "<ul>"
                + "<li><b>Default Username:</b> <code style='background: #f0f0f0; padding: 2px 5px;'>Pavan</code></li>"
                + "<li><b>Default Password:</b> <code style='background: #f0f0f0; padding: 2px 5px;'>Pavan</code></li>"
                + "<li>You can change your administrative password anytime via <b>Tools &rarr; Change Password</b>.</li>"
                + "</ul>"

                + "<h3 style='color: #0b5394;'>2. Managing Employees</h3>"
                + "<ul>"
                + "<li><b>View All Employees:</b> Click <b>Employees Directory</b> on the toolbar or menu to view all active personnel in a live table. You can search in real time by name, code, or position, and export records to CSV.</li>"
                + "<li><b>Add Employee:</b> Click <b>Add - Employee</b>. Enter Code, Name, Designation, Address, and Contact Number. Click <b>Auto Code</b> to generate the next available employee identifier automatically.</li>"
                + "<li><b>Edit Employee:</b> Select an employee code to load their information, update necessary fields, and click <b>Update</b>.</li>"
                + "<li><b>Delete Employee:</b> Select an employee code to review their details and confirm deletion.</li>"
                + "</ul>"

                + "<h3 style='color: #0b5394;'>3. Salary & Position Settings</h3>"
                + "<ul>"
                + "<li>Go to <b>Tools &rarr; Settings</b> to define salary tiers and positions (e.g., CEO, Programmer, Team Leader).</li>"
                + "<li>Configure <b>Basic Salary</b> and allowances (<b>DA</b>: Dearness Allowance, <b>HRA</b>: House Rent Allowance, <b>WA</b>: Washing/Water Allowance).</li>"
                + "<li>Configure statutory deductions (<b>GPF</b>, <b>IT</b>: Income Tax, <b>GIS</b>: Insurance, <b>PF</b>: Provident Fund, <b>LIC</b>).</li>"
                + "<li>Check the box next to each allowance/deduction if it is calculated as a percentage of Basic Salary, or leave unchecked for a fixed Rupee amount.</li>"
                + "</ul>"

                + "<h3 style='color: #0b5394;'>4. Monthly Payslip Generation & Printing</h3>"
                + "<ul>"
                + "<li>Open <b>Reports &rarr; Employee PaySlip</b>.</li>"
                + "<li>Choose an employee from the dropdown list, select the month, and confirm the year.</li>"
                + "<li>Click <b>Generate</b> to compute gross earnings, itemized allowances, total deductions, and net salary.</li>"
                + "<li>Click <b>Print</b> to view the official Salary Slip, send it to a printer, or save it to text.</li>"
                + "</ul>"

                + "<h3 style='color: #0b5394;'>5. Integrated Tools</h3>"
                + "<ul>"
                + "<li>Quick access to the Windows <b>Calculator</b> and <b>Notepad</b> from the toolbar for rapid calculations and note-taking.</li>"
                + "</ul>"
                + "</body></html>");

        JScrollPane scrollPane = new JScrollPane(helpContent);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 210)));

        // Button Panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
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
