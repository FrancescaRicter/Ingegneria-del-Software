package it.polimi.ingsw.Connection.Server.ServerMessage;

import it.polimi.ingsw.Connection.Message;
import it.polimi.ingsw.Connection.MessageType;
import it.polimi.ingsw.View.GameRepresentation;

import java.util.ArrayList;
import java.util.List;


public class StartGameMessage extends Message {
    private static final long serialVersionUID = 938410653255L;
    private int playerId;
    private GameRepresentation gameRepresentation;
    private String first ;

    public StartGameMessage(String nickName,int playerId, GameRepresentation gr,String playersTurn){
        super(nickName, MessageType.START_GAME);
        this.playerId = playerId;
        this.gameRepresentation = gr;
        this.first = playersTurn;
    }

    public String getFirst() {
        return first;
    }

    public GameRepresentation getGameRappresentation() {
        return gameRepresentation;
    }
}
