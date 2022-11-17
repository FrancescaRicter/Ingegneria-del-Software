package it.polimi.ingsw.Observer;

import it.polimi.ingsw.Connection.Message;
import it.polimi.ingsw.Connection.MessageType;
import it.polimi.ingsw.Connection.Server.VirtualView;

import java.util.ArrayList;
import java.util.List;

/**
 * Re-adaptation of the Observable deprecated class
 *
 */
public class Observable {

    private List<Observer> observers = new ArrayList<Observer>();

    public List<Observer> getObservers() {
        return observers;
    }

    /**
     * Method that adds an observer to the list of observers of that Observable
     *
     */
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    /**
     * Method that removes an observer from the list of observers of that Observable
     *
     */
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    /**
     * Method that notifies a change/update to all the observers of that Observable
     *
     * @param messageType the type of message that should be triggered by the change
     */
    public void notifyObservers(MessageType messageType){
        if(observers.size()>0) {
            for (Observer o : observers) {
                o.update(messageType);
            }
        }
    }

    /**
     * Method that notifies a change/update to all the observers of that Observable with a message
     *
     */
    public void notifyObservers(Message message){
        if(observers.size()>0) {
            for (Observer o : observers) {
                o.update(message);
            }
        }
    }

    public void removeObservers(){
        observers.clear();
    }

    /**
     * Method that notifies the single observer (the player) with a message regarding an update in the Observable
     *
     * @param playerId the player's Id
     * @param messageType the type of message that should be sent to the observer
     *
     */
    public void notifyObserver(MessageType messageType, int playerId) {
        if(observers.size()>0) {
            observers.get(playerId).update(messageType);
        }
    }

}

