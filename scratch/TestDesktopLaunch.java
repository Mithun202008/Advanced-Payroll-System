import javax.swing.*;

public class TestDesktopLaunch {
    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        System.setProperty("sun.java2d.uiScale.enabled", "true");

        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("Initializing LoginFrame...");
                LoginFrame login = new LoginFrame();
                System.out.println("LoginFrame initialized successfully: " + login.getTitle());

                System.out.println("Initializing MainMenu...");
                MainMenu main = new MainMenu("admin", new java.util.Date());
                System.out.println("MainMenu initialized successfully: " + main.getTitle());
                System.out.println("Minimum Size: " + main.getMinimumSize());
                System.out.println("Current Size: " + main.getSize());

                login.dispose();
                main.dispose();
                System.out.println("ALL DESKTOP COMPONENTS INITIALIZED AND CLEANED UP SUCCESSFULLY!");
                System.exit(0);
            } catch (Throwable t) {
                t.printStackTrace();
                System.exit(1);
            }
        });
    }
}
