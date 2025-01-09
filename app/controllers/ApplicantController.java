package controllers;
import com.avaje.ebean.Expr;
import com.avaje.ebean.Expression;
import com.avaje.ebean.Page;
import models.*;
import models.Utility.Contracts;
import models.Utility.SendMailer;
import play.data.Form;
import play.mvc.Result;
import views.html.applicant._applicantPage;
import views.html.applicant.all;
import views.html.applicant.edit;
import views.html.error;
import views.html.register.applicants.details;

public class ApplicantController extends React {

    private static Expression withCLE(){
        return expr(true);
    }

    private static Expression withoutCLE(){
        return expr(false);
    }

    private static Expression expr(boolean b){
        return Expr.eq("applied.training.iMode.campusProgram.program.cle",b);
    }

    public static Result allApplicants(int page, String p) {

        Form<Applicant> form = Form.form(Applicant.class).bindFromRequest();
        String search = form.field("search").valueOr("");
        String size = form.field("pageSize").valueOr("20");
        int pageSize = Integer.parseInt(size);

        Expression expression = isCleManager() ? withCLE() : withoutCLE();

        Expression expr = Expr.and(expression,Expr.and(Expr.ne("applied.applicationStatus","derefered"),Expr.ne("applied.applicationStatus","rejected")));

        Contracts<Applicant> contracts = new Contracts<>(Applicant.class);
        Page<Applicant> applicantPage = contracts.setColumns("firstName", "familyName", "nationalDentity", "student.regNo","applied.applicationStatus").setPageExp(expr)
                .page(page, pageSize, search);

        if (p.equalsIgnoreCase("partial")) {
            return ok(_applicantPage.render(applicantPage));
        }
        return ok(all.render(applicantPage));
    }
    public static Result appProfile(Long id) {
        Form<Applicant> form = Form.form(Applicant.class).bindFromRequest();
        Applicant applicant = form.get();
        Applicant applicant1 = Applicant.finderById(id);
        applicant1.profile = uploadProfile(applicant.profile,applicant.profile);
        applicant1.update();
        return ok("1");
    }
    public static Result getApplicant(long id) {
        Applicant applicant = Applicant.finderById(id);
        if (applicant == null) return ok(error.render("Not found"));
        return ok(edit.render(applicant));
    }
    public static Result addDocuments(long id) {
        Applicant applicant = Applicant.finderById(id);
        if (applicant == null) return ok(error.render("Not found"));
        return ok(views.html.applicant.addDocuments.render(applicant));
    }

    public static Result applicantDetails(long id) {
        Applicant applicant = Applicant.finderById(id);
        if (applicant == null) return ok(error.render("Not found"));
        return ok(details.render(applicant,"Accepted"));
    }

    public static Result allApplicantsByPayment(int page, String p,String status ) {

        Form<Applicant> form = Form.form(Applicant.class).bindFromRequest();
        String search = form.field("search").valueOr("");
        String size = form.field("pageSize").valueOr("10");
        int pageSize = Integer.parseInt(size);

        String trainingId = form.field("training").value();
        if (trainingId == null){
            return ok(views.html.register.applicants.all.render(status,getTrainings()));
        }
        session("status",trainingId);

        Training training = Training.finderById(Long.parseLong(trainingId));
        Contracts<Applicant> contracts = new Contracts<>(Applicant.class);
        Page<Applicant> applicantPage = contracts
                .setColumns("firstName", "familyName", "nationalDentity")
                .pageByTraining(page, pageSize, search, training, status);


        if (p.equalsIgnoreCase("partial")) {
            return ok(views.html.register.applicants._allPage.render(applicantPage,status));
        }
        return ok(views.html.register.applicants.all.render(status,getTrainings()));
    }
    public static Result updateApplicant() {
        Form<Applicant> form = Form.form(Applicant.class).bindFromRequest();
        Applicant applicant1 = form.get();

        Applicant applicant = Applicant.finderById(Long.valueOf(form.field("id").value()));
        if (applicant == null) return notFound();

        String districts = form.field("districts.id").valueOr("");

        applicant.firstName = form.field("firstName").value();
        applicant.familyName = form.field("familyName").value();
        applicant.fatherName = form.field("fatherName").value();
        applicant.motherName = form.field("motherName").value();
        applicant.country = form.field("country").value();
        if (!districts.trim().equalsIgnoreCase("")) {
            applicant.districts = Districts.finderById(Long.valueOf(districts));
        }

        applicant.maritalStatus = form.field("maritalStatus").value();
        applicant.nationality = form.field("nationality").value();
        applicant.nationalDentity = form.field("nationalDentity").value();
        applicant.birthPlace = form.field("birthPlace").value();
        applicant.birthDate = form.field("birthDate").value();

        applicant.aFromu = Integer.parseInt(form.field("aFromu").valueOr("0"));
        applicant.aTo = Integer.parseInt(form.field("aTo").valueOr("0"));
        applicant.aSchool = form.field("aSchool").value();

        String value = form.field("feespayment").value();
        applicant.applied.feespayment = value;
        if (!value.equalsIgnoreCase("myself")) {
            applicant.applied.sponsorName = form.field("sponsorName").value();
            applicant.applied.sponsorEmail = form.field("sponsorEmail").value();
            applicant.applied.sponsorPhone = form.field("sponsorPhone").value();
        }
        applicant.applied.contactPerson = form.field("contactPerson").value();
        applicant.applied.contactEmail = form.field("contactEmail").value();
        applicant.applied.contactPhone = form.field("contactPhone").value();
        applicant.profile = uploadProfile(applicant1.profile,applicant1.profile);
        applicant.update();

        Applied applied = Applied.byApp(applicant.id);

                     for(AcademicFiles i : AcademicFiles.byApplicant(applicant.id)) {
                         if (Attachment.checker(applicant.id, i.id) != null) {
                                     Attachment n = Attachment.checker(applicant.id, i.id);
                                     if (n == null) {
                                         n = new Attachment();
                                     } else {
                                         Verification vf = n.amV();
                                         if (vf != null) {
                                             vf.deleteStatus = true;
                                             vf.update();
                                         }
                                     }
                                     n.attachName = uploadFile(n.attachName, i.uniqueName,"personal_and_academic_records/attachment/");
                                     n.app = applicant;
                                     n.file = i;
                                     n.save();
                         }
                     }

        return ok("Data successfully updated");
    }
    public static Result updateApplicant2(Long id) {
        Form<Applicant> form = Form.form(Applicant.class).bindFromRequest();
        Applicant applicant1 = form.get();
        Applicant applicant = Applicant.finderById(id);
        if (applicant == null) return notFound();
        String districts = form.field("districts.id").valueOr("");
        applicant.firstName = form.field("firstName").value();
        applicant.familyName = form.field("familyName").value();
        applicant.fatherName = form.field("fatherName").value();
        applicant.motherName = form.field("motherName").value();
        applicant.country = form.field("country").value();
        if (!districts.trim().equalsIgnoreCase("")) {
            applicant.districts = Districts.finderById(Long.valueOf(districts));
        }

        applicant.maritalStatus = form.field("maritalStatus").value();
        applicant.nationality = form.field("nationality").value();
        applicant.nationalDentity = form.field("nationalDentity").value();
        applicant.birthPlace = form.field("birthPlace").value();
        applicant.birthDate = form.field("birthDate").value();

        applicant.aFromu = Integer.parseInt(form.field("aFromu").valueOr("0"));
        applicant.aTo = Integer.parseInt(form.field("aTo").valueOr("0"));
        applicant.aSchool = form.field("aSchool").value();

        String value = form.field("feespayment").value();
        applicant.applied.feespayment = value;
        if (!value.equalsIgnoreCase("myself")) {
            applicant.applied.sponsorName = form.field("sponsorName").value();
            applicant.applied.sponsorEmail = form.field("sponsorEmail").value();
            applicant.applied.sponsorPhone = form.field("sponsorPhone").value();
        }
        applicant.applied.contactPerson = form.field("contactPerson").value();
        applicant.applied.contactEmail = form.field("contactEmail").value();
        applicant.applied.contactPhone = form.field("contactPhone").value();
        applicant.update();

      /*  Applied applied = Applied.byApp(applicant.id);
                     for(AcademicFiles i : AcademicFiles.byApplicant(applicant.id)) {
                         if (Attachment.checker(applicant.id, i.id) != null) {
                                     Attachment n = Attachment.checker(applicant.id, i.id);
                                     if (n == null) {
                                         n = new Attachment();
                                     } else {
                                         Verification vf = n.amV();
                                         if (vf != null) {
                                             vf.deleteStatus = true;
                                             vf.update();
                                         }
                                     }
                                     n.attachName = uploadFile(n.attachName, i.uniqueName);
                                     n.app = applicant;
                                     n.file = i;
                                     n.save();
                         }
                     } */

        return redirect("/applicants/"+id);
    }

}
