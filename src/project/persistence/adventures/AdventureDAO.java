package project.persistence.adventures;

import project.business.entities.adventure.Adventure;
import project.persistence.exceptions.PersistenceException;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Interface for the AdventureDAO class.
 */
public interface AdventureDAO {
    /**
     * Checks if an adventure with the given name already exists in the persistent storage.
     * This method reads adventures from a JSON file and checks if any adventure has the
     * same name as the provided input, ignoring case considerations.
     *
     * @param adventureName The name of the adventure to check for existence.
     * @return True if an adventure with the given name already exists, false otherwise.
     * @throws PersistenceException If an issue occurs while reading from the file.
     */
    boolean adventureAlreadyExists(String adventureName) throws PersistenceException;

    /**
     * Saves an adventure with its encounters to the persistent storage.
     * This method serializes an adventure with its associated encounters and saves them
     * in a JSON file. Each encounter within the adventure is a LinkedHashMap of monster names
     * along with their quantities. The monster names include the challenge in the format "name (challenge)".
     * The method extracts the name and challenge through regex and creates a structured JSON
     * representation before saving.
     *
     * @param encounters     A list of LinkedHashMaps, each representing an encounter with
     *                       monster names (with challenges) as keys and quantities as values.
     * @param adventureName  The name of the adventure to be saved.
     * @throws PersistenceException If an issue occurs while writing to the file.
     */
    void saveAdventure(List<LinkedHashMap<String, Integer>> encounters, String adventureName) throws PersistenceException;

    /**
     * Retrieves the names of all adventures from the persistent storage.
     * This method reads adventures from a JSON file and extracts the names of
     * all adventures, returning them in a list.
     *
     * @return A list of adventure names.
     * @throws PersistenceException If an issue occurs while reading from the file.
     */
    List<String> getAllAdventuresNames() throws PersistenceException;

    /**
     * Retrieves an adventure by its ID from the persistent storage.
     * This method reads adventures from a JSON file and extracts the adventure
     * corresponding to the provided ID. The method also deserializes the adventure
     * data into an Adventure object, including its encounters and monsters.
     *
     * @param id The ID of the adventure to retrieve.
     * @return An Adventure object representing the adventure with the specified ID.
     * @throws PersistenceException If an issue occurs while reading from the file or if the ID is invalid.
     */
    Adventure getAdventureByID(int id) throws PersistenceException;

    /**
     * Retrieves the names and quantities of monsters in a specific encounter within an adventure.
     * This method searches through adventures stored in a JSON file to find the specified adventure
     * by its name. Within this adventure, it looks for the encounter at the provided index and retrieves
     * the names and quantities of monsters in this encounter.
     *
     * @param encounterIndex The index of the encounter within the adventure (1-based).
     * @param adventureName  The name of the adventure to search within.
     * @return A list of monster names and quantities in the format "quantity x monsterName".
     * @throws PersistenceException If an issue occurs while reading from the file or if the adventure or encounter is not found.
     */
    List<String> getMonsterNamesInEncounter(int encounterIndex, String adventureName) throws PersistenceException;
}
