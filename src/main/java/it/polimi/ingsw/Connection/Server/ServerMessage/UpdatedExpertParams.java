package it.polimi.ingsw.Connection.Server.ServerMessage;
import it.polimi.ingsw.Connection.Message;
import it.polimi.ingsw.Connection.MessageType;
import it.polimi.ingsw.Model.CharacterCard;

import java.util.ArrayList;
import java.util.List;

/**
 * Message sent from server: updated parameters for an expert game if any variations have occurred
 */
public class UpdatedExpertParams extends Message {
    private static final long serialVersionUID= 1111345178;
    private List<CharacterCard> specialCharacterCardList;
    private List<Integer> Coins = new ArrayList<Integer>();

    /**
     * Constructor
     * @param specialCharacterCardList list of expert cards
     * @param Coins the list of coins of the players order following playerId order
     */
    public UpdatedExpertParams(List<CharacterCard> specialCharacterCardList, List<Integer> Coins){
        super("To all", MessageType.EXPERT_PARAMETERS);
        this.specialCharacterCardList= specialCharacterCardList;
        this.Coins = Coins;
    }

    public List<Integer> getCoins() { return this.Coins; }

    public List<CharacterCard> getSpecialCharacterCardList() {
        return specialCharacterCardList;
    }
}
