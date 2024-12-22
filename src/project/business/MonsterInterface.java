package project.business;

import project.persistence.exceptions.PersistenceException;

import java.util.List;

/**
 * Interface for the Monster class
 */
public interface MonsterInterface {
    /**
     * Method that checks if the monster file exists
     *
     * @return boolean value that indicates if the monster file exists
     */
    boolean checkIfMonsterFileExists();

    /**
     * Method that gets a list of strings with the name and challenge rating of all monsters
     *
     * @return list of strings with the name and challenge rating of all monsters
     * @throws PersistenceException if there is an error accessing the data layer
     */
    List<String> listAllMonsters() throws PersistenceException;

    /**
     * Method that gets the name of a monster in the monster file given its position
     *
     * @return name of the monster in the monster file given its position
     * @throws PersistenceException if there is an error accessing the data layer
     */
    String getMonsterName(int which) throws PersistenceException;

    /**
     * Method that gets the monster information in the monster file given its position
     *
     * @return monster information in the monster file given its position
     * @throws PersistenceException if there is an error accessing the data layer
     */
    String getMonsterEntity(int which) throws PersistenceException;

    /**
     * Method that returns the connectivity status of the API server
     *
     * @return boolean value that indicates if the API server is up
ç     */
    boolean checkIfApiServerIsUp();

    /**
     * Method that returns the connectivity status of the API server
     *
     * @param response response code from the API server
     *
     * @return boolean value that indicates if the API server is up
     */
    boolean verifyResponse(int response);

    /**
     * Method that returns the connectivity status of the data layer
     *
     * @param monsterFileStatus boolean value that indicates if the monster file exists
     *
     * @return string with the connectivity status of the API server
     */
    String manageStatus(boolean monsterFileStatus);
    List<String> getMonsterFromAPI();
}
