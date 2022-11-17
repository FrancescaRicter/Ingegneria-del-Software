package it.polimi.ingsw.View;

import it.polimi.ingsw.Connection.Client.ClientMessage.*;
import it.polimi.ingsw.Connection.Message;
import it.polimi.ingsw.Connection.MessageType;
import it.polimi.ingsw.Connection.Server.Lobby;
import it.polimi.ingsw.Connection.Server.ServerMessage.*;
import it.polimi.ingsw.Model.*;
import it.polimi.ingsw.Model.Character;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;
import java.util.List;

/**
 * Class that implements the command-line interface
 *
 */
public class ClientCLI {
    private String IP;
    private int Port;
    private boolean active = true;
    private String clientNickName;
    Scanner scanIn;
    ObjectInputStream in;
    ObjectOutputStream out ;
    Thread readFromCli;
    GameRepresentation clientGame = null;

    int playerId;
    boolean playing = false;
    boolean isNameSet = false;
    boolean isInLobby = false;
    boolean endgameReached=false;

    public ClientCLI(String IP, int Port){
        this.IP = IP;
        this.Port = Port;
    }

    public synchronized boolean isActive(){
        return active;
    }

    public synchronized void setActive(boolean active){
        this.active = active;
    }

    /**
     * Method that runs a thread that reads from the server socket through the ObjectInputStream
     *
     * @param input the ObjectInputStream
     * @return the thread that reads from server
     *
     */
    public Thread readFromSocket(final ObjectInputStream input){
        Thread t = new Thread(() -> {
            try {
                while (isActive()) {
                    Message inputMessage = (Message) input.readObject();
                    switch (inputMessage.getMessageType()){
                        case LOBBIES_RESPONSE: {
                            isNameSet=true;
                            LobbiesResponseMessage lobbiesMessage = (LobbiesResponseMessage) inputMessage;
                            List<Lobby> lobbies = lobbiesMessage.getLobbies();
                            printLobbyEdge("top");
                            for (Lobby lobby :
                                    lobbies) {
                                printLobby(lobby);
                            }
                            printLobbyEdge("bottom");
                            System.out.println(Colors.CYAN + "HELP"+ Colors.RESET);
                        }
                        break;
                        case PLAYER_ID:{
                            PlayerIdMessage playedIdMessage= (PlayerIdMessage) inputMessage;
                            playerId = playedIdMessage.getPlayerId();
                            break;
                        }
                        case INIT_TOWER:{
                            isInLobby=true;
                            System.out.print("\033[H\033[2J");
                            System.out.println(Colors.MAGENTA + "All players are connected.\nPlease choose your tower color and then wait for the other players." + Colors.RESET);
                            System.out.println("");
                            System.out.println("Available Tower Colors:");
                            InitGameMessage towerMessage = (InitGameMessage) inputMessage;
                            for (TowerColor t1 : towerMessage.getTowerColorList()) {
                                System.out.println(Colors.BLUE + t1.toString() + Colors.RESET);
                            }
                            break;
                        }
                        case INIT_TOWER_TEAMS:{
                            isInLobby=true;
                            InitGameMessage towerMessage = (InitGameMessage) inputMessage;
                            System.out.print("\033[H\033[2J");
                            for(String s : towerMessage.getChoices()){
                                System.out.println(s);
                            }
                            System.out.println("");
                            System.out.println(Colors.MAGENTA + "You are playing with Teams, choose the same Tower Color as your team mate.\nThen wait for the other players to make their choice" + Colors.RESET);
                            System.out.println("");
                            System.out.println("Available Tower Colors :");
                            for (TowerColor t1 : towerMessage.getTowerColorList()) {
                                System.out.println(Colors.CYAN+  t1.toString() +Colors.RESET);
                            }
                            break;
                        }
                        case INIT_CHARACTER:{
                            System.out.print("\033[H\033[2J");
                            System.out.println(Colors.MAGENTA + "All players have chosen their Tower Color. Now choose your Card Character. " + Colors.RESET);
                            System.out.println();
                            System.out.println("Available Characters:");
                            InitGameMessage characterMessage = (InitGameMessage) inputMessage;
                            for (Character c : characterMessage.getCharacterList()){
                                System.out.println(Colors.CYAN + c.toString());
                            }
                            System.out.println(Colors.RESET);
                            break;
                        }
                        case LOGIN_REPLY:{
                            isInLobby= true;
                            List<String> players = new ArrayList<>();
                            String gameMode;
                            LoginResponseMessage loginMessage = (LoginResponseMessage) inputMessage;
                            if(loginMessage.isGameMode())  {
                                 gameMode = "Expert";
                            }
                            else{
                                gameMode= "Normal";
                            }
                            System.out.println("" + Colors.GREEN +
                                    "###################################################################\n" +
                                    "||                          LOBBY "+ loginMessage.getLobbyId() +   "                              ||\n" +
                                    "*******************************************************************\n" +
                                    "||     MODE :  "+ gameMode +"            |      MAX_PLAYERS :  "+loginMessage.getMaxPlayers()+"         ||\n" +
                                    "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                            int i=0;
                            for(String s : loginMessage.getPlayers()){
                                System.out.println("   "+ Colors.CYAN + "Player " + i + Colors.GREEN + ":   " + s+ "                                        ");
                                i++;
                            }
                            System.out.println("###################################################################\n");
                            System.out.println(Colors.RESET);
                            break;
                        }
                        case START_GAME:{
                            StartGameMessage startGameMessage = (StartGameMessage)inputMessage;
                            clientGame = new GameRepresentation(startGameMessage.getGameRappresentation())  ;
                            playing=true;
                            printGame();
                            System.out.println("   ");
                            break;
                        }
                        case DISCONNECTED:{
                            System.out.print("\033[H\033[2J");
                            DisconnectionMessage disconnectionMessage = (DisconnectionMessage) inputMessage;
                            System.out.println(disconnectionMessage.getDisconnectionMessage());
                            isInLobby=false;
                            playing=false;
                            break;
                        }
                        case DECK : {
                            UpdatedDeckMessage updatedDeckMessage =(UpdatedDeckMessage) inputMessage;
                            clientGame.setMyDeck(updatedDeckMessage.getDeck());
                            clientGame.setPlayedCards(updatedDeckMessage.getPlayedCards());
                            printGame();
                            break;
                        }
                        case BOARDS:{

                            UpdatedBoardsMessage updatedBoardsMessage = (UpdatedBoardsMessage) inputMessage;
                            clientGame.setBoards(updatedBoardsMessage.getBoards());
                            printGame();

                            break;
                        }
                        case ISLANDS:{
                            UpdatedIslandsMessage updatedIslandsMessage = (UpdatedIslandsMessage) inputMessage;
                            clientGame.setIslands(updatedIslandsMessage.getIslandList());
                            printGame();
                            break;
                        }
                        case CLOUDS:{
                            UpdatedCloudsMessage updatedCloudsMessage = (UpdatedCloudsMessage) inputMessage;
                            clientGame.setClouds(updatedCloudsMessage.getClouds());
                            printGame();
                            break;
                        }
                        case GAME_TURN:{
                            SimpleMessage turnMessage = (SimpleMessage) inputMessage;
                            clientGame.setLog(turnMessage.getText());
                            System.out.println("");
                            System.out.println("Waiting for " + turnMessage.getText() +"...");
                            break;
                        }
                        case EXPERT_PARAMETERS:{
                            UpdatedExpertParams expertParams = (UpdatedExpertParams) inputMessage;
                             clientGame.setCoins(expertParams.getCoins());
                             clientGame.setCharacterCard(expertParams.getSpecialCharacterCardList());
                             printGame();
                             System.out.println("Waiting for " + clientGame.getLog() +"...");
                            break;
                        }
                        case WIN:{
                            endgameReached=true;
                            System.out.print("\033[H\033[2J");
                            System.out.println();
                                        System.out.println("You have won!");
                 System.out.println(
                        Colors.GREEN +
                           "                                        ,----,                                             \n"            +
                           "                                      ,/   .`|  ,----..                                    \n"      +
                           "                ---,  ,----..      ,`   .'  : /   /   \\  ,-.----.                         \n"        +
                           "        ,---.,`--.' | /   /  \\   ;    ;     //   .     : \\   /  \\        ,---,          \n"      +
                           "       /__./||   :  :|   :     :.'___,/    ,'.   /   ;.  \\;   :   \\     /_ ./|           \n"      +
                           "  ,---.;  ; |:   |  '.   |  ;. /|    :     |.   ;   /  ` ;|   | .\\ :,---, |  ' :          \n"       +
                           " /___/\\  | ||   :  |.   ; /--` ;    |.';  ;;   |  ; \\ ; |.   : |: /___/ \\.  : |         \n"      +
                           " \\   ; \\ ' |'   '  ;;   | ;    `----'  |  ||   :  | ; | '|   |  \\ :.  \\ \\ ,' '        \n"+
                           "  \\  \\ \\: ||   |  ||   : |        '   :  ;.   |  ' ' ' :|   : .  / \\  ;  `  ,'         \n"   +
                           "   ;   \\  ' .'   :  ;.   | '___     |   |  ''   ;  \\; /  |;   | |  \\  \\  \\    '       \n"   +
                           "    \\  \\   '|   |  ''   ; : .'|    '   :  | \\  \\  ',  / |   | ;\\  \\  '  \\   |       \n"     +
                           "      \\   `  ;'   :  |'   | '/  :    ;   |.'   ;   :    /  :   ' | \\.'   \\  ;  ;        \n"     +
                           "      :   \\ |;   |.' |   :    /     '---'      \\  \\ .'   :   : :-'      :  \\ \\        \n"     +
                           "       '---' '---'    \\  \\ .'                  `---`     |   |.'         \\  ' ;         \n"     +
                           "                      `---`                              `---'            `--`             \n"      +Colors.RESET );
                            break;
                        }
                        case LOSS:{
                            endgameReached=true;
                            System.out.print("\033[H\033[2J");
                            System.out.println();
                            EndGameMessage endGame = (EndGameMessage)    inputMessage;
                            System.out.println("You have lost!");
                            System.out.println("Winner was" + endGame.getWinnerNickname());
                                    System.out.println(
                                           Colors.RED +
                                       "        ,--,                                             \n "+
                                       "      ,---.'|       ,----..                              \n "+
                                       "      |   | :      /   /  \\   .--.--.    .--.--.        \n "        +
                                       "      :   : |     /   .     : /  /    '. /  /    '.      \n "      +
                                       "      |   ' :    .   /   ;. \\  :  /`. /|  :  /`. /      \n "      +
                                       "      ;   ; '   .   ;   /  ` ;  |  |--` ;  |  |--`       \n "       +
                                       "      '   | |__ ;   |  ; \\ ; |  :  ;_   |  :  ;_        \n "      +
                                       "      |   | :.'||   :  | ; | '\\  \\    `.\\ \\    `.    \n "+
                                       "      '   :    ;.   |  ' ' ' : `----.   \\ `----.   \\   \n "   +
                                       "      |   |  ./ '   ;  \\; /  | __\\ \\  | __ \\ \\  |   \n "   +
                                       "      ;   : ;   \\   \\  ',  / /  /`--'  //  /`--'  /    \n "     +
                                       "      |   ,/      ;   :    / '--'.     /'--'.     /      \n "     +
                                       "      '---'       \\   \\ .'    `--'---'   `--'---'      \n "     +
                                       "                    `---`                                \n "     +
                                       "                                                          "      +Colors.RESET );
                            break;
                        }
                        case ERROR:
                            ErrorMessage errorMessage = (ErrorMessage) inputMessage;
                            System.out.println(""+Colors.RED + errorMessage.getError()+ Colors.RESET);
                            if(!isNameSet){
                                requestLogin();
                            }
                            break;
                    }
                }
            } catch (Exception e){
                System.out.println("Server disconnected");
                setActive(false);
                System.exit(1);
            }
        });
        t.start();
        return t;
    }

    /**
     *
     * Method that runs a thread that writes to the server socket throughout the ObjectOutputStream
     *
     * @param output the ObjectOutputStream
     * @return the thread that writes to server
     */
    public Thread writeToSocket(final ObjectOutputStream output){
        final Thread t = new Thread(() -> {
            try {
                requestLogin();
                while (!isNameSet){
                    Thread.sleep(50);
                }
                while (isActive()){
                    String command = scanIn.nextLine();
                    String text= null;
                    System.out.println("command: "+command);
                    switch (command.toLowerCase()) {
                        case "show games": {
                            if(!endgameReached){
                                SimpleRequest printLobbies = new SimpleRequest(clientNickName, MessageType.SHOW_LOBBIES);
                                send(output,printLobbies);
                                break;
                            }else{
                                System.out.println(Colors.RED + "Game has ended! Exit first" +Colors.RESET);
                                break;
                            }
                        }
                        case "create":{
                            if(!endgameReached){
                                int num;
                                String mode =null;
                                if(isInLobby){
                                    System.out.println(Colors.RED + "You are already in a lobby you can't create a new one. Please leave the current one if you desire to do so." + Colors.RESET);
                                    break;
                                }
                                else{
                                    System.out.println(Colors.BLUE + "Enter the Number of Players and Game Mode:" + Colors.RESET+ "\n[Suggested "+Colors.CYAN +"2/3/4 Normal/Expert"+ Colors.RESET + "] ");
                                    text = scanIn.nextLine();
                                    try{
                                        String players = text.substring(0,1);
                                        num = Integer.parseInt(players);}
                                    catch(Exception e){
                                        System.out.println(Colors.RED + "Invalid player number!" + Colors.RESET);
                                        break;
                                    }
                                    if(text.length()<3)
                                    {
                                        System.out.println(Colors.RED + "Invalid game mode" + Colors.RESET);
                                        break;
                                    }
                                    mode = text.substring(2);
                                    boolean isExpert ;
                                    if(mode.equalsIgnoreCase("Normal")){
                                        isExpert=false;
                                    }
                                    else if(mode.equalsIgnoreCase("Expert")){
                                        isExpert= true;
                                    }
                                    else{
                                        System.out.println(Colors.RED + "Invalid game mode. Type 'Normal' or 'Expert'" + Colors.RESET);
                                        break;
                                    }
                                    CreateLobbyMessage newLobby = new CreateLobbyMessage(clientNickName, isExpert, num);
                                    send(output,newLobby);
                                    break;
                                }
                            }else{
                                System.out.println(Colors.RED + "Game has ended! Exit first" +Colors.RESET);
                                break;
                            }
                        }
                        case "join":{
                            if(!endgameReached){
                                if(isInLobby){
                                    System.out.println(Colors.RED + "You are already in a lobby you can't join another one. Please leave the current one if you desire to do so." + Colors.RESET);
                                    break;
                                }
                                int lobbyId = -1;
                                System.out.print("Enter lobby id: ");
                                try{
                                    String line = scanIn.nextLine();
                                    lobbyId = Integer.parseInt(line);}
                                catch(Exception e){
                                    System.out.println(Colors.RED + "Invalid command!" + Colors.RESET);
                                    break;
                                }
                                JoinLobbyMessage enterLobbyRequest = new JoinLobbyMessage(clientNickName,lobbyId);
                                send(output,enterLobbyRequest);
                                break;
                            }
                            else{
                                System.out.println(Colors.RED + "Game has ended! Exit first" + Colors.RESET);
                                break;
                            }
                        }
                        case "show towers":{
                            if(!endgameReached){
                                if(!playing){
                                    SimpleRequest showTowersMessage = new SimpleRequest(clientNickName,MessageType.SHOW_TOWERS);
                                    send(output,showTowersMessage);
                                    break;
                                }else {
                                    System.out.println(Colors.RED + "Invalid!" + Colors.RESET);
                                    break;
                                }
                            }
                            else{
                                System.out.println(Colors.RED + "Game has ended! Exit first" + Colors.RESET);
                                break;
                            }
                        }
                        case "show characters": {
                            if(!endgameReached){
                                if(!playing){
                                    SimpleRequest showCharactersMessage = new SimpleRequest(clientNickName, MessageType.SHOW_CHARACTERS);
                                    send(output,showCharactersMessage);
                                    break;
                                }else {
                                    System.out.println(Colors.RED + "Invalid!" + Colors.RESET);
                                    break;
                                }
                            } else{
                                System.out.println(Colors.RED + "Game has ended! Exit first" + Colors.RESET);
                                break;
                            }
                        }
                        case "choose tower": {
                            if(!endgameReached ){
                                if(!playing){
                                    System.out.println(Colors.RESET + "Insert the tower color:" + Colors.RESET);
                                    text = scanIn.nextLine();
                                    TowerColorMessage towerMessage = new TowerColorMessage(clientNickName,text);
                                    send(output,towerMessage);
                                    break;
                                }else {
                                    System.out.println(Colors.RED + "Invalid!" + Colors.RESET);
                                    break;
                                }
                            } else{
                                System.out.println(Colors.RED + "Game has ended! Exit first" + Colors.RESET);
                                break;
                            }
                        }
                        case "choose character":{
                            if(!endgameReached){
                                if(!playing){
                                    System.out.println(Colors.BLUE + "Insert the character:" + Colors.RESET);
                                    text = scanIn.nextLine();
                                    CharacterMessage characterMessage = new CharacterMessage(clientNickName,text);
                                    send(output,characterMessage);
                                    break;
                                }else {
                                    System.out.println(Colors.RED + "Invalid!" + Colors.RESET);
                                    break;
                                }
                            } else{
                                System.out.println(Colors.RED + "Game has ended! Exit first" + Colors.RESET);
                                break;
                            }
                        }
                        case "play assistant":{
                            if(!endgameReached){
                                System.out.println(Colors.BLUE + "Insert the Assistant Card you want to play:" +Colors.RESET);
                                String numStr;
                                int num;
                                try{
                                    numStr = scanIn.nextLine();
                                     num = Integer.parseInt(numStr);
                                }
                                catch(Exception e){
                                    System.out.println(Colors.RED + "Invalid command!" + Colors.RESET);
                                    break;
                                }
                                if(num>0 && num<=10){
                                    int i=0;
                                    boolean found=false;
                                    for(;i<clientGame.getMyDeck().size();i++){
                                        if(clientGame.getMyDeck().get(i).getPriorityNumber()==num)
                                        { found=true;
                                            PlayCardMessage playCardMessage = new PlayCardMessage(clientNickName,i);
                                            send(output,playCardMessage);
                                            break;
                                        }
                                    }
                                    if(!found)   {
                                        System.out.println(Colors.RED + "You have already played this Assistant!" + Colors.RESET);
                                        break;
                                    }
                                }
                                else {
                                    System.out.println(Colors.RED + "This Assistant does not exist!" + Colors.RESET);
                                    break;
                                }
                                break;
                            }
                            else{
                                System.out.println(Colors.RED + "Game has ended! Exit first" + Colors.RESET);
                                break;
                            }
                        }
                        case "play character":{
                            if(!endgameReached){
                                System.out.println("");
                                System.out.println(Colors.BLUE + "Insert the Character Card you want to play:\n" + Colors.RESET
                                + "Suggested [type:"+Colors.CYAN +" Give Character info"+Colors.RESET +" ]");
                                String numStr;
                                int num;
                                try{
                                    numStr = scanIn.nextLine();
                                    num = Integer.parseInt(numStr);
                                }
                                catch(Exception e){
                                    System.out.println(Colors.RED + "Invalid command!" + Colors.RESET);
                                    break;
                                }
                                boolean found=false;
                                CharacterCard c=null;
                                for(CharacterCard card :clientGame.getCharacterCard()) {
                                    if (card.getCharacterNum() == num) {
                                        if (card.getCost() + card.getVarCost() > clientGame.getCoins().get(playerId)) {
                                            System.out.println(Colors.RED + "Insufficient coins" + Colors.RESET);
                                            break;
                                        } else {
                                            found = true;
                                            c=card;
                                            break;
                                        }
                                    }
                                }
                                if(found){
                                    String params =null;
                                    if(c.needPars()) {
                                        System.out.println(Colors.MAGENTA + "Insert the parameters suggested by the card" + Colors.RESET);
                                        try{
                                            params = scanIn.nextLine();}
                                        catch(Exception e){
                                            System.out.println(Colors.RED + "Invalid student!" + Colors.RESET);
                                            break;
                                        }
                                        System.out.println(Colors.MAGENTA + "YOU CHOOSE TO PLAY CHARACTER CHARD "  + numStr + Colors.RESET);
                                        System.out.println("");
                                        PlayCardMessage playCardMessage = new PlayCardMessage(clientNickName,clientGame.getCharacterCard().indexOf(c),params);
                                        send(output,playCardMessage);
                                    }
                                    else{
                                        System.out.println(Colors.MAGENTA + "YOU CHOOSE TO PLAY CHARACTER CHARD "  + numStr + Colors.RESET);
                                        System.out.println("");
                                        PlayCardMessage playCardMessage = new PlayCardMessage(clientNickName,clientGame.getCharacterCard().indexOf(c), "");
                                        send(output,playCardMessage);
                                    }
                                    break;
                                } else{
                                    System.out.println(Colors.RED + "Invalid Character Card!" + Colors.RESET);
                                    break;
                                }
                            }
                            else{
                                System.out.println(Colors.RED + "Game has ended!" + Colors.RESET);
                                break;
                            }
                        }
                        case "exit game":{
                            System.out.print("\033[H\033[2J");
                            SimpleRequest exitMessage = new SimpleRequest(clientNickName,MessageType.EXIT);
                            send(output,exitMessage);
                            isInLobby=false;
                            playing=false;
                            endgameReached=false;
                            System.out.println("You have exited the game");
                            break;
                        }
                        case "move":{
                            if(!endgameReached){
                                boolean found=false;
                                System.out.println("Insert the student you want to move: \n[Suggested  " + Colors.CYAN + "frog - fairy - dragon - unicorn - gnome"+ Colors.RESET + "] ");
                                String student ;
                                try{
                                        student= scanIn.nextLine();
                                } catch(Exception e){
                                    System.out.println(Colors.RED + "Invalid command!" + Colors.RESET);
                                    break;
                                }
                                int i;
                                for(i = clientGame.getBoards().get(playerId).getEntrance().size()-1; i!=-1;i--) {
                                    if(clientGame.getBoards().get(playerId).getEntrance().get(i).getPawn().toString().equalsIgnoreCase(student)) {
                                        found=true ;
                                        break;
                                    }
                                }
                                if(found) {
                                    System.out.println("Do you want to move on 'dining' or on 'island' ?");
                                    try{
                                    text = scanIn.nextLine();
                                    } catch(Exception e){
                                        System.out.println(Colors.RED + "Invalid command!" + Colors.RESET);
                                        break;
                                    }
                                    switch (text.toUpperCase()){
                                        case "DINING":{
                                            MoveStudentOnDiningRoomMessage message = new MoveStudentOnDiningRoomMessage(clientNickName,i);
                                            send(output,message);
                                            break;
                                        }
                                        case "ISLAND":{
                                            System.out.println("Insert the number of the island you want to move your student on:");
                                            String island;
                                            int islandNum;
                                            try{
                                             island= scanIn.nextLine();
                                             islandNum = Integer.parseInt(island);
                                            } catch(Exception e){
                                                System.out.println(Colors.RED + "Invalid command!" + Colors.RESET);
                                                break;
                                            }

                                            MoveStudentOnIslandMessage message =new MoveStudentOnIslandMessage(clientNickName,i,islandNum-1);
                                            send(output,message);
                                            break;
                                        }
                                        default:{
                                            System.out.println(Colors.RED + "Invalid!" + Colors.RESET);
                                            break;
                                        }
                                    }
                                } else {
                                    System.out.println(Colors.RED + "There are no " + student + " students in your entry!" + Colors.RESET);
                                    break;
                                }
                            } else {
                                System.out.println(Colors.RED + "Game has ended!" + Colors.RESET);
                                break;
                            }
                            break;
                        }
                        case "move mother nature":{
                            if(!endgameReached){
                                System.out.println("Insert the number of steps for Mother Nature:");
                                int num;
                                try{
                                text = scanIn.nextLine();
                                    num = Integer.parseInt(text);
                                } catch(Exception e){
                                    System.out.println(Colors.RED + "Invalid command!" + Colors.RESET);
                                    break;
                                }

                                MoveMotherNatureMessage motherNatureMessage = new MoveMotherNatureMessage(clientNickName,num);
                                send(output,motherNatureMessage);
                                break;
                            } else{
                            System.out.println(Colors.RED + "Game has ended! Exit first" + Colors.RESET);
                            break;
                        }
                        }
                        case "refill": {
                            if(!endgameReached){
                                System.out.println("Insert the cloud number you would like to retrieve your students from:");
                                int num;
                                try{
                                      text = scanIn.nextLine();
                                      num = Integer.parseInt(text);
                                } catch(Exception e){
                                    System.out.println(Colors.RED + "Invalid command!" + Colors.RESET);
                                    break;
                                }
                                if(num>=0 && num<=clientGame.getClouds().size()){
                                    ChooseCloudMessage chooseCloudMessage = new ChooseCloudMessage(clientNickName,num-1);
                                    send(output,chooseCloudMessage);
                                    break;
                                }
                                else{
                                    System.out.println(Colors.RED + "This cloud does not exist!" + Colors.RESET);
                                    break;
                                }
                            } else{
                                System.out.println(Colors.RED+ "Game has ended!"+Colors.RESET) ;
                                break;
                            }
                        }
                        case "show game":{
                            if(!endgameReached){
                                if(playing){
                                    printGame();
                                    System.out.println(clientGame.getLog());
                                }
                                else {
                                    System.out.println(Colors.RED + "The game hasn't started yet!" + Colors.RESET);
                                    System.out.println("Waiting for " + clientGame.getLog() + "...");
                                }
                                break;
                            } else{
                            System.out.println(Colors.RED +"Game has ended! " + Colors.RESET);
                            break;
                            }
                        }
                        case "show turn":{
                            if(playing) {
                                System.out.println("It's the turn of" + clientGame.getLog());
                                break;
                            }
                            break;
                        }
                        case "exit application":{
                            System.exit(1);
                            break;
                        }
                        case "give character info":{
                            if(!endgameReached){
                                if(clientGame.isExpert){
                                    System.out.println("Insert the Character:");
                                    String character;
                                    int num;
                                    try{
                                        character= scanIn.nextLine();
                                        num = Integer.parseInt(character);
                                    }
                                    catch(Exception e){
                                           System.out.println(Colors.RED + "Invalid command!" + Colors.RESET);
                                           break;
                                    }

                                    for(CharacterCard c : clientGame.getCharacterCard()){
                                        if(c.getCharacterNum()==num)
                                        {
                                            System.out.println(c.getEffectDescription());
                                        }
                                    }
                                } else{
                                    System.out.println(Colors.RED + "We are not in expert mode!" + Colors.RESET);
                                    break;
                                }
                            } else {
                                System.out.println("Game has ended! Exit first");
                                break;
                            }
                            break;
                        }
                        case"help":{
                            System.out.println( "Here is a list of commands you can type: " );
                            if (isInLobby & playing & !endgameReached){
                                System.out.println(Colors.CYAN +"-Show Game");
                                System.out.println("-Show Turn");
                                System.out.println("-Play Assistant");
                                System.out.println("-Move");
                                System.out.println("-Move Mother Nature");
                                System.out.println("-Refill");
                                if(clientGame.isExpert){
                                    System.out.println("-Play Character");
                                    System.out.println("-Give Character info");
                                }
                            }
                            else if(!isInLobby && !endgameReached){
                                System.out.println(Colors.CYAN +"-Create");
                                System.out.println("-Join");
                                System.out.println("-Show Games");
                            }
                            else if(!endgameReached){
                                   System.out.println(Colors.CYAN +"-Show Towers");
                                   System.out.println("-Show Characters");
                                   System.out.println("-Choose Tower");
                                   System.out.println("-Choose Character");
                                }
                            System.out.println(Colors.CYAN + "-Exit Game");
                            System.out.println("-Exit Application"+ Colors.RESET);
                            break;
                        }
                        default: {
                            System.out.println(Colors.RED + "Invalid command try again!" + Colors.RESET);
                            break;
                        }
                    }
                }
            } catch(Exception e){
                System.out.println("Error");
                e.printStackTrace();
                setActive(false);
            }
        });
        t.start();
        return t;
    }

    public void run() throws IOException {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(IP,Port),0);
        System.out.println("Connection established");

        in = new ObjectInputStream(socket.getInputStream());
        out = new ObjectOutputStream(socket.getOutputStream());
        Scanner stdin = new Scanner(System.in);

        try{
            scanIn = new Scanner(System.in);
            Thread t0 = readFromSocket(in);
            readFromCli = writeToSocket(out);
            t0.join();
            readFromCli.join();
            System.out.println("Server closed connection");

        } catch(InterruptedException | NoSuchElementException e){
            System.out.println("Connection closed from the client side");
        } finally {
            stdin.close();
            in.close();
            out.close();
            socket.close();
        }
    }


    private void send(ObjectOutputStream output, Message message) throws IOException {
        output.writeObject(message);
        output.flush();
        output.reset();
    }

    private void requestLogin(){
        System.out.println("");
        System.out.print(Colors.BLUE + "Enter your NickName: " + Colors.RESET);
        try{
        clientNickName = scanIn.nextLine();}
        catch(Exception e){
            System.out.println(Colors.RED + "Invalid command!" + Colors.RESET);
        }
        if(clientNickName.length()>23){
            System.out.println(Colors.RED + "Error! NickName is too long!" + Colors.RESET);
            requestLogin();
        }
        else {
            NickNameMessage nickNameMessage = new NickNameMessage(clientNickName);
            try {
                out.writeObject(nickNameMessage);
                out.flush();
                out.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //prints
    private void printLobbyEdge(String tob) {
        String top = Colors.GREEN + "###################################################################\n" +
                "||                       AVAILABLE LOBBIES                       ||\n" +
                "*******************************************************************\n" +
                "|| ID |  MODE  | MAX_PLAYERS |          CURRENT_PLAYERS          ||\n" +
                "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + Colors.RESET;

        String bottom = Colors.GREEN + "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n" +
                "|| Commands:   \"Join **ID**\"-\"Create **MODE MAX_PLAYERS**\"-Exit  ||\n" +
                "###################################################################" + Colors.RESET;

        if (tob.equals("top"))
            System.out.println(top);
        else if (tob.equals("bottom")) {
            System.out.println(bottom);
        } else
            System.out.println("printLobbyEdge wrong arg!!!!!!!!!!");

    }

    private void printLobby(Lobby lobby) {
        int length = 65;
        StringBuilder start;
        String end = "|";
        String lobbyId;
        String gamemode;
        String numMaxPlayers;
        List<String> players;

        lobbyId = Integer.toString(lobby.getLobbyId());
        numMaxPlayers = "      " + lobby.getNumMaxPlayers() + "     | ";
        players = lobby.getPlayersNickNames();

        if (lobby.isGameMode())
            gamemode = " Expert |";
        else
            gamemode = " Normal |";

        switch (lobbyId.length()) {
            case 1:
                lobbyId = "||  " + lobbyId + " |";
                break;
            case 2:
                lobbyId = "|| " + lobbyId + " |";
                break;
            case 3:
                lobbyId = "|| " + lobbyId + "|";
                break;
            default:
                lobbyId = "||" + lobbyId + "|";
                break;
        }
        start = new StringBuilder(lobbyId + gamemode + numMaxPlayers + players);
        while (start.length() < length) {
            start.append(" ");
        }
        start.append("||");
        System.out.println(Colors.CYAN + start.toString() + Colors.RESET);
    }

    /**
     * Method that returns an array String with the representation of the deck as it will be displayed to the client
     */
    private String[][] giveDeck() {
        String[][] deck = new String[clientGame.getMyDeck().size()][];
        for (int i = 0; i < clientGame.getMyDeck().size(); i++) {
            deck[i] = giveCard(clientGame.getMyDeck().get(i).getPriorityNumber(), clientGame.getMyDeck().get(i).getMotherNatureMovement(), clientGame.getMyDeck().get(i).getCardCharacter());
        }
        return deck;
    }


    /**
     * Method that prints all card one next to the other in the order of the deck
     */
    private void printDeck() {
        String[][] deck = giveDeck();
        int rows = deck.length;
        //retrieve the whole row that will be printed

        StringBuilder currentString = new StringBuilder("");

        if(rows>0){
            for (int i = 0; i < 5; i++) {
                currentString = new StringBuilder("");
                for (String[] strings : deck) {
                    currentString.append(strings[i]);
                    currentString.append("   ");
                }
                System.out.println(currentString);
            }
        }
        else {
            System.out.println("Empty deck");
        }
    }

    private String[] giveIsland(int islandId){
        String[] island = new String[9];

        int red = (int) clientGame.getIslands().get(islandId).getStudents().stream().filter(x -> x.getPawn().equals(Disk.DRAGON)).count();
        int green = (int) clientGame.getIslands().get(islandId).getStudents().stream().filter(x -> x.getPawn().equals(Disk.FROG)).count();
        int yellow = (int) clientGame.getIslands().get(islandId).getStudents().stream().filter(x -> x.getPawn().equals(Disk.GNOME)).count();
        int blue = (int) clientGame.getIslands().get(islandId).getStudents().stream().filter(x -> x.getPawn().equals(Disk.UNICORN)).count();
        int pink = (int) clientGame.getIslands().get(islandId).getStudents().stream().filter(x -> x.getPawn().equals(Disk.FAIRY)).count();

        String redToken = "    ";
        if(red>0){
            redToken = "" +Colors.RED_BACKGROUND + " "+ red +" "+  Colors.RESET;
            if (red<10){
                redToken = redToken + " ";
            }
        }
        String yellowToken ="    ";
        if(yellow>0){
            yellowToken = "" +Colors.YELLOW_BACKGROUND + " " +yellow +" "+ Colors.RESET;
            if (yellow<10){
                yellowToken = yellowToken + " ";
            }
        }
        String pinkToken ="    ";
        if(pink>0){
            pinkToken = "" +Colors.PINK_BACKGROUND + " " +pink +" "+ Colors.RESET;
            if (pink<10){
                pinkToken = pinkToken + " ";
            }
        }
        String blueToken ="    ";
        if(blue>0){
            blueToken = "" +Colors.BLUE_BACKGROUND + " " +blue +" "+ Colors.RESET;
            if (blue<10){
                blueToken = blueToken + " ";
            }
        }
        String greenToken ="    ";
        if(green>0){
            greenToken = "" +Colors.GREEN_BACKGROUND + " " +green +" "+ Colors.RESET;
            if (green<10){
                greenToken = greenToken + " ";
            }
        }
        String towerToken=" ";
        if(clientGame.getIslands().get(islandId).getTowerColor()!=null){
            switch (clientGame.getIslands().get(islandId).getTowerColor()){
                case BLACK:
                    towerToken = towerToken + Colors.BLACK + "♜ " + Colors.RESET;
                    break;
                case WHITE:
                    towerToken = towerToken + Colors.WHITE + "♜ " + Colors.RESET;
                    break;
                case GREY:
                    towerToken = towerToken + Colors.GREY + "♜ " + Colors.RESET;
                    break;
                default:
                    towerToken = towerToken + "  ";
                    break;
            }
        }
        else{
            towerToken = towerToken + "  ";
        }
        if(clientGame.getIslands().get(islandId).getNumTowers()>1 && clientGame.getIslands().get(islandId).getNumTowers()<10) {
            int numTowers= clientGame.getIslands().get(islandId).getNumTowers();
            towerToken = towerToken + String.valueOf(numTowers) +  " ";
        }
        else if(clientGame.getIslands().get(islandId).getNumTowers()==10){
            int numTowers= clientGame.getIslands().get(islandId).getNumTowers();
            towerToken = towerToken + String.valueOf(numTowers);
        }
        else {
            towerToken = towerToken + "  ";
        }

        String MNToken= "";
        if (clientGame.getIslands().get(islandId).hasMotherNature()){
            MNToken =  "♟" ;
        }
        else {
            MNToken = " ";
        }
        String islandToken = Integer.toString(islandId+1);
        if(islandId>8)
        {islandToken =islandToken+"";
        }else {
            islandToken = islandToken+" ";
        }

        island[0] = "* * * * * * * * * *";
        island[1] = "*    Island " + islandToken + "    *";
        island[2] = "*                 *";
        island[3] = "*   " + redToken +    "    " + pinkToken + "  *";
        island[4] = "*                 *";
        island[5] = "*   " + yellowToken + "    " + blueToken + "  *";
        island[6] = "*                 *";
        island[7] = "*   " + greenToken + "  "   + MNToken + "  "+  towerToken +"*";
        island[8] = "* * * * * * * * * *";

        return island;
    }

    private void printIslands(){
        String[][] islands = new String[clientGame.getIslands().size()][];
        for(int i=0;i<clientGame.getIslands().size();i++){
            islands[i] = giveIsland(i);
        }

        StringBuilder currentString = new StringBuilder("");
        int rows=islands.length;

        for (int i = 0; i < 9; i++) {
            currentString = new StringBuilder("");
            for (String[] island : islands) {
                currentString.append(island[i]);
                currentString.append("   ");
            }
            System.out.println(currentString);
        }
    }

    private void printExpertCards(){
        String[][] characters = new String[clientGame.getCharacterCard().size()*4][7];

        int i=0;
        for(CharacterCard c : clientGame.getCharacterCard()){
            characters[i]=giveExpertCard(c);
            i++;
        }
        int rows = characters.length;

        new StringBuilder("                                                                                         " +
                "                                                                    ");
        StringBuilder currentString;
        for (i = 0; i < 7; i++) {
            currentString = new StringBuilder("                                                                                                                              ");
            for (int j=0;j< clientGame.getCharacterCard().size()*4;j++) {
                if(characters[j][i]==null)
                {characters[j][i]="";}
                currentString.append(characters[j][i]);
                currentString.append("  ");
            }
            System.out.println(currentString);
        }
    }

    private String[] giveExpertCard(CharacterCard c){
        String[] card = new String[7];
        String numCard = String.valueOf(c.getCharacterNum());
        if (c.getCharacterNum()<10) {
            numCard = numCard + " ";
        }

        int cost=c.getCost() + c.getVarCost();
        String costToken= String.valueOf(cost);
        if(c.getCost()<10){
            costToken = costToken + " ";
        }

        card[0] = Colors.RED + "—————————————" +Colors.RESET;
        card[1] = Colors.RED + "| Character |";
        card[2] = Colors.RED + "|     " + numCard +   "    |" + Colors.RESET;
        card[3] = Colors.RED + "|===========|" + Colors.RESET;
        card[4] = Colors.RED + "|   COST:   |" + Colors.RESET;
        card[5] = Colors.RED + "|    +" + costToken + "    |" + Colors.RESET;
        card[6] = Colors.RED + "—————————————" + Colors.RESET;

        return card;
    }


    private String[] giveCard(int priority, int Movement, Character character) {
        String[] card = new String[5];
        String priorityStr;
        String characterStr = "";

        switch (character) {
            case KING:
                characterStr = "♕";
                break;
            case WITCH:
                characterStr = "☾";
                break;
            case WIZARD:
                characterStr = "◊";
                break;
            case NINJA:
                characterStr = "☯";
                break;
        }
        if (priority == 10) {
            priorityStr = "" + Colors.BLUE + priority + Colors.RESET + "|";
        } else {
            priorityStr = " " + Colors.BLUE + priority + Colors.RESET + "|";
        }
        card[0] = "_________";
        card[1] = "| " + characterStr + "  |" + priorityStr + "";
        card[2] = "|       |";
        card[3] = "| MN:+" + Movement + " |";
        card[4] = "|_______|";

        return card;
    }

    private void printGame(){
        System.out.print("\033[H\033[2J");
        System.out.println("");
        System.out.println(
                "                                                                                                     ███████╗██████╗░██╗░█████╗░███╗░░██╗████████╗██╗░░░██╗░██████╗\n " +
                        "                                                                                                    ██╔════╝██╔══██╗██║██╔══██╗████╗░██║╚══██╔══╝╚██╗░██╔╝██╔════╝\n"+
                        "                                                                                                     █████╗░░██████╔╝██║███████║██╔██╗██║░░░██║░░░░╚████╔╝░╚█████╗░\n"+
                        "                                                                                                     ██╔══╝░░██╔══██╗██║██╔══██║██║╚████║░░░██║░░░░░╚██╔╝░░░╚═══██╗\n"+
                        "                                                                                                     ███████╗██║░░██║██║██║░░██║██║░╚███║░░░██║░░░░░░██║░░░██████╔╝\n"
        );
        System.out.println("    ");
        System.out.println("    ");
        System.out.println("Clouds:");
        printClouds();
        System.out.println("    ");
        System.out.println("Played Cards:");
        printPlayedCards();
        System.out.println("    ");
        System.out.println("Islands:");
        printIslands();
        System.out.println("    ");
        printBoards();
        if(clientGame.isExpert){
            System.out.println("");
            System.out.println(
                    "                                                                                         " +
                            "                                     " +
                            Colors.RED + "Character Cards:    [type: Play Character]" + Colors.RESET);
            printExpertCards();
        }
        else{
            System.out.println("    ");
            System.out.println("    ");
            System.out.println("    ");}
        System.out.println("My Assistants Deck");
        printDeck();
        System.out.println("");
        System.out.println("For a list of available commands type: HELP");
    }

    private String[][] giveBoardMatrix(int playerId) {
        String[][] boardMatrix = new String[4][];

        // position 0 of boardMatrix - Entrance String List
        String[] EntranceString = new String[17];
        boardMatrix[0] = EntranceString;

        //up to 23 char reserved for player NickName
        StringBuilder playerNickName = new StringBuilder(clientGame.getPlayerNicknames().get(playerId));

        int remainingChar = 22 - playerNickName.length();

        for (int l = 0; l < remainingChar; l++) {
            playerNickName.append(" ");
        }

        if(clientGame.isExpert)   {
            String coinsToken = clientGame.getCoins().get(playerId).toString();
            if (clientGame.getCoins().get(playerId)<10){
                coinsToken = coinsToken + " ";
            }
            playerNickName.append("Coins: ").append(coinsToken);
        }
        else
        {
            playerNickName.append("         ") ;
        }

        //Title for board - only in the first list (Entrance) in position 0 & 1
        EntranceString[0] = "--------------------------------------------------------------------------";
        EntranceString[1] = "| BOARD - PLAYER ID: " + playerId + " - PLAYER NICKNAME: " + playerNickName + "|";

        //Entrance
        EntranceString[2]  = "|---------------|";
        EntranceString[3]  = "|    ENTRANCE   |";
        EntranceString[4]  = "|_______________|";
        EntranceString[5]  = "| " + Colors.GREEN + " □ " + clientGame.getNumInEntrance(playerId, Disk.FROG) + " FROGS " + Colors.RESET + "   |";
        EntranceString[6]  = "|---------------|";
        EntranceString[7]  = "| " + Colors.RED + " □ " + clientGame.getNumInEntrance(playerId, Disk.DRAGON) + " DRAGONS" + Colors.RESET + "  |";
        EntranceString[8]  = "|---------------|";
        EntranceString[9]  = "| " + Colors.YELLOW + " □ " + clientGame.getNumInEntrance(playerId, Disk.GNOME) + " GNOMES " + Colors.RESET + "  |";
        EntranceString[10] = "|---------------|";
        EntranceString[11] = "| " + Colors.PINK + " □ " + clientGame.getNumInEntrance(playerId, Disk.FAIRY) + " FAIRIES " + Colors.RESET + " |";
        EntranceString[12] = "|---------------|";
        EntranceString[13] = "| " + Colors.BLUE + " □ " + clientGame.getNumInEntrance(playerId, Disk.UNICORN) + " UNICORNS" + Colors.RESET + " |";
        EntranceString[14] = "|_______________|";

        int totEntrance = clientGame.getNumInEntrance(playerId, Disk.FROG) + clientGame.getNumInEntrance(playerId, Disk.DRAGON) + clientGame.getNumInEntrance(playerId, Disk.GNOME) +
                clientGame.getNumInEntrance(playerId, Disk.FAIRY) + clientGame.getNumInEntrance(playerId, Disk.UNICORN);

        EntranceString[15] = "| Students: " + totEntrance + "/9" + " |";
        EntranceString[16] = "|_______________|";

        // position 1 of boardMatrix - Dining Room String List
        String[] DiningRoom = new String[17];
        boardMatrix[1] = DiningRoom;

        //2 char reserved for each num of student per colour
        int green = clientGame.getNumInDinningRoom(playerId, Disk.FROG);
        String greenString = " ";
        if (green == 10) {
            greenString = "";
        }
        greenString = greenString + green;
        int red = clientGame.getNumInDinningRoom(playerId, Disk.DRAGON);
        String redString = " ";
        if (red == 10) {
            redString = "";
        }
        redString = redString + red;
        int yellow = clientGame.getNumInDinningRoom(playerId, Disk.GNOME);
        String yellowString = " ";
        if (yellow == 10) {
            yellowString = "";
        }
        yellowString = yellowString + yellow;
        int pink = clientGame.getNumInDinningRoom(playerId, Disk.FAIRY);
        String pinkString = " ";
        if (pink == 10) {
            pinkString = "";
        }
        pinkString = pinkString + pink;
        int blue = clientGame.getNumInDinningRoom(playerId, Disk.UNICORN);
        String blueString = " ";
        if (blue == 10) {
            blueString = "";
        }
        blueString = blueString + blue;

        //Dining Room
        DiningRoom[0]  = "";
        DiningRoom[1]  = "";
        DiningRoom[2]  = "---------------------|";
        DiningRoom[3]  = "     DINING ROOM     |";
        DiningRoom[4]  = "_____________________|";
        DiningRoom[5]  = "     " + Colors.GREEN + "□ " + greenString + " FROGS " + Colors.RESET + "     |";
        DiningRoom[6]  = "---------------------|";
        DiningRoom[7]  = "    " + Colors.RED + "□ " + redString + " DRAGONS" + Colors.RESET + "     |";
        DiningRoom[8]  = "---------------------|";
        DiningRoom[9]  = "     " + Colors.YELLOW + "□ " + yellowString + " GNOMES " + Colors.RESET + "    |";
        DiningRoom[10] = "---------------------|";
        DiningRoom[11] = "     " + Colors.PINK + "□ " + pinkString + " FAIRIES " + Colors.RESET + "   |";
        DiningRoom[12] = "---------------------|";
        DiningRoom[13] = "     " + Colors.BLUE + "□ " + blueString + " UNICORNS" + Colors.RESET + "   |";
        DiningRoom[14] = "_____________________|";

        int totDining = clientGame.getNumInDinningRoom(playerId, Disk.FROG) + clientGame.getNumInDinningRoom(playerId, Disk.DRAGON) + clientGame.getNumInDinningRoom(playerId, Disk.GNOME) +
                clientGame.getNumInDinningRoom(playerId, Disk.FAIRY) + clientGame.getNumInDinningRoom(playerId, Disk.UNICORN);
        String totStudents = " ";
        if (totDining > 9) {
            totStudents = "";
        }
        totStudents = totStudents + totDining;

        DiningRoom[15] = "   Students: " + totStudents + "/50" + "   |";
        DiningRoom[16] = "_____________________|";

        // position 2 of boardMatrix - Professors String list
        String[] Professors = new String[17];
        boardMatrix[2] = Professors;
        int totProf = 0;

        //Professors
        Professors[0] = "";
        Professors[1] = "";
        Professors[2] = "------------|";
        Professors[3] = " PROFESSORS |";
        Professors[4] = "____________|";
        if (clientGame.hasProfessor(playerId, Disk.FROG)) {
            Professors[5] = "    " + Colors.GREEN + " ♔ " + Colors.RESET + "     |";
            totProf++;
        } else {
            Professors[5] = "    " + Colors.GREEN + "   " + Colors.RESET + "     |";
        }
        Professors[6] = "------------|";
        if (clientGame.hasProfessor(playerId, Disk.DRAGON)) {
            Professors[7] = "    " + Colors.RED + " ♔ " + Colors.RESET + "     |";
            totProf++;
        } else {
            Professors[7] = "    " + Colors.RED + "   " + Colors.RESET + "     |";
        }
        Professors[8] = "------------|";
        if (clientGame.hasProfessor(playerId, Disk.GNOME)) {
            Professors[9] = "    " + Colors.YELLOW + " ♔ " + Colors.RESET + "     |";
            totProf++;
        } else {
            Professors[9] = "    " + Colors.YELLOW + "   " + Colors.RESET + "     |";
        }
        Professors[10] = "------------|";
        if (clientGame.hasProfessor(playerId, Disk.FAIRY)) {
            Professors[11] = "    " + Colors.PINK + " ♔ " + Colors.RESET + "     |";
            totProf++;
        } else {
            Professors[11] = "    " + Colors.PINK + "   " + Colors.RESET + "     |";
        }
        Professors[12] = "------------|";
        if (clientGame.hasProfessor(playerId, Disk.UNICORN)) {
            Professors[13] = "    " + Colors.BLUE + " ♔ " + Colors.RESET + "     |";
            totProf++;
        } else {
            Professors[13] = "    " + Colors.BLUE + "   " + Colors.RESET + "     |";
        }
        Professors[14] = "____________|";

        Professors[15] = " Prof: " + totProf + "/5  |";
        Professors[16] = "____________|";

        // position 3 of boardMatrix - Tower Area String list
        String[] TowerArea = new String[17];
        boardMatrix[3] = TowerArea;

        int totTowers = clientGame.getBoards().get(playerId).getNumTowers();
        List<String> towers = new ArrayList<>();
        int i = 0;
        if (clientGame.getBoards().get(playerId).getTowerColor().equals(TowerColor.BLACK)) {
            for (; i < totTowers; i++) {
                towers.add(Colors.BLACK + "♜" + Colors.RESET);
            }
        } else if (clientGame.getBoards().get(playerId).getTowerColor().equals(TowerColor.WHITE)) {
            for (; i < totTowers; i++) {
                towers.add(Colors.WHITE + "♜" + Colors.RESET);
            }
        } else if (clientGame.getBoards().get(playerId).getTowerColor().equals(TowerColor.GREY)) {
            for (; i < totTowers; i++) {
                towers.add(Colors.GREY + "♜" + Colors.RESET);
            }
        }
        for (int j = i; j < 8; j++) {
            towers.add(" ");
        }
        //TowerArea
        TowerArea[0]  = "";
        TowerArea[1]  = "";
        TowerArea[2]  = "---------------------|";
        TowerArea[3]  = "      TOWER AREA     |";
        TowerArea[4]  = "_____________________|";
        TowerArea[5]  = "          |          |";
        TowerArea[6]  = "    " + towers.remove(0) + "     |    " + towers.remove(0) + "     |";
        TowerArea[7]  = "          |          |";
        TowerArea[8]  = "    " + towers.remove(0) + "     |    " + towers.remove(0) + "     |";
        TowerArea[9]  = "          |          |";
        TowerArea[10] = "    " + towers.remove(0) + "     |    " + towers.remove(0) + "     |";
        TowerArea[11] = "          |          |";
        TowerArea[12] = "    " + towers.remove(0) + "     |    " + towers.remove(0) + "     |";
        TowerArea[13] = "          |          |";
        TowerArea[14] = "_____________________|";
        TowerArea[15] = "     Towers: " + totTowers + "/8     |";
        TowerArea[16] = "_____________________|";

        return boardMatrix;
    }


    private String[][] givePlayedCards(){
        String[][] playedCards = new String[clientGame.getPlayedCards().size()][];
        for(int i=0;i<clientGame.getPlayedCards().size();i++){
            playedCards[i] =  giveCard(clientGame.getPlayedCards().get(i).getPriorityNumber(),clientGame.getPlayedCards().get(i).getMotherNatureMovement(),clientGame.getPlayedCards().get(i).getCardCharacter());
        }
        return playedCards;
    }

    private void printPlayedCards(){
        String[][] playedCards = givePlayedCards();
        int rows = playedCards.length;

        StringBuilder currentString = new StringBuilder("");
        if(rows>0){
            for (int i = 0; i < 5; i++) {
                currentString = new StringBuilder("");
                for (String[] playedCard : playedCards) {
                    currentString.append(playedCard[i]);
                    currentString.append("   ");
                }
                System.out.println(currentString);
            }
        }
        else {
            System.out.println("No cards played in this turn yet");
        }
    }

    //to be verified : prints all boards of players one next to the other, row-by-row
    private void printBoards() {
        String[][] boards = new String[clientGame.getBoards().size()*4][17];
        //position in the bigger matrix that contains all boards
        int boardsInx=0;

        //for each board of player
        for(int i = 0; i< clientGame.getBoards().size(); i++){

            //create a board matrix representation
            String[][] boardMatrix;
            boardMatrix = giveBoardMatrix(i);

            //position boardMatrix in boards
            for(int A=0;A<4;A++){
                boards[boardsInx]=boardMatrix[A];
                boardsInx++;
            }
        }

        //retrieve the whole row that will be printed
        StringBuilder currentString = new StringBuilder("");

        //for each column
        for(int Q=0; Q<17;Q++){
            currentString = new StringBuilder("");

            //variable that indicated when a board is finished in the current row
            int boardsSeparator = 0;

            for(int W=0;W<clientGame.getBoards().size()*4;W++){
                currentString.append(boards[W][Q]);
                boardsSeparator++;
                if(boardsSeparator==4){
                    currentString.append("   ");
                    boardsSeparator=0;
                }
            }
            System.out.println(currentString);
        }
    }

    private String[]giveCloud(int cloudId){
        String[] cloud = new String[3];
        String Student0;
        String Student1;
        String Student2;
        String Student3;

        if(clientGame.getClouds().get(cloudId).getStudents().size()==0){
            Student0 = " " ;
            Student1 = " " ;
            Student2 = " " ;
            Student3 = " " ;
            if(clientGame.getClouds().size()==3){
                Student3 = " ";
            }
        }
        else{
            Student0 = toColorString(clientGame.getClouds().get(cloudId).getStudents().get(0));
            Student1 = toColorString(clientGame.getClouds().get(cloudId).getStudents().get(1));
            Student2 = toColorString(clientGame.getClouds().get(cloudId).getStudents().get(2));
            Student3 =" ";
            if(clientGame.getBoards().size()==3){
                Student3 = toColorString(clientGame.getClouds().get(cloudId).getStudents().get(3));
            }
        }

        cloud[0] =  " ____________ ";
        cloud[1] =  "(   "+ Student0 +" "+Student1+" "+ Student2+" "+ Student3+"  )";
        cloud[2] =  "(____________)";
        return cloud;
    }


    private void printClouds(){
        String[][] clouds = new String[clientGame.getClouds().size()][];
        for(int i=0;i<clientGame.getClouds().size();i++){
            clouds[i] = giveCloud(i);
        }

        StringBuilder currentString = new StringBuilder("");
        int rows=clouds.length;

        for (int i = 0; i < 3; i++) {
            currentString = new StringBuilder("");
            for (String[] cloud : clouds) {
                currentString.append(cloud[i]);
                currentString.append("   ");
            }
            System.out.println(currentString);
        }
    }

    private String toColorString(Student student){
        switch (student.getPawn()){
            case GNOME:
                return "" + Colors.YELLOW_BACKGROUND + " " + Colors.RESET;
            case FROG:
                return "" + Colors.GREEN_BACKGROUND + " " + Colors.RESET;
            case DRAGON:
                return "" + Colors.RED_BACKGROUND + " " + Colors.RESET;
            case FAIRY:
                return "" + Colors.PINK_BACKGROUND + " " + Colors.RESET;
            case UNICORN:
                return "" + Colors.BLUE_BACKGROUND + " " + Colors.RESET;
            default:
                return " ";
        }
    }


}
