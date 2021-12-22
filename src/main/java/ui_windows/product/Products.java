package ui_windows.product;

import core.Initializable;
import database.ProductsDB;
import javafx.scene.control.TableView;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.options_window.product_lgbk.ProductLgbkGroups;
import ui_windows.product.vendors.VendorEnum;
import utils.comparation.products.ProductNameResolver;

import java.util.*;
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

    public Product getProductByUnknownVendorAndMaterialId(String id) {
        Product result = productMap.get(id);

        if (result != null) {
            return result;
        }

        id = id.replaceFirst(String.valueOf(id.charAt(0)), String.valueOf(49 - id.charAt(0)));

        return productMap.get(id);
    }

    public Product getProductByMaterial(String material) {
        Product result = productMap.get("1" + material);

        return result == null ? productMap.get("0" + material) : result;
    }

    public Product getItemByMaterialOrArticle(String name) {
        for (VendorEnum vendor : VendorEnum.values()) {
            String id = getVendorMaterial(vendor, name);
            if (productMap.containsKey(id)) {
                return productMap.get(id);
            }
        }

        String resolvedName = ProductNameResolver.resolve(name);
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

    public Boolean isCheckedToPrice(Product product) {
        Boolean isCheckedToPrice = product.getPrice();
        Boolean isBlocked = product.getBlocked();

        return isCheckedToPrice == null
                ? null
                : isBlocked == null ? isCheckedToPrice : isCheckedToPrice && !isBlocked;
    }

    public void addHistory(Product product, String newHistory) {
        if (product.getHistory().isEmpty()) {
            product.setHistory(newHistory);
        } else {
            product.setHistory(product.getHistory().concat("|").concat(newHistory));
        }
    }

    public void addLastImportCodes(Product product, String codes) {
        if (product.getLastImportcodes().isEmpty()) {
            product.setLastImportcodes(codes);
        } else {
            product.setLastImportcodes(product.getLastImportcodes().concat(",").concat(codes));
        }
    }

    public List<Integer> getGlobalNorms(Product product) {
        return new ArrayList<Integer>(ProductLgbkGroups.getInstance().getGlobalNormIds(new ProductLgbk(product)));
    }

    public String getDescriptionRuEn(Product product) {
        return product.getDescriptionru().isEmpty() ? product.getDescriptionen() : product.getDescriptionru();
    }

    public int getLeadTimeRu(Product product) {
        return product.getLeadTime() + 14;
    }

    public String getSsnNotEmpty(Product product) {
        return product.getProductForPrint() == null || product.getProductForPrint().isEmpty() ?
                product.getMaterial() : product.getProductForPrint();
    }
}
