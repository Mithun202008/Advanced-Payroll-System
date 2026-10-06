import java.sql.*;

public class CheckEmployees {
    public static void main(String[] args) {
        try (Connection conn = clsConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT Emp_Code, Emp_Name1, Emp_name2, Emp_Desi, Emp_No, Emp_Add FROM EMPLOYEE")) {
            System.out.println("=== EMPLOYEES ===");
            while (rs.next()) {
                System.out.println(String.format("Code: %-6s | Name: %-15s | Desi: %-15s | Phone: %-12s | Add: %s",
                        rs.getString(1),
                        rs.getString(2) + " " + (rs.getString(3) != null ? rs.getString(3) : ""),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(6)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
