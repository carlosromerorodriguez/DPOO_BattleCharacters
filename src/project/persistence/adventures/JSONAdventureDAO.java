package project.persistence.adventures;

import org.json.JSONArray;
import org.json.JSONObject;
import project.business.entities.adventure.Adventure;
import project.persistence.exceptions.PersistenceException;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class implements the AdventureDAO interface, using a JSON file as the persistence mechanism
 */
public class JSONAdventureDAO implements AdventureDAO {
    /**
     * The path to the JSON file
     */
    private final String ADVENTURES_PATH;
    /**
     * Constructor of the class that sets the path to the JSON file
     */
    public JSONAdventureDAO() {
        this.ADVENTURES_PATH = "data/adventures.json";
    }

    /**
     * Checks if an adventure with the given name already exists in the persistent storage.
     * This method reads adventures from a JSON file and checks if any adventure has the
     * same name as the provided input, ignoring case considerations.
     *
     * @param adventureName The name of the adventure to check for existence.
     * @return True if an adventure with the given name already exists, false otherwise.
     * @throws PersistenceException If an issue occurs while reading from the file.
     */
    @Override
    public boolean adventureAlreadyExists(String adventureName) throws PersistenceException {
        try {
            String content = new String(Files.readAllBytes(Paths.get(ADVENTURES_PATH)));
            JSONArray adventures = new JSONArray(content);

            for (int i = 0; i < adventures.length(); i++) {
                JSONObject adventureObject = adventures.getJSONObject(i);
                JSONObject adventure = adventureObject.getJSONObject("adventure");

                if (adventureName.equalsIgnoreCase(adventure.getString("name"))) {
                    return true;
                }
            }
        } catch (IOException e) {
            throw new PersistenceException("Error: Couldn't open the Adventure's file", e);
        }
        return false;
    }

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
    @Override
    public void saveAdventure(List<LinkedHashMap<String, Integer>> encounters, String adventureName) throws PersistenceException {
        JSONObject adventure = new JSONObject();
        adventure.put("name", adventureName);
        adventure.put("numberOfEncounters", encounters.size());
        JSONArray encountersArray = new JSONArray();

        int encounterNumber = 1;
        for (LinkedHashMap<String, Integer> encounter : encounters) {
            if (encounter != null && !encounter.isEmpty()) {
                JSONObject encounterObject = new JSONObject();
                encounterObject.put("number", encounterNumber);

                JSONArray monsterArray = new JSONArray();
                for (Map.Entry<String, Integer> monsterEntry : encounter.entrySet()) {
                    Pattern pattern = Pattern.compile("^(.*)\\s\\(([^)]+)\\)$");
                    Matcher matcher = pattern.matcher(monsterEntry.getKey());
                    if (matcher.matches()) {
                        JSONObject monsterObject = new JSONObject();
                        monsterObject.put("name", matcher.group(1).trim());
                        monsterObject.put("challenge", matcher.group(2).trim());
                        monsterObject.put("quantity", monsterEntry.getValue());
                        monsterArray.put(monsterObject);
                    }
                }
                encounterObject.put("monsters", monsterArray);
                encountersArray.put(encounterObject);
            }
            encounterNumber++;
        }
        adventure.put("encounters", encountersArray);

        JSONObject adventureWrapper = new JSONObject();
        adventureWrapper.put("adventure", adventure);
        JSONArray mainArray;
        try {
            String content = new String(Files.readAllBytes(Paths.get(ADVENTURES_PATH)));
            mainArray = new JSONArray(content);
        } catch (IOException e) {
            mainArray = new JSONArray();
        }
        mainArray.put(adventureWrapper);

        try (FileWriter file = new FileWriter(ADVENTURES_PATH)) {
            file.write(mainArray.toString());
        } catch (IOException e) {
            throw new PersistenceException("Error: Couldn't open the Adventure's file", e);
        }
    }

    /**
     * Retrieves the names of all adventures from the persistent storage.
     * This method reads adventures from a JSON file and extracts the names of
     * all adventures, returning them in a list.
     *
     * @return A list of adventure names.
     * @throws PersistenceException If an issue occurs while reading from the file.
     */
    @Override
    public List<String> getAllAdventuresNames() throws PersistenceException {
        List<String> adventureNames = new ArrayList<>();
        try {
            String content = new String(Files.readAllBytes(Paths.get(ADVENTURES_PATH)));
            JSONArray adventuresArray = new JSONArray(content);
            for (int i = 0; i < adventuresArray.length(); i++) {
                JSONObject adventureObject = adventuresArray.getJSONObject(i).getJSONObject("adventure");
                String adventureName = adventureObject.getString("name");
                adventureNames.add(adventureName);
            }
        } catch (Exception e) {
            throw new PersistenceException("Error: Couldn't open the Adventure's file", e);
        }
        return adventureNames;
    }

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
    @Override
    public Adventure getAdventureByID(int id) throws PersistenceException {
        Adventure adventure;
        try {
            String content = new String(Files.readAllBytes(Paths.get(ADVENTURES_PATH)));
            JSONArray adventuresArray = new JSONArray(content);

            JSONObject adventureObject = adventuresArray.getJSONObject(id).getJSONObject("adventure");
            String name = adventureObject.getString("name");
            int numberOfEncounters = adventureObject.getInt("numberOfEncounters");
            JSONArray encounters = adventureObject.getJSONArray("encounters");
            adventure = new Adventure(name, numberOfEncounters);

            for (int i = 0; i < encounters.length(); i++) {
                JSONObject encounter = encounters.getJSONObject(i);
                JSONArray monsters = encounter.getJSONArray("monsters");

                for (int j = 0; j < monsters.length(); j++) {
                    JSONObject monsterObject = monsters.getJSONObject(j);
                    String monsterName = monsterObject.getString("name");
                    String challenge = monsterObject.getString("challenge");
                    int quantity = monsterObject.getInt("quantity");
                    adventure.insertNewMonster(monsterName, challenge, quantity, encounter.getInt("number"));
                }
            }
        } catch (Exception e) {
            throw new PersistenceException("Error: Couldn't open the Adventure's file", e);
        }

        return adventure;
    }

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
    @Override
    public List<String> getMonsterNamesInEncounter(int encounterIndex, String adventureName) throws PersistenceException {
        List<String> monsterNames = new ArrayList<>();

        try {
            JSONArray adventuresArray = new JSONArray(new String(Files.readAllBytes(Paths.get(ADVENTURES_PATH))));
            JSONObject selectedAdventure = null;
            for (int i = 0; i < adventuresArray.length(); i++) {
                JSONObject adventureWrapper = adventuresArray.getJSONObject(i);
                JSONObject adventure = adventureWrapper.getJSONObject("adventure");
                if (adventure.getString("name").equals(adventureName)) {
                    selectedAdventure = adventure;
                    break;
                }
            }

            if (selectedAdventure != null) {
                JSONArray encountersArray = selectedAdventure.getJSONArray("encounters");
                if (encounterIndex > 0 && encounterIndex <= encountersArray.length()) {
                    JSONObject selectedEncounter = encountersArray.getJSONObject(encounterIndex - 1);
                    JSONArray monsterArray = selectedEncounter.getJSONArray("monsters");

                    for (int i = 0; i < monsterArray.length(); i++) {
                        JSONObject monster = monsterArray.getJSONObject(i);
                        String monsterName = monster.getString("name");
                        int quantity = monster.getInt("quantity");
                        monsterNames.add("\t- " + quantity + "x " + monsterName);
                    }
                }
            }
        } catch (IOException e) {
            throw new PersistenceException("Error: Couldn't open the Adventure's file", e);
        }
        return monsterNames;
    }
}
