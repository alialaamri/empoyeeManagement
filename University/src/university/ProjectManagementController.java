package university;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.sql.*;
import java.util.Arrays;
import javafx.scene.control.ComboBox;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableRow;

public class ProjectManagementController implements Initializable {

    @FXML
    private TextField projectNameField;

    @FXML
    private Button addButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Label messageLabel;
    @FXML
    private ComboBox<String> employeeNameComboBox;

    @FXML
    private TableView<Map<String, Object>> projectsTable;

    @FXML
    private TableColumn<Map<String, Object>, String> projectNameColumn;

    @FXML
    private TableColumn<Map<String, Object>, String> employeeNameColumn;

    @FXML
    private TableColumn<Map<String, Object>, String> employeesIdColumn;

    private ObservableList<Map<String, Object>> projectList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadEmployeeNames();
        loadProjects();

        projectNameColumn.setCellValueFactory(data -> new SimpleStringProperty((String) data.getValue().get("projectName")));
        employeeNameColumn.setCellValueFactory(data -> new SimpleStringProperty((String) data.getValue().get("employeeName")));
        employeesIdColumn.setCellValueFactory(data -> new SimpleStringProperty((String) data.getValue().get("employeesId")));

        projectsTable.setRowFactory(tv -> {
            TableRow<Map<String, Object>> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    Map<String, Object> rowData = row.getItem();
                    handleRowClick(rowData);
                }
            });
            return row;
        });
    }

    @FXML
    private void handleAddProject() {
        Connection conn = ConnectionDatabase.connectToDatabase();
        String name = projectNameField.getText();
        String selectedText = employeeNameComboBox.getValue();

        // تحقق من أن المستخدم قد أدخل اسم المشروع
        if (name.isEmpty()) {
            messageLabel.setText("يرجى إدخال اسم المشروع.");
            return;
        }

        // تحقق من أن المستخدم قد اختار موظفًا
        if (selectedText == null || !selectedText.contains(":")) {
            messageLabel.setText("يرجى اختيار موظف للمشروع.");
            return;
        }

        // استخراج معرف الموظف من النص المختار
        String[] parts = selectedText.split(": ");
        int employeeId = Integer.parseInt(parts[0]);

        // إدخال المشروع في قاعدة البيانات
        ConnectionDatabase.insertIntoTable(
                "projects",
                Arrays.asList("name", "employee_id"),
                Arrays.asList(name, employeeId)
        );

        // تحديث الرسالة وتنظيف الحقول
        messageLabel.setText("تم إضافة المشروع: " + name);
        clearTable();
        loadProjects();
        projectNameField.clear();
        employeeNameComboBox.setValue(null); // إعادة ضبط الـ ComboBox
    }

    @FXML
    private void loadEmployeeNames() {
        Connection conn = ConnectionDatabase.connectToDatabase();
        String sql = "SELECT id, name FROM employees";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String employeeName = rs.getString("name");
                int employeeId = rs.getInt("id");
                employeeNameComboBox.getItems().add(employeeId + ": " + employeeName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void loadProjects() {
        Connection conn = ConnectionDatabase.connectToDatabase();
        String sql = "SELECT projects.name AS projectName, employees.name AS employeeName, employees.id AS employeesId FROM projects JOIN employees ON projects.employee_id = employees.id";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String employeeName = rs.getString("employeeName");
                String projectName = rs.getString("projectName");
                String employeesId = rs.getString("employeesId");

                Map<String, Object> row = new HashMap<>();
                row.put("projectName", projectName);
                row.put("employeeName", employeeName);
                row.put("employeesId", employeesId);

                projectList.add(row);
            }
            projectsTable.setItems(projectList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearTable() {
        projectList.clear();
        projectsTable.setItems(projectList);
    }

    private void handleRowClick(Map<String, Object> rowData) {
        String projectName = (String) rowData.get("projectName");
        String employeeName = (String) rowData.get("employeeName");
        String employeesId = (String) rowData.get("employeesId");
        projectNameField.setText(projectName);
        employeeNameComboBox.setValue(employeesId + ": " + employeeName);
    }

    @FXML
    private void handleDeleteProject() {
        Map<String, Object> selectedProject = projectsTable.getSelectionModel().getSelectedItem();
        if (selectedProject != null) {
            String projectName = (String) selectedProject.get("projectName");
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "هل أنت متأكد أنك تريد حذف المشروع: " + projectName + "؟");
            alert.setHeaderText(null);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    deleteProjectFromDatabase(projectName);
                    clearTable();
                    loadProjects();
                }
            });
        } else {
            messageLabel.setText("يرجى اختيار مشروع للحذف.");
        }
    }

    private void deleteProjectFromDatabase(String projectName) {
        Connection conn = ConnectionDatabase.connectToDatabase();
        String sql = "DELETE FROM projects WHERE name = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, projectName);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                messageLabel.setText("تم حذف المشروع بنجاح.");
            } else {
                messageLabel.setText("فشل حذف المشروع. قد لا يكون موجودًا.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            messageLabel.setText("خطأ أثناء حذف المشروع: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdateProject() {
        Map<String, Object> selectedProject = projectsTable.getSelectionModel().getSelectedItem();
        if (selectedProject != null) {
            String projectName = projectNameField.getText();
            String selectedText = employeeNameComboBox.getValue();
            if (selectedText != null && selectedText.contains(":")) {
                String[] parts = selectedText.split(": ");
                int selectedEmployeeId = Integer.parseInt(parts[0]);
                int employeeId = selectedEmployeeId;

                String oldProjectName = (String) selectedProject.get("projectName");

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "هل أنت متأكد أنك تريد تعديل المشروع: " + oldProjectName + " إلى " + projectName + "؟");
                alert.setHeaderText(null);
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        updateProjectInDatabase(employeeId, projectName, oldProjectName);
                        clearTable();
                        loadProjects();
                    }
                });
            }
        } else {
            messageLabel.setText("يرجى اختيار مشروع لتعديله.");
        }
    }

    private void updateProjectInDatabase(int employeesId, String projectName, String oldProjectName) {
        Connection conn = ConnectionDatabase.connectToDatabase();
        String sql = "UPDATE projects SET name = ?, employee_id = ? WHERE name = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, projectName);
            pstmt.setInt(2, employeesId);
            pstmt.setString(3, oldProjectName);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                messageLabel.setText("تم تعديل المشروع بنجاح.");
            } else {
                messageLabel.setText("فشل تعديل المشروع. قد يكون المشروع غير موجود.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            messageLabel.setText("خطأ أثناء تعديل المشروع: " + e.getMessage());
        }
    }
}
