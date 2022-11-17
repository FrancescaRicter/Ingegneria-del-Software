package it.polimi.ingsw.Connection.Server.ServerMessage;

import it.polimi.ingsw.Connection.Message;
import it.polimi.ingsw.Connection.MessageType;

public class PlayerIdMessage extends Message {
    private static final long serialVersionUID = 43525156563L;
    private int playerId;

    public PlayerIdMessage(String nickname,int playerId) {
        super(nickname, MessageType.PLAYER_ID);
        this.playerId=playerId;
    }

    public int getPlayerId() {
        return playerId;
    }
}
