package ui_windows.options_window.requirements_types_editor;


import core.CoreModule;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class RequirementTypesTable {
    private TableView<RequirementType> tableView;

    public RequirementTypesTable(TableView<RequirementType> tableView) {
        this.tableView = tableView;

        tableView.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("shortName"));
        tableView.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("fullName"));
        tableView.getItems().addAll(CoreModule.getRequirementTypes().getItems());
    }

    public void addItem(RequirementType requirementType) {
        tableView.getItems().add(requirementType);
    }

    public TableView<RequirementType> getTableView() {
        return tableView;
    }
}
