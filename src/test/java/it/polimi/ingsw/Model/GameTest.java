package it.polimi.ingsw.Model;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.Test;
import org.junit.jupiter.api.RepeatedTest;

import java.util.List;

class GameTest {

    @RepeatedTest(10)
    @Test
    public void canCardBePlayed() {
       Game game = new Game(2);
       Player player1 = game.getPlayers().get(0);
       Player player2 = game.getPlayers().get(1);
       player1.initializeDeck(Character.KING);
       player2.initializeDeck(Character.WIZARD);
       game.playCard(player1,0);
        assertFalse(game.isCardPlayable(player2, player2.getDeck().get(0)));
    }

    @RepeatedTest(10)
    @Test
    public void moveStudentOnIsland() {
        Game game = new Game(2);
        Island island = new Island();
        Player player = new Player("Player1");
        for (int i = 0; i < 7; i++){
            player.getBoard().getEntrance().add(game.getBag().remove(0));
        }
        assertEquals(7,player.getBoard().getEntrance().size());
        for( int i= 0; i<3;i++){
            island.getStudents().add(player.getBoard().getEntrance().remove(0));
        }
        assertEquals(3, island.getStudents().size());
        assertFalse(island.getStudents().size() > 3);

    }

    @RepeatedTest(10)
    @Test
    public void moveStudentOnDiningRoomTest() {
        Game game = new Game(2); //da completare
        Player player1,player2;
        player1 = game.getPlayers().get(0);
        player2 = game.getPlayers().get(1);
        Board board1,board2 ;
        board1 = player1.getBoard();
        board2 = player2.getBoard();
        player1.setNickname("John");
        game.moveStudentOnDiningRoom(player1,0);
        game.moveStudentOnDiningRoom(player1,1);
        //System.out.println(player1.getBoard().getEntrance().size());
        for(int i=0; i<4; i++){
            game.moveStudentOnDiningRoom(player2,i);
        }
        //System.out.println(player2.getBoard().getEntrance().size());
        assertNotNull(player1.getBoard().DiningRoom);
    }

    @RepeatedTest(10)
    @Test
    public void placeTowerOnIsland() {
        Game game = new Game(2);
        Player player1,player2;
        Professor professor1,professor2,prof3,prof4;
        professor1= new Professor(Disk.DRAGON);
        professor2= new Professor(Disk.FROG);
        prof3= new Professor(Disk.FAIRY);
        prof4= new Professor(Disk.UNICORN);
        player1 = game.getPlayers().get(0);
        player2 = game.getPlayers().get(1);
        game.setFirstPlayer(player1);
        assertEquals(player1,game.getFirstPlayer());
        Island island1,island2;
        island1 = new Island();
        island2 = new Island();
        Board board = player1.getBoard();
        game.moveStudentOnIsland(player1,island1,1);
        game.moveStudentOnIsland(player1,island1,3);

        assertEquals(0,island2.getStudents().size());
        island1.setMotherNature(true);
        board.getProfessors().add(professor1);
        board.getProfessors().add(professor2);
        board.getProfessors().add(prof3);
        board.getProfessors().add(prof4);

        game.isTowerPlaceable(player1,island1,game.currentInfluence(island1));
        game.placeTowerOnIsland(player1,island1);

        assertEquals(1,island1.getNumTowers());
        assertEquals(7, board.getNumTowers());
        assertEquals(8,game.getNumOfStartTower());

        assertFalse(game.isTowerPlaceable(player2,island1,game.currentInfluence(island1)));
        if(player2 == game.firstPlayer){
            game.placeTowerOnIsland(player2,island1);}
        assertEquals(8,player2.getBoard().getNumTowers());


    }

    @RepeatedTest(10)
    @Test
    public void replaceTowerOnIsland() {
        Game game = new Game(2);
        Player player1, player2;
        player1 = game.getPlayers().get(0);
        player1.getBoard().setTowerColor(TowerColor.WHITE);
        player2 = game.getPlayers().get(1);
        player2.getBoard().setTowerColor(TowerColor.BLACK);
        game.setFirstPlayer(player1);
        int IMN=0;
        for(int i=0; i<game.getIslands().size();i++){
            if(game.getIslands().get(i).hasMotherNature()){
                IMN = i;
            }
        }
        Island island = game.getIslands().get((IMN+1)%12);
        Professor professor = new Professor(island.getStudents().get(0).getPawn());
        player1.getBoard().getProfessors().add(professor);

        game.placeTowerOnIsland(player1,island);
        assertEquals("WHITE",island.getTowerColor().toString());
        assertEquals(player1,game.getIslandOwner(island));


        player2.getBoard().getProfessors().add(player1.getBoard().getProfessors().remove(0));
        assertFalse(player1.getBoard().getProfessors().contains(professor));

        game.placeTowerOnIsland(player2,island);

        assertEquals(player2,game.getIslandOwner(island));



    }

    @RepeatedTest(10)
    @Test
    public void placeStudentFromBagToCloud() {
        Cloud cloud1, cloud2;
        Game g1 = new Game(2);
        cloud1= new Cloud();
        cloud2= new Cloud();
        int NumOfPlayersOnCloud=0;
        for (int i=0; i<= g1.getNumPlayers(); i++){
            cloud1.getStudents().add(g1.getBag().remove(0));
        }
        for (int i=0; i<= g1.getNumPlayers(); i++){
            cloud2.getStudents().add(g1.getBag().remove(0));
        }
        assertEquals(cloud1.getStudents().size(),cloud2.getStudents().size());  //Checks if we have the same number of students on both the islands

        if(g1.getNumPlayers()==2 || g1.getNumPlayers()==4 ){ NumOfPlayersOnCloud = 3;}
        else if(g1.getNumPlayers()==3) NumOfPlayersOnCloud = 4;

        assertEquals(NumOfPlayersOnCloud, cloud1.getStudents().size());//Checks if the number of students on islands is correct

    }

    @RepeatedTest(10)
    @Test
    public void moveStudentsFromCloudTest() {
        Game game = new Game(2);
        Player player1,player2;
        player2 = new Player("Player2");
        player1 = new Player("Player1");
        Cloud cloud1,cloud2;
        cloud1 = game.getClouds().get(0);
        cloud2 = game.getClouds().get(1);
        assertEquals(0,player1.getBoard().getEntrance().size());
        assertEquals(0, player2.getBoard().getEntrance().size());
        game.moveStudentsFromCloud(player1,cloud1);
        game.moveStudentsFromCloud(player2,cloud2);
        assertEquals(player1.getBoard().getEntrance(),cloud1.getStudents());
        assertEquals(player2.getBoard().getEntrance(),cloud2.getStudents());

    }


    @RepeatedTest(10)
    @Test
    public void isTowerReplacebleTest(){
        Game game = new Game(2);
        int IMN=0;
        for(int i=0; i<game.getIslands().size();i++){
            if(game.getIslands().get(i).hasMotherNature()){
                IMN = i;
            }
        }

        Island island = game.getIslands().get((IMN+1)%12);
        Player player1,player2;
        player1= game.getPlayers().get(0);
        player2= game.getPlayers().get(1);
        Professor professor = new Professor(island.getStudents().get(0).getPawn());
        //System.out.println(island.getStudents().get(0).getPawn());
        player1.getBoard().setTowerColor(TowerColor.WHITE);
        player2.getBoard().setTowerColor(TowerColor.BLACK);
        player1.getBoard().getProfessors().add(professor);
        game.placeTowerOnIsland(player1,island);

        player2.getBoard().getProfessors().add(player1.getBoard().getProfessors().remove(0));
        game.currentInfluence(island);
        assertFalse(game.isTowerReplaceable(player2,island));
        game.placeTowerOnIsland(player2,island);
        assertEquals(player2,game.getIslandOwner(island)); //DC


    }

    @RepeatedTest(10)
    @Test
    public void isIslandJoinableTest(){
        Game game = new Game(2);
        Player player = new Player("Player1");
        player.getBoard().setTowerColor(TowerColor.BLACK);
        Island island1,island2;
        island1= game.getIslands().get(1);
        island2 = game.getIslands().get(2);
        assertEquals(0,game.isIslandJoinable(island1));
        game.placeTowerOnIsland(player,island1);
        game.placeTowerOnIsland(player,island2);
        int side2 = game.isIslandJoinable(island2);
        assertEquals(-1,side2);
        int side1 = game.isIslandJoinable(island1);
        assertEquals(1,side1);
    }

    @RepeatedTest(10)
    @Test
    public void joinIslandTest(){
        Game game = new Game(2);
        Island island1,island2;
        island1 = game.getIslands().get(1);
        island1.setNumTowers(1);
        island2 = game.getIslands().get(2);
        island2.setNumTowers(1);
        int IS1 = island1.getStudents().size();
        int IS2 = island2.getStudents().size();
        game.joinIslands(island1,island2);
        assertEquals(11,game.getIslands().size());
        assertEquals(IS1+IS2,game.getIslands().get(1).getStudents().size());
        assertEquals(2,game.getIslands().get(1).getNumTowers());
    }

    @RepeatedTest(10)
    @Test
    public void joinLastAndPenultimate(){
        Game game = new Game(2);
        Island island11,island10;
        island11 = game.getIslands().get(11);
        island11.setNumTowers(1);
        island10 = game.getIslands().get(10);
        island10.setNumTowers(1);
        int IS1 = island11.getStudents().size();
        int IS2 = island10.getStudents().size();
        game.joinIslands(island10,island11);
        assertEquals(11,game.getIslands().size());
    }

    @RepeatedTest(10)
    @Test
    public void InitializeEntranceTest(){
        Game game = new Game(2);
        Player player1,player2;
        player1= game.getPlayers().get(0);
        player2= game.getPlayers().get(1);
        assertNotNull(player1.getBoard().getEntrance());
        assertEquals(7,player1.getBoard().getEntrance().size());
        assertNotNull(player2.getBoard().getEntrance());
        assertEquals(7,player2.getBoard().getEntrance().size());
    }

    @RepeatedTest(10)
    @Test
    public void getIslandOwnerTest(){
        Game game = new Game(2);
        game.getPlayers().get(1).getBoard().setTowerColor(TowerColor.WHITE);
        Player player = game.getPlayers().get(0); player.getBoard().setTowerColor(TowerColor.BLACK);
        Island island = new Island();
        assertNull(game.getIslandOwner(island));
        player.getBoard().setTowerColor(TowerColor.BLACK);
        game.placeTowerOnIsland(player,island);
        Player check = game.getIslandOwner(island);
        assertEquals(player,check);
    }

    @RepeatedTest(10)
    @Test
    public void lastIsland(){
        Game game = new Game(2);
        assertEquals(0, game.isIslandJoinable(game.Islands.get(11)));
    }


    @RepeatedTest(10)
    @Test
    public void declareWinnerTowerVersionTest(){
        Game game = new Game(2);
        Player player1, player2;
        player1 = game.getPlayers().get(0);
        player2 = game.getPlayers().get(1);
        int i = 0;
        for (Island island: game.getIslands()
        ) {
            game.placeTowerOnIsland(player1,island);
            i++;
            if(i>7) break;
        }
        assertEquals(player1,game.declareWinner());

    }

    @RepeatedTest(10)
    @Test
    public void declareWinnerDeckVersionTest(){
        Game gam1 = new Game();
        Game game = new Game(3);
        Player player1 = game.getPlayers().get(0);
        Island island = game.getIslands().get(1);
        //assertNull(game.declareWinner());
        game.placeTowerOnIsland(player1,island);
        player1.getDeck().clear();
        assertEquals(player1,game.declareWinner());
    }

    @RepeatedTest(10)
    @Test
    public void InitializeTeamsTest(){
        Game game = new Game(4);
        Player player1,player2,player3,player4;
        player1 = game.getPlayers().get(0); player1.setNickname("First");
        player2 = game.getPlayers().get(1); player2.setNickname("Sec");
        player3 = game.getPlayers().get(2); player3.setNickname("third");
        player4 = game.getPlayers().get(3); player4.setNickname("Fourth");
        game.InitializeTeams();
        //assertEquals(2,game.getTeams().get(0).getMembers().size());
        //assertEquals(2,game.getTeams().get(1).getMembers().size());

    }

    @RepeatedTest(10)
    @Test
    public void isTowerPlaceableTeamsTest(){
        Game game = new Game(4);
        Island island = game.getIslands().get(1);
        Team team1,team2;
        Player player1,player2,player3,player4;
        player1 = game.getPlayers().get(0);
        player1.getBoard().setTowerColor(TowerColor.BLACK);
        player2 = game.getPlayers().get(1);
        player3 = game.getPlayers().get(2);
        player4 = game.getPlayers().get(3);
        game.InitializeTeams();
        team1 = game.getTeams().get(0);
        team1.getMembers().add(player1);
        team2 = game.getTeams().get(1);
        Professor professor = new Professor(player1.getBoard().getEntrance().get(0).getPawn());
        player1.getBoard().getProfessors().add(professor);
        assertEquals(team1, game.getTeamOfPlayer(player1));
        game.moveStudentOnIsland(player1,island,0);

        if (game.isTowerPlaceable(team1,island,game.currentTeamInfluence(island))){
            game.placeTowerOnIsland(player1,island);

        }
        assertFalse(game.isTowerPlaceable(team2,island,game.currentTeamInfluence(island)));
        island.getPawnsTypeOnIsland();
       // assertEquals(player1,team1.getPLayerWithTower());
        //assertNotEquals(player2,team1.getPLayerWithTower());


    }

    @RepeatedTest(10)
    @Test
    public void isTowerReplaceableTeamTest(){
        Game game = new Game(4);
        Team team1,team2;
        int IMN=0;
        for(int i=0; i<game.getIslands().size();i++){
            if(game.getIslands().get(i).hasMotherNature()){
                IMN = i;
            }
        }
        Island island = game.getIslands().get((IMN+1)%12);
        Player player1,player2,player3,player4;
        player1= game.getPlayers().get(0); player1.getBoard().setTowerColor(TowerColor.WHITE);
        player2= game.getPlayers().get(1);
        player3= game.getPlayers().get(2);player3.getBoard().setTowerColor(TowerColor.BLACK);
        player4= game.getPlayers().get(3);
        game.InitializeTeams();
        team1 = game.getTeams().get(0);
        team2 = game.getTeams().get(1);
        Professor professor = new Professor(island.getStudents().get(0).getPawn());
        player1.getBoard().getProfessors().add(professor);
        game.placeTowerOnIsland(player1,island);
        player3.getBoard().getProfessors().add(player1.getBoard().getProfessors().remove(0));
        //assertFalse(game.isTowerReplaceable(team2,island));
    }

    @RepeatedTest(10)
    @Test
    public void replaceTowerOnIslandTest(){
        Game game = new Game(2);
        int IMN=0;
        for(int i=0; i<game.getIslands().size();i++){
            if(game.getIslands().get(i).hasMotherNature()){
                IMN = i;
            }
        }
        Island island = game.getIslands().get((IMN+1)%12);
        Player player1,player2;
        player1 = game.getPlayers().get(0); player1.getBoard().setTowerColor(TowerColor.BLACK);
        player2 = game.getPlayers().get(1); player2.getBoard().setTowerColor(TowerColor.WHITE);
        game.placeTowerOnIsland(player1,island);
        assertEquals(player1, game.getIslandOwner(island));
        game.replaceTowerOnIsland(player2,island);
        assertEquals(player2.getBoard().getTowerColor(), island.getTowerColor());
    }

    @RepeatedTest(10)
    @Test
    public void profObtained(){
        Game game = new Game(2);
        Player player1 = game.getPlayers().get(0);
        Player player2 = game.getPlayers().get(1);
        Disk pawnMoved = game.getPlayers().get(0).getBoard().getEntrance().get(0).getPawn();
        game.moveStudentOnDiningRoom(player1,0);
        game.addPlayerProfessor(0,pawnMoved);
        Disk pawn = null;
        for(Professor p : game.getPlayers().get(0).getBoard().getProfessors()){
            pawn = p.getPawn();
        }
        assertEquals(pawnMoved,pawn);
    }

    @RepeatedTest(10)
    @Test
    public void giveProfToOtherPlayer(){
        Game game = new Game(2);
        Player player1 = game.getPlayers().get(0);
        Player player2 = game.getPlayers().get(1);
        Disk pawnMoved = game.getPlayers().get(0).getBoard().getEntrance().get(0).getPawn();
        game.moveStudentOnDiningRoom(player1,0);
        game.addPlayerProfessor(0,pawnMoved);
        game.addPlayerProfessor(1,pawnMoved);
        Disk pawn = null;
        for(Professor P: game.getPlayers().get(0).getBoard().getProfessors()) {
            pawn = P.getPawn();
           }
        assertNull(pawn);
        for(Professor P: game.getPlayers().get(1).getBoard().getProfessors()) {
            pawn = P.getPawn();
        }
        assertEquals(pawnMoved,pawn);
    }
    @RepeatedTest(10)
    @Test
    public void mothernatureMove(){
        Game game = new Game(2);
        Island island1 = game.getIslands().get(0);
        Island island2 = game.getIslands().get(1);
        Player player = game.getPlayers().get(0);
        island1.setMotherNature(true);
        game.moveMotherNature(1);
        assertTrue(island2.hasMotherNature());

    }

    @RepeatedTest(10)
    @Test
    public void setTowerColor(){
        Game game = new Game(2);
        Player player1 = game.getPlayers().get(0);
        Player player2 = game.getPlayers().get(1);
        game.setTowerColor(0,TowerColor.BLACK);
        game.setTowerColor(1,TowerColor.GREY);
        assertEquals(TowerColor.BLACK,player1.getBoard().getTowerColor());
        assertEquals(TowerColor.GREY,player2.getBoard().getTowerColor());
    }

    @RepeatedTest(10)
    @Test
    public void testCLoud(){
        Game game = new Game(4);
        Cloud cloud = game.getClouds().get(0);
        Island island = game.getIslands().get(0);
        Island island1= game.getIslands().get(1);
        Island bigisland= new Island(island,island1);
        bigisland.setTowerColor(TowerColor.BLACK);
        island.addStudent(new Student(Disk.FROG));
        List<Disk> disks= island.getPawnsTypeOnIsland();
        game.placeStudentFromBagToCloud(cloud);
        assertTrue(bigisland.getTowerColor().equals(TowerColor.BLACK));
        assertEquals(3,cloud.getStudents().size());
        Player player = new Player("ben");
    }

    @RepeatedTest(10)
    @Test
    public void setCharacter(){
        Game game = new Game(2);
        Player player1 = game.getPlayers().get(0);
        Player player2 = game.getPlayers().get(1);
        assertEquals(130-14-10,game.getBag().size());
        game.setDeckCharacter(0,Character.KING);
        game.setDeckCharacter(1,Character.WIZARD);
        assertEquals(Character.KING,player1.getDeck().get(0).getCardCharacter());
        assertEquals(Character.WIZARD,player2.getDeck().get(0).getCardCharacter());
    }

    @RepeatedTest(50)
    @Test
    public void onlyOneProfPlaceTowerTest(){
        Game game = new Game(2);
        Player player1 = game.getPlayers().get(0);
        Player player2 = game.getPlayers().get(1);
        game.addPlayerProfessor(0,Disk.UNICORN);
        game.setTowerColor(0,TowerColor.WHITE);
        game.moveStudentOnIsland(player1,game.Islands.get(0),0);
        game.moveStudentOnIsland(player1,game.Islands.get(0),0);
        if(game.getIslands().get(0).getStudents().stream().anyMatch(x -> x.getPawn().equals(Disk.UNICORN))) {
            if(game.getIslands().get(0).getStudents().stream().anyMatch(x -> x.getPawn().equals(Disk.DRAGON))){
                assertTrue(game.isTowerPlaceable(player1, game.getIslands().get(0), game.currentInfluence(game.Islands.get(0))));
                player1.setNickname("ciao");
                if(game.isTowerPlaceable(player1,game.getIslands().get(0),game.currentInfluence(game.Islands.get(0)))){
                    {
                        game.placeTowerOnIsland(player1,game.Islands.get(0));
                        assertEquals(1,game.getIslands().get(0).getNumTowers());
                        assertEquals(TowerColor.WHITE,game.getIslands().get(0).getTowerColor());
                    }
                }
            }
        }
        game.moveStudentOnIsland(player1,game.Islands.get(0),0);
        game.moveStudentOnIsland(player1,game.Islands.get(0),0);
        game.moveStudentOnIsland(player1,game.Islands.get(0),0);
        game.moveStudentOnIsland(player1,game.Islands.get(0),0);
        game.moveStudentOnIsland(player1,game.Islands.get(0),0);
        game.moveStudentOnIsland(player2,game.Islands.get(0),0);
        game.moveStudentOnIsland(player2,game.Islands.get(0),0);
        game.moveStudentOnIsland(player2,game.Islands.get(0),0);
        game.moveStudentOnIsland(player2,game.Islands.get(0),0);
        game.moveStudentOnIsland(player2,game.Islands.get(0),0);
        game.moveStudentOnIsland(player2,game.Islands.get(1),0);
        if(game.getIslands().get(0).getStudents().stream().anyMatch(x -> x.getPawn().equals(Disk.UNICORN))) {
            if(game.getIslands().get(0).getStudents().stream().anyMatch(x -> x.getPawn().equals(Disk.DRAGON))){
                player1.setNickname("ciao");
                    {
                        game.placeTowerOnIsland(player1,game.Islands.get(0));
                        assertEquals(1,game.getIslands().get(0).getNumTowers());
                        assertEquals(TowerColor.WHITE,game.getIslands().get(0).getTowerColor());
                }
            }
        }
    }


}