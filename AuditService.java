import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Service to record sensitive security and administrative actions in the AUDIT_LOGS table.
 */
public class AuditService {

    public static void log(String username, String actionType, String targetEntity, String details) {
        new Thread(() -> {
            try (Connection conn = clsConnection.getConnection()) {
                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                String sql = "INSERT INTO AUDIT_LOGS (Username, Action_Type, Target_Entity, Details, Log_Timestamp) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, username != null ? username : "SYSTEM");
                    pstmt.setString(2, actionType);
                    pstmt.setString(3, targetEntity);
                    pstmt.setString(4, details);
                    pstmt.setString(5, timestamp);
                    pstmt.executeUpdate();
                }
            } catch (Exception e) {
                System.err.println("AuditService error: " + e.getMessage());
            }
        }).start();
    }
}
