package ui_windows.main_window.file_import_window.se;

import javafx.application.Platform;
import ui_windows.main_window.file_import_window.FileImportParameter;
import ui_windows.product.Product;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public class FileImport {
    private ExcelFile excelFile;
    private boolean deleteOldStatistic;
    private boolean resetCostNonFoundItem;
    private ArrayList<Product> productItems;

    public FileImport() {

    }

    public ArrayList<Product> getProductsInAutoMode(File file, int sheetIndex) {
        excelFile = new ExcelFile(file);
        if (excelFile.open()) {
            excelFile.getImportParameters(sheetIndex);
            productItems = excelFile.getData();
            return productItems;
        }
        return null;
    }

    public ArrayList<Product> getProductsInManualMode(File file) {
        excelFile = new ExcelFile(file);
        deleteOldStatistic = false;
        resetCostNonFoundItem = false;
        if (excelFile.open()) {
            AtomicReference<List<FileImportParameter>> parameters = new AtomicReference<>();
            if (!Thread.currentThread().getName().equals("JavaFX Application Thread")) {
                CountDownLatch inputWaiting = new CountDownLatch(1);

                Platform.runLater(() -> {
                    parameters.set(new FileImportWindow(FileImport.this).getParameters());
                    inputWaiting.countDown();
                });

                try {
                    inputWaiting.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                parameters.set(new FileImportWindow(FileImport.this).getParameters());
            }

            excelFile.getMapper().setParameters(parameters.get());
            productItems = excelFile.getData();
            return productItems;
        }
        return null;
    }

    public ExcelFile getExcelFile() {
        return excelFile;
    }

    public void setDeleteOldStatistic(boolean deleteOldStatistic) {
        this.deleteOldStatistic = deleteOldStatistic;
    }

    public boolean isDeleteOldStatistic() {
        return deleteOldStatistic;
    }

    public List<FileImportParameter> getParameters() {
        return excelFile.getMapper().getParameters();
    }

    public ArrayList<Product> getProductItems() {
        return productItems;
    }

    public boolean isResetCostNonFoundItem() {
        return resetCostNonFoundItem;
    }

    public void setResetCostNonFoundItem(boolean resetCostNonFoundItem) {
        this.resetCostNonFoundItem = resetCostNonFoundItem;
    }
}
