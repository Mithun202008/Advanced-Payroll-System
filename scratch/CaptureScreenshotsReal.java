import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Method;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.swing.*;

public class CaptureScreenshotsReal {

    public static BufferedImage captureWindow(Window window) {
        window.setVisible(true);
        window.toFront();
        try { Thread.sleep(600); } catch (Exception ignored) {}

        BufferedImage img = new BufferedImage(window.getWidth(), window.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        window.printAll(g2);
        g2.dispose();
        return img;
    }

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        System.setProperty("sun.java2d.uiScale.enabled", "true");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        File outDir = new File("scratch/new_screenshots");
        outDir.mkdirs();

        try {
            // 1. LoginFrame (Figure 5.1)
            System.out.println("Capturing LoginFrame...");
            LoginFrame login = new LoginFrame();
            login.pack();
            login.setLocationRelativeTo(null);
            BufferedImage imgLogin = captureWindow(login);
            ImageIO.write(imgLogin, "png", new File(outDir, "fig5_1_login.png"));
            login.dispose();

            // 2. MainMenu (Figure 5.2 - Executive Dashboard)
            System.out.println("Capturing MainMenu Dashboard...");
            MainMenu main = new MainMenu("admin", new Date());
            main.setVisible(true);
            main.navigateTo("DASHBOARD");
            Thread.sleep(1200); // allow SwingWorker to complete DB queries
            BufferedImage imgDash = captureWindow(main);
            ImageIO.write(imgDash, "png", new File(outDir, "fig5_2_dashboard.png"));

            // 3. Employee Directory (Figure 5.3)
            System.out.println("Capturing Employee Directory...");
            main.navigateTo("EMPLOYEES");
            Thread.sleep(1000);
            BufferedImage imgEmps = captureWindow(main);
            ImageIO.write(imgEmps, "png", new File(outDir, "fig5_3_employees.png"));

            // 6. Settings Window / Modern Settings View (Figure 5.6)
            System.out.println("Capturing Settings View...");
            main.navigateTo("SETTINGS");
            Thread.sleep(800);
            BufferedImage imgSettings = captureWindow(main);
            ImageIO.write(imgSettings, "png", new File(outDir, "fig5_6_settings.png"));

            // 7. Payroll Run / Processor (Figure 5.7)
            System.out.println("Capturing Payroll Run Pipeline...");
            main.navigateTo("PAYROLL_RUN");
            Thread.sleep(1200);
            BufferedImage imgPayroll = captureWindow(main);
            ImageIO.write(imgPayroll, "png", new File(outDir, "fig5_7_payroll.png"));

            main.dispose();

            // 4. Add Employee Form (Figure 5.4)
            System.out.println("Capturing Add Employee Form...");
            JFrame frameAdd = new JFrame("Employee Registration - Add New Staff");
            Addwindow addWin = new Addwindow(null);
            frameAdd.setContentPane(addWin.getContentPane());
            frameAdd.setSize(520, 480);
            frameAdd.setLocationRelativeTo(null);
            BufferedImage imgAdd = captureWindow(frameAdd);
            ImageIO.write(imgAdd, "png", new File(outDir, "fig5_4_add_employee.png"));
            frameAdd.dispose();

            // 5. Edit Employee Form (Figure 5.5)
            System.out.println("Capturing Edit Employee Form...");
            JFrame frameEdit = new JFrame("Employee Record Modification & Persistence");
            Editwindow editWin = new Editwindow(null);
            try {
                Method m = Editwindow.class.getDeclaredMethod("loadEmployeeDetails", String.class);
                m.setAccessible(true);
                m.invoke(editWin, "01");
            } catch (Exception ignored) {}
            frameEdit.setContentPane(editWin.getContentPane());
            frameEdit.setSize(520, 500);
            frameEdit.setLocationRelativeTo(null);
            BufferedImage imgEdit = captureWindow(frameEdit);
            ImageIO.write(imgEdit, "png", new File(outDir, "fig5_5_edit_employee.png"));
            frameEdit.dispose();

            // 8. Printable Salary Slip (Figure 5.8)
            System.out.println("Capturing Printable Salary Slip...");
            printwindow printWin = new printwindow();
            printWin.fill_data("mithun", "A", "October", "2026", "01",
                    "2026-10-04", "CEO", "50,000.00", "5,000.00", "1,000.00", "0.00",
                    "1,000.00", "1,000.00", "0.00", "1,000.00", "0.00", "6,000.00",
                    "3,000.00", "53,000.00");
            printWin.setLocationRelativeTo(null);
            BufferedImage imgPrint = captureWindow(printWin);
            ImageIO.write(imgPrint, "png", new File(outDir, "fig5_8_print_slip.png"));
            printWin.dispose();

            // 9. Change Password (Figure 5.9)
            System.out.println("Capturing Change Password...");
            JFrame framePass = new JFrame("Administrative Security & Password Management");
            ChangePasswordWindow passWin = new ChangePasswordWindow(null);
            framePass.setContentPane(passWin.getContentPane());
            framePass.setSize(460, 280);
            framePass.setLocationRelativeTo(null);
            BufferedImage imgPass = captureWindow(framePass);
            ImageIO.write(imgPass, "png", new File(outDir, "fig5_9_change_password.png"));
            framePass.dispose();

            // 10. Help Window (Figure 5.10)
            System.out.println("Capturing Help Window...");
            JFrame frameHelp = new JFrame("Integrated User Documentation & Help Guide");
            Helpwindow helpWin = new Helpwindow(null);
            frameHelp.setContentPane(helpWin.getContentPane());
            frameHelp.setSize(680, 540);
            frameHelp.setLocationRelativeTo(null);
            BufferedImage imgHelp = captureWindow(frameHelp);
            ImageIO.write(imgHelp, "png", new File(outDir, "fig5_10_help.png"));
            frameHelp.dispose();

            System.out.println("ALL REAL SCREENSHOTS CAPTURED SUCCESSFULLY!");
            System.exit(0);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }
}
