package project.business.entities.battle.character;

import project.business.Dice;
import project.business.entities.battle.BattleEntity;

/**
 * Represents an Adventurer character in a battle.
 * An Adventurer is a type of BattleCharacter with specific attributes and behaviors.
 */
public class Adventurer extends BattleCharacter {
    private boolean evolvedToWarrior;
    private boolean evolvedToChampion;

    /**
     * Stores the result of damage calculations during attacks.
     */
    protected int damageResult;

    /**
     * Stores the result of rolling a 10-sided die.
     */
    protected int d10;

    /**
     * The type of the character which can be (adventurer, warrior, champion).
     */
    private String characterType;

    /**
     * The type of damage the character deals.
     */
    private final String damageType;

    /**
     * Constructs an Adventurer character with the given attributes.
     *
     * @param body         The body attribute representing physical strength.
     * @param mind         The mind attribute representing intelligence.
     * @param spirit       The spirit attribute representing willpower.
     * @param name         The name of the adventurer.
     * @param xp           The experience points of the adventurer.
     */
    public Adventurer(int body, int mind, int spirit, String name, int xp) {
        this.body = body;
        this.mind = mind;
        this.spirit = spirit;
        this.name = name;
        this.experiencePoints = xp;
        this.level = calculateLevel(xp);
        this.hitPoints = this.maxHitPoints = calculateHitPoints();
        this.whoToWho = new StringBuilder();
        this.attackMessage = new StringBuilder();
        this.damageMessage = new StringBuilder();
        this.evolvedToWarrior = false;
        this.evolvedToChampion = false;
        this.characterType = "Adventurer";
        this.damageType = "Physical";
    }

    @Override
    public String getCharacterType() {
        return characterType;
    }

    protected String getDamageType() {
        return damageType;
    }

    /**
     * Calculates the level of the adventurer based on experience points.
     *
     * @param xp The experience points.
     * @return The calculated level.
     */
    private int calculateLevel(int xp) {
        return Math.min((xp >= 0 && xp <= 99 ? 1 : (xp / 100) + 1), 10);
    }

    /**
     * Calculates the hit points of the adventurer based on level and body attribute.
     *
     * @return The calculated hit points.
     */
    protected int calculateHitPoints() {
        return (10 + this.body) * (this.level);
    }

    /**
     * Allows the Adventurer to make a self-motivation speech, increasing spirit attribute by 1.
     *
     * @return A string describing the action.
     */
    public String makeSelfMotivationSpeech() {
        this.spirit += 1;
        return this.name + " uses Self-Motivated. Their spirit increases in +1.";
    }

    /**
     * Rolls dice to determine the Adventurer's initiative in battle.
     */
    @Override
    public void rollInitiative() {
        this.initiative = Dice.valueBetween(1, 12) + this.spirit;
    }

    /**
     * Performs an attack on a target BattleEntity.
     *
     * @param target The target BattleEntity to attack.
     * @return A string describing the result of the attack.
     */
    @Override
    public String attack(BattleEntity target) {
        resetMessages();
        whoToWho.append(this.name).append(" attacks ").append(target.getName()).append(".");
        return rollDiceAndCalculateDamageResult(target);
    }

    /**
     * Rolls dice and calculates the damage result for an attack on a target BattleEntity.
     *
     * @param target The target BattleEntity to attack.
     * @return A string describing the result of the attack.
     */
    private String rollDiceAndCalculateDamageResult(BattleEntity target) {
        this.d10 = Dice.valueBetween(1, 10);
        this.damageResult = (Dice.valueBetween(1, 6) + this.body);

        if (d10 >= 2 && d10 <= 10) {
            if (d10 == 10) {
                damageResult *= 2;
                attackMessage.append("Critical hit and deals ").append(damageResult).append(" physical damage.");
            } else {
                attackMessage.append("Hits and deals ").append(damageResult).append(" physical damage.");
            }
            this.damageMessage = new StringBuilder(target.takeDamage(damageResult, this.damageType));
        } else {
            attackMessage.append("Fails and deals 0 ").append(" physical damage.");
        }

        return whoToWho.toString() + "\n" + attackMessage.toString() + "\n" + damageMessage.toString() + "\n";
    }

    /**
     * Resets the messages used to construct attack and damage descriptions.
     */
    void resetMessages() {
        this.whoToWho.delete(0, this.whoToWho.length());
        this.attackMessage.delete(0, this.attackMessage.length());
        this.damageMessage.delete(0, this.damageMessage.length());
    }

    /**
     * Processes damage taken by the Adventurer.
     *
     * @param damage The amount of damage taken.
     * @return A string describing the result of taking damage.
     */
    @Override
    public String takeDamage(int damage, String damageType) {
        resetDamageMessage();
        this.hitPoints -= damage;
        if (this.hitPoints <= 0) {
            this.hitPoints = 0;
            this.damageMessage.append(this.name).append(" falls unconscious.\n");
        }
        return this.damageMessage.toString();
    }

    /**
     * Resets the damage message used to construct damage descriptions.
     */
    void resetDamageMessage() {
        this.damageMessage.delete(0, damageMessage.length());
    }

    /**
     * Allows the Adventurer to bandage wounds and recover hit points.
     *
     * @return A string describing the result of the action.
     */
    public String bandageTime() {
        if (this.hitPoints == 0) {
            return this.name + " is unconscious";
        } else {
            int healAmount = Dice.valueBetween(1, 8) + this.mind;
            this.hitPoints += healAmount;
            if (this.hitPoints > this.maxHitPoints) {
                this.hitPoints = this.maxHitPoints;
            }
            return this.name + " uses Bandage time. Heals " + healAmount + " hit points.";
        }
    }

    protected String checkForAdventurerLevelUp() {
        StringBuilder result = new StringBuilder();

        if (this.level >= 4 && !evolvedToWarrior) {
            this.characterType = "Warrior";
            evolvedToWarrior = true;
            result.append("\n").append(this.name).append(" evolves ").append(this.characterType).append("!");
        }

        if (this.level >= 8 && !evolvedToChampion) {
            if (result.length() > 0) {
                result.append("\n");
            }
            this.characterType = "Champion";
            evolvedToChampion = true;
            result.append(this.name).append(" evolves ").append(this.characterType).append("!");
        }

        return result.toString();
    }
}
