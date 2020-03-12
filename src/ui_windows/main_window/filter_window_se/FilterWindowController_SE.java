package ui_windows.main_window.filter_window_se;

import core.CoreModule;
import core.Dialogs;
import core.Module;
import core.SharedData;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import ui_windows.options_window.families_editor.ProductFamily;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.product.data.DataItem;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import static core.SharedData.SHD_DATA_SET;
import static core.SharedData.SHD_FILTER_PARAMETERS;
import static ui_windows.main_window.filter_window_se.FilterParameters_SE.*;
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
            initHierarchySelector();
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
                return object.getName();
            }

            @Override
            public ProductFamily fromString(String name) {
                return CoreModule.getProductFamilies().getFamilyByName(name);
            }
        });

        if (cbFamily.getItems().size() < 2 || filterParameters.getLastChange() < CHANGE_FAMILY ||
                filterParameters.getLastChange() == CHANGE_FAMILY && filterParameters.getFamily() == ALL_FAMILIES) {

            cbFamily.getItems().clear();
            cbFamily.getItems().addAll(filterParameters.getFamilies());
        }

        cbFamily.getSelectionModel().select(filterParameters.getFamily());

        cbFamily.setOnAction(event -> {
            filterParameters.setProductFamily(cbFamily.getValue());
            sync();
        });

        cbFamily.setVisibleRowCount(Math.min(cbFamily.getItems().size(), 10));
    }

    public void initLgbkSelector() {
        cbLgbk.setOnAction(null);

        cbLgbk.setConverter(new StringConverter<ProductLgbk>() {
            @Override
            public String toString(ProductLgbk object) {
                if (object.equals(ALL_LGBKS) || object.equals(LGBK_NO_DATA)) {
                    return object.getLgbk();
                }

//                ProductLgbk pl = CoreModule.getProductLgbks().getGroupLgbkByName(object);
                return object.getCombineDescription();
            }

            @Override
            public ProductLgbk fromString(String string) {
                return null;
            }
        });

        if (cbLgbk.getItems().size() < 2 || filterParameters.getLastChange() < CHANGE_LGBK ||
                filterParameters.getLastChange() == CHANGE_LGBK && filterParameters.getLgbk().equals(TEXT_ALL_ITEMS)) {

            cbLgbk.getItems().clear();
            cbLgbk.getItems().addAll(filterParameters.getLgbks());
        }

        cbLgbk.getSelectionModel().select(filterParameters.getLgbk());

        cbLgbk.setOnAction(event -> {
            if (cbLgbk.getValue() != null) {
                filterParameters.setLgbk(cbLgbk.getValue());
                sync();
            }
        });

        cbLgbk.setVisibleRowCount(Math.min(cbFamily.getItems().size(), 10));
    }

    public void initHierarchySelector() {
        cbHierarchy.setOnAction(null);

        cbHierarchy.setConverter(new StringConverter<ProductLgbk>() {
            @Override
            public String toString(ProductLgbk object) {
                if (object.equals(ALL_LGBKS) || object.equals(LGBK_NO_DATA)) {
                    return object.getHierarchy();
                }

//                ProductLgbk pl = CoreModule.getProductLgbks().getLgbkByHierarchy(object);
//                return pl != null ? pl.getCombineDescription() : String.format("[%s] ------", object);
                return object.getCombineDescription();
            }

            @Override
            public ProductLgbk fromString(String string) {
                return null;
            }
        });

        if (cbHierarchy.getItems().size() < 2 || filterParameters.getLastChange() < CHANGE_HIERARCHY ||
                filterParameters.getLastChange() == CHANGE_HIERARCHY && filterParameters.getHierarchy().equals(TEXT_ALL_ITEMS)) {

            cbHierarchy.getItems().clear();
            cbHierarchy.getItems().addAll(filterParameters.getHierarchies());
        }

        cbHierarchy.getSelectionModel().select(filterParameters.getHierarchy());

        cbHierarchy.setOnAction(event -> {
            if (cbHierarchy.getValue() != null) {
                filterParameters.setHierarchy(cbHierarchy.getValue());
                sync();
            }
        });

        cbHierarchy.setVisibleRowCount(Math.min(cbHierarchy.getItems().size(), 10));
    }

    private void initCustomSelection() {
        ToggleGroup customSelection = new ToggleGroup();
        RadioButton[] customSelectionItems = {rbStartWith, rbEndWith, rbContains, rbNotContains, rbRegularExpression};
        for (RadioButton radioButton : customSelectionItems) {
            radioButton.setToggleGroup(customSelection);
        }

        customSelectionItems[filterParameters.getCustomValueMatcher().ordinal()].setSelected(true);

        customSelection.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                filterParameters.setCustomCondition(CustomValueMatcher.values()[
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
