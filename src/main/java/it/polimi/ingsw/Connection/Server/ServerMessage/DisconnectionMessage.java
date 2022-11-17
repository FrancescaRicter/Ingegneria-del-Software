package it.polimi.ingsw.Connection.Server.ServerMessage;

import it.polimi.ingsw.Connection.Message;
import it.polimi.ingsw.Connection.MessageType;

/** Message sent from server : notifies a disconnection among the players connected to the game
 *
 **/
public class DisconnectionMessage extends Message {
    private static final long serialVersionUID = 1223124779;
    private String playerDisconnected;
    private String disconnectionMessage;

    /** Constructor
     * @param playerDisconnected nickName of the player that has left the game
     * @param disconnectionMessage string message that will be displayed to the players after they have been removed from the game
     **/
    public DisconnectionMessage(String playerDisconnected,String disconnectionMessage){
        super(playerDisconnected, MessageType.DISCONNECTED);
        this.playerDisconnected = playerDisconnected;
        this.disconnectionMessage = disconnectionMessage;
    }

    public String getPlayerDisconnected() {
        return playerDisconnected;
    }

    public String getDisconnectionMessage() {
        return disconnectionMessage;
    }
}
