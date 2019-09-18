package ui_windows.options_window.certificates_editor.certificate_content_editor;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
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

    public CertificatesContentTable(TableView<CertificateContent> tableView) {
        this.tableView = tableView;

        tableView.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.getClickCount() == 1) {
                    if (editedItemIndex != tableView.getSelectionModel().getFocusedIndex()) {
                        displayMode(typeCol);
                        displayMode(tnvedCol);
                        displayMode(namesEnumCol);
                    }
                } else if (event.getClickCount() == 2) {
                    setEditMode(tableView.getSelectionModel().getSelectedIndex());
                }
            }
        });

        typeCol = new TableColumn<>(EQ_TYPE_COL);
        typeCol.setCellValueFactory(new PropertyValueFactory<>("equipmentType"));
        typeCol.setPrefWidth(385);
        typeCol.widthProperty().addListener((observable, oldValue, newValue) -> {
            if (isEditActive) {
                setEditMode(tableView.getSelectionModel().getSelectedIndex());
            } else {
                displayMode(typeCol);
            }
        });
        displayMode(typeCol);

        tnvedCol = new TableColumn<>(TN_VED_COL);
        tnvedCol.setCellValueFactory(new PropertyValueFactory<>("tnved"));
        tnvedCol.setPrefWidth(85);
        tnvedCol.widthProperty().addListener((observable, oldValue, newValue) -> {
            if (isEditActive) {
                setEditMode(tableView.getSelectionModel().getSelectedIndex());
            } else {
                displayMode(tnvedCol);
            }
        });
        displayMode(tnvedCol);

        namesEnumCol = new TableColumn<>(NAMES_COL);
        namesEnumCol.setCellValueFactory(new PropertyValueFactory<>("equipmentName"));
        namesEnumCol.setPrefWidth(520);
        namesEnumCol.widthProperty().addListener((observable, oldValue, newValue) -> {
            if (isEditActive) {
                setEditMode(tableView.getSelectionModel().getSelectedIndex());
            } else {
                displayMode(namesEnumCol);
            }
        });
        displayMode(namesEnumCol);

        tableView.getColumns().addAll(typeCol, tnvedCol, namesEnumCol);
    }

    private void displayMode(TableColumn<CertificateContent, String> tc) {
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
                            Text text = new Text(item);
                            text.setWrappingWidth(param.getWidth() - 30);
                            setPrefHeight(text.getLayoutBounds().getHeight() + 10);
                            setGraphic(text);
                        }
                    }
                };
            }
        });
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
                                setPrefHeight(80);
                                setGraphic(text);

                                text.textProperty().addListener((observable, oldValue, newValue) -> {
                                    CertificateContent cc = tableView.getItems().get(selectedIndex);
                                    if (tc.getText().equals(EQ_TYPE_COL)) {
                                        cc.setEquipmentType(newValue);
                                    } else if (tc.getText().equals(TN_VED_COL)) {
                                        cc.setTnved(newValue);
                                    } else if (tc.getText().equals(NAMES_COL)) {
                                        cc.setEquipmentName(newValue);
                                    }
                                    cc.setWasChanged(true);
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
}