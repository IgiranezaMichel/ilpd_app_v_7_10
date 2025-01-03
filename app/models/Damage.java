package models;

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
public class Damage extends Model {
    @Id
    public Long id;
    @ManyToOne(cascade = CascadeType.ALL)
    public Student student;
    public String name="";
    public String description="";
    public Double amount;
    public Boolean clear = false;
    public Boolean deleteStatus = false;
    public Date date=new Date();
    public static Finder<Long, Damage> finder = new Finder<>(Long.class, Damage.class);

    public static Damage finderById(Long id){
        return finder.ref(id);
    }


    public static List<Damage> all(){
        return finder.where().eq("deleteStatus",false).findList();
    }

    public static List<Damage> notClearedStudent(){
        return finder.where().eq("deleteStatus",false).eq("clear",false).findList();
    }
    public static List<Damage> clearedStudent(){
        return finder.where().eq("deleteStatus",false).eq("clear",true).findList();
    }
    public static double totalDamage(long stId) {
        String s = "SELECT IFNULL(sum(d.amount), 0) AS amount FROM damage d INNER JOIN student s ON s.id = d.student_id WHERE s.id=:sId";
        SqlRow row = Ebean.createSqlQuery(s)
                .setParameter("sId", stId).findUnique();
        return row.getDouble("amount");
    }
}
