package ui_windows.product;

import core.CoreModule;
import core.Dialogs;
import javafx.collections.ObservableList;
import ui_windows.main_window.file_import_window.FileImportParameter;
import ui_windows.main_window.file_import_window.SingleProductsComparator;
import ui_windows.product.data.DataItem;
import ui_windows.product.productEditorWindow.ProductEditorWindowController;
import utils.Utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

import static ui_windows.product.MultiEditorItem.CAN_BE_SAVED;
import static ui_windows.product.MultiEditorItem.CAN_NOT_BE_SAVED;
import static ui_windows.product.data.DataItem.*;

public class MultiEditor {
    private ObservableList<Product> editedItems;
    private ArrayList<MultiEditorItem> fieldsAndControls;
    private Product resultProduct;
    private ProductEditorWindowController pewc;

    public MultiEditor(ObservableList<Product> editedItems, ProductEditorWindowController pewc) {
        this.pewc = pewc;
        this.editedItems = editedItems;
        fieldsAndControls = new ArrayList<>();
        fieldsAndControls.add(new MultiEditorItem(DATA_IS_IN_PRICE, /*pewc.cbxPrice,*/ CAN_BE_SAVED));
        fieldsAndControls.add(new MultiEditorItem(DATA_LGBK, /*pewc.tfLgbk,*/ CAN_NOT_BE_SAVED));
        fieldsAndControls.add(new MultiEditorItem(DATA_HIERARCHY, /*pewc.tfHierarchy,*/ CAN_NOT_BE_SAVED));
        fieldsAndControls.add(new MultiEditorItem(DATA_DCHAIN, /*pewc.tfAccessibility,*/ CAN_NOT_BE_SAVED));//пишется в базу с расшифровкой, НЕ ВКЛЮЧАТЬ
        fieldsAndControls.add(new MultiEditorItem(DATA_SERVICE_END, /*pewc.tfEndOfService,*/ CAN_NOT_BE_SAVED));
        fieldsAndControls.add(new MultiEditorItem(DATA_COUNTRY, /*pewc.tfCountry,*/ CAN_NOT_BE_SAVED));//пишется в базу с расшифровкой, НЕ ВКЛЮЧАТЬ
        fieldsAndControls.add(new MultiEditorItem(DATA_COMMENT, /*pewc.taComments,*/ CAN_BE_SAVED));
        fieldsAndControls.add(new MultiEditorItem(DATA_TYPE, /*pewc.cbType,*/ CAN_BE_SAVED));
        fieldsAndControls.add(new MultiEditorItem(DATA_DESCRIPTION_EN, /*pewc.taDescription,*/ CAN_BE_SAVED));
        fieldsAndControls.add(new MultiEditorItem(DATA_DESCRIPTION_RU, /*pewc.taDescriptionEn,*/ CAN_BE_SAVED));
        fieldsAndControls.add(new MultiEditorItem(DATA_REPLACEMENT, /*pewc.tfReplacement,*/ CAN_BE_SAVED));

        compareAndDisplay();
    }

    public MultiEditor(ObservableList<Product> editedItems, MultiEditorItem... fieldsAndControls) {
        this.editedItems = editedItems;
        this.fieldsAndControls = new ArrayList<>();
        this.fieldsAndControls.addAll(Arrays.asList(fieldsAndControls));

        compareAndDisplay();
    }

    public void compareAndDisplay() {
        resultProduct = new Product();
        for (MultiEditorItem fac : fieldsAndControls) {
            fac.compare(editedItems);
            fac.getDataItem().getField().setAccessible(true);
            try {
                fac.getDataItem().getField().set(resultProduct, fac.getCommonValue());
            } catch (IllegalAccessException e) {
                Dialogs.showMessage("Мультиредактор", "Ошибка сохранения результатов сравнения " + e.getMessage());
            }
//            fac.setUiAccessibility();
        }
        resultProduct.displayInEditorWindow(pewc);
        System.out.println();
    }

    public boolean checkAndSaveChanges() {
        resultProduct = new Product(pewc);
        boolean haveChanges = false;
        FileImportParameter[] parameters = new FileImportParameter[fieldsAndControls.size()];
        int index = 0;
        for (MultiEditorItem mei : fieldsAndControls) {
            parameters[index++] = new FileImportParameter(mei);
        }

        SingleProductsComparator comparator;
        for (Product product : editedItems) {
            comparator = new SingleProductsComparator(product, resultProduct, true, parameters);
            haveChanges |= comparator.getResult().isNeedUpdateInDB();

            if (haveChanges) {
                String oldHistory = product.getHistory();
                String newHistory = Utils.getDateTime().concat(comparator.getResult().getHistoryComment().concat(", ")
                        .concat(CoreModule.getUsers().getCurrentUser().getSurname()));

                product.setHistory(oldHistory != null && !oldHistory.isEmpty() ?
                        oldHistory.concat("|").concat(newHistory) :
                        newHistory);
            }
        }


        /*String changesHistory = "";
        for (MultiEditorItem fac : fieldsAndControls) {
            if (fac.isValueChanged()) {
                changesHistory = changesHistory.concat(fac.getDataItem().getField().getName().concat(": "))
                        .concat(String.valueOf(fac.getCommonValue())).concat(" -> ").concat(String.valueOf(fac.getNewValue()));
            }
        }

        if (!changesHistory.isEmpty()) {
            changesHistory = Utils.getDateTime().concat(", ").concat(changesHistory).concat(", ")
                    .concat(CoreModule.getUsers().getCurrentUser().getSurname());

            for (Product product : editedItems) {
                for (MultiEditorItem fac : fieldsAndControls) {
                    if (fac.isCanBeSaved() && fac.isValueChanged()) {
                        Field field = fac.getDataItem().getField();
                        field.setAccessible(true);
                        try {
                            field.set(product, fac.getNewValue());
                            product.setHistory(product.getHistory().isEmpty() ? changesHistory :
                                    product.getHistory().concat("|").concat(changesHistory));
                        } catch (IllegalAccessException e) {
                            System.out.println("Multieditor, exeption of saving " + field.getName() + " = " + fac.getCommonValue());
                        }
                    }
                }
            }
        }*/

//        return !changesHistory.isEmpty();
        return haveChanges;
    }

    public ObservableList<Product> getEditedItems() {
        return editedItems;
    }

    public MultiEditorItem getMultiEditorItem(DataItem dataItem) {
        for (MultiEditorItem fac : fieldsAndControls) {
            if (fac.getDataItem().equals(dataItem)) {
                return fac;
            }
        }
        return null;
    }
}
