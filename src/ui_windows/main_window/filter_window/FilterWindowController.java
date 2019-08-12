package ui_windows.main_window.filter_window;

import core.CoreModule;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import ui_windows.main_window.MainWindow;
import utils.Utils;

import java.net.URL;
import java.util.ResourceBundle;

public class FilterWindowController implements Initializable {

    @FXML
    CheckBox cbxPrice;

    @FXML
    CheckBox cbxArchive;

    @FXML
    CheckBox cbxNotUsed;

    @FXML
    CheckBox cbxOnlyChanges;

    @FXML
    CheckBox cbxAllRecords;

    @FXML
    ComboBox<String> cbFamily;

    @FXML
    ComboBox<String> cbChangeType;

    @FXML
    Label lChangeType;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbxAllRecords.selectedProperty().addListener((observable, oldValue, newValue) -> {
            CoreModule.getFilter().getFilterSimpleByUIname("cbxAllRecords").setValue(newValue);
            if (newValue) {
                cbxPrice.setSelected(false);
                cbxArchive.setSelected(false);
                cbxNotUsed.setSelected(false);
//                cbxOnlyChanges.setSelected(false);
            }
            applyFilter();
        });
        cbxPrice.selectedProperty().addListener((observable, oldValue, newValue) -> {
            CoreModule.getFilter().getFilterSimpleByUIname("cbxPrice").setValue(newValue);
            if (newValue) cbxAllRecords.setSelected(false);
            applyFilter();
        });
        cbxArchive.selectedProperty().addListener((observable, oldValue, newValue) -> {
            CoreModule.getFilter().getFilterSimpleByUIname("cbxArchive").setValue(newValue);
            if (newValue) cbxAllRecords.setSelected(false);
            applyFilter();
        });
        cbxNotUsed.selectedProperty().addListener((observable, oldValue, newValue) -> {
            CoreModule.getFilter().getFilterSimpleByUIname("cbxNotUsed").setValue(newValue);
            if (newValue) cbxAllRecords.setSelected(false);
            applyFilter();
        });
        cbxOnlyChanges.selectedProperty().addListener((observable, oldValue, newValue) -> {
            CoreModule.getFilter().getFilterSimpleByUIname("cbxNeedAction").setValue(newValue);
            if (newValue) {
//                cbxAllRecords.setSelected(false);
                lChangeType.setDisable(false);
                cbChangeType.setDisable(false);
            } else {
                lChangeType.setDisable(true);
                cbChangeType.setDisable(true);
                cbChangeType.setValue("--- Любое ---");
            }
            applyFilter();
        });

        cbFamily.getItems().add("--- Все ---");
        cbFamily.getItems().addAll(CoreModule.getProductFamilies().getFamiliesNames());

        if (CoreModule.getFilter().getProductFamily() != null) {
            cbFamily.setValue(CoreModule.getFilter().getProductFamily().getName());
        } else {
            cbFamily.setValue("--- Все ---");
        }

        cbFamily.setOnAction(event -> {
            CoreModule.getFilter().setProductFamily(CoreModule.getProductFamilies().getFamilyByName(cbFamily.getValue()));
            applyFilter();
        });

        cbChangeType.getItems().addAll(CoreModule.getFilter().getChangeTexts());
        cbChangeType.setValue(CoreModule.getFilter().getChangeText());

        cbChangeType.setOnAction(event -> {
            CoreModule.getFilter().setChangeCode(cbChangeType.getValue());
            applyFilter();
        });


    }


    public void applyFilter() {
        CoreModule.filter();
    }

    public void close() {
        FilterWindow.getStage().close();
    }


}
