package cz.tomasdvorak.auth;

public class InvalidCredentialsException extends Exception {
    public InvalidCredentialsException(final String message) {
        super(message);
    }
}
