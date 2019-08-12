package core;

import database.ProductsDB;
import ui_windows.main_window.Product;

import java.util.ArrayList;

public class AddActions {

    public static void make() {

//        HashSet<String> hashSet = new HashSet<>();
//        HashSet<String> hashSet2 = new HashSet<>();

        ArrayList<Product> cis = new ArrayList<>();
        for (Product pr : CoreModule.getProducts().getItems()) {
//            pr.setChangecodes("");
            //pr.setHistory(pr.getHistory().replaceAll("\\slgbk.+\\,", ""));

//            if (pr.getLastImportcodes().equals("endofservicedchain")) {
            pr.setLastImportcodes(pr.getLastImportcodes().replaceAll("\\,$", ""));
            pr.setChangecodes(pr.getChangecodes().replaceAll("\\,$", ""));
//            pr.setNeedaction(false);
            cis.add(pr);
//            }

//            if (pr.getDescriptionru().length() > 0) pr.setPrice(true);
//            else pr.setPrice(false);

//            if (pr.getLgbk().trim().length() > 0) {
//                hashSet.add(pr.getLgbk());
//            } else  if (pr.getHierarchy().trim().length() > 0)   hashSet2.add(pr.getHierarchy());

        }
        new ProductsDB().updateData(cis);
        System.out.println("OK");


//        new ProductsDB().updateData(products.getItems());
//        Dialogs.showMessage("all is OK", "all is OK");


//        TreeSet<String> treeSet = new TreeSet<>(hashSet);
//        TreeSet<String> treeSet2 = new TreeSet<>(hashSet2);
//        ArrayList<String> res = new ArrayList<>(treeSet);
//        ArrayList<String> res2 = new ArrayList<>(treeSet2);
//
//        for (String s : res) {
//            productLgbks.addItem(new ProductLgbk(s, "", "", 0, false));
//        }
//
//        for (String s : res2) {
//            productLgbks.addItem(new ProductLgbk("", s, "", 0, false));
//        }
//
//        System.out.println(Arrays.toString(res.toArray()));


    }
}
