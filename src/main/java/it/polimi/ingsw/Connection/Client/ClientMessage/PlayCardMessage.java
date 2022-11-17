package it.polimi.ingsw.Connection.Client.ClientMessage;

import it.polimi.ingsw.Connection.Message;
import it.polimi.ingsw.Connection.MessageType;
import it.polimi.ingsw.Model.Card;

/**
 * Message sent from client : message that indicates the card that the player wants to play (assistant or character)
 *
 * */
public class PlayCardMessage extends Message {
    private static final long serialVersionUID = 547456534;
    private int card;
    private boolean isExpertCard;   /*true--> expertCard */
    private String parameters = null;

    /**
     * Constructor for an assistant card play
     *
     * @param nickname  client nickname
     * @param card position of the card in the deck
     * */
    public PlayCardMessage(String nickname,int card){
        super(nickname, MessageType.PLAY_CARD);
        this.card = card;
        this.isExpertCard=false;
    }

    /**
     * Constructor for a character card play
     *
     * @param nickname  client nickname
     * @param card position of the card un the table
     * @param parameters this value indicates additional parameters that are needed to play a special card
     * */
    public PlayCardMessage(String nickname,int card, String parameters){
        super(nickname, MessageType.PLAY_CARD);
        this.card = card;
        this.isExpertCard=true;
        this.parameters=parameters;
    }

    public String getParameters() {
        return parameters;
    }

    public int getCard() {
        return card;
    }

    public boolean isExpertCard() {
        return isExpertCard;
    }
}
