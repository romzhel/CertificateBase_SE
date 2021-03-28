package ui_windows.main_window.file_import_window.te;


import lombok.Data;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class FilesImportParameters {
    private List<File> files = new ArrayList<>();
    private Map<FilesImportParametersEnum, Boolean> params = new HashMap<>();
}
