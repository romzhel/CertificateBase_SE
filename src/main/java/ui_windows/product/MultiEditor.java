package ui_windows.product;

import lombok.extern.log4j.Log4j2;
import ui.Dialogs;
import ui_windows.main_window.file_import_window.FileImportParameter;
import ui_windows.main_window.file_import_window.SingleProductsComparator;
import ui_windows.options_window.user_editor.Users;
import ui_windows.product.data.DataItem;
import ui_windows.product.productEditorWindow.ProductEditorWindowController;
import utils.Utils;
import utils.comparation.products.ComparationParameterSets;
import utils.comparation.te.PropertyProtectEnum;
import utils.comparation.te.PropertyProtectService;

import java.util.*;

import static utils.comparation.te.PropertyProtectEnum.NON_PROTECTED;
import static utils.comparation.te.PropertyProtectEnum.PROTECTED;

@Log4j2
public class MultiEditor {
    public static int MODE_NO_ITEMS = 0;
    public static int MODE_SINGLE = 1;
    public static int MODE_MULTI = 2;
    private List<Product> editedItems;
    private List<MultiEditorItem> comparedFields;
    private Product resultProduct;
    private ProductEditorWindowController pewc;
    private Map<DataItem, PropertyProtectEnum> propertyProtectMap;

    public MultiEditor(List<Product> editedItems, ProductEditorWindowController pewc) {
        this.pewc = pewc;
        this.editedItems = editedItems;
        this.comparedFields = new ArrayList<>();
        this.propertyProtectMap = new HashMap<>();
        comparedFields.addAll(editedItems.size() > 1 ?
                Arrays.asList(ComparationParameterSets.getMultiProductComparationParameters()) :
                Arrays.asList(ComparationParameterSets.getSingleProductComparationParameters()));
        compareAndDisplay();
    }

    public void compareAndDisplay() {
        resultProduct = new Product();
        for (MultiEditorItem fac : comparedFields) {
            propertyProtectMap.put(fac.getDataItem(), fac.compare(editedItems));
            try {
                if (fac.getDataItem().getField() != null) {
                    fac.getDataItem().getField().setAccessible(true);
                    fac.getDataItem().getField().set(resultProduct, fac.getCommonValue());
                }
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
        boolean havePropertyProtectChanges = false;
        FileImportParameter[] parameters = new FileImportParameter[comparedFields.size()];
        int index = 0;
        for (MultiEditorItem mei : comparedFields) {
            parameters[index++] = new FileImportParameter(mei);
        }

        SingleProductsComparator comparator;
        PropertyProtectService protectService = new PropertyProtectService();
        for (Product product : editedItems) {
            comparator = new SingleProductsComparator(product, resultProduct, true, parameters);
            haveChanges |= comparator.getResult().isNeedUpdateInDB();

//            Comparator<Product> comparator1 = new Comparator<>();
//            comparator1.compare(product, resultProduct, new ComparingParameters(comparedFields, new Rules));

            for (DataItem dataItem : propertyProtectMap.keySet()) {
                if (product.getProtectedData().contains(dataItem) && propertyProtectMap.get(dataItem) == NON_PROTECTED) {
                    product.getProtectedData().remove(dataItem);
                    havePropertyProtectChanges = true;
                } else if (!product.getProtectedData().contains(dataItem) && propertyProtectMap.get(dataItem) == PROTECTED) {
                    product.getProtectedData().add(dataItem);
                    havePropertyProtectChanges = true;
                }
            }

            if (haveChanges) {
                String oldHistory = product.getHistory();
                String newHistory = Utils.getDateTime().concat(comparator.getResult().getHistoryComment().concat(", ")
                        .concat(Users.getInstance().getCurrentUser().getSurname()));

                product.setHistory(oldHistory != null && !oldHistory.isEmpty() ?
                        oldHistory.concat("|").concat(newHistory) :
                        newHistory);
            }
        }
        return haveChanges || havePropertyProtectChanges;
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

    public Map<DataItem, PropertyProtectEnum> getPropertyProtectMap() {
        return propertyProtectMap;
    }
}
