package ui_windows.main_window;

import core.CoreModule;
import core.Dialogs;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.util.Callback;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.product.Product;
import ui_windows.product.certificatesChecker.CertificatesChecker;
import ui_windows.product.certificatesChecker.CheckParameters;
import ui_windows.product.productEditorWindow.ProductEditorWindow;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static ui_windows.Mode.EDIT;
import static ui_windows.main_window.DataSelectorMenu.MENU_DATA_CUSTOM_SELECTION;

public class MainTable {
    private static TableView<Product> tvTable;
    private ExecutorService executorService;

    public MainTable(TableView<Product> tvTable) {
        MainTable.tvTable = tvTable;
        initContextMenu();
        init();
    }

    private void init() {
        executorService = Executors.newFixedThreadPool(10);
        initContextMenu();
        initTable();
    }

    private void initContextMenu() {
        MainTableContextMenuFactory.init(this);
        switchContextMenuData();
    }

    private void initTable() {
        String[] cols = new String[]{"material", "article", "description", "family", "endofservice",
                "country", "dchain"};
        String[] titles = new String[]{"Заказной номер", "Артикул", "Описание", "Направление", "Окончание",
                "Страна", "Доступность"};
        int[] colsWidth = new int[]{130, 130, 500, 100, 100, 50, 150};
        boolean[] centerAligment = new boolean[]{true, true, false, true, true, true, false};

//        for (String s : cols) {//add columns
        for (int i = 0; i < cols.length; i++) {
            TableColumn<Product, String> col = new TableColumn<>(titles[i]);

            if (cols[i] == "family") {
                col.setCellValueFactory(param -> {
                    Product pr = param.getValue();

                    if (pr.getFamily() > 0) {//individual value
                        return new SimpleStringProperty(CoreModule.getProductFamilies().getFamilyNameById(pr.getFamily()));
                    } else {//try to calculate it
                        int id = CoreModule.getProductLgbks().
                                getFamilyIdByLgbk(new ProductLgbk(pr.getLgbk(), pr.getHierarchy()));
                        String family = CoreModule.getProductFamilies().getFamilyNameById(id);

                        if (family == "") return new SimpleStringProperty(pr.getLgbk().concat(" (").
                                concat(pr.getHierarchy()).concat(")"));
                        else return new SimpleStringProperty(family);
                    }
                });

            } else if (cols[i] == "dchain") {
                col.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getOrderableStatus()));
                col.setCellFactory(new Callback<TableColumn<Product, String>, TableCell<Product, String>>() {
                    @Override
                    public TableCell<Product, String> call(TableColumn<Product, String> param) {
                        return new TableCell<Product, String>() {
                            @Override
                            protected void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);

                                if (!empty) {
                                    setText(item);

                                    final Product product = param.getTableView().getItems().get(getIndex());

                                    Runnable certCheck = () -> {
                                        CertificatesChecker certificatesChecker = new CertificatesChecker(product, new CheckParameters());

                                        Platform.runLater(() -> {

                                            getStyleClass().add(certificatesChecker.getCheckStatusResultStyle(getStyleClass()));
                                            setTooltip(new Tooltip(certificatesChecker.getCheckStatusResult()));
                                        });
                                    };

                                    executorService.execute(certCheck);
                                }
                            }
                        };
                    }
                });

            } else if (cols[i] == "description") {
                col.setCellValueFactory(param -> {
                    Product pr = param.getValue();

                    if (!pr.getDescriptionru().trim().isEmpty()) return new SimpleStringProperty(pr.getDescriptionru());
                    else if (!pr.getDescriptionen().trim().isEmpty())
                        return new SimpleStringProperty(pr.getDescriptionen());
                    else return new SimpleStringProperty("");
                });
            } else col.setCellValueFactory(new PropertyValueFactory<>(cols[i]));

            col.setPrefWidth(colsWidth[i]);
            if (centerAligment[i]) col.setStyle("-fx-alignment: CENTER");
            tvTable.getColumns().add(col);
        }

        TableColumn<Product, Boolean> boolCol = new TableColumn<>("Прайс");
        boolCol.setCellFactory(CheckBoxTableCell.forTableColumn(boolCol));
        boolCol.setCellValueFactory(param -> new SimpleBooleanProperty(param.getValue().isPrice()));
        boolCol.setPrefWidth(50);
        boolCol.setStyle("-fx-alignment: CENTER");
        tvTable.getColumns().add(boolCol);

        tvTable.setOnMouseClicked(event -> {//double click on product
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.getClickCount() == 2) {
                    displayEditorWindow();//open product editor window
                }
            }
        });
        tvTable.setPlaceholder(new Label("Нет данных для отображения"));
        tvTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void displayEditorWindow() {
        if (tvTable.getSelectionModel().getSelectedIndex() < 0) Dialogs.showMessage("Выбор строки",
                "Нужно выбрать строку");
        else new ProductEditorWindow(EDIT, tvTable.getSelectionModel().getSelectedItems());
    }

    public void close() {
        executorService.shutdown();
    }

    public void refresh() {
        tvTable.refresh();
        MainWindow.getController().lbRecordCount.setText(Integer.toString(tvTable.getItems().size()));
    }

    public ArrayList<Product> getItemsForReport() {
        if (MENU_DATA_CUSTOM_SELECTION.isSelected()) {
            return new ArrayList<>(CoreModule.getCustomItems());
        } else {
            return new ArrayList<>(tvTable.getSelectionModel().getSelectedItems());
        }
    }

    public static void switchContextMenuData() {
        tvTable.setContextMenu(MainTableContextMenuFactory.createContextMenuForAllData());
    }

    public static void switchContextMenuCustom() {
        tvTable.setContextMenu(MainTableContextMenuFactory.createContectMenuForCustomItems());
    }
}
