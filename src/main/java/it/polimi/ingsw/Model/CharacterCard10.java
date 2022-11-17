package it.polimi.ingsw.Model;

import it.polimi.ingsw.Connection.MessageType;
import it.polimi.ingsw.Exceptions.InvalidInputException;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class CharacterCard10 extends SimpleCharacterCard implements Serializable {
    public CharacterCard10(ExpertGame game){
        super(game, 1);
        needsParameters = true;
    }

    public ExpertGame Effect(int playerId, String parameters) throws InvalidInputException{
        parameters = parameters.toUpperCase();
        List<String> params = Arrays.asList(parameters.split(" ")).stream().filter(x -> !x.equals("")).collect(Collectors.toList());

        if (!(params.size() > 0 && params.size()<=4 && params.size() % 2 == 0))
            throw new InvalidInputException("Wrong number of parameters, given " + params.size() + " parameters, expected 2 or 4.");

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
                        fatherGame.getPlayers().get(playerId).getBoard().getDiningRoom().get(color).size()
                )
                    throw new InvalidInputException("Not enough desired students on your dining room");
            }

            for (Disk color :
                    Disk.values()) {
                for (int i = 0; i < params.stream().filter(x -> params.indexOf(x) >= params.size()/2 && x.equals(color.toString())).count(); i++) {
                    fatherGame.getPlayers().get(playerId).getBoard().getEntrance().add(
                            fatherGame.getPlayers().get(playerId).getBoard().getDiningRoom().get(color).remove(0));
                }
                for (int i = 0; i < params.stream().filter(x -> params.indexOf(x) < params.size()/2 && x.equals(color.toString())).count(); i++) {
                    fatherGame.getPlayers().get(playerId).getBoard().getDiningRoom().get(color).add(
                            fatherGame.getPlayers().get(playerId).getBoard().getEntrance().remove(
                                    fatherGame.getPlayers().get(playerId).getBoard().getEntrance().indexOf(
                                            fatherGame.getPlayers().get(playerId).getBoard().getEntrance()
                                                    .stream().filter(x -> x.getPawn().equals(color)).collect(Collectors.toList()).get(0))));
                }
            }

            for (Disk d : Disk.values()) {
                boolean flag = true;
                for(Player p: fatherGame.getPlayers()) {
                    if(fatherGame.getNumPlayers()<=3) {
                        if(fatherGame.getPlayers().get(playerId).getBoard().getDiningRoom().get(d).size()<=p.getBoard().getDiningRoom().get(d).size() &&
                                fatherGame.getPlayers().indexOf(p)!=playerId) {
                            flag=false;
                            break;
                        }
                    } else {
                        if(fatherGame.getPlayers().get(playerId).getBoard().getDiningRoom().get(d).size()<=p.getBoard().getDiningRoom().get(d).size() &&
                                fatherGame.getPlayers().indexOf(p)!=playerId && fatherGame.getPlayers().indexOf(p)!=fatherGame.getPlayers().indexOf(fatherGame.getPlayerMate(playerId))) {
                            flag=false;
                            break;
                        }
                    }
                }
                if(flag) {
                    fatherGame.addPlayerProfessor(playerId,d);
                }
            }

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
        else throw new InvalidInputException("Invalid Student Type");
        useCard(playerId);
        fatherGame.notifyObservers(MessageType.BOARDS);
        return fatherGame;
    }

    @Override
    public String getEffectDescription() {
        return "You may exchange up to 2 students between your Entrance and your Dining Room.\n" +
                "*Student Type on Entrance X (1, 2 students)* *Student Type on Dining Room X (1, 2 students)*\n";
    }

    //public void Effect(int playerId, List<Integer> studentsIdOnEntrance, List<Integer> studentsIdOnDiningRoom){
    //    useCard(playerId);
    //    Player player = fatherGame.getPlayers().get(playerId);
    //    Map<Disk, List<Student>> diningRoom = player.getBoard().getDiningRoom();
    //    List<Student> buffStudent = new ArrayList<Student>();
    //
    //    for (Disk color :
    //            Disk.values()) {
    //        if (diningRoom.containsKey(color)){
    //            if (diningRoom.get(color).size()>0){
    //                for (Integer student :
    //                        studentsIdOnDiningRoom) {
    //                    if (student < diningRoom.get(color).size()) {
    //                        studentsIdOnDiningRoom.remove(student); //Does remove the integer student or the Integer with student index ?
    //                        buffStudent.add(diningRoom.get(color).remove(0));
    //                    }
    //                }
    //                studentsIdOnDiningRoom.replaceAll(student -> student - diningRoom.get(color).size());
    //            }
    //        }
    //    }
    //
    //    for (Integer student :
    //            studentsIdOnEntrance) {
    //        diningRoom.get(player.getBoard().getEntrance().get(student).getPawn()).add(player.getBoard().getEntrance().remove((int) student));
    //    }
    //    player.getBoard().getEntrance().addAll(buffStudent);
    //}

    @Override
    public int getCharacterNum(){
        return 10;
    }
}
