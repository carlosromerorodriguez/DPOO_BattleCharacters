package project.business.exceptions;

/**
 * Exception to be thrown when the battle has finished or all the party members have fallen unconscious
 */
public class FinishedBattleException extends Exception {

    /**
     * Constructor for the exception when it is a TPU done in battle
     */
    public FinishedBattleException() {
        super("""
                
                Tavern keeper: “Lad, wake up. Yes, your party fell unconscious.”\s
                “Don’t worry, you are safe back at the Tavern.”
                """);
    }

    /**
     * Constructor for the exception when the party has won the adventure
     * @param adventureName The name of the adventure
     */
    public FinishedBattleException(String adventureName) {
        super("Congratulations! Your party completed “" + adventureName + "”");
    }
}
