package ui_windows.main_window.filter_window_se;

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
    private Selector<ProductFamily> familySelector;
    private Selector<ProductLgbk> lgbkSelector;
    private Selector<ProductLgbk> hierarchySelector;

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

        if (SHD_FILTER_PARAMETERS.getData() instanceof FilterParameters_SE) {
            filterParameters = SHD_FILTER_PARAMETERS.getData();

            familySelector = new Selector<>(cbFamily,
                    ProductFamily::getName,
                    CHANGE_FAMILY, filterParameters::getFamilies, filterParameters::getFamily,
                    (pf) -> filterParameters.setProductFamily(pf),
                    this::sync);
            lgbkSelector = new Selector<>(cbLgbk,
                    pl -> pl.equals(ALL_LGBKS) || pl.equals(LGBK_NO_DATA) ? pl.getLgbk() : pl.getCombineDescriptionLgbk(),
                    CHANGE_LGBK, filterParameters::getLgbks, filterParameters::getLgbk,
                    (lgbk) -> filterParameters.setLgbk(lgbk),
                    this::sync);
            hierarchySelector = new Selector<>(cbHierarchy,
                    (h) -> h.equals(ALL_LGBKS) || h.equals(LGBK_NO_DATA) ? h.getHierarchy() : h.getCombineDescriptionHierarchy(),
                    CHANGE_HIERARCHY, filterParameters::getHierarchies, filterParameters::getHierarchy,
                    (hier) -> filterParameters.setHierarchy(hier),
                    this::sync);

            refresh();
        }
    }

    private void refresh() {
        if (SHD_FILTER_PARAMETERS.getData() instanceof FilterParameters_SE) {
            filterParameters = SHD_FILTER_PARAMETERS.getData();

            initMainSelection();
            familySelector.actualize(filterParameters);
            lgbkSelector.actualize(filterParameters);
            hierarchySelector.actualize(filterParameters);
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
                int index = Arrays.asList(rbAllItems, rbPriceItems).indexOf((RadioButton) newValue);
                filterParameters.setItems(ItemsSelection.values()[index]);
                sync(null);
            }
        });
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
                sync(null);
            }
        });

        tfCustomValue.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals(oldValue)) {
                filterParameters.setCustomValue(newValue);
                sync(null);
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

    public void sync(Selector selector) {
        SHD_FILTER_PARAMETERS.setData(filterParameters, this);
    }

    @Override
    public void refreshSubscribedData(SharedData sharedData, Object data) {
        refresh();
    }
}
