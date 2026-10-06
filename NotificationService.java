import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Manages in-app notifications and alerts.
 */
public class NotificationService {

    public static class NotificationItem {
        public int id;
        public String title;
        public String message;
        public String category;
        public String createdAt;
        public boolean isRead;

        public NotificationItem(int id, String title, String message, String category, String createdAt, boolean isRead) {
            this.id = id;
            this.title = title;
            this.message = message;
            this.category = category;
            this.createdAt = createdAt;
            this.isRead = isRead;
        }
    }

    public static void send(String title, String message, String category) {
        new Thread(() -> {
            try (Connection conn = clsConnection.getConnection()) {
                String now = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
                String sql = "INSERT INTO NOTIFICATIONS (Title, Message, Category, Created_At, Is_Read) VALUES (?, ?, ?, ?, 'false')";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, title);
                    pstmt.setString(2, message);
                    pstmt.setString(3, category);
                    pstmt.setString(4, now);
                    pstmt.executeUpdate();
                }
            } catch (Exception e) {
                System.err.println("NotificationService error: " + e.getMessage());
            }
        }).start();
    }

    public static int getUnreadCount() {
        try (Connection conn = clsConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT count(*) FROM NOTIFICATIONS WHERE Is_Read = 'false'")) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception ignored) {}
        return 0;
    }

    public static List<NotificationItem> getAll() {
        List<NotificationItem> list = new ArrayList<>();
        try (Connection conn = clsConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM NOTIFICATIONS ORDER BY Notif_Id DESC")) {
            while (rs.next()) {
                list.add(new NotificationItem(
                        rs.getInt("Notif_Id"),
                        rs.getString("Title"),
                        rs.getString("Message"),
                        rs.getString("Category"),
                        rs.getString("Created_At"),
                        "true".equalsIgnoreCase(rs.getString("Is_Read"))
                ));
            }
        } catch (Exception ignored) {}
        return list;
    }

    public static void markAllAsRead() {
        new Thread(() -> {
            try (Connection conn = clsConnection.getConnection();
                 Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("UPDATE NOTIFICATIONS SET Is_Read = 'true'");
            } catch (Exception ignored) {}
        }).start();
    }
}
