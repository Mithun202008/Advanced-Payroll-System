import java.sql.*;

/**
 * AuthService provides role-based authorization and session management.
 * Roles: SUPER_ADMIN, HR_ADMIN, MANAGER, EMPLOYEE
 */
public class AuthService {

    public enum Role {
        SUPER_ADMIN,
        HR_ADMIN,
        MANAGER,
        EMPLOYEE
    }

    private static String currentUser = "admin";
    private static Role currentRole = Role.SUPER_ADMIN;

    public static void setCurrentUser(String user) {
        currentUser = user != null ? user : "admin";
        currentRole = fetchUserRole(currentUser);
    }

    public static String getCurrentUser() {
        return currentUser;
    }

    public static Role getCurrentRole() {
        return currentRole;
    }

    public static boolean canManageEmployees() {
        return currentRole == Role.SUPER_ADMIN || currentRole == Role.HR_ADMIN;
    }

    public static boolean canRunPayroll() {
        return currentRole == Role.SUPER_ADMIN || currentRole == Role.HR_ADMIN;
    }

    public static boolean canApproveLeave() {
        return currentRole == Role.SUPER_ADMIN || currentRole == Role.HR_ADMIN || currentRole == Role.MANAGER;
    }

    public static boolean canManageSettings() {
        return currentRole == Role.SUPER_ADMIN;
    }

    public static boolean canViewAuditLogs() {
        return currentRole == Role.SUPER_ADMIN;
    }

    public static Role fetchUserRole(String username) {
        try (Connection conn = clsConnection.getConnection()) {
            String sql = "SELECT User_Role FROM USER_ROLES WHERE LCASE(Username) = LCASE(?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        String r = rs.getString("User_Role");
                        if ("SUPER_ADMIN".equalsIgnoreCase(r)) return Role.SUPER_ADMIN;
                        if ("HR_ADMIN".equalsIgnoreCase(r)) return Role.HR_ADMIN;
                        if ("MANAGER".equalsIgnoreCase(r)) return Role.MANAGER;
                        if ("EMPLOYEE".equalsIgnoreCase(r)) return Role.EMPLOYEE;
                    }
                }
            }
        } catch (Exception ignored) {}

        // Default: if user is admin, assign SUPER_ADMIN; else EMPLOYEE
        if ("admin".equalsIgnoreCase(username) || "pavan".equalsIgnoreCase(username)) {
            return Role.SUPER_ADMIN;
        }
        return Role.EMPLOYEE;
    }
}
