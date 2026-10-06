import java.sql.*;

public class TestUpd {
    public static void main(String[] args) {
        try (Connection conn = clsConnection.getConnection()) {
            Statement st = conn.createStatement();
            int n = st.executeUpdate("UPDATE EMPLOYEE SET Department = 'Engineering', Status = 'Active', Employment_Type = 'Full-Time'");
            System.out.println("Updated rows: " + n);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
