package project;

import project.business.*;
import project.persistence.adventures.AdventureDAO;
import project.persistence.adventures.JSONAdventureDAO;
import project.persistence.api.ApiConnection;
import project.persistence.api.ApiConnectionDAO;
import project.persistence.characters.CharacterDAO;
import project.persistence.characters.JSONCharacterDAO;
import project.persistence.exceptions.ApiServerException;
import project.persistence.exceptions.PersistenceException;
import project.persistence.monsters.JSONMonsterDAO;
import project.persistence.monsters.MonsterDAO;
import project.presentation.Controller;
import project.presentation.Menu;

/**
 * Main class of the project.
 * It creates the DAOs and the Managers and runs the program.
 */
public class Main {
    public static void main(String[] args) {
        Menu menu = new Menu();
        try {
            // Initialize the api connection to the server
            ApiConnection apiConnectionDAO = new ApiConnectionDAO();

            // Initialize the DAOs
            AdventureDAO adventureDAO = new JSONAdventureDAO();
            CharacterDAO characterDAO = new JSONCharacterDAO();
            MonsterDAO monsterDAO = new JSONMonsterDAO();

            // Initialize the managers
            AdventureInterface adventureManager = new AdventureManager(adventureDAO, apiConnectionDAO);
            CharacterInterface characterManager = new CharacterManager(characterDAO, apiConnectionDAO);
            MonsterInterface monsterManager = new MonsterManager(monsterDAO, apiConnectionDAO);
            BattleInterface battleManager = new BattleManager(adventureDAO, characterDAO, monsterDAO, apiConnectionDAO);

            // Initialize the controller and run the program
            Controller controller = new Controller(adventureManager, characterManager, monsterManager, battleManager, menu);
            controller.run();
        } catch (PersistenceException | ApiServerException e) {
            menu.showMessage(e.getMessage());
        }
    }
}
