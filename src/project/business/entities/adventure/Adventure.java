package project.business.entities.adventure;

import project.business.entities.battle.BattleEntity;
import project.business.entities.battle.character.*;
import project.business.entities.battle.monster.BattleMonster;
import project.business.entities.battle.monster.Boss;
import project.business.entities.battle.monster.Lieutenant;
import project.business.entities.battle.monster.Minion;
import project.business.entities.character.Character;
import project.business.exceptions.RepeatedPartyCharacterException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents an adventure in a game where players can battle monsters.
 * <p>
 * The adventure is composed of several encounters where characters
 * battle monsters. Characters and monsters have initiative and hit points.
 * <p>
 * The class provides methods to add characters and monsters to the adventure,
 * to perform attacks, to get the state of the characters and monsters,
 * and to manage the initiative order.
 */
public class Adventure implements AdventureInterface {

    /**
     * The name of the adventure.
     */
    private final String name;

    /**
     * The number of encounters in the adventure.
     */
    private final int numberOfEncounters;

    /**
     * The list of monsters in the adventure.
     */
    private final List<BattleMonster> monsters;

    /**
     * The list of characters in the adventure.
     */
    private final List<BattleCharacter> characters;

    /**
     * The priority queue of entities in the adventure.
     */
    private final PriorityQueue<BattleEntity> initiativeOrder;

    /**
     * Constructs an Adventure with the specified name and number of encounters.
     *
     * @param name               The name of the Adventure.
     * @param numberOfEncounters The number of encounters in the Adventure.
     */
    public Adventure(String name, int numberOfEncounters) {
        this.name = name;
        this.numberOfEncounters = numberOfEncounters;
        this.monsters = new ArrayList<>();
        this.characters = new ArrayList<>();
        this.initiativeOrder = new PriorityQueue<>((entity1, entity2) -> entity2.getInitiative() - entity1.getInitiative());
    }

    /**
     * Inserts new monsters into the adventure.
     *
     * @param monsterName The name of the monster.
     * @param challenge   The challenge category of the monster (Boss, Lieutenant, or other).
     * @param quantity    The number of monsters to add.
     * @param encounter   The encounter number in which the monster will appear.
     */
    public void insertNewMonster(String monsterName, String challenge, int quantity, int encounter) {
        for (int i = 0; i < quantity; i++) {
            BattleMonster monster;
            switch (challenge) {
                case "Boss" -> monster = new Boss(monsterName, challenge, encounter);
                case "Lieutenant" -> monster = new Lieutenant(monsterName, challenge, encounter);
                default -> monster = new Minion(monsterName, challenge, encounter);
            }
            this.monsters.add(monster);
        }
    }

    /**
     * Adds a character to the adventure.
     *
     * @param character The character to be added.
     * @throws RepeatedPartyCharacterException if the character is already in the party.
     */
    public void addCharacter(BattleCharacter character) throws RepeatedPartyCharacterException {
        for (BattleCharacter battleCharacter : this.characters) {
            if (battleCharacter.getName().equals(character.getName())) {
                throw new RepeatedPartyCharacterException("Character " + character.getName() + " is already in the party!\n");
            }
        }
        this.characters.add(character);
    }

    /**
     * Returns the list of monsters in the adventure.
     *
     * @return The list of monsters.
     */
    public List<BattleMonster> getMonsters() {
        return monsters;
    }

    /**
     * Returns the name of the adventure.
     *
     * @return The name of the adventure.
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Returns the number of encounters in the adventure.
     *
     * @return The number of encounters.
     */
    @Override
    public int getNumberOfEncounters() {
        return this.numberOfEncounters;
    }

    /**
     * Manages the attack action of an Adventurer against a monster.
     *
     * @param adventurer The Adventurer making the attack.
     * @param monsterToAttack The monster being attacked.
     * @return A string representing the result of the attack.
     */
    @Override
    public String manageAdventurerAttack(Adventurer adventurer, BattleEntity monsterToAttack) {
        return adventurer.attack(monsterToAttack);
    }

    @Override
    public String manageWarriorAttack(Warrior warrior, BattleEntity monsterToAttack) {
        return warrior.attack(monsterToAttack);
    }

    @Override
    public String manageChampionAttack(Champion champion, BattleEntity monsterToAttack) {
        return champion.attack(monsterToAttack);
    }

    @Override
    public String manageClericAttack(Cleric cleric, BattleEntity monsterToAttack) {
        return cleric.attack(monsterToAttack);
    }

    @Override
    public String managePaladinAttack(Paladin paladin, BattleEntity monsterToAttack) {
        return paladin.attack(monsterToAttack);
    }

    @Override
    public String manageMageAttack(Mage mage, BattleEntity monsterToAttack) {
        return mage.attack(monsterToAttack);
    }

    /**
     * Manages the attack action of a Minion against a character.
     *
     * @param minion The Minion making the attack.
     * @param characterToAttack The character being attacked.
     * @return A string representing the result of the attack.
     */
    @Override
    public String manageMinionAttack(Minion minion, BattleEntity characterToAttack) {
        return minion.attack(characterToAttack);
    }

    /**
     * Manages the attack action of a Lieutenant against a character.
     *
     * @param lieutenant The Lieutenant making the attack.
     * @param characterToAttack The character being attacked.
     * @return A string representing the result of the attack.
     */
    @Override
    public String manageLieutenantAttack(Lieutenant lieutenant, BattleEntity characterToAttack) {
        return lieutenant.attack(characterToAttack);
    }

    /**
     * Manages the attack action of a Boss against a character.
     *
     * @param boss The Boss making the attack.
     * @param characterToAttack The character being attacked.
     * @return A string representing the result of the attack.
     */
    @Override
    public String manageBossAttack(Boss boss, BattleEntity characterToAttack) {
        return boss.attack(characterToAttack);
    }

    /**
     * Retrieves a list of character names along with their respective hit points.
     *
     * @return A list of strings with character names and their hit points.
     */
    @Override
    public List<String> getCharacterNamesAndHitPoints() {
        List<String> namesAndHitPoints = new ArrayList<>();

        int maxLength = 0;
        for (BattleCharacter character : this.characters) {
            maxLength = Math.max(maxLength, character.getName().length());
        }

        for (BattleCharacter character : this.characters) {
            int numSpaces = maxLength - character.getName().length();
            String spaces = new String(new char[numSpaces]).replace('\0', ' ');
            String shield = (character instanceof Mage mage) ? " (Shield: " + mage.getShield() + ")" : "";
            namesAndHitPoints.add("\t- " + character.getName() + spaces + "    " + character.getHitPointsAndMaxHitPoints() + " hit points" + shield);
        }

        return namesAndHitPoints;
    }

    /**
     * Calculates the number of living entities in the initiative order.
     *
     * @return The number of living entities.
     */
    @Override
    public int getAliveEntities() {
        int aliveEntities = 0;
        for (BattleEntity entity : this.initiativeOrder) {
            if (entity.isAlive()) {
                aliveEntities++;
            }
        }
        return aliveEntities;
    }

    /**
     * Retrieves a list of experience points gained for every character.
     *
     * @return A list of strings representing the experience points gained by each character.
     */
    @Override
    public List<String> getXPGainedForEveryCharacter() {
        List<String> xpGained = new ArrayList<>();

        int xpGainedTotal = calculateXPGainedForEachMonster();
        for (BattleCharacter character : this.characters) {
            xpGained.add(character.addExperiencePoints(xpGainedTotal));
        }
        return xpGained;
    }

    /**
     * Calculates the total experience points gained for each monster in the initiative
     * order.
     *
     * @return The total experience points gained for each monster.
     */
    private int calculateXPGainedForEachMonster() {
        int xpGainedTotal = 0;
        for (BattleEntity entity : this.initiativeOrder) {
            if (entity instanceof BattleMonster monster) {
                xpGainedTotal += monster.getExperiencePoints();
            }
        }
        return xpGainedTotal;
    }

    /**
     * Retrieves a list of character names along with their rest abilities.
     *
     * @return A list of strings with character names and their rest abilities.
     */
    @Override
    public List<String> getCharacterNamesAndRestAbilities() {
        List<String> namesAndBandageAbilities = new ArrayList<>();

        for (BattleCharacter character : this.characters) {
            if (character instanceof Champion champion) {
                namesAndBandageAbilities.add(champion.improvedBandageTime());
            } else if (character instanceof Warrior warrior) {
                namesAndBandageAbilities.add(warrior.bandageTime());
            } else if (character instanceof Mage mage) {
                namesAndBandageAbilities.add(mage.readABook());
            } else if (character instanceof Adventurer adventurer) {
                namesAndBandageAbilities.add(adventurer.bandageTime());
            } else if (character instanceof Paladin paladin) {
                namesAndBandageAbilities.add(paladin.prayerOfMassHealing());
            } else if (character instanceof Cleric cleric) {
                namesAndBandageAbilities.add(cleric.prayerOfSelfHealing());
            }
        }

        return namesAndBandageAbilities;
    }

    /**
     * Returns the number of characters in the adventure.
     *
     * @return The number of characters.
     */
    public int getNumberOfCharacters() {
        return this.characters.size();
    }

    /**
     * Returns the number of characters in the adventure.
     *
     * @return The number of characters.
     */
    public int getRealSize() {
        return this.characters.size();
    }

    /**
     * Returns the names of characters in the adventure.
     *
     * @param numCharacters The number of characters to retrieve.
     * @return The names of characters in the adventure.
     */
    public List<String> getCharacterNames(int numCharacters) {
        List<String> names = this.characters.stream()
                .map(BattleCharacter::getName)
                .collect(Collectors.toList());

        int remainingSlots = numCharacters - names.size();
        if (remainingSlots > 0) {
            names.addAll(Collections.nCopies(remainingSlots, "Empty"));
        }
        return names;
    }

    /**
     * Retrieves a character by its index.
     *
     * @param index The index of the character in the list.
     * @return The character at the specified index.
     */
    public BattleCharacter getCharacter(int index) {
        return this.characters.get(index);
    }

    /**
     * Retrieves the initiative order for a specific encounter.
     *
     * @param encounter The encounter number for which the initiative order is needed.
     * @return A list of formatted strings representing the initiative order.
     */
    public List<String> getInitiativeOrder(int encounter) {
        addMonstersToInitiativeOrder(encounter);
        if (encounter == 1) {
            addCharactersToInitiativeOrder();
        }
        return buildInitiativeList();
    }

    /**
     * Adds monsters to the initiative order for a specific encounter.
     *
     * @param encounter The encounter number for which the monsters are to be added to the initiative order.
     */
    private void addMonstersToInitiativeOrder(int encounter) {
        quitMonstersFromInitiativeOrder();
        for (BattleMonster monster : this.monsters) {
            if (monster.getEncounter() == encounter) {
                initiativeOrder.add(monster);
            }
        }
    }

    /**
     * Removes all monsters from the initiative order.
     */
    private void quitMonstersFromInitiativeOrder() {
        initiativeOrder.removeIf(entity -> entity instanceof BattleMonster);
    }

    /**
     * Adds characters to the initiative order.
     */
    private void addCharactersToInitiativeOrder() {
        initiativeOrder.addAll(this.characters);
    }

    /**
     * Builds a list of formatted strings representing the initiative order.
     *
     * @return A list of strings with formatted entries representing the initiative order.
     */
    private List<String> buildInitiativeList() {
        List<String> initiative = new ArrayList<>();
        List<BattleEntity> tempList = new ArrayList<>();

        while (!this.initiativeOrder.isEmpty()) {
            BattleEntity entity = this.initiativeOrder.poll();
            String formattedEntry = formatInitiativeEntry(entity);
            initiative.add(formattedEntry);
            tempList.add(entity);
        }

        this.initiativeOrder.addAll(tempList);

        return initiative;
    }

    /**
     * Formats a single entry for the initiative order.
     *
     * @param entity The battle entity to be formatted.
     * @return A formatted string representing the battle entity's initiative order entry.
     */
    private String formatInitiativeEntry(BattleEntity entity) {
        return String.format("\t- %-6d %s", entity.getInitiative(), entity.getName());
    }

    /**
     * Retrieves the monsters in a specific encounter.
     *
     * @param encounter The encounter number for which the monsters are needed.
     * @return A list of monsters in the specified encounter.
     */
    public List<BattleMonster> getMonstersInEncounter(int encounter) {
        List<BattleMonster> monstersInEncounter = new ArrayList<>();
        for (BattleMonster monster : this.monsters) {
            if (monster.getEncounter() == encounter) {
                monstersInEncounter.add(monster);
            }
        }
        return monstersInEncounter;
    }

    /**
     * Retrieves the characters in the adventure.
     *
     * @return A list of characters in the adventure.
     */
    public List<BattleCharacter> getCharacters() {
        return characters;
    }

    /**
     * Rolls the initiative for an Adventurer character.
     *
     * @param adventurer The adventurer character to roll initiative for.
     */
    public void rollAdventurerInitiative(Adventurer adventurer) {
        adventurer.rollInitiative();
    }

    /**
     * Rolls the initiative for the Warrior character.
     *
     * @param warrior The warrior character to roll initiative for.
     */
    public void rollWarriorInitiative(Warrior warrior) {
        warrior.rollInitiative();
    }

    /**
     * Rolls the initiative for a Champion character.
     *
     * @param champion The warrior character to roll initiative for.
     */
    public void rollChampionInitiative(Champion champion) {
        champion.rollInitiative();
    }

    /**
     * Rolls the initiative for the Cleric character.
     *
     * @param cleric The adventurer character to roll initiative for.
     */
    public void rollClericInitiative(Cleric cleric) {
        cleric.rollInitiative();
    }

    /**
     * Rolls the initiative for the Paladin character.
     *
     * @param paladin The paladin character to roll initiative for.
     */
    public void rollPaladinInitiative(Paladin paladin) {
        paladin.rollInitiative();
    }

    /**
     * Rolls the initiative for the Mage character.
     *
     * @param mage The mage character to roll initiative for.
     */
    public void rollMageInitiative(Mage mage) {
        mage.rollInitiative();
    }

    /**
     * Rolls the initiative for a Monster and sets its attributes.
     *
     * @param battleMonster The monster to roll initiative for.
     * @param damageDice    The damage dice of the monster.
     * @param initiative    The initiative value of the monster.
     * @param damageType    The type of damage the monster deals.
     * @param hitPoints     The hit points of the monster.
     * @param experiencePoints The experience points of the monster.
     */
    public void rollMonsterInitiative(BattleMonster battleMonster, int damageDice, int initiative, String damageType, int hitPoints, int experiencePoints) {
        battleMonster.rollInitiative(initiative, damageDice);
        battleMonster.setDamageDice(damageDice);
        battleMonster.setDamageType(damageType);
        battleMonster.setHitPoints(hitPoints);
        battleMonster.setExperiencePoints(experiencePoints);
    }

    /**
     * Retrieves the battle queue.
     *
     * @return The queue of battle entities sorted by initiative.
     */
    public PriorityQueue<BattleEntity> getBattleQueue() {
        return this.initiativeOrder;
    }

    /**
     * Checks if all monsters have died.
     *
     * @param battleEntities The queue of battle entities to check.
     * @return True if all monsters are dead, false otherwise.
     */
    public boolean allMonstersDied(Deque<BattleEntity> battleEntities) {
        for (BattleEntity entity : battleEntities) {
            if (entity instanceof BattleMonster battleMonster) {
                if (battleMonster.getHitPoints() > 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks if all characters have died.
     *
     * @param battleEntities The queue of battle entities to check.
     * @return True if all characters are dead, false otherwise.
     */
    public boolean allCharactersDied(Deque<BattleEntity> battleEntities) {
        for (BattleEntity entity : battleEntities) {
            if (entity instanceof BattleCharacter battleCharacter) {
                if (battleCharacter.getHitPoints() > 0) {
                    return false;
                }
            }
        }
        return true;
    }
}
