package it.polimi.ingsw.Connection.Client.ClientMessage;

import it.polimi.ingsw.Connection.Message;
import it.polimi.ingsw.Connection.MessageType;

/**
 * Message sent from client : the client requests to move a student from the entry on his board to dining room
 *
 * */
public class MoveStudentOnDiningRoomMessage extends Message {
    private static final long serialVersionUID = 564574;
    private int student;

    /**
     * Constructor
     *
     * @param nickname  client nickname
     * @param student id of the student pawn in the entry from which the student will be removed
     * */
    public MoveStudentOnDiningRoomMessage(String nickname,int student){
        super(nickname, MessageType.MOVE_STUDENT_DINING_ROOM);
        this.student = student;
    }

    public int getStudent() {
        return student;
    }
}
