package ui_windows.options_window.price_lists_editor.se;

import core.CoreModule;
import core.Dialogs;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ui_windows.options_window.price_lists_editor.PriceList;
import ui_windows.options_window.price_lists_editor.se.price_sheet.PriceListSheet;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class PriceListEditorWindowControllerv2 implements Initializable {
    PriceList tempPriceList;

    @FXML
    public TabPane mainTabPane;

    @FXML
    public TextField tfPriceName;

    @FXML
    public TextField tfTemplateName;

    @FXML
    public TextField tfPriceFileName;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setTemplateFile() {
        File selectedFile = new Dialogs().selectAnyFile(PriceListEditorWindow.getStage(), "Выбор файла шаблона прайс-листа",
                Dialogs.EXCEL_FILES, CoreModule.getFolders().getTemplatesFolder().getPath());
        if (selectedFile != null && selectedFile.exists()) {
            tempPriceList.setTemplate(selectedFile);
            tfTemplateName.setText(selectedFile.getName());
        }
    }

    public void actionApply() {
//        if (PriceListEditorWindow.getMode() == ADD) {
        if (tfPriceName.getText().isEmpty() || tfPriceFileName.getText().isEmpty() || tfTemplateName.getText().isEmpty()) {
            Dialogs.showMessage("Сохранение прайс-листа", "Не все поля заполнены.");
        } else {
            tempPriceList.setName(tfPriceName.getText());
            tempPriceList.setFileName(tfPriceFileName.getText());
            if (tempPriceList.getId() == -1) {
                if (CoreModule.getPriceLists().addItem(tempPriceList)) {
                    actionClose();
                }
            } else {
                if (CoreModule.getPriceLists().editItem(tempPriceList)) {
                    actionClose();
                }
            }
        }
    }

    public void actionClose() {
        mainTabPane.getSelectionModel().clearSelection(mainTabPane.getSelectionModel().getSelectedIndex());
        ((Stage) mainTabPane.getScene().getWindow()).close();
    }

    public void actionAddSheet() {
        PriceListSheet newSheet = new PriceListSheet("Лист " + mainTabPane.getTabs().size());
        tempPriceList.getSheets().add(newSheet);
        mainTabPane.getTabs().add(newSheet);
    }

    public PriceList getTempPriceList() {
        return tempPriceList;
    }

    public void setTempPriceList(PriceList tempPriceList) {
        this.tempPriceList = tempPriceList;
    }
}
