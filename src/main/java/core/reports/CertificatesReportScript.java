package core.reports;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ui.CertificatesReportDialog;
import ui.CertificatesReportDialogParams;
import ui.Dialogs;
import ui_windows.ExecutionIndicator;
import ui_windows.main_window.MainTable;
import ui_windows.product.certificatesChecker.CertificatesChecker;

public class CertificatesReportScript {
    private static final Logger logger = LogManager.getLogger(CertificatesReportScript.class);

    public static CertificatesReportScript create() {
        return new CertificatesReportScript();
    }

    public void startReport(final MainTable mainTable) {
        logger.trace("запуск отчёта по сертификатам, позиций: {}", mainTable.getItemsForReport().size());
        CertificatesReportDialogParams params = CertificatesReportDialogParams.createDefault();
        try {
            params = CertificatesReportDialog.create().showAndGetParams(params);
            logger.debug("параметры создания отчёта {}", params);
        } catch (RuntimeException re) {
            logger.info("операция отменена пользователем");
            return;
        } catch (Exception e) {
            logger.error("ошибка отчёта по сертификатам {}", e.getMessage(), e);
            return;
        }

        CertificatesReportDialogParams finalParams = params;
        Thread certificateReportThread = new Thread(() -> {
            logger.trace("запуск потока формирования отчёта по сертификатам");
            ExecutionIndicator.getInstance().start();
            try {
                CertificatesChecker.minCheckTime = 1000;
                CertificatesChecker.maxCheckTime = 0;
                CertificatesChecker.averageCheckTime = 2;
                CertificatesChecker.minNormsTime = 1000;
                CertificatesChecker.maxNormsTime = 0;
                CertificatesChecker.averageNormsTime = 2;
                new CertificatesReport().treat(mainTable.getItemsForReport(), finalParams);
                logger.info("поиск сертификатов (мин/ср/макс) {}/{}/{}, проверка норм (мин/ср/макс) {}/{}/{}",
                        CertificatesChecker.minCheckTime, CertificatesChecker.averageCheckTime, CertificatesChecker.maxCheckTime,
                        CertificatesChecker.minNormsTime, CertificatesChecker.averageNormsTime, CertificatesChecker.maxNormsTime);
            } catch (Exception e) {
                logger.error("ошибка создания отчёта по сертификатам {}", e.getMessage(), e);
                Dialogs.showMessageTS("Отчёт по сертификатам", "Произошла ошибка:\n\n" + e.getMessage());
            } finally {
                ExecutionIndicator.getInstance().stop();
            }
        });
        certificateReportThread.setName("cert report thread");
        certificateReportThread.setDaemon(true);
        certificateReportThread.start();
    }
}
