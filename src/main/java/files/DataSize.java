package files;

public class DataSize {
    private int cols;
    private int rows;

    public DataSize(int cols, int rows){
        this.cols = cols;
        this.rows = rows;
    }

    public void resizeData(int cols, int rows){
        this.cols = cols;
        this.rows = rows;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }
}
