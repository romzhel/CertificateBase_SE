package ui_windows.main_window;

import core.CoreModule;
import core.Dialogs;
import files.price_to_excel.ExportPriceListToExcel_SE;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import ui_windows.login_window.LoginWindow;
import ui_windows.main_window.file_import_window.FileImport;
import ui_windows.main_window.filter_window.FilterWindow;
import ui_windows.options_window.OptionsWindow;
import ui_windows.options_window.price_lists_editor.PriceList;
import ui_windows.options_window.user_editor.User;
import ui_windows.product.Product;
import ui_windows.request.RequestWindow;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainWindowsController implements Initializable {
    private DataSelectorMenu dataSelectorMenu;
    boolean clearOldResult;

    @FXML
    TableView<Product> tvTable;
    @FXML
    Label lbRecordCount;
    @FXML
    ProgressBar pbExecuted;
    @FXML
    MenuBar mnuBar;
    @FXML
    Menu miFile;
    @FXML
    MenuItem miOptions;
    @FXML
    public Menu miReports;
    @FXML
    public Menu mPriceList;
    @FXML
    public MenuItem mniOpenNow;
    @FXML
    public ImageView ivFilter;

    private FileImport fileImport;
    private MainTable mainTable;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CoreModule.getProducts().setTableView(tvTable);
        MainWindow.setProgressBar(pbExecuted);
        MainWindow.setMiOptions(miOptions);
        dataSelectorMenu = new DataSelectorMenu(miReports);

        initPriceListMenu();
        mainTable = new MainTable(tvTable);

        lbRecordCount.setText(Integer.toString(tvTable.getItems().size()));
    }

    public void initPriceListMenu() {
        mPriceList.getItems().clear();
        for (PriceList pl : CoreModule.getPriceLists().getItems()) {
            MenuItem mi = new MenuItem("     " + pl.getName() + "     ");
            mPriceList.getItems().add(mi);

            mi.setOnAction(event -> {
                int index = mPriceList.getItems().indexOf(mi);
                new ExportPriceListToExcel_SE(CoreModule.getPriceLists().getItems().get(index));
            });
        }
    }

    public void openNow() {
        fileImport = new FileImport();
    }

    public void openOptionsWindow() {
        new OptionsWindow();
    }

    public void addProduct() {
    }

    public void editProduct() {
        mainTable.displayEditorWindow();
    }

    public void deleteProduct() {
    }

    public void displayInTable(ArrayList<Product> products) {
        tvTable.getItems().clear();
        tvTable.getItems().addAll(products);
        lbRecordCount.setText(Integer.toString(tvTable.getItems().size()));
    }

    public void actionLogin() {
        new LoginWindow();
    }

    public void userInfo() {
        User user = CoreModule.getUsers().getCurrentUser();
        Dialogs.showMessage("Информация о текущем пользователе", "Пользователь: " + user.getName() + " " +
                user.getSurname() + "\nПрофиль: " + user.getProfile().getName());
    }

    public void displayFilterOptions() {
        new FilterWindow();
    }

    public void actionRequest() {
        new RequestWindow();
    }

    public MainTable getMainTable() {
        return mainTable;
    }

    public DataSelectorMenu getDataSelectorMenu() {
        return dataSelectorMenu;
    }
}
