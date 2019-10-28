package ui_windows.main_window.filter_window;

import core.CoreModule;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import ui_windows.options_window.families_editor.ProductFamily;
import ui_windows.options_window.product_lgbk.ProductLgbk;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.TreeSet;

import static ui_windows.main_window.filter_window.Filter.*;

public class FilterWindowController implements Initializable {
    public static final String ALL_RECORDS = "--- Все ---";
    private static final int SELECTOR_LGBK_ROWS_MAX = 10;

    @FXML
    public CheckBox cbxPrice;
    @FXML
    public CheckBox cbxArchive;
    @FXML
    public CheckBox cbxNotUsed;
    @FXML
    public CheckBox cbxOnlyChanges;
    @FXML
    public CheckBox cbxAllRecords;
    @FXML
    public ComboBox<ProductFamily> cbFamily;
    @FXML
    public ComboBox<String> cbChangeType;
    @FXML
    public Label lChangeType;
    @FXML
    public ComboBox<ProductLgbk> cbLgbk;
    @FXML
    public ComboBox<String> cbHier;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initMainSelection();
        initFamilySelector();
        initChangeSelectors();
        initLgbkSelector();
    }

    public void initLgbkSelector() {
        cbLgbk.setConverter(new StringConverter<ProductLgbk>() {
            @Override
            public String toString(ProductLgbk object) {
                return object.getCombineDescription();
            }

            @Override
            public ProductLgbk fromString(String string) {
                return CoreModule.getProductLgbks().getByLgbkCombinedText(string);
            }
        });

        CoreModule.getFilter().setTableRenewedListener(lgbks -> {
            cbLgbk.getItems().clear();
            cbLgbk.getItems().add(FILTER_VALUE_ALL_LGBKS);
            cbLgbk.getItems().addAll(lgbks);
            cbLgbk.setVisibleRowCount(Math.min(cbLgbk.getItems().size() + 1, SELECTOR_LGBK_ROWS_MAX));

            cbLgbk.setOnAction(null);
            ProductLgbk selectedItem = CoreModule.getFilter().getLgbk();
            if (cbLgbk.getItems().indexOf(selectedItem) >= 0) {
                cbLgbk.getSelectionModel().select(selectedItem);
            } else if (!CoreModule.getFilter().getLgbk().equals(FILTER_VALUE_ALL_LGBKS)) {
                cbLgbk.getSelectionModel().select(0);
                CoreModule.getFilter().setLgbk(FILTER_VALUE_ALL_LGBKS);
                applyFilter();
            }

            cbLgbk.setOnAction(event -> {
                if (cbLgbk.getValue() != null) {
                    CoreModule.getFilter().setLgbk(cbLgbk.getValue());
                    applyFilter();
                }
            });

        });
    }

    public void initFamilySelector() {
        cbFamily.setConverter(new StringConverter<ProductFamily>() {
            @Override
            public String toString(ProductFamily object) {
                return object.getName();
            }

            @Override
            public ProductFamily fromString(String string) {
                return null;
            }
        });

        cbFamily.getItems().add(FILTER_VALUE_ALL_FAMILIES);

        TreeSet<ProductFamily> families = new TreeSet<>((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
        families.addAll(CoreModule.getProductFamilies().getItems());
        cbFamily.getItems().addAll(families);

        if (CoreModule.getFilter().getProductFamily() != null) {
            cbFamily.setValue(CoreModule.getFilter().getProductFamily());
        } else {
            cbFamily.setValue(FILTER_VALUE_ALL_FAMILIES);
        }

        cbFamily.setOnAction(event -> {
            CoreModule.getFilter().setProductFamily(cbFamily.getValue());
            applyFilter();
        });
    }

    public void initChangeSelectors() {
        /*cbxOnlyChanges.selectedProperty().addListener((observable, oldValue, newValue) -> {
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
        });*/
    }

    public void initMainSelection() {
        cbxAllRecords.selectedProperty().addListener((observable, oldValue, newValue) -> {
            FILTER_ALL_ITEMS.setValue(newValue);
            if (newValue) {
                cbxPrice.setSelected(false);
//                cbxArchive.setSelected(false);
//                cbxNotUsed.setSelected(false);
//                cbxOnlyChanges.setSelected(false);
            }
            applyFilter();
        });
        cbxPrice.selectedProperty().addListener((observable, oldValue, newValue) -> {
            FILTER_PRICE_ITEMS.setValue(newValue);
            if (newValue) cbxAllRecords.setSelected(false);
            applyFilter();
        });
        /*cbxArchive.selectedProperty().addListener((observable, oldValue, newValue) -> {
            CoreModule.getFilter().getFilterSimpleByUIname("cbxArchive").setValue(newValue);
            if (newValue) cbxAllRecords.setSelected(false);
            applyFilter();
        });
        cbxNotUsed.selectedProperty().addListener((observable, oldValue, newValue) -> {
            CoreModule.getFilter().getFilterSimpleByUIname("cbxNotUsed").setValue(newValue);
            if (newValue) cbxAllRecords.setSelected(false);
            applyFilter();
        });*/
    }


    public void applyFilter() {
        System.out.println("applying filter");
        CoreModule.getFilter().apply();
    }

    public void close() {
        CoreModule.getFilter().setTableRenewedListener(null);
        ((Stage) cbLgbk.getScene().getWindow()).close();
    }


}
