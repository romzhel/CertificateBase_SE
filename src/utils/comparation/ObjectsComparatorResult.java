package utils.comparation;

import javafx.beans.property.StringProperty;

public class ObjectsComparatorResult {
    private boolean isNeedUpdateInDB;
    private String historyComment;
    private String logComment;

    public ObjectsComparatorResult() {
        isNeedUpdateInDB = false;
        historyComment = "";
        logComment = "";
    }

    public ObjectsComparatorResult(boolean isNeedUpdateInDB, String historyComment, String logComment) {
        this.isNeedUpdateInDB = isNeedUpdateInDB;
        this.historyComment = historyComment;
        this.logComment = logComment;
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

    public String getLogComment() {
        return logComment;
    }

    public void setLogComment(String logComment) {
        this.logComment = logComment;
    }
}
