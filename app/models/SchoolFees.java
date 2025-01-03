package models;

import com.avaje.ebean.Expr;
import play.db.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.List;

/**
 * Created by SISI on 10/5/2017.
 */
@Entity
public class SchoolFees extends Model {
    @Id
    public long id;
    public int feesAmount;
    public String feesDetails = "";

    @ManyToOne(cascade = CascadeType.ALL)
    public Intake intake;
    public Program program;

    public Boolean deleteStatus = false;
    @ManyToOne(cascade = CascadeType.ALL)
    public  CampusProgramMode programMode;
    @ManyToOne(cascade = CascadeType.ALL)
    public  SessionMode sessionMode;
    public static Finder<Long, SchoolFees> finder = new Finder<Long, SchoolFees>(Long.class, SchoolFees.class);


    public static List<SchoolFees> allBy(String q) {
        String[] arr = q.split(Vld.split);
        if (!q.equals("") && (arr.length <= 1))
            return finder.where().not(Expr.eq("deleteStatus", true)).like("feesAmount", "%" + q + "%").eq("intake.deleteStatus", false).eq("faculty.deleteStatus", false).findList();

        int a = (arr.length > 1 && Vld.isNumeric(arr[1])) ? Integer.parseInt(arr[1]) : 0;
        return allPage(a);
    }

    public static List<SchoolFees> all() {
        return finder.where().not(Expr.eq("deleteStatus", true)).eq("intake.deleteStatus", false).eq("programMode.deleteStatus", false).eq("sessionMode.deleteStatus", false).findList();
    }

    public static List<SchoolFees> byType(String type) {
        return finder.where().not(Expr.eq("deleteStatus", true)).eq("intake.deleteStatus", false).eq("faculty.deleteStatus", false).findList();
    }

    public static List<SchoolFees> byTypeAndId(String type, Long id) {
        return finder.where().not(Expr.eq("deleteStatus", true)).eq("intake.deleteStatus", false).eq("faculty.deleteStatus", false).orderBy("id='" + id + "' desc").findList();
    }

    public static String count() {
        int api = all().size();
        return String.valueOf((api > 0) ? api : 1);
    }

    public static List<SchoolFees> allPage(int a) {
        int ap = (a - 1) * Vld.limit;
        return finder.where().not(Expr.eq("deleteStatus", true)).eq("intake.deleteStatus", false).eq("programMode.deleteStatus", false).eq("sessionMode.deleteStatus", false).setFirstRow(ap).setMaxRows(Vld.limit).orderBy("id desc").findList();
    }

    public static SchoolFees finderById(Long id) {
        return finder.ref(id);
    }
}
