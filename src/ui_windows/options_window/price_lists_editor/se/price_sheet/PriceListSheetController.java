package ui_windows.options_window.price_lists_editor.se.price_sheet;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import ui_windows.options_window.price_lists_editor.se.PriceListContentTableItem;

import java.net.URL;
import java.util.ResourceBundle;

public class PriceListSheetController implements Initializable {

    @FXML
    public RadioButton rbLangRu;
    @FXML
    public RadioButton rbLangEn;
    @FXML
    public Pane pPriceColumns;
    @FXML
    public TreeTableView<PriceListContentTableItem> ttvPriceContent;
    @FXML
    public Pane pPriceDchain;
    @FXML
    public TextField tfSheetName;
    @FXML
    public TextField tfInitialRow;
    @FXML
    public TextField tfDiscount;
    @FXML
    RadioMenuItem rmiByFamily;
    @FXML
    RadioMenuItem rmiByLgbk;
    @FXML
    public RadioButton rbOrderMaterial;
    @FXML
    public RadioButton rbOrderArticle;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ToggleGroup languageSelector = new ToggleGroup();
        rbLangEn.setToggleGroup(languageSelector);
        rbLangRu.setToggleGroup(languageSelector);

        rbLangRu.setSelected(true);

        ToggleGroup contentModeSelector = new ToggleGroup();
        rmiByFamily.setToggleGroup(contentModeSelector);
        rmiByLgbk.setToggleGroup(contentModeSelector);

        ToggleGroup sortOrderSelector = new ToggleGroup();
        rbOrderMaterial.setToggleGroup(sortOrderSelector);
        rbOrderArticle.setToggleGroup(sortOrderSelector);
    }
}
