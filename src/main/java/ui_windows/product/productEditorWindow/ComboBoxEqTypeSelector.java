package ui_windows.product.productEditorWindow;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.ComboBox;
import ui_windows.product.ProductTypes;
import utils.AutoCompleteComboBoxListener;

public class ComboBoxEqTypeSelector {
    private ComboBox<String> cbEqTypeSelector;
    private CertificateVerificationTable certificateVerificationTable;
    private ChangeListener<String> eqTypeChangeListener;

    public ComboBoxEqTypeSelector(ComboBox<String> cbEqTypeSelector, CertificateVerificationTable certificateVerificationTable) {
        this.cbEqTypeSelector = cbEqTypeSelector;
        this.certificateVerificationTable = certificateVerificationTable;
        new AutoCompleteComboBoxListener<>(cbEqTypeSelector, false);

        eqTypeChangeListener = (observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                certificateVerificationTable.display(certificateVerificationTable.getCheckParameters()
                        .setTemporaryTypeId(ProductTypes.getInstance().getIDbyType(newValue))
                        .setUseTemporaryTypeId(true));
            }
        };
        cbEqTypeSelector.valueProperty().addListener(eqTypeChangeListener);
        cbEqTypeSelector.setEditable(false);

        init();
    }

    public void init() {
        cbEqTypeSelector.getItems().add(ProductTypes.NO_SELECTED);
        cbEqTypeSelector.getItems().addAll(certificateVerificationTable.getProductTypes());
    }

    public void refresh(String prevType) {
        cbEqTypeSelector.valueProperty().removeListener(eqTypeChangeListener);
        if (cbEqTypeSelector.getItems().indexOf(prevType) < 0) {
            cbEqTypeSelector.getItems().add(prevType);
        }
        cbEqTypeSelector.setValue(prevType);
        cbEqTypeSelector.valueProperty().addListener(eqTypeChangeListener);
    }
}
