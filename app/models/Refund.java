package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import play.db.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;
import java.util.List;

@Entity
public class Refund extends Model {
    @Id
    public Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    public Account account;
    public double amount;
    public String checkNumber;

    @ManyToOne
    public BankAccount bankAccount;

    @ManyToOne
    public RefundRequest refundRequest;

    public Date date = new Date();
    public Boolean deleteStatus = false;
    public static Finder<Long, Refund> finder = new Finder<>(Long.class, Refund.class);

    public static double getTotalInReFunded(long accId) {
        String s = "SELECT IFNULL(SUM(r.amount),0) as total FROM refund r INNER JOIN account a ON r.account_id = a.id  WHERE a.id=:id";
        return Ebean.createSqlQuery(s).setParameter("id", accId).findUnique().getDouble("total");
    }
    public static List<Refund> all() {
        return finder.where().not(Expr.eq("deleteStatus", true)).orderBy("id desc").findList();
    }
    public static String count() {
        int api = all().size();
        return String.valueOf((api > 0) ? api : 1);
    }
}
