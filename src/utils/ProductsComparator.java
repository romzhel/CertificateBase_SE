package utils;

import ui_windows.main_window.Product;
import ui_windows.main_window.Products;

public class ProductsComparator {
    ProductsComparatorResult result;

    public ProductsComparator(Products prs1, Products prs2nt, String... exceptions) {
        result = new ProductsComparatorResult();
        Products prs2 = new Products();
        prs2.setItems(new Preprocessor(prs2nt.getItems()).getTreatedItems());

        System.out.println("comparing: existing items \\ importing items : " + prs1.getItems().size() + " \\ " +
                prs2.getItems().size());

        int dou = 0;
        String pr1t = "";
        String pr2t = "";
        for (Product pr1 : prs1.getItems()) {//go throw all the existing items

            for (Product pr2 : prs2.getItems()) {//go throw all the updating item
                pr1t = pr1.getMaterial().replaceAll("\\-", "").replaceAll("BPZ:", "");
                pr2t = pr2.getMaterial().replaceAll("\\-", "").replaceAll("BPZ:", "");

                if (pr1t.equals("P55802-Y110-A100")){
                    int i = 0;
                    System.out.println("checking__________________________________________");
                }



                if (pr1t.equals(pr2t)) {//product exists
                    pr1.setLastChangeDate(Utils.getDateTime());//set last update time

//                    pr1.setChangecodes("");
                    ObjectsComparator pc = new ObjectsComparator(pr1, pr2, true, exceptions);

                    if (pc.getResult().length() > 0) {//product changed
                        result.getChangedItems().add(pr1);//add product to changed list
                        System.out.println(pr1.getMaterial() + ", (" + pr1.getArticle() + ") changed" + pc.getResult());

                        pr1.setHistory(pr1.getHistory().concat(Utils.getDateTime()).concat(pc.getResult().concat("\n")));
                        pr1.setNeedaction(true);
                    }

                    prs2.getItems().remove(pr2);//remove compared item
                    break;//found go to next pr1
                } else if (pr1.getMaterial().substring(1).equals(pr2.getMaterial().substring(1))) {//Vanderbilt

                    System.out.println(dou++ + " pr1 = " + pr1.getMaterial() + " (" + pr1.getMaterial().substring(1) +
                            "), pr2 = " + pr2.getMaterial() + " (" + pr1.getMaterial().substring(1) + "), " +
                            pr1.getArticle() + " vs " + pr2.getArticle());
//                    System.out.println("pr1 = " + pr1.getMaterial() + ", pr = 2" + pr2.getMaterial() + ", " + pr1.getArticle());
                }
            }
        }

        for (Product pr : prs2.getItems()) {//add new items to existing
            if (pr.getArticle() != null && !pr.getArticle().trim().isEmpty()) {
                pr.setChangecodes("new");
                pr.setLastImportcodes("new");
                pr.setNeedaction(true);
                pr.setHistory(pr.getHistory().concat(Utils.getDateTime().concat(", new\n")));// add back <<<<<<<
                pr.setLastChangeDate(Utils.getDateTime());
                prs1.getItems().add(pr);
                result.getNewItems().add(pr);
            }
        }

        System.out.println("new \\ changed items: " + result.getNewItems().size() + " \\ " + result.getChangedItems().size());
    }

    public ProductsComparatorResult getResult() {
        return result;
    }

}