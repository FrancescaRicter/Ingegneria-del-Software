package it.polimi.ingsw.Connection.Server;

import it.polimi.ingsw.Connection.Message;
import it.polimi.ingsw.Connection.MessageType;
import it.polimi.ingsw.Connection.Server.ServerMessage.*;
import it.polimi.ingsw.Controller.Controller;
import it.polimi.ingsw.Controller.PlayerPhase;
import it.polimi.ingsw.Model.*;
import it.polimi.ingsw.Observer.Observer;
import it.polimi.ingsw.Observer.Observable;
import it.polimi.ingsw.View.GameRepresentation;

import java.util.ArrayList;
import java.util.List;

/**
 * Each client has an instance of this class that represents the gateway for receiving and sending messages that are referred to an active game
 *
 * */
public class VirtualView extends Observable implements Observer  {
    ConnectionClientManager clientManager;
    Controller controller;
    String clientNickName;

    /*set by the lobby class once game is created and ready to start*/
    public void setController(Controller controller) {
        this.controller = controller;
    }

    public Controller getController() {
        return controller;
    }

    public VirtualView(ConnectionClientManager clientManager) {
        this.clientManager = clientManager;
    }


    /**
     * Method that passes a message from the client to the controller
     *
     * */
    public void receiveMessage(Message message) {
        this.notifyObservers(message);
    }

    /**
     * Override of the observable method. This method is called by model when an update is performed on the data stored by the model,
     * model calls the method 'notify' that triggers the update through the observers. This method creates the message fetching the information
     * needed from the model the update is propagated to the ConnectionClientManager to be sent to the client
     *
     * @param messageType the type of message that has to be sent to the client
     * */
    @Override
    public void update(MessageType messageType){
        clientNickName = controller.getModel().getPlayers().get(controller.getPlayerId(this)).getNickname();
        switch (messageType){
            case SHOW_TOWERS:{
                if(controller.getModel().getNumPlayers()!=4){
                    Message serverMessageInitT = new InitGameMessage(clientNickName,MessageType.INIT_TOWER,controller.getAvailableTowers(), null);
                    clientManager.sendMessage(serverMessageInitT);}
                else{
                    List<String> players = new ArrayList<>();
                    List<TowerColor> towerColor = new ArrayList<>();
                    for(Player p: controller.getModel().getPlayers()){
                        if(p.getBoard().getTowerColor()!=null){
                            players.add(p.getNickname());
                            towerColor.add(p.getBoard().getTowerColor());}
                    }
                    Message serverMessageInitT = new InitGameMessage(clientNickName, MessageType.INIT_TOWER_TEAMS, controller.getAvailableTowers(), null,players,towerColor);
                    clientManager.sendMessage(serverMessageInitT);
                }
                break;
            }
            case SHOW_CHARACTERS:{
                Message serverMessageInitC = new InitGameMessage(clientNickName,MessageType.INIT_CHARACTER,null, controller.getAvailableCharacters());
                clientManager.sendMessage(serverMessageInitC);
                break;
            }
            case ISLANDS: {
                Message serverMessageIslands = new UpdatedIslandsMessage(controller.getModel().getIslands());
                clientManager.sendMessage(serverMessageIslands);
                break;
            }
            case START_GAME:{
                GameRepresentation gameRap = new GameRepresentation(controller.getModel(),controller.getPlayerId(this));
                Message serverMessageGameRapp = new StartGameMessage(clientNickName,controller.getPlayerId(this),gameRap,controller.getModel().getFirstPlayer().getNickname());
                clientManager.sendMessage(serverMessageGameRapp);
                break;
            }
            case CLOUDS: {
                if(controller.isGameStarted()){
                    Message serverMessageClouds = new UpdatedCloudsMessage(controller.getModel().getClouds());
                    clientManager.sendMessage(serverMessageClouds);
                }
                break;
            }
            case DECK: {
                List<Card> playedCards = new ArrayList<Card>();
                for(Player p: controller.getModel().getPlayers()) {
                    if(p.getPlayedCards().size()==controller.getTurn()) {
                        playedCards.add(p.getPlayedCards().get(p.getPlayedCards().size()-1));
                    }
                }
                Message serverMessageDeck = new UpdatedDeckMessage(clientNickName,controller.getModel().getPlayers().get(controller.getPlayerId(this)).getDeck(),playedCards);
                clientManager.sendMessage(serverMessageDeck);
                break;
            }
            case BOARDS: {
                if(controller.isGameStarted()){
                    List<Board> boards = new ArrayList<>();
                    for(Player p : controller.getModel().getPlayers()){
                        boards.add(p.getBoard());
                    }
                    Message serverMessageBoards = new UpdatedBoardsMessage(boards);
                    clientManager.sendMessage(serverMessageBoards);}
                break;
            }
            case EXPERT_PARAMETERS:{
                ExpertGame expert = (ExpertGame) controller.getModel();
                List<Integer> coins = new ArrayList<Integer>();
                for(Player p: expert.getPlayers()){
                    coins.add(expert.getCoins(p));
                }
                Message serverMessageExpertParams = new UpdatedExpertParams(expert.getCharacterCards(),coins);
                clientManager.sendMessage(serverMessageExpertParams);
                break;
            }
            case WIN:{
                EndGameMessage serverMessageWin = new EndGameMessage(controller.getModel().getPlayers().get(controller.getPlayerId(this)).getNickname());
                clientManager.sendMessage(serverMessageWin);
                break;
            }
            case LOSS:{
                EndGameMessage serverMessageLoss = new EndGameMessage(controller.getModel().getPlayers().get(controller.getPlayerId(this)).getNickname(),controller.winner().getNickname());
                clientManager.sendMessage(serverMessageLoss);
                break;
            }
            case GAME_TURN:{
                System.out.println(controller.getCurrentPlayerIndex());
                PlayerPhase phase = controller.getPlayerPhase(controller.getCurrentPlayerIndex());
                String text = "" + controller.getModel().getPlayers().get(controller.getCurrentPlayerIndex()).getNickname();
                switch (phase){
                    case PLAY_CARD:
                        text = text +" "+ "to play Assistant card";
                        break;
                    case ENTRANCE_MOVE:
                        text = text +" "+ "to move students from the Entrance";
                        break;
                    case MOTHER_NATURE_MOVE:
                        text = text +" "+ "to move Mother Nature";
                        break;
                    case CLOUD_CHOICE:
                        text = text +" "+ "to refill students from Cloud";
                        break;
                }
                SimpleMessage message = new SimpleMessage(clientNickName,MessageType.GAME_TURN,text);
                clientManager.sendMessage(message);
                break;
            }
            default:{
                System.out.println("Problem in VirtualView in casting a message");
                break;
            }
        }
    }


    @Override
    public void update(Message message) {
        clientManager.sendMessage(message);
    }
}
