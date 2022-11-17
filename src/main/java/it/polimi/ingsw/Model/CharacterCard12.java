package it.polimi.ingsw.Model;

import it.polimi.ingsw.Connection.MessageType;
import it.polimi.ingsw.Exceptions.InvalidInputException;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class CharacterCard12 extends SimpleCharacterCard implements Serializable {
    public CharacterCard12(ExpertGame game){
        super(game, 3);
        needsParameters = true;
    }

    public ExpertGame Effect(int playerId, String parameters) throws InvalidInputException{
        Disk color;
        List<String> params = Arrays.asList(parameters.split(" ")).stream().filter(x -> !x.equals("")).collect(Collectors.toList());
        if (params.size() != 1)
            throw new InvalidInputException("Wrong number of parameters, given " + params.size() + " parameters, expected 1.");
        switch (params.get(0)){
            case "gnome":
                color = Disk.GNOME;
                break;
            case "unicorn":
                color = Disk.UNICORN;
                break;
            case "frog":
                color = Disk.FROG;
                break;
            case "dragon":
                color = Disk.DRAGON;
                break;
            case "fairy":
                color = Disk.FAIRY;
                break;
            default:
                throw new InvalidInputException("Invalid disk : Given \"" + params.get(0) + "\", but expected parameters are: gnome, unicorn, frog, dragon, fairy.");
        }
        Effect(playerId, color);
        return fatherGame;
    }

    @Override
    public String getEffectDescription() {
        return "Choose a type of student:\n" +
                "every player (including yourself) must return 3 students of that" +
                "type from their entrance to the bag.\n" +
                "If any player has fewer than 3 students of that type, return as many\n" +
                "students as they have.\n" +
                "*GNOME/FAIRY/DRAGON/UNICORN/FROG*";
    }

    public void Effect(int playerId, Disk color){
        useCard(playerId);
        for (Player player :
                fatherGame.getPlayers()) {
            int count = 0;
            while (player.getBoard().getEntrance().stream().anyMatch(x -> x.getPawn().equals(color))){
                if (count<3)
                    fatherGame.getBag().add(
                            player.getBoard().getEntrance().remove(
                                    player.getBoard().getEntrance().indexOf(
                                            player.getBoard().getEntrance()
                                                    .stream()
                                                    .filter(x -> x.getPawn().equals(color))
                                                    .collect(Collectors.toList()).get(0)
                                     )
                            )
                    );
                else break;

                count += 1;
            }
        }
        fatherGame.notifyObservers(MessageType.BOARDS);

        Collections.shuffle(fatherGame.getBag());
    }

    @Override
    public int getCharacterNum(){
        return 12;
    }
}
