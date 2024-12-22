package project.presentation;

import project.business.*;
import project.business.entities.battle.BattleEntity;
import project.business.entities.character.Character;
import project.business.exceptions.*;
import project.persistence.exceptions.ApiServerException;
import project.persistence.exceptions.PersistenceException;

import java.util.*;
import java.util.stream.IntStream;

/**
 * This class is the controller of the application. It is responsible for managing the flow of the application.
 */
public class Controller {
    /**
     * The adventure manager. It is used to manage adventure actions.
     */
    private final AdventureInterface adventureManager;
    /**
     * The character manager. It is used to manage character actions.
     */
    private final CharacterInterface characterManager;
    /**
     * The monster manager. It is used to manage monster actions.
     */
    private final MonsterInterface monsterManager;
    /**
     * The battle manager. It is used to manage battle actions.
     */
    private final BattleInterface battleManager;

    /**
     * The API connection status. It is used to check if the API server is up.
     */
    private boolean apiConnection;

    /**
     * The menu. It is used to show the menu and ask for user input.
     */
    private final Menu menu;

    /**
     * Creates a new controller with the given managers and menu.
     *
     * @param adventureManager The adventure manager.
     * @param characterManager The character manager.
     * @param monsterManager The monster manager.
     * @param battleManager The battle manager.
     * @param menu The menu.
     */
    public Controller(AdventureInterface adventureManager, CharacterInterface characterManager, MonsterInterface monsterManager, BattleInterface battleManager, Menu menu) {
        this.adventureManager = adventureManager;
        this.characterManager = characterManager;
        this.monsterManager = monsterManager;
        this.battleManager = battleManager;
        this.menu = menu;
        this.apiConnection = false;
    }

    /**
     * Starts the application. It shows the welcome message, loads the data and shows the menu.
     *
     * @throws PersistenceException If there is an error while accessing the data files or the data is corrupted.
     */
    public void run() throws PersistenceException {
        int option;
        boolean monsterFileStatus = false;
        this.apiConnection  = monsterManager.verifyResponse(menu.showWelcome());

        if (this.apiConnection) {
            if (monsterManager.checkIfApiServerIsUp()) {
                menu.apiSuccessConnection();
            } else {
                menu.showApiFailedConnection();
                this.apiConnection = false;
                monsterFileStatus = checkMonsterFileStatus();
            }
        } else {
            monsterFileStatus = checkMonsterFileStatus();
        }

        do {
            menu.showMenu();
            option = menu.askForMenuOption();
            executeOption(option);
        } while ((apiConnection || monsterFileStatus) && (option != 5));
    }

    private boolean checkMonsterFileStatus() {
        boolean monsterFileStatus = monsterManager.checkIfMonsterFileExists();
        menu.showDataLoadingMsg(monsterManager.manageStatus(monsterFileStatus));
        return monsterFileStatus;
    }

    /**
     * Executes the option chosen by the user. It calls the corresponding method.
     *
     * @param option The option chosen by the user.
     *
     * @throws PersistenceException If there is an error while accessing the data files or the data is corrupted.
     */
    private void executeOption(int option) throws PersistenceException {
        switch (option) {
            case Menu.CHARACTER_CREATION -> createCharacter();
            case Menu.LIST_CHARACTER -> listAllCharacters();
            case Menu.CREATE_ADVENTURE -> createAdventure();
            case Menu.START_ADVENTURE -> startAdventure();
            default -> menu.showExitMsg();
        }
    }

    /**
     * Starts the battle. It shows the battle header, the combat stage and the end of the round message.
     * @param encounter The encounter number to manage the battle actions.
     *
     * @throws PersistenceException If there is an error while accessing the data files or the data is corrupted.
     * @throws FinishedBattleException If the battle is finished.
     * @throws ContinueAdventureException If the adventure is not finished.
     */
    private void startBattle(int encounter) throws FinishedBattleException, ContinueAdventureException, PersistenceException, ApiServerException {
        Deque<BattleEntity> battleEntities = battleManager.getBattleQueue();
        int cont = 0;
        int round = 1;
        menu.showBattleHeader();
        menu.combatStage(battleManager.getCharacterNamesAndHitPoints(), (round));

        while (true) {
            try {
                BattleEntity poll = battleEntities.pollFirst();
                menu.showAttackMsg(battleManager.manageAttack(poll));
                battleEntities.addLast(poll);
                cont = battleManager.checkIfIsAlive(cont, poll);

                if (battleManager.getAliveEntities() == cont) {
                    menu.showEndRoundMsg(round);
                    int[] res = battleManager.handleCharacterAndMonstersDies(battleEntities, cont, round);
                    cont = res[0]; round = res[1];

                    menu.combatStage(battleManager.getCharacterNamesAndHitPoints(), round);
                }
            } catch (NonAliveMonsterException e) {
                if (battleManager.isLastMonsterInEncounter(encounter)) {
                    this.makeRestStageActions();
                }
                battleManager.manageNonAliveMonsters(encounter);
            } catch (FinishedBattleException e) {
                throw new FinishedBattleException();
            }
        }
    }

    /**
     * Method that manages the rest stage actions. It shows the enemies defeated, the XP gained and the bandage abilities.
     *
     * @throws PersistenceException If there is an error while accessing the data files or the data is corrupted.
     */
    private void makeRestStageActions() throws PersistenceException, ApiServerException {
        menu.showEnemiesDefeated();
        menu.listAllXPGained(battleManager.getXPGainedForEveryCharacter());
        menu.listCharactersBandageAbilities(battleManager.getCharacterNamesAndRestAbilities());
        if (this.apiConnection) {
            battleManager.updateCharactersXPinAPI();
        } else {
            battleManager.updateCharactersXP();
        }
    }

    /**
     * Starts the encounters. It iterates through the encounters and handles the battle flow.
     *
     * @throws PersistenceException If there is an error while accessing the data files or if the data is corrupted.
     */
    private void startEncounters() throws PersistenceException, ApiServerException {
        for (int i = 0; i < battleManager.getEncounters(); i++) {
            try {
                menu.showStartingEncounterInfo((i + 1), (apiConnection) ? battleManager.getMonstersInEncounterFromAPI(i + 1) : battleManager.getMonsterNamesInEncounter(i + 1));
                menu.showPreparationPhase();
                IntStream.range(0, battleManager.getNumCharacters())
                        .forEach(j -> menu.showMessage(battleManager.prepareCharacter(battleManager.getCharacter(j))));
                menu.rollInitiative(battleManager.getInitiativeOrder(i + 1));
                startBattle((i + 1));
            } catch (FinishedBattleException | ApiServerException e) {
                menu.showMessage(e.getMessage());
                break;
            } catch (ContinueAdventureException e) {
                this.makeRestStageActions();
            }
        }

    }

    /**
     * Starts the adventure. It displays the welcome message, loads and selects an adventure,
     * validates the availability of characters, adds characters to the adventure, and starts the encounters.
     *
     * @throws PersistenceException If there is an error while accessing the data files or the data is corrupted.
     */
    private void startAdventure() throws PersistenceException {
        try {
            menu.welcomeToAdventureMsg();

            if (!loadAndSelectAdventure()) {
                menu.showMessage("[ERROR] There are no adventures available.");
                return;
            } if (!validateCharactersAvailability()) {
                menu.showMessage("[ERROR] There must be at least 3 characters to start an adventure.");
                return;
            }

            addCharactersToAdventure();
            startEncounters();
        } catch (ApiServerException e) {
            menu.showMessage(e.getMessage());
        }

    }

    /**
     * Loads and selects an adventure. It shows the empty adventures list message.
     *
     * @return {@code true} if the adventure is successfully loaded and selected, {@code false} otherwise.
     * @throws PersistenceException If there is an error while accessing the data files or the data is corrupted.
     */
    private boolean loadAndSelectAdventure() throws PersistenceException {
        List<String> battleNames = (apiConnection) ? battleManager.getAllAdventuresFromAPI() : battleManager.getAllAdventuresNames();

        if (battleNames.isEmpty()) {
            menu.showEmptyAdventuresList();
            return false;
        }

        if (apiConnection) {
            battleManager.setAdventureFromApi(menu.listAllAdventuresNames(battleNames));
        } else {
            battleManager.setAdventure(menu.listAllAdventuresNames(battleNames));
        }

        return true;
    }

    /**
     * Validates the availability of characters.
     *
     * @return {@code true} if there are enough characters available to start an adventure, {@code false} otherwise.
     * @throws PersistenceException If there is an error while accessing the data files or if the data is corrupted.
     */
    private boolean validateCharactersAvailability() throws PersistenceException {
        return (apiConnection) ? battleManager.validateCharacterAvailabilityFromAPI() : characterManager.validateCharactersAvailability();
    }

    /**
     * Method that adds the characters to the adventure. It shows the party info, the characters
     * to add, and the adventure start message.
     *
     * @throws PersistenceException If there is an error while accessing the data files or the data is corrupted.
     */
    private void addCharactersToAdventure() throws PersistenceException {
        int charactersSaved = (apiConnection) ? characterManager.listAllCharactersFromApi().size() : characterManager.listAllCharacters().size();
        int numCharacters = menu.askForBattleCharacters(battleManager.getMaxCharactersInBattle(charactersSaved), battleManager.getAdventureName());
        int cont = 1;

        while (battleManager.getNumCharacters() < numCharacters) {
            try {
                menu.showPartyInfo(battleManager.getCharacterNames(numCharacters), battleManager.getRealNumCharacters(), numCharacters);
                int whichCharacter = menu.showAllCharactersToAdd((apiConnection) ? characterManager.listAllCharactersFromApi() : characterManager.listAllCharacters(), cont);
                battleManager.addCharacter((apiConnection) ? characterManager.getCharacterFromApiByID(whichCharacter) : characterManager.getCharacterByID(whichCharacter));
                cont++;
            } catch (RepeatedPartyCharacterException | PersistenceException e) {
                menu.showMessage(e.getMessage());
            }
        }

        menu.showAdventureStartMsg(battleManager.getAdventureName());
    }

    /**
     * Method that manages the adventure creation. It shows the adventure header, the adventure info,
     * the number of encounters, the monsters in the encounter, and the encounter options.
     *
     * @throws PersistenceException If there is an error while accessing the data files or the data is corrupted.
     */
    private void createAdventure() throws PersistenceException {
        String adventureName = adventureManager.title(menu.askForAdventureName());
        boolean repeatedAdventureName;

        if (this.apiConnection) {
            repeatedAdventureName = adventureManager.adventureExistsInAPI(adventureName);
        } else {
            repeatedAdventureName = adventureManager.adventureExists(adventureName);
        }

        if (!repeatedAdventureName) {
            menu.showMessage("\nTavern keeper: “You plan to undertake " + adventureName + ", really?”\n“How long will that take?”\n");

            int numEncounters = menu.askForEncounterNumber();

            if (numEncounters > -1) {
                adventureManager.initializeEncounters(numEncounters);
                createEncounters(numEncounters, adventureName);
            }

            return;
        }
        menu.showMessage("[ERROR] An adventure with that name already exists.");
    }

    /**
     * Creates encounters for the adventure. It shows the encounter info, the monsters in the encounter, and the
     * encounter options. It also handles the encounter options.
     *
     * @param numEncounters The number of encounters to create.
     * @param adventureName The name of the adventure.
     * @throws PersistenceException If there is an error while accessing the data files or if the data is corrupted.
     */
    private void createEncounters(int numEncounters, String adventureName) throws PersistenceException {
        for (int i = 1; i <= numEncounters; i++) {
            menu.showEncounterInfo(i, numEncounters);
            displayMonstersInEncounter(i);
            i = handleEncounterOptions(i, numEncounters, adventureName);
        }
    }

    /**
     * Displays the monsters in the specified encounter.
     *
     * @param encounterIndex The index of the encounter.
     */
    private void displayMonstersInEncounter(int encounterIndex) {
        HashMap<String, Integer> monsters = adventureManager.getMonstersInEncounter(encounterIndex - 1);
        menu.listAllMonstersInEncounter(adventureManager.getMonsterAndQuantity(monsters));
    }

    /**
     *
     * Handles the encounter options for a given encounter.
     * This method displays a menu to create options for the encounter and performs the corresponding
     * actions based on the user's selection. The options include adding a monster to the encounter,
     * removing a monster from the encounter, or checking whether to continue the encounter.
     *
     * @param encounterIndex The index of the current encounter.
     * @param numEncounters The total number of encounters in the adventure.
     * @param adventureName The name of the adventure.
     *
     * @return The updated encounter index based on the user's selection.
     * @throws PersistenceException If there is an error while accessing the data files or if the data is corrupted.
     */
    private int handleEncounterOptions(int encounterIndex, int numEncounters, String adventureName) throws PersistenceException {
        return switch (menu.showMenuCreateAdventure()) {
            case 1 -> addMonsterToEncounter(encounterIndex);
            case 2 -> removeMonsterFromEncounter(encounterIndex);
            default -> checkContinueEncounter(encounterIndex, numEncounters, adventureName);
        };
    }

    /**
     * Checks whether to continue the current encounter or finish the adventure.
     * This method checks if there are monsters added to the current encounter. If no monsters are added,
     * it displays an error message and returns the current encounter index without advancing. If it is
     * the last encounter, it saves the adventure and displays a success message. Then, it resets the
     * bosses for the next encounter and returns the updated encounter index.
     *
     * @param encounterIndex The index of the current encounter.
     * @param numEncounters The total number of encounters in the adventure.
     * @param adventureName The name of the adventure.
     *
     * @return The updated encounter index based on the conditions.
     */
    private int checkContinueEncounter(int encounterIndex, int numEncounters, String adventureName) throws PersistenceException {
        try {
            if (adventureManager.getMonstersInEncounter(encounterIndex - 1).size() == 0) {
                menu.showMessage("[ERROR] You can't continue without adding at least one monster to the encounter.");

                return (encounterIndex - 1);
            } if (encounterIndex == numEncounters) {
                if (this.apiConnection) {
                    adventureManager.saveAdventureToAPI(adventureName);
                } else {
                    adventureManager.saveAdventure(adventureName);
                }
                menu.showMessage("\nThe new adventure " + adventureName + " has been created.");
            }
            adventureManager.resetBossesOnEncounter();
        } catch (ApiServerException e) {
            menu.showMessage("[ERROR] There was an error while saving the adventure to the API.");
        }

        return encounterIndex;
    }

    /**
     * Removes a monster from the specified encounter.
     * This method checks if there are any monsters in the encounter. If no monsters are found, it displays an error message
     * and returns the current encounter index without modifying it. If there are monsters, it prompts the user to select
     * a monster to delete and remove it from the encounter.
     *
     * @param encounterIndex The index of the encounter.
     *
     * @return The updated encounter index.
     */
    private int removeMonsterFromEncounter(int encounterIndex) {
        if (adventureManager.nonMonstersInEncounter(encounterIndex - 1)) {
            menu.showMessage("[ERROR] There are no monsters in this encounter.");
            return (encounterIndex - 1);
        }

        int deleteIndex = menu.askForInteger("-> Which monster do you want to delete? ", 1, adventureManager.getMonstersInEncounter(encounterIndex - 1).size());
        adventureManager.removeMonsterFromEncounter(deleteIndex - 1, encounterIndex - 1);
        return (encounterIndex - 1);
    }

    /**
     * Adds a monster to the specified encounter.
     * This method displays a list of available monsters and prompts the user to select a monster
     * to add. It then asks for the quantity of the selected monster to add to the encounter. The
     * monster is added to the encounter using the Adventure Manager. If a MaxOneBossException is thrown,
     * indicating that the maximum number of boss monsters is already reached, an error message is displayed.
     * If an IllegalStateException is thrown, indicating an incorrect monster file format, an error message
     * is displayed as well.
     *
     * @param encounterIndex The index of the encounter.
     *
     * @return The updated encounter index.
     * @throws PersistenceException If there is an error while accessing the data files or if the data is corrupted.
     */
    private int addMonsterToEncounter(int encounterIndex) throws PersistenceException {
        try {
            int which = menu.listAllMonsters((this.apiConnection ? monsterManager.getMonsterFromAPI() : monsterManager.listAllMonsters()));
            int quantity = menu.askForInteger("-> How many " + monsterManager.getMonsterName(which) + "(s) do you want to add? ", 1, Integer.MAX_VALUE);
            adventureManager.addMonsterToEncounter(monsterManager.getMonsterEntity(which), quantity, (encounterIndex - 1));
        } catch (MaxOneBossException e) {
            menu.showMessage(e.getMessage());
        } catch (IllegalStateException e) {
            menu.showMessage("[ERROR] Wrong monster file format.");
        }

        return (encounterIndex - 1);
    }

    /**
     * Lists all characters and allows interaction with a selected character.
     * This method displays an introduction and prompts the user to enter the name
     * of the player to filter the character list. It retrieves the filtered character
     * list based on the provided name and determines the position of the selected character.
     * If a valid character position is found, it proceeds to handle the interaction with the character.
     *
     * @throws PersistenceException If there is an error while accessing the data files or if the data is corrupted.
     */
    private void listAllCharacters() throws PersistenceException {
        showIntroduction();
        String name = menu.askForString("-> Enter the name of the Player to filter: ");
        List<Character> characterList = getCharacterListFilteredByName(name);
        int characterPos = getCharacterPosition(characterList);

        if (characterPos > 0) {
            handleCharacterInteraction(characterList, characterPos);
        }
    }

    /**
     * Shows an introduction message.
     * This method displays an introduction message from the tavern keeper to the characters.
     */
    private void showIntroduction() {
        menu.showMessage("\nTavern keeper: “Lads! They want to see you!”\n“Who piques your interest?”\n");
    }

    /**
     * Retrieves a filtered character list based on the provided name.
     * This method retrieves a character list filtered by the given name using the 'getCharacterList' method.
     * @param name The name to filter the character list by.
     *
     * @return The filtered character list.
     * @throws PersistenceException If there is an error while accessing the data files or if the data is corrupted.
     */
    private List<Character> getCharacterListFilteredByName(String name) throws PersistenceException {
        return getCharacterList(name);
    }

    /**
     * Gets the position of a character in the character list.
     * This method checks and retrieves the position of a character in the provided character list.
     *
     * @param characterList The character list.
     *
     * @return The position of the character in the list.
     */
    private int getCharacterPosition(List<Character> characterList) {
        return checkAndGetPosition(characterList, characterList.size());
    }

    /**
     * Handles the interaction with a selected character.
     * This method handles the interaction with a selected character from the character list.
     * It displays a greeting message to the character and shows the character's information.
     * It then prompts the user to input a name for deletion or cancellation. Based on the input,
     * it performs the corresponding actions such as deleting the character or canceling the interaction.
     *
     * @param characterList The list of characters.
     * @param characterPos The position of the selected character in the list.
     *
     * @throws PersistenceException If there is an error while accessing the data files or if the data is corrupted.
     */
    private void handleCharacterInteraction(List<Character> characterList, int characterPos) throws PersistenceException {
        Character c = characterList.get(characterPos - 1);
        String characterName = c.name();
        boolean end = false;

        menu.showGreeting(characterName);
        String characterNameToDelete = menu.showCharacterInfo(characterName, c.player(), c.clas(),
                                                              characterManager.convertXpToLvl(c.xp()), c.xp(),
                                                              characterManager.formatSignStats(c.body()),
                                                              characterManager.formatSignStats(c.mind()),
                                                              characterManager.formatSignStats(c.spirit()));
        while (!end) {
            if (characterManager.shouldDeleteCharacter(characterNameToDelete, characterName)) {
                menu.showCharacterLeavingMessage(characterName);
                if (this.apiConnection) {
                    characterManager.deleteCharacterFromApi(c);
                } else {
                    characterManager.deleteCharacter(c);
                }
                return;
            } else if (characterManager.nameIsEmpty(characterNameToDelete)) {
                menu.showMessage("\nTavern keeper: “I see you have changed your mind. Come back whenever you want.”");
                end = true;
            } else {
                menu.showCharacterNotFoundMessage();
                characterNameToDelete = menu.askForString("\n[Enter name to delete, or press enter to cancel]\nDo you want to delete " + characterName + "? ");
            }
        }
    }

    /**
     * Checks and retrieves the position of a character in the character list.
     * This method checks the size of the character list and displays an error message if the size is 0,
     * indicating that there is no player with the provided name. If the size is not 0, it displays the
     * character list and prompts the user to select a character to meet by their corresponding position.
     *
     * @param characterList The character list.
     * @param size The size of the character list.
     *
     * @return The position of the character in the list, or -1 if the size is 0.
     */
    private int checkAndGetPosition(List<Character> characterList, int size) {
        if (size == 0) {
            menu.showPlayerNameSearchingError("\n[ERROR] There isn't a player with that name!\n");
            return -1;
        }
        menu.showCharacterList(characterList);
        return menu.askForInteger("Who would you like to meet [0.." + size + "]: ", 0, size);
    }

    /**
     * Retrieves the character list based on the provided name.
     * This method retrieves the character list either by listing all characters or by listing characters filtered by name.
     * If the provided name is empty or blank, it returns the list of all characters. Otherwise, it returns the list of characters
     * filtered by the provided name (trimmed of leading and trailing space).
     * @param name The name used for filtering the character list.
     *
     * @return The character list based on the provided name.
     * @throws PersistenceException If there is an error while accessing the data files or if the data is corrupted.
     */
    private List<Character> getCharacterList(String name) throws PersistenceException {
        menu.showMessage("\nYou watch as all adventurers get up from their chairs and approach you.");
        if (name.isEmpty() || name.isBlank()) {
            if (this.apiConnection) {
                return characterManager.listAllCharactersFromApi();
            }
            return characterManager.listAllCharacters();
        } else {
            if (this.apiConnection) {
                return characterManager.listAllCharactersByNameFromApi(name.trim());
            }
            return characterManager.listAllCharactersByName(name.trim());
        }
    }

    /**
     * Creates a new character.
     * This method prompts the user for a character name and validates it. If the character name is valid,
     * it proceeds to create a new character by collecting player name, character level, and generating stats.
     * The new character is then displayed.
     *
     * @throws PersistenceException If there is an error while accessing the data files or if the data is corrupted.
     */
    private void createCharacter() throws PersistenceException {
        String name = greetAndGetName();

        if (validateCharacterName(name)) {
            createAndDisplayNewCharacter(name,
                                         getPlayerName(name),
                                         getCharacterLevel(),
                                         calculateFinalStats(generateAndDisplayStats()),
                                         getCharacterClass());
            return;
        }

        menu.showErrorNameMsg("[ERROR] Tavern keeper: “I’m sorry, but that name doesn't meet the requirements”");
    }

    /**
     * Gets the character class from user input.
     * @return The character class from user input.
     */
    private String getCharacterClass() {
        return menu.askForCharacterClass();
    }

    /**
     * Greets the user and prompts for a character name.
     * This method displays a greeting message and prompts the user to enter a name for the character.
     * The entered name is then passed to the Character Manager's title method to format it.
     *
     * @return The formatted character name.
     */
    private String greetAndGetName() {
        return characterManager.title(menu.askForCharacterNameCreation());
    }

    /**
     * Validates the character name.
     * This method validates the provided character name using the Character Manager's validateName method.
     *
     * @param name The character name to validate.
     *
     * @return {@code true} if the character name is valid, {@code false} otherwise.
     * @throws PersistenceException If there is an error while accessing the data files or if the data is corrupted.
     */
    private boolean validateCharacterName(String name) throws PersistenceException {
        if (this.apiConnection) {
            return characterManager.validateNameFromApi(name);
        }
        return characterManager.validateName(name);
    }

    /**
     * Prompts for the player name associated with the character.
     * This method prompts the user to enter the player name associated with the character.
     *
     * @param characterName The name of the character.
     *
     * @return The player name entered by the user.
     */
    private String getPlayerName(String characterName) {
        return menu.askForPlayerName(characterName);
    }

    /**
     * Prompts for the character level.
     * This method prompts the user to enter the level of the character.
     *
     * @return The level of the character entered by the user.
     */
    private int getCharacterLevel() {
        return menu.askTheLevel();
    }

    /**
     * Generates and displays the character stats.
     * This method generates the character stats using the Character Manager's generateStats method.
     * It then displays the generated stats using the menu's showStats method.
     *
     * @return The array of generated stats.
     */
    private int[] generateAndDisplayStats() {
        int[] stats = characterManager.generateStats();
        menu.showStats(stats);
        return stats;
    }

    /**
     * Calculates the final stats based on the provided stats array.
     * This method calculates the final values for body, mind, and spirit stats using the Character Manager's calculateFinalStat method.
     * It then displays the final stats using the menu's showFinalStats method.
     *
     * @param stats The array of stats.
     *
     * @return The array of calculated final stats.
     */
    private int[] calculateFinalStats(int[] stats) {
        int bodyFinalValue = characterManager.calculateFinalStat(stats[0], stats[1]);
        int mindFinalValue = characterManager.calculateFinalStat(stats[2], stats[3]);
        int spiritFinalValue = characterManager.calculateFinalStat(stats[4], stats[5]);

        menu.showFinalStats(characterManager.formatSignStats(bodyFinalValue),
                            characterManager.formatSignStats(mindFinalValue),
                            characterManager.formatSignStats(spiritFinalValue));

        return new int[]{bodyFinalValue, mindFinalValue, spiritFinalValue};
    }

    /**
     * Creates and displays a new character.
     * This method creates a new character using the provided name, player name, level, and final stats.
     * The character is created using the Character Manager's createCharacter method. It then displays
     * a success message using the menu's successfulCharacterMessage method.
     *
     * @param name          The name of the character.
     * @param playerName    The name of the player associated with the character.
     * @param level         The level of the character.
     * @param finalStats    The array of final stats for the character.
     * @param characterClass The name of the character class.
     * @throws PersistenceException If there is an error while accessing the data files or if the data is corrupted.
     */
    private void createAndDisplayNewCharacter(String name, String playerName, int level, int[] finalStats, String characterClass) throws PersistenceException {
        String characterFinalClass;
        if (this.apiConnection) {
            characterFinalClass = characterManager.createCharacterFromApi(name,
                                                                          playerName,
                                                                          characterManager.convertLvlToXp(level),
                                                                          finalStats[0], finalStats[1], finalStats[2],
                                                                          characterManager.title(characterClass));
        } else {
            characterFinalClass = characterManager.createCharacter(name,
                                                                   playerName,
                                                                   characterManager.convertLvlToXp(level),
                                                                   finalStats[0], finalStats[1], finalStats[2],
                                                                   characterManager.title(characterClass));
        }
        menu.successfulCharacterMessage(name, characterFinalClass);
    }
}