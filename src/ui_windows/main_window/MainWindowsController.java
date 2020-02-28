package ui_windows.main_window;

import core.CoreModule;
import core.Dialogs;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import scripts.PriceGenerationScript;
import ui_windows.login_window.LoginWindow;
import ui_windows.main_window.file_import_window.se.ImportNowFile;
import ui_windows.main_window.filter_window.FilterWindow;
import ui_windows.options_window.OptionsWindow;
import ui_windows.options_window.price_lists_editor.PriceList;
import ui_windows.options_window.user_editor.User;
import ui_windows.product.Product;
import ui_windows.request.RequestWindow;
import utils.Utils;
import utils.comparation.prices.PricesComparator;
import utils.comparation.prices.SelectPricesForComparisonWindow;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainWindowsController implements Initializable {
    private final String SPACE = "     ";
    private MainTable mainTable;
    private DataSelectorMenu dataSelectorMenu;

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
            final MenuItem mi = new MenuItem(SPACE + pl.getName() + SPACE);
            mPriceList.getItems().add(mi);

            mi.setOnAction(event -> {
                int generationMode = new Dialogs().chooserTS("Генерация прайс-листа",
                        "Генерируем только прайс-лист, пакет для проверки или пакет для пользования?",
                        "Только прайс", "Для проверки", "Для пользования");
                if (generationMode < 3) {
                    new PriceGenerationScript().run(mPriceList.getItems().indexOf(mi), generationMode);
                }
            });
        }
    }

    public void openNow() {
        new ImportNowFile(new Dialogs().selectAnyFile(MainWindow.getMainStage(), "Выбор файла с выгрузкой",
                Dialogs.EXCEL_FILES, null));
    }

    public void comparePriceLists() {
        ArrayList<File> priceListFiles = new SelectPricesForComparisonWindow().getPriceListFiles();
        if (priceListFiles.get(0) != null) {
            new Thread(() -> {
                PricesComparator pricesComparator = new PricesComparator();
                pricesComparator.compare(priceListFiles);
                Utils.openFile(pricesComparator.exportToExcel(null));
            }).start();
        }
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

    public MainTable getMainTable() {
        return mainTable;
    }
}
