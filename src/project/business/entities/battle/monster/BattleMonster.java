package project.business.entities.battle.monster;

import project.business.Dice;
import project.business.entities.battle.BattleEntity;

/**
 * Represents a battle monster in a game.
 * This class is abstract and should be subclassed by specific types of monsters.
 */
public abstract class BattleMonster extends BattleEntity {

    /**
     * The challenge level or rating of this monster.
     */
    protected String challenge;

    /**
     * A number representing the number of this encounter in the adventure.
     */
    protected int encounterNumber;

    /**
     * The number of dice to be rolled to calculate the damage dealt by the monster.
     */
    protected int damageDice;

    /**
     * The type of damage dealt by this monster (e.g., "fire", "poison").
     */
    protected String damageType;

    /**
     * The experience points that can be earned by defeating this monster.
     */
    protected int experiencePoints;

    /**
     * Creates a new instance of BattleMonster with the specified name, challenge, and encounter number.
     *
     * @param name      the name of the monster.
     * @param challenge the challenge level or rating of the monster.
     * @param encounter the order in which this monster is encountered.
     */
    public BattleMonster(String name, String challenge, int encounter) {
        this.name = name;
        this.challenge = challenge;
        this.encounterNumber = encounter;
    }

    /**
     * Sets the experience points for this monster.
     *
     * @param experiencePoints the experience points to be set.
     */
    public void setExperiencePoints(int experiencePoints) {
        this.experiencePoints = experiencePoints;
    }

    /**
     * Retrieves the experience points of this monster.
     *
     * @return the experience points of this monster.
     */
    public int getExperiencePoints() {
        return this.experiencePoints;
    }

    /**
     * Sets the type of damage this monster deals.
     *
     * @param damageType the type of damage to be set.
     */
    public void setDamageType(String damageType) {
        this.damageType = damageType;
    }

    /**
     * Sets the number of dice to be rolled for damage calculation.
     *
     * @param damageDice the number of dice to be set.
     */
    public void setDamageDice(int damageDice) {
        this.damageDice = damageDice;
    }

    /**
     * Sets the hit points of this monster.
     *
     * @param hitPoints the hit points to be set.
     */
    public void setHitPoints(int hitPoints) {
        this.hitPoints = hitPoints;
    }

    /**
     * Retrieves the hit points of this monster.
     *
     * @return the hit points of this monster.
     */
    public int getHitPoints() {
        return hitPoints;
    }

    /**
     * Retrieves the encounter number of this monster.
     *
     * @return the encounter number of this monster.
     */
    public int getEncounter() {
        return encounterNumber;
    }

    /**
     * Rolls dice to calculate and set the initiative of this monster.
     *
     * @param initiative the base initiative value.
     * @param damageDice the number of dice to be rolled for the initiative calculation.
     */
    public void rollInitiative(int initiative, int damageDice) {
        this.initiative = initiative + Dice.valueBetween(1, damageDice);
    }
}