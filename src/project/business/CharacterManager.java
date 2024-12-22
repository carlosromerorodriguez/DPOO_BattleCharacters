package project.business;

import org.json.JSONArray;
import project.business.entities.character.Character;
import project.persistence.api.ApiConnection;
import project.persistence.api.ApiConnectionDAO;
import project.persistence.characters.CharacterDAO;
import project.persistence.characters.JSONCharacterDAO;
import project.persistence.exceptions.PersistenceException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Class that manages the character
 */
public class CharacterManager implements CharacterInterface {
    /**
     * DAO for the character class
     */
    private final CharacterDAO characterDAO;

    /**
     * DAO for the API connection class
     */
    private final ApiConnection apiDAO;
    /**
     * Constructor for the character manager
     *
     * @param characterDAO DAO for the character class
     */
    public CharacterManager(CharacterDAO characterDAO, ApiConnection apiDAO) {
        this.characterDAO = characterDAO;
        this.apiDAO = apiDAO;
    }

    /**
     * Generates and returns an array of random statistics for a character's attributes.
     * This method simulates the rolling of a six-sided die twice for each of the three-character attributes:
     * body, mind, and spirit. The returned array contains two rolls for body, followed by two rolls for mind,
     * and finally two rolls for a spirit.
     *
     * @return An array of six integers, representing two dice rolls each for body, mind, and spirit attributes in that order.
     */
    @Override
    public int[] generateStats() {
        int bodyFirstAttempt = Dice.valueBetween(1, 6);
        int bodySecondAttempt = Dice.valueBetween(1, 6);

        int mindFirstAttempt = Dice.valueBetween(1, 6);
        int mindSecondAttempt = Dice.valueBetween(1, 6);

        int spiritFirstAttempt = Dice.valueBetween(1, 6);
        int spiritSecondAttempt = Dice.valueBetween(1, 6);

        return new int[] { bodyFirstAttempt, bodySecondAttempt, mindFirstAttempt, mindSecondAttempt, spiritFirstAttempt, spiritSecondAttempt };
    }

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
    @Override
    public int calculateFinalStat(int stat, int stat1) {
        int sum = stat + stat1;
        int valor = 0;

        if (sum == 2) { valor = -1; }
        if (sum >= 6 && sum <= 9) { valor = 1; }
        if (sum >= 10 && sum <= 11) { valor = 2; }
        if (sum == 12) { valor = 3; }

        return valor;
    }

    /**
     * Formats an integer by prefixing it with a plus sign if it is non-negative.
     * This method takes an integer as input and returns a string representation of the number.
     * If the number is non-negative (i.e., positive or zero), it prefixes the number with a plus sign ('+').
     *
     * @param number The integer to be formatted.
     * @return A string representation of the number with a plus sign if it is non-negative.
     */
    @Override
    public String formatSignStats(int number) {
        return (number >= 0 ? "+" : "" ) + number;
    }

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
    public int convertLvlToXp(int lvl) {
        return (lvl == 1 ? 0 : (lvl * 100) - 100);
    }

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
    public int convertXpToLvl(int xp) {
        return Math.min((xp >= 0 && xp <= 99 ? 1 : (xp / 100) + 1), 10);
    }

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
     * @throws PersistenceException If an error occurs during the persistence of the character.
     */
    @Override
    public String createCharacter(String name, String playerName, int xp, int bodyFinalValue, int mindFinalValue, int spiritFinalValue, String characterClass) throws PersistenceException {
        String finalClass = calculateClassFromLevel(convertXpToLvl(xp), characterClass);
        Character character = new Character(name, playerName, xp, bodyFinalValue, mindFinalValue, spiritFinalValue, finalClass);
        characterDAO.createCharacter(character);

        return finalClass;
    }

    /**
     * Updates the character with the specified attributes and persists it.
     * @param lvl The level of the character.
     * @param characterClass The class of the character.
     * @return The class of the character based on the level.
     */
    private String calculateClassFromLevel(int lvl, String characterClass) {
        return switch (characterClass) {
            case "Adventurer" -> (lvl <= 3) ? "Adventurer" : (lvl <= 7) ? "Warrior" : "Champion";
            case "Cleric" -> (lvl <= 4) ? "Cleric" : "Paladin";
            default -> "Mage";
        };
    }


    /**
     * Validates the given character name.
     * This method checks if the provided name conforms to the specified rules:
     * 1. The name should only contain alphabetic characters, including accented characters,
     *    spaces, and no numbers or special characters.
     * 2. The name should not be empty or blank.
     * 3. The name should not be equal (ignoring case) to any existing character's name.
     *
     * @param name The name of the character to be validated.
     * @return {@code true} if the name is valid according to the rules; {@code false} otherwise.
     * @throws PersistenceException If an error occurs while retrieving the list of existing characters from the DAO.
     */
    @Override
    public boolean validateName(String name) throws PersistenceException {
        if(!name.matches("^[a-zA-ZÀ-ÿ\\u00f1\\u00d1\\s]*$") || name.isEmpty() || name.isBlank()) {
            return false;
        }

        return characterDAO.getAllCharacters()
                .stream()
                .noneMatch(character -> character.name().equalsIgnoreCase(name));
    }

    /**
     * Converts the input string to a title case.
     * This method takes a string with potentially multiple words separated by spaces,
     * and returns a new string where the first letter of each word is capitalized and
     * the remaining letters are in lowercase.
     *
     * @param name The input string to be converted to title case.
     * @return The input string converted to a title case.
     */
    @Override
    public String title(String name) {
        String[] names = name.split(" ");
        StringBuilder title = new StringBuilder();
        for (String s : names) {
            title.append(s.substring(0, 1).toUpperCase()).append(s.substring(1).toLowerCase()).append(" ");
        }
        return title.toString().trim();
    }

    /**
     * Retrieves a list of all characters.
     *
     * @return A List of Character objects representing all characters.
     * @throws PersistenceException If there is an issue with accessing the data store.
     */
    @Override
    public List<Character> listAllCharacters() throws PersistenceException {
        return characterDAO.getAllCharacters();
    }

    /**
     * Retrieves a list of characters filtered by the player's name.
     *
     * @param playerName The name to be used as a filter.
     * @return A List of Character objects whose player's name contains the specified name.
     * @throws PersistenceException If there is an issue with accessing the data store.
     */
    @Override
    public List<Character> listAllCharactersByName(String playerName) throws PersistenceException {
        return characterDAO.getAllCharacters().stream()
                .filter(character -> character.player().toLowerCase().contains(playerName.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Deletes the specified character.
     *
     * @param c The Character object to be deleted.
     * @throws PersistenceException If there is an issue with accessing or updating the data store.
     */
    @Override
    public void deleteCharacter(Character c) throws PersistenceException {
        List<Character> characters = characterDAO.getAllCharacters();
        for (int i = 0; i < characters.size(); i++) {;
            if (c.name().equals(characters.get(i).name())) {
                characters.remove(i);
                break;
            }
        }
        characterDAO.reAddCharactersToJSON(characters);
    }

    /**
     * Checks if the provided character name should be deleted.
     *
     * @param characterNameToDelete The name of the character to check for deletion.
     * @param characterName The name to be compared.
     * @return True if the names are equal and the character should be deleted, false otherwise.
     */
    @Override
    public boolean shouldDeleteCharacter(String characterNameToDelete, String characterName) {
        return characterNameToDelete.equals(characterName);
    }

    /**
     * Checks if the provided character name is empty or blank.
     *
     * @param characterNameToDelete The name of the character to check.
     * @return True if the character name is empty or blank, false otherwise.
     */
    @Override
    public boolean nameIsEmpty(String characterNameToDelete) {
        return characterNameToDelete.isEmpty() || characterNameToDelete.isBlank();
    }

    /**
     * Retrieves a character by its index.
     *
     * @param whichCharacter The index of the character to retrieve.
     * @return The Character object at the specified index.
     * @throws PersistenceException If there is an issue with accessing the data store.
     */
    @Override
    public Character getCharacterByID(int whichCharacter) throws PersistenceException {
        return characterDAO.getAllCharacters().get(whichCharacter);
    }

    /**
     * Validates if there are enough characters available.
     *
     * @return True if there are at least 3 characters available, false otherwise.
     * @throws PersistenceException If there is an issue with accessing the data store.
     */
    @Override
    public boolean validateCharactersAvailability() throws PersistenceException {
        return this.listAllCharacters().size() >= 3;
    }

    @Override
    public List<Character> listAllCharactersFromApi() {
        return apiDAO.getCharactersFromApi();
    }

    @Override
    public List<Character> listAllCharactersByNameFromApi(String trim) {
        return apiDAO.getCharacterByPlayerFromApi(trim);
    }

    @Override
    public void deleteCharacterFromApi(Character c) {
        List<Character> characters = apiDAO.getCharactersFromApi();
        int position = -1;

        for (int i = 0; i < characters.size(); i++) {
            if (c.name().equals(characters.get(i).name())) {
                position = i;
                break;
            }
        }

        apiDAO.deleteCharacterFromApi(position);
    }

    @Override
    public String createCharacterFromApi(String name, String playerName, int xp, int bodyFinalValue, int mindFinalValue, int spiritFinalValue, String characterClass) {
        String finalClass = calculateClassFromLevel(convertXpToLvl(xp), characterClass);
        Character character = new Character(name, playerName, xp, bodyFinalValue, mindFinalValue, spiritFinalValue, finalClass);
        apiDAO.createCharacter(character);

        return finalClass;
    }

    @Override
    public boolean validateNameFromApi(String name) {
        if(!name.matches("^[a-zA-ZÀ-ÿ\\u00f1\\u00d1\\s]*$") || name.isEmpty() || name.isBlank()) {
            return false;
        }

        return apiDAO.getCharactersFromApi()
                .stream()
                .noneMatch(character -> character.name().equalsIgnoreCase(name));
    }

    @Override
    public Character getCharacterFromApiByID(int whichCharacter) {
        return apiDAO.getCharacterByID(whichCharacter);
    }
}
