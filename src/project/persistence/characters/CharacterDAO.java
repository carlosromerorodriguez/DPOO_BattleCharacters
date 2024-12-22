package project.persistence.characters;

import project.business.entities.battle.character.BattleCharacter;
import project.business.entities.character.Character;
import project.persistence.exceptions.PersistenceException;

import java.util.List;

/**
 * Interface that defines the methods that a CharacterDAO class must implement
 */
public interface CharacterDAO {
    /**
     * Adds a new character to the persistent storage.
     * This method serializes the given character object and appends it to a JSON array
     * which is stored in a file. The characters are stored as JSON objects within this array.
     * The GsonBuilder is used to ensure that the JSON representation of the character is pretty printed.
     *
     * @param character The character object to be serialized and stored.
     * @throws PersistenceException If there is an issue reading from or writing to the file.
     * This exception wraps the underlying IOException, providing more context as to what
     * the operation was trying to achieve.
     */
    void createCharacter(Character character) throws PersistenceException;

    /**
     * Retrieves a list of all characters from the persistent storage.
     * This method reads a JSON file, deserializes the character objects, and returns them
     * as a list.
     *
     * @return A list of Character objects.
     * @throws PersistenceException If an issue occurs while reading from the file.
     */
    List<Character> getAllCharacters() throws PersistenceException;

    /**
     * Stores a list of characters back to the persistent storage in JSON format.
     * This method serializes a list of character objects and saves them in a JSON array
     * in a file. The GsonBuilder is used for pretty-printing.
     *
     * @param characters The list of Character objects to be serialized and stored.
     * @throws PersistenceException If an issue occurs while writing to the file.
     */
    void reAddCharactersToJSON(List<Character> characters) throws PersistenceException;

    /**
     * Updates the experience points (XP) of characters in the persistent storage.
     * This method reads characters from a JSON file, updates the XP of the characters
     * provided in the input list, and saves the changes back to the file.
     *
     * @param characters The list of BattleCharacter objects with updated XP to be saved.
     * @throws PersistenceException If an issue occurs while reading from or writing to the file.
     */
    void updateCharactersXP(List<BattleCharacter> characters) throws PersistenceException;
}
