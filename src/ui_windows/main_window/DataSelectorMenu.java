package ui_windows.main_window;

import core.CoreModule;
import javafx.scene.control.*;

public class DataSelectorMenu extends Menu {
    private Menu menu;
    private static final String SPACE = "     ";
    public static final RadioMenuItem MENU_DATA_ALL_ITEMS = new RadioMenuItem(SPACE + "Все позиции" + SPACE);
    public static final RadioMenuItem MENU_DATA_CUSTOM_SELECTION = new RadioMenuItem(SPACE + "Запрос" + SPACE);
    public static final RadioMenuItem MENU_DATA_LAST_IMPORT_RESULT = new RadioMenuItem(SPACE + "Результаты последнего импорта" + SPACE);

    public DataSelectorMenu(Menu menu) {
        this.menu = menu;
        init(menu);


    }

    private void init(Menu menu) {
        ToggleGroup dataSelector = new ToggleGroup();

        MENU_DATA_ALL_ITEMS.setOnAction(event -> selectDataAllItems());
        MENU_DATA_CUSTOM_SELECTION.setOnAction(event -> selectDataCustomSelection());
        MENU_DATA_LAST_IMPORT_RESULT.setOnAction(event -> selectLastImportResult());

        menu.getItems().addAll(MENU_DATA_ALL_ITEMS, new SeparatorMenuItem(), MENU_DATA_CUSTOM_SELECTION, new SeparatorMenuItem(),
                MENU_DATA_LAST_IMPORT_RESULT);

        for (MenuItem mi : menu.getItems()) {
            if (mi instanceof RadioMenuItem) {
                ((RadioMenuItem) mi).setToggleGroup(dataSelector);
            }
        }

        selectMenuItem(MENU_DATA_ALL_ITEMS);
    }

    public void selectMenuItem(RadioMenuItem radioMenuItem) {
        for (MenuItem mi : menu.getItems()) {
            if (mi.equals(radioMenuItem)) {
                ((RadioMenuItem) mi).setSelected(true);
            }
        }
    }

    public void selectDataAllItems() {
        CoreModule.setCurrentItems(CoreModule.getProducts().getItems());
        CoreModule.getFilter().switchToDataItems();
        selectMenuItem(MENU_DATA_ALL_ITEMS);
    }

    public void selectDataCustomSelection() {
        CoreModule.setCurrentItems(CoreModule.getCustomItems());
        CoreModule.getFilter().switchToRequestItems();
        selectMenuItem(MENU_DATA_CUSTOM_SELECTION);
    }

    public void selectLastImportResult() {
        CoreModule.setCurrentItems(CoreModule.getProducts().getChangedPositions());
        CoreModule.getFilter().switchToDataItems();
        selectMenuItem(MENU_DATA_LAST_IMPORT_RESULT);
    }

    public void reportNoCertificates() {
        /*ArrayList<Product> result = new ArrayList<>();

        new Thread(() -> {
            for (Product pr : CoreModule.getProducts().getItems()) {
                double progress = (double) CoreModule.getProducts().getItems().indexOf(pr) /
                        (double) CoreModule.getProducts().getItems().size();

                MainWindow.setProgress(progress);

                CertificatesChecker certificatesChecker = CoreModule.getCertificates().getCertificatesChecker();
                certificatesChecker.check(pr, true);
                if (certificatesChecker.getResultTableItems().size() == 0) result.add(pr);
            }

            MainWindow.setProgress(0.0);

            Platform.runLater(() -> {
                CoreModule.setCurrentItems(result);
                CoreModule.filter();
            });
        }).start();*/
    }

    public void reportProblemCertificates() {
        /*ArrayList<Product> result = new ArrayList<>();

        new Thread(() -> {
            for (Product pr : CoreModule.getProducts().getItems()) {
                double progress = (double) CoreModule.getProducts().getItems().indexOf(pr) /
                        (double) CoreModule.getProducts().getItems().size();

                MainWindow.setProgress(progress);

                CertificatesChecker certificatesChecker = CoreModule.getCertificates().getCertificatesChecker();
                certificatesChecker.check(pr, true);
                for (CertificateVerificationItem cv : certificatesChecker.getResultTableItems()) {
                    if (cv.getStatus().startsWith(CertificatesChecker.NOT_OK)) {
                        result.add(pr);
                        break;
                    }
                }
            }

            MainWindow.setProgress(0.0);

            Platform.runLater(() -> {
                CoreModule.setCurrentItems(result);
                CoreModule.filter();
            });
        }).start();*/
    }

    public void reportExpiredSoonCertificates() {
        /*ArrayList<Product> result = new ArrayList<>();
        String durationS = Dialogs.textInput("Ввод данных", "Введите срок, в течении которого истекает\n" +
                "действие сертификата (месяцев)", "2");

        if (durationS == null || !durationS.matches("\\d+")) return;

        final int duration = Integer.parseInt(durationS);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

        new Thread(() -> {
            for (Product pr : CoreModule.getProducts().getItems()) {
                double progress = (double) CoreModule.getProducts().getItems().indexOf(pr) /
                        (double) CoreModule.getProducts().getItems().size();

                MainWindow.setProgress(progress);

                CertificatesChecker certificatesChecker = CoreModule.getCertificates().getCertificatesChecker();
                certificatesChecker.check(pr, true);
                for (CertificateVerificationItem cv : certificatesChecker.getResultTableItems()) {
                    Date certDate = Utils.getDate(cv.getExpirationDate());
                    Date now = new Date();
                    long diff = certDate.getTime() - now.getTime();

                    long months = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) / 30;

                    if (months <= duration) {
                        result.add(pr);
                        break;
                    }
                }
            }

            MainWindow.setProgress(0.0);

            Platform.runLater(() -> {
                CoreModule.setCurrentItems(result);
                CoreModule.filter();
            });
        }).start();*/
    }

    public void reportDoubles() {
        new Thread(() -> {
            CoreModule.setCurrentItems(CoreModule.getProducts().getDoubles());
            CoreModule.getFilter().apply();
        }).start();

    }


}
