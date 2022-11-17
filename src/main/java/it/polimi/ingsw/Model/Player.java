package it.polimi.ingsw.Model;

import it.polimi.ingsw.Exceptions.InvalidInputException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Each player has a Board, a Deck which is a List of Card and a List for played cards,
 * a string for player's nickname.
 */
public class Player implements Serializable {
    private String Nickname;
    private Board Board = new Board();
    private List<Card> Deck = new ArrayList<>();
    private List<Card> PlayedCards = new ArrayList<>();

    /**
     * Usually the player is created with every new game with and empty string,
     * later will be set by the player joining a game.
     * @param nickname
     */
    public Player(String nickname) {
        Nickname = nickname;
    }

    /**
     *
     * @param nickname the real player's nickname
     * @throws NullPointerException Trying to assign the player name without having previously created it with an empty string
     */
    public void setNickname(String nickname) throws NullPointerException {
        if (nickname == null)
            throw new NullPointerException();
        this.Nickname = nickname;
    }

    public List<Card> getPlayedCards(){ return PlayedCards; }

    public String getNickname() {
        return this.Nickname;
    }

    public Board getBoard() {
        return this.Board;
    }

    public List<Card> getDeck() { return this.Deck; }
    /**
     * When a player is created an empty deck is assigned to the player,
     * when the player choose his deck's character, the new deck is created.
     * @param character Player's character chosen.
     */
    public void initializeDeck(Character character) {
        int movements;
        for (int priority = 1; priority < 11; priority++){
            movements = priority / 2;
            if (priority % 2 != 0)
                movements++;
            try {
                Deck.add(new Card(character, movements, priority));
            } catch (InvalidInputException e) {
                e.printStackTrace();
            }
        }
    }
}
