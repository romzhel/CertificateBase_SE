package files;

import core.CoreModule;
import core.Dialogs;
import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import ui_windows.main_window.MainWindow;
import ui_windows.options_window.certificates_editor.certificate_content_editor.certificatesChecker.CertificatesChecker;
import ui_windows.options_window.price_lists_editor.PriceList;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.product.Product;
import utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.TreeSet;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class ExportPriceListToExcel {
    public static final String TEMPLATE_FILE = "PL.xlsx";
    private PriceList priceList;
    private Workbook excelDoc;
    private File templateFile;
    private File resultFile;
    private int itemCount;

    public ExportPriceListToExcel(PriceList priceList) {
        this.priceList = priceList;

        if (loadTemplate()) {
            new Thread(() -> {
                fillDoc();
                saveToFile();
                System.out.println("items in price: " + itemCount);
            }).start();
        }
    }

    private boolean loadTemplate() {
        templateFile = new File(CoreModule.getFolders().getAppFolder() + "\\" + TEMPLATE_FILE);
        String targetFileName = priceList.getFileName().isEmpty() ? "PriceList" : priceList.getFileName();
        resultFile = new File(CoreModule.getFolders().getAppFolder() + "\\" + targetFileName + ".xlsx");

        if (templateFile == null || !templateFile.exists()) {
            File dialogFile = Dialogs.selectNOWFile(MainWindow.getMainStage());
            if (dialogFile == null || !dialogFile.exists()) {
                Dialogs.showMessage("Формирование прайс листа", "Не найден файл шаблона.");
                return false;
            }

            templateFile = dialogFile;
        }

        try {
            Files.copy(templateFile.toPath(), resultFile.toPath(), REPLACE_EXISTING);
        } catch (Exception e) {
            Dialogs.showMessage("Формирование файла прайс листа", e.getMessage());
            return false;
        }

        try {
            FileInputStream fis = new FileInputStream(resultFile);
            excelDoc = WorkbookFactory.create(fis);
            fis.close();
        } catch (IOException | InvalidFormatException e) {
            System.out.println(e.getMessage());
            return false;
        }

        return true;
    }

    private void fillDoc() {
        TreeSet<Product> productsToAdd = new TreeSet<>((o1, o2) -> o1.getMaterial().compareTo(o2.getMaterial()));
        String[] sheetNames = {"Ru", "En"};
        Sheet excelSheet;
        Row row;
        Cell cell;

        for (int sheetIndex = 0; sheetIndex < sheetNames.length; sheetIndex++) {
            excelSheet = excelDoc.getSheet(sheetNames[sheetIndex]);

            int rowIndex = 2;

            TreeItem<ProductLgbk> root = CoreModule.getProductLgbkGroups().getFullTreeSet();
            boolean lgbkMatches;
            boolean lgbkNotEmpty;
            boolean lgbkIsUsed;
            for (TreeItem<ProductLgbk> groupTipl : root.getChildren()) {
                lgbkMatches = priceList.getLgbks().contains(groupTipl.getValue().getLgbk());
                lgbkNotEmpty = !groupTipl.getValue().getLgbk().isEmpty();
                lgbkIsUsed = !groupTipl.getValue().isNotUsed();

                if (lgbkMatches && lgbkNotEmpty && lgbkIsUsed) {
                    row = excelSheet.createRow(rowIndex++);
                    cell = row.createCell(0, CellType.STRING);

                    if (sheetNames[sheetIndex].equals("Ru")) {
                        cell.setCellValue(groupTipl.getValue().getDescriptionRuEn());
                    } else {
                        cell.setCellValue(groupTipl.getValue().getDescriptionEnRu());
                    }

                    for (TreeItem<ProductLgbk> subgroupTipl : groupTipl.getChildren()) {
                        productsToAdd.clear();

//                        boolean lgbkMatches;
                        boolean hierarchyMatches;
                        String hierarchyForComp;
                        for (Product product : CoreModule.getProducts().getItems()) {
                            hierarchyForComp = subgroupTipl.getValue().getHierarchy().replaceAll("\\.", "");
                            lgbkMatches = product.getLgbk().equals(groupTipl.getValue().getLgbk());
                            hierarchyMatches = !hierarchyForComp.isEmpty() && product.getHierarchy().contains(hierarchyForComp);

                            if (product.isPrice() && lgbkMatches && hierarchyMatches) {
                                productsToAdd.add(product);
                            }
                        }

                        if (productsToAdd.size() > 0) {
                            row = excelSheet.createRow(rowIndex++);
                            cell = row.createCell(0, CellType.STRING);

                            if (sheetNames[sheetIndex].equals("Ru")) {
                                cell.setCellValue(groupTipl.getValue().getDescriptionRuEn() + " \\ " + subgroupTipl.getValue().getDescriptionRuEn());
                            } else {
                                cell.setCellValue(groupTipl.getValue().getDescriptionEnRu() + " \\ " + subgroupTipl.getValue().getDescriptionEnRu());
                            }
                        }

                        for (Product product : productsToAdd) {
                            row = excelSheet.createRow(rowIndex++);

                            cell = row.createCell(0, CellType.STRING);
                            cell.setCellValue(product.getProductForPrint());

                            cell = row.createCell(1, CellType.STRING);
                            cell.setCellValue(product.getArticle());

                            cell = row.createCell(2, CellType.STRING);
                            if (sheetNames[sheetIndex].equals("Ru")) {
                                cell.setCellValue(product.getDescriptionru());
                            } else {
                                cell.setCellValue(product.getDescriptionen());
                            }

                            cell = row.createCell(3, CellType.NUMERIC);
                            cell.setCellValue(product.getLocalPrice());

                            cell = row.createCell(4, CellType.NUMERIC);
                            cell.setCellValue(product.getLeadTime() > 0 ? product.getLeadTime() + 14 : 0);

                            cell = row.createCell(5, CellType.NUMERIC);
                            cell.setCellValue(product.getMinOrder());

                            cell = row.createCell(6, CellType.STRING);
                            cell.setCellValue(groupTipl.getValue().getLgbk());

                            cell = row.createCell(7, CellType.NUMERIC);
                            cell.setCellValue(product.getWeight());

                            CertificatesChecker cc = CoreModule.getCertificates().getCertificatesChecker();
                            cc.check(product);
                            cell = row.createCell(8, CellType.STRING);
                            cell.setCellValue(cc.getCheckStatusResult());
                        }
                        if (productsToAdd.size() > 0) row = excelSheet.createRow(rowIndex++);
                        itemCount += productsToAdd.size();
                    }
                }
            }
        }
    }

    private void saveToFile() {
        try {
            FileOutputStream fos = new FileOutputStream(resultFile);
            excelDoc.write(fos);
            fos.close();
            excelDoc.close();

            Platform.runLater(() -> {
                if (Dialogs.confirm("Формирование прайс листа", "Прайс лист сформирован. Желаете открыть его?")) {
                    Utils.openFile(resultFile);
                }
            });
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}

