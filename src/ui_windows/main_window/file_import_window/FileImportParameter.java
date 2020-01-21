package ui_windows.main_window.file_import_window;

import javafx.beans.property.*;
import ui_windows.product.MultiEditorItem;
import ui_windows.product.data.DataItem;

public class FileImportParameter {
    private StringProperty tableTitle;
    private ObjectProperty<DataItem> dataItem;
    private BooleanProperty importValue;
    private BooleanProperty logHistory;
    private int columnIndex;
    private boolean logLastImportCodes;

    public FileImportParameter(String tableTitle, DataItem dataItem, boolean importValue, boolean logHistory,
                               int columnIndex, boolean logLastImportCodes) {
        this.tableTitle = new SimpleStringProperty(tableTitle);
        this.dataItem = new SimpleObjectProperty<>(dataItem);
        this.importValue = new SimpleBooleanProperty(importValue);
        this.logHistory = new SimpleBooleanProperty(logHistory);
        this.columnIndex = columnIndex;
        this.logLastImportCodes = logLastImportCodes;
    }

    public FileImportParameter(MultiEditorItem multiEditorItem) {
        this.dataItem = new SimpleObjectProperty<>(multiEditorItem.getDataItem());
        importValue = new SimpleBooleanProperty(multiEditorItem.getCommonValue() != null && multiEditorItem.isCanBeSaved());
        logHistory = new SimpleBooleanProperty(true);
        logLastImportCodes = true;
    }

    public FileImportParameter(String fieldName, DataItem dataItem) {
        this.tableTitle = new SimpleStringProperty(fieldName);
        this.dataItem = new SimpleObjectProperty<>(dataItem);
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

    public DataItem getDataItem() {
        return dataItem.get();
    }

    public ObjectProperty<DataItem> dataItemProperty() {
        return dataItem;
    }

    public void setDataItem(DataItem dataItem) {
        this.dataItem.set(dataItem);
    }

    @Override
    public String toString() {
        return tableTitle + ", " + dataItem.get().getField().getName() + ", " + isImportValue() + ", " + logHistory;
    }
}
