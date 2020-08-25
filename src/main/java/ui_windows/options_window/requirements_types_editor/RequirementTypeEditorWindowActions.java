package ui_windows.options_window.requirements_types_editor;

import database.RequirementTypesDB;
import javafx.application.Platform;
import javafx.scene.layout.AnchorPane;
import ui.Dialogs;
import ui_windows.options_window.certificates_editor.Certificates;
import utils.Utils;

import static ui_windows.Mode.ADD;
import static ui_windows.Mode.EDIT;

public class RequirementTypeEditorWindowActions {

    public static void apply() {
        AnchorPane root = RequirementTypeEditorWindow.getRootAnchorPane();

        if (RequirementTypeEditorWindow.getMode() == ADD) {//adding new record

            if (Utils.hasEmptyControls(root)) {//empty fields
                Dialogs.showMessage("Пустые поля", "Не все поля заполнены");
            } else {//save to DB
                RequirementType ct = new RequirementType(0, Utils.getControlValue(root, "tfShortName"),
                        Utils.getControlValue(root, "taFullName"));

                if (!RequirementTypes.getInstance().hasDoubles(ct)) {//check duplicates
                    if (new RequirementTypesDB().putData(ct)) {//write to DB
                        RequirementTypes.getInstance().getItems().add(ct);//add item to arrayList
                        RequirementTypesTable.getInstance().addItem(ct);//add record to table
                        close();
                    }
                }
            }

        } else if (RequirementTypeEditorWindow.getMode() == EDIT) {//editing existing record

            RequirementType ct = getItem();
            if (ct != null) {

                ct.setShortName(Utils.getControlValue(root, "tfShortName"));
                ct.setFullName(Utils.getControlValue(root, "taFullName"));

                if (!RequirementTypes.getInstance().hasDoubles(ct)) {//check duplicates
                    if (new RequirementTypesDB().updateData(ct)) {//write to DB
                        Platform.runLater(() -> RequirementTypesTable.getInstance().getTableView().refresh());//refresh table
                        close();
                    }
                }
            }
        }
    }

    public static void close() {
        RequirementTypeEditorWindow.close();
    }

    public static void selectFolder() {
       /* String folderName = Dialogs.selectFolder(RequirementTypeEditorWindow.getStage());
        if (folderName != null) {
            Utils.setControlValue((AnchorPane) RequirementTypeEditorWindow.getStage().getScene().getRoot(),
                    "tfFolderName", folderName);
        }*/
    }

    public static void displayData() {
        RequirementType ct = getItem();
        if (ct != null) {
            Utils.setControlValue((AnchorPane) RequirementTypeEditorWindow.getStage().getScene().getRoot(),
                    "tfShortName", ct.getShortName());
            Utils.setControlValue((AnchorPane) RequirementTypeEditorWindow.getStage().getScene().getRoot(),
                    "taFullName", ct.getFullName());
        }
    }

    public static void deleteData() {
        RequirementType ct = getItem();
        if (ct != null) {
            if (!Certificates.getInstance().isNormUsed(ct.getId())) {
                if (Dialogs.confirm("Удаление записи", "Действительно желаете удалить запись?")) {
                    if (new RequirementTypesDB().deleteData(ct)) RequirementTypes.getInstance().remove(ct);
                }
            }

        }
    }

    private static RequirementType getItem() {
        int index = RequirementTypesTable.getInstance().getTableView().getSelectionModel().getSelectedIndex();

        if (index > -1 && index < RequirementTypesTable.getInstance().getTableView().getItems().size()) {
            return RequirementTypesTable.getInstance().getTableView().getItems().get(index);
        } else return null;
    }
}
