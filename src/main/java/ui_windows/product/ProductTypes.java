package ui_windows.product;

import core.Initializable;
import database.ProductTypesDB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class ProductTypes implements Initializable {
    public final static String NO_SELECTED = "не выбрано";
    private static ProductTypes instance;
    private List<ProductType> productTypes;

    private ProductTypes() {
    }

    public static ProductTypes getInstance() {
        if (instance == null) {
            instance = new ProductTypes();
        }
        return instance;
    }

    @Override
    public void init() {
        productTypes = new ProductTypesDB().getData();
    }

    public ProductType getById(int id) {
        for (ProductType pt : productTypes) {
            if (pt.getId() == id) {
                return pt;
            }
        }

        return null;
    }

    public ProductType getByEqType(String eqType) {
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

    public ProductType addItem(ProductType newProductType) {
        log.trace("adding product type: {}", newProductType);

        ProductType existingProductType = getByEqType(newProductType.getType());
        log.trace("existing matching product type: {}", existingProductType);

        if (newProductType.equals(existingProductType)) {
            newProductType = existingProductType;
        } else {
            if (new ProductTypesDB().putData(newProductType)) {
                productTypes.add(newProductType);
            }
        }

        return newProductType;
    }

    public List<String> getPreparedTypes() {
        List<String> types = new ArrayList<>();
        types.add(NO_SELECTED);

        List<String> singleTypeNameList = productTypes.stream()
                .distinct()
                .sorted((o1, o2) -> o1.getType().compareToIgnoreCase(o2.getType()))
                .map(ProductType::getType)
                .collect(Collectors.toList());

        types.addAll(singleTypeNameList);

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

    public List<ProductType> getItems() {
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
