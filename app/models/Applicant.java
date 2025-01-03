package models;

import com.avaje.ebean.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import controllers.Counts;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

import static org.apache.commons.lang3.text.WordUtils.capitalizeFully;

/**
 * Created by SISI on 9/30/2017.
 */
@Entity
public class Applicant extends Model {
    @Id
    public long id;
    public String profile = "";
    public String firstName = "";
    public String familyName = "";
    public String sponsorInstutute = "";
    public String shelfNumber = "";
    public String comments = "";
    public String state = "";
    public String educationBackground = "";

    @ManyToOne
    public Users user;

    @JsonBackReference
    @OneToOne(mappedBy = "applicant")
    public Account account;

    @JsonBackReference
    @OneToOne(mappedBy = "applicant")
    public Student student;

    public String country = "";
    @JsonBackReference
    @OneToOne(mappedBy = "applicant", cascade = CascadeType.ALL)
    public Applied applied;

    @ManyToOne
    public Districts districts;

    public Boolean chosen = false;

    public String maritalStatus = "";
    public int yearCompletion;
    public String birthDate = "";
    public String birthPlace = "";
    public String gender = "";
    public String nationalDentity = "";
    public String nationality = "";
    public String sponsor = "";
    public String sponsorPhone = "";

    public String aSchool = "";
    public int experienceDraft;
    public int experience;

    public String motherName = "";
    public String fatherName = "";

    public Integer aFromu = 0;
    public Integer aTo = 0;

    public boolean needAccomodation = false;
    public boolean needCatering = false;

    public Boolean deleteStatus = false;
    public Date date = new Date();

    public boolean active = true;

    @JsonProperty
    public boolean isRequiredPaidDone() {
        return Counts.isHaflyPaid(this);
    }

    @Override
    public String toString() {
        return this.familyName.toUpperCase() + " " + capitalizeFully(this.firstName) ;
    }

    public boolean checkStatus(String status) {
        return status.equals("Paid") && this.cleared() || status.equals("Unpaid") && !this.cleared() || status.equals("all");
    }
    public boolean isEacInhabitant(){
        String  eacCountries []=  {"Kenya", "Rwanda","DRC","Burundi","Somalia","South Sudan","Uganda","Tanzania"};
        boolean isEacInhabitant=false;
        for(int i=0;i<eacCountries.length;i++){
            if(this.nationality.toLowerCase().equals(eacCountries[i].toLowerCase())){
                isEacInhabitant=true;
                break;
            }
        }
        return  isEacInhabitant;
    }


    public static Finder<Long, Applicant> finder = new Finder<>(Long.class, Applicant.class);

    public String regNo() {
        return (student != null) ? student.regNo : "undefined";
    }
    public static List<Applicant> all() {
        return finder.where()
                .orderBy("id asc")
                .findList();
    }
    public static List<Applicant> allUncompleted() {
        return finder.where()
                .not(Expr.eq("deleteStatus", true))
                .eq("user.deleteStatus", false)
                .orderBy("id asc")
                .findList();
    }
    public static Expression cleExp(boolean isCLE) {
        return Expr.eq("applied.training.iMode.campusProgram.program.cle", isCLE);
    }

    public static List<Applicant> universityCollegue() {
        return Applicant.finder.setDistinct(true)
                .where("deleteStatus='false' group by aSchool")
                .findList();
    }

    public String genderSir() {
        return gender.equalsIgnoreCase("MALE") ? "Sir" : "Madam";
    }
    public static Applicant finderById(Long id) {
        return finder.ref(id);
    }
    public static Applicant finderByAppliedId(Long id) {
        return finder.where()
                .eq("applied.id", id)
                .eq("deleteStatus", false)
                .setMaxRows(1)
                .orderBy("id asc")
                .findUnique();
    }

    public List<Payment> myPayments() {
        return Payment.finder.where()
                .eq("account.applicant.id", this.id).eq("deleteStatus", false)
                .orderBy("id asc")
                .findList();
    }

    public double getTotalAmountPaid() {
        return hasAccount() ? (this.account.amountPaid - Refund.getTotalInReFunded(this.account.id)): 0.0 ;
    }


    public boolean hasAccount() {
        return this.account != null;
    }

    public boolean cleared() {
        return getTotalAmountRemain() <= 0;
    }

    public double getTotalAmountRemain() {
        return this.getAmountToPay()  - this.getTotalAmountPaid() - ILDPLibrary.cleanedLibrary(this.id);
    }

    public static Applicant finderByUser(Long userId) {
        return finderByUserActive(userId);
    }

    public static Applicant finderByUserActive(Long userId) {
        return finder.where()
                .ne("deleteStatus", true)
                .eq("user.deleteStatus", false)
                .eq("active",true)
                .eq("user.id", userId)
                .setMaxRows(1)
                .findUnique();
    }
    public List<Attachment> myFiles() {
        return Attachment.find.where().eq("deleteStatus", false).eq("app.id", this.id).findList();
    }

    public Boolean notExisted() {
        Applicant ap = finder.where().eq("user.id", this.user.id).setMaxRows(1).findUnique();
        return (ap == null);
    }
    public static int countAccount(Long uId) {
        return finder.where()
                .eq("deleteStatus", false)
                .eq("user.id", uId)
                .eq("active", false)
                .findRowCount();
    }
    public double getAmountToPay() {
        return getAmountToPayByTraining() ;
    }

    public double getAmountToPayByTraining() {
        Applied applied = this.applied;
        double amountToPay = applied.training.schoolFees + applied.training.iMode.intake.registrationFees + applied.training.otherFees + ILDPLibrary.amountRefunded(this.id);
        if (this.needAccomodation) {
            amountToPay += applied.training.accomodationFees;
        }
        if (this.needCatering) {
            amountToPay += applied.training.restaurationFees;
        }if(this.student != null){
            amountToPay += Damage.totalDamage(student.id);
        }if(this.student != null && this.student.status.equalsIgnoreCase("RETAKE-MODULES")){
            List<Module> modules = student.myOnlyModulesDeliberation();
            double totalAmount = 0.0;
            for(Module m : modules) {
                double averageOneMarks = m.allModuleAverage(student.id);
                if(averageOneMarks < ProfileInfo.unique().scoreThree) {
                    totalAmount = totalAmount + m.retakeAmount;
                }
            }
            amountToPay += totalAmount;
        }
        return amountToPay;
    }


    public Account myAccount() {
        return Account.finder.where().eq("deleteStatus", false).eq("applicant.id", this.id).setMaxRows(1).findUnique();
    }

    public static Applicant finderMail(String mail) {
        return Applicant.finder.where()
                .eq("deleteStatus", false)
                .eq("user.email", mail)
                .setMaxRows(1)
                .findUnique();
    }
    public static Applicant finderUser(Users user) {
        return Applicant.finder.where()
                .eq("deleteStatus", false)
                .eq("user", user)
                .setMaxRows(1)
                .findUnique();
    }

    public static Applicant finderMailActive(String mail) {
        return Applicant.finder.where()
                .eq("deleteStatus", false)
                .eq("active",true)
                .eq("user.email", mail)
                .setMaxRows(1)
                .findUnique();
    }

    @JsonProperty
    public String print() {
        return this.toString();
    }

    public double amountRefunded() {
        String s = "SELECT IFNULL(sum(r.amount), 0) AS amount FROM applicant a INNER JOIN account a2 ON a.id = a2.applicant_id INNER JOIN refund r ON a2.id = r.account_id INNER JOIN refund_request rfr ON r.refund_request_id=rfr.id WHERE rfr.status=TRUE AND a.id=:appId";
        SqlRow row = Ebean.createSqlQuery(s)
                .setParameter("appId", this.id).findUnique();
        return row.getDouble("amount");
    }

    public static List<Applicant> applicantList(String statement){
        return Applicant.finder
                .where()
                .eq("user.email",statement)
                .findList();
    }
    public boolean hasAttachment(){
        boolean hasAttachment = false;
        for(Attachment a : Attachment.all()){
            if(a.app != null && a.app.id == this.id){
                hasAttachment = true;
            }
        }
        return hasAttachment;
    }

}
