package it.polimi.ingsw.Model;

import it.polimi.ingsw.Connection.MessageType;
import it.polimi.ingsw.Exceptions.InvalidInputException;
import it.polimi.ingsw.View.Colors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Effect's description: Take 1 student from this card and place it on an island of your choice.
 */
public class CharacterCard1 extends SimpleCharacterCard implements Serializable {

    /**
     * Constructor use super constructor and set needParameters attribute, needsParameters, StudentsOnCard (token directly from fatherGame) and StudentsOnCardString
     * @param game
     */
    public CharacterCard1(ExpertGame game) {
        super(game, 1);
        for (int i = 0; i < 4; i++) {
            StudentsOnCard.add(fatherGame.getBag().remove(0));
        }
        needsParameters = true;
        StudentsOnCardString = studIndex();
    }

    public ExpertGame Effect(int playerId, String parameters) throws InvalidInputException {
        int studentId;
        int islandId;
        try{
            List<String> pars = Arrays.stream(parameters.split(" ")).filter(x -> !x.equals("")).collect(Collectors.toList());
            if (pars.size() != 2)
                throw new InvalidInputException("Wrong number of parameters, given " + pars.size() + " parameters, expected 2.");
            if (StudentsOnCard.stream().noneMatch(x -> x.getPawn().toString().equalsIgnoreCase(pars.get(0))))
                throw new InvalidInputException("No students on this card with " + pars.get(0) + " type");
            islandId = Integer.parseInt(pars.get(1)) - 1;
            studentId = StudentsOnCard.indexOf(
                    StudentsOnCard.stream()
                            .filter(x -> x.getPawn().toString().equalsIgnoreCase(pars.get(0)))
                            .collect(Collectors.toList()).get(0));

            int islandSize = fatherGame.getIslands().size();

            if (!(islandId >= 0 && islandId < islandSize))
                throw new InvalidInputException("Invalid island parameter:" + islandId + "must be from 1 to " + islandSize);
            Effect(playerId, studentId, islandId);
            return fatherGame;
        }catch (NumberFormatException e){
            throw new InvalidInputException("Island parameter must be an integer");
        }
    }

    @Override
    public String getEffectDescription() {
        return "Take 1 student from this card and place it on an island of your choice.\n" +
                "*Student type* *Island ID* \n" + StudentsOnCardString;
    }

    public void Effect(int playerId, int studentId, int islandId){
        useCard(playerId);
        fatherGame.getIslands().get(islandId).getStudents().add(StudentsOnCard.remove(studentId));
        fatherGame.notifyObservers(MessageType.ISLANDS);
        if (fatherGame.getBag().size()>=1)
            StudentsOnCard.add(fatherGame.getBag().remove(0));
        StudentsOnCardString = studIndex();
        fatherGame.notifyObservers(MessageType.ISLANDS);
    }

    @Override
    public int getCharacterNum(){
        return 1;
    }
}
