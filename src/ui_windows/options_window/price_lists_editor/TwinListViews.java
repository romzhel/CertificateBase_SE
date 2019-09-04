package ui_windows.options_window.price_lists_editor;

import core.CoreModule;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import ui_windows.Mode;

import java.util.ArrayList;
import java.util.TreeSet;

public class TwinListViews {
    private ListView<String> lvAll;
    private ListView<String> lvSelected;

    public TwinListViews(PriceList priceList, ListView lvAll, ListView lvSelected) {
        this.lvAll = lvAll;
        this.lvSelected = lvSelected;

        initListViews();
        display();
    }

    public void display() {
        lvAll.getItems().clear();
        lvSelected.getItems().clear();
        lvAll.getItems().addAll(CoreModule.getProductLgbkGroups().getGroupLgbkDescriptions());

        if (PriceListEditorWindow.getMode() == Mode.EDIT) {
            ArrayList<String> plgbks = CoreModule.getPriceLists().getPriceListsTable().getSelectedItem().getLgbks();
            lvSelected.getItems().addAll(CoreModule.getProductLgbks().getLgbkDescALbyNamesAL(plgbks));
            lvAll.getItems().removeAll(lvSelected.getItems());
            sortLV(lvSelected);
        }
    }

    private void initListViews() {
        lvAll.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.getClickCount() == 2) {
                    moveNorm();
                }
            }
        });

        lvSelected.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.getClickCount() == 2) {
                    removeNorm();
                }
            }
        });
    }


    public void moveNorm() {
        int selectedIndex = lvAll.getSelectionModel().getSelectedIndex();
        if (selectedIndex > -1) {
            lvSelected.getItems().add(lvAll.getItems().remove(selectedIndex));
            sortLV(lvSelected);
        }
    }

    public void moveAllNorms() {
        lvSelected.getItems().addAll(lvAll.getItems());
        sortLV(lvSelected);
        lvAll.getItems().clear();
    }

    public void removeNorm() {
        int selectedIndex = lvSelected.getSelectionModel().getSelectedIndex();
        if (selectedIndex > -1) {
            lvAll.getItems().add(lvSelected.getItems().remove(selectedIndex));
            sortLV(lvAll);
        }
    }

    public void removeAllNorms() {
        lvAll.getItems().addAll(lvSelected.getItems());
        sortLV(lvAll);
        lvSelected.getItems().clear();
    }

    private void sortLV(ListView<String> listView) {
        TreeSet<String> sortedList = new TreeSet<>(listView.getItems());
        listView.getItems().clear();
        listView.getItems().addAll(sortedList);
    }

    public String getLgbksAsStringForSave() {
        return null;
    }

}
