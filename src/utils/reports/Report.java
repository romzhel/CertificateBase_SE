package utils.reports;

import java.util.ArrayList;

public interface Report {
    void addData(Object... lineItems);
    ArrayList<Object[]> getData();
}
