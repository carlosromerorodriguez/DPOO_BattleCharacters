package project.presentation;

import project.business.entities.character.Character;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * This class is responsible for showing the menu and getting the user input.
 */
public class Menu {
    /**
     * The different menu options.
     */
    public static final int CHARACTER_CREATION = 1;
    public static final int LIST_CHARACTER = 2;
    public static final int CREATE_ADVENTURE = 3;
    public static final int START_ADVENTURE = 4;

    /**
     * The scanner used to get the user input.
     */
    private final Scanner scanner;

    /**
     * The constructor initializes the scanner.
     */
    public Menu() {
        scanner = new Scanner(System.in);
    }

    /**
     * This method shows the welcome message.
     */
    public int showWelcome() {
        this.showMessage("""
                   _____             __      __   _______  ___  _____
                  / __/(_)_ _  ___  / /__   / /  / __/ _ \\/ _ \\/ ___/
                 _\\ \\/ /  ' \\/ _ \\/ / -_) / /___\\ \\/ , _/ ___/ (_ /
                /___/_/_/_/_/ .__/_/\\__/ /____/___/_/|_/_/   \\___/
                         /_/                                     \s
                       
                Welcome to Simple LSRPG.
                            
                Do you want to use your local or cloud data?
                    1) Local data
                    2) Cloud data
                    
                    """);

        return this.askForInteger("-> Answer: ", 1, 2);
    }

    /**
     * This method shows the menu options.
     */
    public void showMenu() {
        System.out.println();
        this.showMessage("""
                The tavern keeper looks at you and says:\s
                “Welcome adventurer! How can I help you?”
                """);
        this.showMessage("\t1) Character creation");
        this.showMessage("\t2) List characters");
        this.showMessage("\t3) Create an adventure");
        this.showMessage("\t4) Start an adventure");
        this.showMessage("\t5) Exit");
        System.out.println();
    }

    /**
     * Prompts the user to enter an integer within a specified range.
     * This method displays the provided message and prompts the user to enter an integer.
     * It validates the input to ensure it is within the specified range (inclusive of the minimum and maximum values).
     * If the input is not a valid integer or falls outside the range, appropriate error messages are displayed and the
     * user is prompted again.
     *
     * @param message The message to display when prompting for input.
     * @param min The minimum allowed value.
     * @param max The maximum allowed value.
     *
     * @return The validated integer input from the user.
     */
    public int askForInteger(String message, int min, int max) {
        int input = 0;
        boolean valid = false;
        while (!valid) {
            System.out.print(message);
            try {
                input = scanner.nextInt();
                scanner.nextLine();
                if (input >= min && input <= max) {
                    valid = true;
                } else {
                    this.showMessage("[ERROR] Please enter a number between " + min + " and " + max + ".\n");
                }
            } catch (InputMismatchException e) {
                this.showMessage("[ERROR] Please enter a number.\n");
                scanner.nextLine();
            }
        }
        return input;
    }

    /**
     * Prompts the user to enter a string.
     * This method displays the provided message and prompts the user to enter a string.
     *
     * @param message The message to display when prompting for input.
     *
     * @return The string input from the user.
     */
    public String askForString(String message) {
        System.out.print(message);
        return scanner.nextLine();
    }

    /**
     * Displays a message.
     * This method prints the provided message to the console.
     *
     * @param message The message to display.
     */
    public void showMessage(String message) {
        System.out.println(message);
    }

    /**
     * Displays a list of characters.
     * This method prints the list of characters to the console, including their names and corresponding numbers for selection.
     * It also displays an option to go back.
     *
     * @param characters The list of characters to display.
     */
    public void showCharacterList (List<Character> characters) {
        System.out.println();
        for (int i = 0; i < characters.size(); i++) {
            this.showMessage("\t" + (i + 1) + ". " + characters.get(i).name());
        }
        this.showMessage("\n\t0. Back\n");
    }

    /**
     * Displays the character information and prompts for deletion confirmation.
     * This method displays the provided character information, including the name, player, class, level, XP, body, mind, and spirit.
     * It prompts the user to enter a name for deletion or press enter to cancel. The entered name or cancellation status is returned.
     *
     * @param name The name of the character.
     * @param player The name of the player associated with the character.
     * @param clas The class of the character.
     * @param level The level of the character.
     * @param xp The XP of the character.
     * @param body The body stat of the character.
     * @param mind The mind stat of the character.
     * @param spirit The spirit stat of the character.
     *
     * @return The name entered for deletion or an empty string if cancellation is requested.
     */
    public String showCharacterInfo(String name, String player, String clas, int level, int xp, String body, String mind, String spirit) {
        this.showMessage("\n");
        this.showMessage("\t* Name:    " + name);
        this.showMessage("\t* Player:  " + player);
        this.showMessage("\t* Class:   " + clas);
        this.showMessage("\t* Level:   " + level);
        this.showMessage("\t* XP:      " + xp);
        this.showMessage("\t* Body:    " + body);
        this.showMessage("\t* Mind:    " + mind);
        this.showMessage("\t* Spirit:  " + spirit);
        System.out.print("\n[Enter name to delete, or press enter to cancel]\nDo you want to delete " + name + "? ");
        return scanner.nextLine();
    }

    /**
     * Displays a greeting message from the tavern keeper.
     * This method prints a greeting message from the tavern keeper, addressing the character by their name.
     *
     * @param characterName The name of the character.
     */
    public void showGreeting(String characterName) {
        System.out.print("\nTavern keeper: “Hey " + characterName + " get here; the boss wants to see you!”");
    }

    /**
     * Displays a message when a character is leaving the guild.
     * This method prints a message from the tavern keeper informing the character that they have to leave.
     * It also prints a message indicating that the character has left the guild.
     *
     * @param characterName The name of the character.
     */
    public void showCharacterLeavingMessage(String characterName) {
        System.out.print("\nTavern keeper: “I’m sorry kiddo, but you have to leave.”\n");
        System.out.println("\nCharacter " + characterName + " left the Guild.");
    }

    /**
     * Displays a message when a character is not found.
     * This method prints a message from the tavern keeper indicating that they don't know who the user is talking about.
     */
    public void showCharacterNotFoundMessage() {
        System.out.println("\nTavern keeper: “I’m sorry, but I don’t know who you are talking about.”");
    }

    /**
     * Displays the generated stats.
     * This method prints a message indicating that the stats are being generated.
     * It then displays the rolled stats for body, mind, and spirit.
     *
     * @param stats The array of generated stats.
     */
    public void showStats(int[] stats) {
        this.showMessage("“Great, let me get a closer look at you...”\n\nGenerating your stats...\n");
        this.showMessage("Body:    " + "You rolled " + (stats[0] + stats[1]) + " (" + stats[0] + " and " + stats[1] + ").");
        this.showMessage("Mind:    " + "You rolled " + (stats[2] + stats[3]) + " (" + stats[2] + " and " + stats[3] + ").");
        this.showMessage("Spirit:  " + "You rolled " + (stats[4] + stats[5]) + " (" + stats[4] + " and " + stats[5] + ").");
    }

    /**
     * Displays the final stats.
     * This method prints the final stats for body, mind, and spirit.
     *
     * @param bodyFinalValue The final value for the body stat.
     * @param mindFinalValue The final value for the mind stat.
     * @param spiritFinalValue The final value for the spirit stat.
     */
    public void showFinalStats(String bodyFinalValue, String mindFinalValue, String spiritFinalValue) {
        this.showMessage("\nYour stats are:\n" +
                "\t-Body:    " + bodyFinalValue + "\n" +
                "\t-Mind:    " + mindFinalValue + "\n" +
                "\t-Spirit:  " + spiritFinalValue + "\n");
    }

    /**
     * Prompts for the character's level.
     * This method displays a message from the tavern keeper and prompts the user to enter the character's level.
     * It validates the input to ensure it falls within the range of 1 to 10 (inclusive). After validation, a message
     * confirming the selected level is displayed, and the level is returned.
     *
     * @return The character's level entered by the user.
     */
    public int askTheLevel() {
        this.showMessage("\n\nTavern keeper: “I see, I see...”\n“Now, are you an experienced adventurer?”\n");
        int lvl = this.askForInteger("-> Enter the character’s level [1..10]: ", 1, 10);
        this.showMessage("\nTavern keeper: “Oh, so you are level " + lvl + "!”");
        return lvl;
    }

    /**
     * Prompts for the name of the adventure.
     * This method displays a message from the tavern keeper and prompts the user to enter the name of the adventure.
     *
     * @return The name of the adventure entered by the user.
     */
    public String askForAdventureName() {
        this.showMessage("\nTavern keeper: “Planning an adventure? Good luck with that!”");
        return this.askForString("\n-> Name your adventure: ");
    }

    /**
     * Displays encounter information.
     * This method displays the encounter number and the information about the monsters in the encounter.
     *
     * @param encounter The current encounter number.
     * @param numEncounters The total number of encounters.
     */
    public void showEncounterInfo(int encounter, int numEncounters) {
        this.showMessage("\n\n* Encounter " + encounter + " / " + numEncounters);
        this.showMessage("* Monsters in encounter: ");
    }

    /**
     * Displays the menu for creating an adventure.
     * This method prints the menu options for creating an adventure, including adding a monster, removing a monster, and continuing.
     * It prompts the user to enter an option and validates the input to ensure it falls within the range of 1 to 3 (inclusive).
     * The selected option is returned.
     *
     * @return The selected option for creating an adventure.
     */
    public int showMenuCreateAdventure() {
        System.out.println();
        this.showMessage("1. Add Monster");
        this.showMessage("2. Remove Monster");
        this.showMessage("3. Continue");
        return this.askForInteger("\n-> Enter an option [1..3]: ", 1, 3);
    }

    /**
     * Displays a list of monsters and prompts for selection.
     * This method prints the list of monsters to the console, including their names and corresponding numbers for selection.
     * It prompts the user to choose a monster by entering the corresponding number.
     * The selected monster's index in the list is returned.
     *
     * @param monsters The list of monsters to display.
     *
     * @return The index of the selected monster in the list.
     */
    public int listAllMonsters(List<String> monsters) {
        System.out.println();
        for (int i = 0; i < monsters.size(); i++) {
            this.showMessage("\t" + (i + 1) + ". " + monsters.get(i));
        }
        return this.askForInteger("\n-> Choose a monster to add [1.." + monsters.size() + "]: ", 1, monsters.size()) - 1;
    }

    /**
     * Prompts the user to enter the number of encounters.
     * This method displays the provided message and prompts the user to enter the number of encounters.
     *
     * @param message The message to display when prompting for input.
     *
     * @return The number of encounters entered by the user.
     */
    public int askForEncounterNumber(String message) {
        System.out.print(message);
        return scanner.nextInt();
    }

    /**
     * Displays a welcome message for starting an adventure.
     * This method prints a welcome message from the tavern keeper, indicating that the user is looking to go on an adventure.
     * It also prompts the user to choose a destination for the adventure.
     */
    public void welcomeToAdventureMsg() {
        this.showMessage("\n\nTavern keeper: “So, you are looking to go on an adventure?”");
        this.showMessage("“Where do you fancy going?”\n");
    }

    /**
     * Displays a list of all available adventure names and prompts for selection.
     * This method prints the list of all available adventure names to the console, including their names and corresponding numbers for selection.
     * It prompts the user to choose an adventure by entering the corresponding number.
     * The index of the selected adventure name in the list is returned.
     *
     * @param allAdventuresNames The list of all available adventure names to display.
     *
     * @return The index of the selected adventure name in the list.
     */
    public int listAllAdventuresNames(List<String> allAdventuresNames) {
        this.showMessage("Available adventures:");
        for (int i = 0; i < allAdventuresNames.size(); i++) {
            this.showMessage("\t" + (i + 1) + ". " + allAdventuresNames.get(i));
        }
        return this.askForInteger("\n-> Choose an adventure: ", 1, allAdventuresNames.size()) - 1;
    }

    /**
     * Prompts the user to enter the number of characters for the battle.
     * This method displays a message from the tavern keeper, indicating the selected adventure name and asking the user how many people will join the battle.
     * It validates the input to ensure it falls within the range of 3 to the specified size.
     * After validation, a message confirming the selected number of characters is displayed, and the number of characters is returned.
     *
     * @param size The maximum number of characters allowed in the battle.
     * @param adventureName The name of the selected adventure.
     *
     * @return The number of characters for the battle entered by the user.
     */
    public int askForBattleCharacters(int size, String adventureName) {
        this.showMessage("\nTavern keeper: “" + adventureName + " it is”\n“And how many people shall join you?”");
        int numCharacters = this.askForInteger("\n-> Choose a number of characters [3..5]: ", 3, size);
        this.showMessage("\nTavern keeper: “Great, " + numCharacters + " it is.”\n“Who among these lads shall join you?”");
        return numCharacters;
    }

    /**
     * Displays party information.
     * This method prints the party information, including the names of the characters in the party and the current count of characters out of the total number.
     *
     * @param characterNames The list of character names in the party.
     * @param realNumCharacters The current count of characters in the party.
     * @param numCharacters The total number of characters in the party.
     */
    public void showPartyInfo(List<String> characterNames, int realNumCharacters, int numCharacters) {
        this.showMessage("\n------------------------------");
        this.showMessage("Your party (" + realNumCharacters + "/ " + numCharacters + "):");
        for (int i = 0; i < characterNames.size(); i++) {
            this.showMessage("\t" + (i + 1) + ". " + characterNames.get(i));
        }
        this.showMessage("------------------------------");
    }

    /**
     * Displays a list of all available characters and prompts for selection.
     * This method prints the list of all available characters to the console, including their names and corresponding numbers for selection.
     * It prompts the user to choose a character by entering the corresponding number.
     * The index of the selected character in the list is returned.
     *
     * @param characters The list of all available characters to display.
     * @param num The number of the character in the party.
     *
     * @return The index of the selected character in the list.
     */
    public int showAllCharactersToAdd(List<Character> characters, int num) {
        this.showMessage("Available characters:");
        for (int i = 0; i < characters.size(); i++) {
            this.showMessage("\t" + (i + 1) + ". " + characters.get(i).name());
        }
        return this.askForInteger("\n-> Choose character " + num + " in your party: ", 1, characters.size()) - 1;
    }

    /**
     * Displays information about the starting encounter.
     * This method prints the information about the starting encounter, including the encounter number and the names of the monsters in the encounter.
     * It prompts the user to press ENTER to continue.
     *
     * @param numEncounter The encounter number.
     * @param monsterNamesInEncounter The list of monster names in the encounter.
     */
    public void showStartingEncounterInfo(int numEncounter, List<String> monsterNamesInEncounter) {
        this.showMessage("-----------------------------");
        this.showMessage("Starting encounter " + numEncounter + ": ");
        for (String s : monsterNamesInEncounter) {
            this.showMessage(s);
        }
        this.showMessage("-----------------------------\n");
        this.showMessage("Press ENTER to continue...");
        scanner.nextLine();
    }

    /**
     * Displays the preparation phase message.
     * This method prints the message indicating the start of the preparation phase.
     * It provides a visual representation of the preparation stage.
     */
    public void showPreparationPhase() {
        this.showMessage("""
                -------------------------\s
                *** Preparation stage ***\s
                -------------------------
                """);

    }

    /**
     * Displays the initiative order.
     * This method prints the message indicating the initiative is being rolled.
     * It then displays the order of initiative as a list of strings.
     *
     * @param initiativeOrder The list of strings representing the initiative order.
     */
    public void rollInitiative(List<String> initiativeOrder) {
        this.showMessage("\nRolling initiative...");
        for (String s : initiativeOrder) {
            this.showMessage(s);
        }
    }

    /**
     * Displays a message indicating that there are no available adventures.
     * This method prints a message indicating that there are no adventures available.
     */
    public void showEmptyAdventuresList() {
        this.showMessage("There are no adventures available.");
    }

    /**
     * Prompts for the player's name.
     * This method displays a greeting from the tavern keeper, addressing the character by name.
     * It then prompts the user to enter the player's name.
     *
     * @param characterName The name of the character.
     *
     * @return The player's name entered by the user.
     */
    public String askForPlayerName(String characterName) {
        this.showMessage("\nTavern keeper: “Hello, " + characterName + " , be welcome.");
        this.showMessage("“And now, if I may break the fourth wall, who is your Player?”\n");
        return this.askForString("-> Enter the player’s name: ");
    }

    /**
     * Displays a success message for character creation.
     * This method prints a message indicating that the new character has been successfully created.
     *
     * @param name The name of the created character.
     */
    public void successfulCharacterMessage(String name, String characterClass) {
        System.out.println("\nTavern keeper: “Any decent party needs one of those.”");
        System.out.println("“I guess that means you’re a " + characterClass + " by now, nice!”");
        this.showMessage("\nThe new character " + name + " has been created.");
    }

    /**
     * Prompts for the character's name during character creation.
     * This method displays a message from the tavern keeper, indicating that the user is new to the land and asking for their name.
     * It prompts the user to enter their name and trims any leading or trailing space from the input.
     *
     * @return The character's name entered by the user.
     */
    public String askForCharacterNameCreation() {
        this.showMessage("\nTavern keeper: “Oh, so you are new to this land.”\n“What’s your name?”\n");
        return this.askForString("-> Enter your name: ").trim();
    }

    /**
     * Prompts the user to enter the number of encounters for the adventure.
     * This method repeatedly asks the user to enter a valid number of encounters between 1 and 4.
     * If the user enters an invalid input, an error message is displayed. After three consecutive
     * invalid inputs, a final error message is shown and -1 is returned, indicating the user's
     * decision to return to the main menu.
     *
     * @return The number of encounters chosen by the user, or -1 if the maximum number of invalid inputs is reached.
     */
    public int askForEncounterNumber() {
        int errorCount = 0;
        while (true) {
            int numEncounters = this.askForEncounterNumber("-> How many encounters do you want [1..4]: ");
            if (numEncounters >= 1 && numEncounters <= 4) {
                this.showMessage("\nTavern keeper: “" + numEncounters + " That is too much for me...”");
                return (numEncounters);
            } else {
                errorCount++;
                if (errorCount >= 3) {
                    this.showMessage("You have made too many mistakes. Returning to the main menu...");
                    return -1;
                } else {
                    this.showMessage("Invalid input. Please enter a number between 1 and 4.\n");
                }
            }
        }
    }

    /**
     * Displays an attack message.
     * This method prints the provided attack message to the console.
     *
     * @param attackMsg The attack message to display.
     */
    public void showAttackMsg(String attackMsg) {
        System.out.print(attackMsg);
    }

    /**
     * Displays the combat stage information.
     * This method prints the round number and the list of character names and their hit points for the combat stage.
     * It provides an overview of the party's status during the combat.
     *
     * @param characterNamesAndHitPoints The list of character names and their hit points.
     * @param encounter The current round number.
     */
    public void combatStage(List<String> characterNamesAndHitPoints, int encounter) {
        this.showMessage("Round: " + encounter);
        this.showMessage("Party:");
        for (String s : characterNamesAndHitPoints) {
            this.showMessage(s);
        }
        this.showMessage("");
    }

    /**
     * Displays the battle header.
     * This method prints the header for the combat stage, indicating the start of the battle.
     * It provides a visual representation of the combat stage.
     */
    public void showBattleHeader() {
        this.showMessage("""
                
                -------------------------
                *** Combat stage ***
                -------------------------""");
    }

    /**
     * Displays a message indicating the end of a round.
     * This method prints a message indicating the end of the specified round.
     *
     * @param round The round number.
     */
    public void showEndRoundMsg(int round) {
        this.showMessage("End of round " + round + ".");
    }

    /**
     * Displays a message indicating that all enemies are defeated.
     * This method prints a message indicating that all enemies have been defeated.
     */
    public void showEnemiesDefeated() {
        this.showMessage("All enemies are defeated.");
    }

    /**
     * Displays a message related to data loading.
     * This method prints the provided message related to data loading.
     *
     * @param s The message to display.
     */
    public void showDataLoadingMsg(String s) {
        this.showMessage(s);
    }

    /**
     * Displays an error message related to character name.
     * This method prints the provided error message related to character name.
     *
     * @param s The error message to display.
     */
    public void showErrorNameMsg(String s) {
        this.showMessage(s);
    }

    /**
     * Displays an exit message.
     * This method prints the provided exit message.
     */
    public void showExitMsg() {
        this.showMessage("\nTavern keeper: “Are you leaving already? See you soon, adventurer.”");
    }

    /**
     * Displays an error message related to player name searching.
     * This method prints the provided error message related to player name searching.
     *
     * @param s The error message to display.
     */
    public void showPlayerNameSearchingError(String s) {
        this.showMessage(s);
    }

    /**
     * Lists all monsters in the encounter.
     * This method prints the list of monsters in the encounter, including their names and quantities.
     *
     * @param monsterAndQuantity The list of strings representing each monster and its quantity in the encounter.
     */
    public void listAllMonstersInEncounter(List<String> monsterAndQuantity) {
        for (String s : monsterAndQuantity) {
            this.showMessage(s);
        }
    }

    /**
     * Lists all XP gained for every character.
     * This method prints the list of XP gained for every character in the battle.
     *
     * @param xpGainedForEveryCharacter The list of strings representing the XP gained for every character.
     */
    public void listAllXPGained(List<String> xpGainedForEveryCharacter) {
        System.out.println();
        for (String s : xpGainedForEveryCharacter) {
            this.showMessage(s);
        }
    }

    /**
     * Displays a message indicating the start of an adventure.
     * This method prints a message from the tavern keeper, wishing the adventurers good luck on their adventure.
     * It also displays the name of the adventure that is about to start.
     *
     * @param adventureName The name of the adventure.
     */
    public void showAdventureStartMsg(String adventureName) {
        this.showMessage("------------------------------\n");
        this.showMessage("Tavern keeper: “Great, good luck on your adventure lads!”\n");
        this.showMessage("The “" + adventureName + "” will start soon...\n");
    }

    /**
     * Lists the characters with their bandage abilities.
     * This method prints the list of character names along with their bandage abilities.
     *
     * @param characterNamesAndRestAbilities The list of strings representing each character's name and bandage ability.
     */
    public void listCharactersBandageAbilities(List<String> characterNamesAndRestAbilities) {
        System.out.println();
        for (String s : characterNamesAndRestAbilities) {
            this.showMessage(s);
        }
        System.out.println("\n");
    }

    public void showApiFailedConnection() {
        this.showMessage("\nLoading data...");
        System.out.print("""
                        Couldn’t connect to the remote server.\s
                        Reverting to local data.
                        """);
    }

    public void apiSuccessConnection() {
        this.showMessage("\nLoading data...");
        this.showDataLoadingMsg("Data was successfully loaded.");
    }

    public int askForMenuOption() {
        return this.askForInteger("Your answer: ", 1, 5);
    }

    public String askForCharacterClass() {
        while (true) {
            System.out.print("-> Enter the character’s initial class [Adventurer, Cleric, Mage]: ");
            String characterClass = this.scanner.nextLine();
            if (characterClass.equalsIgnoreCase("Adventurer") || characterClass.equalsIgnoreCase("Cleric") || characterClass.equalsIgnoreCase("Mage")) {
                return characterClass;
            } else {
                this.showMessage("[ERROR] Invalid input. Please enter a valid class.\n");
            }
        }
    }
}
