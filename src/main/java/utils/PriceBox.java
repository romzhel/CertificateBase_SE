package utils;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

public class PriceBox extends Region {
    private TextField textBox;
    private ToggleButton hideButton;
    private BooleanProperty buttonChangeProperty;

    public PriceBox(TextField target) {
        Pane par = (Pane) target.getParent();
        par.getChildren().add(this);
        setId("priceBox");

        setMinHeight(25);
        setPrefSize(target.getPrefWidth(), target.getPrefHeight());
        setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
        setLayoutX(target.getLayoutX());
        setLayoutY(target.getLayoutY());

        try {
            par.getStylesheets().add(this.getClass().getResource("/css/priceBox.css").toExternalForm());
            getStyleClass().add("price-box");
        } catch (Exception e) {
            e.printStackTrace();
        }

        hideButton = new ToggleButton();
        hideButton.setVisible(true);
        hideButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            buttonChangeProperty.set(newValue);
        });
        textBox = new TextField();
        textBox.setMinHeight(25);
        getChildren().addAll(textBox, hideButton);

        buttonChangeProperty = new SimpleBooleanProperty();
    }

    @Override
    protected void layoutChildren() {
        textBox.resize(getWidth(), getHeight());
        hideButton.resizeRelocate(getWidth() - 21, 7, 18, 10);
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
