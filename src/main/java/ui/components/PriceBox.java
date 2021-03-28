package ui.components;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PriceBox extends Region {
    private final TextField textBox;
    private final ToggleButton hideButton;
    private BooleanProperty buttonChangeProperty;

    private static final Logger logger = LogManager.getLogger(PriceBox.class);

    public PriceBox(TextField target) {
        super();

        this.textBox = target;

        Pane par = (Pane) target.getParent();
        par.getChildren().add(this);
        setId("priceBox");

        setMinHeight(target.getHeight());
        setPrefSize(25, target.getPrefHeight());
        setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
        setLayoutX(target.getLayoutX() + target.getPrefWidth() - 25);
        setLayoutY(target.getLayoutY());

        try {
            par.getStylesheets().add(this.getClass().getResource("/css/ui_components.css").toExternalForm());
            getStyleClass().add("price-box");
        } catch (Exception e) {
            logger.error("Ошибка стилизации {}", e.getMessage());
        }

        hideButton = new ToggleButton();
        hideButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            buttonChangeProperty.set(newValue);
        });

        getChildren().add(hideButton);

        buttonChangeProperty = new SimpleBooleanProperty();
    }

    @Override
    protected void layoutChildren() {
        hideButton.resizeRelocate(0, 7, 18, 10);
    }

    public void setValue(Object value) {
        textBox.setText(value.toString());
    }

    public boolean getButtonStatus() {
        return hideButton.isSelected();
    }

    public void setButtonStatus(Boolean status) {
        if (status == null) {
            hideButton.getStyleClass().removeAll();
            hideButton.getStyleClass().add("combined");
        } else {
            hideButton.setSelected(status);
            getStyleClass().add("price-box");
        }
    }

    public void setButtonDisabled(boolean dis) {
        hideButton.setDisable(dis);
    }

    public BooleanProperty getButtonChangeProperty() {
        return buttonChangeProperty;
    }
}
