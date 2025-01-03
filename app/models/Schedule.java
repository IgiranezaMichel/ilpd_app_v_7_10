package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonProperty;
import play.db.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;
import java.util.List;

/**
 * Created by Noel on 06-Dec-17.
 */
@Entity
public class Schedule extends Model {
    @Id
    public long id;
    public String componentType = "";
    public String assignment = "";
    public String exam = "";
    public String doneBy = "";
    public Boolean deleteStatus = false;

    @ManyToOne(cascade = CascadeType.ALL)
    public Lecture lecture;

    @ManyToOne(cascade = CascadeType.ALL)
    public Component component;

    @ManyToOne(cascade = CascadeType.ALL)
    public Room room;

    @ManyToOne(cascade = CascadeType.ALL)
    public Training training;

    @ManyToOne(cascade = CascadeType.ALL)
    public DateRange dateRange;

    public String startHour;

    public String endHour;

    public Date date;


    public String getHour() {
        return startHour + " - " + endHour;
    }

    @JsonProperty
    public String print(){
        return component.compName + " - "+training.tNames() + " - "+ room.roomName;
    }

    public static Finder<Long, Schedule> find = new Finder<>(Long.class, Schedule.class);


    public static ExpressionList<Schedule> query(){
        return find.where().eq("deleteStatus",true);
    }

    public static Page<Schedule> find(Integer page) {
        return query().orderBy("id desc").findPagingList(10).setFetchAhead(false).getPage(page);
    }
    public static Page<Schedule> find(Integer page,boolean isCLE) {
        return find.where()
                .eq("training.iMode.campusProgram.program.cle",isCLE)
                .orderBy("date desc")
                .findPagingList(10)
                .setFetchAhead(false).getPage(page);
    }

    public Boolean isRoomTaken() {
        Schedule schedule = query().not(Expr.eq("id", this.id)).eq("room.id", this.room.id).gt("date", this.date).eq("training.id", this.training.id).setMaxRows(1).findUnique();
        return schedule != null;
    }

    public Schedule NoDoubleTeacher() {
        return query().not(Expr.eq("id", this.id)).not(Expr.eq("lecture.id", this.lecture.id)).eq("component.id", this.component.id).eq("training.id", this.training.id).setMaxRows(1).findUnique();
    }

    //This function returns all lecturers who are on timetable to teach
    public static List<Lecture> allLecturersTimetabled(boolean cle){
        return Lecture.find.where()
                .eq("deleteStatus", false)
                .in("id",find.where()
                .eq("deleteStatus", false)
                .eq("training.iMode.campusProgram.program.cle",cle)
                .query().select("lecture.id"))
                .findList();
    }


    public Boolean isNoDoubleTeacher() {
        return (this.NoDoubleTeacher() != null);
    }


    public Boolean isTeacherTaken() {
        Schedule sd = find.where().not(Expr.eq("id", this.id)).eq("deleteStatus", false).eq("training.iMode.sessionMode.session.id", this.training.iMode.sessionMode.session.id).eq("date", this.date).eq("lecture.id", this.lecture.id).setMaxRows(1).findUnique();
        return (sd != null);
    }

    public Boolean isNotSameCampus() {
        return (this.room.campus.id != this.training.iMode.campusProgram.campus.id);
    }


    public Schedule exist() {
        return find.where().not(Expr.eq("id", this.id)).eq("deleteStatus", false).eq("date", this.date).eq("date", this.date).eq("training.id", this.training.id).setMaxRows(1).findUnique();
    }


    public List<Student> compStudents() {
        return Student.finder.where().eq("deleteStatus", false).eq("training.iMode.campusProgram.program.id", this.component.module.program.id).findList();
    }

    public static List<Schedule> byIoMode(long iomodeId) {
        return Schedule.find.where()
                .eq("deleteStatus", false)
                .eq("training.iMode.id", iomodeId)
                .findList();
    }

    public static List<Schedule> allBy(String q) {
        String[] arr = q.split(Vld.split);
        if (!q.equals("") && (arr.length <= 1))
            return find.where().not(Expr.eq("deleteStatus", true)).like("fName", "%" + q + "%").orderBy("id desc").findList();

        int a = (arr.length > 1 && Vld.isNumeric(arr[1])) ? Integer.parseInt(arr[1]) : 0;
        return allPage(a);
    }

    public static String count() {
        int api = all().size();
        return String.valueOf((api > 0) ? api : 1);
    }
    public static List<Schedule> all() {
        return find.where().not(Expr.eq("deleteStatus", true))
                .orderBy("date desc")
                .findList();
    }
    public static List<Schedule> allOne() {
        return find.where()
                .setMaxRows(1)
                .findList();
    }

    public static List<Schedule> allCle() {
        return find.where().eq("deleteStatus", false).eq("training.iMode.campusProgram.program.cle", true).orderBy("id desc").findList();
    }

    public static List<Schedule> byTraining(long trId) {
        return find.where()
                .eq("deleteStatus", false)
                .eq("training.id", trId)
                .findList();
    }

    public static List<Schedule> allPage(int a) {
        int ap = (a - 1) * Vld.limit;
        return find.where().not(Expr.eq("deleteStatus", true)).setFirstRow(ap).setMaxRows(Vld.limit).orderBy("id desc").findList();
    }

    public static Schedule finderById(long id) {
        return find.ref(id);
    }

    public static boolean isExists(Long tId, Long drhs) {
        Schedule schedule = Schedule.find.where()
                .eq("training.id", tId)
                .eq("deleteStatus", false)
                .eq("dateRange.id", drhs).setMaxRows(1)
                .findUnique();
        return schedule != null;
    }

    public List<Module> myModules(){
        return Module.finder.setDistinct(true).where().eq("deleteStatus",false).eq("id",this.component.module.id).orderBy("id desc").findList();
    }
    public static String studentsOfComponentPerComponentsLecture(Long lId, Long compId) {
        String s = "SELECT DISTINCT count(s.id) AS total FROM schedule sc INNER JOIN lecture l ON sc.lecture_id = l.id INNER JOIN component c on sc.component_id = c.id inner join training t on sc.training_id = t.id inner join student s ON t.id = s.training_id where l.id="+lId+" and c.id="+compId+" and t.is_closed_a='false' and sc.delete_status='false' and l.delete_status='false' and c.delete_status='false' and t.delete_status='false'";
        return Ebean.createSqlQuery(s)
                .findUnique()
                .getInteger("total")
                .toString();
    }
}
