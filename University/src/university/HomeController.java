package university;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class HomeController {

    @FXML
    private Label label;  // عنصر الـ Label من الـ FXML

    @FXML
    private AnchorPane contentArea;  // مكان لعرض المحتوى

    // دالة يتم استدعاؤها عند النقر على زر "إدارة المشاريع"
    @FXML
    private void handleProjectManagement() {
       try {
            // تحميل واجهة إدارة الموظفين
            AnchorPane ProjectManagement = FXMLLoader.load(getClass().getResource("ProjectManagement.fxml"));
           
            // مسح المحتوى السابق وإضافة المحتوى الجديد
            contentArea.getChildren().clear();
            contentArea.getChildren().add(ProjectManagement);
        } catch (IOException e) {
            e.printStackTrace();
        
    }}

    // دالة يتم استدعاؤها عند النقر على زر "إدارة الأقسام"
    @FXML
    private void handleDepartmentManagement() {
         try {
            // تحميل واجهة إدارة الموظفين
            AnchorPane departmentPane = FXMLLoader.load(getClass().getResource("department.fxml"));
            
            // مسح المحتوى السابق وإضافة المحتوى الجديد
            contentArea.getChildren().clear();
            contentArea.getChildren().add(departmentPane);
        } catch (IOException e) {
            e.printStackTrace();
        
    }
    }

    // دالة يتم استدعاؤها عند النقر على زر "إدارة الموظفين"
    @FXML
    private void handleEmployeeManagement() {
        try {
            // تحميل واجهة إدارة الموظفين
            AnchorPane employee = FXMLLoader.load(getClass().getResource("NewEmployees.fxml"));
           
            // مسح المحتوى السابق وإضافة المحتوى الجديد
            contentArea.getChildren().clear();
            contentArea.getChildren().add(employee);
        } catch (IOException e) {
            e.printStackTrace();
        
    }
    }
}
