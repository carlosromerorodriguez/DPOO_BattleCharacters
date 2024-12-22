package project.business.entities.battle.character;

import project.business.entities.battle.BattleEntity;

import java.util.List;

public class Champion extends Warrior {
    private final String characterType;
    public Champion(int body, int mind, int spirit, String name, int xp) {
        super(body, mind, spirit, name, xp);
        this.characterType = "Champion";
    }

    public void setParty(List<BattleCharacter> party) {
        this.party = party;
    }

    @Override
    protected int calculateHitPoints() {
        return super.calculateHitPoints() + (this.body * this.level);
    }

    public String makeSelfMotivationSpeech() {
        this.spirit += 1;
        for (BattleCharacter character : party) {
            character.spirit += 1;
        }
        return this.name + " uses Motivational Speech. Everyone’s Spirit increases in +1.";
    }


    @Override
    public String attack(BattleEntity target) {
        return super.attack(target);
    }

    public String improvedBandageTime() {
        int healAmount = this.maxHitPoints - this.hitPoints;
        this.hitPoints = this.maxHitPoints;
        return this.name + " uses Improved Bandage Time. Heals " + healAmount + " hit points.";
    }

    public String getCharacterType() {
        return this.characterType;
    }
}