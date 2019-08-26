package ui_windows.options_window.price_lists_editor;

import core.CoreModule;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import ui_windows.Mode;

import java.net.URL;
import java.util.ResourceBundle;

public class PriceListEditorWindowController implements Initializable {
    private TwinListViews twinListViews;

    @FXML
    ListView lvAll;

    @FXML
    ListView lvSelected;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        twinListViews = new TwinListViews(CoreModule.getPriceLists().getPriceListsTable().getSelectedItem(),
                lvAll, lvSelected);
    }

    public void apply() {
        if (PriceListEditorWindow.getMode() == Mode.ADD) {
            CoreModule.getPriceLists().addItem(new PriceList(PriceListEditorWindow.getRootAnchorPane()));
        } else if (PriceListEditorWindow.getMode() == Mode.EDIT) {
            PriceList editedItem = CoreModule.getPriceLists().getPriceListsTable().getSelectedItem();
            PriceList changedItem = new PriceList(PriceListEditorWindow.getRootAnchorPane());
            editedItem.getLgbks().clear();
            editedItem.getLgbks().addAll(lvSelected.getItems());
            editedItem.setName(changedItem.getName());
            CoreModule.getPriceLists().editItem(editedItem);
        }
        CoreModule.getPriceLists().getPriceListsTable().getTableView().refresh();
        close();
    }

    public void close() {
        PriceListEditorWindow.getStage().close();
    }

    public void movePriceList() {
        twinListViews.moveNorm();
    }

    public void removePriceList() {
        twinListViews.removeNorm();
    }

    public void moveAllPriceLists() {
        twinListViews.moveAllNorms();
    }

    public void removeAllPriceLists() {
        twinListViews.removeAllNorms();
    }

}
