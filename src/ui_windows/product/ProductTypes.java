package ui_windows.product;

import database.ProductTypesDB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ui_windows.options_window.certificates_editor.certificate_content_editor.CertificateContent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;

public class ProductTypes {
    public final static String NO_SELECTED = "не выбрано";
    private ArrayList<ProductType> productTypes;

    public ProductTypes() {
//        productTypes = new ArrayList<>();
    }

    public ProductTypes getFromDB() {
        productTypes = new ProductTypesDB().getData();
        return this;
    }

    public ProductType getById(int id) {
        for (ProductType pt : productTypes) {
            if (pt.getId() == id) {
                return pt;
            }
        }

        return null;
    }

    public ProductType getByEqType(String eqType){
        for (ProductType pt : productTypes) {
            if (pt.getType().trim().toLowerCase().equals(eqType.trim().toLowerCase())) {
                return pt;
            }
        }

        return null;
    }

    public int getIDbyType(String type) {
        for (ProductType pt : productTypes) {
            if (pt.getType().equals(type.trim())) return pt.getId();//existing type
        }

//        ProductType pt = new ProductType(0, type);//create new one
//        if (request.putData(pt)) {
//            addItem(pt);
//        }

//        return productTypes.indexOf(pt);//return index of new item
        return 0;
    }

    public void addItem(ProductType productType) {
        if (new ProductTypesDB().putData(productType)) {
            productTypes.add(productType);
        }
    }

    public ArrayList<String> getPreparedTypes() {
        ArrayList<String> types = new ArrayList<>();
        types.add(NO_SELECTED);

        HashSet<String> singleList = new HashSet<>();
        for (ProductType pt : productTypes) {
            singleList.add(pt.getType());
        }

        TreeSet<String> sortedList = new TreeSet<>(singleList);
        types.addAll(sortedList);

        return types;
    }

    public ObservableList<String> getObs() {
        return FXCollections.observableArrayList(getPreparedTypes());
    }

    public String getTypeById(int id) {
        for (ProductType pt : productTypes) {
            if (pt.getId() == id) return pt.getType();
        }
        return NO_SELECTED;
    }

    public String getTnVedById(int id) {
        for (ProductType pt : productTypes) {
            if (pt.getId() == id) return pt.getTen();
        }
        return "";
    }

    public ArrayList<ProductType> getItems() {
        return productTypes;
    }

    public void delete(String type) {
        for (ProductType pt : productTypes) {
            if (pt.getType().equals(type)) {
                new ProductTypesDB().deleteData(pt);
                productTypes.remove(pt);
                break;
            }
        }
    }
}
