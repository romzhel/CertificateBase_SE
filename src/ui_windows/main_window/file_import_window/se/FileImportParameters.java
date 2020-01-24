package ui_windows.main_window.file_import_window.se;

import javafx.collections.ObservableList;
import ui_windows.main_window.file_import_window.FileImportParameter;

import java.io.File;

public class FileImportParameters {
    private File file;
    private ObservableList<FileImportParameter> parameters;
    private boolean deleteOldImportStatistic;

    private FileImportParameters(){}

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public ObservableList<FileImportParameter> getParameters() {
        return parameters;
    }

    public void setParameters(ObservableList<FileImportParameter> parameters) {
        this.parameters = parameters;
    }

    public boolean isDeleteOldImportStatistic() {
        return deleteOldImportStatistic;
    }

    public void setDeleteOldImportStatistic(boolean deleteOldImportStatistic) {
        this.deleteOldImportStatistic = deleteOldImportStatistic;
    }

    public static class Builder {
        private FileImportParameters parameters;

        public Builder(){
            parameters = new FileImportParameters();
        }

        public Builder setFile(File file) {
            parameters.file = file;
            return this;
        }

        public Builder setDeleteOldStatistic(boolean value) {
            parameters.deleteOldImportStatistic = value;
            return this;
        }
    }
}
