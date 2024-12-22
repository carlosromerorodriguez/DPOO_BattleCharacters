package project.business;

import project.business.entities.character.Character;
import project.persistence.exceptions.PersistenceException;

import java.util.List;

/**
 * Interface for the Character class
 */
public interface CharacterInterface {
    /**
     * Generates and returns an array of random statistics for a character's attributes.
     * This method simulates the rolling of a six-sided die twice for each of the three-character attributes:
     * body, mind, and spirit. The returned array contains two rolls for body, followed by two rolls for mind,
     * and finally two rolls for a spirit.
     *
     * @return An array of six integers, representing two dice rolls each for body, mind, and spirit attributes in that order.
     */
    int[] generateStats();

    /**
     * Calculates the final value of a character attribute based on the sum of two input statistics.
     *
     * <p>This method takes two integers as input, representing statistics for a character attribute.
     * It sums these two statistics and then determines the final value of the attribute based on the
     * sum. The final value is determined as follows:</p>
     * <ul>
     *     <li>Sum of 2 results in -1</li>
     *     <li>Sum between 6 and 9 inclusive results in 1</li>
     *     <li>Sum between 10 and 11 inclusive results in 2</li>
     *     <li>Sum of 12 results in 3</li>
     * </ul>
     *
     * <p>Any other sum results in a final value of 0.</p>
     *
     * @param stat The first statistic to be summed.
     * @param stat1 The second statistic to be summed.
     * @return The final value of the attribute based on the sum of the two input statistics.
     */
    int calculateFinalStat(int stat, int stat1);

    /**
     * Formats an integer by prefixing it with a plus sign if it is non-negative.
     * This method takes an integer as input and returns a string representation of the number.
     * If the number is non-negative (i.e., positive or zero), it prefixes the number with a plus sign ('+').
     *
     * @param number The integer to be formatted.
     * @return A string representation of the number with a plus sign if it is non-negative.
     */
    String formatSignStats(int number);

    /**
     * Converts a character level to its equivalent experience points (XP).
     *
     * <p>This method takes an integer representing the level of a character and converts it to experience points.
     * The conversion is based on a linear scale where each level above 1 is equal to 100 times the level minus 100.</p>
     *
     * <p>For example:</p>
     * <ul>
     *     <li>Level 1 is equal to 0 XP.</li>
     *     <li>Level 2 is equal to 100 XP (2 * 100 - 100).</li>
     *     <li>Level 3 is equal to 200 XP (3 * 100 - 100), and so on.</li>
     * </ul>
     *
     * @param lvl The level of the character.
     * @return The equivalent experience points for the given level.
     */
    int convertLvlToXp(int lvl);

    /**
     * Converts experience points (XP) to its equivalent character level.
     *
     * <p>This method takes an integer representing experience points and converts it to a character level.
     * The conversion is based on a linear scale where each 100 XP is equal to one level above 1.</p>
     *
     * <p>For example:</p>
     * <ul>
     *     <li>0 to 99 XP is equal to Level 1.</li>
     *     <li>100 to 199 XP is equal to Level 2.</li>
     *     <li>200 to 299 XP is equal to Level 3, and so on.</li>
     * </ul>
     *
     * <p>The maximum level achievable is 10. Any XP greater than or equal to 1000 will result in Level 10.</p>
     *
     * @param xp The experience points of the character.
     * @return The equivalent level for the given experience points.
     */
    int convertXpToLvl(int xp);

    /**
     * Creates a new character with the specified attributes and persists it.
     * This method creates a new character with the given name, player's name, experience points (XP), and final
     * values for body, mind, and spirit. The character is assigned a default class of "Adventurer". After creating
     * the character object, it delegates the persistence to a Character Data Access Object (DAO).
     *
     * @param name             The name of the character.
     * @param playerName       The name of the player controlling the character.
     * @param xp               The experience points of the character.
     * @param bodyFinalValue   The final value for the body attribute of the character.
     * @param mindFinalValue   The final value for the mind attribute of the character.
     * @param spiritFinalValue The final value for the spirit attribute of the character.
     * @param characterClass   The class of the character (Adventurer, Cleric or Mage).
     *
     * @return The type of character message.
     * @throws PersistenceException If an error occurs during the persistence of the character.
     */
    String createCharacter(String name, String playerName, int xp, int bodyFinalValue, int mindFinalValue, int spiritFinalValue, String characterClass) throws PersistenceException;
    /**
     * Validates the given character name.
     * This method checks if the provided name conforms to the specified rules:
     * 1. The name should only contain alphabetic characters, including accented characters,
     *    spaces, and no numbers or special characters.
     * 2. The name should not be empty or blank.
     * 3. The name should not be equal (ignoring case) to any existing character's name.
     *
     * @param name The name of the character to be validated.
     *
     * @return {@code true} if the name is valid according to the rules; {@code false} otherwise.
     * @throws PersistenceException If an error occurs while retrieving the list of existing characters from the DAO.
     */
    boolean validateName(String name) throws PersistenceException;

    /**
     * Converts the input string to a title case.
     * This method takes a string with potentially multiple words separated by spaces,
     * and returns a new string where the first letter of each word is capitalized and
     * the remaining letters are in lowercase.
     *
     * @param name The input string to be converted to title case.
     *
     * @return The input string converted to a title case.
     */
    String title(String name);

    /**
     * Retrieves a list of all characters.
     *
     * @return A List of Character objects representing all characters.
     * @throws PersistenceException If there is an issue with accessing the data store.
     */
    List<Character> listAllCharacters() throws PersistenceException;

    /**
     * Retrieves a list of characters filtered by the player's name.
     *
     * @param name The name to be used as a filter.
     *
     * @return A List of Character objects whose player's name contains the specified name.
     * @throws PersistenceException If there is an issue with accessing the data store.
     */
    List<Character> listAllCharactersByName(String name) throws PersistenceException;

    /**
     * Deletes the specified character.
     *
     * @param c The Character object to be deleted.
     *
     * @throws PersistenceException If there is an issue with accessing or updating the data store.
     */
    void deleteCharacter(Character c) throws PersistenceException;

    /**
     * Checks if the provided character name should be deleted.
     *
     * @param characterNameToDelete The name of the character to check for deletion.
     * @param characterName The name to be compared.
     *
     * @return True if the names are equal and the character should be deleted, false otherwise.
     */
    boolean shouldDeleteCharacter(String characterNameToDelete, String characterName);

    /**
     * Checks if the provided character name is empty or blank.
     *
     * @param characterNameToDelete The name of the character to check.
     *
     * @return True if the character name is empty or blank, false otherwise.
     */
    boolean nameIsEmpty(String characterNameToDelete);

    /**
     * Retrieves a character by its index.
     *
     * @param whichCharacter The index of the character to retrieve.
     *
     * @return The Character object at the specified index.
     * @throws PersistenceException If there is an issue with accessing the data store.
     */
    Character getCharacterByID(int whichCharacter) throws PersistenceException;

    /**
     * Validates if there are enough characters available.
     *
     * @return True if there are at least 3 characters available, false otherwise.
     * @throws PersistenceException If there is an issue with accessing the data store.
     */
    boolean validateCharactersAvailability() throws PersistenceException;
    List<Character> listAllCharactersFromApi();
    List<Character> listAllCharactersByNameFromApi(String trim);
    void deleteCharacterFromApi(Character c);
    String createCharacterFromApi(String name, String playerName, int i, int finalStat, int finalStat1, int finalStat2, String title);
    boolean validateNameFromApi(String name);
    Character getCharacterFromApiByID(int whichCharacter);
}
