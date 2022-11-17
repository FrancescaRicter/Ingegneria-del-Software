package it.polimi.ingsw.Exceptions;

/**
 * The class InvalidInputException is a form of throwable that indicates that the method cannot be called with the given input
 *
 * */
public class InvalidInputException extends Exception {
   public InvalidInputException(String errorMessage)
                                          {super(errorMessage);}

}
