package core;

import ui_windows.main_window.filter_window_se.FilterParameters_SE;
import ui_windows.product.Product;

import java.util.List;

import static core.SharedData.SHD_DATA_SET;
import static core.SharedData.SHD_FILTER_PARAMETERS;

public class Listen implements Module {

    public Listen() {
//        SHD_FILTER_PARAMETERS.subscribe(this);
//        SHD_DATA_SET.subscribe(this);
    }

    @Override
    public void refreshSubscribedData(SharedData sharedData, Object data) {
        System.out.print(sharedData.name() + ": ");

        if (sharedData == SHD_FILTER_PARAMETERS && data instanceof FilterParameters_SE) {
            System.out.println(this.getClass().getName() + "; filter params " + data.toString());
        } else if (sharedData == SHD_DATA_SET && data instanceof List) {
            List<Product> dataSet = SHD_DATA_SET.getData();
            System.out.println(this.getClass().getName() + "; dataset " + dataSet.size());
        }
    }
}
