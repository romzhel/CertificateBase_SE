package ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.nio.file.Paths;

import static ui.CertificatesReportDialogParams.Params.*;

public class CertificatesReportDialog {
    public CheckBox cbxStrongFilter;
    public CheckBox cbxOpenSummaryReport;
    public TextField tfResultPath;
    public RadioButton rbtnResultArchive;
    public RadioButton rbtnResultFiles;
    public RadioButton rbtnCopyClipBoard;
    public RadioButton rbtnOpenFolder;
    public RadioButton rbtnSortByArticle;
    public RadioButton rbtnSortByOrderNumber;
    public Button btnBrowse;
    public Button btnOk;
    public Button btnCancel;
    boolean isCancelled = false;
    private Stage stage;

    private CertificatesReportDialog() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/certificates_report_dialog.fxml"));
        loader.setController(this);
        AnchorPane root = loader.load();
        stage = new Stage();
        stage.setTitle("Отчёт по сертификатам");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UTILITY);
        stage.setScene(new Scene(root));
    }

    public static CertificatesReportDialog create() throws Exception {
        return new CertificatesReportDialog();
    }

    public CertificatesReportDialogParams showAndGetParams(CertificatesReportDialogParams params) {
        ToggleGroup resultTypeGroup = new ToggleGroup();
        rbtnResultArchive.setToggleGroup(resultTypeGroup);
        rbtnResultFiles.setToggleGroup(resultTypeGroup);
        rbtnResultArchive.setSelected(params.getOutput() == ARCHIVE);
        rbtnResultFiles.setSelected(params.getOutput() == FILES);

        resultTypeGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            params.setOutput(newValue == rbtnResultArchive ? ARCHIVE : FILES);
        });

        ToggleGroup finalActionsGroup = new ToggleGroup();
        rbtnCopyClipBoard.setToggleGroup(finalActionsGroup);
        rbtnOpenFolder.setToggleGroup(finalActionsGroup);
        rbtnCopyClipBoard.setSelected(params.getFinalActions() == COPY_TO_BUFFER);
        rbtnOpenFolder.setSelected(params.getFinalActions() == OPEN_FOLDER);

        finalActionsGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            params.setFinalActions(newValue == rbtnCopyClipBoard ? COPY_TO_BUFFER : OPEN_FOLDER);
        });

        ToggleGroup sortOrderToggleGroup = new ToggleGroup();
        rbtnSortByArticle.setToggleGroup(sortOrderToggleGroup);
        rbtnSortByOrderNumber.setToggleGroup(sortOrderToggleGroup);
        rbtnSortByArticle.setSelected(params.getSortOrder() == SORT_BY_ARTICLE);
        rbtnSortByOrderNumber.setSelected(params.getSortOrder() == SORT_BY_ORDER_NUMBER);

        sortOrderToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            params.setSortOrder(newValue == rbtnSortByArticle ? SORT_BY_ARTICLE : SORT_BY_ORDER_NUMBER);
        });

        cbxStrongFilter.setSelected(params.isStrongFilter());
        cbxStrongFilter.selectedProperty().addListener((observable, oldValue, newValue) -> params.setStrongFilter(newValue));
        cbxOpenSummaryReport.setSelected(params.isNeedToOpenReport());
        cbxOpenSummaryReport.selectedProperty().addListener((observable, oldValue, newValue) -> params.setNeedToOpenReport(newValue));

        btnBrowse.setOnAction(event -> {
            File folder = new Dialogs().selectFolder(stage, "Выбор папки для сохранения результатов");
            if (folder != null) {
                tfResultPath.setText(folder.getPath());
            }
        });

        tfResultPath.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                params.setTargetFolder(Paths.get(newValue));
            }
        });

        isCancelled = false;

        stage.setOnCloseRequest(event -> {
            event.consume();
            isCancelled = true;
            stage.close();
        });

        btnCancel.setOnAction(event -> {
            isCancelled = true;
            stage.close();
        });

        btnOk.setOnAction(event -> stage.close());

        btnOk.requestFocus();
        stage.showAndWait();

        if (isCancelled) {
            throw new RuntimeException("Операция отменена пользователем");
        }

        return params;
    }
}
