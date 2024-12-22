package project.business.entities.battle.character;

import project.business.Dice;
import project.business.entities.battle.BattleEntity;

/**
 * Represents a Warrior character in a battle.
 * A Warrior is a type of Adventurer with specific attributes and behaviors.
 */
public class Warrior extends Adventurer {
    /**
     * Whether the warrior has evolved to a champion.
     */
    private boolean evolvedToChampion;
    /**
     * The type of the character which can be (adventurer, warrior, champion).
     */
    private String characterType;
    /**
     * Constructs a Warrior character with the given attributes.
     *
     * @param body         The body attribute representing physical strength.
     * @param mind         The mind attribute representing intelligence.
     * @param spirit       The spirit attribute representing willpower.
     * @param name         The name of the warrior.
     * @param xp           The experience points of the warrior.
     */
    public Warrior(int body, int mind, int spirit, String name, int xp) {
        super(body, mind, spirit, name, xp);
        this.characterType = "Warrior";
        this.evolvedToChampion = false;
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
        return ImprovedSwordSlash(target);
    }

    /**
     * Rolls dice and calculates the damage result for an attack on a target BattleEntity.
     *
     * @param target The target BattleEntity to attack.
     * @return A string describing the result of the attack.
     */
    public String ImprovedSwordSlash(BattleEntity target) {
        this.d10 = Dice.valueBetween(1, 10);
        this.damageResult = (this.d10 + this.body);

        if (d10 >= 2 && d10 <= 10) {
            attackMessage.append("Hits and deals ").append(damageResult).append(" physical damage.");
            this.damageMessage = new StringBuilder(target.takeDamage(damageResult, super.getDamageType()));
        } else {
            attackMessage.append("Fails and deals 0 ").append(" physical damage.");
        }

        return whoToWho.toString() + "\n" + attackMessage.toString() + "\n" + damageMessage.toString() + "\n";
    }

    /**
     * Processes damage taken by the Warrior.
     *
     * @param damage The amount of damage taken.
     * @return A string describing the result of taking damage.
     */
    @Override
    public String takeDamage(int damage, String damageType) {
        resetDamageMessage();
        damage = damageType.equals(super.getDamageType()) ? damage : damage / 2;
        this.hitPoints -= damage;
        if (this.hitPoints <= 0) {
            this.hitPoints = 0;
            this.damageMessage.append(this.name).append(" falls unconscious.\n");
        }
        return this.damageMessage.toString();
    }

    protected String checkWarriorLevelUp() {
        if (this.level >= 8 && !evolvedToChampion) {
            this.characterType = "Champion";
            this.evolvedToChampion = true;
            return "\n" + this.name + " evolves to " + this.characterType + "!";
        }
        return "";
    }

    public String getCharacterType() {
        return this.characterType;
    }
}