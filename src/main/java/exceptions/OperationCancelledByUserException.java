package exceptions;

public class OperationCancelledByUserException extends RuntimeException {
    public OperationCancelledByUserException() {
        super("Операция была отменена пользователем");
    }
}
