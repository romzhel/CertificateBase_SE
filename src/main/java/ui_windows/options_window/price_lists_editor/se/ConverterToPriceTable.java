package ui_windows.options_window.price_lists_editor.se;

import javafx.scene.control.TreeItem;

public class ConverterToPriceTable<T extends PriceListContentItem> extends TreeItem<PriceListContentTableItem> {
//    private TreeItem<PriceListContentTableItem> result;

    public ConverterToPriceTable(TreeItem<T> source) {
        super(source.getValue().getTableItem());

//        result = new TreeItem<>(source.getValue().getTableItem());

        for (TreeItem<T> treeItems1level : source.getChildren()) {
            TreeItem<PriceListContentTableItem> resultItem1level = new TreeItem<>(treeItems1level.getValue().getTableItem());
            this.getChildren().add(resultItem1level);

            for (TreeItem<T> treeItems2level : treeItems1level.getChildren()) {
                TreeItem<PriceListContentTableItem> resultItem2level = new TreeItem<>(treeItems2level.getValue().getTableItem());
                resultItem1level.getChildren().add(resultItem2level);

                for (TreeItem<T> treeItems3level : treeItems2level.getChildren()) {

                    TreeItem<PriceListContentTableItem> resultItem3level = new TreeItem<>(treeItems3level.getValue().getTableItem());
                    resultItem2level.getChildren().add(resultItem3level);
                }
            }
        }
    }
}
