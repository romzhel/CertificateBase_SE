package ui_windows.main_window.filter_window;

import core.CoreModule;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import ui_windows.product.Product;

import java.net.URL;
import java.util.ResourceBundle;

public class FilterWindowController implements Initializable {
    public static final String ALL_RECORDS = "--- Все ---";
    private String lastLgbk;
    private ListChangeListener<Product> changeListener;

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

    @FXML
    ComboBox<String> cbLgbk;

    @FXML
    ComboBox<String> cbHier;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initMainSelection();
        initFamilySelector();
        initChangeSelectors();


        initLgbkSelector();


    }

    public void initLgbkSelector() {
        cbLgbk.getItems().clear();
        cbLgbk.getItems().add(ALL_RECORDS);
        cbLgbk.getItems().addAll(CoreModule.getProductLgbks().getLgbkNamesFromTable(CoreModule.getProducts().getItems()));
        cbLgbk.setValue(ALL_RECORDS);

        cbLgbk.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
//                if (cbLgbk.getValue() != null) {
                    if (cbLgbk.getValue().equals(ALL_RECORDS)) CoreModule.getFilter().setLgbk(cbLgbk.getValue());
                    else CoreModule.getFilter().setLgbk(cbLgbk.getValue().split("\\]")[0].replaceAll("[\\[\\s]", ""));
                    applyFilter();
//                }
            }
        });
    }

    public void initFamilySelector() {
        cbFamily.getItems().add(ALL_RECORDS);
        cbFamily.getItems().addAll(CoreModule.getProductFamilies().getFamiliesNames());

        if (CoreModule.getFilter().getProductFamily() != null) {
            cbFamily.setValue(CoreModule.getFilter().getProductFamily().getName());
        } else {
            cbFamily.setValue(ALL_RECORDS);
        }

        cbFamily.setOnAction(event -> {
            CoreModule.getFilter().setProductFamily(CoreModule.getProductFamilies().getFamilyByName(cbFamily.getValue()));
            applyFilter();
        });
    }

    public void initChangeSelectors() {
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

        cbChangeType.getItems().addAll(CoreModule.getFilter().getChangeTexts());
        cbChangeType.setValue(CoreModule.getFilter().getChangeText());

        cbChangeType.setOnAction(event -> {
            CoreModule.getFilter().setChangeCode(cbChangeType.getValue());
            applyFilter();
        });
    }

    public void initMainSelection() {
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
    }


    public void applyFilter() {
        CoreModule.filter();
    }

    public void close() {
        FilterWindow.getStage().close();
    }


}
