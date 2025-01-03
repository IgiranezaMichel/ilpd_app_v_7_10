package models;

import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.fasterxml.jackson.annotation.JsonProperty;
import controllers.Counts;
import play.db.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;
import java.util.List;

@Entity
public class Training extends Model {
    @Id
    public long id;
    public String title = "";
    public String trainer = "";
    public Date startDate = null;
    public Date endDate = null;
    public Date startDateApplication = null;
    public Date endDateApplication = null;
    public int duration;
    // not longer supported
    public double schoolFees;
    // alternativer of above
    public double eacStudentTuitionFees;
    public double nonEacStudentTuitionFees;
    public  double minEacStudentTuitionFees;
    public double minNonEacStudentTuitionFees;
    public double accomodationFees;
    public double restaurationFees;
    public Double minPayment = null;
    public double otherFees = 0;
    public String otherFeesSpec = "";
    public Boolean status = false;
    public Boolean transcript = false;
    public Boolean transcriptPrint = false;
    public Boolean isClosed = false;
    public Boolean isClosedA = false;
    public Boolean isComponent = false;
    @ManyToOne(cascade = CascadeType.ALL)
    public IntakeSessionMode iMode;
    public Date graduation;
    public Boolean deleteStatus = false;
    public boolean hasGraduated = false;
    public boolean hasGraduatedReSit = false;
    public boolean hasGraduatedFinal = false;
    public Boolean isMarksEntered = false;

    public double sum(Applicant applicant) {
        double sum = schoolFees + otherFees;

        sum += applicant.needAccomodation ? accomodationFees : 0;
        sum += applicant.needCatering ? restaurationFees : 0;

        return sum;
    }
    public String totalEacStudentPayment(){
        return Counts.formatAmount(this.schoolFees + this.accomodationFees + this.restaurationFees + this.otherFees + this.iMode.intake.registrationFees+this.eacStudentTuitionFees);
    }
    public String totalNonEacStudentPayment(){
        return Counts.formatAmount( this.accomodationFees + this.restaurationFees + this.otherFees + this.iMode.intake.registrationFees+this.nonEacStudentTuitionFees);
    }
    public Boolean notExist() {
        Training t = find.where()
                .eq("deleteStatus", false)
                .eq("isClosed", false)
                .eq("iMode.id", this.iMode.id)
                .setMaxRows(1)
                .findUnique();
        return (t == null);
    }
    public String totalSum() {
        return Counts.formatAmount(this.schoolFees + this.accomodationFees + this.restaurationFees + this.otherFees + this.iMode.intake.registrationFees);
    }

    public String printProgram() {
        return (iMode != null && iMode.campusProgram != null && iMode.campusProgram.program != null) ? iMode.campusProgram.program.toString() : " no program found";
    }

    @JsonProperty
    public String print() {
        return this.title +" "+ this.iMode.print() + ( this.title !=null && !this.title.trim().isEmpty() ? " ("+this.title+")" : "");
    }
    private ExpressionList<Student> stQuery() {
        return Student.finder.where()
                .eq("training.id", this.id)
                .eq("deleteStatus", false);
    }
    private ExpressionList<Student> stQueryInResit() {
        return Student.finder.where()
                .eq("training.id", this.id)
                .eq("status", "RE-SIT")
                .eq("deleteStatus", false);
    }
    private ExpressionList<Student> stQueryFinal() {
        return Student.finder.where()
                .eq("deleteStatus", false)
                .eq("training.deleteStatus", false)
                .eq("training.id", this.id)
                .eq("status", "PASS");
    }
    public static List<Training> allActive() {
        return  find.where()
                .eq("isClosed", 0)
                .orderBy("id desc")
                .findList();
    }
    public static List<Training> allDeliberated() {
        return  find.where()
                .eq("hasGraduatedFinal", 1)
                .orderBy("id desc")
                .findList();
    }
    public List<Student> students() {
        return stQuery().findList();
    }
    public List<Student> studentsInResit() {
        return stQueryInResit().findList();
    }
    public List<Student> studentsFinal() {
        return stQueryFinal().findList();
    }
    public List<Student> Mystudents() {
        return Student.finder.where()
                .eq("training.id", this.id)
                .findList();
    }
    public List<Applied> applieds() {
        return Applied.finder.where()
                .eq("training.id", this.id)
                .eq("training.isClosed", false)
                .orderBy("id asc")
                .findList();
    }
    public int tStudents() {
        return stQuery().findRowCount();
    }
    public int tStudentsInResit() {
        return stQueryInResit().findRowCount();
    }
    public List<DateRange> myRanges() {
        return DateRange.finder.where()
                .eq("deleteStatus", false)
                .eq("hourSession.session.id", this.iMode.sessionMode.session.id)
                .findList();
    }
    public Schedule t(Long hour, Long day) {
        return Schedule.find.where()
                .eq("deleteStatus", false)
                .eq("training.id", this.id)
                .eq("training.isClosed", false)
                .eq("dateRangeHourSession.id", hour)
                .eq("daySession.id", day)
                .setMaxRows(1)
                .findUnique();
    }
    public List<DaySession> trainDays() {
        return DaySession.find.where()
                .eq("deleteStatus", false)
                .eq("day.deleteStatus", false)
                .eq("session.id", this.iMode.sessionMode.session.id)
                .orderBy("id desc")
                .findList();
    }
    

    public static List<Training> withoutCle() {
        return cleAbout(false);
    }

    public static List<Training> withCle() {
        return cleAbout(true);
    }

    public static List<Training> cleAbout(boolean b) {
        return find.where()
                .eq("iMode.campusProgram.program.cle", b)
                .eq("deleteStatus", false)
                .orderBy("id desc")
                .findList();
    }
    public static List<Training> cleAbout2(boolean b) {
        return find.where()
                .eq("iMode.campusProgram.program.cle", b)
                .eq("deleteStatus", false)
                .orderBy("id desc")
                .findList();
    }
     public static List<Training> trainingByIomode(long iomId, boolean b){
         return find.where()
                 .eq("iMode.campusProgram.program.cle", false)
                 .eq("iMode.id", iomId)
                 .eq("deleteStatus", false)
                 .orderBy("id desc")
                 .findList();

     }
    @JsonProperty
    public String printIntake() {
        return this.iMode.intake.print()+" "+this.title;
    }

    public long intakeId() {
        return this.iMode.intake.id;
    }

    public List<Component> myComp(Lecture lct) {
        return Component.finder.where().in("id", Schedule.find.where()
                .eq("training.id", this.id)
                .eq("lecture.id", lct.id)
                .select("component.id"))
                .orderBy("id desc")
                .findList();
    }
    public List<Component> myComponents() {
        return Component.finder.where().in("id", Schedule.find.where()
                .eq("training.id", this.id)
                .select("component.id"))
                .orderBy("id desc")
                .findList();
    }
    public static Model.Finder<Long, Training> find = new Finder<Long, Training>(Long.class, Training.class);
    public static List<Training> finderByDual(Long intakeId, Long sMode, Long campusProgramId,long userId) {
        return finderByDualExp(intakeId, sMode, campusProgramId)
                .not(Expr.in("id",Student.finder.where()
                .eq("applicant.user.id",userId)
                .select("training.id")))
                .eq("isClosed", 0)
                .eq("isClosedA", 0)
                .orderBy("id desc")
                .findList();
    }
    private static ExpressionList<Training> finderByDualExp(Long intakeId, Long sMode, Long campusProgramId) {
        return find.where()
                .eq("deleteStatus", false)
                .eq("isClosed", false)
                .eq("iMode.intake.id", intakeId)
                .eq("iMode.sessionMode.id", sMode)
                .eq("iMode.campusProgram.id", campusProgramId);
    }
    public static List<Training> byIntake(Long id) {
        return find.where()
                .eq("deleteStatus", false)
                .eq("isClosed", false)
                .eq("isClosedA", false)
                .eq("iMode.intake.id", id)
                .orderBy("id desc")
                .findList();
    }
    public static List<Training> allBy() {
            return find.where().not(Expr.eq("deleteStatus", true)).orderBy("id desc")
                    .orderBy("id desc")
                    .findList();
    }
    public static List<Training> allBy(String q)
    {
        String[] arr = q.split(Vld.split);
        if (!q.equals("") && (arr.length <= 1)) {
            return find.where()
                    .not(Expr.eq("deleteStatus", true))
                    .like("iMode.intake.intakeName", "%" + q + "%")
                    .orderBy("id desc")
                    .findList();
        }
        int a = (arr.length > 1 && Vld.isNumeric(arr[1])) ? Integer.parseInt(arr[1]) : 0;
        return allPage(a);
    }
    public static List<Training> allPage(int a)
    {
        int ap = (a - 1) * Vld.limit;
        return find.where()
                .not(Expr.eq("deleteStatus", true))
                .setFirstRow(ap)
                .setMaxRows(Vld.limit)
                .orderBy("id desc").findList();
    }
    public static String count() {
        int api = all().size();
        return String.valueOf((api > 0) ? api : 1);
    }

    public static List<Training> all() {
        return find.where()
                .eq("deleteStatus", false)
                .eq("isClosedA", false)
                .eq("iMode.sessionMode.session.deleteStatus", false)
                .eq("iMode.sessionMode.deleteStatus", false)
                .eq("iMode.intake.deleteStatus", false)
                .eq("iMode.intake.isClosed", false)
                .eq("iMode.sessionMode.mode.deleteStatus", false)
                .eq("iMode.campusProgram.program.deleteStatus", false)
                .eq("iMode.campusProgram.campus.deleteStatus", false)
                .orderBy("id desc").findList();
    }
    public static List<Training> allOpen() {
        return find.where()
                .eq("deleteStatus", false)
                .eq("iMode.sessionMode.session.deleteStatus", false)
                .eq("iMode.sessionMode.deleteStatus", false)
                .eq("iMode.intake.deleteStatus", false)
                .eq("iMode.sessionMode.mode.deleteStatus", false)
                .eq("iMode.campusProgram.program.deleteStatus", false)
                .eq("iMode.campusProgram.campus.deleteStatus", false)
                .eq("isClosedA", false)
                .orderBy("id desc").findList();
    }
    public static List<Training> allOpenRegistrar() {
        return find.where()
                .eq("deleteStatus", false)
                .eq("iMode.sessionMode.session.deleteStatus", false)
                .eq("iMode.sessionMode.deleteStatus", false)
                .eq("iMode.intake.deleteStatus", false)
                .eq("iMode.sessionMode.mode.deleteStatus", false)
                .eq("iMode.campusProgram.program.deleteStatus", false)
                .eq("iMode.campusProgram.program.cle", false)
                .eq("iMode.campusProgram.campus.deleteStatus", false)
                .eq("isClosedA", false)
                .orderBy("id desc").findList();
    }
    public static List<Training> allOpenRegistrarR() {
        return find.where()
                .eq("deleteStatus", false)
                .eq("iMode.sessionMode.session.deleteStatus", false)
                .eq("iMode.sessionMode.deleteStatus", false)
                .eq("iMode.intake.deleteStatus", false)
                .eq("iMode.sessionMode.mode.deleteStatus", false)
                .eq("iMode.campusProgram.program.deleteStatus", false)
                .eq("iMode.campusProgram.program.cle", false)
                .eq("iMode.campusProgram.campus.deleteStatus", false)
                .eq("isClosedA", false)
                .orderBy("id desc").findList();
    }
    public static List<Training> allOpenCle() {
        return find.where()
                .eq("deleteStatus", false)
                .eq("iMode.sessionMode.session.deleteStatus", false)
                .eq("iMode.sessionMode.deleteStatus", false)
                .eq("iMode.intake.deleteStatus", false)
                .eq("iMode.sessionMode.mode.deleteStatus", false)
                .eq("iMode.campusProgram.program.deleteStatus", false)
                .eq("iMode.campusProgram.program.cle", true)
                .eq("iMode.campusProgram.campus.deleteStatus", false)
                .eq("isClosedA", false)
                .orderBy("id desc").findList();
    }
    public static List<Training> allZ() {
        return find.where()
                .eq("deleteStatus", false)
                .eq("iMode.sessionMode.session.deleteStatus", false)
                .eq("iMode.sessionMode.deleteStatus", false)
                .eq("iMode.intake.deleteStatus", false)
                .eq("iMode.sessionMode.mode.deleteStatus", false)
                .eq("iMode.campusProgram.program.deleteStatus", false)
                .eq("iMode.campusProgram.campus.deleteStatus", false)
                .orderBy("id desc").findList();
    }
    public static List<Training> allDTR() {
        return find.where()
                .eq("deleteStatus", false)
                .eq("isClosed", false)
                .eq("iMode.sessionMode.session.deleteStatus", false)
                .eq("iMode.sessionMode.deleteStatus", false)
                .eq("iMode.intake.deleteStatus", false)
                .eq("iMode.sessionMode.mode.deleteStatus", false)
                .eq("iMode.campusProgram.program.cle", true)
                .eq("iMode.campusProgram.campus.deleteStatus", false)
                .orderBy("id desc")
                .findList();
    }
    public static Training finderById(Long id) {
        return find.byId(id);
    }
    @JsonProperty
    public String tNames() {
        return this.iMode.print()+" "+this.title;
    }
    public static List<Training> byProgram(Long id) {
        return find.where()
                .eq("deleteStatus", false)
                .eq("iMode.campusProgram.program.id", id)
                .eq("isClosed", false)
                .eq("isClosedA", false)
                .orderBy("id desc")
                .findList();
    }
    public List<Groups> myGroups() {
        return Groups.find.where()
                .eq("deleteStatus", false)
                .eq("training.id", this.id)
                .orderBy("id desc")
                .findList();
    }
}
