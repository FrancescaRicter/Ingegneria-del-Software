package it.polimi.ingsw.View;


import it.polimi.ingsw.Model.*;
import it.polimi.ingsw.Model.Character;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that consists in a reduced game representation for client viewing
 *
 * */
public class GameRepresentation implements Serializable {
    private int playerId;
    private int numPlayers;
    private  List<Card> myDeck = new ArrayList<Card>();
    private List<Board> Boards = new ArrayList<Board>();
    protected List<Island> Islands = new ArrayList<Island>();
    protected List<Cloud> Clouds = new ArrayList<Cloud>();
    protected List<CharacterCard> CharacterCard = new ArrayList<CharacterCard>();
    protected List<Integer> Coins = new ArrayList<Integer>();
    protected List<Card> playedCards = new ArrayList<Card>();
    protected String Log= null;

    protected List<String> playerNicknames = new ArrayList<String> ();
    protected Map<String,Character> playersCharacter = new HashMap<>() ;
    protected boolean isExpert=false;

    /**
     * Constructor method that returns a reduced game representation for client viewing given the original game model
     *
     * */
    public GameRepresentation(GameRepresentation gm){
        myDeck = gm.myDeck;
        Boards = gm.Boards;
        Islands = gm.Islands;
        Clouds = gm.Clouds;
        CharacterCard = gm.CharacterCard;
        Coins = gm.Coins;
        playerNicknames= gm.playerNicknames;
        isExpert = gm.isExpert;
        playersCharacter=gm.playersCharacter;
    }

    /**
     * Constructor method that returns a reduced game representation for client viewing given the original game model
     *
     * @param game the model of the game
     * @param playerId the id of the player
     * */
    public GameRepresentation(Game game, int playerId){
        this.myDeck = game.getPlayers().get(playerId).getDeck();
        this.Islands = game.getIslands();
        this.Clouds = game.getClouds();
        for(int i=0;i<game.getPlayers().size();i++){
            this.playerNicknames.add(game.getPlayers().get(i).getNickname());
            this.Boards.add(game.getPlayers().get(i).getBoard());
            this.playersCharacter.put(game.getPlayers().get(i).getNickname(),game.getPlayers().get(i).getDeck().get(0).getCardCharacter());
        }
        try {
            ExpertGame gg = (ExpertGame) game;
            
            this.CharacterCard = gg.getCharacterCards();
            
            List<Integer> Coin = new ArrayList<Integer>();
            for (Player p :
                    gg.getPlayers()) {
                Coin.add(gg.getCoins(p));
            }
            this.Coins = Coin;
            this.isExpert=true;
        }catch (ClassCastException e){
            System.out.println("Normal Game...");
        }
    }

    /**
     * Method that returns the number of students of the given pawn type present in the entrance of the board of a given player
     *
     * @param playerId the id of the player
     * @param diskType pawn type
     * @return  number of red students present in the entrance of the player's board
     *
     * */
    public int getNumInEntrance(int playerId,Disk diskType){
        int num=0;
        for(Student s : Boards.get(playerId).getEntrance()){
            if(s.getPawn().equals(diskType)){
                num++;
            }
        }
        return num;
    }

    /**
     * Method that returns the number of students of the given pawn type present in the dining room of the board of a given player
     *
     * @param playerId the id of the player
     * @param diskType pawn type
     * @return  number of red students present in the dining room of the player's board
     *
     * */
    public int getNumInDinningRoom(int playerId,Disk diskType){
        return Boards.get(playerId).getDiningRoom().get(diskType).size();
    }

    /**
     * Method that tells if a player has the professor of the given pawn type on his board
     *
     * @param playerId the id of the player
     * @param diskType pawn type
     * @return true if the player has the professor, false otherwise
     *
     * */
    public boolean hasProfessor(int playerId, Disk diskType){
        for(Professor p : Boards.get(playerId).getProfessors()){
            if(p.getPawn().equals(diskType)){
                return true;
            }
        }
        return false;
    }

    /**
     * Method that tells how many student of a certain pwan type are present on an island
     *
     * @param cloudId the index of the cloud
     * @param diskType the type of disk
     *
     * */
    public int getNumStudentsCloud(int cloudId, Disk diskType){
        int num=0;
        for(int i=0; i<Clouds.get(cloudId).getStudents().size();i++){
            if(Clouds.get(cloudId).getStudents().get(i).getPawn().equals(diskType)){
                num++;
            }
        }
        return num;
    }

    /**
     * Method that gives the index of the first occurrence of the given pawn-type present in the list of given location for the player's board
     *
     * @param diskType the type of disk searched
     * @param playerId id of the player
     *
     * @return index in the list of students corresponding for that diskType, if there is no correspondence the method returns -1
     *
     * */
    public int firstOccurrenceInEntrance(String diskType,int playerId){
        for(Student s: Boards.get(playerId).getEntrance()) {
            if(s.getPawn().toString().equalsIgnoreCase(diskType)){
               return Boards.get(playerId).getEntrance().indexOf(s);
            }
        }
        return -1;
    }

    public String getPlayerCharacter(String Nickname) {
        return playersCharacter.get(Nickname).toString();
    }

    public void setLog(String turn) {
        Log = turn;
    }

    public String getLog() {
        return Log;
    }

    public void setMyDeck(List<Card> myDeck) {
        this.myDeck = myDeck;
    }

    public void setBoards(List<Board> boards) {
        Boards = boards;
    }

    public void setIslands(List<Island> islands) {
        Islands = islands;
    }

    public void setClouds(List<Cloud> clouds) {
        Clouds = clouds;
    }

    public void setCharacterCard(List<it.polimi.ingsw.Model.CharacterCard> characterCard) {
        CharacterCard = characterCard;
    }

    public void setCoins(List<Integer> coins) {
        Coins = coins;
    }

    public void setPlayerNicknames(List<String> playerNicknames) {
        this.playerNicknames = playerNicknames;
    }

    public void setExpert(boolean expert) {
        isExpert = expert;
    }

    public List<Card> getMyDeck() {
        return myDeck;
    }

    public List<Board> getBoards() {
        return Boards;
    }

    public List<Island> getIslands() {
        return Islands;
    }

    public List<Cloud> getClouds() {
        return Clouds;
    }

    public List<it.polimi.ingsw.Model.CharacterCard> getCharacterCard() {
        return CharacterCard;
    }

    public List<Integer> getCoins() {
        return Coins;
    }

    public List<String> getPlayerNicknames() {
        return playerNicknames;
    }

    public List<Card> getPlayedCards() {
        return playedCards;
    }

    public void setPlayedCards(List<Card> playedCards) {
        this.playedCards = playedCards;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

}
