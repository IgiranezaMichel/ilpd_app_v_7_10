package controllers;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import models.*;
import models.stock.*;
import org.mindrot.jbcrypt.BCrypt;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static controllers.Application.Ins;
public class UpdateForm extends React
{
    public static Result updateYear(long id)
    {
        if (session("admin") != null)
        {
            Form<AcademicYear> acc = Form.form(AcademicYear.class).bindFromRequest();
            AcademicYear acx = acc.get();
            if (acx.yearName.trim().isEmpty())
            {
                return ok("Year name is required");
            }
            AcademicYear uAcc = AcademicYear.finderById(id);
            uAcc.yearName = acx.yearName.trim();

            uAcc.deleteStatus = false;
            uAcc.update();

            return ok("1");

        }
        else
        {
            return ok("login error");
        }
    }
    public static Result returnAssignment(long id)
    {
            Form<Submission> form = Form.form(Submission.class).bindFromRequest();
            Submission submission = form.get();
            Submission submission1 = Submission.finderById(id);
            submission1.status = "pending";
            submission1.update();
            return ok("1");
    }
    public static Result updateApp(long id)
    {
        if (session("admin") != null)
        {
            Form<Applied> acc = Form.form(Applied.class).bindFromRequest();
            Applied acx = acc.get();
            Applied applied = Applied.finderById(id);
            List<Applicant> applicantList = Applicant.finder.setDistinct(true)
                    .where()
                    .eq("applied.id", applied.id)
                    .orderBy("id asc")
                    .findList();
            applied.deleteStatus = true;
            applied.update();
            for(Applicant a : applicantList){
                if(a.id == applied.applicant.id){
                    a.deleteStatus = true;
                    a.active = false;
                    a.update();
                }
            }
            return ok("1");
        }
        else
        {
            return ok("login error");
        }
    }
    public static Result deleteApplicantUnc(long id)
    {
        if (session("admin") != null)
        {
            Form<Applied> acc = Form.form(Applied.class).bindFromRequest();
            Applied acx = acc.get();
            Applied applicant = Applied.finderById(id);
            applicant.deleteStatus = true;
            applicant.update();
            Applicant applicant1 =  Applicant.finderById(applicant.applicant.id);
            applicant1.deleteStatus = true;
            applicant1.update();
            Users user = Users.finderById(applicant1.user.id);
            user.deleteStatus = true;
            user.update();
            return ok("1");
        }
        else
        {
            return ok("login error");
        }
    }
    public static Result deleteApplicantUncAll()
    {
        if (session("admin") != null)
        {
            Form<Applied> acc = Form.form(Applied.class).bindFromRequest();
            Applied acx = acc.get();
            List<Applied> applicants = Applied.allUncompleted();
            for(Applied app : applicants){
                    app.deleteStatus = true;
                    app.update();
                    Applicant applicant = Applicant.finderById(app.applicant.id);
                    applicant.deleteStatus = true;
                    applicant.update();
                    Users user = app.applicant.user;
                    user.deleteStatus = true;
                    user.update();
            }
            return ok("1");
        }
        else
        {
            return ok("login error");
        }
    }
    public static Result updateAppSucc(long id)
    {
        if (session("admin") != null)
        {
            Form<Applied> acc = Form.form(Applied.class).bindFromRequest();
            Applied acx = acc.get();
            Applied applied = Applied.finderById(id);
            applied.deleteStatus = false;
            applied.update();
            List<Applicant> applicantList = Applicant.finder.setDistinct(true)
                    .where()
                    .eq("applied.id", applied.id)
                    .orderBy("id asc")
                    .findList();
            for(Applicant a : applicantList){
                if(a.id == applied.applicant.id){
                    a.deleteStatus = false;
                    a.active = true;
                    a.update();
                }
            }
            return ok("1");
        }
        else
        {
            return ok("login error");
        }
    }

    public static Result marksClaim(long id)
    {
        if (session("student") != null)
        {
            Form<SubMark> form = Form.form(SubMark.class).bindFromRequest();
            if( form.hasErrors() ) return ok(form.errorsAsJson());
            SubMark subMark = form.get();
            SubMark subMark1 = SubMark.find.byId(id);
            subMark1.comment = subMark.comment;
            subMark1.student = Ins("student").me();
            subMark1.claimExam = Float.parseFloat(form.field("claimExam").value());
            subMark1.deleteStatus = false;
            subMark1.update(id);
            return ok("1");
        }
        else
        {
            return ok("login error");
        }
    }
    public static Result marksClaimApproval(long id)
    {
            Form<SubMark> form = Form.form(SubMark.class).bindFromRequest();
            if( form.hasErrors() ) return ok(form.errorsAsJson());
            SubMark subMark = form.get();
            SubMark subMark1 = SubMark.find.byId(id);
            subMark1.comment = subMark.comment;
            subMark1.claimExam = Float.parseFloat(form.field("claimExam").value());
            subMark1.claimRe = 0;
            subMark1.claimExam = 0;
            subMark1.deleteStatus = false;
            subMark1.update(id);
            return ok("1");
    }
    public static Result marksClaimApprovalDenial(long id)
    {
            Form<SubMark> form = Form.form(SubMark.class).bindFromRequest();
            if( form.hasErrors() ) return ok(form.errorsAsJson());
            SubMark subMark = form.get();
            SubMark subMark1 = SubMark.find.byId(id);
            subMark1.claimRe = 0;
            subMark1.claimExam = 0;
            subMark1.deleteStatus = false;
            subMark1.update(id);
            return ok("1");
    }
    public static Result removeDepr(long id)
    {
        if (session("Logistic") != null)
        {
            Form<Item> form = Form.form(Item.class).bindFromRequest();
            if( form.hasErrors() ) return ok(form.errorsAsJson());
            Item item = form.get();
            Item item1 = Item.finder.byId(id);
            item1.removed = "pending";
            int quantity = Integer.parseInt(form.field("quantity").value());
            item1.update();
            Supplied supplied = new Supplied();
            supplied.suppliedQty = quantity;
            long sId = 14;
            supplied.supplier = Supplier.finder.byId(sId);
            supplied.aquistionDate = new Date();
            supplied.expirationDate = new Date();
            supplied.item = item1;
            supplied.save();
            return ok("1");
        }
        else{
            return ok("login error");
        }
    }

    public static Result removeDefec(long id)
    {
        if (session("Logistic") != null)
        {
            Form<Item> form = Form.form(Item.class).bindFromRequest();
            if( form.hasErrors() ) return ok(form.errorsAsJson());
            Item item = form.get();
            int defQnty = Integer.parseInt(form.field("quantity").value());
            Item item1 = Item.finder.byId(id);
            item1.removed = "pending";
            if(defQnty >= item1.remainQty()){
                return ok("Expired quantity cannot be greater than remain!!");
            }
            item1.defQnty = defQnty;
            item1.status = "defective";
            item1.update();
            return ok("1");
        }
        else{
            return ok("login error");
        }
    }
    public static Result removeDeprApprove(long id)
    {
        if (session("Manager") != null)
        {
            Form<Item> form = Form.form(Item.class).bindFromRequest();
            if( form.hasErrors() ) return ok(form.errorsAsJson());
            Item item = form.get();
            Item item1 = Item.finder.byId(id);
            item1.removed = "approved";
            item1.update();
            return ok("1");
        }
        else{
            return ok("User not allowed");
        }
    }
    public static Result removeDefecApprove(long id)
    {
        if (session("Manager") != null)
        {
            Form<Item> form = Form.form(Item.class).bindFromRequest();
            if( form.hasErrors() ) return ok(form.errorsAsJson());
            Item item = form.get();
            int defQnty = Integer.parseInt(form.field("quantity").value());
            Item item1 = Item.finder.byId(id);
            item1.removed = "approved";
            item1.status = "good";
            item1.defQnty = 0;
            item1.update();
            long empId = 0;
            List<Employee> em =  Employee.all();
            for(Employee e : em){
                empId = e.id;
            }
            Request request = new Request();
            request.employee = Employee.finder.byId(empId);
            request.save();
            StockMovement stockMovement = new StockMovement();
            stockMovement.confirmedQty = defQnty;
            stockMovement.item = item1;
            stockMovement.request = request;
            if(defQnty > item1.remainQty()){
                return ok("Defective quantity cannot be greater than remain quantity");
            }
            stockMovement.save();
            return ok("1");
        }
        else{
            return ok("User not allowed");
        }
    }
    public static Result marksClaimAss(long id)
    {
        if (session("student") != null)
        {
            Form<AssignmentResult> form = Form.form(AssignmentResult.class).bindFromRequest();
            if( form.hasErrors() ) return ok(form.errorsAsJson());
            AssignmentResult mResult = form.get();
            AssignmentResult mResult1 = AssignmentResult.find.byId(id);
            mResult1.cliamResult = mResult.cliamResult;
            mResult1.comment = mResult.comment;
            //if(mResult.cliamResult > mResult1.assignment.max ) return ok("The maximum marks is "+mResult1.assignment.max );
            mResult1.update(id);
            return ok("1");
        }
        else
        {
            return ok("login error");
        }
    }

    public static Result marksClaimAssApproval(long id)
    {
        if (session("registrar") != null || session("Coordinator") != null || session("mark_officer") != null || session("Instructor") != null || session("DTR/Coordinator") != null)
        {

            Form<AssignmentResult> form = Form.form(AssignmentResult.class).bindFromRequest();
            if( form.hasErrors() ) return ok(form.errorsAsJson());
            AssignmentResult mResult = form.get();
            AssignmentResult mResult1 = AssignmentResult.find.byId(id);
            mResult1.cliamResult = mResult.cliamResult;
            mResult1.comment = mResult.comment;
            mResult1.result = mResult.result;
            mResult1.commentLecrurer = mResult.commentLecrurer;
            //if(mResult.cliamResult > mResult1.assignment.max ) return ok("The maximum marks allowed is "+mResult1.assignment.max );
            mResult1.update(id);
            return ok("1");
        }
        else
        {
            return ok("login error");
        }
    }

    public static Result updateEvCat(long id){
        EvCategory category = EvCategory.finder.byId(id);

        if( category == null ) return unauthorized();

        category.content = Form.form(EvCategory.class).bindFromRequest().get().content;

        category.update();

        return ok("1");
    }

    public static Result updateEvQuestion(long id){
        EvQuestion question = EvQuestion.finder.byId(id);

        if( question == null ) return unauthorized();

        EvQuestion form = Form.form(EvQuestion.class).bindFromRequest().get();

        question.question = form.question;
        question.category = EvCategory.finder.byId(form.category.id);

        question.update();

        return ok("1");
    }

    public static Result updateAnnouncement(Long id)
    {
        if (session("student") != null)
        {
            return ok("unauthorized");
        }
        Form<Announce> t = Form.form(Announce.class).bindFromRequest();
        Announce ann = t.get();

        Announce n = Announce.finderById(id);
        n.content = ann.content;
        n.title = ann.title;
        n.date = new Date();
        try {
            n.pubDate = new SimpleDateFormat("yyyy-MM-dd").parse(t.field("pubDate").value());
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
        n.type = ann.type;
        n.status = "active";
        n.update();
        return ok("1");
    }

    public static Result updateProductChangeImage(Long id){
        Form<Announce> form = Form.form(Announce.class).bindFromRequest();
        if( form.hasErrors() ) return ok(form.errorsAsJson());
        Announce announce = form.get();
        Announce announce1 = Announce.finderById(id);
        announce1.attachment = uploadFile(new Date().toString(), "attachment","announcement/attachments/");
        announce1.update();
        return redirect("/%2Fregister%2FgetAnnounceMentPage%2F#");
    }
    public static Result updateLibrary(long id)
    {
        Form<ILDPLibrary> libraryForm = Form.form(ILDPLibrary.class).bindFromRequest();
        String bookTitle = libraryForm.field("bookTitle").value();
        String bookCode = libraryForm.field("bookCode").value();
        String bookCost = libraryForm.field("bookCost").value();
//        String comment = libraryForm.field("bookComment").value();

        if (!Objects.equals(bookTitle.trim(), "") && !Objects.equals(bookCode.trim(), "") && !Objects.equals(bookCost.trim(), ""))
        {
            ILDPLibrary library = ILDPLibrary.finderById(id);
            if (library != null)
            {
                library.bookName = bookTitle;
                try
                {
                    library.bookCost = Float.parseFloat(bookCost);
                }
                catch (Exception e)
                {
                    return ok("Invalid cost!");
                }

                library.bookNumber = bookCode;
//                library.comment=comment;
                library.update();
                return ok("1");
            }
        }
        return ok("Fill al fields");
    }

    public static Result updateIntake(long id)
    {
        if (session("admin") != null)
        {
            Form<Intake> acc = Form.form(Intake.class).bindFromRequest();

            Intake acx = acc.get();

            Intake uAcc = Intake.finderById(id);
            uAcc.intakeName = acx.intakeName;

            String fileder = acc.field("intakeYear").value();
            if (fileder != null && Vld.isNumeric(fileder))
            {
                Long yId = Long.parseLong(fileder);
                uAcc.year = AcademicYear.finderById(yId);
            }
            else
            {
                uAcc.year = null;
            }

            uAcc.registrationFees = acx.registrationFees;
            uAcc.update();

            return ok("1");

        }
        else
        {
            return ok("login error");
        }
    }

    public static Result updateModule(long id)
    {
        if (session("admin") != null || session("registrar") != null || session("VRAC") != null)
        {
            Form<Module> moduleForm = Form.form(Module.class).bindFromRequest();
            Module module = moduleForm.get();
            Module module1 = Module.finderById(id);
            module1.moduleName = module.moduleName;
            module1.moduleCode = module.moduleCode;
            module1.orderNumber = module.orderNumber;
            module1.credits = module.credits;
            module1.reMax = module.reMax;
            module1.reseatResearchMax = module.reseatResearchMax;
            module1.catMax = module.catMax;
            module1.examMax = module.examMax;
            module1.minMarks = module.minMarks;
            module1.resitAmount = module.resitAmount;
            module1.retakeAmount = module.retakeAmount;
            module1.hasComponent = module.hasComponent;
            Long lectId = Long.parseLong(moduleForm.field("lecting").value());
            module1.lecture = Lecture.finderById(lectId);
            Long fac = Long.parseLong(moduleForm.field("prog").value());
            module1.program = Program.finderById(fac);
            String moduleInternship = moduleForm.field("moduleInternship").value();
            module1.moduleInternship = moduleInternship;
            module1.update();

            if (module.hasComponent)
            {

                Component c = new Component();

                c.compName = module1.moduleName;
                c.credits = module1.credits;
                c.module = module1;
                c.code = module1.moduleCode;


                if (c.notExist()) c.save();
            }

            return ok("1");
        }
        else
        {
            return ok("update error");
        }
    }
    public static Result appealExemption(long id)
    {
        if (session("student") != null ) {
            Form<InternshipAppeal> form = Form.form(InternshipAppeal.class).bindFromRequest();
            InternshipAppeal appeal = form.get();
            Applicant applicant = Applicant.finderById(id);
            InternshipAppeal appeal1 = new InternshipAppeal();
            appeal1.reason = appeal.reason;
            appeal1.appType = appeal.appType;
            appeal1.attachment = uploadFile(appeal.attachment, "attachment","class_files/internship/appeal/");
            appeal1.applicant = applicant;
            appeal1.save();
            return ok("1");
        }
        else
        {
            return ok("update error");
        }
    }
    public static Result appealDoingInternship(long id)
    {
        if (session("student") != null ) {
            Form<InternshipAppeal> form = Form.form(InternshipAppeal.class).bindFromRequest();
            InternshipAppeal appeal = form.get();
            Applicant applicant = Applicant.finderById(id);
            InternshipAppeal appeal1 = new InternshipAppeal();
            appeal1.reason = appeal.reason;
            appeal1.appType = appeal.appType;
            appeal1.attachment = uploadFile(appeal.attachment, "attachment","class_files/internship/appeal/");
            appeal1.applicant = applicant;
            appeal1.save();
            return ok("1");
        }
        else
        {
            return ok("update error");
        }
    }

    public static Result updateLecture(long id)
    {
        if (session("admin") != null || session("registrar") != null || session("VRAC") != null)
        {
            Form<Lecture> lectureForm = Form.form(Lecture.class).bindFromRequest();
            Lecture lecture = lectureForm.get();
            Lecture lecture1 = Lecture.finderById(id);
            lecture1.fName = lecture.fName;
            lecture1.lName = lecture.lName;
            lecture1.qualification = lecture.qualification;
            lecture1.specialization = lecture.specialization;
            lecture1.update();

            Employee employee=lecture1.user.employee;
            if(employee==null){
                employee=new Employee();
                employee.employeeFirstName=lecture1.fName;
                employee.employeeLastName=lecture1.lName;
                employee.isUser=true;
                employee.save();

                lecture1.user.employee=employee;
                lecture1.user.update();
            }

            return ok("1");
        }
        else
        {
            return ok("Update error");
        }
    }

    public static Result updateSession(long id)
    {
        if (session("admin") != null)
        {
            Form<Session> acc = Form.form(Session.class).bindFromRequest();

            Session acx = acc.get();

            Session uAcc = Session.finderById(id);
            uAcc.sessionName = acx.sessionName;
            uAcc.sessionStart = acx.sessionStart;
            uAcc.sessionEnd = acx.sessionEnd;
            uAcc.startHour = acx.startHour;
            uAcc.endHour = acx.endHour;
            uAcc.deleteStatus = false;
            uAcc.update();

            return ok("1");

        }
        else
        {
            return ok("login error");
        }
    }

    public static Result updateCampus(long id)
    {
        if (session("admin") != null || session("registrar") != null || session("VRAC") != null)
        {
            Form<Campus> acc = Form.form(Campus.class).bindFromRequest();

            Campus acx = acc.get();

            Campus uAcc = Campus.finderById(id);
            uAcc.campusName = acx.campusName;
            uAcc.campusLocation = acx.campusLocation;
            uAcc.deleteStatus = false;
            uAcc.update();

            return ok("1");

        }
        else
        {
            return ok("login error");
        }
    }

    public static Result makeGraduation(Long id) {
        Transaction txn = Ebean.beginTransaction();
        try {
            Training training = Training.finderById(id);
            if (training == null) {
                return notFound();
            }
            List<Student> students = training.students();
            for (Student student : students) {
                List<Module> modules = student.myOnlyModulesDeliberation();
                String decision = "";
                String gradeP = "";
                if (student.training.iMode.campusProgram.program.cle) {
                    student.status = "Graduated";
                    student.update();
                    training.hasGraduated = true;
                    training.update();
                    break;
                }
                double total = 0.0;
                double averageAllMarks = 0.0;
                int inc = 1;
                int tot = 0;
                int count1 = 0;
                int count2 = 0;
                int count3 = 0;
                double averageOneMarks = 0.0;
                for (Module module : modules) {
                        if (AssignmentResult.numberAssignmentDone(student.id, module.id, student.training.id) <= 0.0 && AssignmentResult.numberResearchDone(student.id, module.id, student.training.id) <= 0.0 && Counts.getExamMaxTrainingModule(student.training.id, module.id) <= 0.0) {
                        } else {
                            if (inc == 1) {
                                tot = tot + 1;
                            averageOneMarks = module.allModuleAverage(student.id);
                            if (averageOneMarks >= ProfileInfo.unique().scoreTwo) {
                                count1 = count1 + 1;
                            }
                            if (averageOneMarks < ProfileInfo.unique().scoreThree) {
                                count2 = count2 + 1;
                            }
                            if (averageOneMarks < ProfileInfo.unique().scoreTwo) {
                                count3 = count3 + 1;
                            }
                            total = total + module.allModuleAverage(student.id);
                            }
                        }
                }
                averageAllMarks = total / tot;
                // DELIBERATION BEGIN
                if (AssignmentResult.numberAssignmentDoneStudent(student.id, student.training.id) <= 0.0 && AssignmentResult.numberResearchDoneStudent(student.id, student.training.id) <= 0.0 && Counts.getExamMaxTrainingModuleStudent(student.training.id) <= 0.0) {
                } else {
                    if (averageAllMarks >= ProfileInfo.unique().scoreThree && count1 == tot) {
                        //1 PASS FIRST FIRST PHASE
                        decision = "PASS";
                    }
                    if (averageAllMarks >= ProfileInfo.unique().scoreThree && count3 >= 1) {
                        //2.1 RE-SIT
                        decision = "RE-SIT";
                        //student.inResit = true;
                    }
                    if (averageAllMarks >= ProfileInfo.unique().scoreTwo && averageAllMarks < ProfileInfo.unique().scoreThree && count2 >= 1) {
                        //3.1 RETAKE-MODULES
                        decision = "RE-SIT";
                        // student.inResit = true;
                    }
                    if (averageAllMarks < ProfileInfo.unique().scoreTwo && count2 >= 1) {
                        //3.2 RETAKE-MODULES
                        decision = "RETAKE-MODULES";
                        student.inRetake = true;
                    }
            }
            student.status = decision;
            student.gradeP = gradeP;
            student.academicEmail = decision + "...."+count1 + ",,,," + count2 +",!!!"+count3+"@@@@"+averageAllMarks;
            student.update();
            }
            training.hasGraduated = true;
            training.update();
            txn.commit();
        } finally {
            txn.end();
        }
        return ok("1");
    }
    public static Result makeGraduationResit(Long id) {
        Transaction txn = Ebean.beginTransaction();
        try {
            Training training = Training.finderById(id);
            if (training == null) {
                return notFound();
            }
            List<Student> students = training.studentsInResit();
            for (Student student : students) {
                List<Module> modules = student.myOnlyModulesDeliberation();
                String decision = "";
                if (student.training.iMode.campusProgram.program.cle) {
                    student.status = "Graduated";
                    student.update();
                    training.hasGraduated = true;
                    training.update();
                    break;
                }
                double total = 0.0;
                double averageAllMarks = 0.0;
                int inc = 1;
                int tot = 0;
                int count1 = 0;
                int count2 = 0;
                for (Module module : modules) {
                    double averageOneMarks = 0.0;
                    if (module.moduleInternship.equalsIgnoreCase("module") || module.moduleInternship.equalsIgnoreCase("Moot court")) {
                        if (SubMark.byDualInResitModule(student.id, module.id) == null) {
                          //  total = total + module.allModuleAverage(student.id);
                        total = total + (Counts.getStudentResearchMarksResearchModule(student.id, module.id) + (SubMark.componetExamResultModuleResearch(student.id, module.id)/(SubMark.componentsPerModule(module.id) - 1)));
                        } else {
                            total = total + module.allResitModuleAverage(student.id);
                        }
                        if (AssignmentResult.numberAssignmentDone(student.id, module.id, student.training.id) <= 0.0 && AssignmentResult.numberResearchDone(student.id, module.id, student.training.id) <= 0.0 && Counts.getExamMaxTrainingModule(student.training.id, module.id) <= 0.0) {
                        } else {
                            if (inc == 1) {
                                tot = tot + 1;
                            }
                            averageOneMarks = module.allModuleAverage(student.id);
                            if (averageOneMarks >= ProfileInfo.unique().scoreTwo) {
                                count1 = count1 + 1;
                            } else if (averageOneMarks < ProfileInfo.unique().scoreOne) {
                                count2 = count2 + 1;
                            }
                            if (averageOneMarks < ProfileInfo.unique().scoreTwo) {
                                student.failCount += 1;
                            }
                        }
                }
                averageAllMarks = total;
                // DELIBERATION BEGIN
                if (averageAllMarks >= ProfileInfo.unique().scoreThree) {
                    //1 PASS FIRST FIRST PHASE
                    decision = "PASS";
                } else if ((averageAllMarks >= ProfileInfo.unique().scoreThree && count2 > 0) || (averageAllMarks < ProfileInfo.unique().scoreThree)) {
                    //1 RETAKE-MODULES
                    decision = "RETAKE-MODULES";
                    student.inRetake = true;
                } else {
                    //2 RETAKE-PROGRAM
                    decision = "RETAKE-PROGRAM";
                    student.inRetake = true;
                }
                student.status = decision;
                student.inResit = false;
                student.academicEmail = count1 + "|" + averageAllMarks;
                student.update();
            }
        }
            training.hasGraduated = true;
            training.hasGraduatedReSit = true;
            training.update();
            txn.commit();
        } finally {
            txn.end();
        }
        return ok("1");
    }
    public static Result makeGraduationFinal(Long id) {
        Transaction txn = Ebean.beginTransaction();
        try {
            Training training = Training.finderById(id);
            if (training == null) {
                return notFound();
            }
            List<Student> students = training.students();
            for (Student student : students) {
        //        if(student.status.equalsIgnoreCase("PASS")){
                List<Module> modules = student.myModuleInternshipDeliberation();
                String decision = "";
                String gradeP = "";
                Double gradePoint = 0.0;
                if (student.training.iMode.campusProgram.program.cle) {
                    student.status = "Graduated";
                    student.update();
                    training.hasGraduated = true;
                    training.update();
                    break;
                }
                double total = 0.0;
                double averageAllMarks = 0.0;
                int inc = 1;
                int tot = 0;
                int count1 = 0;
                int count2 = 0;
                int count3 = 0;
                double averageOneMarks = 0.0;
                for (Module module : modules) {
                    if (!module.moduleInternship.equalsIgnoreCase("internship")) {
                        if (AssignmentResult.numberAssignmentDone(student.id, module.id, student.training.id) <= 0.0 && AssignmentResult.numberResearchDone(student.id, module.id, student.training.id) <= 0.0 && Counts.getExamMaxTrainingModule(student.training.id, module.id) <= 0.0) {
                        } else {
                            if (inc == 1) {
                                tot = tot + 1;
                                averageOneMarks = module.allModuleAverage(student.id);
                                if (averageOneMarks >= ProfileInfo.unique().scoreTwo) {
                                    count1 = count1 + 1;
                                } else if (averageOneMarks >= ProfileInfo.unique().scoreOne && averageOneMarks < ProfileInfo.unique().scoreThree) {
                                    count2 = count2 + 1;
                                } else if (averageOneMarks < ProfileInfo.unique().scoreOne) {
                                    count3 = count3 + 1;
                                } else if (averageOneMarks < ProfileInfo.unique().scoreTwo) {
                                    student.failCount += 1;
                                }
                                total = total + module.allModuleAverage(student.id);
                            }
                        }
                    }
                }
                if(student.inResit && (total > ProfileInfo.unique().maximumResit)) {
                    averageAllMarks = 60.0;
                    gradeP = "C";
                }else{
                    averageAllMarks = total;
                }
                    if (averageAllMarks >= ProfileInfo.unique().aMin && averageAllMarks <= ProfileInfo.unique().aMax) {
                        //2.1 RE-SIT
                        gradePoint = 5.0;
                        gradeP = "A";
                    } else if (averageAllMarks >= ProfileInfo.unique().bPlusMin && averageAllMarks <= ProfileInfo.unique().bPlusMax) {
                        //3.1 RETAKE-MODULES
                        gradePoint = 4.0;
                        gradeP = "B+";
                    }else if (averageAllMarks >= ProfileInfo.unique().bMin && averageAllMarks <= ProfileInfo.unique().bMax) {
                        gradePoint = 3.5;
                        gradeP = "B";
                    }else if (averageAllMarks >= ProfileInfo.unique().cPlusMin && averageAllMarks <= ProfileInfo.unique().cPlusMax) {
                        gradePoint = 3.0;
                        gradeP = "C+";
                    }else if (averageAllMarks >= ProfileInfo.unique().cMin && averageAllMarks <= ProfileInfo.unique().cMax) {
                        gradePoint = 2.5;
                        gradeP = "C";
                    }else if (averageAllMarks >= ProfileInfo.unique().dMin && averageAllMarks <= ProfileInfo.unique().dMax) {
                        gradePoint = 2.0;
                        gradeP = "D";
                    }else if (averageAllMarks >= ProfileInfo.unique().eMin && averageAllMarks <= ProfileInfo.unique().eMax) {
                        gradePoint = 1.0;
                        gradeP = "E";
                    }else if (averageAllMarks >= ProfileInfo.unique().fMin && averageAllMarks <= ProfileInfo.unique().fMax) {
                        gradePoint = 0.0;
                        gradeP = "F";
                    }
                    if (averageAllMarks >= ProfileInfo.unique().distinctionMin && averageAllMarks <= ProfileInfo.unique().distinctionMax) {
                        decision = "Distinction";
                    }else if (averageAllMarks >= ProfileInfo.unique().meritMin && averageAllMarks <= ProfileInfo.unique().meritMax) {
                        decision = "Merit";
                    }else if (averageAllMarks >= ProfileInfo.unique().satisfactoryMin && averageAllMarks <= ProfileInfo.unique().satisfactoryMax) {
                        decision = "Satisfactory";
                    }else if (averageAllMarks >= ProfileInfo.unique().passMin && averageAllMarks <= ProfileInfo.unique().passMax) {
                        decision = "Pass";
                        gradeP = "C";
                    }else if (averageAllMarks >= ProfileInfo.unique().failMin && averageAllMarks <= ProfileInfo.unique().failMax) {
                        decision = "Fail";
                        gradeP = "F";
                    } else if (averageAllMarks >= ProfileInfo.unique().scoreThree && count3 > 0) {
                        decision = "Retake-modules";
                    } else if (averageAllMarks < ProfileInfo.unique().scoreThree) {
                        decision = "Retake-modules";
                    } else if (gradeP.equalsIgnoreCase("C")) {
                        decision = "Pass";
                    }
                student.gradePoint = gradePoint;
                student.status = decision;
                student.gradeP = gradeP;
                student.academicEmail = total+" "+averageAllMarks+" "+decision+""+gradePoint;
                student.update();
            }
            training.hasGraduatedFinal = true;
            training.update();
            txn.commit();
        } finally {
            txn.end();
        }
        return ok("1");
    }
    public static Result updateBank(Long id)
    {

        if (session("admin") != null)
        {
            Form<BankAccount> form = Form.form(BankAccount.class).bindFromRequest();
            BankAccount bankAccount = form.get();

            BankAccount accountFind = BankAccount.find.ref(id);
            BankAccount bankAccount1 = new BankAccount();
            if (accountFind == null) return ok(Message.bankReferenceError);

            bankAccount1.bankName = bankAccount.bankName.trim();
            bankAccount1.bankAcronym = bankAccount.bankAcronym.trim();
            bankAccount1.openBranch = bankAccount.openBranch.trim();
            bankAccount1.accountNumber = bankAccount.accountNumber.trim();

            if (bankAccount1.doesExist(accountFind)) return ok(Message.bankError);

            bankAccount1.update(accountFind.id);

        }
        return ok("1");

    }

    public static Result updateCourseMaterial(Long id)
    {
        if (session("admin") != null)
        {
            Form<CourseMaterial> form = Form.form(CourseMaterial.class).bindFromRequest();
            CourseMaterial courseMaterial = form.get();
            CourseMaterial courseMaterial1 = CourseMaterial.finder.byId(id);
            courseMaterial1.schedule = Schedule.finderById(Long.parseLong(form.field("scheduleId").value()));
            courseMaterial1.name =courseMaterial.name;
            courseMaterial1.update(id);
        }
        return ok("1");

    }

    public static Result updateProgram(long id)
    {
        if (session("admin") != null)
        {
            Form<Program> a = Form.form(Program.class).bindFromRequest();

            Program acx = a.get();

            Program uAcc = Program.finderById(id);
            uAcc.cle = acx.cle;
            uAcc.programName = acx.programName;
            uAcc.programAcronym = acx.programAcronym;

            uAcc.deleteStatus = false;
            uAcc.update();

            return ok("1");

        }
        else
        {
            return ok("login error");
        }
    }

    public static Result updateAssResult(long id)
    {
        if (session("registrar") != null || session("Coordinator") != null || session("mark_officer") != null)
        {
            Form<AssignmentResult> a = Form.form(AssignmentResult.class).bindFromRequest();

            AssignmentResult assignmentResult = a.get();

            AssignmentResult assignmentResult1 = AssignmentResult.finderById(id);
            Assignment assignment = assignmentResult1.assignment;
            Component component = assignmentResult1.assignment.component;
            IntakeSessionMode iomode = assignmentResult1.student.training.iMode;
            Campus campus = assignmentResult1.student.training.iMode.campusProgram.campus;
            assignmentResult1.result = assignmentResult.result;
            assignmentResult1.comment = assignmentResult.comment;
            if(assignmentResult.result > assignmentResult1.assignment.max)
                return ok("The maximum marks is"+ assignmentResult1.assignment.max);
            assignmentResult1.update();
            return ok("Assignment result updated to "+assignmentResult.result);
        }
        else
        {
            return ok("login error");
        }
    }
    public static Result updateFees(long id)
    {
        if (session("admin") != null)
        {
            Form<SchoolFees> a = Form.form(SchoolFees.class).bindFromRequest();

            SchoolFees acx = a.get();

            SchoolFees f = SchoolFees.finderById(id);

            f.feesAmount = acx.feesAmount;
            f.feesDetails = acx.feesDetails;
            Long idea = Long.parseLong(a.field("intaker").value());
            f.intake = Intake.finderById(idea);

            f.update();

            return ok("1");

        }
        else
        {
            return ok("login error");
        }
    }

    public static Result submitAttendanceClaim(long id)
    {
            Form<Attendance> form = Form.form(Attendance.class).bindFromRequest();
            Attendance attendance = form.get();
            Attendance attendance1 = Attendance.find.byId(id);
            attendance1.comment = attendance.comment;
            attendance1.update();
            return ok("1");
    }

    public static Result submitAttendanceClaimApproval(long id)
    {
            Form<Attendance> form = Form.form(Attendance.class).bindFromRequest();
            Attendance attendance = form.get();
            Attendance attendance1 = Attendance.find.byId(id);
            attendance1.comment = attendance.comment;
            attendance1.attended = attendance.attended;
            attendance1.attendedp = attendance.attendedp;
            attendance1.claimed = true;
            attendance1.update();
            return ok("1");
    }

    public static Result updatePassword(Long id)
    {
        Form<Users> f = Form.form(Users.class).bindFromRequest();
        Users user = Users.finderById(id);
        String oldPassword = f.field("oldPassword").value();
        if (!user.password.equals(oldPassword)) return ok("old password is incorrect");
        user.password = BCrypt.hashpw(f.get().password, BCrypt.gensalt());
        user.resetRequired = false;
        user.update();
        return ok("1");
    }

    public static Result hashPassword(Long id)
    {
        Form<Users> f = Form.form(Users.class).bindFromRequest();
        Users user = Users.finderById(id);
        String password = f.field("password").value();
        user.password = BCrypt.hashpw(password, BCrypt.gensalt());
        user.resetRequired = false;
        user.update();
        return ok("1");
    }

    public static Result updateSchedule(Long id)
    {
        if (session("registrar") != null)
        {
            Form<Schedule> s = Form.form(Schedule.class).bindFromRequest();

            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
            Schedule newSd = Schedule.finderById(id);
            try
            {
                newSd.date = format.parse(s.field("date").value());
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
            newSd.room = Room.finderById(Long.parseLong(s.field("rooms").value()));
            newSd.training = Training.finderById(Long.parseLong(s.field("train").value()));
            newSd.lecture = Lecture.finderById(Long.parseLong(s.field("lect").value()));
            newSd.component = Component.finderById(Long.parseLong(s.field("comp").value()));
            newSd.dateRange = DateRange.finderById(Long.parseLong(s.field("dateRange").value()));
            newSd.componentType = s.field("componentType").value();
            newSd.assignment = s.field("assignment").value();
            newSd.exam = s.field("exam").value();
            newSd.startHour = s.field("startHour").value();
            newSd.endHour = s.field("endHour").value();

        /*    String chCekc = newSd.check();

            if (!chCekc.equals("1")) return ok(chCekc);*/

            newSd.save();

            return ok("1");

        }
        return ok("0");
    }

    public static Result updateSchedule2(Long id)
    {
        if (session("registrar") != null || session("DTR/Coordinator") != null || session("Coordinator") != null || session("mark_officer") != null)
        {
            Form<Schedule> s = Form.form(Schedule.class).bindFromRequest();
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
            Schedule newSd = Schedule.finderById(id);
            Lecture lecture = Lecture.finderById(newSd.lecture.id);
            try
            {
                newSd.date = format.parse(s.field("date").value());
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
            newSd.room = Room.finderById(Long.parseLong(s.field("rooms").value()));
            newSd.training = Training.finderById(Long.parseLong(s.field("train").value()));
            newSd.lecture = Lecture.finderById(Long.parseLong(s.field("lect").value()));
            newSd.component = Component.finderById(Long.parseLong(s.field("comp").value()));
            newSd.dateRange = DateRange.finderById(Long.parseLong(s.field("dateRange").value()));
            newSd.startHour = s.field("startHour").value();
            newSd.endHour = s.field("endHour").value();
            newSd.update(id);
            return redirect("/scheduleByLecture/?lectureId="+lecture.id);
        }
        return ok("0");
    }
    public static Result updateUsers(long id)
    {
        if (session("admin") != null)
        {
            Form<Employee> empFrm = Form.form(Employee.class).bindFromRequest();
            Ebean.beginTransaction();
            Unit unit = Unit.find(Long.parseLong(empFrm.field("unit").value()));
            if (unit == null)
            {
                return ok("Unit not found");
            }

            Position position = Position.find(Long.parseLong(empFrm.field("position").value()));
            if (position == null)
            {
                return ok("Position not found");
            }


            Employee employee = Employee.find(id);
            if (employee == null)
            {
                return ok("Employee not found");
            }

            employee.employeeLastName = empFrm.field("employeeLastName").value();
            employee.employeeFirstName = empFrm.field("employeeFirstName").value();
            employee.position = position;
            employee.gender = empFrm.field("gender").value();
            employee.unit = unit;

            Boolean isUser = Boolean.parseBoolean(empFrm.field("isUser").value());
            if (employee.isUser && !isUser)
            {
                Users users = Users.finder.where().eq("deleteStatus", false).eq("employee.id", employee.id).setMaxRows(1).findUnique();
                for (UserRole role : users.RolesForUser())
                {
                    role.deleteStatus = true;
                    role.update();
                }
                employee.isUser = false;
                users.deleteStatus = true;
                users.update();
            }
            else if (!employee.isUser && isUser)
            {
                employee.isUser = true;
                Users nUser = new Users();
                nUser.email = empFrm.field("email").value();
                nUser.employee = employee;
                nUser.phone = empFrm.field("phone").value();
                nUser.password = BCrypt.hashpw(empFrm.field("password").value(), BCrypt.gensalt());
                nUser.role = "Employee";
                nUser.resetCode = randomString();
                nUser.save();
            }
            else if (employee.isUser && isUser)
            {
                Users user = Users.finder.where().eq("deleteStatus", false).eq("employee.id", employee.id).setMaxRows(1).findUnique();
                user.email = empFrm.field("email").value();
                user.employee = employee;
                user.phone = empFrm.field("phone").value();
                user.password = BCrypt.hashpw(empFrm.field("password").value(), BCrypt.gensalt());
                user.role = "Employee";
                user.resetCode = randomString();
                user.update();
            }
            employee.update();
            Ebean.commitTransaction();
            return ok("1");
        }
        return ok("login error");
    }

    public static Result updateFiles(Long id)
    {
        if (session("admin") != null)
        {
            Form<AcademicFiles> f = Form.form(AcademicFiles.class).bindFromRequest();
            AcademicFiles fl = f.get();

            AcademicFiles fx = AcademicFiles.finderById(id);
            fx.fileName = fl.fileName;
            fx.description = fl.description;

            Long prog = Long.parseLong(f.field("prog").value());

            fx.program = Program.finderById(prog);

            Long sessionId = Long.valueOf(f.field("sessionId").value());
            Session session = Session.finderById(sessionId);
            if (session == null)
            {
                return notFound("Session not found");
            }
            fx.session = session;

            fx.update();

            return ok("1");
        }
        else
        {
            return ok("error");
        }
    }

    public static Result updateComponent(Long id)
    {
        if (session("admin") != null || session("registrar") != null || session("VRAC") != null)
        {
            Form<Component> f = Form.form(Component.class).bindFromRequest();
            Component fl = f.get();
            Component fx = Component.finderById(id);
            fx.compName = fl.compName;
            fx.code = fl.code;
            Long mod = Long.parseLong(f.field("mod").value());
            fx.module = Module.finderById(mod);
            fx.credits = fl.credits;
            fx.credits = fl.credits;
            fx.update();
            return ok("1");
        }
        else
        {
            return ok("error");
        }
    }

    public static Result updateMode(Long mId)
    {
        if (session("admin") != null)
        {
            Form<StudyMode> f = Form.form(StudyMode.class).bindFromRequest();
            StudyMode s = f.get();

            StudyMode st = StudyMode.finderById(mId);
            st.modeName = s.modeName;
            st.modeAcronym = s.modeAcronym;
            st.update();
            return ok("1");
        }
        return ok("0");
    }

    public static Result updateHours(Long id)
    {
        if (session("admin") != null)
        {
            Form<Hours> f = Form.form(Hours.class).bindFromRequest();
            Hours s = f.get();
            Hours hours = Hours.finderById(id);
            hours.hourName = s.hourName;
            hours.update();
            return ok("1");
        }
        return ok("0");
    }

    public static Result updateCampusProgram(Long id)
    {
        if (session("admin") != null)
        {
            Form<CampusProgram> f = Form.form(CampusProgram.class).bindFromRequest();
            CampusProgram cProgram = CampusProgram.finderById(id);

            Long campusId = Long.valueOf(f.field("myCampus").value());
            cProgram.campus = Campus.finderById(campusId);

            Long programId = Long.valueOf(f.field("myProgram").value());
            cProgram.program = Program.finderById(programId);

            if (cProgram.exist()) return ok(Message.requestedChanges);

            cProgram.update();
            return ok("1");
        }
        return ok("0");
    }

    public static Result updateCampusProgramMode(Long id)
    {
        if (session("admin") != null)
        {
            Form<CampusProgramMode> f = Form.form(CampusProgramMode.class).bindFromRequest();
            CampusProgramMode cMode = CampusProgramMode.finderById(id);

            Long campusId = Long.valueOf(f.field("myCProg").value());
            cMode.campusProgram = CampusProgram.finderById(campusId);

            Long programId = Long.valueOf(f.field("sMode").value());
            cMode.mode = StudyMode.finderById(programId);

            if (cMode.exist()) return ok(Message.requestedChanges);

            cMode.update();

            return ok("1");
        }
        return ok("0");
    }

    public static Result updateSessionMode(Long id)
    {
        if (session("admin") != null)
        {
            Form<SessionMode> f = Form.form(SessionMode.class).bindFromRequest();
            SessionMode sMode = SessionMode.finderById(id);

            Long sessionId = Long.valueOf(f.field("mySession").value());
            sMode.session = Session.finderById(sessionId);

            Long modeId = Long.valueOf(f.field("myMode").value());
            sMode.mode = StudyMode.finderById(modeId);

            if (sMode.exist()) return ok(Message.requestedChanges);

            sMode.update();
            return ok("1");
        }
        return ok("0");
    }

    public static Result intakeSessionMode(Long id)
    {
        if (session("admin") != null)
        {
            Form<IntakeSessionMode> iMode = Form.form(IntakeSessionMode.class).bindFromRequest();
            IntakeSessionMode intakeSessionMode = IntakeSessionMode.finderById(id);

            Long intakeId = Long.valueOf(iMode.field("myIntake").value());
            intakeSessionMode.intake = Intake.finderById(intakeId);
            Long cProg = Long.valueOf(iMode.field("myCprogram").value());
            intakeSessionMode.campusProgram = CampusProgram.finderById(cProg);
            Long sModeId = Long.valueOf(iMode.field("mySmode").value());
            intakeSessionMode.sessionMode = SessionMode.finderById(sModeId);

            if (intakeSessionMode.exist()) return ok(Message.requestedChanges);
            intakeSessionMode.update();
            return ok("1");
        }
        return ok("0");
    }

    public static Result updateAssignedDays(Long id)
    {
        if (session("admin") != null)
        {
            Form<DaySession> daySessionForm = Form.form(DaySession.class).bindFromRequest();
            DaySession daySession = DaySession.finderById(id);
            if (daySession == null) return notFound();

            Long sessionId = Long.valueOf(daySessionForm.field("mySession").value());
            daySession.session = Session.finderById(sessionId);
            Long dayId = Long.valueOf(daySessionForm.field("myDay").value());
            daySession.day = Days.finderById(dayId);

            if (daySession.exist()) return ok(Message.requestedChanges);

            daySession.update();

            return ok("1");
        }
        return ok("0");
    }

    public static Result updateHoursSession(Long id)
    {
        if (session("admin") != null)
        {
            Form<HourSession> daySessionForm = Form.form(HourSession.class).bindFromRequest();
            HourSession hourSession = HourSession.finderById(id);
            if (hourSession == null) return notFound();

            Long sessionId = Long.valueOf(daySessionForm.field("mySession").value());
            hourSession.session = Session.finderById(sessionId);
            Long dayId = Long.valueOf(daySessionForm.field("myHour").value());
            hourSession.hour = Hours.finderById(dayId);

            if (hourSession.exist()) return ok(Message.requestedChanges);
            hourSession.update();
            return ok("1");
        }
        return ok("0");
    }

    public static Result updateCompMax(Long id)
    {
        if (session("Instructor") != null || session("Coordinator") != null || session("mark_officer") != null || session("registrar") != null)
        {
            Form<ComponentMax> form = Form.form(ComponentMax.class).bindFromRequest();
            ComponentMax componentMax = form.get();
            ComponentMax componentMax1 = ComponentMax.finder.byId(id);
            if (componentMax1 == null) return notFound();
            Training training = Training.finderById(Long.parseLong(form.field("trainingId").value()));
            Component component = Component.finderById(Long.parseLong(form.field("componentId").value()));
            componentMax1.researchMax = componentMax.researchMax;
            componentMax1.examMax = componentMax.examMax;
            componentMax1.resitMax = componentMax.resitMax;
            componentMax1.resitResearchMax = componentMax.resitResearchMax;
            if (componentMax.researchMax < 0 || componentMax.examMax < 0) return ok("Negative entry not allowed");
            componentMax1.researchMax = componentMax.researchMax;
            componentMax1.training = training;
            componentMax1.component = component;
            System.out.println(componentMax1.component=component);
            componentMax1.update();
            return ok("1");
        }
        return ok("0");
    }

    public static Result updateRoom(Long mId)
    {
        if (session("admin") != null || session("registrar") != null || session("VRAC") != null)
        {
            Form<Room> f = Form.form(Room.class).bindFromRequest();
            Room s = f.get();

            Room st = Room.finderById(mId);
            st.roomName = s.roomName;
            st.roomCode = s.roomCode;
            st.roomType = s.roomType;

            Long camp = Long.parseLong(f.field("camp").value());
            st.campus = Campus.finderById(camp);
            st.flowNumber = s.flowNumber;
            st.numberSeat = s.numberSeat;

            st.update();

            return ok("1");
        }
        return ok("0");
    }

    public static Result allMarksEntered(Long mId)
    {
        if (session("admin") != null || session("registrar") != null || session("VRAC") != null)
        {
            Form<Training> form = Form.form(Training.class).bindFromRequest();
            Training training = form.get();

            Training training1 = Training.finderById(mId);
            training1.isMarksEntered = true;
            training1.update();
            return ok("1");
        }
        return ok("0");
    }

    public static Result appealDecision(Long id)
    {
        if (session("registrar") != null)
        {
            Form<InternshipAppeal> form = Form.form(InternshipAppeal.class).bindFromRequest();
            InternshipAppeal appeal = form.get();
            InternshipAppeal appeal1 = InternshipAppeal.byId(id);
            appeal1.status = form.field("status").value();
            appeal1.update();
            Applicant applicant = appeal1.applicant;
            applicant.experience = Integer.parseInt(form.field("experience").value());
            applicant.update();
            return ok("1");
        }
        return ok("0");
    }
    public static Result resitModulesDecision(Long id)
    {
        if (session("registrar") != null)
        {
            Form<ReSitReTakeRequest> form = Form.form(ReSitReTakeRequest.class).bindFromRequest();
            ReSitReTakeRequest request = form.get();
            ReSitReTakeRequest request1 = ReSitReTakeRequest.finderById(id);
            request1.status = form.field("status").value();
            request1.commentRegistrar = form.field("commentRegistrar").value();
            request1.update();
            Student student = Student.finderById(request1.student.id);
            student.inResit = true;
            student.update();
            return ok("1");
        }
        return ok("0");
    }
    public static Result retakeModuleDecision(Long id)
    {
        if (session("registrar") != null)
        {
            Form<ReSitReTakeRequest> form = Form.form(ReSitReTakeRequest.class).bindFromRequest();
            ReSitReTakeRequest request = form.get();
            ReSitReTakeRequest request1 = ReSitReTakeRequest.finderById(id);
            request1.status = form.field("status").value();
            request1.commentRegistrar = form.field("commentRegistrar").value();
            request1.update();
            if(form.field("status").value().equalsIgnoreCase("approved")){
                Student student = Student.finderById(request1.student.id);
                Training preTraining = Training.finderById(student.training.id);
                Training currentTraining = Training.finderById(Long.parseLong(form.field("trainingId").value()));
                Student student1 = Student.finderById(request1.student.id);
                student1.inRetake = true;
                student1.training = currentTraining;
                student1.trainingPrevious = preTraining;
                student1.update();
            }
            return ok("1");
        }
        return ok("0");
    }
    public static Result resitAppealDecisionProgram(Long id)
    {
        if (session("registrar") != null)
        {
            Form<ReSitReTakeRequest> form = Form.form(ReSitReTakeRequest.class).bindFromRequest();
            ReSitReTakeRequest request = form.get();
            ReSitReTakeRequest request1 = ReSitReTakeRequest.finderById(id);
            Training training = Training.finderById(Long.parseLong(form.field("trainingId").value()));
            request1.status = form.field("status").value();
            request1.commentRegistrar = form.field("commentRegistrar").value();
            request1.training = training;
            request1.update();
            Student student = Student.finderById(request1.student.id);
            Training preTraining = Training.finderById(student.training.id);
            Training currentTraining = Training.finderById(Long.parseLong(form.field("trainingId").value()));
            if(form.field("status").value().equalsIgnoreCase("approved")) {
                Student student1 = Student.finderById(request1.student.id);
                student1.inRetake = true;
                student1.training = currentTraining;
                student1.trainingPrevious = preTraining;
                student1.update();
            }
            return ok("1");
        }
        return ok("0");
    }

    public static Result updateSignature(String s)
    {
        Form<ProfileInfo> form = Form.form(ProfileInfo.class).bindFromRequest();
        //ProfileInfo info = form.get();

        ProfileInfo info1 = ProfileInfo.unique();
        if (s.equalsIgnoreCase("registrar"))
        {
            info1.registrarSignature = uploadImage("institute/profile-information-files/",info1.registrarSignature);
        }
        else if (s.equalsIgnoreCase("rector"))
        {
            info1.rector = uploadImage("institute/profile-information-files/",info1.rector);
        }
        else if (s.equalsIgnoreCase("stamp"))
        {
            info1.stamp = uploadImage("institute/profile-information-files/",info1.stamp);
        }
        info1.update();
        return ok("1");
    }

    public static Result updateTraining(Long tId)
    {
        if (session("admin") != null || session("VRAC") != null || session("registrar") != null)
        {
            Form<Training> f = Form.form(Training.class).bindFromRequest();
            Training s = f.get();

            if (s.startDate.equals(s.endDate) || s.endDate.before(s.startDate))
            {
                return ok(Message.endDateError);
            }
            if (s.schoolFees < 0)
            {
                return ok(Message.feesError);
            }
            if (s.accomodationFees < 0 || s.restaurationFees < 0)
            {
                return ok(Message.feesError2);
            }

            IntakeSessionMode mode = IntakeSessionMode.finderById(Long.parseLong(f.field("intakeSess").value()));
            Training st = Training.finderById(tId);
            st.schoolFees = Double.valueOf(f.field("schoolFees").value());
            st.accomodationFees = Double.valueOf(f.field("accomodationFees").value());
            st.restaurationFees = Double.valueOf(f.field("restaurationFees").value());
            st.iMode = mode;
            st.startDate = s.startDate;
            st.endDate = s.endDate;
            st.startDateApplication = s.startDateApplication;
            st.endDateApplication = s.endDateApplication;
            st.title = s.title;
            st.trainer = s.trainer;
            st.otherFees = f.get().otherFees;
            /*tuition fee*/
            st.eacStudentTuitionFees=Double.valueOf(f.field("eacStudentTuitionFees").value());
            st.minEacStudentTuitionFees=Double.valueOf(f.field("minEacStudentTuitionFees").value());
            if(st.eacStudentTuitionFees<st.minEacStudentTuitionFees)
                return ok("Minimum payment "+st.minEacStudentTuitionFees+" cant be greater than the maximum payment "+st.eacStudentTuitionFees);
            st.nonEacStudentTuitionFees=Double.valueOf(f.field("nonEacStudentTuitionFees").value());
            st.minNonEacStudentTuitionFees=Double.valueOf(f.field("minNonEacStudentTuitionFees").value());

            if(st.nonEacStudentTuitionFees<st.minNonEacStudentTuitionFees)
                return ok("Minimum payment "+st.minNonEacStudentTuitionFees+" cant be greater than the maximum payment "+st.nonEacStudentTuitionFees);
            /**/
            st.otherFeesSpec = f.get().otherFeesSpec;
            st.minPayment = f.get().minPayment;
            st.update();

            return ok("1");
        }
        return ok("0");
    }
    public static Result updateInfo()
    {
        Form<Users> applicantForm = Form.form(Users.class).bindFromRequest();
        Users applicant = Users.finder.where().eq("email", session().get("student")).eq("password", applicantForm.field("oldPassword").value()).eq("role", "student").eq("deleteStatus", false).findUnique();
        if (applicant == null)
        {
            return notFound();
        }
        if (!Objects.equals(applicantForm.field("newPassword").value(), applicantForm.field("confirmPassword").value()))
        {
            return notFound("Password son't match");
        }
        applicant.email = applicantForm.field("email").value();
        applicant.phone = applicantForm.field("phoneNumber").value();
        applicant.password = applicantForm.field("newPassword").value();
        applicant.update();
        return ok();
    }


    public static Result updateStudentInfo()
    {
        Form<Users> form = Form.form(Users.class).bindFromRequest();
        Users user = Users.finderById(Long.parseLong(form.field("userId").value()));
        Users user1 = form.get();
        if (user == null)
        {
            return notFound();
        }
        user.email = user1.email;
        user.phone = user1.phone;
        user.password = BCrypt.hashpw(user1.password, BCrypt.gensalt());
        user.deleteStatus = false;
        user.update();
        return ok("1");
    }
    public static Result deleteUser()
    {
        Form<Users> form = Form.form(Users.class).bindFromRequest();
        Users user = Users.finderById(Long.parseLong(form.field("userId").value()));
        if (user == null)
        {
            return notFound();
        }
        user.deleteStatus = true;
        user.update();
        return ok("1");
    }
    public static Result regNewEmail()
    {
        Form<Student> form = Form.form(Student.class).bindFromRequest();
        Student student = Student.finderById(Long.parseLong(form.field("studentId").value()));
        if (student == null)
        {
            return notFound();
        }
        student.emailStatus = "given";
        student.update();
        Users user = student.applicant.user;
        user.usedEmail = form.field("email").value();
        user.password = BCrypt.hashpw(form.field("password").value(), BCrypt.gensalt());
        String message = "Dear student "+student.firstName+" "+student.familyName+" your academic email to be used in ILPD MIS is: "+ student.academicEmail + " and new password is "+form.field("password").value()+" kindly change this password for the first login!";
        new Counts().sendUserEmail2(form.field("email").value(), "Academic email provided!", message);
        user.email = student.academicEmail;
        user.deleteStatus = false;
        user.update();
        return ok("1");
    }
    public static Result deleteUncompletedUser(){
        Form<Users> form = Form.form(Users.class).bindFromRequest();
        List<Users> users = Users.finder.where()
                .eq("deleteStatus", false)
                .eq("names", "")
                .findList();
        if(users != null) {
            for (Users u : users) {
                    u.deleteStatus = true;
                    u.update(u.id);
            }
        }else{
            return ok("No un completed user found!");
        }
        return ok("1");
    }

    public static Result updateData(String s, long id)
    {
        if (s.equalsIgnoreCase("graduation")) {
            Form<Training> form = Form.form(Training.class).bindFromRequest();
            Training training = Training.finderById(id);
            try
            {
                training.graduation = new SimpleDateFormat("MM-dd-yyyy").parse(form.field("graduation").value());
                training.update();
            }
            catch (ParseException e)
            {
                e.printStackTrace();
                return badRequest();
            }
            return ok("1");
        }
        if (s.equalsIgnoreCase("UndoGraduation")) {
            Form<Training> form = Form.form(Training.class).bindFromRequest();
            Training training = Training.finderById(id);
            if (training != null) {
                List<Student> students = Student.byTraining(training.id);
                for (Student ss : students) {
                    ss.status = "Active";
                    ss.failCount = 0;
                    ss.gradePoint = 0.0;
                    ss.inRetake = false;
                    ss.inResit = false;
                    ss.gradeP = "";
                    ss.update();
                    List<Module> modules = ss.myOnlyModulesDeliberation();
                }
                training.isMarksEntered = false;
                training.hasGraduated = false;
                training.hasGraduatedReSit = false;
                training.hasGraduatedFinal = false;
                training.update();
            }
            return ok("1");
        }
        if (s.equalsIgnoreCase("UndoGraduationResit")) {
            Form<Training> form = Form.form(Training.class).bindFromRequest();
            Training training = Training.finderById(id);
            if(training != null){
                List<Student> students = Student.byTraining(training.id);
                for(Student ss : students){
                    ss.status = "RE-SIT";
                    ss.update();
                }
                training.isMarksEntered = false;
                training.update();
            }
            return ok("1");
        }
        if (s.equalsIgnoreCase("UndoGraduationFinal")) {
            Form<Training> form = Form.form(Training.class).bindFromRequest();
            Training training = Training.finderById(id);
            if(training != null){
                List<Student> students = Student.byTraining(training.id);
                for(Student ss : students){
                    if(ss.status.equalsIgnoreCase("DISTINCTION") || ss.status.equalsIgnoreCase("PASS") || ss.status.equalsIgnoreCase("Merit") || ss.status.equalsIgnoreCase("Satisfactory")) {
                        ss.status = "PASS";
                        ss.update();
                    }
                }
                training.isMarksEntered = true;
                training.hasGraduatedFinal = false;
                training.update();
            }
            return ok("1");
        }
        else if (s.equalsIgnoreCase("new"))
        {
            Training training = Training.finderById(id);
            if (training == null) return notFound();
            session("status", String.valueOf(training.id));
            List<Applied> applieds = Applied.finder.where().eq("deleteStatus", false).eq("training.id", training.id).eq("applicationStatus", "")
                    .eq("status", true)
                    .orderBy("id asc")
                    .findList();
            return ok(Json.toJson(applieds));
        }
        else if (s.equalsIgnoreCase("accepted"))
        {
            Training training = Training.finderById(id);
            if (training == null) return notFound();
            session("status", String.valueOf(training.id));
            List<Applied> applieds = Applied.finder.where().eq("deleteStatus", false).eq("training.id", training.id).eq("applicationStatus", "accepted")
                    .eq("status", true)
                    .orderBy("id asc")
                    .findList();
            return ok(Json.toJson(applieds));
        }
        else if (s.equalsIgnoreCase("derefered"))
        {
            Training training = Training.finderById(id);
            if (training == null) return notFound();
            session("status", String.valueOf(training.id));
            List<Applied> applieds = Applied.finder.where()
                    .eq("deleteStatus", false)
                    .eq("training.id", training.id)
                    .eq("applicationStatus", "derefered")
                    .eq("status", true)
                    .orderBy("id asc")
                    .findList();
            return ok(Json.toJson(applieds));
        }
        else if (s.equalsIgnoreCase(":all"))
        {
            Training training = Training.finderById(id);
            if (training == null) return notFound();
            session("status", String.valueOf(training.id));
            List<Applied> applieds = Applied.finder.where()
                    .eq("deleteStatus", false)
                    .eq("training.id", training.id)
                    .eq("status", true)
                    .orderBy("id asc")
                    .findList();
            return ok(Json.toJson(applieds));
        }
        return ok("0");
    }


}
