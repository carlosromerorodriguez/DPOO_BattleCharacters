package project.persistence.exceptions;

/**
 * Generic class for exceptions to the persistence layer, with a message and a cause
 */
public class PersistenceException extends Exception {
    /**
     * This exception is thrown when a persistence operation fails
     * @param message The message of the exception
     */
    public PersistenceException(String message, Exception cause) {
        super(message, cause);
    }
}
