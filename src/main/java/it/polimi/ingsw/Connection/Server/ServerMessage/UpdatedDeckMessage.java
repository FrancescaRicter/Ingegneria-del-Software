package it.polimi.ingsw.Connection.Server.ServerMessage;
import it.polimi.ingsw.Connection.Message;
import it.polimi.ingsw.Connection.MessageType;
import it.polimi.ingsw.Model.Card;

import java.util.List;

/**
 * Message sent from server: sends the updated deck to the player
*/
public class UpdatedDeckMessage extends Message {
    private static final long serialVersionUID = 346334326;
    private List<Card> deck;
    private List<Card> playedCards;

    /**
     * Constructor
     * @param nickName nickName of the player to whom the message will be sent
     * @param deck updated deck of the player receiving the message
     */
    public UpdatedDeckMessage(String nickName,List<Card> deck, List<Card> playedCards){
        super(nickName, MessageType.DECK);
        this.deck = deck;
        this.playedCards = playedCards;
    }

    public List<Card> getPlayedCards() {
        return playedCards;
    }

    public List<Card> getDeck() {
        return deck;
    }
}
