package project.business.entities.battle.character;

import project.business.Dice;
import project.business.entities.battle.BattleEntity;
import project.business.entities.battle.monster.BattleMonster;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Represents a Mag character in a battle.
 * A Mag is a type of Adventurer with specific attributes and behaviors.
 */
public class Mage extends BattleCharacter {
    private String characterType;
    private int shield;
    private List<BattleMonster> monsters;

    /**
     * Constructs a Mag character with the given attributes.
     *
     * @param body         The body attribute representing physical strength.
     * @param mind         The mind attribute representing intelligence.
     * @param spirit       The spirit attribute representing willpower.
     * @param name         The name of the mag.
     * @param xp           The experience points of the mag.
     */
    public Mage(int body, int mind, int spirit, String name, int xp) {
        this.body = body;
        this.mind = mind;
        this.spirit = spirit;
        this.name = name;
        this.experiencePoints = xp;
        this.level = this.calculateLevel(xp);
        this.hitPoints = this.maxHitPoints = this.calculateMaxHealthPoints();
        this.whoToWho = new StringBuilder();
        this.attackMessage = new StringBuilder();
        this.damageMessage = new StringBuilder();
        this.characterType = "Mage";
    }

    public String getCharacterType() {
        return characterType;
    }

    private int calculateLevel(int xpGain) {
        return Math.min((xpGain >= 0 && xpGain <= 99 ? 1 : (xpGain / 100) + 1), 10);
    }

    public int getShield() {
        return shield;
    }

    public void setMonsters(List<BattleMonster> monsters) {
        this.monsters = monsters;
    }

    private int calculateShield() {
        return (Dice.valueBetween(1, 6) + this.mind) * this.level;
    }

    /**
     * Performs a shield regeneration action.
     */
    public String mageShield() {
        this.shield = this.calculateShield();
        return this.name + " uses Mage Shield. Shield recharges to " + this.shield + ".";
    }

    /**
     * Calculates the maximum health points for a Mag.
     */
    private int calculateMaxHealthPoints() {
        return (10 + this.body) * this.level;
    }

    /**
     * Performs an attack on a target BattleEntity.
     *
     * @param target The target BattleEntity to attack.
     * @return A string describing the result of the attack.
     */
    @Override
    public String attack(BattleEntity target) {
        long aliveMonstersCount = monsters.stream().filter(monster -> monster.getHitPoints() > 0).count();

        if (aliveMonstersCount >= 3) {
            return fireball();
        } else {
            BattleMonster bm = monsters.stream()
                    .filter(monster -> monster.getHitPoints() > 0)
                    .max(Comparator.comparingInt(BattleMonster::getHitPoints))
                    .orElse(null);

            return bm != null ? arcaneMissile(target) : "";
        }
    }


    /**
     * Performs a fireball attack on a list of target BattleMonsters.
     * <p>
     * This method initiates a fireball attack by the caster BattleEntity
     * on a list of target BattleMonsters. The fireball inflicts magical damage
     * which is calculated as the sum of the caster's 'mind' attribute and
     * a random value between 1 and 4 (inclusive). Each monster in the list
     * takes the same amount of damage.
     *
     * @return A string describing the result of the attack, including the names
     *         of the target BattleMonsters and the amount of damage inflicted.
     */
    public String fireball() {
        StringBuilder result = new StringBuilder();
        StringBuilder dies = new StringBuilder();
        result.append(this.name).append(" attacks ");

        int damage = Dice.valueBetween(1, 4) + this.mind;
        for (BattleMonster monster : this.monsters) {
            dies.append(monster.takeDamage(damage, "Psychical"));
            result.append(monster.getName()).append(" ");
        }

        return result.append("with Fireball.\n").append("They take ").append(damage).append(" psychical damage.\n").append(dies).append("\n").toString();
    }

    /**
     * Performs an arcane missile attack on a target BattleEntity.
     *
     * @param target The target BattleEntity to attack.
     * @return A string describing the result of the attack.
     */
    public String arcaneMissile(BattleEntity target) {
        int damage = Dice.valueBetween(1, 6) + this.mind;
        String aux = target.takeDamage(damage, "Psychical");
        return this.name + " uses Arcane Missile.\n" + target.getName() + " takes " + damage + " magical damage.\n" + aux + "\n";
    }

    /**
     * Takes damage, first reducing the shield before reducing health points.
     *
     * @param damage The amount of damage to take.
     * @param damageType The type of damage.
     * @return A string describing the result of taking damage.
     */
    @Override
    public String takeDamage(int damage, String damageType) {
        resetDamageMessage();

        if (this.shield > 0) {
            if (this.shield >= damage) {
                this.shield -= damage;
                damage = 0;
            } else {
                damage -= this.shield;
                this.shield = 0;
            }
        }

        this.hitPoints -= damage;
        if (this.hitPoints <= 0) {
            this.hitPoints = 0;
            this.damageMessage.append(this.name).append(" falls unconscious.\n");
        }

        return this.damageMessage.toString();
    }

    private void resetDamageMessage() {
        this.damageMessage.delete(0, damageMessage.length());
    }

    public String readABook() {
        return this.name + " is reading a book.";
    }

    @Override
    public void rollInitiative() {
        this.initiative = this.mind + Dice.valueBetween(1, 20);
    }
}