import java.sql.*;

public class TestAlter {
    public static void main(String[] args) {
        try (Connection conn = clsConnection.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            System.out.println("Checking columns in EMPLOYEE...");
            ResultSet rs = meta.getColumns(null, null, "EMPLOYEE", "Department");
            boolean hasDept = rs.next();
            rs.close();

            if (!hasDept) {
                System.out.println("Adding Department column...");
                Statement stmt = conn.createStatement();
                stmt.executeUpdate("ALTER TABLE EMPLOYEE ADD COLUMN Department VARCHAR(50)");
                stmt.executeUpdate("ALTER TABLE EMPLOYEE ADD COLUMN Status VARCHAR(20)");
                stmt.executeUpdate("ALTER TABLE EMPLOYEE ADD COLUMN Email VARCHAR(100)");
                stmt.executeUpdate("ALTER TABLE EMPLOYEE ADD COLUMN Joining_Date VARCHAR(20)");
                stmt.executeUpdate("ALTER TABLE EMPLOYEE ADD COLUMN Employment_Type VARCHAR(30)");
                stmt.close();
                System.out.println("Columns added successfully!");

                // Initialize existing records with sensible defaults
                stmt = conn.createStatement();
                stmt.executeUpdate("UPDATE EMPLOYEE SET Department = 'Engineering', Status = 'Active', Employment_Type = 'Full-Time' WHERE Department IS NULL");
                stmt.close();
                System.out.println("Existing employees updated with defaults!");
            } else {
                System.out.println("Department column already exists!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
