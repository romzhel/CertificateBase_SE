package ui.components;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Control;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import lombok.extern.log4j.Log4j2;
import ui_windows.product.Product;
import ui_windows.product.data.DataItem;
import utils.comparation.te.PropertyProtectEnum;

import java.util.Map;

import static utils.comparation.te.PropertyProtectEnum.*;

@Log4j2
public class ProtectedBox extends Region {
    private final TextInputControl textBox;
    private final ToggleButton protectButton;
    private final BooleanProperty buttonChangeProperty;
    private Product item;
    private final DataItem dataItem;

    public ProtectedBox(TextInputControl target, DataItem dataItem) {
        super();

        this.textBox = target;
        this.dataItem = dataItem;

        Pane par = (Pane) target.getParent();
        par.getChildren().add(this);
        setId("protectedBox");

        setMinHeight(target.getHeight());
        setPrefSize(25, target.getPrefHeight());
        setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
        setLayoutX(target.getLayoutX() + target.getPrefWidth() - 17);
        setLayoutY(target.getLayoutY());

        try {
            par.getStylesheets().add(this.getClass().getResource("/css/ui_components.css").toExternalForm());
            getStyleClass().add("protected-box");
        } catch (Exception e) {
            log.error("Ошибка стилизации {}", e.getMessage());
        }

        buttonChangeProperty = new SimpleBooleanProperty(false);

        protectButton = new ToggleButton();
        protectButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            buttonChangeProperty.set(newValue);
        });

        getChildren().add(protectButton);
    }

    @Override
    protected void layoutChildren() {
        protectButton.resizeRelocate(0, 5, 12, 15);
    }

    /*public void setItem(Product item) {
        this.item = item;
        textBox.setText(dataItem.getValue(item).toString());
        showProtectStatus(item.getProtectedData().contains(dataItem));
    }*/

    public PropertyProtectEnum getProtectStatus() {
        return protectButton.isSelected() ? PROTECTED : NON_PROTECTED;
    }

    public void showProtectStatus(Map<DataItem, PropertyProtectEnum> map) {
        showProtectStatus(map.getOrDefault(dataItem, NON_PROTECTED));
    }

    public void showProtectStatus(PropertyProtectEnum en) {
        if (en == COMBINED) {
            protectButton.getStyleClass().removeAll();
            protectButton.getStyleClass().add("combined");
            protectButton.setDisable(true);
        } else {
            protectButton.setSelected(en == PROTECTED);
            getStyleClass().add("protected-box");
        }
    }

    public void disableButton(boolean dis) {
        protectButton.setDisable(dis);
    }

    public void hideButton(boolean hide) {
        protectButton.setVisible(hide);
    }

    public BooleanProperty getButtonChangeProperty() {
        return buttonChangeProperty;
    }

    public DataItem getDataItem() {
        return dataItem;
    }
}
