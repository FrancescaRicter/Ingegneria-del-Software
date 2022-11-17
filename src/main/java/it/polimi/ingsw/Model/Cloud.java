package it.polimi.ingsw.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Each cloud contains a list of students, representing the students on the cloud
 */
public class Cloud implements Serializable {
    private List<Student> Students = new ArrayList<Student>();

    public List<Student> getStudents() throws NullPointerException{
         return this.Students;
        }


}
