package university;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorMessage;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // تحقق من صحة بيانات تسجيل الدخول
       
            // الانتقال إلى الشاشة التالية
            try {
                // تحميل شاشة home.fxml
                FXMLLoader loader = new FXMLLoader(getClass().getResource("home.fxml"));
                Stage stage = (Stage) usernameField.getScene().getWindow();
                Scene scene = new Scene(loader.load());
                stage.setScene(scene);
                stage.setTitle("Home"); // تعيين عنوان النافذة
                stage.show();
            } catch (IOException e) {
                e.printStackTrace(); // طباعة الخطأ في حالة وجود مشكلة في تحميل الشاشة
            }
        
        
    }

    // دالة تحقق من صحة تسجيل الدخول (تحتاج إلى تعديلها حسب منطق التطبيق)
    private boolean validateLogin(String username, String password) {
        // في هذا المثال، سنعتبر أن اسم المستخدم هو "admin" وكلمة المرور هي "password"
        return "admin".equals(username) && "password".equals(password);
    }
}
