package ui_windows.main_window.file_import_window;

import core.CoreModule;
import core.Dialogs;
import database.ProductsDB;
import javafx.application.Platform;
import ui_windows.main_window.MainWindow;
import ui_windows.main_window.MainWindowsController;
import ui_windows.product.Product;
import ui_windows.product.Products;
import utils.comparation.ProductsComparator;
import utils.comparation.ProductsComparatorResult;

import java.util.ArrayList;
import java.util.HashSet;

public class FileImport {
    private FileImportWindowController controller;
    private ExcelFile2 excelFile;
    //    private ColumnsMapper2 mapper;
    private ProductsComparatorResult lastComparationResult;

    public FileImport() {
        excelFile = new ExcelFile2();
        if (excelFile.open(Dialogs.selectNOWFile(MainWindow.getMainStage()))) {//open file
            new FileImportWindow(this);
            controller.cbSheetNames.getItems().addAll(excelFile.getSheetNames());
            controller.cbSheetNames.getSelectionModel().select(0);
        }
    }

    public void startImport() {
        MainWindow.setProgress(-1);


        new Thread(() -> {
            ColumnsMapper2.FieldForImport[] ffis = excelFile.getMapper().getFieldsForImport(controller.tvFields.getItems());

            ArrayList<Product> resetedItems = controller.cbxDelPrevStat.isSelected() == false ? new ArrayList<>() : CoreModule.getProducts().resetLastImportCodes();
            HashSet<Product> changedItemsForDB = new HashSet<>(resetedItems);

            Products compProducts;
            compProducts = new Products();
            compProducts.setItems(excelFile.getProductsFromCurrentSheet());

            ProductsComparator pc = new ProductsComparator(CoreModule.getProducts(), compProducts, ffis);//compare new and existing products

            lastComparationResult = pc.getResult();
            changedItemsForDB.addAll(lastComparationResult.getChangedItems());

            if ((lastComparationResult.getNewItems().size() + lastComparationResult.getChangedItems().size()) > 0) {
                MainWindowsController mwc = MainWindow.getFxmlLoader().getController();
                mwc.rmiLastImportResult.setSelected(true);
                mwc.selectLastImportResult();
            }

            if (lastComparationResult.getNewItems().size() > 0) new ProductsDB().putData(lastComparationResult.getNewItems());// save new items to db
            if (changedItemsForDB.size() > 0) new ProductsDB().updateData(new ArrayList<>(changedItemsForDB));//save changed items to db

            System.out.println("writing to DB finished");

            MainWindow.setProgress(0);

            Platform.runLater(() -> Dialogs.showMessage("Результаты импорта", "Новых позиций: " +
                    lastComparationResult.getNewItems().size() + "\nИзменённых позиций: " +
                    lastComparationResult.getChangedItems().size()));
        }).start();

        /*MainWindow.setProgress(-1);

            clearOldResult = false;


            new Thread(() -> {
                rmiLastImportResult.setSelected(true);

//                selectLastImportResult();

                ProductsDB request = new ProductsDB();

                request.putData(lastComparationResult.getNewItems());// save new items to db
                request.updateData(new ArrayList<>(changedItemsForDB));//save changed items to db

                System.out.println("writing to DB finished");

                MainWindow.setProgress(0);

                Platform.runLater(() -> Dialogs.showMessage("Результаты импорта", "Новых позиций: " +
                        lastComparationResult.getNewItems().size() + "\nИзменённых позиций: " +
                        lastComparationResult.getChangedItems().size()));

            }).start();*/
    }

    public void addTitlesToTable(int sheetIndex) {
        excelFile.setCurrentSheet(sheetIndex);
        controller.tvFields.getItems().clear();

        String fieldName;
        String[] fieldsName = excelFile.getCurrentSheetTitles().getAll();
        String title;

        for (int colIndex = 0; colIndex < fieldsName.length; colIndex++) {
            title = fieldsName[colIndex];
            if (title != null && !title.trim().isEmpty()) {
                fieldName = excelFile.getMapper().getNameByTitle(title);

                controller.tvFields.getItems().add(
                        new FileImportTableItem(title, fieldName, fieldName.isEmpty() ? false : true,
                                fieldName.isEmpty() ? false : true, colIndex, true));
            }
        }
    }

    public void setController(FileImportWindowController controller) {
        this.controller = controller;
    }

    public ExcelFile2 getExcelFile() {
        return excelFile;
    }

    public void setExcelFile(ExcelFile2 excelFile) {
        this.excelFile = excelFile;
    }

    public boolean checkNameDoubles() {
        ArrayList<String> selectedNames = new ArrayList<>();
        for (FileImportTableItem fiti : controller.tvFields.getItems()) {
            if (!fiti.getProductField().isEmpty()) selectedNames.add(fiti.getProductField());
        }

        HashSet<String> singlesNames = new HashSet<>(selectedNames);
        return selectedNames.size() != singlesNames.size();
    }

    public ProductsComparatorResult getLastComparationResult() {
        return lastComparationResult;
    }
}
