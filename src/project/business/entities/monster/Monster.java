package project.business.entities.monster;

/**
 * Record class that represents a monster.
 * This class has all the getters and setters needed to access the attributes of the monster,
 * without the need of writing them.
 *
 * @param name Name of the monster.
 * @param challenge Challenge rating of the monster.
 * @param xp Experience points awarded for defeating the monster.
 * @param hitPoints HitPoints of the monster.
 * @param initiative Initiative of the monster.
 * @param damageDice Damage dice of the monster.
 * @param damageType Damage type of the monster.
 */
public record Monster (String name, String challenge, int xp, int hitPoints, int initiative, String damageDice, String damageType){}