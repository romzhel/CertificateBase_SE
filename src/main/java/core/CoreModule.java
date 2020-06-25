package core;

import database.DataBase;
import files.Folders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private static final Logger logger = LogManager.getLogger(CoreModule.class);

    public void init() throws Exception {
        logger.info("Initializing Folders");
        Folders.getInstance().init();
        logger.info("Initializing Connection to DB");
        DataBase.getInstance().firstConnect(Folders.getInstance().getMainDbFile(), Folders.getInstance().getCashedDbFile());

        logger.info("Initializing Profiles");
        Profiles.getInstance().getFromDB();
        logger.info("Initializing Users");
        Users.getInstance().getFromDB();
        logger.info("Initializing Current user rights");
        Users.getInstance().checkCurrentUser(Utils.getComputerName());

        logger.info("Initializing Orders Accessibility data");
        OrdersAccessibility.getInstance().getFromDB();
        logger.info("Initializing Requirements Types");
        RequirementTypes.getInstance().getFromDb();
        logger.info("Initializing Product types");
        ProductTypes.getInstance().getFromDB();

        logger.info("Initializing Certificates Content");
        CertificatesContent.getInstance().getFromDb();
        logger.info("Initializing Certificates");
        Certificates.getInstance().getFromDb();

        logger.info("Initializing Product families");
        ProductFamilies.getInstance().getFromDB();
        logger.info("Initializing Product GBK hierarchy");
        ProductLgbks.getInstance().getFromDB();
        logger.info("Initializing Products");
        Products.getInstance().getFromDB();

        logger.info("Initializing Filter");
        Filter_SE.getInstance();
        SHD_FILTER_PARAMETERS.setData(CoreModule.class, new FilterParameters_SE(), NOT_NOTIFY);

        logger.info("Initializing Product GBK groups");
        ProductLgbkGroups.getInstance().init();
        logger.info("Initializing PriceLists");
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
