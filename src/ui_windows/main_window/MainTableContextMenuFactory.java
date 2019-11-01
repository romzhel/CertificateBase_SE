package ui_windows.main_window;

import core.CoreModule;
import core.Dialogs;
import files.ExportToExcel;
import files.SelectorExportWindow;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import ui_windows.product.Product;
import ui_windows.product.certificatesChecker.CertificateVerificationItem;
import ui_windows.product.certificatesChecker.CertificatesChecker;
import ui_windows.product.certificatesChecker.CheckParameters;
import ui_windows.request.CertificateRequestResult;
import utils.Utils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;

public class MainTableContextMenuFactory {
    private static final String SPACE = "     ";
    public static final MenuItem MENU_OPEN_ITEM = new MenuItem(SPACE + "Подробные сведения" + SPACE);
    public static final MenuItem MENU_DELETE_ITEM_FROM_LIST = new MenuItem(SPACE + "Удалить из списка" + SPACE);
    public static final MenuItem MENU_DELETE_ALL_ITEMS = new MenuItem(SPACE + "Очистить список" + SPACE);
    public static final MenuItem MENU_ADD_ITEM_TO_CUSTOM = new MenuItem(SPACE + "Добавить к выбору" + SPACE);
    public static final MenuItem MENU_EXPORT = new MenuItem(SPACE + "Экспорт в Excel" + SPACE);
    public static final MenuItem MENU_CHECK_CERTIFICATES = new MenuItem(SPACE + "Отчет по сертификатам" + SPACE);
    private static boolean wasInited = false;

    public static void init(MainTable mainTable) {
        wasInited = true;
        MENU_OPEN_ITEM.setOnAction(event -> mainTable.displayEditorWindow());
        MENU_DELETE_ITEM_FROM_LIST.setOnAction(event -> {
            CoreModule.getProducts().getTableView().getItems().removeAll(CoreModule.getProducts().getTableView().getSelectionModel().getSelectedItems());
            mainTable.refresh();
        });
        MENU_DELETE_ALL_ITEMS.setOnAction(event -> {
            CoreModule.getProducts().getTableView().getItems().clear();
            mainTable.refresh();
        });
        MENU_ADD_ITEM_TO_CUSTOM.setOnAction(event -> {
            CoreModule.getCustomItems().addAll(CoreModule.getProducts().getTableView().getSelectionModel().getSelectedItems());
        });
        MENU_CHECK_CERTIFICATES.setOnAction(event -> {
            ArrayList<CertificateRequestResult> results = new ArrayList<>();
            HashSet<File> lostFiles = new HashSet<>();
            HashSet<File> allFiles = new HashSet<>();

            for (Product product : mainTable.getItemsForReport()) {
                HashSet<File> foundFiles = new HashSet<>();
                CertificatesChecker certificatesChecker = new CertificatesChecker(product, new CheckParameters());
                for (CertificateVerificationItem cvi : certificatesChecker.getResultTableItems()) {
                    if (cvi.getCertificate() != null && cvi.getCertificate().getFileName() != null && !cvi.getCertificate().getFileName().isEmpty()) {
                        File certificate = new File(CoreModule.getFolders().getCertFolder() + "\\" + cvi.getCertificate().getFileName());
                        if (certificate.exists()) foundFiles.add(certificate);
                        else lostFiles.add(certificate);
                    }
                }

                results.add(new CertificateRequestResult(product, new ArrayList<>(foundFiles)));
                allFiles.addAll(foundFiles);
            }

            if (lostFiles.size() > 0) {
                String message = "";
                for (File file : lostFiles) {
                    message = message.concat("\n").concat("- ").concat(file.getName());
                }

                Dialogs.showMessage("Создание отчёта по сертификатам", "Не удалось найти следующие файлы:" + message);
            }

            for (File file : allFiles) {
                File target = new File(CoreModule.getFolders().getTempFolder() + "\\" + file.getName());

                try {
                    if (!target.exists()) Files.copy(file.toPath(), target.toPath());
                } catch (IOException ee) {
                    System.out.println("copying error " + ee.getMessage());
                    Dialogs.showMessage("Ошибка копирования файла", ee.getMessage());
                }
            }

//        File excelFile = ExcelFile.exportToFile(results);
            File excelFile = new ExportToExcel(results).getFile();

            if (excelFile == null) {
                Dialogs.showMessage("Ошибка создания файла Excel", "Не удалось создать файл сводного отчёта" +
                        " Excel, тем не менее файлы сертификатов помещены в буфер обмена для дальнейшего использования");
                Utils.copyFilesToClipboard(new ArrayList<>(allFiles));
                return;
            }

            allFiles.add(excelFile);
            Utils.copyFilesToClipboard(new ArrayList<>(allFiles));

            /*try {
                Desktop.getDesktop().open(excelFile);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                Dialogs.showMessage("Ошибка открытия сводного файла", e.getMessage());
            }*/
        });
        MENU_EXPORT.setOnAction(event -> new SelectorExportWindow(MainWindow.getMainStage()));
    }

    public static ContextMenu createContextMenuForAllData(){
        return new ContextMenu(
                MENU_OPEN_ITEM,
                new SeparatorMenuItem(),
                MENU_ADD_ITEM_TO_CUSTOM,
                new SeparatorMenuItem(),
                MENU_CHECK_CERTIFICATES,
                new SeparatorMenuItem(),
                MENU_EXPORT);
    }

    public static ContextMenu createContectMenuForCustomItems(){
        return new ContextMenu(
                MENU_OPEN_ITEM,
                new SeparatorMenuItem(),
                MENU_DELETE_ITEM_FROM_LIST,
                MENU_DELETE_ALL_ITEMS,
                new SeparatorMenuItem(),
                MENU_CHECK_CERTIFICATES,
                new SeparatorMenuItem(),
                MENU_EXPORT);
    }
}
