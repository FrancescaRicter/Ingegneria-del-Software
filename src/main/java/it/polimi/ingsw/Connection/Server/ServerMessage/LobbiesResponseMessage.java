package it.polimi.ingsw.Connection.Server.ServerMessage;

import it.polimi.ingsw.Connection.Message;
import it.polimi.ingsw.Connection.MessageType;
import it.polimi.ingsw.Connection.Server.Lobby;

import java.util.List;

/**
 * Message sent from server : sends the updated list of all the available lobbies that the player can join
 *
 * */
public class LobbiesResponseMessage extends Message {
    private static final long serialVersionUID = 4342352456673L;
    private List<Lobby> lobbies;

    /**
     * Constructor
     * @param nickName Nickname of the player to whom the message will be sent
     * @param lobbies list of available lobbies
     * */
    public LobbiesResponseMessage(String nickName,List<Lobby> lobbies) {
        super(nickName, MessageType.LOBBIES_RESPONSE);
        this.lobbies = lobbies;
    }

    public List<Lobby> getLobbies() {
        return lobbies;
    }
}
