import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class printwindow extends JFrame implements ActionListener, Printable {
    private JLabel LblHeader, LblSubHeader;
    private JLabel LblEmp_Name, LblEmp_Code, LblEmp_Desi, LblDate, LblSalary_Month, LblBasic_Pay;
    private JLabel LblDA, LblHRA, LblWA, LblTot_Allowance;
    private JLabel LblGPF, LblIT, LblGIS, LblPF, LblLIC, LblTot_Deduction;
    private JLabel LblNet_Salary;

    private JTextField TxtEmp_Name, TxtEmp_Code, TxtEmp_Desi, TxtDate, TxtSalary_Month, TxtBasic_Pay;
    private JTextField TxtDA, TxtHRA, TxtWA, TxtTot_Allowance;
    private JTextField TxtGPF, TxtIT, TxtGIS, TxtPF, TxtLIC, TxtTot_Deduction;
    private JTextField TxtNet_Salary;

    private JButton PrintBtn, SaveBtn, ExitBtn;
    private JPanel printableSlipPanel;

    private String rawName = "", rawMonth = "", rawYear = "", rawCode = "", rawDesi = "", rawNet = "";

    public printwindow() {
        super("Official Salary Slip - Print & Preview");
        setSize(650, 720);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setIconImage(new ImageIcon("images/Business.png").getImage());

        JPanel rootPanel = new JPanel(new BorderLayout(10, 10));
        rootPanel.setBorder(new EmptyBorder(12, 15, 12, 15));
        rootPanel.setBackground(Color.WHITE);

        // Printable Slip Container
        printableSlipPanel = new JPanel();
        printableSlipPanel.setLayout(new BoxLayout(printableSlipPanel, BoxLayout.Y_AXIS));
        printableSlipPanel.setBackground(Color.WHITE);
        printableSlipPanel.setBorder(new LineBorder(new Color(200, 210, 225), 2));

        // Company Header
        JPanel pHead = new JPanel(new GridLayout(3, 1, 0, 2));
        pHead.setBackground(new Color(245, 248, 255));
        pHead.setBorder(new EmptyBorder(12, 10, 12, 10));

        String orgName = OrgSettingsService.get("ORG_NAME", "ENTERPRISE PAYROLL SYSTEM");
        String orgAddr = OrgSettingsService.get("ORG_ADDRESS", "Corporate Tech Park, Outer Ring Road");
        LblHeader = new JLabel(orgName.toUpperCase(), JLabel.CENTER);
        LblHeader.setFont(new Font("Segoe UI", Font.BOLD, 17));
        LblHeader.setForeground(new Color(20, 50, 110));

        JLabel lblOrgAddr = new JLabel(orgAddr + " • TAX ID: " + OrgSettingsService.get("TAX_ID", "GSTIN29AAACG0561K1ZV"), JLabel.CENTER);
        lblOrgAddr.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblOrgAddr.setForeground(new Color(100, 110, 130));

        LblSubHeader = new JLabel("OFFICIAL MONTHLY SALARY PAYSLIP", JLabel.CENTER);
        LblSubHeader.setFont(new Font("Segoe UI", Font.BOLD, 13));
        LblSubHeader.setForeground(new Color(60, 70, 90));

        pHead.add(LblHeader);
        pHead.add(lblOrgAddr);
        pHead.add(LblSubHeader);
        printableSlipPanel.add(pHead);
        printableSlipPanel.add(Box.createVerticalStrut(10));

        // Employee Metadata Grid
        JPanel pMeta = new JPanel(new GridLayout(3, 4, 10, 8));
        pMeta.setBackground(Color.WHITE);
        pMeta.setBorder(new EmptyBorder(5, 15, 5, 15));

        pMeta.add(new JLabel("Employee Code:"));
        TxtEmp_Code = createReadOnlyField();
        pMeta.add(TxtEmp_Code);

        pMeta.add(new JLabel("Slip Date:"));
        TxtDate = createReadOnlyField();
        pMeta.add(TxtDate);

        pMeta.add(new JLabel("Employee Name:"));
        TxtEmp_Name = createReadOnlyField();
        pMeta.add(TxtEmp_Name);

        pMeta.add(new JLabel("Designation:"));
        TxtEmp_Desi = createReadOnlyField();
        pMeta.add(TxtEmp_Desi);

        pMeta.add(new JLabel("Pay Period:"));
        TxtSalary_Month = createReadOnlyField();
        pMeta.add(TxtSalary_Month);

        pMeta.add(new JLabel("Basic Salary (Rs):"));
        TxtBasic_Pay = createReadOnlyField();
        pMeta.add(TxtBasic_Pay);

        printableSlipPanel.add(pMeta);
        printableSlipPanel.add(Box.createVerticalStrut(10));

        // Allowances Section
        JPanel pAllowSection = new JPanel(new BorderLayout(5, 5));
        pAllowSection.setBackground(Color.WHITE);
        pAllowSection.setBorder(new EmptyBorder(5, 15, 5, 15));

        JLabel lblAllowTitle = new JLabel("EARNINGS & ALLOWANCES");
        lblAllowTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblAllowTitle.setForeground(new Color(20, 110, 50));
        pAllowSection.add(lblAllowTitle, BorderLayout.NORTH);

        JPanel pAllowGrid = new JPanel(new GridLayout(2, 4, 10, 6));
        pAllowGrid.setBackground(Color.WHITE);
        pAllowGrid.add(new JLabel("DA (Rs):"));
        TxtDA = createReadOnlyField();
        pAllowGrid.add(TxtDA);

        pAllowGrid.add(new JLabel("HRA (Rs):"));
        TxtHRA = createReadOnlyField();
        pAllowGrid.add(TxtHRA);

        pAllowGrid.add(new JLabel("WA (Rs):"));
        TxtWA = createReadOnlyField();
        pAllowGrid.add(TxtWA);

        pAllowGrid.add(new JLabel("Total Allowances (Rs):"));
        TxtTot_Allowance = createReadOnlyField();
        TxtTot_Allowance.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pAllowGrid.add(TxtTot_Allowance);

        pAllowSection.add(pAllowGrid, BorderLayout.CENTER);
        printableSlipPanel.add(pAllowSection);
        printableSlipPanel.add(Box.createVerticalStrut(10));

        // Deductions Section
        JPanel pDeduSection = new JPanel(new BorderLayout(5, 5));
        pDeduSection.setBackground(Color.WHITE);
        pDeduSection.setBorder(new EmptyBorder(5, 15, 5, 15));

        JLabel lblDeduTitle = new JLabel("DEDUCTIONS");
        lblDeduTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblDeduTitle.setForeground(new Color(180, 40, 30));
        pDeduSection.add(lblDeduTitle, BorderLayout.NORTH);

        JPanel pDeduGrid = new JPanel(new GridLayout(3, 4, 10, 6));
        pDeduGrid.setBackground(Color.WHITE);
        pDeduGrid.add(new JLabel("GPF (Rs):"));
        TxtGPF = createReadOnlyField();
        pDeduGrid.add(TxtGPF);

        pDeduGrid.add(new JLabel("Income Tax (Rs):"));
        TxtIT = createReadOnlyField();
        pDeduGrid.add(TxtIT);

        pDeduGrid.add(new JLabel("GIS (Rs):"));
        TxtGIS = createReadOnlyField();
        pDeduGrid.add(TxtGIS);

        pDeduGrid.add(new JLabel("PF (Rs):"));
        TxtPF = createReadOnlyField();
        pDeduGrid.add(TxtPF);

        pDeduGrid.add(new JLabel("LIC (Rs):"));
        TxtLIC = createReadOnlyField();
        pDeduGrid.add(TxtLIC);

        pDeduGrid.add(new JLabel("Total Deductions (Rs):"));
        TxtTot_Deduction = createReadOnlyField();
        TxtTot_Deduction.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pDeduGrid.add(TxtTot_Deduction);

        pDeduSection.add(pDeduGrid, BorderLayout.CENTER);
        printableSlipPanel.add(pDeduSection);
        printableSlipPanel.add(Box.createVerticalStrut(10));

        // Net Salary Banner
        JPanel pNet = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        pNet.setBackground(new Color(245, 252, 245));
        pNet.setBorder(new LineBorder(new Color(180, 220, 180), 1));

        JLabel lblNet = new JLabel("NET SALARY PAYABLE:");
        lblNet.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblNet.setForeground(new Color(20, 100, 40));
        pNet.add(lblNet);

        TxtNet_Salary = new JTextField(12);
        TxtNet_Salary.setEditable(false);
        TxtNet_Salary.setBackground(Color.WHITE);
        TxtNet_Salary.setFont(new Font("Segoe UI", Font.BOLD, 15));
        TxtNet_Salary.setForeground(new Color(20, 100, 40));
        TxtNet_Salary.setHorizontalAlignment(JTextField.RIGHT);
        pNet.add(TxtNet_Salary);

        printableSlipPanel.add(pNet);
        rootPanel.add(new JScrollPane(printableSlipPanel), BorderLayout.CENTER);

        // Action Toolbar
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        PrintBtn = new JButton("Print Slip");
        PrintBtn.setIcon(new ImageIcon("images/print.png"));
        PrintBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        PrintBtn.addActionListener(this);

        SaveBtn = new JButton("Save As Text");
        SaveBtn.setIcon(new ImageIcon("images/save_all.png"));
        SaveBtn.addActionListener(this);

        ExitBtn = new JButton("Close");
        ExitBtn.setIcon(new ImageIcon("images/exit.png"));
        ExitBtn.addActionListener(this);

        btnPanel.add(PrintBtn);
        btnPanel.add(SaveBtn);
        btnPanel.add(ExitBtn);
        rootPanel.add(btnPanel, BorderLayout.SOUTH);

        getContentPane().add(rootPanel);
    }

    private JTextField createReadOnlyField() {
        JTextField tf = new JTextField();
        tf.setEditable(false);
        tf.setBackground(new Color(250, 250, 250));
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return tf;
    }

    public void fill_data(String name1, String name2, String month, String year, String code,
                          String date, String desi, String basic_pay, String da, String hra, String wa,
                          String gpf, String it, String gis, String pf, String lic, String allow,
                          String dedu, String net_salary) {

        this.rawName = ((name1 != null ? name1 : "") + " " + (name2 != null ? name2 : "")).trim();
        this.rawMonth = month != null ? month : "";
        this.rawYear = year != null ? year : "";
        this.rawCode = code != null ? code : "";
        this.rawDesi = desi != null ? desi : "";
        this.rawNet = net_salary != null ? net_salary : "";

        TxtEmp_Name.setText(this.rawName);
        TxtDate.setText(date);
        TxtEmp_Code.setText(code);
        TxtEmp_Desi.setText(desi);
        TxtSalary_Month.setText(month + ", " + year);
        TxtBasic_Pay.setText(basic_pay);

        TxtDA.setText(da);
        TxtHRA.setText(hra);
        TxtWA.setText(wa);
        TxtTot_Allowance.setText(allow);

        TxtGPF.setText(gpf);
        TxtIT.setText(it);
        TxtGIS.setText(gis);
        TxtPF.setText(pf);
        TxtLIC.setText(lic);
        TxtTot_Deduction.setText(dedu);
        TxtNet_Salary.setText("Rs. " + net_salary);
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == PrintBtn) {
            doPrint();
        } else if (src == SaveBtn) {
            doSaveText();
        } else if (src == ExitBtn) {
            setVisible(false);
            dispose();
        }
    }

    private void doPrint() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(this);
        if (job.printDialog()) {
            try {
                job.print();
                JOptionPane.showMessageDialog(this, "Print job sent successfully.", "Print Job", JOptionPane.INFORMATION_MESSAGE);
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(this, "Printing failed: " + ex.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void doSaveText() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Save Payslip as Text File");
        fc.setSelectedFile(new File("PaySlip_" + rawCode + "_" + rawMonth + "_" + rawYear + ".txt"));
        int userChoice = fc.showSaveDialog(this);

        if (userChoice == JFileChooser.APPROVE_OPTION) {
            File outFile = fc.getSelectedFile();
            try (PrintWriter pw = new PrintWriter(new FileWriter(outFile))) {
                pw.println("==============================================================");
                pw.println("                 ADVANCED PAYROLL SYSTEM                      ");
                pw.println("                 MONTHLY SALARY PAYSLIP                       ");
                pw.println("==============================================================");
                pw.println("Date:             " + TxtDate.getText());
                pw.println("Pay Period:       " + TxtSalary_Month.getText());
                pw.println("Employee Code:    " + TxtEmp_Code.getText());
                pw.println("Employee Name:    " + TxtEmp_Name.getText());
                pw.println("Designation:      " + TxtEmp_Desi.getText());
                pw.println("Basic Salary:     Rs. " + TxtBasic_Pay.getText());
                pw.println("--------------------------------------------------------------");
                pw.println("EARNINGS & ALLOWANCES:");
                pw.println("  Dearness Allowance (DA):        Rs. " + TxtDA.getText());
                pw.println("  House Rent Allowance (HRA):     Rs. " + TxtHRA.getText());
                pw.println("  Washing Allowance (WA):         Rs. " + TxtWA.getText());
                pw.println("  TOTAL ALLOWANCES:               Rs. " + TxtTot_Allowance.getText());
                pw.println("--------------------------------------------------------------");
                pw.println("DEDUCTIONS:");
                pw.println("  General Provident Fund (GPF):   Rs. " + TxtGPF.getText());
                pw.println("  Income Tax (IT):                Rs. " + TxtIT.getText());
                pw.println("  Group Insurance Scheme (GIS):   Rs. " + TxtGIS.getText());
                pw.println("  Provident Fund (PF):            Rs. " + TxtPF.getText());
                pw.println("  Life Insurance (LIC):           Rs. " + TxtLIC.getText());
                pw.println("  TOTAL DEDUCTIONS:               Rs. " + TxtTot_Deduction.getText());
                pw.println("==============================================================");
                pw.println("NET SALARY PAYABLE:               " + TxtNet_Salary.getText());
                pw.println("==============================================================");
                pw.println("Generated on: " + new java.util.Date());

                JOptionPane.showMessageDialog(this, "Payslip saved successfully to:\n" + outFile.getAbsolutePath(), "Saved", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to save file: " + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public int print(Graphics g, PageFormat pf, int pi) throws PrinterException {
        // Fix infinite printing loop: ONLY page 0 exists
        if (pi > 0) {
            return Printable.NO_SUCH_PAGE;
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.translate(pf.getImageableX(), pf.getImageableY());

        // Scale down slightly to fit printable area if needed
        double scaleX = pf.getImageableWidth() / printableSlipPanel.getWidth();
        double scaleY = pf.getImageableHeight() / printableSlipPanel.getHeight();
        double scale = Math.min(1.0, Math.min(scaleX, scaleY));
        g2.scale(scale, scale);

        printableSlipPanel.printAll(g2);
        return Printable.PAGE_EXISTS;
    }
}
