package ui_windows.options_window.price_lists_editor.se.price_sheet;

import com.sun.org.apache.xml.internal.security.Init;
import core.CoreModule;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import ui_windows.options_window.order_accessibility_editor.OrderAccessibility;
import ui_windows.options_window.price_lists_editor.se.*;
import utils.twin_list_views.TwinListViews;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ResourceBundle;

import static ui_windows.main_window.file_import_window.NamesMapping.*;

public class PriceListSheetController implements Initializable {

    @FXML
    public RadioButton rbLangRu;

    @FXML
    public RadioButton rbLangEn;

    @FXML
    public Pane pPriceColumns;

    @FXML
    public TreeTableView<PriceListContentTableItem> ttvPriceContent;

    @FXML
    public Pane pPriceDchain;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ToggleGroup languageSelector = new ToggleGroup();
        rbLangEn.setToggleGroup(languageSelector);
        rbLangRu.setToggleGroup(languageSelector);


        rbLangRu.setSelected(true);




        TwinListViews<OrderAccessibility> dchainSelector = new TwinListViews<>(pPriceDchain,
                CoreModule.getOrdersAccessibility().getItems());

        /*dchainSelector.setListViewsCellFactory(new Callback<ListView<OrderAccessibility>, ListCell<OrderAccessibility>>() {
            @Override
            public ListCell<OrderAccessibility> call(ListView<OrderAccessibility> param) {
                return new ListCell<OrderAccessibility>(){
                    @Override
                    protected void updateItem(OrderAccessibility item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item != null && !empty) {
                            String description = item.getDescriptionRu().isEmpty() ? item.getDescriptionEn() : item.getDescriptionRu();
                            setText("[" + item.getStatusCode() + "] " + description);
                        } else {
                            setText(null);
                        }
                    }
                };
            }
        });
        Comparator<OrderAccessibility> dchainComparator = (o1, o2) -> o1.getStatusCode().compareTo(o2.getStatusCode());
        dchainSelector.setListViewsAllComparator(dchainComparator);
        dchainSelector.setListViewsSelectedComparator(dchainComparator);*/


//        new PriceListContentTable(ttvPriceContent);
    }
}
