import java.sql.*;

public class InspectDB {
    public static void main(String[] args) {
        try {
            Connection conn = clsConnection.getConnection();
            DatabaseMetaData meta = conn.getMetaData();
            System.out.println("=== TABLES ===");
            ResultSet rs = meta.getTables(null, null, "%", new String[]{"TABLE"});
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                System.out.println("\n=== Table: " + tableName + " ===");
                Statement stmt = conn.createStatement();
                ResultSet dataRs = stmt.executeQuery("SELECT * FROM [" + tableName + "]");
                ResultSetMetaData dmeta = dataRs.getMetaData();
                int colCount = dmeta.getColumnCount();
                while (dataRs.next()) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 1; i <= colCount; i++) {
                        sb.append(dmeta.getColumnName(i)).append(": ").append(dataRs.getString(i)).append(" | ");
                    }
                    System.out.println("  ROW: " + sb.toString());
                }
                dataRs.close();
                stmt.close();
            }
            rs.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
