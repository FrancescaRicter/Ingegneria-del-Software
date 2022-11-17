package it.polimi.ingsw.Connection.Server.ServerMessage;
import it.polimi.ingsw.Connection.Message;
import it.polimi.ingsw.Connection.MessageType;
import it.polimi.ingsw.Model.Island;

import java.util.List;

/**
 * Message sent from server: sends the updated island list to the player
 */
public class UpdatedIslandsMessage extends Message {
    private static final long serialVersionUID= 787655674L;
    private List<Island> islandList ;

    /**
     * Constructor
     * @param islandList list of all the islands
     */
    public UpdatedIslandsMessage(List<Island> islandList){
        super("To all", MessageType.ISLANDS);
        this.islandList= islandList;

    }

    public List<Island> getIslandList() {
        return islandList;
    }
}
