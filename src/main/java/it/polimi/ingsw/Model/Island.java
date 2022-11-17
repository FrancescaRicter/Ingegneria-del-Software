package it.polimi.ingsw.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Each Island contains a list of students, number of towers on it and the respective color (ENUM) if presents.
 * Mother Nature is represented as a boolean.
 */
public class Island implements Serializable {
    private boolean MotherNature;
    private int NumTowers;
    private TowerColor TowerColor;
    private List<Student> Students = new ArrayList<>();

    public Island(){}

    /**
     * Constructor for merge 2 islands in 1 with a combination of attributes on both initial islands.
     * Order in parameters are not determinant for the original scope.
     * @param island1
     * @param island2
     */
    public Island(Island island1, Island island2){
        this.MotherNature = island1.MotherNature || island2.MotherNature;
        this.NumTowers = island1.NumTowers + island2.NumTowers;
        this.TowerColor = island1.TowerColor;
        this.Students = island1.getStudents();
        this.Students.addAll(island2.getStudents());
    }

    /**
     *
     * @return List of student's colors presents in this island, empty list if no student is on this island
     */
    public List<Disk> getPawnsTypeOnIsland(){
        List<Disk> ColorsOnIsland= new ArrayList<>();
        for (Student student :
                Students) {
            if (!ColorsOnIsland.contains(student.getPawn()))
                ColorsOnIsland.add(student.getPawn());
        }
        return ColorsOnIsland;
    }

    /**
     *
     * @param pawn type/color of the student trying to search on this island.
     * @return Number of students on this island with pawn color, 0 if none is present.
     */
    public int getNumOfStudentsOfType(Disk pawn){
        return (int) Students.stream().filter(x -> x.getPawn().equals(pawn)).count();
    }

    /**
     * Mother Nature setter
     * @param b true for Mother Nature present, false otherwise.
     */
    public void setMotherNature(boolean b){ this.MotherNature = b; }

    public boolean hasMotherNature(){ return this.MotherNature; }

    public int getNumTowers() { return NumTowers; }

    public void setNumTowers(int numTowers){ this.NumTowers = numTowers; }

    public TowerColor getTowerColor(){ return this.TowerColor; }

    public void setTowerColor(TowerColor towerColor){ this.TowerColor = towerColor; }

    /**
     * Add a single student on this island.
     * @param student
     */
    public void addStudent(Student student){ this.Students.add(student); }

    public List<Student> getStudents(){ return this.Students; }
}
