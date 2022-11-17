package it.polimi.ingsw.Connection.Server.ServerMessage;
import it.polimi.ingsw.Connection.Message;
import it.polimi.ingsw.Connection.MessageType;

/**
 * Message sent from server : notifies the winner and losers on the outcome of the match once the game has ended
 *
 * */
public class EndGameMessage extends Message {
    private static final long serialVersionUID = 45164340;
    private String winnerNickname;
    private String message;

    /**
     * Constructor for message that will be sent to players that have lost the game
     *
     * @param nickName nickName of the player to which the message is going to be sent
     * @param winnerNickname  nickName of the winner of the game
     * */
    public EndGameMessage(String nickName, String winnerNickname) {
        super(nickName, MessageType.LOSS);
        this.winnerNickname = winnerNickname;
        message = "YOU HAVE LOST THE GAME! THE WINNER IS " + winnerNickname;
    }

    /**
     * Constructor for message that will be sent to players that have won the game
     *
     * @param nickName nickName of the player to which the message is going to be sent that is at the same time the winner
     * */
    public EndGameMessage(String nickName){
        super(nickName,MessageType.WIN);
        message = "CONGRATULATIONS YOU HAVE WON THE GAME!";
    }

    public String getWinnerNickname() {
        return winnerNickname;
    }
}
