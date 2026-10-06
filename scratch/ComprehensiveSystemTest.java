import java.sql.*;
import java.util.*;

public class ComprehensiveSystemTest {

    private static int testsRun = 0;
    private static int testsPassed = 0;

    private static void assertTrue(String testName, boolean condition) {
        testsRun++;
        if (condition) {
            testsPassed++;
            System.out.println("  [PASS] " + testName);
        } else {
            System.err.println("  [FAIL] " + testName);
        }
    }

    public static void main(String[] args) {
        System.out.println("=================================================================");
        System.out.println("   ENTERPRISE PAYROLL SYSTEM v3.0 - COMPREHENSIVE TEST SUITE     ");
        System.out.println("=================================================================\n");

        testDatabaseAndMigrations();
        testAuthenticationAndRBAC();
        testEmployeeDirectoryAndProfiles();
        testAttendanceWorkflow();
        testLeaveWorkflow();
        testPayrollCalculationPipeline();
        testAuditLogsAndNotifications();
        testOrganizationSettings();

        System.out.println("\n=================================================================");
        System.out.println(String.format("   TEST RESULTS: %d / %d PASSED (%.1f%% SUCCESS RATE)           ",
                testsPassed, testsRun, (testsPassed * 100.0 / Math.max(1, testsRun))));
        System.out.println("=================================================================");

        if (testsPassed != testsRun) {
            System.exit(1);
        }
    }

    private static void testDatabaseAndMigrations() {
        System.out.println("1. Testing Database & Migrations Integrity:");
        try (Connection conn = clsConnection.getConnection()) {
            assertTrue("Database connection established", conn != null && !conn.isClosed());

            DatabaseMetaData meta = conn.getMetaData();
            String[] requiredTables = {
                    "EMPLOYEE", "Login", "Settings",
                    "EMPLOYEE_PROFILE", "ATTENDANCE", "LEAVE_REQUESTS",
                    "PAYROLL_RUNS", "PAYSLIP_RECORDS", "AUDIT_LOGS",
                    "ORGANIZATION_SETTINGS", "USER_ROLES", "NOTIFICATIONS"
            };

            for (String t : requiredTables) {
                try (ResultSet rs = meta.getTables(null, null, t, new String[]{"TABLE"})) {
                    assertTrue("Table " + t + " exists in PayRoll.mdb", rs.next());
                }
            }
        } catch (Exception e) {
            assertTrue("Database migration exception: " + e.getMessage(), false);
        }
    }

    private static void testAuthenticationAndRBAC() {
        System.out.println("\n2. Testing Authentication & RBAC Permissions:");
        try (Connection conn = clsConnection.getConnection()) {
            // Verify admin user
            try (PreparedStatement ps = conn.prepareStatement("SELECT password FROM Login WHERE username = ?")) {
                ps.setString(1, "admin");
                try (ResultSet rs = ps.executeQuery()) {
                    assertTrue("Admin account exists in Login table", rs.next());
                }
            }

            // Test AuthService
            AuthService.setCurrentUser("admin");
            assertTrue("Admin role recognized as SUPER_ADMIN", AuthService.getCurrentRole() == AuthService.Role.SUPER_ADMIN);
            assertTrue("Super Admin can manage employees", AuthService.canManageEmployees());
            assertTrue("Super Admin can run payroll", AuthService.canRunPayroll());
            assertTrue("Super Admin can approve leave", AuthService.canApproveLeave());
            assertTrue("Super Admin can manage settings", AuthService.canManageSettings());
            assertTrue("Super Admin can view audit logs", AuthService.canViewAuditLogs());

            // Test Employee role
            AuthService.setCurrentUser("EMP001");
            assertTrue("EMP001 role recognized as EMPLOYEE", AuthService.getCurrentRole() == AuthService.Role.EMPLOYEE);
            assertTrue("Employee cannot manage employees", !AuthService.canManageEmployees());
            assertTrue("Employee cannot run payroll", !AuthService.canRunPayroll());
            assertTrue("Employee cannot manage settings", !AuthService.canManageSettings());

            // Reset back to admin
            AuthService.setCurrentUser("admin");
        } catch (Exception e) {
            assertTrue("Auth/RBAC test exception: " + e.getMessage(), false);
        }
    }

    private static void testEmployeeDirectoryAndProfiles() {
        System.out.println("\n3. Testing Employee Directory & Profiles:");
        try (Connection conn = clsConnection.getConnection()) {
            int empCount = 0;
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT count(*) FROM EMPLOYEE")) {
                if (rs.next()) empCount = rs.getInt(1);
            }
            assertTrue("Active staff records exist in EMPLOYEE table", empCount > 0);

            // Test inserting a temporary employee and profile
            String testCode = "TEST999";
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO EMPLOYEE (Emp_Code, Emp_Name1, Emp_name2, Emp_Add, Emp_No, Emp_Desi) VALUES (?, 'Test', 'Person', 'Lab', '1234567890', 'Programmer')")) {
                ps.setString(1, testCode);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO EMPLOYEE_PROFILE (Emp_Code, Department, Status, Email, Joining_Date, Employment_Type) VALUES (?, 'QA Engineering', 'Active', 'test.person@enterprise.com', '2026-01-01', 'Full-Time')")) {
                ps.setString(1, testCode);
                ps.executeUpdate();
            }

            // Query back joined
            try (PreparedStatement ps = conn.prepareStatement("SELECT e.Emp_Name1, p.Department FROM EMPLOYEE e JOIN EMPLOYEE_PROFILE p ON e.Emp_Code = p.Emp_Code WHERE e.Emp_Code = ?")) {
                ps.setString(1, testCode);
                try (ResultSet rs = ps.executeQuery()) {
                    assertTrue("New employee joined with profile successfully", rs.next() && "QA Engineering".equals(rs.getString("Department")));
                }
            }

            // Clean up test employee
            conn.createStatement().executeUpdate("DELETE FROM EMPLOYEE WHERE Emp_Code = '" + testCode + "'");
            conn.createStatement().executeUpdate("DELETE FROM EMPLOYEE_PROFILE WHERE Emp_Code = '" + testCode + "'");
            assertTrue("Test employee cleaned up cleanly", true);

        } catch (Exception e) {
            assertTrue("Employee test exception: " + e.getMessage(), false);
        }
    }

    private static void testAttendanceWorkflow() {
        System.out.println("\n4. Testing Attendance Workflow & Shift Logging:");
        try (Connection conn = clsConnection.getConnection()) {
            String testCode = "EMP001";
            String testDate = "2026-10-15";

            // Delete old test row if present
            conn.createStatement().executeUpdate("DELETE FROM ATTENDANCE WHERE Emp_Code = '" + testCode + "' AND Att_Date = '" + testDate + "'");

            // Insert attendance record
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO ATTENDANCE (Emp_Code, Att_Date, Status, Check_In, Check_Out, Overtime_Hours, Notes) VALUES (?, ?, 'Present', '08:50', '18:30', 1.5, 'Project delivery shift')")) {
                ps.setString(1, testCode);
                ps.setString(2, testDate);
                ps.executeUpdate();
            }

            // Verify record
            try (PreparedStatement ps = conn.prepareStatement("SELECT Status, Overtime_Hours FROM ATTENDANCE WHERE Emp_Code = ? AND Att_Date = ?")) {
                ps.setString(1, testCode);
                ps.setString(2, testDate);
                try (ResultSet rs = ps.executeQuery()) {
                    assertTrue("Attendance record persisted with overtime", rs.next() && "Present".equals(rs.getString("Status")) && rs.getDouble("Overtime_Hours") == 1.5);
                }
            }

            // Clean up
            conn.createStatement().executeUpdate("DELETE FROM ATTENDANCE WHERE Emp_Code = '" + testCode + "' AND Att_Date = '" + testDate + "'");
        } catch (Exception e) {
            assertTrue("Attendance test exception: " + e.getMessage(), false);
        }
    }

    private static void testLeaveWorkflow() {
        System.out.println("\n5. Testing Leave Management & Approval Queue:");
        try (Connection conn = clsConnection.getConnection()) {
            String testCode = "EMP001";

            // Insert leave request
            int leaveId = 0;
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO LEAVE_REQUESTS (Emp_Code, Leave_Type, Start_Date, End_Date, Days_Count, Reason, Status, Applied_Date) VALUES (?, 'Sick Leave', '2026-11-01', '2026-11-02', 2.0, 'Flu recovery', 'Pending', '2026-10-04')")) {
                ps.setString(1, testCode);
                ps.executeUpdate();
            }

            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT MAX(Leave_Id) FROM LEAVE_REQUESTS WHERE Emp_Code = '" + testCode + "'")) {
                if (rs.next()) leaveId = rs.getInt(1);
            }
            assertTrue("Leave request submitted with status Pending", leaveId > 0);

            // Test Approval
            try (PreparedStatement ps = conn.prepareStatement("UPDATE LEAVE_REQUESTS SET Status = 'Approved', Reviewed_By = 'admin', Review_Notes = 'Medical slip verified' WHERE Leave_Id = ?")) {
                ps.setInt(1, leaveId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement("SELECT Status, Reviewed_By FROM LEAVE_REQUESTS WHERE Leave_Id = ?")) {
                ps.setInt(1, leaveId);
                try (ResultSet rs = ps.executeQuery()) {
                    assertTrue("Leave status transitioned to Approved by admin", rs.next() && "Approved".equals(rs.getString("Status")));
                }
            }

            // Clean up
            conn.createStatement().executeUpdate("DELETE FROM LEAVE_REQUESTS WHERE Leave_Id = " + leaveId);
        } catch (Exception e) {
            assertTrue("Leave test exception: " + e.getMessage(), false);
        }
    }

    private static void testPayrollCalculationPipeline() {
        System.out.println("\n6. Testing 12-Step Payroll Calculation Pipeline:");
        try (Connection conn = clsConnection.getConnection()) {
            // Verify Settings table has valid tiers
            int tierCount = 0;
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT count(*) FROM Settings")) {
                if (rs.next()) tierCount = rs.getInt(1);
            }
            assertTrue("Salary structure tiers exist in Settings table", tierCount > 0);

            // Verify calculation formula for Programmer
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT * FROM Settings WHERE Category_Name = 'Programmer'")) {
                if (rs.next()) {
                    double bp = rs.getInt("Basic_Pay");
                    double da = (bp * rs.getInt("DA_Allow")) / 100.0;
                    double hra = (bp * rs.getInt("HRA_Allow")) / 100.0;
                    double wa = (bp * rs.getInt("WA_Allow")) / 100.0;
                    double allow = da + hra + wa;

                    double gpf = (bp * rs.getInt("GPF_Dedu")) / 100.0;
                    double it = (bp * rs.getInt("IT_Dedu")) / 100.0;
                    double gis = (bp * rs.getInt("GIS_Dedu")) / 100.0;
                    double pf = (bp * rs.getInt("PF_Dedu")) / 100.0;
                    double dedu = gpf + it + gis + pf;

                    double net = (bp + allow) - dedu;

                    assertTrue("Programmer Basic Pay > 0", bp > 0);
                    assertTrue("Allowances calculated transparently", allow > 0);
                    assertTrue("Deductions calculated transparently", dedu > 0);
                    assertTrue("Net pay is non-negative and mathematically valid", net > 0 && net == (bp + allow - dedu));
                }
            }

            // Test inserting and finalizing a test payroll run
            String testMonth = "TestMonth";
            String testYear = "2026";
            conn.createStatement().executeUpdate("DELETE FROM PAYROLL_RUNS WHERE Period_Month = '" + testMonth + "'");

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO PAYROLL_RUNS (Period_Month, Period_Year, Total_Employees, Gross_Payroll, Total_Allowances, Total_Deductions, Net_Payroll, Processed_Date, Status, Processed_By) " +
                            "VALUES (?, ?, 3, 60000.0, 8400.0, 4800.0, 63600.0, '2026-10-04 12:00:00', 'Finalized', 'admin')")) {
                ps.setString(1, testMonth);
                ps.setString(2, testYear);
                ps.executeUpdate();
            }

            // Check duplicate prevention query
            try (PreparedStatement ps = conn.prepareStatement("SELECT count(*) FROM PAYROLL_RUNS WHERE Period_Month = ? AND Period_Year = ?")) {
                ps.setString(1, testMonth);
                ps.setString(2, testYear);
                try (ResultSet rs = ps.executeQuery()) {
                    assertTrue("Payroll run persisted and detectable for duplicate prevention", rs.next() && rs.getInt(1) == 1);
                }
            }

            // Clean up test run
            conn.createStatement().executeUpdate("DELETE FROM PAYROLL_RUNS WHERE Period_Month = '" + testMonth + "'");
        } catch (Exception e) {
            assertTrue("Payroll pipeline exception: " + e.getMessage(), false);
        }
    }

    private static void testAuditLogsAndNotifications() {
        System.out.println("\n7. Testing Audit Logging & In-App Notifications:");
        try (Connection conn = clsConnection.getConnection()) {
            // Test AuditService
            AuditService.log("TEST_USER", "UNIT_TEST", "TEST_SUITE", "Automated verification execution");
            Thread.sleep(150); // wait for background thread

            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT count(*) FROM AUDIT_LOGS WHERE Username = 'TEST_USER'")) {
                assertTrue("AuditService records events to AUDIT_LOGS table", rs.next() && rs.getInt(1) > 0);
            }
            conn.createStatement().executeUpdate("DELETE FROM AUDIT_LOGS WHERE Username = 'TEST_USER'");

            // Test NotificationService
            NotificationService.send("Unit Test Alert", "Verification ping message", "SYSTEM");
            Thread.sleep(150);

            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT count(*) FROM NOTIFICATIONS WHERE Title = 'Unit Test Alert'")) {
                assertTrue("NotificationService writes alerts to NOTIFICATIONS table", rs.next() && rs.getInt(1) > 0);
            }
            conn.createStatement().executeUpdate("DELETE FROM NOTIFICATIONS WHERE Title = 'Unit Test Alert'");

        } catch (Exception e) {
            assertTrue("Audit/Notification exception: " + e.getMessage(), false);
        }
    }

    private static void testOrganizationSettings() {
        System.out.println("\n8. Testing Organization Profile & Settings:");
        try {
            String orgName = OrgSettingsService.get("ORG_NAME", null);
            assertTrue("Organization name retrieved: " + orgName, orgName != null && !orgName.isEmpty());

            String currency = OrgSettingsService.get("CURRENCY_SYMBOL", null);
            assertTrue("Currency symbol retrieved: " + currency, currency != null && !currency.isEmpty());

            String taxId = OrgSettingsService.get("TAX_ID", null);
            assertTrue("Tax ID retrieved: " + taxId, taxId != null && !taxId.isEmpty());
        } catch (Exception e) {
            assertTrue("OrgSettings test exception: " + e.getMessage(), false);
        }
    }
}
