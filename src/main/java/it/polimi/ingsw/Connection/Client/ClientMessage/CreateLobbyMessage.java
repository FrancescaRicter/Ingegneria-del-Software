package it.polimi.ingsw.Connection.Client.ClientMessage;

import it.polimi.ingsw.Connection.Message;
import it.polimi.ingsw.Connection.MessageType;

/**
 * Message sent from client : the client asks to create a new lobby setting the given parameters
 *
 * */
public class CreateLobbyMessage extends Message {
    private static final long serialVersionUID= 123123;
    private boolean gameMode;     /*true->expert*/
    private String nickname;
    private int numOfPlayers;

    /**
     * Constructor
     *
     * @param nickname  client nickname
     * @param gameMode  the chosen game mode
     * @param numOfPlayers the number of players
     * */
    public CreateLobbyMessage(String nickname, boolean gameMode, int numOfPlayers){
        super(nickname, MessageType.CREATE_LOBBY);
        this.nickname= nickname;
        this.gameMode = gameMode;
        this.numOfPlayers = numOfPlayers;
    }

    public int getNumOfPlayers() {
        return numOfPlayers;
    }

    public boolean isGameMode() {
        return gameMode;
    }

}
