package exceptions;

public class AppExpiredException extends RuntimeException {
    public AppExpiredException(String message) {
        super(message);
    }
}
