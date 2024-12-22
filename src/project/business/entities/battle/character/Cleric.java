package project.business.entities.battle.character;

import project.business.Dice;
import project.business.entities.battle.BattleEntity;

import java.util.List;

/**
 * Represents a Cleric character in a battle.
 * A Cleric is a type of Adventurer with specific attributes and behaviors.
 */
public class Cleric extends BattleCharacter {
    private boolean evolvedToPaladin;
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
    public Cleric(int body, int mind, int spirit, String name, int xp) {
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
        this.evolvedToPaladin = false;
        this.characterType = "Cleric";
        this.damageType = "Magical";
    }

    public void setParty(List<BattleCharacter> party) {
        this.party = party;
    }

    public String getDamageType() {
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
     * Performs an attack on a target BattleEntity.
     *
     * @param target The target BattleEntity to attack.
     * @return A string describing the result of the attack.
     */
    @Override
    public String attack(BattleEntity target) {
        BattleCharacter targetCharacter = (BattleCharacter) target;
        if (targetCharacter.getHitPoints() <= targetCharacter.maxHitPoints / 2) {
            return prayerOfHealing(target);
        } else {
            return notOnMyWatch(target);
        }
    }

    @Override
    public String takeDamage(int damage, String damageType) {
        return null;
    }

    public boolean anyPartyMemberNeedsHealing() {
        for (BattleCharacter character : party) {
            if (character.getHitPoints() <= character.maxHitPoints / 2) {
                return true;
            }
        }
        return false;
    }

    /**
     * Performs a healing prayer on a target BattleEntity.
     *
     * @param target The target BattleEntity to heal.
     * @return A string describing the result of the healing.
     */
    public String prayerOfHealing(BattleEntity target) {
        BattleCharacter targetCharacter = (BattleCharacter) target;
        int healing = Dice.valueBetween(1, 10) + this.mind;
        targetCharacter.setHitPoints(healing);
        return this.name + " uses Prayer of Healing. " + target.getName() + " is healed for " + healing + " points.";
    }

    /**
     * Performs an attack on a target BattleEntity.
     *
     * @param target The target BattleEntity to attack.
     * @return A string describing the result of the attack.
     */
    public String notOnMyWatch(BattleEntity target) {
        int damage = Dice.valueBetween(1, 4) + this.spirit;
        String aux = target.takeDamage(damage, "psychical");
        return this.name + " uses Not On My Watch. " + target.getName() + " takes " + damage + " psychical damage." + aux;
    }

    /**
     * Performs a self-healing action.
     *
     * @return A string describing the result of the self-healing.
     */
    public String prayerOfSelfHealing() {
        int healing = Dice.valueBetween(1, 10) + this.mind;
        this.setHitPoints(healing);
        return this.name + " uses Prayer of Self-Healing. They are healed for " + healing + " points.";
    }

    /**
     * Performs a motivation speech that increases the mind attribute of all characters in the party.
     * @return A string describing the result of the motivation speech.
     */
    public String prayerOfGoodLuck() {
        this.mind += 1;
        for (BattleCharacter character : this.party) {
            character.mind += 1;
        }
        return this.name + " uses Prayer of Good Luck. Their mind and the mind of their party increases by +1.";
    }

    protected String checkClericLevelUp() {
        if (this.level >= 5 && !evolvedToPaladin) {
            this.characterType = "Paladin";
            this.evolvedToPaladin = true;
            return "\n" + this.name + " evolves to " + this.characterType + "!";
        }
        return "";
    }

    @Override
    public void rollInitiative() {
        this.initiative = Dice.valueBetween(1, 10) + this.spirit;
    }
}