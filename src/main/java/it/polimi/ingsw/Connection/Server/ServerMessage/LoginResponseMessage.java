package it.polimi.ingsw.Connection.Server.ServerMessage;
import it.polimi.ingsw.Connection.Message;
import it.polimi.ingsw.Connection.MessageType;

import java.util.List;

//Server notifies if the nickname and the connection are valid
public class LoginResponseMessage extends Message {
    private static final long serialVersionUID = 12133432;
    private boolean isValidNickname;
    private int lobbyId;

    private String nickname;
    private List<String> players;
    private int maxPlayers;
    private boolean gameMode; /* --> true = expertGame */


    public boolean isGameMode() {
        return gameMode;
    }

    public int getLobbyId() {
        return lobbyId;
    }

    public LoginResponseMessage(String nickname, int lobbyId,  List<String> players, int maxPlayers, boolean gameMode){
        super(nickname,MessageType.LOGIN_REPLY);

        this.lobbyId = lobbyId;
        this.players = players;
        this.maxPlayers = maxPlayers;
        this.gameMode = gameMode;
    }
    public List<String> getPlayers() {
        return players;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }
}
