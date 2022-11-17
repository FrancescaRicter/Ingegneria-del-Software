package it.polimi.ingsw.Connection.Server.ServerMessage;
import it.polimi.ingsw.Connection.Message;
import it.polimi.ingsw.Connection.MessageType;
import it.polimi.ingsw.Model.Character;
import it.polimi.ingsw.Model.TowerColor;

import java.util.ArrayList;
import java.util.List;

/**
 * Message sent from server : notifies the beginning of the tower selection phase or the character phase,
 * and sends the updated lists everytime requested by client
 *
 * */
public class InitGameMessage extends Message {
    private static final long serialVersionUID = 1067575345;
    private List<TowerColor> towerColorList;
    private List<Character> characterList;
    private List<String> choices = new ArrayList<>();

    /**
     * Constructor for message in 2-3 player mode
     *
     * @param name player Nickname to whom the message will be sent or id of the lobby to which this message will be broadcast
     * @param messageType the type of message sent (INIT_TOWER - INIT_CHARACTER)
     *
     * */
    public InitGameMessage(String name,MessageType messageType,List<TowerColor> towerColor, List<Character> characterList) {
        super(name, messageType);
        this.towerColorList = towerColor;
        this.characterList = characterList;
    }

    /**
     * Constructor for message in the case of 4 players mode (teams)
     *
     * @param name player Nickname to whom the message will be sent or id of the lobby to which this message will be broadcast
     * @param messageType the type of message sent (INIT_TOWER - INIT_CHARACTER)
     *
     * */
    public InitGameMessage(String name,MessageType messageType,List<TowerColor> towerColor, List<Character> characterList,List<String> playersSet,List<TowerColor> towersChosen){
        super(name, messageType);
        this.towerColorList= towerColor;
        this.characterList = characterList;
        for (int i=0; i<playersSet.size() || i<towersChosen.size();i++){
            choices.add( playersSet.get(i)+" has chosen " + towersChosen.get(i));
        }

    }

    public List<String> getChoices() {
        return choices;
    }

    public List<TowerColor> getTowerColorList() {
        return towerColorList;
    }

    public List<Character> getCharacterList() {
        return characterList;
    }
}
