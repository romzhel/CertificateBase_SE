package ui_windows.options_window.price_lists_editor.se;

import core.CoreModule;
import core.Dialogs;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ui_windows.main_window.MainWindow;
import ui_windows.options_window.price_lists_editor.PriceList;
import ui_windows.options_window.price_lists_editor.se.price_sheet.PriceListSheet;

import java.io.File;

public class PriceListEditorWindowControllerv2 {
    PriceList tempPriceList;
    @FXML
    public TabPane mainTabPane;
    @FXML
    public TextField tfPriceName;
    @FXML
    public TextField tfTemplateName;
    @FXML
    public TextField tfPriceFileName;
    @FXML
    public TextField tfDestinationFolder;
    @FXML
    public Button btnApply;

    public void setTemplateFile() {
        File selectedFile = new Dialogs().selectAnyFile(PriceListEditorWindow.getStage(), "Выбор файла шаблона прайс-листа",
                Dialogs.EXCEL_FILES, CoreModule.getFolders().getTemplatesFolder().getPath()).get(0);
        if (selectedFile != null && selectedFile.exists()) {
            tempPriceList.setTemplate(selectedFile);
            tfTemplateName.setText(selectedFile.getName());
        }
    }

    public void setDestinationFile() {
        File selectedFolder = new Dialogs().selectFolder(PriceListEditorWindow.getStage(),
                "Выбор папки для сохранения прайс-листов");
        if (selectedFolder != null) {
            tempPriceList.setDestination(selectedFolder);
            tfDestinationFolder.setText(selectedFolder.getPath());
        }
    }

    public void actionApply() {
        if (checkUiFilling()) {
            tempPriceList.setName(tfPriceName.getText());
            tempPriceList.setFileName(tfPriceFileName.getText());
            if (tempPriceList.getId() == -1) {
                if (CoreModule.getPriceLists().addItem(tempPriceList)) {
                    MainWindow.getController().initPriceListMenu();
                    actionClose();
                }
            } else {
                if (CoreModule.getPriceLists().editItem(tempPriceList)) {
                    actionClose();
                }
            }
        }
    }

    private boolean checkUiFilling() {
        if (tfPriceName.getText().isEmpty() || tfPriceFileName.getText().isEmpty() || tfTemplateName.getText().isEmpty() ||
                tfDestinationFolder.getText().isEmpty()) {
            Dialogs.showMessage("Сохранение прайс-листа", "Не все поля заполнены.");
            return false;
        }

        for (PriceListSheet priceListSheet : tempPriceList.getSheets()) {
            String discountS = priceListSheet.getController().tfDiscount.getText();
            if (discountS.matches("^\\d+$") && Integer.parseInt(discountS) > 30) {
                Dialogs.showMessage("Сохранение прайс-листа", "Неправильно указана скидка в листе " +
                        (tempPriceList.getSheets().indexOf(priceListSheet) + 1));
                return false;
            }
        }

        return true;
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
