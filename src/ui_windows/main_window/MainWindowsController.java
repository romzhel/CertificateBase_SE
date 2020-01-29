package ui_windows.main_window;

import core.CoreModule;
import core.Dialogs;
import files.price_to_excel.ExportPriceListToExcel_SE;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import ui_windows.login_window.LoginWindow;
import ui_windows.main_window.file_import_window.se.ImportNowFile;
import ui_windows.main_window.filter_window.FilterWindow;
import ui_windows.options_window.OptionsWindow;
import ui_windows.options_window.price_lists_editor.PriceList;
import ui_windows.options_window.user_editor.User;
import ui_windows.product.Product;
import ui_windows.request.RequestWindow;
import utils.comparation.prices.PriceComparationWindow;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MainWindowsController implements Initializable {
    public MainTable mainTable;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainTable = new MainTable(tvTable);
        CoreModule.getProducts().setTableView(tvTable);
        MainWindow.setProgressBar(pbExecuted);
        MainWindow.setMiOptions(miOptions);
        dataSelectorMenu = new DataSelectorMenu(miReports);

        initPriceListMenu();

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
        new ImportNowFile(new Dialogs().selectAnyFile(MainWindow.getMainStage(), "Выбор файла с выгрузкой",
                Dialogs.EXCEL_FILES, null));
    }

    public void comparePriceLists() {
        new PriceComparationWindow();
    }

    public void openOptionsWindow() {
        new OptionsWindow();
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

    public DataSelectorMenu getDataSelectorMenu() {
        return dataSelectorMenu;
    }
}
