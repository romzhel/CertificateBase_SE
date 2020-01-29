package ui_windows.main_window.file_import_window.se;

import javafx.collections.ObservableList;
import ui_windows.main_window.file_import_window.FileImportParameter;
import ui_windows.product.Product;

import java.io.File;
import java.util.ArrayList;

public class FileImport {
    private ExcelFile excelFile;
    private boolean deleteOldStatistic;
    private SelectionListener selectionListener;
    private ActionsToDoListener actionsToDoListener;
    private ArrayList<Product> result;

    public FileImport() {

    }

    public ArrayList<Product> getProductsInAutoMode(File file, int sheetIndex) {
        excelFile = new ExcelFile(file);
        if (excelFile.open()) {
            excelFile.getImportParameters(sheetIndex);
            return excelFile.getData();
        }
        return null;
    }

    public void getProductsInManualMode(File file, ActionsToDoListener actionsToDoListener) {
        this.selectionListener = selectionListener;
        excelFile = new ExcelFile(file);
        deleteOldStatistic = false;
        if (excelFile.open()) {
            new FileImportWindow(this);

            selectionListener = parameters -> {
                excelFile.getMapper().setParameters(new ArrayList<>(parameters));
                result = excelFile.getData();
                actionsToDoListener.ActionToDoEvent();
            };
        }
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

    interface SelectionListener {
        void selectionEvent(ObservableList<FileImportParameter> parameters);
    }

    public SelectionListener getSelectionListener() {
        return selectionListener;
    }

    interface ActionsToDoListener {
        void ActionToDoEvent();
    }

    public void setActionsToDoListener(ActionsToDoListener actionsToDoListener) {
        this.actionsToDoListener = actionsToDoListener;
    }

    public ArrayList<FileImportParameter> getParameters() {
        return excelFile.getMapper().getParameters();
    }

    public ArrayList<Product> getResult() {
        return result;
    }
}
