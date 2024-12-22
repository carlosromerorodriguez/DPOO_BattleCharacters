package project.business.entities.battle;

/**
 * Represents an entity capable of engaging in battle.
 * This class serves as a base for all battle-related entities,
 * providing common attributes and behaviors such as attacking,
 * taking damage, and checking if the entity is alive.
 * <p>
 * Each BattleEntity has a name, hit points, initiative, and can have messages
 * associated with the attack and damage taken during a battle.
 * </p>
 * <p>
 * Note that this class is abstract and must be subclassed to be used.
 * Subclasses must implement the {@link #attack(BattleEntity)} and
 * methods.
 * </p>
 */
public abstract class BattleEntity {
    /**
     * The hit points (health) of this BattleEntity.
     */
    protected int hitPoints;

    /**
     * The name of this BattleEntity.
     */
    protected String name;

    /**
     * The initiative value of this BattleEntity which might be used
     * for determining order of actions in a battle.
     */
    protected int initiative;

    /**
     * A message that holds information about the damage taken.
     */
    protected StringBuilder damageMessage;

    /**
     * A message that holds information about the interaction between entities.
     */
    protected StringBuilder whoToWho;

    /**
     * A message that holds information about the attack performed.
     */
    protected StringBuilder attackMessage;

    /**
     * Performs an attack on the specified target.
     *
     * @param target The BattleEntity to be attacked.
     * @return A string message describing the attack performed.
     */
    public abstract String attack(BattleEntity target);

    /**
     * Processes the damage taken by this BattleEntity.
     *
     * @param damage The amount of damage to be processed.
     * @return A string message describing the damage taken.
     */
    public abstract String takeDamage(int damage, String damageType);

    /**
     * Retrieves the name of this BattleEntity.
     *
     * @return The name of this BattleEntity.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Retrieves the initiative value of this BattleEntity.
     *
     * @return The initiative value.
     */
    public int getInitiative() {
        return this.initiative;
    }

    /**
     * Determines if this BattleEntity is alive based on its hit points.
     *
     * @return {@code true} if this BattleEntity has more than 0 hit points,
     *         {@code false} otherwise.
     */
    public boolean isAlive() {
        return this.hitPoints > 0;
    }
}