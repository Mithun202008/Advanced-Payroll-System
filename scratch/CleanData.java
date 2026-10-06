import java.sql.*;

public class CleanData {
    public static void main(String[] args) {
        try (Connection conn = clsConnection.getConnection()) {
            Statement st = conn.createStatement();

            // Ensure admin user exists in Login
            ResultSet rs = st.executeQuery("SELECT count(*) FROM Login WHERE LCASE(username) = 'admin'");
            if (rs.next() && rs.getInt(1) == 0) {
                st.executeUpdate("INSERT INTO Login (username, password) VALUES ('admin', 'admin')");
                System.out.println("Added admin / admin to Login table.");
            }
            rs.close();

            // Fix corrupted columns in rows 01, 02, 069 if needed
            st.executeUpdate("UPDATE EMPLOYEE SET Emp_Add = 'Hosur', Emp_No = '8825671929', Emp_Desi = 'CEO' WHERE Emp_Code = '01' AND Emp_Add = '50000'");
            st.executeUpdate("UPDATE EMPLOYEE SET Emp_Add = 'Erode', Emp_No = '9842109876', Emp_Desi = 'Programmer' WHERE Emp_Code = '02' AND Emp_Add = '30000'");
            st.executeUpdate("UPDATE EMPLOYEE SET Emp_Add = 'Chennai', Emp_No = '9840123456', Emp_Desi = 'Team Leader' WHERE Emp_Code = '069' AND Emp_Add = '20000'");

            System.out.println("Data cleanup complete!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
