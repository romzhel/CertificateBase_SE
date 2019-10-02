package ui_windows.options_window.price_lists_editor.se;

import core.CoreModule;
import javafx.scene.control.TreeItem;
import ui_windows.options_window.families_editor.ProductFamily;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import utils.ItemsGroup;

public class FamilyTree extends TreeItem {

    public FamilyTree(FamilyGroups familyGroups) {
        super(new ProductFamily("Все направления"));

        for (ItemsGroup<ProductFamily, ProductLgbk> familyGroup : familyGroups) {
            TreeItem newFamilyGroup = new TreeItem<>(familyGroup.getGroupNode());
            super.getChildren().add(newFamilyGroup);

            for (ProductLgbk lgbk : familyGroup.getItems()) {
                if (familyGroup.getGroupNode().getId() == lgbk.getFamilyId() || lgbk.getFamilyId() == -1) {
                    TreeItem lgbkGroup = new TreeItem<>(lgbk);
                    newFamilyGroup.getChildren().add(lgbkGroup);

                    for (TreeItem<ProductLgbk> checkedLgbk : CoreModule.getProductLgbkGroups().getTreeItem(lgbk).getChildren()) {
                        if (checkedLgbk.getValue().getFamilyId() == familyGroup.getGroupNode().getId() ||
                                checkedLgbk.getValue().getFamilyId() == -1) {
                            lgbkGroup.getChildren().add(checkedLgbk);
                        }
                    }
                }
            }
        }
    }
}
