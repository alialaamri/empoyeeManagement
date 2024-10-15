
package university.model;
import javafx.beans.property.SimpleStringProperty;

public class Department {
    private final SimpleStringProperty departmentName;

    public Department(String departmentName) {
        this.departmentName = new SimpleStringProperty(departmentName);
    }

    public String getDepartmentName() {
        return departmentName.get();
    }

    public SimpleStringProperty departmentNameProperty() {
        return departmentName;
    }
}
