package models;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import controllers.Counts;
import play.db.ebean.Model;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Entity
public class AssignmentResult extends Model {
    @Id
    public long id;
    public Double result = 0.0;
    public float cliamResult = 0;
    public String comment = "";
    public String commentLecrurer = "";

    @ManyToOne
    public Assignment assignment;

    @ManyToOne
    public Student student;
    public String doneBy = "";

    public Boolean isResitResult = false;
    public boolean deleteStatus=false;
    public Timestamp doneAt = new Timestamp(new Date().getTime());

    public static Finder<Long, AssignmentResult> find = new Finder<>(Long.class, AssignmentResult.class);
    public static AssignmentResult finderById(Long id)
    {
        return find.ref(id);
    }
    public static List<AssignmentResult> all(){
        return find.where()
                .findList();
    }
    public static List<AssignmentResult> allByStudentAndComponent(Long sId, Long cId){
        return find.setDistinct(true).where()
                .eq("deleteStatus", false)
                .eq("student.deleteStatus", false)
                .eq("assignment.deleteStatus", false)
                .eq("assignment.component.deleteStatus", false)
                .eq("student.id", sId)
                .eq("assignment.component.id", cId)
                .findList();
    }
    public static List<AssignmentResult> allByStudentAndModule(Long sId, Long mId){
        return find.setDistinct(true).where()
                .eq("deleteStatus", false)
                .eq("student.deleteStatus", false)
                .eq("assignment.deleteStatus", false)
                .eq("assignment.component.deleteStatus", false)
                .eq("student.id", sId)
                .eq("assignment.component.module.id", mId)
                .findList();
    }
    public static double studentAssignmentResult(long sId, long aIs) {
        String s = "SELECT IFNULL(ar.result,0.0) as total FROM assignment_result ar INNER JOIN assignment a ON ar.assignment_id=a.id INNER JOIN student s ON ar.student_id=s.id INNER JOIN component c ON a.component_id = c.id INNER JOIN module m ON c.module_id = m.id WHERE s.id=:sid AND a.id=:aid";
        double total = Ebean.createSqlQuery(s)
                .setParameter("sid", sId)
                .setParameter("aid", aIs)
                .setMaxRows(1)
                .findUnique()
                .getDouble("total");
        return total;
    }
    public static double totalMarks(long sId, long cId) {
        String s = "SELECT IFNULL(SUM(ar.result),0.0) as total FROM assignment_result ar INNER JOIN assignment a ON ar.assignment_id=a.id INNER JOIN student s ON ar.student_id=s.id INNER JOIN component c ON a.component_id = c.id INNER JOIN module m ON c.module_id = m.id WHERE s.id=:sid AND c.id=:cid AND a.types =\"assignment\" and c.delete_status = false";
        double total = Ebean.createSqlQuery(s)
                .setParameter("sid", sId)
                .setParameter("cid", cId)
                .findUnique()
                .getDouble("total");
        return total;
    }
    public static double totalMarksM(long sId, long mId) {
        String s = "SELECT IFNULL(SUM(ar.result),0.0) as total FROM assignment_result ar INNER JOIN assignment a ON ar.assignment_id=a.id INNER JOIN student s ON ar.student_id=s.id INNER JOIN component c ON a.component_id = c.id INNER JOIN module m ON c.module_id = m.id WHERE s.id=:sid AND m.id=:cid AND a.types =\"assignment\" and c.delete_status = false";
        double total = Ebean.createSqlQuery(s)
                .setParameter("sid", sId)
                .setParameter("cid", mId)
                .findUnique()
                .getDouble("total");
        return total;
    }
    public static double totalAssignmentMax(long sId, long cId) {
        String s = "SELECT IFNULL(SUM(a.max),0.0) as total FROM assignment_result ar INNER JOIN assignment a ON ar.assignment_id=a.id INNER JOIN student s ON ar.student_id=s.id INNER JOIN component c ON a.component_id = c.id INNER JOIN module m ON c.module_id = m.id WHERE s.id=:sid AND c.id=:cid AND a.types =\"assignment\" and c.delete_status = false";
        double total = Ebean.createSqlQuery(s)
                .setParameter("sid", sId)
                .setParameter("cid", cId)
                .findUnique()
                .getDouble("total");
        return total;
    }
    public static double totalAssignmentMaxM(long sId, long mId) {
        String s = "SELECT IFNULL(SUM(a.max),0.0) as total FROM assignment_result ar INNER JOIN assignment a ON ar.assignment_id=a.id INNER JOIN student s ON ar.student_id=s.id INNER JOIN component c ON a.component_id = c.id INNER JOIN module m ON c.module_id = m.id WHERE s.id=:sid AND m.id=:cid AND a.types =\"assignment\" and c.delete_status = false";
        double total = Ebean.createSqlQuery(s)
                .setParameter("sid", sId)
                .setParameter("cid", mId)
                .findUnique()
                .getDouble("total");
        return total;
    }
    public static double totalCattMax(long sId, long cId) {
        String s = "SELECT DISTINCT IFNULL((m.cat_max),0.0) as total FROM assignment_result ar INNER JOIN assignment a ON ar.assignment_id=a.id INNER JOIN student s ON ar.student_id=s.id INNER JOIN component c ON a.component_id = c.id INNER JOIN module m ON c.module_id = m.id WHERE s.id=:sid AND c.id=:cid AND a.types =\"assignment\" and c.delete_status = false";
        double total = Ebean.createSqlQuery(s)
                .setParameter("sid", sId)
                .setParameter("cid", cId)
                .findUnique()
                .getDouble("total");
        return total;
    }

    public static double totalCattMaxM(long sId, long mId) {
    //    String s =  "SELECT DISTINCT IFNULL((m.cat_max),0.0) as total FROM assignment_result ar INNER JOIN assignment a ON ar.assignment_id=a.id INNER JOIN student s ON ar.student_id=s.id INNER JOIN component c ON a.component_id = c.id INNER JOIN module m ON c.module_id = m.id WHERE ar.id != null and a.id != null and s.id=:sid AND m.id=:cid AND a.types =\"assignment\" and c.delete_status = false";
        String s = "SELECT DISTINCT IFNULL((m.cat_max),0.0) as total FROM assignment_result ar INNER JOIN assignment a ON ar.assignment_id=a.id INNER JOIN student s ON ar.student_id=s.id INNER JOIN component c ON a.component_id = c.id INNER JOIN module m ON c.module_id = m.id WHERE s.id=:sid AND c.id=:cid AND a.types =\"assignment\" and c.delete_status = false";
        double total = Ebean.createSqlQuery(s)
                .setParameter("sid", sId)
                .setParameter("cid", mId)
                .findUnique()
                .getDouble("total");
        return total;
    }



    public static double componetResultComponent(long sId, long cId) {
               return (totalMarks(sId,cId) * totalCattMax(sId,cId))/totalAssignmentMax(sId,cId);
    }
   /* public static double componetResultComponent(long sId, long cId) {
        String s = "SELECT IFNULL(SUM((ar.result * m.cat_max)/a.max)/COUNT(c.id),0.0) as total FROM assignment_result ar INNER JOIN assignment a ON ar.assignment_id=a.id INNER JOIN student s ON ar.student_id=s.id INNER JOIN component c ON a.component_id = c.id INNER JOIN module m ON c.module_id = m.id WHERE s.id=:sid AND c.id=:cid AND a.types =\"assignment\" and c.delete_status = false";
        double total = Ebean.createSqlQuery(s)
                .setParameter("sid", sId)
                .setParameter("cid", cId)
                .findUnique()
                .getDouble("total");
        return total;
    } */
    public static double componetResultComponentResearch(long sId, long cId) {
        String s = "SELECT IFNULL(SUM((ar.result * m.cat_max)/a.max)/COUNT(c.id),0.0) as total FROM assignment_result ar INNER JOIN assignment a ON ar.assignment_id=a.id INNER JOIN student s ON ar.student_id=s.id INNER JOIN component c ON a.component_id = c.id INNER JOIN module m ON c.module_id = m.id WHERE s.id=:sid AND c.id=:cid AND a.types =\"resitResearch\"";
        double total = Ebean.createSqlQuery(s)
                .setParameter("sid", sId)
                .setParameter("cid", cId)
                .findUnique()
                .getDouble("total");
        return total;
    }
    public static double numberAssignmentDone(long sId, long mId, long tId) {
        String s = "SELECT DISTINCT IFNULL(COUNT(a.id),0) as total FROM assignment_result ar INNER JOIN assignment a ON ar.assignment_id=a.id INNER JOIN student s ON ar.student_id=s.id INNER JOIN component c ON a.component_id = c.id WHERE s.id=:sid AND c.module_id=:mid and s.training_id=:tid AND a.types =\"assignment\" and a.delete_status = false and ar.delete_status = false and a.id IS NOT NULL";
        double total = Ebean.createSqlQuery(s)
                .setParameter("sid", sId)
                .setParameter("mid", mId)
                .setParameter("tid", tId)
                .findUnique()
                .getDouble("total");
        return total;
    }
    public static double numberAssignmentDoneResit(long sId, long mId, long tId) {
        String s = "SELECT DISTINCT IFNULL(COUNT(a.id),0) as total FROM assignment_result ar INNER JOIN assignment a ON ar.assignment_id=a.id INNER JOIN student s ON ar.student_id=s.id INNER JOIN component c ON a.component_id = c.id WHERE s.id=:sid AND c.module_id=:mid and s.training_id=:tid AND a.types =\"resitResearch\" and a.delete_status = false and ar.delete_status = false and a.id IS NOT NULL";
        double total = Ebean.createSqlQuery(s)
                .setParameter("sid", sId)
                .setParameter("mid", mId)
                .setParameter("tid", tId)
                .findUnique()
                .getDouble("total");
        return total;
    }
    public static double numberAssignmentDoneStudent(long sId, long tId) {
        String s = "SELECT IFNULL(COUNT(a.id),0) as total FROM assignment_result ar INNER JOIN assignment a ON ar.assignment_id=a.id INNER JOIN student s ON ar.student_id=s.id INNER JOIN component c ON a.component_id = c.id INNER JOIN module m ON c.module_id = m.id WHERE s.id=:sid and s.training_id=:tid AND a.types =\"assignment\" and a.id IS NOT NULL";
        double total = Ebean.createSqlQuery(s)
                .setParameter("sid", sId)
                .setParameter("tid", tId)
                .findUnique()
                .getDouble("total");
        return total;
    }
    public static double numberAssignmentDoneComponent(long sId, long cId, long tId) {
        String s = "SELECT IFNULL(COUNT(a.id),0) as total FROM assignment_result ar INNER JOIN assignment a ON ar.assignment_id=a.id INNER JOIN student s ON ar.student_id=s.id INNER JOIN component c ON a.component_id = c.id WHERE s.id=:sid AND c.id=:cid and s.training_id=:tid AND a.types =\"assignment\"";
        double total = Ebean.createSqlQuery(s)
                .setParameter("sid", sId)
                .setParameter("cid", cId)
                .setParameter("tid", tId)
                .findUnique()
                .getDouble("total");
        return total;
    }
    public static double numberAssignmentDoneComponentResearch(long sId, long cId, long tId) {
        String s = "SELECT IFNULL(COUNT(a.id),0) as total FROM assignment_result ar INNER JOIN assignment a ON ar.assignment_id=a.id INNER JOIN student s ON ar.student_id=s.id INNER JOIN component c ON a.component_id = c.id WHERE s.id=:sid AND c.id=:cid and s.training_id=:tid AND a.types =\"resitResearch\"";
        double total = Ebean.createSqlQuery(s)
                .setParameter("sid", sId)
                .setParameter("cid", cId)
                .setParameter("tid", tId)
                .findUnique()
                .getDouble("total");
        return total;
    }
    public static double numberResearchDone(long sId, long mId, long tId) {
        String s = "SELECT DISTINCT IFNULL(COUNT(a.id),0) as total FROM assignment_result ar INNER JOIN assignment a ON ar.assignment_id=a.id INNER JOIN student s ON ar.student_id=s.id INNER JOIN component c ON a.component_id = c.id INNER JOIN module m ON c.module_id = m.id WHERE s.id=:sid AND m.id=:mid and s.training_id=:tid AND a.types =\"research\" and ar.delete_status = false and a.delete_status = false and c.delete_status = false and a.id IS NOT NULL";
        double total = Ebean.createSqlQuery(s)
                .setParameter("sid", sId)
                .setParameter("mid", mId)
                .setParameter("tid", tId)
                .findUnique()
                .getDouble("total");
        return total;
    }
    public static double numberResearchDoneResit(long sId, long mId, long tId) {
        String s = "SELECT DISTINCT IFNULL(COUNT(a.id),0) as total FROM assignment_result ar INNER JOIN assignment a ON ar.assignment_id=a.id INNER JOIN student s ON ar.student_id=s.id INNER JOIN component c ON a.component_id = c.id INNER JOIN module m ON c.module_id = m.id WHERE s.id=:sid AND m.id=:mid and s.training_id=:tid AND a.types =\"resitResearch\" and ar.delete_status = false and a.delete_status = false and c.delete_status = false and a.id IS NOT NULL";
        double total = Ebean.createSqlQuery(s)
                .setParameter("sid", sId)
                .setParameter("mid", mId)
                .setParameter("tid", tId)
                .findUnique()
                .getDouble("total");
        return total;
    }
    public static double numberResearchDoneStudent(long sId, long tId) {
        String s = "SELECT IFNULL(COUNT(a.id),0) as total FROM assignment_result ar INNER JOIN assignment a ON ar.assignment_id=a.id INNER JOIN student s ON ar.student_id=s.id INNER JOIN component c ON a.component_id = c.id INNER JOIN module m ON c.module_id = m.id WHERE s.id=:sid and s.training_id=:tid AND a.types =\"research\" and a.id IS NOT NULL";
        double total = Ebean.createSqlQuery(s)
                .setParameter("sid", sId)
                .setParameter("tid", tId)
                .findUnique()
                .getDouble("total");
        return total;
    }
    public static double numberResearchDoneComp(long sId, long cId, long tId) {
        String s = "SELECT IFNULL(COUNT(a.id),0) as total FROM assignment_result ar INNER JOIN assignment a ON ar.assignment_id=a.id INNER JOIN student s ON ar.student_id=s.id INNER JOIN component c ON a.component_id = c.id INNER JOIN module m ON c.module_id = m.id WHERE s.id=:sid AND c.id=:cid and s.training_id=:tid AND a.types =\"research\" and a.id IS NOT NULL";
        double total = Ebean.createSqlQuery(s)
                .setParameter("sid", sId)
                .setParameter("cid", cId)
                .setParameter("tid", tId)
                .findUnique()
                .getDouble("total");
        return total;
    }
    public static double numberResearchDoneCompResearch(long sId, long cId, long tId) {
        String s = "SELECT IFNULL(COUNT(a.id),0) as total FROM assignment_result ar INNER JOIN assignment a ON ar.assignment_id=a.id INNER JOIN student s ON ar.student_id=s.id INNER JOIN component c ON a.component_id = c.id INNER JOIN module m ON c.module_id = m.id WHERE s.id=:sid AND c.id=:cid and s.training_id=:tid AND a.types =\"resitResearch\" and a.id IS NOT NULL";
        double total = Ebean.createSqlQuery(s)
                .setParameter("sid", sId)
                .setParameter("cid", cId)
                .setParameter("tid", tId)
                .findUnique()
                .getDouble("total");
        return total;
    }
    public static double numberResearchDoneCompResearchModule(long sId, long mId, long tId) {
        String s = "SELECT IFNULL(COUNT(a.id),0) as total FROM assignment_result ar INNER JOIN assignment a ON ar.assignment_id=a.id INNER JOIN student s ON ar.student_id=s.id INNER JOIN component c ON a.component_id = c.id INNER JOIN module m ON c.module_id = m.id WHERE s.id=:sid AND m.id=:mid and s.training_id=:tid AND a.types =\"resitResearch\" and a.id IS NOT NULL";
        double total = Ebean.createSqlQuery(s)
                .setParameter("sid", sId)
                .setParameter("mid", mId)
                .setParameter("tid", tId)
                .findUnique()
                .getDouble("total");
        return total;
    }
    public static double componentResearchResult(long sId, long compId) {
        String s = "SELECT IFNULL(SUM((ar.result * m.re_max)/a.max)/COUNT(c.id),0.0) as total FROM assignment_result ar INNER JOIN assignment a ON ar.assignment_id=a.id INNER JOIN student s ON ar.student_id=s.id INNER JOIN component c ON a.component_id = c.id INNER JOIN module m ON c.module_id = m.id WHERE s.id=:sid AND c.id=:cid AND a.types =\"research\" and a.id IS NOT NULL";
        double total = Ebean.createSqlQuery(s)
                .setParameter("sid", sId)
                .setParameter("cid", compId)
                .findUnique()
                .getDouble("total");
        return total;
    }
    public static double componentResearchResultResearch(long sId, long compId) {
        String s = "SELECT IFNULL(SUM((ar.result * m.re_max)/a.max)/COUNT(c.id),0.0) as total FROM assignment_result ar INNER JOIN assignment a ON ar.assignment_id=a.id INNER JOIN student s ON ar.student_id=s.id INNER JOIN component c ON a.component_id = c.id INNER JOIN module m ON c.module_id = m.id WHERE s.id=:sid AND c.id=:cid AND a.types =\"resitResearch\" and a.id IS NOT NULL";
        double total = Ebean.createSqlQuery(s)
                .setParameter("sid", sId)
                .setParameter("cid", compId)
                .findUnique()
                .getDouble("total");
        return total;
    }
    public static double componentResearchResultResearchModule(long sId, long mId) {
        String s = "SELECT IFNULL(SUM((ar.result * m.re_max)/a.max)/COUNT(c.id),0.0) as total FROM assignment_result ar INNER JOIN assignment a ON ar.assignment_id=a.id INNER JOIN student s ON ar.student_id=s.id INNER JOIN component c ON a.component_id = c.id INNER JOIN module m ON c.module_id = m.id WHERE s.id=:sid AND m.id=:mid AND a.types =\"resitResearch\" and a.id IS NOT NULL";
        double total = Ebean.createSqlQuery(s)
                .setParameter("sid", sId)
                .setParameter("mid", mId)
                .findUnique()
                .getDouble("total");
        return total;
    }
    /*
    public static double resultTotal(long sId, long mId) {
        String s = "SELECT DISTINCT IFNULL(SUM((ar.result)),0.0) as total FROM assignment_result ar INNER JOIN assignment a ON ar.assignment_id=a.id INNER JOIN student s ON ar.student_id=s.id INNER JOIN component c ON a.component_id = c.id INNER JOIN module m ON c.module_id = m.id WHERE s.id=:sid AND m.id=:cid AND a.types =\"assignment\" and ar.delete_status = false and a.delete_status = false and m.delete_status = false";
        double total = Ebean.createSqlQuery(s)
                .setParameter("sid", sId)
                .setParameter("cid", mId)
                .findUnique()
                .getDouble("total");
        return total;
    } */
    public static double totalMaxPerStudent(long sId, long mId) {
        String s = "SELECT DISTINCT IFNULL(SUM((a.max)),0.0) as total FROM assignment_result ar INNER JOIN assignment a ON ar.assignment_id=a.id INNER JOIN student s ON ar.student_id=s.id INNER JOIN component c ON a.component_id = c.id INNER JOIN module m ON c.module_id = m.id WHERE s.id=:sid AND m.id=:mid AND a.types =\"assignment\" and ar.delete_status = false and a.delete_status = false and m.delete_status = false";
        double total = Ebean.createSqlQuery(s)
                .setParameter("sid", sId)
                .setParameter("mid", mId)
                .findUnique()
                .getDouble("total");
        return total;
    }
    public static double totalAssignmentModuleMax(long mId) {
        String s = "SELECT DISTINCT IFNULL(m.cat_max,0.0) as total FROM module m WHERE m.id=:mid and m.delete_status = false";
        double total = Ebean.createSqlQuery(s)
                .setParameter("mid", mId)
                .findUnique()
                .getDouble("total");
        return total;
    }
    public static double totalCatMax(long sId, long mId) {
        String s = "SELECT DISTINCT IFNULL(m.cat_max,0.0) as total FROM module m WHERE m.id=:mid and m.delete_status = false";
        double total = Ebean.createSqlQuery(s)
                .setParameter("sid", sId)
                .setParameter("mid", mId)
                .findUnique()
                .getDouble("total");
        return total;
    }
    public static double totalMax(long sId, long mId) {
        String s = "SELECT DISTINCT IFNULL(SUM(a.max),0.0) as total FROM assignment_result ar INNER JOIN assignment a ON ar.assignment_id=a.id INNER JOIN student s ON ar.student_id=s.id INNER JOIN component c ON a.component_id = c.id INNER JOIN module m ON c.module_id = m.id WHERE s.id=:sid AND m.id=:cid AND a.types =\"assignment\" and ar.delete_status = false and a.delete_status = false and m.delete_status = false";
        double total = Ebean.createSqlQuery(s)
                .setParameter("sid", sId)
                .setParameter("cid", mId)
                .findUnique()
                .getDouble("total");
        return total;
    }
    public static double totalCount(long sId, long mId) {
        String s = "SELECT DISTINCT IFNULL(COUNT(c.id),0.0) as total FROM assignment_result ar INNER JOIN assignment a ON ar.assignment_id=a.id INNER JOIN student s ON ar.student_id=s.id INNER JOIN component c ON a.component_id = c.id INNER JOIN module m ON c.module_id = m.id WHERE s.id=:sid AND m.id=:cid AND a.types =\"assignment\" and ar.delete_status = false and a.delete_status = false and m.delete_status = false";
        double total = Ebean.createSqlQuery(s)
                .setParameter("sid", sId)
                .setParameter("cid", mId)
                .findUnique()
                .getDouble("total");
        return total;
    }
    public static double moduleResult(long sId, long mId) {
        return resultTotal(sId, mId) /totalMax(sId, mId)/totalCount(sId, mId);
    }
    public static double resultTotal(long sId, long mId) {
       // return resultTotalPerStudent(sId, mId)/ totalMaxPerStudent(sId, mId) * totalAssignmentModuleMax(mId);
        return getTotalComponentPerModule(sId, mId)/ totalMaxPerStudent(sId, mId) * totalAssignmentModuleMax(mId);
    }
    public static double resultTotalPerStudent(long sId, long mId) {
        String s = "SELECT DISTINCT IFNULL(SUM(ar.result),0.0) as total FROM assignment_result ar INNER JOIN assignment a ON ar.assignment_id=a.id INNER JOIN component c ON a.component_id = c.id INNER JOIN module m ON c.module_id = m.id WHERE ar.student_id=:sid AND m.id=:cid AND a.types =\"assignment\" and ar.delete_status = false and a.delete_status = false and m.delete_status = false";
        double total = Ebean.createSqlQuery(s)
                .setParameter("sid", sId)
                .setParameter("cid", mId)
                .findUnique()
                .getDouble("total");
        return total;
    }
    public static List<Component> componentsPerModule(Long mId) {
        return Component.finder.where()
                .not(Expr.eq("deleteStatus", true))
                .eq("module.deleteStatus", false)
                .eq("module.id", mId)
                .orderBy("id asc")
                .findList();
    }
    public static double getTotalComponentPerModule(long sId, Long mId) {
        String s = "SELECT DISTINCT IFNULL(SUM(ar.result/a.max * 15),0.0) as total FROM assignment_result ar INNER JOIN assignment a ON ar.assignment_id=a.id INNER JOIN component c ON a.component_id = c.id inner join module m on c.module_id = m.id WHERE ar.student_id=:sid AND c.module_id="+mId+" AND a.types =\"assignment\" and ar.delete_status = false and a.delete_status = false";
        double total = Ebean.createSqlQuery(s)
                .setParameter("sid", sId)
                .findUnique()
                .getDouble("total");
        return total;

    }
    public static double componetResult(long sId, long mId) {
        String s = "SELECT IFNULL(SUM((ar.result * m.cat_max)/a.max)/COUNT(c.id),0.0) as total FROM assignment_result ar INNER JOIN assignment a ON ar.assignment_id=a.id INNER JOIN student s ON ar.student_id=s.id INNER JOIN component c ON a.component_id = c.id INNER JOIN module m ON c.module_id = m.id WHERE s.id=:sid AND m.id=:mid AND a.types =\"assignment\"";
        double total = Ebean.createSqlQuery(s)
                .setParameter("sid", sId)
                .setParameter("mid", mId)
                .findUnique()
                .getDouble("total");
        return total;
    }
    public static double moduleResultBefore(long sId, long mId) {
        String s = "SELECT DISTINCT IFNULL(SUM((ar.result * m.cat_max)/a.max)/COUNT(c.id),0.0) as total FROM assignment_result ar INNER JOIN assignment a ON ar.assignment_id=a.id INNER JOIN student s ON ar.student_id=s.id INNER JOIN component c ON a.component_id = c.id INNER JOIN module m ON c.module_id = m.id WHERE s.id=:sid AND m.id=:cid and ar.is_resit_result = false and ar.delete_status = false and a.delete_status = false and m.delete_status = false";
        double total = Ebean.createSqlQuery(s)
                .setParameter("sid", sId)
                .setParameter("cid", mId)
                .findUnique()
                .getDouble("total");
        return total;
    }
    public static double getStudentCatMarksModule(long studentId,Long mid) {
        return (moduleResult(studentId, mid));
    }


    public static double moduleResult(long sId, Long modId) {
    String s = "SELECT IFNULL(AVG(inner_query.total),0) as av FROM (SELECT COALESCE(SUM((ar.result * m.cat_max)/a.max)/COUNT(c.id),0) as total FROM assignment_result ar INNER JOIN assignment a ON ar.assignment_id=a.id INNER JOIN student s ON ar.student_id=s.id INNER JOIN component c ON a.component_id = c.id INNER JOIN module m ON c.module_id = m.id WHERE s.id=:sid AND m.id=:mid group by c.id)  as inner_query";
    double total = Ebean.createSqlQuery(s)
            .setParameter("sid", sId)
            .setParameter("mid", modId)
            .findUnique()
            .getDouble("av");
    for(int i = 0; i < Counts.countComponentsPerModule(sId, modId); i ++){
        total += total;
    }
    return total;
 }
}
