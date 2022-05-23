package kpi.diploma.ovcharenko.exception;

import java.util.function.Supplier;

public class BookDoesntPresentException extends RuntimeException {
    public BookDoesntPresentException(String massage) {
        super(massage);
    }
}
