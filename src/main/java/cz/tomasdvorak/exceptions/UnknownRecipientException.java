package cz.tomasdvorak.exceptions;

public class UnknownRecipientException extends Exception {
    public UnknownRecipientException(final String message) {
        super(message);
    }
}
