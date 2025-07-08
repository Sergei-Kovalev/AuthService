package jdev.kovalev.exception;

public class EmailNotRegisteredException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Email not registered";
    public EmailNotRegisteredException() {
        super(DEFAULT_MESSAGE);
    }
}
