package it.polimi.ingsw.Model;

import it.polimi.ingsw.Exceptions.InvalidInputException;

import java.io.Serializable;
import java.util.Map;

public class CharacterCard8 extends SpecialCharacterCard implements Serializable {

    public CharacterCard8(ExpertGame game) {
        super(game, 2);
        needsParameters = false;
    }

    public ExpertGame Effect(int playerId, String parameters) throws InvalidInputException {
        if (!parameters.equals(""))
            throw new InvalidInputException("Invalid Input: this card doesn't take any parameter");
        else return super.Effect(playerId, parameters);
    }

    @Override
    public String getEffectDescription() {
        return "During the influence calculation this turn, you count as having 2 more influence.\n" +
                "THIS CARD TAKES NO PARAMETERS";
    }

    @Override
    public boolean isTowerPlaceable(Team team, Island island, Map<Team, Integer> currentInfluence) {
        currentInfluence.replace(team, currentInfluence.get(team)+2);
        return super.isTowerPlaceable(team, island, currentInfluence);
    }

    @Override
    public boolean isTowerPlaceable(Player player, Island island, Map<Player, Integer> currentInfluence) {
        currentInfluence.replace(player, currentInfluence.get(player)+2);
        return super.isTowerPlaceable(player, island, currentInfluence);
    }

    @Override
    public int getCharacterNum(){
        return 8;
    }
}
