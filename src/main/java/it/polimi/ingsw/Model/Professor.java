package it.polimi.ingsw.Model;

import java.io.Serializable;

/**
 * Every game as only a professor for every student's color/type
 */
public class Professor implements Serializable {
    private final Disk Pawn;
    Professor(Disk pawn){ this.Pawn = pawn; }
    public Disk getPawn(){
        return this.Pawn;
    }


}
