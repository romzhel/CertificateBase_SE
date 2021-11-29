package ui_windows.main_window.filter_window_se;

import core.Module;
import core.SharedData;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.WindowEvent;
import lombok.extern.log4j.Log4j2;
import ui.Dialogs;
import ui_windows.options_window.families_editor.ProductFamilies;
import ui_windows.options_window.families_editor.ProductFamily;
import ui_windows.options_window.product_lgbk.LgbkAndParent;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.options_window.product_lgbk.ProductLgbkGroups;
import ui_windows.product.Product;
import ui_windows.product.data.DataItem;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import static core.SharedData.SHD_DISPLAYED_DATA;
import static core.SharedData.SHD_FILTER_PARAMETERS;
import static ui_windows.main_window.filter_window_se.FilterParameters_SE.*;
import static ui_windows.main_window.filter_window_se.ItemsSelection.ALL_ITEMS;
import static ui_windows.main_window.filter_window_se.ItemsSelection.PRICE_ITEMS;
import static ui_windows.options_window.families_editor.ProductFamilies.UNKNOWN;
import static ui_windows.product.data.DataItem.DATA_EMPTY;

@Log4j2
public class FilterWindowController_SE implements Initializable, Module {
    private static final String TEXT_ALL_ITEMS = "--- Все ---";
    private static final int SELECTOR_LGBK_ROWS_MAX = 10;
    private static final boolean REFRESH_AND_SEND_DATA = true;
    private static final boolean REFRESH_ONLY = false;
    private FilterParameters_SE filterParameters;
    private Selector<ProductFamily> familySelector;
    private Selector<ProductLgbk> lgbkSelector;
    private Selector<ProductLgbk> hierarchySelector;
    private Selector<DataItem> customPropertySelector;
    private RadioButton[] customSelectionItems;
    private boolean refreshMode;

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
        SHD_DISPLAYED_DATA.subscribe(this);
        refreshMode = REFRESH_AND_SEND_DATA;

        if (SHD_FILTER_PARAMETERS.getData() instanceof FilterParameters_SE) {
            filterParameters = SHD_FILTER_PARAMETERS.getData();

            readFilterStatisticParams();

            initMainSelection();

            familySelector = new Selector<>(cbFamily,
                    ProductFamily::getName,
                    filterParameters::getFamilies, filterParameters::getFamily,
                    (pf) -> filterParameters.setFamily(pf),
                    this::sync);
            lgbkSelector = new Selector<>(cbLgbk,
                    pl -> pl.equals(ALL_LGBKS) || pl.equals(LGBK_NO_DATA) ? pl.getLgbk() : pl.getCombineDescriptionLgbk(),
                    filterParameters::getLgbks, filterParameters::getLgbk,
                    (lgbk) -> filterParameters.setLgbk(lgbk),
                    this::sync);
            hierarchySelector = new Selector<>(cbHierarchy,
                    (h) -> h.equals(ALL_LGBKS) || h.equals(LGBK_NO_DATA) ? h.getHierarchy() : h.getCombineDescriptionHierarchy(),
                    filterParameters::getHierarchies, filterParameters::getHierarchy,
                    (hier) -> filterParameters.setHierarchy(hier),
                    this::sync);
            customPropertySelector = new Selector<>(cbCustomProperty,
                    (di) -> di == DATA_EMPTY ? TEXT_NO_SELECTED : di.getDisplayingName(),
                    filterParameters::getCustomProperties, filterParameters::getCustomProperty,
                    (di) -> filterParameters.setCustomProperty(di),
                    this::sync);

            initCustomSelection();

            refresh();
        }
    }

    private void readFilterStatisticParams() {
        filterParameters.clearComboBoxItems();
        List<Product> displayedProducts = SHD_DISPLAYED_DATA.getData();
        for (Product product : displayedProducts) {
            ProductFamily pf = ProductFamilies.getInstance().getProductFamily(product);
            filterParameters.getFamilies().add(pf == UNKNOWN ? FAMILY_NOT_ASSIGNED : pf);

            ProductLgbk productLgbk = new ProductLgbk(product);
            LgbkAndParent lap = ProductLgbkGroups.getInstance().getLgbkAndParent(productLgbk);
            ProductLgbk gbk = lap.getLgbkParent();
            ProductLgbk hier = lap.getLgbkItem();
            filterParameters.getLgbks().add(gbk == null || gbk.getLgbk().isEmpty() ? LGBK_NO_DATA : gbk);
            filterParameters.getHierarchies().add(hier == null || hier.getHierarchy().isEmpty() ? LGBK_NO_DATA : hier);
        }
    }

    private void refresh() {
        if (SHD_FILTER_PARAMETERS.getData() instanceof FilterParameters_SE) {
            filterParameters = SHD_FILTER_PARAMETERS.getData();
            readFilterStatisticParams();

            rbAllItems.setSelected(filterParameters.getFilterItems() == ALL_ITEMS);
            rbPriceItems.setSelected(filterParameters.getFilterItems() == PRICE_ITEMS);

            familySelector.actualize(filterParameters);
            lgbkSelector.actualize(filterParameters);
            hierarchySelector.actualize(filterParameters);
            customPropertySelector.actualize(filterParameters);

            tfCustomValue.setText(filterParameters.getCustomValue());
            customSelectionItems[filterParameters.getCustomValueMatcher().ordinal()].setSelected(true);
        } else {
            Dialogs.showMessageTS("Обновление окна фильтра", "Не найдено параметров фильтра!");
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
                filterParameters.setFilterItems(ItemsSelection.values()[index]);
                sync(null);
            }
        });
    }

    private void initCustomSelection() {
        ToggleGroup customSelection = new ToggleGroup();
        customSelectionItems = new RadioButton[]{rbStartWith, rbEndWith, rbContains, rbNotContains, rbRegularExpression};
        for (RadioButton radioButton : customSelectionItems) {
            radioButton.setToggleGroup(customSelection);
        }

        customSelection.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (filterParameters.getCustomProperty() != DATA_EMPTY && newValue != null) {
                filterParameters.setCustomValueMatcher(CustomValueMatcher.values()[
                        Arrays.asList(customSelectionItems).indexOf((RadioButton) newValue)]);
                sync(null);
            }
        });

        tfCustomValue.textProperty().addListener((observable, oldValue, newValue) -> {
            if (filterParameters.getCustomProperty() != DATA_EMPTY && newValue != null && !newValue.equals(oldValue)) {
                filterParameters.setCustomValue(newValue);
                sync(null);
            }
        });
    }

    public void close() {
        SHD_DISPLAYED_DATA.unsubscribe(this);
        cbLgbk.getScene().getWindow().fireEvent(new WindowEvent(cbLgbk.getScene().getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    public void sync(Selector<?> selector) {
        filterParameters.setChangedSelector(selector);
        if (refreshMode == REFRESH_AND_SEND_DATA) {
            SHD_FILTER_PARAMETERS.setData(this.getClass(), filterParameters);
        }
    }

    @Override
    public void refreshSubscribedData(SharedData sharedData, Object data) {
        refreshMode = REFRESH_ONLY;
        refresh();
        refreshMode = REFRESH_AND_SEND_DATA;
    }
}
