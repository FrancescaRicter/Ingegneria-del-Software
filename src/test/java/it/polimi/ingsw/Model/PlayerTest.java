package it.polimi.ingsw.Model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void setNicknameTest() {
        Game game = new Game(2);
        Player player= new Player("Player1");
        player.setNickname("Ben");
        assertEquals("Ben",player.getNickname());
    }

     @Test
     void getPlayedCardsTest(){
         Game game= new Game(2);
         Player player = game.getPlayers().get(0);
         assertEquals(game.getPlayers().get(0).getPlayedCards().size(),0);
         game.getPlayers().get(0).initializeDeck(Character.KING);
         game.getPlayers().get(1).initializeDeck(Character.NINJA);
         game.playCard(player,0);
         assertEquals(1,game.getPlayers().get(0).getPlayedCards().size());
         game.playCard(game.getPlayers().get(1),0);
         assertEquals(game.getPlayers().get(1).getPlayedCards().size(),1);
     }
}