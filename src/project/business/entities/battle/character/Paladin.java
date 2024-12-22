package project.business.entities.battle.character;

import project.business.Dice;
import project.business.entities.battle.BattleEntity;
import project.business.entities.battle.monster.BattleMonster;

import java.util.List;

/**
 * Represents a Paladin character in a battle.
 * A Paladin is a type of Adventurer with specific attributes and behaviors.
 */
public final class Paladin extends Cleric {
    private String characterType;

    /**
     * Constructs a Paladin character with the given attributes.
     *
     * @param body         The body attribute representing physical strength.
     * @param mind         The mind attribute representing intelligence.
     * @param spirit       The spirit attribute representing willpower.
     * @param name         The name of the paladin.
     * @param xp           The experience points of the paladin.
     */
    public Paladin(int body, int mind, int spirit, String name, int xp) {
        super(body, mind, spirit, name, xp);
        this.characterType = "Paladin";
    }

    /**
     * Gets the party of characters that the paladin is in.
     */
    public void setParty(List<BattleCharacter> party) {
        this.party = party;
    }

    /**
     * Performs a motivation speech that increases the mind attribute of all characters in the party.
     * @return A string describing the result of the motivation speech.
     */
    @Override
    public String prayerOfGoodLuck() {
        int increase = Dice.valueBetween(1, 3);
        this.mind += increase;
        for (BattleCharacter character : this.party) {
            character.mind += increase;
        }
        return this.name + " uses Blessing of Good Luck. Their mind and the mind of their party increases by +" + increase + ".";
    }

    /**
     * Performs an attack on a target BattleEntity.
     *
     * @param target The target BattleEntity to attack.
     * @return A string describing the result of the attack.
     */
    @Override
    public String attack(BattleEntity target) {
        BattleMonster targetCharacter = (BattleMonster) target;
        if (targetCharacter.getHitPoints() <= ((BattleMonster) target).getHitPoints() / 2) {
            return prayerOfMassHealing();
        } else {
            return notOnMyWatch(target);
        }
    }

    /**
     * Performs an attack on a target BattleEntity.
     *
     * @param target The target BattleEntity to attack.
     * @return A string describing the result of the attack.
     */
    public String notOnMyWatch(BattleEntity target) {
        int damage = Dice.valueBetween(1, 8) + this.spirit;
        String aux = target.takeDamage(damage, "Psychical");
        return this.name + " attacks " + target.getName() + " with Not On My Watch.\n" + target.getName() + " takes " + damage + " psychical damage.\n" + aux + "\n";
    }

    /**
     * Performs a mass healing action.
     * @return A string describing the result of the mass healing.
     */
    public String prayerOfMassHealing() {
        int healing = Dice.valueBetween(1, 10) + this.mind;
        for (BattleCharacter character : this.party) {
            character.setHitPoints(healing);
        }
        return this.name + " uses Prayer of Mass Healing. All party members are healed for " + healing + " points.";
    }

    /**
     * Reduces psychic damage taken by half.
     *
     * @param damage The amount of damage to take.
     * @param type The type of damage to take.
     */
    @Override
    public String takeDamage(int damage, String type) {
        if (type.equals("Psychical")) {
            super.takeDamage(damage / 2, type);
        } else {
            super.takeDamage(damage, type);
        }
        return "\n";
    }
}