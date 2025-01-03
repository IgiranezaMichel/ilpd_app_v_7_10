package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class EvQuestion extends Model {
    @Id
    public long id;

    @ManyToOne
    public EvCategory category;

    public String question = "";

    public boolean textarea=false;


    public static Finder<Long, EvQuestion> finder = new Finder<>(Long.class, EvQuestion.class);


    public String select(){
        return textarea ? "Text" : "Select";
    }

}
