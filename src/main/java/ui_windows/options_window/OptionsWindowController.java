package ui_windows.options_window;

import database.ProfilesDB;
import files.Folders;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ui.Dialogs;
import ui_windows.options_window.certificates_editor.*;
import ui_windows.options_window.families_editor.FamiliesEditorWindow;
import ui_windows.options_window.families_editor.ProductFamilies;
import ui_windows.options_window.families_editor.ProductFamiliesTable;
import ui_windows.options_window.families_editor.ProductFamily;
import ui_windows.options_window.order_accessibility_editor.OrderAccessibility;
import ui_windows.options_window.order_accessibility_editor.OrdersAccessibility;
import ui_windows.options_window.order_accessibility_editor.OrdersAccessibilityEditorWindow;
import ui_windows.options_window.order_accessibility_editor.OrdersAccessibilityTable;
import ui_windows.options_window.price_lists_editor.PriceList;
import ui_windows.options_window.price_lists_editor.PriceLists;
import ui_windows.options_window.price_lists_editor.PriceListsTable;
import ui_windows.options_window.price_lists_editor.se.PriceListEditorWindow;
import ui_windows.options_window.product_lgbk.*;
import ui_windows.options_window.profile_editor.Profile;
import ui_windows.options_window.profile_editor.Profiles;
import ui_windows.options_window.profile_editor.ProfilesTable;
import ui_windows.options_window.requirements_types_editor.RequirementType;
import ui_windows.options_window.requirements_types_editor.RequirementTypeEditorWindow;
import ui_windows.options_window.requirements_types_editor.RequirementTypeEditorWindowActions;
import ui_windows.options_window.requirements_types_editor.RequirementTypesTable;
import ui_windows.options_window.user_editor.User;
import ui_windows.options_window.user_editor.UserEditorWindow;
import ui_windows.options_window.user_editor.Users;
import ui_windows.options_window.user_editor.UsersTable;
import utils.Utils;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static ui_windows.Mode.ADD;
import static ui_windows.Mode.EDIT;

public class OptionsWindowController implements Initializable {

    private static final Logger logger = LogManager.getLogger(OptionsWindowController.class);
    @FXML
    public ContextMenu cmCertTypes;
    @FXML
    public ContextMenu cmFamilies;
    @FXML
    public ContextMenu cmLgbkHierarchy;
    @FXML
    public ContextMenu cmOrderable;
    @FXML
    public ContextMenu cmPriceListsTable;
    @FXML
    TableView<RequirementType> tvCertificateTypes;
    @FXML
    TableView<Certificate> tvCertificates;
    @FXML
    CheckBox hideNonCorrectCerts;
    @FXML
    TableView<ProductFamily> tvFamilies;
    @FXML
    TreeTableView<ProductLgbk> tvLgbk;
    @FXML
    TableView<OrderAccessibility> tvOrdersAccessibility;
    @FXML
    TableView<Profile> tvProfiles;
    @FXML
    TableView<User> tvUsers;
    @FXML
    TabPane tpOptions;
    @FXML
    ContextMenu cmCertificates;
    @FXML
    TableView<PriceList> tvPriceLists;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        OptionsWindow.setTpOptions(tpOptions);
        logger.trace("Initializing options window....");
        //------------------------------certificates--------------------------------------------------------------------
        RequirementTypesTable.getInstance().init(tvCertificateTypes);//fill certificates types table
        tvCertificateTypes.setOnMouseClicked(event -> {//double click on certificate type
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.getClickCount() == 2) {
                    actionEditCertificateType();//open certificate type editor window
                }
            }
        });
        logger.trace("Certificates types table initialized");

        CertificatesTable.getInstance().init(tvCertificates);//fill certificates table
        tvCertificates.setOnMouseClicked(event -> {//double click on certificate
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.getClickCount() == 2) {
//                    actionEditCertificate();//open certificate editor window
                    actionOpenCertificateFile();
                }
            }
        });
        logger.trace("Certificates table initialized");

        hideNonCorrectCerts.selectedProperty().addListener((observable, oldValue, newValue) -> {
            tvCertificates.getItems().clear();
            List<Certificate> certificateList = !newValue ? Certificates.getInstance().getItems() :
                    Certificates.getInstance().getItems().stream()
                            .filter(cert -> !cert.getName().startsWith(CertificateEditorWindowActions.DELETED_MARK))
                            .collect(Collectors.toList());
            tvCertificates.getItems().addAll(certificateList);
        });
        hideNonCorrectCerts.setSelected(true);

        //----------------------------product families------------------------------------------------------------------
        ProductFamilies.getInstance().setProductFamiliesTable(new ProductFamiliesTable(tvFamilies));  //fill families table
        tvFamilies.setOnMouseClicked(event -> {//double click on product
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.getClickCount() == 2) {
                    actionEditFamily();//open family editor window
                }
            }
        });
        logger.trace("Product families table initialized");

        ProductLgbks.getInstance().setProductLgbksTable(new ProductLgbksTable(tvLgbk));  //fill lgbk table
        tvLgbk.setOnMouseClicked(event -> {//double click on product
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.getClickCount() == 2) {
                    actionEditLgbk();//open lgbk editor window
                }
            }
        });
        tvLgbk.getRoot().setExpanded(true);
//        tvLgbk.setStyle();
        logger.trace("Product GBKs table initialized");

        //------------------------------------------order accessibility-------------------------------------------------
        OrdersAccessibility.getInstance().setTable(new OrdersAccessibilityTable(tvOrdersAccessibility)); //fill order acc table
        tvOrdersAccessibility.setOnMouseClicked(event -> {//double click on product
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.getClickCount() == 2) {
                    actionEditOrderAccessItem();//open orderable editor window
                }
            }
        });
        logger.trace("Order accessibility table initialized");

        //------------------------------------------profiles------------------------------------------------------------
        Profiles.getInstance().setTable(new ProfilesTable(tvProfiles));
        logger.trace("Profiles table initialized");
        //------------------------------------------users---------------------------------------------------------------
        Users.getInstance().setTable(new UsersTable(tvUsers));
        tvUsers.setOnMouseClicked(event -> {//double click on product
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.getClickCount() == 2) {
                    actionEditUser();//open orderable editor window
                }
            }
        });
        logger.trace("Users table initialized");

        //---------------------------------------price lists--------------------------------------------------------------
        PriceLists.getInstance().setPriceListsTable(new PriceListsTable(tvPriceLists));
        tvPriceLists.setOnMouseClicked(event -> {//double click on product
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.getClickCount() == 2) {
                    actionEditPriceList();//open orderable editor window
                }
            }
        });
        logger.trace("Price-lists table initialized");
    }

    public void actionAddCertificateType() {
        new RequirementTypeEditorWindow(ADD);
    }

    public void actionEditCertificateType() {
        new RequirementTypeEditorWindow(EDIT);
    }

    public void actionDeleteCertificateType() {
        RequirementTypeEditorWindowActions.deleteData();
    }

    public void actionAddCertificate() {
        new CertificateEditorWindow(null, ADD);
    }

    public void actionEditCertificate() {
        Certificate selectedCertificate = tvCertificates.getSelectionModel().getSelectedItem();
        if (selectedCertificate != null) {
            new CertificateEditorWindow(selectedCertificate, EDIT);
        }
    }

    public void actionDeleteCertificate() {
        Certificate selectedCertificate = tvCertificates.getSelectionModel().getSelectedItem();
        if (selectedCertificate != null && Dialogs.confirm("Удаление записи", "Действительно желаете удалить запись без возможности восстановления?")) {
            new CertificateEditorWindowActions().delete(selectedCertificate);
        }
    }

    public void actionOpenCertificateFile() {
        Certificate selectedCertificate = tvCertificates.getSelectionModel().getSelectedItem();
        if (selectedCertificate == null) {
            return;
        }

        File certFile = null;
        try {
            certFile = Folders.getInstance().getCalcCertFile(selectedCertificate.getFileName()).toFile();
            Utils.openFile(certFile);
        } catch (Exception e) {
            logger.error("ошибка открытия файла сертификата {}", certFile);
            Dialogs.showMessageTS("Ошибка открытия файла сертификата", e.getMessage());
        }
    }

    public void actionAddFamily() {
        new FamiliesEditorWindow(ADD);
    }

    public void actionEditFamily() {
        new FamiliesEditorWindow(EDIT);
    }

    public void actionDeleteFamily() {
        TableView<ProductFamily> pft = ProductFamilies.getInstance().getProductFamiliesTable().getTableView();
        int index = pft.getSelectionModel().getSelectedIndex();
        ProductFamily pf = pft.getItems().get(index);

        if (ProductLgbks.getInstance().isFamilyUsed(pf)) Dialogs.showMessage("Удаление элемента",
                "Элемент не может быть удалён, так как используется");
        else {
            if (pf != null) {
                if (Dialogs.confirm("Удаление записи", "Действительно желаете удалить запись?")) {
                    ProductFamilies.getInstance().removeItem(pf);//delete from class
                }
            }
        }
    }

    public void actionAddLgbk() {
        new LgbkEditorWindow(ADD);
    }

    public void actionEditLgbk() {
        new LgbkEditorWindow(EDIT);
    }

    public void actionDeleteLgbk() {
        TreeItem<ProductLgbk> deletedItem = tvLgbk.getSelectionModel().getSelectedItem();
        ProductLgbk deletedLgbk = deletedItem.getValue();
        if (deletedItem == null) {
            Dialogs.showMessage("Удаление элемента", "Выберите элемент");
            return;
        }

        if (deletedLgbk.getNodeType() == 0 || deletedLgbk.getNodeType() == 1) {
            Dialogs.showMessage("Удаление элемента", "Удаление корневого элемента и элементов групп не поддерживается");
        } else {
            if (Dialogs.confirm("Удаление элемента", "Действительно желаете удалить элемент?")) {
                ProductLgbks.getInstance().removeItem(deletedLgbk);
                deletedItem.getParent().getChildren().remove(deletedItem);
            }
        }
    }

    public void actionReCheckLgbkFromProducts() {
        ProductLgbkGroups.getInstance().checkConsistency();
    }

    public void actionAddOrderAccessItem() {
        new OrdersAccessibilityEditorWindow(ADD);
    }

    public void actionEditOrderAccessItem() {
        int index = tvOrdersAccessibility.getSelectionModel().getSelectedIndex();

        if (index >= 0) {
            new OrdersAccessibilityEditorWindow(EDIT);
        }
    }

    public void actionDeleteOrderAccessItem() {
        int index = tvOrdersAccessibility.getSelectionModel().getSelectedIndex();

        if (index >= 0) {
            if (Dialogs.confirm("Удаление элемента", "Действительно желаете удалить элемент"))
                OrdersAccessibility.getInstance().removeItem(tvOrdersAccessibility.getItems().get(index));
        }
    }

    public void actionAddProfile() {
        tvProfiles.getItems().add(new Profile());
    }

    public void actionEditProfile() {
    }

    public void actionSaveProfile() {
        int index = tvProfiles.getSelectionModel().getSelectedIndex();

        if (index >= 0) {
            Profile editedProfile = tvProfiles.getItems().get(index);

            if (editedProfile.isNewItem()) {
                if (Profiles.getInstance().hasDuplicateName(editedProfile))
                    Dialogs.showMessage("Повтор имени", "Профиль с таким именем уже существует");
                else Profiles.getInstance().addItem(editedProfile);
            } else {
                new ProfilesDB().updateData(editedProfile);
            }
        }
    }

    public void actionDeleteProfile() {
        int index = tvProfiles.getSelectionModel().getSelectedIndex();

        if (index >= 0) {
            Profile editedProfile = tvProfiles.getItems().get(index);

            if (editedProfile.isNewItem()) tvProfiles.getItems().remove(index);
            else Profiles.getInstance().removeItem(editedProfile);
        }
    }

    public void actionAddUser() {
        new UserEditorWindow(ADD);
    }

    public void actionEditUser() {
        if (tvUsers.getSelectionModel().getSelectedIndex() >= 0) new UserEditorWindow(EDIT);
    }

    public void actionDeleteUser() {
        User deletedUser = Users.getInstance().getTable().getSelectedItem();
        if (deletedUser != null) Users.getInstance().removeItem(deletedUser);
    }

    public void actionCertCheckCountries() {
//        new CertificateContentChecker(tvCertificates.getSelectionModel().getSelectedItems());
        new CertificateCountriesReportScript().process(tvCertificates.getSelectionModel().getSelectedItems());
    }

    public void actionCheckCertificates() {
        /*new Thread(() -> {
            ArrayList<CertificateVerificationItem> problemCv = new ArrayList<>();
            HashSet<Certificate> problemCertificates = new HashSet<>();
            HashSet<Product> problemProducts = new HashSet<>();
            HashSet<File> files = new HashSet<>();

            CertificatesChecker certificatesChecker = CoreModule.getCertificates().getCertificatesChecker();
            for (Product product : CoreModule.getProducts().getItems()) {
                certificatesChecker.check(product, true);
                for (CertificateVerificationItem cv : certificatesChecker.getResultTableItems()) {
                    if (cv.getStatus().startsWith(CertificatesChecker.NOT_OK) && cv.getStatus().contains(CertificatesChecker.BAD_COUNTRY)) {
                        problemCv.add(cv);
                        problemCertificates.add(cv.getCertificate());
                        problemProducts.add(cv.getProduct());
                        files.add(new File(CoreModule.getFolders().getCertFolder().getPath() + "\\" +
                                cv.getCertificate().getFileName()));
                    }
                }
            }

            for (File certFile : files) {
                File targetFile = new File(CoreModule.getFolders().getTempFolder().getPath() + "\\" +
                        certFile.getName());
                try {
                    if (certFile.exists() && !targetFile.exists()) Files.copy(certFile.toPath(), targetFile.toPath());
                } catch (IOException ee) {
                    System.out.println("copying error " + ee.getMessage());

                    Platform.runLater(() -> Dialogs.showMessage("Ошибка копирования файла",
                            "Произошла ошибка копирования файла\n" +
                                    certFile.getPath() + "\n\nв папку " + targetFile.getPath()));
                }
            }

            File excelFile = new ExportToExcel(new CertificateCheckingResult(problemCv, problemCertificates,
                    problemProducts, files)).getFile();

            try {
                Desktop.getDesktop().open(excelFile);
            } catch (Exception e) {
                System.out.println("Can't open created file");
            }
        }).start();*/
    }

    public void actionCertPositionsReport() {
        new CertificatePositionsReportScript().process(tvCertificates.getSelectionModel().getSelectedItems());
    }

//    public void actionAddPriceList() {
//        new PriceListEditorWindow(ADD);
//    }

    public void actionAddPriceList() {
        new PriceListEditorWindow(new PriceList());
    }

    public void actionEditPriceList() {
        PriceList selectedItem = tvPriceLists.getSelectionModel().getSelectedItem();
        if (selectedItem != null) new PriceListEditorWindow(new PriceList(selectedItem));
    }


    public void actionRemovePriceList() {
//        PriceList priceList = tvPriceLists.getSelectionModel().getSelectedItem();
//        if (priceList != null) {
//            CoreModule.getPriceLists().deleteItem(priceList);
//            ((MainWindowsController) MainWindow.getFxmlLoader().getController()).initPriceListMenu();
//        }
    }


}
