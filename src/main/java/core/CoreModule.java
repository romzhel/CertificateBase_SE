package core;

import database.DataBase;
import files.Folders;
import ui_windows.main_window.DataSelectorMenu;
import ui_windows.main_window.filter_window_se.FilterParameters_SE;
import ui_windows.main_window.filter_window_se.Filter_SE;
import ui_windows.options_window.certificates_editor.Certificates;
import ui_windows.options_window.certificates_editor.certificate_content_editor.CertificatesContent;
import ui_windows.options_window.families_editor.ProductFamilies;
import ui_windows.options_window.order_accessibility_editor.OrdersAccessibility;
import ui_windows.options_window.price_lists_editor.PriceLists;
import ui_windows.options_window.product_lgbk.ProductLgbkGroups;
import ui_windows.options_window.product_lgbk.ProductLgbks;
import ui_windows.options_window.profile_editor.Profiles;
import ui_windows.options_window.requirements_types_editor.RequirementTypes;
import ui_windows.options_window.user_editor.Users;
import ui_windows.product.Product;
import ui_windows.product.ProductTypes;
import ui_windows.product.Products;
import utils.Utils;

import java.util.HashSet;
import java.util.List;

import static core.SharedData.NOT_NOTIFY;
import static core.SharedData.SHD_FILTER_PARAMETERS;

public class CoreModule {
    private static HashSet<Product> customItems = new HashSet<>();

    public void init() throws RuntimeException {
        Folders.getInstance().init();
        DataBase.getInstance().connect(Folders.getInstance().getDbFile());

        Profiles.getInstance().getFromDB();
        Users.getInstance().getFromDB();
        Users.getInstance().checkCurrentUser(Utils.getComputerName());

        OrdersAccessibility.getInstance().getFromDB();
        RequirementTypes.getInstance().getFromDb();
        ProductTypes.getInstance().getFromDB();

        CertificatesContent.getInstance().getFromDb();
        Certificates.getInstance().getFromDb();

        ProductFamilies.getInstance().getFromDB();
        ProductLgbks.getInstance().getFromDB();
        Products.getInstance().getFromDB();

        Filter_SE.getInstance();
        SHD_FILTER_PARAMETERS.setData(CoreModule.class, new FilterParameters_SE(), NOT_NOTIFY);

        ProductLgbkGroups.getInstance().init();
        PriceLists.getInstance().getFromDB();

        DataBase.getInstance().disconnect();
    }

    public static HashSet<Product> getCustomItems() {
        return customItems;
    }

    public static void setAndDisplayCustomItems(List<Product> customItems) {
        SharedData.SHD_CUSTOM_DATA.setData(CoreModule.class, customItems);
        CoreModule.customItems.clear();
        CoreModule.customItems.addAll(customItems);
//        MainWindow.getController().getDataSelectorMenu().selectMenuItem(DataSelectorMenu.MENU_DATA_CUSTOM_SELECTION);
        DataSelectorMenu.MENU_DATA_CUSTOM_SELECTION.activate();
    }
}
