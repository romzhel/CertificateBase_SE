package ui_windows.options_window.certificates_editor.certificate_content_editor;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.util.ArrayList;

public class CertificatesContentTable {
    private TableView<CertificateContent> tableView;
    private TableColumn<CertificateContent, String> typeCol, tnvedCol, namesEnumCol;
    private static final String EQ_TYPE_COL = "Тип оборудования";
    private static final String TN_VED_COL = "ТН ВЭД";
    private static final String NAMES_COL = "Наименования";
    private boolean isEditActive = false;
    private int editedItemIndex = -1;
    private ArrayList<CertificateContent> backupItems;

    public CertificatesContentTable(TableView<CertificateContent> tableView) {
        initTableView(tableView);

        typeCol = initColumn(EQ_TYPE_COL, "equipmentType", 385);
        tnvedCol = initColumn(TN_VED_COL, "tnved", 85);
        namesEnumCol = initColumn(NAMES_COL, "equipmentName", 520);
    }

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
                    }
                } else if (event.getClickCount() == 2) {
                    setEditMode(tableView.getSelectionModel().getSelectedIndex());
                }
            }
        });
    }

    public TableColumn<CertificateContent, String> initColumn(String title, String fieldName, double width){
        TableColumn<CertificateContent, String> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(fieldName));
        TableColumn fColumn = column;
        column.widthProperty().addListener((observable, oldValue, newValue) -> {
            if (isEditActive) {
                setEditMode(tableView.getSelectionModel().getSelectedIndex());
            } else {
                editMode(fColumn, -1);
            }
        });
        column.setPrefWidth(width);
        editMode(column, -1);
        tableView.getColumns().add(column);

        return column;
    }

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
                                setPrefHeight(120);
                                setGraphic(text);

                                text.textProperty().addListener((observable, oldValue, newValue) -> {
                                    CertificateContent cc = tableView.getItems().get(selectedIndex);
                                    if (tc.getText().equals(EQ_TYPE_COL)) {
                                        cc.setEquipmentType(newValue.replaceAll("\\n", ""));
                                    } else if (tc.getText().equals(TN_VED_COL)) {
                                        cc.setTnved(newValue.replaceAll("\\n", ""));
                                    } else if (tc.getText().equals(NAMES_COL)) {
                                        cc.setEquipmentName(newValue.replaceAll("\\n", ""));
                                    }
                                    String t = text.getText().replaceAll("\\n", "");
                                    cc.setWasChanged(true);
                                });

                                text.setOnKeyReleased(event -> {
                                    if (event.getCode() == KeyCode.ENTER) {
                                        String t = text.getText();

                                        editMode(tc, -1);
                                    }
                                });

                            } else {
                                Text text = new Text(item);
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

    public void setEditMode(int selectedIndex) {
        editMode(typeCol, selectedIndex);
        editMode(tnvedCol, selectedIndex);
        editMode(namesEnumCol, selectedIndex);
        editedItemIndex = selectedIndex;
    }

    public TableView<CertificateContent> getTableView() {
        return tableView;
    }

    public ArrayList<CertificateContent> getContent() {
        ArrayList<CertificateContent> certContent = new ArrayList<>();

        for (CertificateContent cc : tableView.getItems()) {
            certContent.add(cc);
        }

        return certContent;
    }

    public void setContent(ArrayList<CertificateContent> content) {
        tableView.getItems().clear();
        if (content != null) tableView.getItems().addAll(content);
    }

    public void setEditModeActive(boolean editMode) {
        isEditActive = editMode;
    }

    public ArrayList<CertificateContent> getBackupItems() {
        return backupItems;
    }

    public void setBackupItems(ArrayList<CertificateContent> backupItems) {
        this.backupItems = new ArrayList<>();
        for (CertificateContent cc:backupItems             ) {
            this.backupItems.add(new CertificateContent(cc.getId(), cc.getCertId(), cc.getEquipmentType(),
                    cc.getTnved(), cc.getEquipmentName()));
        }
    }

    public void restoreBackupedItems(){
        for (CertificateContent cc : tableView.getItems()) {
            for (CertificateContent bcc : backupItems) {
                if (cc.getId() == bcc.getId()) {
                    cc.setEquipmentType(bcc.getEquipmentType());
                    cc.setTnved(bcc.getTnved());
                    cc.setEquipmentName(bcc.getEquipmentName());
                    break;
                }
            }
        }
    }
}