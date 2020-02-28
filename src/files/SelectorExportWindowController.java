package files;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Callback;
import ui_windows.product.data.DataItem;
import ui_windows.product.data.DataSets;
import utils.twin_list_views.TwinListViews;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

public class SelectorExportWindowController implements Initializable {
    private TwinListViews<DataItem> columnsSelector;

    @FXML
    public Pane pColsSelector;
    @FXML
    public Button btnOk;
    @FXML
    public Button btnCancel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initTwinListsView();
        initButtons();
    }

    private void initButtons() {
        btnOk.setOnAction(event -> ((Stage) btnOk.getScene().getWindow()).close());
        btnCancel.setOnAction(event -> ((Stage) btnOk.getScene().getWindow()).close());
    }

    private void initTwinListsView() {
        ArrayList<DataItem> columns = new ArrayList<>();
        columns.addAll(Arrays.asList(DataSets.getDataItemsForExcelExport()));
        columnsSelector = new TwinListViews<>(pColsSelector, columns);
        columnsSelector.setListViewsCellFactory(new Callback<ListView<DataItem>, ListCell<DataItem>>() {
            @Override
            public ListCell call(ListView<DataItem> param) {
                return new ListCell<DataItem>() {
                    @Override
                    protected void updateItem(DataItem item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item != null && !empty) {
                            setText(item.getDisplayingName());
                        } else {
                            setText(null);
                        }
                    }
                };
            }
        });
//        columnsSelector.setListViewsAllComparator((o1, o2) -> o1.getDisplayingName().compareToIgnoreCase(o2.getDisplayingName()));
        columnsSelector.setListViewsAllComparator(null);
        columnsSelector.setListViewsSelectedComparator(null);
        columnsSelector.setConvertToText(param -> {
            String result = "";
            for (DataItem item : param) {
                result = result.concat(item.getDisplayingName()).concat(",");
            }
            result = result.replaceAll("\\,$", "");

            return result;
        });
        columnsSelector.setConvertFromText(new Callback<String, ArrayList<DataItem>>() {
            @Override
            public ArrayList<DataItem> call(String param) {
                ArrayList<DataItem> result = new ArrayList<>();
                for (String columnName : param.split("\\,")) {
                    for (DataItem die : columns) {
                        if (die.getDisplayingName().equals(columnName)) {
                            result.add(die);
                            break;
                        }
                    }
                }
                return result;
            }
        });
        columnsSelector.setSelectedItems(columns);
    }

    public TwinListViews<DataItem> getColumnsSelector() {
        return columnsSelector;
    }
}
