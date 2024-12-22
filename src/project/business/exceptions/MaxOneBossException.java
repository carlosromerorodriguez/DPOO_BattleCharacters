package project.business.exceptions;

/**
 * Exception thrown when the user tries to add more than one boss to the team
 */
public class MaxOneBossException extends RuntimeException {
    /**
     * Constructor
     * @param message The message to be shown
     */
    public MaxOneBossException(String message) {
        super(message);
    }
}
