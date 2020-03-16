package ui_windows.main_window.filter_window;

import core.CoreModule;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import ui_windows.options_window.families_editor.ProductFamily;
import ui_windows.options_window.product_lgbk.ProductLgbk;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.TreeSet;

import static ui_windows.main_window.filter_window.FilterParameters.*;

public class FilterWindowController implements Initializable {
    private static final int SELECTOR_LGBK_ROWS_MAX = 10;

    @FXML
    public RadioButton rbPriceItems;
    @FXML
    public RadioButton rbAllItems;
    @FXML
    public CheckBox cbxOnlyChanges;
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
                return object.getCombineDescriptionLgbk();
            }

            @Override
            public ProductLgbk fromString(String string) {
                return CoreModule.getProductLgbks().getByLgbkCombinedText(string);
            }
        });

        syncLgbkSelector(FILTER_FAMILY.getValue());

        cbLgbk.setOnAction(event -> {
            if (cbLgbk.getValue() != null) {
                FILTER_LGBK.setValue(cbLgbk.getValue());
                applyFilter();
            }
        });
    }

    public void syncLgbkSelector(ProductFamily pf) {
        cbLgbk.getItems().clear();
        cbLgbk.getItems().add(FILTER_VALUE_ALL_LGBKS);
        TreeSet<ProductLgbk> lgbkGroups = new TreeSet<>((o1, o2) -> o1.getLgbk().compareTo(o2.getLgbk()));

        for (ProductLgbk pl : CoreModule.getProductLgbks().getItems()) {
            if (pf == FILTER_VALUE_ALL_FAMILIES || pl.getFamilyId() == pf.getId()) {
                lgbkGroups.add(pl);
            }
        }

        cbLgbk.getItems().addAll(lgbkGroups);
        cbLgbk.setVisibleRowCount(Math.min(cbLgbk.getItems().size() + 1, SELECTOR_LGBK_ROWS_MAX));

        ProductLgbk selectedItem = FILTER_LGBK.getValue();
        if (cbLgbk.getItems().indexOf(selectedItem) >= 0) {
            cbLgbk.getSelectionModel().select(selectedItem);
        } else {
            cbLgbk.getSelectionModel().select(0);
            FILTER_LGBK.setValue(FILTER_VALUE_ALL_LGBKS);
        }
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

        ProductFamily productFamily = FILTER_FAMILY.getValue();
        if (productFamily != null) {
            cbFamily.setValue(productFamily);
        } else {
            cbFamily.setValue(FILTER_VALUE_ALL_FAMILIES);
        }

        cbFamily.setOnAction(event -> {
            FILTER_FAMILY.setValue(cbFamily.getValue());
            syncLgbkSelector(cbFamily.getValue());
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
        ToggleGroup itemsGroup = new ToggleGroup();
        rbAllItems.setToggleGroup(itemsGroup);
        rbPriceItems.setToggleGroup(itemsGroup);

        rbAllItems.selectedProperty().addListener((observable, oldValue, newValue) -> {
            FILTER_ALL_ITEMS.setValue(newValue);
            FILTER_PRICE_ITEMS.setValue(!newValue);
            applyFilter();
        });
        /*cbxPrice.selectedProperty().addListener((observable, oldValue, newValue) -> {
            FILTER_PRICE_ITEMS.setValue(newValue);
            if (newValue) cbxAllRecords.setSelected(false);
            applyFilter();
        });*/
    }

    public void applyFilter() {
        CoreModule.getFilter().apply();
    }

    public void close() {
        ((Stage) cbLgbk.getScene().getWindow()).close();
    }
}
