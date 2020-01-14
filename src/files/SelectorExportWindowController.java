package files;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Callback;
import ui_windows.main_window.MainWindow;
import ui_windows.product.data.DataItemEnum;
import utils.twin_list_views.TwinListViews;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

public class SelectorExportWindowController implements Initializable {
    private TwinListViews<DataItemEnum> columnsSelector;

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
        btnOk.setOnAction(event -> {
            new Thread(() -> new ExportToExcel(columnsSelector.getSelectedItems(), MainWindow.getMainTable().getItemsForReport())).start();
            ((Stage)btnOk.getScene().getWindow()).close();
        });
        btnCancel.setOnAction(event -> ((Stage)btnOk.getScene().getWindow()).close());
    }

    private void initTwinListsView() {
        ArrayList<DataItemEnum> columns = new ArrayList<>();
        columns.addAll(Arrays.asList(DataItemEnum.values()));
        columnsSelector = new TwinListViews<>(pColsSelector, columns);
        columnsSelector.setListViewsCellFactory(new Callback<ListView<DataItemEnum>, ListCell<DataItemEnum>>() {
            @Override
            public ListCell call(ListView<DataItemEnum> param) {
                return new ListCell<DataItemEnum>() {
                    @Override
                    protected void updateItem(DataItemEnum item, boolean empty) {
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
            for (DataItemEnum item : param) {
                result = result.concat(item.getDisplayingName()).concat(",");
            }
            result = result.replaceAll("\\,$", "");

            return result;
        });
        columnsSelector.setConvertFromText(new Callback<String, ArrayList<DataItemEnum>>() {
            @Override
            public ArrayList<DataItemEnum> call(String param) {
                ArrayList<DataItemEnum> result = new ArrayList<>();
                for (String columnName : param.split("\\,")) {
                    for (DataItemEnum die : columns) {
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
}
