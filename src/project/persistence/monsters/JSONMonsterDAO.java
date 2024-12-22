package project.persistence.monsters;

import org.json.JSONArray;
import org.json.JSONObject;
import project.business.entities.monster.Monster;
import project.persistence.exceptions.PersistenceException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Class that implements the MonsterDAO interface
 */
public class JSONMonsterDAO implements MonsterDAO {
    /**
     * Path to the monsters.json file
     */
    private final String MONSTERS_PATH;

    /**
     * Constructor for the JSONMonsterDAO class that sets the path to the monsters.json file
     */
    public JSONMonsterDAO() {
        this.MONSTERS_PATH = "data/monsters.json";
    }

    /**
     * Method that checks if the monsters.json file exists
     *
     * @return true if the file exists, false otherwise
     */
    @Override
    public boolean checkIfMonsterFileExists() {
        return new File(MONSTERS_PATH).exists();
    }

    /**
     * Method that gets all the monsters from the monsters.json file
     *
     * @return a list of all the monster names and challenges
     * @throws PersistenceException if the file couldn't be opened
     */
    @Override
    public List<String> getAllMonsterNameAndChallenge() throws PersistenceException {
        List<String> result = new ArrayList<>();
        try {
            String content = new String(Files.readAllBytes(Paths.get(MONSTERS_PATH)));
            JSONArray jsonArray = new JSONArray(content);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("name");
                String challenge = jsonObject.getString("challenge");
                result.add(name + " (" + challenge + ")");
            }
        } catch (IOException e) {
            throw new PersistenceException("Error: Couldn't open the Monster's file", e);
        }
        return result;
    }

    /**
     * Method that gets all the monsters from the monsters.json file
     *
     * @return a list of all the monsters
     * @throws PersistenceException if the file couldn't be opened
     */
    @Override
    public List<Monster> getAllMonsters() throws PersistenceException {
        try {
            JSONArray monstersJSON = new JSONArray(new String(Files.readAllBytes(Paths.get(MONSTERS_PATH))));
            return IntStream.range(0, monstersJSON.length())
                    .mapToObj(monstersJSON::getJSONObject)
                    .map(jsonObject -> new Monster(
                            jsonObject.getString("name"),
                            jsonObject.getString("challenge"),
                            jsonObject.getInt("experience"),
                            jsonObject.getInt("hitPoints"),
                            jsonObject.getInt("initiative"),
                            jsonObject.getString("damageDice"),
                            jsonObject.getString("damageType")))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new PersistenceException("Error: Getting all monsters", e);
        }
    }
}
