package ui_windows.options_window.certificates_editor.content_checker;

import core.CoreModule;
import ui_windows.options_window.certificates_editor.Certificate;
import ui_windows.options_window.certificates_editor.certificate_content_editor.CertificateContent;
import ui_windows.options_window.families_editor.ProductFamily;
import ui_windows.product.Product;

import java.util.TreeSet;

public class CertificateContentChecker {

    public CertificateContentChecker(Certificate certificate) {
        TreeSet<String> certAbsentCountries = new TreeSet<>();

        System.out.println(certificate.getFileName());
        for (CertificateContent cc : certificate.getContent()) {
            String[] names = cc.getEquipmentName().split("\\,");

            for (String name : names) {
                TreeSet<String> countries = new TreeSet<>();
                System.out.println(";" + name + ";(" + cc.getEquipmentType() + ")");
                for (Product product : CoreModule.getProducts().getItems()) {
                    String comparingValue = "^".concat(name).concat("[^a-zA-Z]").concat(".*");
                    boolean articleMatches = product.getArticle().matches(comparingValue);

                    if (articleMatches || certificate.isMaterialMatch() && product.getMaterial().matches(comparingValue)) {
                        countries.add(product.getCountry());

                        if (!certificate.getCountries().contains(product.getCountry())) {
                            ProductFamily productFamily = product.getProductFamily();
                            String family = "";
                            String resp = "";
                            if (productFamily != null) {
                                family = productFamily.getName();
                                resp = productFamily.getResponsible();
                            }

                            System.out.println(";;;" + product.getArticle() + "; " + product.getDescriptionRu() + "; " +
                                    product.getCountry() + ";" + family + ";" + resp);
                        }

                    }
                }

                for (String country : countries) {
                    boolean countryExists = certificate.getCountries().contains(country);
                    if (!countryExists) certAbsentCountries.add(country);
                    System.out.println(";;" + country + " - " + (countryExists ? "OK" : "NOT OK"));
                }
            }
        }

        if (certAbsentCountries.size() > 0) {
            System.out.print("absent countries: ");
            for (String country : certAbsentCountries) {
                System.out.print(country + ",");
            }
        }
        System.out.println();
        System.out.println();
    }
}
