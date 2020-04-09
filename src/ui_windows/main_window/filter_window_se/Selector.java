package ui_windows.main_window.filter_window_se;

import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;

import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Selector<T> {
    private ComboBox<T> comboBox;
    private int changeLevel;
    private Supplier<TreeSet<T>> valuesSupplier;
    private Supplier<T> valueSupplier;
    private Consumer<T> valueSetter;
    private Consumer<Selector<T>> syncAction;

    public Selector(ComboBox<T> comboBox, Function<T, String> converter, int changeLevel, Supplier<TreeSet<T>> valuesSupplier,
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
        this.changeLevel = changeLevel;
        this.valueSupplier = valueSupplier;
        this.valuesSupplier = valuesSupplier;
        this.valueSetter = valueSetter;
        this.syncAction = syncAction;
    }

    public void actualize(FilterParameters_SE parameters) {
        comboBox.setOnAction(null);

        if (comboBox.getItems().size() < 3 || parameters.getLastChange() < changeLevel ||
                parameters.getLastChange() == changeLevel && valueSupplier.get().equals(FilterParameters_SE.TEXT_ALL_ITEMS)) {

            comboBox.getItems().clear();
            comboBox.getItems().addAll(valuesSupplier.get());
        }

        if (!comboBox.getItems().contains(valueSupplier.get())) {
            comboBox.getItems().add(valueSupplier.get());
        }
        comboBox.getSelectionModel().select(valueSupplier.get());

        comboBox.setOnAction(event -> {
            valueSetter.accept(comboBox.getValue());
            sync();
        });

        comboBox.setVisibleRowCount(Math.min(comboBox.getItems().size(), 10));
    }

    public void sync() {
        syncAction.accept(this);
    }
}
