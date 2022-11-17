package it.polimi.ingsw.Model;

import it.polimi.ingsw.Connection.MessageType;
import it.polimi.ingsw.Exceptions.InvalidInputException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class CharacterCard11 extends SimpleCharacterCard implements Serializable {

    public List<Student> getStudentsOnCard(){ return StudentsOnCard; }

    public CharacterCard11(ExpertGame game) {
        super(game, 2);
        for (int i = 0; i < 4; i++) {
            StudentsOnCard.add(fatherGame.getBag().remove(0));
        }
        needsParameters = true;
        StudentsOnCardString=studIndex();
    }

    public ExpertGame Effect(int playerId, String parameters) throws InvalidInputException{
        try{
            List<String> params = Arrays.stream(parameters.split(" ")).filter(x -> !x.equals("")).collect(Collectors.toList());
            int par;
            if (params.size() != 1)
                throw new InvalidInputException("Wrong number of parameters, given " + params.size() + " parameters, expected 1.");

            if (StudentsOnCard.stream().noneMatch(x -> x.getPawn().toString().equalsIgnoreCase(params.get(0))))
                throw new InvalidInputException("No students on this card with " + params.get(0) + " type");

            par = StudentsOnCard.indexOf(
                    StudentsOnCard.stream()
                            .filter(x -> x.getPawn().toString().equalsIgnoreCase(params.get(0)))
                            .collect(Collectors.toList()).get(0));

            if (!(par >= 0 && par < StudentsOnCard.size()))
                throw new InvalidInputException("Invalid student parameter:" + par + "must be from 0 to " + (StudentsOnCard.size() - 1));
            Effect(playerId, par);
            return fatherGame;
        }catch (NumberFormatException e){
            throw new InvalidInputException("Parameter is not an integer");
        }
    }

    @Override
    public String getEffectDescription() {
        return "Take 1 student from this card and place it in your dining room.\n" +
                "*Student Type*\n" + StudentsOnCardString;
    }

    public void Effect(int playerId, int studentId){
        useCard(playerId);
        Student stu = StudentsOnCard.remove(studentId);
        fatherGame.getPlayers().get(playerId).getBoard().getDiningRoom().get(stu.getPawn()).add(stu);
        if (fatherGame.getBag().size()>=1)
            StudentsOnCard.add(fatherGame.getBag().remove(0));

        if (fatherGame.getPlayers().get(playerId).getBoard().getProfessors().stream()
                .anyMatch(x -> x.getPawn().equals(StudentsOnCard.get(studentId).getPawn())))
            return;
        else if (fatherGame.getPlayers().stream().filter(x -> playerId!=fatherGame.getPlayers().indexOf(x))
                .anyMatch(x -> x.getBoard().getProfessors().stream()
                        .anyMatch(y -> y.getPawn().equals(StudentsOnCard.get(studentId).getPawn())))){
            for (Player p :
                    fatherGame.getPlayers()) {
                if (!fatherGame.getPlayers().get(playerId).equals(p)){
                    for (Professor professor :
                            p.getBoard().getProfessors()) {
                        if (fatherGame.getPlayers().get(playerId)
                                .getBoard().getDiningRoom().get(professor.getPawn()).size()
                                >
                                p.getBoard().getDiningRoom().get(professor.getPawn()).size())
                            fatherGame.getPlayers().get(playerId)
                                    .getBoard().getProfessors().add(p.getBoard().getProfessors()
                                            .remove(p.getBoard().getProfessors().indexOf(professor)));
                    }
                }
            }
        }
        else
            fatherGame.addPlayerProfessor(playerId, stu.getPawn());
        StudentsOnCardString=studIndex();
        fatherGame.notifyObservers(MessageType.BOARDS);
    }

    @Override
    public int getCharacterNum(){
        return 11;
    }
}
