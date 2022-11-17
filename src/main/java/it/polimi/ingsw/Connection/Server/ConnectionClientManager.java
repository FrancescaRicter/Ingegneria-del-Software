package it.polimi.ingsw.Connection.Server;

import it.polimi.ingsw.Connection.Client.ClientMessage.CreateLobbyMessage;
import it.polimi.ingsw.Connection.Client.ClientMessage.JoinLobbyMessage;
import it.polimi.ingsw.Connection.Message;
import it.polimi.ingsw.Connection.Server.ServerMessage.ErrorMessage;
import it.polimi.ingsw.Connection.Server.ServerMessage.LobbiesResponseMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * The ConnectionClientManager implements Runnable, each instance of this class is a thread associated to a client connected
 */
public class ConnectionClientManager implements Runnable {
    private final Socket socket;
    private final Server server;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private boolean connected;
    private String clientNickName;

    /* created when the client-thread is created, after client connection si accepted by server */
    private VirtualView virtualView;

    /**
     * Constructor
     *
     * @param socket   the socket of the server
     * @param server   the instance that represents the server entity
     */
    public ConnectionClientManager(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
        this.connected = true;       /* when the thread is created the client is connected */
    }

    /**
     * Method that delivers messages based on the 'Message Type' to the right entity that will handle them
     *
     * @param message    message received by the thread from the specific client
     */
    public synchronized void deliverMessage(Message message) {
        switch (message.getMessageType()) {
            case NICK_NAME_INSERTED: {
                if(server.isNicknameUnique(message.getNickname())){
                    server.addClientNickName(message.getNickname(),this);
                    this.clientNickName=message.getNickname();
                    Message messageLobbiesList = new LobbiesResponseMessage(message.getNickname(),server.getAvailableLobbies());
                    this.sendMessage(messageLobbiesList);
                }
                else {
                    Message ErrorMessage = new ErrorMessage(message.getNickname(),"The chosen NickName is already taken. Please try a different one!");
                    this.sendMessage(ErrorMessage);
                }
                break;
            }
            case SHOW_LOBBIES:{
                if(server.getPlayersConnectionsToLobby(this)==null){
                    Message updatedLobbiesList = new LobbiesResponseMessage(message.getNickname(),server.getAvailableLobbies());
                    this.sendMessage(updatedLobbiesList);
                }
                else {
                    Message ErrorMessage = new ErrorMessage(message.getNickname(),"You first have to leave the current lobby if you desire to view other lobbies!");
                    this.sendMessage(ErrorMessage);
                }
                break;
            }
            case CREATE_LOBBY:{
                if(server.getPlayersConnectionsToLobby(this)==null){
                    CreateLobbyMessage castedMessage = (CreateLobbyMessage) message;
                    if(castedMessage.getNumOfPlayers()>1 && castedMessage.getNumOfPlayers()<5){
                        server.handleMessage(castedMessage);
                    }
                    else {
                        Message ErrorMessage = new ErrorMessage(message.getNickname(),"Invalid number of players!");
                        this.sendMessage(ErrorMessage);
                    }
                }
                else {
                    Message ErrorMessage = new ErrorMessage(message.getNickname(),"You can't create a new lobby, you first have leave your current one!");
                    this.sendMessage(ErrorMessage);
                }
                break;
            }
            case JOIN_LOBBY:{
                if(server.getPlayersConnectionsToLobby(this)==null){
                    JoinLobbyMessage castedMessage = (JoinLobbyMessage) message;
                    if(server.getAvailableLobby(castedMessage.getLobbyID())!=null) {
                        server.handleMessage(message);
                    }
                    else {
                        Message ErrorMessage = new ErrorMessage(message.getNickname(),"The lobby selected is not available!");
                        this.sendMessage(ErrorMessage);
                    }
                }
                else {
                    Message ErrorMessage = new ErrorMessage(message.getNickname(),"You have already joined a lobby! Leave your current one if you desire to join a new one!");
                    this.sendMessage(ErrorMessage);
                }
                break;
            }
            case SHOW_TOWERS:{
                if(server.getPlayersConnectionsToLobby(this)!=null){
                    server.handleMessage(message);
                }
                else {
                    Message ErrorMessage = new ErrorMessage(message.getNickname(),"You have to join a lobby before you can choose your tower color!");
                    this.sendMessage(ErrorMessage);
                }
                break;
            }
            case SHOW_CHARACTERS:
                if(server.getPlayersConnectionsToLobby(this)!=null){
                    server.handleMessage(message);
                }
                else {
                    Message ErrorMessage = new ErrorMessage(message.getNickname(),"You have to join a lobby before you can choose your character!");
                    this.sendMessage(ErrorMessage);
                }
                break;
            case EXIT:{
                if(server.getPlayersConnectionsToLobby(this)!=null){
                    server.handleMessage(message);
                }
                else {
                    Message ErrorMessage = new ErrorMessage(message.getNickname(),"Invalid! You haven't joined a lobby!");
                    this.sendMessage(ErrorMessage);
                }
                break;
            }
            default:     /* the other messages are all related to a specific match */{
                if(server.getPlayersConnectionsToLobby(this)!=null){
                    virtualView.receiveMessage(message);
                }
                else {
                    Message ErrorMessage = new ErrorMessage(message.getNickname(),"You have to select your tower color and your deck Character!");
                    this.sendMessage(ErrorMessage);
                }
                break;
            }
        }
    }

    /**
     * Delivers the message that the server sends to the specific client via thread
     *
     * @param message
     */
    public synchronized void sendMessage(Message message) {
        try {
            output.reset();
            output.writeObject(message);
            output.flush();
            output.reset();
            System.out.println("Sent");
        }catch (Exception e)
        {System.out.println(e.getMessage());}
    }


    @Override
    public void run() {
        try {
            System.out.println("ClientConnected");
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
            Message receivedMessage;
            while (isConnected()) {
                receivedMessage = (Message) input.readObject() ;
                System.out.println(receivedMessage.getMessageType());
                this.deliverMessage(receivedMessage);
            }           // delivers the client message to the server
        } catch (ClassNotFoundException e) {
            // e.printStackTrace();
        }
        catch (IOException e){
            System.out.println("Client is disconnecting ");
            //e.printStackTrace();
        }
        finally {
            try {
                this.disconnect();
            }
            catch (IOException e) {
                // e.printStackTrace();
            }
        }
    }


    /**
     * Method that closes the socket
     */
    public synchronized void disconnectClient() throws IOException {
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Error in closing socket!");}
        connected = false;
    }


    /**
     * Method that disconnects the client
     */
    public void disconnect() throws IOException {
        System.out.println("Client connection closing...");
        server.closeClientConnection(this);
        disconnectClient();
        System.out.println("Connection closed!");
    }

    /**
     * Returns the current status of the client connection
     */
    public synchronized boolean isConnected()
    {return connected;}

    public String getClientNickName() {
        return clientNickName;
    }

    public void setVirtualView(VirtualView virtualView) {
        this.virtualView = virtualView;
    }

    public Server getServer() {
        return server;
    }

    public VirtualView getVirtualView() {
        return virtualView;
    }

}