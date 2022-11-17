package it.polimi.ingsw.Model;

import it.polimi.ingsw.Exceptions.InvalidInputException;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.RepeatedTest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ExpertGameTest {

    @Test
    @RepeatedTest(20)
    void ExpertGameTest(){
        ExpertGame game = new ExpertGame(3);
        assertEquals(3, game.getCharacterCards().size());
        for (Player player :
                game.getPlayers()) {
           assertEquals(1, game.getCoins(player));
        }
        CharacterCard1 character1 = new CharacterCard1(game);
        assertEquals(1,character1.Cost);
        CharacterCard7 characterCard7   =new CharacterCard7(game);
        assertEquals(1,characterCard7.Cost);
    }

    @Test
    @RepeatedTest(20)
    void CoinTest(){
        ExpertGame game = new ExpertGame(3);
        while (game.getPlayers().get(0).getBoard().getEntrance().size()!=0)
            game.getPlayers().get(0).getBoard().getEntrance().remove(0);
        assertEquals(0, game.getPlayers().get(0).getBoard().getEntrance().size());

        for (int i = 0; i < 3; i++) {
            game.getPlayers().get(0).getBoard().getEntrance().add(new Student(Disk.UNICORN));
            game.moveStudentOnDiningRoom(game.getPlayers().get(0), 0);
        }
        assertEquals(2, game.getCoins(game.getPlayers().get(0)));

        for (int i = 0; i < 3; i++) {
            game.getPlayers().get(0).getBoard().getEntrance().add(new Student(Disk.UNICORN));
            game.moveStudentOnDiningRoom(game.getPlayers().get(0), 0);
        }
        assertEquals(3, game.getCoins(game.getPlayers().get(0)));
    }

    boolean equivalent(ExpertGame game, ExpertGame game1){
        return (
                game1.CharacterCard.equals(game.CharacterCard) &&
                        game1.Coins.equals(game.Coins) &&
                        game1.NumPlayers == game.NumPlayers &&
                        game1.NumOfStudentsOnCloud == game.NumOfStudentsOnCloud &&
                        game1.NumOfStartTower == game.NumOfStartTower &&
                        game1.firstPlayer.equals(game.firstPlayer) &&
                        game1.Teams.equals(game.Teams) &&
                        game1.Players.equals(game.Players) &&
                        game1.actionPlayers.equals(game.actionPlayers) &&
                        game1.Islands.equals(game.Islands) &&
                        game1.Bag.equals(game.Bag) &&
                        game1.Clouds.equals(game.Clouds) &&
                        game1.redProfessor.equals(game.redProfessor) &&
                        game1.yellowProfessor.equals(game.yellowProfessor) &&
                        game1.pinkProfessor.equals(game.pinkProfessor) &&
                        game1.blueProfessor.equals(game.blueProfessor) &&
                        game1.greenProfessor.equals(game.greenProfessor)
        );
    }
    void emptyCharacterCards(ExpertGame game){
        while (game.getCharacterCards().size() != 0)
            game.getCharacterCards().remove(0);
        assertEquals(0, game.getCharacterCards().size());
    }


    @Test
    @RepeatedTest(20)
    void SpecialCharacterCardTest() throws InvalidInputException {
        ExpertGame game = new ExpertGame(3);

        emptyCharacterCards(game);

        game.getCharacterCards().add(new CharacterCard8(game));

        Island empty = null;
        for (Island i :
                game.getIslands()) {
            if (i.getStudents().size() == 0)
                empty = i;
        }
        empty.addStudent(new Student(Disk.UNICORN));
        empty.addStudent(new Student(Disk.DRAGON));
        game.addPlayerProfessor(0, Disk.UNICORN);
        game.addPlayerProfessor(1, Disk.DRAGON);
        assertFalse(game.isTowerPlaceable(game.getPlayers().get(0), empty, game.currentInfluence(empty)));

        assertEquals(1, game.getCharacterCards().size());
        assertEquals(0, game.getCharacterCards().get(0).getVarCost());

        game = (SpecialCharacterCard) game.getCharacterCards().get(0).Effect(0, "");

        assertTrue(game instanceof SpecialCharacterCard);
        assertEquals(1, game.getCharacterCards().get(0).getVarCost());

        assertTrue(game.isTowerPlaceable(game.getPlayers().get(0), empty, game.currentInfluence(empty)));

        game = ((SpecialCharacterCard) game).getFatherGame();
        assertFalse(game instanceof SpecialCharacterCard);
    }

    @Test
    @RepeatedTest(20)
    void SimpleCharacterCardTest() throws InvalidInputException {
        ExpertGame game = new ExpertGame(3);
        emptyCharacterCards(game);
        game.getCharacterCards().add(new CharacterCard2(game));
        game.getCharacterCards().get(0).Effect(0, "").getPlayers().add(new Player("GIACOMO"));
        for (Player p :
                game.getPlayers()) {
            System.out.println(p.getNickname());
        }

    }
    
    @Test
    @RepeatedTest(1)
    void CharacterCard12Test() throws InvalidInputException {
        ExpertGame game = new ExpertGame(3);
        emptyCharacterCards(game);
        game.getCharacterCards().add(new CharacterCard12(game));
        List<Integer> stu = new ArrayList<Integer>();
        for (Player player :
                game.getPlayers()) {
            stu.add((int) player.getBoard().getEntrance().stream().filter(x -> x.getPawn().equals(Disk.FROG)).count());
        }
        game.getCharacterCards().get(0).Effect(0, "frog").getPlayers().add(new Player("GIACOMO"));
        List<Integer> stu2 = new ArrayList<Integer>();
        for (Player player :
                game.getPlayers()) {
            stu2.add((int) player.getBoard().getEntrance().stream().filter(x -> x.getPawn().equals(Disk.FROG)).count());
        }
        for (int i = 0; i < stu.size(); i++) {
            if (stu.get(i)!=0)
                assertTrue(stu.get(i) >= stu2.get(i));
        }
    }

    @Test
    @RepeatedTest(1)
    void CharacterCard12SecondTest() throws InvalidInputException {
        ExpertGame game = new ExpertGame(3);
        emptyCharacterCards(game);
        game.getCharacterCards().add(new CharacterCard12(game));
        List<Integer> stu = new ArrayList<Integer>();
        for (Player player :
                game.getPlayers()) {
            stu.add((int) player.getBoard().getEntrance().stream().filter(x -> x.getPawn().equals(Disk.FROG)).count());
        }
        game.getCharacterCards().get(0).Effect(0, "dragon").getPlayers().add(new Player("GIACOMO"));
        List<Integer> stu2 = new ArrayList<Integer>();
        for (Player player :
                game.getPlayers()) {
            stu2.add((int) player.getBoard().getEntrance().stream().filter(x -> x.getPawn().equals(Disk.FROG)).count());
        }
        for (int i = 0; i < stu.size(); i++) {
            if (stu.get(i)!=0)
                assertTrue(stu.get(i) >= stu2.get(i));
        }
    }

    @Test
    @RepeatedTest(1)
    void CharacterCard12ThirdTest() throws InvalidInputException {
        ExpertGame game = new ExpertGame(3);
        emptyCharacterCards(game);
        game.getCharacterCards().add(new CharacterCard12(game));
        List<Integer> stu = new ArrayList<Integer>();
        for (Player player :
                game.getPlayers()) {
            stu.add((int) player.getBoard().getEntrance().stream().filter(x -> x.getPawn().equals(Disk.FROG)).count());
        }
        game.getCharacterCards().get(0).Effect(0, "gnome").getPlayers().add(new Player("GIACOMO"));
        List<Integer> stu2 = new ArrayList<Integer>();
        for (Player player :
                game.getPlayers()) {
            stu2.add((int) player.getBoard().getEntrance().stream().filter(x -> x.getPawn().equals(Disk.FROG)).count());
        }
        for (int i = 0; i < stu.size(); i++) {
            if (stu.get(i)!=0)
                assertTrue(stu.get(i) >= stu2.get(i));
        }
    }

    @Test
    @RepeatedTest(1)
    void CharacterCard12FourthTest() throws InvalidInputException {
        ExpertGame game = new ExpertGame(3);
        emptyCharacterCards(game);
        game.getCharacterCards().add(new CharacterCard12(game));
        List<Integer> stu = new ArrayList<Integer>();
        for (Player player :
                game.getPlayers()) {
            stu.add((int) player.getBoard().getEntrance().stream().filter(x -> x.getPawn().equals(Disk.FROG)).count());
        }
        game.getCharacterCards().get(0).Effect(0, "unicorn").getPlayers().add(new Player("GIACOMO"));
        List<Integer> stu2 = new ArrayList<Integer>();
        for (Player player :
                game.getPlayers()) {
            stu2.add((int) player.getBoard().getEntrance().stream().filter(x -> x.getPawn().equals(Disk.FROG)).count());
        }
        for (int i = 0; i < stu.size(); i++) {
            if (stu.get(i)!=0)
                assertTrue(stu.get(i) >= stu2.get(i));
        }
    }

    @Test
    @RepeatedTest(1)
    void CharacterCard12FifthTest() throws InvalidInputException {
        ExpertGame game = new ExpertGame(3);
        emptyCharacterCards(game);
        game.getCharacterCards().add(new CharacterCard12(game));
        List<Integer> stu = new ArrayList<Integer>();
        for (Player player :
                game.getPlayers()) {
            stu.add((int) player.getBoard().getEntrance().stream().filter(x -> x.getPawn().equals(Disk.FROG)).count());
        }
        game.getCharacterCards().get(0).Effect(0, "fairy").getPlayers().add(new Player("GIACOMO"));
        List<Integer> stu2 = new ArrayList<Integer>();
        for (Player player :
                game.getPlayers()) {
            stu2.add((int) player.getBoard().getEntrance().stream().filter(x -> x.getPawn().equals(Disk.FROG)).count());
        }
        for (int i = 0; i < stu.size(); i++) {
            if (stu.get(i)!=0)
                assertTrue(stu.get(i) >= stu2.get(i));
        }
    }

    @Test
    @RepeatedTest(1)
    void CharacterCard1Test() throws InvalidInputException {
        ExpertGame game = new ExpertGame(3);
        emptyCharacterCards(game);
        CharacterCard1 c1 = new CharacterCard1(game);
        game.getCharacterCards().add(c1);
        String c1s = c1.StudentsOnCard.get(0).getPawn().toString();
        game.getCharacterCards().get(0).Effect(0, c1s + " 1");
        assertTrue(game.getIslands().get(0).getStudents().stream().filter(x -> x.getPawn().toString().equalsIgnoreCase(c1s)).count()>0);
    }

    @Test
    @RepeatedTest(1)
    void CharacterCard3Test() throws InvalidInputException {
        ExpertGame game = new ExpertGame(2);
        emptyCharacterCards(game);
        game.getCharacterCards().add(new CharacterCard3(game));
        for (Student s :
                game.getIslands().get(0).getStudents()) {
            game.getIslands().get(0).getStudents().remove(s);
        }
        game.getIslands().get(0).getStudents().add(new Student(Disk.DRAGON));
        game.getPlayers().get(0).getBoard().getProfessors().add(new Professor(Disk.DRAGON));
        game.getCharacterCards().get(0).Effect(0, "1");
        assertTrue(game.getIslands().get(0).getNumTowers()>0);
    }

    @Test
    @RepeatedTest(1)
    void CharacterCard10Test() throws InvalidInputException {
        ExpertGame game = new ExpertGame(2);
        emptyCharacterCards(game);
        game.getCharacterCards().add(new CharacterCard10(game));
        game.getPlayers().get(0).getBoard().getEntrance().add(new Student(Disk.DRAGON));
        game.getPlayers().get(0).getBoard().getDiningRoom().get(Disk.FROG).add(new Student(Disk.FROG));
        int d = game.getPlayers().get(0).getBoard().getDiningRoom().get(Disk.FROG).size();
        int e = (int) game.getPlayers().get(0).getBoard().getEntrance().stream().filter(x -> x.getPawn().equals(Disk.DRAGON)).count();
        game.getCharacterCards().get(0).Effect(0, "dragon frog");
        assertTrue(game.getPlayers().get(0).getBoard().getDiningRoom().get(Disk.FROG).size() == d-1 &&
        (int) game.getPlayers().get(0).getBoard().getEntrance().stream().filter(x -> x.getPawn().equals(Disk.DRAGON)).count() == e-1);
    }

    @Test
    @RepeatedTest(1)
    void EgTest(){
        ExpertGame e = new ExpertGame(2);
        ExpertGame game = new ExpertGame(e);
        assertTrue(
                e.lastTurn == game.lastTurn&&
        e.CharacterCard == game.CharacterCard&&
        e.Coins == game.Coins&&
        e.NumPlayers == game.NumPlayers&&
        e.NumOfStudentsOnCloud == game.NumOfStudentsOnCloud&&
        e.NumOfStartTower == game.NumOfStartTower&&
        e.firstPlayer == game.firstPlayer&&
        e.Teams == game.Teams&&
        e.Players == game.Players&&
        e.actionPlayers == game.actionPlayers&&
        e.Islands == game.Islands&&
        e.Bag == game.Bag&&
        e.Clouds == game.Clouds&&
        e.redProfessor == game.redProfessor&&
        e.yellowProfessor == game.yellowProfessor&&
        e.pinkProfessor == game.pinkProfessor&&
        e.blueProfessor == game.blueProfessor&&
        e.greenProfessor == game.greenProfessor
        );
    }
    @Test
    @RepeatedTest(1)
    void CharacterCard7Test() throws InvalidInputException {
        ExpertGame game = new ExpertGame(2);
        emptyCharacterCards(game);
        game.getCharacterCards().add(new CharacterCard7(game));
        game.getPlayers().get(0).getBoard().getEntrance().add(new Student(Disk.DRAGON));
        ((CharacterCard7) game.getCharacterCards().get(0)).getStudentsOnCard().add(new Student(Disk.FROG));
        int d = (int) game.getPlayers().get(0).getBoard().getEntrance().stream().filter(x -> x.equals(Disk.DRAGON)).count();
        int e = (int) ((CharacterCard7) game.getCharacterCards().get(0)).getStudentsOnCard().stream().filter(x -> x.getPawn().equals(Disk.FROG)).count();
        game.getCharacterCards().get(0).Effect(0, "dragon frog");
        assertTrue((int) ((CharacterCard7) game.getCharacterCards().get(0)).getStudentsOnCard().stream().filter(x -> x.getPawn().equals(Disk.FROG)).count() != e &&
                (int) game.getPlayers().get(0).getBoard().getEntrance().stream().filter(x -> x.getPawn().equals(Disk.DRAGON)).count() != d);
    }
}