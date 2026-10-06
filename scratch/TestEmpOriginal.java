import java.sql.*;

public class TestEmpOriginal {
    public static void main(String[] args) {
        try (Connection conn = clsConnection.getConnection()) {
            Statement st = conn.createStatement();
            int n = st.executeUpdate("UPDATE EMPLOYEE SET Emp_Add = 'hosur' WHERE Emp_Code = 'EMP001'");
            System.out.println("Update result: " + n);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
