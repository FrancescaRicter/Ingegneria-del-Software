package it.polimi.ingsw.Connection.Client.ClientMessage;

import it.polimi.ingsw.Connection.Message;
import it.polimi.ingsw.Connection.MessageType;

/**
 * Message sent from client : first message sent by the client, this specifies the nickName that will be linked to that client
 *
 * */
public class NickNameMessage extends Message {
    private static final long serialVersionUID = 234569876542345L;

    /**
     * Constructor
     *
     * @param nickname  client nickname
     * */
    public NickNameMessage(String nickname) {
        super(nickname, MessageType.NICK_NAME_INSERTED);
    }

}
