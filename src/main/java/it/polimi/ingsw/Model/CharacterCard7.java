package it.polimi.ingsw.Model;

import it.polimi.ingsw.Connection.MessageType;
import it.polimi.ingsw.Exceptions.InvalidInputException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class CharacterCard7 extends SimpleCharacterCard implements Serializable {

    public List<Student> getStudentsOnCard(){ return StudentsOnCard; }

    public CharacterCard7(ExpertGame game){
        super(game, 1);
        for (int i = 0; i < 6; i++) {
            StudentsOnCard.add(fatherGame.getBag().remove(0));
        }
        needsParameters = true;
        StudentsOnCardString = studIndex();
    }

    public ExpertGame Effect(int playerId, String parameters) throws InvalidInputException {
        parameters = parameters.toUpperCase();
        List<String> params = Arrays.asList(parameters.split(" ")).stream().filter(x -> !x.equals("")).collect(Collectors.toList());

        if (!(params.size() > 0 && params.size()<=6 && params.size() % 2 == 0))
            throw new InvalidInputException("Wrong number of parameters, given " + params.size() + " parameters, expected 2, 4 or 6.");

        if (params.stream().allMatch(x ->
                x.equals("DRAGON") || x.equals("GNOME") || x.equals("FAIRY") ||
                        x.equals("UNICORN") || x.equals("FROG"))){

            for (int i = 0; i < params.size() / 2; i++) {
                for (int j = params.size()/2; j < params.size(); j++) {
                    if (params.get(i).equalsIgnoreCase(params.get(j)))
                        throw new InvalidInputException("Why exchange same type of students ? Move not allowed.");
                }
            }

            for (Disk color :
                    Disk.values()) {
                if (
                        params.stream().filter(x->
                                        params.indexOf(x) < params.size()/2 && x.equalsIgnoreCase(color.toString()))
                                .count()
                                >
                                fatherGame.getPlayers().get(playerId).getBoard().getEntrance().stream().filter(x ->
                                        x.getPawn().equals(color)).count()
                )
                    throw new InvalidInputException("Not enough desired students on your entrance");

                if (
                        params.stream().filter(x->
                                        params.indexOf(x) >= params.size()/2 && x.equalsIgnoreCase(color.toString()))
                                .count()
                                >
                                StudentsOnCard.stream().filter(x -> x.getPawn().equals(color)).count()
                )
                    throw new InvalidInputException("Not enough desired students on this card");
            }

            for (Disk color :
                    Disk.values()) {
                for (int i = 0; i < params.stream().filter(x -> params.indexOf(x) >= params.size()/2 && x.equalsIgnoreCase(color.toString())).count(); i++) {
                    fatherGame.getPlayers().get(playerId).getBoard().getEntrance().add(
                            StudentsOnCard.remove(
                                    StudentsOnCard.indexOf(
                                            StudentsOnCard.stream().filter(x -> x.getPawn().equals(color)).collect(Collectors.toList()).get(0)
                                    )
                            )
                    );
                }
                for (int i = 0; i < params.stream().filter(x -> params.indexOf(x) < params.size()/2 && x.equalsIgnoreCase(color.toString())).count(); i++) {
                    StudentsOnCard.add(
                            fatherGame.getPlayers().get(playerId).getBoard().getEntrance().remove(
                                    fatherGame.getPlayers().get(playerId).getBoard().getEntrance().indexOf(
                                            fatherGame.getPlayers().get(playerId).getBoard().getEntrance().stream()
                                                    .filter(x -> x.getPawn().equals(color)).collect(Collectors.toList()).get(0)
                                    )
                            )
                    );
                }
            }
        }
        else throw new InvalidInputException("Invalid Student Type");
        useCard(playerId);
        StudentsOnCardString = studIndex();
        fatherGame.notifyObservers(MessageType.BOARDS);
        return fatherGame;
    }

    @Override
    public String getEffectDescription() {
        return "You may take up to 3 students from this card and \n" +
                "replace them with the same number of students from your entrance.\n" +
                "*Student Type on Entrance X (1, 2, 3 students)* *StudentType on Card X (1, 2, 3 students)*\n" +
                StudentsOnCardString;
    }

    @Override
    public int getCharacterNum(){
        return 7;
    }
}
