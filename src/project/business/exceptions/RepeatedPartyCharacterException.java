package project.business.exceptions;

/**
 * Exception thrown when a character is added to a party, and it is already in it.
 */
public class RepeatedPartyCharacterException extends Exception {

    /**
     * Constructor with message parameter to be shown when the exception is thrown.
     * @param message Message to be shown when the exception is thrown.
     */
    public RepeatedPartyCharacterException(String message) {
        super(message);
    }
}
