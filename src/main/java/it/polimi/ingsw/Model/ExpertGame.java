package it.polimi.ingsw.Model;
import it.polimi.ingsw.Connection.MessageType;
import it.polimi.ingsw.Exceptions.InvalidInputException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpertGame extends Game implements Serializable {
    protected List<CharacterCard> CharacterCard = new ArrayList<CharacterCard>();
    protected Map<Player, Integer> Coins = new HashMap<>();

    public ExpertGame(){}

    public ExpertGame(int np) {
        super(np);
        for (Player player : Players)
            Coins.put(player, 1);
        List<Integer> cardsAlreadyChoosen = new ArrayList<Integer>();
        while (CharacterCard.size() < 3){
            Integer card = (int) (Math.random() * 7);
            if (cardsAlreadyChoosen.contains(card))
                continue;
            cardsAlreadyChoosen.add(card);
            switch (card){
                case 0:
                    CharacterCard.add(new CharacterCard1(this));
                    break;
                case 1:
                    CharacterCard.add(new CharacterCard2(this));
                    break;
                case 2:
                    CharacterCard.add(new CharacterCard3(this));
                    break;
                case 3:
                    CharacterCard.add(new CharacterCard7(this));
                    break;
                case 4:
                    CharacterCard.add(new CharacterCard8(this));
                    break;
                case 5:
                    CharacterCard.add(new CharacterCard10(this));
                    break;
                case 6:
                    CharacterCard.add(new CharacterCard11(this));
                    break;
                case 7:
                    CharacterCard.add(new CharacterCard12(this));
                    break;
            }
        }
        for(CharacterCard c: CharacterCard){
            if(c.getClass().toString().equals("CharacterCard7")){
                CharacterCard7 card = (CharacterCard7)c;
                for (int i = 0; i < card.getStudentsOnCard().size(); i++)
                    Bag.add( ( card.getStudentsOnCard().remove(0)));
            }
            else if (c.getClass().toString().equals("CharacterCard11")){
                CharacterCard11 card = (CharacterCard11) c;
                for (int i = 0; i < card.getStudentsOnCard().size(); i++)
                    Bag.add( (card.getStudentsOnCard().remove(0)));
            }
        }
    }

    public ExpertGame(ExpertGame game) {
        this.lastTurn = game.lastTurn;
        this.CharacterCard = game.CharacterCard;
        this.Coins = game.Coins;
        this.NumPlayers = game.NumPlayers;
        this.NumOfStudentsOnCloud = game.NumOfStudentsOnCloud;
        this.NumOfStartTower = game.NumOfStartTower;
        this.firstPlayer = game.firstPlayer;
        this.Teams = game.Teams;
        this.Players = game.Players;
        this.actionPlayers = game.actionPlayers;
        this.Islands = game.Islands;
        this.Bag = game.Bag;
        this.Clouds = game.Clouds;
        this.redProfessor = game.redProfessor;
        this.yellowProfessor = game.yellowProfessor;
        this.pinkProfessor = game.pinkProfessor;
        this.blueProfessor = game.blueProfessor;
        this.greenProfessor = game.greenProfessor;
    }


    public ExpertGame playCharacterCard(SimpleCharacterCard characterCard, int playerId, String parameters) throws InvalidInputException {
        characterCard.Effect(playerId, parameters);
        System.out.println("EXPERT : " + parameters);
        notifyObservers(MessageType.EXPERT_PARAMETERS);
        return this;
    }

    public ExpertGame playCharacterCard(SpecialCharacterCard characterCard, int playerId, String parameters) throws InvalidInputException {
        ExpertGame deco = characterCard.Effect(playerId, parameters);
        System.out.println("EXPERT : " + parameters);
        notifyObservers(MessageType.EXPERT_PARAMETERS);
        return deco;
    }

    public List<CharacterCard> getCharacterCards() { return CharacterCard; }

    @Override
    public void moveStudentOnDiningRoom(Player player, int StudentID) {
        Disk color = player.getBoard().getEntrance().get(StudentID).getPawn();
        super.moveStudentOnDiningRoom(player, StudentID);
        if (player.getBoard().getDiningRoom().get(color).size()%3==0)
            Coins.replace(player, Coins.get(player)+1);
        notifyObservers(MessageType.BOARDS);
    }

    public int getCoins (Player player){ return Coins.get(player); }

    public Map<Player, Integer> getCoins(){ return Coins; }
    public void removeCoins (Player player, int Cost){ Coins.replace(player, Coins.get(player) - Cost); }

    public void giveCoin(Player player){
        Coins.put(player,Coins.get(player)+1);
        notifyObservers(MessageType.EXPERT_PARAMETERS);
    }

}
