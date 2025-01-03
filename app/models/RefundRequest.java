package models;

import Helper.ReportProperty;
import com.avaje.ebean.ExpressionList;
import play.db.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;
import java.util.List;

@Entity
@ReportProperty(name = "Student refunds report")
public class RefundRequest extends Model
{
    @Id
    public long id;

    @ManyToOne(cascade = CascadeType.ALL)
    public Account account;
    public double amount;
    public String attachment = "";

    public String details = "";


    public String accountNumber;
    public String accountUserName;


    public Date date = new Date();
    public boolean status = false;

    public Boolean deleteStatus = false;
    private static Finder<Long, RefundRequest> finder = new Finder<>(Long.class, RefundRequest.class);

    public static ExpressionList<RefundRequest> query()
    {
        return finder.where().eq("deleteStatus", false).eq("status", false);
    }

    public static List<RefundRequest> all()
    {
        return query().findList();
    }


    public static List<RefundRequest> myRefundsRequest(Applicant  applicant)
    {
        return finder.where().eq("deleteStatus",false).eq("account.id",applicant.account.id).orderBy("id desc").findList();
    }

    public static RefundRequest finderById(Long id)
    {
        return finder.ref(id);
    }

    public String viewStatus(){
        return status ? "Accepted" : "pending ...";
    }
    public static String count() {
        int api = all().size();
        return String.valueOf((api > 0) ? api : 1);
    }
}
