package models;

import Helper.ReportProperty;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;
import play.db.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;
import java.util.List;

@Entity
@ReportProperty(name = "Student attendance report")
public class Attendance extends Model
{
    @Id
    public long id;

    @ManyToOne(cascade = CascadeType.ALL)
    public Schedule schedule;
    @ManyToOne(cascade = CascadeType.ALL)
    public Component component;
    @ManyToOne(cascade = CascadeType.ALL)
    public Student student;
    public Boolean attended = true;
    public Boolean attendedp = true;
    public String comment = "";
    public Boolean claimed = false;
    public Date date = new Date();
    public Boolean deleteStatus = false;

    public static Model.Finder<Long, Attendance> find = new Model.Finder<>(Long.class, Attendance.class);


    public static boolean componentWasAttended(long cId, long sId)
    {
        String s="SELECT a.id as id FROM attendance a INNER JOIN component c ON a.component_id = c.id INNER JOIN schedule s ON a.schedule_id = s.id WHERE c.id=:cId AND s.id=:sId LIMIT 1";
       SqlRow sqlRow= Ebean.createSqlQuery(s).setParameter("cId",cId).setParameter("sId",sId).findUnique();
        return sqlRow != null;
    }

    public List<Module> myModules(){
        return Module.finder.setDistinct(true).where().eq("deleteStatus",false).eq("id",this.component.module.id).orderBy("id desc").findList();
    }
}
