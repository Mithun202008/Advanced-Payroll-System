import java.sql.*;

public class TestInsertEmp {
    public static void main(String[] args) {
        try (Connection conn = clsConnection.getConnection()) {
            Statement st = conn.createStatement();
            st.executeUpdate("INSERT INTO EMPLOYEE (Emp_Code, Emp_Name1, Emp_name2, Emp_Add, Emp_No, Emp_Desi) " +
                    "VALUES ('EMP004', 'Rajesh', 'Kumar', 'Bangalore', '9876543210', 'Programmer')");
            System.out.println("Inserted EMP004 successfully!");

            // clean up
            st.executeUpdate("DELETE FROM EMPLOYEE WHERE Emp_Code = 'EMP004'");
            System.out.println("Cleaned up EMP004!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
