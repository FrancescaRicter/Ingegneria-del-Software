package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Connection.Client.ClientMessage.*;
import it.polimi.ingsw.Exceptions.InvalidGamePhaseException;
import it.polimi.ingsw.Exceptions.InvalidInputException;
import it.polimi.ingsw.Exceptions.NotPossibleActionException;
import it.polimi.ingsw.Model.*;
import it.polimi.ingsw.Model.Character;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.Test;
import org.junit.jupiter.api.RepeatedTest;

import java.io.IOException;


/**
 * Tests performed on the Controller class
 *
 */
public class ControllerTest {

    /**
     * Test of a three players game
     *
     * */
    @Test
    @RepeatedTest(100)
    public void threePlayers() throws InvalidInputException, InvalidGamePhaseException, NotPossibleActionException {
        Exception exception;
        Controller testController = new Controller(false,3);
        assertEquals(3,testController.getModel().getNumPlayers());
        assertEquals(PlayerPhase.CHOOSE_NICKNAME,testController.PlayerPhases.get(0));
        assertEquals(PlayerPhase.CHOOSE_NICKNAME,testController.PlayerPhases.get(1));
        assertEquals(PlayerPhase.CHOOSE_NICKNAME,testController.PlayerPhases.get(2));
        int i = testController.setNewPlayer("Francesca");
        assertEquals(0,i);
        i = testController.setNewPlayer("Eris");
        assertEquals(1,i);
        assertEquals(2,testController.getPlayersEntered());
        exception = assertThrows(NotPossibleActionException.class,()-> testController.chooseTowerColor(0,TowerColor.GREY));
        assertEquals("The game hasn't reached the full number of players. The tower color can't be chosen yet!", exception.getMessage());
        i = testController.setNewPlayer("Leonardo");
        exception = assertThrows(NotPossibleActionException.class,()-> testController.setNewPlayer("Francesca2"));
        assertEquals("The game has already reached the total number of players!", exception.getMessage());
        assertEquals(2,i);
        assertEquals("Francesca",testController.getModel().getPlayers().get(0).getNickname());
        assertEquals("Eris",testController.getModel().getPlayers().get(1).getNickname());
        assertEquals("Leonardo",testController.getModel().getPlayers().get(2).getNickname());
        assertEquals(PlayerPhase.CHOOSE_TOWER_COLOR,testController.PlayerPhases.get(0));
        assertEquals(PlayerPhase.CHOOSE_TOWER_COLOR,testController.PlayerPhases.get(1));
        assertEquals(PlayerPhase.CHOOSE_TOWER_COLOR,testController.PlayerPhases.get(2));
        assertEquals(0,testController.getCurrentPlayerIndex());
        testController.chooseTowerColor(0, TowerColor.BLACK);
        exception = assertThrows(NotPossibleActionException.class,()-> testController.chooseTowerColor(0,TowerColor.GREY));
        assertEquals("You hava already chosen your tower color!", exception.getMessage());
        testController.chooseTowerColor(1,TowerColor.WHITE);
        exception = assertThrows(InvalidInputException.class,()-> testController.chooseTowerColor(10,TowerColor.BLACK));
        assertEquals("Invalid playerId!", exception.getMessage());
        exception = assertThrows(NotPossibleActionException.class,()-> testController.chooseTowerColor(2,TowerColor.BLACK));
        assertEquals("This Tower Color is already taken!", exception.getMessage());
        testController.chooseTowerColor(2,TowerColor.GREY);
        exception = assertThrows(NotPossibleActionException.class,()-> testController.chooseTowerColor(0,TowerColor.GREY));
        assertEquals("You hava already chosen your tower color!", exception.getMessage());
        assertEquals(TowerColor.BLACK,testController.getModel().getPlayers().get(0).getBoard().getTowerColor());
        assertEquals(TowerColor.WHITE,testController.getModel().getPlayers().get(1).getBoard().getTowerColor());
        assertEquals(TowerColor.GREY,testController.getModel().getPlayers().get(2).getBoard().getTowerColor());
        testController.chooseCharacter(0, Character.WITCH);
        exception = assertThrows(InvalidGamePhaseException.class,()-> testController.chooseCharacter(0,Character.KING));
        assertEquals("You can't perform this move at this stage of game!", exception.getMessage());
        testController.chooseCharacter(1,Character.NINJA);
        testController.chooseCharacter(2, Character.KING);
        assertEquals(Character.WITCH,testController.getModel().getPlayers().get(0).getDeck().get(0).getCardCharacter());
        assertEquals(Character.NINJA,testController.getModel().getPlayers().get(1).getDeck().get(0).getCardCharacter());
        assertEquals(Character.KING,testController.getModel().getPlayers().get(2).getDeck().get(0).getCardCharacter());
        testController.startGame();
        assertTrue(testController.isGameStarted());
        assertEquals(1,testController.getTurn());
        assertNull(testController.winner());
        assertEquals(1,testController.getAvailableCharacters().size());
        assertEquals(0,testController.getAvailableTowers().size());
        int numStudents=0;
        for(Island isl : testController.getModel().getIslands()) {
            int red = (int) isl.getStudents().stream().filter(x -> x.getPawn().equals(Disk.DRAGON)).count();
            int green = (int) isl.getStudents().stream().filter(x -> x.getPawn().equals(Disk.FROG)).count();
            int yellow = (int) isl.getStudents().stream().filter(x -> x.getPawn().equals(Disk.GNOME)).count();
            int blue = (int) isl.getStudents().stream().filter(x -> x.getPawn().equals(Disk.UNICORN)).count();
            int pink = (int) isl.getStudents().stream().filter(x -> x.getPawn().equals(Disk.FAIRY)).count();
            numStudents=numStudents +red+green+pink+yellow+blue;
        }
        assertEquals(10,numStudents);
        assertEquals(6,testController.getModel().getNumOfStartTower());
        assertEquals(3,testController.getModel().getNumPlayers());
        assertEquals(3,testController.PlayerPhases.size());
        assertEquals(Character.KING,testController.getModel().getPlayers().get(2).getDeck().get(0).getCardCharacter());
       testController.playCard(0,2);
        exception = assertThrows(NotPossibleActionException.class,()-> testController.playCard(2,4));
        assertEquals("It is not your turn to play your card yet!", exception.getMessage());
        assertEquals(2, testController.getModel().getPlayers().get(0).getPlayedCards().get(0).getMotherNatureMovement());
         testController.playCard(1,4);
        assertEquals(PlayerPhase.CARD_PLAYED,testController.getPlayerPhase(0));
        assertEquals(3,testController.getModel().getNumPlayers());
        assertEquals(TowerColor.WHITE,testController.getModel().getPlayers().get(1).getBoard().getTowerColor());
        assertEquals(3,testController.PlayerPhases.size());
        testController.playCard(2,5);
        testController.moveStudentOnIsland(0,5,1);
        assertEquals(1,testController.getModel().getIslands().get(5).getStudents().size());
        testController.moveStudentOnIsland(0,4,3);
        assertEquals(2,testController.getModel().getIslands().get(4).getStudents().size());
        testController.moveStudentOnIsland(0,5,2);
        testController.moveStudentOnIsland(0,4,3);
        testController.moveMotherNature(0,1);
        assertEquals(4,testController.getModel().getClouds().get(0).getStudents().size());
        testController.selectCloud(0,2);
        testController.moveStudentOnIsland(1,5,4);
        testController.moveStudentOnIsland(1,4,3);
        testController.moveStudentOnIsland(1,5,2);
        testController.moveStudentOnIsland(1,4,1);
        testController.moveMotherNature(1,1);
        testController.selectCloud(1,1);
        testController.moveStudentOnIsland(2,5,2);
        testController.moveStudentOnIsland(2,4,3);
        testController.moveStudentsOnDiningRoom(2,4);
        testController.moveStudentsOnDiningRoom(2,3);
        testController.moveMotherNature(2,1);
        testController.selectCloud(2,0);
        testController.playCard(0,0);
        assertEquals(4,testController.getModel().getClouds().get(0).getStudents().size());
        assertEquals(4,testController.getModel().getClouds().get(1).getStudents().size());
        assertEquals(4,testController.getModel().getClouds().get(2).getStudents().size());
        testController.playCard(1,1);
        testController.playCard(2,2);
        assertEquals(testController.getModel().getPlayers().get(0),testController.getModel().getActionPlayers().get(0));
        assertEquals(testController.getModel().getPlayers().get(1),testController.getModel().getActionPlayers().get(1));
        assertEquals(testController.getModel().getPlayers().get(2),testController.getModel().getActionPlayers().get(2));
        testController.moveStudentOnIsland(0,2,3);
        testController.moveStudentOnIsland(0,2,3);
        testController.moveStudentOnIsland(0,3,4);
        testController.moveStudentsOnDiningRoom(0,2);
        assertEquals(PlayerPhase.MOTHER_NATURE_MOVE,testController.getPlayerPhase(0));
        testController.moveMotherNature(0,1);
        testController.selectCloud(0,1);
        testController.moveStudentsOnDiningRoom(1,1);
        testController.moveStudentsOnDiningRoom(1,4);
        testController.moveStudentOnIsland(1,3,1);
        testController.moveStudentOnIsland(1,2,3);
        testController.moveMotherNature(1,1);
        testController.selectCloud(1,2);
        testController.moveStudentsOnDiningRoom(2,1);
        testController.moveStudentsOnDiningRoom(2,4);
        testController.moveStudentOnIsland(2,3,1);
        testController.moveStudentOnIsland(2,2,3);
        testController.moveMotherNature(2,1);
        testController.selectCloud(2,0);
        testController.playCard(0,5);
        testController.playCard(1,0);
        testController.playCard(2,1);
        assertEquals(testController.getModel().getPlayers().get(1),testController.getModel().getActionPlayers().get(0));
        assertEquals(testController.getModel().getPlayers().get(2),testController.getModel().getActionPlayers().get(1));
        assertEquals(testController.getModel().getPlayers().get(0),testController.getModel().getActionPlayers().get(2));
        testController.removePlayer("Francesca");
        assertEquals(testController.getModel().getPlayers().size(),2);
   }

    /**
     * Test of a two players game
     *
     * */
   @RepeatedTest(100)
   @Test
   public void twoPlayersTest() throws NotPossibleActionException, InvalidGamePhaseException, InvalidInputException {
        Controller controller= new Controller(false,2);
        controller.setNewPlayer("Fra");
        controller.setNewPlayer("Eris");
        controller.chooseTowerColor(0, TowerColor.BLACK);
        controller.chooseTowerColor(1,TowerColor.WHITE);
        controller.chooseCharacter(0, Character.WITCH);
        controller.chooseCharacter(1,Character.NINJA);
        controller.startGame();
        controller.playCard(0,1);
        controller.playCard(1,2);
        controller.moveStudentOnIsland(0,1,0);
        Disk pawnMoved = controller.getModel().getPlayers().get(0).getBoard().getEntrance().get(2).getPawn();
        controller.moveStudentsOnDiningRoom(0,2);
        Disk pawn = null;
        for(Professor p : controller.getModel().getPlayers().get(0).getBoard().getProfessors()){
           pawn = p.getPawn();
        }
        assertEquals(pawnMoved,pawn);
        controller.moveStudentsOnDiningRoom(0,0);
        assertEquals(2,controller.getModel().getIslands().get(1).getStudents().size());
        controller.moveMotherNature(0,1);
        controller.selectCloud(0,1);
        assertEquals(7,controller.getModel().getPlayers().get(0).getBoard().getEntrance().size());
   }

    /**
     * Test of a game with Teams
     *
     * */
   @RepeatedTest(100)
   @Test
   public void teamsTest() throws NotPossibleActionException, InvalidGamePhaseException, InvalidInputException {
       Controller controller= new Controller(false,4);
       controller.setNewPlayer("Francesca");
       controller.setNewPlayer("MoonLight");
       controller.setNewPlayer("Leonardo");
       controller.setNewPlayer("Eris");
       controller.chooseTowerColor(0, TowerColor.WHITE);
       controller.chooseTowerColor(1,TowerColor.WHITE);
       Exception exception = assertThrows(NotPossibleActionException.class,()-> controller.chooseTowerColor(2,TowerColor.WHITE));
       assertEquals("Team has reached the maximum number of players!", exception.getMessage());
       controller.chooseTowerColor(2,TowerColor.BLACK);
       controller.chooseTowerColor(3,TowerColor.BLACK);
       assertEquals("Leonardo",controller.getModel().getTeams().get(1).getMembers().get(0).getNickname());
       assertEquals("Francesca",controller.getModel().getTeams().get(0).getMembers().get(0).getNickname());
       controller.chooseCharacter(0, Character.WITCH);
       controller.chooseCharacter(1,Character.NINJA);
       controller.chooseCharacter(2, Character.KING);
       controller.chooseCharacter(3,Character.WIZARD);
       controller.startGame();
       controller.playCard(0,1);
       controller.playCard(1,4);
       controller.playCard(2,5);
       controller.playCard(3,7);
       controller.moveStudentsOnDiningRoom(0,1);
       controller.moveStudentOnIsland(0,1,2);
       controller.moveStudentsOnDiningRoom(0,1);
       controller.moveMotherNature(0,1);


   }

    /**
     * Test of a change throughout the turns of the order of the players
     *
     * */
    @Test
    @RepeatedTest(100)
    public void variationInTurnOrder() throws NotPossibleActionException, InvalidGamePhaseException, InvalidInputException {
        Controller testC = new Controller(true,3);
        testC.setNewPlayer("TestName1");
        testC.setNewPlayer("TestName2");
        testC.setNewPlayer("TestName3");
        testC.chooseTowerColor(0,TowerColor.BLACK);
        testC.chooseTowerColor(1,TowerColor.WHITE);
        testC.chooseTowerColor(2,TowerColor.GREY);
        testC.chooseCharacter(2,Character.WITCH);
        testC.chooseCharacter(0,Character.NINJA);
        testC.chooseCharacter(1,Character.WIZARD);
        testC.startGame();
        Exception exception = assertThrows(NotPossibleActionException.class,()-> testC.playCharacterCard(0,1,""));
        assertEquals("You can't play a Character Card now, wait for your action turn!", exception.getMessage());
        exception = assertThrows(InvalidInputException.class,()-> testC.playCharacterCard(1,1,""));
        assertEquals("It is not your turn, you can't play a Character Card!", exception.getMessage());
        testC.playCard(0,7);
        exception = assertThrows(NotPossibleActionException.class,()-> testC.playCard(1,7));
        assertEquals("This card has already been played by another player!", exception.getMessage());
        testC.playCard(1,5);
        testC.playCard(2,1);
        assertEquals(testC.getModel().getActionPlayers().get(0),testC.getModel().getPlayers().get(2));
        assertEquals(testC.getModel().getActionPlayers().get(1),testC.getModel().getPlayers().get(1));
        assertEquals(testC.getModel().getActionPlayers().get(2),testC.getModel().getPlayers().get(0));
        testC.moveStudentsOnDiningRoom(2,0);
        testC.moveStudentsOnDiningRoom(2,0);
        testC.moveStudentsOnDiningRoom(2,0);
        testC.moveStudentsOnDiningRoom(2,0);
        testC.moveMotherNature(2,1);
        testC.selectCloud(2,1);
        testC.moveStudentsOnDiningRoom(1,0);
        testC.moveStudentsOnDiningRoom(1,0);
        testC.moveStudentsOnDiningRoom(1,0);
        testC.moveStudentsOnDiningRoom(1,0);
        testC.moveMotherNature(1,1);
        testC.selectCloud(1,0);
        testC.moveStudentsOnDiningRoom(0,0);
        testC.moveStudentsOnDiningRoom(0,0);
        testC.moveStudentsOnDiningRoom(0,0);
        testC.moveStudentsOnDiningRoom(0,0);
        testC.moveMotherNature(0,1);
        testC.selectCloud(0,2);
        testC.playCard(2,1);
        testC.playCard(0,7);
        testC.playCard(1,3);
        testC.moveStudentsOnDiningRoom(2,0);
        testC.moveStudentsOnDiningRoom(2,0);
        testC.moveStudentsOnDiningRoom(2,0);
        testC.moveStudentsOnDiningRoom(2,0);
        testC.moveMotherNature(2,1);
        testC.selectCloud(2,2);

    }

    /**
     * Test of arrival of messages and changes performed on Model
     *
     * */
    @RepeatedTest(100)
    @Test
    public void gameWithMessages() throws NotPossibleActionException, IOException {
        Controller c= new Controller(false,2);
        c.setNewPlayer("Fra");
        c.setNewPlayer("Eris");
        TowerColorMessage message = new TowerColorMessage("Fra","black");
        c.update(message);
        assertEquals(TowerColor.BLACK,c.getModel().getPlayers().get(0).getBoard().getTowerColor());
        message = new TowerColorMessage("Eris","white");
        c.update(message);
        assertEquals(TowerColor.WHITE,c.getModel().getPlayers().get(1).getBoard().getTowerColor());
        CharacterMessage messageChar = new CharacterMessage("Eris","ninja");
        c.update(messageChar);
        assertEquals(Character.NINJA,c.getModel().getPlayers().get(1).getDeck().get(0).getCardCharacter());
        messageChar = new CharacterMessage("Fra","witch");
        c.update(messageChar);
        assertEquals(Character.WITCH,c.getModel().getPlayers().get(0).getDeck().get(0).getCardCharacter());
        PlayCardMessage playCardMessage =new PlayCardMessage("Fra",1);
        c.update(playCardMessage);
        assertEquals(1,c.getModel().getPlayers().get(0).getPlayedCards().size());
        playCardMessage =new PlayCardMessage("Eris",2);
        c.update(playCardMessage);
        assertEquals(1,c.getModel().getPlayers().get(1).getPlayedCards().size());
        MoveStudentOnDiningRoomMessage moveDining = new MoveStudentOnDiningRoomMessage("Fra",0);
        c.update(moveDining);
        assertEquals(6,c.getModel().getPlayers().get(0).getBoard().getEntrance().size());
        MoveStudentOnIslandMessage moveStudentOnIslandMessage = new MoveStudentOnIslandMessage("Fra",0,0);
        c.update(moveStudentOnIslandMessage);
        assertEquals(5,c.getModel().getPlayers().get(0).getBoard().getEntrance().size());
        assertEquals(1,c.getModel().getIslands().get(0).getStudents().size());
        moveStudentOnIslandMessage = new MoveStudentOnIslandMessage("Fra",0,0);
        c.update(moveStudentOnIslandMessage);
        assertEquals(4,c.getModel().getPlayers().get(0).getBoard().getEntrance().size());
        MoveMotherNatureMessage motherNatureMessage= new MoveMotherNatureMessage("Fra",1);
        c.update(motherNatureMessage);
        assertTrue(c.getModel().getIslands().get(1).hasMotherNature());
        ChooseCloudMessage chooseCloudMessage= new ChooseCloudMessage("Fra",0);
        c.update(chooseCloudMessage);
        assertEquals(0,c.getModel().getClouds().get(0).getStudents().size());
    }


}
