package models;
import com.avaje.ebean.Expr;
import com.avaje.ebean.annotation.Formula;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class Account extends BaseModel {

    @OneToOne
    public Applicant applicant;

    public double amount;
    public boolean deleteStatus=false;
    public Date createdAt=new Date();

    @Formula(select = "(SELECT IFNULL(SUM(p.accomodation_amount+p.retake_amount+p.fees_amount+p.application_fees+p.restaurant_amount+p.other_fees),0) as sum FROM applied a INNER JOIN applicant a2 ON a.applicant_id = a2.id INNER JOIN training t ON a.training_id = t.id INNER JOIN account a3 ON a2.id = a3.applicant_id INNER JOIN payment p ON a3.id = p.account_id WHERE p.status='approved' AND a3.id=${ta}.id)")
    public Double amountPaid;

    @JsonBackReference
    @OneToMany(mappedBy = "account")
    public List<Payment>  payments;
    public static Model.Finder<Long, Account> finder = new Finder<>(Long.class, Account.class);

    public static Account finderById(long id){
        return finder.where().eq("deleteStatus",false).eq("id",id).findUnique();
    }

    @JsonProperty
    public String print(){
        return applicant != null ? applicant.toString() : "not student";
    }

    public static List<Account> all() {
        return finder.where()
                .not(Expr.eq("deleteStatus", true))
                .eq("applicant.deleteStatus", false)
                .orderBy("id desc")
                .findList();
    }
}
