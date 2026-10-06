import java.sql.*;
import java.util.Date;

public class SmokeTest {
    public static void main(String[] args) {
        try {
            System.out.println("1. Testing DB connection...");
            Connection conn = clsConnection.getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("   DB Connection OK!");
            }

            System.out.println("2. Testing Employee count...");
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT count(*) FROM EMPLOYEE");
            if (rs.next()) {
                System.out.println("   Employees count: " + rs.getInt(1));
            }
            rs.close();
            st.close();

            System.out.println("3. Testing Settings categories...");
            st = conn.createStatement();
            rs = st.executeQuery("SELECT Category_Name, Basic_Pay FROM Settings");
            while (rs.next()) {
                System.out.println("   Category: " + rs.getString(1) + ", Basic: " + rs.getInt(2));
            }
            rs.close();
            st.close();

            System.out.println("4. Testing Login credentials...");
            st = conn.createStatement();
            rs = st.executeQuery("SELECT username FROM Login");
            while (rs.next()) {
                System.out.println("   User: " + rs.getString(1));
            }
            rs.close();
            st.close();
            conn.close();
            System.out.println("All basic backend tests passed!");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
