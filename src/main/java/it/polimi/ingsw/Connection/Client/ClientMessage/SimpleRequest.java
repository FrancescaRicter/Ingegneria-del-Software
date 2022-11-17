package it.polimi.ingsw.Connection.Client.ClientMessage;

import it.polimi.ingsw.Connection.Message;
import it.polimi.ingsw.Connection.MessageType;

/**
* Message sent from client : simple client command that doesn't require parameters (ex. SHOW_LOBBIES - SHOW_TOWERS - SHOW_CHARACTERS - EXIT)
*
* */
public class SimpleRequest extends Message{
    private static final long serialVersionUID = 43562643043L;

    /**
     * Constructor
     *
     * @param nickname  client nickname
     * @param requestType  the client command requested. The possible choices of 'MessageType' are : SHOW_LOBBIES, EXIT, SHOW_TOWERS, SHOW_CHARACTERS)
     * */
    public SimpleRequest(String nickname,MessageType requestType) {
        super(nickname, requestType);
    }

}
