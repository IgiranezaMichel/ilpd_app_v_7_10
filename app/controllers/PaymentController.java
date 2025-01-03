package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.avaje.ebean.SqlRow;
import com.avaje.ebean.Transaction;
import models.*;
import models.Utility.Contracts;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.ApplicantServices;
import services.PaymentServices;
import views.html.errorPage;
import views.html.student.notStudent;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PaymentController extends React {

    public static Result getPayment(long id) {
        Payment payment = Payment.finderById(id);
        if (payment == null) {
            return notFound();
        }
        return ok(Json.toJson(payment));
    }
    public static Result recordPayment() {
        if (session().containsKey("Finance")) {
            List<Training> trainings = Training.allZ();
            return ok(views.html.finance.recordPaymentbyTraining.render(trainings));
        }
        return unauthorized();
    }
    public static Result recordPaymentByTraining(int page, String p) {
        Form<Payment> form = Form.form(Payment.class).bindFromRequest();
        String search = form.field("search").valueOr("");
        String size = form.field("pageSize").valueOr("8");
        int pageSize = Integer.parseInt(size);

        if (session().containsKey("Finance")) {
            Training training = Training.finderById(Long.parseLong(form.field("training").value()));
            if (training == null) {
                return notFound();
            }

            Contracts<Payment> contracts = new Contracts<>(Payment.class);
            Page<Payment> paymentPage = contracts
                    .setColumns("account.applicant.firstName", "account.applicant.student.regNo", "account.applicant.familyName", "account.applicant.nationalDentity","slipNumber", "status")
                    .paymentsByTraining(page, pageSize, search, training);
            if (p.equalsIgnoreCase("partial")) {
                return ok(views.html.finance._paymentRecord.render(paymentPage, training));
            }
            return ok(views.html.finance.paymentRecord.render(training));
        }
        return unauthorized();
    }

    public static Result paymemtHistory() {
        if (session().containsKey("student")) {
            Users user = Users.finderByMail(session().get("student"));
            if (user == null) {
                return notFound();
            }
            Applicant applicant = user.amApplicant();
            if (applicant == null) {
                return ok(errorPage.render(Message.waitError, null, !isAjax()));
            }

            List<RefundRequest> refundRequests = RefundRequest.myRefundsRequest(applicant);
            List<Payment> payments = applicant.myPayments();
            return ok(views.html.student.payment.render(payments, applicant, BankAccount.all(), refundRequests));
        }
        return badRequest();
    }

    public static Result studentRefund() {
        Student student = getStudent();
        if (student == null) return ok("You are not student yet, please get reg number!");
        List<RefundRequest> requestList = student.requestList();
        return ok(views.html.student.refundPage.render(requestList, student.applicant));
    }

    public static Result refundRequest() {
        Student student = getStudent();

        if (student != null) {
            Applicant applicant = student.applicant;
            Account account = applicant.account;
            if (account == null) return ok(errorPage.render("No account for you", null, !isAjax()));
            Form<RefundRequest> form = Form.form(RefundRequest.class).bindFromRequest();
            RefundRequest request = form.get();

            if (request.amount <= 0)
                return ok("Invalid refund amount");

            RefundRequest newRequest = new RefundRequest();
            newRequest.amount = request.amount;
            newRequest.details = request.details;
            newRequest.account = account;
            newRequest.accountNumber = request.accountNumber;
            newRequest.accountUserName = request.accountUserName;
            newRequest.attachment = uploadFile("null", "photo");
            newRequest.save();
        }
        return ok("1");
    }

    public static Result refundView() {
        return ok(views.html.finance.refund.render(RefundRequest.all()));
    }
    public static Result studentRefundCancel() {
        return ok(views.html.student.studentRefundCancel.render(Refund.all()));
    }
    public static Result paymentSuccess() {
        List<Payment> payments;
        if (session().containsKey("student")) {
            Users user = Users.finderByMail(session().get("student"));
            // check if he is a student
            Student student = user.me();

            if (student == null) {
                // check for applicant may be he is not a student yet
                Applicant applicant = Applicant.finderByUserActive(user.id);
                if (applicant == null) {
                    return ok(errorPage.render("Unable to see the information .. Please wait until you are eligible", null, !Application.isAjax()));
                } else {
                    // all payment of the applicant who is not a student
                    payments = Payment.finder.where()
                            .eq("account.applicant.active",true)
                            .eq("account.applicant.id", applicant.id)
                            .orderBy("id desc").findList();
                    return ok(views.html.student.paymentSuccess.render(payments, applicant, applicant.getAmountToPay()));
                }
            }

            //
            payments = student.myPaymentsApproved();
            return ok(views.html.student.paymentSuccess.render(payments, student.applicant, student.getAmountToPay()));
        }
        return unauthorized();
    }

    public static Result confirmPayment() {
        if (session("Finance") != null || session("DAF") != null || session("Accountant") != null) {
            Users user = null;
            if(session("Finance") != null) {
                user  = Application.Ins("Finance");
            }
            if(session("DAF") != null) {
                user  = Application.Ins("DAF");
            }
            if(session("Accountant") != null) {
                user  = Application.Ins("Accountant");
            }
            Form<Payment> paymentForm = Form.form(Payment.class).bindFromRequest();
            Transaction txn = Ebean.beginTransaction();
            try {
                Payment payment = Payment.finderById(Long.valueOf(paymentForm.field("paymentId").value()));
                if (payment == null) return ok("error");
                payment.status = paymentForm.field("status").value();
                payment.comment = paymentForm.field("comment").valueOr("");
                payment.doneBy = user.print();
                payment.update();
                txn.commit();
            } finally {
                txn.end();
            }
            return ok("1");
        }else{
            return ok("0");
        }
    }

    public static Result savePayment() {
        Form<Payment> paymentForm = Form.form(Payment.class).bindFromRequest();
        if ( isApplicant() ) {
            Applicant applicant = applicant();
            if (applicant.account == null) return ok("No account found");

            double amountRemain = applicant.getAmountToPay() - applicant.getTotalAmountPaid();

            Payment payment = new Payment();
            payment.feesAmount = getDouble(paymentForm.field("feesAmount").value());
            payment.slipNumber = paymentForm.field("slipNumber").value();
            payment.accomodationAmount = getDouble(paymentForm.field("accomodationAmount").value());
            payment.restaurantAmount = getDouble(paymentForm.field("restaurantAmount").value());
            payment.otherFees = getDouble(paymentForm.field("otherFees").value());
            payment.paymentType = paymentForm.field("paymentType").value();
            payment.comment = paymentForm.field("comment").value();
            Long bankId = Long.parseLong(paymentForm.field("bankName").value());
            payment.bankAccount = BankAccount.find.ref(bankId);
            payment.attachment = uploadFile("null", "photo");
            payment.remain = amountRemain;
            payment.account = applicant.account;
            payment.retakeAmount = getDouble(paymentForm.field("retakeAmount").value());

            if (payment.slipExist()) {
                return ok(Message.slipError);
            }
            String verify = Payment.checkExist(paymentForm.field("slipNumber").value());
            if(!verify.equalsIgnoreCase("")){
                return ok(verify);
            }
            if(paymentForm.field("slipNumber").value().length() < 12 && paymentForm.field("slipNumber").value().length() > 14)
                return ok("Bank slip number must be between 12 and 14 digits!");
            if (payment.sum() > 0)
                payment.save();

            return ok("1");
        }
        return ok("error");
    }



    public static Result accountStatus() {

        if (session().containsKey("student")) {
            Users user = Users.finderByMail(session().get("student"));
            if (user == null) return notFound();

            Student student = user.me();
            if (student == null) return ok(notStudent.render());

            return ok(views.html.student.accountStatus.render(student));
        }
        return unauthorized();
    }


    public static Result refundStudent() {
        Form<Student> studentForm = Form.form(Student.class).bindFromRequest();
        Account account = Account.finder.where().eq("applicant.id", Long.valueOf(studentForm.field("id").value()))
                .eq("deleteStatus", false).findUnique();

        if (account == null) {
            return notFound("Account not found");
        }

        RefundRequest request = RefundRequest.finderById(Long.parseLong(studentForm.field("request").value()));
        if (request == null) {
            return notFound();
        }

        BankAccount bankAccount=BankAccount.find.byId(Long.parseLong(studentForm.field("bankAccount").value()));
        if(bankAccount==null) return notFound();

        double amount = Double.valueOf(studentForm.field("amount").value());

        Ebean.beginTransaction();
        try {

            Refund refund = new Refund();
            refund.account = account;
            refund.amount = amount;
            refund.checkNumber = studentForm.field("checkNumber").value();
            refund.refundRequest=request;
            refund.bankAccount=bankAccount;
            refund.save();

//            update student account
            //update refund request
            request.status = true;
            request.update();
            // commit the transaction
            Ebean.commitTransaction();
            return ok("1");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // rollback the transaction if it was not committed
            Ebean.endTransaction();
        }
        return badRequest("Other problem");
    }


    public static Result deleteRefund(Long id) {
        Form<Refund> form = Form.form(Refund.class).bindFromRequest();
        Refund refund = form.get();
        Refund refund1 = Refund.finder.byId(id);
        refund1.deleteStatus = true;
        refund1.update();
        return ok("1");
    }
//    student refund history

    public static Result studentRefunds() {
        if (session("student") == null) {
            return unauthorized("You are not allowed to see this information");
        }
        Applicant applicant = Applicant.finderMail(session("student"));
        if (applicant.account != null) {
            List<Refund> refunds = Refund.finder.where().eq("account.id", applicant.account.id).findList();
            return ok(views.html.student.refund.render(refunds, applicant.account));
        }
        return ok(views.html.student.refund.render(null, null));
    }


    public static Result payments() {
        Form<Payment> form = Form.form(Payment.class).bindFromRequest();
        Map<Object, Object> payments = PaymentServices.paymentsPages(form);
        return ok(Json.toJson(payments));
    }
}
