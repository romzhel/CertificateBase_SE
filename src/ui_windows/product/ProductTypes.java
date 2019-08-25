package ui_windows.product;

import database.ProductTypesDB;
import database.Request;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ui_windows.options_window.certificates_editor.certificate_content_editor.CertificateContent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;

public class ProductTypes {
    Request request;
    ArrayList<ProductType> productTypes;

    public ProductTypes() {
        request = new ProductTypesDB();
//        productTypes = new ArrayList<>();
    }

    public ProductTypes getFromDB() {
        productTypes = request.getData();
        return this;
    }

    public int getID(CertificateContent cc) {
        for (ProductType pt : productTypes) {
            if (pt.getType().equals(cc.getEquipmentType().trim())) return pt.getId();//existing type
        }

        ProductType pt = new ProductType(0, cc.getEquipmentType(), cc.getTnved());//create new one
        if (request.putData(pt)) {
            addItem(pt);
        }

        return productTypes.indexOf(pt);//return index of new item
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
        productTypes.add(productType);
    }

    public ArrayList<String> getPreparedTypes() {
        ArrayList<String> types = new ArrayList<>();
        for (ProductType pt : productTypes) {
            types.add(pt.getType());
        }

        return new ArrayList<>(new TreeSet<>(new HashSet<>(types)));
    }

    public ObservableList<String> getObs() {
        return FXCollections.observableArrayList(getPreparedTypes());
    }

    public String getTypeById(int id) {
        for (ProductType pt : productTypes) {
            if (pt.getId() == id) return pt.getType();
        }
        return "";
    }

    public String getTnVedById(int id){
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

    public ProductType getProductTypeByType(String type) {
        for (ProductType pt : productTypes) {
            if (pt.getType().equals(type)) return pt;
        }
        return null;
    }


}
