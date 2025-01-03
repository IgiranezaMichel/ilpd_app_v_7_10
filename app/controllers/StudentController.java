package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import models.*;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import views.html.student.evaluation;
import views.html.student.internship;

import java.util.*;

import static play.libs.Json.toJson;

public class StudentController extends React {
    public static Result selectPeriod() {
        return ok(views.html.register.selectTraining.render(Training.all()));
    }

    public static Result selectedPeriod() {
        Form<Training> form = Form.form(Training.class).bindFromRequest();

        Training training = Training.finderById(Long.parseLong(form.field("training").value()));
        if (training == null) {
            return notFound();
        }
        return ok(views.html.register.manageStudents.render(training));
    }

    public static Result studentByTraining(long id) {
        Training training = Training.finderById(id);
        if (training == null) return notFound();
        List<Student> students = Student.finder.where().eq("deleteStatus", false).eq("training.id", training.id).findList();
        return ok(toJson(students));

    }

    public static Result changeStudentTraining(long studentId, long trainingId) {
        Transaction txn= Ebean.beginTransaction();
        try {
            Training training = Training.finderById(trainingId);
            if (training == null) return ok("Invalid period");
            Student student = Student.finderById(studentId);
            if (student == null) return ok("Student not found");
            student.training = training;
            student.update();

            Applied applied = Applied.getApplied(student.applicant.user);
            if (applied != null) {
                applied.training = training;
                applied.update();
                Counts.resetStudentAccount(applied);
                txn.commit();
                return ok("1");
            }
        } finally {
            txn.end();
        }
        return ok("Reference chain error!");
    }





    public static Result saveInternship() {
        Form<Internship> form = Form.form(Internship.class).bindFromRequest();
        if (form.hasErrors()) return badRequest(form.errorsAsJson());
        Users user = Users.finderByMail(session("student"));
        Student student = user.me();

        Internship internship = new Internship();
        internship.description = form.field("description").value();
        internship.student = student;
        internship.attachment = uploadFile(new Date().toString(), "attachment");
        internship.save();
        return ok("1");
    }

    public static Result myInternships() {
        Users user = Users.finderByMail(session("student"));
        Student student = user.me();

        List<Internship> internships = Internship.finder.where()
                .eq("student.id", student.id)
                .findList();
        return ok(internship.render(internships));
    }
    public static Result evaluation() {
        Student student = getStudent();
        if( student == null ) return ok("error");
        List<Schedule> schedules = student.myLectures();
        return ok(evaluation.render(schedules, student));
    }

    public static Result studentAttendance() {
        Student student = getStudent();
        if( student == null ) return ok("error");
        List<Attendance> attendances = Attendance.find.where()
                .eq("student.id", student.id)
                .eq("student.training.transcript", true)
                .eq("student.deleteStatus", false)
                .eq("deleteStatus", false)
                .findList();
        return ok(views.html.student.studentAttendance.render(attendances, student));
    }

    public static Result evaluate(long id){
        Schedule schedule = Schedule.finderById(id);

        if( schedule == null ) return unauthorized();

        return ok(views.html.student.evaluationPage.render(EvCategory.finder.all(),schedule));
    }

    public static Result postEvaluate(long id){
        Schedule schedule = Schedule.finderById(id);

        Student student = getStudent();

        if( schedule == null || student == null ) return unauthorized();

        Http.RequestBody body = request().body();

        if( body == null ) return unauthorized();

        Map<String, String[]> map = body.asFormUrlEncoded();

        Set<String> stringSet = map.keySet();

        System.out.println("...................-------------");

        for (String string : stringSet){

            for (String s : map.get(string) ){

                if( !isNumeric(string) ) continue;

                long aLong = Long.parseLong(string);
                long l = getLong(s);
                EvQuestion question = EvQuestion.finder.byId(aLong);

                if( question == null ) continue;

                Evaluation evaluation = new Evaluation();
                evaluation.question = question;
                evaluation.schedule = schedule;
                evaluation.student = student;
                evaluation.mark = (float) l;
                evaluation.answer = s;

                if( evaluation.exist() ) continue;

                evaluation.save();

            }

        }

        return ok("1");
    }

    public static Result submitEvaluation() {
        Users user = Users.finderByMail(session("student"));
        Student student = user.me();

        Form<Evaluation> form = Form.form(Evaluation.class).bindFromRequest();

        Lecture lecture = Lecture.finderById(Long.parseLong(form.field("lecture").value()));
        if (lecture == null) return notFound();
        Evaluation evaluation = Evaluation.finder.where()
                .eq("student.id", student.id)
                .eq("lecture.id", lecture.id)
                .findUnique();

        if (evaluation == null) {
            Evaluation ev = new Evaluation();
            ev.student = student;
            ev.mark = Float.parseFloat(form.field("mark").value());
            ev.save();
            return ok(Json.toJson(ev));
        }
        Map<String, String> map = new HashMap<>();
        map.put("message", "You've already evaluate this lecturer.");
        return badRequest(Json.toJson(map));
    }
}
