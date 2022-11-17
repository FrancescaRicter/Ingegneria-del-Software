package it.polimi.ingsw.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Each board contains a list of student as Entrance, a list of Professors, TowerColor ENUM, cardinality of towers on the board (int),
 * Dining Room is a map of Disk (color/type of students) to List of Students of the respective color/type
 */
public  class Board implements Serializable {
    private ArrayList<Student> Entrance = new ArrayList<Student>();
    public  Map<Disk, List<Student>> DiningRoom = new HashMap<>() ;
    private List<Professor> Professors = new ArrayList<Professor>();
    private TowerColor TowerColor;
    private int NumTowers;

    /**
     * Constructor: set dining room map for each student type
     */
    public Board(){
        for (Disk d :
                Disk.values()) {
            DiningRoom.put(d, new ArrayList<Student>());
        }
    }

    /**
     * clears towers on the boards (NumTower=0)
     */
    public void removeTowers(){
        NumTowers=0;
    }

    /**
     *
     * @param professor add to List of professors
     */
    public void addProfessor(Professor professor) {
        Professors.add(professor);
    }

    public List<Professor> getProfessors(){ return this.Professors; }

    public List<Student> getEntrance(){ return this.Entrance; }

    public void setNumTowers(int numTowers){ this.NumTowers = numTowers; }

    public int getNumTowers(){ return NumTowers; }

    public void setTowerColor(TowerColor towerColor){ this.TowerColor = towerColor;}

    public TowerColor getTowerColor(){return this.TowerColor; }

    public Map<Disk, List<Student>> getDiningRoom() { return DiningRoom; }

}
