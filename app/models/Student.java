package models;

import Helper.ReportProperty;
import com.avaje.ebean.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import controllers.Counts;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Entity
@ReportProperty(name = "Student reports")
public class Student extends BaseModel {
    public String firstName = "";
    public String familyName = "";
    public String regNo = "";
    public String academicEmail = "";
    public String gradeP = "";
    public Double gradePoint = 0.0;

    @OneToOne
    public Applicant applicant;

    @ManyToOne
    public Training training;

    @ManyToOne
    public Training trainingPrevious;
    public String status;
    public boolean isSecond = false;
    public boolean inResit = false;
    public boolean inRetake = false;
    public boolean viewMarks = false;
    public String emailStatus = "pending";
    public int failCount = 0;
    public Boolean deleteStatus = false;


    @Override
    public String toString() {
        return this.familyName + " " + this.firstName;
    }
    public String toString2() {
        return this.regNo;
    }

    @JsonProperty
    public String print() {
        return this.toString();
    }

    public static Model.Finder<Long, Student> finder = new Finder<>(Long.class, Student.class);
    public static  List<Student> byTraining(long trId) {
        return Student.finder.where().or(
                com.avaje.ebean.Expr.like("training.id", "%" + trId + "%"),
                com.avaje.ebean.Expr.like("trainingPrevious.id",  "%" + trId + "%")
                )
                .eq("deleteStatus", false)
                .findList();
    }
    public static  List<Student> byTrainingInResit(long trId) {
        return Student.finder.where().or(
                com.avaje.ebean.Expr.like("training.id", "%" + trId + "%"),
                com.avaje.ebean.Expr.like("trainingPrevious.id",  "%" + trId + "%")
             )
                .eq("deleteStatus", false)
                .eq("training.deleteStatus", false)
                .eq("status", "RE-SIT")
                .findList();
    }

    public Boolean notExist() {
        Student dep = finder.where()
                .eq("deleteStatus", false)
                .eq("id", this.id)
                .setMaxRows(1)
                .findUnique();
        return (dep == null);
    }

    public static  List<Student> byTrainingInResitTrue(long trId) {
        return Student.finder.where().or(
                com.avaje.ebean.Expr.like("training.id", "%" + trId + "%"),
                com.avaje.ebean.Expr.like("trainingPrevious.id",  "%" + trId + "%")
             )
                .eq("deleteStatus", false)
                .eq("training.deleteStatus", false)
                .eq("inResit", true)
                .findList();
    }
    public static  List<Student> byTrainingDeliberation(long trId) {
        return Student.finder.where().or(
                com.avaje.ebean.Expr.like("training.id", "%" + trId + "%"),
                com.avaje.ebean.Expr.like("trainingPrevious.id",  "%" + trId + "%")
        )
                .eq("deleteStatus", false)
                .not(Expr.eq("status", "active"))
                .not(Expr.eq("status", "RE-SIT"))
                .not(Expr.eq("status", "RETAKE-MODULES"))
                .not(Expr.eq("status", "ETAKE-PROGRAM"))
                .not(Expr.eq("status", "FAIL"))
                .findList();
    }
    public static  List<Student> reportByTrainingStatus(long trId, String status) {
        return Student.finder.where().or(
                com.avaje.ebean.Expr.like("training.id", "%" + trId + "%"),
                com.avaje.ebean.Expr.like("trainingPrevious.id",  "%" + trId + "%")
        )
                .eq("deleteStatus", false)
                .eq("status", status)
                .findList();
    }

    public List<SqlRow> myGroupAssignments() {
        String sql = "SELECT * FROM `assignment` WHERE assignment.training_id=" + this.training.id + " AND grouped = 1 AND (group_id IS NULL OR  group_id IN(SELECT groups_id FROM group_members WHERE group_members.student_id=" + this.id + ")) AND id NOT IN(SELECT group_submission.assignment_id FROM group_submission WHERE group_submission.status ='submitted' and group_submission.student_id = "+this.id+")";
        SqlQuery sqlQuery = Ebean.createSqlQuery(sql);
        return sqlQuery.findList();
    }
    public List<SqlRow> myGroupAssignmentsResearch() {
        String sql = "SELECT * FROM `assignment` WHERE assignment.training_id=" + this.training.id + " and assignment.types=\"resitResearch\" AND grouped = 1 AND (group_id IS NULL OR  group_id IN(SELECT groups_id FROM group_members WHERE group_members.student_id=" + this.id + ")) AND id NOT IN(SELECT group_submission.assignment_id FROM group_submission WHERE group_submission.status ='submitted' and group_submission.student_id = "+this.id+")";
        SqlQuery sqlQuery = Ebean.createSqlQuery(sql);
        return sqlQuery.findList();
    }
    public static List<SqlRow> notEvaluated(long comId, long trainId) {
        String sql = "SELECT DISTINCT reg_no as ger_number, first_name as fname, family_name as lname FROM student s INNER JOIN training t on s.training_id = t.id inner join schedule s2 on t.id = s2.training_id inner join component c on s2.component_id = c.id WHERE t.id =:trid AND c.id =:id AND s.id not in (select student_id from evaluation ev inner JOIN schedule st on ev.schedule_id = st.id inner join component c2 on st.component_id = c2.id where ev.student_id = s.id AND c2.id = c.id)";
        SqlQuery sqlQuery = Ebean.createSqlQuery(sql)
                .setParameter("id", comId)
                .setParameter("trid", trainId);

        return sqlQuery.findList();
    }
    public static List<SqlRow> notEvaluatedModule(long modId, long trainId) {
        String sql = "SELECT DISTINCT reg_no as ger_number, first_name as fname, family_name as lname FROM student s INNER JOIN training t on s.training_id = t.id inner join schedule s2 on t.id = s2.training_id inner join component c on s2.component_id = c.id inner join module md on c.module_id = md.id WHERE t.id =:trid AND md.id =:id AND s.id not in (select student_id from evaluation ev inner JOIN schedule st on ev.schedule_id = st.id inner join component c2 on st.component_id = c2.id inner join module md2 on c2.module_id = md2.id where ev.student_id = s.id AND md2.id = md.id)";
        SqlQuery sqlQuery = Ebean.createSqlQuery(sql)
                .setParameter("id", modId)
                .setParameter("trid", trainId);

        return sqlQuery.findList();
    }


    public List<SqlRow> myGroupAssignmentsTwo() {
        String sql = "SELECT * FROM `assignment` WHERE assignment.training_id=" + this.training.id + " AND grouped=1 AND (group_id IS NULL OR  group_id IN(SELECT groups_id FROM group_members WHERE group_members.student_id=" + this.id + ")) AND id IN(SELECT group_submission.assignment_id FROM group_submission WHERE group_submission.status ='submitted')";
        SqlQuery sqlQuery = Ebean.createSqlQuery(sql);

        return sqlQuery.findList();

    }
    public List<SqlRow> myGroupAssignmentsTwoResearch() {
        String sql = "SELECT * FROM `assignment` WHERE assignment.training_id=" + this.training.id + " AND assignment.types=\"resitResearch\" AND grouped=1 AND (group_id IS NULL OR  group_id IN(SELECT groups_id FROM group_members WHERE group_members.student_id=" + this.id + ")) AND id IN(SELECT group_submission.assignment_id FROM group_submission WHERE group_submission.status ='submitted')";
        SqlQuery sqlQuery = Ebean.createSqlQuery(sql);

        return sqlQuery.findList();

    }

    public Groups MyGroup(Assignment ass) {
        return (gM(ass) != null) ? gM(ass).groups : null;
    }

    public GroupMembers gM(Assignment ass) {
        return GroupMembers.finder.where().eq("deleteStatus", false).eq("student.id", this.id).eq("groups.deleteStatus", false).eq("groups.component.id", ass.component.id).setMaxRows(1).findUnique();
    }
    public List<RefundRequest> requestList(){
        return RefundRequest.query().eq("account.applicant.student.id",id).findList();
    }
    public List<Assignment> myIndividualAssignments() {
        return Assignment.finder.where()
                .eq("deleteStatus", false)
                .eq("training.id", this.training.id)
                .eq("component.module.program.id", this.training.iMode.campusProgram.program.id).eq("grouped", false).findList();
    }
    public List<Assignment> myIndividualAssignmentsReseach() {
        return Assignment.finder.where()
                .eq("deleteStatus", false)
                .eq("training.id", this.training.id)
                .eq("types", "resitResearch")
                .eq("component.module.program.id", this.training.iMode.campusProgram.program.id).eq("grouped", false).findList();
    }
    public static List<AssignmentResult> myIndividualAssignmentsResult(long sId) {
        return AssignmentResult.find.where().eq("student.id", sId).findList();
    }
    public SubMark myMarks(Long compId) {
        return SubMark.find.where()
                .eq("deleteStatus", false)
                .eq("student.id", this.id)
                .eq("component.id", compId)
                .setMaxRows(1)
                .findUnique();
    }
    public SubMark myMarksResit(Long compId) {
        return SubMark.find.where()
                .eq("deleteStatus", false)
                .eq("student.id", this.id)
                .eq("component.id", compId)
                .eq("isResitResult", 1)
                .setMaxRows(1)
                .findUnique();
    }
    public static int number(boolean b) {
        return Student.finder.where().add(cleExp(b)).findRowCount();
    }


    public static int numberMale(boolean b) {
        return Student.finder.where().add(cleExp(b)).eq("applicant.gender", "male").findRowCount();
    }

    public static int numberFemale(boolean b) {
        return Student.finder.where().add(cleExp(b)).eq("applicant.gender", "female").findRowCount();
    }
    public List<Module> myModules() {
        return Module.finder.where()
                .eq("deleteStatus", false)
                .eq("moduleInternship", "module")
                .eq("program.id", this.training.iMode.campusProgram.program.id)
                .findList();
    }
    public List<Component> myComponents() {
        return Component.finder.where()
                .eq("deleteStatus", false)
                .eq("module.moduleInternship", "module")
                .eq("module.program.id", this.training.iMode.campusProgram.program.id)
                .findList();
    }
    public List<Module> myPreviousModules() {
        return Module.finder.where()
                .eq("deleteStatus", false)
                .eq("moduleInternship", "module")
                .eq("program.id", this.trainingPrevious.iMode.campusProgram.program.id)
                .findList();
    }
    public List<Module> myOnlyModulesDeliberation() {
        return Module.finder.where()
                .eq("deleteStatus", false)
                .eq("program.id", this.training.iMode.campusProgram.program.id)
                .not(Expr.eq("moduleInternship", "Internship"))
                .findList();
    }
    public List<Module> myModuleInternshipDeliberation() {
        return Module.finder.where()
                .eq("deleteStatus", false)
                .eq("program.id", this.training.iMode.campusProgram.program.id)
                .findList();
    }
    public boolean hasReSeat() {
        List<Module> modules = this.myModules();
        for (Module module : modules) {
            if (this.modPercent(module) < module.minMarks) {
                return status.equalsIgnoreCase("re-seat") && this.failCount == 1;
            }
        }

        return false;
    }


    public boolean hasFail() {
        return grade("FAIL");
    }


    public boolean hasRetake() {
        List<Module> modules = this.myModules();
        for (Module module : modules) {
            if (this.modPercent(module) < module.minMarks) {
                return status.equalsIgnoreCase("retake") && this.failCount > 1;
            }
        }

        return false;
    }


    public List<Module> getReSeat() {
        List<Module> modules = this.myModules();
        List<Module> moduleList = new ArrayList<>();
        for (Module module : modules) {
            if (this.modPercent(module) < module.minMarks) {
                moduleList.add(module);
            }
        }
        return moduleList;
    }
    public Float isMyMarks(Long compId, String types) {
        SubMark m = myMarks(compId);
        Boolean bld = (myMarks(compId) != null);
        return (bld && types.equals("CAT")) ? m.catResult : (bld && types.equals("EX")) ? m.examResult : (bld) ? m.reResult : 0;
    }
    public Float isMyMarksResit(Long compId, Long sid) {
        SubMark m = myMarksResit(compId);
        if(m != null) {
            return m.examResult;
        }else{
        SubMark subMark = new SubMark();
        subMark.examResult = 0.0f;
        subMark.component = Component.finderById(compId);
        subMark.student = Student.finderById(sid);
        if(SubMark.checkExist(subMark.student.id, compId)){
            subMark.save();
        }
        return subMark.examResult;
        }
    }

    public Float totalMarks(Long compId) {
        SubMark m = myMarks(compId);
        Boolean bld = (myMarks(compId) != null);

        return (bld) ? (m.catResult + m.examResult + m.reResult) : 0;
    }

    public Float percent(Component component) {
        Float flt = (totalMarks(component.id) * 100) / component.module.totalMax();
        DecimalFormat f = new DecimalFormat("#0.00");
        return Float.valueOf(f.format(flt));
    }
    public double modPercent(Module module) {
        double flt = (module.allModuleAverage(this.id) * 100) / module.totalMax();
        return flt;
    }
    public static Expression cleExp(boolean isCLE) {
        return Expr.eq("training.iMode.campusProgram.program.cle", isCLE);
    }
    public Float totalForAll() {
        String query = "SELECT IFNULL(sum(sub_mark.cat_result+sub_mark.re_result+sub_mark.exam_result),0) as result FROM sub_mark where student_id=" + this.id;
        SqlRow row = Ebean.createSqlQuery(query).findUnique();
        return row.getFloat("result");
    }
    public Float maximum() {
        String query2 = "SELECT sum(module.re_max+module.cat_max+module.exam_max) as maximum FROM module,component where module.id=component.module_id";
        return Ebean.createSqlQuery(query2).findUnique().getFloat("maximum");
    }
    public String grade() {
        Float percent = percentValue();

        return hasRetake() ? "Re-take" : hasReSeat() ? "Re-seat" : percent >= 80 ? "DISTINCTION" : percent >= 70 ? "Merit" : percent >= 60 ? "PASS" : "FAIL";
    }
    public boolean grade(String grade) {
        Float percent = percentValue();

        String string = hasRetake() ? "Re-take" : hasReSeat() ? "Re-seat" : percent >= 60 ? "PASS" : "FAIL";

        return string.equals(grade) || "0".equals(grade);
    }
    private Float percentValue() {
        Float maximum = this.maximum();
        return (totalForAll() * 100) / maximum;
    }

    public String percentAll() {
        DecimalFormat f = new DecimalFormat("#0.00");
        Float value = percentValue();
        return f.format(value) + "%";
    }

    public static String rounding(double value) {
        DecimalFormat f = new DecimalFormat("#0.00");
        Double result = value;
        return f.format(result);
    }

    public String studentColor(Component component) {
        Float prc = percent(component);
        return (prc >= 80) ? "success" : (prc >= 65) ? "primary" : (prc >= 50) ? "warning" : "danger";
    }

    public String studentModuleColor(Module module) {
        double prc = modPercent(module);
        return (prc >= 80) ? "success" : (prc >= 65) ? "primary" : (prc >= 50) ? "warning" : "danger";
    }

    public static List<Student> all() {
        return finder.where().not(Expr.eq("deleteStatus", true)).eq("applicant.deleteStatus", false).orderBy("id desc").findList();
    }

    public static List<Student> all(String search, boolean cle) {
        return allSearch(search).add(cleExp(cle)).findList();
    }


    public static ExpressionList<Student> allSearch(String search) {
        return finder.where().not(Expr.eq("deleteStatus", true)).eq("applicant.deleteStatus", false).icontains("firstName", search).orderBy("id desc").where();
    }

    public static List<Student> all(String search) {
        return finder.where().not(Expr.eq("deleteStatus", true)).eq("applicant.deleteStatus", false).like("firstName", "%" + search + "%").orderBy("id desc").findList();
    }

    public static List<Student> all(int a) {
        return query()
                .eq("applicant.deleteStatus", false)
                .setMaxRows(a)
                .orderBy("id desc")
                .findList();
    }

    public static List<Student> allPendingEmail(int a) {
        return query()
                .eq("applicant.user.deleteStatus", true)
                .not(Expr.eq("academicEmail", ""))
                .eq("emailStatus", "pending")
                .setMaxRows(a)
                .orderBy("id desc")
                .findList();
    }
    public static String counts() {
        int api = finder.where()
                .eq("applicant.user.deleteStatus", true)
                .not(Expr.eq("academicEmail", ""))
                .eq("emailStatus", "pending")
                .findRowCount();
        return String.valueOf((api > 0) ? api : 1);
    }
    public static Student byApp(Long id) {
        return query().eq("applicant.id", id).setMaxRows(1).findUnique();
    }

    public static List<Student> byIntake(Long id) {
        return query().eq("training.iMode.intake.id", id).findList();
    }

    public static List<Student> byIntakeOrdered(Long id) {
        return query().eq("training.iMode.intake.id", id).findList();
    }

    private static ExpressionList<Student> query() {
        return finder.where().ne("deleteStatus", true);
    }

    public static int countIntake(Long intake) {
        return byIntake(intake).size();
    }


    public static List<Student> byIntake(Long id, boolean isCLE) {
        return byIntakeQuery(id, isCLE).orderBy("id desc").findList();
    }

    private static ExpressionList<Student> byIntakeQuery(Long id, boolean isCLE) {
        return finder.where().not(Expr.eq("deleteStatus", true)).eq("training.iMode.intake.id", id).add(cleExp(isCLE));
    }


    public static int countIntake(Long intake, boolean isCLE) {
        return byIntakeQuery(intake, isCLE).findRowCount();
    }

    public static Student finderById(Long id) {
        return finder.where()
                .eq("id", id)
                .setMaxRows(1)
                .findUnique();
    }
    public static Student byUserId(Long id) {
        return finder.where()
                .eq("applicant.user.id", id)
                .setMaxRows(1)
                .findUnique();
    }
    public static Student findByRegNo(String regno) {
        return Student.finder.where()
                .eq("regNo", regno)
                .eq("deleteStatus", false).findUnique();
    }
    public static Student findByRegNoByAppied(long aId) {
        return Student.finder.where()
                .eq("applicant.id", aId)
                .eq("deleteStatus", false)
                .findUnique();
    }


    public static Student findByRegNo(String regNo, boolean cle) {
        return Student.finder.where().eq("regNo", regNo)
                .eq("deleteStatus", false).add(cleExp(cle)).findUnique();
    }
    public static Student findByRegNo2(String regNo) {
        return Student.finder.where().eq("regNo", regNo)
                .eq("deleteStatus", false).findUnique();
    }
    public double allModuleAverage(Long mId)
    {
        Module module = Module.finderById(mId);
        // C, R & E
        if(AssignmentResult.numberAssignmentDone(this.id, mId, this.training.id) > 0.0 && AssignmentResult.numberResearchDone(this.id, mId, this.training.id)> 0.0 &&  Counts.getExamMaxTrainingModule(this.training.id,mId) > 0.0) {
            return (Counts.getStudentCatMarksModule(this.id, mId) + Counts.getStudentResearchMarksModule(this.id, mId) + SubMark.componetExamResultModule(this.id, mId, this.training.id));
        }else if((AssignmentResult.numberAssignmentDone(this.id, mId, this.training.id) > 0.0 && AssignmentResult.numberResearchDone(this.id, mId, this.training.id) <= 0.0 &&  Counts.getExamMaxTrainingModule(this.training.id,mId) > 0.0)){
            // C & E
            return (((Counts.getStudentCatMarksModule(this.id, mId) + SubMark.componetExamResultModule(this.id, mId, this.training.id)) * 100) / (module.catMax + module.examMax));
        }else if(AssignmentResult.numberAssignmentDone(this.id, mId, this.training.id) > 0.0 && AssignmentResult.numberResearchDone(this.id, mId, this.training.id)> 0.0 &&  Counts.getExamMaxTrainingModule(this.training.id,mId) <= 0.0){
            // C & R
            return (((Counts.getStudentCatMarksModule(this.id, mId) + Counts.getStudentResearchMarksModule(this.id, mId)) * 100) / (module.catMax + module.reMax));
        }else if(AssignmentResult.numberAssignmentDone(this.id, mId, this.training.id) > 0.0 && AssignmentResult.numberResearchDone(this.id, mId, this.training.id) <= 0.0 &&  Counts.getExamMaxTrainingModule(this.training.id,mId) <= 0.0){
            // C
            return ((Counts.getStudentCatMarksModule(this.id, mId) * 100) / module.catMax);
        }else if(AssignmentResult.numberAssignmentDone(this.id, mId, this.training.id) <= 0.0 && AssignmentResult.numberResearchDone(this.id, mId, this.training.id)> 0.0 &&  Counts.getExamMaxTrainingModule(this.training.id,mId) > 0.0){
            // R & E
            return (((Counts.getStudentResearchMarksModule(this.id, mId) + SubMark.componetExamResultModule(this.id, mId, this.training.id)) * 100) / (module.reMax + module.examMax));
        }else if(AssignmentResult.numberAssignmentDone(this.id, mId, this.training.id) <= 0.0 && AssignmentResult.numberResearchDone(this.id, mId, this.training.id)> 0.0 &&  Counts.getExamMaxTrainingModule(this.training.id,mId) > 0.0){
            // R
            return ((Counts.getStudentResearchMarksModule(this.id, mId) * 100) / module.reMax);
        }else if(AssignmentResult.numberAssignmentDone(this.id, mId, this.training.id) <= 0.0 && AssignmentResult.numberResearchDone(this.id, mId, this.training.id) <= 0.0 &&  Counts.getExamMaxTrainingModule(this.training.id,mId) > 0.0){
            // E
            return ((SubMark.componetExamResultModule(this.id, mId, this.training.id) * 100) / module.examMax);
        } else{
            return 0.0;
        }
    }

    private static Query<Student> allQuery() {
        return finder.where()
                .eq("deleteStatus", false)
                .eq("applicant.deleteStatus", false)
                .orderBy("id desc");
    }
    public static Integer count() {
        return allQuery().findRowCount();
    }

    public static Integer count(boolean isCLE) {
        return allQuery().where().add(cleExp(isCLE)).findRowCount();
    }


    public Account myAccount() {
        return Account.finder.where().eq("deleteStatus", false).eq("applicant.id", this.applicant.id).setMaxRows(1).findUnique();
    }

    public double financePayed() {
        double sum = 0.0;
        for (Payment payment : myPayments()) {
            sum += payment.accomodationAmount + payment.restaurantAmount + payment.feesAmount;
        }
        return sum;
    }


//clearance


    // damage clearance
    public Boolean damageCleared() {
        Damage damage = Damage.finder.where().eq("student.id", this.id).eq("clear", false).setMaxRows(1).findUnique();
        return damage == null;
    }

    //finance clearance
    public Boolean financeCleared() {
        return (this.applicant.getTotalAmountRemain() <= 0.0);
    }

    // library clearance
    public Boolean libClear() {
        ILDPLibrary ilpd = ILDPLibrary.finder.where().eq("student.id", this.id).eq("clear", false).setMaxRows(1).findUnique();
        return ilpd == null;
    }

    public Boolean cleared() {
        return libClear() && financeCleared() && damageCleared();
    }


    public String clearStatus() {
        return cleared() ? "Cleared" : "Not yet cleared";
    }
//end


    public static List<Student> byKey(String key) {
        String k = key.replace("'", "''");
        return finder.where("regNo like '%" + k + "%' or firstName like '%" + k + "%' ").findList();
    }

    public double getAmountToPay() {
        Student student = this;
        double amountToPay = student.training.schoolFees + student.training.iMode.intake.registrationFees;
        amountToPay = (student.applicant.needAccomodation) ? (amountToPay + student.training.accomodationFees) : amountToPay;
        if (student.applicant.needCatering) {
            amountToPay += student.training.restaurationFees;
        }
        return amountToPay;
    }

    public double getTotalAmountPaid() {
        List<Payment> payments = Payment.finder.where().eq("account.applicant.id", this.id).eq("deleteStatus", false).eq("status", "approved").findList();
        double sum = 0.0;
        for (Payment payment : payments) {
            sum += payment.sum();
        }
        return sum;
    }

    public double getTotalAmountRemain() {
        return (getAmountToPay() - getTotalAmountPaid());
    }


    private ExpressionList<Payment> approvedPayment() {
        return Payment.finder.where().eq("account.applicant.id", this.applicant.id).eq("status", "approved");
    }

    public List<Payment> myPayments() {
        return approvedPayment().orderBy("id desc").findList();
    }

    public List<Payment> myPaymentsApproved() {
        return approvedPayment().orderBy("id desc").findList();
    }

    public List<Module> failedModules() {
        List<Module> modules = new ArrayList<>();
        for (Module module : myModules()) {
            if (module.minMarks > modPercent(module)) {
                modules.add(module);
            }
        }
        return modules;
    }

    public double getRetakeAmountToPay() {
        double sum = 0.0;
        for (Module module : failedModules()) {
            sum += module.retakeAmount;
        }
        return sum;
    }

    public double getRetakeAmountPaid() {
        String s = "SELECT IFNULL(SUM(p.retake_amount),0) as amount FROM payment p INNER JOIN account a ON p.account_id = a.id INNER JOIN applicant a2 ON a.applicant_id = a2.id INNER JOIN student s ON a2.id = s.applicant_id WHERE p.retake_amount > 0.0 AND s.id=:id AND p.status='approved'";
        return Ebean.createSqlQuery(s).setParameter("id", this.id).findUnique().getDouble("amount");
    }

    public double getRemainRetakeAmountToPay() {
        return getRetakeAmountToPay() - getRetakeAmountPaid();
    }

    public List<Schedule> myLectures() {
        return Schedule.find.where("deleteStatus='false' and training.id='" + training.id + "' group by lecture.id").findList();
    }

    public static List<Student> allByQ(String q)
    {
        String[] arr = q.split(Vld.split);
        if (!q.equals("") && (arr.length <= 1))
        {
            return finder.where()
                    .not(Expr.eq("deleteStatus", true))
                    .not(Expr.eq("academicEmail", ""))
                    .eq("emailStatus", "pending")
                    .icontains("applicant.user.email", q)
                    .orderBy("id desc")
                    .findList();
        }
        int a = (arr.length > 1 && Vld.isNumeric(arr[1])) ? Integer.parseInt(arr[1]) : 0;
        return allPendingEmail(a);
    }
    public double averageOneMarks(Long mId) {
        int inc = 1;
        double ass = 0.0;
        double ress = 0.0;
        double Exams = 0.0;
        double tot1 = 0.0;
        double tot2 = 0.0;
        double tot3 = 0.0;
        double examMarks=0.0;
        Student student = Student.byApp(this.id);
        Module module = Module.finderById(mId);
        if(student == null) return 1.01;
        if(module == null) return 1.02;
        List<SubMark> subMarks = SubMark.allPerModule(mId, this.id);
        if(subMarks != null) {
            for (SubMark s : subMarks) {
                tot1 = Counts.getStudentCatMarks(this.id, s.component.module.id);
                if (AssignmentResult.numberResearchDone(this.id, s.component.module.id, this.training.id) > 0) {
                    tot2 = tot2 + (Counts.getStudentResearchMarksModule(this.id, s.component.module.id) / AssignmentResult.numberResearchDone(this.id, s.component.module.id, this.training.id));
                } else {
                    tot2 = tot2;
                }
                tot3 = tot3 + SubMark.moduleExamResult(this.id, s.component.module.id);
                inc = inc + 1;
                ass = AssignmentResult.numberAssignmentDone(this.id, s.component.module.id, this.training.id);
                ress = AssignmentResult.numberResearchDone(this.id, s.component.module.id, this.training.id);
                if(Counts.getExamMaxTrainingModule(this.training.id,mId) > 0){
                    Exams = Exams + 1;
                }
            }
        }
        if(ass > 0.0 && ress > 0.0 && Exams > 0.0) {
            if(Exams > 0) {
                examMarks = (tot1 + tot2 + (tot3 / Exams));
            } else {
                examMarks = (tot1 + tot2 + tot3);
            }
            return examMarks;
        }
        if(ass <= 0.0 && ress > 0.0 && Exams > 0.0) {
            if(Exams > 0) {
                examMarks = (((tot2 + (tot3 / Exams)) * 100) / (module.reMax + module.examMax));
            } else {
                examMarks = (((tot2 + tot3) * 100) / (module.reMax + module.examMax));
            }
            return examMarks;
        }
        if(ass <= 0.0 && ress <= 0.0 && Exams > 0.0) {
            if(Exams > 0) {
                examMarks = (((tot3 / Exams) * 100) / module.examMax);
            } else {
                examMarks = ((tot3 * 100) / module.examMax);
            }
            return examMarks;
        }
        if(ass > 0.0 && ress <= 0.0 && Exams <= 0.0) {
            examMarks = ((tot1 * 100) / module.catMax);
            return examMarks;
        }
        if(ass <= 0.0 && ress > 0.0 && Exams <= 0.0) {
            examMarks = ((tot1 * 100) / module.reMax);
            return examMarks;
        }
        if(ass > 0.0 && ress > 0.0 && Exams <= 0.0) {
            examMarks = (((tot1 + tot2) * 100) / (module.catMax + module.reMax));
            return examMarks;
        }
        if(ass > 0.0 && ress <= 0.0 && Exams > 0.0) {
            if(Exams > 0) {
                examMarks = (((tot1 + (tot3 / Exams)) * 100) / (module.catMax + module.examMax));
            } else {
                examMarks = (((tot1 + tot3) * 100) / (module.catMax + module.examMax));
            }
            return examMarks;
        }
        return examMarks;
    }
}
