package it.polimi.ingsw.Model;

import it.polimi.ingsw.Connection.MessageType;
import it.polimi.ingsw.Exceptions.InvalidInputException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CharacterCard2 extends SpecialCharacterCard implements Serializable {

    public CharacterCard2(ExpertGame game){
        super(game, 2);
        needsParameters = false;
    }

    public ExpertGame Effect(int playerId, String parameters) throws InvalidInputException {
        if (!parameters.equals(""))
            throw new InvalidInputException("Invalid Input: this card doesn't take any parameter");
        else{
            this.playerId = playerId;
            newProfessorsManagement();
            fatherGame.notifyObservers(MessageType.BOARDS);
            return super.Effect(playerId, parameters);
        }
    }

    @Override
    public void moveStudentOnDiningRoom(Player player, int StudentID){
        Student s = player.getBoard().getEntrance().get(StudentID);
        player.getBoard().getDiningRoom().get(s.getPawn()).add(player.getBoard().getEntrance().remove(StudentID));
        newProfessorsManagement();
        notifyObservers(MessageType.BOARDS);
    }

    private void newProfessorsManagement() {
        List<Professor> prof = new ArrayList<Professor>();
        for (Player p :
                fatherGame.getPlayers()) {
            if (!fatherGame.getPlayers().get(this.playerId).equals(p)){
                for (Professor professor :
                        p.getBoard().getProfessors()) {
                    if (fatherGame.getPlayers().get(this.playerId)
                            .getBoard().getDiningRoom().get(professor.getPawn()).size()
                            >=
                            p.getBoard().getDiningRoom().get(professor.getPawn()).size())
                        prof.add(professor);
                }
            }
        }
        fatherGame.getPlayers().get(playerId).getBoard().getProfessors().addAll(prof);
        for (Professor professor :
                prof) {
            for (Player player :
                    fatherGame.getPlayers().stream().filter(x -> fatherGame.getPlayers().indexOf(x) != playerId).collect(Collectors.toList())) {
                player.getBoard().getProfessors().remove(professor);
            }
        }
    }

    @Override
    public String getEffectDescription() {
        return "During this turn, you take control of any number of Professors\n" +
                "even if you have the same number od students as the player who\n" +
                "currently controls them.\n" +
                "THIS CARD TAKES NO PARAMETERS";
    }

    @Override
    public int getCharacterNum(){
        return 2;
    }
}
