package it.polimi.ingsw.Model;
import it.polimi.ingsw.Connection.MessageType;
import it.polimi.ingsw.Observer.Observable;

import java.util.*;

public class Game extends Observable {
    protected boolean lastTurn = false;
    protected int NumPlayers;
    protected int NumOfStudentsOnCloud;
    protected int NumOfStartTower;
    /*this is the first player for playing the cards only (not for the action playing part of the game) */
    protected Player firstPlayer;
    protected List<Team> Teams;
    /* clockwise order, based on player entrance, used for card playing */
    protected List<Player> Players = new ArrayList<Player>();
    /*turn order based on ascendant order of Priority Number */
    protected List<Player> actionPlayers = new ArrayList<>();
    protected List<Island> Islands = new ArrayList<Island>();
    protected List<Student> Bag = new ArrayList<Student>();
    protected List<Cloud> Clouds = new ArrayList<Cloud>();
    protected Professor redProfessor = new Professor(Disk.DRAGON);
    protected Professor yellowProfessor = new Professor(Disk.GNOME);
    protected Professor pinkProfessor = new Professor(Disk.FAIRY);
    protected Professor blueProfessor = new Professor(Disk.UNICORN);
    protected Professor greenProfessor = new Professor(Disk.FROG);


    /**
     * This method sets the Tower Color to the board of a player
     *
     * @param playerId the player Id
     * @param color the color of the tower
     */
    public void setTowerColor(int playerId,TowerColor color){
        getPlayers().get(playerId).getBoard().setTowerColor(color);
        int i=0;
            for (Player p: Players){
                if(p.getBoard().getTowerColor()==null ){
                    i++;
                    notifyObserver(MessageType.SHOW_TOWERS, Players.indexOf(p));
                }
            }
        if(i==0){
            for(Player p : Players){
                notifyObserver(MessageType.SHOW_CHARACTERS,Players.indexOf(p));
            }
        }
    }


    public void setTowerColorTeams(int playerId,TowerColor color,boolean first){
        if(!first){
            getPlayers().get(playerId).getBoard().removeTowers();
        }
        setTowerColor(playerId,color);
    }

    /**
     * This method gives the team-mate of a player for a 4 pòayers game
     *
     * @param playerId the id of the player
     *
     * @return the player mate
     */
    public Player getPlayerMate(int playerId)
           {Player mate= null;
               for(Team t:Teams)
               {if (t.getMembers().contains(Players.get(playerId)))
                   {int teamIndex = Teams.indexOf(t);
                    for(Player p : Teams.get(teamIndex).getMembers())
                          {if(p!=Players.get(playerId))
                              mate = p;}
                   }
               }
               return mate;
           }

  /**
   * This method gives a professor to a player, even if this professor is kept by another player
   *
   * @param playerId the id of the player
   * @param color the disk type
   *
   */
   public void addPlayerProfessor(int playerId, Disk color) {
       if(color.equals(Disk.FROG)) {
           for(int i=0;i< NumPlayers;i++) {
               Players.get(i).getBoard().getProfessors().remove(greenProfessor);
           }
           Players.get(playerId).getBoard().addProfessor(greenProfessor);
       }
       if(color.equals(Disk.FAIRY)) {
           for(int i=0;i< NumPlayers;i++) {
               Players.get(i).getBoard().getProfessors().remove(pinkProfessor);
           }
           Players.get(playerId).getBoard().addProfessor(pinkProfessor);
       }
       if(color.equals(Disk.GNOME)) {
           for(int i=0;i< NumPlayers;i++)
           {
               Players.get(i).getBoard().getProfessors().remove(yellowProfessor);
           }
           Players.get(playerId).getBoard().addProfessor(yellowProfessor);
       }
       if(color.equals(Disk.UNICORN)) {
           for(int i=0;i< NumPlayers;i++) {
               Players.get(i).getBoard().getProfessors().remove(blueProfessor);
           }
           Players.get(playerId).getBoard().addProfessor(blueProfessor);
       }
       if(color.equals(Disk.DRAGON) ) {
           for(int i=0;i< NumPlayers;i++) {
               Players.get(i).getBoard().getProfessors().remove(redProfessor);
           }
           Players.get(playerId).getBoard().addProfessor(redProfessor);
       }
       notifyObservers(MessageType.BOARDS);
   }


    public List<Player> getActionPlayers() {
        return actionPlayers;
    }

    public Game(){}

    public void setFirstPlayer(Player player){ firstPlayer = player; }

    public Player getFirstPlayer(){ return firstPlayer; }

    public List<Team> getTeams(){ return Teams; }

    public int getNumOfStudentsOnCloud(){ return NumOfStudentsOnCloud; }

    public int getNumPlayers(){ return NumPlayers; }

    public int getNumOfStartTower(){ return NumOfStartTower; }

    public List<Island> getIslands(){ return Islands; }

    public List<Player> getPlayers(){ return Players; }

    public List<Student> getBag(){ return Bag; }

    public List<Cloud> getClouds(){ return Clouds; }

    public void placeStudentFromBagToCloud(Cloud cloud){
        for (int i = 0; i < NumOfStudentsOnCloud; i++) {
            cloud.getStudents().add(Bag.remove(0));
        }
        notifyObservers(MessageType.CLOUDS);
        notifyObservers(MessageType.BOARDS);
    }

    public void startGame(){notifyObservers(MessageType.START_GAME);}

    public void gameTurnChange(){notifyObservers(MessageType.GAME_TURN);}

    public boolean isCardPlayable(Player player, Card c){
        for (Player p :
                Players) {
            if (p.getPlayedCards().size() > player.getPlayedCards().size()){
                if (p.getPlayedCards().get(p.getPlayedCards().size()-1).getPriorityNumber() == c.getPriorityNumber()){
                    for (Card card :
                            player.getDeck()) {
                        if (!card.equals(c)){
                            for (Player pp :
                                    Players) {
                                if (pp.getPlayedCards().size() > player.getPlayedCards().size())
                                    if (card.getPriorityNumber() != pp.getPlayedCards().get(pp.getPlayedCards().size()-1).getPriorityNumber())
                                        return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    public void playCard(Player player, int cardPosition){
        player.getPlayedCards().add(player.getDeck().remove(cardPosition));
        notifyObservers(MessageType.DECK);
    }//C


    public void setFirstPlayer(){
        Player exFirst = firstPlayer;
        int epi = Players.indexOf(exFirst);
        for (int i = 0; i < NumPlayers; i++) {
            Player cp = Players.get((i + epi)%NumPlayers);
            int cd = cp.getPlayedCards().get(cp.getPlayedCards().size()-1).getPriorityNumber();
            if (cd < firstPlayer.getPlayedCards().get(firstPlayer.getPlayedCards().size()-1).getPriorityNumber())
                firstPlayer = cp;
        }
    }

    public void moveStudentOnIsland(Player player, Island island, int studentPosition){ //C
        island.getStudents().add(player.getBoard().getEntrance().remove(studentPosition));
        notifyObservers(MessageType.ISLANDS);
        notifyObservers(MessageType.BOARDS);
    }

    public void moveStudentOnDiningRoom(Player player, int StudentID){
        Student s = player.getBoard().getEntrance().get(StudentID);
        player.getBoard().getDiningRoom().get(s.getPawn()).add(player.getBoard().getEntrance().remove(StudentID));
        notifyObservers(MessageType.BOARDS);
    } //C

    public void moveMotherNature(int moves){
        for (int i = 0; i < Islands.size(); i++) {
            if (Islands.get(i).hasMotherNature()){
                Islands.get(i).setMotherNature(false);
                Islands.get((i+moves)%Islands.size()).setMotherNature(true);
                break;
            }
        }
        notifyObservers(MessageType.ISLANDS);
    } //C

    //doesn't work with teams and group of islands   ???
    public Map<Player, Integer> currentInfluence(Island island){
        Map<Player, Integer> influence = new HashMap<>();
        int currentInfluence;
        for (Player player :
                Players) {
            currentInfluence = 0;
            for (Professor professor :
                    player.getBoard().getProfessors()) {
                currentInfluence = currentInfluence + island.getNumOfStudentsOfType(professor.getPawn());
            }
            influence.put(player, currentInfluence);
        }
        return influence;
    }

    public Map<Team, Integer> currentTeamInfluence(Island island){
        Map<Team, Integer> influence = new HashMap<>();
        int teamInfluence;
        for (Team team :
                Teams) {
            teamInfluence = 0;
            for (Player player :
                    team.getMembers()) {
                for (Professor professor :
                        player.getBoard().getProfessors()) {
                    teamInfluence = teamInfluence + island.getNumOfStudentsOfType(professor.getPawn());
                }
            }
            influence.put(team, teamInfluence);
        }
        return influence;
    }


    public boolean isTowerPlaceable(Player player, Island island, Map<Player, Integer> currentInfluence){  //return -1 if tower is not placeable, otherwise return the id of the player who can place it
        Integer max = Collections.max(currentInfluence.values());
        int playersWithMax = 0;
        for (Player p :
                Players) {
            if (currentInfluence.get(p).equals(max))
                playersWithMax++;
        }
        return (island.getTowerColor()==null && playersWithMax==1 && currentInfluence.get(player).equals(max));
    }

    public boolean isTowerPlaceable(Team team, Island island, Map<Team, Integer> currentInfluence){
        Integer max = Collections.max(currentInfluence.values());
        int teamsWithMax = 0;
        for (Team t :
                Teams) {
            if (currentInfluence.get(t).equals(max))
                teamsWithMax++;
        }
        return (island.getTowerColor()==null && teamsWithMax == 1 && currentInfluence.get(team).equals(max));
    }


    public Team getTeamOfPlayer(Player player){
        for (Team team :
                Teams) {
            if (team.getMembers().contains(player))
                return team;
        }
        return null;
    }

    public void placeTowerOnIsland(Player player, Island island){
        Board board = player.getBoard();
        board.setNumTowers(board.getNumTowers()-1);
        if (island.getNumTowers() == 0)
            island.setNumTowers(1);
        island.setTowerColor(board.getTowerColor());
        notifyObservers(MessageType.ISLANDS);
        notifyObservers(MessageType.BOARDS);
    } //C


    public Player getIslandOwner(Island island){
        for (Player player:
             Players) {
            if (player.getBoard().getTowerColor().equals(island.getTowerColor()))
                return player;
        }
        return null;
    }

    public boolean isTowerReplaceable(Player player, Island island){
        Map<Player, Integer> currentInfluence = currentInfluence(island);
        Player currentOwner = getIslandOwner(island);
        Integer max;
        int playersWithMax = 0;

        currentInfluence.replace(currentOwner, currentInfluence.get(currentOwner) + island.getNumTowers());
        max = Collections.max(currentInfluence.values());

        for (Player p :
                Players) {
            if (currentInfluence.get(p).equals(max))
                playersWithMax++;
        }
        return (!island.getTowerColor().equals(player.getBoard().getTowerColor()) && playersWithMax==1 && currentInfluence.get(player).equals(max));
    }


    public boolean isTowerReplaceable(Team team, Island island){
        Map<Team, Integer> currentInfluence = currentTeamInfluence(island);
        Player currentOwner = getIslandOwner(island);
        Integer max;
        int playersWithMax = 0;

        currentInfluence.replace(getTeamOfPlayer(currentOwner), currentInfluence.getOrDefault(currentOwner,0) + island.getNumTowers()); //getOrdefault
        max = Collections.max(currentInfluence.values());

        for (Team p :
                Teams) {
            if (currentInfluence.get(p).equals(max))
                playersWithMax++;
        }
        return (!island.getTowerColor().equals(team.getPLayerWithTower().getBoard().getTowerColor()) && playersWithMax==1 && currentInfluence.get(team.getPLayerWithTower()).equals(max));
    }

    public void replaceTowerOnIsland(Player player, Island island){ //C
        //if (isTowerReplaceable(player, island))
        Player exOwner = getIslandOwner(island);
        exOwner.getBoard().setNumTowers(exOwner.getBoard().getNumTowers() + island.getNumTowers());
        player.getBoard().setNumTowers(player.getBoard().getNumTowers() - island.getNumTowers());
        island.setTowerColor(player.getBoard().getTowerColor());
        notifyObservers(MessageType.ISLANDS);
        notifyObservers(MessageType.BOARDS);
    }

    //return 0 if not joinable otherwise 1 is joinable clockwise or -1 anticlockwise (remember to double lunch the function if joinable in both sides)
    public int isIslandJoinable(Island island){
       int i = Islands.indexOf(island);
       int j = i - 1;
       if (j<0)
           j= Islands.size()-1;
        if (Islands.get(j).getTowerColor() != null && Islands.get(j).getTowerColor().equals(island.getTowerColor())){
            return -1;}
        else if (Islands.get((i + 1)%Islands.size()).getTowerColor() != null && Islands.get((i + 1)%Islands.size()).getTowerColor().equals(island.getTowerColor())){
            return 1;}
        return 0;
    }

    public void joinIslands(Island i1, Island i2){
        Island island = new Island(i1, i2);
        Islands.remove(i2);
        Islands.set(Islands.indexOf(i1), island);
        notifyObservers(MessageType.ISLANDS);
    }

    public void moveStudentsFromCloud(Player player, Cloud cloud){ //C
        while(cloud.getStudents().size()>0) {
            player.getBoard().getEntrance().add(cloud.getStudents().remove(0));
        }
        notifyObservers(MessageType.CLOUDS);
        notifyObservers(MessageType.BOARDS);
    }

    public boolean isLastTurn(){ return lastTurn; }

    public void setLastTurn(boolean lastTurn) {
        this.lastTurn = lastTurn;
    }

    public Player declareWinner(){
        boolean winCondition = false;
        Player minTower;
        Player maxProfessor;
        List<Player> playersWithMinTower = new ArrayList<>();

        if (Islands.size()==3 || Bag.size()==0)
            winCondition = true;
        if (Bag.size()==0)
            lastTurn = true;

        for (Player player :
                Players) {
            if (player.getBoard().getNumTowers() == 0)
                return player;
            if (player.getDeck().size()==0){
                lastTurn = true;
                winCondition = true;
            }
        }

        if (winCondition){
            minTower = Players.get(0);
            for (int i = 1; i < NumPlayers; i++) {
                if (Players.get(i).getBoard().getNumTowers()<minTower.getBoard().getNumTowers())
                    minTower = Players.get(i);
            }
            for (Player player :
                    Players) {
                if (player.getBoard().getNumTowers()==minTower.getBoard().getNumTowers())
                    playersWithMinTower.add(player);
            }
            if (playersWithMinTower.size()==1)
                return minTower;
            else {
                maxProfessor = playersWithMinTower.get(0);
                for (int i = 1; i < playersWithMinTower.size(); i++) {
                    if (playersWithMinTower.get(i).getBoard().getProfessors().size()>maxProfessor.getBoard().getProfessors().size())
                        maxProfessor = playersWithMinTower.get(i);
                }
                if(maxProfessor!=null){
                    notifyObserver(MessageType.WIN,Players.indexOf(maxProfessor));
                    if(getPlayerMate(Players.indexOf(maxProfessor))!=null){
                        notifyObserver(MessageType.WIN,Players.indexOf(getPlayerMate(Players.indexOf(maxProfessor))));
                    }
                    for(int i= 0; i<NumPlayers && i!=Players.indexOf(maxProfessor) && i!=Players.indexOf(getPlayerMate(Players.indexOf(maxProfessor))); i++){
                        notifyObserver(MessageType.LOSS,i);
                    }
                }
                return maxProfessor;
                //Doesnt manage the parity professors case
            }
        }
        return null;
    }

    public Game(int np) {
        this.NumPlayers = np;
        this.NumOfStudentsOnCloud = NumPlayers + 1 - 2*(NumPlayers/4);
        this.NumOfStartTower = 8 - 2*(NumPlayers/3) + 2*(NumPlayers/4);

        //Creation of Players, Clouds and Decks
        for (int i = 0; i < np; i++) {
            Players.add(new Player("defaultName"));
            Players.get(i).getBoard().setNumTowers(NumOfStartTower);
            Clouds.add(new Cloud());//remember to set nickname
        }
        if (np==4) InitializeTeams(); //to implement method

        //Creation Islands
        for (int i = 0; i < 12; i++) {
            this.Islands.add(new Island());
        }

        for (Disk c:
                Disk.values()) {
            for (int i = 0; i < 2; i++) {
                this.Bag.add(new Student(c));
            }
        }

        //Setting Island
        Collections.shuffle(Bag);
        for (int i = 1; i < 12 ; i++) {
            if(i!=5)
                this.Islands.get(i).addStudent(this.Bag.remove(0));
        }

        //Setting Bag
        for (Disk c:
                Disk.values()) {
            for (int i = 0; i < 24; i++) {
                this.Bag.add(new Student(c));
            }
        }
        Collections.shuffle(Bag);
        InitializeEntrance(np);
    }

    public void setDeckCharacter(int playerID, Character character) {
        Players.get(playerID).initializeDeck(character);
        for(Player p : Players){
            if(p.getDeck().size()<1){
                notifyObserver(MessageType.SHOW_CHARACTERS,Players.indexOf(p));
            }
        }
    }


    public void InitializeTeams(){
       Teams = new ArrayList<>();
       Team Team1 = new Team();
       Team Team2 = new Team();
       Teams.add(Team1);
       Teams.add(Team2);
    }


    public void InitializeEntrance(int np){
        if (np==2 || np==4){
            for (Player p :
                    this.Players) {
                for (int i = 0; i < 7; i++) {
                    p.getBoard().getEntrance().add(Bag.remove(0));
                }
            }
        }
        else if (np==3){
            for (Player p :
                    this.Players) {
                for (int i = 0; i < 9; i++) {
                    p.getBoard().getEntrance().add(Bag.remove(0));
                }
            }
        }
    }

}
