package project.business.entities.battle.monster;

import project.business.Dice;
import project.business.entities.battle.BattleEntity;

/**
 * Represents a boss monster in a game, extending the properties and behaviors of a BattleMonster.
 */
public class Boss extends BattleMonster {

    /**
     * The calculated damage result dealt by the boss.
     */
    protected int damageResult;

    /**
     * A variable to store a value from rolling a ten-sided dice (d10).
     */
    protected int d10;

    /**
     * Creates a new instance of Boss with the specified name, challenge, and encounter number.
     *
     * @param name      the name of the boss monster.
     * @param challenge the challenge level or rating of the boss monster.
     * @param encounter the order in which this boss monster is encountered.
     */
    public Boss(String name, String challenge, int encounter) {
        super(name, challenge, encounter);

        this.name = name;
        this.challenge = challenge;
        this.encounterNumber = encounter;
        this.whoToWho = new StringBuilder();
        this.attackMessage = new StringBuilder();
        this.damageMessage = new StringBuilder();
    }

    /**
     * Attacks the specified target BattleEntity and returns the attack message.
     *
     * @param target the BattleEntity to be attacked.
     * @return a String containing the attack message.
     */
    @Override
    public String attack(BattleEntity target) {
        resetMessages();
        whoToWho.append(this.name).append(" attacks ").append(target.getName()).append(".");
        return this.rollDiceAndCalculateDamageResult(target);
    }

    /**
     * Rolls dice and calculates the damage to be dealt to the target BattleEntity.
     * Also builds the attack message.
     *
     * @param target the BattleEntity to receive damage.
     * @return a String containing the attack message.
     */
    private String rollDiceAndCalculateDamageResult(BattleEntity target) {
        this.d10 = Dice.valueBetween(1, 10);
        this.damageResult = Dice.valueBetween(1, this.damageDice);

        if (d10 >= 2 && d10 <= 10) {
            if (d10 == 10) {
                this.damageResult = Dice.valueBetween(1, this.damageDice) * 2;
                attackMessage.append("Critical hit and deals ").append(damageResult).append(" ").append(this.damageType.toLowerCase()).append(" damage.");
            } else {
                this.damageResult = Dice.valueBetween(1, this.damageDice);
                attackMessage.append("Hits and deals ").append(damageResult).append(" ").append(this.damageType.toLowerCase()).append(" damage.");
            }
            this.damageMessage = new StringBuilder(target.takeDamage(damageResult, this.damageType));
        } else {
            attackMessage.append("Fails and deals 0 ").append(this.damageType.toLowerCase()).append(" damage.");
        }

        return whoToWho.toString() + "\n" + attackMessage.toString() + "\n" + damageMessage.toString() + "\n";
    }

    /**
     * Resets the messages related to attacks.
     */
    private void resetMessages() {
        whoToWho.delete(0, whoToWho.length());
        attackMessage.delete(0, attackMessage.length());
        damageMessage.delete(0, damageMessage.length());
    }

    /**
     * Takes the specified damage and returns a message about the damage taken.
     *
     * @param damage the amount of damage to be taken.
     * @return a String containing the message about the damage taken.
     */
    @Override
    public String takeDamage(int damage, String damageType) {
        resetDamageMessage();
        this.hitPoints -= damage;
        if (this.hitPoints <= 0) {
            this.hitPoints = 0;
            this.damageMessage.append(this.name).append(" dies.\n");
        }
        return this.damageMessage.toString();
    }

    /**
     * Resets the damage message.
     */
    private void resetDamageMessage() {
        this.damageMessage.delete(0, damageMessage.length());
    }
}