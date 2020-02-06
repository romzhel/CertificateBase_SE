package utils.comparation.products;

import ui_windows.main_window.file_import_window.FileImportParameter;
import ui_windows.main_window.file_import_window.SingleProductsComparator;
import ui_windows.product.Product;
import utils.DoublesPreprocessor;
import utils.Utils;
import utils.comparation.se.*;

import java.util.ArrayList;

public class ProductsComparator {
    private ProductsComparatorResult result;

    public ProductsComparator(ArrayList<Product> prs1, ArrayList<Product> prs2nt, FileImportParameter... parameters) {
        result = new ProductsComparatorResult();
        ArrayList<Product> goneItems = new ArrayList<>(prs1);
        ArrayList<Product> prs2 = new DoublesPreprocessor(prs2nt).getTreatedItems();

        ObjectsComparatorSe<Product> ocse = new ObjectsComparatorSe<>();
        ProductsCompareResultsSe<Product> pscr = new ProductsCompareResultsSe<>();

        System.out.println("comparing: existing items \\ importing items : " + prs1.size() + " \\ " + prs2.size());

        ComparingParameters oscp /*= new Adapter().convert(parameters)*/ = null;

        int dou = 0;
        String pr1t = "";
        String pr2t = "";
        for (Product pr1 : prs1) {//go throw all the existing items

            for (Product pr2 : prs2) {//go throw all the updating item
                pr1t = pr1.getMaterial().replaceAll("(\\-)*(\\:)*(VBPZ)*(BPZ)*", "");
                pr2t = pr2.getMaterial().replaceAll("(\\-)*(\\:)*(VBPZ)*(BPZ)*", "");

                if (pr2t.matches("^0+\\d+$")) {
                    pr2t = pr2t.replaceAll("^0+", "");
                }

                if (pr1t.equals(pr2t)) {//product exists
                    pr1.setLastChangeDate(Utils.getDateTime());//set last update time

                    SingleProductsComparator pc = new SingleProductsComparator(pr1, pr2, true, parameters);
//                    ObjectsComparatorResult ocr = pc.getResult();

                    ObjectsComparatorResultSe<Product> res = ocse.compare(pr1, pr2, oscp);

//                    if (ocr.isNeedUpdateInDB()) {//product changed
                    if (res.isChanged()) {//product changed
                        pscr.addNewItems(res);

                        /*result.getChangedItems().add(new ProductsComparatorResultItem(pr1, ocr.getLogComment()));//add product to changed list
                        System.out.println(pr1.getMaterial() + ", (" + pr1.getArticle() + ") changed " + ocr.getLogComment());

                        if (!ocr.getHistoryComment().isEmpty()) {
                            if (pr1.getHistory() != null && !pr1.getHistory().isEmpty()) {
                                pr1.setHistory(pr1.getHistory().concat("|" + Utils.getDateTime()).concat(ocr.getHistoryComment()));
                            } else {
                                pr1.setHistory(pr1.getHistory().concat(Utils.getDateTime()).concat(ocr.getHistoryComment()));
                            }
                        }
                        result.addToReport(ocr.getReportLines());*/
                    }

                    goneItems.remove(pr1);
                    prs2.remove(pr2);//remove compared item
                    break;//found go to next pr1
                } else if (pr1.getMaterial().substring(1).equals(pr2.getMaterial().substring(1))) {//Vanderbilt

                    System.out.println(dou++ + " pr1 = " + pr1.getMaterial() + " (" + pr1.getMaterial().substring(1) +
                            "), pr2 = " + pr2.getMaterial() + " (" + pr1.getMaterial().substring(1) + "), " +
                            pr1.getArticle() + " vs " + pr2.getArticle());
//                    System.out.println("pr1 = " + pr1.getMaterial() + ", pr = 2" + pr2.getMaterial() + ", " + pr1.getArticle());
                }
            }
        }

        for (Product pr : prs2) {//add new items to existing
            if (pr.getArticle() != null && !pr.getArticle().trim().isEmpty()) {
                /*pr.setLastImportcodes("new");
                pr.setHistory(pr.getHistory().concat(Utils.getDateTime().concat(", new")));// add back <<<<<<<
                pr.setLastChangeDate(Utils.getDateTime());
                prs1.getItems().add(pr);
                result.getNewItems().add(new ProductsComparatorResultItem(pr, "добавилась"));
                result.addToReport(pr, "new");*/

                pscr.addNewItems(new ObjectsComparatorResultSe<>(null, pr));
            } else {
                System.out.println(pr.getMaterial() + " was not added due empty Article!");
            }
        }

        for (Product goneProduct : goneItems) {
            result.getGoneItems().add(new ProductsComparatorResultItem(goneProduct, "удалена"));

            pscr.addGoneItems(new ObjectsComparatorResultSe<>(goneProduct, null));
        }

        System.out.println("new \\ changed \\ not found items: " + result.getNewItems().size() + " \\ "
                + result.getChangedItems().size() + " \\ " + result.getGoneItems().size());
    }

    public ProductsComparatorResult getResult() {
        return result;
    }
}