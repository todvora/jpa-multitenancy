package cz.tomasdvorak.exceptions;

public class UnknownTenantException extends Exception {
    public UnknownTenantException(final String message) {
        super(message);
    }
}
