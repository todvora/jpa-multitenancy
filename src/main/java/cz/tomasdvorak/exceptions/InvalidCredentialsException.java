package cz.tomasdvorak.exceptions;

public class InvalidCredentialsException extends Exception {
    public InvalidCredentialsException(final String message) {
        super(message);
    }
}
