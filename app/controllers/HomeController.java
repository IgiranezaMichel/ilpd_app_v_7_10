package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import models.*;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import scala.App;
import views.html.fix.training;

import java.util.List;

public class HomeController extends Controller {
    public static Result getTrainingView() {
        return ok(training.render(Training.all()));
    }

    public static Result resetStudentAccount() {
        Form<Training> form = Form.form(Training.class).bindFromRequest();
        long trainingId = Long.parseLong(form.field("training").value());
        Training training = Training.finderById(trainingId);
        if (training == null) return notFound();

        List<Applied> appliedList = training.applieds();
        resetStudentAccount(appliedList);
        return ok("Account successfully reset");
    }

    private static void resetStudentAccount(List<Applied> appliedList) {
        for (Applied applied : appliedList) {
            Counts.resetStudentAccount(applied);
        }
    }

}
