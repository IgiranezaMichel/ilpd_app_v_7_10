package models;

import com.avaje.ebean.Expr;
import com.fasterxml.jackson.annotation.JsonProperty;
import play.db.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.List;

/**
 * Created by SISI on 10/3/2017.
 */
@Entity
public class Intake extends Model {
    @Id
    public long id;
    public String intakeName = "";
    public Boolean isClosed = false;

    @ManyToOne(cascade = CascadeType.ALL)
    public AcademicYear year;

    public double registrationFees = 0.0;

    public Boolean deleteStatus = false;


    public static Model.Finder<Long, Intake> finder = new Finder<>(Long.class, Intake.class);

    public String display(){
        return this.intakeName;
    }

    public static List<Intake> bySession(Long id){
        return finder.where().eq("deleteStatus",false).eq("session.id",id).findList();
    }
    public static List<Intake> allBy(String q){
        String[] arr = q.split(Vld.split);
        if( !q.equals("")  && ( arr.length <= 1 ) )
            return finder.where().not(Expr.eq("deleteStatus",true)).like("intakeName","%"+q+"%").eq("year.deleteStatus",false).orderBy("id desc").findList();

        int a = (arr.length > 1 && Vld.isNumeric(arr[1]) ) ? Integer.parseInt(arr[1]) : 0 ;
        return allPage(a);
    }

    public static List<Intake> allPage(int a){
        int ap = (a -1) * Vld.limit;
        return finder.where().not(Expr.eq("deleteStatus",true)).eq("year.deleteStatus",false).setFirstRow(ap).setMaxRows(Vld.limit).orderBy("id desc").findList();
    }

    public static String count(){
        int api = all().size();
        return String.valueOf( (api>0) ? api : 1 );
    }

    public static List<Intake> all(){
        return finder.where().not(Expr.eq("deleteStatus",true)).orderBy("id desc").findList();
    }
    public static Intake finderById(Long id){
        return finder.ref(id);
    }

    @Override
    public String toString()
    {
        return intakeName + (this.year!=null ? "-"+year.yearName:"");
    }

    @JsonProperty
    public String print(){
        return toString();
    }
}
