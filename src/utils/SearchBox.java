package utils;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;

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
        final ChangeListener<String> textListener =
                (ObservableValue<? extends String> observable,
                 String oldValue, String newValue) -> {
                    clearButton.setVisible(textBox.getText().length() != 0);
                };
        textBox.textProperty().addListener(textListener);
        getChildren().addAll(textBox, clearButton);
    }

    @Override
    protected void layoutChildren() {
        textBox.resize(getWidth(), getHeight());
        clearButton.resizeRelocate(getWidth() - 18, 6, 12, 13);
    }

    public TextField getTextBox() {
        return textBox;
    }

    public String getText(){
        return textBox.getText();
    }

    public void setText(String text) {
        textBox.setText(text);
    }
}
