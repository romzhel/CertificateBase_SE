package ui_windows.options_window.certificates_editor.certificate_content_editor;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Callback;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class CertificatesContentTable {
    private static final String NAMES_COL = "Наименования";
    private static final String EQ_TYPE_COL = "Тип оборудования";
    private static final String TN_VED_COL = "ТН ВЭД";
    private static CertificatesContentTable instance;
    private TableView<CertificateContent> tableView;
    private TableColumn<CertificateContent, String> namesEnumCol, typeCol, tnvedCol;
    private boolean isEditActive = false;
    private int editedItemIndex = -1;
    private List<CertificateContent> editedContent;

    private CertificatesContentTable() {
    }

    public static CertificatesContentTable getInstance() {
        if (instance == null) {
            instance = new CertificatesContentTable();
        }
        return instance;
    }

    public void init(TableView<CertificateContent> tableView) {
        initTableView(tableView);

        typeCol = initColumn(EQ_TYPE_COL, 385);
        typeCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getProductType().getType()));

        tnvedCol = initColumn(TN_VED_COL, 85);
        tnvedCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getProductType().getTen()));

        namesEnumCol = initColumn(NAMES_COL, 520);
        namesEnumCol.setCellValueFactory(new PropertyValueFactory<>("equipmentName"));
    }

    /**
     * Инициализация кликов мыши на таблице:<br>
     * - одиночный клик вне редактируемого контента - сбрасывает режим редактирования<br>
     * - двойной клик на контенте - активирует режим редактирования данного контента<br>
     *
     * @param tableView таблица с контентом
     */
    public void initTableView(TableView<CertificateContent> tableView) {
        this.tableView = tableView;
        tableView.setPlaceholder(new Label("Нет данных для отображения"));
        tableView.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.getClickCount() == 1) {
                    if (editedItemIndex != tableView.getSelectionModel().getFocusedIndex()) {
                        editMode(typeCol, -1);
                        editMode(tnvedCol, -1);
                        editMode(namesEnumCol, -1);
                        log.trace("edit mode is reset");
                    }
                } else if (event.getClickCount() == 2) {
                    int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
                    setEditMode(selectedIndex);
                    log.trace("edit mode is set for {}", selectedIndex);
                }
            }
        });
    }

    /**
     * Создание столбцов с добавлением их в таблицу контента
     *
     * @param title название столбца
     * @param width ширина столбца
     * @return TableColumn&lt;CertificateContent, String&gt;
     */
    public TableColumn<CertificateContent, String> initColumn(String title, double width) {
        TableColumn<CertificateContent, String> column = new TableColumn<>(title);
        TableColumn fColumn = column;
        column.widthProperty().addListener((observable, oldValue, newValue) -> {//todo непонятка
            if (isEditActive) {
                setEditMode(tableView.getSelectionModel().getSelectedIndex());
                log.trace("width property listener, set edit mode");
            } else {
                editMode(fColumn, -1);
                log.trace("width property listener, reset edit mode");
            }
        });
        column.setPrefWidth(width);
        editMode(column, -1);
        tableView.getColumns().add(column);

        return column;
    }

    /**
     * @param tc
     * @param selectedIndex
     */
    public void editMode(TableColumn<CertificateContent, String> tc, int selectedIndex) {
        tc.setCellFactory(new Callback<TableColumn<CertificateContent, String>, TableCell<CertificateContent, String>>() {
            @Override
            public TableCell<CertificateContent, String> call(TableColumn<CertificateContent, String> param) {
                return new TableCell<CertificateContent, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setText(null);
                            setStyle("");
                        } else {
                            if (getIndex() == selectedIndex) {
                                TextArea text = new TextArea(item);
                                text.setWrapText(true);
                                text.setPromptText("Введите значение");
                                setPrefHeight(120);
                                setGraphic(text);

                                text.textProperty().addListener((observable, oldValue, newValue) -> {
                                    if (!newValue.trim().isEmpty()) {
                                        CertificateContent cc = tableView.getItems().get(selectedIndex);
                                        if (tc.getText().equals(EQ_TYPE_COL)) {
                                            cc.getProductType().setType(newValue.replaceAll("\\n", ""));
                                            cc.getProductType().setWasChanged(true);
                                        } else if (tc.getText().equals(TN_VED_COL)) {
                                            cc.getProductType().setTen(newValue.replaceAll("\\n", ""));
                                            cc.getProductType().setWasChanged(true);
                                        } else if (tc.getText().equals(NAMES_COL)) {
                                            cc.setEquipmentName(newValue.replaceAll("\\n", ""));
                                        }
                                        cc.setWasChanged(true);
                                    }
                                });

                                text.setOnKeyReleased(event -> {
                                    if (event.getCode() == KeyCode.ENTER) {
//                                        editMode(tc, -1);
                                        resetEditMode();
                                    }
                                });

                            } else {
                                Text text;
                                if (item.trim().isEmpty()) {
                                    text = new Text("Введите значение");
                                    text.setFill(Color.RED);
                                } else {
                                    text = new Text(item);
                                }
                                text.setWrappingWidth(param.getWidth() - 30);
                                setPrefHeight(text.getLayoutBounds().getHeight() + 10);
                                setGraphic(text);
                            }
                        }
                    }
                };
            }
        });
    }

    /**
     * Перевод в режим редактирования контента
     *
     * @param selectedIndex индекс контента
     */
    public void setEditMode(int selectedIndex) {
        editMode(typeCol, selectedIndex);
        editMode(tnvedCol, selectedIndex);
        editMode(namesEnumCol, selectedIndex);
        editedItemIndex = selectedIndex;
    }

    public void resetEditMode() {
        setEditMode(-1);
    }

    public TableView<CertificateContent> getTableView() {
        return tableView;
    }

    public List<CertificateContent> getContent() {
        return new ArrayList<>(tableView.getItems());
    }

    public void setContent(List<CertificateContent> contentList) {
        if (contentList != null) {
            tableView.getItems().clear();
            tableView.getItems().addAll(contentList);
        }
    }

    public void setEditModeActive(boolean editMode) {
        isEditActive = editMode;
    }
}