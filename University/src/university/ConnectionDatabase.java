package university;

import java.sql.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.util.List;

/**
 * 
 * @author ali
 */
public class ConnectionDatabase {
    @FXML
    private Label messageLabel;

    private static Connection connection;

    // جعل الدالة static ليتسنى استدعاؤها بدون إنشاء كائن
    public static Connection connectToDatabase() {
        try {
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            String url = "jdbc:oracle:thin:@localhost:1521:orcl";
            String user = "alialaaamri";
            String password = "2003";

            connection = DriverManager.getConnection(url, user, password);

            if (connection != null && !connection.isClosed()) {
                System.out.println("تم الاتصال بقاعدة البيانات بنجاح!");
            } else {
                System.out.println("فشل الاتصال بقاعدة البيانات.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("استثناء SQL: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("خطأ غير متوقع: " + e.getMessage());
        }

        return connection;
    }

    // استدعاء الدالة عند الحاجة في أي مكان
    
    
    public static void insertIntoTable(String tableName, List<String> columns, List<Object> values) {
        Connection conn = connectToDatabase();

        if (columns.size() != values.size()) {
            throw new IllegalArgumentException("عدد الأعمدة يجب أن يساوي عدد القيم.");
        }

        StringBuilder sql = new StringBuilder("INSERT INTO " + tableName + " (");

        // بناء نص الأعمدة
        for (int i = 0; i < columns.size(); i++) {
            sql.append(columns.get(i));
            if (i < columns.size() - 1) {
                sql.append(", ");
            }
        }

        sql.append(") VALUES (");

        // بناء نص القيم (العلامات ؟)
        for (int i = 0; i < values.size(); i++) {
            sql.append("?");
            if (i < values.size() - 1) {
                sql.append(", ");
            }
        }
        sql.append(")");

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql.toString());

            // تعيين القيم في علامات الاستفهام
            for (int i = 0; i < values.size(); i++) {
                if (values.get(i) instanceof String) {
                    pstmt.setString(i + 1, (String) values.get(i));
                } else if (values.get(i) instanceof Integer) {
                    pstmt.setInt(i + 1, (Integer) values.get(i));
                } else if (values.get(i) instanceof Double) {
                    pstmt.setDouble(i + 1, (Double) values.get(i));
                } else if (values.get(i) instanceof Float) {
                    pstmt.setFloat(i + 1, (Float) values.get(i));
                }
                // يمكنك إضافة المزيد من أنواع البيانات حسب الحاجة
            }

            pstmt.executeUpdate();
            System.out.println("تم إدخال البيانات بنجاح!");
           
      
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("حدث خطأ أثناء إدخال البيانات: " + e.getMessage());
        }
    }
  
}
