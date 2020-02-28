package core;

import database.DataBase;
import files.Folders;
import javafx.application.Platform;
import javafx.scene.control.TableView;
import ui_windows.main_window.DataSelectorMenu;
import ui_windows.main_window.MainWindow;
import ui_windows.main_window.filter_window.Filter;
import ui_windows.options_window.certificates_editor.Certificates;
import ui_windows.options_window.certificates_editor.CertificatesTable;
import ui_windows.options_window.certificates_editor.certificate_content_editor.CertificatesContent;
import ui_windows.options_window.certificates_editor.certificate_content_editor.CertificatesContentTable;
import ui_windows.options_window.families_editor.ProductFamilies;
import ui_windows.options_window.families_editor.ProductFamily;
import ui_windows.options_window.order_accessibility_editor.OrdersAccessibility;
import ui_windows.options_window.price_lists_editor.PriceLists;
import ui_windows.options_window.product_lgbk.LgbkAndParent;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.options_window.product_lgbk.ProductLgbkGroups;
import ui_windows.options_window.product_lgbk.ProductLgbks;
import ui_windows.options_window.profile_editor.Profiles;
import ui_windows.options_window.requirements_types_editor.RequirementTypes;
import ui_windows.options_window.requirements_types_editor.RequirementTypesTable;
import ui_windows.options_window.user_editor.Users;
import ui_windows.product.Product;
import ui_windows.product.ProductTypes;
import ui_windows.product.Products;
import utils.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;

public class CoreModule {
    private static DataBase dataBase;
    private static RequirementTypes requirementTypes;
    private static RequirementTypesTable requirementTypesTable;
    private static Certificates certificates;
    private static CertificatesTable certificatesTable;
    private static CertificatesContent certificatesContent;
    private static CertificatesContentTable certificatesContentTable;
    private static Products products;
    private static ProductTypes productTypes;
    private static ProductFamilies productFamilies;
    private static ProductLgbks productLgbks;
    private static ProductLgbkGroups productLgbkGroups;
    private static OrdersAccessibility ordersAccessibility;
    private static Profiles profiles;
    private static Users users;
    private static Folders folders;
    private static Filter filter;
    private static PriceLists priceLists;

    private static ArrayList<Product> currentItems = new ArrayList<>();
    private static HashSet<Product> customItems = new HashSet<>();

    public static boolean init() {
        folders = new Folders();
        if ((folders.getDbFile()) == null || (folders.getDbFile() != null && !folders.getDbFile().exists())) {
            Dialogs.showMessage("Файл данных", "Файл базы данных не найден.\n" +
                    "Программа не может продолжить работу.");
            return false;
        }

        dataBase = new DataBase();
        dataBase.connect(folders.getDbFile());

        profiles = new Profiles().getFromDB();
        users = new Users().getFromDB();
        users.checkCurrentUser(Utils.getComputerName());

        ordersAccessibility = new OrdersAccessibility().getFromDB();
        requirementTypes = new RequirementTypes();

        productTypes = new ProductTypes().getFromDB();

        certificatesContent = new CertificatesContent();
        certificates = new Certificates();

        productFamilies = new ProductFamilies().getFromDB();
        productLgbks = new ProductLgbks().getFromDB();

        filter = new Filter();
        products = new Products().getFromDB();
        currentItems.addAll(products.getItems());

        productLgbkGroups = new ProductLgbkGroups().get();
        priceLists = new PriceLists().getFromDB();

//        if (users.getCurrentUser().getProfile().getName() == "Общий доступ") {
        if (!dataBase.disconnect()) System.out.println("fail of DB disconnecting");
//        }

        return true;
    }

    public static DataBase getDataBase() {
        return dataBase;
    }

    public static RequirementTypes getRequirementTypes() {
        return requirementTypes;
    }

    public static RequirementTypesTable getRequirementTypesTable() {
        return requirementTypesTable;
    }

    public static void setRequirementTypesTable(RequirementTypesTable requirementTypesTable) {
        CoreModule.requirementTypesTable = requirementTypesTable;
    }

    public static Certificates getCertificates() {
        return certificates;
    }

    public static CertificatesTable getCertificatesTable() {
        return certificatesTable;
    }

    public static void setCertificatesTable(CertificatesTable certificatesTable) {
        CoreModule.certificatesTable = certificatesTable;
    }

    public static CertificatesContent getCertificatesContent() {
        return certificatesContent;
    }

    public static CertificatesContentTable getCertificatesContentTable() {
        return certificatesContentTable;
    }

    public static void setCertificatesContentTable(CertificatesContentTable certificatesContentTable) {
        CoreModule.certificatesContentTable = certificatesContentTable;
    }

    public static Products getProducts() {
        return products;
    }

    public static void setProducts(Products products) {
        CoreModule.products = products;
    }

    public static ProductTypes getProductTypes() {
        return productTypes;
    }

    public static ProductFamilies getProductFamilies() {
        return productFamilies;
    }

    public static void setProductFamilies(ProductFamilies productFamilies) {
        CoreModule.productFamilies = productFamilies;
    }

    public static ProductLgbks getProductLgbks() {
        return productLgbks;
    }

    public static void setProductLgbks(ProductLgbks productLgbks) {
        CoreModule.productLgbks = productLgbks;
    }

    public static OrdersAccessibility getOrdersAccessibility() {
        return ordersAccessibility;
    }

    public static void setOrdersAccessibility(OrdersAccessibility ordersAccessibility) {
        CoreModule.ordersAccessibility = ordersAccessibility;
    }

    public static Profiles getProfiles() {
        return profiles;
    }

    public static Users getUsers() {
        return users;
    }

    public static Folders getFolders() {
        return folders;
    }

    public static Filter getFilter() {
        return filter;
    }

    public static ArrayList<Product> getCurrentItems() {
        return currentItems;
    }

    public static synchronized void setCurrentItems(ArrayList<Product> items) {
        CoreModule.currentItems.clear();
        CoreModule.currentItems.addAll(items);
    }

    public static synchronized void setCurrentItems(HashSet<Product> items) {
        CoreModule.currentItems.clear();
        CoreModule.currentItems.addAll(items);
    }

    public static ProductLgbkGroups getProductLgbkGroups() {
        return productLgbkGroups;
    }

    public static PriceLists getPriceLists() {
        return priceLists;
    }

    public static HashSet<Product> getCustomItems() {
        return customItems;
    }

    public static void setCustomItems(HashSet<Product> customItems) {
        SharedData.SHD_CUSTOM_DATA.setData(new ArrayList<>(customItems));
        CoreModule.customItems = customItems;
    }

    public static void setCustomItems(ArrayList<Product> customItems) {
        SharedData.SHD_CUSTOM_DATA.setData(customItems);
        CoreModule.customItems.clear();
        CoreModule.customItems.addAll(customItems);
    }

    public static void setAndDisplayCustomItems(ArrayList<Product> customItems) {
        SharedData.SHD_CUSTOM_DATA.setData(customItems);
        CoreModule.customItems.clear();
        CoreModule.customItems.addAll(customItems);
        MainWindow.getController().getDataSelectorMenu().selectMenuItem(DataSelectorMenu.MENU_DATA_CUSTOM_SELECTION);
    }
}
