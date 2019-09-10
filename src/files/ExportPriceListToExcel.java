package files;

import core.CoreModule;
import core.Dialogs;
import javafx.application.Platform;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import ui_windows.main_window.MainWindow;
import ui_windows.options_window.certificates_editor.certificate_content_editor.certificatesChecker.CertificatesChecker;
import ui_windows.options_window.price_lists_editor.PriceList;
import ui_windows.options_window.product_lgbk.LgbkAndParent;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.product.Product;
import utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.TreeSet;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class ExportPriceListToExcel {
    public static final String TEMPLATE_FILE = "PL.xlsx";
    private PriceList priceList;
    private Workbook excelDoc;
    private File templateFile;
    private File resultFile;
    private int itemCount;
    private Structure priceRuEn;
    private Structure priceService;

    public ExportPriceListToExcel(PriceList priceList) {
        this.priceList = priceList;

        if (loadTemplate()) {
            MainWindow.setProgress(-1);

            new Thread(() -> {
                fillDoc();
                saveToFile();

                Platform.runLater(() -> MainWindow.setProgress(0.0));
            }).start();
        }
    }

    private boolean loadTemplate() {
        templateFile = new File(CoreModule.getFolders().getAppFolder() + "\\" + TEMPLATE_FILE);
        if (templateFile == null || !templateFile.exists()) {
            File dialogFile = new Dialogs().selectAnyFile(MainWindow.getMainStage(), "Выбор файла шаблона",
                    Dialogs.EXCEL_FILES, null);
            if (dialogFile == null || !dialogFile.exists()) {
                Dialogs.showMessage("Формирование прайс листа", "Не найден файл шаблона.");
                return false;
            }
            templateFile = dialogFile;
        }

        String targetFileName = priceList.getFileName().isEmpty() ? "PriceList" : priceList.getFileName();
        resultFile = new Dialogs().selectAnyFile(MainWindow.getMainStage(), "Выбор места сохранения",
                Dialogs.EXCEL_FILES, targetFileName + ".xlsx");
        if (resultFile == null) {
            Dialogs.showMessage("Выбор места сохранения", "Операция отменена, так как не было выбрано " +
                    "место сохранения");
            return false;
        }

        try {
            Files.copy(templateFile.toPath(), resultFile.toPath(), REPLACE_EXISTING);
        } catch (Exception e) {
            Dialogs.showMessage("Ошибка копирования шаблона", "Ошибка копирования файла шаблона: " + e.getMessage());
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
        priceRuEn = new Structure();
        priceService = new Structure();

        for (Product product : CoreModule.getProducts().getItems()) {
            String status = product.getDchain();

            LgbkAndParent lap = CoreModule.getProductLgbkGroups().getLgbkAndParent(new ProductLgbk(product.getLgbk(), product.getHierarchy()));
            boolean globalNotUsed = lap == null ? true : lap.getLgbkItem().isNotUsed() || lap.getLgbkParent().isNotUsed();
            boolean isInPrice = product.isPrice() && !product.isNotused() && !globalNotUsed && !isNewProduct(product);

            if (isInPrice && isPricePosition(product)) {
                priceRuEn.addProduct(product);
            } else if (isInPrice && isServicePosition(product)) {
                priceService.addProduct(product);
            }
        }

        priceRuEn.export(excelDoc.getSheetAt(0), 2);
//        priceRuEn.export(excelDoc.getSheetAt(1), 1);
        priceService.export(excelDoc.getSheetAt(1), 2);

        System.out.println("price items: " + priceRuEn.getSize() + " / " + priceService.getSize());

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
            Dialogs.showMessage("Формирование прайс-листа", "Произошла ошибка " + e.getMessage());
            System.out.println(e.getMessage());
        }
    }

    private class Structure {
        private TreeSet<LgbkGroup> lgbkGroups;

        public Structure() {
            lgbkGroups = new TreeSet<>(new Comparator<LgbkGroup>() {
                @Override
                public int compare(LgbkGroup o1, LgbkGroup o2) {
                    return o1.name.compareTo(o2.name);
                }
            });
        }

        public void addProduct(Product product) {
            for (LgbkGroup group : lgbkGroups) {
                String l = product.getLgbk();
                String n = group.getName();
                if (l.equals(n)) {
                    group.addProduct(product);
                    return;
                }
            }
            LgbkGroup newGroup = new LgbkGroup(product.getLgbk());
            newGroup.addProduct(product);
            lgbkGroups.add(newGroup);
        }

        public int getSize() {
            int result = 0;
            for (LgbkGroup lg : lgbkGroups) {
                result += lg.getSize();
            }
            return result;
        }

        public TreeSet<LgbkGroup> getLgbkGroups() {
            return lgbkGroups;
        }

        public int export(Sheet sheet, int rowIndex) {
            for (LgbkGroup lgroup : lgbkGroups) {
                rowIndex = lgroup.print(sheet, rowIndex);
            }
            return rowIndex;
        }

    }

    private class LgbkGroup {
        private String name;
        private TreeSet<HierarchyGroup> hierarchyGroups;

        public LgbkGroup(String name) {
            this.name = name;
            hierarchyGroups = new TreeSet<>(new Comparator<HierarchyGroup>() {
                @Override
                public int compare(HierarchyGroup o1, HierarchyGroup o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
        }

        public void addProduct(Product product) {
            for (HierarchyGroup group : hierarchyGroups) {
                if (isSpProduct(product) || product.getHierarchy().contains(group.getName())) {
                    group.addProduct(product);
                    return;
                }
            }
            HierarchyGroup newGroup = new HierarchyGroup(product.getHierarchy());
            newGroup.addProduct(product);
            hierarchyGroups.add(newGroup);
        }

        public String getName() {
            return name;
        }

        public int getSize() {
            int result = 0;
            for (HierarchyGroup hg : hierarchyGroups) {
                result += hg.getSize();
            }
            return result;
        }

        public int print(Sheet sheet, int rowIndex) {
            boolean isInPriceList = priceList.getLgbks().indexOf(name) != -1;
            boolean isEmpty = name.isEmpty() || getSize() == 0;
            if (!isInPriceList || isEmpty) return rowIndex;

            Row row;
            Cell cell;

            rowIndex++;
            row = sheet.createRow(rowIndex++);
            cell = row.createCell(0, CellType.STRING);

            ProductLgbk pl = CoreModule.getProductLgbks().getByLgbkName(name);

            if (sheet.getSheetName().toLowerCase().contains("en")) {
                cell.setCellValue(pl.getDescriptionEnRu());
            } else {
                cell.setCellValue(pl.getDescriptionRuEn());
            }

            for (HierarchyGroup hgroup : hierarchyGroups) {
                rowIndex = hgroup.export(sheet, rowIndex, name, hierarchyGroups.first().equals(hgroup));
            }

            return rowIndex;
        }

        public TreeSet<HierarchyGroup> getHierarchyGroups() {
            return hierarchyGroups;
        }
    }

    private class HierarchyGroup {
        private String name;
        private TreeSet<Product> products;

        public HierarchyGroup(String name) {
            if (name == null || name.length() < 4 && name.matches("^\\d") || name.length() < 3 && !name.matches("^\\d")) {
                this.name = "no name";
            } else {
                this.name = name.replaceAll("^\\d", "").substring(0, 3);
            }

            products = new TreeSet<>((o1, o2) -> o1.getMaterial().compareTo(o2.getMaterial()));
        }

        public void addProduct(Product product) {
            products.add(product);
        }

        public String getName() {
            return name;
        }

        public int getSize() {
            return products.size();
        }

        public TreeSet<Product> getProducts() {
            return products;
        }

        public int export(Sheet sheet, int rowIndex, String lgroupName, boolean space) {
            if (name.isEmpty() || getSize() == 0) return rowIndex;
            Row row;
            Cell cell;

            if (!space) rowIndex++;

            LgbkAndParent lap = CoreModule.getProductLgbkGroups().getLgbkAndParent(
                    new ProductLgbk(lgroupName, name));

            if (lap != null && lap.getLgbkItem() != null && lap.getLgbkParent() != null) {
                String enText = CoreModule.getProductLgbks().getByLgbkName(lgroupName).getDescriptionEnRu() + " / " +
                        lap.getLgbkItem().getDescriptionEnRu();
                String ruText = CoreModule.getProductLgbks().getByLgbkName(lgroupName).getDescriptionRuEn() + " / " +
                        lap.getLgbkItem().getDescriptionRuEn();
                String printText;

                if (sheet.getSheetName().toLowerCase().contains("en")) {
                    printText = enText;
                } else {
                    printText = ruText;
                }

                if (!printText.isEmpty()) {
                    row = sheet.createRow(rowIndex++);
                    cell = row.createCell(0, CellType.STRING);
                    cell.setCellValue(printText);
                }
            }

            for (Product product : products) {
                row = sheet.createRow(rowIndex++);
                int colIndex = 0;
                cell = row.createCell(colIndex++, CellType.STRING);
                if (!product.getProductForPrint().isEmpty()) {
                    cell.setCellValue(product.getProductForPrint());
                } else {
                    cell.setCellValue(product.getMaterial());
                }

                cell = row.createCell(colIndex++, CellType.STRING);
                cell.setCellValue(product.getArticle());

                cell = row.createCell(colIndex++, CellType.STRING);
                if (sheet.getSheetName().toLowerCase().contains("en")) {
                    cell.setCellValue(product.getDescriptionen());
                } else {
                    cell.setCellValue(product.getDescriptionRuEn());
                }

                boolean licence = product.getLgbk().equals("H3FQ") || product.getLgbk().equals("H5ET");
                boolean priceEmpty = product.getLocalPrice() == 0.0;

                if (isPricePosition(product) && !licence && !isSpProduct(product) && !priceEmpty) {
                    cell = row.createCell(colIndex++, CellType.NUMERIC);
                    cell.setCellValue(product.getLocalPrice());
                } else if (isServicePosition(product) && !licence && !isSpProduct(product) && !priceEmpty) {//service positions
                    cell = row.createCell(colIndex++, CellType.NUMERIC);
                    cell.setCellValue(product.getLocalPrice() * 0.7);
                } else {
                    cell = row.createCell(colIndex++, CellType.STRING);

                    if (sheet.getSheetName().toLowerCase().contains("en")) {
                        cell.setCellValue("By request");
                    } else {
                        cell.setCellValue("По запросу");
                    }
                }

                if (!sheet.getSheetName().toLowerCase().contains("сервисные")) {
                    cell = row.createCell(colIndex++, CellType.NUMERIC);
                    cell.setCellValue(product.getPreparedLeadTime());
                }

                cell = row.createCell(colIndex++, CellType.NUMERIC);
                cell.setCellValue(product.getMinOrder());

                cell = row.createCell(colIndex++, CellType.STRING);
                cell.setCellValue(product.getLgbk()/*.concat("-").concat(product.getHierarchy())*/);

                cell = row.createCell(colIndex++, CellType.NUMERIC);
                cell.setCellValue(product.getWeight());

                /*CertificatesChecker cc = CoreModule.getCertificates().getCertificatesChecker();
                cc.check(product);
                cell = row.createCell(colIndex++, CellType.STRING);
                cell.setCellValue(cc.getCheckStatusResult());

                cell = row.createCell(colIndex++, CellType.STRING);
                cell.setCellValue(product.getDchain());*/
            }
            return rowIndex;
        }
    }

    private boolean isSpProduct(Product product) {
        int id = CoreModule.getProductLgbks().getFamilyIdByLgbk(new ProductLgbk(product.getLgbk(), product.getHierarchy()));
        boolean productFamId = product.getFamily() == 24;
        return id == 24 || productFamId;
    }

    private boolean isEvacProduct(Product product) {
        int id = CoreModule.getProductLgbks().getFamilyIdByLgbk(new ProductLgbk(product.getLgbk(), product.getHierarchy()));
        boolean productFamId = product.getFamily() == 28;
        return id == 28 || productFamId;
    }

    private boolean isNewProduct(Product product) {
        String status = product.getDchain();
        return status.equals("0") || status.equals("20") || status.equals("22") || status.equals("23") || status.equals("24");
    }

    private boolean isPricePosition(Product product) {
        String status = product.getDchain();
        return status.equals("28") || status.equals("30") || (status.isEmpty() && isSpProduct(product));
//                /*|| isEvacProduct(product)*/);//эвакуация
    }

    private boolean isServicePosition(Product product) {
        String status = product.getDchain();
        return status.equals("36") || status.equals("52") || status.equals("56") ||status.equals("58") ||
                status.equals("60") || status.equals("61") || status.equals("62");
    }

}
