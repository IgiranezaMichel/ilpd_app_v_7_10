package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.Transaction;
import models.*;
import models.Utility.SendMailer;
import models.Utility.Template;
import models.stock.Position;
import models.stock.Unit;
import org.h2.engine.User;
import org.joda.time.DateTime;
import org.mindrot.jbcrypt.BCrypt;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Result;
import java.time.LocalDate;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.function.Function;

import static com.avaje.ebean.Expr.eq;


public class Registration extends React {
    public static Result studentRegForm() {
        return ok(views.html.student.studentRegForm.render());
    }
    public static Result saveBank() {
        if (session("admin") != null) {
            Form<BankAccount> form = Form.form(BankAccount.class).bindFromRequest();
            BankAccount bankAccount = form.get();
            BankAccount account = new BankAccount();
            account.bankAcronym = bankAccount.bankAcronym.trim();
            account.bankName = bankAccount.bankName.trim();
            account.accountNumber = bankAccount.accountNumber.trim();
            account.openBranch = bankAccount.openBranch.trim();

            if (account.doesExist(account)) return ok(Message.bankError);

            account.save();
        }
        return ok("1");
    }
    public static Result approveTrainingMarks(Long tId) {
            Form<Training> form = Form.form(Training.class).bindFromRequest();
            Training training = form.get();
            Training training1 = Training.finderById(tId);
            training1.transcript = true;
            training1.update();
            List<Student> students = Student.byTraining(tId);
            for(Student s : students){
                Users user = s.applicant.user;
                new Counts().sendUserEmail(user, "Marks results approved!", Template.marksApproved);
            }
        return ok("1");
    }
    public static Result changePhoto() {

        Form<Users> f = Form.form(Users.class).bindFromRequest();
        Users u = f.get();

        Users user = null;
        Applicant applicant = null;
        if (session("admin") != null) {
            user = Users.finderByMail(session("admin"));
        } else if (session("registrar") != null) {
            user = Users.finderByMail(session("registrar"));
        } else if (session("student") != null) {
            user = Users.finderByMail(session("student"));
        } else if (session("Coordinator") != null) {
            user = Users.finderByMail(session("Coordinator"));
        } else if (session("mark_officer") != null) {
            user = Users.finderByMail(session("mark_officer"));
        } else if (session("Finance") != null) {
            user = Users.finderByMail(session("Finance"));
        } else if (session("Library") != null) {
            user = Users.finderByMail(session("Library"));
        } else if (session("Logistic") != null) {
            user = Users.finderByMail(session("Logistic"));
        } else if (session("HeadOfUnit") != null) {
            user = Users.finderByMail(session("HeadOfUnit"));
        } else if (session("DAF") != null) {
            user = Users.finderByMail(session("DAF"));
        } else if (session("Auditor") != null) {
            user = Users.finderByMail(session("Auditor"));
        } else if (session("Accountant") != null) {
            user = Users.finderByMail(session("Accountant"));
        } else if (session("Manager") != null) {
            user = Users.finderByMail(session("Manager"));
        } else if (session("Rector") != null) {
            user = Users.finderByMail(session("Rector"));
        } else if (session("DTR/Coordinator") != null) {
            user = Users.finderByMail(session("DTR/Coordinator"));
        } else if (session("quality_insurance") != null) {
            user = Users.finderByMail(session("quality_insurance"));
        } else if (session("Staff") != null) {
            user = Users.finderByMail(session("Staff"));
        }else if (session("Instructor") != null) {
            user = Users.finderByMail(session("Instructor"));
        } else if ( isApplicant() ) {
            applicant = applicant();
            user = applicant.user;
        }


        if (user != null) {
            user.profile = uploadImage(u.profile);
            if( applicant != null ){
                applicant.profile = user.profile;
                applicant.update();
            }
            user.save();
        }
        return redirect(routes.Application.index());
    }



    public static Result saveEvCategory(){
        Form.form(EvCategory.class).bindFromRequest().get().save();
        return ok("1");
    }

    public static Result saveEvQuestion(){
        Form<EvQuestion> form = Form.form(EvQuestion.class).bindFromRequest();

        if( form.hasErrors() ) return ok(form.errorsAsJson());


        EvQuestion question = form.get();

        question.category = EvCategory.finder.byId(question.category.id);



        question.save();

        return ok("1");
    }

    private static Users saveUser(Users fData, String type, Long cId) {
        if (Users.checkMail(fData.email)) {
            Users nUser = new Users();
            nUser.email = fData.email;
            nUser.deleteStatus = false;
            nUser.names = fData.names;
            nUser.phone = fData.phone;
            nUser.role = type;
            nUser.password = BCrypt.hashpw(fData.password, BCrypt.gensalt());


            Country country = Country.finderById(cId);
            nUser.code = country.phonecode;
            if(Users.checkMail(fData.email)){
                nUser.save();
            }
            String folderName = randomString() + "." + nUser.id;
            if (!newFolder(folderName)) return null;
            nUser.resetCode = randomString() + randomString() + nUser.id;
            nUser.path = folderName;
            nUser.update();
            return nUser;
        } else {
            return null;
        }
    }

    public static Result updateFile(Long id) {
        Applicant applicant = Applicant.finderById(id);
        List<AcademicFiles> academicFiles = AcademicFiles.find.where()
                .eq("session.id", applicant.applied.training.iMode.sessionMode.session.id)
                .findList();

        for (AcademicFiles fp : AcademicFiles.byProgram(applicant.applied.training.iMode.campusProgram.program.id, applicant.id)) {
            Attachment n = Attachment.checker(applicant.id, fp.id);
            if (n == null) {
                n = new Attachment();
            } else {
                Verification vf = n.amV();
                if (vf != null) {
                    vf.deleteStatus = true;
                    vf.update();
                }
            }
            n.attachName = uploadFile(n.attachName, fp.uniqueName);
            n.app = applicant;
            n.file = fp;
            n.update();
        }
        return ok("1");
    }

    public static Users saveUser(Users fData, Employee employee) {

        Users nUser = new Users();
        nUser.email = fData.email;
        nUser.names=employee.print();
        nUser.deleteStatus = false;
        nUser.employee = employee;
        nUser.phone = fData.phone;
        nUser.role = "Employee";
        nUser.password = BCrypt.hashpw(fData.password, BCrypt.gensalt());
        nUser.resetCode = randomString();

        if (nUser.employee == null) {
            nUser.save();
            return nUser;
        }
        if (nUser.notExist()) nUser.save();

        return nUser;
    }

    public static Result saveSystemUser() {
        if (session("admin") != null) {
            Form<Users> frm = Form.form(Users.class).bindFromRequest();
            Users user = frm.get();
            Long cId = Long.parseLong(frm.field("country").value());
            Users newed = saveUser(user, user.role, cId);
            if (newed != null) {
                return ok("1");
            } else {
                return ok(Message.emailError);
            }
        } else {
            return ok("error");
        }
    }

    public static Result activateNew(String email){
        Users user = Users.finderByMail(email);
        if( user == null ) return ok();
        user.stating = "";
        DynamicForm form = Form.form().bindFromRequest();
        List<Applicant> list = user.applicantList;
        for (Applicant app : list ){
            app.active = false;
            app.update();
        }
        user.password = BCrypt.hashpw(form.field("password").value(), BCrypt.gensalt());
        user.update();
        session("student", user.email);
        session("user", user.email);
        return ok("1");
    }



    public static Result activateLang(String str) {
        session("language", str);
        return redirect(routes.Application.index());
    }

//    public static Result saveApplication(String def) {
//        if (session("student") != null || session("registrar") != null) {
//            Form<Applied> f = Form.form(Applied.class).bindFromRequest();
//            Applied frm = f.get();
//
//            Applicant check = null;
//
//            if(session("student") != null ) {
//                 check = Applicant.finderByUser(Users.finderByMail(session("student")).id);
//            }
//            if(session("registrar") != null ) {
//                 check = Applicant.finderById(Long.parseLong(f.field("applicantId").value()));
//            }
//            if (check != null) {
//                Applied ap = Applied.byApp(check.id);
//
//                if (ap != null && ap.status) {
//                    return ok("done");
//                }
//                Applied newAp = new Applied();
//                if (ap != null) {
//                    newAp = ap;
//                }
//                newAp.applicant = check;
//                Payment payment = null;
//                Account account =null;
//                switch (def) {
//                    case "1":
//                        Long mood = Long.parseLong(f.field("mood").value());
//                        newAp.training = Training.finderById(mood);
//                        if (frm.year > 0) {
//                            newAp.year = frm.year;
//                        } else {
//                            newAp.year = 1;
//                        }
//                        break;
//                    case "2":
//                        newAp.bornCountry = frm.bornCountry;
//                        break;
//                    case "3":
//                        newAp.secondarySchool = frm.secondarySchool;
//                        newAp.firstPrincipal = frm.firstPrincipal;
//                        newAp.secondPrincipal = frm.secondPrincipal;
//                        newAp.firstGrade = frm.firstGrade;
//                        newAp.secondGrade = frm.secondGrade;
//                        break;
//                    case "4":
//                        newAp.feespayment = frm.feespayment;
//                        newAp.sponsorName = frm.sponsorName;
//                        newAp.sponsorPhone = frm.sponsorPhone;
//                        newAp.sponsorEmail = frm.sponsorEmail;
//
//                        check.needAccomodation = f.field("needAccomodation").value() != null;
//                        check.needCatering = f.field("needCatering").value() != null;
//                        check.update();
//
//                        break;
//                    case "5":
//                        newAp.statement = "";
//                        break;
//                    case "6":
//                        newAp.contactPerson = frm.contactPerson;
//                        newAp.contactEmail = frm.contactEmail;
//                        newAp.contactPhone = frm.contactPhone;
//                        break;
//                    case "7":
//                        newAp.disability = frm.disability;
//                        newAp.disabiltyDetails = frm.disabiltyDetails;
//                        break;
//                    case "8":
//                        newAp.publicInfo = frm.publicInfo;
//                        newAp.publicInfoDetails = frm.publicInfoDetails;
//                        newAp.applicationStatus = "";
//                        break;
//                    case "9":
//                        newAp.status = true;
//
//                        /////
//                       /* Account account = Account.finder.where()
//                                .eq("deleteStatus", false)
//                                .eq("applicant.id", newAp.applicant.id)
//                                .setMaxRows(1).findUnique();*/
//			            account = Account.finder.where().eq("deleteStatus", false)
//							.eq("applicant.id", newAp.applicant.id).setMaxRows(1).findUnique();
//                        if (account == null)
//                            Counts.createApplicantAccount(newAp.applicant);
//
//                        BankAccount bankAccount = null;
//                        List<BankAccount> bankAccounts = BankAccount.find.where()
//                                .eq("deleteStatus", false)
//                                .setMaxRows(1)
//                                .findList();
//                        for(BankAccount b : bankAccounts)
//                            bankAccount = b;
//
//                        Applicant applicant = check;
//                        double regAmount = newAp.training.iMode.intake.registrationFees;
//                        double amountRemain = applicant.getAmountToPay() - regAmount;
//                        payment = new Payment();
//                        payment.remain = amountRemain;
//                        payment.applicationFees = regAmount;
//			            account = Account.finder.where().eq("deleteStatus", false)
//							.eq("applicant.id", newAp.applicant.id).setMaxRows(1).findUnique();
//
//                        payment.account = account;
//                        payment.bankAccount = bankAccount;
//                        payment.paymentType = "bank slip";
//                        payment.save();
//                        break;
//                }
//                newAp.save();
//
//                if (def.equals("9")) {
//                    String messEmail = "Hi," + check.familyName + " " + check.firstName + " ";
//                    new Counts().sendUserEmail(check.user, "Application submission", messEmail + Template.appEmail);
//                    for (AcademicFiles fp : AcademicFiles.byProgram(newAp.training.iMode.campusProgram.program.id, newAp.applicant.id)) {
//
//                        Attachment n = Attachment.checker(check.id, fp.id);
//
//                        if (n == null) {
//                            n = new Attachment();
//                        } else {
//                            Verification vf = n.amV();
//                            if (vf != null) {
//                                vf.deleteStatus = true;
//                                vf.update();
//                            }
//                        }
//
//                        n.attachName = uploadFile(n.attachName, fp.uniqueName);
//
//                        n.app = check;
//                        n.file = fp;
//
//                        System.out.print("File :" +n.app.familyName + " :Name: " + n.attachName + "\n");
//                        n.save();
//
//                    }
//                  //  Payment payment = Payment.finder.where().eq("applicant.account.id", check.account.id).setMaxRows(1).findUnique();
//                    String key1 = "Payment";
//                    String key2 = "fees";
//                    String key3 = "Proof of payment";
//                    String key4 = "Application fees";
//		            payment=Payment.finder.where().eq("deleteStatus", false)
//							.eq("account.id", account.id).setMaxRows(1).findUnique();
//
//                    payment.attachment = Attachment.find.where().or(
//                                    Expr.like("file.fileName", "%" + key1 + "%"),
//                                    Expr.like("file.fileName",  "%" + key2 + "%")
//                            )
//                            .eq("app.id", check.id)
//                            .setMaxRows(1)
//                            .findUnique().attachName;
//                    payment.update();
//                    payment.attachment = Attachment.find.where().or(
//                                    Expr.like("file.fileName", "%" + key3 + "%"),
//                                    Expr.like("file.fileName",  "%" + key4 + "%")
//                            )
//                            .eq("app.id", check.id)
//                            .setMaxRows(1)
//                            .findUnique().attachName;
//                    payment.update();
//                    String s = "Your application is successfully submitted in registrar office. you will be notified on your email shortly. Thank you! ";
//                    return ok(views.html.student.registrationFinish.render(s));
//                }
//            }
//        }
//        return ok("1");
//    }

    public static Result saveApplication(String def) {
        if (session("student") != null || session("registrar") != null) {
            Form<Applied> f = Form.form(Applied.class).bindFromRequest();
            Applied frm = f.get();

            Applicant check = null;

            if(session("student") != null ) {
                check = Applicant.finderByUser(Users.finderByMail(session("student")).id);
            }
            if(session("registrar") != null ) {
                check = Applicant.finderById(Long.parseLong(f.field("applicantId").value()));
            }
            if (check != null) {
                Applied ap = Applied.byApp(check.id);

                if (ap != null && ap.status) {
                    for (AcademicFiles fp : AcademicFiles.byProgram(ap.training.iMode.campusProgram.program.id, ap.applicant.id)) {
                        Attachment n = Attachment.checker(check.id, fp.id);
                        n.file = fp;
                        n.attachName = uploadFile(n.attachName, fp.uniqueName);
                        n.update();
                    }
                    return ok("done");
                }
                Applied newAp = new Applied();
                if (ap != null) {
                    newAp = ap;
                }
                newAp.applicant = check;
                Payment payment = null;
                // Account account =null;
                switch (def) {
                    case "1":
                        Long mood = Long.parseLong(f.field("mood").value());
                        newAp.training = Training.finderById(mood);
                        if (frm.year > 0) {
                            newAp.year = frm.year;
                        } else {
                            newAp.year = 1;
                        }
                        break;
                    case "2":
                        newAp.bornCountry = frm.bornCountry;
                        break;
                    case "3":
                        newAp.secondarySchool = frm.secondarySchool;
                        newAp.firstPrincipal = frm.firstPrincipal;
                        newAp.secondPrincipal = frm.secondPrincipal;
                        newAp.firstGrade = frm.firstGrade;
                        newAp.secondGrade = frm.secondGrade;
                        break;
                    case "4":
                        newAp.feespayment = frm.feespayment;
                        newAp.sponsorName = frm.sponsorName;
                        newAp.sponsorPhone = frm.sponsorPhone;
                        newAp.sponsorEmail = frm.sponsorEmail;

                        check.needAccomodation = f.field("needAccomodation").value() != null;
                        check.needCatering = f.field("needCatering").value() != null;
                        check.update();

                        break;
                    case "5":
                        newAp.statement = "";
                        break;
                    case "6":
                        newAp.contactPerson = frm.contactPerson;
                        newAp.contactEmail = frm.contactEmail;
                        newAp.contactPhone = frm.contactPhone;
                        break;
                    case "7":
                        newAp.disability = frm.disability;
                        newAp.disabiltyDetails = frm.disabiltyDetails;
                        break;
                    case "8":
                        newAp.publicInfo = frm.publicInfo;
                        newAp.publicInfoDetails = frm.publicInfoDetails;
                        newAp.applicationStatus = "";
                        break;
                    case "9":
                        newAp.status = true;

                        /////

                        BankAccount bankAccount = null;
                        List<BankAccount> bankAccounts = BankAccount.find.where()
                                .eq("deleteStatus", false)
                                .setMaxRows(1)
                                .findList();
                        for(BankAccount b : bankAccounts){
                            bankAccount = b;
                        }
                        Applicant applicant = check;
                        Account account = Account.finder.where()
                                .eq("deleteStatus", false)
                                // .eq("applicant.id", newAp.applicant.id)
                                .eq("applicant.id", applicant.id)
                                .setMaxRows(1).findUnique();
                        if (account == null) {
                            // creating user account
                            //  Counts.createApplicantAccount(newAp.applicant);
                            Applied applied = applicant.applied;
                            Training training = applied.training;

                            Account account1 = new Account();
                            account1.applicant = applicant;
                            double amountToPay = training.schoolFees + training.iMode.intake.registrationFees + training.otherFees;
                            if (applicant.needCatering) {
                                amountToPay += training.restaurationFees;
                            }
                            if (applicant.needAccomodation) {
                                amountToPay += training.accomodationFees;
                            }
                            account1.amount = amountToPay * -1;
                            account1.save();
                            account = account1;
                        }
//                        System.out.print(account.amount+" Account" );
//                        System.out.print(bankAccount.bankName+" Bank account" );
                        payment = new Payment();
                        payment.account = account;
                        payment.bankAccount = bankAccount;
                        payment.paymentType = "bank slip";
                        payment.save();
                        double regAmount = newAp.training.iMode.intake.registrationFees;
                        double amountRemain = payment.account.applicant.getAmountToPay() - regAmount;
//                        System.out.print(regAmount+" Reg11 "+amountRemain+" Remain" );
                        payment.remain = amountRemain;
                        payment.applicationFees = regAmount;
                        payment.update();
                        break;
                }
                newAp.save();

                if (def.equals("9")) {
                    String messEmail = "Hi," + check.familyName + " " + check.firstName + " ";
                    new Counts().sendUserEmail(check.user, "Application submission", messEmail + Template.appEmail);
                    for (AcademicFiles fp : AcademicFiles.byProgram(newAp.training.iMode.campusProgram.program.id, newAp.applicant.id)) {

                        Attachment n = Attachment.checker(check.id, fp.id);

                        if (n == null) {
                            n = new Attachment();
                        } else {
                            Verification vf = n.amV();
                            if (vf != null) {
                                vf.deleteStatus = true;
                                vf.update();
                            }
                        }

                        n.attachName = uploadFile(n.attachName, fp.uniqueName);

                        n.app = check;
                        n.file = fp;

                        n.save();


                    }
                    //  Payment payment = Payment.finder.where().eq("applicant.account.id", check.account.id).setMaxRows(1).findUnique();
                    String key1 = "Payment";
                    String key2 = "fees";
                    String key3 = "Proof of payment";
                    String key4 = "Application fees";
                    payment.attachment = Attachment.find.where().or(
                            Expr.like("file.fileName", "%" + key1 + "%"),
                            Expr.like("file.fileName",  "%" + key2 + "%")
                    )
                            .eq("app.id", check.id)
                            .setMaxRows(1)
                            .findUnique().attachName;
                    payment.update();
                    payment.attachment = Attachment.find.where().or(
                            Expr.like("file.fileName", "%" + key3 + "%"),
                            Expr.like("file.fileName",  "%" + key4 + "%")
                    )
                            .eq("app.id", check.id)
                            .setMaxRows(1)
                            .findUnique().attachName;
                    payment.update();
                    String s = "Your application is successfully submitted in registrar office. you will be notified on your email shortly. Thank you! ";
                    return ok(views.html.student.registrationFinish.render(s));
                    // return redirect("Ntiri kuhagera");
                }
            }
        }
        return ok("1");
    }

    public static Result saveEmployee() {
        if (session("admin") != null) {
            Form<Employee> empFrm = Form.form(Employee.class).bindFromRequest();
//            Employee employee1 = empFrm.get();
            Unit unit = Unit.find(Long.parseLong(empFrm.field("unit").value()));
            if (unit == null) {
                return notFound();
            }
            Position position = Position.find(Long.parseLong(empFrm.field("position").value()));
            if (position == null) {
                return notFound();
            }
            Employee employee = new Employee();
            employee.employeeLastName = empFrm.field("employeeLastName").value();
            employee.employeeFirstName = empFrm.field("employeeFirstName").value();
            employee.position = position;
            employee.gender = empFrm.field("gender").value();
            if (empFrm.field("isHeadOfUnit").value() != null) {
                Employee head = Employee.finder.where().eq("isHeadOfUnit", true).eq("deleteStatus", false).setMaxRows(1).findUnique();
                if (head == null) {
                    employee.isHeadOfUnit = true;
                } else {
                    head.isHeadOfUnit = false;
                    head.update();
                    employee.isHeadOfUnit = true;
                }
            }
            employee.unit = unit;
            employee.isUser = Boolean.parseBoolean(empFrm.field("isUser").value());
            if (employee.exist()) return ok("Duplicate Entry, try again later with different input");
            employee.save();
            if (employee.isUser && !position.name.equalsIgnoreCase("Lecturer")) {
                Form<Users> frm = Form.form(Users.class).bindFromRequest();
                Users user = frm.get();
                Users newed = saveUser(user, employee);
            }
            if (employee.isUser && position.name.equalsIgnoreCase("Lecturer")) {
                Form<Users> frm = Form.form(Users.class).bindFromRequest();
                Users user = frm.get();
                Users newed = saveUser(user, employee);
                Lecture lecture = new Lecture();
                lecture.fName = empFrm.field("employeeFirstName").value();
                lecture.lName = empFrm.field("employeeLastName").value();
                lecture.user = newed;
                lecture.save();
                Roles role = Roles.finderByName("Instructor");
                UserRole userRole = new UserRole();
                userRole.user = newed;
                userRole.role = role;
                userRole.save();
            }
            return ok("1");
        } else {
            return ok("error");
        }
    }

    public static Result saveApplicant(String num) {
        if (session("student") != null) {
            Form<Applicant> f = Form.form(Applicant.class).bindFromRequest();
            Applicant ap = f.get();

            Users exUser = Users.finderByMail(session("student"));
            Applicant check = Applicant.finderByUserActive(exUser.id);


            Applicant neW = new Applicant();

            if (check != null) {
                neW = check;
            }

            if (num.equals("1")) {
                if(!neW.applied.training.iMode.campusProgram.program.cle){
                    neW.profile = uploadImage(neW.profile);
                }
                neW.firstName = ap.firstName;
                neW.familyName = ap.familyName;
                neW.motherName = ap.motherName;
                neW.fatherName = ap.fatherName;
                neW.gender = ap.gender;
                Country count = Country.finderById(Long.valueOf(ap.country));
                neW.country = count.nicename;

                exUser.phone = f.field("phone").value();
                exUser.code = count.phonecode;
                exUser.update();


                neW.maritalStatus = ap.maritalStatus;
                neW.nationality = ap.nationality;
                neW.birthDate = ap.birthDate;
                neW.birthPlace = ap.birthPlace;
                String nx = f.field("dist").value();
                if (nx != null) {
                    Long sec = Long.parseLong(nx);
                    neW.districts = Districts.finderById(sec);
                }
                neW.yearCompletion = ap.yearCompletion;
                neW.chosen = true;
                neW.state = ap.state;
                neW.nationalDentity = f.field("nationals").value();
                neW.user = Users.finderByMail(session("student"));
                neW.deleteStatus = false;
            } else if (num.equals("3")) {
                if(neW.applied.training.iMode.campusProgram.program.cle){
                   neW.educationBackground=ap.educationBackground;
                    neW.deleteStatus = false;
                }else{
                    neW.aSchool = ap.aSchool;
                    neW.experienceDraft = ap.experienceDraft;
                    neW.aFromu = ap.aFromu;
                    neW.aTo = ap.aTo;
                    neW.deleteStatus = false;
                }

                SimpleDateFormat date = new SimpleDateFormat("yyyy");
                int year = Integer.parseInt(date.format(new Date()));
                if (neW.aFromu > neW.aTo) {
                    neW.deleteStatus = false;
                    return ok(Message.invalidYear);
                } else if (neW.aTo > year || neW.aFromu > year) {
                    neW.deleteStatus = false;
                    return ok(Message.invalidYearNow);
                }
            }
            if (check != null) {
                neW.deleteStatus = false;
                neW.save();
            } else {
                neW.deleteStatus = false;
                neW.save();
            }
        }
        return ok("1");
    }

    public static Result saveIntake() {
        if (session("admin") != null) {
            Form<Intake> f = Form.form(Intake.class).bindFromRequest();
            Intake inT = f.get();


            Intake n = new Intake();
            n.intakeName = inT.intakeName;

            String fileder = f.field("intakeYear").value();

            if (fileder != null && Vld.isNumeric(fileder)) {
                Long yId = Long.parseLong(fileder);
                n.year = AcademicYear.finderById(yId);
            }

            n.registrationFees = inT.registrationFees;
            n.isClosed = Boolean.parseBoolean(f.field("isClosed").value());
            n.save();
            return ok("1");
        } else {
            return ok("error");
        }
    }

    public static Result saveModule() {
        if (session("admin") != null || session("registrar") != null || session("VRAC") != null) {
            Form<Module> moduleForm = Form.form(Module.class).bindFromRequest();
            Module module = moduleForm.get();
            Module module1 = new Module();
            module1.moduleName = module.moduleName;
            module1.moduleCode = module.moduleCode;
            module1.orderNumber = module.orderNumber;
            module1.credits = module.credits;
            module1.catMax = module.catMax;
            module1.examMax = module.examMax;
            module1.reMax = module.reMax;
            module1.reseatResearchMax = module.reseatResearchMax;
            module1.hasComponent = module.hasComponent;
            module1.minMarks = module.minMarks;
            module1.resitAmount = module.resitAmount;
            module1.retakeAmount = module.retakeAmount;
            Long lectId = Long.parseLong(moduleForm.field("lecting").value());
            String moduleInternship = moduleForm.field("moduleInternship").value();
            module1.moduleInternship = moduleInternship;
            module1.lecture = Lecture.finderById(lectId);
            Long programID = Long.parseLong(moduleForm.field("pro").value());
            module1.program = Program.finderById(programID);
            if (module1.exist()) return ok(Message.moduleExisted);
            module1.save();
            if (module.hasComponent) {
                Component c = new Component();
                c.compName = module1.moduleName;
                c.credits = module1.credits;
                c.module = module1;
                c.code = module1.moduleCode;
                if (c.notExist()) c.save();
            }
            return ok("1");
        } else {
            return ok("error");
        }
    }

    public static Result saveLecture() {
        if (session("admin") != null) {

            Form<Lecture> lectureForm = Form.form(Lecture.class).bindFromRequest();
            Lecture lecture = lectureForm.get();

            Form<Users> uForm = Form.form(Users.class).bindFromRequest();
            Users u = uForm.get();

            Ebean.beginTransaction();
            try {
                u.names = lecture.fName + " " + lecture.lName;
                Long cId = Long.parseLong(uForm.field("country").value());
                Users fUser = saveUser(u, "Instructor", cId);

                if (fUser == null) return ok(Message.emailError);

                Lecture lecture1 = new Lecture();
                lecture1.fName = lecture.fName;
                lecture1.lName = lecture.lName;
                lecture1.qualification = lecture.qualification;
                lecture1.specialization = lecture.specialization;
                lecture1.user = fUser;
                lecture1.save();

                Roles role = Roles.finderByName("Instructor");
                if (role == null) return notFound();
                UserRole userRole = new UserRole();
                userRole.user = fUser;
                userRole.role = role;
                userRole.save();
                Ebean.commitTransaction();
                return ok("1");
            } finally {
                Ebean.endTransaction();
            }
        } else {
            return ok("error");
        }
    }

    public static Result saveFees() {
        if (session("admin") != null) {
            Form<SchoolFees> s = Form.form(SchoolFees.class).bindFromRequest();
            SchoolFees sp = s.get();

            SchoolFees xp = new SchoolFees();
            xp.feesAmount = sp.feesAmount;
            xp.feesDetails = sp.feesDetails;
            Long idea = Long.parseLong(s.field("intaker").value());
            xp.intake = Intake.finderById(idea);
            Long studyModeId = Long.parseLong(s.field("studyModeId").value());
            xp.programMode = CampusProgramMode.finderById(studyModeId);
            xp.sessionMode = SessionMode.finderById(Long.valueOf(s.field("sessionId").value()));
            xp.save();

            return ok("1");
        } else {
            return ok("error");
        }
    }
    public static Result saveSubAssignmentMark(Long id) {
        if (session("Instructor") != null || session("Coordinator") != null || session("mark_officer") != null) {
            Form<Submission> s = Form.form(Submission.class).bindFromRequest();
            if (s.hasErrors()) return ok(s.errorsAsJson());
            Submission submission = Submission.finderById(id);
            if (submission == null) return ok("Error not found");
            float ma = Assignment.finderById(Long.parseLong(s.field("assignmentId").value())).max;
            Users user = null;
            if(session("Instructor") != null) {
                user  = Application.Ins("Instructor");
            }
            if(session("Coordinator") != null) {
                user  = Application.Ins("Coordinator");
            }
            if(session("mark_officer") != null) {
                user  = Application.Ins("mark_officer");
            }
            if(submission.assignment.types.equalsIgnoreCase("research")){
                SubMark subMark = new SubMark();
                subMark.student = submission.student;
                subMark.component = submission.assignment.component;
                subMark.reResult = Float.parseFloat(s.field("resultResult").value());
                if (ma < Float.parseFloat(s.field("resultResult").value())) return ok("The maximum is: " + ma);
                subMark.doneBy = user.print();
                subMark.save();
                AssignmentResult assignmentResult = new AssignmentResult();
                assignmentResult.assignment = Assignment.finderById(Long.parseLong(s.field("assignmentId").value()));
                assignmentResult.student = Student.finderById(Long.parseLong(s.field("studentId").value()));
                assignmentResult.result = Double.parseDouble(s.field("resultResult").value());
                if (ma < assignmentResult.result) return ok("The maximum allowed is: " + ma);
                submission.status = "viewed";
                submission.doneBy = user.print();
                assignmentResult.doneBy = user.print();
                submission.update();
                assignmentResult.save();
            }
            if(submission.assignment.types.equalsIgnoreCase("assignment")) {
                AssignmentResult assignmentResult = new AssignmentResult();
                assignmentResult.assignment = Assignment.finderById(Long.parseLong(s.field("assignmentId").value()));
                assignmentResult.student = Student.finderById(Long.parseLong(s.field("studentId").value()));
                assignmentResult.result = Double.parseDouble(s.field("resultResult").value());
                if (ma < Float.parseFloat(s.field("resultResult").value())) return ok("The maximum allowed is: " + ma);
                submission.status = "viewed";
                submission.doneBy = user.print();
                submission.update();
                assignmentResult.doneBy = user.print();
                assignmentResult.save();
            }
            if(submission.assignment.types.equalsIgnoreCase("resitResearch")) {
                AssignmentResult assignmentResult = new AssignmentResult();
                assignmentResult.assignment = Assignment.finderById(Long.parseLong(s.field("assignmentId").value()));
                assignmentResult.student = Student.finderById(Long.parseLong(s.field("studentId").value()));
                assignmentResult.result = Double.parseDouble(s.field("resultResult").value());
                if (ma < Float.parseFloat(s.field("resultResult").value())) return ok("The maximum allowed is: " + ma);
                submission.status = "viewed";
                submission.doneBy = user.print();
                submission.update();
                assignmentResult.isResitResult = true;
                assignmentResult.doneBy = user.print();
                assignmentResult.save();
            }
            return ok("1");
        }
        return ok("Not allowed to perform this");
    }
    public static Result changeSubAssignmentMark(Long id) {
        if (session("Instructor") != null || session("Coordinator") != null || session("mark_officer") != null) {
            Form<Submission> s = Form.form(Submission.class).bindFromRequest();
            if (s.hasErrors()) return ok(s.errorsAsJson());
            Submission submission = Submission.finderById(id);
            if (submission == null) return ok("Error not found");
            float ma = Assignment.finderById(Long.parseLong(s.field("assignmentId").value())).max;
            Users user = null;
            if(session("Instructor") != null) {
                user  = Application.Ins("Instructor");
            }
            if(session("Coordinator") != null) {
                user  = Application.Ins("Coordinator");
            }
            if(session("mark_officer") != null) {
                user  = Application.Ins("mark_officer");
            }
                SubMark subMark1 = SubMark.find.where()
                        .eq("component.id", Assignment.finderById(Long.parseLong(s.field("assignmentId").value())).component.id)
                        .eq("student.id", Student.finderById(Long.parseLong(s.field("studentId").value())).id)
                        .setMaxRows(1)
                        .orderBy("id asc")
                        .findUnique();
                if(subMark1 != null) {
                    subMark1.student = submission.student;
                    subMark1.component = submission.assignment.component;
                    subMark1.reResult = Float.parseFloat(s.field("resultResult").value());
                    if (ma < Float.parseFloat(s.field("resultResult").value())) return ok("The maximum is: " + ma);
                    subMark1.doneBy = user.print();
                    subMark1.update();
                }
                AssignmentResult assignmentResult = AssignmentResult.find.where()
                    .eq("assignment.id", submission.assignment.id)
                    .eq("student.id", submission.student.id)
                    .eq("deleteStatus",false)
                    .setMaxRows(1)
                    .orderBy("id desc")
                    .findUnique();
                    assignmentResult.assignment = Assignment.finderById(Long.parseLong(s.field("assignmentId").value()));
                    assignmentResult.student = Student.finderById(Long.parseLong(s.field("studentId").value()));
                    assignmentResult.result = Double.parseDouble(s.field("resultResult").value());
                    if (ma < assignmentResult.result) return ok("The maximum allowed is: " + ma);
                    submission.status = "viewed";
                    submission.doneBy = user.print();
                    assignmentResult.doneBy = user.print();
                    submission.update();
                    assignmentResult.update();
            return ok("1");
        }
        return ok("Not allowed to perform this");
    }



    public static Result saveSubAssignmentMarkGroup(Long id) {
        if (session("Instructor") != null || session("Coordinator") != null || session("mark_officer") != null || session("registrar") != null) {
            Form<GroupSubmission> s = Form.form(GroupSubmission.class).bindFromRequest();
            GroupSubmission submission = GroupSubmission.finder.byId(id);
            Lecture lect = submission.assignment.lecture;
            if (lect == null) return ok("0");
            if (submission == null) return ok("Error not found");

            AssignmentResult result=new AssignmentResult();
            result.assignment=submission.assignment;
            result.student=submission.student;
            result.result= Double.parseDouble(s.field("result").value());

            float ma = submission.assignment.max;
            if (ma < result.result)
                return ok("You can't go beyond " + ma);

            submission.status = "viewed";
            submission.update();
            result.save();
            submission.isMarked = true;
            submission.update();
            return ok("1");
        }
        return ok("Not allowed to perform this action");
    }
    public static Result saveSubAssignmentSameMarkGroup(Long id) {
        if (session("Instructor") != null || session("Coordinator") != null || session("mark_officer") != null || session("registrar") != null) {
            Form<GroupSubmission> s = Form.form(GroupSubmission.class).bindFromRequest();
            Assignment assignment = Assignment.finder.byId(id);
            Lecture lect = assignment.lecture;
            if (lect == null) return ok("0");
            if (assignment == null) return ok("Error not found");
            List<GroupSubmission> subs = GroupSubmission.finder.setDistinct(true).where()
                    .eq("deleteStatus",false)
                    .eq("status","submitted")
                    .eq("isMarked",false)
                    .eq("assignment.id", id)
                    .findList();

            for (GroupSubmission gs : subs){
                AssignmentResult result = new AssignmentResult();
                result.assignment=gs.assignment;
                result.student=gs.student;
                result.result=Double.parseDouble(s.field("result").value());
                float ma = gs.assignment.max;
                if (ma < result.result)
                    return ok("You can't go beyond " + ma);
                gs.status = "viewed";
                gs.update();
                result.save();
                gs.isMarked = true;
                gs.update();
            }
            return ok("1");
        }
        return ok("Not allowed to perform this action");
    }
    public static Result cancelSubmissionPost(Long id) {
        if (session("Instructor") != null || session("Coordinator") != null || session("mark_officer") != null || session("registrar") != null) {
            Form<GroupSubmission> s = Form.form(GroupSubmission.class).bindFromRequest();
            Assignment assignment = Assignment.finder.byId(id);
            Lecture lect = assignment.lecture;
            if (lect == null) return ok("0");
            if (assignment == null) return ok("Error not found");
            List<GroupSubmission> subs = GroupSubmission.finder.setDistinct(true).where()
                    .eq("deleteStatus",false)
                    .eq("status","submitted")
                    .eq("isMarked",false)
                    .eq("assignment.id", id)
                    .findList();
            for (GroupSubmission gs : subs){
                gs.status = "pending";
                gs.isMarked = false;
                gs.update();
            }
            return ok("1");
        }
        return ok("Not allowed to perform this action");
    }

    public static Result addMember(Long grId) {
        Form<GroupMembers> userForm = Form.form(GroupMembers.class).bindFromRequest();
        //Projects pro = userForm.get();

        Map<String, String[]> map = request().body().asFormUrlEncoded();
        String[] checkedVal = map.get("students");
        for (String one : checkedVal) {

            GroupMembers d = new GroupMembers();
            Long stId = Long.parseLong(one);
            Student mod = Student.finderById(stId);
            d.student = mod;

            d.groups = Groups.finderById(grId);

            if (d.selfCheck()) d.save();
        }
        return ok("ok");
    }

    public static Result saveAttendance(Long scheduleId) {
        if ( isInstructor() || isCleOrCoordinator()  || isRegistrar() ) {
            Schedule sched = Schedule.finderById(scheduleId);
            if (sched == null) {
                return ok("Server error unknown 500 status");
            }
            // validate attendance
            Attendance attendance1 = Attendance.find.where()
                    .eq("deleteStatus", false)
                    .eq("schedule.id", sched.id).setMaxRows(1)
                    .findUnique();
            if (attendance1 != null) {
                return ok("Attendance is already recorded");
            }

            Component component = Component.finderById(sched.component.id);
            if (component == null) {
                return notFound();
            }

            Map<String, String[]> map = request().body().asFormUrlEncoded();
            String[] studentIds = map.get("attender[]");
            String[] studentIds2 = map.get("attendedp[]");

            if(studentIds == null){
                return ok(" Not allowed to make an attendance when there is no student present in the morning!");
            }

            if(studentIds2 == null){
                return ok("  Not allowed to make an attendance when there is no student present in the after noon!");
            }

            List<String> strings = new ArrayList<>(Arrays.asList(studentIds));
            List<String> strings2 = new ArrayList<>(Arrays.asList(studentIds2));

            List<Student> students = Student.finder.where()
                    .eq("deleteStatus", false)
                    .eq("training.id", sched.training.id)
                    .findList();

            for (Student student : students) {
                Attendance attendance = new Attendance();
                attendance.attended = strings.contains(String.valueOf(student.id));
                attendance.attendedp = strings2.contains(String.valueOf(student.id));
                attendance.student = student;
                attendance.schedule = sched;
                attendance.component = component;
                attendance.save();
            }
            return ok("1");
        }
        return ok("0");
    }






    public static Result saveRoom() {
        if (session("admin") != null || session("VRAC") != null || session("registrar") != null) {
            Form<Room> s = Form.form(Room.class).bindFromRequest();
            Room acc = s.get();

            Room nG = new Room();
            nG.roomName = acc.roomName;
            nG.roomCode = acc.roomCode;

            Long campId = Long.parseLong(s.field("camp").value());


            nG.campus = Campus.finderById(campId);

            nG.flowNumber = acc.flowNumber;
            nG.numberSeat = acc.numberSeat;
            nG.roomType = acc.roomType;

            if (!nG.notExist()) return ok("0");

            nG.save();


            return ok("1");
        }
        return ok("0");
    }
    public static Result saveGroup(Long compId) {
        if (session("Instructor") != null || session("Coordinator") != null) {
            Ebean.beginTransaction();
            try {
                Form<Groups> s = Form.form(Groups.class).bindFromRequest();
                Groups acc = s.get();
                Groups nG = new Groups();
                nG.groupName = acc.groupName;
                Component component = Component.finderById(compId);
                nG.component = component;
                try {
                    Long trId = Long.parseLong(s.field("trainingId").value());
                    nG.training = Training.finderById(trId);
                } catch (NumberFormatException e) {
                    Ebean.rollbackTransaction();
                    return ok("Please.. You must put at least one student in the group");
                }
                if (!nG.notExist()) {
                    Ebean.rollbackTransaction();
                    return ok("You've already created this group,i.e.Group already exist.");
                }
                nG.save();
                Map<String, String[]> map = request().body().asFormUrlEncoded();
                String[] studentIds = map.get("studentIds[]");
                if (studentIds.length <= 0) {
                    Ebean.rollbackTransaction();
                    return ok("Please.. You must put at least one student in the group");
                }
                saveGroupMemmber(studentIds, nG.id);
                Ebean.commitTransaction();
                return ok("1");
            } finally {
                return ok("1");
            }
        }
        return ok("No access found to perform this action");
    }
    private static void saveGroupMemmber(String[] studentIds, long grId) {
        for (String one : studentIds) {
            GroupMembers groupMembers = new GroupMembers();
            Long stId = Long.parseLong(one);
            groupMembers.student = Student.finderById(stId);
            groupMembers.groups = Groups.finderById(grId);
            if (groupMembers.selfCheck()) {
                groupMembers.save();
            }
        }
    }
    public static Result saveGroupExist() {
        if (session("Instructor") != null || session("Coordinator") != null || session("mark_officer") != null) {
            try {
                Form<Groups> s = Form.form(Groups.class).bindFromRequest();
                Groups acc = s.get();
                Groups group = Groups.finderById(Long.parseLong(s.field("groupId").value()));
                Map<String, String[]> map = request().body().asFormUrlEncoded();
                String[] studentIds = map.get("studentId[]");
                if (studentIds.length <= 0) {
                    return ok("Please.. You must put at least one student in the group");
                }
                Training training = Training.finderById(group.training.id);
                for (String one : studentIds) {
                    GroupMembers groupMembers = new GroupMembers();
                    Long stId = Long.parseLong(one);
                    groupMembers.student = Student.finderById(stId);
                    groupMembers.groups = group;
                    if (groupMembers.selfCheck()) {
                        groupMembers.save();
                    }
                }
                return ok(views.html.register.tStudentGroup.render(training.Mystudents(), group.component.id, group));
            } finally {
                return ok("1");
            }
        }
        return ok("No access found to perform this action");
    }
    public static Result saveAssignment() {
        if (session("Instructor") == null && session("Coordinator") == null && session("mark_officer") == null && session("registrar") == null) {
            return ok("error");
        }
        Form<Assignment> sForm = Form.form(Assignment.class).bindFromRequest();

        String lecturerId = sForm.field("lecturerId").value();
        String componentIdId = sForm.field("comp").value();
        String trainingId = sForm.field("trainings").value();

        if (lecturerId.trim().equals("") || componentIdId.trim().equals("") || trainingId.trim().equals("")) {
            return ok("error");
        }

        Long compnt = Long.parseLong(componentIdId);
        Long trainId = Long.parseLong(trainingId);

        Lecture lecturer = Lecture.finderById(Long.parseLong(lecturerId));
        if (lecturer == null) return ok("error");

        Assignment acc = sForm.get();

        DateTime d1 = new DateTime(new Date()).withTimeAtStartOfDay();
        DateTime d2 = new DateTime(acc.startDate).withTimeAtStartOfDay();
        Assignment nAcc = new Assignment();
        nAcc.description = acc.description;
        nAcc.assignmentTitle = acc.assignmentTitle;
        nAcc.duration = acc.duration;
        nAcc.startDate = acc.startDate;
        Date dt = acc.endDate;
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        c.add(Calendar.DATE, 1);
        dt = c.getTime();
        nAcc.endDate = dt;
        nAcc.max = acc.max;
        nAcc.types = acc.types;
        nAcc.deleteStatus = false;
        nAcc.grouped = acc.grouped;
        nAcc.lecture = lecturer;
        nAcc.component = Component.finderById(compnt);
        nAcc.training = Training.finderById(trainId);
        nAcc.save();
        if (acc.grouped) {
            Long groupId = Long.parseLong(sForm.field("groupId").value());
            Groups group = Groups.finderById(groupId);
            if (!Objects.equals(groupId, (long) -1) && groupId != 0) {
            List<GroupMembers> studentLst = GroupMembers.finder
                    .setDistinct(true)
                    .where()
                    .eq("groups.id", group.id)
                    .findList();//Not same for all groups
                nAcc.group = Groups.finderById(groupId);
                for(GroupMembers s : studentLst) {
                    GroupSubmission gs = new GroupSubmission();
                    gs.groups = Groups.finderById(groupId);
                    gs.assignment = Assignment.finderById(nAcc.id);
                    gs.student = Student.finderById(s.student.id);
                    gs.comment = "";
                    gs.status = "pending";
                    if(gs.notExist()) {
                        gs.save();
                    }
                }
                nAcc.group = group;
                nAcc.update();
            }else{// Same for all groups
                List<GroupMembers> groupMembers = GroupMembers.finder.where()
                        .eq("student.training.id", trainId)
                        .eq("student.deleteStatus", false)
                        .findList();
                for(GroupMembers s : groupMembers) {
                    GroupSubmission gs = new GroupSubmission();
                    gs.assignment = Assignment.finderById(nAcc.id);
                    gs.student = Student.finderById(s.student.id);
                    gs.groups = s.groups;
                    gs.comment = "";
                    gs.status = "pending";
                    if(gs.notExist2()) {
                        gs.save();
                    }
                }
                nAcc.group = null;
                nAcc.update();
            }
        }
        List<Student> studentList = Student.byTraining(trainId);
        for(Student s : studentList){
            if(!nAcc.grouped) {
                Submission submission = new Submission();
                submission.assignment = Assignment.finderById(nAcc.id);
                submission.student = Student.finderById(s.id);
                submission.comment = Component.finderById(compnt).compName;
                submission.attachment = "";
                submission.status = "pending";
                submission.save();
            }
        }
        return redirect("/%2Fcoordinator%2FassignmentPage%2F#tab1");

    }
    public static Result updateAssignment(Long id) {
        Users u = Application.Ins("Instructor");
        if (session("Instructor") != null && u != null) {
            Lecture lecture = Application.activeL(u);
            if (lecture == null) return ok("Error");
            Form<Assignment> sForm = Form.form(Assignment.class).bindFromRequest();
            Assignment acc = sForm.get();
            DateTime d1 = new DateTime(new Date()).withTimeAtStartOfDay();
            DateTime d2 = new DateTime(acc.startDate).withTimeAtStartOfDay();
            Assignment nAcc = Assignment.finderById(id);
            nAcc.assignmentTitle = acc.assignmentTitle;
            nAcc.duration = acc.duration;
            nAcc.startDate = acc.startDate;
            nAcc.endDate = acc.endDate;
            nAcc.deleteStatus = false;
            nAcc.grouped = acc.grouped;
            nAcc.types = acc.types;
            nAcc.description = acc.description;
            nAcc.max = acc.max;
            nAcc.lecture = lecture;
            nAcc.attachment = uploadFile(acc.attachment, "attachment");
            Long compnt = Long.parseLong(sForm.field("comp").value());
            nAcc.component = Component.finderById(compnt);
            Long trainId = Long.parseLong(sForm.field("trainings").value());
            nAcc.training = Training.finderById(trainId);
            nAcc.update();
            return ok("1");
        }
        if (session("Coordinator") != null || session("mark_officer") != null) {
            Lecture lecture = Lecture.finderById(Assignment.finderById(id).lecture.id);
            if (lecture == null) return ok("error");
            Form<Assignment> sForm = Form.form(Assignment.class).bindFromRequest();
            Assignment acc = sForm.get();
            DateTime d1 = new DateTime(new Date()).withTimeAtStartOfDay();
            DateTime d2 = new DateTime(acc.startDate).withTimeAtStartOfDay();
            Assignment nAcc = Assignment.finderById(id);
            nAcc.description = acc.description;
            nAcc.assignmentTitle = acc.assignmentTitle;
            nAcc.duration = acc.duration;
            nAcc.startDate = acc.startDate;
            nAcc.endDate = acc.endDate;
            nAcc.deleteStatus = false;
            nAcc.grouped = acc.grouped;
            nAcc.max = acc.max;
            nAcc.lecture = lecture;
            nAcc.attachment = uploadFile(acc.attachment, "attachment");
            Long compnt = Long.parseLong(sForm.field("comp").value());
            nAcc.component = Component.finderById(compnt);
            Long trainId = Long.parseLong(sForm.field("trainings").value());
            nAcc.training = Training.finderById(trainId);
            nAcc.update();
            return ok("1");
        } else {
            return ok("error");
        }
    }
    public static Result uploadAssignment(Long id) {
        Users u = Application.Ins("Instructor");
        if (session("Instructor") != null && u != null) {
            Lecture lecture = Application.activeL(u);
            if (lecture == null) return ok("Error");
            Form<Assignment> sForm = Form.form(Assignment.class).bindFromRequest();
            Assignment acc = sForm.get();
            Assignment nAcc = Assignment.finderById(id);
            nAcc.lecture = lecture;
            nAcc.attachment = uploadFile(acc.attachment, "attachment");
            nAcc.update();
            return ok("1");
        }
        if (session("Coordinator") != null || session("mark_officer") != null) {
            Lecture lecture = Lecture.finderById(Assignment.finderById(id).lecture.id);
            if (lecture == null) return ok("error");
            Form<Assignment> sForm = Form.form(Assignment.class).bindFromRequest();
            Assignment acc = sForm.get();
            Assignment nAcc = Assignment.finderById(id);
            nAcc.lecture = lecture;
            nAcc.attachment = uploadFile(acc.attachment, "attachment");
            nAcc.update();
            return ok("1");
        } else {
            return ok("error");
        }
    }
    public static Result deleteAssignment(Long id) {
        Assignment assignment = Assignment.finderById(id);
        if (assignment != null) {
            assignment.deleteStatus = true;
            assignment.update(id);
            List<AssignmentResult> assignmentResults = AssignmentResult.find.where()
                    .eq("assignment.id", assignment.id)
                    .eq("assignment.deleteStatus", false)
                    .eq("deleteStatus", false)
                    .findList();
            for(AssignmentResult a : assignmentResults){
                a.deleteStatus = true;
                a.update();
            }
            return ok("1");
        } else {
            return ok("error");
        }
    }

    public static Result saveComponent() {
        if (session("admin") != null || session("VRAC") != null || session("registrar") != null) {
            Form<Component> sForm = Form.form(Component.class).bindFromRequest();
            Component acc = sForm.get();

            Component nAcc = new Component();
            nAcc.compName = acc.compName;
            nAcc.code = acc.code;
            nAcc.credits = acc.credits;
            Long compnt = Long.parseLong(sForm.field("mod").value());
            nAcc.module = Module.finderById(compnt);


            nAcc.save();
            return ok("1");
        } else {
            return ok("error");
        }
    }

    public static Result saveYear() {
        if (session("admin") != null) {
            Form<AcademicYear> sForm = Form.form(AcademicYear.class).bindFromRequest();
            AcademicYear acc = sForm.get();

            if (acc.yearName.trim().isEmpty()) {
                return ok("Year is required");
            }

            AcademicYear nAcc = new AcademicYear();
            nAcc.yearName = acc.yearName.trim();
            nAcc.deleteStatus = false;
            nAcc.status = false;
            if(AcademicYear.yearExist(nAcc.yearName)) return ok("Year already exist");
            if(nAcc.yearName.matches("\\d+")){
                int yearDifference=Integer.parseInt(nAcc.yearName)-LocalDate.now().getYear();
                if(yearDifference<0){
                    return ok("Accepted year cant under go "+LocalDate.now().getYear());
                }else if(yearDifference>1){
                    return ok("Accepted year has to be a year following "+LocalDate.now().getYear()+" or the current year");
                }
            }
            nAcc.save();
            return ok("1");

        } else {
            return ok("error");
        }
    }

    public static Result saveProgram() {
        if (session("admin") != null) {
            Form<Program> sForm = Form.form(Program.class).bindFromRequest();
            Program acc = sForm.get();

            Program nAcc = new Program();
            nAcc.cle = acc.cle;
            nAcc.programName = acc.programName;
            nAcc.programAcronym = acc.programAcronym;

            nAcc.deleteStatus = false;
            nAcc.save();
            return ok("1");
        } else {
            return ok("error");
        }
    }

    public static Result saveCampus() {
        if (session("admin") != null) {
            Form<Campus> sForm = Form.form(Campus.class).bindFromRequest();
            Campus acc = sForm.get();

            Campus nAcc = new Campus();
            nAcc.campusName = acc.campusName;
            nAcc.campusLocation = acc.campusLocation;
            nAcc.deleteStatus = false;
            nAcc.save();
            return ok("1");
        } else {
            return ok("error");
        }
    }
    public static Result saveStudent() {
        Form<Users> frm = Form.form(Users.class).bindFromRequest();
        Users user = frm.get();
        Long cId = Long.parseLong(frm.field("country").value());
        Users using = saveUser(user, "student", cId);
        if (using != null) {
            session("student", user.email);
            session("user", user.email);
            new Counts().sendUserEmail(user, "Account created", Template.registrationEmail);
            UserRole role = new UserRole();
            role.user = using;
            role.role = Roles.finderByName("student");
            if (role.notExist() && (role.role != null))
                role.save();
            return ok("1");
        } else {
            String route = routes.Registration.activateNew(user.email).url();
            response().setHeader("new-acc",route);
            return ok(Message.emailError);
        }
    }
    public static Result saveAnnouncement() {
        Form<AnounceRole> form = Form.form(AnounceRole.class).bindFromRequest();
        Map<String, String[]> map = request().body().asFormUrlEncoded();
        String[] checkedVal = map.get("roleUserId");
        AnounceRole anounceRole1 = form.get();
        if (checkedVal == null && !checkedVal.equals(0)) {
            return ok("Role not found");
        }
        Announce n = new Announce();
        n.title = form.field("title").value();
        n.content = form.field("content").value();
        n.date = new Date();
        try {
            n.pubDate = new SimpleDateFormat("yyyy-MM-dd").parse(form.field("pubDate").value());
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
        n.category = form.field("category").value();
        n.status = "active";
        n.save();
        for (String roleString : checkedVal) {
            Long roleId = Long.parseLong(roleString);
            Roles role = Roles.finder.byId(roleId);
            AnounceRole anounceRole = new AnounceRole();
            anounceRole.announce = n;
            anounceRole.role = role;
            anounceRole.save();
            List<UserRole> userRoles = UserRole.finder.where()
                    .eq("role.id", anounceRole.role.id)
                    .findList();
            for(UserRole ur : userRoles){
                List<Users> usersList = ur.myUsers();
                for(Users user : usersList){
                        new Counts().sendUserEmail(user, "ILPD Announcement/Event ", n.title+" "+n.content);
                    }
            }
        }
        return redirect("/%2Fregister%2FgetAnnounceMentPage%2F#tab7");

    }

    public static Result saveSchedule() {
        if ( isRegistrarOrCle() || isCleManager() || isCoordinator()) {
            Form<Schedule> s = Form.form(Schedule.class).bindFromRequest();
           // Schedule sd = s.get();
            DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
            DateFormat dayFomater = new SimpleDateFormat("EEEE");
            try {
                Date startDate = formatter.parse(s.field("startDate").value());
                Date endDate = formatter.parse(s.field("endDate").value());

                //                today's date
                DateTime dateTime = new DateTime(new Date()).withTimeAtStartOfDay();

                DateTime dateTime1 = new DateTime(startDate).withTimeAtStartOfDay();

                DateTime endTime = new DateTime(endDate).withTimeAtStartOfDay();
                /// end date cannot be before start date
                if (endTime.isBefore(dateTime1)) {
                    return ok("End date cannot be before start date of the component schedule");
                }

                DateRange dateRange = DateRange.finderById(Long.parseLong(s.field("dateRange").value()));
                if (dateRange == null) {
                    return notFound();
                }
                // check if the end date of schedule component selected is after date range saved
                if (new DateTime(dateRange.endDate).withTimeAtStartOfDay().isBefore(endTime)) {
                    return ok("End date of component schedule is out of the date range selected");
                }

                long interVal = 24 * 1000 * 60 * 60;// 1 hour in milli
                long endTimeDate = endDate.getTime();
                long currentTime = startDate.getTime();
                Date date;
                String day;
                while (currentTime <= endTimeDate) {

                    Schedule newSd = new Schedule();
                    date = new Date(currentTime);
                    newSd.date = formatter.parse(formatter.format(date));
                    newSd.room = Room.finderById(Long.parseLong(s.field("rooms").value()));
                    Training training = Training.finderById(Long.parseLong(s.field("iMode").value()));
                    //newSd.training = Training.finderById(Long.parseLong(s.field("train").value()));
                    newSd.lecture = Lecture.finderById(Long.parseLong(s.field("lect").value()));
                    newSd.component = Component.finderById(Long.parseLong(s.field("component").value()));
                    newSd.dateRange = dateRange;
                    newSd.training = training;
                    newSd.componentType = s.field("componentType").value();
                    newSd.assignment = s.field("assignment").value();
                    newSd.exam = s.field("exam").value();
                    newSd.startHour = s.field("startHour").value();
                    newSd.endHour = s.field("endHour").value();

                    // validation
                    if(!training.iMode.campusProgram.program.cle) {
                        //check if this teacher is already taken
                        if (newSd.isTeacherTaken()) {
                            return ok("The teacher is already assign to teach this time in this class");
                        }
                        if (newSd.isNotSameCampus()) {
                            return ok("Choosen room is not in the same campus with chosen period");
                        }
                        //check if this component is already taken
                        if (newSd.isRoomTaken()) {
                            return ok("The room is taken to be used in this time");
                        }
                        day = dayFomater.format(date);
                        if (newSd.training.iMode.sessionMode.session.sessionName.equalsIgnoreCase("Day") || newSd.training.iMode.sessionMode.session.sessionName.equalsIgnoreCase("Evening")) {
                            if ((day.equalsIgnoreCase("Saturday") || day.equalsIgnoreCase("Sunday"))) {
                                currentTime += interVal;//increment for one day
                                continue;
                            }
                        }
                    }
                    newSd.save();
                    currentTime += interVal;//increment for one day
                }

                return ok("1");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return ok("0");
    }

    private static void saveTimeTable(Form<Schedule> s) {

    }

    public static Result saveDays() {
        if (session("admin") != null) {
            Form<Days> d = Form.form(Days.class).bindFromRequest();
            Days st = d.get();

            Days studyDay = new Days();
            studyDay.dayName = st.dayName;

            if (studyDay.exist()) return ok(Message.dayExist);

            studyDay.save();

            return ok("1");
        }
        return ok("0");
    }

    public static Result daysAssign() {
        if (session("admin") != null) {
            Form<DaySession> d = Form.form(DaySession.class).bindFromRequest();

            DaySession newDay = new DaySession();

            Long dayId = Long.parseLong(d.field("myDay").value());
            newDay.day = Days.finderById(dayId);

            Long sessionId = Long.parseLong(d.field("mySession").value());
            newDay.session = Session.finderById(sessionId);

            if (newDay.exist()) return ok(Message.daySessionError);

            newDay.save();

            return ok("1");

        }
        return ok("0");
    }

    public static Result hoursAssign() {
        if (session("admin") != null) {
            Form<HourSession> d = Form.form(HourSession.class).bindFromRequest();

            HourSession newHour = new HourSession();

            Long dayId = Long.parseLong(d.field("myHour").value());
            newHour.hour = Hours.finderById(dayId);

            Long sessionId = Long.parseLong(d.field("mySession").value());
            newHour.session = Session.finderById(sessionId);

            if (newHour.exist()) return ok(Message.hourSessionError);

            newHour.save();

            return ok("1");

        }
        return ok("0");
    }

    public static Result saveHours() {
        if (session("admin") != null) {
            Form<Hours> d = Form.form(Hours.class).bindFromRequest();
            Hours st = d.get();

            Hours hours = new Hours();
            hours.hourName = st.hourName;

            if (hours.exist()) return ok(Message.hourExist);

            hours.save();

            return ok("1");
        }
        return ok("0");
    }

    public static Result programAssign() {
        if (session("admin") != null) {
            Form<CampusProgram> sForm = Form.form(CampusProgram.class).bindFromRequest();

            CampusProgram nAcc = new CampusProgram();
            Long campId = Long.parseLong(sForm.field("camp").value());
            nAcc.campus = Campus.finderById(campId);

            Long proId = Long.parseLong(sForm.field("prog").value());

            nAcc.program = Program.finderById(proId);

            nAcc.deleteStatus = false;
            if (nAcc.exist()) return ok(Message.campusProError);

            nAcc.save();

            return ok("1");
        } else {
            return ok("0");
        }
    }

    public static Result modesAssign() {
        if (session("admin") != null) {
            Form<CampusProgramMode> sForm = Form.form(CampusProgramMode.class).bindFromRequest();

            CampusProgramMode c = new CampusProgramMode();

            Long campusPro = Long.parseLong(sForm.field("campPro").value());
            c.campusProgram = CampusProgram.finderById(campusPro);

            Long modeId = Long.parseLong(sForm.field("sMode").value());
            c.mode = StudyMode.finderById(modeId);

            if (c.exist()) return ok(Message.studyModeError);

            c.save();

            return ok("1");
        } else {
            return ok("0");
        }
    }

    public static Result sessionIntakeAssign() {
        if (session("admin") != null) {
            Form<IntakeSessionMode> f = Form.form(IntakeSessionMode.class).bindFromRequest();

            IntakeSessionMode s = new IntakeSessionMode();

            Long intakeId = Long.parseLong(f.field("myIntake").value());
            s.intake = Intake.finderById(intakeId);

            Long sessionModeId = Long.parseLong(f.field("mySessionMode").value());
            s.sessionMode = SessionMode.finderById(sessionModeId);

            Long campProId = Long.parseLong(f.field("myCampPro").value());
            s.campusProgram = CampusProgram.finderById(campProId);

            if (s.exist()) return ok(Message.intakeSessionError);

            s.save();

            return ok("1");
        } else {
            return ok("0");
        }
    }
    public static Result sessionAssign() {
        if (session("admin") != null) {
            Form<SessionMode> sForm = Form.form(SessionMode.class).bindFromRequest();

            SessionMode c = new SessionMode();

            Long sessionId = Long.parseLong(sForm.field("mySession").value());
            c.session = Session.finderById(sessionId);

            Long modeId = Long.parseLong(sForm.field("myMode").value());
            c.mode = StudyMode.finderById(modeId);

            if (c.exist()) return ok(Message.sessionstudyModeError);

            c.save();
            return ok("1");
        } else {
            return ok("0");
        }
    }
    public static Result reSitRetakeRequest() {
        if (session("student") != null) {
            Form<ReSitReTakeRequest> form = Form.form(ReSitReTakeRequest.class).bindFromRequest();
            ReSitReTakeRequest request = new ReSitReTakeRequest();
            Student student = Student.finderById(Long.parseLong(form.field("studentId").value()));
            request.requestType = form.field("requestType").value();
            request.student = student;
            request.training = Training.finderById(Long.parseLong(form.field("trainingId").value()));
            request.comment = form.field("comment").value();
            if (request.exist()) return ok(Message.resitRequestExists);
            request.save();
            List<Module> modules = student.myOnlyModulesDeliberation();
            double averageOneMarks = 0.0;
                for(Module module : modules) {
                    averageOneMarks = module.allModuleAverage(student.id);
                    if(averageOneMarks < ProfileInfo.unique().scoreThree) {
                      RequestDetails requestDetail = new RequestDetails();
                        requestDetail.module = module;
                        requestDetail.request = request;
                        requestDetail.save();
                    }
            }
            return ok("1");
        } else {
            return ok("0");
        }
    }
    public static Result retakeModuleRetakeRequest() {
        if (session("student") != null) {
            Form<ReSitReTakeRequest> form = Form.form(ReSitReTakeRequest.class).bindFromRequest();
            ReSitReTakeRequest request = new ReSitReTakeRequest();
            Student student = Student.finderById(Long.parseLong(form.field("studentId").value()));
            request.requestType = form.field("requestType").value();
            request.student = student;
            request.training = Training.finderById(Long.parseLong(form.field("trainingId").value()));
            request.comment = form.field("comment").value();
            if (request.exist()) return ok(Message.resitRequestExists);
            request.save();
            List<Module> modules = student.myOnlyModulesDeliberation();
            double averageOneMarks = 0.0;
                for(Module module : modules) {
                    averageOneMarks = module.allModuleAverage(student.id);
                    if(averageOneMarks < ProfileInfo.unique().scoreThree) {
                      RequestDetails requestDetail = new RequestDetails();
                        requestDetail.module = module;
                        requestDetail.request = request;
                        requestDetail.save();
                    }
            }
            return ok("1");
        } else {
            return ok("0");
        }
    }
    public static Result retakeProgramRetakeRequest() {
        if (session("student") != null) {
            Form<ReSitReTakeRequest> form = Form.form(ReSitReTakeRequest.class).bindFromRequest();
            ReSitReTakeRequest request = new ReSitReTakeRequest();
            Student student = Student.finderById(Long.parseLong(form.field("studentId").value()));
            request.requestType = form.field("requestType").value();
            request.student = student;
            request.training = Training.finderById(Long.parseLong(form.field("trainingId").value()));
            request.comment = form.field("comment").value();
            if (request.exist()) return ok(Message.resitRequestExists);
            request.save();
            return ok("1");
        } else {
            return ok("0");
        }
    }

    public static Result saveSession() {
        if (session("admin") != null) {
            Form<Session> sForm = Form.form(Session.class).bindFromRequest();
            Session acc = sForm.get();

            Session nAcc = new Session();
            nAcc.sessionName = acc.sessionName;
            nAcc.sessionStart = acc.sessionStart;
            nAcc.sessionEnd = acc.sessionEnd;
            nAcc.startHour = acc.startHour;
            nAcc.endHour = acc.endHour;
            nAcc.deleteStatus = false;
            nAcc.save();
            return ok("1");
        } else {
            return ok("error");
        }
    }

    public static Result saveStudyMode() {
        if (session("admin") != null) {
            Form<StudyMode> sForm = Form.form(StudyMode.class).bindFromRequest();
            StudyMode acc = sForm.get();

            StudyMode nAcc = new StudyMode();
            nAcc.modeName = acc.modeName;
            nAcc.deleteStatus = false;
            nAcc.save();
            return ok("1");
        } else {
            return ok("error");
        }
    }

    public static Result saveFiles() {
        if (session("admin") != null) {
            Form<AcademicFiles> f = Form.form(AcademicFiles.class).bindFromRequest();
            AcademicFiles fl = f.get();

            Long proId = Long.parseLong(f.field("prog").value());
            Program program = Program.finderById(proId);

            Long sessionId = Long.valueOf(f.field("sessionId").value());
            Long trainingId = Long.valueOf(f.field("trainingId").value());
            Session session = Session.finderById(sessionId);
            Training training = Training.finderById(trainingId);
            if (session == null) {
                return notFound("Session not found");
            }
            if (training == null) {
                return notFound("Training not found");
            }

            AcademicFiles n = new AcademicFiles();
            AcademicFiles academicFile = AcademicFiles.find.where().eq("fileName", fl.fileName.trim()).eq("deleteStatus", false).eq("program.id", program.id).eq("session.id", session.id).setMaxRows(1).findUnique();
            if (academicFile != null) {
                return ok("File name already exist");
            }

            n.fileName = fl.fileName.trim();
            UUID uuid = UUID.randomUUID();
            n.uniqueName = uuid.toString();
            n.description = fl.description.trim();
            n.program = program;


            n.session = session;


            n.required = fl.required;

            n.save();

            return ok("1");
        } else {
            return ok("error");
        }
    }

    public static Result saveTraining() {
        if (session("admin") != null || session("VRAC") != null || session("registrar") != null) {
            Form<Training> f = Form.form(Training.class).bindFromRequest();
            Training fl = f.get();

            if (fl.startDate.equals(fl.endDate) || fl.endDate.before(fl.startDate)) {
                return ok(Message.endDateError);
            } else if (fl.schoolFees < 0) {
                return ok(Message.feesError);
            } else if (fl.accomodationFees < 0 || fl.restaurationFees < 0) {
                return ok(Message.feesError2);
            }
            Training n = new Training();
            if(f.field("title").value() != null) {
                n.title = f.field("title").value();
            }else{
                n.title = "";
            }
            if(f.field("trainer").value() != null) {
                n.trainer = f.field("trainer").value();
            }else{
                n.trainer = "";
            }
            n.startDate = fl.startDate;
            n.endDate = fl.endDate;
            n.startDateApplication = fl.startDateApplication;
            n.endDateApplication = fl.endDateApplication;
            // 
            n.eacStudentTuitionFees=Double.valueOf(f.field("eacStudentTuitionFees").value());
            n.minEacStudentTuitionFees=Double.valueOf(f.field("minEacStudentTuitionFees").value());
            if(n.eacStudentTuitionFees<n.minEacStudentTuitionFees)
                return ok("Minimum payment "+n.minEacStudentTuitionFees+" cant be greater than the maximum payment "+n.eacStudentTuitionFees);
            n.nonEacStudentTuitionFees=Double.valueOf(f.field("nonEacStudentTuitionFees").value());
            n.minNonEacStudentTuitionFees=Double.valueOf(f.field("minNonEacStudentTuitionFees").value());

            if(n.nonEacStudentTuitionFees<n.minNonEacStudentTuitionFees)
                return ok("Minimum payment "+n.minNonEacStudentTuitionFees+" cant be greater than the maximum payment "+n.nonEacStudentTuitionFees);            n.accomodationFees = Double.valueOf(f.field("accomodationFees").value());
            n.restaurationFees = Double.valueOf(f.field("restaurationFees").value());
            n.otherFees = f.get().otherFees;
            n.otherFeesSpec = f.get().otherFeesSpec;
            n.minPayment = f.get().minPayment;
            n.status = false;
            n.iMode = IntakeSessionMode.finderById(Long.parseLong(f.field("intakeSess").value()));
            if (n.iMode.campusProgram.program.cle) {
                n.transcript = true;
            }
            n.save();

            return ok("1");
        } else {
            return ok("error");
        }
    }

    public static Result resetPassword() {
        if (session("reset") != null) {
            Form<Users> u = Form.form(Users.class).bindFromRequest();

            Users user = u.get();

            Users cUser = Users.finderByMail(session("reset"));
            if (cUser != null) {
                cUser.password = BCrypt.hashpw(user.password, BCrypt.gensalt());
                cUser.resetCode = randomString() + randomString() + "." + cUser.id;
                cUser.update();
                session().clear();
                session(cUser.role, cUser.email);
                session("user", cUser.email);
            }
        }
        return redirect(routes.Application.index());
    }

    public static Result updateEveryProfile() {
        if (session(defaultSession) != null) {
            Form<Users> user = Form.form(Users.class).bindFromRequest();
            Users users = user.get();

            Users users1 = Application.Ins(defaultSession);
            users1.names = users.names;
            users1.profile = uploadImage(users1.profile);
            if (!users.emailTaken()) {
                users1.email = users.email;
            }
            users1.phone = users.phone;
            Long cId = Long.parseLong(user.field("country").value());
            Country country = Country.finderById(cId);
            users.code = country.phonecode;

            String oldPassword = user.field("old-password").value();
            String newPassword = user.field("password").value();
            String confirmPassword = user.field("conf-password").value();
            if (oldPassword != null && !oldPassword.equals("")) {
                if (oldPassword.equals(users1.password)) {
                    if (newPassword != null && !newPassword.equals("")) {
                        if (confirmPassword.equals(newPassword)) {
                            users1.password = newPassword;
                        } else {
                            return ok("Confirmed password must be the same as above password");
                        }
                    }else{
                        return ok("Invalid new password!");
                    }
                } else {
                    return ok("Invalid old password!");
                }
            } else {
                return ok("Please enter old password!");
            }
            users1.update();
        }
        return ok("1");
    }

    public static Result saveDate() {
        Form<DateRange> rangeForm = Form.form(DateRange.class).bindFromRequest();
        DateRange dateRange = new DateRange();
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        try {

            dateRange.startDate = format.parse(rangeForm.field("startDate").value());
            dateRange.endDate = format.parse(rangeForm.field("endDate").value());

            DateTime d1 = new DateTime(new Date()).withTimeAtStartOfDay();
            DateTime d2 = new DateTime(dateRange.startDate).withTimeAtStartOfDay();
            if (new DateTime(dateRange.endDate).isBefore(new DateTime(dateRange.startDate))) {
                return ok("End date cannot be before the start date");
            }

            dateRange.save();
            return ok("1");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return badRequest("Error");
    }
    public static Result updateDate(Long id) {
        Form<DateRange> rangeForm = Form.form(DateRange.class).bindFromRequest();
        if (session("admin") != null) {
            DateRange dateRange = DateRange.finderById(id);
            if (dateRange == null) {
                return notFound();
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
            try {
                dateRange.startDate = format.parse(rangeForm.field("startDate").value());
                dateRange.endDate = format.parse(rangeForm.field("endDate").value());

                DateTime d1 = new DateTime(new Date()).withTimeAtStartOfDay();
                DateTime d2 = new DateTime(dateRange.startDate).withTimeAtStartOfDay();
               /*
                if (d2.isBefore(d1)) {
                    return ok("Start date cannot be before today's date");
                } else
                */
                if (new DateTime(dateRange.endDate).isBefore(new DateTime(dateRange.startDate))) {
                    return ok("End date cannot be before the start date");
                }
                dateRange.update();
                return ok("1");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return badRequest("error");
    }

    public static Result rejectProvisionallyAcceptedApplication(Long id) {
        Form<Applied> appliedForm = Form.form(Applied.class).bindFromRequest();
        Applied applied = Applied.finderById(id);
        if (applied == null) {
            return notFound();
        }
        applied.applicationStatus = "rejected";
        applied.statusComment = appliedForm.field("comment").value();
        applied.update();
        return ok();
    }

    public static Result showStudentInfo(long id) {
        Applicant student = Applicant.finderById(id);
        if (student == null) {
            return notFound();
        }
        return ok(views.html.finance.studentInfo.render(student));
    }
    //
    public static Result disableAccommodation(long id) {
        Applicant applicant = Applicant.finderById(id);
        if (applicant == null) {
            return notFound();
        }
        Account account = Account.finder.where().eq("deleteStatus", false).eq("applicant.id", applicant.id).setMaxRows(1).findUnique();
        if (account == null) {
            return badRequest();
        }
        applicant.needAccomodation = false;
        applicant.update();
        Counts.resetStudentAccount(applicant.applied);
        return ok();
    }
    //
    public static Result enableAccommodation(long id) {
        Transaction txn = Ebean.beginTransaction();
        try {
            Applicant applicant = Applicant.finderById(id);
            Account account = Account.finder.where().eq("deleteStatus", false).eq("applicant.id", applicant.id).setMaxRows(1).findUnique();
            if (account == null) {
                return notFound();
            }
            applicant.needAccomodation = true;
            applicant.update();
            Counts.resetStudentAccount(applicant.applied);
            txn.commit();
        } finally {
            txn.end();
        }

        return ok();
    }
    //
    public static Result disableRestoration(long id) {
        Transaction txn=Ebean.beginTransaction();
        try {
            Applicant applicant = Applicant.finderById(id);
            if (applicant == null) {
                return notFound();
            }
            Account account = Account.finder.where().eq("deleteStatus", false).eq("applicant.id", applicant.id).setMaxRows(1).findUnique();
            if (account == null) {
                return notFound();
            }
            applicant.needCatering = false;
            applicant.update();
            Counts.resetStudentAccount(applicant.applied);
            txn.commit();
        }finally {
            txn.end();
        }

        return ok();
    }
    //
    public static Result enableRestoration(long id) {
        Transaction txn=Ebean.beginTransaction();
        try {
            Applicant applicant = Applicant.finderById(id);
            Account account = Account.finder.where().eq("deleteStatus", false).eq("applicant.id", applicant.id).setMaxRows(1).findUnique();
            if (account == null) {
                return notFound();
            }
            applicant.needCatering = true;
            applicant.update();
            Counts.resetStudentAccount(applicant.applied);
            txn.commit();
        }finally {
            txn.end();
        }

        return ok();
    }

    public static Result managePeriodTranscript(long id, boolean b) {
        Training training = Training.finderById(id);
        if (training == null) {
            return notFound();
        }
        training.transcript = b;
        training.update();
        if(b){
            List<Student> students = Student.byTraining(id);
            for(Student s : students){
                Users user = s.applicant.user;
                new Counts().sendUserEmail(user, "Transcript enabled!", Template.transcriptEnabled);
            }
        }
        return ok();
    }

    public static Result managePeriodTranscriptPrint(long id, boolean b) {
        Training training = Training.finderById(id);
        if (training == null) {
            return notFound();
        }
        training.transcriptPrint = b;
        training.update();
        if(b){
            List<Student> students = Student.byTraining(id);
            for(Student s : students){
                Users user = s.applicant.user;
                new Counts().sendUserEmail(user, "Transcript print enabled!", Template.transcriptPrintEnabled);
            }
        }
        return ok();
    }

    public static Result managePeriodClosingStatus(long id, boolean b) {
        Training training = Training.finderById(id);
        if (training == null) {
            return notFound();
        }
        training.isClosed = b;
        training.update();
        if(training.isClosed){
          List<Applied> appliedList = Applied.finder.where()
          .eq("training.id", training.id)
          .findList();
          if(appliedList != null) {
              for (Applied a : appliedList) {
                  Users user = a.applicant.user;
                  user.deleteStatus = true;
                  user.update();
              }
          }
        }
        return ok();
    }

}
