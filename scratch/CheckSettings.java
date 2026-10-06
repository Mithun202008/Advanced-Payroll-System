import java.sql.*;

public class CheckSettings {
    public static void main(String[] args) {
        try (Connection conn = clsConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT Category_Name, Basic_Pay, DA, DA_Allow, HRA, HRA_Allow, WA, WA_Allow, GPF, GPF_Dedu, IT, IT_Dedu, GIS, GIS_Dedu, PF, PF_Dedu, LIC, LIC_Dedu FROM Settings")) {
            System.out.println("=== SETTINGS ROWS ===");
            while (rs.next()) {
                System.out.println(String.format("Cat: %-15s | Basic: %-8d | DA: %s(%d) | HRA: %s(%d) | GPF: %s(%d) | PF: %s(%d) | IT: %s(%d)",
                        rs.getString(1), rs.getInt(2),
                        rs.getString(3), rs.getInt(4),
                        rs.getString(5), rs.getInt(6),
                        rs.getString(9), rs.getInt(10),
                        rs.getString(15), rs.getInt(16),
                        rs.getString(11), rs.getInt(12)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
