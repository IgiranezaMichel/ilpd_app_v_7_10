package models;

import Helper.ReportProperty;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import play.db.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;
import java.util.List;

@Entity
@ReportProperty(name = "Group assignment submission report")
public class GroupSubmission extends Model {
    @Id
    public long id;

    @ManyToOne(cascade = CascadeType.ALL)
    public Groups groups;

    @ManyToOne(cascade = CascadeType.ALL)
    public Assignment assignment;

    @ManyToOne(cascade = CascadeType.ALL)
    public Student student;

    public String comment = "";
    public String attachment = "";
    public Boolean isMarked = false;

    public String status = "pending";
    public Date date=new Date();
    public Boolean deleteStatus = false;

    public static Finder<Long, GroupSubmission> finder = new Finder<Long, GroupSubmission>(Long.class, GroupSubmission.class);

    public Boolean notExist() {
        GroupSubmission groupSubmission = finder.where()
                .eq("deleteStatus", false)
                .eq("groups.id", this.groups.id)
                .eq("assignment.id", this.assignment.id)
                .eq("student.id", this.student.id)
                .setMaxRows(1)
                .findUnique();
        return (groupSubmission == null);
    }
    public Boolean notExist2() {
        GroupSubmission groupSubmission = finder.where()
                .eq("deleteStatus", false)
                .eq("assignment.id", this.assignment.id)
                .eq("student.id", this.student.id)
                .setMaxRows(1)
                .findUnique();
        return (groupSubmission == null);
    }

    public static List<GroupSubmission> getSubmittedAssignment(Student student){
        //Considering the parameter student object as me
        Query<GroupMembers> myGroups = GroupMembers.finder.where().eq("student.id",student.id).query().select("groups.id");//All groups that I belong to
        Query<GroupMembers> allGroupMembers = GroupMembers.finder.where().in("groups.id",myGroups).query().select("student.id");/*All students that we belong to the same groups including me.
         This means that I can see assignments submitted by me or any other student that we belong to the same group.*/
           return GroupSubmission.finder.where().eq("status","submitted").in("status",allGroupMembers).findList();
    }
    public static List<GroupSubmission> groupSubmissionList (long asId){
        return finder.setDistinct(true)
                .where()
                .eq("assignment.id", asId)
                .eq("status", "submitted")
                .findList();
    }
    public static List<GroupSubmission> byAssignAndCom(long asIs, long grId){
        return finder.setDistinct(true).where()
                .eq("deleteStatus", false)
                .eq("assignment.grouped", true)
                .eq("assignment.id", asIs)
                .eq("status", "submitted")
                .eq("groups.id", grId)
                .findList();
    }
    public static GroupSubmission byAssignment(long gId, long asIs, Long sId){
        return finder.setDistinct(true).where()
                .eq("deleteStatus", false)
                .eq("assignment.grouped", true)
                .eq("groups.id", gId)
                .eq("assignment.id", asIs)
                .eq("student.id", sId)
                .eq("status", "pending")
                .setMaxRows(1)
                .findUnique();
    }
}
