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
 * Created by SISI on 10/10/2017.
 */
@Entity
public class AcademicFiles extends Model
{
    @Id
    public long id;

    @ManyToOne(cascade = CascadeType.ALL)
    public Program program;

    public String fileName = "";
    public String uniqueName = "";
    public String description = "";

    public Boolean required = true;


    @ManyToOne(cascade = CascadeType.ALL)
    public Session session;

    public Boolean deleteStatus = false;

    public static Finder<Long, AcademicFiles> find = new Finder<Long, AcademicFiles>(Long.class, AcademicFiles.class);

    public static List<AcademicFiles> all()
    {
        return find.where().not(Expr.eq("deleteStatus", true)).eq("program.deleteStatus", false).findList();
    }

    public static List<AcademicFiles> byPro(Long id)
    {
        return find.where().not(Expr.eq("deleteStatus", true)).eq("program.id", id).findList();
    }

    public static List<AcademicFiles> byProgram(Long id, Long appId)
    {
        Applied app = Applied.byApp(appId);
        return find.setDistinct(true).where()
                .not(Expr.eq("deleteStatus", true))
                .eq("program.id", id)
                .eq("session.id", app.training.iMode.sessionMode.session.id)
                .orderBy("id desc")
                .findList();
    }
    public static List<AcademicFiles> byApplicant( Long appId)
    {
        Applied app = Applied.byApp(appId);
        List<AcademicFiles> academicFiles = AcademicFiles.find.where("deleteStatus='false' and session.id ="+app.training.iMode.sessionMode.session.id+" group by id").findList();
        return academicFiles;
    }

    public static List<AcademicFiles> byProBy(Long id, String q)
    {
        String[] arr = q.split(Vld.split);
        if (!q.equals("") && (arr.length <= 1))
        {
            return find.where().not(Expr.eq("deleteStatus", true)).like("fileName", "%" + q + "%").eq("faculty.id", id).findList();
        }

        int a = (arr.length > 1 && Vld.isNumeric(arr[1])) ? Integer.parseInt(arr[1]) : 0;
        return allPage(a, id);
    }

    public static List<AcademicFiles> allP(int a)
    {
        int ap = (a - 1) * Vld.limit;
        return find.where()
                .not(Expr.eq("deleteStatus", true))
                .setFirstRow(ap).setMaxRows(Vld.limit).orderBy("id desc")
                .findList();
    }

    public static List<AcademicFiles> allPage(int a, Long id)
    {
        int ap = (a - 1) * Vld.limit;
        return find.where().not(Expr.eq("deleteStatus", true)).eq("program.id", id).setFirstRow(ap).setMaxRows(Vld.limit).orderBy("id desc").findList();
    }

    public static String count()
    {
        int api = all().size();
        return String.valueOf((api > 0) ? api : 1);
    }

    public static List<AcademicFiles> allActive(Long id)
    {
        return find.where().eq("deleteStatus", true).eq("program.id", id).findList();
    }

    public static AcademicFiles finderById(Long id)
    {
        return find.byId(id);
    }
    @JsonProperty
    public String print(){
        return fileName;
    }

}
