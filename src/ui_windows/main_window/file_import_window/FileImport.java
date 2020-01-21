package ui_windows.main_window.file_import_window;

import core.CoreModule;
import core.Dialogs;
import database.ProductsDB;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import ui_windows.main_window.DataSelectorMenu;
import ui_windows.main_window.MainWindow;
import ui_windows.main_window.MainWindowsController;
import ui_windows.product.Product;
import ui_windows.product.Products;
import ui_windows.product.data.DataItem;
import utils.comparation.products.ProductsComparator;
import utils.comparation.products.ProductsComparatorResult;

import java.util.ArrayList;
import java.util.HashSet;

import static ui_windows.product.data.DataItem.DATA_EMPTY;
import static ui_windows.product.data.DataItem.DATA_ORDER_NUMBER;

public class FileImport {
    private FileImportWindowController controller;
    private ExcelFile excelFile;
    private ColumnsMapper mapper;

    public FileImport() {
        mapper = new ColumnsMapper();
        excelFile = new ExcelFile(mapper);
        if (excelFile.open(Dialogs.selectNOWFile(MainWindow.getMainStage()))) {//open file
            new FileImportWindow(this);
            controller.cbSheetNames.getItems().addAll(excelFile.getSheetNames());
            controller.cbSheetNames.getSelectionModel().select(0);
        }
    }

    public void startImport(ObservableList<FileImportParameter> parameters) {
        MainWindow.setProgress(-1);

        new Thread(() -> {
            FileImportParameter[] comparingParameters = mapper.getColumnsForComparing(parameters).toArray(new FileImportParameter[]{});

            ArrayList<Product> resetedItems = controller.cbxDelPrevStat.isSelected() ? CoreModule.getProducts().resetLastImportCodes() : new ArrayList<>();
            HashSet<Product> changedItemsForDB = new HashSet<>(resetedItems);

            Products compProducts = new Products();
            compProducts.setItems(excelFile.getProductsFromCurrentSheet());

            ProductsComparator pc = new ProductsComparator(CoreModule.getProducts(), compProducts, comparingParameters);
            ProductsComparatorResult lastComparationResult = pc.getResult();

            if (lastComparationResult.getNewItems().size() + lastComparationResult.getChangedItems().size() > 0) {
                lastComparationResult.saveReport();
            }

            changedItemsForDB.addAll(lastComparationResult.getChangedItems());

            if ((lastComparationResult.getNewItems().size() + lastComparationResult.getChangedItems().size()) > 0) {
                MainWindowsController mwc = MainWindow.getFxmlLoader().getController();
                mwc.getDataSelectorMenu().selectMenuItem(DataSelectorMenu.MENU_DATA_LAST_IMPORT_RESULT);
            }

            if (lastComparationResult.getNewItems().size() > 0)
                new ProductsDB().putData(lastComparationResult.getNewItems());// save new items to db
            if (changedItemsForDB.size() > 0)
                new ProductsDB().updateData(new ArrayList<>(changedItemsForDB));//save changed items to db

            System.out.println("writing to DB finished");

            MainWindow.setProgress(0);

            Platform.runLater(() -> Dialogs.showMessage("Результаты импорта", "Новых позиций: " +
                    lastComparationResult.getNewItems().size() + "\nИзменённых позиций: " +
                    lastComparationResult.getChangedItems().size()));
        }).start();
    }

    public void displayTitlesAndMapping(int sheetIndex) {
        excelFile.setCurrentSheet(sheetIndex);
        controller.tvFields.getItems().clear();

        DataItem dataItem;
        String[] fieldsName = excelFile.getCurrentSheetTitles().getAll();//получаем заголовки из файла
        String title;

        for (int colIndex = 0; colIndex < fieldsName.length; colIndex++) {
            title = fieldsName[colIndex];
            if (title != null && !title.trim().isEmpty()) {
                dataItem = mapper.getDataItemByTitle(title);

                controller.tvFields.getItems().add(new FileImportParameter(
                        title,
                        dataItem,
                        dataItem != DATA_EMPTY,
                        dataItem != DATA_EMPTY,
                        colIndex,
                        true));
            }
        }
    }

    public void setController(FileImportWindowController controller) {
        this.controller = controller;
    }

    public ExcelFile getExcelFile() {
        return excelFile;
    }

    public void setExcelFile(ExcelFile excelFile) {
        this.excelFile = excelFile;
    }

    public boolean checkNameDoubles() {
        boolean hasntMaterial = true;
        ArrayList<DataItem> selectedItems = new ArrayList<>();
        for (FileImportParameter fiti : controller.tvFields.getItems()) {
            if (fiti.getDataItem() != DATA_EMPTY) selectedItems.add(fiti.getDataItem());
            if (fiti.getDataItem() == DATA_ORDER_NUMBER) hasntMaterial = false;
        }

        HashSet<DataItem> singlesNames = new HashSet<>(selectedItems);
        return selectedItems.size() != singlesNames.size() || selectedItems.size() < 2 || hasntMaterial;
    }
}
