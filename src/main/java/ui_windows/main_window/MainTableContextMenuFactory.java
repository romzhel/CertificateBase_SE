package ui_windows.main_window;

import core.InitModule;
import core.reports.CertificatesReport;
import files.SelectorExportWindow;
import files.reports.ReportToExcel;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ui.CertificatesReportDialog;
import ui.CertificatesReportDialogParams;
import ui.Dialogs;
import ui_windows.ExecutionIndicator;
import ui_windows.product.Product;
import ui_windows.product.data.DataItem;
import utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static core.SharedData.SHD_DATA_SET;
import static core.SharedData.SHD_SELECTED_PRODUCTS;

public class MainTableContextMenuFactory {
    private static final String SPACE = "     ";
    public static final MenuItem MENU_OPEN_ITEM = new MenuItem(SPACE + "Редактировать" + SPACE);
    public static final MenuItem MENU_DELETE_ITEMS_FROM_LIST = new MenuItem(SPACE + "Удалить из списка" + SPACE);
    public static final MenuItem MENU_DELETE_ALL_ITEMS = new MenuItem(SPACE + "Очистить список" + SPACE);
    public static final MenuItem MENU_ADD_ITEM_TO_CUSTOM = new MenuItem(SPACE + "Добавить к выбору" + SPACE);
    public static final MenuItem MENU_EXPORT = new MenuItem(SPACE + "Экспорт в Excel" + SPACE);
    public static final MenuItem MENU_CHECK_CERTIFICATES = new MenuItem(SPACE + "Отчет по сертификатам" + SPACE);
    private static final Logger logger = LogManager.getLogger(MainTableContextMenuFactory.class);
    private static boolean wasInited = false;
    private static ContextMenu contextMenu = new ContextMenu();

    public static void init(final MainTable mainTable) {
        wasInited = true;
        MENU_OPEN_ITEM.setOnAction(event -> mainTable.displayEditorWindow());
        MENU_DELETE_ITEMS_FROM_LIST.setOnAction(event -> {
//            List<Product> itemsForDelete = SHD_SELECTED_PRODUCTS.getData();
            InitModule.getCustomItems().removeAll(SHD_SELECTED_PRODUCTS.getData());
            SHD_DATA_SET.setData(MainTableContextMenuFactory.class, new ArrayList<>(InitModule.getCustomItems()));

//            CoreModule.getProducts().getTableView().getItems().removeAll(itemsForDelete);
//            mainTable.refresh();
        });
        MENU_DELETE_ALL_ITEMS.setOnAction(event -> {
            InitModule.getCustomItems().clear();
            SHD_DATA_SET.setData(MainTableContextMenuFactory.class, new ArrayList<Product>());

//            CoreModule.getProducts().getTableView().getItems().clear();
//            mainTable.refresh();
        });
        MENU_ADD_ITEM_TO_CUSTOM.setOnAction(event -> {
//            ObservableList itemsForAdd = CoreModule.getProducts().getTableView().getSelectionModel().getSelectedItems();
            InitModule.getCustomItems().addAll(SHD_SELECTED_PRODUCTS.getData());
//            SHD_DATA_SET.setData(new ArrayList<>(CoreModule.getCustomItems()));
        });
        MENU_CHECK_CERTIFICATES.setOnAction(event -> {
//            new Thread(() -> {
//                logger.info("Start certificates report");
            startCertsReport(mainTable);
//                logger.info("Certificates report is finished");
//            }).start();
        });
        MENU_EXPORT.setOnAction(event -> {
            ArrayList<DataItem> columns = new SelectorExportWindow(MainWindow.getMainStage()).getColumns();
            new Thread(() -> {
                List<File> files = new Dialogs().selectAnyFileTS(MainWindow.getMainStage(), "Выбор места сохранения",
                        Dialogs.EXCEL_FILES, Utils.getDateTimeForFileName().concat("_report.xlsx"));
                if (files != null && files.get(0) != null) {
//                    logger.info("Exporting to Excel");
                    Utils.openFile(new ReportToExcel().export(columns, SHD_SELECTED_PRODUCTS.getData(), files.get(0)));
                }
            }).start();
        });
    }

    private static void startCertsReport(final MainTable mainTable) {
        logger.trace("запуск отчёта по сертификатам, позиций: {}", mainTable.getItemsForReport().size());
        CertificatesReportDialogParams params = CertificatesReportDialogParams.create();
        try {
            params = CertificatesReportDialog.create().showAndGetParams(params);
            logger.debug("параметры создания отчёта {}", params);
        } catch (RuntimeException re) {
            logger.info("операция отменена пользователем");
            return;
        } catch (Exception e) {
            logger.error("ошибка отчёта по сертификатам {}", e.getMessage(), e);
            return;
        }

        CertificatesReportDialogParams finalParams = params;
        Thread certificateReportThread = new Thread(() -> {
            logger.trace("запуск потока формирования отчёта по сертификатам");
            ExecutionIndicator.getInstance().start();
            try {
                new CertificatesReport().check(mainTable.getItemsForReport(), finalParams);
            } /*catch (RuntimeException re) {
                logger.warn("{}", re.getMessage(), re);
            }*/ catch (Exception e) {
                logger.error("ошибка создания отчёта по сертификатам {}", e.getMessage(), e);
                Dialogs.showMessageTS("Отчёт по сертификатам", "Произошла ошибка:\n\n" + e.getMessage());
            } finally {
                ExecutionIndicator.getInstance().stop();
            }
        });
        certificateReportThread.setName("cert report thread");
        certificateReportThread.setDaemon(true);
        certificateReportThread.start();
    }

    public static ContextMenu createContextMenuForAllData() {
        contextMenu.getItems().clear();
        contextMenu.getItems().addAll(
                MENU_OPEN_ITEM,
                new SeparatorMenuItem(),
                MENU_ADD_ITEM_TO_CUSTOM,
                new SeparatorMenuItem(),
                MENU_CHECK_CERTIFICATES,
                new SeparatorMenuItem(),
                MENU_EXPORT);
        return contextMenu;
    }

    public static ContextMenu createContectMenuForCustomItems() {
        contextMenu.getItems().clear();
        contextMenu.getItems().addAll(
                MENU_OPEN_ITEM,
                new SeparatorMenuItem(),
                MENU_DELETE_ITEMS_FROM_LIST,
                MENU_DELETE_ALL_ITEMS,
                new SeparatorMenuItem(),
                MENU_CHECK_CERTIFICATES,
                new SeparatorMenuItem(),
                MENU_EXPORT);
        return contextMenu;
    }
}
