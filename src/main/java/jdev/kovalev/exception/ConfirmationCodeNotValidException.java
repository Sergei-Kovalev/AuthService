package jdev.kovalev.exception;

public class ConfirmationCodeNotValidException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "The confirmation code is invalid.";
    public ConfirmationCodeNotValidException() {
        super(DEFAULT_MESSAGE);
    }
}
