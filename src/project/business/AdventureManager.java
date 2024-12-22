package project.business;

import project.business.entities.adventure.Adventure;
import project.business.exceptions.MaxOneBossException;
import project.persistence.adventures.AdventureDAO;
import project.persistence.adventures.JSONAdventureDAO;
import project.persistence.api.ApiConnection;
import project.persistence.api.ApiConnectionDAO;
import project.persistence.exceptions.ApiServerException;
import project.persistence.exceptions.PersistenceException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Manages adventures consisting of encounters with monsters.
 */
public class AdventureManager implements AdventureInterface {
    private final AdventureDAO adventureDAO;
    private final ApiConnection apiDAO;
    private final List<LinkedHashMap<String, Integer>> encounters;
    private int bossesInEncounter;
    private final Pattern pattern;

    /**
     * Constructs an AdventureManager with the given DAO.
     *
     * @param adventureDAO Data Access Object for adventures.
     */
    public AdventureManager(AdventureDAO adventureDAO, ApiConnection apiDAO) {
        this.adventureDAO = adventureDAO;
        this.apiDAO = apiDAO;
        this.encounters = new ArrayList<>();
        this.bossesInEncounter = 0;
        this.pattern = Pattern.compile("^(.*)\\s\\(([^)]+)\\)$");
    }

    /**
     * Initializes the list of encounters.
     *
     * @param numberOfEncounters The number of encounters to initialize.
     */
    @Override
    public void initializeEncounters(int numberOfEncounters) {
        for (int i = 0; i < numberOfEncounters; i++) {
            encounters.add(new LinkedHashMap<>());
        }
    }

    /**
     * Extracts the name of the monster from the given string.
     *
     * @param monsterName The name of the monster in a specific format.
     * @return The name of the monster.
     * @throws IllegalStateException If the monster name is not in the correct format.
     */
    @Override
    public String splitMonsterName(String monsterName) throws IllegalStateException {
        Matcher matcher = pattern.matcher(monsterName);
        if (matcher.matches()) {
            return matcher.group(1).trim();
        } else {
            throw new IllegalStateException("Monster name is not in the correct format.");
        }
    }

    /**
     * Formats the given name into a title case.
     *
     * @param name The name to be formatted.
     * @return The formatted name in title case.
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
     * Resets the count of bosses in the encounter.
     */
    @Override
    public void resetBossesOnEncounter() {
        this.bossesInEncounter = 0;
    }

    /**
     * Retrieves the monster names and their quantities.
     *
     * @param monsters The HashMap containing monster names and quantities.
     * @return List of monster names and quantities in formatted strings.
     */
    @Override
    public List<String> getMonsterAndQuantity(HashMap<String, Integer> monsters) {
        List<String> result = new ArrayList<>();
        if (monsters.size() == 0) {
            result.add("\t# Empty");
        } else {
            int j = 1;
            for (String monsterName : monsters.keySet()) {
                result.add("\t" + j + ". " + this.splitMonsterName(monsterName) + " (x" + monsters.get(monsterName) + ")");
                j++;
            }
        }
        return result;
    }

    /**
     * Checks if there are no monsters in a specific encounter.
     *
     * @param encounterIndex Index of the encounter to check.
     * @return true if there are no monsters in the encounter, false otherwise.
     */
    @Override
    public boolean nonMonstersInEncounter(int encounterIndex) {
        return (this.getMonstersInEncounter(encounterIndex).size() == 0);
    }

    @Override
    public boolean adventureExistsInAPI(String adventureName) {
        List<Adventure> adventures = apiDAO.getAdventuresFromApi();
        return adventures.stream().anyMatch(adventure -> adventure.getName().equals(adventureName));
    }

    @Override
    public void saveAdventureToAPI(String adventureName) throws ApiServerException {
        apiDAO.saveAdventureToApi(encounters, adventureName);
    }

    /**
     * Checks if an adventure with the given name already exists.
     *
     * @param adventureName The name of the adventure.
     * @return true if the adventure already exists, false otherwise.
     * @throws PersistenceException If there is an issue accessing the data store.
     */
    @Override
    public boolean adventureExists(String adventureName) throws PersistenceException {
        return adventureDAO.adventureAlreadyExists(adventureName);
    }

    /**
     * Retrieves the monsters in a specific encounter.
     *
     * @param encounterIndex Index of the encounter.
     * @return HashMap of monster names and their quantities.
     */
    @Override
    public HashMap<String, Integer> getMonstersInEncounter(int encounterIndex) {
        return encounters.get(encounterIndex);
    }

    /**
     * Adds a monster to a specific encounter.
     *
     * @param monsterName     Name of the monster to add.
     * @param monsterQuantity Quantity of the monster.
     * @param encounterIndex  Index of the encounter.
     * @throws MaxOneBossException  If there is an attempt to add more than one boss to an encounter.
     * @throws IllegalStateException If the monster name format is invalid.
     */
    @Override
    public void addMonsterToEncounter(String monsterName, int monsterQuantity, int encounterIndex) throws MaxOneBossException, IllegalStateException {
        Matcher matcher = pattern.matcher(monsterName);
        if (matcher.matches()) {
            String challenge = matcher.group(2).trim();
            checkIfIsBoss(challenge, monsterQuantity);
        } else {
            throw new IllegalStateException("[ERROR] Invalid monster format name: " + monsterName);
        }

        if (this.encounters.get(encounterIndex).containsKey(monsterName)) {
            this.encounters.get(encounterIndex).put(monsterName, this.encounters.get(encounterIndex).get(monsterName) + monsterQuantity);
        } else {
            this.encounters.get(encounterIndex).put(monsterName, monsterQuantity);
        }
    }

    /**
     * Checks if the monster is a boss and updates the count of bosses.
     *
     * @param monsterChallenge The challenge rating of the monster.
     * @param monsterQuantity  The quantity of the monster.
     * @throws MaxOneBossException If there is an attempt to add more than one boss to an encounter.
     */
    private void checkIfIsBoss(String monsterChallenge, int monsterQuantity) throws MaxOneBossException {
        if (monsterChallenge.equalsIgnoreCase("Boss")) {
            bossesInEncounter++;
            if (monsterQuantity > 1 || bossesInEncounter > 1) {
                bossesInEncounter--;
                throw new MaxOneBossException("[ERROR] You can't have more than one boss in an encounter");
            }
        }
    }

    /**
     * Removes a monster from a specific encounter.
     *
     * @param deleteIndex     Index of the monster to delete.
     * @param encounterIndex  Index of the encounter.
     */
    @Override
    public void removeMonsterFromEncounter(int deleteIndex, int encounterIndex) {
        int i = 0;
        for (String monsterName : encounters.get(encounterIndex).keySet()) {
            if (i == deleteIndex) {
                encounters.get(encounterIndex).remove(monsterName);
                if (monsterName.endsWith("(Boss)")) {
                    bossesInEncounter--;
                }
                break;
            }
            i++;
        }
    }


    /**
     * Saves the current state of encounters to an adventure with the given name.
     *
     * @param adventureName Name of the adventure to save.
     * @throws PersistenceException If there is an issue saving the adventure to the data store.
     */
    @Override
    public void saveAdventure(String adventureName) throws PersistenceException {
        adventureDAO.saveAdventure(encounters, adventureName);
    }
}
