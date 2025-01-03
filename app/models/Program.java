package models;

import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.annotation.Formula;
import com.fasterxml.jackson.annotation.JsonProperty;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

/**
 * Created by SISI on 9/28/2017.
 */
@Entity
public class Program extends Model
{
    @Id
    public long id;
    public boolean cle = false;
    public String programName = "";
    public String programAcronym = "";
    public Boolean deleteStatus = false;


    public static Finder<Long, Program> finder = new Finder<>(Long.class, Program.class);

    public List<CampusProgramMode> myModes()
    {
        return CampusProgramMode.find.where().eq("deleteStatus", false).eq("campusProgram.program.id", this.id).findList();
    }

    public static List<Program> allBy(String q)
    {
        String[] arr = q.split(Vld.split);
        if (!q.equals("") && (arr.length <= 1))
        {
            return query().icontains("programName", q).orderBy("id desc").findList();
        }

        int a = (arr.length > 1 && Vld.isNumeric(arr[1])) ? Integer.parseInt(arr[1]) : 0;
        return allPage(a);
    }

    public static String count()
    {
        int api = all().size();
        return String.valueOf((api > 0) ? api : 1);
    }

    public static List<Program> allPage(int a)
    {
        int ap = (a - 1) * 10;
        return query().setFirstRow(ap).setMaxRows(10).orderBy("id desc").findList();
    }

    public static List<Program> all()
    {
        return query().orderBy("id desc").findList();
    }

    public static List<Program> all(boolean cle)
    {
        return query().eq("cle",cle).orderBy("id desc").findList();
    }

    private static ExpressionList<Program> query(){
        return finder.where().ne("deleteStatus",true);
    }

    public static List<Program> allBy(Long id)
    {
        return query().orderBy("id='" + id + "' desc").findList();
    }

    public static List<Program> allByCamp(Long id)
    {
        return query().findList();
    }

    public int myStudents()
    {
        return Student.finder.where().eq("training.iMode.campusProgram.program.id", this.id).findRowCount();
    }

    public static Program finderById(Long id)
    {
        return finder.ref(id);
    }

    @Override
    public String toString()
    {
        return programName + " ( " + programAcronym + " ) ";
    }

    @JsonProperty
    public String print(){
        return this.toString();
    }
}
