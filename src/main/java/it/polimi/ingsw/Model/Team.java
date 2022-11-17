package it.polimi.ingsw.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Team is an abstraction of a list of Players,
 * Each Team has a specific tower color, and a list of players composing it.
 * When a game with Teams is created, only a player has effectively a Board since Team's members must share the board.
 */
public class Team {
    private List<Player> Members = new ArrayList<>();
    private TowerColor towerColor;

    public Team(Player player1, Player player2){
        Members.add(player1);
        Members.add(player2);
    }

   public Team(){}

    public void addMember(Player player){
        Members.add(player);
    }

    public List<Player> getMembers() {
        return this.Members;
    }

    public void setTowerColor(TowerColor towerColor) {
        this.towerColor = towerColor;
    }

    public TowerColor getTowerColor() {
        return towerColor;
    }

    /**
     *
     * @return Player that effectively has the board.
     */
    public Player getPLayerWithTower(){
        for (Player player : this.getMembers()) {
            if (player.getBoard().getNumTowers()>= 0)
                return player;
        }
        return null;
    }
}
