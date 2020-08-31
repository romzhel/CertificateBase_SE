package ui_windows.main_window;

import files.Folders;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import scripts.PriceGenerationScript;
import ui.Dialogs;
import ui_windows.ExecutionIndicator;
import ui_windows.login_window.LoginWindow;
import ui_windows.main_window.file_import_window.se.ImportNowFile;
import ui_windows.main_window.filter_window_se.FilterWindow_SE;
import ui_windows.options_window.OptionsWindow;
import ui_windows.options_window.price_lists_editor.PriceList;
import ui_windows.options_window.price_lists_editor.PriceLists;
import ui_windows.options_window.user_editor.User;
import ui_windows.options_window.user_editor.Users;
import ui_windows.product.Product;
import ui_windows.product.Products;
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
    @FXML
    public Menu miDataSource;
    @FXML
    public Menu mPriceList;
    @FXML
    public MenuItem mniOpenNow;
    @FXML
    public ImageView ivFilter;
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
    private MainTable mainTable;
    private DataSelectorMenu dataSelectorMenu;
    private static final Logger logger = LogManager.getLogger(MainWindowsController.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainTable = new MainTable(tvTable);
        Products.getInstance().setTableView(tvTable);
        ExecutionIndicator.getInstance().init(pbExecuted);
        MainWindow.setMiOptions(miOptions);
        dataSelectorMenu = new DataSelectorMenu(miDataSource);

        initPriceListMenu();

        lbRecordCount.setText(Integer.toString(tvTable.getItems().size()));
    }

    public void initPriceListMenu() {
        mPriceList.getItems().clear();
        for (PriceList pl : PriceLists.getInstance().getItems()) {
            final MenuItem mi = new MenuItem(SPACE + pl.getName() + SPACE);
            mPriceList.getItems().add(mi);

            mi.setOnAction(event -> {
                int generationMode = new Dialogs().chooserTS("Генерация прайс-листа",
                        "Генерируем только прайс-лист, пакет для проверки или пакет для пользования?",
                        "Только прайс", "Для проверки", "Для пользования");
                if (generationMode < 3) {
                    logger.info("Generating price-list {}", PriceLists.getInstance().getItems()
                            .get(mPriceList.getItems().indexOf(mi)).getName());
                    new PriceGenerationScript().run(mPriceList.getItems().indexOf(mi), generationMode);
                }
            });
        }
    }

    public void importFromNow() {
        Thread nowImportThread = new Thread(() -> {
            try {
                ExecutionIndicator.getInstance().start();
                boolean isFullPackage = Dialogs.confirmTS("Формирование полного пакета отчётов",
                        "Желаете сформировать полный пакет отчётов?");

                ImportNowFile importNowFile = new ImportNowFile();
                importNowFile.treat(new Dialogs().selectAnyFileTS(MainWindow.getMainStage(),
                        "Выбор файла с выгрузкой", Dialogs.EXCEL_FILES, null));

                if (isFullPackage) {
                    File importReportFile = Folders.getInstance().getTempFolder().resolve(
                            "import_report_" + Utils.getDateTimeForFileName() + ".xlsx").toFile();
                    importReportFile = importNowFile.getReportFile(importReportFile);
                    new PriceGenerationScript().run(0, PriceGenerationScript.REPORTS_FOR_CHECK);
                    Utils.openFile(importReportFile.getParentFile());
                } else {
                    Utils.openFile(importNowFile.getReportFile(null));
                }
            } catch (RuntimeException re) {
                logger.warn("{}", re.getMessage());
            } catch (Exception e) {
                logger.error("ошибка импорта NOW {}", e.getMessage(), e);
            } finally {
                ExecutionIndicator.getInstance().stop();
            }
        });
        nowImportThread.setDaemon(true);
        nowImportThread.setName("NOW import thread");
        nowImportThread.start();
    }

    public void comparePriceLists() {
        ArrayList<File> priceListFiles = new SelectPricesForComparisonWindow().getPriceListFiles();
        if (priceListFiles.get(0) != null) {
            new Thread(() -> {
                logger.info("Starting price-list comparing {} vs {}", priceListFiles.get(0), priceListFiles.get(1));
                ExecutionIndicator.getInstance().start();
                PricesComparator pricesComparator = new PricesComparator();
                pricesComparator.compare(priceListFiles);
                Utils.openFile(pricesComparator.exportToExcel(null));
                ExecutionIndicator.getInstance().stop();
            }).start();
        }
    }

    public void openOptionsWindow() {
        logger.info("Opening Options window");
        new OptionsWindow();
    }

    public void actionLogin() {
        logger.info("Opening authorization window");
        new LoginWindow();
    }

    public void userInfo() {
        User user = Users.getInstance().getCurrentUser();
        Dialogs.showMessage("Информация о текущем пользователе", "Пользователь: " + user.getName() + " " +
                user.getSurname() + "\nПрофиль: " + user.getProfile().getName());
    }

    public void displayFilterOptions() {
        logger.info("Opening filter window");
        FilterWindow_SE.openFilterWindow();
    }

    public void actionRequest() {
        logger.info("Opening positions request window");
        new RequestWindow();
    }

    public DataSelectorMenu getDataSelectorMenu() {
        return dataSelectorMenu;
    }

    public MainTable getMainTable() {
        return mainTable;
    }
}
