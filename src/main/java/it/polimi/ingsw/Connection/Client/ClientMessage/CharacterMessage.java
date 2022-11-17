package it.polimi.ingsw.Connection.Client.ClientMessage;

import it.polimi.ingsw.Connection.Message;
import it.polimi.ingsw.Connection.MessageType;

/**
 * Message sent from client : character chosen for his deck
 *
 * */
public class CharacterMessage extends Message {
    private static final long serialVersionUID = 835251873;
    private String character;

    /**
     * Constructor
     *
     * @param nickname  client nickname
     * @param character the character that will be display on his deck
     * */
    public CharacterMessage(String nickname, String character){
        super(nickname, MessageType.CHARACTER_SETUP);
        this.character = character;
    }

    public String getCharacter() {
        return character;
    }
}