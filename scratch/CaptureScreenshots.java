import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Method;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.swing.*;

public class CaptureScreenshots {

    public static BufferedImage capture(Component comp, int w, int h) {
        comp.setSize(w, h);
        if (comp instanceof JFrame) {
            ((JFrame) comp).getRootPane().setSize(w, h);
            ((JFrame) comp).getRootPane().doLayout();
            ((JFrame) comp).getRootPane().validate();
        }
        comp.doLayout();
        comp.validate();

        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        comp.paint(g2);
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
            BufferedImage imgLogin = capture(login.getContentPane(), 520, 360);
            ImageIO.write(imgLogin, "png", new File(outDir, "fig5_1_login.png"));
            login.dispose();

            // 2. MainMenu (Figure 5.2 - Executive Dashboard)
            System.out.println("Capturing MainMenu Dashboard...");
            MainMenu main = new MainMenu("admin", new Date());
            main.navigateTo("DASHBOARD");
            Thread.sleep(800); // allow async workers to populate
            BufferedImage imgDash = capture(main.getContentPane(), 1260, 800);
            ImageIO.write(imgDash, "png", new File(outDir, "fig5_2_dashboard.png"));

            // 3. Employee Directory (Figure 5.3)
            System.out.println("Capturing Employee Directory...");
            main.navigateTo("EMPLOYEES");
            Thread.sleep(800);
            BufferedImage imgEmps = capture(main.getContentPane(), 1260, 800);
            ImageIO.write(imgEmps, "png", new File(outDir, "fig5_3_employees.png"));

            // 4. Add Employee Form (Figure 5.4)
            System.out.println("Capturing Add Employee Form...");
            Addwindow addWin = new Addwindow(null);
            BufferedImage imgAdd = capture(addWin.getContentPane(), 540, 480);
            ImageIO.write(imgAdd, "png", new File(outDir, "fig5_4_add_employee.png"));
            addWin.dispose();

            // 5. Edit Employee Form (Figure 5.5)
            System.out.println("Capturing Edit Employee Form...");
            Editwindow editWin = new Editwindow(null);
            try {
                Method m = Editwindow.class.getDeclaredMethod("loadEmployeeDetails", String.class);
                m.setAccessible(true);
                m.invoke(editWin, "01");
            } catch (Exception ignored) {}
            BufferedImage imgEdit = capture(editWin.getContentPane(), 540, 500);
            ImageIO.write(imgEdit, "png", new File(outDir, "fig5_5_edit_employee.png"));
            editWin.dispose();

            // 6. Settings Window / Modern Settings View (Figure 5.6)
            System.out.println("Capturing Settings View...");
            main.navigateTo("SETTINGS");
            Thread.sleep(500);
            BufferedImage imgSettings = capture(main.getContentPane(), 1260, 800);
            ImageIO.write(imgSettings, "png", new File(outDir, "fig5_6_settings.png"));

            // 7. Payroll Run / Processor (Figure 5.7)
            System.out.println("Capturing Payroll Run Pipeline...");
            main.navigateTo("PAYROLL_RUN");
            Thread.sleep(800);
            BufferedImage imgPayroll = capture(main.getContentPane(), 1260, 800);
            ImageIO.write(imgPayroll, "png", new File(outDir, "fig5_7_payroll.png"));

            // 8. Printable Salary Slip (Figure 5.8)
            System.out.println("Capturing Printable Salary Slip...");
            printwindow printWin = new printwindow();
            printWin.fill_data("mithun", "A", "October", "2026", "01",
                    "2026-10-04", "CEO", "50,000.00", "5,000.00", "1,000.00", "0.00",
                    "1,000.00", "1,000.00", "0.00", "1,000.00", "0.00", "6,000.00",
                    "3,000.00", "53,000.00");
            BufferedImage imgPrint = capture(printWin.getContentPane(), 660, 720);
            ImageIO.write(imgPrint, "png", new File(outDir, "fig5_8_print_slip.png"));
            printWin.dispose();

            // 9. Change Password (Figure 5.9)
            System.out.println("Capturing Change Password...");
            ChangePasswordWindow passWin = new ChangePasswordWindow(null);
            BufferedImage imgPass = capture(passWin.getContentPane(), 480, 280);
            ImageIO.write(imgPass, "png", new File(outDir, "fig5_9_change_password.png"));
            passWin.dispose();

            // 10. Help & Documentation (Figure 5.10)
            System.out.println("Capturing Help Window...");
            Helpwindow helpWin = new Helpwindow(null);
            BufferedImage imgHelp = capture(helpWin.getContentPane(), 680, 540);
            ImageIO.write(imgHelp, "png", new File(outDir, "fig5_10_help.png"));
            helpWin.dispose();

            main.dispose();
            System.out.println("ALL SCREENSHOTS CAPTURED SUCCESSFULLY!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
