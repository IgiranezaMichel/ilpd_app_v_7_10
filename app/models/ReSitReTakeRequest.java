package models;

import com.avaje.ebean.Expr;
import play.data.format.Formats;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;
import java.util.List;

@Entity
public class ReSitReTakeRequest extends BaseModel {
    @Id
    public long id;
    public String requestType = "";
    public String status = "pending";
    @Formats.DateTime(pattern = "dd-MM , YYYY")
    public Date requestDate = new Date();
    public String comment = "";
    public String commentRegistrar = "";
    public Boolean deleteStatus = false;
    @ManyToOne
    public Student student;
    @ManyToOne
    public Training training;


    public static Finder<Long, ReSitReTakeRequest> finder = new Finder<Long, ReSitReTakeRequest>(Long.class, ReSitReTakeRequest.class);

    public static List<ReSitReTakeRequest> allBy(String q){
        String[] arr = q.split(Vld.split);
        if( !q.equals("")  && ( arr.length <= 1 ) )
            return finder
                    .where().or(
                            com.avaje.ebean.Expr.like("student.firstName", "%" + q + "%"),
                            com.avaje.ebean.Expr.like("student.familyName",  "%" + q + "%")
                    )
                    .not(Expr.eq("deleteStatus",true))
                    .orderBy("id desc")
                    .findList();

        int a = (arr.length > 1 && Vld.isNumeric(arr[1]) ) ? Integer.parseInt(arr[1]) : 0 ;
        return allPage(a);
    }
    public static List<ReSitReTakeRequest> all(){
        return finder.where()
                .not(Expr.eq("deleteStatus",true))
                .orderBy("id desc")
                .findList();
    }
    public static List<ReSitReTakeRequest> allPendingResit(){
        return finder.where()
                .not(Expr.eq("deleteStatus",true))
                .eq("status", "pending")
                .eq("requestType", "resit")
                .orderBy("id desc")
                .findList();
    }
    public static List<ReSitReTakeRequest> allPendingRetakeModule(){
        return finder.where()
                .not(Expr.eq("deleteStatus",true))
                .eq("status", "pending")
                .eq("requestType", "retake-modules")
                .orderBy("id desc")
                .findList();
    }
    public static List<ReSitReTakeRequest> allPendingRetakeProgram(){
        return finder.where()
                .not(Expr.eq("deleteStatus",true))
                .eq("status", "pending")
                .eq("requestType", "retake-program")
                .orderBy("id desc")
                .findList();
    }
    public static ReSitReTakeRequest resitPerStudent(Long sId){
        return finder.where()
                .not(Expr.eq("deleteStatus",true))
                .eq("requestType", "resit")
                .eq("student.id", sId)
                .orderBy("id desc")
                .setMaxRows(1)
                .findUnique();
    }
    public static ReSitReTakeRequest retakeModulesPerStudent(Long sId){
        return finder.where()
                .not(Expr.eq("deleteStatus",true))
                .eq("requestType", "retake-modules")
                .eq("student.id", sId)
                .orderBy("id desc")
                .setMaxRows(1)
                .findUnique();
    }
    public static ReSitReTakeRequest retakeProgramPerStudent(Long sId){
        return finder.where()
                .not(Expr.eq("deleteStatus",true))
                .eq("requestType", "retake-program")
                .eq("student.id", sId)
                .orderBy("id desc")
                .setMaxRows(1)
                .findUnique();
    }

    public static List<ReSitReTakeRequest> allPage(int a){
        int ap = (a -1) * Vld.limit;
        return finder.where().not(Expr.eq("deleteStatus",true)).setFirstRow(ap).setMaxRows(Vld.limit).orderBy("id desc").findList();
    }

    public static String count(){
        int api = all().size();
        return String.valueOf( (api>0) ? api : 1 );
    }

    public static List<ReSitReTakeRequest> allBy(Long id){
        return finder.where().not(Expr.eq("deleteStatus",true)).orderBy("id='"+id+"' desc").findList();
    }

    public static ReSitReTakeRequest finderById(Long id){
        return finder.ref(id);
    }
    public Boolean exist(){
        ReSitReTakeRequest p = finder.where()
                .not(Expr.eq("id",this.id))
                .eq("deleteStatus",false)
                .eq("requestType",this.requestType)
                .eq("student",this.student)
                .setMaxRows(1)
                .findUnique();
        return ( p != null );
    }
}
