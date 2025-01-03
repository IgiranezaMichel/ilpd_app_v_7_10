package models;
import Helper.ReportProperty;
import com.avaje.ebean.ExpressionList;
import play.data.format.Formats;
import play.db.ebean.Model;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Entity
@ReportProperty(name = "Payment reports")
public class Payment extends Model
{
    @Id
    public long id;
    public String paymentType = "";
    //    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    public Account account;
    @ManyToOne(cascade = CascadeType.ALL)
    public BankAccount bankAccount;
    public double applicationFees = 0.0;
    public double feesAmount = 0.0;
    public double accomodationAmount = 0.0;
    public double restaurantAmount = 0.0;
    public double retakeAmount = 0.0;
    public double remain = 0.0;
    public String slipNumber = "";
    public String attachment = "";
    public String status = "pending";
    public String comment = "";
    public boolean deleteStatus = false;
    @Formats.DateTime(pattern = "dd-MM , YYYY")
    public Date date = new Date();
    public static Model.Finder<Long, Payment> finder = new Finder<>(Long.class, Payment.class);
    public double otherFees = 0.0;
    public String doneBy = "";
    public Timestamp doneAt = new Timestamp(new Date().getTime());

    public String getDate()
    {
        return new SimpleDateFormat("dd-MM , YYYY: hh:mm").format(this.date);
    }

    public static List<Payment> all()
    {
        return finder.where().eq("deleteStatus", false).findList();
    }

    public static Payment finderById(long id)
    {
        return query().eq("id", id).findUnique();
    }

    public static ExpressionList<Payment> query()
    {
        return finder.where().eq("deleteStatus", false);
    }

    public Boolean slipExist()
    {
        Payment payment = query()
                .eq("slipNumber", this.slipNumber)
                .ne("status", "rejected")
                .eq("bankAccount.id", this.bankAccount.id)
                .setMaxRows(1).findUnique();
        return (payment != null);
    }

    public String printBank()
    {
        return bankAccount != null ? bankAccount.print() : "undefined bank";
    }

    public Double sum()
    {
        return this.feesAmount + this.restaurantAmount + this.accomodationAmount + this.applicationFees + this.otherFees + this.retakeAmount;
    }

    public Double sumWithoutRetake()
    {
        return this.feesAmount + this.restaurantAmount + this.accomodationAmount + this.applicationFees + this.otherFees;
    }

    public static List<Payment> search(String str)
    {
        return query().like("account.applicant.student.regNo", "%" + str + "%").findList();
    }
    public static String checkExist(String slip){
        boolean Exist = false;
        String student = "";
        for(Payment p : all()){
            if(p.slipNumber.equalsIgnoreCase(slip)){
                Exist = true;
                student = "The student "+p.account.applicant.firstName +" "+p.account.applicant.familyName+" has already used the same bank slip";
            }
        }
        if(Exist) {
            return student;
        }else{
            return "";
        }
    }
}
