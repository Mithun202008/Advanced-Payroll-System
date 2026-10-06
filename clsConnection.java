import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import java.util.logging.Level;
import java.util.logging.Logger;

public class clsConnection {
    static {
        try {
            Logger.getLogger("net.ucanaccess").setLevel(Level.SEVERE);
            Logger.getLogger("com.healthmarketscience.jackcess").setLevel(Level.SEVERE);
        } catch (Exception ignored) {}
    }

    private String url = "";
    private String username = "";
    private String password = "";

    public static Connection getConnection() throws SQLException {
        try {
            Properties props = new Properties();
            String fileName = "MakeDB.ini";
            try (FileInputStream in = new FileInputStream(fileName)) {
                props.load(in);
            }
            String drivers = props.getProperty("jdbc.drivers");
            if (drivers != null && !drivers.trim().isEmpty()) {
                System.setProperty("jdbc.drivers", drivers.trim());
            }
            String dbUrl = props.getProperty("jdbc.url");
            String dbUser = props.getProperty("jdbc.username", "");
            String dbPass = props.getProperty("jdbc.password", "");
            return DriverManager.getConnection(dbUrl, dbUser, dbPass);
        } catch (IOException e) {
            System.err.println("clsConnection: Error loading MakeDB.ini: " + e.getMessage());
            throw new SQLException("Cannot read MakeDB.ini", e);
        }
    }

    public Connection setConnection(Connection conn, String uName, String pwd) {
        try {
            return getConnection();
        } catch (SQLException e) {
            System.err.println("SQL Exception in setConnection: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            System.err.println("General Exception in setConnection: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}