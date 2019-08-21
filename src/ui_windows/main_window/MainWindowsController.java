package ui_windows.main_window;

import core.CoreModule;
import core.Dialogs;
import database.ProductsDB;
import files.ExcelFile;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import ui_windows.login_window.LoginWindow;
import ui_windows.main_window.filter_window.FilterWindow;
import ui_windows.options_window.OptionsWindow;
import ui_windows.options_window.certificates_editor.certificate_content_editor.certificatesChecker.CertificateVerificationItem;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.options_window.user_editor.User;
import ui_windows.productEditorWindow.ProductEditorWindow;
import ui_windows.request_certificates.CertificateRequestWindow;
import utils.ProductsComparator;
import utils.ProductsComparatorResult;
import utils.Utils;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import static ui_windows.Mode.*;

public class MainWindowsController implements Initializable {
    ProductsComparatorResult lastComparationResult;
    boolean clearOldResult;

    @FXML
    TableView<Product> tvTable;

    @FXML
    TextField tfFilter;

    @FXML
    Label lbRecordCount;

    @FXML
    ProgressBar pbExecuted;

    @FXML
    MenuBar mnuBar;

    @FXML
    MenuItem miFile;

    @FXML
    MenuItem miOptions;

    @FXML
    RadioMenuItem rmiAllItems;

    @FXML
    RadioMenuItem rmiWithoutCerts;

    @FXML
    RadioMenuItem rmiWithProblemCerts;

    @FXML
    RadioMenuItem rmiWithExpCerts;

    @FXML
    RadioMenuItem rmiDoubles;

    @FXML
    RadioMenuItem rmiLastImportResult;

    @FXML
    Menu miReports;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lastComparationResult = new ProductsComparatorResult();

        CoreModule.getProducts().setTableView(tvTable);
        MainWindow.setProgressBar(pbExecuted);

        MainWindow.setMiFile(miFile);
        MainWindow.setMiOptions(miOptions);
        MainWindow.setMiReports(miReports);

        ToggleGroup tg = new ToggleGroup();
        rmiAllItems.setToggleGroup(tg);
        rmiWithoutCerts.setToggleGroup(tg);
        rmiWithProblemCerts.setToggleGroup(tg);
        rmiWithExpCerts.setToggleGroup(tg);
        rmiDoubles.setToggleGroup(tg);
        rmiLastImportResult.setToggleGroup(tg);

        rmiAllItems.setSelected(true);

        String[] cols = new String[]{"material", "article", "description", "family", "endofservice",
                "country", "dchain"};
        String[] titles = new String[]{"Заказной номер", "Артикул", "Описание", "Направление", "Окончание",
                "Страна", "Доступность"};
        int[] colsWidth = new int[]{130, 130, 500, 100, 100, 50, 150};
        boolean[] centerAligment = new boolean[]{true, true, false, true, true, true, false};

//        for (String s : cols) {//add columns
        for (int i = 0; i < cols.length; i++) {
            TableColumn<Product, String> col = new TableColumn<>(titles[i]);

            if (cols[i] == "family") {
                col.setCellValueFactory(param -> {
                    Product pr = param.getValue();

                    if (pr.getFamily() > 0) {//individual value
                        return new SimpleStringProperty(CoreModule.getProductFamilies().getFamilyNameById(pr.getFamily()));
                    } else {//try to calculate it
                        int id = CoreModule.getProductLgbks().
                                getFamilyIdByLgbk(new ProductLgbk(pr.getLgbk(), pr.getHierarchy()));
                        String family = CoreModule.getProductFamilies().getFamilyNameById(id);

                        if (family == "") return new SimpleStringProperty(pr.getLgbk().concat(" (").
                                concat(pr.getHierarchy()).concat(")"));
                        else return new SimpleStringProperty(family);
                    }
                });

            } else if (cols[i] == "dchain") {
                col.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getOrderableStatus()));

            } else if(cols[i] == "description") {
                col.setCellValueFactory(param -> {
                    Product pr = param.getValue();

                    if (!pr.getDescriptionru().trim().isEmpty()) return new SimpleStringProperty(pr.getDescriptionru());
                    else if (!pr.getDescriptionen().trim().isEmpty()) return  new SimpleStringProperty(pr.getDescriptionen());
                    else return new SimpleStringProperty("");
                });
            } else col.setCellValueFactory(new PropertyValueFactory<>(cols[i]));

            col.setPrefWidth(colsWidth[i]);
            if (centerAligment[i]) col.setStyle("-fx-alignment: CENTER");
            tvTable.getColumns().add(col);
        }

        TableColumn<Product, Boolean> boolCol = new TableColumn<>("Прайс");
        boolCol.setCellFactory(CheckBoxTableCell.forTableColumn(boolCol));
        boolCol.setCellValueFactory(param -> new SimpleBooleanProperty(param.getValue().isPrice()));
        boolCol.setPrefWidth(50);
        boolCol.setStyle("-fx-alignment: CENTER");
        tvTable.getColumns().add(boolCol);

        tvTable.setOnMouseClicked(event -> {//double click on product
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.getClickCount() == 2) {
                    editProduct();//open product editor window
                }
            }
        });
        tvTable.setPlaceholder(new Label("Нет данных для отображения"));

        lbRecordCount.setText(Integer.toString(tvTable.getItems().size()));
    }


    public void openNow() {
        if (ExcelFile.open(Dialogs.selectNOWFile(MainWindow.getMainStage()))) {//open file

            MainWindow.setProgress(-1);

            clearOldResult = false;
            if (Dialogs.confirm("Удаление информации о предыдущем импорте", "Желаете удалить информацию об " +
                    "изменённых позициях во время предыдущего импорта?")) clearOldResult = true;

            new Thread(() -> {

                ArrayList<Product> resetedItems = clearOldResult == false ? new ArrayList<>() : CoreModule.getProducts().resetLastImportCodes();
                HashSet<Product> changedItemsForDB = new HashSet<>(resetedItems);

                Products compProducts = new Products();
                compProducts.setItems(ExcelFile.getProductDataFromAllSheets());//get new products from Excel file

                ProductsComparator pc = new ProductsComparator(CoreModule.getProducts(), compProducts, "material", "price",
                        "archive", "needaction", "notused");//compare new and existing products
                lastComparationResult = pc.getResult();
                changedItemsForDB.addAll(lastComparationResult.getChangedItems());

                rmiLastImportResult.setSelected(true);

                selectLastImportResult();

                ProductsDB request = new ProductsDB();

                //applying not used property from Lgbk table for new and changed items
                CoreModule.getProducts().getChangedNotUsedProductsFromLgbk(lastComparationResult.getChangedItems());
                CoreModule.getProducts().getChangedNotUsedProductsFromLgbk(lastComparationResult.getNewItems());

                request.putData(lastComparationResult.getNewItems());// save new items to db
                request.updateData(new ArrayList<>(changedItemsForDB));//save changed items to db

                System.out.println("writing to DB finished");

                MainWindow.setProgress(0);

                Platform.runLater(() -> Dialogs.showMessage("Результаты импорта", "Новых позиций: " +
                        lastComparationResult.getNewItems().size() + "\nИзменённых позиций: " +
                        lastComparationResult.getChangedItems().size()));

            }).start();
        }
    }

    public void openOptionsWindow() {
        new OptionsWindow();
    }

    public void addProduct() {
        new ProductEditorWindow(ADD);
    }

    public void editProduct() {
        if (tvTable.getSelectionModel().getSelectedIndex() < 0) Dialogs.showMessage("Выбор строки",
                "Нужно выбрать строку");
        else new ProductEditorWindow(EDIT);
    }

    public void deleteProduct() {

    }

    public void displayInTable(ArrayList<Product> products) {
        tvTable.getItems().clear();
        tvTable.getItems().addAll(products);
        lbRecordCount.setText(Integer.toString(tvTable.getItems().size()));
    }

    public void displayAllItems() {
        displayInTable(CoreModule.getProducts().getItems());
    }

    public void displayNewItems() {
        displayInTable(lastComparationResult.getNewItems());
    }

    public void displayChangedItems() {
        if (lastComparationResult.getChangedItems() != null)
            displayInTable(lastComparationResult.getChangedItems());
    }

    public void displayGoneItems() {
        if (lastComparationResult.getGoneItems() != null)
            displayInTable(lastComparationResult.getGoneItems());
    }

    public void clearDisplay() {
        tvTable.getItems().clear();
    }

    public void actionLogin() {
        new LoginWindow();
    }

    public void userInfo() {
        User user = CoreModule.getUsers().getCurrentUser();
        Dialogs.showMessage("Информация о текущем пользователе", "Пользователь: " + user.getName() + " " +
                user.getSurname() + "\nПрофиль: " + user.getProfile().getName());
    }

    public void displayFilterOptions() {
        new FilterWindow();
    }

    public void reportAllItems() {
        CoreModule.setCurrentItems(CoreModule.getProducts().getItems());
        CoreModule.filter();
    }

    public void reportNoCertificates() {
        ArrayList<Product> result = new ArrayList<>();

        new Thread(() -> {
            for (Product pr : CoreModule.getProducts().getItems()) {
                double progress = (double) CoreModule.getProducts().getItems().indexOf(pr) /
                        (double) CoreModule.getProducts().getItems().size();

                MainWindow.setProgress(progress);

                if (CoreModule.getCertificates().checkCertificates(pr).size() == 0) result.add(pr);
            }

            MainWindow.setProgress(0.0);

            Platform.runLater(() -> {
                CoreModule.setCurrentItems(result);
                CoreModule.filter();
            });
        }).start();
    }

    public void reportProblemCertificates() {
        ArrayList<Product> result = new ArrayList<>();

        new Thread(() -> {
            for (Product pr : CoreModule.getProducts().getItems()) {
                double progress = (double) CoreModule.getProducts().getItems().indexOf(pr) /
                        (double) CoreModule.getProducts().getItems().size();

                MainWindow.setProgress(progress);

                for (CertificateVerificationItem cv : CoreModule.getCertificates().checkCertificates(pr)) {
                    if (cv.getStatus().startsWith("НЕ ОК")) {
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
        }).start();
    }

    public void reportExpiredSoonCertificates() {
        ArrayList<Product> result = new ArrayList<>();
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

                for (CertificateVerificationItem cv : CoreModule.getCertificates().checkCertificates(pr)) {
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
        }).start();
    }

    public void reportDoubles() {
        new Thread(() -> {
            CoreModule.setCurrentItems(CoreModule.getProducts().getDoubles());
            CoreModule.filter();
        }).start();

    }

    public void actionRequestCertificates() {
        new CertificateRequestWindow();
    }

    public void selectLastImportResult() {
        CoreModule.setCurrentItems(CoreModule.getProducts().getChangedPositions());
        CoreModule.filter();
    }
}
