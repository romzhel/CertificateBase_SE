package utils.comparation;

import javafx.beans.property.StringProperty;

public class ObjectsComparatorResult {
    private boolean isNeedUpdateInDB;
    private String historyComment;

    public ObjectsComparatorResult() {
        isNeedUpdateInDB = false;
        historyComment = "";
    }

    public ObjectsComparatorResult(boolean isNeedUpdateInDB, String historyComment) {
        this.isNeedUpdateInDB = isNeedUpdateInDB;
        this.historyComment = historyComment;
    }

    public boolean isNeedUpdateInDB() {
        return isNeedUpdateInDB;
    }

    public String getHistoryComment() {
        return historyComment;
    }

    public void setNeedUpdateInDB(boolean needUpdateInDB) {
        isNeedUpdateInDB = needUpdateInDB;
    }

    public void setHistoryComment(String historyComment) {
        this.historyComment = historyComment;
    }
}
