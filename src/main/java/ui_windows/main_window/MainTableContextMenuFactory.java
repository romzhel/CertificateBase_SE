package ui_windows.main_window;

import core.Dialogs;
import core.InitModule;
import files.Folders;
import files.SelectorExportWindow;
import files.reports.CertificateMatrixReportToExcel;
import files.reports.ReportToExcel;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ui_windows.ExecutionIndicator;
import ui_windows.product.Product;
import ui_windows.product.certificatesChecker.CertificateVerificationItem;
import ui_windows.product.certificatesChecker.CertificatesChecker;
import ui_windows.product.certificatesChecker.CheckParameters;
import ui_windows.product.data.DataItem;
import ui_windows.request.CertificateRequestResult;
import utils.Utils;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
            new Thread(() -> {
                logger.info("Get certificates report");
                ExecutionIndicator.getInstance().start();
                startCertsReport(mainTable);
                ExecutionIndicator.getInstance().stop();
                logger.info("Certificates report is finished");
            }).start();
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

    private static void startCertsReport(MainTable mainTable) {
        logger.trace("certificates reporting started for {} items", mainTable.getItemsForReport().size());
        List<CertificateRequestResult> results = new ArrayList<>();
        Set<File> lostFiles = new HashSet<>();
        Set<File> allFiles = new HashSet<>();

        Set<File> neededFiles = new HashSet<>();
        Set<File> allNeededFiles = new HashSet<>();

        for (Product product : mainTable.getItemsForReport()) {
//            logger.trace("finding certs for {}", product);
            neededFiles.clear();
//            Set<File> foundFiles = new HashSet<>();
            CertificatesChecker certificatesChecker = new CertificatesChecker(product, new CheckParameters());
            for (CertificateVerificationItem cvi : certificatesChecker.getResultTableItems()) {
                if (cvi.getCertificate() != null && cvi.getCertificate().getFileName() != null && !cvi.getCertificate().getFileName().isEmpty()) {
                    File certificateFile = new File(Folders.getInstance().getCertFolder() + "\\" + cvi.getCertificate().getFileName());
                    neededFiles.add(certificateFile);
                }
            }

            allNeededFiles.addAll(neededFiles);
            results.add(new CertificateRequestResult(product, new ArrayList<>(neededFiles)));
//            allFiles.addAll(foundFiles);
        }

        for (File file : allNeededFiles) {
            File targetTempFolder = new File(Folders.getInstance().getTempFolder() + "\\" + file.getName());
            logger.trace("try to copy certificate file {} -> {}", file, targetTempFolder);

            try {
                if (!targetTempFolder.exists()) {
                    Files.copy(Utils.getFileFromMultiLocation(
                            file.getName(),
                            Folders.getInstance().getCashedCertFolder(), Folders.getInstance().getCertFolder().toPath()
                            ),
                            targetTempFolder.toPath());
                }
                allFiles.add(targetTempFolder);
            } catch (Exception ee) {
                logger.trace("copying error {}", ee.getMessage());
//                Dialogs.showMessage("Ошибка копирования файла", ee.getMessage());
                lostFiles.add(file);
            }
        }

        if (lostFiles.size() > 0) {
            String message = "";
            for (File file : lostFiles) {
                message = message.concat("\n").concat("- ").concat(file.getName());
            }

            Dialogs.showMessageTS("Создание отчёта по сертификатам", "Не удалось найти следующие файлы:" + message);

            for (CertificateRequestResult crr : results) {
                crr.getFiles().removeAll(lostFiles);
            }
        }

//        File excelFile = ExcelFile.exportToFile(results);
        File excelFile = new CertificateMatrixReportToExcel(results).getFile();

        if (excelFile == null) {
            Dialogs.showMessage("Ошибка создания файла Excel", "Не удалось создать файл сводного отчёта" +
                    " Excel, тем не менее файлы сертификатов помещены в буфер обмена для дальнейшего использования");
            Utils.copyFilesToClipboard(new ArrayList<>(allFiles));
            return;
        }
//        logger.trace("copy {} files to clipboard", allFiles.size());
        allFiles.add(excelFile);
        Utils.copyFilesToClipboard(new ArrayList<>(allFiles));
//        logger.trace("certs reporting finished");
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
