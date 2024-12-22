package project.business;

import project.business.entities.adventure.Adventure;
import project.business.entities.battle.BattleEntity;
import project.business.entities.battle.character.*;
import project.business.entities.battle.monster.BattleMonster;
import project.business.entities.battle.monster.Boss;
import project.business.entities.battle.monster.Lieutenant;
import project.business.entities.battle.monster.Minion;
import project.business.entities.character.Character;
import project.business.entities.monster.Monster;
import project.business.exceptions.ContinueAdventureException;
import project.business.exceptions.FinishedBattleException;
import project.business.exceptions.NonAliveMonsterException;
import project.business.exceptions.RepeatedPartyCharacterException;
import project.persistence.adventures.AdventureDAO;
import project.persistence.api.ApiConnection;
import project.persistence.characters.CharacterDAO;
import project.persistence.exceptions.PersistenceException;
import project.persistence.monsters.MonsterDAO;

import java.util.*;

/**
 * Manages battles within the game.
 */
public class BattleManager implements BattleInterface {
    /**
     * Private attributes including data access objects and the current adventure
     */
    private final AdventureDAO adventureDAO;
    private final CharacterDAO characterDAO;
    private final MonsterDAO monsterDAO;
    private final ApiConnection apiDAO;
    private Adventure adventure;

    /**
     * Constructs a BattleManager with specified data access objects.
     *
     * @param adventureDAO the adventure data access object
     * @param characterDAO the character data access object
     * @param monsterDAO   the monster data access object
     * @param apiDAO       the api data access object
     */
    public BattleManager(AdventureDAO adventureDAO, CharacterDAO characterDAO, MonsterDAO monsterDAO, ApiConnection apiDAO) {
        this.adventureDAO = adventureDAO;
        this.characterDAO = characterDAO;
        this.monsterDAO = monsterDAO;
        this.apiDAO = apiDAO;
    }

    /**
     * @return the name of the current adventure
     */
    @Override
    public String getAdventureName() {
        return this.adventure.getName();
    }

    /**
     * @return the number of characters in the current adventure
     */
    @Override
    public int getNumCharacters() {
        return this.adventure.getNumberOfCharacters();
    }

    /**
     * Retrieves character names within the adventure.
     *
     * @param numCharacters the number of characters to retrieve
     * @return a list of character names
     */
    @Override
    public List<String> getCharacterNames(int numCharacters) {
        return this.adventure.getCharacterNames(numCharacters);
    }

    /**
     * Calculates the maximum number of characters that can participate in a battle.
     *
     * @param charactersSaved the number of characters saved
     * @return the maximum number of characters allowed in battle
     */
    @Override
    public int getMaxCharactersInBattle(int charactersSaved) {
        return Math.min(charactersSaved, 5);
    }

    /**
     * Adds a character to the current adventure.
     *
     * @param characterByID the character to be added
     * @throws RepeatedPartyCharacterException if the character already exists in the party
     */
    @Override
    public void addCharacter(Character characterByID) throws RepeatedPartyCharacterException {
        switch (characterByID.clas()) {
            case "Adventurer" -> this.adventure.addCharacter(new Adventurer(characterByID.body(), characterByID.mind(), characterByID.spirit(), characterByID.name(), characterByID.xp()));
            case "Warrior" -> this.adventure.addCharacter(new Warrior(characterByID.body(), characterByID.mind(), characterByID.spirit(), characterByID.name(), characterByID.xp()));
            case "Champion" -> this.adventure.addCharacter(new Champion(characterByID.body(), characterByID.mind(), characterByID.spirit(), characterByID.name(), characterByID.xp()));
            case "Cleric" -> this.adventure.addCharacter(new Cleric(characterByID.body(), characterByID.mind(), characterByID.spirit(), characterByID.name(), characterByID.xp()));
            case "Paladin" -> this.adventure.addCharacter(new Paladin(characterByID.body(), characterByID.mind(), characterByID.spirit(), characterByID.name(), characterByID.xp()));
            case "Mage" -> this.adventure.addCharacter(new Mage(characterByID.body(), characterByID.mind(), characterByID.spirit(), characterByID.name(), characterByID.xp()));
        }
    }

    /**
     * @return the actual number of characters in the adventure
     */
    @Override
    public int getRealNumCharacters() {
        return this.adventure.getRealSize();
    }

    /**
     * @return the number of encounters in the adventure
     */
    @Override
    public int getEncounters() {
        return this.adventure.getNumberOfEncounters();
    }

    /**
     * Retrieves the names of monsters in a specified encounter.
     *
     * @param i the index of the encounter
     * @return a list of monster names in the encounter
     * @throws PersistenceException if there is an error retrieving the data
     */
    @Override
    public List<String> getMonsterNamesInEncounter(int i) throws PersistenceException {
        return adventureDAO.getMonsterNamesInEncounter(i, adventure.getName());
    }

    /**
     * Retrieves a character from the adventure by index.
     *
     * @param index the index of the character
     * @return the BattleCharacter at the given index
     */
    @Override
    public BattleCharacter getCharacter(int index) {
        return this.adventure.getCharacter(index);
    }

    /**
     * Calculates and retrieves the initiative order for characters and monsters in an encounter.
     *
     * @param encounter the index of the encounter
     * @return a list of names representing the initiative order
     * @throws PersistenceException if there is an error retrieving the data
     */
    @Override
    public List<String> getInitiativeOrder(int encounter) throws PersistenceException {
        setMonsterInitiative(encounter);
        setCharacterInitiative();
        return this.adventure.getInitiativeOrder(encounter);
    }

    /**
     * Sets initiative values for the characters in the adventure.
     */
    private void setCharacterInitiative() {
        List<BattleCharacter> characters = this.adventure.getCharacters();
        for (BattleCharacter character : characters) {
            if (character instanceof Champion champion) {
                this.adventure.rollChampionInitiative(champion);
            } else if (character instanceof Warrior warrior) {
                this.adventure.rollWarriorInitiative(warrior);
            } else if (character instanceof Mage mage) {
                this.adventure.rollMageInitiative(mage);
            } else if (character instanceof Adventurer adventurer) {
                this.adventure.rollAdventurerInitiative(adventurer);
            } else if (character instanceof Paladin paladin) {
                this.adventure.rollPaladinInitiative(paladin);
            } else if (character instanceof Cleric cleric) {
                this.adventure.rollClericInitiative(cleric);
            }
        }
    }

    /**
     * Sets initiative values for the monsters in the encounter.
     *
     * @param encounter the index of the encounter
     * @throws PersistenceException if there is an error retrieving the data
     */
    private void setMonsterInitiative(int encounter) throws PersistenceException {
        List<Monster> monsters = monsterDAO.getAllMonsters();
        List<BattleMonster> battleMonsters = this.adventure.getMonstersInEncounter(encounter);
        for (BattleMonster battleMonster : battleMonsters) {
            for (Monster monster : monsters) {
                if (battleMonster.getName().equals(monster.name())) {
                    this.adventure.rollMonsterInitiative(battleMonster, Integer.parseInt(monster.damageDice().substring(1)), monster.initiative(), monster.damageType(), monster.hitPoints(), monster.xp());
                }
            }
        }
    }

    /**
     * Prepares a character for battle.
     *
     * @param character the character to be prepared
     * @return a String message indicating the preparation status, or null if not an adventurer
     */
    @Override
    public String prepareCharacter(BattleCharacter character) {
        List<BattleCharacter> party = this.adventure.getCharacters();
        List<BattleMonster> monsters = this.adventure.getMonsters();

        if (character instanceof Champion champion) {
            champion.setParty(party);
            return (champion.makeSelfMotivationSpeech());
        } else if (character instanceof Warrior warrior) {
            return (warrior.makeSelfMotivationSpeech());
        } else if (character instanceof Mage mage) {
            mage.setMonsters(monsters);
            return (mage.mageShield());
        } else if (character instanceof Adventurer adventurer) {
            return (adventurer.makeSelfMotivationSpeech());
        } else if (character instanceof Paladin paladin) {
            paladin.setParty(party);
            return (paladin.prayerOfGoodLuck());
        } else if (character instanceof Cleric cleric) {
            cleric.setParty(party);
            return (cleric.prayerOfGoodLuck());
        }
        return null;
    }

    /**
     * Retrieves the battle queue.
     *
     * @return a queue of BattleEntity representing the order of entities in battle
     */
    @Override
    public ArrayDeque<BattleEntity> getBattleQueue() {
        return new ArrayDeque<>(this.adventure.getBattleQueue());
    }

    /**
     * Manages the attack action of a BattleEntity in battle.
     *
     * @param poll the BattleEntity that will perform the attack
     * @return a String message describing the result of the attack
     * @throws FinishedBattleException if the battle is over
     * @throws NonAliveMonsterException if the monster is not alive
     */
    @Override
    public String manageAttack(BattleEntity poll) throws FinishedBattleException, NonAliveMonsterException {
        if (poll.isAlive()) {
            if (poll instanceof BattleCharacter character) {
                switch (character) {
                    case Champion champion -> {
                        return this.adventure.manageChampionAttack(champion, getMonsterToAttack());
                    }
                    case Warrior warrior -> {
                        return this.adventure.manageWarriorAttack(warrior, getMonsterToAttack());
                    }
                    case Mage mage -> {
                        return this.adventure.manageMageAttack(mage, getMonsterToAttack());
                    }
                    case Adventurer adventurer -> {
                        return this.adventure.manageAdventurerAttack(adventurer, getMonsterToAttack());
                    }
                    case Paladin paladin -> {
                        return this.adventure.managePaladinAttack(paladin, getMonsterToAttack());
                    }
                    case Cleric cleric -> {
                        return this.adventure.manageClericAttack(cleric, getMonsterToAttack());
                    }
                    default -> {
                    }
                }
            } else if (poll instanceof BattleMonster monster) {
                switch (monster) {
                    case Boss boss -> {
                        return this.adventure.manageBossAttack(boss, getAdventurerToAttack());
                    }
                    case Lieutenant lieutenant -> {
                        return this.adventure.manageLieutenantAttack(lieutenant, getAdventurerToAttack());
                    }
                    case Minion minion -> {
                        return this.adventure.manageMinionAttack(minion, getAdventurerToAttack());
                    }
                    default -> {}
                }
            }
        }
        return "";
    }

    /**
     * Retrieves the names and hit points of all characters in the adventure.
     *
     * @return a list of Strings containing the names and hit points of characters
     */
    @Override
    public List<String> getCharacterNamesAndHitPoints() {
        return this.adventure.getCharacterNamesAndHitPoints();
    }

    /**
     * Handles the condition of character and monster deaths during the battle.
     *
     * @param battleEntities the queue of BattleEntity participating in the battle
     * @param cont the current count of entities
     * @param round the current round of the battle
     * @return an array of two integers representing the updated count and round
     * @throws NonAliveMonsterException if all monsters have died
     * @throws FinishedBattleException if all characters have died
     */
    @Override
    public int[] handleCharacterAndMonstersDies(Deque<BattleEntity> battleEntities, int cont, int round) throws NonAliveMonsterException, FinishedBattleException {
        if (allMonstersDied(battleEntities)) {
            throw new NonAliveMonsterException();
        } else if (allCharactersDied(battleEntities)) {
            throw new FinishedBattleException(this.getAdventureName());
        }
        cont = 0; round++;
        return new int[] {cont, round};
    }

    /**
     * Checks if all monsters have died.
     *
     * @param battleEntities the queue of BattleEntity participating in the battle
     * @return true if all monsters have died, false otherwise
     */
    private boolean allMonstersDied(Deque<BattleEntity> battleEntities) {
        return this.adventure.allMonstersDied(battleEntities);
    }

    /**
     * Checks if all characters have died.
     *
     * @param battleEntities the queue of BattleEntity participating in the battle
     * @return true if all characters have died, false otherwise
     */
    private boolean allCharactersDied(Deque<BattleEntity> battleEntities) {
        return this.adventure.allCharactersDied(battleEntities);
    }

    /**
     * Checks if the provided BattleEntity is alive and increments the counter if it is.
     *
     * @param cont the current count of entities
     * @param poll the BattleEntity to check
     * @return the updated count of entities
     */
    @Override
    public int checkIfIsAlive(int cont, BattleEntity poll) {
        assert poll != null;
        if (poll.isAlive()) {
            cont++;
        }
        return cont;
    }

    /**
     * Retrieves the number of BattleEntities that are still alive.
     *
     * @return the number of living entities
     */
    @Override
    public int getAliveEntities() {
        return this.adventure.getAliveEntities();
    }

    /**
     * Manages the condition of non-alive monsters during an encounter.
     *
     * @param encounter the current encounter index
     * @throws FinishedBattleException if it is the last monster in the last encounter
     * @throws ContinueAdventureException if there are more encounters remaining
     */
    @Override
    public void manageNonAliveMonsters(int encounter) throws FinishedBattleException, ContinueAdventureException {
        if (encounter == this.getEncounters()) {
            throw new FinishedBattleException(this.getAdventureName());
        } else {
            throw new ContinueAdventureException();
        }
    }

    /**
     * Checks if the given encounter is the last encounter.
     *
     * @param encounter the current encounter index
     * @return true if it is the last encounter, false otherwise
     */
    @Override
    public boolean isLastMonsterInEncounter(int encounter) {
        return (encounter == this.getEncounters());
    }

    /**
     * Retrieves the XP gained by each character.
     *
     * @return a list of Strings representing the XP gained by each character
     */
    @Override
    public List<String> getXPGainedForEveryCharacter() {
        return this.adventure.getXPGainedForEveryCharacter();
    }

    /**
     * Retrieves the names and rest abilities of all characters in the adventure.
     *
     * @return a list of Strings containing the names and rest abilities of characters
     */
    @Override
    public List<String> getCharacterNamesAndRestAbilities() {
        return this.adventure.getCharacterNamesAndRestAbilities();
    }

    /**
     * Updates the XP of the characters.
     *
     * @throws PersistenceException if there is an issue with persisting the data
     */
    @Override
    public void updateCharactersXP() throws PersistenceException {
        this.characterDAO.updateCharactersXP(this.adventure.getCharacters());
    }

    @Override
    public List<String> getAllAdventuresFromAPI() {
        List<Adventure> adventures = this.apiDAO.getAdventuresFromApi();
        List<String> names = new ArrayList<>();
        for (Adventure adventure : adventures) {
            names.add(adventure.getName());
        }
        return names;
    }

    @Override
    public boolean validateCharacterAvailabilityFromAPI() {
        return this.apiDAO.getCharactersFromApi().size() >= 3;
    }

    @Override
    public List<String> getMonstersInEncounterFromAPI(int encounterIndex) {
        return this.apiDAO.getMonsterNamesInEncounter(encounterIndex, this.adventure.getName());
    }

    @Override
    public void updateCharactersXPinAPI() {
        try {
            List<Character> charactersFromApi = this.apiDAO.getCharactersFromApi();

            for (int i = 0; i < this.adventure.getCharacters().size(); i++) {
                BattleCharacter battleCharacter = this.adventure.getCharacters().get(i);
                for (int j = 0; j < charactersFromApi.size(); j++) {
                    Character character = charactersFromApi.get(j);
                    if (battleCharacter.getName().equals(character.name())) {
                       charactersFromApi.set(j, new Character(character.name(), character.player(), battleCharacter.getXP(),
                               character.body(), character.mind(), character.spirit(), battleCharacter.getCharacterType()));
                    }
                }
            }

            this.apiDAO.deleteAllCharacters();

            // Update the characters in the API with the new XP values
            for (Character updatedCharacter : charactersFromApi) {
                this.apiDAO.addCharacterToApi(updatedCharacter);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error updating characters in API", e);
        }
    }

    /**
     * Retrieves a BattleEntity (Adventurer) that will be attacked.
     *
     * @return a BattleEntity representing the Adventurer to be attacked
     * @throws FinishedBattleException if there are no Adventurers left
     */
    private BattleEntity getAdventurerToAttack() throws FinishedBattleException {
        List<BattleCharacter> characters = this.adventure.getCharacters().stream()
                .filter(BattleCharacter::isAlive)
                .toList();

        if (characters.isEmpty()) {
            throw new FinishedBattleException();
        } else {
            return characters.get(Dice.valueBetween(0, characters.size() - 1));
        }
    }

    /**
     * Retrieves a BattleEntity (Monster) that will be attacked.
     *
     * @return a BattleEntity representing the Monster to be attacked
     * @throws NonAliveMonsterException if there are no Monsters left
     */
    private BattleEntity getMonsterToAttack() throws NonAliveMonsterException {
        List<BattleMonster> monsters = this.adventure.getMonsters().stream()
                .filter(BattleMonster::isAlive)
                .sorted(Comparator.comparingInt(BattleMonster::getHitPoints))
                .toList();

        if (monsters.isEmpty()) {
            throw new NonAliveMonsterException();
        } else {
            return monsters.get(0);
        }
    }

    /**
     * Retrieves the names of all adventures.
     *
     * @return a list of Strings containing the names of all adventures
     * @throws PersistenceException if there is an issue with retrieving the data
     */
    @Override
    public List<String> getAllAdventuresNames() throws PersistenceException {
        return adventureDAO.getAllAdventuresNames();
    }

    /**
     * Sets the adventure for the BattleManager by the given index.
     *
     * @param whichAdventure the index of the adventure
     * @throws PersistenceException if there is an issue with retrieving the adventure data
     */
    @Override
    public void setAdventure(int whichAdventure) throws PersistenceException {
        this.adventure = adventureDAO.getAdventureByID(whichAdventure);
    }

    @Override
    public void setAdventureFromApi(int adventureIndex) {
        this.adventure = this.apiDAO.getAdventureByID(adventureIndex);
    }
}