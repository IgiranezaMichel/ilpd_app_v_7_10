package models;
import com.avaje.ebean.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import play.db.ebean.Model;
import javax.persistence.*;
import java.util.List;
@Entity
public class Component extends Model {
    @Id
    public long id;
    public String compName = "";
    public String code = "";
    public Integer credits;

    @ManyToOne(cascade = CascadeType.ALL)
    public Module module;


    public Boolean deleteStatus = false;

    public static Model.Finder<Long, Component> finder = new Finder<Long, Component>(Long.class, Component.class);

    @JsonProperty
    public String print(){
        return (this.compName+"-"+this.module.program.programAcronym);
    }
    public SubMark compMarks(Long stuId){
       return SubMark.find.where().eq("deleteStatus",false)
               .eq("component.id",this.id)
               .eq("student.id",stuId).setMaxRows(1).findUnique();
    }
    public Boolean notExist() {
        Component c = finder.where().eq("deleteStatus", false).eq("module.id", this.module.id).setMaxRows(1).findUnique();
        return (c == null);
    }
    public List<Assignment> compAssignments(Lecture lecture) {
        return Assignment.finder.where().eq("deleteStatus", false).eq("component.id", this.id).eq("lecture.id",lecture.id).orderBy("id desc").findList();
    }
    public List<Training> compTrain() {
        return Training.find.where()
                .eq("deleteStatus", false)
                .eq("iMode.campusProgram.program.id", this.module.program.id)
                .findList();
    }
    private static Expression cleExpr(boolean b){
        return Expr.eq("module.program.cle",b);
    }
    public static int number(boolean b){
        return Component.finder.where().add(cleExpr(b)).findRowCount();
    }
    public static List<Component> all() {
        return finder.where().eq("deleteStatus", false).findList();
    }
    public static List<Component> allByModule(long modId) {
        return finder.where().eq("deleteStatus", false).eq("module.id", modId).findList();
    }
    public static List<Component> all(boolean isCLE) {
        return finder.where()
                .eq("module.program.cle",isCLE)
                .eq("deleteStatus", false)
                .findList();
    }
    public static Component finderById(Long id) {
        return finder.byId(id);
    }
    public List<SqlRow> compStudents2(long lectId) {
        String query="SELECT s2.id as id, CONCAT(s2.first_name,' ',s2.family_name) as name,t.id as tid FROM schedule s INNER JOIN training t ON s.training_id = t.id  INNER JOIN student s2 ON t.id = s2.training_id  INNER JOIN intake_session_mode ism ON t.i_mode_id = ism.id  INNER JOIN campus_program program ON ism.campus_program_id = program.id  INNER JOIN program p ON program.program_id = p.id  INNER JOIN component c ON s.component_id = c.id  INNER JOIN lecture l ON s.lecture_id = l.id  INNER JOIN module m ON c.module_id = m.id WHERE p.id = :pId AND c.delete_status=0 AND l.id=:lectId AND s.delete_status=0 AND ism.delete_status=0 AND t.delete_status=0 AND l.delete_status=0 and m.delete_status=0 GROUP BY s2.id;";
      return  Ebean.createSqlQuery(query)
                .setParameter("pId",this.module.program.id)
                .setParameter("lectId",lectId)
                .findList();
    }
    public List<Assignment> compAssignments(Boolean b) {
        return cAssQuery(b).orderBy("id desc")
                .findList();
    }
    public List<Assignment> compAssignmentsUnsub(Boolean b) {
        return cAssQueryUnsub(b).findList();
    }
    public List<Assignment> assignmentMarksCorrection(Boolean b) {
        return cAssQueryUnsub(b).findList();
    }
    public int compAssignInt(boolean b){
        return cAssQuery(b)
                .findRowCount();
    }
    private ExpressionList<Assignment> cAssQuery(boolean b){
        return Assignment.finder.where()
                .eq("deleteStatus", false)
                .eq("grouped", b)
                .eq("component.id", this.id);
    }
    private ExpressionList<Assignment> cAssQueryUnsub(boolean b){
        return Assignment.finder.where()
                .eq("deleteStatus", false)
                .eq("component.id", this.id);
    }
    @JsonProperty
    public String print1(){
        return compName;
    }
    public static List<Component> allBy(String q)
    {
        String[] arr = q.split(Vld.split);
        if (!q.equals("") && (arr.length <= 1)) {
            return finder.where()
                    .not(Expr.eq("deleteStatus", true))
                    .like("compName", "%" + q + "%")
                    .orderBy("id desc")
                    .findList();
        }
        int a = (arr.length > 1 && Vld.isNumeric(arr[1])) ? Integer.parseInt(arr[1]) : 0;
        return allPage(a);
    }
    public static List<Component> allPage(int a)
    {
        int ap = (a - 1) * Vld.limit;
        return finder.where()
                .not(Expr.eq("deleteStatus", true))
                .setFirstRow(ap)
                .setMaxRows(Vld.limit)
                .orderBy("id desc").findList();
    }
}
