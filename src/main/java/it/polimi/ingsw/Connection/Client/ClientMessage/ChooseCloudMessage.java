package it.polimi.ingsw.Connection.Client.ClientMessage;

import it.polimi.ingsw.Connection.Message;
import it.polimi.ingsw.Connection.MessageType;

/**
 * Message sent from client : the cloud from which the player chooses to refill the entry with students
 *
 * */
public class ChooseCloudMessage extends Message {
    private static final long serialVersionUID = 4352513L;
    private int cloudId;

    /**
     * Constructor
     *
     * @param nickname  client nickname
     * @param cloudId the index of the cloud
     * */
    public ChooseCloudMessage(String nickname,int cloudId) {
        super(nickname, MessageType.CHOOSE_CLOUD);
        this.cloudId = cloudId;
    }

    public int getCloudId() {
        return cloudId;
    }
}
