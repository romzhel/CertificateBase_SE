package ui_windows.options_window.price_lists_editor.se;

import javafx.scene.control.TreeItem;
import ui_windows.options_window.families_editor.ProductFamily;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.options_window.product_lgbk.ProductLgbkGroups;
import utils.ItemsGroup;

public class FamilyTree extends TreeItem<PriceListContentTableItem> {

    public FamilyTree(FamilyGroups familyGroups) {
        super(new ProductFamily("Все направления").getTableItem());

        for (ItemsGroup<ProductFamily, ProductLgbk> familyGroup : familyGroups) {
            TreeItem<PriceListContentTableItem> newFamilyGroup = new TreeItem<>(familyGroup.getGroupNode().getTableItem());
            super.getChildren().add(newFamilyGroup);

            for (ProductLgbk lgbk : familyGroup.getItems()) {
                if (familyGroup.getGroupNode().getId() == lgbk.getFamilyId() || lgbk.getFamilyId() == -1) {
                    TreeItem<PriceListContentTableItem> lgbkGroup = new TreeItem<>(lgbk.getTableItem());
                    newFamilyGroup.getChildren().add(lgbkGroup);

                    for (TreeItem<ProductLgbk> checkedLgbk : ProductLgbkGroups.getInstance().getTreeItem(lgbk).getChildren()) {
                        if (checkedLgbk.getValue().getFamilyId() == familyGroup.getGroupNode().getId() ||
                                checkedLgbk.getValue().getFamilyId() == -1) {
                            lgbkGroup.getChildren().add(new TreeItem<>(checkedLgbk.getValue().getTableItem()));
                        }
                    }
                }
            }
        }
    }
}
