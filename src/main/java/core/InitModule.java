package core;

import database.DataBase;
import files.Folders;
import javafx.application.Application;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import preloader.Notification;
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

public class InitModule {
    private static HashSet<Product> customItems = new HashSet<>();
    private static final Logger logger = LogManager.getLogger(InitModule.class);
    private Application application;

    public void init(Application application) throws Exception {
        this.application = application;
        String[] initStages = {
                "Инициализация папок и подключения к сетевым ресурсам",
                "Инициализация подключения к БД",
                "Инициализация профилей",
                "Инициализация пользователей",
                "Инициализация прав текущего пользователя",
                "Инициализация кодов доступности для заказа",
                "Инициализация типов норм для сертификатов",
                "Инициализация типов продукции",
                "Инициализация сертификатов",
                "Инициализация семейств продукции",
                "Инициализация иерархии продукции",
                "Инициализация продукции",
                "Инициализация фильтра",
                "Инициализация вспомогательных элементов",
                "Инициализация прайс-листов",
        };
        int stageOrder = 0;

        logAndNotifyPreloader(initStages[stageOrder++]);
        Folders.getInstance().init();
        logAndNotifyPreloader(initStages[stageOrder++]);
        DataBase.getInstance().firstConnect(Folders.getInstance().getMainDbFile(), Folders.getInstance().getCashedDbFile());

        logAndNotifyPreloader(initStages[stageOrder++]);
        Profiles.getInstance().getFromDB();
        logAndNotifyPreloader(initStages[stageOrder++]);
        Users.getInstance().getFromDB();
        logAndNotifyPreloader(initStages[stageOrder++]);
        Users.getInstance().checkCurrentUser(Utils.getComputerName());

        logAndNotifyPreloader(initStages[stageOrder++]);
        OrdersAccessibility.getInstance().getFromDB();
        logAndNotifyPreloader(initStages[stageOrder++]);
        RequirementTypes.getInstance().getFromDb();
        logAndNotifyPreloader(initStages[stageOrder++]);
        ProductTypes.getInstance().getFromDB();

        logAndNotifyPreloader(initStages[stageOrder++]);
        CertificatesContent.getInstance().getFromDb();
        Certificates.getInstance().getFromDb();

        logAndNotifyPreloader(initStages[stageOrder++]);
        ProductFamilies.getInstance().getFromDB();
        logAndNotifyPreloader(initStages[stageOrder++]);
        ProductLgbks.getInstance().getFromDB();
        logAndNotifyPreloader(initStages[stageOrder++]);
        Products.getInstance().getFromDB();

        logAndNotifyPreloader(initStages[stageOrder++]);
        Filter_SE.getInstance();
        SHD_FILTER_PARAMETERS.setData(InitModule.class, new FilterParameters_SE(), NOT_NOTIFY);

        logAndNotifyPreloader(initStages[stageOrder++]);
        ProductLgbkGroups.getInstance().init();
        logAndNotifyPreloader(initStages[stageOrder++]);
        PriceLists.getInstance().getFromDB();

        DataBase.getInstance().disconnect();
    }

    private void logAndNotifyPreloader(String details) throws Exception {
        logger.info(details);
        application.notifyPreloader(Notification.build(details + "..."));
    }

    public static HashSet<Product> getCustomItems() {
        return customItems;
    }

    public static void setAndDisplayCustomItems(List<Product> customItems) {
        SharedData.SHD_CUSTOM_DATA.setData(InitModule.class, customItems);
        InitModule.customItems.clear();
        InitModule.customItems.addAll(customItems);
//        MainWindow.getController().getDataSelectorMenu().selectMenuItem(DataSelectorMenu.MENU_DATA_CUSTOM_SELECTION);
        DataSelectorMenu.MENU_DATA_CUSTOM_SELECTION.activate();
    }
}
