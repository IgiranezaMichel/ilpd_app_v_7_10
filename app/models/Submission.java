package models;
import Helper.BaseModel;
import play.db.ebean.Model;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Entity
public class Submission extends Model {
    @Id
    public long id;

    @ManyToOne(cascade = CascadeType.ALL)
    public Assignment assignment;

    @ManyToOne(cascade = CascadeType.ALL)
    public Student student;

    public String comment = "";
    public String attachment = "";

    public Boolean forGroup = false;
    public String status = "pending";
    public Date date=new Date();
    public Boolean deleteStatus = false;
    public String doneBy = "";
    public Timestamp doneAt = new Timestamp(new Date().getTime());

    public static Finder<Long, Submission> finder = new Finder<Long, Submission>(Long.class, Submission.class);
    public static BaseModel.Look<Submission> on = new BaseModel.Look<>(Submission.class);
    public static Submission finderById(Long id) {
        return finder.ref(id);
    }

    public Boolean notExist() {
        Submission s = finder.where().eq("deleteStatus", false).eq("status", "submitted").eq("student.id", this.student.id).eq("forGroup", this.forGroup).eq("assignment.id", this.assignment.id).setMaxRows(1).findUnique();
        return (s == null);
    }
    public static List<Submission> all() {
        return finder.where()
                .eq("deleteStatus", true)
                .findList();
    }
    public static Submission allByTra(long asId, long sId) {
        return finder.where()
                .eq("deleteStatus", true)
                .eq("assignment.id", asId)
                .eq("student.id", sId)
                .findUnique();
    }
    public static List<Submission> submissionListIndividual(long id){
        return Submission.finder.setDistinct(true).where()
                .eq("student.id",id)
                .eq("deleteStatus",false)
                .eq("assignment.grouped",false)
             //   .or(Expr.ne("status", "submitted"),Expr.ne("status", "viewed"))
                .findList();
    }

    public static List<Submission> submissionByAssignment(long studId, long assId){
        return Submission.finder.where()
                .eq("student.id",studId)
                .eq("assignment.id",assId)
                .eq("deleteStatus",false)
                .eq("status","pending")
                .findList();
    }

    public static List<Submission> submissionListGroup(long id){
        return Submission.finder.where()
                .eq("student.id",id)
                .eq("deleteStatus",false)
                .eq("assignment.grouped",true)
                .findList();
    }
    public static List<Submission> groupSubmissionList (long asId){
        return finder.setDistinct(true)
                .where()
                .eq("assignment.id", asId)
                .eq("status", "submitted")
                .findList();
    }
}
