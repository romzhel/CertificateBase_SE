package files.price_to_excel;

import core.CoreModule;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import ui_windows.options_window.price_lists_editor.se.PriceListColumn;
import ui_windows.options_window.price_lists_editor.se.price_sheet.PriceListSheet;
import ui_windows.options_window.product_lgbk.LgbkAndParent;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.product.Product;

import java.util.TreeSet;

import static files.price_to_excel.ExportPriceListToExcel_SE.CELL_ALIGN_LEFT;
import static files.price_to_excel.ExportPriceListToExcel_SE.CELL_ALIGN_LEFT_BOLD;

public class HierarchyGroup {
    private PriceListSheet priceListSheet;
    private String name;
    private TreeSet<Product> products;

    public HierarchyGroup(String name, PriceListSheet priceListSheet) {
        this.priceListSheet = priceListSheet;
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

    public int export(XSSFSheet sheet, int rowIndex, String lgroupName, boolean space) {
        if (name.isEmpty() || getSize() == 0) return rowIndex;
        int firstRowForGroup = rowIndex;
        XSSFRow row;
        XSSFCell cell;

        if (!space) {
            rowIndex++;
            firstRowForGroup++;
        }

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
                cell.setCellStyle(CELL_ALIGN_LEFT);
                cell.setCellValue(printText);
            }
        } else {
            firstRowForGroup--;
        }

        for (Product product : products) {
            row = sheet.createRow(rowIndex++);

            int colIndex = 0;
            for (PriceListColumn plc : priceListSheet.getColumnsSelector().getSelectedItems()) {
                plc.createXssfCell(product, row, colIndex++);
            }

            /*cell = row.createCell(colIndex++, CellType.STRING); //material
//                cell.setCellStyle(CELL_ALIGN_LEFT);
            if (!product.getProductForPrint().isEmpty()) {
                cell.setCellValue(product.getProductForPrint());
            } else {
                cell.setCellValue(product.getMaterial());
            }

            cell = row.createCell(colIndex++, CellType.STRING); //article
            cell.setCellValue(product.getArticle());
//                cell.setCellStyle(CELL_ALIGN_LEFT);

            cell = row.createCell(colIndex++, CellType.STRING); //description
//                cell.setCellStyle(CELL_ALIGN_LEFT);
            if (sheet.getSheetName().toLowerCase().contains("en")) {
                cell.setCellValue(product.getDescriptionen());
            } else {
                cell.setCellValue(product.getDescriptionRuEn());
            }*/

//                boolean licence = product.getLgbk().equals("H3FQ") || product.getLgbk().equals("H5ET");
//            boolean priceEmpty = product.getLocalPrice() == 0.0;

                /*if (isPricePosition(product) && !priceEmpty) {
                    cell = row.createCell(colIndex++, CellType.NUMERIC);
//                    cell.setCellStyle(CELL_CURRENCY_FORMAT);
                    cell.setCellValue(product.getLocalPrice());
                } else if (isServicePosition(product) && !priceEmpty) {//service positions
                    cell = row.createCell(colIndex++, CellType.NUMERIC);
                    cell.setCellStyle(CELL_CURRENCY_FORMAT);
                    cell.setCellValue(product.getLocalPrice() * 0.7);
                } else {
                    cell = row.createCell(colIndex++, CellType.STRING);
                    cell.setCellStyle(CELL_ALIGN_RIGHT);*/

                    /*if (sheet.getSheetName().toLowerCase().contains("en")) {
                        cell.setCellValue("By request");
                    } else {
                        cell.setCellValue("По запросу");
                    }
                }*/

            /*if (!sheet.getSheetName().toLowerCase().contains("сервисные")) {
                cell = row.createCell(colIndex++, CellType.NUMERIC);
                cell.setCellValue(product.getPreparedLeadTime());
//                    cell.setCellStyle(CELL_ALIGN_CENTER);
            }

            cell = row.createCell(colIndex++, CellType.NUMERIC);
//                cell.setCellStyle(CELL_ALIGN_CENTER);
            cell.setCellValue(product.getMinOrder());

            cell = row.createCell(colIndex++, CellType.STRING);
//                cell.setCellStyle(CELL_ALIGN_CENTER);
            cell.setCellValue(product.getLgbk());

            cell = row.createCell(colIndex++, CellType.NUMERIC);
//                cell.setCellStyle(CELL_ALIGN_RIGHT);
            cell.setCellValue(product.getWeight());*/
//                cellAlignRight(cell);

                /*CertificatesChecker cc = CoreModule.getCertificates().getCertificatesChecker();
                cc.check(product);
                cell = row.createCell(colIndex++, CellType.STRING);
                cell.setCellValue(cc.getCheckStatusResult());

                cell = row.createCell(colIndex++, CellType.STRING);
                cell.setCellValue(product.getDchain());*/

        }
        sheet.groupRow(firstRowForGroup + 1, rowIndex - 1);
//            sheet.setRowGroupCollapsed(rowIndex - 1, true);
        return rowIndex;
    }

}
