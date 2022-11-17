package it.polimi.ingsw.Model;

import it.polimi.ingsw.Exceptions.InvalidInputException;

import java.io.Serializable;
import java.util.List;

/**
 * Interface for method unification for every Character Card, every method that need to
 * be used without knowing the class of character chard needs to be added here first.
 */
public interface CharacterCard extends Serializable {

    void useCard(int playerId);

    /**
     * Try to activate the card's effect
     * @param playerId id of the player who played the effect
     * @param parameters contains all optional parameters for the effect, the card validate the string and extract the parameters; "" if the parameters are not required
     * @return
     * @throws InvalidInputException Invalid parameters
     */
    ExpertGame Effect(int playerId, String parameters) throws InvalidInputException;

    ExpertGame getFatherGame();

    int getCost();

    int getVarCost();

    String getEffectDescription();

    int getCharacterNum();

    boolean needPars();
}
