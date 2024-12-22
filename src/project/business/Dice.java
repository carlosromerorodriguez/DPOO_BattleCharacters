package project.business;

import java.util.Random;

/**
 * This class represents a dice. It is used to generate random numbers.
 */
public final class Dice {
    /**
     * The random number generator.
     */
    private static final Random random = new Random();
    /**
     * The constructor is private to prevent instantiation.
     */
    private Dice() {}
    /**
     * This method returns a random number between min and max (both included).
     *
     * @return a random number between min and max (both included)
     */
    public static int valueBetween(int min, int max) {
        return random.nextInt(min, max + 1);
    }
}
