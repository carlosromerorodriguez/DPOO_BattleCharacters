package project.business.entities.battle.character;

import project.business.entities.battle.BattleEntity;

import java.util.List;

/**
 * Represents a character involved in battles. This class is an abstract class that extends BattleEntity.
 * BattleCharacters have experience points, attributes like mind, body, and spirit, and have levels.
 */
public abstract class BattleCharacter extends BattleEntity {

    /**
     * The experience points the character has earned.
     */
    protected int experiencePoints;

    /**
     * The character's mind attribute, influencing rude abilities.
     */
    protected int mind;

    /**
     * The character's body attribute, influencing physical abilities.
     */
    protected int body;

    /**
     * The character's spirit attribute, influencing spiritual or magical abilities.
     */
    protected int spirit;

    /**
     * The current level of the character.
     */
    protected int level;

    /**
     * The maximum hit points that the character can have.
     */
    protected int maxHitPoints;

    /**
     * The type of character (e.g. Warrior, Mage, etc.).
     */
    protected String characterType;

    /**
     * The party that the character is in.
     */
    protected List<BattleCharacter> party;

    /**
     * Retrieves the current and maximum hit points of the character as a formatted string.
     *
     * @return A string representing the character's current hit points and maximum hit points in the format "current / max".
     */
    public String getHitPointsAndMaxHitPoints() {
        return hitPoints + " / " + maxHitPoints;
    }

    /**
     * Retrieves the current hit points of the character.
     *
     * @return The current hit points as an integer.
     */
    public int getHitPoints() {
        return this.hitPoints;
    }

    /**
     * Adds experience points to the character and checks if they should level up.
     *
     * @param xpGainedTotal The total experience points gained.
     * @return A string message indicating the experience points gained and if the character leveled up.
     */
    public String addExperiencePoints(int xpGainedTotal) {
        StringBuilder result = new StringBuilder();

        this.experiencePoints += xpGainedTotal;
        result.append(this.name).append(" gains ").append(xpGainedTotal).append(" xp. ").append(levelUp());
        switch (BattleCharacter.this) {
            case Warrior warrior -> result.append(warrior.checkWarriorLevelUp());
            case Adventurer adventurer -> result.append(adventurer.checkForAdventurerLevelUp());
            case Cleric cleric -> result.append(cleric.checkClericLevelUp());
            default -> {}
        }
        return result.toString();
    }

    /**
     * Calculates if the character levels up based on their experience points.
     *
     * @return A string message indicating if the character has leveled up.
     */
    private String levelUp() {
        int newLevel = calculateLevel(this.experiencePoints);
        if (this.level < newLevel) {
            this.level = newLevel;
            return this.name + " levels up. They are now lvl " + this.level + "!";
        }
        return "";
    }

    public String getCharacterType() {
        return this.characterType;
    }

    /**
     * Calculates the character's level based on experience points.
     *
     * @param xpGain The experience points gained.
     * @return The calculated level as an integer.
     */
    private int calculateLevel(int xpGain) {
        return Math.min((xpGain >= 0 && xpGain <= 99 ? 1 : (xpGain / 100) + 1), 10);
    }

    /**
     * Retrieves the experience points of the character.
     *
     * @return The experience points as an integer.
     */
    public int getXP() {
        return this.experiencePoints;
    }

    /**
     * Rolls the initiative of the character by rolling a X-sided die.
     */
    protected abstract void rollInitiative();

    protected void setHitPoints(int healing) {
        this.hitPoints = Math.min(this.hitPoints + healing, this.maxHitPoints);
    }
}
