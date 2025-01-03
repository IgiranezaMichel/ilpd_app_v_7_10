package controllers;
import models.*;
import play.data.Form;
import play.mvc.Result;
import views.html.errorPage;
import java.util.List;

public class CoordinatorController extends React
{
    public static Result assignmentPage()
    {
        if (session("Coordinator") != null ||session("mark_officer") != null || session("registrar") != null)
        {

            return ok(views.html.coordinator.assignmentPage.render());
        }
        else
        {
            return ok("error requested");
        }
    }
    public static Result courseMaterialPage()
    {
        if (session("Coordinator") != null || session("mark_officer") != null || session("registrar") != null)
        {

            return ok(views.html.coordinator.courseMaterialPage.render());
        }
        else
        {
            return ok("error requested");
        }
    }

    public static Result internships()
    {
        List<Internship> internships = Internship.finder.all();
        return ok(views.html.coordinator.internships.render(internships));
    }

    public static Result assignmentPages(String definer, String q)
    {
        if (definer.equals("add"))
        {
            return (!q.equals(Vld.key)) ? redirect(routes.CoordinatorController.viewLecturersTimetabled("NewAssignment")) : ok(AcademicYear.count());
        }
        if (definer.equals("courseMaterial"))
        {
            return (!q.equals(Vld.key)) ? redirect(routes.CoordinatorController.viewLecturersTimetabled("NewCourseMaterial")) : ok(AcademicYear.count());
        }
        else if (definer.equals("group"))
        {
            return (!q.equals(Vld.key)) ? redirect(routes.CoordinatorController.viewLecturersTimetabled("CreateGroups")) : ok(AcademicYear.count());
        }
        else
        {
            return ok();
        }
    }

    public static Result viewLecturersTimetabled(String nextAction)
    {
        String label = "";
        if (nextAction.equals("NewAssignment"))
        {
            label = " A page by which you can manage assignments.";
        }
        if (nextAction.equals("NewCourseMaterial"))
        {
            label = " A page by which you can manage course material.";
        }
        else if (nextAction.equals("CreateGroups"))
        {
            label = "Lecturer's module components.";
        }
        else if (nextAction.equals("assignmentMarks"))
        {
            label = "Record assignment marks.";
        }
        else if (nextAction.equals("changeAssignmentMarks"))
        {
            label = "Change assignment and research marks.";
        }
        else if (nextAction.equals("componentMarks"))
        {
            label = "Set component assignment marks.";
        }
        else if (nextAction.equals("IndividualAssignments"))
        {
            label = "Individual assignments submitted.";
        }
        else if (nextAction.equals("GroupAssignments"))
        {
            label = "Group assignments submitted.";
        }
        else if (nextAction.equals("SetModuleMarks"))
        {
            label = "Module marks.";
        }
        else if (nextAction.equals("SetComponentMarks"))
        {
            label = "Component marks.";
        }
        else if (nextAction.equals("fetchSetResitMarkPage"))
        {
            label = "Component re-sit marks.";
        }
        else if (nextAction.equals("componentMarksClaim"))
        {
            label = "Component marks claim";
        }
        else if (nextAction.equals("Attendance"))
        {
            label = "Attendance.";
        }
        else if (nextAction.equals("assignmentReport"))
        {
            label = "Marks by assignment.";
        }
        else
        {
            label = "more.";
        }

        boolean manager = isCleManager();
        String string = manager ? "Trainers" : "Lecturers";
        return ok(views.html.coordinator.selectLecturer.render(nextAction, label,manager,string));
    }

    public static Result loadForLecturer()
    {
        Form<Lecture> lectureForm = Form.form(Lecture.class).bindFromRequest();
        String id = lectureForm.field("lecturers").value();
        String nextAction = lectureForm.field("NextAction").value();
        long lecturerId = Long.parseLong(id);
        Lecture lecturer = Lecture.finderById(lecturerId);

        if (lecturer == null)
        {
            return ok("Error");
        }
        switch (nextAction) {
            case "NewAssignment":
                return ok(views.html.lecture.setAssignment.render(lecturer, lecturer.myComp()));
            case "NewCourseMaterial":
                List<CourseMaterial> courseMaterials = CourseMaterial.finder.where()
                        .eq("schedule.lecture.id", lecturer.id)
                        .eq("deleteStatus", false)
                        .orderBy("date desc")
                        .findList();
                return ok(views.html.lecture.NewCourseMaterial.render(courseMaterials, lecturer));
            case "CreateGroups":
                return ok(views.html.lecture.group.render(lecturer));
            case "componentMarks": {
                String userType = "";
                if(session().containsKey("Coordinator")) {
                     userType = "Coordinator";
                }
                if(session().containsKey("mark_officer")) {
                     userType = "mark_officer";
                }
                List<Component> myComp = lecturer.myComp();
                List<ComponentMax> list = ComponentMax.finder.where()
                        .eq("lecture.id", lecturerId).setMaxRows(10).findList();
                return ok(views.html.lecture.componentMax.render(lecturer, list, myComp, userType));
            }
            case "assignmentMarks": {
                List<Component> myComp = lecturer.myComp();
                return ok(views.html.lecture.viewAssignmentsMarks.render(lecturer, myComp, false));
            }
            case "changeAssignmentMarks": {
                List<Component> myComp = lecturer.myComp();
                return ok(views.html.lecture.changeAssignmentMarks.render(lecturer, myComp, false));
            }
            case "IndividualAssignments": {
                List<Component> myComp = lecturer.myComp();
                return ok(views.html.lecture.viewAssignments.render(lecturer, myComp, false));
            }
            case "GroupAssignments": {
                List<Component> myComp = lecturer.myComp();
                return ok(views.html.lecture.viewAssignments.render(lecturer, myComp, true));
            }
            case "SetModuleMarks":
                return ok(views.html.lecture.moduleMarksPage.render(lecturer, lecturer.myTrainingsAll()));
            case "SetComponentMarks":
                return ok(views.html.lecture.setMarks.render(lecturer.myTrainingsAll(), lecturer.id));
            case "fetchSetResitMarkPage":
                return ok(views.html.lecture.fetchSetResitMarkPage.render(lecturer.myTrainingsAll(), lecturer.id));
            case "componentMarksClaim":
                return ok(views.html.lecture.componentMarksClaim.render(lecturer.myTrainingsAll(), lecturer.id));
            case "Attendance":
                if(session("registrar") != null || session("Coordinator") != null || session("mark_officer") != null) {
                    return ok(views.html.lecture.attendancePage.render(lecturer, lecturer.myTrainingsAll(), errorPage.render("Choose training class to set attendance", null, false)));
                }
                if(session("DTR/Coordinator") != null) {
                    return ok(views.html.lecture.attendancePage.render(lecturer, lecturer.myTrainingsAllDtr(), errorPage.render("Choose training class to set attendance", null, false)));
                }
                case "assignmentReport":
                List<Assignment> assignments = Assignment.finder.where()
                        .eq("deleteStatus", false)
                        .eq("grouped", false)
                        .findList();
                List<Training> trainings = Training.allOpen();
                return ok(views.html.lecture.assignmentReport.render(assignments, trainings));
            default:
                return ok("error");
        }

    }

    public static Result getAssignmentUpdatePage(Long id)
    {
        if (session("Coordinator") != null || session("mark_officer") != null || session("registrar") != null)
        {
            Assignment assignment = Assignment.finderById(id);
            if (assignment == null) return ok(errorPage.render("Assignment not available", null, !isAjax()));
            return ok(views.html.coordinator.assignmentUpdate.render(assignment));
        }
        return ok();
    }
}
