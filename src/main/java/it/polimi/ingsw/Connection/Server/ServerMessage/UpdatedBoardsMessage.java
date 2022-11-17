package it.polimi.ingsw.Connection.Server.ServerMessage;

import it.polimi.ingsw.Connection.Message;
import it.polimi.ingsw.Connection.MessageType;
import it.polimi.ingsw.Model.Board;
import java.util.ArrayList;
import java.util.List;

/**
 * Message sent from server: sends the updated list of board to all players in the corresponding game
 */
public class UpdatedBoardsMessage extends Message {
    private static final long serialVersionUID= 1732693456;
    private List<Board> boards = new ArrayList<Board>(); // Boards order must follow the players order

    /**
     *Constructor
     * @param boards list of all the players boards order following playerId order
     */
    public UpdatedBoardsMessage(List<Board> boards){
        super("To all", MessageType.BOARDS);
        this.boards = boards;
    }

    public List<Board> getBoards(){
        return boards;
    }

    public Board getBoard(int playerId){
        return boards.get(playerId);
    }

}
