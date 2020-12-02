package utils.comparation.products;

import ui_windows.product.MultiEditorItem;

import static ui_windows.product.data.DataItem.*;

public class ComparationParameterSets {

    public static MultiEditorItem[] getSingleProductComparationParameters() {
        MultiEditorItem[] parameters = new MultiEditorItem[]{
                new MultiEditorItem(DATA_DESCRIPTION_RU, true),
                new MultiEditorItem(DATA_DESCRIPTION_EN, true),
                new MultiEditorItem(DATA_TYPE, true),
                new MultiEditorItem(DATA_FAMILY_ID, true),
                new MultiEditorItem(DATA_MANUAL_FILE, true),
                new MultiEditorItem(DATA_REPLACEMENT, true),
                new MultiEditorItem(DATA_COMMENT, true),
                new MultiEditorItem(DATA_IS_IN_PRICE, true),
                new MultiEditorItem(DATA_IS_BLOCKED, true)
        };
        return parameters;
    }

    public static MultiEditorItem[] getMultiProductComparationParameters() {
        MultiEditorItem[] parameters = new MultiEditorItem[]{
                new MultiEditorItem(DATA_DESCRIPTION_RU, true),
                new MultiEditorItem(DATA_DESCRIPTION_EN, true),
                new MultiEditorItem(DATA_TYPE, true),
                new MultiEditorItem(DATA_FAMILY_ID, true),
                new MultiEditorItem(DATA_MANUAL_FILE, true),
                new MultiEditorItem(DATA_REPLACEMENT, true),
                new MultiEditorItem(DATA_COMMENT, true),
                new MultiEditorItem(DATA_IS_IN_PRICE, true),
                new MultiEditorItem(DATA_IS_BLOCKED, true),

                new MultiEditorItem(DATA_LGBK, false),
                new MultiEditorItem(DATA_HIERARCHY, false),
                new MultiEditorItem(DATA_DCHAIN, false),
                new MultiEditorItem(DATA_SERVICE_END, false),
                new MultiEditorItem(DATA_COUNTRY, false)
        };
        return parameters;
    }
}
