package ui_windows.main_window;

import core.ThreadManager;
import exceptions.DataNotSelectedException;
import exceptions.OperationCancelledByUserException;
import javafx.application.Platform;
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
import ui_windows.main_window.file_import_window.te.FilesImportParameters;
import ui_windows.main_window.file_import_window.te.FilesSelectionWindow;
import ui_windows.main_window.file_import_window.te.ProductDataFileImportTask;
import ui_windows.main_window.filter_window_se.FilterWindow_SE;
import ui_windows.options_window.OptionsWindow;
import ui_windows.options_window.price_lists_editor.PriceList;
import ui_windows.options_window.price_lists_editor.PriceLists;
import ui_windows.options_window.user_editor.User;
import ui_windows.options_window.user_editor.Users;
import ui_windows.product.Product;
import ui_windows.product.Products;
import ui_windows.request.RequestWindow;
import utils.comparation.prices.SelectPricesForComparisonWindow;
import utils.comparation.te.PricesComparisonTask;
import utils.requests_handlers.ArticlesRequestHandler;
import utils.requests_handlers.ProductsRequestHandler;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {
    private static final Logger logger = LogManager.getLogger(MainWindowController.class);
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
                    ThreadManager.startNewThread(
                            "Price generator Thread",
                            ExecutionIndicator.getInstance().wrapTask(
                                    new PriceGenerationScript(mPriceList.getItems().indexOf(mi), generationMode)),
                            throwable -> {
                                logger.error("Execution error: ", throwable);
                                ThreadManager.executeFxTaskSafe(() ->
                                        Dialogs.showMessage("Ошибка во время выполнения", throwable.getMessage()));
                            }
                    );
                }
            });
        }
    }

    public void importFromNow() {
        FilesImportParameters filesImportParameters = new FilesSelectionWindow().getDataForImport();

        ThreadManager.startNewThread("NOW import thread",
                ExecutionIndicator.getInstance().wrapTask(new ProductDataFileImportTask(filesImportParameters)),
                throwable -> {
                    if (throwable instanceof OperationCancelledByUserException) {
                        logger.info("Пользователь отменил операцию импорта");
                    } else if (throwable instanceof DataNotSelectedException) {
                        logger.info("Не выбраны файлы для импорта, {}", filesImportParameters);
                    } else {
                        logger.error("Произошла ошибка: {}", throwable.getMessage(), throwable);
                    }
                    ThreadManager.executeFxTaskSafe(() -> Dialogs.showMessage("Сведения о выполнении", throwable.getMessage()));
                });
    }

    public void comparePriceLists() {
        List<File> priceListFiles = new SelectPricesForComparisonWindow().getPriceListFiles();
        if (priceListFiles.get(0) != null) {
            ThreadManager.startNewThread("PrLst comp Thread",
                    () -> {
                        logger.info("Starting price-list comparing {} vs {}", priceListFiles.get(0), priceListFiles.get(1));
                        ExecutionIndicator.getInstance().start();

                        PricesComparisonTask pricesComparisonTask = new PricesComparisonTask();
                        pricesComparisonTask.comparePriceFilesAndGenerateReport(priceListFiles.get(0), priceListFiles.get(1), null);
                        ExecutionIndicator.getInstance().stop();
                    }, throwable -> {
                        logger.error(throwable);
                        Platform.runLater(() -> Dialogs.showMessage("Ошибка сравнения прайс-листов", throwable.getMessage()));
                        throw new RuntimeException(throwable);
                    }, () -> ExecutionIndicator.getInstance().stop(),
                    () -> ExecutionIndicator.getInstance().stop()
            );
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
        String request = new RequestWindow().showAndGetValues();
        if (!request.isEmpty()) {
            ProductsRequestHandler.getInstance().findAndShowProductsFromText(request);
        }
    }

    public void actionRequestByShortArticles() {
        logger.info("Opening positions request by short articles window");
        String request = new RequestWindow().showAndGetValues();
        if (!request.isEmpty()) {
            try {
                ArticlesRequestHandler.getInstance().createArticleExistingReport(request);
            } catch (Exception e) {
                logger.warn(e.getMessage());
            }
        }
    }

    public DataSelectorMenu getDataSelectorMenu() {
        return dataSelectorMenu;
    }

    public MainTable getMainTable() {
        return mainTable;
    }
}
