package ui_windows.main_window;

import core.CoreModule;
import javafx.scene.control.*;
import ui_windows.main_window.filter_window.DataSelectorMenuItem;
import ui_windows.main_window.filter_window.FilterParameters;

import java.util.ArrayList;

public class DataSelectorMenu extends Menu {
    private static final String SPACE = "     ";
    public static final DataSelectorMenuItem MENU_DATA_CUSTOM_SELECTION = new DataSelectorMenuItem(
            SPACE + "Запрос" + SPACE, new FilterParameters(), () -> new ArrayList<>(CoreModule.getCustomItems()));
    public static final DataSelectorMenuItem MENU_DATA_LAST_IMPORT_RESULT = new DataSelectorMenuItem(
            SPACE + "Результаты последнего импорта" + SPACE, new FilterParameters(), () -> CoreModule.getProducts().getChangedPositions());
    private static final DataSelectorMenuItem MENU_DATA_ALL_ITEMS = new DataSelectorMenuItem(
            SPACE + "Все позиции" + SPACE, new FilterParameters().setPriceItems(true), () -> CoreModule.getProducts().getItems());
    private Menu menu;

    public DataSelectorMenu(Menu menu) {
        this.menu = menu;
        init(menu);
    }

    private void init(Menu menu) {
        menu.getItems().addAll(
                MENU_DATA_ALL_ITEMS,
                new SeparatorMenuItem(),
                MENU_DATA_CUSTOM_SELECTION,
                new SeparatorMenuItem(),
                MENU_DATA_LAST_IMPORT_RESULT);

        ToggleGroup dataSelector = new ToggleGroup();
        for (MenuItem mi : menu.getItems()) {
            if (mi instanceof RadioMenuItem) {
                ((RadioMenuItem) mi).setToggleGroup(dataSelector);
            }
        }

        dataSelector.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != oldValue) {
                FilterParameters oldFilterParameters = null;
                FilterParameters newFilterParameters = null;
                if (oldValue != null) oldFilterParameters = ((DataSelectorMenuItem) oldValue).getFilterParameters();
                if (newValue != null) {
                    newFilterParameters = ((DataSelectorMenuItem) newValue).getFilterParameters();
                    CoreModule.setCurrentItems(((DataSelectorMenuItem) newValue).getSyncDataSource().syncData());
                }
                CoreModule.getFilter().switchFilterParameters(oldFilterParameters, newFilterParameters);
            }
        });

        selectMenuItem(MENU_DATA_ALL_ITEMS);
    }

    public void selectMenuItem(RadioMenuItem radioMenuItem) {
        for (MenuItem mi : menu.getItems()) {
            if (mi.equals(radioMenuItem)) {
                ((RadioMenuItem) mi).setSelected(true);
            }
        }
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
