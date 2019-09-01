package ui_windows.main_window.file_import_window;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FileImportTableItem {
    private StringProperty tableTitle;
    private StringProperty productField;
    private BooleanProperty importValue;
    private BooleanProperty logHistory;
    private int columnIndex;
    private boolean logLastImportCodes;

    public FileImportTableItem(String tableTitle, String productProperty, boolean importValue, boolean logHistory,
                               int columnIndex, boolean logLastImportCodes) {
        this.tableTitle = new SimpleStringProperty(tableTitle);
        this.productField = new SimpleStringProperty(productProperty);
        this.importValue = new SimpleBooleanProperty(importValue);
        this.logHistory = new SimpleBooleanProperty(logHistory);
        this.columnIndex = columnIndex;
        this.logLastImportCodes = logLastImportCodes;
    }

    public FileImportTableItem(String fieldName, String productProperty) {
        this.tableTitle = new SimpleStringProperty(fieldName);
        this.productField = new SimpleStringProperty(productProperty);
    }

    public String getTableTitle() {
        return tableTitle.get();
    }

    public StringProperty tableTitleProperty() {
        return tableTitle;
    }

    public void setTableTitle(String tableTitle) {
        this.tableTitle.set(tableTitle);
    }

    public String getProductField() {
        return productField.get();
    }

    public StringProperty productFieldProperty() {
        return productField;
    }

    public void setProductField(String productField) {
        this.productField.set(productField);
    }

    public boolean isImportValue() {
        return importValue.get();
    }

    public BooleanProperty importValueProperty() {
        return importValue;
    }

    public void setImportValue(boolean importValue) {
        this.importValue.set(importValue);
    }

    public boolean isLogHistory() {
        return logHistory.get();
    }

    public BooleanProperty logHistoryProperty() {
        return logHistory;
    }

    public void setLogHistory(boolean logHistory) {
        this.logHistory.set(logHistory);
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    public boolean isLogLastImportCodes() {
        return logLastImportCodes;
    }

    public void setLogLastImportCodes(boolean logLastImportCodes) {
        this.logLastImportCodes = logLastImportCodes;
    }

    @Override
    public String toString() {
        return tableTitle + ", " + productField + ", " + isImportValue() + ", " + logHistory;
    }
}
