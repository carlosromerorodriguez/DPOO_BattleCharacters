package project.persistence.characters;

import com.google.gson.GsonBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import project.business.entities.battle.character.BattleCharacter;
import project.business.entities.character.Character;
import project.persistence.exceptions.PersistenceException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Class that implements the CharacterDAO interface, using a JSON file as the persistence method
 */
public class JSONCharacterDAO implements CharacterDAO {
    /**
     * The path to the JSON file
     */
    private final String CHARACTERS_PATH;

    /**
     * Constructor of the class that sets the path to the JSON file
     */
    public JSONCharacterDAO() {
        this.CHARACTERS_PATH = "data/characters.json";
    }

    /**
     * Adds a new character to the persistent storage.
     * This method serializes the given character object and appends it to a JSON array
     * which is stored in a file. The characters are stored as JSON objects within this array.
     * The GsonBuilder is used to ensure that the JSON representation of the character is pretty printed.
     *
     * @param character The character object to be serialized and stored.
     * @throws PersistenceException If there is an issue reading from or writing to the file.
     * This exception wraps the underlying IOException, providing more context as to what
     * the operation was trying to achieve.
     */
    @Override
    public void createCharacter(Character character) throws PersistenceException {
        try {
            Path path = Paths.get(CHARACTERS_PATH);
            JSONArray root = new JSONArray(new String(Files.readAllBytes(path)));
            root.put(new JSONObject(new GsonBuilder().setPrettyPrinting().create().toJson(character)));
            Files.write(path, root.toString().getBytes());
        } catch (IOException e) {
            throw new PersistenceException("Error: Couldn't open the Character's file", e);
        }
    }

    /**
     * Retrieves a list of all characters from the persistent storage.
     * This method reads a JSON file, deserializes the character objects, and returns them
     * as a list.
     *
     * @return A list of Character objects.
     * @throws PersistenceException If an issue occurs while reading from the file.
     */
    @Override
    public List<Character> getAllCharacters() throws PersistenceException {
        try {
            JSONArray charactersJSON = new JSONArray(new String(Files.readAllBytes(Paths.get(CHARACTERS_PATH))));
            return IntStream.range(0, charactersJSON.length())
                    .mapToObj(charactersJSON::getJSONObject)
                    .map(jsonObject -> new Character(
                            jsonObject.getString("name"),
                            jsonObject.getString("player"),
                            jsonObject.getInt("xp"),
                            jsonObject.getInt("body"),
                            jsonObject.getInt("mind"),
                            jsonObject.getInt("spirit"),
                            jsonObject.getString("class")))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new PersistenceException("Error: Couldn't open the Character's file", e);
        }
    }

    /**
     * Stores a list of characters back to the persistent storage in JSON format.
     * This method serializes a list of character objects and saves them in a JSON array
     * in a file. The GsonBuilder is used for pretty-printing.
     *
     * @param characters The list of Character objects to be serialized and stored.
     * @throws PersistenceException If an issue occurs while writing to the file.
     */
    @Override
    public void reAddCharactersToJSON(List<Character> characters) throws PersistenceException {
        try {
            JSONArray root = new JSONArray();
            characters.forEach(character -> root.put(new JSONObject(new GsonBuilder().setPrettyPrinting().create().toJson(character))));
            Files.write(Paths.get(CHARACTERS_PATH), root.toString().getBytes());
        } catch (IOException e) {
            throw new PersistenceException("Error: Couldn't open the Character's file", e);
        }
    }

    /**
     * Updates the experience points (XP) of characters in the persistent storage.
     * This method reads characters from a JSON file, updates the XP of the characters
     * provided in the input list, and saves the changes back to the file.
     *
     * @param characters The list of BattleCharacter objects with updated XP to be saved.
     * @throws PersistenceException If an issue occurs while reading from or writing to the file.
     */
    @Override
    public void updateCharactersXP(List<BattleCharacter> characters) throws PersistenceException {
        try {
            Path path = Paths.get(CHARACTERS_PATH);
            JSONArray array = new JSONArray(new String(Files.readAllBytes(path)));

            for (BattleCharacter character : characters) {
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonObject = array.getJSONObject(i);
                    if (jsonObject.getString("name").equals(character.getName())) {
                        jsonObject.put("xp", character.getXP());
                        jsonObject.put("class", character.getCharacterType());
                        break;
                    }
                }
            }

            Files.write(path, array.toString().getBytes());
        } catch (IOException e) {
            throw new PersistenceException("Error: Couldn't open the Character's file", e);
        }
    }
}
