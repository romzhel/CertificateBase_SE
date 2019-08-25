package core;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;
import database.ProductsDB;
import ui_windows.product.Product;

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
//            pr.setLastImportcodes(pr.getLastImportcodes().replaceAll("\\,$", ""));
//            pr.setChangecodes(pr.getChangecodes().replaceAll("\\,$", ""));
//            pr.setNeedaction(false);
//            if (pr.getHistory().isEmpty()) {


//            if (pr.getHistory().endsWith("\n")) System.out.println("\\n");

//            pr.setHistory(pr.getHistory().replaceAll("\n", "|"));
//            printOutSymbols(pr.getHistory());

            if (pr.getHistory().endsWith("\r\n")) {
                pr.setHistory(pr.getHistory().replaceAll("\r\n$", ""));
                pr.setHistory(pr.getHistory().replaceAll("\r\n", "|"));
                cis.add(pr);
            }


            /*pr.setHistory(pr.getHistory().replace("\n\n", "\n"));
            if (!pr.getHistory().isEmpty()) System.out.println(pr.getHistory());*/



//            if (pr.getDescriptionru().length() > 0) pr.setPrice(true);
//            else pr.setPrice(false);

//            if (pr.getLgbk().trim().length() > 0) {
//                hashSet.add(pr.getLgbk());
//            } else  if (pr.getHierarchy().trim().length() > 0)   hashSet2.add(pr.getHierarchy());

        }
        new ProductsDB().updateData(cis);
        System.out.println("OK");
    }

    private static void printOutSymbols(String text) {
        for (char c : text.toCharArray()) {
            if (c == 10) {
                System.out.print("\\" + "n");
            } if (c == 13){
                System.out.print("\\" + "r");
            } else {
                System.out.print(c);
            }
        }
    }
}
