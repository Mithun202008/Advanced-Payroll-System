import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DatabaseMigration {

    public static void runMigrations() {
        try (Connection conn = clsConnection.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();

            // 1. EMPLOYEE_PROFILE
            if (!tableExists(meta, "EMPLOYEE_PROFILE")) {
                System.out.println("Creating EMPLOYEE_PROFILE table...");
                try (Statement st = conn.createStatement()) {
                    st.executeUpdate("CREATE TABLE EMPLOYEE_PROFILE (" +
                            "Emp_Code VARCHAR(10) PRIMARY KEY, " +
                            "Department VARCHAR(50), " +
                            "Status VARCHAR(20), " +
                            "Email VARCHAR(100), " +
                            "Joining_Date VARCHAR(20), " +
                            "Employment_Type VARCHAR(30), " +
                            "Bank_Account VARCHAR(50), " +
                            "Bank_Name VARCHAR(50), " +
                            "PAN_Tax_No VARCHAR(50)" +
                            ")");
                }
            }

            // Sync profiles for existing employees in EMPLOYEE
            syncEmployeeProfiles(conn);

            // 2. ATTENDANCE
            if (!tableExists(meta, "ATTENDANCE")) {
                System.out.println("Creating ATTENDANCE table...");
                try (Statement st = conn.createStatement()) {
                    st.executeUpdate("CREATE TABLE ATTENDANCE (" +
                            "Att_Id AUTOINCREMENT PRIMARY KEY, " +
                            "Emp_Code VARCHAR(10), " +
                            "Att_Date VARCHAR(20), " +
                            "Status VARCHAR(20), " +
                            "Check_In VARCHAR(10), " +
                            "Check_Out VARCHAR(10), " +
                            "Overtime_Hours DOUBLE, " +
                            "Notes VARCHAR(255)" +
                            ")");
                }
            }

            // 3. LEAVE_REQUESTS
            if (!tableExists(meta, "LEAVE_REQUESTS")) {
                System.out.println("Creating LEAVE_REQUESTS table...");
                try (Statement st = conn.createStatement()) {
                    st.executeUpdate("CREATE TABLE LEAVE_REQUESTS (" +
                            "Leave_Id AUTOINCREMENT PRIMARY KEY, " +
                            "Emp_Code VARCHAR(10), " +
                            "Leave_Type VARCHAR(50), " +
                            "Start_Date VARCHAR(20), " +
                            "End_Date VARCHAR(20), " +
                            "Days_Count DOUBLE, " +
                            "Reason VARCHAR(255), " +
                            "Status VARCHAR(20), " +
                            "Applied_Date VARCHAR(30), " +
                            "Reviewed_By VARCHAR(50), " +
                            "Review_Notes VARCHAR(255)" +
                            ")");
                }
            }

            // 4. PAYROLL_RUNS
            if (!tableExists(meta, "PAYROLL_RUNS")) {
                System.out.println("Creating PAYROLL_RUNS table...");
                try (Statement st = conn.createStatement()) {
                    st.executeUpdate("CREATE TABLE PAYROLL_RUNS (" +
                            "Run_Id AUTOINCREMENT PRIMARY KEY, " +
                            "Period_Month VARCHAR(20), " +
                            "Period_Year VARCHAR(10), " +
                            "Total_Employees INTEGER, " +
                            "Gross_Payroll DOUBLE, " +
                            "Total_Allowances DOUBLE, " +
                            "Total_Deductions DOUBLE, " +
                            "Net_Payroll DOUBLE, " +
                            "Processed_Date VARCHAR(30), " +
                            "Status VARCHAR(20), " +
                            "Processed_By VARCHAR(50)" +
                            ")");
                }
            }

            // 5. PAYSLIP_RECORDS
            if (!tableExists(meta, "PAYSLIP_RECORDS")) {
                System.out.println("Creating PAYSLIP_RECORDS table...");
                try (Statement st = conn.createStatement()) {
                    st.executeUpdate("CREATE TABLE PAYSLIP_RECORDS (" +
                            "Slip_Id AUTOINCREMENT PRIMARY KEY, " +
                            "Run_Id INTEGER, " +
                            "Emp_Code VARCHAR(10), " +
                            "Period_Month VARCHAR(20), " +
                            "Period_Year VARCHAR(10), " +
                            "Basic_Pay DOUBLE, " +
                            "DA_Amount DOUBLE, " +
                            "HRA_Amount DOUBLE, " +
                            "WA_Amount DOUBLE, " +
                            "Allowances_Total DOUBLE, " +
                            "GPF_Amount DOUBLE, " +
                            "IT_Amount DOUBLE, " +
                            "GIS_Amount DOUBLE, " +
                            "PF_Amount DOUBLE, " +
                            "LIC_Amount DOUBLE, " +
                            "Deductions_Total DOUBLE, " +
                            "Net_Salary DOUBLE, " +
                            "Generated_Date VARCHAR(30), " +
                            "Payment_Status VARCHAR(20)" +
                            ")");
                }
            }

            // 6. AUDIT_LOGS
            if (!tableExists(meta, "AUDIT_LOGS")) {
                System.out.println("Creating AUDIT_LOGS table...");
                try (Statement st = conn.createStatement()) {
                    st.executeUpdate("CREATE TABLE AUDIT_LOGS (" +
                            "Log_Id AUTOINCREMENT PRIMARY KEY, " +
                            "Username VARCHAR(50), " +
                            "Action_Type VARCHAR(50), " +
                            "Target_Entity VARCHAR(50), " +
                            "Details VARCHAR(255), " +
                            "Log_Timestamp VARCHAR(30)" +
                            ")");
                }
            }

            // 7. ORGANIZATION_SETTINGS
            if (!tableExists(meta, "ORGANIZATION_SETTINGS")) {
                System.out.println("Creating ORGANIZATION_SETTINGS table...");
                try (Statement st = conn.createStatement()) {
                    st.executeUpdate("CREATE TABLE ORGANIZATION_SETTINGS (" +
                            "Org_Key VARCHAR(50) PRIMARY KEY, " +
                            "Org_Value VARCHAR(255)" +
                            ")");
                }
                seedOrgSettings(conn);
            }

            // 8. USER_ROLES
            if (!tableExists(meta, "USER_ROLES")) {
                System.out.println("Creating USER_ROLES table...");
                try (Statement st = conn.createStatement()) {
                    st.executeUpdate("CREATE TABLE USER_ROLES (" +
                            "Username VARCHAR(50) PRIMARY KEY, " +
                            "User_Role VARCHAR(30), " +
                            "Full_Name VARCHAR(100), " +
                            "Email VARCHAR(100), " +
                            "Status VARCHAR(20)" +
                            ")");
                }
                seedUserRoles(conn);
            }

            // 9. NOTIFICATIONS
            if (!tableExists(meta, "NOTIFICATIONS")) {
                System.out.println("Creating NOTIFICATIONS table...");
                try (Statement st = conn.createStatement()) {
                    st.executeUpdate("CREATE TABLE NOTIFICATIONS (" +
                            "Notif_Id AUTOINCREMENT PRIMARY KEY, " +
                            "Title VARCHAR(100), " +
                            "Message VARCHAR(255), " +
                            "Category VARCHAR(30), " +
                            "Created_At VARCHAR(30), " +
                            "Is_Read VARCHAR(5)" +
                            ")");
                }
                seedNotifications(conn);
            }

            // Seed sample attendance and leaves if empty
            seedInitialAttendanceAndLeaves(conn);

            System.out.println("All migrations and seeds verified successfully!");
        } catch (Exception e) {
            System.err.println("Migration error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static boolean tableExists(DatabaseMetaData meta, String tableName) throws SQLException {
        try (ResultSet rs = meta.getTables(null, null, tableName, new String[]{"TABLE"})) {
            return rs.next();
        }
    }

    private static void syncEmployeeProfiles(Connection conn) {
        try {
            // Find all EMPLOYEES and ensure they have a row in EMPLOYEE_PROFILE
            String selSql = "SELECT e.Emp_Code, e.Emp_Desi, e.Emp_Name1, e.Emp_name2 FROM EMPLOYEE e " +
                    "WHERE NOT EXISTS (SELECT 1 FROM EMPLOYEE_PROFILE p WHERE p.Emp_Code = e.Emp_Code)";
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(selSql)) {

                while (rs.next()) {
                    String code = rs.getString("Emp_Code");
                    String desi = rs.getString("Emp_Desi");
                    String n1 = rs.getString("Emp_Name1");
                    String n2 = rs.getString("Emp_name2");

                    String dept = "Engineering";
                    if (desi != null && desi.toLowerCase().contains("ceo")) dept = "Executive Management";
                    else if (desi != null && desi.toLowerCase().contains("leader")) dept = "Product & Engineering";

                    String email = (n1 != null ? n1.toLowerCase() : "emp") + "." + (n2 != null ? n2.toLowerCase() : "user") + "@enterprise.com";

                    try (PreparedStatement pstmt = conn.prepareStatement(
                            "INSERT INTO EMPLOYEE_PROFILE (Emp_Code, Department, Status, Email, Joining_Date, Employment_Type, Bank_Account, Bank_Name, PAN_Tax_No) " +
                                    "VALUES (?, ?, 'Active', ?, '2023-01-15', 'Full-Time', '910022334455', 'HDFC Bank', 'ABCDE1234F')")) {
                        pstmt.setString(1, code);
                        pstmt.setString(2, dept);
                        pstmt.setString(3, email);
                        pstmt.executeUpdate();
                    }
                }
            }
        } catch (Exception ex) {
            System.err.println("Error syncing employee profiles: " + ex.getMessage());
        }
    }

    private static void seedOrgSettings(Connection conn) {
        String[][] defaults = {
                {"ORG_NAME", "Global Enterprise Corp"},
                {"ORG_TAGLINE", "Advanced Payroll & Workforce Intelligence"},
                {"ORG_ADDRESS", "Tech Park, Outer Ring Road, Bangalore - 560103"},
                {"ORG_PHONE", "+91 (080) 4123-8899"},
                {"ORG_EMAIL", "payroll@enterprise.com"},
                {"TAX_ID", "GSTIN29AAACG0561K1ZV"},
                {"CURRENCY_SYMBOL", "Rs"},
                {"PAYROLL_CYCLE", "Monthly (Last Working Day)"},
                {"STANDARD_WORK_HOURS", "8.0"}
        };
        for (String[] pair : defaults) {
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO ORGANIZATION_SETTINGS (Org_Key, Org_Value) VALUES (?, ?)")) {
                ps.setString(1, pair[0]);
                ps.setString(2, pair[1]);
                ps.executeUpdate();
            } catch (Exception ignored) {}
        }
    }

    private static void seedUserRoles(Connection conn) {
        String[][] users = {
                {"admin", "SUPER_ADMIN", "Administrator", "admin@enterprise.com", "Active"},
                {"Pavan", "HR_ADMIN", "Pavan Kumar", "pavan@enterprise.com", "Active"},
                {"EMP001", "EMPLOYEE", "Mithun A", "mithun.a@enterprise.com", "Active"},
                {"EMP002", "EMPLOYEE", "Sham Guru", "sham.guru@enterprise.com", "Active"},
                {"EMP003", "MANAGER", "Venkat Raman", "venkat.raman@enterprise.com", "Active"}
        };
        for (String[] u : users) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO USER_ROLES (Username, User_Role, Full_Name, Email, Status) VALUES (?, ?, ?, ?, ?)")) {
                ps.setString(1, u[0]);
                ps.setString(2, u[1]);
                ps.setString(3, u[2]);
                ps.setString(4, u[3]);
                ps.setString(5, u[4]);
                ps.executeUpdate();
            } catch (Exception ignored) {}
        }
    }

    private static void seedNotifications(Connection conn) {
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
        String[][] notifs = {
                {"Welcome to Enterprise Payroll v3.0", "New modern dashboard, payroll runs, attendance tracker, and leave workflow initialized.", "SYSTEM"},
                {"Upcoming Monthly Payroll Cycle", "October 2026 payroll run is pending review and processing.", "PAYROLL"},
                {"Attendance Tracking Live", "Daily attendance status marking is active for all departments.", "ATTENDANCE"}
        };
        for (String[] n : notifs) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO NOTIFICATIONS (Title, Message, Category, Created_At, Is_Read) VALUES (?, ?, ?, ?, 'false')")) {
                ps.setString(1, n[0]);
                ps.setString(2, n[1]);
                ps.setString(3, n[2]);
                ps.setString(4, now);
                ps.executeUpdate();
            } catch (Exception ignored) {}
        }
    }

    private static void seedInitialAttendanceAndLeaves(Connection conn) {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT count(*) FROM ATTENDANCE")) {
            if (rs.next() && rs.getInt(1) == 0) {
                String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                st.executeUpdate("INSERT INTO ATTENDANCE (Emp_Code, Att_Date, Status, Check_In, Check_Out, Overtime_Hours, Notes) " +
                        "VALUES ('EMP001', '" + today + "', 'Present', '09:05', '18:00', 0.0, 'Regular Shift')");
                st.executeUpdate("INSERT INTO ATTENDANCE (Emp_Code, Att_Date, Status, Check_In, Check_Out, Overtime_Hours, Notes) " +
                        "VALUES ('EMP002', '" + today + "', 'Present', '08:55', '18:30', 0.5, 'Overtime logged')");
                st.executeUpdate("INSERT INTO ATTENDANCE (Emp_Code, Att_Date, Status, Check_In, Check_Out, Overtime_Hours, Notes) " +
                        "VALUES ('EMP003', '" + today + "', 'Late', '09:35', '18:15', 0.0, 'Traffic delay')");
            }
        } catch (Exception ignored) {}

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT count(*) FROM LEAVE_REQUESTS")) {
            if (rs.next() && rs.getInt(1) == 0) {
                st.executeUpdate("INSERT INTO LEAVE_REQUESTS (Emp_Code, Leave_Type, Start_Date, End_Date, Days_Count, Reason, Status, Applied_Date, Reviewed_By, Review_Notes) " +
                        "VALUES ('EMP002', 'Casual Leave', '2026-10-10', '2026-10-11', 2.0, 'Family function', 'Approved', '2026-10-02', 'admin', 'Approved by HR')");
                st.executeUpdate("INSERT INTO LEAVE_REQUESTS (Emp_Code, Leave_Type, Start_Date, End_Date, Days_Count, Reason, Status, Applied_Date, Reviewed_By, Review_Notes) " +
                        "VALUES ('EMP003', 'Sick Leave', '2026-10-05', '2026-10-05', 1.0, 'Medical appointment', 'Pending', '2026-10-03', null, null)");
            }
        } catch (Exception ignored) {}

        // Log initial audit entry
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT count(*) FROM AUDIT_LOGS")) {
            if (rs.next() && rs.getInt(1) == 0) {
                String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                st.executeUpdate("INSERT INTO AUDIT_LOGS (Username, Action_Type, Target_Entity, Details, Log_Timestamp) " +
                        "VALUES ('SYSTEM', 'DATABASE_INIT', 'ALL_TABLES', 'Enterprise Payroll v3.0 schemas initialized', '" + now + "')");
            }
        } catch (Exception ignored) {}
    }

    public static void main(String[] args) {
        System.out.println("Starting Database Migration...");
        runMigrations();
        System.out.println("Done.");
    }
}
