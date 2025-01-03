package controllers;

import models.ILDPLibrary;
import models.Student;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.library.libraryStatus;
import views.html.reports.libraryReport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LibraryController extends Controller {
    public static Result getLibraryStatus() {
        return ok(libraryStatus.render(ILDPLibrary.all()));
    }

    public static Result saveEntry() {
        Form<ILDPLibrary> libraryForm = Form.form(ILDPLibrary.class).bindFromRequest();
        String studentId = libraryForm.field("studentId").value();
        String bookTitle = libraryForm.field("bookTitle").value();
        String bookCode = libraryForm.field("bookCode").value();
        String bookCost = libraryForm.field("bookCost").value();
//        String comment=libraryForm.field("bookComment").value();
        String act = libraryForm.field("book").value();

        if (studentId.trim() != "" && !Objects.equals(bookTitle.trim(), "") && bookCode.trim() != "" && bookCost.trim() != "") {
            Student student = Student.finderById(Long.parseLong(studentId));
            if (student != null) {
                ILDPLibrary library = new ILDPLibrary();
                library.student = student;
                library.bookName = bookTitle;
                library.bookCost = Float.parseFloat(bookCost);
                library.bookNumber = bookCode;
//                library.comment=comment;
                library.act = act;
                library.save();
                return ok("1");
            }
        }

        return ok("Please fill all fields!");
    }

    public static Result findStudent(String regno) {
        if (!regno.trim().equals("")) {
            Student student = Student.findByRegNo(regno);
            if (student != null) {
                long studentId = student.id;
                String names = student.firstName + " " + student.familyName;
                String program = student.training.iMode.campusProgram.program.programName;
                String json = "{";
                json += " \"id\": " + studentId + ",";
                json += " \"names\":\"" + names + "\",";
                json += " \"program\":\"" + program + "\"";
                json += "}";
                return ok(Json.parse(json));
            }
        }
        return notFound();
    }

    public static Result postFindStudent() {
        Form<ILDPLibrary> form = Form.form(ILDPLibrary.class).bindFromRequest();
        String regno=form.field("regNo").valueOr("");
        if (regno.trim().equals("")) {
            return notFound();
        }

        Student student = Student.findByRegNo(regno);
        if (student == null) {
            return notFound();
        }

        long studentId = student.id;
        String names = student.firstName + " " + student.familyName;
        String program = student.training.iMode.campusProgram.program.programName;
        String json = "{";
        json += " \"id\": " + studentId + ",";
        json += " \"names\":\"" + names + "\",";
        json += " \"program\":\"" + program + "\"";
        json += "}";
        return ok(Json.parse(json));
    }

    public static Result findLibraryEntry(String regno, String bookCode) {
        if (!regno.trim().equals("") && !bookCode.trim().equals("")) {
            ILDPLibrary entry = ILDPLibrary.finder.where().eq("deleteStatus", false).eq("clear", false).eq("student.regNo", regno).eq("bookNumber", bookCode).setMaxRows(1).findUnique();
            if (entry != null) {
                float bookCost = entry.bookCost;
                String names = entry.student.firstName + " " + entry.student.familyName;
                String bookTitle = entry.bookName;
                String json = "{";
                json += " \"id\":" + entry.id + ",";
                json += " \"names\":\"" + names + "\",";
                json += " \"bookCost\":" + bookCost + ",";
                json += " \"bookTitle\":\"" + bookTitle + "\"";
                json += "}";
                return ok(Json.parse(json));
            }
        }
        return notFound();
    }

    public static Result clearStudent() {
        Form<ILDPLibrary> libraryForm = Form.form(ILDPLibrary.class).bindFromRequest();
        String libraryId = libraryForm.field("libraryId").value();
        if (libraryId.trim() != "") {
            ILDPLibrary library = ILDPLibrary.finderById(Long.parseLong(libraryId));
            if (library != null) {
                library.clear = true;
                library.update();
                return ok("1");
            }
        }
        return ok("Entry not found!");

    }

    public static Result libraryReport() {
        return ok(libraryReport.render());
    }

    public static Result getStudentReport(String regno, Integer lost) {

        if (!Objects.equals(regno.trim(), "")) {
            List<ILDPLibrary> libraryList = null;
            if (lost == 1) {
                libraryList = ILDPLibrary.finder.where().eq("deleteStatus", false).eq("clear", false).eq("student.regNo", regno).eq("act", "Lost").findList();
            } else if (lost == 0) {
                libraryList = ILDPLibrary.finder.where().eq("deleteStatus", false).eq("clear", false).eq("student.regNo", regno).eq("act", "Late return").findList();
            }

            if (libraryList != null && libraryList.size() > 0) {
                StringBuilder json = new StringBuilder("[");
                for (ILDPLibrary entry : libraryList) {
                    if (json.charAt(json.length() - 1) == '}') {
                        json.append(",{");
                    } else {
                        json.append("{");
                    }
                    json.append(" \"bookTitle\":\"").append(entry.bookName).append("\",");
                    json.append(" \"bookCode\":\"").append(entry.bookNumber).append("\",");
                    json.append(" \"bookCost\":").append(entry.bookCost);
                    json.append("}");
                }
                json.append("]");
                return ok(Json.parse(json.toString()));
            } else {
                Map<String, String> map = new HashMap<>();
                map.put("code", "404");
                return notFound(Json.toJson(map));
            }
        }
        Map<String, String> map = new HashMap<>();
        map.put("code", "400");
        return badRequest(Json.toJson(map));
    }
}
