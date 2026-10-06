import java.sql.*;

public class TestDDL {
    public static void main(String[] args) {
        try (Connection conn = clsConnection.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            System.out.println("Testing DDL execution...");

            // Check if ATTENDANCE table exists
            ResultSet rs = meta.getTables(null, null, "ATTENDANCE", new String[]{"TABLE"});
            boolean exists = rs.next();
            rs.close();

            if (!exists) {
                System.out.println("Creating ATTENDANCE table...");
                Statement stmt = conn.createStatement();
                stmt.executeUpdate("CREATE TABLE ATTENDANCE (" +
                        "Att_Id AUTOINCREMENT PRIMARY KEY, " +
                        "Emp_Code VARCHAR(10), " +
                        "Att_Date VARCHAR(20), " +
                        "Status VARCHAR(20), " +
                        "Check_In VARCHAR(10), " +
                        "Check_Out VARCHAR(10), " +
                        "Overtime_Hours DOUBLE, " +
                        "Notes VARCHAR(255)" +
                        ")");
                stmt.close();
                System.out.println("ATTENDANCE table created successfully!");
            } else {
                System.out.println("ATTENDANCE table already exists!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
