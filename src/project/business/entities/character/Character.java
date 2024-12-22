package project.business.entities.character;

import com.google.gson.annotations.SerializedName;

/**
 * Record class that represents a character.
 * This class has all the getters and setters needed to access the attributes of the monster,
 * without the need of writing them.
 *
 * @param name Name of the character.
 * @param player Name of the player that controls the character.
 * @param xp Experience points of the character.
 * @param body Body points of the character.
 * @param mind Mind points of the character.
 * @param spirit Spirit points of the character.
 * @param clas Class of the character.

 */
public record Character(String name, String player, int xp, int body, int mind, int spirit, @SerializedName("class") String clas) {}
