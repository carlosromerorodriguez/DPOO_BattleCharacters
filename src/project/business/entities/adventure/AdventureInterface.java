package project.business.entities.adventure;

import project.business.entities.battle.BattleEntity;
import project.business.entities.battle.character.*;
import project.business.entities.battle.monster.Boss;
import project.business.entities.battle.monster.Lieutenant;
import project.business.entities.battle.monster.Minion;

import java.util.List;

/**
 * Represents an adventure interface that is used to manage the battle system.
 * This interface is implemented by the Adventure class.
 */
public interface AdventureInterface {

    /**
     * Returns the name of the adventure.
     *
     * @return The name of the adventure.
     */
    String getName();

    /**
     * Returns the number of encounters in the adventure.
     *
     * @return The number of encounters.
     */
    int getNumberOfEncounters();

    /**
     * Manages the attack action of an Adventurer against a monster.
     *
     * @param adventurer The Adventurer making the attack.
     * @param monsterToAttack The monster being attacked.
     * @return A string representing the result of the attack.
     */
    String manageAdventurerAttack(Adventurer adventurer, BattleEntity monsterToAttack);

    /**
     * Manages the attack action of a Minion against an adventurer.
     *
     * @param minion The Minion making the attack.
     * @param adventurerToAttack The adventurer being attacked.
     * @return A string representing the result of the attack.
     */
    String manageMinionAttack(Minion minion, BattleEntity adventurerToAttack);

    /**
     * Manages the attack action of a Lieutenant against an adventurer.
     *
     * @param lieutenant The Lieutenant making the attack.
     * @param adventurerToAttack The adventurer being attacked.
     * @return A string representing the result of the attack.
     */
    String manageLieutenantAttack(Lieutenant lieutenant, BattleEntity adventurerToAttack);

    /**
     * Manages the attack action of a Boss against an adventurer.
     *
     * @param boss The Boss making the attack.
     * @param adventurerToAttack The adventurer being attacked.
     * @return A string representing the result of the attack.
     */
    String manageBossAttack(Boss boss, BattleEntity adventurerToAttack);

    /**
     * Retrieves a list of character names along with their respective hit points.
     *
     * @return A list of strings with character names and their hit points.
     */
    List<String> getCharacterNamesAndHitPoints();

    /**
     * Calculates the number of living entities in the initiative order.
     *
     * @return The number of living entities.
     */
    int getAliveEntities();

    /**
     * Retrieves a list of experience points gained for every character.
     *
     * @return A list of strings representing the experience points gained by each character.
     */
    List<String> getXPGainedForEveryCharacter();

    /**
     * Retrieves a list of character names along with their rest abilities.
     *
     * @return A list of strings with character names and their rest abilities.
     */
    List<String> getCharacterNamesAndRestAbilities();

    String manageWarriorAttack(Warrior warrior, BattleEntity monsterToAttack);

    String manageChampionAttack(Champion champion, BattleEntity monsterToAttack);

    String manageClericAttack(Cleric cleric, BattleEntity monsterToAttack);

    String managePaladinAttack(Paladin paladin, BattleEntity monsterToAttack);

    String manageMageAttack(Mage mage, BattleEntity monsterToAttack);
}
