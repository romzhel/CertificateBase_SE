package ui_windows.product;

import core.Initializable;
import database.ProductsDB;
import javafx.scene.control.TableView;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ui_windows.product.vendors.VendorEnum;
import utils.Utils;
import utils.comparation.products.ProductNameResolver;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@NoArgsConstructor
public class Products implements Initializable {
    private static Products instance;
    private Map<String, Product> productMap = new HashMap<>();
    private TableView<Product> tableView;

    public static Products getInstance() {
        if (instance == null) {
            instance = new Products();
        }
        return instance;
    }

    @Override
    public void init() throws Exception {
        addToProductMap(new ProductsDB().getData());
    }

    public void addToProductMap(Collection<Product> productList) {
        Map<String, Product> addedItemMap = productList.stream()
                .collect(Collectors.toMap(
                        this::getVendorMaterial,
                        product -> product
                ));
        productMap.putAll(addedItemMap);
    }

    public List<Product> getItems() {
        return productMap.values().stream()
                .sorted((o1, o2) -> o1.getMaterial().compareToIgnoreCase(o2.getMaterial()))
                .collect(Collectors.toList());
    }

    public Product getProductByVendorMaterial(VendorEnum vendor, String material) {
        String id = getVendorMaterial(vendor, material);
        return productMap.get(id);
    }

    public Product getProductByVendorMaterialId(String id) {
        return productMap.get(id);
    }

    public Product getItemByMaterialOrArticle(String name) {
        String resolvedName = ProductNameResolver.resolve(Utils.toEN(name)).toUpperCase();

        for (VendorEnum vendor : VendorEnum.values()) {
            if (productMap.containsKey(getVendorMaterial(vendor, name))) {
                return productMap.get(resolvedName);
            }
        }

        return productMap.values().stream()
                .filter(product -> ProductNameResolver.resolve(product.getArticle()).equals(resolvedName))
                .findAny().orElse(null);
    }

    public TableView<Product> getTableView() {
        return tableView;
    }

    public void setTableView(TableView<Product> tableView) {
        this.tableView = tableView;
    }

    public boolean isProductTypeIsUsed(String type) {
        return productMap.values().stream()
                .anyMatch(product -> ProductTypes.getInstance().getTypeById(product.getType_id()).equals(type));
    }

    public Product getSelectedItem() {
        int index = tableView.getSelectionModel().getSelectedIndex();

        if (index >= 0) return tableView.getItems().get(index);
        else return null;
    }

    public List<Product> resetLastImportCodes() {
        log.info("clearing last import results...");
        return productMap.values().stream()
                .filter(product -> !product.getLastImportcodes().isEmpty() || !product.getChangecodes().isEmpty())
                .peek(product -> product.setLastImportcodes(""))
                .peek(product -> product.setChangecodes(""))
                .collect(Collectors.toList());
    }

    public List<Product> getChangedPositions() {
        return productMap.values().stream()
                .filter(product -> !product.getLastImportcodes().isEmpty())
                .collect(Collectors.toList());
    }

    public String getVendorMaterial(Product product) {
        return getVendorMaterial(product.getVendor(), product.getMaterial());
    }

    public String getVendorMaterial(VendorEnum vendor, String material) {
        return String.format("%d%s", vendor.getId(), ProductNameResolver.resolve(material));
    }
}
