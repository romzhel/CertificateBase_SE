package ui;

import java.nio.file.Path;
import java.nio.file.Paths;

public class CertificatesReportDialogParams {
    private boolean isStrongFilter;
    private Path targetFolder;
    private Params output;
    private Params finalActions;
    private Params sortOrder;
    private boolean needToOpenReport;

    private CertificatesReportDialogParams() {
        isStrongFilter = true;
        targetFolder = Paths.get("");
        output = Params.ARCHIVE;
        finalActions = Params.COPY_TO_BUFFER;
        sortOrder = Params.SORT_BY_ARTICLE;
        needToOpenReport = true;
    }

    public static CertificatesReportDialogParams create() {
        return new CertificatesReportDialogParams();
    }

    public boolean isStrongFilter() {
        return isStrongFilter;
    }

    public CertificatesReportDialogParams setStrongFilter(boolean strongFilter) {
        isStrongFilter = strongFilter;
        return this;
    }

    public Path getTargetFolder() {
        return targetFolder;
    }

    public CertificatesReportDialogParams setTargetFolder(Path targetFolder) {
        this.targetFolder = targetFolder;
        return this;
    }

    public Params getOutput() {
        return output;
    }

    public CertificatesReportDialogParams setOutput(Params output) {
        this.output = output;
        return this;
    }

    public Params getFinalActions() {
        return finalActions;
    }

    public CertificatesReportDialogParams setFinalActions(Params finalActions) {
        this.finalActions = finalActions;
        return this;
    }

    public boolean isNeedToOpenReport() {
        return needToOpenReport;
    }

    public CertificatesReportDialogParams setNeedToOpenReport(boolean needToOpenReport) {
        this.needToOpenReport = needToOpenReport;
        return this;
    }

    public Params getSortOrder() {
        return sortOrder;
    }

    public CertificatesReportDialogParams setSortOrder(Params sortOrder) {
        this.sortOrder = sortOrder;
        return this;
    }

    @Override
    public String toString() {
        return "CertificatesReportDialogParams{" +
                "isStrongFilter=" + isStrongFilter +
                ", targetFolder=" + targetFolder +
                ", output=" + output +
                ", finalActions=" + finalActions +
                ", sortOrder=" + sortOrder +
                ", needToOpenReport=" + needToOpenReport +
                '}';
    }

    public enum Params {
        ARCHIVE, FILES, COPY_TO_BUFFER, OPEN_FOLDER, SORT_BY_ARTICLE, SORT_BY_ORDER_NUMBER
    }
}
