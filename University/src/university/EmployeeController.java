package university;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.sql.*;
import java.util.Arrays;
import javafx.scene.control.ComboBox;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableRow;
import javafx.util.Callback;

public class EmployeeController implements Initializable {

    @FXML
    private Label messageLabel;
    @FXML
    private TextField employeeIdField;
    @FXML
    private TextField employeeNameField;
    @FXML
    private TextField positionField;
    @FXML
    private ComboBox<String> departmentComboBox;
    @FXML
    private TableView<Map<String, Object>> employeesTable;
    @FXML
    private TableColumn<Map<String, Object>, String> employeeIdColumn;
    @FXML
    private TableColumn<Map<String, Object>, String> departmentNameColumn;
    @FXML
    private TableColumn<Map<String, Object>, String> departmentIdColumn;
    @FXML
    private TableColumn<Map<String, Object>, String> positionColumn;
    @FXML
    private TableColumn<Map<String, Object>, String> employeeNameColumn;

    private ObservableList<Map<String, Object>> employeeList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadDepartments();
        loadEmployees();

        employeeIdColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map<String, Object>, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map<String, Object>, String> data) {
                return new SimpleStringProperty((String) data.getValue().get("employeeId"));
            }
        });
        departmentNameColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map<String, Object>, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map<String, Object>, String> data) {
                return new SimpleStringProperty((String) data.getValue().get("departmentName"));
            }
        });
        positionColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map<String, Object>, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map<String, Object>, String> data) {
                return new SimpleStringProperty((String) data.getValue().get("position"));
            }
        });
        departmentIdColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map<String, Object>, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map<String, Object>, String> data) {
                return new SimpleStringProperty((String) data.getValue().get("departmentId"));
            }
        });
        employeeNameColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map<String, Object>, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map<String, Object>, String> data) {
                return new SimpleStringProperty((String) data.getValue().get("employeeName"));
            }
        });

        employeesTable.setRowFactory(tv -> {
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
    public void loadEmployees() {
        Connection conn = ConnectionDatabase.connectToDatabase();
        String sql = "SELECT emp.id AS employeeId, emp.name AS employeeName, emp.position AS position, emp.department_id AS departmentId, dept.name AS departmentName "
                + "FROM employees emp INNER JOIN departments dept "
                + "ON dept.id = emp.department_id";

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("employeeId", rs.getString("employeeId"));
                row.put("employeeName", rs.getString("employeeName"));
                row.put("position", rs.getString("position"));
                row.put("departmentId", rs.getString("departmentId"));
                row.put("departmentName", rs.getString("departmentName"));
                employeeList.add(row);
            }
            employeesTable.setItems(employeeList);
        } catch (SQLException e) {
            messageLabel.setText(e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddEmployee() {
        String name = employeeNameField.getText().trim();
        String position = positionField.getText().trim();
        String selectedText = departmentComboBox.getValue(); // الحصول على النص المختار من الـ ComboBox

        // التحقق من صحة الإدخال
        if (name.isEmpty() || position.isEmpty() || selectedText == null || !selectedText.contains(":")) {
            messageLabel.setText("يرجى إدخال اسم الموظف والمسمى الوظيفي واختيار قسم.");
            return; // إيقاف العملية إذا كان أحد الحقول فارغًا أو غير صحيح
        }

        int deptID = 0;
        // تقسيم النص للحصول على الـ ID والاسم
        String[] parts = selectedText.split(": ");
        if (parts.length > 0) {
            try {
                deptID = Integer.parseInt(parts[0]); // تحويل الجزء الأول إلى int وهو الـ ID
            } catch (NumberFormatException e) {
                messageLabel.setText("خطأ في تحديد القسم. يرجى اختيار قسم صحيح.");
                return; // إيقاف العملية إذا كان هناك خطأ في تحويل الـ ID
            }
        }

        // إضافة الموظف إلى قاعدة البيانات
        ConnectionDatabase.insertIntoTable(
                "employees",
                Arrays.asList("name", "position", "department_id"), // الأعمدة
                Arrays.asList(name, position, deptID) // القيم
        );

        messageLabel.setText("تم إضافة الموظف بنجاح: " + name);
        clearTable();
        loadEmployees();
    }

    @FXML
    private void loadDepartments() {
        Connection conn = ConnectionDatabase.connectToDatabase();
        String sql = "SELECT id, name FROM departments"; // Ensure this matches your database table
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String departmentName = rs.getString("name");
                int departmentId = rs.getInt("id");

                // Add the employee ID and name as a formatted string
                departmentComboBox.getItems().add(departmentId + ": " + departmentName); // Format: "ID: Name"
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearTable() {
        employeeList.clear(); // مسح جميع العناصر من القائمة
        employeesTable.setItems(employeeList); // تحديث الجدول
    }

    private void handleRowClick(Map<String, Object> rowData) {
        String employeeName = (String) rowData.get("employeeName");
        String position = (String) rowData.get("position");
        String departmentId = (String) rowData.get("departmentId");
        String departmentName = (String) rowData.get("departmentName");
        employeeNameField.setText(employeeName);
        positionField.setText(position);
        departmentComboBox.setValue(departmentId + ": " + departmentName);
    }

    private void deleteEmployeeFromDatabase(String employeeId) {
        Connection conn = ConnectionDatabase.connectToDatabase();
        String sql = "DELETE FROM employees WHERE id = ?"; // Adjust the table name and field

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, employeeId);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                messageLabel.setText("تم حذف الموظف بنجاح.");
            } else {
                messageLabel.setText("فشل حذف الموظف. قد لا يكون موجودًا.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            messageLabel.setText("خطأ أثناء حذف الموظف: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteEmployee() {
        Map<String, Object> selectedEmployee = employeesTable.getSelectionModel().getSelectedItem();
        if (selectedEmployee != null) {
            String employeeId = (String) selectedEmployee.get("employeeId"); // استخدام معرف الموظف

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "هل أنت متأكد أنك تريد حذف الموظف: " + employeeId + "؟");
            alert.setHeaderText(null);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    deleteEmployeeFromDatabase(employeeId);
                    clearTable();
                    loadEmployees(); // Refresh the table after deletion
                }
            });
        } else {
            messageLabel.setText("يرجى اختيار موظف للحذف.");
        }
    }

    @FXML
    private void handleUpdateEmployee() {
        Map<String, Object> selectedEmployee = employeesTable.getSelectionModel().getSelectedItem();
        if (selectedEmployee != null) {
            String employeeId = (String) selectedEmployee.get("employeeId");
            String name = employeeNameField.getText().trim();
            String position = positionField.getText().trim();
            String selectedText = departmentComboBox.getValue();

            // التحقق من صحة الإدخال
            if (name.isEmpty() || position.isEmpty() || selectedText == null || !selectedText.contains(":")) {
                messageLabel.setText("يرجى إدخال اسم الموظف والمسمى الوظيفي واختيار قسم.");
                return; // إيقاف العملية إذا كان أحد الحقول فارغًا أو غير صحيح
            }

            int deptID = 0;
            // تقسيم النص للحصول على الـ ID والاسم
            String[] parts = selectedText.split(": ");
            if (parts.length > 0) {
                try {
                    deptID = Integer.parseInt(parts[0]);
                } catch (NumberFormatException e) {
                    messageLabel.setText("خطأ في تحديد القسم. يرجى اختيار قسم صحيح.");
                    return;
                }
            }

            Connection conn = ConnectionDatabase.connectToDatabase();
            String sql = "UPDATE employees SET name = ?, position = ?, department_id = ? WHERE id = ?";
            try {
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, name);
                pstmt.setString(2, position);
                pstmt.setInt(3, deptID);
                pstmt.setString(4, employeeId);
                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    messageLabel.setText("تم تحديث الموظف بنجاح.");
                    clearTable();
                    loadEmployees();
                } else {
                    messageLabel.setText("فشل تحديث الموظف. يرجى المحاولة مرة أخرى.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                messageLabel.setText("خطأ أثناء تحديث الموظف: " + e.getMessage());
            }
        } else {
            messageLabel.setText("يرجى اختيار موظف للتحديث.");
        }
    }
}
