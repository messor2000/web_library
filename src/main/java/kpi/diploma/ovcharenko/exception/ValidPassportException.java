package kpi.diploma.ovcharenko.exception;

public class ValidPassportException extends Exception {

    public ValidPassportException(String message) {
        super(message);
    }

    public ValidPassportException(String message, Throwable cause) {
        super(message, cause);
    }
}
