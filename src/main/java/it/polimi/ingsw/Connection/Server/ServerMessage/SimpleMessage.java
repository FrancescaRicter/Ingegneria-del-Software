package it.polimi.ingsw.Connection.Server.ServerMessage;

import it.polimi.ingsw.Connection.Message;
import it.polimi.ingsw.Connection.MessageType;

/**
 * Message sent from server : simple text message sent from server to communicate player about updated on the game (ex GAME_TURN)
 *
 * */
public class SimpleMessage extends Message{
        private static final long serialVersionUID = 1232567843;
        private String text;

       /**
        * Constructor
        * @param name nickName of the client that will be receiving the message
        * @param messageType the type of message sent
        * @param text the String text to be displayed for to the client
        * */
        public SimpleMessage(String name,MessageType messageType,String text) {
            super(name, messageType);
            this.text = text;
        }

        public String getText() {
            return text;
        }

}
