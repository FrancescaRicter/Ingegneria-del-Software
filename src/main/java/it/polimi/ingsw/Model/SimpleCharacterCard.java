package it.polimi.ingsw.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Every character card that executes only a "simple" effect.
 * Definition of simple effect is every effect that doesn't modify the game rules,
 * but only execute an automatic action without the need of other human interaction.
 */
public abstract class SimpleCharacterCard implements CharacterCard, Serializable {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[40m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    /**
     * True: The Card's effect needs additional parameters to be correctly executed.
     * False: Otherwise.
     */
    protected boolean needsParameters = false;

    /**
     * Optional Attribute, valid only for characters which effects
     * allow the card to place students on top of it.
     */
    protected List<Student> StudentsOnCard = new ArrayList<Student>();

    /**
     * Optional Attribute, string representation for students on top of the card.
     * Developed for cli implementation.
     */
    protected String StudentsOnCardString;

    /**
     * Reference to the game that has this card.
     */
    protected ExpertGame fatherGame;

    /**
     * Initial cost of the card.
     */
    protected int Cost;

    /**
     * After each use of the card's effect, the cost will be 1 + previous cost.
     */
    protected int varCost = 0;

    /**
     * Constructor for every simple card, set fatherGame attribute and Cost in coins
     * @param game
     * @param cost
     */
    public SimpleCharacterCard(ExpertGame game, int cost){
        this.fatherGame = game;
        this.Cost = cost;
    }

    public boolean needPars(){ return needsParameters; }

    public List<Student> getStudentsOnCard(){ return StudentsOnCard; }

    public int getCost(){ return varCost; }

    public int getVarCost(){ return Cost; }

    public ExpertGame getFatherGame() { return fatherGame; }

    /**
     * Each card manage the coins of the card and the player involved in the played effect.
     * @param playerId index of the player in Players list
     */
    public void useCard(int playerId){
        fatherGame.removeCoins(fatherGame.getPlayers().get(playerId), Cost + varCost);
        if (varCost==0)
            varCost++;
    }

    /**
     * Optional Method: Create the string representation of the students on top of cards, if presents.
     * @return String representation for students on card
     */
    public String studIndex(){
        return ANSI_GREEN + "FROG X" + (int) StudentsOnCard.stream().filter(x -> x.getPawn().equals(Disk.FROG)).count() +
                ANSI_RED + " DRAGON X" + (int) StudentsOnCard.stream().filter(x -> x.getPawn().equals(Disk.DRAGON)).count() +
                ANSI_YELLOW + " GNOME X" + (int) StudentsOnCard.stream().filter(x -> x.getPawn().equals(Disk.GNOME)).count() +
                ANSI_PURPLE + " FAIRY X" + (int) StudentsOnCard.stream().filter(x -> x.getPawn().equals(Disk.FAIRY)).count() +
                ANSI_CYAN + " UNICORN X" + (int) StudentsOnCard.stream().filter(x -> x.getPawn().equals(Disk.UNICORN)).count() +
                ANSI_RESET;
    }
}
