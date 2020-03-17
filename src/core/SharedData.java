package core;


import java.util.ArrayList;

public enum SharedData {
    SHD_DATA_SET, SHD_DISPLAYED_DATA, SHD_FILTER_PARAMETERS, SHD_SELECTED_PRODUCTS, SHD_CUSTOM_DATA;

    private Object data;
    private ArrayList<Module> subscribers;

    <T> SharedData() {
        this.data = null;
    }

    public <T> T getData() {
        return (T) data;
    }

    public <T> void setData(T data) {
        setData(data, null);
    }

    public <T> void setData(T data, Module module) {
//        System.out.println(String.format("%s set data from module %s", this.name(),
//                (module == null ? "null" : module.getClass().getSimpleName())));

        this.data = data;
        if (subscribers != null) {
            for (Module subscriber : subscribers) {
                if (!subscriber.equals(module)) {
                    subscriber.refreshSubscribedData(this, this.data);
                }
            }
        }
    }

    public boolean subscribe(Module module) {
        if (subscribers == null) {
            subscribers = new ArrayList<>();
        }
        subscribers.add(module);
        return true;
    }

    public boolean unsubscribe(Module module) {
        if (subscribers == null) {
            return false;
        }
        return subscribers.remove(module);
    }
}
