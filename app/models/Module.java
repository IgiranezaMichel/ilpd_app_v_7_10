package models;
import com.avaje.ebean.Expr;
import com.avaje.ebean.Expression;
import com.fasterxml.jackson.annotation.JsonProperty;
import controllers.Counts;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Module extends Model
{
    @Id
    public long id;
    public String moduleName = "";
    public String moduleCode = "";
    public int orderNumber = 0;
    public int credits = 0;
    public int catMax = 0;
    public int examMax = 0;
    public int reMax = 0;
    public String moduleInternship = "module";
    public double reseatResearchMax;
    public double minMarks;
    public double resitAmount;
    public double retakeAmount;

    public Boolean hasComponent = true;

    @ManyToOne(cascade = CascadeType.ALL)
    public Lecture lecture;



    @ManyToOne(cascade = CascadeType.ALL)
    public Program program;


    public Boolean deleteStatus = false;

    public static Model.Finder<Long, Module> find = new Finder<Long, Module>(Long.class, Module.class);
    public static Model.Finder<Long, Module> finder = new Finder<Long, Module>(Long.class, Module.class);

    public int totalMax()
    {
        return (catMax + examMax + reMax);
    }

    @JsonProperty
    public String print(){
        return this.moduleName;
    }

    public static String gradeForSum(float val)
    {
        return (val > 90) ? "A" : (val > 80) ? "B" : (val > 60) ? "C" : "FAIL";
    }



    public static Expression cleExpr(boolean b){
        return Expr.eq("program.id",b);
    }


    public static int number(boolean b){
        return Module.finder.where().add(cleExpr(b)).findRowCount();
    }
    public double allModuleAverage(Long sId)
    {
        Student student = Student.finderById(sId);

        double result = 0;

            // C, R & E
            if (AssignmentResult.numberAssignmentDone(sId, this.id, student.training.id) > 0.0 && AssignmentResult.numberResearchDone(student.id, this.id, student.training.id) > 0.0 && Counts.getExamMaxTrainingModule(student.training.id, this.id) > 0.0) {
                result = (Counts.getStudentCatMarksModule(sId, this.id) + Counts.getStudentResearchMarksModule(sId, this.id) + SubMark.componetExamResultModule(sId, this.id, student.training.id));
            } else if ((AssignmentResult.numberAssignmentDone(sId, this.id, student.training.id) > 0.0 && AssignmentResult.numberResearchDone(sId, this.id, student.training.id) <= 0.0 && Counts.getExamMaxTrainingModule(student.training.id, this.id) > 0.0)) {
                // C & E
                result = (((Counts.getStudentCatMarksModule(sId, this.id) + SubMark.componetExamResultModule(sId, this.id, student.training.id)) * 100) / (this.catMax + this.examMax));
            } else if (AssignmentResult.numberAssignmentDone(sId, this.id, student.training.id) > 0.0 && AssignmentResult.numberResearchDone(sId, this.id, student.training.id) > 0.0 && Counts.getExamMaxTrainingModule(student.training.id, this.id) <= 0.0) {
                // C & R
                result = (((Counts.getStudentCatMarksModule(sId, this.id) + Counts.getStudentResearchMarksModule(sId, this.id)) * 100) / (this.catMax + this.reMax));
            } else if (AssignmentResult.numberAssignmentDone(sId, this.id, student.training.id) > 0.0 && AssignmentResult.numberResearchDone(sId, this.id, student.training.id) <= 0.0 && Counts.getExamMaxTrainingModule(student.training.id, this.id) <= 0.0) {
                // C
                result = ((Counts.getStudentCatMarksModule(sId, this.id) * 100) / this.catMax);
            } else if (AssignmentResult.numberAssignmentDone(sId, this.id, student.training.id) <= 0.0 && AssignmentResult.numberResearchDone(sId, this.id, student.training.id) > 0.0 && Counts.getExamMaxTrainingModule(student.training.id, this.id) > 0.0) {
                // R & E
                result = (((Counts.getStudentResearchMarksModule(sId, this.id) + SubMark.componetExamResultModule(sId, this.id, student.training.id)) * 100) / (this.reMax + this.examMax));
            } else if (AssignmentResult.numberAssignmentDone(sId, this.id, student.training.id) <= 0.0 && AssignmentResult.numberResearchDone(sId, this.id, student.training.id) > 0.0 && Counts.getExamMaxTrainingModule(student.training.id, this.id) > 0.0) {
                // R
                result = ((Counts.getStudentResearchMarksModule(sId, this.id) * 100) / this.reMax);
            } else if (AssignmentResult.numberAssignmentDone(sId, this.id, student.training.id) <= 0.0 && AssignmentResult.numberResearchDone(sId, this.id, student.training.id) <= 0.0 && Counts.getExamMaxTrainingModule(student.training.id, this.id) > 0.0) {
                // E
                result = ((SubMark.componetExamResultModule(sId, this.id, student.training.id) * 100) / this.examMax);
            } else {
                result = 0;
            }
        return result;
    }
    public double alllyModuleAverageResit(Long sId)
    {
        Student student = Student.finderById(sId);
        double result = 0;
        if (AssignmentResult.numberResearchDoneResit(sId, this.id, student.training.id) > 0.0 && Counts.getExamMaxTrainingModuleResit(student.training.id, this.id) > 0.0) {
                // R & E
                result = (((Counts.getStudentResearchMarksModuleResit(sId, this.id) + SubMark.componetExamResultModuleResit(sId, this.id, student.training.id)) * 100) / (this.reMax + this.examMax));
            } else {
             result = 0;
            }
        return result;
    }
    public double allResitModuleAverage(Long sId)
    {
        Student student = Student.finderById(sId);
        double result = 0;

        // C, R & E
         if(SubMark.byDualInResitModule(sId, this.id) == null && AssignmentResult.numberAssignmentDone(sId, this.id, student.training.id) > 0.0 && AssignmentResult.numberResearchDone(student.id, this.id, student.training.id)> 0.0 && Counts.getExamMaxTrainingModule(student.training.id, this.id) > 0.0) {
            result =  (Counts.getStudentCatMarksModule(sId, this.id) + Counts.getStudentResearchMarksModule(sId, this.id) + SubMark.componetExamResultModule(sId, this.id, student.training.id));
            }else if((AssignmentResult.numberAssignmentDone(sId, this.id, student.training.id) > 0.0 && AssignmentResult.numberResearchDone(sId, this.id, student.training.id) <= 0.0 &&  Counts.getExamMaxTrainingModule(student.training.id, this.id) > 0.0)){
         // C & E
             result = (((Counts.getStudentCatMarksModule(sId, this.id) + SubMark.componetExamResultModule(sId, this.id, student.training.id)) * 100) / (this.catMax + this.examMax));
         }else if(SubMark.byDualInResitModule(sId, this.id) == null && AssignmentResult.numberAssignmentDone(sId, this.id, student.training.id) > 0.0 && AssignmentResult.numberResearchDone(sId, this.id, student.training.id)> 0.0 &&  Counts.getExamMaxTrainingModule(student.training.id, this.id) <= 0.0){
             // C & R
             result = (((Counts.getStudentCatMarksModule(sId, this.id) + Counts.getStudentResearchMarksModule(sId, this.id)) * 100) / (this.catMax + this.reMax));
         }else if(SubMark.byDualInResitModule(sId, this.id) == null && AssignmentResult.numberAssignmentDone(sId, this.id, student.training.id) > 0.0 && AssignmentResult.numberResearchDone(sId, this.id, student.training.id) <= 0.0 &&  Counts.getExamMaxTrainingModule(student.training.id, this.id) <= 0.0){
             // C
             result = ((Counts.getStudentCatMarksModule(sId, this.id) * 100) / this.catMax);
         }else if(SubMark.byDualInResitModule(sId, this.id) == null && AssignmentResult.numberAssignmentDone(sId, this.id, student.training.id) <= 0.0 && AssignmentResult.numberResearchDone(sId, this.id, student.training.id)> 0.0 &&  Counts.getExamMaxTrainingModule(student.training.id, this.id) > 0.0){
             // R & E
             result = (((Counts.getStudentResearchMarksModule(sId, this.id) + SubMark.componetExamResultModule(sId, this.id, student.training.id)) * 100) / (this.reMax + this.examMax));
         }else if(SubMark.byDualInResitModule(sId, this.id) == null && AssignmentResult.numberAssignmentDone(sId, this.id, student.training.id) <= 0.0 && AssignmentResult.numberResearchDone(sId, this.id, student.training.id)> 0.0 &&  Counts.getExamMaxTrainingModule(student.training.id, this.id) > 0.0){
             // R
             result = ((Counts.getStudentResearchMarksModule(sId, this.id) * 100) / this.reMax);
         }else if(SubMark.byDualInResitModule(sId, this.id) == null && AssignmentResult.numberAssignmentDone(sId, this.id, student.training.id) <= 0.0 && AssignmentResult.numberResearchDone(sId, this.id, student.training.id) <= 0.0 &&  Counts.getExamMaxTrainingModule(student.training.id, this.id) > 0.0){
             // E
             result = ((SubMark.componetExamResultModule(sId, this.id, student.training.id) * 100) / this.examMax);
         }else if(SubMark.byDualInResitModule(sId, this.id) != null){
             // RE-SIT
             if(((SubMark.componetExamResultModule(sId, this.id, student.training.id) * 100) / this.examMax) < ProfileInfo.unique().scoreThree) {
                 result = ((SubMark.componetExamResultModule(sId, this.id, student.training.id) * 100) / this.examMax);
             }else{
                 result = ProfileInfo.unique().scoreThree;
             }
         }else{
             result = 0;
         }
        return result;
    }
    public Boolean exist()
    {
        Module m = find.where().eq("deleteStatus", false).eq("moduleCode", this.moduleCode).eq("program.id", this.program.id).setMaxRows(1).orderBy("orderNumber asc").findUnique();
        return (m != null);
    }
    public List<Component> myComp()
    {
        return Component.finder.where()
                .eq("deleteStatus", false)
                .eq("module.id", this.id)
                .findList();
    }
    public static List<Module> hasComp()
    {
        return find.where().eq("deleteStatus", false).eq("hasComponent", false).findList();
    }
    public List<Student> moduleStudents()
    {
        return Student.finder.where().eq("deleteStatus", false).eq("training.iMode.campusProgram.program.id", this.program.id).findList();
    }
    public List<Student> moduleStudents(Long lectureId)
    {
        return Student.finder.where().eq("deleteStatus", false).eq("training.iMode.campusProgram.program.id", this.program.id).in("training.id",Schedule.find.where().eq("component.module.id",this.id).eq("lecture.id",lectureId).select("training.id")).findList();
    }
    public static List<Module> allBy(String q)
    {
        String[] arr = q.split(Vld.split);
        if (!q.equals("") && (arr.length <= 1)) {
            return finder.where().or(
                    Expr.like("moduleName", "%" + q + "%"),
                    Expr.like("program.programName",  "%" + q + "%")
            )
                    .not(Expr.eq("deleteStatus", true))
                    .orderBy("orderNumber asc")
                    .findList();
        }
        int a = (arr.length > 1 && Vld.isNumeric(arr[1])) ? Integer.parseInt(arr[1]) : 0;
        return allPage(a);
    }

    public static String count()
    {
        int api = all().size();
        return String.valueOf((api > 0) ? api : 1);
    }

    public static List<Module> all()
    {
        return find.where().not(Expr.eq("deleteStatus", true)).orderBy("orderNumber asc").findList();
    }

    public static List<Module> allPage(int a)
    {
        int ap = (a - 1) * Vld.limit;
        return find.where()
                .not(Expr.eq("deleteStatus", true))
                .setFirstRow(ap)
                .setMaxRows(Vld.limit)
                .orderBy("orderNumber asc").findList();
    }

    public static Module finderById(Long id)
    {
        return find.ref(id);
    }

    @JsonProperty
    public String print1(){
        return moduleName;
    }
    public int countOne() {
        return  finder.where()
                .eq("deleteStatus", false)
                .eq("id", this.id)
                .orderBy("orderNumber asc")
                .findRowCount();
    }
}
