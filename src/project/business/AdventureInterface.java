package project.business;

import project.business.exceptions.MaxOneBossException;
import project.persistence.exceptions.ApiServerException;
import project.persistence.exceptions.PersistenceException;

import java.util.HashMap;
import java.util.List;

/**
 * Manages adventures consisting of encounters with monsters.
 */
public interface AdventureInterface {

    /**
     * Checks if an adventure with the given name already exists.
     *
     * @param adventureName The name of the adventure.
     * @return true if the adventure already exists, false otherwise.
     * @throws PersistenceException If there is an issue accessing the data store.
     */
    boolean adventureExists(String adventureName) throws PersistenceException;

    /**
     * Retrieves the monsters in a specific encounter.
     *
     * @param encounterIndex Index of the encounter.
     * @return HashMap of monster names and their quantities.
     */
    HashMap<String, Integer> getMonstersInEncounter(int encounterIndex);

    /**
     * Adds a monster to a specific encounter.
     *
     * @param monsterName     Name of the monster to add.
     * @param monsterQuantity Quantity of the monster.
     * @param encounterIndex  Index of the encounter.
     * @throws MaxOneBossException  If there is an attempt to add more than one boss to an encounter.
     * @throws IllegalStateException If the monster name format is invalid.
     */
    void addMonsterToEncounter(String monsterName, int monsterQuantity, int encounterIndex);

    /**
     * Removes a monster from a specific encounter.
     *
     * @param deleteIndex     Index of the monster to delete.
     * @param encounterIndex  Index of the encounter.
     */
    void removeMonsterFromEncounter(int deleteIndex, int encounterIndex);

    /**
     * Saves the current state of encounters to an adventure with the given name.
     *
     * @param adventureName Name of the adventure to save.
     * @throws PersistenceException If there is an issue saving the adventure to the data store.
     */
    void saveAdventure(String adventureName) throws PersistenceException;

    /**
     * Initializes the list of encounters.
     *
     * @param numEncounters The number of encounters to initialize.
     */
    void initializeEncounters(int numEncounters);

    /**
     * Extracts the name of the monster from the given string.
     *
     * @param monsterName The name of the monster in a specific format.
     * @return The name of the monster.
     * @throws IllegalStateException If the monster name is not in the correct format.
     */
    String splitMonsterName(String monsterName);

    /**
     * Formats the given name into a title case.
     *
     * @param s The name to be formatted.
     * @return The formatted name in title case.
     */
    String title(String s);

    /**
     * Resets the count of bosses in the encounter.
     */
    void resetBossesOnEncounter();

    /**
     * Retrieves the monster names and their quantities.
     *
     * @param monsters The HashMap containing monster names and quantities.
     * @return List of monster names and quantities in formatted strings.
     */
    List<String> getMonsterAndQuantity(HashMap<String, Integer> monsters);

    /**
     * Checks if there are no monsters in a specific encounter.
     *
     * @param encounterIndex Index of the encounter to check.
     * @return true if there are no monsters in the encounter, false otherwise.
     */
    boolean nonMonstersInEncounter(int encounterIndex);
    boolean adventureExistsInAPI(String adventureName);
    void saveAdventureToAPI(String adventureName) throws ApiServerException;
}
