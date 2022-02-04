package ui_windows.options_window.certificates_editor;

import core.ThreadManager;
import files.reports.CertCountriesToExcel;
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
public class CertificateCountriesReportScript {
    private Map<Certificate, Map<String, Map<String, List<Product>>>> certMap = new HashMap<>();

    public void process(List<Certificate> selectedItems) {
        List<Product> productList = Products.getInstance().getItems();

        for (Certificate cert : selectedItems) {
            Map<String, Map<String, List<Product>>> namesMap = new HashMap<>();
            certMap.put(cert, namesMap);

            for (CertificateContent content : cert.getContent()) {
                String contentEnum = content.getEquipmentName().replaceAll("\\s", "");
                String[] contentArr = contentEnum.split("\\,");

                for (String name : contentArr) {
                    String fullName = String.format("%s (%s)", name, content.getProductType().getType());

                    Map<String, List<Product>> noCountriesMap = new HashMap<>();
                    namesMap.put(fullName, noCountriesMap);

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

                        if ((cert.isFullNameMatch() & product.getArticle().equals(name)) | calcArticle.equals(name) |
                                calcMaterial.equals(name)) {

                            List<Product> noCountriesProducts = cert.getCountries().contains(product.getCountry()) ?
                                    Collections.emptyList() : Collections.singletonList(product);

                            noCountriesMap.merge(product.getCountry(), noCountriesProducts, (products, products2) -> {
                                List<Product> result = new LinkedList<>(products);
                                result.addAll(products2);
                                return result;
                            });
                        }
                    }
                }
            }
        }

        String fileName = Utils.getDateTimeForFileName().concat("_certificate_countries_report.xlsx");
        ThreadManager.startNewThread(
                "cert_countries_exp_thr",
                new CertCountriesToExcel(certMap, Paths.get(fileName)),
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
