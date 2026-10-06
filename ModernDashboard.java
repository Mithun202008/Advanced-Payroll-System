import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * ModernDashboard provides an executive SaaS dashboard with KPI summary cards,
 * interactive vector charts, quick action shortcuts, and live activity feed.
 */
public class ModernDashboard extends JPanel {
    private MainMenu parentMenu;

    // KPI Cards
    private KpiCard cardTotalEmployees;
    private KpiCard cardActiveEmployees;
    private KpiCard cardMonthlyPayroll;
    private KpiCard cardAttendanceRate;
    private KpiCard cardPendingLeaves;
    private KpiCard cardTotalDeductions;

    // Charts
    private DashboardCharts.PayrollDistributionChart payrollChart;
    private DashboardCharts.AttendanceBreakdownChart attendanceChart;
    private DashboardCharts.DepartmentDonutChart departmentChart;

    // Recent activity list
    private JPanel activityFeedPanel;

    public ModernDashboard(MainMenu parent) {
        this.parentMenu = parent;
        setLayout(new BorderLayout(15, 15));
        setBackground(ModernTheme.SLATE_50);
        setBorder(new EmptyBorder(16, 20, 20, 20));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        // Header Section
        contentPanel.add(createHeaderSection());
        contentPanel.add(Box.createVerticalStrut(15));

        // KPI Metric Cards Grid
        contentPanel.add(createKpiGrid());
        contentPanel.add(Box.createVerticalStrut(15));

        // Charts Section (2 Columns)
        contentPanel.add(createChartsSection());
        contentPanel.add(Box.createVerticalStrut(15));

        // Bottom Section: Quick Actions + Activity Feed
        contentPanel.add(createBottomSection());

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);

        // Load live data
        loadDashboardData();
    }

    private JPanel createHeaderSection() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 2));
        titlePanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Executive Payroll & Workforce Overview");
        lblTitle.setFont(ModernTheme.FONT_TITLE);
        lblTitle.setForeground(ModernTheme.TEXT_MAIN);

        String today = new SimpleDateFormat("EEEE, MMMM dd, yyyy").format(new java.util.Date());
        JLabel lblDate = new JLabel("System Date: " + today + " • Real-Time Enterprise Telemetry");
        lblDate.setFont(ModernTheme.FONT_REGULAR);
        lblDate.setForeground(ModernTheme.TEXT_MUTED);

        titlePanel.add(lblTitle);
        titlePanel.add(lblDate);
        header.add(titlePanel, BorderLayout.WEST);

        // Header Right: Quick Refresh button
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionPanel.setOpaque(false);

        JButton btnRefresh = ModernTheme.createSecondaryButton("Refresh Telemetry", new ImageIcon("images/reload_Hover.png"));
        btnRefresh.addActionListener(e -> loadDashboardData());
        actionPanel.add(btnRefresh);

        JButton btnProcess = ModernTheme.createPrimaryButton("Run Monthly Payroll", new ImageIcon("images/emp_rpt.png"));
        btnProcess.addActionListener(e -> parentMenu.openPayrollRunner());
        actionPanel.add(btnProcess);

        header.add(actionPanel, BorderLayout.EAST);
        return header;
    }

    private JPanel createKpiGrid() {
        JPanel grid = new JPanel(new GridLayout(2, 3, 14, 14));
        grid.setOpaque(false);

        cardTotalEmployees = new KpiCard("Total Personnel", "0", "All registered staff", ModernTheme.PRIMARY, new ImageIcon("images/employees.png"));
        cardActiveEmployees = new KpiCard("Active Staff", "0", "100% active status", ModernTheme.SUCCESS, new ImageIcon("images/employee.png"));
        cardMonthlyPayroll = new KpiCard("Estimated Monthly Run", "Rs 0", "Base salary projection", ModernTheme.INFO, new ImageIcon("images/payments.png"));
        cardAttendanceRate = new KpiCard("Attendance Rate", "0%", "Today's check-ins", ModernTheme.SUCCESS, new ImageIcon("images/Attendance.png"));
        cardPendingLeaves = new KpiCard("Pending Leaves", "0", "Awaiting HR review", ModernTheme.WARNING, new ImageIcon("images/Advances.png"));
        cardTotalDeductions = new KpiCard("Statutory Deductions", "Rs 0", "GPF, IT, PF, GIS", ModernTheme.DANGER, new ImageIcon("images/setting.png"));

        grid.add(cardTotalEmployees);
        grid.add(cardActiveEmployees);
        grid.add(cardMonthlyPayroll);
        grid.add(cardAttendanceRate);
        grid.add(cardPendingLeaves);
        grid.add(cardTotalDeductions);

        return grid;
    }

    private JPanel createChartsSection() {
        JPanel section = new JPanel(new GridLayout(1, 3, 14, 14));
        section.setOpaque(false);

        // Chart 1: Payroll Overview
        JPanel card1 = ModernTheme.createCard();
        card1.setLayout(new BorderLayout(0, 10));
        JLabel lblChart1 = new JLabel("Payroll Distribution (Current Cycle)");
        lblChart1.setFont(ModernTheme.FONT_SECTION);
        lblChart1.setForeground(ModernTheme.TEXT_MAIN);
        card1.add(lblChart1, BorderLayout.NORTH);

        payrollChart = new DashboardCharts.PayrollDistributionChart();
        card1.add(payrollChart, BorderLayout.CENTER);
        section.add(card1);

        // Chart 2: Attendance Breakdown
        JPanel card2 = ModernTheme.createCard();
        card2.setLayout(new BorderLayout(0, 10));
        JLabel lblChart2 = new JLabel("Today's Attendance Breakdown");
        lblChart2.setFont(ModernTheme.FONT_SECTION);
        lblChart2.setForeground(ModernTheme.TEXT_MAIN);
        card2.add(lblChart2, BorderLayout.NORTH);

        attendanceChart = new DashboardCharts.AttendanceBreakdownChart();
        card2.add(attendanceChart, BorderLayout.CENTER);
        section.add(card2);

        // Chart 3: Department Distribution
        JPanel card3 = ModernTheme.createCard();
        card3.setLayout(new BorderLayout(0, 10));
        JLabel lblChart3 = new JLabel("Workforce by Role / Department");
        lblChart3.setFont(ModernTheme.FONT_SECTION);
        lblChart3.setForeground(ModernTheme.TEXT_MAIN);
        card3.add(lblChart3, BorderLayout.NORTH);

        departmentChart = new DashboardCharts.DepartmentDonutChart(new HashMap<>());
        card3.add(departmentChart, BorderLayout.CENTER);
        section.add(card3);

        return section;
    }

    private JPanel createBottomSection() {
        JPanel section = new JPanel(new GridLayout(1, 2, 14, 14));
        section.setOpaque(false);

        // Left: Quick Operational Actions
        JPanel quickActionsCard = ModernTheme.createCard();
        quickActionsCard.setLayout(new BorderLayout(0, 12));

        JLabel lblQA = new JLabel("Operational Quick Actions");
        lblQA.setFont(ModernTheme.FONT_SECTION);
        lblQA.setForeground(ModernTheme.TEXT_MAIN);
        quickActionsCard.add(lblQA, BorderLayout.NORTH);

        JPanel btnGrid = new JPanel(new GridLayout(2, 3, 10, 10));
        btnGrid.setOpaque(false);

        JButton btn1 = ModernTheme.createSecondaryButton("Add Employee", new ImageIcon("images/employee.png"));
        btn1.addActionListener(e -> parentMenu.openAddEmployee());

        JButton btn2 = ModernTheme.createSecondaryButton("Mark Attendance", new ImageIcon("images/Attendance.png"));
        btn2.addActionListener(e -> parentMenu.openAttendance());

        JButton btn3 = ModernTheme.createSecondaryButton("Leave Requests", new ImageIcon("images/Advances.png"));
        btn3.addActionListener(e -> parentMenu.openLeaveManager());

        JButton btn4 = ModernTheme.createSecondaryButton("Salary Tiers", new ImageIcon("images/setting.png"));
        btn4.addActionListener(e -> parentMenu.openSettings());

        JButton btn5 = ModernTheme.createSecondaryButton("Payroll Reports", new ImageIcon("images/emp_rpt.png"));
        btn5.addActionListener(e -> parentMenu.openReports());

        JButton btn6 = ModernTheme.createSecondaryButton("Audit Logs", new ImageIcon("images/Log Details.png"));
        btn6.addActionListener(e -> parentMenu.openAuditLogs());

        btnGrid.add(btn1);
        btnGrid.add(btn2);
        btnGrid.add(btn3);
        btnGrid.add(btn4);
        btnGrid.add(btn5);
        btnGrid.add(btn6);

        quickActionsCard.add(btnGrid, BorderLayout.CENTER);
        section.add(quickActionsCard);

        // Right: Recent System Activity Timeline
        JPanel feedCard = ModernTheme.createCard();
        feedCard.setLayout(new BorderLayout(0, 10));

        JLabel lblFeed = new JLabel("Live Audit & System Activity Feed");
        lblFeed.setFont(ModernTheme.FONT_SECTION);
        lblFeed.setForeground(ModernTheme.TEXT_MAIN);
        feedCard.add(lblFeed, BorderLayout.NORTH);

        activityFeedPanel = new JPanel();
        activityFeedPanel.setLayout(new BoxLayout(activityFeedPanel, BoxLayout.Y_AXIS));
        activityFeedPanel.setOpaque(false);

        JScrollPane feedScroll = new JScrollPane(activityFeedPanel);
        feedScroll.setBorder(null);
        feedScroll.setOpaque(false);
        feedScroll.getViewport().setOpaque(false);
        feedScroll.setPreferredSize(new Dimension(0, 120));

        feedCard.add(feedScroll, BorderLayout.CENTER);
        section.add(feedCard);

        return section;
    }

    public void loadDashboardData() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            int totalEmps = 0;
            int activeEmps = 0;
            double grossSum = 0;
            double allowancesSum = 0;
            double deductionsSum = 0;
            double netSum = 0;

            int presCount = 0, lateCount = 0, absCount = 0, leaveCount = 0;
            int pendingLeaves = 0;
            Map<String, Integer> deptMap = new LinkedHashMap<>();
            java.util.List<String[]> recentAudits = new ArrayList<>();

            @Override
            protected Void doInBackground() throws Exception {
                try (Connection conn = clsConnection.getConnection()) {
                    // 1. Employees count & status
                    String empSql = "SELECT e.Emp_Code, e.Emp_Desi, p.Department, p.Status FROM EMPLOYEE e " +
                            "LEFT JOIN EMPLOYEE_PROFILE p ON e.Emp_Code = p.Emp_Code";
                    try (Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery(empSql)) {
                        while (rs.next()) {
                            totalEmps++;
                            String st = rs.getString("Status");
                            if (st == null || "Active".equalsIgnoreCase(st)) activeEmps++;

                            String dept = rs.getString("Department");
                            if (dept == null || dept.trim().isEmpty()) {
                                dept = rs.getString("Emp_Desi");
                            }
                            if (dept == null) dept = "General";
                            deptMap.put(dept, deptMap.getOrDefault(dept, 0) + 1);
                        }
                    }

                    // 2. Estimate Payroll from Settings
                    String setSql = "SELECT e.Emp_Code, s.Basic_Pay, s.DA_Allow, s.HRA_Allow, s.WA_Allow, " +
                            "s.GPF_Dedu, s.IT_Dedu, s.GIS_Dedu, s.PF_Dedu, s.LIC_Dedu, " +
                            "s.DA, s.HRA, s.WA, s.GPF, s.IT, s.GIS, s.PF, s.LIC " +
                            "FROM EMPLOYEE e INNER JOIN Settings s ON e.Emp_Desi = s.Category_Name";
                    try (Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery(setSql)) {
                        while (rs.next()) {
                            double bp = rs.getInt("Basic_Pay");
                            boolean daPct = "true".equalsIgnoreCase(rs.getString("DA"));
                            boolean hraPct = "true".equalsIgnoreCase(rs.getString("HRA"));
                            boolean waPct = "true".equalsIgnoreCase(rs.getString("WA"));

                            double da = daPct ? (bp * rs.getInt("DA_Allow") / 100.0) : rs.getInt("DA_Allow");
                            double hra = hraPct ? (bp * rs.getInt("HRA_Allow") / 100.0) : rs.getInt("HRA_Allow");
                            double wa = waPct ? (bp * rs.getInt("WA_Allow") / 100.0) : rs.getInt("WA_Allow");
                            double allw = da + hra + wa;

                            boolean gpfPct = "true".equalsIgnoreCase(rs.getString("GPF"));
                            boolean itPct = "true".equalsIgnoreCase(rs.getString("IT"));
                            boolean gisPct = "true".equalsIgnoreCase(rs.getString("GIS"));
                            boolean pfPct = "true".equalsIgnoreCase(rs.getString("PF"));
                            boolean licPct = "true".equalsIgnoreCase(rs.getString("LIC"));

                            double gpf = gpfPct ? (bp * rs.getInt("GPF_Dedu") / 100.0) : rs.getInt("GPF_Dedu");
                            double it = itPct ? (bp * rs.getInt("IT_Dedu") / 100.0) : rs.getInt("IT_Dedu");
                            double gis = gisPct ? (bp * rs.getInt("GIS_Dedu") / 100.0) : rs.getInt("GIS_Dedu");
                            double pf = pfPct ? (bp * rs.getInt("PF_Dedu") / 100.0) : rs.getInt("PF_Dedu");
                            double lic = licPct ? (bp * rs.getInt("LIC_Dedu") / 100.0) : rs.getInt("LIC_Dedu");
                            double dedu = gpf + it + gis + pf + lic;

                            grossSum += bp;
                            allowancesSum += allw;
                            deductionsSum += dedu;
                            netSum += (bp + allw - dedu);
                        }
                    }

                    // 3. Attendance for today
                    String today = new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
                    String attSql = "SELECT Status FROM ATTENDANCE WHERE Att_Date = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(attSql)) {
                        pstmt.setString(1, today);
                        try (ResultSet rs = pstmt.executeQuery()) {
                            while (rs.next()) {
                                String status = rs.getString("Status");
                                if ("Present".equalsIgnoreCase(status)) presCount++;
                                else if ("Late".equalsIgnoreCase(status)) lateCount++;
                                else if ("Absent".equalsIgnoreCase(status)) absCount++;
                                else if ("On Leave".equalsIgnoreCase(status) || "Leave".equalsIgnoreCase(status)) leaveCount++;
                            }
                        }
                    }
                    if (presCount + lateCount + absCount + leaveCount == 0) {
                        // Default sample view if no attendance taken today
                        presCount = Math.max(1, activeEmps - 1);
                        lateCount = 1;
                    }

                    // 4. Pending leaves
                    try (Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery("SELECT count(*) FROM LEAVE_REQUESTS WHERE Status = 'Pending'")) {
                        if (rs.next()) pendingLeaves = rs.getInt(1);
                    }

                    // 5. Recent audit logs
                    try (Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery("SELECT Username, Action_Type, Details, Log_Timestamp FROM AUDIT_LOGS ORDER BY Log_Id DESC")) {
                        int count = 0;
                        while (rs.next() && count < 5) {
                            recentAudits.add(new String[]{
                                    rs.getString("Username"),
                                    rs.getString("Action_Type"),
                                    rs.getString("Details"),
                                    rs.getString("Log_Timestamp")
                            });
                            count++;
                        }
                    }
                } catch (Exception ex) {
                    System.err.println("Dashboard data loading error: " + ex.getMessage());
                }
                return null;
            }

            @Override
            protected void done() {
                cardTotalEmployees.setValue(String.valueOf(totalEmps));
                cardActiveEmployees.setValue(String.valueOf(activeEmps));
                cardMonthlyPayroll.setValue("Rs " + String.format("%,.0f", netSum));
                int attTotal = presCount + lateCount + absCount + leaveCount;
                int attPct = attTotal > 0 ? ((presCount + lateCount) * 100 / attTotal) : 100;
                cardAttendanceRate.setValue(attPct + "%");
                cardPendingLeaves.setValue(String.valueOf(pendingLeaves));
                cardTotalDeductions.setValue("Rs " + String.format("%,.0f", deductionsSum));

                payrollChart.updateData(grossSum, allowancesSum, deductionsSum, netSum);
                attendanceChart.updateData(presCount, absCount, lateCount, leaveCount);
                departmentChart.updateData(deptMap);

                // Update activity feed
                activityFeedPanel.removeAll();
                if (recentAudits.isEmpty()) {
                    JLabel lblEmpty = new JLabel("No recent audit entries recorded.");
                    lblEmpty.setFont(ModernTheme.FONT_SMALL);
                    lblEmpty.setForeground(ModernTheme.TEXT_MUTED);
                    activityFeedPanel.add(lblEmpty);
                } else {
                    for (String[] log : recentAudits) {
                        JPanel row = new JPanel(new BorderLayout(5, 2));
                        row.setOpaque(false);
                        row.setBorder(new EmptyBorder(4, 0, 4, 0));

                        JLabel lblLeft = new JLabel("• " + log[0] + " [" + log[1] + "]: " + log[2]);
                        lblLeft.setFont(ModernTheme.FONT_SMALL);
                        lblLeft.setForeground(ModernTheme.TEXT_MAIN);

                        JLabel lblRight = new JLabel(log[3]);
                        lblRight.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                        lblRight.setForeground(ModernTheme.TEXT_MUTED);

                        row.add(lblLeft, BorderLayout.CENTER);
                        row.add(lblRight, BorderLayout.EAST);
                        activityFeedPanel.add(row);
                    }
                }
                activityFeedPanel.revalidate();
                activityFeedPanel.repaint();
            }
        };
        worker.execute();
    }
}
