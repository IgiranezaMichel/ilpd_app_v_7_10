package models;

import Helper.ReportProperty;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Date;
@Entity
@ReportProperty(name = "Internship report")
public class Internship extends BaseModel
{
    public String description;
    public String attachment;
    @ManyToOne
    public Student student;
    public Date date = new Date();

    public static Model.Finder<Long, Internship> finder = new Finder<>(Long.class, Internship.class);

}
