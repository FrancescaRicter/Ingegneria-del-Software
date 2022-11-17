package it.polimi.ingsw.Connection.Server.ServerMessage;

import it.polimi.ingsw.Connection.Message;
import it.polimi.ingsw.Connection.MessageType;
import it.polimi.ingsw.Model.Cloud;

import java.util.List;

/**
 * Message sent from server: sends the updated list of clouds to all players in the corresponding game
*/
public class UpdatedCloudsMessage extends Message {
    private static final long serialVersionUID= 2065557767;
    private List<Cloud> clouds;

/**
 * Constructor
 * @param clouds list of updated clouds
*/
 public UpdatedCloudsMessage(List<Cloud> clouds){
        super("To all", MessageType.CLOUDS);
        this.clouds= clouds;
    }
    public List<Cloud> getClouds() {
        return clouds;
    }
}
