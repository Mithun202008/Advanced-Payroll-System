import java.awt.*;
import java.awt.geom.*;
import java.util.Map;
import javax.swing.*;

/**
 * Modern vector-rendered lightweight analytics charts for the Executive Dashboard.
 */
public class DashboardCharts {

    /**
     * Payroll Overview Bar Chart comparing Gross, Allowances, Deductions, and Net.
     */
    public static class PayrollDistributionChart extends JPanel {
        private double gross = 100000;
        private double allowances = 15000;
        private double deductions = 8000;
        private double net = 107000;

        public PayrollDistributionChart() {
            setOpaque(false);
            setPreferredSize(new Dimension(300, 200));
        }

        public void updateData(double gross, double allowances, double deductions, double net) {
            this.gross = gross;
            this.allowances = allowances;
            this.deductions = deductions;
            this.net = net;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int padding = 30;
            int bottomPadding = 40;

            double maxVal = Math.max(1.0, Math.max(gross, Math.max(deductions, net)));
            int chartH = h - padding - bottomPadding;

            String[] labels = {"Gross", "Allowances", "Deductions", "Net Pay"};
            double[] vals = {gross, allowances, deductions, net};
            Color[] colors = {ModernTheme.PRIMARY, ModernTheme.INFO, ModernTheme.DANGER, ModernTheme.SUCCESS};

            int barWidth = Math.max(25, (w - padding * 2) / (labels.length * 2));
            int gap = barWidth;

            // Draw axis line
            g2.setColor(ModernTheme.SLATE_200);
            g2.drawLine(padding, h - bottomPadding, w - padding, h - bottomPadding);

            for (int i = 0; i < labels.length; i++) {
                int x = padding + gap / 2 + i * (barWidth + gap);
                int barH = (int) ((vals[i] / maxVal) * chartH);
                int y = h - bottomPadding - barH;

                // Bar
                g2.setColor(colors[i]);
                g2.fill(new RoundRectangle2D.Float(x, y, barWidth, barH, 6, 6));

                // Value on top
                g2.setFont(ModernTheme.FONT_SMALL_BOLD);
                g2.setColor(ModernTheme.TEXT_MAIN);
                String valStr = String.format("%.0f", vals[i]);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(valStr, x + (barWidth - fm.stringWidth(valStr)) / 2, y - 5);

                // Label below
                g2.setFont(ModernTheme.FONT_SMALL);
                g2.setColor(ModernTheme.TEXT_MUTED);
                g2.drawString(labels[i], x + (barWidth - fm.stringWidth(labels[i])) / 2, h - bottomPadding + 16);
            }

            g2.dispose();
        }
    }

    /**
     * Attendance breakdown chart (Present, Absent, Late, Leave).
     */
    public static class AttendanceBreakdownChart extends JPanel {
        private int present = 18;
        private int absent = 1;
        private int late = 2;
        private int leave = 1;

        public AttendanceBreakdownChart() {
            setOpaque(false);
            setPreferredSize(new Dimension(300, 200));
        }

        public void updateData(int p, int a, int l, int lv) {
            this.present = p;
            this.absent = a;
            this.late = l;
            this.leave = lv;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int total = Math.max(1, present + absent + late + leave);
            int w = getWidth();
            int h = getHeight();

            String[] categories = {"Present", "Late", "Absent", "On Leave"};
            int[] counts = {present, late, absent, leave};
            Color[] colors = {ModernTheme.SUCCESS, ModernTheme.WARNING, ModernTheme.DANGER, ModernTheme.INFO};

            int startY = 30;
            int barHeight = 16;
            int rowGap = 35;

            for (int i = 0; i < categories.length; i++) {
                int y = startY + i * rowGap;

                // Label and count
                g2.setFont(ModernTheme.FONT_BOLD);
                g2.setColor(ModernTheme.TEXT_MAIN);
                g2.drawString(categories[i], 20, y + 12);

                String countStr = counts[i] + " (" + (counts[i] * 100 / total) + "%)";
                g2.setFont(ModernTheme.FONT_SMALL_BOLD);
                g2.setColor(colors[i].darker());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(countStr, w - 20 - fm.stringWidth(countStr), y + 12);

                // Background track
                int barX = 100;
                int maxBarW = w - 200;
                if (maxBarW > 40) {
                    g2.setColor(ModernTheme.SLATE_100);
                    g2.fill(new RoundRectangle2D.Float(barX, y, maxBarW, barHeight, 8, 8));

                    // Filled progress
                    int fillW = (int) (((double) counts[i] / total) * maxBarW);
                    if (fillW > 0) {
                        g2.setColor(colors[i]);
                        g2.fill(new RoundRectangle2D.Float(barX, y, Math.max(8, fillW), barHeight, 8, 8));
                    }
                }
            }

            g2.dispose();
        }
    }

    /**
     * Department / Role Donut Chart.
     */
    public static class DepartmentDonutChart extends JPanel {
        private Map<String, Integer> data;

        public DepartmentDonutChart(Map<String, Integer> initialData) {
            this.data = initialData;
            setOpaque(false);
            setPreferredSize(new Dimension(280, 200));
        }

        public void updateData(Map<String, Integer> newData) {
            this.data = newData;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (data == null || data.isEmpty()) return;

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int total = 0;
            for (int count : data.values()) total += count;
            if (total == 0) total = 1;

            int w = getWidth();
            int h = getHeight();
            int diameter = Math.min(130, Math.min(w / 2, h - 30));
            int cx = 20 + diameter / 2;
            int cy = h / 2;

            Color[] palette = {
                    ModernTheme.PRIMARY,
                    ModernTheme.INFO,
                    ModernTheme.SUCCESS,
                    ModernTheme.WARNING,
                    new Color(168, 85, 247), // Purple
                    new Color(236, 72, 153)  // Pink
            };

            double curAngle = 90;
            int colorIdx = 0;

            int legendX = cx + diameter / 2 + 30;
            int legendY = 35;

            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                double arcAngle = (entry.getValue() / (double) total) * 360.0;
                Color col = palette[colorIdx % palette.length];

                // Donut outer arc
                g2.setColor(col);
                g2.fill(new Arc2D.Double(cx - diameter / 2.0, cy - diameter / 2.0, diameter, diameter, curAngle, arcAngle, Arc2D.PIE));
                curAngle += arcAngle;

                // Legend item
                if (legendX + 100 < w || true) {
                    g2.setColor(col);
                    g2.fill(new RoundRectangle2D.Float(legendX, legendY, 10, 10, 3, 3));

                    g2.setFont(ModernTheme.FONT_SMALL_BOLD);
                    g2.setColor(ModernTheme.TEXT_MAIN);
                    String lbl = entry.getKey();
                    if (lbl.length() > 14) lbl = lbl.substring(0, 12) + "..";
                    g2.drawString(lbl + " (" + entry.getValue() + ")", legendX + 16, legendY + 9);
                    legendY += 22;
                }

                colorIdx++;
            }

            // Inner hole for donut effect
            int holeD = (int) (diameter * 0.62);
            g2.setColor(Color.WHITE);
            g2.fill(new Ellipse2D.Double(cx - holeD / 2.0, cy - holeD / 2.0, holeD, holeD));

            // Center total label
            g2.setFont(ModernTheme.FONT_BOLD);
            g2.setColor(ModernTheme.TEXT_MAIN);
            String totStr = String.valueOf(total);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(totStr, cx - fm.stringWidth(totStr) / 2, cy + 4);

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));
            g2.setColor(ModernTheme.TEXT_MUTED);
            String sub = "Staff";
            fm = g2.getFontMetrics();
            g2.drawString(sub, cx - fm.stringWidth(sub) / 2, cy + 16);

            g2.dispose();
        }
    }
}
