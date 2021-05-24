package ui.components;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Control;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ui_windows.product.Product;
import ui_windows.product.data.DataItem;

public class ProtectedBox extends Region {
    private final TextInputControl textBox;
    private final ToggleButton hideButton;
    private final BooleanProperty buttonChangeProperty;
    private Product item;
    private final DataItem dataItem;

    private static final Logger logger = LogManager.getLogger(ProtectedBox.class);

    public ProtectedBox(TextInputControl target, DataItem dataItem) {
        super();

        this.textBox = target;
        this.dataItem = dataItem;

        Pane par = (Pane) target.getParent();
        par.getChildren().add(this);
        setId("priceBox");

        setMinHeight(target.getHeight());
        setPrefSize(25, target.getPrefHeight());
        setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
        setLayoutX(target.getLayoutX() + target.getPrefWidth() - 17);
        setLayoutY(target.getLayoutY());

        try {
            par.getStylesheets().add(this.getClass().getResource("/css/ui_components.css").toExternalForm());
            getStyleClass().add("protected-box");
        } catch (Exception e) {
            logger.error("Ошибка стилизации {}", e.getMessage());
        }

        buttonChangeProperty = new SimpleBooleanProperty(false);

        hideButton = new ToggleButton();
        hideButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            buttonChangeProperty.set(newValue);
        });

        getChildren().add(hideButton);
    }

    @Override
    protected void layoutChildren() {
        hideButton.resizeRelocate(0, 5, 12, 15);
    }

    public void setItem(Product item) {
        this.item = item;
        textBox.setText(dataItem.getValue(item).toString());
        showProtectStatus(item.getProtectedData().contains(dataItem));//todo
    }

    public boolean getProtectStatus() {
        return hideButton.isSelected();
    }

    public void showProtectStatus(Boolean status) {//todo
        if (status == null) {
            hideButton.getStyleClass().removeAll();
            hideButton.getStyleClass().add("combined");
        } else {
            hideButton.setSelected(status);
            getStyleClass().add("protected-box");
        }
    }

    public void disableButton(boolean dis) {
        hideButton.setDisable(dis);
    }

    public void hideButton(boolean hide) {
        hideButton.setVisible(hide);
    }

    public BooleanProperty getButtonChangeProperty() {
        return buttonChangeProperty;
    }
}
