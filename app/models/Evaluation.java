package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import play.data.validation.Constraints;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Date;
import java.util.List;

@Entity
public class Evaluation extends BaseModel
{
    @ManyToOne
    private Lecture lecture;
    @ManyToOne
    public Student student;
    @Constraints.Max(10)
    public float mark;

    public String answer="";


    @ManyToOne (cascade = CascadeType.ALL)
    public Schedule schedule;

    @ManyToOne (cascade = CascadeType.ALL)
    public EvQuestion question;


    public Date date = new Date();
    public static Finder<Long, Evaluation> finder = new Finder<>(Long.class, Evaluation.class);

    public static List<Evaluation> allNotEvaluated() {
        return finder.setDistinct(true).where()
                .eq("student", null)
                .findList();
    }
    public static List<Evaluation> allEvaluated(long lecId) {
        return finder.where()
                .eq("question.id", null)
                .eq("schedule.id", null)
                .eq("answer", null)
                .eq("lecture.id", lecId)
                .findList();
    }

    public static String getPoint(Lecture lecture, Student student)
    {
        Evaluation evaluation = Evaluation.finder.where()
                .eq("student.id", student.id)
                .eq("lecture.id", lecture.id)
                .findUnique();
        return evaluation == null ? "Not evaluated" : String.valueOf(evaluation.mark);
    }
    public static boolean isEvaluated(Schedule schedule, Student student)
    {
        return Evaluation.finder.where()
                .eq("student.id", student.id)
                .eq("schedule.id", schedule.id)
                .findRowCount() > 0;
    }

    public boolean exist(){
        return finder.where().eq("student.id",student.id)
                .eq("schedule.id",schedule.id)
                .eq("question.id",question.id)
                .findRowCount() > 0;
    }

    public static int countModule(long id,long mId,long tr,int mark){
        return countQuery2(id,mId,tr)
                .eq("mark",mark)
                .eq("question.id",id).findRowCount();
    }

    public static int count(long id,long lecture,long comp,long tr,int mark){
        return countQuery(id,lecture,comp,tr)
                .eq("mark",mark)
                .eq("question.id",id).findRowCount();
    }

    public static List<Evaluation> list(long id, long lecture, long comp, long tr){
        return countQuery(id,lecture,comp,tr)
                .eq("question.id",id).findList();
    }

    public static List<Evaluation> listModule(long id, long mod, long tr){
        return countQuery2(id,mod,tr)
                .eq("question.id",id).findList();
    }

    private static ExpressionList<Evaluation> countQuery(long id, long lecture, long comp, long tr){
        return finder.where()
                .eq("schedule.lecture.id",lecture)
                .eq("schedule.component.id",comp)
                .eq("schedule.training.id",tr)
                .eq("question.id",id);
    }

    private static ExpressionList<Evaluation> countQuery2(long id, long mod, long tr){
        return finder.where()
                .eq("schedule.component.module.id",mod)
                .eq("schedule.training.id",tr)
                .eq("question.id",id);
    }

    public static int count(long id,long lecture,long comp,long tr){
        return countQuery(id,lecture,comp,tr).eq("question.id",id).findRowCount();
    }

    public static int countMod(long id,long mid,long tr){
        return countQuery2(id,mid,tr).eq("question.id",id).findRowCount();
    }

    public static int sum(long id,long lecture,long comp,long tr){
        String sql = "SELECT IFNULL(sum(e.mark),0) as sum FROM evaluation e INNER JOIN schedule s ON e.schedule_id=s.id WHERE s.lecture_id=:lId AND s.component_id=:c AND s.training_id=:tId AND e.question_id=:q";
        return Ebean.createSqlQuery(sql)
                .setParameter("q",id)
                .setParameter("lId",lecture)
                .setParameter("c",comp)
                .setParameter("tId",tr)
                .findUnique()
                .getInteger("sum");
    }
    public static int sumModule(long qId,long mid,long tr){
        String sql = "SELECT IFNULL(sum(e.mark),0) as sum FROM evaluation e INNER JOIN schedule s ON e.schedule_id=s.id inner join component comp on s.component_id = comp.id WHERE comp.module_id=:c AND e.question_id=:q AND s.training_id=:tId";
        return Ebean.createSqlQuery(sql)
                .setParameter("q",qId)
                .setParameter("c",mid)
                .setParameter("tId",tr)
                .findUnique()
                .getInteger("sum");
    }
    public static int avg(long id,long lecture,long comp,long tr){
        int count = count(id, lecture, comp, tr);
        count = count > 0 ? count : 1;
        return sum(id,lecture,comp,tr) / count;
    }

    public static int avgModule(long id,long mId,long tr){
        int count = countMod(id, mId, tr);
        count = count > 0 ? count : 1;
        return sumModule(id,mId,tr) / count;
    }

    public String sNames(){
        return student != null ? student.print() : "";
    }
}
