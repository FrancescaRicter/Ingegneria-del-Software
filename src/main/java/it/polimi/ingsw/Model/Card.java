package it.polimi.ingsw.Model;

import it.polimi.ingsw.Exceptions.InvalidInputException;

import java.io.Serializable;

/**
 * Each card contains a Character (ENUM) attribute as representation of the back of the card, every deck of card as the same character,
 * the respective possible mother nature movements, and a priority number for turns identification.
 */
public class Card implements Serializable {
    private Character CardCharacter;
    private int MotherNatureMovement;
    private int PriorityNumber;

    /**
     *
     * @param character back of the card
     * @param MotherNatureMovement maximum mother nature's steps allowed by the card
     * @param PriorityNumber Turn's priority
     * @throws InvalidInputException Lunched when trying to create a card not allowed in the game's rules
     */
    public Card(Character character, int MotherNatureMovement, int PriorityNumber) throws InvalidInputException { 
        if(MotherNatureMovement > 5 || MotherNatureMovement < 0 || PriorityNumber > 10 || PriorityNumber < 0)
            throw new InvalidInputException("Invalid Input");
        else{
            this.PriorityNumber = PriorityNumber;
            this.MotherNatureMovement = MotherNatureMovement;
        }
        if(character!=null)
          {this.CardCharacter = character;}
        else
            throw new InvalidInputException("Invalid input");
    }

    public int getMotherNatureMovement(){
        return this.MotherNatureMovement;
    }

    public int getPriorityNumber(){
        return this.PriorityNumber;
    }

    public Character getCardCharacter(){
        return this.CardCharacter;
    }
}
