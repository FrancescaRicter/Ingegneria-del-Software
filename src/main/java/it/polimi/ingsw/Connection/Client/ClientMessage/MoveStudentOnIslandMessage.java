package it.polimi.ingsw.Connection.Client.ClientMessage;

import it.polimi.ingsw.Connection.Message;
import it.polimi.ingsw.Connection.MessageType;

/**
 * Message sent from client : the client requests to move a student from the entry on his board to the  indicated island
 *
 * */
public class MoveStudentOnIslandMessage extends Message {
    private static final long serialVersionUID = 564574;
    private int student;
    private int islandPosition;

    /**
     * Constructor
     *
     * @param nickname  client nickname
     * @param student id of the student pawn in the entry from which the student will be removed
     * @param islandPosition id opf the island on which the student will be placed
     * */
    public MoveStudentOnIslandMessage(String nickname, int student, int islandPosition){
        super(nickname, MessageType.MOVE_STUDENT_ISLAND);
        this.student=student;
        this.islandPosition = islandPosition;
    }

    public int getIslandPosition() {
        return islandPosition;
    }

    public int  getStudent() {
        return student;
    }
}
