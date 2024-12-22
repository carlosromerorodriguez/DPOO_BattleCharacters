package project.business;

import project.business.entities.monster.Monster;
import project.persistence.api.ApiConnection;
import project.persistence.api.ApiConnectionDAO;
import project.persistence.exceptions.PersistenceException;
import project.persistence.monsters.JSONMonsterDAO;
import project.persistence.monsters.MonsterDAO;

import java.util.ArrayList;
import java.util.List;

/**
 * Class MonsterManager is the business layer for the Monster entity
 */
public class MonsterManager implements MonsterInterface {
    private final MonsterDAO monsterDAO;
    private final ApiConnection apiDAO;

    /**
     * Constructor for MonsterManager class that receives a JSONMonsterDAO object
     *
     * @param monsterDAO JSONMonsterDAO object that will be used to access the data layer
     * @param apiDAO     ApiConnectionDAO object that will be used to access the api layer
     */
    public MonsterManager(MonsterDAO monsterDAO, ApiConnection apiDAO) {
        this.monsterDAO = monsterDAO;
        this.apiDAO = apiDAO;
    }

    /**
     * Method that checks if the monster file exists
     *
     * @return boolean value that indicates if the monster file exists
     */
    @Override
    public boolean checkIfMonsterFileExists() {
        return monsterDAO.checkIfMonsterFileExists();
    }

    /**
     * Method that gets a list of strings with the name and challenge rating of all monsters
     *
     * @return list of strings with the name and challenge rating of all monsters
     * @throws PersistenceException if there is an error accessing the data layer
     */
    @Override
    public List<String> listAllMonsters() throws PersistenceException {
        return monsterDAO.getAllMonsterNameAndChallenge();
    }

    /**
     * Method that gets the name of a monster in the monster file given its position
     *
     * @return name of the monster in the monster file given its position
     * @throws PersistenceException if there is an error accessing the data layer
     */
    @Override
    public String getMonsterName(int which) throws PersistenceException {
        return this.listAllMonsters().get(which).split(" ")[0];
    }

    /**
     * Method that gets the monster information in the monster file given its position
     *
     * @return monster information in the monster file given its position
     * @throws PersistenceException if there is an error accessing the data layer
     */
    @Override
    public String getMonsterEntity(int which) throws PersistenceException {
        return this.listAllMonsters().get(which);
    }

    @Override
    public boolean checkIfApiServerIsUp() {
        return apiDAO.checkIfApiServerIsUp();
    }

    @Override
    public boolean verifyResponse(int response) {
        return (response == 2);
    }

    @Override
    public String manageStatus(boolean monsterFileStatus) {
        String status = "\nLoading data...\n";
        if (monsterFileStatus) {
            return status + "Data was successfully loaded.";
        } else {
            return status + "Error: The monsters.json file can’t be accessed.";
        }
    }

    @Override
    public List<String> getMonsterFromAPI() {
        List<Monster> monsters = apiDAO.getMonstersFromApi();
        List<String> monstersString = new ArrayList<>();
        for (Monster m : monsters) {
            monstersString.add(m.name() + " (" + m.challenge() + ")");
        }
        return monstersString;
    }
}
