package ui_windows.main_window.filter_window_se;

import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Selector<T> {
    private ComboBox<T> comboBox;
    private Supplier<Set<T>> valuesSupplier;
    private Supplier<T> valueSupplier;
    private Consumer<T> valueSetter;
    private Consumer<Selector<T>> syncAction;
    private Function<T, String> converter;

    public Selector(ComboBox<T> comboBox, Function<T, String> converter, Supplier<Set<T>> valuesSupplier,
                    Supplier<T> valueSupplier, Consumer<T> valueSetter, Consumer<Selector<T>> syncAction) {
        super();
        comboBox.setConverter(new StringConverter<T>() {
            @Override
            public String toString(T object) {
                return converter.apply(object);
            }

            @Override
            public T fromString(String string) {
                return null;
            }
        });

        this.comboBox = comboBox;
        this.converter = converter;
        this.valueSupplier = valueSupplier;
        this.valuesSupplier = valuesSupplier;
        this.valueSetter = valueSetter;
        this.syncAction = syncAction;
    }

    public void actualize(FilterParameters_SE parameters) {
//        Platform.runLater(() -> {//работа листенера и изменение списка элементов приводят к исключению

        if (comboBox.getSelectionModel().isSelected(0) || parameters.getChangedSelector() != this) {
            comboBox.setOnAction(null);

            comboBox.getItems().clear();
            comboBox.getItems().addAll(valuesSupplier.get());
            comboBox.getItems().sort((o1, o2) -> converter.apply(o1).compareToIgnoreCase(converter.apply(o2)));


            if (!comboBox.getItems().contains(valueSupplier.get())) {
                comboBox.getItems().add(valueSupplier.get());
            }

            comboBox.getSelectionModel().select(valueSupplier.get());

            comboBox.setOnAction(event -> {
                valueSetter.accept(comboBox.getValue());
                sync();
            });

            comboBox.setVisibleRowCount(Math.min(comboBox.getItems().size(), 10));
//        });
        }
    }

    public void sync() {
        syncAction.accept(this);
    }
}
