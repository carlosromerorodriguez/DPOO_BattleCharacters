package project.business;

import project.business.entities.battle.BattleEntity;
import project.business.entities.battle.character.BattleCharacter;
import project.business.entities.character.Character;
import project.business.exceptions.*;
import project.persistence.exceptions.ApiServerException;
import project.persistence.exceptions.PersistenceException;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * Interface for the BattleManager class. Provides methods for managing the battle.
 */
public interface BattleInterface {

    /**
     * Retrieves the names of all adventures.
     *
     * @return a list of Strings containing the names of all adventures
     * @throws PersistenceException if there is an issue with retrieving the data
     */
    List<String> getAllAdventuresNames() throws PersistenceException;

    /**
     * Sets the adventure for the BattleManager by the given index.
     *
     * @param whichAdventure the index of the adventure
     * @throws PersistenceException if there is an issue with retrieving the adventure data
     */
    void setAdventure(int whichAdventure) throws PersistenceException;

    /**
     * @return the name of the current adventure
     */
    String getAdventureName();

    /**
     * @return the number of characters in the current adventure
     */
    int getNumCharacters();

    /**
     * Retrieves character names within the adventure.
     *
     * @param numCharacters the number of characters to retrieve
     * @return a list of character names
     */
    List<String> getCharacterNames(int numCharacters);

    /**
     * Calculates the maximum number of characters that can participate in a battle.
     *
     * @param charactersSaved the number of characters saved
     * @return the maximum number of characters allowed in battle
     */
    int getMaxCharactersInBattle(int charactersSaved);

    /**
     * Adds a character to the current adventure.
     *
     * @param characterByID the character to be added
     * @throws RepeatedPartyCharacterException if the character already exists in the party
     */
    void addCharacter(Character characterByID) throws RepeatedPartyCharacterException;

    /**
     * @return the actual number of characters in the adventure
     */
    int getRealNumCharacters();

    /**
     * @return the number of encounters in the adventure
     */
    int getEncounters();

    /**
     * Retrieves the names of monsters in a specified encounter.
     *
     * @param encounterIndex the index of the encounter
     * @return a list of monster names in the encounter
     * @throws PersistenceException if there is an error retrieving the data
     */
    List<String> getMonsterNamesInEncounter(int encounterIndex) throws PersistenceException;

    /**
     * Retrieves a character from the adventure by index.
     *
     * @param index the index of the character
     * @return the BattleCharacter at the given index
     */
    BattleCharacter getCharacter(int index);

    /**
     * Calculates and retrieves the initiative order for characters and monsters in an encounter.
     *
     * @param encounter the index of the encounter
     * @return a list of names representing the initiative order
     * @throws PersistenceException if there is an error retrieving the data
     */
    List<String> getInitiativeOrder(int encounter) throws PersistenceException;

    /**
     * Prepares a character for battle.
     *
     * @param character the character to be prepared
     * @return a String message indicating the preparation status, or null if not an adventurer
     */
    String prepareCharacter(BattleCharacter character);

    /**
     * Retrieves the battle queue.
     *
     * @return a queue of BattleEntity representing the order of entities in battle
     */
    ArrayDeque<BattleEntity> getBattleQueue();

    /**
     * Manages the attack action of a BattleEntity in battle.
     *
     * @param poll the BattleEntity that will perform the attack
     * @return a String message describing the result of the attack
     * @throws FinishedBattleException if the battle is over
     * @throws NonAliveMonsterException if the monster is not alive
     */
    String manageAttack(BattleEntity poll) throws FinishedBattleException, NonAliveMonsterException;

    /**
     * Retrieves the names and hit points of all characters in the adventure.
     *
     * @return a list of Strings containing the names and hit points of characters
     */
    List<String> getCharacterNamesAndHitPoints();

    /**
     * Handles the condition of character and monster deaths during the battle.
     *
     * @param battleEntities the queue of BattleEntity participating in the battle
     * @param cont the current count of entities
     * @param round the current round of the battle
     * @return an array of two integers representing the updated count and round
     * @throws NonAliveMonsterException if all monsters have died
     * @throws FinishedBattleException if all characters have died
     */
    int[] handleCharacterAndMonstersDies(Deque<BattleEntity> battleEntities, int cont, int round) throws NonAliveMonsterException, FinishedBattleException;

    /**
     * Checks if the provided BattleEntity is alive and increments the counter if it is.
     *
     * @param cont the current count of entities
     * @param poll the BattleEntity to check
     * @return the updated count of entities
     */
    int checkIfIsAlive(int cont, BattleEntity poll);

    /**
     * Retrieves the number of BattleEntities that are still alive.
     *
     * @return the number of living entities
     */
    int getAliveEntities();

    /**
     * Manages the condition of non-alive monsters during an encounter.
     *
     * @param encounter the current encounter index
     * @throws FinishedBattleException if it is the last monster in the last encounter
     * @throws ContinueAdventureException if there are more encounters remaining
     */
    void manageNonAliveMonsters(int encounter) throws FinishedBattleException, ContinueAdventureException;

    /**
     * Checks if the given encounter is the last encounter.
     *
     * @param encounter the current encounter index
     * @return true if it is the last encounter, false otherwise
     */
    boolean isLastMonsterInEncounter(int encounter);

    /**
     * Retrieves the XP gained by each character.
     *
     * @return a list of Strings representing the XP gained by each character
     */
    List<String> getXPGainedForEveryCharacter();

    /**
     * Retrieves the names and rest abilities of all characters in the adventure.
     *
     * @return a list of Strings containing the names and rest abilities of characters
     */
    List<String> getCharacterNamesAndRestAbilities();

    /**
     * Updates the XP of the characters.
     *
     * @throws PersistenceException if there is an issue with persisting the data
     */
    void updateCharactersXP() throws PersistenceException;

    List<String> getAllAdventuresFromAPI() throws PersistenceException;

    void setAdventureFromApi(int adventureIndex);

    boolean validateCharacterAvailabilityFromAPI();

    List<String> getMonstersInEncounterFromAPI(int encounterIndex);
    void updateCharactersXPinAPI() throws ApiServerException;
}
