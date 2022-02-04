package ui_windows.options_window.certificates_editor;

import core.ThreadManager;
import files.reports.CertPositionsToExcel;
import lombok.extern.log4j.Log4j2;
import ui.Dialogs;
import ui_windows.options_window.certificates_editor.certificate_content_editor.CertificateContent;
import ui_windows.product.Product;
import ui_windows.product.Products;
import ui_windows.product.certificatesChecker.CertificatesChecker;
import utils.Utils;

import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;

@Log4j2
public class CertificatePositionsReportScript {
    private Map<Certificate, Map<String, List<Product>>> certProductNameMap = new HashMap<>();

    public void process(List<Certificate> selectedItems) {
        List<Product> productList = Products.getInstance().getItems();

        for (Certificate cert : selectedItems) {
            Map<String, List<Product>> nameProductMap = new HashMap<>();
            certProductNameMap.put(cert, nameProductMap);

            for (CertificateContent content : cert.getContent()) {
                String contentEnum = content.getEquipmentName().replaceAll("\\s", "");
                String[] contentArr = contentEnum.split("\\,");

                for (String name : contentArr) {
                    String fullName = String.format("%s (%s)", name, content.getProductType().getType());

                    nameProductMap.put(fullName, Collections.emptyList());

                    for (Product product : productList) {
                        String calcArticle = "";
                        Matcher calcArticleMatcher = CertificatesChecker.PATTERN.matcher(product.getArticle());
                        if (calcArticleMatcher.matches()) {
                            calcArticle = calcArticleMatcher.group(1) != null ? calcArticleMatcher.group(1) : calcArticleMatcher.group(2);
                        }

                        String calcMaterial = "";
                        Matcher calcMaterialMatcher = CertificatesChecker.PATTERN.matcher(product.getMaterial());
                        if (calcMaterialMatcher.matches()) {
                            calcMaterial = calcMaterialMatcher.group(1) != null ? calcMaterialMatcher.group(1) : calcMaterialMatcher.group(2);
                        }

                        if ((cert.isFullNameMatch() & product.getArticle().equals(name)) |
                                calcArticle.equals(name) |
                                calcMaterial.equals(name)) {
                            nameProductMap.merge(fullName, Collections.singletonList(product), (oldList, addedList) -> {
                                List<Product> result = new ArrayList<>(oldList);
                                result.addAll(addedList);
                                return result;
                            });
                        }
                    }
                }
            }
        }

        String fileName = Utils.getDateTimeForFileName().concat("_certificate_positions_report.xlsx");
        ThreadManager.startNewThread(
                "cert_pos_exp_thr",
                new CertPositionsToExcel(certProductNameMap, Paths.get(fileName)),
                new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        log.error("Exception: {}", throwable.getMessage());
                        ThreadManager.executeFxTaskSafe(() -> Dialogs.showMessage("Ошибка", throwable.getMessage()));
                    }
                }
        );
    }
}
