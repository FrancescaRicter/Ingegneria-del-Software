package it.polimi.ingsw.Connection.Client.ClientMessage;
import it.polimi.ingsw.Connection.Message;
import it.polimi.ingsw.Connection.MessageType;

/**
 * Message sent from client : the client requests to move mother nature forward of the indicated number of steps
 *
 * */
public class MoveMotherNatureMessage extends Message {
    private static final long serialVersionUID = 239825;
    private int motherNatureSteps;

    /**
     * Constructor
     *
     * @param nickname  client nickname
     * @param motherNatureSteps the number of steps mother nature will be moved in clockwise direction
     * */
    public  MoveMotherNatureMessage(String nickname,int motherNatureSteps){
        super(nickname, MessageType.MOVE_MOTHER_NATURE);
        this.motherNatureSteps = motherNatureSteps;
    }

    public int getMotherNatureSteps() {
        return motherNatureSteps;
    }
}
