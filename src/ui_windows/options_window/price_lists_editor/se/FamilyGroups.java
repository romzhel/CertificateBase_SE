package ui_windows.options_window.price_lists_editor.se;

import ui_windows.options_window.families_editor.ProductFamilies;
import ui_windows.options_window.families_editor.ProductFamily;
import ui_windows.options_window.product_lgbk.ProductLgbk;
import ui_windows.options_window.product_lgbk.ProductLgbks;
import utils.ItemsGroup;

import java.util.Iterator;
import java.util.TreeSet;

public class FamilyGroups extends TreeSet<ItemsGroup<ProductFamily, ProductLgbk>> {

    public FamilyGroups() {
        super(new TreeSet<>((o1, o2) -> {
            String familyName1 = ProductFamilies.getInstance().getFamilyById(o1.getGroupNode().getId()).getName();
            String familyName2 = ProductFamilies.getInstance().getFamilyById(o2.getGroupNode().getId()).getName();

            return familyName1.compareToIgnoreCase(familyName2);
        }));

        for (ProductFamily pf : ProductFamilies.getInstance().getItems()) {

            for (ProductLgbk pl : ProductLgbks.getInstance().getItems()) {
                if (pf.getId() == pl.getFamilyId()) {
                    ItemsGroup<ProductFamily, ProductLgbk> newFamilyGroup = new ItemsGroup<>(pf,
                            (o1, o2) -> o1.getLgbk().compareTo(o2.getLgbk()));

                    if (super.contains(newFamilyGroup)) {
                        Iterator<ItemsGroup<ProductFamily, ProductLgbk>> iterator = super.iterator();
                        while (iterator.hasNext()) {
                            ItemsGroup<ProductFamily, ProductLgbk> temp = iterator.next();
                            if (temp.getGroupNode().getId() == pl.getFamilyId()) {
                                temp.addItem(pl);
                                break;
                            }
                        }
                    } else {
                        newFamilyGroup.addItem(pl);
                        super.add(newFamilyGroup);
                    }
                }
            }
        }
    }
}
