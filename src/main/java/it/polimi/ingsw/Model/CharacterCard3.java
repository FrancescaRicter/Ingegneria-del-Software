package it.polimi.ingsw.Model;

import it.polimi.ingsw.Connection.MessageType;
import it.polimi.ingsw.Exceptions.InvalidInputException;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class CharacterCard3 extends SimpleCharacterCard implements Serializable {

    public CharacterCard3(ExpertGame game){
        super(game, 3);
        needsParameters = true;
    }

    public ExpertGame Effect(int playerId, String parameters) throws InvalidInputException {
        try{
            int size = (int) Arrays.stream(parameters.split(" ")).filter(x -> !x.equals("")).count();
            if(size != 1)
                throw new InvalidInputException("Wrong number of parameters, given " + size + " parameters, expected 1.");

            int islandId = Integer.parseInt(parameters) - 1;
            if (islandId<0 || islandId >= fatherGame.getIslands().size())
                throw new InvalidInputException("Invalid island Id: Must be from 1 to " + fatherGame.getIslands().size());

            Effect(playerId, islandId);
            return fatherGame;
        }catch (NumberFormatException e){
            throw new InvalidInputException("One or more parameters are not integer");
        }
    }

    @Override
    public String getEffectDescription() {
        return "Choose an island and resolve the island as if mother nature had ended her movement there.\n" +
                "*Island Id*";
    }



    public void Effect(int playerId, int islandId){
        useCard(playerId);
        Island island = fatherGame.getIslands().get(islandId);
        if (fatherGame.getTeams()==null){
            for (Player player :
                    fatherGame.getPlayers()) {
                if (fatherGame.isTowerPlaceable(player, island, fatherGame.currentInfluence(island)))
                    fatherGame.placeTowerOnIsland(player, island);
            }
        }
        else {
            for (Team team :
                    fatherGame.getTeams()) {
                if (fatherGame.isTowerPlaceable(team, island, fatherGame.currentTeamInfluence(island)))
                    fatherGame.placeTowerOnIsland(team.getPLayerWithTower(), island);
            }
        }

        while (fatherGame.getIslands().stream().anyMatch(x -> fatherGame.isIslandJoinable(x) != 0)){
            if (fatherGame.getIslands().stream().anyMatch(x -> fatherGame.isIslandJoinable(x) == 1))
                fatherGame.joinIslands(
                        fatherGame.getIslands().get(
                                fatherGame.getIslands().indexOf(
                                        fatherGame.getIslands().stream().filter(x -> fatherGame.isIslandJoinable(x)==1)
                                                .collect(Collectors.toList()).get(0)
                                )
                        ),
                        fatherGame.getIslands().get(
                                (1 + fatherGame.getIslands().indexOf(
                                        fatherGame.getIslands().stream().filter(x -> fatherGame.isIslandJoinable(x)==1)
                                                .collect(Collectors.toList()).get(0))
                                ) % fatherGame.getIslands().size())
                );
            if (fatherGame.getIslands().stream().anyMatch(x -> fatherGame.isIslandJoinable(x) == -1 &&
                    fatherGame.getIslands().indexOf(x) > 0))
                fatherGame.joinIslands(
                        fatherGame.getIslands().get(
                                fatherGame.getIslands().indexOf(
                                        fatherGame.getIslands().stream().filter(x -> fatherGame.isIslandJoinable(x)==-1)
                                                .collect(Collectors.toList()).get(0)
                                )
                        ),
                        fatherGame.getIslands().get(
                                fatherGame.getIslands().indexOf(
                                        fatherGame.getIslands().stream().filter(x -> fatherGame.isIslandJoinable(x)==-1)
                                                .collect(Collectors.toList()).get(0)
                                ) - 1
                        )
                );
            else if (fatherGame.getIslands().stream().anyMatch(x -> fatherGame.isIslandJoinable(x) == -1)) {
                fatherGame.joinIslands(fatherGame.getIslands().get(0), fatherGame.getIslands().get(fatherGame.getIslands().size()-1));
            }
        }

        fatherGame.notifyObservers(MessageType.ISLANDS);
        fatherGame.notifyObservers(MessageType.BOARDS);
    }

    @Override
    public int getCharacterNum(){
        return 3;
    }
}
