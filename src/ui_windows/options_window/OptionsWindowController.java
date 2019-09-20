package ui_windows.options_window;

import core.CoreModule;
import core.Dialogs;
import database.ProfilesDB;
import files.ExportToExcel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import ui_windows.main_window.MainWindow;
import ui_windows.main_window.MainWindowsController;
import ui_windows.options_window.certificates_editor.*;
import ui_windows.options_window.certificates_editor.certificatesChecker.CertificateVerificationItem;
import ui_windows.options_window.certificates_editor.certificatesChecker.CertificatesChecker;
import ui_windows.options_window.families_editor.FamiliesEditorWindow;
import ui_windows.options_window.families_editor.ProductFamiliesTable;
import ui_windows.options_window.families_editor.ProductFamily;
import ui_windows.options_window.order_accessibility_editor.OrderAccessibility;
import ui_windows.options_window.order_accessibility_editor.OrdersAccessibilityEditorWindow;
import ui_windows.options_window.order_accessibility_editor.OrdersAccessibilityTable;
import ui_windows.options_window.price_lists_editor.PriceList;
import ui_windows.options_window.price_lists_editor.PriceListEditorWindow;
import ui_windows.options_window.price_lists_editor.PriceListsTable;
import ui_windows.options_window.product_lgbk.LgbkEditorWindow;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.options_window.product_lgbk.ProductLgbksTable;
import ui_windows.options_window.profile_editor.Profile;
import ui_windows.options_window.profile_editor.ProfilesTable;
import ui_windows.options_window.requirements_types_editor.RequirementType;
import ui_windows.options_window.requirements_types_editor.RequirementTypeEditorWindow;
import ui_windows.options_window.requirements_types_editor.RequirementTypeEditorWindowActions;
import ui_windows.options_window.requirements_types_editor.RequirementTypesTable;
import ui_windows.options_window.user_editor.User;
import ui_windows.options_window.user_editor.UserEditorWindow;
import ui_windows.options_window.user_editor.UsersTable;
import ui_windows.product.Product;
import utils.Utils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.ResourceBundle;

import static ui_windows.Mode.ADD;
import static ui_windows.Mode.EDIT;

public class OptionsWindowController implements Initializable {

    @FXML
    TableView<RequirementType> tvCertificateTypes;

    @FXML
    TableView<Certificate> tvCertificates;

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
    ContextMenu cmCertTypes;

    @FXML
    ContextMenu cmCertificates;

    @FXML
    TableView<PriceList> tvPriceLists;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        OptionsWindow.setTpOptions(tpOptions);
        //------------------------------certificates--------------------------------------------------------------------
        CoreModule.setRequirementTypesTable(new RequirementTypesTable(tvCertificateTypes));//fill certificates types table
        CoreModule.setCertificatesTable(new CertificatesTable(tvCertificates));//fill certificates table

        tvCertificateTypes.setOnMouseClicked(event -> {//double click on product
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.getClickCount() == 2) {
                    actionEditCertificateType();//open certificate type editor window
                }
            }
        });

        tvCertificates.setOnMouseClicked(event -> {//double click on product
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.getClickCount() == 2) {
                    actionEditCertificate();//open certificate editor window
                }
            }
        });

        //----------------------------product families------------------------------------------------------------------
        CoreModule.getProductFamilies().setProductFamiliesTable(new ProductFamiliesTable(tvFamilies));  //fill families table
        CoreModule.getProductLgbks().setProductLgbksTable(new ProductLgbksTable(tvLgbk));  //fill lgbk table

        tvFamilies.setOnMouseClicked(event -> {//double click on product
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.getClickCount() == 2) {
                    actionEditFamily();//open family editor window
                }
            }
        });

        tvLgbk.setOnMouseClicked(event -> {//double click on product
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.getClickCount() == 2) {
                    actionEditLgbk();//open lgbk editor window
                }
            }
        });
        tvLgbk.getRoot().setExpanded(true);
//        tvLgbk.setStyle();

        //------------------------------------------order accessibility-------------------------------------------------
        CoreModule.getOrdersAccessibility().setTable(new OrdersAccessibilityTable(tvOrdersAccessibility)); //fill order acc table

        tvOrdersAccessibility.setOnMouseClicked(event -> {//double click on product
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.getClickCount() == 2) {
                    actionEditOrderAccessItem();//open orderable editor window
                }
            }
        });

        //------------------------------------------profiles------------------------------------------------------------
        CoreModule.getProfiles().setTable(new ProfilesTable(tvProfiles));

        //------------------------------------------users---------------------------------------------------------------
        CoreModule.getUsers().setTable(new UsersTable(tvUsers));

        tvUsers.setOnMouseClicked(event -> {//double click on product
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.getClickCount() == 2) {
                    actionEditUser();//open orderable editor window
                }
            }
        });

        //---------------------------------------price lists--------------------------------------------------------------
        CoreModule.getPriceLists().setPriceListsTable(new PriceListsTable(tvPriceLists));

        tvPriceLists.setOnMouseClicked(event -> {//double click on product
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.getClickCount() == 2) {
                    actionEditPriceList();//open orderable editor window
                }
            }
        });
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
        new CertificateEditorWindow(ADD);
    }

    public void actionEditCertificate() {
        new CertificateEditorWindow(EDIT);
    }

    public void actionDeleteCertificate() {
        CertificateEditorWindowActions.deleteData();
    }

    public void actionOpenCertificateFile() {
        File certFile = new File(CoreModule.getFolders().getCertFolder() + "\\" +
                tvCertificates.getSelectionModel().getSelectedItem().getFileName());
        Utils.openFile(certFile);
    }

    public void actionAddFamily() {
        new FamiliesEditorWindow(ADD);
    }

    public void actionEditFamily() {
        new FamiliesEditorWindow(EDIT);
    }

    public void actionDeleteFamily() {
        TableView<ProductFamily> pft = CoreModule.getProductFamilies().getProductFamiliesTable().getTableView();
        int index = pft.getSelectionModel().getSelectedIndex();
        ProductFamily pf = pft.getItems().get(index);

        if (CoreModule.getProductLgbks().isFamilyUsed(pf)) Dialogs.showMessage("Удаление элемента",
                "Элемент не может быть удалён, так как используется");
        else {
            if (pf != null) {
                if (Dialogs.confirm("Удаление записи", "Действительно желаете удалить запись?")) {
                    CoreModule.getProductFamilies().removeItem(pf);//delete from class
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
                CoreModule.getProductLgbks().removeItem(deletedLgbk);
                deletedItem.getParent().getChildren().remove(deletedItem);
            }
        }
    }

    public void actionReCheckLgbkFromProducts() {
        CoreModule.getProductLgbkGroups().checkConsistency();
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
                CoreModule.getOrdersAccessibility().removeItem(tvOrdersAccessibility.getItems().get(index));
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
                if (CoreModule.getProfiles().hasDuplicateName(editedProfile))
                    Dialogs.showMessage("Повтор имени", "Профиль с таким именем уже существует");
                else CoreModule.getProfiles().addItem(editedProfile);
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
            else CoreModule.getProfiles().removeItem(editedProfile);
        }
    }

    public void actionAddUser() {
        new UserEditorWindow(ADD);
    }

    public void actionEditUser() {
        if (tvUsers.getSelectionModel().getSelectedIndex() >= 0) new UserEditorWindow(EDIT);
    }

    public void actionDeleteUser() {
        User deletedUser = CoreModule.getUsers().getTable().getSelectedItem();
        if (deletedUser != null) CoreModule.getUsers().removeItem(deletedUser);
    }

    public void actionCheckCertificates() {
        new Thread(() -> {
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
        }).start();


    }

    public void actionAddPriceList() {
        new PriceListEditorWindow(ADD);
    }

    public void actionEditPriceList() {
        new PriceListEditorWindow(EDIT);
    }

    public void actionRemovePriceList() {
        PriceList priceList = tvPriceLists.getSelectionModel().getSelectedItem();
        if (priceList != null) {
            CoreModule.getPriceLists().deleteItem(priceList);
            ((MainWindowsController) MainWindow.getFxmlLoader().getController()).initPriceListMenu();
        }
    }


}
