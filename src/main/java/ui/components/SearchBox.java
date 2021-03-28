package ui.components;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import ui_windows.main_window.filter_window_se.FilterParameters_SE;

import static core.SharedData.SHD_FILTER_PARAMETERS;

public class SearchBox extends Region {
    private TextField textBox;
    private Button clearButton;

    public SearchBox() {
        setId("SearchBox");
        getStyleClass().add("search-box");
        setMinHeight(25);
        setPrefSize(450, 25);
        setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);

        clearButton = new Button();
        clearButton.setVisible(false);
        clearButton.setOnAction((ActionEvent actionEvent) -> {
            textBox.setText("");
            textBox.requestFocus();
        });
        textBox = new TextField();
        textBox.setPromptText("Начните ввод для поиска");

        textBox.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                clearButton.setVisible(textBox.getText().length() != 0);

                FilterParameters_SE parameters = SHD_FILTER_PARAMETERS.getData();
                parameters.setSearchText(newValue);
                SHD_FILTER_PARAMETERS.setData(this.getClass(), parameters);
            }
        });
        getChildren().addAll(textBox, clearButton);

        setLayoutX(158);
        setLayoutY(601);
    }

    @Override
    protected void layoutChildren() {
        textBox.resize(getWidth(), getHeight());
        clearButton.resizeRelocate(getWidth() - 18, 6, 12, 13);
    }

    public TextField getTextBox() {
        return textBox;
    }

    public String getText() {
        return textBox.getText();
    }

    public void setText(String text) {
        textBox.setText(text);
    }
}
