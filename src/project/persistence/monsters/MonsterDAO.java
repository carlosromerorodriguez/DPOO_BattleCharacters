package project.persistence.monsters;

import project.business.entities.monster.Monster;
import project.persistence.exceptions.PersistenceException;

import java.util.List;

/**
 * Interface for the MonsterDAO.
 */
public interface MonsterDAO {
    /**
     * Method that checks if the monsters.json file exists
     *
     * @return true if the file exists, false otherwise
     */
    boolean checkIfMonsterFileExists();

    /**
     * Method that gets all the monsters from the monsters.json file
     *
     * @return a list of all the monster names and challenges
     * @throws PersistenceException if the file couldn't be opened
     */
    List<String> getAllMonsterNameAndChallenge() throws PersistenceException;

    /**
     * Method that gets all the monsters from the monsters.json file
     *
     * @return a list of all the monsters
     * @throws PersistenceException if the file couldn't be opened
     */
    List<Monster> getAllMonsters() throws PersistenceException;
}
