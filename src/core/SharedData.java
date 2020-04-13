package core;


import utils.Utils;

import java.util.HashSet;
import java.util.Set;

public enum SharedData {
    SHD_DATA_SET, SHD_DISPLAYED_DATA, SHD_FILTER_PARAMETERS, SHD_SELECTED_PRODUCTS, SHD_CUSTOM_DATA;

    public static boolean NOT_NOTIFY = false;
    public static boolean NOTIFY = true;
    private Object data;
    private Set<Module> subscribers;

    <T> SharedData() {
        this.data = null;
    }

    public <T> T getData() {
        return (T) data;
    }

    public <T> void setData(Class from, T data) {
        setData(from, data, null, NOTIFY);
    }

    public <T> void setData(Class from, T data, boolean notify) {
        setData(from, data, null, notify);
    }

    public <T> void setData(Class from, T data, Module module) {
        setData(from, data, module, NOTIFY);
    }

    public <T> void setData(Class from, T data, Module module, boolean notify) {
//        System.out.printf("%s set data from module %s", this.name(),
//                module == null ? "null" : module.getClass().getSimpleName());

//        System.out.printf("\n[%s] %s -> %s -> ", Utils.getExactTime(), from.getSimpleName(), this.name());

        this.data = data;
        if (subscribers != null && notify) {
            for (Module subscriber : subscribers) {
                if (subscriber != module) {
//                    System.out.printf("%s, ", subscriber.getClass().getSimpleName());
                    subscriber.refreshSubscribedData(this, this.data);
                }
            }
        }
    }

    public boolean subscribe(Module module) {
        if (subscribers == null) {
            subscribers = new HashSet<>();
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
