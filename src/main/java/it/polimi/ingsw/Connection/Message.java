package it.polimi.ingsw.Connection;
import java.io.Serializable;

/**
 * Abstract class that represents a message sent from client to server or vice-versa
 *
 * */
public abstract class Message implements Serializable {
    private static final long serialVersionUID = 234567876542345L;
    private String nickname;
    private MessageType messageType;

    public Message(){}

    /**
     * Constructor method for message
     *
     * @param Nickname the nickname of the player to which the message is sent or that receives the message
     * */
    public Message(String Nickname, MessageType messageType){
        this.nickname= Nickname;
        this.messageType= messageType;
    }

    public String getNickname() {
        return nickname;
    }

    /**
     * Getter method that allows to identify immediately the type of message received or that has to be sent,
     * locating the correspondent class extended by the instance message
     *
     * @return ENUM value that corresponds to the type of message
     * */
    public MessageType getMessageType() {
        return messageType;
    }
}