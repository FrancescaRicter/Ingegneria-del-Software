package it.polimi.ingsw.Connection.Server;

import it.polimi.ingsw.Connection.Client.ClientMessage.CreateLobbyMessage;
import it.polimi.ingsw.Connection.Client.ClientMessage.JoinLobbyMessage;
import it.polimi.ingsw.Connection.Message;
import it.polimi.ingsw.Connection.MessageType;
import it.polimi.ingsw.Connection.Server.ServerMessage.*;
import it.polimi.ingsw.Exceptions.NotPossibleActionException;
import it.polimi.ingsw.Model.Player;
import it.polimi.ingsw.Model.TowerColor;
import it.polimi.ingsw.Observer.Observer;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.*;

/**
 * Class that represents the entity of the server,this manages all the active games
 *
 */
public class Server implements Runnable {
    private final int Port = 1236;
    private final ServerSocket serverSocket = new ServerSocket(Port);
    private List<Lobby> availableLobbies = new ArrayList<>() ;                                   /* all games not started */
    private List<Lobby> allMatches = new ArrayList<>();                                          /* all games (started and not started) */

    private Map<ConnectionClientManager,VirtualView> clientsVirtualViews = new HashMap<>();      /* maps client thread on server to virtual view - set when client thread is created */

    /**
     * This map contains all clients that are connected to the server and maps their nickName to their client thread on server
     *
     * */
    private Map<String,ConnectionClientManager> clientNickNameConnection = new HashMap<>();      /* maps nickName with client thread */


    private Map<ConnectionClientManager,Lobby> playersConnectionsToLobby = new HashMap<>();      /* maps client thread to lobby if the client has entered a lobby */

    /**
     * Basic constructor method
     */
    public Server() throws IOException {}

    /**
     * Method that maps the NickName of the client to his thread on server
     *
     * @param Nickname     client nickname
     * @param connectionClientManager   client thread on server
     */
    public void addClientNickName(String Nickname, ConnectionClientManager connectionClientManager) {
        clientNickNameConnection.put(Nickname,connectionClientManager);
    }

    /**
     * Method that retrieves client's virtualView given the client nickName
     *
     * @param Nickname client nickName
     */
    public  VirtualView getClientVirtualView(String Nickname) {
        return clientsVirtualViews.get(clientNickNameConnection.get(Nickname));
    }

    /**
     * Method that handles all the messages sent by client
     *
     * @param message   the message that needs to be handled
     */
    public void handleMessage(Message message) {
        switch(message.getMessageType()) {
            case CREATE_LOBBY: {

                /* id of the new lobby created */
                int lobbyId = this.createLobby((CreateLobbyMessage) message);
                Lobby lobby = getAvailableLobby(lobbyId);
                playersConnectionsToLobby.put(clientNickNameConnection.get(message.getNickname()),lobby);

                if(lobby!=null) {
                    try{
                        addToLobby(message.getNickname(),lobby);

                        /*setting client/player's attributes in server and lobby*/
                        lobby.setPlayerConnection(0,clientNickNameConnection.get(message.getNickname()));
                        lobby.setPlayerVirtualView(0,clientNickNameConnection.get(message.getNickname()).getVirtualView());

                        /* setting controller in the virtualView */
                        clientNickNameConnection.get(message.getNickname()).getVirtualView().setController(lobby.getController());

                        /* setting the observer and observable in the MVC pattern */
                        lobby.getController().getModel().addObserver(clientNickNameConnection.get(message.getNickname()).getVirtualView());
                        lobby.getController().setVirtualView(0,this.getClientVirtualView(message.getNickname()));


                        lobby.getController().getVirtualView(0).addObserver((Observer) lobby.getController());
                        Message messageLobbyStatus = new LoginResponseMessage(message.getNickname(),lobbyId,getLobbyNickNames(lobbyId), getAvailableLobby(lobbyId).getNumMaxPlayers(), getAvailableLobby(lobbyId).isGameMode());
                        clientNickNameConnection.get(message.getNickname()).sendMessage(messageLobbyStatus);
                    }
                    catch (NotPossibleActionException e) {
                        Message ErrorMessage = new ErrorMessage(message.getNickname(),e.getMessage());
                        clientNickNameConnection.get(message.getNickname()).sendMessage(ErrorMessage);
                    }
                }
                else {
                    Message ErrorMessage = new ErrorMessage(message.getNickname(),"There was an error in creating the lobby. Please try again.");
                    clientNickNameConnection.get(message.getNickname()).sendMessage(ErrorMessage);
                }
                break;
            }
            case JOIN_LOBBY: {
                int indexPlayer=-1;
                try {
                    JoinLobbyMessage messageCasted = (JoinLobbyMessage) message;
                    int lobbyIndex = messageCasted.getLobbyID();
                    indexPlayer = this.addToLobby(message.getNickname(),getLobbyFromAllMatches(lobbyIndex));

                    Lobby lobby = getAvailableLobby(lobbyIndex);

                    /*setting client/player's attributes in server and lobby*/
                    lobby.setPlayerConnection(indexPlayer,clientNickNameConnection.get(message.getNickname()));
                    lobby.setPlayerVirtualView(indexPlayer, clientNickNameConnection.get(message.getNickname()).getVirtualView());
                    playersConnectionsToLobby.put(clientNickNameConnection.get(message.getNickname()),getAvailableLobby(lobbyIndex));

                    /* setting controller in the virtualView */
                    clientNickNameConnection.get(message.getNickname()).getVirtualView().setController(getAvailableLobby(lobbyIndex).getController());

                    /* setting the observer and observable in the MVC pattern */
                    lobby.getController().getModel().addObserver(clientNickNameConnection.get(message.getNickname()).getVirtualView());
                    lobby.getController().setVirtualView(indexPlayer,this.getClientVirtualView(message.getNickname()));
                    lobby.getController().getVirtualView(indexPlayer).addObserver((Observer) lobby.getController());

                    /*sends to the client his playerId for the game just entered*/
                    Message messagePlayerId = new PlayerIdMessage( message.getNickname(), indexPlayer);
                    clientNickNameConnection.get(message.getNickname()).sendMessage(messagePlayerId);

                    String lobbyIdString = String.valueOf(lobbyIndex);

                    /* checking if lobby is now full : cancel from available lobbies list and send init game message to all */
                    if (this.getAvailableLobby(((JoinLobbyMessage) message).getLobbyID()).getNumMaxPlayers() == this.getAvailableLobby(((JoinLobbyMessage) message).getLobbyID()).getController().getPlayersEntered()) {
                        Lobby lob = new Lobby();
                        for (Lobby l : availableLobbies) {
                            if (l.getLobbyId() == ((JoinLobbyMessage) message).getLobbyID()) {
                                lob = l;
                                InitGameMessage messageInit = new InitGameMessage("ToAllInLobby: " + lobbyIdString,MessageType.INIT_TOWER,l.getController().getAvailableTowers(),null);
                                broadcastMessageToLobby(messageInit,lobbyIndex);
                            }
                        }
                        { try {//the game has started, the lobby is not available for selection anymore
                            availableLobbies.remove(lob);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                        }
                    }
                    else {
                        /*everytime a player enters the updated lobby status is broadcast to all players in the lobby */
                        Message messageLobbyStatus = new LoginResponseMessage("ToAllInLobby: " + lobbyIdString, lobbyIndex, getLobbyNickNames(lobbyIndex), getAvailableLobby(lobbyIndex).getNumMaxPlayers(), getAvailableLobby(lobbyIndex).isGameMode());
                        this.broadcastMessageToLobby(messageLobbyStatus, lobbyIndex);
                    }
                }
                catch (NotPossibleActionException e) {
                    Message ErrorMessage = new ErrorMessage(message.getNickname(),e.getMessage());
                    clientNickNameConnection.get(message.getNickname()).sendMessage(ErrorMessage);
                }
                break;
            }
            case SHOW_TOWERS:{
                Lobby l = playersConnectionsToLobby.get(clientNickNameConnection.get(message.getNickname()));
                if(l.getNumMaxPlayers()!=4){
                    /*send the list of towers to choose from*/
                    Message messageTowers = new InitGameMessage(message.getNickname() ,MessageType.INIT_TOWER,playersConnectionsToLobby.get(clientNickNameConnection.get(message.getNickname())).getController().getAvailableTowers(), null);
                    clientNickNameConnection.get(message.getNickname()).sendMessage(messageTowers);
                }
                else{
                    List<String> players = null;
                    List<TowerColor> towerColor = null;
                    for(Player p: l.getController().getModel().getPlayers()){
                        if(p.getBoard().getTowerColor()!=null){
                            players.add(p.getNickname());
                            towerColor.add(p.getBoard().getTowerColor());}
                    }
                    Message serverMessageInitT = new InitGameMessage(message.getNickname(), MessageType.INIT_TOWER, l.getController().getAvailableTowers(), null,players,towerColor);
                    clientNickNameConnection.get(message.getNickname()).sendMessage(serverMessageInitT);
                }
                break;
            }
            case SHOW_CHARACTERS:{
                /*send the list of characters to choose from*/
                Message messageCharacters = new InitGameMessage(message.getNickname() , MessageType.INIT_CHARACTER,null,playersConnectionsToLobby.get(clientNickNameConnection.get(message.getNickname())).getController().getAvailableCharacters());
                clientNickNameConnection.get(message.getNickname()).sendMessage(messageCharacters);
                break;
            }
            case EXIT:{
                if(playersConnectionsToLobby.get(clientNickNameConnection.get(message.getNickname()))!=null){
                    System.out.println(message.getNickname() + " is exiting lobby " + getPlayersConnectionsToLobby(clientNickNameConnection.get(message.getNickname())));

                    Lobby l = playersConnectionsToLobby.remove(clientNickNameConnection.get(message.getNickname()));
                    deleteLobby(l,message.getNickname());
                    //int removedIndex = l.removePlayer(message.getNickname());

                    l = null;
                    break;
                }
                else {
                    ErrorMessage messageError = new ErrorMessage(message.getNickname(),"You haven't entered a lobby yet");
                    clientNickNameConnection.get(message.getNickname()).sendMessage(messageError);
                }
            }
            default: {
                break;}
        }
    }

    /**
     * Method that returns a list of all the NickNames of the players present in the given lobby
     *
     * @param lobbyId id of the lobby
     */
    public List <String> getLobbyNickNames(int lobbyId)
    {List <String> playersNames = new ArrayList<>();
        for(int i = 0; i< getAvailableLobby(lobbyId).getController().getPlayersEntered(); i++){
            playersNames.add(getAvailableLobby(lobbyId).getController().getModel().getPlayers().get(i).getNickname());
        }
        return playersNames;
    }


    /**
     * Method that indicates if the given Nickname is already associated to another player in the server
     *
     * @return true if the nickName is not associated to another player
     */
    public boolean isNicknameUnique(String Nickname) {
        return clientNickNameConnection.get(Nickname) == null;
    }

    /**
     * Method that sends a  message to all the players present in the given lobby
     *
     * @param message message to be sent
     * @param lobbyId the id of the lobby
     */
    public void broadcastMessageToLobby (Message  message, int lobbyId) {
        for (Map.Entry<Integer,ConnectionClientManager> set : getAvailableLobby(lobbyId).getPlayersConnections().entrySet()) {
            set.getValue().sendMessage(message);
        }
    }

    /**
     * Method that creates a new lobby with unique lobby id
     *
     * @param message message received by a client that asks to create a new lobby
     * @return the id of the new lobby just created
     */
    public int createLobby(CreateLobbyMessage message) {
        int idNewLobby = -1;
        if(allMatches.size()==0){
            idNewLobby = 0;
        }
        else {
            idNewLobby = allMatches.get(allMatches.size()-1).getLobbyId() + 1;
        }
        Lobby newLobby = new Lobby(message.getNumOfPlayers(),message.isGameMode());
        newLobby.setLobbyId(idNewLobby);
        allMatches.add(newLobby);
        availableLobbies.add(newLobby);
        return idNewLobby;
    }

    /**
     * Method that creates a player for a client and adds it to the given lobby
     *
     * @param playerNickName the player's Nickname
     * @param lobby the lobby to which the player will be added
     *
     */
    public int addToLobby (String playerNickName, Lobby lobby) throws NotPossibleActionException {
        return lobby.addPlayer(playerNickName);
    }


    /**
     * Getter method
     *
     * @return list of available lobbies, these are all the matches that have not yet started (still missing one or more players)
     */
    public List<Lobby> getAvailableLobbies() {
        return availableLobbies;
    }


    /**
     * Method that returns the object Lobby given the lobbyId even for lobbies that have reached the total number of players
     *
     * @param lobbyId the id of the lobby requested
     * @return the lobby if found or null
     */
    public Lobby getLobbyFromAllMatches(int lobbyId) {
        for (Lobby l: allMatches) {
            if (l.getLobbyId()==lobbyId) {
                return l;
            }
        }
        return null;
    }

    /**
     * Method that returns the object Lobby given the lobbyId - only for lobbies that haven't reached the total number of players yet
     *
     * @param lobbyId the id of the lobby requested
     * @return the lobby if a correspondence is found or null
     */
    public Lobby getAvailableLobby(int lobbyId) {
        for (Lobby l: availableLobbies) {
            if (l.getLobbyId()==lobbyId) {
                return l;
            }
        }
        return null;
    }

    /**
     * Override of the run method inherited by thread, this method accepts the connections of the clients that try to connect to the server creating a thread
     * for each client and a virtual view
     *
     */
    @Override
    public void run() {
        while(true) {
            try{
                Socket newSocket = serverSocket.accept();
                ConnectionClientManager clientConnection = new ConnectionClientManager(newSocket,this);
                Thread clientThread = new Thread(clientConnection);
                clientThread.start();
                VirtualView virtualView = new VirtualView(clientConnection);
                clientsVirtualViews.put(clientConnection,virtualView);
                clientConnection.setVirtualView(virtualView);
            }
            catch(Exception e1){
                System.out.println("Error in connection! Server closing... ");
                try {
                    this.disconnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }

    }

    /**
     * Method retrieves the lobby that contains the player associated with the given thread (ConnectionClientManager) on server side
     *
     * @param connectionClientManager client thread
     *
     */
    public Lobby getPlayersConnectionsToLobby(ConnectionClientManager connectionClientManager) {
        return playersConnectionsToLobby.get(connectionClientManager);
    }

    /**
     * Method that deletes the given lobby
     *
     * @param lobby the lobby to delete
     * @param playerExited the nickName of the player that has exited or disconnected from the game
     *
     */
    public void deleteLobby(Lobby lobby, String playerExited) {
        availableLobbies.remove(lobby);
        allMatches.remove(lobby);

        /*handle other players in the same lobby */
        for(String p : lobby.getPlayersNickNames()){
            if(!Objects.equals(p, playerExited)){
                playersConnectionsToLobby.remove(clientNickNameConnection.get(p));
                clientsVirtualViews.get(clientNickNameConnection.get(p)).controller=null;
                DisconnectionMessage removedMessage = new DisconnectionMessage(playerExited, playerExited + " has left the game. You have been removed from the current match, choose a new one.");
                clientsVirtualViews.get(clientNickNameConnection.get(p)).removeObservers();
                clientsVirtualViews.get(clientNickNameConnection.get(p)).update(removedMessage);
                clientsVirtualViews.get(clientNickNameConnection.get(p)).setController(null);

            }
        }
    }

    /**
     * Method that deletes all references of a client when he disconnects, destroys the lobby and
     * removes all the other players placing them in the waiting area, in order to choose another lobby
     *
     * @param clientConnection    client thread
     */
    public void closeClientConnection(ConnectionClientManager clientConnection) {
        System.out.println(clientConnection.getClientNickName() + " is disconnecting from server..");

        //removing player's references
        String exitedPlayer = clientConnection.getClientNickName();
        clientNickNameConnection.remove(exitedPlayer);
        clientsVirtualViews.remove(clientConnection);

        //removing lobby
        Lobby lobby=null;
        if(playersConnectionsToLobby.containsKey(clientConnection)){
            lobby = playersConnectionsToLobby.remove(clientConnection);}

        if(lobby!=null){
            deleteLobby(lobby,exitedPlayer);}

        lobby = null;
    }

    /**
     * Method that closes the socket
     */
    public void disconnection() throws IOException {
        serverSocket.close();
    }

}