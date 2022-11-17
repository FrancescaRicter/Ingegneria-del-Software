package it.polimi.ingsw.Exceptions;

/**
 * The class InitGameException is a form of throwable that indicates that the action cannot be performed with the given inputs,
 * because these do not correspond to a possible phase of the game
 *
 * */
public class InitGameException extends Exception {
    public InitGameException(String errorMessage)
                          {super(errorMessage);}
}

