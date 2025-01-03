package models;
import Helper.BaseModel;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.fasterxml.jackson.annotation.JsonProperty;
import models.stock.Item;
import play.db.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Entity
public class SubMark extends Model {
    @Id
    public long id;

    @ManyToOne(cascade = CascadeType.ALL)
    public Student student;
    @ManyToOne(cascade = CascadeType.ALL)
    public Component component;

    public float catResult = 0;
    public float reResult = 0;
    public float examResult = 0;

    public float claimCat = 0;
    public float claimRe = 0;
    public float claimExam = 0;
    public String comment = "";

    public Boolean isResitResult = false;
    public Boolean deleteStatus = false;
    public String doneBy = "";
    public Timestamp doneAt = new Timestamp(new Date().getTime());
    public static Finder<Long, SubMark> find = new Finder<>(Long.class, SubMark.class);
    public static BaseModel.Look<SubMark> on = new BaseModel.Look<>(SubMark.class);
    public static List<SubMark> all() {
        return find.where().not(Expr
                .eq("deleteStatus", true))
                .eq("component.deleteStatus", false)
                .eq("student.deleteStatus", false)
                .orderBy("id desc").findList();
    }

    public static SubMark mootCourtResults(Long sId,Long cId){
        Component component = Component.finderById(cId);
        return find.where()
                .eq("deleteStatus",false)
                // .eq("component.module.id", Module.finderById(component.module.id).id)
                .eq("component.module.moduleInternship", "Moot court")
                .eq("student.id",sId)
                .setMaxRows(1)
                .findUnique();
    }
    public static SubMark internshipResults(Long sId,Long cId){
        Component component = Component.finderById(cId);
        return find.where()
                .eq("deleteStatus",false)
                // .eq("component.module.id", Module.finderById(component.module.id).id)
                .eq("component.module.moduleInternship", "Internship")
                .eq("student.id",sId)
                .setMaxRows(1)
                .findUnique();
    }
    public static SubMark mootModule(Long sId,Long cId){
        Component component = Component.finderById(cId);
        return find.where()
                .eq("deleteStatus",false)
                // .eq("component.module.id", Module.finderById(component.module.id).id)
                .eq("component.module.moduleInternship", "module")
                .eq("student.id",sId)
                .setMaxRows(1)
                .findUnique();
    }
    public SubMark notExist(){
        return find.where().eq("deleteStatus",false)
                .eq("student.id",this.student.id)
                .eq("component.id",this.component.id)
                .setMaxRows(1).findUnique();
    }

    public static SubMark byDual(Long id,Long component){
        return find.where()
                .eq("deleteStatus",false)
                .eq("component.id",component)
                .eq("student.id",id)
                .setMaxRows(1)
                .findUnique();
    }
    public static SubMark byDualInResit(Long id,Long component){
        return find.where()
                .eq("deleteStatus",false)
                .eq("component.id",component)
                .eq("isResitResult",true)
                .eq("student.id",id)
                .setMaxRows(1)
                .findUnique();
    }
    public static SubMark byDualInResitModule(Long sId,Long cId){
        Component component = Component.finderById(cId);
        return find.where()
                .eq("deleteStatus",false)
                .eq("component.module.id", Module.finderById(component.module.id).id)
                .eq("isResitResult",true)
                .eq("student.id",sId)
                .setMaxRows(1)
                .findUnique();
    }
    @JsonProperty
    public String print(){
        return this.student.print();
    }
    public static List<SubMark> allPerModule(long modId, long sId) {
        return find.setDistinct(true)
                .where("deleteStatus='false' and student.deleteStatus='false' and component.deleteStatus='false' and component.module.deleteStatus='false' and component.module.id = '"+modId+"' and student.id = '"+sId+"' group by component.id").findList();
    }
    public static double componetExamResult(long sId, long compId) {
        double total = 0;
        total = componetExamResults(sId,compId);
        return total;
    }
    public static double componetExamResultResearch(long sId, long compId) {
        double total = 0;
        total = componetExamResultsResearch(sId,compId);
        return total;
    }
    public static double componetExamResultModuleResearch(long sId, long mId) {
        double total = 0;
        total = componetExamResultsModuleResearch(sId,mId);
        return total;
    }
    public static double componentsPerModule(long mId) {
        String s = "SELECT DISTINCT IFNULL(COUNT(c.id),0.0) as total FROM component c inner join module m on c.module_id = m.id WHERE m.id=:mid and m.delete_status = false and c.delete_status = false";
        double total = Ebean.createSqlQuery(s)
                .setParameter("mid", mId)
                .findUnique()
                .getDouble("total");
        return total;
    }
    public static double moduleExamResult(long sId, long mId) {
        double total = 0;
        total = componetExamResultsResearch(sId,mId);
        return total;
    }
    public static double componetExamResults(long sId, long compId) {
        String s = "SELECT IFNULL(SUM(DISTINCT(sub.exam_result * m.exam_max)/cm.exam_max),0.0) as total FROM sub_mark sub INNER JOIN student s ON sub.student_id = s.id INNER JOIN component c ON sub.component_id = c.id inner join module mm on c.module_id = mm.id INNER JOIN component_max cm ON cm.component_id = sub.component_id inner join training t on cm.training_id = t.id INNER JOIN module m ON c.module_id = m.id WHERE t.id = s.training_id and sub.student_id=:sid AND sub.component_id=:cid and c.delete_status = false and sub.delete_status = false and c.delete_status = false";
        double total = Ebean.createSqlQuery(s)
                .setParameter("sid", sId)
                .setParameter("cid", compId)
                .findUnique()
                .getDouble("total");
        return total;
    }
    public static double componetExamResultsResearch(long sId, long compId) {
        String s = "SELECT IFNULL(SUM(DISTINCT(sub.exam_result * m.min_marks)/cm.resit_max),0.0) as total FROM sub_mark sub INNER JOIN student s ON sub.student_id = s.id INNER JOIN component c ON sub.component_id = c.id INNER JOIN component_max cm ON cm.component_id = sub.component_id inner join training t on cm.training_id = t.id INNER JOIN module m ON c.module_id = m.id WHERE s.in_resit = true and t.id = s.training_id and sub.is_resit_result = 1 and sub.component_id =:cid and sub.student_id=:sid and c.delete_status = false and sub.delete_status = false and c.delete_status = false and m.delete_status = false";
        double total = Ebean.createSqlQuery(s)
                .setParameter("sid", sId)
                .setParameter("cid", compId)
                .findUnique()
                .getDouble("total");
        return total;
    }
    public static double componetExamResultsModuleResearch(long sId, long mId) {
        String s = "SELECT IFNULL(SUM(DISTINCT(sub.exam_result * m.min_marks)/cm.resit_max),0.0) as total FROM sub_mark sub INNER JOIN student s ON sub.student_id = s.id INNER JOIN component c ON sub.component_id = c.id INNER JOIN component_max cm ON cm.component_id = sub.component_id inner join training t on cm.training_id = t.id INNER JOIN module m ON c.module_id = m.id WHERE s.in_resit = true and t.id = s.training_id and sub.is_resit_result = 1 and m.id =:mid and sub.student_id=:sid and c.delete_status = false and sub.delete_status = false and c.delete_status = false and m.delete_status = false";
        double total = Ebean.createSqlQuery(s)
                .setParameter("sid", sId)
                .setParameter("mid", mId)
                .findUnique()
                .getDouble("total");
        return total;
    }
    public static double totalExamDoneModules(long sId,long mId, long tid) {
        String s = "SELECT IFNULL(COUNT(m.id),0.0) as total FROM component_max c INNER JOIN component comp ON c.component_id=comp.id inner join module m on comp.module_id = m.id inner join training t on c.training_id = t.id WHERE t.id =:tid and m.id=:mid and c.exam_max > 0.0 and m.delete_status = false";
        double total = Ebean.createSqlQuery(s)
                .setParameter("sid", sId)
                .setParameter("mid", mId)
                .setParameter("tid", tid)
                .findUnique()
                .getDouble("total");
        return total;
    }
    public static double componetExamResultModules(long sId, long mId) {
        String s = "SELECT IFNULL(SUM(DISTINCT(sub.exam_result * m.exam_max)/cm.exam_max),0.0) as total FROM sub_mark sub INNER JOIN student s ON sub.student_id = s.id INNER JOIN component c ON sub.component_id = c.id inner join module md on c.module_id = md.id INNER JOIN component_max cm ON cm.component_id = sub.component_id inner join training t on cm.training_id = t.id INNER JOIN module m ON c.module_id = m.id WHERE t.id = s.training_id and sub.student_id=:sid AND md.id=:cid and c.delete_status = false and sub.delete_status = false and c.delete_status = false";
        double total = Ebean.createSqlQuery(s)
                .setParameter("sid", sId)
                .setParameter("cid", mId)
                .findUnique()
                .getDouble("total");
        return total;
    }
    public static double componetExamResultModule(long sId, long mId, long tId) {
        double total = 0;
        double sub = 0;
        if(totalExamDoneModules(sId,mId,tId) <= 0){
            sub = 1;
        }else{
            sub = totalExamDoneModules(sId,mId,tId);
        }
        total = componetExamResultModules(sId,mId)/sub;
        return total;
    }
    public static double componetExamResultModuleBefore(long sId, long mId, long tId) {
        double total = 0;
        double sub = 0;
        if(totalExamDoneModules(sId,mId,tId) <= 0){
            sub = 1;
        }else{
            sub = totalExamDoneModules(sId,mId,tId);
        }
        total = componetExamResultModulesBefore(sId,mId)/sub;
        return total;
    }
    public static double componetExamResultModuleResit(long sId, long mId, long tId) {
        double total = 0;
        double sub = 0;
        if(totalExamDoneModulesResit(sId,mId,tId) <= 0){
            sub = 1;
        }else{
            sub = totalExamDoneModulesResit(sId,mId,tId);
        }
        total = componetExamResultModules(sId,mId)/sub;
        return total;
    }
    public static double reSitExamResultModule(long sId, long mId) {
        double result = 0;
        double numberComp = 0;
        if(totalResitExamDoneModules(mId) <= 0){
            numberComp = 1;
        }else{
            numberComp = totalResitExamDoneModules(mId);
        }
        result = (resitExamResultModules(sId,mId) * 100 )/(numberComp * examMaxPerModule(mId));
        return result;
    }
    public static double reSitExamResultModuleBefore(long sId, long mId) {
        double result = 0;
        double numberComp = 0;
        if(totalResitExamDoneModules(mId) <= 0){
            numberComp = 1;
        }else{
            numberComp = totalResitExamDoneModules(mId);
        }
        result = (resitExamResultModulesBefore(sId,mId) * 100 )/(numberComp * examMaxPerModule(mId));
        return result;
    }
    public static Float totalPerStudentAssignment(Long sId, Long cId){
        String s = "SELECT SUM(s.cat_result) as total FROM sub_mark s INNER JOIN student st ON s.student_id=st.id inner join component c on s.component_id = c.id where st.id = "+sId+" and c.id="+cId+" ";
        return Ebean.createSqlQuery(s).findUnique().getFloat("total");
    }
    public static double componetExamResultModulesBefore(long sId, long mId) {
        String s = "SELECT IFNULL(SUM(DISTINCT(sub.exam_result * m.exam_max)/cm.exam_max),0.0) as total FROM sub_mark sub INNER JOIN student s ON sub.student_id = s.id INNER JOIN component c ON sub.component_id = c.id inner join module md on c.module_id = md.id INNER JOIN component_max cm ON cm.component_id = sub.component_id inner join training t on cm.training_id = t.id INNER JOIN module m ON c.module_id = m.id WHERE t.id = s.training_id and sub.is_resit_result = false and sub.student_id=:sid AND md.id=:cid and c.delete_status = false and sub.delete_status = false and c.delete_status = false";
        double total = Ebean.createSqlQuery(s)
                .setParameter("sid", sId)
                .setParameter("cid", mId)
                .findUnique()
                .getDouble("total");
        return total;
    }
    public static double resitExamResultModules(long sId, long mId) {
        String s = "SELECT IFNULL(SUM(DISTINCT(sub.exam_result * m.exam_max)/cm.resit_max),0.0) as total FROM sub_mark sub INNER JOIN student s ON sub.student_id = s.id INNER JOIN component c ON sub.component_id = c.id inner join module md on c.module_id = md.id INNER JOIN component_max cm ON cm.component_id = sub.component_id inner join training t on cm.training_id = t.id INNER JOIN module m ON c.module_id = m.id WHERE t.id = s.training_id and sub.student_id=:sid and s.status = \"RE-SIT\" AND md.id=:cid and sub.is_resit_result = true and c.delete_status = false and sub.delete_status = false and c.delete_status = false";
        double total = Ebean.createSqlQuery(s)
                .setParameter("sid", sId)
                .setParameter("cid", mId)
                .findUnique()
                .getDouble("total");
        return total;
    }
    public static double resitExamResultModulesBefore(long sId, long mId) {
        String s = "SELECT IFNULL(SUM(DISTINCT(sub.exam_result * m.exam_max)/cm.resit_max),0.0) as total FROM sub_mark sub INNER JOIN student s ON sub.student_id = s.id INNER JOIN component c ON sub.component_id = c.id inner join module md on c.module_id = md.id INNER JOIN component_max cm ON cm.component_id = sub.component_id inner join training t on cm.training_id = t.id INNER JOIN module m ON c.module_id = m.id WHERE t.id = s.training_id and sub.student_id=:sid and sub.is_resit_result = false and s.status = \"RE-SIT\" AND md.id=:cid and sub.is_resit_result = true and c.delete_status = false and sub.delete_status = false and c.delete_status = false";
        double total = Ebean.createSqlQuery(s)
                .setParameter("sid", sId)
                .setParameter("cid", mId)
                .findUnique()
                .getDouble("total");
        return total;
    }
    public static double examMaxPerModule(long mId) {
        String s = "SELECT DISTINCT IFNULL(COUNT(m.exam_max),0.0) as total FROM module m WHERE m.id=:mid and m.delete_status = false";
        double total = Ebean.createSqlQuery(s)
                .setParameter("mid", mId)
                .findUnique()
                .getDouble("total");
        return total;
    }
    public static double totalExamDoneModulesResit(long sId,long mId, long tid) {
        String s = "SELECT IFNULL(SUM((sb.re_result * m.re_max)/cm.research_max)/COUNT(c.id),0) as total FROM sub_mark sb INNER JOIN student s ON sb.student_id=s.id INNER JOIN component c ON sb.component_id = c.id inner join component_max cm on cm.component_id = c.id INNER JOIN module m ON c.module_id = m.id WHERE s.id=:sid and s.training_id =:tid AND m.id=:mid and sb.re_result > 0";
        double total = Ebean.createSqlQuery(s)
                .setParameter("sid", sId)
                .setParameter("mid", mId)
                .setParameter("tid", tid)
                .findUnique()
                .getDouble("total");
        return total;
    }
    public static double totalResitExamDoneModules(long mId) {
        String s = "SELECT IFNULL(COUNT(c.id),0.0) as total FROM component c INNER JOIN module m ON c.module_id = c.id WHERE m.id =:mid and m.delete_status = false and c.delete_status = false";
        double total = Ebean.createSqlQuery(s)
                .setParameter("mid", mId)
                .findUnique()
                .getDouble("total");
        return total;
    }
    public static double totalExamDone(long sId, long modId) {
        String s = "SELECT DISTINCT IFNULL(COUNT(m.id),1) as total FROM sub_mark sub INNER JOIN student s ON sub.student_id = s.id INNER JOIN component c ON sub.component_id = c.id INNER JOIN module m ON c.module_id = m.id WHERE s.id=:sid AND m.id=:mid and sub.delete_status = false";
        double total = Ebean.createSqlQuery(s)
                .setParameter("sid", sId)
                .setParameter("mid", modId)
                .findUnique()
                .getDouble("total");
        return total;
    }
    public static boolean checkExist(Long sId, Long cId){
        boolean Exist = false;
        for(SubMark c : all()){
            if(!c.deleteStatus && c.student.id == sId && c.component.id == cId){
                Exist = true;
            }
        }
        return Exist;
    }
}
