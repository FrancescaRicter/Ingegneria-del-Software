package it.polimi.ingsw.Model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class TeamTest {

    @Test
    public void SameTeamTest(){
        Player player1,player2;
        player1 = new Player("Player1");
        player2 = new Player("Player2");
        Team team1 = new Team(player1 ,player2);
        Team team2 = new Team(player1, player2);
        assertNotEquals(team1, team2);

    }

    @Test
    void getMembers() {
        assertTrue( true );
    }

    @Test
    void getPLayerWithTowerTest() {
        Game game = new Game(4);
        Team team1,team2;
        Player player1,player2,player3,player4;
        game.InitializeTeams();
        team1= game.getTeams().get(0);
        team2 = game.getTeams().get(1);
        assertNull(team1.getPLayerWithTower());
        player1= game.getPlayers().get(0);player1.getBoard().setTowerColor(TowerColor.BLACK);
        player2= game.getPlayers().get(1);player2.getBoard().setTowerColor(TowerColor.BLACK);
        player3= game.getPlayers().get(2);player3.getBoard().setTowerColor(TowerColor.WHITE);
        player4= game.getPlayers().get(3);player4.getBoard().setTowerColor(TowerColor.WHITE);
        team1.getMembers().add(player1);
        team2.getMembers().add(player2);

        assertEquals(team1.getMembers().get(0),team1.getPLayerWithTower());
        assertEquals(team2.getMembers().get(0),team2.getPLayerWithTower());}


}
