package ui_windows.options_window.requirements_types_editor;


import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class RequirementTypesTable {
    private static RequirementTypesTable instance;
    private TableView<RequirementType> tableView;

    private RequirementTypesTable() {
    }

    public static RequirementTypesTable getInstance() {
        if (instance == null) {
            instance = new RequirementTypesTable();
        }
        return instance;
    }

    public void init(TableView<RequirementType> tableView) {
        this.tableView = tableView;

        tableView.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("shortName"));
        tableView.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("fullName"));
        tableView.getItems().addAll(RequirementTypes.getInstance().getItems());
    }

    public void addItem(RequirementType requirementType) {
        tableView.getItems().add(requirementType);
    }

    public TableView<RequirementType> getTableView() {
        return tableView;
    }
}
