package kpi.diploma.ovcharenko.exception;

public class ValidPasswordException extends Exception {

    public ValidPasswordException(String message) {
        super(message);
    }

    public ValidPasswordException(String message, Throwable cause) {
        super(message, cause);
    }
}
