package core.reports;

import files.Folders;
import files.reports.CertificateMatrixReportToExcel;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ui.CertificatesReportDialogParams;
import ui.Dialogs;
import ui_windows.product.Product;
import ui_windows.product.certificatesChecker.CertificateVerificationItem;
import ui_windows.product.certificatesChecker.CertificatesChecker;
import ui_windows.product.certificatesChecker.CheckParameters;
import utils.Archiver;
import utils.Utils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static ui.CertificatesReportDialogParams.Params.*;

public class CertificatesReport {
    private static final Logger logger = LogManager.getLogger(CertificatesReport.class);

    public CertificatesReport() {

    }

    public void check(List<Product> productsForCheck, CertificatesReportDialogParams params) throws Exception {
        long t1 = System.currentTimeMillis();
        logger.trace("запуск скрипта формирования отчёта {} позиций с параметрами {}", productsForCheck.size(), params);
        ConcurrentLinkedQueue<CertificatesReportResult> results = new ConcurrentLinkedQueue<>();
        ConcurrentSkipListSet<String> allNeededFileNames = new ConcurrentSkipListSet<>();

        productsForCheck.parallelStream()
                .forEach(product -> {
//                    logger.trace("обработка {}", product);
                    CertificatesReportResult crr = new CertificatesReportResult(product);
                    CertificatesChecker certsChecker = new CertificatesChecker(
                            product,
                            CheckParameters.getDefault().setEqTypeFiltered(params.isStrongFilter())
                    );

                    for (CertificateVerificationItem cvi : certsChecker.getResultTableItems()) {
                        if (!cvi.getFile().isEmpty()) {
                            crr.addCertificateFile(cvi.getNorm(), cvi.getFile());
                            allNeededFileNames.add(cvi.getFile());
                        }
                    }

                    results.add(crr);
                });

        logger.trace("для отчёта необходимо {} файлов сертификатов", allNeededFileNames.size());

        List<Path> existingFiles = allNeededFileNames.stream()
                .filter(fileName -> fileName != null && !fileName.isEmpty() &&
                        (Files.exists(Folders.getInstance().getCashedCertFolder().resolve(fileName)) ||
                                Files.exists(Folders.getInstance().getCertFolder().resolve(fileName))))
                .map(Paths::get)
                .collect(Collectors.toList());
        logger.trace("найдено имеющихся файлов: {}", existingFiles.size());

        Platform.runLater(() -> Dialogs.showMessage("Отчёт по сертификатам",
                String.format("Найдено %d файлов из %d", existingFiles.size(), allNeededFileNames.size())));

        List<Path> absentFiles = allNeededFileNames.stream()
                .map(Paths::get)
                .filter(fileName -> !existingFiles.contains(fileName))
                .collect(Collectors.toList());
        logger.trace("определено отсутствующих файлов: {}: {}", absentFiles.size(), absentFiles);

        logger.trace("удаление отсутствующих файлов из отчёта");
        for (CertificatesReportResult crr : results) {
            for (Map.Entry<String, List<Path>> entry : crr.getCertFilesGroupedByNorms().entrySet()) {
                List<Path> filePaths = new ArrayList<>(entry.getValue());
                filePaths.removeAll(absentFiles);
                entry.setValue(filePaths);
            }
        }

        List<File> resultTempFiles = existingFiles.stream()
                .map((Function<Path, File>) fileName -> Folders.getInstance().getTempFolder().resolve(fileName).toFile())
                .collect(Collectors.toList());

        logger.trace("формирование отчёта в Excel");
        TreeSet<CertificatesReportResult> productCertificateReport = new TreeSet<>((o1, o2) -> params.getSortOrder() == SORT_BY_ARTICLE ?
                o1.getProduct().getArticle().compareToIgnoreCase(o2.getProduct().getArticle()) :
                o1.getProduct().getMaterial().compareToIgnoreCase(o2.getProduct().getMaterial()));
        productCertificateReport.addAll(results);

        File excelFile = null;
        try {
            excelFile = new CertificateMatrixReportToExcel(productCertificateReport).getFile();
            resultTempFiles.add(excelFile);
        } catch (Exception e) {
            Dialogs.showMessageTS("Ошибка создания файла Excel", "Не удалось создать файл сводного отчёта" +
                    " Excel, тем не менее запрашиваемые файлы будут помещены в буфер обмена для дальнейшего использования");
            logger.error("Ошибка создания сводного файла {}", e.getMessage(), e);
        }

        logger.trace("копирование имеющихся файлов во временную папку");
        for (Path fileName : existingFiles) {
            Path tempFilePath = Folders.getInstance().getTempFolder().resolve(fileName);
            Path sourcePath = Folders.getInstance().getCalcCertFile(fileName);

            try {
                if (Files.notExists(tempFilePath)) {
                    Files.copy(sourcePath, tempFilePath, REPLACE_EXISTING);
                }
            } catch (Exception ee) {
                logger.error("copying error {} {}", fileName, ee.getMessage());
            }
        }

        List<File> resultFiles;

        if (params.getOutput() == ARCHIVE) {
            logger.debug("помещаем файлы ({}) в архив", resultTempFiles.size());
            File zipFile = Folders.getInstance().getTempFolder()
                    .resolve("CertificatesReport_" + Utils.getDateTimeForFileName() + ".zip")
                    .toFile();
            resultFiles = Collections.singletonList(Archiver.addToArchive(resultTempFiles, zipFile));
        } else {
            resultFiles = resultTempFiles;
        }

        if (!params.getTargetFolder().toString().isEmpty()) {
            logger.trace("копирование файлов ({}) в папку пользователя", resultFiles.size());
            for (File file : resultFiles) {
                try {
                    Files.copy(file.toPath(), params.getTargetFolder().resolve(file.getName()), REPLACE_EXISTING);
                } catch (IOException e) {
                    logger.error("ошибка копирования файла {} в папку пользователя {}", file, params.getTargetFolder());
                }
            }
        }

        if (params.getFinalActions() == COPY_TO_BUFFER) {
            logger.debug("копируем файлы ({}) в буфер обмена", resultFiles.size());
            Utils.copyFilesToClipboard(resultFiles);
        } else {
            Desktop.getDesktop().open(params.getTargetFolder().toString().isEmpty() ?
                    Folders.getInstance().getTempFolder().toFile() : params.getTargetFolder().toFile());
        }

        if (params.isNeedToOpenReport()) {
            logger.debug("открываем файл сводного отчёта {}", excelFile);
            Desktop.getDesktop().open(excelFile);
        }

        logger.trace("формирование отчёта завершено за {} мсек", System.currentTimeMillis() - t1);
    }
}
