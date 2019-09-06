package ui_windows.options_window.requirements_types_editor;

import core.CoreModule;
import core.Dialogs;
import database.RequirementTypesDB;
import javafx.application.Platform;
import javafx.scene.layout.AnchorPane;
import utils.Utils;

import static ui_windows.Mode.*;

public class RequirementTypeEditorWindowActions {

    public static void apply() {
        AnchorPane root = RequirementTypeEditorWindow.getRootAnchorPane();

        if (RequirementTypeEditorWindow.getMode() == ADD) {//adding new record

            if (Utils.hasEmptyControls(root)) {//empty fields
                Dialogs.showMessage("Пустые поля", "Не все поля заполнены");
            } else {//save to DB
                RequirementType ct = new RequirementType(0, Utils.getControlValue(root, "tfShortName"),
                        Utils.getControlValue(root, "taFullName"));

                if (!CoreModule.getRequirementTypes().hasDoubles(ct)) {//check duplicates
                    if (new RequirementTypesDB().putData(ct)) {//write to DB
                        CoreModule.getRequirementTypes().getItems().add(ct);//add item to arrayList
                        CoreModule.getRequirementTypesTable().addItem(ct);//add record to table
                        close();
                    }
                }
            }

        } else if (RequirementTypeEditorWindow.getMode() == EDIT) {//editing existing record

            RequirementType ct = getItem();
            if (ct != null) {

                ct.setShortName(Utils.getControlValue(root, "tfShortName"));
                ct.setFullName(Utils.getControlValue(root, "taFullName"));

                if (!CoreModule.getRequirementTypes().hasDoubles(ct)) {//check duplicates
                    if (new RequirementTypesDB().updateData(ct)) {//write to DB
                        Platform.runLater(() -> CoreModule.getRequirementTypesTable().getTableView().refresh());//refresh table
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
            if (!CoreModule.getCertificates().isNormUsed(ct.getId())) {
                if (Dialogs.confirm("Удаление записи", "Действительно желаете удалить запись?")) {
                    if (new RequirementTypesDB().deleteData(ct)) CoreModule.getRequirementTypes().remove(ct);
                }
            }

        }
    }

    private static RequirementType getItem() {
        int index = CoreModule.getRequirementTypesTable().getTableView().getSelectionModel().getSelectedIndex();

        if (index > -1 && index < CoreModule.getRequirementTypesTable().getTableView().getItems().size()) {
            return CoreModule.getRequirementTypesTable().getTableView().getItems().get(index);
        } else return null;
    }
}
