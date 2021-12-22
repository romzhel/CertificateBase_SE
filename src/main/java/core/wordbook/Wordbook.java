package core.wordbook;

import core.Initializable;

public class Wordbook implements Initializable {
    private static Wordbook instance;

    private Wordbook() {
    }

    public static Wordbook getInstance() {
        if (instance == null) {
            instance = new Wordbook();
        }
        return instance;
    }

    @Override
    public void init() throws Exception {

    }

    public String getText(int id) {
        return "";
    }

    public int getId(String text) {
        return -1;
    }
}
