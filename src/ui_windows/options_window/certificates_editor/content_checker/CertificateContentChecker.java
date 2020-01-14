package ui_windows.options_window.certificates_editor.content_checker;

import core.CoreModule;
import core.Dialogs;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ui_windows.main_window.MainWindow;
import ui_windows.options_window.certificates_editor.Certificate;
import ui_windows.options_window.certificates_editor.certificate_content_editor.CertificateContent;
import ui_windows.product.Product;
import ui_windows.product.data.DataItemEnum;
import utils.ItemsGroup;
import utils.ItemsGroups;
import utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import static ui_windows.product.data.DataItemEnum.*;

public class CertificateContentChecker {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private int rowIndex;

    public CertificateContentChecker(final ObservableList<Certificate> certificates) {
        new Thread(() -> {
            MainWindow.setProgress(-1);
            workbook = new XSSFWorkbook();
            workbook.createSheet("certificate(s)_countries_report");
            sheet = workbook.getSheetAt(0);

            ArrayList<Product> itemsWithNotOkCountry = new ArrayList<>();

            for (Certificate certificate : certificates) {
                itemsWithNotOkCountry.addAll(getItemsWithBadCountries(certificate));
            }

            if (itemsWithNotOkCountry.size() > 0) {
                CoreModule.setAndDisplayCustomItems(itemsWithNotOkCountry);
            }

            Platform.runLater(() -> saveToFile());

            MainWindow.setProgress(0.0);
        }).start();
    }

    private ArrayList<Product> getItemsWithBadCountries(Certificate certificate) {
        ArrayList<Product> itemsWithNotOkCountry = new ArrayList<>();

        int firstRow = rowIndex;
        addRow(0, certificate.getFileName());

        for (CertificateContent cc : certificate.getContent()) {
            String[] names = cc.getEquipmentName().split("\\,");

            addRow(1, cc.getEquipmentType());

            for (String name : names) {
                ItemsGroups<String, String, Product> nameGroup = new ItemsGroups<>(name, (o1, o2) -> o1.getGroupNode().compareTo(o2.getGroupNode()));

                addRow(2, name + " (" + cc.getEquipmentType() + ")");

                for (Product product : CoreModule.getProducts().getItems()) {
                    String comparingValue = "^".concat(name).concat("[^a-zA-Z]").concat(".*");
                    boolean articleMatches = product.getArticle().matches(comparingValue);

                    if (!product.getCountry().isEmpty() && (articleMatches || certificate.isMaterialMatch() && product.getMaterial().matches(comparingValue))) {
                        ItemsGroup<String, Product> countryGroup = new ItemsGroup<>(product.getCountry(), (o1, o2) -> o1.getArticle().compareTo(o2.getArticle()));

                        if (!certificate.getCountries().contains(product.getCountry())) {
                            countryGroup.addItem(product);
                        }

                        nameGroup.addGroup(countryGroup);
                    }
                }

                for (ItemsGroup<String, Product> countryGroup : nameGroup.getItems()) {
                    boolean countryExists = certificate.getCountries().contains(countryGroup.getGroupNode());

                    String countryStatus = countryGroup.getGroupNode() + " - " + (countryExists ? "OK" : "NOT OK");
                    addRow(3, countryStatus);

                    for (Product product : countryGroup.getItems()) {
                        addProductInfo(4, product);
                        itemsWithNotOkCountry.add(product);
                    }
                }
            }
        }

        sheet.groupRow(firstRow + 1, rowIndex);
        rowIndex += 2;

        return itemsWithNotOkCountry;
    }

    private void saveToFile() {
        File file;
        if ((file = new Dialogs().selectAnyFile(MainWindow.getMainStage(), "Сохранение отчёта", Dialogs.EXCEL_FILES,
                "Certificates_Countries_Report.xlsx")) != null) {
            OutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                workbook.write(fos);

                fos.close();
                workbook.close();

            } catch (Exception e) {
                Dialogs.showMessage("Ошибка сохранения файла", e.getMessage());
            }

            Utils.openFile(file);
        }
    }

    private void addRow(int column, String value) {
        XSSFRow row = sheet.createRow(rowIndex++);
        XSSFCell cell = row.createCell(column, CellType.STRING);
        cell.setCellValue(value);
    }

    private void addProductInfo(int column, Product product) {
        DataItemEnum[] dataItems = new DataItemEnum[]{DATA_ARTICLE, DATA_DESCRIPTION,
                DATA_COUNTRY, DATA_FAMILY, DATA_RESPONSIBLE, DATA_IS_IN_PRICE, DATA_DCHAIN, DATA_DCHAIN_COMMENT};

        XSSFRow row = sheet.createRow(rowIndex++);
        XSSFCell cell;
        for (DataItemEnum dataItem : dataItems) {
            cell = row.createCell(column++);
            dataItem.fillExcelCell(cell, product,null);
        }
    }
}
