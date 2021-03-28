package exceptions;

public class DataNotSelectedException extends RuntimeException {
    public DataNotSelectedException() {
        super("Пользователь не выбрал данные");
    }
}
