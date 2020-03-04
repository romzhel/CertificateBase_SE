package ui_windows.product;

import core.CoreModule;
import core.Dialogs;
import ui_windows.main_window.file_import_window.FileImportParameter;
import ui_windows.main_window.file_import_window.SingleProductsComparator;
import ui_windows.product.data.DataItem;
import ui_windows.product.productEditorWindow.ProductEditorWindowController;
import utils.Utils;
import utils.comparation.products.ComparationParameterSets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultiEditor {
    public static int MODE_NO_ITEMS = 0;
    public static int MODE_SINGLE = 1;
    public static int MODE_MULTI = 2;
    private List<Product> editedItems;
    private List<MultiEditorItem> comparedFields;
    private Product resultProduct;
    private ProductEditorWindowController pewc;

    public MultiEditor(List<Product> editedItems, ProductEditorWindowController pewc) {
        this.pewc = pewc;
        this.editedItems = editedItems;
        this.comparedFields = new ArrayList<>();
        comparedFields.addAll(editedItems.size() > 1 ?
                Arrays.asList(ComparationParameterSets.getMultiProductComparationParameters()) :
                Arrays.asList(ComparationParameterSets.getSingleProductComparationParameters()));
        compareAndDisplay();
    }

    public void compareAndDisplay() {
        resultProduct = new Product();
        for (MultiEditorItem fac : comparedFields) {
            fac.compare(editedItems);
            fac.getDataItem().getField().setAccessible(true);
            try {
                fac.getDataItem().getField().set(resultProduct, fac.getCommonValue());
            } catch (IllegalAccessException e) {
                Dialogs.showMessage("Мультиредактор", "Ошибка сохранения результатов сравнения " + e.getMessage());
            }
//            fac.setUiAccessibility();
        }

        if (editedItems.size() == 1) {
            editedItems.get(0).displayInEditorWindow(pewc);
        } else if (editedItems.size() > 1) {
            resultProduct.displayInEditorWindow(pewc);
        }
    }

    public boolean checkAndSaveChanges() {
        resultProduct = new Product(pewc);
        boolean haveChanges = false;
        FileImportParameter[] parameters = new FileImportParameter[comparedFields.size()];
        int index = 0;
        for (MultiEditorItem mei : comparedFields) {
            parameters[index++] = new FileImportParameter(mei);
        }

        SingleProductsComparator comparator;
        for (Product product : editedItems) {
            comparator = new SingleProductsComparator(product, resultProduct, true, parameters);
            haveChanges |= comparator.getResult().isNeedUpdateInDB();

//            Comparator<Product> comparator1 = new Comparator<>();
//            comparator1.compare(product, resultProduct, new ComparingParameters(comparedFields, new Rules));

            if (haveChanges) {
                String oldHistory = product.getHistory();
                String newHistory = Utils.getDateTime().concat(comparator.getResult().getHistoryComment().concat(", ")
                        .concat(CoreModule.getUsers().getCurrentUser().getSurname()));

                product.setHistory(oldHistory != null && !oldHistory.isEmpty() ?
                        oldHistory.concat("|").concat(newHistory) :
                        newHistory);
            }
        }
        return haveChanges;
    }

    public List<Product> getEditedItems() {
        return editedItems;
    }

    public MultiEditorItem getMultiEditorItem(DataItem dataItem) {
        for (MultiEditorItem fac : comparedFields) {
            if (fac.getDataItem().equals(dataItem)) {
                return fac;
            }
        }
        return null;
    }

    public int getMode() {
        return editedItems.size() == 0 ? MODE_NO_ITEMS :
                editedItems.size() == 1 ? MODE_SINGLE : MODE_MULTI;
    }

    public Product getSingleEditedItem() {
        return editedItems.get(0);
    }
}
