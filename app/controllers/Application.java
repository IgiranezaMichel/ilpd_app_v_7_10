package controllers;
import com.avaje.ebean.*;
import models.*;
import models.Utility.Constants;
import models.stock.*;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import views.html.*;
import views.html.admininistrator.days;
import views.html.helper.form;
import views.html.library.updateLibrary;
import views.html.stock.block.edit;
import views.html.stock.dashboard;
import views.html.stock.requests.details;
import views.html.student.notStudent;
import java.io.File;
import java.util.*;

public class Application extends React {
    public static Result evSettingsPage() {
        return ok(views.html.admininistrator.evSettings.render());
    }
    public static Result dateRange(){
        return ok(views.html.dateRange.render());
    }
    public static Result getChartDataByGender(String gender, boolean b) {

        String sql = "SELECT COUNT(a.id) as total,MONTH(a.date) as month FROM applicant a INNER JOIN student s ON a.id=s.applicant_id INNER JOIN training t ON t.id = s.training_id INNER JOIN intake_session_mode ism ON t.i_mode_id = ism.id INNER JOIN campus_program cp ON cp.id = ism.campus_program_id INNER JOIN program p ON cp.program_id = p.id WHERE a.gender=:g and p.cle=:b GROUP BY MONTH(a.date)";

        SqlQuery sqlQuery = Ebean.createSqlQuery(sql);
        List<SqlRow> sqlRows = sqlQuery
                .setParameter("g", gender)
                .setParameter("b", b).findList();
        String data = "";
        for (SqlRow row : sqlRows) {
            if (!Objects.equals(data, "")) {
                data += ",";
            }
            data += "[" + row.getString("month") + "," + row.getInteger("total") + "]";
        }
        data = "[" + data + "]";
        return ok(data);
    }
    public static Result getStudentBy(Long id, Long c) {
        Training training = Training.finderById(id);
        if (training == null) return ok();
        return ok(views.html.register.tStudent.render(training.students(), c));
    }
    public static Result getStudentExistsGroupBy(Long id, Long gid) {
        Groups group = Groups.finderById(gid);
        Training training = Training.finderById(group.training.id);
        Component component = Component.finderById(group.component.id);
        if (training == null) return ok();
        return ok(views.html.register.tStudentGroup.render(training.Mystudents(), component.id, group));
    }
    public static Result uploadPayment(Long id) {
        Payment payment = Payment.finderById(id);
        if (payment == null) return ok("Not found");
        return ok(views.html.student.uploadPayment.render(payment));
    }

    //Charts by program
    public static Result getStudentByProgram() {
        String sql = "SELECT program.program_acronym as name,COUNT(program.id) AS total FROM student INNER JOIN training ON student.training_id=training.id INNER JOIN intake_session_mode ON training.i_mode_id=intake_session_mode.id INNER JOIN campus_program ON campus_program.id=intake_session_mode.campus_program_id INNER JOIN program ON program.id=campus_program.program_id GROUP BY program.program_acronym";

        SqlQuery sqlQuery = Ebean.createSqlQuery(sql);
        List<SqlRow> sqlRows = sqlQuery.findList();
        String data = "";
        for (SqlRow row : sqlRows) {
            if (!Objects.equals(data, "")) {
                data += ",";
            }
            data += "[\"" + row.getString("name") + "\"," + row.getInteger("total") + "]";
        }
        data = "[" + data + "]";
        return ok(data);
    }
    public static Result pendingIntershipClaims() {
        List<InternshipAppeal> internshipAppeals = InternshipAppeal.allPending();
        return ok(views.html.register.pendingIntershipClaims.render(internshipAppeals));
    }
    public static Result reSitRequests() {
        List<ReSitReTakeRequest> requests = ReSitReTakeRequest.allPendingResit();
        return ok(views.html.register.reSitRequests.render(requests));
    }
    public static Result uploadStudentPayment(Long id) {
        Form<Payment> f = Form.form(Payment.class).bindFromRequest();
        Payment payment = f.get();
        Payment payment1 = Payment.finderById(id);
        if(payment.attachment != null) {
            payment1.attachment = uploadFile(new Date().toString(), "attachment","payment_transaction_records/");
        }
        payment1.update(id);
        return redirect("/%2Fstudent%2Fpayments%2F#");
    }
    public static Result retakemoduleRequests() {
        List<ReSitReTakeRequest> requests = ReSitReTakeRequest.allPendingRetakeModule();
        return ok(views.html.register.retakemoduleRequests.render(requests));
    }
    public static Result retakeProgramRequests() {
        List<ReSitReTakeRequest> requests = ReSitReTakeRequest.allPendingRetakeProgram();
        return ok(views.html.register.retakeProgramRequests.render(requests));
    }
    public static Result processedIntershipClaims() {
        List<InternshipAppeal> internshipAppeals = InternshipAppeal.allProcessed();
        return ok(views.html.register.processedIntershipClaims.render(internshipAppeals));
    }

    public static Result refreshChat() {
        Users u = Ins("user");
        if (u != null) {
            List<ChatMessage> chats = u.newMess();
            return ok(Json.toJson(chats));
        }
        return ok("0");
    }


    // ### Charts Ends #####################3

    static Users Ins(String key) {
        return Users.finderByMail(session(key));
    }

    static Lecture activeL(Users u) {
        return Lecture.byUser(u.id);
    }

    public static Result getClearance() {
        return ok(views.html.finance.clearance.render());
    }

    public static Result searchStudent(String key) {
        List<Student> lst = Student.byKey(key);
        return ok(views.html.finance.clearedStudents.render(lst));
    }




    public static Result HomeIndex(String string) {
        return index();
    }
    public static Result index() {

        if (isAjax()) {
            return ok(errorPage.render("external request is authorized", null, false));
        }

        if (session("admin") != null) {
            return adminPlace("",Ins("admin"));
        } else if (session("student") != null) {
            return studentPage(Ins("student"));
        } else if (isRegistrar()) {
            Users u = Ins("registrar");
            List<AnounceRole> anounceRoles = AnounceRole.finder
                    .setDistinct(true)
                    .where()
                    .eq("deleteStatus", false)
                    .eq("role.sessionName", "registrar")
                    .orderBy("id desc").findList();
            return ok(views.html.register.register.render(anounceRoles, "registrar", u, getAnnounce(u)));
        }else if (isPlanning()) {
            Users u = Ins("Planning");
            List<AnounceRole> anounceRoles = AnounceRole.finder
                    .setDistinct(true)
                    .where()
                    .eq("deleteStatus", false)
                    .eq("role.sessionName", "Planning")
                    .orderBy("id desc").findList();
            return ok(views.html.register.plans.render(anounceRoles, "Planning", u, getAnnounce(u)));
        } else if (isCleManager()) {
            List<AnounceRole> anounceRoles = AnounceRole.finder
                    .setDistinct(true)
                    .where()
                    .eq("deleteStatus", false)
                    .eq("role.sessionName", "DTR/Coordinator")
                    .orderBy("id desc").findList();
            Users u = Ins("DTR/Coordinator");
            return ok(views.html.coordinator.DTRCoordinator.render(anounceRoles, "DTR/Coordinator", u, getAnnounce(u)));
        } else if (session("Rector") != null) {
            List<AnounceRole> anounceRoles = AnounceRole.finder
                    .setDistinct(true)
                    .where()
                    .eq("deleteStatus", false)
                    .eq("role.sessionName", "Rector")
                    .orderBy("id desc").findList();
            Users u = Ins("Rector");
            return ok(views.html.rector.index.render(anounceRoles,"Rector", u, getAnnounce(u)));
        }else if (session("VRAC") != null) {
            List<AnounceRole> anounceRoles = AnounceRole.finder
                    .setDistinct(true)
                    .where()
                    .eq("deleteStatus", false)
                    .eq("role.sessionName", "VRAC")
                    .orderBy("id desc").findList();
            Users u = Ins("VRAC");
            return ok(views.html.vrac.index.render(anounceRoles, "VRAC", u, Announce.all("VRAC")));
        } else if (session("Staff") != null) {
            List<AnounceRole> anounceRoles = AnounceRole.finder
                    .setDistinct(true)
                    .where()
                    .eq("deleteStatus", false)
                    .eq("role.sessionName", "Staff")
                    .orderBy("id desc").findList();
            Users u = Ins("Staff");
            return ok(views.html.staff.index.render(anounceRoles, "Staff", u, getAnnounce(u)));
        } else if (session("assistant") != null) {
            List<AnounceRole> anounceRoles = AnounceRole.finder
                    .setDistinct(true)
                    .where()
                    .eq("deleteStatus", false)
                    .eq("role.sessionName", "assistant")
                    .orderBy("id desc").findList();
            Users u = Ins("assistant");
            return ok(views.html.register.register.render(anounceRoles, "assistant", u, getAnnounce(u)));
        } else if (session("Instructor") != null) {
            List<AnounceRole> anounceRoles = AnounceRole.finder
                    .setDistinct(true)
                    .where()
                    .eq("deleteStatus", false)
                    .eq("role.sessionName", "Instructor")
                    .orderBy("id desc").findList();
            Users u = Ins("Instructor");
            return ok(views.html.lecture.lectureM.render(anounceRoles, "Instructor", u, getAnnounce(u)));
        } else if (session("Coordinator") != null) {
            List<AnounceRole> anounceRoles = AnounceRole.finder
                    .setDistinct(true)
                    .where()
                    .eq("deleteStatus", false)
                    .eq("role.sessionName", "Coordinator")
                    .orderBy("id desc").findList();
            Users u = Ins("Coordinator");
            return ok(views.html.coordinat.coordinator.render(anounceRoles,"Coordinator", u, getAnnounce(u)));
        } else if (session("mark_officer") != null) {
            List<AnounceRole> anounceRoles = AnounceRole.finder
                    .setDistinct(true)
                    .where()
                    .eq("deleteStatus", false)
                    .eq("role.sessionName", "mark_officer")
                    .orderBy("id desc").findList();
            Users u = Ins("mark_officer");
            return ok(views.html.coordinator.marksOfficer.render(anounceRoles,"mark_officer", u, getAnnounce(u)));
        } else if (session("Finance") != null) {
            List<AnounceRole> anounceRoles = AnounceRole.finder
                    .setDistinct(true)
                    .where()
                    .eq("deleteStatus", false)
                    .eq("role.sessionName", "Finance")
                    .orderBy("id desc").findList();
            Users u = Ins("Finance");
            return ok(views.html.finance.finance.render(anounceRoles, "Finance", u, getAnnounce(u)));
        } else if (session("Library") != null) {
            List<AnounceRole> anounceRoles = AnounceRole.finder
                    .setDistinct(true)
                    .where()
                    .eq("deleteStatus", false)
                    .eq("role.sessionName", "Library")
                    .orderBy("id desc").findList();
            Users u = Ins("Library");
            return ok(views.html.library.library.render(anounceRoles, "Library", u, getAnnounce(u)));
        } else if (session("Logistic") != null) {
            List<AnounceRole> anounceRoles = AnounceRole.finder
                    .setDistinct(true)
                    .where()
                    .eq("deleteStatus", false)
                    .eq("role.sessionName", "Logistic")
                    .orderBy("id desc").findList();
            Users u = Ins("Logistic");
            return ok(views.html.stock.logistic.render(anounceRoles, "Logistic", u));
        } else if (session("DAF") != null) {
            List<AnounceRole> anounceRoles = AnounceRole.finder
                    .setDistinct(true)
                    .where()
                    .eq("deleteStatus", false)
                    .eq("role.sessionName", "DAF")
                    .orderBy("id desc").findList();
            Users u = Ins("DAF");
            return ok(views.html.stock.DAF.index.render(anounceRoles, "DAF", u, Announce.all("DAF")));
        } else if (session("Accountant") != null) {
            List<AnounceRole> anounceRoles = AnounceRole.finder
                    .setDistinct(true)
                    .where()
                    .eq("deleteStatus", false)
                    .eq("role.sessionName", "Accountant")
                    .orderBy("id desc").findList();
            Users u = Ins("Accountant");
            return ok(views.html.stock.Accountant.Accountant.render(anounceRoles, u, "Accountant page", "Accountant"));
        } else if (isInsurance()) {
            List<AnounceRole> anounceRoles = AnounceRole.finder
                    .setDistinct(true)
                    .where()
                    .eq("deleteStatus", false)
                    .eq("role.sessionName", "qualityInsurance")
                    .orderBy("id desc").findList();
            Users u = Ins(qualityInsurance);
            return ok(views.html.stock.Accountant.Accountant.render(anounceRoles, u, "Quality assurance", qualityInsurance));
        } else if (session("Auditor") != null) {
            List<AnounceRole> anounceRoles = AnounceRole.finder
                    .setDistinct(true)
                    .where()
                    .eq("deleteStatus", false)
                    .eq("role.sessionName", "Auditor")
                    .orderBy("id desc").findList();
            Users u = Ins("Auditor");
            return ok(views.html.stock.Auditor.Auditor.render(anounceRoles,"Auditor", u));
        } else if (session("Manager") != null) {
            List<AnounceRole> anounceRoles = AnounceRole.finder
                    .setDistinct(true)
                    .where()
                    .eq("deleteStatus", false)
                    .eq("role.sessionName", "Manager")
                    .orderBy("id desc").findList();
            Users u = Ins("Manager");
            return ok(views.html.stock.Manager.Manager.render(anounceRoles,"Manager", u));
        } else if (session("HeadOfUnit") != null) {
            List<AnounceRole> anounceRoles = AnounceRole.finder
                    .setDistinct(true)
                    .where()
                    .eq("deleteStatus", false)
                    .eq("role.sessionName", "HeadOfUnit")
                    .orderBy("id desc").findList();
            Users u = Ins("HeadOfUnit");
            return ok(views.html.stock.HeadOfUnit.index.render(anounceRoles,"HeadOfUnit", u, Announce.all("HeadOfUnit")));
        }else {
            return ok(index.render());
        }
    }

    private static List<Announce> getAnnounce(Users user) {
        List<Announce> anns = Announce.all(user.role);
        for (Announce a : anns) {
            if (a.readBy(user)) {
                a.deleteStatus = true;
            }
        }
        return anns;
    }

    public static int getAnnounceNumber(Users user) {
        List<Announce> anns = Announce.all(defaultSession);
        int ap = 0;
        for (Announce a : anns) {
            if (!a.readBy(user)) {
                ap++;
            }
        }
        return ap;
    }

    private static Result studentPage(Users user) {
        if (user != null) {
            List<AnounceRole> anounceRoles = AnounceRole.finder
                    .setDistinct(true)
                    .where()
                    .eq("deleteStatus", false)
                    .eq("role.sessionName", "student")
                    .orderBy("id desc").findList();
            return ok(views.html.student.studentPage.render(anounceRoles, user, getAnnounce(user)));
        } else {
            return redirect(routes.Application.getOut());
        }
    }

    public static Result announceFullView(Long id) {
        Announce announce = Announce.finderById(id);
        if (announce == null) {
            return ok(errorPage.render("bad Reference", null, !isAjax()));
        }
        return ok(views.html.student.moreAnnounce.render(announce));
    }

    public static Result downloadAnnounce(Long id) {
        Announce announce = Announce.finderById(id);
        if(announce == null || announce.attachment == null || new java.io.File("public/uploads/" + announce.attachment) != null){
            return ok("Announcement not found");
        }
        return ok(new java.io.File("public/uploads/" + announce.attachment));
    }

    public static Result markViewed(Long id) {
        if (session("user") != null) {
            Users user = Ins("user");
            if (user == null) {
                return ok("0");
            }

            Announce announce = Announce.finderById(id);

            Checked r = new Checked();
            r.user = user;
            r.announce = announce;


            if (r.notExist()) {
                r.save();
            }

            return ok("1");
        }
        return ok("0");
    }

    private static Result adminPlace(String userType, Users user) {
        userType = "admin";
        List<AnounceRole> anounceRoles = AnounceRole.finder
                .setDistinct(true)
                .where()
                .eq("deleteStatus", false)
                .eq("role.sessionName", "admin")
                .orderBy("id desc").findList();
        if (user != null) {
            return ok(admin.render(anounceRoles, userType, user, Announce.all(user.role)));
        } else {
            return redirect(routes.Application.getOut());
        }
    }

    public static Result printCard(Long stuId) {
        if (isRegistrar()) {
            Student st = Student.finderById(stuId);
            return ok(views.html.register.printer.render(st));
        } else {
            return ok();
        }
    }

    public static Result getOut() {
        session().clear();
        return redirect(routes.Application.index());
    }
    public static Result getAssignmentUpdatePage(Long id, Long lId) {
        if (session("Instructor") != null || session("Coordinator") != null || session("mark_officer") != null || session("registrar") != null) {
            Lecture lecture = Lecture.finderById(lId);
            Assignment assignment = Assignment.finderById(id);
            if (assignment == null) {
                return ok(errorPage.render("Assignment not available", null, !isAjax()));
            }
            return ok(views.html.lecture.assignmentUpdate.render(assignment, lecture.myComp(), lecture.myTrainingsAll()));
        }
        return ok();
    }
    public static Result uploadAssignment(Long id, Long lId) {
        if (session("Instructor") != null || session("Coordinator") != null || session("mark_officer") != null || session("registrar") != null) {
            Lecture lecture = Lecture.finderById(lId);
            Assignment assignment = Assignment.finderById(id);
            if (assignment == null) {
                return ok(errorPage.render("Assignment not available", null, !isAjax()));
            }
            return ok(views.html.lecture.uploadAssignment.render(assignment, lecture.myComp(), lecture.myTrainingsAll()));
        }
        return ok();
    }

    public static Result deleteAssignmentPage(Long id, Long lId) {
        if (session("Instructor") != null || session("Coordinator") != null || session("mark_officer") != null || session("registrar") != null) {
            Lecture lecture = Lecture.finderById(lId);
            Assignment assignment = Assignment.finderById(id);
            if (assignment == null) {
                return ok(errorPage.render("Assignment not available", null, !isAjax()));
            }
            return ok(views.html.lecture.deleteAssignmentPage.render(assignment, lecture.myComp(), lecture.myTrainingsAll()));
        }
        return ok();
    }

    public static Result getAssignment() {
        if (isApplicant()) {
            Student stu = applicant().student;
            if (stu == null) {
                return ok(notStudent.render());
            } else {
                if(!stu.inResit) {
                    List<SqlRow> assign = stu.myGroupAssignments();
                    List<SqlRow> assignTwo = stu.myGroupAssignmentsTwo();
                    List<Assignment> ind = stu.myIndividualAssignments();
                    return ok(views.html.student.viewAssignment.render(stu, assign, assignTwo, ind));
                }
                if(stu.inResit) {
                    List<SqlRow> assign = stu.myGroupAssignments();
                    List<SqlRow> assignTwo = stu.myGroupAssignmentsTwoResearch();
                    List<Assignment> ind = stu.myIndividualAssignmentsReseach();
                    return ok(views.html.student.viewAssignment.render(stu, assign, assignTwo, ind));
                }
            }
        }
        return ok("0");
    }

    public static Result getUnSubmiters(Long assId) {
        Assignment ass = Assignment.finderById(assId);
        if (ass == null) {
            return ok("Assignment not found");
        }
        if(!ass.grouped) {
            List<Submission> sub = ass.myUnSubmissions();
            if (session("Instructor") != null && Ins("Instructor") != null) {
                Lecture lect = activeL(Ins("Instructor"));
                if (lect == null) {
                    return ok("error");
                }
                return ok(views.html.lecture.allUnSubmissions.render(sub));
            }
            if (session("Coordinator") != null || Ins("Coordinator") != null  || Ins("mark_officer") != null || session("registrar") != null) {
                return ok(views.html.lecture.allUnSubmissions.render(sub));
            }
        }
        if(ass.grouped) {
            List<GroupSubmission> sub = ass.myUnSubmissionsGroup();
            if (session("Instructor") != null && Ins("Instructor") != null) {
                Lecture lect = activeL(Ins("Instructor"));
                if (lect == null) {
                    return ok("error");
                }
                return ok(views.html.lecture.allUnSubmissionsGroup.render(sub));
            }
            if (session("Coordinator") != null && Ins("Coordinator") != null && Ins("mark_officer") != null || session("registrar") != null) {
                return ok(views.html.lecture.allUnSubmissionsGroup.render(sub));
            }
        }
        return ok("0");
    }

    public static Result getViewedSubmiters(Long assId) {
        Assignment ass = Assignment.finderById(assId);
        if (ass == null) {
            return ok("Assignment not found");
        }
        if(!ass.grouped) {
            List<Submission> sub = ass.myViewedSubmissions();
            if (session("Instructor") != null && Ins("Instructor") != null) {
                Lecture lect = activeL(Ins("Instructor"));
                if (lect == null) {
                    return ok("error");
                }
                return ok(views.html.lecture.allViewedSubmissions.render(sub));
            }
            if (session("Coordinator") != null || Ins("Coordinator") != null  || Ins("mark_officer") != null || session("registrar") != null) {
                return ok(views.html.lecture.allViewedSubmissions.render(sub));
            }
        }
        if(ass.grouped) {
            List<GroupSubmission> sub = ass.myViewedSubmissionsGroup();
            if (session("Instructor") != null && Ins("Instructor") != null) {
                Lecture lect = activeL(Ins("Instructor"));
                if (lect == null) {
                    return ok("error");
                }
                return ok(views.html.lecture.allViewedSubmissionsGroup.render(sub));
            }
            if (session("Coordinator") != null && Ins("Coordinator") != null && Ins("mark_officer") != null || session("registrar") != null) {
                return ok(views.html.lecture.allViewedSubmissionsGroup.render(sub));
            }
        }
        return ok("0");
    }

    public static Result getUnEditSubmiters(Long assId) {
        Assignment ass = Assignment.finderById(assId);
        if (ass == null) {
            return ok("Assignment not found");
        }
        if(!ass.grouped) {
            List<Submission> sub = ass.myViewedSubmissions();
            if (session("Instructor") != null && Ins("Instructor") != null) {
                Lecture lect = activeL(Ins("Instructor"));
                if (lect == null) {
                    return ok("error");
                }
                return ok(views.html.lecture.getUnEditSubmiters.render(sub));
            }
            if (session("Coordinator") != null || Ins("Coordinator") != null  || Ins("mark_officer") != null || session("registrar") != null) {
                return ok(views.html.lecture.allUnSubmissions.render(sub));
            }
        }
        if(ass.grouped) {
            List<GroupSubmission> sub = ass.myViewedSubmissionsGroup();
            if (session("Instructor") != null && Ins("Instructor") != null) {
                Lecture lect = activeL(Ins("Instructor"));
                if (lect == null) {
                    return ok("error");
                }
                return ok(views.html.lecture.allUnSubmissionsGroup.render(sub));
            }
            if (session("Coordinator") != null && Ins("Coordinator") != null && Ins("mark_officer") != null || session("registrar") != null) {
                return ok(views.html.lecture.allUnSubmissionsGroup.render(sub));
            }
        }
        return ok("0");
    }

    public static Result getSubmiters(Long assId, Boolean bool) {
        Assignment ass = Assignment.finderById(assId);
        if (ass == null) {
            return ok("0");
        }
        if (bool) {
            if(ass == null){
                return ok("No assignment");
            }
            List<GroupSubmission> subList = ass.myGroupSubmissions();
            return ok(views.html.lecture.groupSubmission.render(subList, ass));
        } else {
            List<Submission> sub = ass.mySubmissions();

            if (session("Instructor") != null && Ins("Instructor") != null) {
                Lecture lect = activeL(Ins("Instructor"));
                if (lect == null) {
                    return ok("error");
                }
                return ok(views.html.lecture.allSubmissions.render(sub));
            }
            if (session("Coordinator") != null && Ins("Coordinator") != null || (session("registrar") != null) || (session("mark_officer") != null)) {
                return ok(views.html.lecture.allSubmissions.render(sub));
            }
        }
        return ok("0");
    }

    public static Result preGet() {
        return ok(views.html.register.preGet.render());
    }

    public static Result studentRegistration() {
        if (session("student") != null) {
            Users u = Users.finderByMail(session("student"));
            if (u != null) {
                return ok(views.html.student.firstPage.render(u, Intake.all()));
            } else {
                return ok("errorx");
            }
        } else {
            return ok("error");
        }
    }

    public static Result formRequested() {
        if (session("student") != null) {
            Users u = Users.finderByMail(session("student"));

            return form(Applicant.finderByUserActive(u.id), u.stating);
        }
        return ok("error");
    }

    public static Result form(Applicant applicant, String str) {
        Applied aps = null;
        if (applicant != null && applicant.active ) {
            aps = Applied.byApp(applicant.id);
            applicant = aps.applicant;
        }

        if (aps == null || aps.deleteStatus == true) {
            return Application.getOut();
        }else {
            return ok(views.html.student.registration.render(str, applicant, applicant.user, aps));
        }
    }
    public static Result errorRegistration(){
        return ok(views.html.student.errorRegistration.render());
    }
    public static Result checkTrain(Long intId, Long sMode, Long proId) {
        Users userStudent = userStudent();

        if( userStudent == null ) return ok("0");

        List<Training> t = Training.finderByDual(intId, sMode, proId,userStudent.id);

        return ok(Json.toJson(t));
    }
    public static Result formRequest(String str) {
        if ( isApplicant() ) {
            Form<Training> fc = Form.form(Training.class).bindFromRequest();
            Users u = userStudent();
            String iId = fc.field("myIntake").value();
            Long intakeId = Long.parseLong(iId);
            str = fc.field("mySession").value();
            Long seS = Long.parseLong(str);
            String tId = fc.field("training").value();
            if( tId == null ) return notFound();
            Long trainingId = Long.parseLong(tId);
            Training training = Training.finderById(trainingId);
            if( training == null ) return notFound();
            u.stating = str;
            u.update();
            Applicant app = new Applicant();
            app.user = u;
            int cls = Counts.countMyCLE(u.id);
            int dlps = Counts.countMyDLP(u.id);
            int nberTraining = Counts.countSameTraining(u.id, training.id);
            /*
            if(Counts.isCles(training) && cls > 0){
                return ok(views.html.student.errorRegistration.render());
            }
             */
            if(nberTraining > 0){
                return ok(views.html.student.errorRegistration.render());
            }
            if(!Counts.isCles(training) && dlps > 0){
                return ok(views.html.student.errorRegistration.render());
            }
            System.out.print(app.familyName + app.firstName);
            app.save();
            Applied applied = new Applied();
            applied.applicant = app;
            applied.training = training;
            if (applied.notExisted()) {
                applied.save();
            }
            return form(app, str);
        } else {
            return ok("er ror");
        }
    }

    public static Result updateInfo() {
        if (session("admin") != null) {

            return ok(views.html.admininistrator.updatePro.render());
        } else {
            return ok("error requested");
        }
    }

    public static Result academicPage() {
        if (session("admin") != null) {
            return ok(views.html.admininistrator.academicPage.render());
        } else {
            return ok("error requested");
        }
    }

    public static Result assignmentPage() {
        if (session("Instructor") != null) {

            return ok(views.html.lecture.assignmentPage.render());
        } else {
            return ok("error requested");
        }
    }

    public static Result myTranscript(Boolean truely) {
        if (session("student") != null && Ins("student") != null) {
            Users user = Users.finderByMail(session("student"));
            Applicant applicant = user.amApplicant();
            Student student = applicant.student;
            if (student == null) {
                return ok(notStudent.render());
            }

            return loadTranscript(student.id, truely);
        }
        return ok(notStudent.render());
    }

    public static Result loadTranscript(Long student, Boolean tr) {
        Student stu = Student.finderById(student);
        if (stu == null) {
            return ok(notStudent.render());
        }
        List<Module> lstComps = stu.myModules();
        List<Component> components = stu.myComponents();
        String title = "";
        if(stu.training.hasGraduatedFinal){
             title = "FINAL ACADEMIC TRANSCRIPT";
        }else{
            title = "PROVISIONAL MARKS RESULTS";
        }
        return ok(views.html.student.transcript.render(stu, lstComps, components, title, tr));
    }


    public static Result loadDegree(Long student, Boolean tr) {
        Student stu = Student.finderById(student);
        if (stu == null) {
            return ok(notStudent.render());
        }
        List<Module> lstComps = stu.myModules();
        String title = "";
        if(stu.training.hasGraduatedFinal){
             title = "FINAL ACADEMIC TRANSCRIPT";
        }else{
            title = "PROVISIONAL MARKS RESULTS";
        }
        List<Component> components = stu.myComponents();
        return ok(views.html.student.transcript.render(stu, lstComps, components, title, tr));
    }

    public static Result signatureForm() {
        return ok(views.html.register.signature.render(ProfileInfo.finder.all().get(0)));
    }

    private static Boolean activator(Long id) {
        AcademicYear ac = AcademicYear.finderById(id);
        ac.status = true;
        ac.update();
        return true;
    }

    private static Boolean activatorIntake(Long id) {
        Intake ac = Intake.finderById(id);
        ac.isClosed = true;
        ac.update();
        return true;
    }
    private static Boolean activatorProgram(Long id) {
        Training ac = Training.finderById(id);
        ac.isClosedA = true;
        ac.update();
        return true;
    }
    private static Boolean activatorResult(Long id) {
        Training ac = Training.finderById(id);
        ac.isComponent = true;
        ac.update();
        return true;
    }

    public static Result activate(Long id) {
        if (session("admin") != null) {
            Boolean ip = activator(id);
        }

        return ok("1");
    }

    public static Result activateIntake(Long id) {
        if (session("admin") != null) {
            Boolean ip = activatorIntake(id);
        }

        return ok("1");
    }
    public static Result activateProgram(Long id) {
        if (session("admin") != null || session("Coordinator") != null || session("mark_officer") != null || session("registrar") != null) {
            Boolean ip = activatorProgram(id);
        }

        return ok("1");
    }
    public static Result activateResult(Long id) {
        if (session("admin") != null || session("Coordinator") != null || session("mark_officer") != null || session("registrar") != null) {
            Training ac = Training.finderById(id);
            ac.isComponent = true;
            ac.update();
        }
        return ok("1");
    }

    public static Result deActivate(Long id) {
        if (session("admin") != null) {
            AcademicYear ac = AcademicYear.finderById(id);
            ac.status = false;
            ac.update();
            return ok("1");
        }
        return ok("0");
    }

    public static Result deActivateIntake(Long id) {
        if (session("admin") != null) {
            Intake in = Intake.finderById(id);
            in.isClosed = false;
            in.update(id);
            return ok("1");
        }
        return ok("0");
    }

    public static Result deActivateProgram(Long id) {
        if (session("admin") != null || session("Coordinator") != null || session("mark_officer") != null || session("registrar") != null) {
            Training in = Training.finderById(id);
            in.isClosedA = false;
            in.update(id);
            return ok("1");
        }
        return ok("0");
    }

    public static Result deActivaResult(Long id) {
        if (session("admin") != null || session("Coordinator") != null || session("mark_officer") != null || session("registrar") != null) {
            Training ac = Training.finderById(id);
            ac.isComponent = false;
            ac.update();
        }
        return ok("0");
    }

    public static Result studentList(Long grId) {
        if (session("Instructor") != null || session("Coordinator") != null || session("mark_officer") != null) {
            Groups c = Groups.finderById(grId);
            List<GroupMembers> stus = c.gMembers();
            return ok(views.html.lecture.studentList.render(stus, c));
        }
        return ok("0");
    }

    public static Result moveNext(Long memberShip) {
        GroupMembers g = GroupMembers.finder.ref(memberShip);
        if (g == null) {
            return ok(errorPage.render("bad reference", null, !isAjax()));
        }

        List<Groups> notG = g.groups.notThis();
        return ok(views.html.lecture.moveGroup.render(notG, g));
    }

    public static Result moveGroupMember(Long mShip) {
        GroupMembers g = GroupMembers.finder.ref(mShip);
        Form<Groups> gr = Form.form(Groups.class).bindFromRequest();

        Long grId = Long.parseLong(gr.field("groupId").value());

        g.groups = Groups.finderById(grId);

        if (g.checkeer()) {
            g.update();
        }

        return ok("1");
    }

    public static Result searchMember(Long grId, String q) {
        if (session("Instructor") != null || session("Coordinator") != null || session("mark_officer") != null) {
            Groups group = Groups.finderById(grId);

            List<Student> stList = group.gStudents(q);
            return ok(views.html.lecture.searchList.render(stList, group));
        }
        return ok();
    }

    public static Result allStudents() {
        if (isRegistrarOrCle()) {
            return ok(views.html.register.allApproved.render(null));
        } else {
            return ok("");
        }
    }

    public static Result registrarReports() {
        if (session("registrar") != null) {
            return ok(views.html.register.registrarReports.render(null));
        } else {
            return ok("");
        }
    }

    public static Result requirements() {
        if (session("admin") != null) {

            return ok(views.html.admininistrator.requirements.render());
        } else {
            return ok("error requested");
        }
    }
    public static Result deliberationSettings() {
        if (session("admin") != null) {
            return ok(views.html.admininistrator.deliberationSettings.render());
        } else {
            return ok("error requested");
        }
    }

    public static Result verify(Long id, String typ) {
        if (session("registrar") != null
                || session("DTR/Coordinator") != null) {
            Verification ver = Verification.finderByAtt(id);
            if (ver == null) {
                ver = new Verification();
            }
            Users users = new Users();
            if (session("DTR/Coordinator") != null) {
                users = Users.finderByMail(session("DTR/Coordinator"));
            } else if (session("registrar") != null) {
                users = Users.finderByMail(session("registrar"));
            }
            ver.status = true;
            ver.attachment = Attachment.finderById(id);
            ver.verifier = users;

            ver.save();

            return showAppliedForm(Applied.byApp(ver.attachment.app.id).id, "under");

        }
        return ok();
    }

    public static String htmlE(String str) {
        return str.replace("&amp;", "&").replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"");
    }
    public static Result modulePages() {
        return ok(views.html.admininistrator.modulePage.render());
    }
    public static Result trainingPermission() {
        return ok(views.html.admininistrator.trainingPermission.render());
    }
    public static Result setStatuses(Long id, String status) {
        Student student = Student.finderById(id);
        student.status = status;
        student.update();
        return ok("1");
    }

    public static Result viewByComp() {
        Form<Component> f = Form.form(Component.class).bindFromRequest();
        Long depId = Long.parseLong(f.field("depart").value());
        Long lecturerId = Long.parseLong(f.field("lecturer").value());
        Lecture lecturer = Lecture.finderById(lecturerId);
        Component dep = Component.finderById(depId);
        if (lecturer != null) {
            Users u = lecturer.user;
            if (dep == null) {
                return ok(errorPage.render("not found ", null, !isAjax()));
            }
            return ok(views.html.lecture.lectStudents.render(dep, "", lecturer));
        }
        return ok("error");
    }
    public static Result academicPages(String definer, String q) {
        if (session("admin") != null || session("Finance") != null || session("registrar") != null || session("VRAC") != null) {
            switch (definer) {
                case "year":
                    return (!q.equals(Vld.key)) ? ok(views.html.admininistrator.setAcademicYear.render(q)) : ok(AcademicYear.count());
                case "campus":
                    return (!q.equals(Vld.key)) ? ok(views.html.admininistrator.campuses.render(q)) : ok(Campus.count());
                case "program":
                    return (!q.equals(Vld.key)) ? ok(views.html.admininistrator.program.render(Program.allBy(q))) : ok(Program.count());
                case "session":
                    return (!q.equals(Vld.key)) ? ok(views.html.admininistrator.sessionPage.render(Session.allBy(q))) : ok(Session.count());
                case "bank_acc":
                    return (!q.equals(Vld.key)) ? ok(views.html.admininistrator.bankAccount.render(BankAccount.all())) : ok(Intake.count());
                case "ev_category":
                    return (!q.equals(Vld.key)) ? ok(views.html.admininistrator.evCategory.render(EvCategory.finder.all())) : ok(Room.count());

                case "ev_question":
                    return (!q.equals(Vld.key)) ? ok(views.html.admininistrator.evQuestion.render(EvQuestion.finder.all(), EvCategory.finder.all())) : ok(Room.count());

                case "intake":
                    return (!q.equals(Vld.key)) ? ok(views.html.admininistrator.intake.render(q)) : ok(Intake.count());
                case "module":
                    return (!q.equals(Vld.key)) ? ok(views.html.admininistrator.module.render(Module.allBy(q), Program.all())) : ok(Module.count());
                case "comp":
                    return (!q.equals(Vld.key)) ? ok(views.html.lecture.component.render(Component.allBy(q), Module.hasComp())) : ok(Program.count());
                case "train":
                    String userType = "";
                    if(session().containsKey("VRAC")){
                        userType = "VRAC";
                    }
                    return (!q.equals(Vld.key)) ? ok(views.html.admininistrator.training.render(userType, Training.allBy(q), IntakeSessionMode.all())) : ok(Program.count());
                case "permission":
                    return (!q.equals(Vld.key)) ? ok(views.html.admininistrator.permissions.render( Training.all(), IntakeSessionMode.all())) : ok(Program.count());
                case "employee": {
                    return (!q.equals(Vld.key)) ? ok(views.html.admininistrator.addEmployee.render(Employee.allBy(q))) : ok(Employee.count());
                }
                case "Instructor":
                    return (!q.equals(Vld.key)) ? ok(views.html.admininistrator.lecture.render(q)) : ok(Lecture.count());
                case "sFees":
                    return (!q.equals(Vld.key)) ? ok(views.html.admininistrator.schoolfees.render(SchoolFees.allBy(q), Program.all())) : ok(SchoolFees.count());
                case "dateRange":
                    List<DateRange> dateRanges = DateRange.all();
                    return ok(views.html.admininistrator.dateRange.render(dateRanges));
                case "users":
                    return (!q.equals(Vld.key)) ? ok(views.html.admininistrator.addUser.render(Users.allByQ(q))) : ok(Users.count());
                case "users-email":
                    return (!q.equals(Vld.key)) ? ok(views.html.admininistrator.addUserEmail.render(Student.allByQ(q))) : ok(Student.counts());
                case "files":
                    return (!q.equals(Vld.key)) ? ok(views.html.admininistrator.files.render(q, Program.all())) : ok(AcademicFiles.count());
                case "Room":
                    return (!q.equals(Vld.key)) ? ok(views.html.admininistrator.room.render(Campus.all())) : ok(Room.count());
                case "mini":
                    return (!q.equals(Vld.key)) ? ok(updateAge.render()) : ok(AcademicFiles.count());
                case "ex-board":
                    return (!q.equals(Vld.key)) ? ok(views.html.register.examinationBoard.render()) : ok(AcademicFiles.count());
                case "CGPA_range":
                    return (!q.equals(Vld.key)) ? ok(views.html.register.CGPA.render()) : ok(AcademicFiles.count());
                case "CGPA_range_settings":
                    return (!q.equals(Vld.key)) ? ok(views.html.register.CGPA_range_settings.render()) : ok(AcademicFiles.count());
                case "CGPA":
                    return (!q.equals(Vld.key)) ? ok(views.html.admininistrator.CGPACumulative.render()) : ok(AcademicFiles.count());
                case "aca-sena":
                    return (!q.equals(Vld.key)) ? ok(views.html.register.academivSenate.render()) : ok(AcademicFiles.count());
                case "sMode":
                    return (!q.equals(Vld.key)) ? ok(views.html.admininistrator.studyMode.render(StudyMode.allBy(q))) : ok(Program.count());
                case "day":
                    return (!q.equals(Vld.key)) ? ok(days.render(Days.all())) : ok(Program.count());
                case "hours":
                    return (!q.equals(Vld.key)) ? ok(views.html.admininistrator.hours.render(Hours.all())) : ok(Program.count());
                case "assign_pro":
                    return (!q.equals(Vld.key)) ? ok(views.html.admininistrator.assignProgram.render(CampusProgram.all(), Campus.all(), Program.all())) : ok(Program.count());
                case "assign_mode":
                    return (!q.equals(Vld.key)) ? ok(views.html.admininistrator.assignMode.render(CampusProgramMode.all(), CampusProgram.all(), StudyMode.all())) : ok(Program.count());
                case "assign_session":
                    return (!q.equals(Vld.key)) ? ok(views.html.admininistrator.assignSession.render(SessionMode.all(), Session.all(), StudyMode.all())) : ok(Program.count());
                case "assign_intake":
                    return (!q.equals(Vld.key)) ? ok(views.html.admininistrator.assignIntakeSession.render(IntakeSessionMode.all(), Intake.all(), SessionMode.all(), CampusProgram.all())) : ok(Program.count());
                case "assign_day":
                    return (!q.equals(Vld.key)) ? ok(views.html.admininistrator.assignDays.render(DaySession.all(), Session.all(), Days.all())) : ok(Program.count());
                case "assign_hours":
                    return (!q.equals(Vld.key)) ? ok(views.html.admininistrator.assignHours.render(HourSession.all(), Session.all(), Hours.all())) : ok(Program.count());
            }
        }

        if (definer.equals("chat") && chatUser() != null) {
            return ok(chat.render(chatUser().newMess(), chatUser()));
        } else if (definer.equals("Schedule") && session("registrar") != null) {
            return (!q.equals(Vld.key)) ? ok(views.html.admininistrator.schedule.render(Schedule.find(1), Lecture.all(), Room.all(), Training.allOpenRegistrar(), Component.all())) : ok(Schedule.count());
        }
        return ok("error requested");
    }
    public static Result moreSettingsPage() {
        return ok(views.html.admininistrator.moreSettings.render());
    }
    public static Result showSchedules(Integer page, String p) {
        if (isRegistrar() || isCleManager() || session("Coordinator") != null || session("mark_officer") != null || session("quality_insurance") != null || session("VRAC") != null) {
            boolean manager = isCleManager();
            Page<Schedule> schedules = Schedule.find(page, manager);
            List<Lecture> lectures = Lecture.all();
            List<Room> rooms = Room.all();
            List<Training> trainings = null;
            if (isRegistrar() || session("Coordinator") != null || session("mark_officer") != null){
                trainings = Training.allOpenRegistrar();
             }
            if (isCleManager() || session("DTR/Coordinator") != null){
                trainings = Training.allOpenCle();
             }
            List<Component> components = Component.all(manager);
            if (p.equalsIgnoreCase("partial")) {
                return ok(views.html.admininistrator._Schedules.render(schedules));
            }
            return ok(views.html.admininistrator.schedule.render(schedules, lectures, rooms, trainings, components));
        }
        return unauthorized();
    }
    public static Result showSchedulesQuality(Integer page, String p) {
        if (session("quality_insurance") != null || session("VRAC") != null) {
            boolean manager = isCleManager();
            Page<Schedule> schedules = Schedule.find(page, manager);
            List<Lecture> lectures = Lecture.all();
            List<Room> rooms = Room.all();
            List<Training> trainings = null;
            if (session("quality_insurance") != null || session("VRAC") != null){
                trainings = Training.allOpenRegistrar();
             }
            List<Component> components = Component.all(manager);
            if (p.equalsIgnoreCase("partial")) {
                return ok(views.html.admininistrator._SchedulesQuality.render(schedules));
            }
            return ok(views.html.admininistrator.showSchedulesQuality.render(schedules, lectures, rooms, trainings, components));
        }
        return unauthorized();
    }

    public static Result getStudyMode(Long progId) {
        if (session("admin") != null) {
            Program p = Program.finderById(progId);
            if (p == null) {
                return ok(errorPage.render("requested url is not found", null, false));
            }
            List<CampusProgramMode> pros = p.myModes();
            return ok(Json.toJson(pros));
        }
        return ok("0");
    }

    public static Result getSession(Long modeId) {
        if (session("admin") != null) {
            CampusProgramMode campusProgramMode = CampusProgramMode.finderById(modeId);
            if (campusProgramMode == null) {
                return ok(errorPage.render("requested url is not found", null, false));
            }
            List<SessionMode> sessionModes = SessionMode.find.where().eq("mode.id", campusProgramMode.mode.id).eq("delete_status", false).findList();
            return ok(Json.toJson(sessionModes));
        }
        return ok("0");
    }

    public static Result printTimetable(Long id) {
        if (session("registrar") != null) {
            Training t = Training.finderById(id);
            if (t == null) {
                return ok(errorPage.render("Requested period is not found !.", null, !isAjax()));
            }

            List<DateRange> tHours = DateRange.finder.where().eq("hourSession.session.id", t.iMode.sessionMode.session.id).findList();
            List<DaySession> days = t.trainDays();

            return TODO;
        }
        return ok("0");
    }

    public static Result getTimetable(Long traingingId, Long dateId) {
        if (session("registrar") != null || session("VRAC") != null || session("DTR/Coordinator") != null || session("Coordinator") != null || session("mark_officer") != null || session("quality_insurance") != null) {
            Training t = Training.finderById(traingingId);
            DateRange dateRange = DateRange.finderById(dateId);

            if (t == null || dateRange == null) {
                return ok(errorPage.render("Requested period is not found !.", null, !isAjax()));
            }

            List<Schedule> schedules = Schedule.find.where()
                    .eq("dateRange.id", dateId)
                    .eq("training.id", traingingId)
                    .eq("deleteStatus", false)
                    .orderBy("date desc")
                    .findList();
            List<DaySession> days = t.trainDays();
            return ok(newTimeTable.render(schedules, t, dateRange));
        }
        return ok("0");
    }
    public static Result roomPossibleComponent(Long id) {
        if (session("registrar") != null) {
            Room r = Room.finderById(id);
            if (r == null) {
                return ok("0");
            }
            List<Component> comps = r.possibleComp();
            for (Component tin : comps) {
                tin.compName = tin.print();
            }
            return ok(Json.toJson(comps));
        }
        return ok("0");
    }

    public static Result TrainPossibleHours(Long id) {
        if (session("registrar") != null || session("DTR/Coordinator") != null) {
            Training train = Training.finderById(id);
        }
        return ok("0");
    }

    public static Result TrainPossibleDays(Long id) {
        if (session("registrar") != null || session("DTR/Coordinator") != null) {
            Training train = Training.finderById(id);
            if (train == null) {
                return ok("0");
            }
            List<DaySession> comps = train.trainDays();
            return ok(Json.toJson(comps));
        }
        return ok("0");
    }

    public static Result CompPossibleTraining(Long id) {
        if (session("registrar") != null) {
            Component comp = Component.finderById(id);
            if (comp == null) {
                return ok("0");
            }
            List<Training> comps = comp.compTrain();
            for (Training ting : comps) {
                ting.title = ting.tNames();
            }
            return ok(Json.toJson(comps));
        }
        return ok("0");
    }

    public static Result updateAge() {
        if (session("admin") != null) {
            Form<ProfileInfo> pf = Form.form(ProfileInfo.class).bindFromRequest();
            ProfileInfo pfg = pf.get();
            ProfileInfo pg = ProfileInfo.unique();
            pg.minAge = pfg.minAge;
            pg.defaultText = pfg.defaultText;
            pg.cancelText = pfg.cancelText;
            pg.registrarName = pfg.registrarName;
            pg.deferText = pfg.deferText;
            pg.attendancePercentage = pfg.attendancePercentage;
            pg.experience = pfg.experience;
            if(pfg.attendancePercentage > 100) return ok("The maximum percentage is 100!!");
            pg.update();
        }
        return ok("1");
    }

    public static Result updateAcSenate() {
        if (session("admin") != null) {
            Form<ProfileInfo> pf = Form.form(ProfileInfo.class).bindFromRequest();
            ProfileInfo pfg = pf.get();
            ProfileInfo pg = ProfileInfo.unique();
            pg.failMax = pfg.failMax;
            pg.passMin = pfg.passMin;
            pg.creditMin = pfg.creditMin;
            pg.distinctionMin = pfg.distinctionMin;
            pg.passMax = pfg.passMax;
            pg.creditMax = pfg.creditMax;
            pg.distinctionMax = pfg.distinctionMax;
            pg.update();
        }
        return ok("1");
    }

    public static Result updateExamBoard() {
        if (session("admin") != null) {
            Form<ProfileInfo> pf = Form.form(ProfileInfo.class).bindFromRequest();
            ProfileInfo pfg = pf.get();
            ProfileInfo pg = ProfileInfo.unique();
            pg.scoreOne = pfg.scoreOne;
            pg.scoreTwo = pfg.scoreTwo;
            pg.scoreThree = pfg.scoreThree;
            pg.update();
            return ok("1");
        }else{
            return ok("Not found!");
        }
    }
    public static Result updateCumulativeCGPA() {
        if (session("admin") != null) {
            Form<ProfileInfo> pf = Form.form(ProfileInfo.class).bindFromRequest();
            ProfileInfo pfg = pf.get();
            ProfileInfo pg = ProfileInfo.unique();
            pg.gradePointA = pfg.gradePointA;
            pg.gradePointBplus = pfg.gradePointBplus;
            pg.gradePointB = pfg.gradePointB;
            pg.gradePointCplus = pfg.gradePointCplus;
            pg.gradePointC = pfg.gradePointC;
            pg.gradePointD = pfg.gradePointD;
            pg.gradePointE = pfg.gradePointE;
            pg.gradePointF = pfg.gradePointF;
            pg.update();
        }
        return ok("1");
    }
    public static Result updateCumulative() {
        if (session("admin") != null) {
            Form<ProfileInfo> form = Form.form(ProfileInfo.class).bindFromRequest();
            ProfileInfo pfg = form.get();
            ProfileInfo pg = ProfileInfo.unique();
            pg.aMin = Double.parseDouble(form.field("aMin").value());
            pg.aMax = Double.parseDouble(form.field("aMax").value());
            pg.bPlusMin = Double.parseDouble(form.field("bPlusMin").value());
            pg.bPlusMax = Double.parseDouble(form.field("bPlusMax").value());
            pg.bMin = Double.parseDouble(form.field("bMin").value());
            pg.bMax = Double.parseDouble(form.field("bMax").value());
            pg.cPlusMin = Double.parseDouble(form.field("cPlusMin").value());
            pg.cPlusMax = Double.parseDouble(form.field("cPlusMax").value());
            pg.cMin = Double.parseDouble(form.field("cMin").value());
            pg.cMax = Double.parseDouble(form.field("cMax").value());
            pg.dMin = Double.parseDouble(form.field("dMin").value());
            pg.dMax = Double.parseDouble(form.field("dMax").value());
            pg.eMin = Double.parseDouble(form.field("eMin").value());
            pg.eMax = Double.parseDouble(form.field("eMax").value());
            pg.fMin = Double.parseDouble(form.field("fMin").value());
            pg.fMax = Double.parseDouble(form.field("fMax").value());
            pg.maximumResit = Double.parseDouble(form.field("maximumResit").value());
            pg.update();
        }
        return ok("1");
    }
    public static Result updateCumulativeRange() {
        if (session("admin") != null) {
            Form<ProfileInfo> pf = Form.form(ProfileInfo.class).bindFromRequest();
            ProfileInfo pfg = pf.get();
            ProfileInfo pg = ProfileInfo.unique();
            pg.distinctionMin = pfg.distinctionMin;
            pg.distinctionMax = pfg.distinctionMax;
            pg.meritMin = pfg.meritMin;
            pg.meritMax = pfg.meritMax;
            pg.satisfactoryMin = pfg.satisfactoryMin;
            pg.satisfactoryMax = pfg.satisfactoryMax;
            pg.passMin = pfg.passMin;
            pg.passMax = pfg.passMax;
            pg.failMin = pfg.failMin;
            pg.failMax = pfg.failMax;
            pg.update();
        }
        return ok("1");
    }

    public static Result getIntake(Long sModeId, Long campusProgramId) {
        SessionMode sMode = SessionMode.finderById(sModeId);
        List<IntakeSessionMode> tr = sMode.allbySession(campusProgramId);
        return ok(Json.toJson(tr));
    }

    public static Result getMode(String types, Long proId) {
        switch (types) {
            case "1":
                Campus campus = Campus.finderById(proId);
                List<CampusProgram> f = campus.myProgram();
                return ok(Json.toJson(f));
            case "2":
                CampusProgram p = CampusProgram.finderById(proId);
                List<CampusProgramMode> sp = p.myAll();
                return ok(Json.toJson(sp));
            case "3":
                CampusProgramMode cP = CampusProgramMode.finderById(proId);
                List<SessionMode> sess = cP.allSess();
                return ok(Json.toJson(sess));
            case "4": {
                SessionMode sMode = SessionMode.finderById(proId);
                List<IntakeSessionMode> tr = sMode.allbySession();
                return ok(Json.toJson(tr));
            }
            case "5": {
                List<Training> tr = Training.byIntake(proId);
                return ok(Json.toJson(tr));
            }
            case "6": {
                List<Training> ss = Training.byProgram(proId);
                return ok(Json.toJson(ss));
            }
            case "7": {
                Training training = Training.finderById(proId);
                List<Groups> groups = training.myGroups();
                return ok(Json.toJson(groups));
            }
            default:
                return ok("1");
        }
    }
    public static Result getComps(String types, Long tId) {
        switch (types) {
            case "1":
                Training training = Training.finderById(tId);
                List<Component> f = training.myComponents();
                return ok(Json.toJson(f));
            default:
                return ok("1");
        }
    }

    public static Result getApplicationForm(long id) {
        Applied applied = Applied.finderById(id);
        Users u = Users.finderById(applied.applicant.user.id);
        if (applied != null) {
            return ok(views.html.reports.applicationForm.render(applied));
        } else {
            return ok("Applicant is not found!!");
        }

    }
    public static Result dashboard() {
        if (session("admin") != null ||
                session("registrar") != null ||
                session("Instructor") != null ||
                session("Coordinator") != null ||
                session("mark_officer") != null ||
                session("Finance") != null ||
                session("Rector") != null ||
                session("VRAC") != null ||
                session("Planning") != null ||
                session("DTR/Coordinator") != null) {
            return ok(views.html.admininistrator.adminChart.render(Program.all(), isCleManager()));
        } else if (session("student") != null) {
            String email = session("student");
            Users user = Users.finderByMail(email);
            Applicant applicant = user.amApplicant();
            if (applicant == null) {
                return ok(views.html.student.dashboard.render(null, null));
            }
            if (applicant.student != null) {
                Date date = new Date();
                Date d = new Date();
                d.setDate(d.getDate()-1);
                List<Schedule> schedules = Schedule.find.where()
                        .eq("deleteStatus", false)
                        .eq("training.deleteStatus", false)
                        .eq("training.id", applicant.student.training.id)
                        .ge("date", d)
                        .orderBy("date asc")
                        .setMaxRows(5)
                        .findList();

                return ok(views.html.student.dashboard.render(schedules, applicant.student.training));
            }
            return ok(views.html.student.dashboard.render(null, null));
        } else if (session("Logistic") != null || session("HeadOfUnit") != null || session("DAF") != null || session("Accountant") != null || session("Auditor") != null || session("Manager") != null || session("Staff") != null || session("VRAC") != null) {
            return ok(dashboard.render());
        } else {
            return ok("error requested");
        }
    }

    public static Result resetFetch() {

        return ok(changeUser.render());
    }

    public static Result searchUser(String qeury) {
        if (chatUser() == null) {
            return ok("login");
        }
        return ok(qeury);
    }

    public static Result chatPeople() {
        if (chatUser() == null) {
            return ok("login");
        }
        return ok(people.render(chatUser().exceptMe(), chatUser()));
    }

    public static Result searchChat(String chatKey) {
        if (chatUser() == null) {
            return ok("login");
        }
        return ok(people.render(chatUser().exceptMe(chatKey), chatUser()));
    }

    public static Result singleChat(Long id) {
        if (chatUser() == null) {
            return ok("0");
        }

        Users u = Users.finderById(id);
        if (u == null) {
            return ok("0");
        }

        return ok(person.render(u, chatUser().ourChat(u), chatUser().SpecificUnread(u)));
    }

    public static Result allTranscriptForTraining(Long trainId) {
        Training t = Training.finderById(trainId);

        return ok(views.html.register.periodsTranscript.render(t.students()));
    }

    public static Result allDegreeForTraining(Long trainId) {
        Training t = Training.finderById(trainId);

        return ok(views.html.register.periodsDegree.render(t.students()));
    }

    public static Result allTranscript() {
        return ok(views.html.register.allTranscript.render(Training.cleAbout(isCleManager())));
    }

    public static Result allDegrees() {
        return ok(views.html.register.allDegree.render(Training.cleAbout(isCleManager())));
    }

    private static Users chatUser() {
        //if( session("user") != null )
        return Users.finderByMail(session(defaultSession));

    }

    public static Result listComp(Long id) {
        Lecture lecture = Lecture.finderById(id);

        if (lecture == null) return ok();

        List<Component> components = lecture.myComp(isCleManager());

        return ok(Json.toJson(components));
    }
    public static Result listComponents(Long id) {
        Module module = Module.finderById(id);

        if (module == null) return ok();
        List<Component> components = module.myComp();
        return ok(Json.toJson(components));
    }
    public static Result sendMessage(Long id) {
        if (chatUser() == null) {
            return ok("0");
        }
        Users receiver = Users.finderById(id);

        if (receiver == null) {
            return ok("0");
        }

        Form<ChatMessage> chat = Form.form(ChatMessage.class).bindFromRequest();

        ChatMessage c = chat.get();

        ChatMessage newChat = new ChatMessage();
        newChat.content = c.content;
        newChat.type = "text";
        newChat.sendFrom = chatUser();
        newChat.sendTo = receiver;
        newChat.date = new Date();

        newChat.save();

        Boolean b = chatUser().markRead(receiver);

        return ok("1");
    }

    public static Result setMarks(Long id) {
        List<Training> trainingList = Training.allOpenRegistrar();
        List<Component> components = Component.all();
        ComponentMax componentMax = ComponentMax.finder.byId(id);
        return ok(views.html.lecture.updateSetMarks.render(componentMax, trainingList, components));
    }
    public static Result studentAttendanceClaime() {
        List<Attendance> attendances = Attendance.find.where()
                .not(Expr.eq("comment", ""))
                .eq("student.deleteStatus", false)
                .eq("deleteStatus", false)
                .eq("claimed", false)
                .findList();
        return ok(views.html.register.studentAttendanceClaime.render(attendances));
    }
    public static Result updateAll(Long id, String def) {
        if (session("Planning") != null || session("VRAC") != null || session("admin") != null || session("Library") != null || isRegistrarOrCle() || session("Logistic") != null || session("Finance") != null || session("Coordinator") != null || session("mark_officer") != null || session("Instructor") != null || session("HeadOfUnit") != null || session("DAF") != null || session("Manager") != null || session("Auditor") != null || session("Accountant") != null || session("Staff") != null || session("student") != null || session("DTR/Coordinator") != null || session("registrar") != null || session("student") != null) {

            switch (def) {
                case "startDeliberation": {
                    Training training = Training.finderById(id);
                    return ok(views.html.register.startDeliberation.render(training));
                }
                case "startDeliberationRESit": {
                    Training training = Training.finderById(id);
                    return ok(views.html.register.startDeliberationRESit.render(training));
                }
                case "startDeliberationFinal": {
                    Training training = Training.finderById(id);
                    return ok(views.html.register.startDeliberationFinal.render(training));
                }
                case "marksEntered": {
                    Training training = Training.finderById(id);
                    return ok(views.html.register.marksEntered.render(training));
                }
                case "claimAttendance":
                    Attendance attendance = Attendance.find.byId(id);
                    return ok(views.html.student.claimAttendance.render(attendance));
                case "approveAttendanceClaim":
                    Attendance attendance1 = Attendance.find.byId(id);
                    return ok(views.html.register.approveAttendanceClaim.render(attendance1));
                case "damageCl":
                    Damage damage = Damage.finderById(id);
                    return ok(views.html.admininistrator.clearDamage.render(damage));
                case "deleteMaterial":
                    CourseMaterial courseMaterial = CourseMaterial.finder.byId(id);
                    return ok(views.html.admininistrator.deleteMaterial.render(courseMaterial));
                case "returnBack":
                    Submission submission = Submission.finderById(id);
                    return ok(views.html.admininistrator.returnBack.render(submission));
                case "year":
                    AcademicYear yr = AcademicYear.finderById(id);
                    return ok(views.html.admininistrator.updateYear.render(yr));
                case "update-appProfile":
                    Applicant app = Applicant.finderById(id);
                    return ok(views.html.admininistrator.appProfile.render(app));
                case "courseMaterial-app":
                    Applied applied = Applied.finderById(id);
                    return ok(views.html.admininistrator.harmonize.render(applied));
                case "delete-app":
                    Applied applicant = Applied.finderById(id);
                    return ok(views.html.admininistrator.deleteApplicantUncompleted.render(applicant));
                case "courseMaterial-app-rec":
                    Applied applied1 = Applied.finderById(id);
                    return ok(views.html.admininistrator.harmonizeSucc.render(applied1));
                case "claim-marks":
                    SubMark sb = SubMark.find.byId(id);
                    return ok(views.html.lecture.marksClaim.render(sb));
                case "claim-marks-approval":
                    SubMark sb1 = SubMark.find.byId(id);
                    return ok(views.html.lecture.marksClaimAproval.render(sb1));
                case "claim-marks-denial":
                    SubMark sb2s = SubMark.find.byId(id);
                    return ok(views.html.lecture.marksClaimReject.render(sb2s));
                case "remove-depr":
                    models.stock.Item item = Item.finder.byId(id);
                    return ok(views.html.stock.removeDepr.render(item));
                case "remove-defe":
                    models.stock.Item itemd = Item.finder.byId(id);
                    return ok(views.html.stock.removeDefec.render(itemd));
                case "remove-depr-approve":
                    models.stock.Item items = Item.finder.byId(id);
                    return ok(views.html.stock.removeDeprApprove.render(items));
                case "remove-def-approve":
                    models.stock.Item itemsd = Item.finder.byId(id);
                    return ok(views.html.stock.removeDefApprove.render(itemsd));
                case "claim-marks-ass":
                    AssignmentResult aReslt = AssignmentResult.find.byId(id);
                    return ok(views.html.lecture.marksClaimAss.render(aReslt));
                case "claim-marks-ass-appr":
                    AssignmentResult aResltAppro = AssignmentResult.find.byId(id);
                    return ok(views.html.lecture.marksClaimAssApprove.render(aResltAppro));
                case "evCategory":
                    EvCategory category = EvCategory.finder.byId(id);
                    return ok(views.html.admininistrator.updateEvCategory.render(category));
                case "evQuestion":
                    EvQuestion question = EvQuestion.finder.byId(id);
                    return ok(views.html.admininistrator.updateEvQuestion.render(question));
                case "bank":
                    BankAccount account = BankAccount.find.ref(id);
                    return ok(views.html.admininistrator.updateBank.render(account));

                case "viewImage": {
                    Announce announce = Announce.finderById(id);
                    return ok(views.html.admininistrator.viewImage.render(announce));
                }
                case "updateMaterial":
                    CourseMaterial courseMaterial2 = CourseMaterial.finder.byId(id);
                    List<Schedule> scheduleList = Schedule.all();
                    return ok(views.html.admininistrator.updateMaterial.render(courseMaterial2, scheduleList));
                case "downloadCourseMaterial":
                    CourseMaterial courseMaterials = CourseMaterial.finder.byId(id);
                    if (courseMaterials == null) {
                        return notFound();
                    }
                    try {
                        return ok(new File("public/uploads/" + courseMaterials.fileName));
                    } catch (Exception e) {
                        return ok("File not found");
                    }
                case "downloadInternshipClaimProof":
                    InternshipAppeal appeal = InternshipAppeal.byId(id);
                    if (appeal == null) {
                        return notFound();
                    }
                    try {
                        return ok(new File("public/uploads/" + appeal.attachment));
                    } catch (Exception e) {
                        return ok("File not found");
                    }

                case "downloadInternship":
                    Internship internship = Internship.finder.byId(id);
                    if (internship == null) {
                        return notFound();
                    }
                    return ok(new File("public/uploads/" + internship.attachment));

                case "downloadAttachment":
                    Attachment attachment = Attachment.finderById(id);
                    if (attachment == null) {
                        return notFound();
                    }
                    return ok(new File("public/uploads/" + attachment.attachName));
                case "studentCourseMaterial":
                    List<CourseMaterial> courseMaterialsX;
                    Users user = Users.finderByMail(session("student"));
                    Applicant app1 = user.amApplicant();
                    Training trainingx = app1.student.training;
                    if (id == 0) {
                        courseMaterialsX = CourseMaterial.finder.where()
                                .orderBy("date desc")
                                .findList();
                    } else {
                        courseMaterialsX = CourseMaterial.finder.where()
                                .eq("schedule.training.id", trainingx.id)
                                .orderBy("date desc")
                                .findList();
                    }
                    List<Schedule> scheduleListX = Schedule.all();
                    Boolean isStudent = false;
                    if(session("student") != null){
                        isStudent = true;
                    }
                    return ok(views.html.student.courseMaterial.render(Schedule.all(), isStudent, courseMaterialsX, scheduleListX, app1));
                case "graduationMenu":
                    return ok(views.html.register.periods.render(Training.cleAbout(isCleManager()), IntakeSessionMode.all()));
                case "degreeTranscript":
                    return ok(views.html.register.degreeTranscript.render(Training.cleAbout(isCleManager()), IntakeSessionMode.all()));
                case "graduationMenuReceipt":
                    return ok(views.html.register.graduationMenuReceipt.render(Training.cleAbout(isCleManager()), IntakeSessionMode.all()));
                case "graduationMenuFinale":
                    return ok(views.html.register.graduationMenuFinale.render(Training.cleAbout(isCleManager()), IntakeSessionMode.all()));
                case "graduation": {
                    Training training = Training.finderById(id);
                    if (training == null) {
                        return notFound();
                    }
                    return ok(views.html.register.graduation.render(training));
                }
                case "undoDeliberation": {
                    Training training = Training.finderById(id);
                    if (training == null) {
                        return notFound();
                    }
                    return ok(views.html.register.undoDeliberation.render(training));
                }
                case "undoDeliberationReceipt": {
                    Training training = Training.finderById(id);
                    if (training == null) {
                        return notFound();
                    }
                    return ok(views.html.register.undoDeliberationReceipt.render(training));
                }
                case "undoDeliberationFinal": {
                    Training training = Training.finderById(id);
                    if (training == null) {
                        return notFound();
                    }
                    return ok(views.html.register.undoDeliberationFinal.render(training));
                }
                case "updateStockHistory": {
                    StockMovement stockMovement = StockMovement.find(id);
                    if (stockMovement == null) {
                        return notFound();
                    }
                    return ok(views.html.stock.stockMovements.edit.render(stockMovement, Employee.finder.all(), Location.finder.all(), Item.finder.all()));
                }
                case "updateUser":
                    Users user2 = Users.finderById(id);
                    if (user2 == null) {
                        return notFound();
                    }
                    return ok(views.html.admininistrator.editStudent.render(user2));
                case "deleteUser":
                    Users user1 = Users.finderById(id);
                    if (user1 == null) {
                        return notFound();
                    }
                    return ok(views.html.admininistrator.deleteStudent.render(user1));
                case "giveAcademicEmail":
                    Student student = Student.finderById(id);
                    if (student == null) {
                        return notFound();
                    }
                    return ok(views.html.admininistrator.giveAcademicEmail.render(student));
                case "removeGroup":
                    Groups group = Groups.finderById(id);
                    if (group == null) {
                        return notFound();
                    }
                    group.delete();
                    return ok("Group successfully removed");
                case "refund": {
                    RefundRequest request = RefundRequest.finderById(id);
                    if (request == null) {
                        return notFound();
                    }
                    return ok(views.html.finance.refundForm.render(request.account.applicant, request));
                }
                case "cancel": {
                    Refund refund = Refund.finder.byId(id);
                    if (refund == null) {
                        return notFound();
                    }
                    return ok(views.html.finance.deleteRefun.render(refund));
                }
                case "cPMode":
                    CampusProgramMode campusProgramMode = CampusProgramMode.finderById(id);
                    return ok(views.html.admininistrator.updateStudyMode.render(campusProgramMode, CampusProgram.all(), StudyMode.all()));
                case "sMode":
                    SessionMode sessionMode = SessionMode.finderById(id);
                    return ok(views.html.admininistrator.updateSessionMode.render(sessionMode, Session.all(), StudyMode.all()));
                case "getRequest": {
                    Request request = Request.find(id);
                    return ok(details.render(request, Location.all())).as("text/html");
                }
                case "iMode": {
                    IntakeSessionMode intakeSessionMode = IntakeSessionMode.finderById(id);
                    return ok(views.html.admininistrator.updateIntakeSessionMode.render(intakeSessionMode, Intake.all(), SessionMode.all(), CampusProgram.all()));
                }
                case "daySession":
                    DaySession days = DaySession.finderById(id);
                    return ok(views.html.admininistrator.updateAssignedDays.render(days, Session.all(), Days.all()));
                case "hourSession":
                    HourSession hourSession = HourSession.finderById(id);
                    return ok(views.html.admininistrator.updateAssignedHours.render(hourSession, Session.all(), Hours.all()));
                case "program":
                    Program prog = Program.finderById(id);
                    return ok(views.html.admininistrator.updateProgram.render(prog));
                case "assignment-marks":
                    AssignmentResult result = AssignmentResult.finderById(id);
                    return ok(views.html.register.updateResult.render(result));
                case "hours":
                    Hours hours = Hours.finderById(id);
                    return ok(views.html.admininistrator.updateHours.render(hours));
                case "camp":
                    Campus comp = Campus.finderById(id);
                    return ok(views.html.admininistrator.updateCampus.render(comp));
                case "payment":
                    Payment payment = Payment.finderById(id);
                    return ok(views.html.student.uploadPayment.render(payment));
                case "session":
                    Session sess = Session.finderById(id);
                    return ok(views.html.admininistrator.updateSession.render(sess));
                case "intake": {
                    Intake spec = Intake.finderById(id);
                    return ok(views.html.admininistrator.updateIntake.render(spec));
                }
                case "clear":
                    ILDPLibrary library = ILDPLibrary.finderById(id);
                    return ok(views.html.library.clearStudent.render(library));
                case "module": {
                    Module spec = Module.finderById(id);
                    return ok(views.html.admininistrator.updateModule.render(spec, Program.all()));
                }
                case "internshipAppeal": {
                    Student student1 = Student.finderById(id);
                    Users userX = Users.finderByMail(session("student"));
                    return ok(views.html.student.internshipAppeal.render(userX, student1));
                }
                case "internshipAppealExemption": {
                    Student student1 = Student.finderById(id);
                    Users user3 = Users.finderByMail(session("student"));
                    return ok(views.html.student.internshipAppealExemption.render(user3, student1));
                }
                case "block": {
                    Block obj = Block.find(id);
                    return ok(edit.render(obj));
                }
                case "location": {
                    Location obj = Location.find(id);
                    return ok(views.html.stock.location.edit.render(obj));
                }
                case "Instructor": {
                    Lecture spec = Lecture.finderById(id);
                    return ok(views.html.admininistrator.updateLecture.render(spec));
                }
                case "fees": {
                    SchoolFees spe = SchoolFees.finderById(id);
                    return ok(views.html.admininistrator.updateFees.render(spe));
                }
                case "employee":
                    Employee employee = Employee.find(id);
                    return ok(views.html.admininistrator.updateUsers.render(employee));
                case "files": {
                    AcademicFiles spe = AcademicFiles.finderById(id);
                    return ok(views.html.admininistrator.updateFiles.render(spe, Program.all()));
                }
                case "comp": {
                    Component spe = Component.finderById(id);
                    return ok(views.html.admininistrator.updateComponent.render(spe));
                }
                case "mode": {
                    StudyMode spe = StudyMode.finderById(id);
                    return ok(views.html.admininistrator.updateStudy.render(spe));
                }
                case "lecture": {
                    Lecture spe = Lecture.finderById(id);
                    return ok(views.html.admininistrator.updateLecture.render(spe));
                }
                case "room": {
                    Room spe = Room.finderById(id);
                    return ok(views.html.admininistrator.updateRoom.render(spe));
                }
                case "appealDecision": {
                    InternshipAppeal spe = InternshipAppeal.byId(id);
                    return ok(views.html.register.appealDecision.render(spe));
                }
                case "resitAppealDecision": {
                    ReSitReTakeRequest request = ReSitReTakeRequest.finderById(id);
                    Student student1 = request.student;
                    List<Module> modules = student1.myOnlyModulesDeliberation();
                    double averageOneMarks = 0.0;
                    int number = 0;
                    for(Module module : modules) {
                        averageOneMarks = module.allModuleAverage(student1.id);
                        if(averageOneMarks < ProfileInfo.unique().scoreThree) {
                            number = number + 1;
                        }
                    }
                    return ok(views.html.register.resitAppealDecision.render(request, number, modules));
                }
                case "retakeModulesAppealDecision": {
                    ReSitReTakeRequest request = ReSitReTakeRequest.finderById(id);
                    Student student1 = request.student;
                    List<Module> modules = student1.myOnlyModulesDeliberation();
                    double averageOneMarks = 0.0;
                    int number = 0;
                    for(Module module : modules) {
                        averageOneMarks = module.allModuleAverage(student1.id);
                        if(averageOneMarks < ProfileInfo.unique().scoreThree) {
                            number = number + 1;
                        }
                    }
                    return ok(views.html.register.retakeModulesAppealDecision.render(request, number, modules));
                }
                case "retakeProgramAppealDecision": {
                    ReSitReTakeRequest request = ReSitReTakeRequest.finderById(id);
                    Student student1 = request.student;
                    List<Module> modules = student1.myOnlyModulesDeliberation();
                    double averageOneMarks = 0.0;
                    int number = 0;
                    for(Module module : modules) {
                        averageOneMarks = module.allModuleAverage(student1.id);
                        if(averageOneMarks < ProfileInfo.unique().scoreThree) {
                            number = number + 1;
                        }
                    }
                    return ok(views.html.register.retakeProgramAppealDecision.render(request, number, modules));
                }
                case "train": {
                    Training spe = Training.finderById(id);
                    List<IntakeSessionMode> intakeSessionMode = IntakeSessionMode.all();
                    return ok(views.html.admininistrator.updateTrain.render(spe, intakeSessionMode));
                }
                case "ann":
                    Announce ann = Announce.finderById(id);
                    return ok(views.html.register.updateAnnouncement.render(ann));
                case "libraryEntry":
                    ILDPLibrary entry = ILDPLibrary.finderById(id);
                    return ok(updateLibrary.render(entry));
                case "cPro":
                    CampusProgram campusProgram = CampusProgram.finderById(id);
                    return ok(views.html.admininistrator.updateCampusProgram.render(campusProgram, Campus.all(), Program.all()));
                case "dateRange":
                    DateRange range = DateRange.finderById(id);
                    return ok(views.html.admininistrator.updateDateRange.render(range));
                case "hashPassword":
                    Users usear = Users.finderById(id);
                    return ok(views.html.admininistrator.hashUserPassword.render(usear));
                case "schedule":
                    Schedule schedule = Schedule.finderById(id);
                    return ok(views.html.admininistrator.updateSchedule.render(schedule, Lecture.all(), Room.all(), Component.all(), DateRange.all(), Training.allOpen()));
                case "schedule2":
                    Schedule schedule2 = Schedule.finderById(id);
                    return ok(views.html.admininistrator.updateSchedule2.render(schedule2, Lecture.all(), Room.all(), Component.all(), DateRange.all(), Training.allOpen()));
                case "editSupp":
                    Supplied supplied = Supplied.find(id);
                    if (supplied == null) {
                        return notFound();
                    }
                    return ok(views.html.stock.stockMovements.editStockIn.render(supplied, Item.all(), Supplier.all()));
                case "ass":
                    Assignment assignment = Assignment.finderById(id);
                    if (assignment == null) {
                        return notFound();
                    }
                    return ok(views.html.lecture.updateAssign.render(assignment));
                default:
                    return ok("error");
            }
        } else

        {
            return ok("error requested");
        }

    }

    public static Result district(Long var) {
        List<Districts> finded = Districts.allByPro(var);
        return ok(Json.toJson(finded));
    }

    public static Result sectors(Long var) {
        List<Sector> finded = Sector.allByDis(var);
        return ok(Json.toJson(finded));
    }

    public static Result showApplied(String type, String var) {
        if (session("registrar") != null || session("DTR/Coordinator") != null) {
                List<Applied> finded = Applied.allByName(var);
                return ok(Json.toJson(finded));
        } else {
            return ok("error");
        }
    }

    public static Result showByProgram(Long intake) {
        if (session("registrar") != null || session("assistant") != null) {

            List<Applied> finded = Applied.allByProg(intake);
            return ok(Json.toJson(finded));

        } else {
            return ok("error");
        }
    }

    public static Result showByIntake(Long intake) {
        if (session("registrar") != null || session("assistant") != null) {

            List<Applied> finded = Applied.allByIntake(intake);
            return ok(Json.toJson(finded));
        } else {
            return ok("error");
        }
    }

    public static Result showByStatus(String type, String var, String status) {
        if (session("registrar") != null || session("DTR/Coordinator") != null) {
                List<Applied> finded = Applied.allByNameAndStatus(var, status);
                return ok(Json.toJson(finded));
        }
        return ok("error");
    }

    public static Result myAddmission() {
        if (session("student") != null) {
            Applicant applicant = applicant();
            Applied app = Applied.byApp(applicant.id);
            if (app == null) {
                return ok(notStudent.render());
            }
            return admissionLetter(app.id);
        }
        return ok("0");
    }

    public static Result myComponentsViews() {
        if (session("student") != null) {
            Applicant applicant = applicant();
            Applied app = Applied.byApp(applicant.id);
            if (app == null) {
                return ok(notStudent.render());
            }
            List<Module> modules = null;
            Student student = null;
            student = Student.byUserId(applicant.user.id);
            if(student.status.equalsIgnoreCase("RETAKE-MODULES")){
                modules = student.myPreviousModules();
            }else {
                modules = student.myModules();
            }
            return ok(views.html.student.myComponentsViews.render(Student.byUserId(applicant.user.id), modules));
        }
        return ok("0");
    }
    public static Result myAddmissionRegistrar(Long id) {
        if (session("registrar") != null) {
            Applicant applicant = Applicant.finderById(id);
            Applied app = Applied.byApp(applicant.id);
            if (app == null) {
                return ok(notStudent.render());
            }
            return admissionLetter(app.id);
        }
        return ok("0");
    }
//    public static Result myAddmissionRegistrarSelect(Long id) {
//        Form<Applicant> form = Form.form(Applicant.class).bindFromRequest();
//        Applicant applicants = form.get();
//        if (session("registrar") != null) {
//            Applicant applicant = Applicant.finderById(id);
//            Applied app = Applied.byApp(applicant.id);
//            if (app == null) {
//                return ok(notStudent.render());
//            }
//            String letterType =  form.field("admissionType").value();
//            String title = "";
//            if(letterType.equalsIgnoreCase("Final")){
//                title = "Final admission Letter to the ";
//            }
//            if(letterType.equalsIgnoreCase("Provisional")){
//                title = "Provisional admission Letter to the ";
//            }
//            return ok(views.html.student.admission.render(app, title));
//        }
//        return ok("0");
//    }
    public static Result myAddmissionRegistrarSelect(Long id) {
        Form<Applicant> form = Form.form(Applicant.class).bindFromRequest();
        Applicant applicants = form.get();
        if (session("registrar") != null) {
            Applicant applicant = Applicant.finderById(id);
            Applied app = Applied.byApp(applicant.id);
            if (app == null) {
                return ok(notStudent.render());
            }
            String letterType =  form.field("admissionType").value();
            String title = "";
            if(letterType.equalsIgnoreCase("Final")){
                title = "Final admission Letter to the ";
                return ok(views.html.student.admissionFinal.render(app, title));
            }
            if(letterType.equalsIgnoreCase("Provisional")){
                title = "Provisional admission Letter to the ";
            }
            return ok(views.html.student.admission.render(app, title));
        }
        return ok("0");
    }

    public static Result admissionLetter(Long id) {
        Applied ap = Applied.finderById(id);
        if (ap == null) {
            return ok("0");
        }
        return ok(views.html.student.admission.render(ap, ""));
    }

    public static Result showAppliedForm(Long var, String type) {
        if (session("registrar") != null
                || session("assistant") != null
                || session("DTR/Coordinator") != null) {

            Users user = new Users();
            if (session("registrar") != null) {
                user = Users.finderByMail(session("registrar"));
            } else if (session("assistant") != null) {
                user = Users.finderByMail(session("assistant"));
            } else if (session("DTR/Coordinator") != null) {
                user = Users.finderByMail(session("DTR/Coordinator"));
            }

            Applied applied = Applied.finderById(var);
            if (applied != null && type.equals("under")) {
                return ok(views.html.register.receivedApp.render(applied, user, applied.applicant, type));
            } else {
                return ok("error");
            }
        } else {
            return ok("error");
        }
    }

    public static Result downloadFinanceAttachment(Long payMant) {
        Payment pay = Payment.finderById(payMant);
        if (pay == null) {
            return ok(errorPage.render("Attachment not yet available", null, !isAjax()));
        }
        File file = new File("public/uploads/" + pay.attachment);
        if (file.exists()) {
            return ok(file);
        }
        else{
             return notFound("File not found").as("text/html");
        }
    }

    public static Result downloadRefund(Long id) {
        RefundRequest refundRequest = RefundRequest.finderById(id);
        if (refundRequest == null) {
            return ok(errorPage.render("Attachment not available", null, !isAjax()));
        }
        File file = new File("public/uploads/" + refundRequest.attachment);
        if (file.exists()) {
            return ok(file);
        }
        return notFound("File not found").as("text/html");
    }

    public static Result studentNotes() {
        if (session("student") != null) {
            Users u = Users.finderByMail(session("student"));
            if (u != null) {
                u.update();
                Applicant apcnt = Applicant.finderByUser(u.id);
                Applied aps = null;
                if (apcnt != null) {
                    aps = Applied.byApp(apcnt.id);
                }
                return ok(views.html.student.notifications.render(u, apcnt, aps));
            } else {
                return ok("error");
            }
        } else {
            return ok("error");
        }
    }

    public static Result detailedView(Long id, String type) {
        Attachment att = Attachment.finderById(id);
        Verification v = Verification.finderByAtt(att.id);

        String bits = att.attachName;
        String lastOne = Vld.getExt(bits);

        return ok(views.html.register.detailed.render(att, v, type, lastOne));
    }

    public static Result getSubForm(Long assId, Boolean aBoolean) {
        if (session("student") != null && Ins("student") != null) {
            Student stu = Ins("student").me();
            Assignment as = Assignment.finderById(assId);
            //Groups group = GroupMembers.getGroup(stu.id, Assignment.finderById(assId).component.id);
            if (stu != null && as != null) {
                String route = String.valueOf(routes.Application.submitAssignment(assId, false)); // String.valueOf((!aBoolean) ? routes.Application.submitAssignment(assId, false)  : routes.Application.GroupSubmitAssignment(assId, group.id));
                return ok(views.html.student.submissionForm.render(stu, as, route));
            }
        }
        return ok("0");
    }

    public static Result getSubGroupForm(Long assId,Long gid, Boolean aBoolean) {
        if (session("student") != null && Ins("student") != null) {
            Student stu = Ins("student").me();
            Groups group = Groups.finderById(gid);
            Assignment as = Assignment.finderById(assId);
            if (stu != null && as != null) {
                return ok(views.html.student.submissionGroupForm.render(stu, group, as));
            }
        }
        return ok("0");
    }
    public static Result submitAssignment(Long idea, Boolean isGroup) {
        if (session("student") != null && Ins("student") != null) {
            Student stu = Ins("student").me();
            if (stu == null) {
                return ok(notStudent.render());
            }
            Form<Submission> f = Form.form(Submission.class).bindFromRequest();
            if (f.hasErrors()) return ok(f.errorsAsJson());
            Submission sub = f.get();
            Assignment assignment = Assignment.finderById(idea);
            if (assignment == null) {
                return ok("0");
            }
            Date endDate = assignment.endDate;
            if (endDate == null) return ok("1");
            Calendar instance = Calendar.getInstance();
            instance.setTime(endDate);
            instance.add(Calendar.DATE, 1);
            Date time = instance.getTime();
            if (time.before(new Date())) {
                return ok("Date of submission is expired.");
            }
            List<Submission> submissionList = Submission.submissionByAssignment(stu.id, idea);
            Submission n = null;
                for(Submission submission : submissionList) {
                    String regNo = stu.regNo;
                    n = submission;
                    n.comment = sub.comment;
                    n.assignment = assignment;
                    n.student = stu;
                    String regN = stu.regNo;
                    n.forGroup = isGroup;
                    n.attachment = uploadFile(n.attachment, "attachment","class_files/assignment/submission/");
                    if (n != null) {
                        n.status = "submitted";
                        n.update();
                    }
                }
            return ok("1");
        }
        return ok("0");
    }

    public static Result GroupSubmitAssignment(Long idea, Long gid) {
        if (session("student") != null && Ins("student") != null) {
            Student stu = Ins("student").me();
            if (stu == null) {
                return ok(notStudent.render());
            }

            Form<GroupSubmission> f = Form.form(GroupSubmission.class).bindFromRequest();
            GroupSubmission sub = f.get();
            Assignment assignment = Assignment.finderById(idea);
            if (assignment == null) {
                return ok("0");
            }
            Date endDate = assignment.endDate;
            if (endDate == null) return ok("1");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endDate);
            calendar.add(Calendar.DATE, 1);
            Date time = calendar.getTime();
            if (time.before(new Date())) {
                return ok("Date of submission already expired.");
            }
            List<GroupSubmission> groupSubmissionList = GroupSubmission.finder.setDistinct(true).where()
                    .eq("assignment.id", assignment.id)
                    .eq("groups.id", gid)
                    .findList();
            for(GroupSubmission g: groupSubmissionList){
                      g.comment = sub.comment;
                      g.assignment = assignment;
                      g.groups = Groups.finderById(gid);
                      g.status = "submitted";
                      g.attachment = uploadFile(g.attachment, "attachment","class_files/assignment/group_submission");
                      g.update();
            }
            return ok("1");
        }
        return ok("0");
    }

    public static Result viewMines(Boolean bool) {
        if (session("Instructor") != null && Ins("Instructor") != null || session("mark_officer") != null || session("Coordinator") != null) {
            Lecture lect = activeL(Ins("Instructor"));
            if (lect == null) {
                return ok("error");
            }
            List<Component> myComp = lect.myComp();
            return ok(views.html.lecture.viewAssignments.render(lect, myComp, bool));
        }
        return ok("0");
    }
    public static Result viewMinesGroup() {
        if (session("Instructor") != null && Ins("Instructor") != null) {
            Lecture lect = activeL(Ins("Instructor"));
            if (lect == null) {
                return ok("error");
            }
            List<Component> myComp = lect.myComp();
            return ok(views.html.lecture.viewAssignmentsGroup.render(lect, myComp));
        }
        return ok("0");
    }

    public static Result deleteUncompleted() {
        for(Applicant applicant : Applicant.all()){
            if(applicant.toString().trim().equalsIgnoreCase("")){
                applicant.deleteStatus = true;
                applicant.update();
                Users user = applicant.user;
                user.deleteStatus = true;
                user.update();
            }else{
                applicant.deleteStatus = false;
                applicant.update();
                Users user = applicant.user;
                user.deleteStatus = false;
                user.update();
            }
        }
        return ok("All deleted success!");
    }
    public static Result unableStudent(String stdEm) {
        Users user1 = Users.finderByMail(stdEm);
        if(user1 != null) {
            user1.deleteStatus = false;
            user1.stating = "10";
            user1.update();
            return ok("User " + user1.names + " Enabled successfully!");
        }else{
            return ok("User with email: "+stdEm+" not exists!");
        }
    }
    public static Result enableStudent() {
        Form<Users> f = Form.form(Users.class).bindFromRequest();
        Users user = f.get();
        String email = f.field("email").value();
        return unableStudent(email);
    }
    public static Result backUncompleted() {
        for(Applicant applicant : Applicant.allUncompleted()){
            if(applicant.hasAttachment()){
                applicant.deleteStatus = false;
                applicant.update();
                Users user = applicant.user;
                user.deleteStatus = false;
                user.update();
            }
        }
        return ok("All backed success!");
    }
    public static Result assignmentMarks(Boolean bool) {
        if (session("Instructor") != null && Ins("Instructor") != null) {
            Lecture lect = activeL(Ins("Instructor"));
            if (lect == null) {
                return ok("error");
            }
            List<Training> trainings = lect.myTrainingsAll();
            List<Component> myComp = lect.myComp();
            return ok(views.html.lecture.viewAssignmentsMarks.render(lect, myComp, bool));
        }
        return ok("0");
    }
    public static Result editAssignmentMarks(Boolean bool) {
        if (session("Instructor") != null && Ins("Instructor") != null) {
            Lecture lect = activeL(Ins("Instructor"));
            if (lect == null) {
                return ok("error");
            }
            List<Training> trainings = lect.myTrainingsAll();
            List<Component> myComp = lect.myComp();
            return ok(views.html.lecture.editAssignmentMarks.render(lect, myComp, bool));
        }
        return ok("0");
    }

    public static Result viewAssignmentsCor(Boolean bool) {
        if (session("Coordinator") != null && Ins("Coordinator") != null && Ins("mark_officer") != null) {
            Users coordinator = Ins("Coordinator");
            if (coordinator == null) {
                return ok("error");
            }
            List<Schedule> schedules = Schedule.all();
            return ok(views.html.coordinat.viewAssignmentsCor.render(coordinator, schedules, bool));
        }
        return ok("0");
    }
    public static Result fetchSetMarkPage() {
        if (isInstructor()) {
            Lecture lect = activeL(Ins("Instructor"));
            if (lect == null) {
                return ok("error");
            }
            return ok(views.html.lecture.setMarks.render(lect.myTrainingsAll(), lect.id));
        } else if (isRegistrar()) {
            return CoordinatorController.viewLecturersTimetabled("SetComponentMarks");
        } else if (isCoordinator()) {
            return CoordinatorController.viewLecturersTimetabled("SetComponentMarks");
        } else if (isCleManager()) {
            return ok(views.html.lecture.setMarks.render(Training.withCle(), 0L));
        }
        return ok("0");
    }
    public static Result fetchSetResitMarkPage() {
        if (isInstructor()) {
            Lecture lect = activeL(Ins("Instructor"));
            if (lect == null) {
                return ok("error");
            }
            return ok(views.html.lecture.fetchSetResitMarkPage.render(lect.myTrainingsAll(), lect.id));
        } else if (isRegistrar()) {
            return CoordinatorController.viewLecturersTimetabled("fetchSetResitMarkPage");
        } else if (isCoordinator()) {
            return CoordinatorController.viewLecturersTimetabled("fetchSetResitMarkPage");
        } else if (isCleManager()) {
            return ok(views.html.lecture.fetchSetResitMarkPage.render(Training.withCle(), 0L));
        }
        return ok("0");
    }

    public static Result saveMarksAll(Long studentId, Long componentId, float marks, String types) {
        if (isInstructor() || session("Coordinator") != null || session("registrar") != null || session("mark_officer") != null) {
            if (Ins("Instructor") != null) {
                Lecture lect = activeL(Ins("Instructor"));
                if (lect == null) {
                    return ok("error");
                }
            }
            Users user = null;
            if(session("Instructor") != null) {
                user  = Application.Ins("Instructor");
            }
            if(session("Coordinator") != null) {
                user  = Application.Ins("Coordinator");
            }
            if(session("registrar") != null) {
                user  = Application.Ins("registrar");
            }
            if(session("mark_officer") != null) {
                user  = Application.Ins("mark_officer");
            }
            Boolean exist = true;
            Student student = Student.finderById(studentId);
                SubMark subMark = SubMark.byDual(studentId, componentId);
                SubMark sub = new SubMark();
                if (subMark == null) {
                    sub.student = student;
                    sub.component = Component.finderById(componentId);
                    sub.examResult = (types.equals("EXAM")) ? marks : (subMark != null) ? subMark.examResult : sub.examResult;
                    Component co = Component.finderById(componentId);
                    if (!(co != null && co.module.examMax > marks)) {
                        return ok(marks + " you have out range of maximum marks " + co.module.reMax);
                    }
                    if(student.status.equalsIgnoreCase("RE-SIT")) {
                        sub.isResitResult = true;
                    }
                    sub.deleteStatus = false;
                    sub.doneBy = user.print();
                    sub.save();
                } else {
                    subMark.examResult = (types.equals("EXAM")) ? marks : (subMark != null) ? subMark.examResult : sub.examResult;
                    subMark.doneBy = user.print();
                    subMark.update(subMark.id);
                }

            return ok("1");
        }
        return ok("0");

    }

    public static Result fetchTable(Long trainId, long lecturerId) {
        if (session("registrar") != null || session("Instructor") != null || session("Coordinator") != null || session("mark_officer") != null || session("DTR/Coordinator") != null) {
            Lecture lecturer = Lecture.finderById(lecturerId);
            Training t = Training.finderById(trainId);
            if (t == null) {
                return ok(errorPage.render("requested training is not found !!", null, false));
            }
            return ok(views.html.lecture.lectTable.render(lecturer, views.html.admininistrator.timetable.render(t, lecturer, false)));
        }
        return ok("0");
    }

    public static Result attendanceCheck(Long sch) {

        Schedule sched = Schedule.finderById(sch);
        if (sched == null) {
            return ok("error");
        }
        Training t = sched.training;
        List<Student> listStudent = t.students();
        if (session("Instructor") != null && Ins("Instructor") != null) {

            Lecture lecture = activeL(Ins("Instructor"));
            if (lecture == null || sched == null) {
                return ok("error");
            }
            return ok(views.html.lecture.attendanceStudents.render(sched, listStudent, t));
        } else if (session("Coordinator") != null ||session("mark_officer") != null || session("DTR/Coordinator") != null || session("registrar") != null) {
            return ok(views.html.lecture.attendanceStudents.render(sched, listStudent, t));
        }
        return ok("0");
    }

    public static Result setAttending() {
        if (session("Instructor") != null && Ins("Instructor") != null) {
            Lecture lecture = activeL(Ins("Instructor"));
            if (lecture == null) {
                return ok("error");
            }
            return ok(views.html.lecture.attendancePage.render(lecture, lecture.myTrainingsAll(), errorPage.render("Choose training class to set attendance", null, false)));
        }
        return ok("0");
    }

    public static Result CoAttending() {
        if (session("Coordinator") != null && Ins("Coordinator") != null && Ins("mark_officer") != null) {
            return ok(views.html.lecture.attendancePage.render(null, Training.all(), errorPage.render("Choose training class to set attendance", null, false)));
        }
        return ok("0");
    }
    public static Result setMarkPage(Long lId, Long trainId, Long compId) {
        Training t = Training.finderById(trainId);
        Component comp = Component.finderById(compId);
        if (t == null || comp == null) {
            return ok(errorPage.render("Bade reference ", null, !isAjax()));
        }
        List<Student> stus = t.students();
        if (session("Instructor") != null && Ins("Instructor") != null) {
            Lecture lecture = activeL(Ins("Instructor"));
            if (lecture == null) {
                return ok("error");
            }
            return ok(views.html.lecture.studentsMarksEdit.render(stus, t, comp, lecture));
        } else if (session("Coordinator") != null || session("mark_officer") != null || isRegistrarOrCle()) {
            Lecture lecture = Lecture.finderById(lId);
            return ok(views.html.lecture.studentsMarksEdit.render(stus, t, comp, lecture));
        }
        return ok("0");
    }

    public static Result setResitMarkPage(Long lId, Long trainId, Long compId) {
        Training t = Training.finderById(trainId);
        Component comp = Component.finderById(compId);
        if (t == null || comp == null) {
            return ok(errorPage.render("Bade reference ", null, !isAjax()));
        }
        List<Student> stus = t.studentsInResit();
        if (session("Instructor") != null && Ins("Instructor") != null) {
            Lecture lecture = activeL(Ins("Instructor"));
            if (lecture == null) {
                return ok("error");
            }
            return ok(views.html.lecture.studentsResitMarksEdit.render(stus, t, comp, lecture));
        } else if (session("Coordinator") != null || session("mark_officer") != null || isRegistrarOrCle()) {
            Lecture lecture = Lecture.finderById(lId);
            return ok(views.html.lecture.studentsResitMarksEdit.render(stus, t, comp, lecture));
        }
        return ok("0");
    }

    public static Result setMarkClaimPage(Long lId, Long trainId, Long compId) {
        Training t = Training.finderById(trainId);
        Component comp = Component.finderById(compId);
        if (t == null || comp == null) {
            return ok(errorPage.render("Bade reference ", null, !isAjax()));
        }
        List<Student> stus = t.students();
        List<SubMark> subMarks = SubMark.find.where()
                .eq("deleteStatus", false)
                .eq("student.deleteStatus", false)
                .eq("student.training.deleteStatus", false)
                .eq("student.training.isClosed", false)
                .findList();
        if (session("Instructor") != null && Ins("Instructor") != null) {
            Lecture lecture = activeL(Ins("Instructor"));
            if (lecture == null) {
                return ok("error");
            }
            return ok(views.html.lecture.setMarkClaimPage.render(subMarks, comp.module));
        } else if (session("Coordinator") != null || session("mark_officer") != null || isRegistrarOrCle()) {
            Lecture lecture = Lecture.finderById(lId);
            return ok(views.html.lecture.setMarkClaimPage.render(subMarks, comp.module));
        }
        return ok("0");
    }
    public static Result marksPage(Long tr, Long lectureId) {
        Training training = Training.finderById(tr);
        if (training == null) {
            return ok("error not training");
        }
        List<Student> stuList = training.students();
        Lecture lecture = Lecture.finderById(lectureId);
        if (lecture == null) {
            return ok("error");
        }
        List<Component> comps = training.myComp(lecture);
        return ok(views.html.lecture.studentsMarksPage.render(lecture, stuList, training, comps));
    }
    public static Result resitMarksPage(Long tr, Long lectureId) {
        Training training = Training.finderById(tr);
        if (training == null) {
            return ok("error not training");
        }
        List<Student> stuList = training.studentsInResit();
        Lecture lecture = Lecture.finderById(lectureId);
        if (lecture == null) {
            return ok("error");
        }
        List<Component> comps = training.myComp(lecture);
        return ok(views.html.lecture.resitMarksPage.render(lecture, stuList, training, comps));
    }

    public static Result marksClaimPage(Long tr, Long lectureId) {
        Training training = Training.finderById(tr);
        if (training == null) {
            return ok("error not training");
        }
        List<Student> stuList = training.students();
        Lecture lecture = Lecture.finderById(lectureId);
        if (lecture == null) {
            return ok("error");
        }
        List<Component> comps = training.myComp(lecture);
        return ok(views.html.lecture.marksClaimPage.render(lecture, stuList, training, comps));
    }

    public static Result singleAssign(long componentId, Boolean bld) {
        Component myComp = Component.finderById(componentId);

        if (myComp == null) {
            return ok("0");
        }
            List<Assignment> assign = myComp.compAssignments(bld);
            if (session("Instructor") != null || session("Coordinator") != null || session("mark_officer") != null || session("registrar") != null) {
                return ok(views.html.lecture.singleAssign.render(assign, bld));
            }
        return ok("0");
    }
    public static Result singleAssignGroup(long componentId, long gId, long l) {
        Component myComp = Component.finderById(componentId);
        if (myComp == null) {
            return ok("0");
        }
        Groups groups = Groups.finderById(gId);
        List<Assignment> assignments = Assignment.finder.where()
                .eq("group.id", groups.id)
                .eq("grouped", true)
                .findList();
            if (session("Instructor") != null || session("Coordinator") != null || session("mark_officer") != null || session("registrar") != null) {
                return ok(views.html.lecture.singleAssignGroup.render(assignments,groups));
            }
        return ok("0");
    }
    public static Result unsubmitedAssignment(long componentId, Boolean bld) {
        Component myComp = Component.finderById(componentId);

        if (myComp == null) {
            return ok("0");
        }
        List<Assignment> assign = myComp.compAssignmentsUnsub(bld);

        if (session("Instructor") != null || session("Coordinator") != null || session("mark_officer") != null || session("registrar") != null) {
            return ok(views.html.lecture.singleAssignUnsubited.render(assign, bld));
        }
        return ok("0");
    }
    public static Result assignmentMarksCorrection(long componentId, Boolean bld) {
        Component myComp = Component.finderById(componentId);
        if (myComp == null) {
            return ok("0");
        }
        List<Assignment> assign = myComp.assignmentMarksCorrection(bld);
        if (session("Instructor") != null || session("Coordinator") != null || session("mark_officer") != null || session("registrar") != null) {
            return ok(views.html.lecture.assignmentMarksCorrection.render(assign, bld));
        }
        return ok("0");
    }
    public static Result unsubmitedEditAssignment(long componentId, Boolean bld) {
        Component myComp = Component.finderById(componentId);

        if (myComp == null) {
            return ok("0");
        }
        List<Assignment> assign = myComp.compAssignmentsUnsub(bld);

        if (session("Instructor") != null || session("Coordinator") != null || session("mark_officer") != null || session("registrar") != null) {
            return ok(views.html.lecture.unsubmitedEditAssignment.render(assign, bld));
        }
        return ok("0");
    }


    private static Result makeFinalStudent(Long id, boolean isForce) {
        Form<Applicant> apForm = Form.form(Applicant.class).bindFromRequest();
        Applicant apping = apForm.get();
        Applicant app = Applicant.finderById(id);
        app.experience = apping.experience;
        app.update();
        if (app == null) {
            return notFound();
        }

        List<Applicant> list = app.user.applicantList;
        for (Applicant apps : list ){
            apps.active = true;
            apps.update();
        }
        if(app.applied.training.iMode.campusProgram.program.cle){
            isForce = true;
        }
        Applied applied = Applied.getAppliedB(app);
        if (applied == null && !app.applied.training.iMode.campusProgram.program.cle) {
            return notFound();
        }
        Double amount = applied.training.minPayment;
        if (amount == null) {
            amount = app.getAmountToPay();
        }

        if (app.getTotalAmountPaid() < amount && !isForce) {
            String s = "This applicant needs to pay minimum of the amount to pay";
            flash("error", s);
            return sortPage("accepted");
        }

        app.sponsorInstutute = apping.sponsorInstutute;
        app.shelfNumber = apping.shelfNumber;
        app.comments = apping.comments;
        app.save();

        Applied appl = Applied.byApp(app.id);
        Student st = new Student();

        st.firstName = app.firstName;
        st.familyName = app.familyName;
        st.training = appl.training;
        String regNumber;
        String emailRegNumber;

        String sLetter = appl.training.iMode.campusProgram.program.programAcronym.toUpperCase();
        String sModeLetter = appl.training.iMode.sessionMode.mode.modeAcronym.toUpperCase();
        String sessionLetter = appl.training.iMode.sessionMode.session.sessionName.substring(0, 2).toUpperCase();
        int year = Calendar.getInstance().get(Calendar.YEAR);
        Student lastStudent = Student.finder.where().eq("deleteStatus", false).setMaxRows(1).orderBy("id desc").findUnique();
        long studentId = 1L;
        if (lastStudent != null) {
            studentId = lastStudent.id + 1;
        }

        regNumber = sLetter + "/" + sModeLetter + "" + sessionLetter + "/" + year + "/" + Counts.leftPadWithZeroes(String.valueOf(studentId), 3);
        emailRegNumber = sLetter.toLowerCase()+sModeLetter.toLowerCase()+sessionLetter.toLowerCase()+year+Counts.leftPadWithZeroes(String.valueOf(studentId), 3);
        String officialEmail = emailRegNumber+"@ilpd.ac.rw";
        st.regNo = regNumber;
        st.academicEmail = officialEmail;
        st.applicant = app;
        st.deleteStatus = false;
        Users user = app.user;
        user.deleteStatus = false;
        user.update();
        Student xCheck = Student.byApp(app.id);
        if (xCheck == null && appl.applicationStatus.equals("accepted")) {
            appl.shelfNumber = apForm.field("shelfNumber").value();
            appl.applicationStatus = "student";
            appl.update();
            st.status = "active";
            st.emailStatus = "pending";
            st.save();
            String link = request().host() + routes.Application.admissionLetter(appl.id);
            String messEmail = "Hi," + st.toString() + " you are now confirmed as student of ILPD your Registration Number is " + st.regNo
                    + " <a href=\"http://" + link + "\">   Download final admission letter </a> ";
            new Counts().sendAppliedEmail(st.applicant.applied, Constants.APPLICATION_INFO, messEmail);
        }

        return sortPage("accepted");
    }

    public static Result makeStudent(Long id) {
        if (isRegistrarOrCle()) {
            return makeFinalStudent(id, false);
        } else if (session("Finance") != null) {
            return makeFinalStudent(id, true);
        }else if (isCleManager()) {
            return makeFinalStudent(id, true);
        }
        return sortPage("accepted");
    }


    public static Result resetMail(String codes) {
        Users check = Users.finderByCodes(codes);
        if (check != null) {
            session("reset", check.email);
            return ok(forgot.render(check));
        } else {
            return ok(fakeMail.render(codes));
        }
    }

    public static Result getEmailForReset() {
        Form<Users> f = Form.form(Users.class).bindFromRequest();

        Users u = f.get();

        Users user = Users.finderByMail(u.email);
        if (user == null) {
            return ok(Message.emailNotFound);
        } else {
            String link = request().host() + routes.Application.resetMail(user.resetCode);
            String message = "<div>Hi " + user.email + "</div><a href=\"http://" + link + "\">Click here to change password for your account</a>";

            new Counts().sendUserEmail(user, Constants.PASSWORD_REST_TITLE, message);
            return ok(Message.sentMailforReset);
        }
    }
    public static Result enableStudentAccount() {
        return ok(views.html.student.enableStudentAccount.render());
    }
    public static Result getStudents(String str) {
        List<Student> l1 = Student.all(str, isCleManager());
        return ok(views.html.register.approved.render(l1));
    }
    public static Result searchApplicant(String str) {
        List<Applied> l1 = Applied.alls(str);
        return ok(views.html.register.approvedApp.render(l1));
    }

    public static Result getStudentsIntake(Long id) {
        List<Student> l1 = Student.byIntake(id, isCleManager());
        return ok(views.html.register.approved.render(l1));
    }

    public static Result studentByIntake(Long id) {
        return ok(views.html.register.allApproved.render(id));
    }

    public static Result resetApp(Long id) {
        if (session("student") != null) {
            Verification vx = Verification.finderById(id);
            Users u = Users.finderByMail(session("student"));

            if (vx != null) {
                Applied app = Applied.finderById(id);
                app.status = false;
                app.applicationStatus = "";
                app.update();
            }

            return redirect(routes.Application.index());
        } else {
            return ok("error");
        }
    }

    public static Result viewStudentDetails(Long id, String type) {
        Student stu = Student.finderById(id);
        return ok(views.html.register.DetailedStudent.render(stu));

    }

    public static Result viewApplicantDetails(Long id, String type) {
        Applicant applicant = Applicant.finderById(id);
        return ok(views.html.register.applicants.details.render(applicant,""));

    }

    public static Result shelfer(Long id) {
        Verification vrf = Verification.finderById(id);
        vrf.shelfed = !vrf.shelfed;
        vrf.update();
        return ok("");

    }

    public static Result updateAppStatus(Long id, Boolean grp) {
        Form<Applied> apForm = Form.form(Applied.class).bindFromRequest();
        Applied ap = apForm.get();

        Applied app = Applied.finderById(id);
        app.applicationStatus = ap.applicationStatus;
        app.statusComment = ap.statusComment;
        app.update();

        StringBuilder body = new StringBuilder();
        body.append("Dear applicant ,").append(app.applicant.toString()).append("<br/><br/>");

        for (Attachment atte : Attachment.byApp(app.applicant.id)) {
            Verification vrf = Verification.finderByAtt(atte.id);
            String liveStatus = (vrf == null) ? "still pending" : (vrf.status) ? "accepted" : "canceled";
            body.append("Your ").append(atte.file.fileName).append(" is ").append(liveStatus).append("\n");
        }
        String link = request().host() + routes.Application.admissionLetter(app.id);
        String messEmail = "";
        if (!(app.applicationStatus.equalsIgnoreCase("derefered") || app.applicationStatus.equalsIgnoreCase("rejected"))) {
            messEmail = " <a href=\"http://" + link + "\">Download provisional admission letter</a> ";
        }

        body.append("<br></br> ").append(ap.statusComment).append(" ").append(messEmail);
        String sts = "Application info from ILPD Registrar";
        new Counts().sendAppliedEmail(app, sts, body.toString());

        if (app.applicationStatus.equalsIgnoreCase("rejected")) {
            Transaction transaction = Ebean.beginTransaction();
            try {
                deleteAccount(app.applicant.account);
                deleteAttachment(app.applicant);
                deleteApplied(app);
                deleteApplicant(app.applicant);
                deleteUserRole(app.applicant.user);
                deleteUser(app.applicant.user);
                transaction.commit();
            } finally {
                transaction.end();
            }

        }
        return sortPage("new");
    }

    private static void deleteUserRole(Users user) {
        List<UserRole> userRoles = UserRole.finder.where().eq("user.id", user.id).findList();
        for (UserRole userRole : userRoles) {
            userRole.delete();
        }
    }

    private static void deleteAttachment(Applicant app) {
        List<Attachment> attachments = Attachment.byApp(app.id);
        for (Attachment attachment : attachments) {
            deleteVerificationByAttachment(attachment);
            attachment.delete();
        }
    }

    private static void deleteVerificationByAttachment(Attachment attachment) {
        List<Verification> list = Verification.finder.where().eq("attachment.id", attachment.id).findList();
        for (Verification verification : list) {
            verification.delete();
        }
    }

    private static void deleteAccount(Account account) {
        if (account == null) {
            return;
        }
        account.delete();
    }

    private static void deleteUser(Users user) {
        user.delete();
    }

    private static void deleteApplicant(Applicant applicant) {
        applicant.delete();
    }

    private static void deleteApplied(Applied app) {
        app.delete();
    }

    public static Result downloader(Long id) {
        try {
            Attachment att = Attachment.finderById(id);
            return ok(new java.io.File("public/uploads/" + att.attachName));
        } catch (Exception ex) {
            return ok("File Not Found");
        }
    }

    public static Result downloadAssign(Long id, Boolean bool) {
        try {
            if (bool) {
                Assignment att = Assignment.finderById(id);
                return ok(new java.io.File("public/uploads/" + att.attachment));
            } else {
                Submission sub = Submission.finderById(id);
                return ok(new java.io.File("public/uploads/" + sub.attachment));
            }
        } catch (Exception ex) {
            return ok("File Not Found");
        }
    }
    public static Result downloadAssignGroup(Long id) {
        try {
                GroupSubmission att = GroupSubmission.finder.byId(id);
                return ok(new java.io.File("public/uploads/"+att.attachment));
        } catch (Exception ex) {
            return ok("File Not Found");
        }
    }

    public static Result sortPage(String sorter) {
        return ok(views.html.register.preLoader.render(sorter, null, getTrainings()));
    }


    public static Result sortIntake(Long id) {
        Intake init = Intake.finderById(id);
        if (init != null) {
            return ok(views.html.register.preLoader.render("inApp", id, getTrainings()));
        } else {
            return ok("0");
        }
    }

    public static Result sortByProgram(Long id) {
        Program init = Program.finderById(id);
        if (init != null) {
            return ok(views.html.register.preLoader.render("prog", id, getTrainings()));
        } else {
            return ok();
        }
    }

    public static Result denyFile(Long id, String typ) {
        if (isRegistrar()) {
            Form<Verification> f = Form.form(Verification.class).bindFromRequest();

            Verification vx = f.get();
            Verification ver = Verification.finderByAtt(id);
            if (ver == null) {
                ver = new Verification();
            }

            ver.status = false;
            ver.userComment = vx.userComment;
            ver.attachment = Attachment.finderById(id);
            ver.verifier = Users.finderByMail(session("registrar"));

            ver.save();

            return showAppliedForm(Applied.byApp(ver.attachment.app.id).id, "under");

        }
        return ok();
    }

    public static Result deleteAll(Long id, String def) {
        if (session("admin") != null || session("Library") != null || isRegistrarOrCle() || session("Instructor") != null || session("Logistic") != null || session("VRAC") != null || session("registrar") != null || session("Coordinator") != null) {
            switch (def) {
                case "year":
                    AcademicYear yr = AcademicYear.finderById(id);
                    yr.deleteStatus = true;
                    yr.update();
                    break;
                case "courseMaterial":
                    CourseMaterial courseMaterial = CourseMaterial.finder.byId(id);
                    if (id == null) {
                        return notFound();
                    }
                    courseMaterial.deleteStatus = true;
                    courseMaterial.update(id);
                    break;
                case "hours":
                    Hours hours = Hours.finderById(id);
                    hours.deleteStatus = true;
                    hours.update();
                    break;
                case "cPro":
                    CampusProgram campusProgram = CampusProgram.finderById(id);
                    campusProgram.deleteStatus = true;
                    campusProgram.update();
                    break;
                case "cPMode":
                    CampusProgramMode campusProgramMode = CampusProgramMode.finderById(id);
                    campusProgramMode.deleteStatus = true;
                    campusProgramMode.update();
                    break;
                case "sMode":
                    SessionMode sessionMode = SessionMode.finderById(id);
                    sessionMode.deleteStatus = true;
                    sessionMode.update();
                    break;
                case "iMode":
                    IntakeSessionMode intakeSessionMode = IntakeSessionMode.finderById(id);
                    intakeSessionMode.deleteStatus = true;
                    intakeSessionMode.update();
                    break;
                case "daySession":
                    DaySession daySession = DaySession.finderById(id);
                    daySession.deleteStatus = true;
                    daySession.update();
                    break;
                case "hourSession":
                    HourSession hourSession = HourSession.finderById(id);
                    hourSession.deleteStatus = true;
                    hourSession.update();
                    break;
                case "program": {
                    Program pro = Program.finderById(id);
                    pro.deleteStatus = true;
                    pro.update();
                    break;
                }
                case "camp":
                    Campus comp = Campus.finderById(id);
                    comp.deleteStatus = true;
                    comp.update();
                    break;
                case "pro": {
                    Program pro = Program.finderById(id);
                    pro.deleteStatus = true;
                    pro.update();
                    break;
                }
                case "session":
                    Session sess = Session.finderById(id);
                    sess.deleteStatus = true;
                    sess.update();
                    break;
                case "intake":
                    Intake intk = Intake.finderById(id);
                    intk.deleteStatus = true;
                    intk.update();
                    break;
                case "module":
                    Module module = Module.finderById(id);
                    module.deleteStatus = true;
                    module.update();
                    break;
                case "block": {
                    Block obj = Block.find(id);
                    obj.deleteStatus = true;
                    obj.update();
                    break;
                }
                case "location": {
                    Location obj = Location.find(id);
                    obj.deleteStatus = true;
                    obj.update();
                    break;
                }
                case "Instructor": {
                    Transaction txn = Ebean.beginTransaction();
                    try {
                        Lecture lecture = Lecture.finderById(id);
                        lecture.deleteStatus = true;
                        lecture.update();
                        Users users = lecture.user;
                        users.deleteStatus = true;
                        users.update();
                        txn.commit();
                    } finally {
                        txn.end();
                    }
                    break;
                }
                case "fees":
                    SchoolFees fees = SchoolFees.finderById(id);
                    fees.deleteStatus = true;
                    fees.update();
                    break;
                case "users": {
                    Users users = Users.finderById(id);
                    users.deleteStatus = true;
                    users.update();
                    break;
                }
                case "files":
                    AcademicFiles files = AcademicFiles.finderById(id);
                    files.deleteStatus = true;
                    files.update();
                    break;
                case "mode":
                    StudyMode studyMode = StudyMode.finderById(id);
                    studyMode.deleteStatus = true;
                    studyMode.update();
                    break;
                case "libraryEntry":
                    ILDPLibrary library = ILDPLibrary.finderById(id);
                    library.deleteStatus = true;
                    library.clear = true;
                    library.update();
                    break;
                case "lecture": {
                    Lecture lecture = Lecture.finderById(id);
                    lecture.deleteStatus = true;
                    lecture.update();
                    break;
                }
                case "room":
                    Room room = Room.finderById(id);
                    room.deleteStatus = true;
                    room.update();
                    break;
                case "gMember": {
                    GroupMembers g = GroupMembers.finder.ref(id);
                    g.deleteStatus = true;
                    g.update();
                    g.delete();
                    break;
                }
                case "deleGroup": {
                    Groups g = Groups.find.byId(id);
                    g.deleteStatus = true;
                    g.update();
                    break;
                }
                case "ann": {
                    Announce g = Announce.finderById(id);
                    g.deleteStatus = true;
                    g.update();
                    break;
                }
                case "day": {
                    Days g = Days.finderById(id);
                    g.deleteStatus = true;
                    g.update();
                    break;
                }
                case "comp": {
                    Component g = Component.finderById(id);
                    g.deleteStatus = true;
                    g.update();
                    break;
                }
                case "train": {
                    Training g = Training.finderById(id);
                    g.deleteStatus = true;
                    g.update();
                    break;
                }
                case "dateRange": {
                    DateRange g = DateRange.finderById(id);
                    g.deleteStatus = true;
                    g.update();
                    break;
                }
                case "schedule": {
                    Schedule g = Schedule.finderById(id);
                    g.deleteStatus = true;
                    g.update();
                    break;
                }
                case "employee": {
                    Employee g = Employee.find(id);
                    g.deleteStatus = true;
                    g.update();
                    Users users = Users.getEmployee(g);
                    users.deleteStatus = true;
                    users.update();
                    break;
                }
                case "deleteItem": {
                    Supplied supplied = Supplied.find(id);
                    supplied.deleteStatus = true;
                    supplied.update();

                    // update item qty
                    supplied.item.balanceQty -= supplied.suppliedQty;
                    supplied.item.purchaseQty -= supplied.suppliedQty;
                    supplied.item.update();
                }
                default:
                    return ok("error");
            }
            return ok("1");
        }
        return ok("error requested");
    }


    public static Result announcePage() {

        List<Roles> rolesList = Roles.allAll();
        return ok(views.html.register.announce.render(Announce.all(true), rolesList));
    }

    public static Result eventpage() {
        List<Roles> rolesList = Roles.allAll();
        return ok(views.html.register.event.render(Announce.all(false), rolesList));
    }

    public static Result updateInst(Long id) {
        if (session("admin") != null) {
            Form<ProfileInfo> userForm = Form.form(ProfileInfo.class).bindFromRequest();
            ProfileInfo pF = userForm.get();

            ProfileInfo proInfo = ProfileInfo.finderById(id);
            proInfo.aboutInfo = pF.aboutInfo;
            proInfo.email = pF.email;
            proInfo.profileLogo = uploadImage("personal_detail/profile_information/",proInfo.profileLogo);
            proInfo.profileName = pF.profileName;
            proInfo.profileAcronym = pF.profileAcronym;
            proInfo.address = pF.address;
            proInfo.website = pF.website;
            proInfo.phone = pF.phone;
            proInfo.admissionEmail1 = pF.admissionEmail1;
            proInfo.admissionEmail2 = pF.admissionEmail2;

            proInfo.update();

            return redirect("/%2Fadmin%2FupdateInfo#Ptab1");
        } else {
            return ok("error requested");
        }
    }


    public static Result renderRoles() {
        if (session(defaultSession) != null) {
            Users user = Users.finderByMail(session(defaultSession));
            if (user == null) {
                return ok("0");
            }
            return ok(views.html.userRoles.render(user.RolesForUser(), user));
        }
        return ok(errorPage.render("No page found, try later", null, !isAjax()));
    }

    public static Result setUserRoles(Long id) {
        if (session("admin") != null) {
            Users user = Users.finderById(id);
            if (user == null) {
                return ok(errorPage.render("Requested activity not currently available", null, !isAjax()));
            }

            return ok(setRoles.render(Roles.allSet(), user));
        }
        return ok();
    }

    public static Result removeRole(Long id) {
        if (session("admin") != null) {
            UserRole role = UserRole.finder.byId(id);
            role.deleteStatus = true;
            role.update(id);
            return ok("1");
        }
        return ok("0");
    }

    private static String getLname(String names) {
        return names.substring(names.indexOf(" "), names.length());
    }

    private static String getFname(String names) {
        return names.substring(0, names.indexOf(" "));
    }


    public static Boolean externalRender(UserRole role) {
        try {
            if (role.role.sessionName.equals("lecture") || role.role.sessionName.equalsIgnoreCase("Instructor")) {
                Lecture lecture = new Lecture();
                lecture.user = role.user;
                lecture.fName = getFname(role.user.names);
                lecture.lName = getLname(role.user.names);
                lecture.qualification = "";
                if (lecture.notExist()) {
                    lecture.save();
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public static Result assignRoles() {
        if (session("admin") != null) {
            Form<UserRole> uform = Form.form(UserRole.class).bindFromRequest();
            Map<String, String[]> map = request().body().asFormUrlEncoded();
            String[] checkedVal = map.get("roleID");
            if (checkedVal == null) {
                return ok("Role not found");
            }
            Long userId = Long.parseLong(uform.field("userID").value());
            Users user = Users.finderById(userId);
            if (user == null) {
                return ok("0");
            }
            for (String roleString : checkedVal) {
                Long roleId = Long.parseLong(roleString);
                UserRole role = new UserRole();
                role.user = user;
                role.role = Roles.finder.byId(roleId);
                Boolean render = externalRender(role);
                if (role.notExist() && render) {
                    role.save();
                }
            }
        }
        return ok("1");
    }

    public static Result confirmShowMarks() {
        Form<Student> form = Form.form(Student.class).bindFromRequest();
        Map<String, String[]> map = request().body().asFormUrlEncoded();
        String[] checkedVal = map.get("studentId");
        if (checkedVal == null) {
            return ok("Student not found");
        }
        Long trainingId = Long.parseLong(form.field("trainingId").value());
        Training training = Training.finderById(trainingId);
        if (training == null) {
            return ok("0");
        }
        for (String studentString : checkedVal) {
            Long studentId = Long.parseLong(studentString);
            Student student = Student.finderById(studentId);
            student.viewMarks = true;
            student.update();
        }
        return redirect("/get/marksReportByPermission/mark/"+trainingId+"/");
    }


    public static Result performLogin() {
        Form<Users> userForm = Form.form(Users.class).bindFromRequest();
        Users user = userForm.get();
        Boolean loged = true;
        List<UserRole> list = CheckUser(user);
        loged = loged && list == null;
        if (loged || list.size() < 1) {
            return ok("0");
        }
        return renderRoles();
    }

    public static Result getProfile() {
        if (isApplicant()) {
            Applied applied = applicant().applied;
            if (applied == null) {
                return notFound();
            }
            return ok(views.html.student.profile.render(applied));
        }
        return unauthorized();
    }

    public static Result getStudentsByRegister() {
        List<Student> students = Student.all();
        return ok(views.html.register.students.render(students));
    }

    public static Result getStudentPaymentHistory(long id) {
        Applicant applicant = Applicant.finderById(id);
        if (applicant == null) {
            return notFound();
        }
        List<Payment> payments = Payment.finder.where().eq("account.applicant.id", id).findList();
        return ok(views.html.register.viewStudentPaymentHistory.render(payments, applicant));
    }

    public static Result FinalStudent() {
        Form<Users> usersForm = Form.form(Users.class).bindFromRequest();
        String id = "applicantId";
        Long roleId = Long.parseLong(usersForm.field(id).value());
        Applied applied = Applied.finderById(roleId);
        if( applied == null ) return ok("1");


        List<Applicant> applicantList = applied.applicant.user.applicantList;

        for (Applicant app : applicantList){
            app.active = false;
            app.update();
        }
        applied.applicant.active = true;
        applied.applicant.update();
        session().put(id, String.valueOf(roleId));

        return ok("1");
    }

    public static Result FinalLogin() {
        Form<Users> usersForm = Form.form(Users.class).bindFromRequest();
        Long roleId = Long.parseLong(usersForm.field("roleId").value());
        UserRole role = UserRole.finder.byId(roleId);
        if (role == null) {
            return ok("Request failed , try again");
        }
        session().clear();
        session(defaultSession, role.user.email);
        session(role.role.sessionName, role.user.email);
        if (role.role.sessionName.equals("student")) {
            List<Applied> appliedList = Applied.appliedList(role.user.email);
            if (appliedList.size() > 0 && !role.user.stating.equals("")) {
                response().setHeader("next", "1");
                return ok(views.html.apList.render(appliedList, role.user));
            }
        }
        return ok("1");
    }

    public static Result updateUserProfile() {
        if (session(defaultSession) != null) {
            return ok(changeProfile.render(Ins(defaultSession)));
        }
        return ok();
    }

    public static Result getStudentById(long id) {
        Applicant applicant = Applicant.finderById(id);
        if (applicant == null) {
            return notFound();
        }
        return ok(Json.toJson(applicant));
    }

    public static Result getStudentDamage() {
        List<Damage> damages = Damage.notClearedStudent();
        return ok(views.html.finance.recordDamage.render(damages));
    }

    public static Result postStudentDamage() {
        Form<Damage> damageForm = Form.form(Damage.class).bindFromRequest();
        Student student = Student.finderById(Long.parseLong(damageForm.field("studentId").value()));
        if (student == null) {
            return notFound();
        }

        Damage damage = new Damage();
        damage.name = damageForm.field("damage").value();
        damage.amount = Double.parseDouble(damageForm.field("amount").value());
        damage.description = damageForm.field("description").value();
        damage.student = student;
        damage.save();
        return ok(Json.toJson(damage));
    }

    public static Result clearStudentDamage(Long id) {
        if (id == null) {
            return badRequest("bad data");
        }
        Damage damage = Damage.finderById(id);
        if (damage == null) {
            return notFound("not found");
        }
        damage.clear = true;
        damage.update(id);
        return ok("1");
    }
    public static Result deleteMaterial(Long id) {
        if (id == null) {
            return badRequest("bad data");
        }
        CourseMaterial courseMaterial1 = CourseMaterial.finder.byId(id);
        if (courseMaterial1 == null) {
            return notFound("not found");
        }
        courseMaterial1.deleteStatus = true;
        courseMaterial1.update(id);
        return ok("1");
    }


    public static Result searchStudentByReg(String regNumber) {
        List<Payment> payments = Payment.search(regNumber);
        return ok(Json.toJson(payments));
    }

    public static Result reqeustItems() {
        Users user = Ins(defaultSession);
        if (session(defaultSession) != null && user != null) {
            List<Request> myRequested = Category.requests(user);
            return ok(views.html.ItemRequestPage.render(models.stock.Category.all(), myRequested));
        }
        return badRequest();
    }


    public static Result requestPerCategory() {
        Users user = Ins(defaultSession);
        if (session(defaultSession) != null && user != null) {
            List<Item> items = Item.finder.where().eq("deleteStatus", false).orderBy("id desc").findList();
            List<Request> myRequested = Category.requests(user);
            return ok(views.html.stock.perCategory.render(items, myRequested));
        }
        return ok(errorPage.render("user requested not found", null, !isAjax()));
    }


    public static Result sendRequestItem() {
        if (session(defaultSession) != null) {
            Map<String, String[]> map = request().body().asFormUrlEncoded();
            String[] checkedVal = map.get("itemID");
            List<StockMovement> items = new ArrayList<>();

            List<Item> itemList = new ArrayList<>();
            Employee employee = Ins(defaultSession).employee;

            if (employee == null) {
                return ok(errorPage.render("not employee for ILPD university", null, !isAjax()));
            }

            for (String one : checkedVal) {
                StockMovement movement = new StockMovement();
                Long itemId = Long.parseLong(one);
                Item item = Item.find(itemId);
                if (item == null) {
                    continue;
                }
                itemList.add(item);
            }
            return ok(views.html.stock.sendRequest.render(itemList, employee));
        }
        return ok();
    }

    public static Result saveRequest() {
        Form<StockMovement> form = Form.form(StockMovement.class).bindFromRequest();
        Long employeeId = Long.parseLong(form.field("employeeId").value());

        Map<String, String[]> map = request().body().asFormUrlEncoded();
        String[] suppliedIds = map.get("items[]");


        Employee employee = Employee.finder.where()
                .eq("deleteStatus", false)
                .eq("id", employeeId)
                .setMaxRows(1).findUnique();
        if (employee == null) {
            return notFound();
        }

        Ebean.beginTransaction();
        Request request = new Request();
        request.employee = employee;
        request.date = new Date();
        request.save();


        for (String item : suppliedIds) {
            Long itemId = Long.parseLong(item);
            Item item1 = Item.find(itemId);
            StockMovement stockMovement = new StockMovement();
            stockMovement.item = item1;
            stockMovement.request = request;
            stockMovement.proposedQty = Integer.parseInt(form.field("qty" + item).value());
            stockMovement.confirmedQty = 0;
            stockMovement.save();
        }
        Ebean.commitTransaction();
        return ok();
    }

    public static Result moduleMarks() {
        if (session("Instructor") != null && Ins("Instructor") != null) {

            Lecture lect = activeL(Ins("Instructor"));
            if (lect == null) {
                return ok("error");
            }
            return ok(views.html.lecture.moduleMarksPageOg.render(lect, lect.myTrainingsAll()));
        } else if (session("Coordinator") != null || session("mark_officer") != null) {
            return ok(views.html.lecture.moduleMarksPageOg.render(null, Training.all()));
        }
        return ok("0");
    }
    public static Result moduleMarksPrint() {
        if (session("Instructor") != null && Ins("Instructor") != null) {

            Lecture lect = activeL(Ins("Instructor"));
            if (lect == null) {
                return ok("error");
            }
            return ok(views.html.lecture.moduleMarksPageOg.render(lect, lect.myTrainingsAll()));
        } else if (session("Coordinator") != null || session("mark_officer") != null) {
            return ok(views.html.lecture.moduleMarksPageOg.render(null, Training.all()));
        }
        return ok("0");
    }

    private static void sendHOU(Employee employee) {
        int count = Request.finder.where()
                .eq("deleteStatus", false)
                .eq("status", "pending")
                .eq("headOfUnitStatus", false)
                .eq("employee.unit.id", employee.unit.id)
                .findRowCount();
        new Counts().sendEmail("HeadOfUnit", count);
    }

    private static int countAvailable(StockMovement movement) {
        String sqlQuery = "SELECT (items.supplied_qty-sum(stock_movement.confirmed_qty)) as counted from stock_movement,items where stock_movement.supplied_id=items.id and items.id=" + movement.item.id + "";
        SqlQuery query = Ebean.createSqlQuery(sqlQuery);
        return query.findUnique().getInteger("counted");
    }

    public static Result approveRequest(Long mvId, String comment) {
        StockMovement movement = StockMovement.find(mvId);
        if (movement == null) {
            return ok(errorPage.render("request not available", null, !isAjax()));
        }
        movement.logisticStatus = true;
        movement.logisticComment = comment;
        Integer value = countAvailable(movement);
        if (movement.proposedQty > value) {
            return ok("requested quantity is too much for stock available \"" + value + "\" and requested is \"" + movement.proposedQty + "\"");
        }
        movement.confirmedQty = movement.proposedQty;
        movement.update();
        return ok("1");
    }

    public static Result updateMovement(Long id) {
        return ok();
    }

    public static Result editNumber(Long moveId, Integer integer) {
        StockMovement movement = StockMovement.find(moveId);
        if (movement == null) {
            return ok(errorPage.render("request not available", null, !isAjax()));
        }
        movement.proposedQty = integer;
        if (movement.isEditable()) {
            movement.update();
        } else {
            return ok("not editable");
        }
        return ok("1");
    }

    public static Result updateFinal(Long id) {
        StockMovement movement = StockMovement.find(id);
        if (movement == null) {
            return ok(errorPage.render("request not available", null, !isAjax()));
        }
        movement.status = true;
        if (!movement.isEditable()) {
            movement.update();
        } else {
            return ok("not editable");
        }
        return ok("1");
    }


    public static Result moduleStudents(long trainingId, long lecturerId) {
        if (session("Instructor") != null || session("Coordinator") != null || session("mark_officer") != null || session("registrar") != null) {

            Training training = Training.find.byId(trainingId);
            Lecture lect = Lecture.finderById(lecturerId);

            if (lect == null || training == null) {
                return ok("error");
            }

            List<Module> moduleList = lect.myModulesWithPeriod(trainingId);
            return ok(views.html.lecture.moduleStudents.render(lect, moduleList, training));
        }
        return ok("0");
    }
    public static Result componentsResults() {
        if (session("student") != null) {
            Users user = Users.finderByMail(session("student"));
            Applicant applicant = user.amApplicant();
            Training training = applicant.student.training;
            Student student = applicant.student;
            List<Module> modules = null;
            if(student.status.equalsIgnoreCase("RETAKE-MODULES")){
                modules = student.myPreviousModules();
            }else {
                modules = student.myModules();
            }
            return ok(views.html.lecture.componentsResult.render(modules,training, student));
        }
        return ok("Student not found");
    }
    public static Result deleteAllUncompleted() {
        if (session("admin") != null) {
            List<Users> users = Users.finder.where("deleteStatus='false' and names='' and role='student' group by id").findList();
            int number = 0;
            return ok(views.html.admininistrator.deleteUncompletedUsers.render( users));
        }
        return ok("Users not found");
    }
    public static Result deleteUncompletedUsers(){
        Form<Users> form = Form.form(Users.class).bindFromRequest();
        Map<String, String[]> map = request().body().asFormUrlEncoded();
        String[] checkedVal = map.get("userId");
        for (String formString : checkedVal) {
            Long uId = Long.parseLong(formString);
            Users user = Users.finder.byId(uId);
            user.deleteStatus = true;
            user.update(uId);
        }
        return redirect("/%2Fadmin%2FupdateInfo#Ptab4");
    }
    public static Result componentsStudent(long mId, long sId, long tId) {
        if (session("student") != null) {
            Module module = Module.finderById(mId);
            Student student = Student.finderById(sId);
            Training training = Training.finderById(tId);
            List<SubMark> subMarkList = SubMark.allPerModule(mId, sId);
            return ok(views.html.lecture.componentsStudent.render(subMarkList, student, module, training));
        }
        return ok("Student not found");
    }
    public static Result moduleStudentsM(long moduleId, long lecturerId, long trainingId) {
        if (session("Instructor") != null || session("Coordinator") != null || session("mark_officer") != null || session("registrar") != null) {

            Training training = Training.finderById(trainingId);
            Lecture lecture = Lecture.finderById(lecturerId);
            Module mod = Module.finderById(moduleId);

            if (lecture == null || mod == null) {
                return ok("error");
            }
            List<Student> studentList = training.students();
            return ok(views.html.lecture.moduleStudentsM.render(training, studentList, mod));
        }
        return ok("0");
    }
    public static Result moduleReportMarks(Long tId, Long mId) {
        if (session("Instructor") != null || session("Coordinator") != null || session("mark_officer") != null || session("registrar") != null) {

            Training training = Training.finderById(tId);
            Module mod = Module.finderById(mId);
            List<Student> studentList = training.students();
            return ok(views.html.lecture.moduleStudentsMs.render(training, studentList, mod));
        }
        return ok("0");
    }
    public static Result scheduleByLecture() {
        if (session("Coordinator") != null ||session("mark_officer") != null || session("registrar") != null || session("DTR/Coordinator") != null) {
            Form<Lecture> form = Form.form(Lecture.class).bindFromRequest();
            Lecture lecture = form.get();
            Lecture lecture1 = Lecture.finderById(Long.parseLong(form.field("lectureId").value()));
            if (lecture1 != null) {
                List<Schedule> scheduleList = Schedule.find.where()
                        .eq("lecture", lecture1)
                        .eq("deleteStatus", false)
                        .orderBy("date desc")
                        .findList();

                return ok(views.html.lecture.scheduleLecture.render(scheduleList, lecture));
            } else {
                return ok("Lecturer not found");
            }
        }
        return ok("0");
    }

    public static Result getComponent(String types, Long secId) {
        switch (types) {
            case "1":
                Module module = Module.finderById(secId);
                List<Component> components = module.myComp();
                return ok(Json.toJson(components));
            default:
                return ok("1");
        }
    }
    public static Result removeDuplicate() {
            for(Applied app : Applied.all()){
                for(Applicant a : Applicant.all()){
                    for(Users u : Users.all()){
                        if(app.applicant.id == a.id && a.user.id == u.id && a.user.myApplication() > 1){
                            app.deleteStatus = true;
                            app.update();
                        }
                    }
            }
        }
        return ok("1");
    }
}
