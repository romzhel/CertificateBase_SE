package ui_windows.options_window;

import core.CoreModule;
import core.Dialogs;
import database.ProfilesDB;
import files.ExportToExcel;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import ui_windows.main_window.Product;
import ui_windows.options_window.certificates_editor.*;
import ui_windows.options_window.product_lgbk.*;
import ui_windows.options_window.profile_editor.Profile;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseButton;
import ui_windows.options_window.families_editor.FamiliesEditorWindow;
import ui_windows.options_window.families_editor.ProductFamiliesTable;
import ui_windows.options_window.families_editor.ProductFamily;
import ui_windows.options_window.order_accessibility_editor.OrderAccessibility;
import ui_windows.options_window.order_accessibility_editor.OrdersAccessibilityEditorWindow;
import ui_windows.options_window.order_accessibility_editor.OrdersAccessibilityTable;
import ui_windows.options_window.profile_editor.ProfilesTable;
import ui_windows.options_window.user_editor.User;
import ui_windows.options_window.user_editor.UserEditorWindow;
import ui_windows.options_window.user_editor.UsersTable;
import ui_windows.options_window.requirements_types_editor.*;
import utils.Utils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.ResourceBundle;

import static ui_windows.Mode.*;
import static ui_windows.options_window.profile_editor.SimpleRight.*;

public class OptionsWindowController implements Initializable {

    @FXML
    TableView<RequirementType> tvCertificateTypes;

    @FXML
    TableView<Certificate> tvCertificates;

    @FXML
    TableView<ProductFamily> tvFamilies;

    @FXML
    TableView<ProductLgbk> tvLgbk;
    @FXML
    TreeTableView<ProductLgbk> ttvLgbk;

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

        tvLgbk.getColumns().get(0).setSortType(TableColumn.SortType.DESCENDING);
        tvLgbk.getColumns().get(1).setSortType(TableColumn.SortType.ASCENDING);
        tvLgbk.getSortOrder().setAll(tvLgbk.getColumns().get(0), tvLgbk.getColumns().get(1));

        //---------
        TreeTableColumn<ProductLgbk, String> lgbkCol = new TreeTableColumn<>("lgbk");
        lgbkCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("lgbk"));
        lgbkCol.setPrefWidth(100);

        TreeTableColumn<ProductLgbk, String> hierarchy = new TreeTableColumn<>("hierarchy");
        hierarchy.setCellValueFactory(new TreeItemPropertyValueFactory<>("hierarchy"));
        hierarchy.setPrefWidth(75);

        TreeTableColumn<ProductLgbk, String> description = new TreeTableColumn<>("description");
        description.setPrefWidth(300);

        ttvLgbk.getColumns().addAll(lgbkCol, hierarchy, description);

        ProductLgbkGroups plgs = new ProductLgbkGroups();
        plgs.create(CoreModule.getProducts());
        ttvLgbk.setRoot(CoreModule.getProductLgbks().getFromLgbkGroups(plgs));


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
        int index = tvLgbk.getSelectionModel().getSelectedIndex();

        if (index >= 0) {
            if (Dialogs.confirm("Удаление элемента", "Действительно желаете удалить элемент"))
                CoreModule.getProductLgbks().removeItem(tvLgbk.getItems().get(index));
        }

    }

    public void actionReCheckLgbkFromProducts() {
        ArrayList<ProductLgbk> lostLgbk = CoreModule.getProductLgbks().getLostLgbkFromProducts(CoreModule.getProducts());

        if (lostLgbk.size() == 0) Dialogs.showMessage("Проверка lgbk", "Все lgbk учтены");
        else {
            String result = "";
            for (ProductLgbk pl : lostLgbk) {
                result = result.concat(pl.getLgbk().concat(" (").concat(pl.getHierarchy()).concat(")").concat("\n"));
            }

            Dialogs.showMessage("Проверка lgbk", "Есть неучтенные lgbk:\n\n" + result);
        }
    }

    public void actionApplyChangedLgbkToProducts() {
//        new Thread(() -> {
        CoreModule.getProducts().applyNotUsedFromLgbk();
//            Platform.runLater(() -> Dialogs.showMessage("ok", "ok"));
        System.out.println("ok");
//        }).start();

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
            ArrayList<CertificateVerification> problemCv = new ArrayList<>();
            HashSet<Certificate> problemCertificates = new HashSet<>();
            HashSet<Product> problemProducts = new HashSet<>();
            HashSet<File> files = new HashSet<>();

            for (Product product : CoreModule.getProducts().getItems()) {
                for (CertificateVerification cv : CoreModule.getCertificates().checkCertificates(product)) {
                    if (cv.getStatus().startsWith("НЕ ОК, нет страны")) {
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


}
