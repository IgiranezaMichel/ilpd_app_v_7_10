package controllers;

import com.avaje.ebean.*;
import models.*;
import models.stock.*;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import viewModels.FilterBook;
import views.html.error;
import views.html.register.attendanceComponentReport;
import views.html.stock.reports.staffRequestHistory;
import views.html.student.notStudent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.avaje.ebean.Expr.eq;

public class Reports extends React {

    public static Result getStudentReport() {
        return ok(views.html.register.studentsReport.render(isCleManager()));
    }
    public static Result getApplicationReport() {
        return ok(views.html.register.applicationReport.render(isCleManager()));
    }
    public static Result getAuditReport() {
        return ok(views.html.register.getAuditReport.render(isCleManager()));
    }

    public static Result getFinancialReport() {
        return ok(views.html.register.financialReport.render());
    }

    public static Result getAttendanceReport() {
        return ok(views.html.register.attendanceReport.render(isCleManager()));
    }
    public static Result timeTableReport() {
        return ok(views.html.register.timeTableReport.render());
    }
    public static Result deliberationTableReport() {
        List<Training> trainings = Training.allDeliberated();
        return ok(views.html.register.deliberationTableReport.render(trainings));
    }
    public static Result timeTableSubmit() {
        DynamicForm form = Form.form().bindFromRequest();
        String trainingId = form.field("trainingId").value();
        Training training = Training.finderById(Long.parseLong(trainingId));
        List<Schedule> schedules = Schedule.find.where()
                .eq("training.id", training.id)
                .findList();
        String title = "The time table report "+training.print();
        return ok(views.html.register.timeTableSubmit.render(schedules, title));
    }
    public static Result deliberationReport() {
        DynamicForm form = Form.form().bindFromRequest();
        String trainingId = form.field("trainingId").value();
        String decision = form.field("decision").value();
        Long tId = Long.parseLong(trainingId);
        Training training = Training.finderById(tId);
        String title = "Deliberation report "+training.print();
            if (training == null) {
                return notFound();
            }
            List<Student> students = Student.byTraining(training.id);
            List<Module> modules = new ArrayList<>();
            if (!students.isEmpty())
                modules = Module.finder.where()
                        .eq("program.id", students.get(0)
                                .training.iMode.campusProgram.program.id)
                        .orderBy("orderNumber asc")
                        .findList();
            return ok(views.html.register.deliberationReport.render(students, training, modules, decision, title));
    }
    public static Result deliberationResitReport() {
        DynamicForm form = Form.form().bindFromRequest();
        String trainingId = form.field("trainingId").value();
        String status = form.field("status").value();
        Long tId = Long.parseLong(trainingId);
        Training training = Training.finderById(tId);
        String title = "";
        if(status.equalsIgnoreCase("before")) {
            title = "Student marks report before resit" + training.print();
        }
        if(status.equalsIgnoreCase("during")) {
            title = "Student marks report during resit" + training.print();
        }
        if(status.equalsIgnoreCase("after")) {
            title = "Student marks report after resit decision" + training.print();
        }
            if (training == null) {
                return notFound();
            }
            List<Student> students = Student.byTrainingInResitTrue(training.id);
            List<Module> modules = new ArrayList<>();
            if (!students.isEmpty())
                modules = Module.finder.where()
                        .eq("program.id", students.get(0)
                                .training.iMode.campusProgram.program.id)
                        .orderBy("orderNumber asc")
                        .findList();
            return ok(views.html.register.deliberationResitReport.render(students, training, modules, status, title));
    }
    public static Result resitRequestReport() {
        DynamicForm form = Form.form().bindFromRequest();
        String trainingId = form.field("trainingId").value();
        String decision = form.field("decision").value();
        Long tId = Long.parseLong(trainingId);
        Training training = Training.finderById(tId);
        String title = "Re-sit request report "+training.print();
            if (training == null) {
                return notFound();
            }
            List<Student> students = Student.byTraining(training.id);
            return ok(views.html.register.resitRequestReport.render(students, title, decision));
    }
    public static Result retakeModulesRequestReport() {
        DynamicForm form = Form.form().bindFromRequest();
        String trainingId = form.field("trainingId").value();
        String decision = form.field("decision").value();
        Long tId = Long.parseLong(trainingId);
        Training training = Training.finderById(tId);
        String title = "Retake modules request report "+training.print();
            if (training == null) {
                return notFound();
            }
            List<Student> students = Student.byTraining(training.id);
            return ok(views.html.register.retakeModulesRequestReport.render(students, title, decision));
    }
    public static Result marksReportByTraining(Long id) {
        Training training = Training.finderById(id);
        if (training == null) {
            return notFound();
        }
        List<Student> students = Student.byTraining(training.id);
        List<Module> modules = new ArrayList<>();
        if (!students.isEmpty())
            modules = Module.finder.where()
                    .eq("deleteStatus", false)
                    .eq("program.id", students.get(0).training.iMode.campusProgram.program.id).orderBy("module_code asc").findList();
        return ok(views.html.reports.marksReportByTraining.render(students, training, modules));
    }
    public static Result printTranscripts(Long id) {
        Training training = Training.finderById(id);
        if (training == null) {
            return notFound();
        }
        List<Student> students = Student.byTraining(training.id);
        List<Module> modules = new ArrayList<>();
        if (!students.isEmpty())
            modules = Module.finder.where().eq("program.id", students.get(0).training.iMode.campusProgram.program.id).orderBy("module_code asc").findList();

        String title = "";
        if(training.hasGraduatedFinal){
             title = "FINAL ACADEMIC TRANSCRIPT";
        }else{
            title = "PROVISIONAL MARKS RESULTS";
        }
      return ok(views.html.reports.printTranscripts.render(students, training, modules, title));
    }
    public static Result printDegrees(Long id) {
        Training training = Training.finderById(id);
        if (training == null) {
            return notFound();
        }
        List<Student> students = Student.byTraining(training.id);
        List<Module> modules = new ArrayList<>();
        if (!students.isEmpty())
            modules = Module.finder.where().eq("program.id", students.get(0).training.iMode.campusProgram.program.id).orderBy("module_code asc").findList();
        return ok(views.html.reports.printDegrees.render(students, training, modules));
    }
    public static Result marksReportByPermission(Long id) {
        Training training = Training.finderById(id);
        if (training == null) {
            return notFound();
        }
        List<Student> students = Student.byTraining(training.id);
        List<Module> modules = new ArrayList<>();
        if (!students.isEmpty())
            modules = Module.finder.where().eq("program.id", students.get(0).training.iMode.campusProgram.program.id).findList();
        return ok(views.html.register.marksReportByPermission.render(students, training, modules));
    }
    public static Result evaluationReport() {
        return ok(views.html.register.evaluation.render(isCoordinator(), isCleManager()));
    }
    public static Result printTranscript() {
        if (session("student") != null && Ins("student") != null) {
            Users user = Users.finderByMail(session("student"));
            Applicant applicant = user.amApplicant();
            Student student = applicant.student;
            String title = "";
            List<Module> lstComps = student.myModules();
            if(student.training.hasGraduated) {
                 title = "Academic official transcript";
            }else{
                title = "Academic provisional transcript";
            }

            return ok(views.html.reports.printTranscript.render(student,lstComps, title,true));
        }
        return ok(notStudent.render());
    }
    public static Result printAdmission(Long id) {
        if (session("registrar") != null || (session("student") != null && Ins("student") != null)) {
            Users user = Users.finderByMail(session("student"));
            Applied applied = Applied.finderById(id);
            return ok(views.html.reports.printAdmission.render(applied));
        }
        return ok(notStudent.render());
    }
        //return ok(views.html.student.admission.render(ap));
    protected static boolean isCoordinator()
    {
        return session().containsKey("Coordinator");
    }
    public static Result getStudentByIntake() {
        Form<Applied> appliedForm = Form.form(Applied.class).bindFromRequest();
        Long id = Long.parseLong(appliedForm.field("intakeId").value());
        ExpressionList<Student> where = Student.finder.where().add(Student.cleExp(isCleManager()));
        String intakeName = "All intake";
        if (id != 0) {
            intakeName = Intake.finderById(id).intakeName;
            where
                    .eq("deleteStatus", false)
                    .eq("training.iMode.intake.id", id);
        }
        List<Student> students = where.findList();

        return ok(views.html.reports.studentReportByIntake.render(students, intakeName));
    }

    // by study mode
    public static Result getStudentByStudyMode() {
        Form<StudyMode> studyModeForm = Form.form(StudyMode.class).bindFromRequest();
        Long id = Long.parseLong(studyModeForm.field("modeId").value());

        ExpressionList<Student> add = Student.finder.where().add(Student.cleExp(isCleManager()));
        if (id == 0) {
            List<Student> students = add.eq("deleteStatus", false)
                    .not(Expr.eq("status", "suspended"))
                    .not(Expr.eq("status", "dropout")).findList();
            String studyMode = "All study mode";
            return ok(views.html.reports.studentByStudyMode.render(students, studyMode));
        }
        List<Student> students = add.eq("training.iMode.sessionMode.mode.id", id)
                .not(Expr.eq("status", "suspended"))
                .not(Expr.eq("status", "dropout")).findList();
        String studyMode = StudyMode.finderById(id).modeName;
        return ok(views.html.reports.studentByStudyMode.render(students, studyMode));
    }

    // by study mode and intake
    public static Result getStudentByIntakeStudyMode() {
        Form<StudyMode> studyModeForm = Form.form(StudyMode.class).bindFromRequest();
        Long intakeId = Long.parseLong(studyModeForm.field("intakeId").value());
        Long studyModeId = Long.parseLong(studyModeForm.field("modeId").value());
        List<Student> students = Student.finder.where().add(Student.cleExp(isCleManager())).eq("training.iMode.sessionMode.mode.id", studyModeId).eq("training.iMode.intake.id", intakeId)
                .not(Expr.eq("status", "suspended"))
                .not(Expr.eq("status", "dropout")).findList();
        StudyMode studyMode = StudyMode.finderById(studyModeId);
        Intake intake = Intake.finderById(intakeId);

        return ok(views.html.reports.studentByStudyModeIntake.render(students, intake, studyMode));
    }
    public static Result getStudentByIntakeGender() {
        Form<StudyMode> studyModeForm = Form.form(StudyMode.class).bindFromRequest();
        Long intakeId = Long.parseLong(studyModeForm.field("intakeId").value());
        String gender = studyModeForm.field("gender").value();
        Intake intake = Intake.finderById(intakeId);
        if(gender.equalsIgnoreCase("all") && intakeId == 0) {
            List<Student> students = Student.finder.where()
                    .add(Student.cleExp(isCleManager()))
                    .not(Expr.eq("status", "suspended"))
                    .not(Expr.eq("status", "dropout"))
                    .findList();
            String title = "List of student from all intake and all genders";
            return ok(views.html.reports.getStudentByIntakeGender.render(students, intake, title));
        }
        if(!gender.equalsIgnoreCase("all") && intakeId == 0) {
            List<Student> students = Student.finder.where()
                    .add(Student.cleExp(isCleManager()))
                    .eq("applicant.gender", gender)
                    .not(Expr.eq("status", "suspended"))
                    .not(Expr.eq("status", "dropout"))
                    .findList();
            String title = "List of student from all intakes "+gender+" gender";
            return ok(views.html.reports.getStudentByIntakeGender.render(students, intake, title));
        }
        if(gender.equalsIgnoreCase("all") && intakeId != 0) {
            List<Student> students = Student.finder.where()
                    .add(Student.cleExp(isCleManager()))
                    .eq("training.iMode.intake.id", intakeId)
                    .not(Expr.eq("status", "suspended"))
                    .not(Expr.eq("status", "dropout"))
                    .findList();
            String title = "List of student from all gender "+intake.intakeName;
            return ok(views.html.reports.getStudentByIntakeGender.render(students, intake, title));
        }
        List<Student> students = Student.finder.where()
                .add(Student.cleExp(isCleManager()))
                .eq("applicant.gender", gender)
                .eq("training.iMode.intake.id", intakeId)
                .not(Expr.eq("status", "suspended"))
                .not(Expr.eq("status", "dropout"))
                .findList();
        String title = "List of student from "+intake.intakeName+" and "+gender;
        return ok(views.html.reports.getStudentByIntakeGender.render(students, intake, title));
    }
    public static Result getStudentByIntakeStatus() {
        Form<StudyMode> studyModeForm = Form.form(StudyMode.class).bindFromRequest();
        Long intakeId = Long.parseLong(studyModeForm.field("intakeId").value());
        String status = studyModeForm.field("status").value();
        Intake intake = Intake.finderById(intakeId);
        if(status.equalsIgnoreCase("all") && intakeId == 0) {
            List<Student> students = Student.finder.where()
                    .add(Student.cleExp(isCleManager()))
                    .not(Expr.eq("status", "RE-SIT"))
                    .not(Expr.eq("status", "RETAKE-MODULES"))
                    .not(Expr.eq("status", "Merit"))
                    .not(Expr.eq("status", "PASS"))
                    .not(Expr.eq("status", "FAIL"))
                    .findList();
            String title = "List of student from all intake and all status";
            return ok(views.html.reports.getStudentByIntakeGender.render(students, intake, title));
        }
        if(!status.equalsIgnoreCase("all") && intakeId == 0) {
            List<Student> students = Student.finder.where()
                    .add(Student.cleExp(isCleManager()))
                    .eq("status", status)
                    .not(Expr.eq("status", "RE-SIT"))
                    .not(Expr.eq("status", "RETAKE-MODULES"))
                    .not(Expr.eq("status", "Merit"))
                    .not(Expr.eq("status", "PASS"))
                    .not(Expr.eq("status", "FAIL"))
                    .findList();
            String title = "List of student from all intakes "+status+" status";
            return ok(views.html.reports.getStudentByIntakeGender.render(students, intake, title));
        }
        if(status.equalsIgnoreCase("all") && intakeId != 0) {
            List<Student> students = Student.finder.where()
                    .add(Student.cleExp(isCleManager()))
                    .eq("training.iMode.intake.id", intakeId)
                    .not(Expr.eq("status", "RE-SIT"))
                    .not(Expr.eq("status", "RETAKE-MODULES"))
                    .not(Expr.eq("status", "Merit"))
                    .not(Expr.eq("status", "PASS"))
                    .not(Expr.eq("status", "FAIL"))
                    .findList();
            String title = "List of student from all gender "+intake.intakeName;
            return ok(views.html.reports.getStudentByIntakeGender.render(students, intake, title));
        }
        List<Student> students = Student.finder.where()
                .add(Student.cleExp(isCleManager()))
                .eq("status", status)
                .eq("training.iMode.intake.id", intakeId)
                .not(Expr.eq("status", "RE-SIT"))
                .not(Expr.eq("status", "RETAKE-MODULES"))
                .not(Expr.eq("status", "Merit"))
                .not(Expr.eq("status", "PASS"))
                .not(Expr.eq("status", "FAIL"))
                .findList();
        String title = "List of student from "+intake.intakeName+" and "+status+" status";
        return ok(views.html.reports.getStudentByIntakeGender.render(students, intake, title));
    }

    // by study program and intake
    public static Result getStudentByIntakeProgram() {
        Form<StudyMode> studyModeForm = Form.form(StudyMode.class).bindFromRequest();
        Long intakeId = Long.parseLong(studyModeForm.field("intakeId").value());
        Long programId = Long.parseLong(studyModeForm.field("programId").value());
        String pro = "All programs", iString = "All intakes";
        ExpressionList<Student> where = Student.finder.where().add(Student.cleExp(isCleManager()));
        if (programId != 0) {
            Program faculty = Program.finderById(programId);
            pro = faculty.print();
            where.eq("training.iMode.campusProgram.program.id", programId);
        }
        if (intakeId != 0) {
            Intake intake = Intake.finderById(intakeId);
            iString = intake.print();
            where.eq("training.iMode.intake.id", intakeId);
        }

        List<Student> students = where.findList();
        return ok(views.html.reports.studentByProgramIntake.render(students, iString, pro));
    }

    // by study by gender
    public static Result getStudentByGender(String gender) {
        List<Student> students = Student.finder.where().eq("applicant.gender", gender)
                .not(Expr.eq("status", "suspended"))
                .not(Expr.eq("status", "dropout")).findList();
        return ok(views.html.reports.studentByGender.render(students, gender));
    }

    // by study by country
    public static Result getStudentByCountry() {
        Form<Country> countryForm = Form.form(Country.class).bindFromRequest();
        String country = countryForm.field("country").value();
        ExpressionList<Student> status = Student.finder.where().add(Student.cleExp(isCleManager())).eq("deleteStatus", false)
                .not(Expr.eq("status", "suspended"))
                .not(Expr.eq("status", "dropout"));
        if (country.equalsIgnoreCase("all")) {
            List<Student> students = status.findList();
            return ok(views.html.reports.studentByCountry.render(students, "All countries"));
        }
        List<Student> students = status.eq("applicant.country", country).findList();

        return ok(views.html.reports.studentByCountry.render(students, country));
    }

    // Student by martial status
    public static Result getStudentByMaritial(String status) {
        List<Student> students = Student.finder.where().add(Student.cleExp(isCleManager())).eq("applicant.maritalStatus", status)
                .not(Expr.eq("status", "suspended"))
                .not(Expr.eq("status", "dropout")).findList();
        return ok(views.html.reports.studentByMartialSatus.render(students, status));
    }

    // Applicants by intake
    public static Result getApplicantsByIntake() {
        Form<Applied> appliedForm = Form.form(Applied.class).bindFromRequest();
        Long intake = Long.parseLong(appliedForm.field("intakeId").value());
        ExpressionList<Applied> eq = Applied.finder.where().add(Student.cleExp(isCleManager())).eq("deleteStatus", false);
        if (intake == 0) {
            List<Applied> applieds = eq
                    .eq("deleteStatus", false)
                    .eq("status", true)
                    .orderBy("id asc")
                    .findList();
            String intake1 = "All intakes";

            return ok(views.html.reports.applicantReportByIntake.render(applieds, intake1));

        }
        List<Applied> applieds = eq.eq("training.iMode.intake.id", intake)
                .eq("status", true)
                .orderBy("id asc")
                .findList();
        String intake1 = Intake.finderById(intake).intakeName;

        return ok(views.html.reports.applicantReportByIntake.render(applieds, intake1));
    }

    // Applicants by by study mode
    public static Result getApplicantsByStudyMode() {
        Form<Applied> appliedForm = Form.form(Applied.class).bindFromRequest();
        Long modeId = Long.parseLong(appliedForm.field("modeId").value());
        ExpressionList<Applied> status = Applied.finder.where().eq("status", true).add(Student.cleExp(isCleManager()));
        if (modeId == 0) {
            List<Applied> applieds = status
                    .orderBy("id asc")
                    .findList();
            String studyMode = "All study mode";
            return ok(views.html.reports.applicantReportByStudyMode.render(applieds, studyMode));
        }
        List<Applied> applieds = status
                .eq("deleteStatus", false)
                .eq("training.iMode.sessionMode.mode.id", modeId)
                .orderBy("id asc")
                .findList();
        String studyMode = StudyMode.finderById(modeId).modeName;
        return ok(views.html.reports.applicantReportByStudyMode.render(applieds, studyMode));
    }

    // Applicants by study_mode intake
    public static Result getApplicantsByStudyModeIntake() {
        Form<Applied> appliedForm = Form.form(Applied.class).bindFromRequest();
        Long modeId = Long.parseLong(appliedForm.field("modeId").value());
        Long intakeId = Long.parseLong(appliedForm.field("intakeId").value());

        ExpressionList<Applied> query = Applied.finder.where().add(Student.cleExp(isCleManager()))
                .eq("deleteStatus", false)
                .eq("status", true);

        String studyMode = "All study mode", intake = "All intakes";

        if (intakeId != 0) {
            intake = Intake.finderById(intakeId).print();
            query.eq("training.iMode.intake.id", intakeId);

        }
        if (modeId != 0) {
            query.eq("training.iMode.sessionMode.mode.id", modeId);
            studyMode = StudyMode.finderById(modeId).modeName;

        }


        List<Applied> applieds = query.findList();

        return ok(views.html.reports.applicantReportByStudyModeIntake.render(applieds, intake, studyMode));
    }

    // Applicants by program and intake
    public static Result getApplicantsByProgramIntake() {
        Form<Applied> appliedForm = Form.form(Applied.class).bindFromRequest();
        Long programId = Long.parseLong(appliedForm.field("programId").value());
        Long intakeId = Long.parseLong(appliedForm.field("intakeId").value());
        ExpressionList<Applied> status = Applied.finder.where().add(Student.cleExp(isCleManager())).eq("status", true);
        String intake = "All intakes", prog = "All programs";
        if (intakeId != 0) {
            status
                    .eq("deleteStatus", false)
                    .eq("training.iMode.intake.id", intakeId);
            intake = Intake.finderById(intakeId).print();
        }
        if (programId != 0) {
            status.eq("training.iMode.campusProgram.program.id", programId);
            prog = Program.finderById(programId).programName;
        }
        List<Applied> applieds = status
                .eq("deleteStatus", false)
                .orderBy("id asc")
                .findList();

        return ok(views.html.reports.applicantReportByProgramIntake.render(applieds, intake, prog));
    }

    // Applicants by gender
    public static Result getApplicantsByGender(String gender) {
        List<Applied> applieds = Applied.finder.where().add(Student.cleExp(isCleManager())).eq("applicant.gender", gender).eq("status", true).findList();
        return ok(views.html.reports.applicantReportByGender.render(applieds, gender));
    }

    //applicants by country
    public static Result getApplicantByCountry() {
        Form<Country> countryForm = Form.form(Country.class).bindFromRequest();
        String country = countryForm.field("country").value();
        ExpressionList<Applied> add = Applied.finder.where().add(Student.cleExp(isCleManager()));
        if (country.equalsIgnoreCase("all")) {
            List<Applied> applieds = add
                    .eq("deleteStatus", false)
                    .orderBy("id asc")
                    .findList();
            return ok(views.html.reports.applicantsByCountry.render(applieds, country));
        }
        List<Applied> applieds = add.eq("applicant.country", country).findList();
        return ok(views.html.reports.applicantsByCountry.render(applieds, country));
    }

    //applicants by university
    public static Result getApplicantByUniversity() {
        Form<Applicant> form = Form.form(Applicant.class).bindFromRequest();
        Applicant applicant = form.get();
        String aSchool = form.field("aSchool").value();
        if (aSchool.equalsIgnoreCase("all")) {
            List<Applicant> applicantList = Applicant.finder.where()
            .eq("deleteStatus", 0)
            .orderBy("id asc")
            .findList();
            return ok(views.html.reports.getApplicantByUniversity.render(applicantList, "LIST OF APPLICANTS FROM ALL UNIVERSITIES/INSTITUTES"));
        }
            List<Applicant> applicantList = Applicant.finder.where()
                    .eq("deleteStatus", 0)
                    .eq("aSchool", aSchool)
                    .orderBy("id asc")
                    .findList();
            return ok(views.html.reports.getApplicantByUniversity.render(applicantList, "LIST OF APPLICANTS FROM "+ aSchool ));
    }

    //applicants by university
    public static Result getStudentByUniversity() {
        Form<Student> form = Form.form(Student.class).bindFromRequest();
        Student student = form.get();
        String aSchool = form.field("aSchool").value();
        if (aSchool.equalsIgnoreCase("all")) {
            List<Student> studentList = Student.finder.where()
            .eq("deleteStatus", 0)
                    .not(Expr.eq("status", "suspended"))
                    .not(Expr.eq("status", "dropout"))
            .findList();
            return ok(views.html.reports.getStudentByUniversity.render(studentList, "LIST OF STUDENTS FROM ALL UNIVERSITIES/INSTITUTES"));
        }
            List<Student> studentList = Student.finder.where()
                    .eq("deleteStatus", 0)
                    .eq("applicant.aSchool", aSchool)
                    .not(Expr.eq("status", "suspended"))
                    .not(Expr.eq("status", "dropout"))
                    .findList();
            return ok(views.html.reports.getStudentByUniversity.render(studentList, "LIST OF STUDENTS FROM "+ aSchool ));
    }
    public static Result getStudentByIntakeUniversity() {
        Form<Student> form = Form.form(Student.class).bindFromRequest();
        Student student = form.get();
        String aSchool = form.field("aSchool").value();
        Long iId = Long.parseLong(form.field("intakeId").value());
        Intake intake = Intake.finderById(iId);
        if (aSchool.equalsIgnoreCase("all") && iId == 0) {
            List<Student> studentList = Student.finder.where()
            .eq("deleteStatus", 0)
                    .not(Expr.eq("status", "suspended"))
                    .not(Expr.eq("status", "dropout"))
            .findList();
            return ok(views.html.reports.getStudentByUniversity.render(studentList, "LIST OF STUDENTS FROM ALL UNIVERSITIES/INSTITUTES AND ALL INTAKES"));
        }
        if (!aSchool.equalsIgnoreCase("all") && iId == 0) {
            List<Student> studentList = Student.finder.where()
                    .eq("deleteStatus", 0)
                    .eq("applicant.aSchool", aSchool)
                    .not(Expr.eq("status", "suspended"))
                    .not(Expr.eq("status", "dropout"))
                    .findList();
            return ok(views.html.reports.getStudentByUniversity.render(studentList, "LIST OF STUDENTS FROM ALL INTAKE AND " + aSchool+" UNIVERSITY/INSTITUTION"));
        }
        if (aSchool.equalsIgnoreCase("all") && iId != 0) {
            List<Student> studentList = Student.finder.where()
                    .eq("deleteStatus", 0)
                    .eq("training.deleteStatus", 0)
                    .eq("training.iMode.deleteStatus", 0)
                    .eq("training.iMode.intake.deleteStatus", 0)
                    .eq("training.iMode.intake", intake)
                    .not(Expr.eq("status", "suspended"))
                    .not(Expr.eq("status", "dropout"))
                    .findList();
            return ok(views.html.reports.getStudentByUniversity.render(studentList, "LIST OF STUDENTS FROM ALL INTAKE AND " + aSchool+" UNIVERSITY/INSTITUTION"));
        }
            List<Student> studentList = Student.finder.where()
                    .eq("deleteStatus", 0)
                    .eq("training.deleteStatus", 0)
                    .eq("training.iMode.deleteStatus", 0)
                    .eq("training.iMode.intake.deleteStatus", 0)
                    .eq("applicant.aSchool", aSchool)
                    .eq("training.iMode.intake", intake)
                    .not(Expr.eq("status", "suspended"))
                    .not(Expr.eq("status", "dropout"))
                    .findList();
            return ok(views.html.reports.getStudentByUniversity.render(studentList, "LIST OF STUDENTS FROM INTAKE "+intake.intakeName+" AND " + aSchool+" UNIVERSITY/INSTITUTION"));
    }
    public static Result getStudentByIntakeNationality() {
        Form<Student> form = Form.form(Student.class).bindFromRequest();
        Student student = form.get();
        String countryName = form.field("countryName").value();
        Long iId = Long.parseLong(form.field("intakeId").value());
        Intake intake = Intake.finderById(iId);
        Country country = Country.finderById(iId);
        if (countryName.equalsIgnoreCase("all") && iId == 0) {
            List<Student> studentList = Student.finder.where()
            .eq("deleteStatus", 0)
                    .not(Expr.eq("status", "suspended"))
                    .not(Expr.eq("status", "dropout"))
            .findList();
            return ok(views.html.reports.getStudentByUniversity.render(studentList, "LIST OF STUDENTS FROM ALL UNIVERSITIES/INSTITUTES AND ALL NATIONALITIES"));
        }
        if (!countryName.equalsIgnoreCase("all") && iId == 0) {
            List<Student> studentList = Student.finder.where()
                    .eq("deleteStatus", 0)
                    .eq("applicant.country", countryName)
                    .not(Expr.eq("status", "suspended"))
                    .not(Expr.eq("status", "dropout"))
                    .findList();
            return ok(views.html.reports.getStudentByUniversity.render(studentList, "LIST OF STUDENTS FROM ALL INTAKE AND " +countryName+" COUNTRY NAME"));
        }
        if (countryName.equalsIgnoreCase("all") && iId != 0) {
            List<Student> studentList = Student.finder.where()
                    .eq("deleteStatus", 0)
                    .eq("training.deleteStatus", 0)
                    .eq("training.iMode.deleteStatus", 0)
                    .eq("training.iMode.intake.deleteStatus", 0)
                    .not(Expr.eq("status", "suspended"))
                    .not(Expr.eq("status", "dropout"))
                    .eq("training.iMode.intake", intake)
                    .findList();
            return ok(views.html.reports.getStudentByUniversity.render(studentList, "LIST OF STUDENTS FROM ALL INTAKE AND " + countryName+" COUNTRY NAME"));
        }
            List<Student> studentList = Student.finder.where()
                    .eq("deleteStatus", 0)
                    .eq("training.deleteStatus", 0)
                    .eq("training.iMode.deleteStatus", 0)
                    .eq("training.iMode.intake.deleteStatus", 0)
                    .eq("applicant.country", countryName)
                    .not(Expr.eq("status", "suspended"))
                    .not(Expr.eq("status", "dropout"))
                    .eq("training.iMode.intake", intake)
                    .findList();
            return ok(views.html.reports.getStudentByUniversity.render(studentList, "LIST OF STUDENTS FROM INTAKE "+intake.intakeName+" AND " + countryName+" COUNTRY NAME"));
    }
    // Applicants by martial status
    public static Result getApplicantsByMaritial(String status) {
        List<Applied> applieds = Applied.finder.where()
                .eq("deleteStatus", false)
                .add(Student.cleExp(isCleManager())).eq("applicant.maritalStatus", status).findList();
        return ok(views.html.reports.applicantsByMartial.render(applieds, status));
    }
    public static Result getStudentComponentsMarks(Long mId, Long sId, Long tId) {
        List<SubMark> subMarks = SubMark.find.setDistinct(true)
                .where("deleteStatus='false' and student.deleteStatus='false' and component.deleteStatus='false' and component.module.deleteStatus='false' and component.module.id = '"+mId+"' and student.id = '"+sId+"' group by component.id").findList();
        Student student = Student.finderById(sId);
        Module module = Module.finderById(mId);
        Training training = student.training;
        return ok(views.html.reports.getStudentComponentsMarks.render(subMarks, student, module, training));
    }
    static Users Ins(String key) {
        return Users.finderByMail(session(key));
    }

    public static Result evaluationPost() {
        DynamicForm form = Form.form().bindFromRequest();
        long trainingId = getLong(form.field("period").value());
        long lecture = getLong(form.field("lecture").value());
        long component = getLong(form.field("component").value());
        String status = form.field("status").value();
        Lecture l = Lecture.finderById(lecture);
        if (l == null) return ok();
        Training training = Training.finderById(trainingId);
        if(status.equalsIgnoreCase("not")){

            return ok(views.html.register.evReportNotEvaluated.render(Student.notEvaluated(component, trainingId), training));
        }
        return ok(views.html.register.evReport.render(EvCategory.finder.all(), !isAjax(), lecture, component, trainingId, l));
    }
    public static Result evaluationPostModule() {
        DynamicForm form = Form.form().bindFromRequest();
        long trainingId = getLong(form.field("trainingId").value());
        long moduleId = getLong(form.field("moduleId").value());
        String status = form.field("status").value();
        Training training = Training.finderById(trainingId);
        Module module = Module.finderById(moduleId);
        if(status.equalsIgnoreCase("not")){
            return ok(views.html.register.evReportNotEvaluatedModule.render(Student.notEvaluatedModule(module.id, trainingId), training));
        }
        return ok(views.html.register.evReportModule.render(EvCategory.finder.all(), !isAjax(), module.id, trainingId));
    }

    public static Result componentAttendanceReport() {
        Form<Attendance> attendanceForm = Form.form(Attendance.class).bindFromRequest();
        Campus campus = Campus.finderById(Long.parseLong(attendanceForm.field("campus").value()));
        Training training1 = Training.finderById(Long.parseLong(attendanceForm.field("iMode").value()));
        Component component = Component.finderById(Long.parseLong(attendanceForm.field("component").value()));
        if (campus == null || component == null || training1 == null) {
            return notFound("Some Error");
        }

        Training training = Training.find.where()
                .eq("deleteStatus", false)
                .eq("id", training1.id)
                .findUnique();
        if (training == null) {
            return notFound("Error");
        }
            List<Student> students = Student.finder.where()
                   // .add(Student.cleExp(isCleManager()))
                    .eq("deleteStatus", false)
                    .eq("training.id", training.id)
                    .not(Expr.eq("status", "suspended"))
                    .not(Expr.eq("status", "dropout"))
                    .findList();
            return ok(views.html.register.attendanceComponentReport.render(students, training, component));
    }


    public static Result moduleAttendanceReport() {
        Form<Attendance> attendanceForm = Form.form(Attendance.class).bindFromRequest();
        Training training = Training.finderById(Long.parseLong(attendanceForm.field("period").value()));
        Module module = Module.finderById(Long.parseLong(attendanceForm.field("module").value()));
        if (training == null || module == null) {
            return notFound("Some Error");
        }
        List<Student> students = Student.finder.where()
                .add(Student.cleExp(isCleManager()))
                .eq("deleteStatus", false)
                .eq("training.id", training.id)
                .not(Expr.eq("status", "suspended"))
                .not(Expr.eq("status", "dropout"))
                .findList();
        return ok(views.html.reports.moduleReport.render(students, training, module));

    }


    public static Result summaryReport() {
        return ok(views.html.reports.summaryReport.render());
    }

    public static Result libraryReport() {
        return ok(views.html.reports.reportLibrary.render());
    }

    public static Result libraryReportFilter() {
        Form<FilterBook> filterBookForm = Form.form(FilterBook.class).bindFromRequest();
        FilterBook filterBook = filterBookForm.get();

        List<ILDPLibrary> list = ILDPLibrary.finder.where().eq("act", filterBook.action).eq("deleteStatus", false).eq("clear", false).findList();
        return ok(Json.toJson(list));
    }


    public static Result getImodes(long campusId) {
        Campus campus = Campus.finderById(campusId);
        if (campus == null) {
            return notFound();
        }
        ExpressionList<Training> tr = Training.find.where()
                .eq("deleteStatus", false);
        if (campusId != 0)
            tr.eq("iMode.campusProgram.campus.id", campus.id);
        List<Training> trainings = null;
        if (isRegistrar() || isCoordinator()){
            trainings = Training.allOpenRegistrarR();
        }
        if (isCleManager() || session("DTR/Coordinator") != null){
            trainings = Training.allOpenCle();
        }
        if (session("Instructor") != null || session("quality_insurance") != null || session("Coordinator") != null || session("mark_officer") != null){
            trainings = Training.allOpenRegistrarR();
        }
        return ok(Json.toJson(trainings));
    }

    public static Result getComponents(long iModeId) {
        Training training = Training.finderById(iModeId);
        if (training == null) {
            return notFound();
        }
        String s = "SELECT DISTINCT c.id,c.comp_name, p.program_acronym FROM component c INNER JOIN module m ON c.module_id=m.id INNER JOIN program p ON m.program_id = p.id INNER JOIN campus_program cp ON cp.program_id = p.id INNER JOIN intake_session_mode i ON i.campus_program_id = cp.id INNER JOIN training t ON t.i_mode_id = i.id WHERE t.id=:id and c.delete_status=0";
        List<SqlRow> sqlRows = Ebean.createSqlQuery(s).setParameter("id", training.id).findList();
        return ok(Json.toJson(sqlRows));
    }

    public static Result getTraining(long inId) {
        Intake intake = Intake.finderById(inId);
        if (intake == null) {
            return notFound();
        }
        String s = "SELECT t.id, t.title FROM training t INNER JOIN intake_session_mode i ON t.i_mode_id=i.id INNER JOIN intake in ON i.intake_id = in.id WHERE in.id=:id  GROUP BY t.id";
        List<SqlRow> sqlRows = Ebean.createSqlQuery(s).setParameter("id", intake.id).findList();
        return ok(Json.toJson(sqlRows));
    }

    public static Result getAssignments(long compId) {
        Component component = Component.finderById(compId);
        if (component == null) {
            return notFound();
        }
        String s = "SELECT a.id,a.assignment_title FROM assignment a INNER JOIN component c ON a.component_id=c.id WHERE c.id=:id  GROUP BY a.id";
        List<SqlRow> sqlRows = Ebean.createSqlQuery(s).setParameter("id", component.id).findList();
        return ok(Json.toJson(sqlRows));
    }


    public static Result byCampus(String s) {
        long id = 0;
        boolean cle = isCleManager();
        if (s.equalsIgnoreCase("campus")) {

            Form<Campus> campusForm = Form.form(Campus.class).bindFromRequest();
            long cid = Long.parseLong(campusForm.field("campusId").value());
            ExpressionList<Applied> where = Applied.finder.where().eq("deleteStatus", false);
            String cName = "All campuses";
            if (cid != 0) {
                Campus campus = Campus.finderById(cid);
                cName = campus.campusName;
                where.eq("training.iMode.campusProgram.campus.id", campus.id);
            }

            where.add(Student.cleExp(cle));

            return ok(views.html.reports.applicantsByCampus.render(where.findList(), cName));
        } else if (s.equalsIgnoreCase("program")) {
            Form<Program> programForm = Form.form(Program.class).bindFromRequest();
            long proId = Long.parseLong(programForm.field("programId").value());
            Program program = Program.finderById(proId);
            if (program == null) {
                return notFound();
            }
            ExpressionList<Applied> eq = Applied.finder.where().add(Student.cleExp(cle)).eq("status", true).eq("deleteStatus", false);
            if (proId == 0) {
                List<Applied> applieds = eq.findList();
                return ok(views.html.reports.applicantsByCampus.render(applieds, "All programs"));
            }
            List<Applied> applieds = eq.eq("training.iMode.campusProgram.program.id", program.id).findList();
            return ok(views.html.reports.applicantsByCampus.render(applieds, program.programName + "(" + program.programAcronym + ")"));
        } else if (s.equalsIgnoreCase("status")) {
            Form<Applied> appliedForm = Form.form(Applied.class).bindFromRequest();
            String applStatus = appliedForm.field("apStatus").value();
            Long inId = Long.parseLong(appliedForm.field("intakeId").value());
            Intake intake = Intake.finderById(inId);
            if(inId != 0 && intake == null) return ok("Errors");
            if (inId == 0 && applStatus.equalsIgnoreCase("all")) {
                ExpressionList<Applied> list = Applied.finder.where()
                        .add(Student.cleExp(cle))
                        .eq("deleteStatus", false);
                List<Applied> applieds = list.findList();
                return ok(views.html.reports.applicantsByStatus.render(applieds, "All applicants"));
            }
            if (inId == 0 && !applStatus.equalsIgnoreCase("all")) {
                ExpressionList<Applied> list = Applied.finder.where()
                        .add(Student.cleExp(cle))
                        .eq("status", applStatus)
                        .eq("deleteStatus", false);
                List<Applied> applieds = list.findList();
                return ok(views.html.reports.applicantsByStatus.render(applieds, "All applicants"));
            }
            if(inId != 0 && applStatus.equalsIgnoreCase("all")) {
                ExpressionList<Applied> list = Applied.finder.where()
                        .add(Student.cleExp(cle))
                        .eq("deleteStatus", false)
                        .eq("training.iMode.intake.id", inId);
                List<Applied> applieds = list.findList();
                return ok(views.html.reports.applicantsByStatus.render(applieds, applStatus));
            }
            if (inId != 0 && !applStatus.equalsIgnoreCase("all")) {
                ExpressionList<Applied> list = Applied.finder.where()
                        .add(Student.cleExp(cle))
                        .eq("training.iMode.intake.id", inId)
                        .eq("status", applStatus)
                        .eq("deleteStatus", false);
                List<Applied> applieds = list.findList();
                return ok(views.html.reports.applicantsByStatus.render(applieds, "All applicants"));
            }
        } else if (s.equalsIgnoreCase("statusIntake")) {
            Form<Applied> appliedForm = Form.form(Applied.class).bindFromRequest();
            String applStatus = appliedForm.field("apStatus").value();
            Long intakeId = Long.parseLong(appliedForm.field("intakeId").value());
            ExpressionList<Applied> where = Applied.finder.where().add(Student.cleExp(cle));

            if (intakeId != 0) {
                where.eq("training.iMode.intake.id", intakeId);
            }

            if (!applStatus.equals("all")) {
                where.eq("applicationStatus", applStatus);
            }


            List<Applied> applieds = where.findList();
            return ok(views.html.reports.applicantsByStatus.render(applieds, applStatus));
        } else if (s.equalsIgnoreCase("campusStudent")) {
            Form<Campus> campusForm = Form.form(Campus.class).bindFromRequest();
            long campusId = Long.parseLong(campusForm.field("campusId").value());
            String campusName = "All campuses";
            ExpressionList<Applied> where = Applied.finder.where().add(Student.cleExp(cle));
            if (campusId != 0) {

                Campus campus = Campus.finderById(campusId);
                if (campus == null) {
                    return notFound();
                }
                where.eq("applicant.student.training.iMode.campusProgram.campus.id", campus.id);
            }
            List<Applied> applieds = where.eq("status", true).eq("deleteStatus", false).findList();
            return ok(views.html.reports.studentsByCampus.render(applieds, campusName));
        } else if (s.equalsIgnoreCase("programStudent")) {
            Form<Program> programForm = Form.form(Program.class).bindFromRequest();
            long cid = Long.parseLong(programForm.field("programId").value());
            Program program = Program.finderById(cid);
            ExpressionList<Applied> eq = Applied.finder.where().eq("status", true).eq("deleteStatus", false);
            if (program == null) {
                return notFound();
            }
            if (cid == 0) {
                List<Applied> applieds = eq.findList();

                return ok(views.html.reports.studentsByCampus.render(applieds, "All program"));
            }
            List<Applied> applieds = eq.eq("applicant.student.training.iMode.campusProgram.program.id", program.id).findList();

            return ok(views.html.reports.studentsByCampus.render(applieds, program.programName + "(" + program.programAcronym + ")"));
        } else if (id == 0 && (s.equalsIgnoreCase("new") || s.equalsIgnoreCase("deferred") || s.equalsIgnoreCase("accepted") || s.equalsIgnoreCase("rejected"))) {
            List<Applied> applieds = Applied.finder.where().eq("applicationStatus", s).eq("status", true).eq("deleteStatus", false).findList();

            return ok(views.html.reports.applicantsByCampus.render(applieds, "With " + s.toUpperCase() + " Status"));
        } else if (id == 1 && (s.equalsIgnoreCase("student") || s.equalsIgnoreCase("deferred") || s.equalsIgnoreCase("accepted") || s.equalsIgnoreCase("rejected"))) {
            List<Applied> applieds = Applied.finder.where().eq("applicationStatus", s).eq("status", true).eq("deleteStatus", false).findList();


            return ok(views.html.reports.studentsByCampus.render(applieds, "With " + s.toUpperCase() + " Status"));
        }
        return badRequest();
    }

    public static Result programIntakeMode(String s) {
        Form<Applied> appliedForm = Form.form(Applied.class).bindFromRequest();
        Long programId = Long.parseLong(appliedForm.field("programId").value());
        Long intakeId = Long.parseLong(appliedForm.field("intakeId").value());
        Long modeId = Long.parseLong(appliedForm.field("modeId").value());

        Program program = Program.finderById(programId);
        Intake intake = Intake.finderById(intakeId);
        StudyMode studyMode = StudyMode.finderById(modeId);
        if (program == null || intake == null || studyMode == null) {
            return notFound();
        }
        if (s.equalsIgnoreCase("app")) {
            List<Applied> applieds = Applied.finder.where()
                    .add(Student.cleExp(isCleManager()))
                    .eq("status", true)
                    .eq("deleteStatus", false)
                    .eq("training.iMode.campusProgram.program.id", program.id)
                    .eq("training.iMode.intake.id", intake.id)
                    .eq("training.iMode.sessionMode.mode.id", studyMode.id)
                    .findList();
            return ok(views.html.reports.applicantsByCampus.render(applieds, "With " + program.programAcronym + "-" + intake.intakeName + " " + (intake.year != null ? intake.year.yearName : "") + "-" + studyMode.modeName));
        } else if (s.equalsIgnoreCase("student")) {
            List<Applied> applieds = Applied.finder.where()
                    .add(Student.cleExp(isCleManager()))
                    .eq("status", true)
                    .eq("deleteStatus", false)
                    .eq("training.iMode.campusProgram.program.id", program.id)
                    .eq("applicant.student.status", "active")
                    .eq("applicant.student.training.iMode.intake.id", intake.id)
                    .eq("training.iMode.sessionMode.mode.id", studyMode.id)
                    .findList();
            return ok(views.html.reports.studentsByCampus.render(applieds, "With " + program.programAcronym + "-" + intake.intakeName + " " + (intake.year != null ? intake.year.yearName : "") + "-" + studyMode.modeName));
        }
        return badRequest();
    }
    public static Result programIntakeMode2(String s) {
        Form<Applied> appliedForm = Form.form(Applied.class).bindFromRequest();
        Long programId = Long.parseLong(appliedForm.field("programId").value());
        Long trainingId = Long.parseLong(appliedForm.field("trainingId").value());
        int attendance = Integer.parseInt(appliedForm.field("attendance").value());

        Program program = Program.finderById(programId);
        Training training = Training.finderById(trainingId);
        if (program == null) {
            return notFound();
        }
        if (s.equalsIgnoreCase("studentAttendateList")) {
            List<Student> studentList = Student.finder.where()
                    .eq("deleteStatus", false)
                    .eq("training.iMode.campusProgram.program.id", program.id)
                    .eq("training.id", training.id)
                    .not(Expr.eq("status", "suspended"))
                    .not(Expr.eq("status", "dropout"))
                    .findList();
            for(Student ss : studentList){
                if(attendance == 1 && Counts.attendancePerStudent(ss.id) >= ProfileInfo.unique().attendancePercentage){
                    String title = "Student who merit certificate";
                    return ok(views.html.reports.studentsMeritCertf.render(studentList, attendance, title));
                }
                if(attendance == 0 && Counts.attendancePerStudent(ss.id) <= ProfileInfo.unique().attendancePercentage){
                    String title = "Student who do not merit certificate";
                    return ok(views.html.reports.studentsMeritCertf.render(studentList, attendance, title));
                }else{
                    return ok("No report available for selected choice!");
                }
            }
        }
        return badRequest();
    }
    public static Result programIntakeMode3() {
        Form<Applied> form = Form.form(Applied.class).bindFromRequest();
        Long programId = Long.parseLong(form.field("programId").value());
        Long intakeId = Long.parseLong(form.field("intakeId").value());
        Program program = Program.finderById(programId);
        Intake intake = Intake.finderById(intakeId);
        if (intake == null || program == null) {
            return notFound();
        }
        if (intakeId == 0) {
            return ok(views.html.reports.studentsAnalyticalAll.render());
        }
            return ok(views.html.reports.studentsAnalytical.render(intake));
    }
    public static Result generalStockReport(String s) {
        if (s.equalsIgnoreCase("groups")) {
            return ok(views.html.stock.reports.groups.render(Group.all()));
        } else if (s.equalsIgnoreCase("categories")) {
            return ok(views.html.stock.reports.categories.render(Category.all()));
        } else if (s.equalsIgnoreCase("suppliers")) {
            return ok(views.html.stock.reports.suppliers.render(Supplier.all()));
        } else if (s.equalsIgnoreCase("items")) {
            return ok(views.html.stock.reports.itemsReport.render(Item.all(), "Items"));
        } else if (s.equalsIgnoreCase("items-cons")) {
            return ok(views.html.stock.reports.itemsReportConsumable.render(Item.all(), "Items"));
        } else if (s.equalsIgnoreCase("items-non")) {
            return ok(views.html.stock.reports.itemsReportNonConsumable.render(Item.all(), "Items"));
        } else if (s.equalsIgnoreCase("itemsInStock")) {
            return ok(views.html.stock.reports.itemsInStock.render(Supplied.all()));
        } else if (s.equalsIgnoreCase("itemsLowStock")) {
            List<Item> supplieds = Item.finder.where().eq("deleteStatus", false).lt("balanceQty", 15).findList();
            return ok(views.html.stock.reports.itemsLowStock.render(supplieds));
        } else if (s.equalsIgnoreCase("consItems")) {
            List<Item> supplieds = Item.finder.where().eq("deleteStatus", false).eq("iType", "Consumable").findList();
            return ok(views.html.stock.reports.itemsByType.render(supplieds, "Consumable"));
        } else if (s.equalsIgnoreCase("nonConsItems")) {
            List<Item> supplieds = Item.finder.where().eq("deleteStatus", false).eq("iType", "Non-Consumable").findList();
            return ok(views.html.stock.reports.itemsByType.render(supplieds, "Non-Consumable"));
        } else if (s.equalsIgnoreCase("stockReportCategory")) {
            Form<Item> form = Form.form(Item.class).bindFromRequest();
            Long cId = Long.parseLong(form.field("category").value());
            Category category = Category.find(cId);
            if (category == null && cId != 0) {
                return notFound();
            }
            String startDate = form.field("startDate").value();
            String endDate = form.field("endDate").value();
            String title = "Report of stock based on item category from "+startDate+" To "+endDate;
            if(cId == 0) {
                List<Supplied> supplieds = Supplied.finder.where()
                        .eq("deleteStatus", false)
                        .between("date", startDate, endDate)
                        .eq("item.iType", "Consumable")
                        .findList();
                return ok(views.html.stock.reports.stockReportByCategory1.render(supplieds, title+" all categories"));
            }
                List<Supplied> supplieds = Supplied.finder.where()
                        .eq("deleteStatus", false)
                        .between("date", startDate, endDate)
                        .eq("item.iType", "Consumable")
                        .eq("item.category.id", category.id)
                        .findList();
                return ok(views.html.stock.reports.stockReportByCategory1.render(supplieds, title+" "+category.name+" Category"));
        }else if (s.equalsIgnoreCase("stockReportCategoryAsset")) {
            Form<Item> form = Form.form(Item.class).bindFromRequest();
            Long cId = Long.parseLong(form.field("category").value());
            Category category = Category.find(cId);
            if (category == null && cId != 0) {
                return notFound();
            }
            String startDate = form.field("startDate").value();
            String endDate = form.field("endDate").value();
            String title = "Report of asset based on category from "+startDate+" To "+endDate;
            if(cId == 0) {
                List<Supplied> supplieds = Supplied.finder.where()
                        .eq("deleteStatus", false)
                        .between("date", startDate, endDate)
                        .eq("item.iType", "Non-consumable")
                        .findList();
                return ok(views.html.stock.reports.stockReportCategoryAsset.render(supplieds, title+" all categories"));
            }
                List<Supplied> supplieds = Supplied.finder.where()
                        .eq("deleteStatus", false)
                        .between("date", startDate, endDate)
                        .eq("item.iType", "Non-consumable")
                        .eq("item.category.id", category.id)
                        .findList();
                return ok(views.html.stock.reports.stockReportCategoryAsset.render(supplieds, title+" "+category.name+" Category"));
        } else if (s.equalsIgnoreCase("assetsReportCategory")) {
            Form<Item> form = Form.form(Item.class).bindFromRequest();
            long cId = Long.parseLong(form.field("category").value());
            Category category = Category.find(cId);
            if (category == null && cId != 0) {
                return notFound();
            }
            String startDate = form.field("startDate").value();
            String endDate = form.field("endDate").value();
            String title = "";
            if(cId == 0) {
                title = "Report all categories";
                List<StockMovement> supplieds = StockMovement.finder.where()
                        .eq("deleteStatus", false)
                        .between("date", startDate, endDate)
                        .findList();
                return ok(views.html.stock.reports.stockReportByCategory2.render(supplieds, startDate, endDate, title));
            }else {
                title = "Report for category: " + category.name;
                List<StockMovement> supplieds = StockMovement.finder.where()
                        .eq("deleteStatus", false)
                        .between("date", startDate, endDate)
                        .eq("item.category.id", category.id)
                        .findList();
                return ok(views.html.stock.reports.stockReportByCategory2.render(supplieds, startDate, endDate, title));
            }
        } else if (s.equalsIgnoreCase("stockInHistory")) {
            Form<Item> form = Form.form(Item.class).bindFromRequest();
            Long gId = Long.parseLong(form.field("groupId").value());
            models.stock.Group group = models.stock.Group.find(gId);
            if(gId != 0 && group == null){
                return notFound();
            }
            String startDate = form.field("startDate").value();
            String endDate = form.field("endDate").value();
            if(gId == 0) {
                List<Supplied> supplieds = Supplied.finder.where().eq("deleteStatus", false).between("date", startDate, endDate).findList();
                return ok(views.html.stock.reports.stockInReportHistory.render(supplieds, startDate, endDate));
            }
            List<Supplied> supplieds = Supplied.finder.where()
                    .eq("deleteStatus", false)
                    .eq("item.category.group", group)
                    .between("date", startDate, endDate)
                    .findList();
            return ok(views.html.stock.reports.stockInReportHistory.render(supplieds, startDate, endDate));
        } else if (s.equalsIgnoreCase("harmonizationInReport")) {
            Form<Item> form = Form.form(Item.class).bindFromRequest();
            Long gId = Long.parseLong(form.field("groupId").value());
            models.stock.Group group = models.stock.Group.find(gId);
            if(gId != 0 && group == null){
                return notFound();
            }
            String startDate = form.field("startDate").value();
            String endDate = form.field("endDate").value();
            if(gId == 0) {
                List<Supplied> supplieds = Supplied.finder.where()
                        .eq("deleteStatus", false)
                        .eq("supplier.name", "harmonization")
                        .between("date", startDate, endDate)
                        .findList();
                return ok(views.html.stock.reports.harmonizationInReport.render(supplieds, startDate, endDate));
            }
            List<Supplied> supplieds = Supplied.finder.where()
                    .eq("deleteStatus", false)
                    .eq("supplier.name", "harmonization")
                    .eq("item.category.group", group)
                    .between("date", startDate, endDate)
                    .findList();
            return ok(views.html.stock.reports.harmonizationInReport.render(supplieds, startDate, endDate));
        } else if (s.equalsIgnoreCase("stockOutHistory")) {
            Form<Item> form = Form.form(Item.class).bindFromRequest();
            Long gId = Long.parseLong(form.field("groupId").value());
            models.stock.Group group = models.stock.Group.find(gId);
            if(gId != 0 && group == null){
                return notFound();
            }
            String startDate = form.field("startDate").value();
            String endDate = form.field("endDate").value();
            if(gId == 0) {
                List<StockMovement> supplieds = StockMovement.finder.where().eq("deleteStatus", false).between("date", startDate, endDate).findList();
                return ok(views.html.stock.reports.stockOutReportHistory.render(supplieds, startDate, endDate));
            }
            List<StockMovement> supplieds = StockMovement.finder.where()
                    .eq("deleteStatus", false)
                    .eq("item.category.group", group)
                    .between("date", startDate, endDate)
                    .findList();
            return ok(views.html.stock.reports.stockOutReportHistory.render(supplieds, startDate, endDate));
        }  else if (s.equalsIgnoreCase("harmonizationOut")) {
            Form<Item> form = Form.form(Item.class).bindFromRequest();
            Long gId = Long.parseLong(form.field("groupId").value());
            models.stock.Group group = models.stock.Group.find(gId);
            if(gId != 0 && group == null){
                return notFound();
            }
            String startDate = form.field("startDate").value();
            String endDate = form.field("endDate").value();
            if(gId == 0) {
                List<StockMovement> supplieds = StockMovement.finder.where()
                        .eq("deleteStatus", false)
                        .eq("request.employee.employeeFirstName", "Harmonization")
                        .between("date", startDate, endDate)
                        .findList();
                return ok(views.html.stock.reports.harmonizationOut.render(supplieds, startDate, endDate));
            }
            List<StockMovement> supplieds = StockMovement.finder.where()
                    .eq("deleteStatus", false)
                    .eq("request.employee.employeeFirstName", "Harmonization")
                    .eq("item.category.group", group)
                    .between("date", startDate, endDate)
                    .findList();
            return ok(views.html.stock.reports.harmonizationOut.render(supplieds, startDate, endDate));
        } else if (s.equalsIgnoreCase("FinanceLogReport")) {
            Form<Payment> form = Form.form(Payment.class).bindFromRequest();
            String uName = form.field("userNames").value();
            String startDate = form.field("startDate").value();
            String endDate = form.field("endDate").value();
            String doneBy = "";
            List<Payment> payments = null;
            if(!uName.equalsIgnoreCase("all")) {
                doneBy = uName;
                payments = Payment.finder.where()
                        .eq("deleteStatus", 0)
                        .eq("doneBy", uName)
                        .findList();
                return ok(views.html.reports.FinanceLogReport.render(payments, startDate, endDate, doneBy));
            }

            doneBy = "All staff";
            payments = Payment.finder.where()
                    .eq("deleteStatus", 0)
                    // .between("date", startDate, endDate)
                    .findList();
            return ok(views.html.reports.FinanceLogReport.render(payments, startDate, endDate, doneBy));
        }else if (s.equalsIgnoreCase("marksRecords")) {
            Form<AssignmentResult> form = Form.form(AssignmentResult.class).bindFromRequest();
            String uName = form.field("userNames").value();
            String startDate = form.field("startDate").value();
            String endDate = form.field("endDate").value();
            String doneBy = "";
            List<AssignmentResult> assignmentResults = null;
            if(!uName.equalsIgnoreCase("all")) {
                doneBy = uName;
                assignmentResults = AssignmentResult.find.where()
                       // .between("doneAt", startDate, endDate)
                        .eq("doneBy", uName)
                        .findList();
            }else{
                doneBy = "All staff";
                assignmentResults = AssignmentResult.find.where()
                        //.between("doneAt", startDate, endDate)
                        .findList();
            }
            return ok(views.html.reports.MarksLogReport.render(assignmentResults, startDate, endDate, doneBy));
        }else if (s.equalsIgnoreCase("examMarksResultsTrail")) {
            Form<SubMark> form = Form.form(SubMark.class).bindFromRequest();
            String uName = form.field("userNames").value();
            String startDate = form.field("startDate").value();
            String endDate = form.field("endDate").value();
            String doneBy = "";
            List<SubMark> subMarks = null;
            if(!uName.equalsIgnoreCase("all")) {
                doneBy = uName;
                subMarks = SubMark.find.where()
                       // .between("doneAt", startDate, endDate)
                        .eq("doneBy", uName)
                        .findList();
            }else{
                doneBy = "All staff";
                subMarks = SubMark.find.where()
                        //.between("doneAt", startDate, endDate)
                        .findList();
            }
            return ok(views.html.reports.examMarksResultsTrail.render(subMarks, startDate, endDate, doneBy));
        }else if (s.equalsIgnoreCase("stockApprovalTrail")) {
            Form<StockMovement> form = Form.form(StockMovement.class).bindFromRequest();
            String uName = form.field("userNames").value();
            String startDate = form.field("startDate").value();
            String endDate = form.field("endDate").value();
            String doneBy = "";
            List<StockMovement> stockMovements = null;
            if(!uName.equalsIgnoreCase("all")) {
                doneBy = uName;
                stockMovements = StockMovement.finder.where()
                       // .between("doneAt", startDate, endDate)
                        .eq("doneBy", uName)
                        .findList();
            }else{
                doneBy = "All staff";
                stockMovements = StockMovement.finder.where()
                        //.between("doneAt", startDate, endDate)
                        .findList();
            }
            return ok(views.html.reports.stockApprovalTrail.render(stockMovements, startDate, endDate, doneBy));
        } else if (s.equalsIgnoreCase("stockInOutHistory")) {
            Form<Item> form = Form.form(Item.class).bindFromRequest();
            String startDate = form.field("startDate").value();
            String endDate = form.field("endDate").value();

            List<Item> items = Item.finder
                    .where()
                    .eq("deleteStatus", false)
                    .findList();
            return ok(views.html.stock.reports.stockInOutReportHistory.render(items, startDate, endDate));
        } else if (s.equalsIgnoreCase("staffRequestHistory")) {
            Form<Request> form = Form.form(Request.class).bindFromRequest();
            String startDate = form.field("startDate").value();
            String endDate = form.field("endDate").value();
            Long emId = Long.parseLong(form.field("employee").value());
            Employee employee = Employee.find(emId);
            if (employee == null && emId != 0) return notFound();
            String title = "";
            if(emId == 0) {
                title = "Repport all employees";
                List<StockMovement> list = StockMovement.finder.where()
                        .eq("deleteStatus", false)
                        .not(Expr.eq("request.status", "pending"))
                        .between("date", startDate, endDate).findList();
                return ok(staffRequestHistory.render(list, startDate, endDate, title));
            }
            title = "Repport employee: "+employee.employeeFirstName;
            List<StockMovement> list = StockMovement.finder.where()
                    .eq("deleteStatus", false)
                    .eq("request.employee.id", employee.id)
                    .not(Expr.eq("request.status", "pending"))
                    .between("date", startDate, endDate).findList();
            return ok(staffRequestHistory.render(list, startDate, endDate, title));
        } else if (s.equalsIgnoreCase("staffRequestHistoryItem")) {
            Form<Request> form = Form.form(Request.class).bindFromRequest();
            String startDate = form.field("startDate").value();
            String endDate = form.field("endDate").value();
            Long empId = Long.parseLong(form.field("employee").value());
            Long iId = Long.parseLong(form.field("itemId").value());
            Employee employee = Employee.find(empId);
            Item item = Item.find(iId);
            String title = "";
            if(empId != 0 && iId != 0) {
                title = "Repport "+item.name+" "+employee.employeeFirstName;
                List<StockMovement> list = StockMovement.finder.where()
                        .eq("deleteStatus", false)
                        .eq("request.employee.id", employee.id)
                        .eq("item.id", item.id)
                        .eq("request.status", "Processed")
                        .between("date", startDate, endDate).findList();
                return ok(staffRequestHistory.render(list, startDate, endDate, title));
            }
            if(empId != 0 && iId == 0) {
                title = "Repport "+employee.employeeFirstName;
                List<StockMovement> list = StockMovement.finder.where()
                        .eq("deleteStatus", false)
                        .eq("request.employee.id", employee.id)
                        .eq("request.status", "Processed")
                        .between("date", startDate, endDate).findList();
                return ok(staffRequestHistory.render(list, startDate, endDate, title));
            }
            if(empId == 0 && iId != 0) {
                title = "Repport "+item.name;
                List<StockMovement> list = StockMovement.finder.where()
                        .eq("deleteStatus", false)
                        .eq("item.id", item.id)
                        .eq("request.status", "Processed")
                        .between("date", startDate, endDate).findList();
                return ok(staffRequestHistory.render(list, startDate, endDate, title));
            }
            title = "Repport all employee all item";
            List<StockMovement> list = StockMovement.finder.where()
                    .eq("deleteStatus", false)
                    .eq("request.status", "Processed")
                    .between("date", startDate, endDate).findList();
            return ok(staffRequestHistory.render(list, startDate, endDate, title));
        } else if (s.equalsIgnoreCase("staffRequestHistoryUnit")) {
            Form<Request> form = Form.form(Request.class).bindFromRequest();
            String startDate = form.field("startDate").value();
            String endDate = form.field("endDate").value();
            Long uId = Long.parseLong(form.field("unitId").value());
            Long iId = Long.parseLong(form.field("itemId").value());
            Unit unit = Unit.find(uId);
            Item item = Item.find(iId);
            String title = "";
            if(uId != 0 && iId != 0) {
                title = "Report "+unit.name+" "+item.name;
                List<StockMovement> list = StockMovement.finder.where()
                        .eq("deleteStatus", false)
                        .eq("request.employee.unit.id", unit.id)
                        .eq("item.id", item.id)
                        .eq("request.status", "Processed")
                        .between("date", startDate, endDate).findList();
                return ok(staffRequestHistory.render(list, startDate, endDate, title));
            }
            if(uId == 0 && iId != 0) {
                title = "Report "+item.name;
                List<StockMovement> list = StockMovement.finder.where()
                        .eq("deleteStatus", false)
                        .eq("item.id", item.id)
                        .eq("request.status", "Processed")
                        .between("date", startDate, endDate).findList();
                return ok(staffRequestHistory.render(list, startDate, endDate, title));
            }
            if(uId != 0 && iId == 0) {
                title = "Report "+unit.name;
                List<StockMovement> list = StockMovement.finder.where()
                        .eq("deleteStatus", false)
                        .eq("request.employee.unit.id", unit.id)
                        .eq("request.status", "Processed")
                        .between("date", startDate, endDate).findList();
                return ok(staffRequestHistory.render(list, startDate, endDate, title));
            }
            title = "Report all unity and item";
            List<StockMovement> list = StockMovement.finder.where()
                    .eq("deleteStatus", false)
                    .eq("request.status", "Processed")
                    .between("date", startDate, endDate).findList();
            return ok(staffRequestHistory.render(list, startDate, endDate, title));
        } else if (s.equalsIgnoreCase("assetLocation")) {
            Form<Request> form = Form.form(Request.class).bindFromRequest();
            long location = Long.parseLong(form.field("location").value());
            long category = Long.parseLong(form.field("item").value());
            String status = form.field("status").value();
            ExpressionList<StockMovement> where = StockMovement.finder.where();
            if (location != 0) where.eq("location.id", location);
            if (category != 0) where.eq("item.id", category);
            if (status.equals("1"))
                where.raw("DATE(DATE_ADD(date,INTERVAL item.depleciationMinMonths YEAR))<DATE(now())");
            else if (status.equals("2"))
                where.raw("DATE(DATE_ADD(date,INTERVAL item.depleciationMinMonths YEAR))>=DATE(now())");

            where.eq("item.iType", "Non-Consumable")
                    .raw("deleteStatus = 0")
                    .findList();

            List<StockMovement> list = where.findList();

            return ok(views.html.stock.reports.assetByLocation.render(list));
        } else if (s.equalsIgnoreCase("depreciatedItems")) {
            Form<Request> form = Form.form(Request.class).bindFromRequest();
            List<Item> list = Item.finder.
                    where()
                    .eq("iType", "Non-consumable")
                    .raw("DATE(DATE_ADD(date,INTERVAL depleciation_min_months YEAR))<DATE(now())")
                    .findList();
            String s1 = "Report of removed expired items and depreciated assets";
            return ok(views.html.stock.reports.itemsReport.render(list, s1));
        } else if (s.equalsIgnoreCase("depreciatedItems-removed")) {
            Form<Request> form = Form.form(Request.class).bindFromRequest();
            List<Item> list = Item.finder.
                    where()
                    .eq("iType", "Non-consumable")
                    .eq("removed", "approved")
                    .raw("DATE(DATE_ADD(date,INTERVAL depleciation_min_months YEAR))<DATE(now())")
                    .findList();
            String s1 = "Report of removed depreciated asset ";
            return ok(views.html.stock.reports.itemsReportApproved.render(list, s1));
        } else if (s.equalsIgnoreCase("stockInSupplierItems")) {
            Form<Request> form = Form.form(Request.class).bindFromRequest();

            long supplier1 = Long.parseLong(form.field("supplier").value());

            Supplier supplier = Supplier.finder.byId(supplier1);

            long l = Long.parseLong(form.field("item").value());

            Item item = Item.finder.byId(l);


            String sName = supplier == null ? "All" : supplier.name;
            String iName = item == null ? "All" : item.name;

            ExpressionList<Supplied> where = Supplied.finder.where();

            if (supplier1 != 0) where.eq("supplier.id", supplier1);

            if (l != 0) where.eq("item.id", l);

            List<Supplied> suppliedList = where
                    .between("date", form.field("startDate").value(), form.field("endDate").value())
                    .findList();
            String s1 = "Supplier(" + sName + ") and item(" + iName + ") report";
            return ok(views.html.stock.reports.stockInSupplierItems.render(sName, suppliedList, s1));
        }
        return badRequest();
    }
    public static Result marksReport() {
        return ok(views.html.register.marksReports.render(Intake.all()));
    }
    public static Result DeliberationReports() {
        List<Training> trainings = Training.allDeliberated();
        return ok(views.html.register.DeliberationReports.render(trainings));
    }
    public static Result marksReportByStudent(String r) {
        Form<Student> studentForm = Form.form(Student.class).bindFromRequest();
        String regNumber = studentForm.field("regNumber").value();
        if (r.equalsIgnoreCase("reg")) {
            Student student = Student.findByRegNo2(regNumber);
            if (student == null) {
                return notFound();
            }
            List<Module> modules = student.myModules();
            boolean tr = false;
            String title = "";
            return ok(views.html.reports.marksReportByStudent.render(student, modules, title, tr));
        } else if (r.equalsIgnoreCase("dip")) {
            Student student = Student.findByRegNo(regNumber, isCleManager());
            if (student == null) {
                return ok(error.render("Oops!! Student not Found"));
            }
            return ok(views.html.student.certificate.render(student));
        }
        return badRequest();
    }
    public static Result deliberationReportPerTrainingStatus() {
        Form<Training> form = Form.form(Training.class).bindFromRequest();
        String status = form.field("status").value();
        Training training = Training.finderById(Long.parseLong(form.field("trainingId").value()));
        String title = "";
            if (status.equalsIgnoreCase("all")) {
                    if (training == null) {
                        return notFound();
                    }
                    List<Student> students = Student.byTrainingDeliberation(training.id);
                    List<Module> modules = new ArrayList<>();
                    if (!students.isEmpty())
                        modules = Module.finder.where()
                                .eq("program.id", students.get(0).training.iMode.campusProgram.program.id)
                                .findList();
                    title = "Deliberation report all status ";
                    return ok(views.html.reports.deliberationReportPerTrainingStatus.render(students, training, modules, title));
                }
            if (!status.equalsIgnoreCase("all")) {
                    if (training == null) {
                        return notFound();
                    }
                    List<Student> students = Student.reportByTrainingStatus(training.id, status);
                    List<Module> modules = new ArrayList<>();
                    if (!students.isEmpty())
                        modules = Module.finder.where()
                                .eq("program.id", students.get(0).training.iMode.campusProgram.program.id)
                                .findList();
                    title = "Deliberation report "+status+" status ";
                    return ok(views.html.reports.deliberationReportPerTrainingStatus.render(students, training, modules, title));
                }
        return notFound();
    }
    public static Result marksReportByIntake(Long id) {
        Intake intake = Intake.finderById(id);
        if (intake == null) {
            return notFound();
        }
        List<Student> students = Student.byIntakeOrdered(intake.id);

        List<Module> modules = new ArrayList<>();
        Training training = null;
        for(Student s : students){
            if(s.training.deleteStatus == false && s.training != null){
                training = s.training;
            }
        }

        if (!students.isEmpty())
            modules = Module.finder.where().eq("program.id", students.get(0).training.iMode.campusProgram.program.id).findList();
        return ok(views.html.reports.studentReportsByIntake.render(students, training, modules));
    }
    public static Result marksReportByTrainingFinal(Long id) {
        Training training = Training.finderById(id);
        if (training == null) {
            return notFound();
        }
        List<Student> students = Student.byTraining(training.id);

        List<Module> modules = new ArrayList<>();

        if (!students.isEmpty())
            modules = Module.finder.where().eq("program.id", students.get(0).training.iMode.campusProgram.program.id).findList();

        return ok(views.html.reports.marksReportByTrainingFinal.render(students, training, modules));
    }
    public static Result marksReportByTrainingReSit(Long id) {
        Training training = Training.finderById(id);
        if (training == null) {
            return notFound();
        }
        List<Student> students = Student.byTrainingInResit(training.id);

        List<Module> modules = new ArrayList<>();

        if (!students.isEmpty())
            modules = Module.finder.where()
                    .eq("deleteStatus", false)
                    .eq("program.id", students.get(0).training.iMode.campusProgram.program.id).findList();

        return ok(views.html.reports.marksReportByTrainingReSit.render(students, training, modules));
    }

    public static Result financialReportBySatus(String s) {
        Form<Student> form = Form.form(Student.class).bindFromRequest();
        List<Student> students;
        if (s.equalsIgnoreCase("pro")) {
            Program program = Program.finderById(Long.parseLong(form.field("program").value()));
            String status = form.field("status").value();
            List<Applied> applieds;
            applieds = Applied.finder.where()
                    .eq("applicant.student.training.iMode.campusProgram.program.id", program.id)
                    .eq("deleteStatus", false)
                    .eq("applicant.active", true)
                    .findList();
            return ok(views.html.reports.reportStatusByProgram.render(applieds, program, status));
        }
        if (s.equalsIgnoreCase("damagedItems")) {
            List<Damage> damages = Damage.all();
            return ok(views.html.reports.damagedItems.render(damages));
        } else if (s.equalsIgnoreCase("intake")) {
            List<Applied> applieds;
            Intake intake = Intake.finderById(Long.parseLong(form.field("intake").value()));
            String status = form.field("status").value();
            applieds = Applied.finder.where()
                    .add(Student.cleExp(isCleManager()))
                    .eq("status", true)
                    .eq("deleteStatus", false)
                    .eq("applicant.student.training.iMode.intake.id", intake.id)
                    .eq("applicant.active", true)
                    .findList();
            return ok(views.html.reports.reportStatusByIntake.render(applieds, intake, status));
        }else if (s.equalsIgnoreCase("intakeSessionStatus")) {
            Session session = Session.finderById(Long.parseLong(form.field("session").value()));
            Intake intake = Intake.finderById(Long.parseLong(form.field("intake").value()));
            String status = form.field("status").value();
            List<Applied> applieds;
            applieds = Applied.finder.where()
                    .add(Student.cleExp(isCleManager()))
                    .eq("applicant.student.training.iMode.sessionMode.session.id", session.id)
                    .eq("applicant.student.training.iMode.intake.id", intake.id)
                    .eq("applicant.deleteStatus", false)
                    .eq("applicant.active", true)
                    .not(Expr.eq("applicant", null))
                    .findList();
            String string = "Intake :" + intake.print() + ", Session :" + session.sessionName;
            return ok(views.html.reports.financialReportByIntakeSessionSatus.render(applieds, status, string));
        } else if (s.equalsIgnoreCase("session")) {
            List<Applied> applieds;
            Session session = Session.finderById(Long.parseLong(form.field("session").value()));
            String status = form.field("status").value();
            applieds = Applied.finder.where()
                    .eq("applicant.student.training.iMode.sessionMode.session.id", session.id)
                    .eq("deleteStatus", false)
                    .eq("applicant.active", true)
                    .findList();
            return ok(views.html.reports.reportStatusBySession.render(applieds, session, status));
        }else if (s.equalsIgnoreCase("refundReport")) {
            List<Refund> refundList = Refund.all();
            List<Training> trainings = Training.all();
            List<Intake> intakeList = Intake.all();
            return ok(views.html.reports.refundReport.render(refundList, trainings, intakeList));
        } else if (s.equalsIgnoreCase("refurnrepoerFinance")) {
            Long inId = Long.parseLong(form.field("intakeId").value());
            String status = form.field("status").value();
            Intake intake = Intake.finderById(inId);
            String intakeName = "";
            if(intake != null && inId != 0 && intake.year != null){
                intakeName = intake.intakeName+" - "+intake.year.yearName;
            }
            if(intake != null && inId != 0 && intake.year == null){
                intakeName = intake.intakeName;
            }else{
                intakeName = "All intake";
            }
            if(status.equalsIgnoreCase("all") &&  inId == 0){
                List<Refund> refundList3  = Refund.all();
                return ok(views.html.reports.refurnrepoerFinance.render(refundList3, status, intakeName));
            }
            if(!status.equalsIgnoreCase("all") &&  inId == 0){
                List<Refund> refundList2  = Refund.finder.where()
                        .eq("refundRequest.status", Boolean.parseBoolean(status))
                        .findList();
                return ok(views.html.reports.refurnrepoerFinance.render(refundList2, status, intakeName));
            }
            if(status.equalsIgnoreCase("all") && inId != 0){
                List<Refund> refundList1  = Refund.finder.where()
                        .eq("account.applicant.student.training.iMode.intake.id", intake.id)
                        .findList();
                return ok(views.html.reports.refurnrepoerFinance.render(refundList1, status, intakeName));
            }
            if(!status.equalsIgnoreCase("all") &&  inId != 0){
                List<Refund> refundList4  = Refund.finder.where()
                        .eq("account.applicant.student.training.iMode.intake.id", intake.id)
                        .eq("refundRequest.status", Boolean.parseBoolean(status))
                        .findList();
                return ok(views.html.reports.refurnrepoerFinance.render(refundList4, status, intakeName));
            }
        } else if (s.equalsIgnoreCase("intakeStatus")) {
            List<Applied> applieds;
            Long iId = Long.parseLong(form.field("intake").value());
            Intake intake = Intake.finderById(iId);
            String status = form.field("status").value();
            if(iId != 0 && intake == null) return notFound();
            if(iId == 0) {
                applieds = Applied.finder.where()
                        .eq("applicant.deleteStatus", false)
                        .eq("deleteStatus", false)
                        .findList();
                String string = "";
                return ok(views.html.reports.financialReportByIntakeSessionSatusApplicants.render(applieds, status, string));
            }
            applieds = Applied.finder.where()
                    .eq("training.iMode.intake.id", intake.id)
                    .eq("applicant.deleteStatus", false)
                    .eq("deleteStatus", false)
                    .findList();
            String string = "";
            return ok(views.html.reports.financialReportByIntakeSessionSatusApplicants.render(applieds, status, string));
        }
        return ok();
    }
}
