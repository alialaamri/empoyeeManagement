package university;

import java.net.URL;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableRow;

import javafx.util.Callback;

public class DepartmentController implements Initializable {

    @FXML
    private TextField departmentNameField;
    @FXML
    private TableView<Map<String, Object>> departmentTable;

    @FXML
    private TableColumn<Map<String, Object>, String> departmentNameColumn;

    @FXML
    private Label messageLabel;

    ObservableList<Map<String, Object>> departmentList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadDepartments();
        departmentNameColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map<String, Object>, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map<String, Object>, String> data) {
                return new SimpleStringProperty((String) data.getValue().get("departmentName"));
            }
        });

        departmentTable.setRowFactory(tv -> {
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

    private void handleRowClick(Map<String, Object> rowData) {
        String departmentName = (String) rowData.get("departmentName");
        departmentNameField.setText(departmentName);
    }

    @FXML
    private void handleAddDepartment() {
        String name = departmentNameField.getText();
        if (name.isEmpty()) {
            messageLabel.setText("يرجى إدخال اسم القسم.");
            return;
        }

        try {
            Connection conn = ConnectionDatabase.connectToDatabase();
            ConnectionDatabase.insertIntoTable(
                    "departments",
                    Arrays.asList("name"), // الأعمدة
                    Arrays.asList(name) // القيم
            );
            messageLabel.setText("تم إضافة القسم: " + name);
            clearTable();
            loadDepartments();
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error adding department.");
        }
    }

    @FXML
    private void handleDeleteDepartment() {
        Map<String, Object> selectedDepartment = departmentTable.getSelectionModel().getSelectedItem();
        if (selectedDepartment != null) {
            String departmentName = (String) selectedDepartment.get("departmentName");

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "هل أنت متأكد أنك تريد حذف القسم: " + departmentName + "؟");
            alert.setHeaderText(null);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    deleteDepartmentFromDatabase(departmentName);
                }
            });
        } else {
            messageLabel.setText("يرجى اختيار القسم للحذف.");
        }
    }

    private void clearTable() {
        departmentList.clear();
        departmentTable.setItems(departmentList);
    }

    @FXML
    private void deleteDepartmentFromDatabase(String departmentName) {
        try {
            Connection conn = ConnectionDatabase.connectToDatabase();
            String sql = "DELETE FROM departments WHERE name = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, departmentName);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                messageLabel.setText("تم حذف القسم بنجاح.");
            } else {
                messageLabel.setText("فشل حذف القسم. قد لا يكون موجودًا.");
            }
            clearTable();
            loadDepartments();
        } catch (SQLException e) {
            e.printStackTrace();
            messageLabel.setText("خطأ أثناء حذف القسم: " + e.getMessage());
        }
    }

    @FXML
    private void loadDepartments() {
        try {
            Connection conn = ConnectionDatabase.connectToDatabase();
            String sql = "SELECT name AS departmentName FROM departments";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("departmentName", rs.getString("departmentName"));
                departmentList.add(row);
            }
            departmentTable.setItems(departmentList);
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error loading departments.");
        }
    }

    private void clearFields() {
        departmentNameField.clear();
    }
}
