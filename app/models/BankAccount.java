package models;

import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.fasterxml.jackson.annotation.JsonProperty;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

@Entity
public class BankAccount extends Model {
    @Id
    public long id;

    public String bankName = "";
    public String bankAcronym = "";
    public String openBranch = "";
    public String accountNumber = "";

    public Boolean deleteStatus = false;

    public static Finder<Long, BankAccount> find = new Finder<Long, BankAccount>(Long.class, BankAccount.class);

    public static ExpressionList<BankAccount> query(){
        return find.where().eq("deleteStatus",false);
    }
    public static List<BankAccount> all(){
        return query().findList();
    }
    public Boolean doesExist(BankAccount accountEd){
        BankAccount account = query().not(Expr.eq("id",accountEd.id)).eq("accountNumber",this.accountNumber).setMaxRows(1).findUnique();
        return  ( account != null );
    }
    public String print(){
        return bankName+" ( "+accountNumber+" )";
    }

    @JsonProperty
    public String print1(){
        return bankName+" ( "+accountNumber+" )";
    }
}
