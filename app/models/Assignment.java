package models;

import Helper.ReportProperty;
import com.avaje.ebean.Expr;
import controllers.Counts;
import play.db.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;
import java.util.List;

@Entity
@ReportProperty(name = "Assignment report")
public class Assignment extends Model {
    @Id
    public long id;
    public String assignmentTitle = "";
    public String description = "";
    public String attachment = "";
    public Date startDate;
    public Date endDate;
    public String duration = "";

    public float max = 0;
    public String types = "";

    public Boolean grouped;

    @ManyToOne(cascade = CascadeType.ALL)
    public Groups group;

    @ManyToOne(cascade = CascadeType.ALL)
    public Component component;

    @ManyToOne(cascade = CascadeType.ALL)
    public Lecture lecture;

    @ManyToOne(cascade = CascadeType.ALL)
    public Training training;

    public Boolean deleteStatus;


    public static Model.Finder<Long, Assignment> finder = new Model.Finder<Long, Assignment>(Long.class, Assignment.class);

    public static List<Assignment> all(){
        return finder.where().eq("deleteStatus",false).findList();
    }

    public static Assignment finderById(Long id){
        return finder.ref(id);
    }
    public Boolean notDone(Long sId){
        Submission s = Submission.finder.where()
                .eq("deleteStatus",false)
                .eq("assignment.id",this.id)
                .eq("student.id", sId)
                .eq("status","pending")
                .setMaxRows(1).findUnique();
        return ( s != null );
    }
    public Boolean notDoneReseat(Long sId){
        Submission s = Submission.finder.where()
                .eq("deleteStatus",false)
                .eq("assignment.id",this.id)
                .eq("student.id", sId)
                .eq("status","pending")
                .eq("assignment.types","resitResearch")
                .setMaxRows(1).findUnique();
        return ( s != null );
    }
    public Boolean notDone(Student student){
        Groups grp = student.MyGroup(this);
        return ( sub(student) == null );
    }
    public Boolean done(Student student) {
        return (sub(student) != null);
    }

    public GroupSubmission sub(Student student){
        Groups grp = student.MyGroup(this);
        if( grp == null ) return null;
        return GroupSubmission.finder.where().eq("deleteStatus",false).eq("assignment.id",this.id).eq("groups.id",grp.id).eq("status", "submitted").setMaxRows(1).findUnique();
    }

    public Boolean haveGroup(Student  student){
        Groups grp = student.MyGroup(this);
        return ( grp != null );
    }

    public List<Submission> mySubmissions(){
        return Submission.finder.setDistinct(true).where()
                .eq("deleteStatus",false)
                .eq("status","submitted")
                .eq("assignment.id",this.id)
                .not(Expr.eq("student", null))
                .orderBy("student.regNo asc")
                .findList();
    }
    public List<Submission> myUnSubmissions(){
        return Submission.finder.where()
                .eq("deleteStatus",false)
                .eq("status","pending")
                .eq("assignment.id",this.id).findList();
    }
    public List<Submission> myViewedSubmissions(){
        return Submission.finder.where()
                .eq("deleteStatus",false)
                .eq("status","viewed")
                .eq("assignment.id",this.id).findList();
    }
    public List<GroupSubmission> myUnSubmissionsGroup(){
        return GroupSubmission.finder.where()
                .eq("deleteStatus",false)
                .eq("status","pending")
                .eq("assignment.id",this.id)
                .findList();
    }

    public List<GroupSubmission> myViewedSubmissionsGroup(){
        return GroupSubmission.finder.where()
                .eq("deleteStatus",false)
                .eq("status","viewed")
                .eq("assignment.id",this.id)
                .findList();
    }
    public List<GroupSubmission> myGroupSubmissions(){
        return GroupSubmission.finder.setDistinct(true).where()
                .eq("deleteStatus",false)
                .eq("status","submitted")
                .eq("isMarked",false)
                .eq("assignment.id",this.id)
                .findList();
    }
    public Integer number(Boolean fool){
        if(fool)
            return GroupSubmission.finder
                    .where()
                    .eq("deleteStatus",false)
                    .eq("status","submitted")
                    .eq("isMarked",false)
                    .eq("assignment.id",this.id)
                    .findRowCount();
        else
            return Submission.finder.setDistinct(true).where()
                    .eq("deleteStatus",false)
                    .eq("status","submitted")
                    .eq("assignment.id",this.id)
                    .findRowCount();
    }
    public Integer numberUnsub(){
            return Assignment.finder.where()
                    .eq("deleteStatus",false)
                    .eq("id",this.id)
                    .findRowCount();
    }
    public int leftDays(){
        return Counts.daysLeft(this.endDate);
    }
    public String expireColor(){
        int days = leftDays();
        return ( days > 4 ) ? "bg-green" : ( days > 0 ) ? "bg-yellow" : "bg-red";
    }

    public static List<Assignment> submittedStudents(long cId, long lId){
        return finder.setDistinct(true).where()
                .eq("deleteStatus", false)
                .eq("grouped", true)
                .eq("component.id", cId)
                .eq("lecture.id", lId)
                .findList();
    }
}
