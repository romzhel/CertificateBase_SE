package ui_windows.main_window.filter_window_se;

import core.CoreModule;
import core.Dialogs;
import core.Module;
import core.SharedData;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import ui_windows.options_window.families_editor.ProductFamily;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.product.data.DataItem;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.TreeSet;

import static core.SharedData.SHD_DATA_SET;
import static core.SharedData.SHD_FILTER_PARAMETERS;
import static ui_windows.main_window.filter_window.FilterParameters.*;
import static ui_windows.main_window.filter_window_se.FilterParameters_SE.ALL_FAMILIES;
import static ui_windows.main_window.filter_window_se.ItemsSelection.ALL_ITEMS;
import static ui_windows.main_window.filter_window_se.ItemsSelection.PRICE_ITEMS;

public class FilterWindowController_SE implements Initializable, Module {
    private static final String TEXT_ALL_ITEMS = "--- Все ---";
    private static final int SELECTOR_LGBK_ROWS_MAX = 10;
    private FilterParameters_SE filterParameters;

    @FXML
    private RadioButton rbPriceItems;
    @FXML
    private RadioButton rbAllItems;
    @FXML
    private ComboBox<ProductFamily> cbFamily;
    @FXML
    private ComboBox<ProductLgbk> cbLgbk;
    @FXML
    private ComboBox<ProductLgbk> cbHierarchy;
    @FXML
    private ComboBox<DataItem> cbCustomProperty;
    @FXML
    private TextField tfCustomValue;
    @FXML
    private RadioButton rbStartWith;
    @FXML
    private RadioButton rbEndWith;
    @FXML
    private RadioButton rbContains;
    @FXML
    private RadioButton rbNotContains;
    @FXML
    private RadioButton rbRegularExpression;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SHD_FILTER_PARAMETERS.subscribe(this);
        SHD_DATA_SET.subscribe(this);
        init();
    }

    private void init() {
        if (SHD_FILTER_PARAMETERS.getData() instanceof FilterParameters_SE) {
            filterParameters = SHD_FILTER_PARAMETERS.getData();

            initMainSelection();
            initFamilySelector();
            initLgbkSelector();
            initCustomSelection();
        } else {
            Dialogs.showMessageTS("Инициализация окна фильтра", "Не найдено параметров фильтра!");
        }
    }

    public void initMainSelection() {
        ToggleGroup itemsGroup = new ToggleGroup();
        rbAllItems.setToggleGroup(itemsGroup);
        rbPriceItems.setToggleGroup(itemsGroup);

        rbAllItems.setSelected(filterParameters.getFilterItems() == ALL_ITEMS);
        rbPriceItems.setSelected(filterParameters.getFilterItems() == PRICE_ITEMS);

        itemsGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                filterParameters.setItems(ItemsSelection.values()[Arrays.asList(rbAllItems, rbPriceItems).indexOf(newValue)]);
                sync();
            }
        });
    }

    public void initFamilySelector() {
        cbFamily.setOnAction(null);

        cbFamily.setConverter(new StringConverter<ProductFamily>() {
            @Override
            public String toString(ProductFamily object) {
                return /*object != null ? */object.getName() /*: TEXT_ALL_ITEMS*/;
            }

            @Override
            public ProductFamily fromString(String name) {
                return CoreModule.getProductFamilies().getFamilyByName(name);
            }
        });

        cbFamily.getItems().clear();
        cbFamily.getItems().addAll(filterParameters.getFamilies());

//        if (filterParameters.getFilterProductFamily() != null) {
            cbFamily.getSelectionModel().select(filterParameters.getFilterProductFamily());
//        } else {
//            cbFamily.getSelectionModel().select(ALL_FAMILIES);
//        }

        cbFamily.setOnAction(event -> {
            filterParameters.setProductFamily(cbFamily.getValue());
            sync();
        });
    }

    public void initLgbkSelector() {
        cbLgbk.setConverter(new StringConverter<ProductLgbk>() {
            @Override
            public String toString(ProductLgbk object) {
                return object.getLgbk().equals(FilterParameters_SE.TEXT_ALL_ITEMS) ?
                        object.getLgbk() : object.getCombineLgbkDescription();
            }

            @Override
            public ProductLgbk fromString(String string) {
                return null;
            }
        });

        cbLgbk.getItems().clear();
        cbLgbk.getItems().addAll(filterParameters.getLgbks());

        if (filterParameters.getFilterProductFamily() != null) {
            cbLgbk.getSelectionModel().select(filterParameters.getFilterProductLgbk());
        } else {
            cbLgbk.getSelectionModel().select(0);
        }

        cbLgbk.setOnAction(event -> {
            if (cbLgbk.getValue() != null) {
                filterParameters.setLgbk(cbLgbk.getValue());
                sync();
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

    private void initCustomSelection() {
        ToggleGroup customSelection = new ToggleGroup();
        RadioButton[] customSelectionItems = {rbStartWith, rbEndWith, rbContains, rbNotContains, rbRegularExpression};
        for (RadioButton radioButton : customSelectionItems) {
            radioButton.setToggleGroup(customSelection);
        }

        customSelectionItems[filterParameters.getFilterCustomCondition().ordinal()].setSelected(true);

        customSelection.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                filterParameters.setCustomCondition(CustomValueCondition.values()[
                        Arrays.asList(customSelectionItems).indexOf((RadioButton) newValue)]);
                sync();
            }
        });

        tfCustomValue.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals(oldValue)) {
                filterParameters.setCustomValue(newValue);
                sync();
            }
        });
    }

    /*public void applyFilter() {
        CoreModule.getFilter().apply();
    }*/

    public void close() {
        SHD_FILTER_PARAMETERS.unsubscribe(this);
        SHD_DATA_SET.unsubscribe(this);

        ((Stage) cbLgbk.getScene().getWindow()).close();
    }

    private void sync() {
        SHD_FILTER_PARAMETERS.setData(filterParameters, this);
    }

    @Override
    public void refreshSubscribedData(SharedData sharedData, Object data) {
        init();
    }
}
