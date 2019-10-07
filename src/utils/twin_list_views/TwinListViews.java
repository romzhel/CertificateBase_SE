package utils.twin_list_views;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class TwinListViews<T> {
    private ArrayList<T> allItems;
    private final static double BUTTON_HEIGHT = 25;
    private final static double BUTTON_WIDTH = 26;
    private final static double BUTTON_WIDTH_2 = 36;
    private final static double SPACE = 10;
    private Comparator<T> comparatorAll;
    private Comparator<T> comparatorSelected;
    private ListView<T> lvAll;
    private ListView<T> lvSelected;
    private Button btnMove;
    private Button btnRemove;
    private Button btnMoveAll;
    private Button btnRemoveAll;
    private Callback<ObservableList<T>, String> convertToText;
    private Callback<String, ArrayList<T>> convertFromText;

    public TwinListViews (Pane pane, ArrayList<T> allItems) {
        this.allItems = allItems;

        double globalWidth = pane.getPrefWidth();
        double globalHeight = pane.getPrefHeight();

        double listWidth = initListViews(globalWidth, globalHeight);
        initButtons(globalHeight, listWidth);

        pane.getChildren().addAll(lvAll, lvSelected, btnMove, btnRemove, btnMoveAll, btnRemoveAll);
    }

    private void initButtons(double globalHeight, double listWidth) {
        double buttonX = listWidth + (56 - BUTTON_WIDTH) / 2;
        double buttonY = (globalHeight / 2 - (BUTTON_HEIGHT * 2 + SPACE)) / 2;

        btnMove = createButton(buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT, ">");
        btnRemove = createButton(buttonX, buttonY + BUTTON_HEIGHT + SPACE, BUTTON_WIDTH, BUTTON_HEIGHT, "<");

        double buttonAllX = listWidth + (56 - BUTTON_WIDTH_2) / 2;
        double buttonAllY = globalHeight / 2 + (globalHeight / 2 - BUTTON_HEIGHT * 2 - SPACE) / 2;

        btnMoveAll = createButton(buttonAllX, buttonAllY, BUTTON_WIDTH_2, BUTTON_HEIGHT, ">>");
        btnRemoveAll = createButton(buttonAllX, buttonAllY + BUTTON_HEIGHT + SPACE, BUTTON_WIDTH_2, BUTTON_HEIGHT, "<<");

        btnMove.setOnAction(event -> move());
        btnRemove.setOnAction(event -> remove());
        btnMoveAll.setOnAction(event -> moveAll());
        btnRemoveAll.setOnAction(event -> removeAll());
    }

    private double initListViews(double globalWidth, double globalHeight) {
        double listWidth = (globalWidth - 56) / 2;

        lvAll = createListView(0, 0, listWidth, globalHeight);
        lvSelected = createListView(listWidth + 56, 0, listWidth, globalHeight);

        lvAll.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) move();
        });

        lvSelected.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) remove();
        });
        lvSelected.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

//        lvAll.getItems().addAll(allItems);

        return listWidth;
    }

    private ListView<T> createListView(double x, double y, double width, double height) {
        ListView<T> listView = new ListView<>();
        listView.setPrefSize(width, height);
        listView.setLayoutX(x);
        listView.setLayoutY(y);

        return listView;
    }

    private Button createButton(double x, double y, double width, double height, String label) {
        Button button = new Button();
        button.setPrefSize(width, height);
        button.setLayoutX(x);
        button.setLayoutY(y);
        button.setText(label);

        return button;
    }

    private void move() {
        int selectedIndex = lvAll.getSelectionModel().getSelectedIndex();
        if (selectedIndex > -1) {
            lvSelected.getItems().add(lvAll.getItems().remove(selectedIndex));
            sort();
        }
    }

    private void moveAll() {
        lvSelected.getItems().addAll(lvAll.getItems());
        lvAll.getItems().clear();
        sort();
    }

    private void remove() {
        int selectedIndex = lvSelected.getSelectionModel().getSelectedIndex();
        if (selectedIndex > -1) {
            lvAll.getItems().add(lvSelected.getItems().remove(selectedIndex));
            sort();
        }
    }

    private void removeAll() {
        lvAll.getItems().addAll(lvSelected.getItems());
        lvSelected.getItems().clear();
        sort();
    }

    private void sort() {
        if (comparatorAll != null) lvAll.getItems().sort(comparatorAll);
        if (comparatorSelected != null) lvSelected.getItems().sort(comparatorSelected);
    }

    public void setListViewsCellFactory(Callback<ListView<T>, ListCell<T>> callback) {
        lvAll.setCellFactory(callback);
        lvSelected.setCellFactory(callback);
        lvAll.getItems().addAll(allItems);
    }

    public void setListViewsAllComparator(Comparator<T> comparator) {
        this.comparatorAll = comparator;
        sort();
    }

    public void setListViewsSelectedComparator(Comparator<T> comparator) {
        this.comparatorSelected = comparator;
        sort();
    }

    public ArrayList<T> getSelectedItems() {
        return new ArrayList<T>(lvSelected.getItems());
    }

    public String getSelectedItemsAsString() {
        return convertToText != null ? convertToText.call(lvSelected.getItems()) : "";
    }

    public void setSelectedItemsFromString(String text) {
        setSelectedItems(convertFromText.call(text));
    }

    public void setSelectedItems(ArrayList<T> selectedItems) {
        lvSelected.getItems().clear();
        lvSelected.getItems().addAll(selectedItems);
        lvAll.getItems().removeAll(selectedItems);
        sort();
    }

    public void setConvertToText(Callback<ObservableList<T>, String> convertToText) {
        this.convertToText = convertToText;
    }

    public void setConvertFromText(Callback<String, ArrayList<T>> convertFromText) {
        this.convertFromText = convertFromText;
    }
}
