package it.polimi.ingsw.Observer;

import it.polimi.ingsw.Connection.Message;
import it.polimi.ingsw.Connection.MessageType;


/**
 * Interface that is a re-adaptation of the deprecated Observer interface
 *
 */
public interface Observer {

    void update(Message message);

    void update(MessageType messageType);
}