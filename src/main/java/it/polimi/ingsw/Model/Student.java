package it.polimi.ingsw.Model;

import java.io.Serializable;

public class Student implements Serializable {
    private final Disk Pawn;

    Student(Disk pawn){
        this.Pawn = pawn;
    }
    public Disk getPawn() { return this.Pawn; }
}
