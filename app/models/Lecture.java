package models;

import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.fasterxml.jackson.annotation.JsonProperty;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Noel on 05-Dec-17.
 */
@Entity
public class Lecture extends Model {
    @Id
    public long id;
    public String fName = "";
    public String lName = "";
    public String qualification = "";
    public String specialization = "";
    public String doneBy = "";

    @OneToOne(cascade = CascadeType.ALL)
    public Users user;

    public Boolean deleteStatus = false;

    public static Finder<Long, Lecture> find = new Finder<Long, Lecture>(Long.class, Lecture.class);

    @JsonProperty
    public String print() {
        return this.fName + " " + this.lName;
    }

    public static Lecture byUser(Long userId) {
        return find.where().eq("deleteStatus", false).eq("user.id", userId).setMaxRows(1).findUnique();
    }

    public List<CourseMaterial> materialList(){
        return CourseMaterial.finder.where()
                .eq("deleteStatus", false)
                .eq("schedule.lecture.id",this.id).findList();
    }

    public Boolean notExist() {
        Lecture dep = find.where().eq("deleteStatus", false).eq("user.id", this.user.id).setMaxRows(1).findUnique();
        return (dep == null);
    }

    public List<Module> myModules() {
        return Module.find.where().eq("deleteStatus", false).eq("lecture.id", this.id).findList();
    }


    public List<Module> myModulesWithPeriod(long trainingId) {
        return Module.find.where()
                .eq("deleteStatus", false)
                .in("id", Component.finder.where()
                        .in("id", Schedule.find.where()
                                .eq("lecture.id", this.id)
                                .eq("training.id", trainingId)
                                .select("component.id"))
                        .select("module.id"))
                .findList();
    }
    private ExpressionList<Component> myCompQuery(){
        return Component.finder.where()
                .in("id", Schedule.find.where()
                        .eq("deleteStatus", false)
                        .eq("lecture.id", this.id)
                        .select("component.id"));
    }

    public List<Component> myComp() {
        return myCompQuery().findList();
    }

    public List<Component> myComp(boolean cle) {
        return myCompQuery().eq("module.program.cle",cle).findList();
    }

    public List<Training> myTrainingsAll() {
        return Training.find.query().setDistinct(true).where()
                .in("id", Schedule.find.where()
                .eq("lecture.id", this.id)
                .select("training.id"))
                .eq("isClosedA", false)
                .findList();
    }
    public List<Training> myTrainingsAllDtr() {
        return Training.find.query().setDistinct(true).where().in("id", Schedule.find.where()
                .eq("lecture.id", this.id)
                .eq("training.iMode.campusProgram.program.cle", true)
                .select("training.id"))
                .orderBy("id desc")
                .findList();
    }
    public List<Schedule> myTraining(Component comp) {
        return Schedule.find
                .where("deleteStatus='false' and training.iMode.campusProgram.program.id='" + comp.module.program.id + "' and lecture.id='" + this.id + "' and training.isClosedA='false' group by training.id").findList();
    }
    public List<Schedule> scheduleList() {
        return Schedule.find.setDistinct(true)
                .where("deleteStatus='false' and lecture.id='" + this.id + "' group by training.id")
                .findList();
    }

    public static List<Lecture> allBy(String q) {
        String[] arr = q.split(Vld.split);
        if (!q.equals("") && (arr.length <= 1)) {
            return find.where().not(Expr.eq("deleteStatus", true)).like("fName", "%" + q + "%").orderBy("id desc").findList();
        }

        int a = (arr.length > 1 && Vld.isNumeric(arr[1])) ? Integer.parseInt(arr[1]) : 0;
        return allPage(a);
    }

    public static String count() {
        int api = all().size();
        return String.valueOf((api > 0) ? api : 1);
    }

    public static List<Lecture> all() {
        return find.where().not(Expr.eq("deleteStatus", true)).orderBy("id desc").findList();
    }

    public static List<Lecture> allPage(int a) {
        int ap = (a - 1) * Vld.limit;
        return find.where().not(Expr.eq("deleteStatus", true)).setFirstRow(ap).setMaxRows(Vld.limit).orderBy("id desc").findList();
    }

    public static Lecture finderById(Long id) {
        return find.byId(id);
    }

    public String hisNames() {
        return this.fName + " " + this.lName;
    }


}
