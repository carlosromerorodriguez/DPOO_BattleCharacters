package project.persistence.api;

import project.business.entities.adventure.Adventure;
import project.business.entities.battle.character.BattleCharacter;
import project.business.entities.character.Character;
import project.business.entities.monster.Monster;
import project.persistence.exceptions.ApiServerException;

import java.util.LinkedHashMap;
import java.util.List;

public interface ApiConnection {
    boolean checkIfApiServerIsUp();
    void createCharacter(Character character);
    List<Character> getCharactersFromApi();
    List<Character> getCharacterByPlayerFromApi(String name);
    void deleteCharacterFromApi(int position);
    void saveAdventureToApi(List<LinkedHashMap<String, Integer>> encounters, String adventureName) throws ApiServerException;
    Adventure getAdventureByID(int adventureIndex);
    Character getCharacterByID(int whichCharacter);
    List<String> getMonsterNamesInEncounter(int encounterIndex, String adventureName);
    void addCharacterToApi(Character updatedCharacter);
    List<Adventure> getAdventuresFromApi();
    List<Monster> getMonstersFromApi();
    void deleteAllCharacters() throws ApiServerException;
}
