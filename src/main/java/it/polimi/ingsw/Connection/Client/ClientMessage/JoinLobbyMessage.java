package it.polimi.ingsw.Connection.Client.ClientMessage;
import it.polimi.ingsw.Connection.Message;
import it.polimi.ingsw.Connection.MessageType;

/**
 * Message sent from client : the client requests to join a lobby given the id
 *
 * */
public class JoinLobbyMessage extends Message {
    private static final long serialVersionUID = 43525131;
    private String nickname;
    private int lobbyId;

    /**
     * Constructor
     *
     * @param nickname  client nickname
     * @param lobbyId the id of the lobby the player wants to join
     * */
    public JoinLobbyMessage(String nickname, int lobbyId){
        super(nickname, MessageType.JOIN_LOBBY);
        this.nickname= nickname;
        this.lobbyId = lobbyId;
    }

    public int getLobbyID() {
        return lobbyId;
    }
}
