package controllers;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;
import models.*;
import play.data.Form;
import play.libs.Json;
import play.mvc.Call;
import play.mvc.Result;
import play.mvc.Security;
import views.html.viewError;
import java.util.Date;
import java.util.List;


public class LectureController extends React {

    public static Lecture activeL() {
        Users u = Application.Ins("Instructor");
        return u.activeL();
    }

    public static Result viewSubmitted(Long sId) {
        Submission sub = Submission.finderById(sId);
        if (sub == null) return ok("no submission found");
        String ext = Vld.getExt(sub.attachment);
        Call rout = routes.Assets.at(Vld.imagePro(sub.attachment));
        if (Vld.checkValid(ext)) {
            return ok(views.html.lecture.viewFullSubmission.render(sub, ext, rout, views.html.lecture.saveCompMarks.render(sub)));
        } else {
            return ok(viewError.render(views.html.lecture.saveCompMarks.render(sub), false));
        }
    }
    public static Result addMarks(Long sId) {
        Submission sub = Submission.finderById(sId);
        if (sub == null) return ok("Submission not found");
        String ext = Vld.getExt(sub.attachment);
        Call rout = routes.Assets.at(Vld.imagePro(sub.attachment));
        if (Vld.checkValid(ext)) {
            return ok(views.html.lecture.viewFullSubmission.render(sub, ext, rout, views.html.lecture.saveCompMarks.render(sub)));
        } else {
            return ok(viewError.render(views.html.lecture.saveCompMarks.render(sub), false));
        }
    }
    public static Result changeMarks(Long sId) {
        Submission sub = Submission.finderById(sId);
        if (sub == null) return ok("Submission not found");
        String ext = Vld.getExt(sub.attachment);
        Call rout = routes.Assets.at(Vld.imagePro(sub.attachment));
        if (Vld.checkValid(ext)) {
            return ok(views.html.lecture.viewFullMarksCorrection.render(sub, ext, rout, views.html.lecture.changeMarks.render(sub)));
        } else {
            return ok(viewError.render(views.html.lecture.changeMarks.render(sub), false));
        }
    }
    public static Result addMarksGroup(Long sId) {
        GroupSubmission sub = GroupSubmission.finder.byId(sId);
        if (sub == null) return ok("No group submission found");
        String ext = Vld.getExt(sub.attachment);
        Call rout = routes.Assets.at(Vld.imagePro(sub.attachment));
        if (Vld.checkValid(ext)) {
            return ok(views.html.lecture.viewFullSubmissionGroup.render(sub, ext, rout, views.html.lecture.saveCompMarksGroupView.render(sub)));
        } else {
            return ok(viewError.render(views.html.lecture.saveCompMarksGroupView.render(sub), false));
        }
    }

    public static Result addSameMarksGroup(Long aId) {
        Assignment assignment = Assignment.finderById(aId);
            return ok(views.html.lecture.viewFullAllSubmissionGroup.render(views.html.lecture.saveCompMarksAllGroupView.render(assignment)));
    }

    public static Result cancelGroupSubmission(Long aId) {
        Assignment assignment = Assignment.finderById(aId);
            return ok(views.html.lecture.cancelGroupSubmission.render(views.html.lecture.cancelGroupSubmissionView.render(assignment)));
    }

    public static Result assignmentPages(String definer, String q) {

        Lecture lect = activeL();

        switch (definer) {
            case "add":
                return (!q.equals(Vld.key)) ? ok(views.html.lecture.setAssignment.render(lect, lect.myComp())) : ok(AcademicYear.count());
            case "group":
                return (!q.equals(Vld.key)) ? ok(views.html.lecture.group.render(lect)) : ok(AcademicYear.count());
            default:
                return ok();
        }
    }

    public static Result getGroupsForAssignment(long componentId) {
        String s = "SELECT DISTINCT g.id,g.group_name FROM groups_student g INNER JOIN component c ON g.component_id=c.id WHERE c.id= "+componentId+" ";
        List<SqlRow> sqlRows = Ebean.createSqlQuery(s)
                .findList();
        return ok(Json.toJson(sqlRows));
    }

    public static Result saveCourseMaterial() {
        Form<CourseMaterial> form = Form.form(CourseMaterial.class).bindFromRequest();
        if (form.hasErrors()) return ok(form.errorsAsJson());
        CourseMaterial material = new CourseMaterial();
        material.name = form.field("name").value();
        if(session("Instructor") != null || session("Coordinator") != null || session("mark_officer") != null || session("registrar") != null){
            material.schedule = Schedule.finderById(Long.parseLong(form.field("scheduleId").value()));
        }else {
            Training training = Training.finderById(Long.parseLong(form.field("iMode").value()));
            List<Schedule> schedule = Schedule.byTraining(training.id);
            for(Schedule s : schedule) {
                material.schedule = Schedule.finderById(s.id);
            }
        }
        material.fileName = uploadFile(new Date().toString(), "fileName","class_files/course/material/");
        material.save();
        return ok("1");
    }

    public static Result uploadAcademicFiles() {
        Form<Attachment> form = Form.form(Attachment.class).bindFromRequest();
        if (form.hasErrors()) return ok(form.errorsAsJson());
        Attachment attachment = new Attachment();

        Long apId = Long.parseLong(form.field("appId").value());
        Long dId = Long.parseLong(form.field("docId").value());
        AcademicFiles academicFile = AcademicFiles.finderById(dId);
        Applicant applicant = Applicant.finderById(apId);
        Attachment attachment1 = Attachment.checker(applicant.id, dId);
        if (attachment1 == null) {
            attachment1 = new Attachment();
        } else {
            Verification vf = attachment1.amV();
            if (vf != null) {
                vf.deleteStatus = true;
                vf.update();
            }
        }
        attachment1.attachName = uploadFile(attachment1.attachName, academicFile.uniqueName,"person_and_academic_records/attachment");
        attachment1.app = applicant;
        attachment1.file = academicFile;
        attachment1.save();
        return ok("1");
    }

    public static Result componentMax(String userType) {
        Lecture lect = activeL();
        List<Component> components = lect.myComp();
        List<ComponentMax> list = ComponentMax.finder.where()
                .eq("lecture.id", lect.id).setMaxRows(10).findList();
        return ok(views.html.lecture.componentMax.render(lect, list, components, userType));
    }

    public static Result updateComponentMax() {
        if (session("Instructor") != null || session("Coordinator") != null || session("mark_officer") != null || session("registrar") != null) {
            Form<ComponentMax> form = Form.form(ComponentMax.class).bindFromRequest();
            if (form.hasErrors()) return ok(form.errorsAsJson());
            ComponentMax componentMax = form.get();
            Lecture lecture = Lecture.finderById(Long.parseLong(form.field("lecturer").value()));
            Component component = Component.finderById(Long.parseLong(form.field("componentId").value()));
            Training training = Training.finderById(Long.parseLong(form.field("iMode").value()));
            ComponentMax componentMax1 = new ComponentMax();
            componentMax1.lecture = lecture;
            componentMax1.component = component;
            if(componentMax.researchMax > 0 ) {
                componentMax1.researchMax = componentMax.researchMax;
            }else{
                componentMax1.researchMax = 0;
            }
            componentMax1.examMax = componentMax.examMax;
            componentMax1.resitMax = componentMax.resitMax;
            componentMax1.resitResearchMax = componentMax.resitResearchMax;
            componentMax1.training = training;
            componentMax1.save();

            return ok("1");
        }
            return ok("0");
    }
}
