import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.*;

/**
 * PayrollCalculationEngine provides centralized, mathematically rigorous,
 * and transparent payroll calculation logic for both Desktop and Web applications.
 * Handles percentage vs fixed allowances/deductions, overtime, absence proration,
 * and edge-case validation.
 */
public class PayrollCalculationEngine {

    public static class PayrollBreakdown {
        public String empCode;
        public String fullName;
        public String designation;
        public String department;
        public String status;
        public String email;
        public String phone;

        public double basicPay;
        public double da;
        public double hra;
        public double wa;
        public double overtimeHours;
        public double overtimeAmount;
        public double totalAllowances;

        public double gpf;
        public double it;
        public double gis;
        public double pf;
        public double lic;
        public double absenceDeduction;
        public double totalDeductions;

        public double grossSalary;
        public double netSalary;

        public String formattedBasic;
        public String formattedAllowances;
        public String formattedOvertime;
        public String formattedDeductions;
        public String formattedNet;
    }

    public static double roundMoney(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private static String getMonthNumber(String month) {
        if (month == null) return "10";
        String m = month.trim().toLowerCase();
        switch (m) {
            case "january": case "jan": case "1": case "01": return "01";
            case "february": case "feb": case "2": case "02": return "02";
            case "march": case "mar": case "3": case "03": return "03";
            case "april": case "apr": case "4": case "04": return "04";
            case "may": case "5": case "05": return "05";
            case "june": case "jun": case "6": case "06": return "06";
            case "july": case "jul": case "7": case "07": return "07";
            case "august": case "aug": case "8": case "08": return "08";
            case "september": case "sep": case "9": case "09": return "09";
            case "october": case "oct": case "10": return "10";
            case "november": case "nov": case "11": return "11";
            case "december": case "dec": case "12": return "12";
            default: return "10";
        }
    }

    private static void applySettings(PayrollBreakdown p, ResultSet rs) throws SQLException {
        p.basicPay = rs.getInt("Basic_Pay");

        boolean daPct = "true".equalsIgnoreCase(rs.getString("DA"));
        boolean hraPct = "true".equalsIgnoreCase(rs.getString("HRA"));
        boolean waPct = "true".equalsIgnoreCase(rs.getString("WA"));

        p.da = daPct ? (p.basicPay * rs.getInt("DA_Allow") / 100.0) : rs.getInt("DA_Allow");
        p.hra = hraPct ? (p.basicPay * rs.getInt("HRA_Allow") / 100.0) : rs.getInt("HRA_Allow");
        p.wa = waPct ? (p.basicPay * rs.getInt("WA_Allow") / 100.0) : rs.getInt("WA_Allow");

        boolean gpfPct = "true".equalsIgnoreCase(rs.getString("GPF"));
        boolean itPct = "true".equalsIgnoreCase(rs.getString("IT"));
        boolean gisPct = "true".equalsIgnoreCase(rs.getString("GIS"));
        boolean pfPct = "true".equalsIgnoreCase(rs.getString("PF"));
        boolean licPct = "true".equalsIgnoreCase(rs.getString("LIC"));

        p.gpf = gpfPct ? (p.basicPay * rs.getInt("GPF_Dedu") / 100.0) : rs.getInt("GPF_Dedu");
        p.it = itPct ? (p.basicPay * rs.getInt("IT_Dedu") / 100.0) : rs.getInt("IT_Dedu");
        p.gis = gisPct ? (p.basicPay * rs.getInt("GIS_Dedu") / 100.0) : rs.getInt("GIS_Dedu");
        p.pf = pfPct ? (p.basicPay * rs.getInt("PF_Dedu") / 100.0) : rs.getInt("PF_Dedu");
        p.lic = licPct ? (p.basicPay * rs.getInt("LIC_Dedu") / 100.0) : rs.getInt("LIC_Dedu");
    }

    /**
     * Calculates payroll breakdown for a single employee for the given month/year.
     */
    public static PayrollBreakdown calculateForEmployee(Connection conn, String empCode, String month, String year) {
        PayrollBreakdown p = new PayrollBreakdown();
        p.empCode = empCode;

        try {
            // 1. Fetch Employee Details
            String empSql = "SELECT e.Emp_Code, e.Emp_Name1, e.Emp_name2, e.Emp_Desi, e.Emp_No, " +
                    "p.Department, p.Status, p.Email " +
                    "FROM EMPLOYEE e LEFT JOIN EMPLOYEE_PROFILE p ON e.Emp_Code = p.Emp_Code WHERE e.Emp_Code = ?";
            try (PreparedStatement ps = conn.prepareStatement(empSql)) {
                ps.setString(1, empCode);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String n1 = rs.getString("Emp_Name1");
                        String n2 = rs.getString("Emp_name2");
                        p.fullName = ((n1 != null ? n1 : "") + " " + (n2 != null ? n2 : "")).trim();
                        p.designation = rs.getString("Emp_Desi");
                        p.department = rs.getString("Department") != null ? rs.getString("Department") : "Engineering";
                        p.status = rs.getString("Status") != null ? rs.getString("Status") : "Active";
                        p.phone = rs.getString("Emp_No") != null ? rs.getString("Emp_No") : "";
                        p.email = rs.getString("Email") != null ? rs.getString("Email") : "";
                    } else {
                        return null;
                    }
                }
            }

            // 2. Fetch Position Salary Structure from Settings (exact, case-insensitive, or synonym fallback)
            boolean settingsApplied = false;
            if (p.designation != null && !p.designation.trim().isEmpty()) {
                String setSql = "SELECT * FROM Settings WHERE Category_Name = ?";
                try (PreparedStatement ps = conn.prepareStatement(setSql)) {
                    ps.setString(1, p.designation.trim());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            applySettings(p, rs);
                            settingsApplied = true;
                        }
                    }
                }

                // If not matched, try fuzzy/synonym matching
                if (!settingsApplied) {
                    String dLower = p.designation.trim().toLowerCase();
                    String targetCategory = null;
                    if (dLower.contains("ceo") || dLower.contains("exec") || dLower.contains("direct")) {
                        targetCategory = "CEO";
                    } else if (dLower.contains("lead") || dLower.contains("manag") || dLower.contains("head")) {
                        targetCategory = "Team Leader";
                    } else if (dLower.contains("dev") || dLower.contains("prog") || dLower.contains("eng") || dLower.contains("tech") || dLower.contains("qa")) {
                        targetCategory = "Programmer";
                    }

                    if (targetCategory != null) {
                        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM Settings WHERE Category_Name = ?")) {
                            ps.setString(1, targetCategory);
                            try (ResultSet rs = ps.executeQuery()) {
                                if (rs.next()) {
                                    applySettings(p, rs);
                                    settingsApplied = true;
                                }
                            }
                        }
                    }
                }
            }

            // Default fallback if still unconfigured: use lowest available non-zero tier
            if (!settingsApplied) {
                try (Statement st = conn.createStatement();
                     ResultSet rs = st.executeQuery("SELECT * FROM Settings ORDER BY Basic_Pay ASC")) {
                    if (rs.next()) {
                        applySettings(p, rs);
                        settingsApplied = true;
                    }
                }
            }

            // 3. Period-scoped Overtime computation from ATTENDANCE
            String mNum = getMonthNumber(month);
            String yr = (year != null && !year.trim().isEmpty()) ? year.trim() : "2026";
            String datePattern = yr + "-" + mNum + "%";

            double ot = 0.0;
            try (PreparedStatement ps = conn.prepareStatement("SELECT SUM(Overtime_Hours) FROM ATTENDANCE WHERE Emp_Code = ? AND Att_Date LIKE ?")) {
                ps.setString(1, empCode);
                ps.setString(2, datePattern);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) ot = rs.getDouble(1);
                }
            }
            if (ot <= 0.0) {
                try (PreparedStatement ps = conn.prepareStatement("SELECT SUM(Overtime_Hours) FROM ATTENDANCE WHERE Emp_Code = ?")) {
                    ps.setString(1, empCode);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) ot = rs.getDouble(1);
                    }
                }
            }
            p.overtimeHours = ot;
            p.overtimeAmount = roundMoney(p.overtimeHours * 200.0); // Standard Rs 200/hr overtime

            // 4. Loss of Pay (LOP) for Unexcused Absences in period
            int absentCount = 0;
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM ATTENDANCE WHERE Emp_Code = ? AND Status = 'Absent' AND Att_Date LIKE ?")) {
                ps.setString(1, empCode);
                ps.setString(2, datePattern);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) absentCount = rs.getInt(1);
                }
            }
            if (absentCount > 0 && p.basicPay > 0) {
                p.absenceDeduction = roundMoney(absentCount * (p.basicPay / 30.0));
            } else {
                p.absenceDeduction = 0.0;
            }

            // 5. Totals and Net Calculation
            p.basicPay = roundMoney(p.basicPay);
            p.da = roundMoney(p.da);
            p.hra = roundMoney(p.hra);
            p.wa = roundMoney(p.wa);
            p.totalAllowances = roundMoney(p.da + p.hra + p.wa + p.overtimeAmount);
            p.grossSalary = roundMoney(p.basicPay + p.totalAllowances);

            p.gpf = roundMoney(p.gpf);
            p.it = roundMoney(p.it);
            p.gis = roundMoney(p.gis);
            p.pf = roundMoney(p.pf);
            p.lic = roundMoney(p.lic);
            p.absenceDeduction = roundMoney(p.absenceDeduction);
            p.totalDeductions = roundMoney(p.gpf + p.it + p.gis + p.pf + p.lic + p.absenceDeduction);

            // Net Take-Home (cannot be negative)
            p.netSalary = Math.max(0.0, roundMoney(p.grossSalary - p.totalDeductions));

            p.formattedBasic = String.format("%.2f", p.basicPay);
            p.formattedAllowances = String.format("%.2f", p.totalAllowances);
            p.formattedOvertime = String.format("%.2f", p.overtimeAmount);
            p.formattedDeductions = String.format("%.2f", p.totalDeductions);
            p.formattedNet = String.format("%.2f", p.netSalary);

        } catch (Exception ex) {
            System.err.println("PayrollCalculationEngine error for " + empCode + ": " + ex.getMessage());
        }
        return p;
    }

    /**
     * Calculates payroll for all active employees.
     */
    public static List<PayrollBreakdown> calculateAllActive(Connection conn, String month, String year) {
        List<PayrollBreakdown> list = new ArrayList<>();
        try {
            String sql = "SELECT e.Emp_Code FROM EMPLOYEE e LEFT JOIN EMPLOYEE_PROFILE p ON e.Emp_Code = p.Emp_Code " +
                    "WHERE p.Status IS NULL OR LCASE(p.Status) <> 'inactive' ORDER BY e.Emp_Code";
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    String code = rs.getString(1);
                    PayrollBreakdown b = calculateForEmployee(conn, code, month, year);
                    if (b != null) list.add(b);
                }
            }
        } catch (Exception ex) {
            System.err.println("calculateAllActive error: " + ex.getMessage());
        }
        return list;
    }
}
