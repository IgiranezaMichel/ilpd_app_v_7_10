package models;

import Helper.ReportProperty;
import play.db.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@ReportProperty(name = "Student marks report")
public class MarkSheet extends Model {
    @Id
    public long id;

    @ManyToOne(cascade = CascadeType.ALL)
    public Student student;

    @ManyToOne(cascade = CascadeType.ALL)
    public Module module;

    public Integer catResult;
    public Integer reResult;
    public Integer examResult;

    public Boolean deleteStatus = false;

    public static Finder<Long, MarkSheet> find = new Finder<>(Long.class, MarkSheet.class);


    public int totalResult(){
        return (this.examResult+this.catResult+this.reResult);
    }

}
