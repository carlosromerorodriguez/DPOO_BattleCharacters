package project.business.exceptions;

/**
 * Exception thrown when all monsters in an encounter are defeated.
 */
public class NonAliveMonsterException extends Exception {
    /**
     * Constructor with message parameter to be shown when the exception is thrown.
     */
    public NonAliveMonsterException() {
        super("""
              All monsters in this encounter are defeated!
              Onwards to the next challenge! Good luck!
                    
              Press ENTER to continue...""");
    }
}
