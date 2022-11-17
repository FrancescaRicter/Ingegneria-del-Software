package it.polimi.ingsw.Model;

import it.polimi.ingsw.Exceptions.InvalidInputException;

import java.io.Serializable;

/**
 * Every character card that executes a "special" effect.
 * Definition of special effect is every effect that modify the game rules,
 * or an effect that is activated for a period of time when
 * human interaction need to be processed differently from the normal execution (without effect activated).
 */
public abstract class SpecialCharacterCard extends ExpertGame implements CharacterCard, Serializable {
    protected transient ExpertGame fatherGame;
    protected int Cost;
    protected int varCost = 0;
    protected boolean needsParameters = false;
    protected int playerId;

    /**
     * Constructor for every special card, set references to every fatherGame's attribute, card's fatherGame attribute and cost
     * SpecialCharacterCard is the son of the class expert game, allowing every special card able to freely
     * modify the game's rules just by overriding the game's methods or add new methods if needed.
     * @param game the game containing the characters card
     */
    public SpecialCharacterCard(ExpertGame game, int Cost) {
        this.lastTurn = game.lastTurn;
        this.Cost = Cost;
        this.fatherGame = game;
        this.CharacterCard = game.CharacterCard;
        this.Coins = game.Coins;
        this.NumPlayers = game.NumPlayers;
        this.NumOfStudentsOnCloud = game.NumOfStudentsOnCloud;
        this.NumOfStartTower = game.NumOfStartTower;
        this.firstPlayer = game.firstPlayer;
        this.Teams = game.Teams;
        this.Players = game.Players;
        this.actionPlayers = game.actionPlayers;
        this.Islands = game.Islands;
        this.Bag = game.Bag;
        this.Clouds = game.Clouds;
        this.redProfessor = game.redProfessor;
        this.yellowProfessor = game.yellowProfessor;
        this.pinkProfessor = game.pinkProfessor;
        this.blueProfessor = game.blueProfessor;
        this.greenProfessor = game.greenProfessor;
    }

    /**
     * True: The Card's effect needs additional parameters to be correctly executed.
     * False: Otherwise.
     */
    public boolean needPars(){ return needsParameters; }

    /**
     * Return the game without the effect activated.
     * @return
     */
    public ExpertGame getFatherGame(){ return fatherGame; }

    /**
     * Each card manage the coins of the card and the player involved in the played effect.
     * @param playerId
     */
    @Override
    public void useCard(int playerId){
        removeCoins(getPlayers().get(playerId), Cost + varCost);
        if (varCost==0)
            varCost++;
    }

    /**
     * Implementation of the Interface's method: Manage coins and return this card, which is the game with activated effect
     * @param playerId id of the player who played the effect
     * @param parameters contains all optional parameters for the effect, the card validate the string and extract the parameters; "" if the parameters are not required
     * @return game with modified rules by the effect
     * @throws InvalidInputException parameters are not correct
     */
    @Override
    public ExpertGame Effect(int playerId, String parameters) throws InvalidInputException {
        useCard(playerId);
        return this;
    }

    public int getCost(){ return Cost; }

    public int getVarCost(){ return varCost; }

}
