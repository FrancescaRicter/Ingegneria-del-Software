package it.polimi.ingsw.Connection.Server;

import it.polimi.ingsw.Controller.Controller;
import it.polimi.ingsw.Exceptions.InitGameException;
import it.polimi.ingsw.Exceptions.NotPossibleActionException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that keeps all the information of a game
 */
public class Lobby implements Serializable {
    private transient Controller controller;
    private int lobbyId;
    private int numMaxPlayers;
    private boolean gameMode; /* true-> expert game   false-> normal game */
    private transient Map <Integer,VirtualView> playersVirtualViews = new HashMap<>();  /* mapping between players index and virtualViews */
    private transient Map <Integer,ConnectionClientManager> playersConnections = new HashMap<>() ;  /*mapping between player index and client thread */
    private List<String> playersNickNames = new ArrayList<>();
    public Lobby(){}

    /**
     * Constructor
     *
     * @param numPlayers    max number of players fot the game
     * @param isExpert      true if the game is expert
     */
    public Lobby(int numPlayers, boolean isExpert) {
        this.controller = new Controller(isExpert,numPlayers);
        this.numMaxPlayers = numPlayers;
        this.gameMode = isExpert;
    }

    /**
     * Method that adds a player to a lobby
     *
     * @param Nickname    the String that will be associated to that player as NickName
     * @return            player id
     *
     * @throws  InitGameException
     */
    public int addPlayer(String Nickname) throws NotPossibleActionException {
        int i = controller.setNewPlayer(Nickname);
        playersNickNames.add(Nickname);
        return i;
    }

    /**
     * Method that removes a player from a lobby and all his references related to that lobby such as ClientConnectionManager, VirtualView, playerId
     *
     * @param nickName    the String associated to that player as NickName
     * @return the original player Index if successfully removed, -1 otherwise
     */
    public int removePlayer(String nickName){
        int playerId = -1;
        playerId = controller.removePlayer(nickName);
        playersNickNames.remove(nickName);
        playersVirtualViews.remove(playerId);
        playersConnections.remove(playerId);
        return playerId;
    }

    public void setPlayerConnection(int playerIndex, ConnectionClientManager connection) {
        System.out.println("in lobby");
        playersConnections.put(playerIndex,connection);
        System.out.println("bkfffg");
    }

    public void setPlayerVirtualView(int playerIndex, VirtualView virtualView) {
        playersVirtualViews.put(playerIndex,virtualView);
    }

    public Map<Integer, ConnectionClientManager> getPlayersConnections() {
        return playersConnections;
    }

    public void setLobbyId(int lobbyId) {
        this.lobbyId = lobbyId;
    }

    public boolean isGameMode() {
        return gameMode;
    }

    public int getNumMaxPlayers() {
        return numMaxPlayers;
    }

    public int getLobbyId() {
        return lobbyId;
    }

    public Controller getController() {
        return controller;
    }

    public List<String> getPlayersNickNames() {
        return playersNickNames;
    }
}
