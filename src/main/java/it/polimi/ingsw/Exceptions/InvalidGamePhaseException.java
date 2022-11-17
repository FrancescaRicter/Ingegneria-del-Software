package it.polimi.ingsw.Exceptions;

/**
 * The class InvalidGamePhaseException is a form of throwable that indicates that the method called with the given inputs would perform an invalid
 * action for the phase of the game in which the player is set
 *
 * */
public class InvalidGamePhaseException extends Exception {
    public InvalidGamePhaseException() {
        super("You can't perform this move at this stage of game!");
    }
}
