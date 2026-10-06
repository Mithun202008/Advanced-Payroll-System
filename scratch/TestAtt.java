import java.sql.*;

public class TestAtt {
    public static void main(String[] args) {
        try (Connection conn = clsConnection.getConnection()) {
            Statement st = conn.createStatement();
            st.executeUpdate("INSERT INTO ATTENDANCE (Emp_Code, Att_Date, Status, Check_In, Check_Out, Overtime_Hours, Notes) " +
                    "VALUES ('EMP001', '2026-10-01', 'Present', '09:00', '18:00', 0.0, 'Regular shift')");
            System.out.println("Inserted successfully into ATTENDANCE!");

            ResultSet rs = st.executeQuery("SELECT * FROM ATTENDANCE");
            while (rs.next()) {
                System.out.println("Row: " + rs.getInt("Att_Id") + " | " + rs.getString("Emp_Code") + " | " + rs.getString("Status"));
            }
            rs.close();
            st.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
