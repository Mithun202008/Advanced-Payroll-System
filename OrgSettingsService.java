import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages organization metadata and configuration.
 */
public class OrgSettingsService {

    public static Map<String, String> getSettings() {
        Map<String, String> map = new HashMap<>();
        try (Connection conn = clsConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Org_Key, Org_Value FROM ORGANIZATION_SETTINGS")) {
            while (rs.next()) {
                map.put(rs.getString("Org_Key"), rs.getString("Org_Value"));
            }
        } catch (Exception ignored) {}
        return map;
    }

    public static String get(String key, String defaultVal) {
        try (Connection conn = clsConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT Org_Value FROM ORGANIZATION_SETTINGS WHERE Org_Key = ?")) {
            pstmt.setString(1, key);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String v = rs.getString("Org_Value");
                    return v != null ? v : defaultVal;
                }
            }
        } catch (Exception ignored) {}
        return defaultVal;
    }

    public static void set(String key, String value) {
        try (Connection conn = clsConnection.getConnection()) {
            String check = "SELECT count(*) FROM ORGANIZATION_SETTINGS WHERE Org_Key = ?";
            boolean exists = false;
            try (PreparedStatement ps = conn.prepareStatement(check)) {
                ps.setString(1, key);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) exists = true;
                }
            }
            if (exists) {
                try (PreparedStatement ps = conn.prepareStatement("UPDATE ORGANIZATION_SETTINGS SET Org_Value = ? WHERE Org_Key = ?")) {
                    ps.setString(1, value);
                    ps.setString(2, key);
                    ps.executeUpdate();
                }
            } else {
                try (PreparedStatement ps = conn.prepareStatement("INSERT INTO ORGANIZATION_SETTINGS (Org_Key, Org_Value) VALUES (?, ?)")) {
                    ps.setString(1, key);
                    ps.setString(2, value);
                    ps.executeUpdate();
                }
            }
        } catch (Exception e) {
            System.err.println("Error saving org setting: " + e.getMessage());
        }
    }
}
