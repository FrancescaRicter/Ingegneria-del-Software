package it.polimi.ingsw.Connection.Server.ServerMessage;
import it.polimi.ingsw.Connection.Message;
import it.polimi.ingsw.Connection.MessageType;

/**
 * Message sent from server : notifies an accured error
 *
 * */
public class ErrorMessage extends Message {
    private static final long serialVersionUID = 123234523;
    private String error;

    /**
     * Constructor message for an error
     *
     * */
    public ErrorMessage(String nickname,String error) {
        super(nickname, MessageType.ERROR);
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
