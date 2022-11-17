package it.polimi.ingsw.Controller;
import it.polimi.ingsw.Connection.Client.ClientMessage.*;
import it.polimi.ingsw.Connection.Message;
import it.polimi.ingsw.Connection.MessageType;
import it.polimi.ingsw.Connection.Server.ServerMessage.EndGameMessage;
import it.polimi.ingsw.Connection.Server.ServerMessage.ErrorMessage;
import it.polimi.ingsw.Connection.Server.VirtualView;
import it.polimi.ingsw.Exceptions.InvalidGamePhaseException;
import it.polimi.ingsw.Exceptions.InvalidInputException;
import it.polimi.ingsw.Exceptions.NotPossibleActionException;
import it.polimi.ingsw.Model.*;
import it.polimi.ingsw.Model.Character;
import java.util.*;
import it.polimi.ingsw.Observer.Observable;
import it.polimi.ingsw.Observer.Observer;
import static java.util.Collections.frequency;

/**
 * Class that represents the controller for an Eriantys game, entity that generates the business logic of a game using the model to allow players to perform game-moves
 * (in this class three methods could return as a value the winner player: 'playCard', 'moveMotherNature' or 'selectCloud')
 *
 */
public class Controller extends Observable implements Observer {
    private Game game;
    private ExpertGame expertGame;

    /* the first player to enter the game will be the one playing his card for first, just for the first turn */
    private int currentPlayerIndex = 0;

    private final int NumPlayers;
    private final boolean gameMode;
    private int turn = 0;
    protected List<TowerColor> availableTowers = new ArrayList<>();
    protected List<Character> availableCharacters = new ArrayList<>();
    protected int whiteChosen=0;
    protected int blackChosen=0;
    protected int playersEntered=0;
    protected Map<Integer,Boolean> playedExpert = new HashMap<>();              //this tells if a player has played an expert card in the current turn


    /* maps indexOfPlayer to the virtualView */
    protected Map <Integer, VirtualView> playersIdVirtualViews = new HashMap<>();

    protected Map <VirtualView,Integer> VirtualViewsPlayersId = new HashMap<>();
    protected boolean gameStarted = false;
    private Player winner=null;


    /**
     *List that keeps track of the phase each player is in using  ENUM PlayerPhase :the order of this list is based on how the
     * players enter the game,and is never changed; this is the same order kept in the list Players in the class 'game',
     * in the methods of this class playerId will always refer to index of this list
     *
     */
    protected List<PlayerPhase> PlayerPhases = new ArrayList<>();

    public List<TowerColor> getAvailableTowers() {
        return availableTowers;
    }

    public List<Character> getAvailableCharacters() {
        return availableCharacters;
    }

    public int getPlayersEntered() {
        return playersEntered;
    }

    public int getTurn() {
        return turn;
    }

    /**
     * Method that allows to retrieve player's game phase by playerId
     *
     * @param playerId the id of the player
     * @return the player-phase in the game
     */
    public PlayerPhase getPlayerPhase(int playerId) {
        return PlayerPhases.get(playerId);
    }

    /**
     * Method that simply tells if the playerId is a valid number
     *
     * @param playerId the id of the player
     * @return TRUE if valid, FALSE if not valid
     */
    public boolean isPlayerIdValid(int playerId) {
        return (playerId >= 0 && playerId < game.getNumPlayers());
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public Player winner(){
        return winner;
    }

    /**
     * Constructor called by the entity lobby when a new match is created by a player
     *
     * @param gameMode    indicates the type of game : true stand for expert game, false stars for normal game
     * @param numPlayers  indicates the number of players that will be present during the whole game
     */
    public Controller(boolean gameMode, int numPlayers) {
        this.NumPlayers = numPlayers;
        this.gameMode = gameMode;
        if (!gameMode)
            this.game = new Game(numPlayers);
        else {
            this.expertGame = new ExpertGame(numPlayers);
            this.game = expertGame;
            for(int i=0;i<numPlayers;i++){
                playedExpert.put(i,false);
            }
        }
        for (int i = 0; i < NumPlayers; i++) {
            PlayerPhases.add(PlayerPhase.CHOOSE_NICKNAME);
        }
        availableTowers.addAll(Arrays.asList(TowerColor.values()));
        availableCharacters.addAll(Arrays.asList(Character.values()));
        if(numPlayers!=3){
            availableTowers.remove(TowerColor.GREY);
        }
    }

    /**
     * Method used when a new player is created, the nickName will be set and the player will be added to the Lobby
     *
     * @param Nickname the Nickname chosen by the client associated to this player
     * @return if there is a place for the player in the related game the method will return the playerIndex
     * given to entered player, in other case the method returns -1
     *
     * @throws NotPossibleActionException thrown when the game has reached the maximum number of players
     */
    public int setNewPlayer(String Nickname) throws NotPossibleActionException {
        if(playersEntered<NumPlayers) {
            game.getPlayers().get(playersEntered).setNickname(Nickname);
            playersEntered++;
            if(playersEntered== NumPlayers) {
                for (int j = 0; j < NumPlayers; j++) {
                    PlayerPhases.set(j, PlayerPhase.CHOOSE_TOWER_COLOR);
                }
            }
            return playersEntered-1;
        }
        else throw new NotPossibleActionException("The game has already reached the total number of players!");
    }

    /**
     * Method that removes a player from a game given the Nickname, this method removes also this associated Tower Color e Character
     *
     * @param Nickname the String associated to that player as NickName
     * @return the original player Index if successfully removed, -1 otherwise
     */
    public int removePlayer(String Nickname){
        int index=-1;
        for(int i=0;i<game.getPlayers().size();i++){
            if(game.getPlayers().get(i).getNickname().equals(Nickname)){
                index= game.getPlayers().indexOf(game.getPlayers().get(i));
                TowerColor tower = game.getPlayers().get(index).getBoard().getTowerColor();
                if(tower!=null){
                    availableTowers.add(tower);
                }
                Character character = null;
                if(game.getPlayers().get(index).getDeck().size()>0){
                    character = game.getPlayers().get(index).getDeck().get(0).getCardCharacter();}
                if(character!=null){
                    availableCharacters.add(character);
                }
                game.getPlayers().remove(game.getPlayers().get(i));
            }
        }
        return index;
    }

    /**
     * Method that links the given character to the deck of the player given his playerId
     *
     * @param playerId the playerId (this index is used to retrieve the player from the model's Player list, follows the model's list's order)
     * @param color the ENUM that corresponds to the tower color chosen
     *
     * @throws NotPossibleActionException thrown when this method is called with the id of a player that can't perform the action at that moment
     * @throws InvalidInputException thrown when the playerId does not correspond to an actual player
     *
     * */
    public void chooseTowerColor(int playerId, TowerColor color) throws NotPossibleActionException, InvalidInputException {
        if (isPlayerIdValid(playerId)) {
            if(playersEntered==game.getNumPlayers()){
                if(game.getPlayers().get(playerId).getBoard().getTowerColor()==null){
                    if (availableTowers.contains(color) || NumPlayers==4 ) {
                        if (NumPlayers!=4){
                            boolean T = availableTowers.remove(color);
                            if(T){
                                game.setTowerColor(playerId,color);
                                PlayerPhases.set(playerId, PlayerPhase.CHOOSE_CHARACTER);
                            }
                        }else{
                            if(color.equals(TowerColor.WHITE)){
                                if((whiteChosen==0 )){
                                    game.setTowerColorTeams(playerId,color,true);
                                    game.getTeams().get(0).setTowerColor(TowerColor.WHITE);
                                    game.getTeams().get(0).addMember(game.getPlayers().get(playerId));
                                    PlayerPhases.set(playerId, PlayerPhase.CHOOSE_CHARACTER);
                                    whiteChosen++;
                                }
                                else if(whiteChosen==1){
                                    availableTowers.remove(color);
                                    game.setTowerColorTeams(playerId,color,false);
                                    game.getTeams().get(0).addMember(game.getPlayers().get(playerId));
                                    PlayerPhases.set(playerId, PlayerPhase.CHOOSE_CHARACTER);
                                    whiteChosen++;
                                }
                                else
                                    throw new NotPossibleActionException("Team has reached the maximum number of players!");
                            } else {
                                if((blackChosen==0 ) && color.equals(TowerColor.BLACK)){
                                    game.setTowerColorTeams(playerId,color,true);
                                    game.getTeams().get(1).setTowerColor(TowerColor.BLACK);
                                    game.getTeams().get(1).addMember(game.getPlayers().get(playerId));
                                    PlayerPhases.set(playerId, PlayerPhase.CHOOSE_CHARACTER);
                                    blackChosen++;}
                                else if(blackChosen==1){
                                    availableTowers.remove(color);
                                    game.setTowerColorTeams(playerId,color,false);
                                    game.getTeams().get(1).addMember(game.getPlayers().get(playerId));
                                    PlayerPhases.set(playerId, PlayerPhase.CHOOSE_CHARACTER);
                                    blackChosen++;
                                }
                                else
                                    throw new NotPossibleActionException("Team has reached the maximum number of players");
                            }
                        }
                    } else
                        throw new NotPossibleActionException("This Tower Color is already taken!");
                }else
                    throw new NotPossibleActionException("You hava already chosen your tower color!");
            }
            else
                throw new NotPossibleActionException("The game hasn't reached the full number of players. The tower color can't be chosen yet!");
        } else
            throw new InvalidInputException("Invalid playerId!");
    }


    /**
     * Method that links the given character to the given player
     *
     * @param playerId the playerId that follows the order of the game list
     * @param character the ENUM value that corresponds to the character chosen
     *
     * @throws InvalidInputException  thrown when this method is called with the id of a player that can't perform the action at that moment
     * @throws InvalidGamePhaseException  thrown when this method is called by a player in the wrong phase of the game
     * @throws NotPossibleActionException thrown when the player has already performed this move of the game
     * */
    /* allows a player to choose the wanted deck Character (the wanted character on the cards) */
    public void chooseCharacter(int playerId, Character character) throws InvalidInputException, InvalidGamePhaseException, NotPossibleActionException {
        if (isPlayerIdValid(playerId)) {
            if (getPlayerPhase(playerId).equals(PlayerPhase.CHOOSE_CHARACTER)) {
                if (availableCharacters.contains(character)) {
                    if(game.getPlayers().get(playerId).getDeck().size()<1){
                        PlayerPhases.set(playerId, PlayerPhase.IDLE);
                        availableCharacters.remove(character);
                        game.setDeckCharacter(playerId, character);
                    }else
                        throw new NotPossibleActionException("You have already chosen your deck Character!");
                }else
                    throw new InvalidInputException("This character is already taken!");
            } else
                throw new InvalidGamePhaseException();
        } else
            throw new InvalidInputException("Invalid playerId!");
    }


    /**
     * Method that signals to the model the beginning of the game, and set all parameters in order to begin a match
     *
     * @throws NotPossibleActionException thrown when the game is not ready to start because some parameters are still missing
     * */
    /* method for starting the game after all parameters of all the players (towerColor and deck character) have been chosen */
    public void startGame() throws NotPossibleActionException {
        if(frequency(PlayerPhases,PlayerPhase.IDLE)==NumPlayers ){
            game.setFirstPlayer(game.getPlayers().get(0));
            game.getIslands().get(0).setMotherNature(true);    /*mother nature set on the first island*/
            for (int i = 0; i < NumPlayers; i++) {
                game.placeStudentFromBagToCloud(game.getClouds().get(i));
            }
            gameStarted=true;
            game.startGame();
            for (int i = 0; i < NumPlayers; i++) {
                PlayerPhases.set(i, PlayerPhase.PLAY_CARD);
            }
            gameStarted=true;
            turn=1;
            game.gameTurnChange();
        }
        else
            throw new NotPossibleActionException("Player parameters still have to be set");
    }


    /**
     * Method that allows a player to play a card given the card Id in the list of cards (once the current player has played his card his state will
     * be changed to 'PLAYED-CARD' and the next player following the order of the list will move to 'playCard' phase)
     *
     * @param playerId the Id of the player in the list of Players present in the model
     * @param cardId the Id of the card asked to be played
     *
     * @throws InvalidInputException  thrown when this method is called with the id of a player that can't perform the action at that moment
     * @throws InvalidGamePhaseException  thrown when the method is called by a player in the wrong moment of the game
     * @throws NotPossibleActionException thrown when the player tries to play a card already played by another player in the current turn or it's not yet his turn
     *
     * */
    public void playCard(int playerId, int cardId) throws NotPossibleActionException, InvalidGamePhaseException, InvalidInputException {
        if (isPlayerIdValid(playerId)) {
            /*if you are not first player, and you are in PLAY-CARD phase then you can play your card */
            if ((getPlayerPhase(playerId).equals(PlayerPhase.PLAY_CARD) && game.getFirstPlayer() != game.getPlayers().get(playerId)) ||
                    /*if you are the first player in first turn all players have to have set their game parameters before you can play your card*/
                    (frequency(PlayerPhases, PlayerPhase.PLAY_CARD) == game.getNumPlayers() && turn == 1 && game.getFirstPlayer() == game.getPlayers().get(playerId))
                    /*if you are the first player and not in the first turn, and you are in PLAY-CARD phase then you can play your card*/
                    || (turn != 1 && game.getFirstPlayer() == game.getPlayers().get(playerId) && getPlayerPhase(playerId).equals(PlayerPhase.PLAY_CARD))) {
                if (playerId == currentPlayerIndex) {
                    if (game.isCardPlayable(game.getPlayers().get(playerId), game.getPlayers().get(playerId).getDeck().get(cardId))) {
                        game.playCard(game.getPlayers().get(playerId), cardId);
                        PlayerPhases.set(playerId, PlayerPhase.CARD_PLAYED);
                        int lastIndexPlayer = -1;  //the player that is before in anti-clockwise order to the player that has to play the card first
                        if (game.getFirstPlayer().equals(game.getPlayers().get(0))) {
                            lastIndexPlayer = this.NumPlayers - 1;
                        } else
                            lastIndexPlayer = game.getPlayers().indexOf(game.getFirstPlayer()) - 1;
                        if (playerId != lastIndexPlayer)          /*if not the last player*/ {
                            if (playerId + 1 == NumPlayers) {
                                PlayerPhases.set(0, PlayerPhase.PLAY_CARD);
                                currentPlayerIndex=0;
                            } else {
                                PlayerPhases.set(playerId + 1, PlayerPhase.PLAY_CARD);
                                currentPlayerIndex++;
                            }
                        } else
                               /*determine if all players have played their card and set the first player of this round, so we can move to the next part of the game
                                 and the first player can move to the PlayerPhase MOVE-ENTRANCE */
                             /*set the turn order*/ {
                            if (frequency(PlayerPhases, PlayerPhase.CARD_PLAYED) == game.getNumPlayers()) {
                                game.getActionPlayers().clear();
                                Player oldFirstPlayer = game.getFirstPlayer();   /*first to have played card*/
                                game.setFirstPlayer();
                                List<Integer> cardValues = new ArrayList<Integer>();
                                List<Player> PlayersCard = new ArrayList<Player>();
                                int j = 0;
                                int lastIndex = 0;
                                if (game.getPlayers().indexOf(oldFirstPlayer) == 0){
                                    lastIndex = NumPlayers - 1;
                                }
                                else{
                                    lastIndex = game.getPlayers().indexOf(oldFirstPlayer) - 1;
                                }
                                for (int i = game.getPlayers().indexOf(oldFirstPlayer); i != lastIndex; i++) {
                                    cardValues.add(j, game.getPlayers().get(i).getPlayedCards().get(game.getPlayers().get(i).getPlayedCards().size() - 1).getPriorityNumber());
                                    PlayersCard.add(j, game.getPlayers().get(i));
                                    j++;
                                    if (i == NumPlayers - 1) {
                                        i = -1;
                                    }
                                }
                                cardValues.add(j, game.getPlayers().get(lastIndex).getPlayedCards().get(game.getPlayers().get(lastIndex).getPlayedCards().size() - 1).getPriorityNumber());
                                PlayersCard.add(j, game.getPlayers().get(lastIndex));
                                for (int A = 0; A < NumPlayers; A++) {
                                    int min = cardValues.get(0);
                                    int index = 0;
                                    for (int i = 0; i < cardValues.size(); i++) {
                                        if (min > cardValues.get(i)) {
                                            min = cardValues.get(i);
                                            index = i;
                                        }
                                    }
                                    game.getActionPlayers().add(PlayersCard.get(index));
                                    cardValues.remove(index);
                                    PlayersCard.remove(index);
                                }
                                for (int i = 0; i < this.NumPlayers; i++) {
                                    PlayerPhases.set(i, PlayerPhase.WAITING_TURN);
                                }

                                PlayerPhases.set(game.getPlayers().indexOf(game.getFirstPlayer()), PlayerPhase.ENTRANCE_MOVE);
                                currentPlayerIndex = game.getPlayers().indexOf(game.getFirstPlayer());
                            }
                        }
                        game.gameTurnChange();
                    } else
                        throw new NotPossibleActionException("This card has already been played by another player!");
                } else
                    throw new NotPossibleActionException("It is not your turn to play your card yet!");
            } else
                throw new InvalidGamePhaseException();
        } else
            throw new InvalidInputException("Invalid playerId!");
    }


    /**
     * Method that allows a player to move a student on an island, given the id of student and id of the island
     *
     * @param playerId the id of the player in the list of Players present in the model
     * @param islandId the id of the island of which the player asks to move his student
     * @param studentId the id of the student that the player asks to move on island
     *
     * @throws InvalidInputException  thrown when this method is called with a wrong parameter
     * @throws NotPossibleActionException thrown when the player has already moved the maximum number of students from his entry
     * @throws InvalidGamePhaseException  thrown when this method is called by a player in the wrong phase of the game
     * */
    public void moveStudentOnIsland(int playerId, int islandId, int studentId) throws NotPossibleActionException, InvalidGamePhaseException, InvalidInputException {
        if (isPlayerIdValid(playerId)) {
            if (getPlayerPhase(playerId).equals(PlayerPhase.ENTRANCE_MOVE)) {
                if (((this.NumPlayers == 2 || this.NumPlayers == 4) &&
                        (game.getPlayers().get(playerId).getBoard().getEntrance().size() > 7 - game.getNumOfStudentsOnCloud() &&
                                game.getPlayers().get(playerId).getBoard().getEntrance().size() <=7)) ||
                        (this.NumPlayers == 3 && game.getPlayers().get(playerId).getBoard().getEntrance().size() > 9 - game.getNumOfStudentsOnCloud() &&
                                game.getNumOfStudentsOnCloud() <=9)) {
                    if (islandId >= 0 && islandId < game.getIslands().size()) {
                        if (studentId >= 0 && studentId < game.getPlayers().get(playerId).getBoard().getEntrance().size()) {
                            game.moveStudentOnIsland(game.getPlayers().get(playerId), game.getIslands().get(islandId), studentId);
                            if (((this.NumPlayers == 2 || this.NumPlayers == 4) &&
                                    (game.getPlayers().get(playerId).getBoard().getEntrance().size() == 7 - game.getNumOfStudentsOnCloud())) ||
                                    (this.NumPlayers == 3 && game.getPlayers().get(playerId).getBoard().getEntrance().size() == 9 - game.getNumOfStudentsOnCloud())) {
                                PlayerPhases.set(playerId, PlayerPhase.MOTHER_NATURE_MOVE);
                            }
                            game.gameTurnChange();
                        } else
                            throw new InvalidInputException("Invalid studentId!");
                    } else
                        throw new InvalidInputException("Invalid islandId!");
                } else
                    throw new NotPossibleActionException("You can't move any more students in this turn!");
            } else
                throw new InvalidGamePhaseException();
        } else
            throw new InvalidInputException("Invalid playerId!");
    }

    /**
     * Method that allows a player to move a student in the dining room of his Board
     *
     * @param playerId the id of the player in the list of Players present in the model
     * @param StudentId the id of the student that the player asks to move on
     *
     * @throws InvalidInputException  thrown when this method is called with a wrong parameter
     * @throws NotPossibleActionException thrown when the player has already moved the maximum number of students from his entry
     * @throws InvalidGamePhaseException  thrown when this method is called by a player in the wrong phase of the game
     *
     * */
    public void moveStudentsOnDiningRoom(int playerId, int StudentId) throws NotPossibleActionException, InvalidGamePhaseException, InvalidInputException {
        if (isPlayerIdValid(playerId)) {
            if (getPlayerPhase(playerId).equals(PlayerPhase.ENTRANCE_MOVE) && currentPlayerIndex == playerId) {
                if (((this.NumPlayers == 2 || this.NumPlayers == 4) &&
                        (game.getPlayers().get(playerId).getBoard().getEntrance().size()> 7 - game.getNumOfStudentsOnCloud() &&
                                game.getPlayers().get(playerId).getBoard().getEntrance().size() <=7)) ||
                        (this.NumPlayers == 3 && game.getPlayers().get(playerId).getBoard().getEntrance().size() > 9 - game.getNumOfStudentsOnCloud() &&
                                game.getNumOfStudentsOnCloud() <=9)) {
                    if (StudentId >= 0 && StudentId < game.getPlayers().get(playerId).getBoard().getEntrance().size()) {
                        Disk diskMoved = game.getPlayers().get(playerId).getBoard().getEntrance().get(StudentId).getPawn();
                        game.moveStudentOnDiningRoom(game.getPlayers().get(playerId), StudentId);
                        for (Disk d : Disk.values()) {
                            boolean flag = true;
                            for(Player p: game.getPlayers()) {
                                if(NumPlayers<=3) {
                                    if(game.getPlayers().get(playerId).getBoard().getDiningRoom().get(d).size()<=p.getBoard().getDiningRoom().get(d).size() &&
                                            game.getPlayers().indexOf(p)!=playerId) {
                                        flag=false;
                                        break;
                                    }
                                } else {
                                    if(game.getPlayers().get(playerId).getBoard().getDiningRoom().get(d).size()<=p.getBoard().getDiningRoom().get(d).size() &&
                                            game.getPlayers().indexOf(p)!=playerId && game.getPlayers().indexOf(p)!=game.getPlayers().indexOf(game.getPlayerMate(playerId))) {
                                        flag=false;
                                        break;
                                    }
                                }
                            }
                            if(flag) {
                                game.addPlayerProfessor(playerId,d);
                            }
                        }
                        if (((this.NumPlayers == 2 || this.NumPlayers == 4) &&
                                (game.getPlayers().get(playerId).getBoard().getEntrance().size() == 7 - game.getNumOfStudentsOnCloud())) ||
                                (this.NumPlayers == 3 && game.getPlayers().get(playerId).getBoard().getEntrance().size() == 9 - game.getNumOfStudentsOnCloud())) {
                            PlayerPhases.set(playerId, PlayerPhase.MOTHER_NATURE_MOVE);
                        }

                        if(gameMode){
                            ExpertGame expert = (ExpertGame) game;
                            switch (game.getPlayers().get(playerId).getBoard().getDiningRoom().get(diskMoved).size()) {
                                case 2:
                                    expert.giveCoin(game.getPlayers().get(playerId));
                                    break;
                                case 5:
                                    expert.giveCoin(game.getPlayers().get(playerId));
                                    break;
                                case 8:
                                    expert.giveCoin(game.getPlayers().get(playerId));
                                    break;
                            }
                        }
                        game.gameTurnChange();
                    } else throw new InvalidInputException("Invalid studentId!");
                } else
                    throw new NotPossibleActionException("You can't move any more students in this turn!");
            } else
                throw new InvalidGamePhaseException();
        } else
            throw new InvalidInputException("Invalid playerId!");
    }


    /**
     * Method that allows a player to move a student on an island, given the id of student and id of the island
     *
     * @param playerId the id of the player in the list of Players present in the model
     * @param movements the id of the island of which the player asks to move his student
     *
     * @throws InvalidInputException  thrown when this method is called with a wrong parameter for the playerId
     * @throws NotPossibleActionException thrown when the player tries to move Mother Nature of a number of steps not allowed by the card played in the turn
     * @throws InvalidGamePhaseException  thrown when this method is called by a player in the wrong phase of the game
     *
     * @return if all tower are finished or there are only three islands left in the game the method returns the Player that has won the game, in every other case it returns null
     * */
    public Player moveMotherNature(int playerId, int movements) throws InvalidGamePhaseException, InvalidInputException, NotPossibleActionException {
        if (isPlayerIdValid(playerId)) {
            if (getPlayerPhase(playerId).equals(PlayerPhase.MOTHER_NATURE_MOVE)) {
                if (movements > 0 && movements <= game.getPlayers().get(playerId).getPlayedCards().get(game.getPlayers().get(playerId).getPlayedCards().size() - 1).getMotherNatureMovement()) {
                    /* ^-(the if means: 'if' the number of moments is possible taking into consideration che card played -^ */

                    int indexIsland = -1;   //island that has mother nature
                    int joinValue;          //tells if the island can be joined and in which direction
                    game.moveMotherNature(movements);
                    for (int i = 0; i < game.getIslands().size(); i++) {
                        if (game.getIslands().get(i).hasMotherNature()){
                            indexIsland = i;}
                    }

                    //for every player in the game check if he has the max influence
                    for(Player p : game.getPlayers()){
                        //da verificare : 4 players - teams
                        if (game.getNumPlayers() == 4) {
                            //place tower
                            if (game.getIslandOwner(game.getIslands().get(indexIsland)) == null) {
                                if (game.isTowerPlaceable(game.getTeamOfPlayer(game.getPlayers().get(playerId)), game.getIslands().get(indexIsland), game.currentTeamInfluence(game.getIslands().get(indexIsland)))) {
                                    game.placeTowerOnIsland(game.getPlayers().get(playerId), game.getIslands().get(indexIsland));
                                }
                            //replace tower
                            } else {
                                if (game.isTowerReplaceable(game.getTeamOfPlayer(game.getPlayers().get(playerId)), game.getIslands().get(indexIsland)))
                                    game.replaceTowerOnIsland(game.getPlayers().get(playerId), game.getIslands().get(indexIsland));
                            }
                        }
                        // game with 2/3 players
                        else {
                            //place tower
                            if (game.getIslandOwner(game.getIslands().get(indexIsland)) == null) {
                                if (game.isTowerPlaceable(p, game.getIslands().get(indexIsland), game.currentInfluence(game.getIslands().get(indexIsland)))) {
                                    game.placeTowerOnIsland(p, game.getIslands().get(indexIsland));
                                }
                            //replace tower
                            } else if (game.isTowerReplaceable(p, game.getIslands().get(indexIsland))) {
                                game.replaceTowerOnIsland(p, game.getIslands().get(indexIsland));
                            }
                        }
                       //
                    }
                    /*check if the Island can be joined in one or two directions and joins islands*/
                    while(game.isIslandJoinable(game.getIslands().get(indexIsland)) !=0) {
                        joinValue = game.isIslandJoinable(game.getIslands().get(indexIsland));
                        if (joinValue == 1) {
                            if(indexIsland==game.getIslands().size()-1){
                                game.joinIslands(game.getIslands().get(0), game.getIslands().get(indexIsland));
                            }  else {
                                game.joinIslands(game.getIslands().get(indexIsland), game.getIslands().get(indexIsland + 1));
                            }
                        }
                        else if (joinValue == -1) {
                            if (indexIsland == 0) {
                                game.joinIslands(game.getIslands().get(game.getIslands().size()-1), game.getIslands().get(0));
                            }
                            else
                                {
                                    game.joinIslands(game.getIslands().get(indexIsland - 1 ), game.getIslands().get(indexIsland));
                                }
                        }
                        for (int i = 0; i < game.getIslands().size(); i++) {
                            if (game.getIslands().get(i).hasMotherNature()){
                                indexIsland = i;}
                        }

                        for (int i = 0; i < game.getIslands().size(); i++) {
                            if (game.getIslands().get(i).hasMotherNature()) {
                                indexIsland = i;
                            }
                        }
                    }

                    PlayerPhases.set(playerId, PlayerPhase.CLOUD_CHOICE);
                    game.gameTurnChange();
                    if (game.declareWinner() != null ) {
                        for (int p = 0; p < this.NumPlayers; p++) {
                            PlayerPhases.set(p, PlayerPhase.ENDGAME);
                        }
                        gameStarted=false;
                        return game.declareWinner();
                    }
                    else{
                        return null;
                    }
                } else
                    throw new NotPossibleActionException("Invalid Mother Nature movement!");
            } else
                throw new InvalidGamePhaseException();
        } else
            throw new InvalidInputException("Invalid playerId!");
    }

    /**
     * Method that allows a player to select from which cloud to refill the students from his entry
     *
     * @param playerId the id of the player in the list of Players present in the model
     * @param cloudId the id of the cloud
     *
     * @throws NotPossibleActionException thrown when the cloud is empty and can't be selected or it's not that player's turn
     * @throws InvalidGamePhaseException  thrown when this method is called by a player in the wrong phase of the game
     * @throws InvalidInputException thrown when this method is called with a wrong parameter for the playerId or cloudId
     *
     * @return if all students are finished in the bag the method returns the Player that has won the game, in every other case it returns null
     * */
    public Player selectCloud(int playerId, int cloudId) throws NotPossibleActionException, InvalidGamePhaseException, InvalidInputException {
        if (isPlayerIdValid(playerId)) {
            if (getPlayerPhase(playerId).equals(PlayerPhase.CLOUD_CHOICE)) {
                if (game.getClouds().get(cloudId).getStudents().size() > 0 ) {
                    if(cloudId<game.getClouds().size()) {
                        if (game.getPlayers().get(playerId).equals(game.getPlayers().get(currentPlayerIndex))) {
                                game.moveStudentsFromCloud(game.getPlayers().get(playerId), game.getClouds().get(cloudId));
                                PlayerPhases.set(playerId, PlayerPhase.WAITING_NEXT_TURN);
                                /*move to next turn*/
                                if (frequency(PlayerPhases, PlayerPhase.WAITING_NEXT_TURN) == NumPlayers) {
                                    turn++;
                                    for (int i = 0; i < game.getNumPlayers(); i++) {
                                        game.placeStudentFromBagToCloud(game.getClouds().get(i));
                                    }
                                    currentPlayerIndex = game.getPlayers().indexOf(game.getFirstPlayer());
                                    if (currentPlayerIndex == -1){
                                        int min = 110;
                                        int minp = 0;
                                        for (int i = 0; i < game.getPlayers().size(); i++) {
                                            if (game.getPlayers().get(i).getPlayedCards().get(game.getPlayers().get(i).getPlayedCards().size()-1).getPriorityNumber()<min){
                                                min = game.getPlayers().get(i).getPlayedCards().get(game.getPlayers().get(i).getPlayedCards().size()-1).getPriorityNumber();
                                                minp = i;
                                            }
                                        }
                                        currentPlayerIndex = minp;
                                    }
                                    //currentPlayerIndex = game.getPlayers().indexOf(game.getFirstPlayer());
                                    for (int i = 0; i < game.getNumPlayers(); i++) {
                                        PlayerPhases.set(i, PlayerPhase.PLAY_CARD);
                                    }

                                    //reset of the play card method for expert in the new turn
                                    for(Player p: game.getPlayers()){
                                        playedExpert.put(game.getPlayers().indexOf(p),false);
                                    }
                                } else
                                    /* move to next player -- ENTRANCE MOVE phase set for the next player in the turn order */ {
                                    int temp = game.getActionPlayers().indexOf(game.getPlayers().get(playerId)) + 1;   //index in next player in the actionPlayer's list
                                    currentPlayerIndex = game.getPlayers().indexOf(game.getActionPlayers().get(temp));
                                    PlayerPhases.set(currentPlayerIndex, PlayerPhase.ENTRANCE_MOVE);
                                }
                                game.gameTurnChange();
                                if(gameMode){
                                    game=expertGame.getCharacterCards().get(0).getFatherGame();
                                    expertGame=expertGame.getCharacterCards().get(0).getFatherGame();
                                }
                            if (game.declareWinner() != null ) {
                                for (int p = 0; p < this.NumPlayers; p++) {
                                    PlayerPhases.set(p, PlayerPhase.ENDGAME);
                                }
                                gameStarted=false;
                                return game.declareWinner();
                            }
                            else{
                                return null;
                            }
                        } else
                            throw new NotPossibleActionException("It's not this player's turn!");
                    }else
                        throw new InvalidInputException("This cloud does not exist");
                } else
                    throw new NotPossibleActionException("The cloud has been already selected.");
            } else
                throw new InvalidGamePhaseException();
        } else
            throw new InvalidInputException("Invalid playerId");
    }

    /**
     * Method that allows to play a card for an Expert Game
     *
     * @param playerId the id of the player
     * @param cardId the Id of the expert card to be played
     * @param parameters the parameters requested to play the card
     *
     * @return the Player that has won the game, in every other case it returns null
     * */
    public Player playCharacterCard(int playerId, int cardId, String parameters) throws NotPossibleActionException, InvalidInputException {
        if(gameMode){
            if(currentPlayerIndex==playerId){
                if(getPlayerPhase(playerId)!=PlayerPhase.PLAY_CARD && getPlayerPhase(playerId)!=PlayerPhase.CARD_PLAYED) {
                      if(!playedExpert.get(playerId)){
                            try{
                                try {
                                    SpecialCharacterCard special = (SpecialCharacterCard) expertGame.getCharacterCards().get(cardId);
                                    game = expertGame.playCharacterCard(special, playerId, parameters);
                                    expertGame=(ExpertGame) game;
                                    for(VirtualView v : getPlayersIdVirtualViews()){
                                        game.addObserver(v);
                                    }
                                    playedExpert.put(playerId,true);
                                } catch (ClassCastException e) {
                                    game = expertGame.playCharacterCard(((SimpleCharacterCard) expertGame.getCharacterCards().get(cardId)), playerId, parameters);
                                    expertGame=(ExpertGame) game;
                                    playedExpert.put(playerId,true);
                                }
                                if (game.declareWinner() != null ) {
                                    for (int p = 0; p < this.NumPlayers; p++) {
                                        PlayerPhases.set(p, PlayerPhase.ENDGAME);
                                    }
                                    gameStarted=false;
                                    return game.declareWinner();
                                }
                                else{
                                    return null;
                                }
                            }catch(InvalidInputException e){
                                throw new NotPossibleActionException(e.getMessage());
                            }
                    }else
                       throw new NotPossibleActionException("You have already played an expert card in this turn please wait for a new turn!");
                }else
                    throw new NotPossibleActionException("You can't play a Character Card now, wait for your action turn!");
            }else
                throw new InvalidInputException("It is not your turn, you can't play a Character Card!");
        } else
            throw new NotPossibleActionException("We are not in and Expert Game");
    }

    /**
     * Method that given playerId returns his VirtualView
     *
     * */
    public VirtualView getVirtualView(int playerId) {
        return playersIdVirtualViews.get(playerId);
    }

    /**
     * Method that returns the list of all the virtual views linked to the players in the Game
     *
     * */
    public List<VirtualView> getPlayersIdVirtualViews() {
        List<VirtualView> listVirtualViews = new ArrayList<>();
        for (int i=0;i<NumPlayers; i++) {
            listVirtualViews.add(playersIdVirtualViews.get(i));
        }
        return listVirtualViews;
    }

    /**
     * Getter for the model managed by the Controller
     *
     * @return the model that represents an Eriantys game
     */
    public Game getModel() {
        if (!gameMode)
            return game;
        else return expertGame;
    }

    /**
     * Method that given the VirtualView returns the playerId associated to it in the game
     *
     * */
    public int getPlayerId(VirtualView virtualView) {
        return VirtualViewsPlayersId.get(virtualView);
    }

    public void setVirtualView(int playerId, VirtualView virtualView) {
        playersIdVirtualViews.put(playerId,virtualView);
        VirtualViewsPlayersId.put(virtualView,playerId);
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }


    /**
     * Override of the method of the Observer class: this method receives the message sent by the client from the virtualView and calls the
     * methods of the controller that will trigger the change in the model
     *
     * @param message the message that needs to be unPacked and updated in the model
     *
     * */
    @Override
    public void update(Message message) {
        int playerId = -1;
        for(Player p : game.getPlayers()){
            if(p.getNickname().equals(message.getNickname()))
            {playerId = game.getPlayers().indexOf(p);
                break;}}
        try {
            switch (message.getMessageType()) {
                case TOWER_SETUP: {
                    TowerColorMessage messageCasted = (TowerColorMessage) message;
                    switch (messageCasted.getTowerColor().toLowerCase()) {
                        case "black":
                            this.chooseTowerColor(playerId, TowerColor.BLACK);
                            break;
                        case "white":
                            this.chooseTowerColor(playerId,TowerColor.WHITE);
                            break;
                        case "grey":
                            if(game.getNumPlayers()==3){
                                this.chooseTowerColor(playerId,TowerColor.GREY);
                            } else {
                                Message ErrorMessage = new ErrorMessage(message.getNickname(),"This tower Color is not selectable in this player mode!");
                                getVirtualView(playerId).update(ErrorMessage);}
                            break;
                        default:
                            Message ErrorMessage = new ErrorMessage(message.getNickname(),"This tower color does not exits!");
                            getVirtualView(playerId).update(ErrorMessage);
                            break;}
                    break;}
                case CHARACTER_SETUP: {
                    CharacterMessage messageCasted = (CharacterMessage) message;
                    switch (messageCasted.getCharacter().toLowerCase()) {
                        case "witch":
                            this.chooseCharacter(playerId, Character.WITCH);
                            break;
                        case "ninja" :
                            this.chooseCharacter(playerId, Character.NINJA);
                            break;
                        case "wizard":
                            this.chooseCharacter(playerId, Character.WIZARD);
                            break;
                        case "king":
                            this.chooseCharacter(playerId, Character.KING);
                            break;
                        default:
                            Message ErrorMessage = new ErrorMessage(message.getNickname(),"This character does not exist!");
                            getVirtualView(playerId).update(ErrorMessage);
                            break;}
                    /* if all players have chosen their tower and character start the game automatically */
                    if(availableTowers.size()==0 && availableCharacters.size()== Character.values().length - NumPlayers){
                        this.startGame();
                    }
                    break;
                }
                case PLAY_CARD: {
                    PlayCardMessage messageCasted = (PlayCardMessage) message;
                    if (!messageCasted.isExpertCard()) {
                        this.playCard(playerId, messageCasted.getCard());
                    }
                    else {
                        winner = playCharacterCard(playerId,messageCasted.getCard(),messageCasted.getParameters());
                    }
                    if(winner!=null){
                        if(NumPlayers<=3){
                            int winnerId = game.getPlayers().indexOf(winner);
                            Message winnerMessage = new EndGameMessage(winner.getNickname());
                            getPlayersIdVirtualViews().get(winnerId).update(winnerMessage);
                            for(Player p: game.getPlayers()){
                                if(p!=winner){
                                    Message loserMessage = new EndGameMessage(p.getNickname(),winner.getNickname());
                                    getPlayersIdVirtualViews().get(game.getPlayers().indexOf(p)).update(loserMessage);
                                }
                            }
                        }else{

                        }
                    }
                    break;
                }
                case MOVE_STUDENT_DINING_ROOM: {
                    MoveStudentOnDiningRoomMessage messageCasted = (MoveStudentOnDiningRoomMessage) message;
                    this.moveStudentsOnDiningRoom(playerId, messageCasted.getStudent());
                    break;}
                case MOVE_STUDENT_ISLAND: {
                    MoveStudentOnIslandMessage messageCasted = (MoveStudentOnIslandMessage) message;
                    this.moveStudentOnIsland(playerId, messageCasted.getIslandPosition(), messageCasted.getStudent());
                    break;}
                case MOVE_MOTHER_NATURE: {
                    MoveMotherNatureMessage messageCasted = (MoveMotherNatureMessage) message;
                    winner = this.moveMotherNature(playerId, messageCasted.getMotherNatureSteps());
                    if(winner!=null){
                        int winnerId = game.getPlayers().indexOf(winner);
                        Message winnerMessage = new EndGameMessage(winner.getNickname());
                        getPlayersIdVirtualViews().get(winnerId).update(winnerMessage);
                        for(Player p: game.getPlayers()){
                            if(p!=winner){
                                Message loserMessage = new EndGameMessage(p.getNickname(),winner.getNickname());
                                getPlayersIdVirtualViews().get(game.getPlayers().indexOf(p)).update(loserMessage);
                            }
                        }
                    }
                    break;
                }
                case CHOOSE_CLOUD: {
                    ChooseCloudMessage messageCasted = (ChooseCloudMessage) message;
                    winner = this.selectCloud(playerId, messageCasted.getCloudId());
                    if(winner!=null){
                        int winnerId = game.getPlayers().indexOf(winner);
                        Message winnerMessage = new EndGameMessage(winner.getNickname());
                        getPlayersIdVirtualViews().get(winnerId).update(winnerMessage);
                        for(Player p: game.getPlayers()){
                            if(p!=winner){
                                Message loserMessage = new EndGameMessage(p.getNickname(),winner.getNickname());
                                getPlayersIdVirtualViews().get(game.getPlayers().indexOf(p)).update(loserMessage);
                            }
                        }
                    }
                    break;
                }
                default: {
                    System.out.println("Error in the performed action!");
                    Message ErrorMessage = new ErrorMessage(message.getNickname(),"Error! This action is not valid!");
                    getVirtualView(playerId).update(ErrorMessage);
                    break;
                }
            }
        } catch(Exception e1) {
            Message ErrorMessage = new ErrorMessage(message.getNickname(),e1.getMessage());
            getVirtualView(playerId).update(ErrorMessage);
        }
    }

    @Override
    public void update(MessageType messageType) {}
}