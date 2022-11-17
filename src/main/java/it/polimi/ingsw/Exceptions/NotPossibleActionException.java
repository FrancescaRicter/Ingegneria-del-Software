package it.polimi.ingsw.Exceptions;

/**
 * The class NotPossibleActionException is a form of throwable that indicates that a method cannot be called,
 * because it would trigger an action not possible in the game in that stage
 *
 * */
public class NotPossibleActionException extends Exception {
    public NotPossibleActionException(String errorMessage)
    {super(errorMessage);}
}


