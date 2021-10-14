package core;

import database.DataBase;
import files.Folders;
import javafx.application.Application;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import preloader.Notification;
import ui_windows.main_window.DataSelectorMenu;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InitModule {
    private static final Logger logger = LogManager.getLogger(InitModule.class);
    private static Set<Product> customItems = new HashSet<>();
    private Application application;

    public static Set<Product> getCustomItems() {
        return customItems;
    }

    public static void setAndDisplayCustomItems(List<Product> customItems) {
        SharedData.SHD_CUSTOM_DATA.setData(InitModule.class, customItems);
        InitModule.customItems.clear();
        InitModule.customItems.addAll(customItems);
//        MainWindow.getController().getDataSelectorMenu().selectMenuItem(DataSelectorMenu.MENU_DATA_CUSTOM_SELECTION);
        DataSelectorMenu.MENU_DATA_CUSTOM_SELECTION.activate();
    }

    public void init(Application application) throws Exception {
        this.application = application;
        Object[][] initStages = {
                {"Инициализация папок и подключения к сетевым ресурсам", Folders.getInstance()},
                {"Инициализация подключения к БД", DataBase.getInstance()},
                {"Инициализация профилей", Profiles.getInstance()},
                {"Инициализация пользователей", Users.getInstance()},
                {"Инициализация прав текущего пользователя", null},
                {"Инициализация кодов доступности для заказа", OrdersAccessibility.getInstance()},
                {"Инициализация типов норм для сертификатов", RequirementTypes.getInstance()},
                {"Инициализация типов продукции", ProductTypes.getInstance()},
                {"Инициализация содержимого сертификатов", CertificatesContent.getInstance()},
                {"Инициализация сертификатов", Certificates.getInstance()},
                {"Инициализация семейств продукции", ProductFamilies.getInstance()},
                {"Инициализация иерархии продукции", ProductLgbks.getInstance()},
                {"Инициализация продукции", Products.getInstance()},
                {"Инициализация фильтра", Filter_SE.getInstance()},
                {"Инициализация вспомогательных элементов", ProductLgbkGroups.getInstance()},
                {"Инициализация прайс-листов", PriceLists.getInstance()},
        };

        for (Object[] initStage : initStages) {
            logAndNotifyPreloader(initStage[0].toString());
            if (initStage[1] != null) {
                ((Initializable) initStage[1]).init();
            }
        }

        DataBase.getInstance().disconnect();
    }

    private void logAndNotifyPreloader(String details) throws Exception {
        logger.info(details);
        application.notifyPreloader(Notification.build(details + "..."));
    }
}
