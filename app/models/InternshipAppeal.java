package models;

import com.avaje.ebean.Expr;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;
import java.util.List;

@Entity
public class InternshipAppeal extends BaseModel {
    @Id
    public long id;
    public String reason = "";
    public String attachment = "";
    public String commentRegistrar = "";
    public String status = "pending";
    public String appType = "";

    @ManyToOne
    public Applicant applicant;

    public Boolean deleteStatus = false;
    public Date date = new Date();

    public static Finder<Long, InternshipAppeal> find = new Finder<Long, InternshipAppeal>(Long.class, InternshipAppeal.class);

    public static List<InternshipAppeal> all(){
        return find.where()
                .eq("deleteStatus",false)
                .findList();
    }
    public static List<InternshipAppeal> allPending(){
        return find.where()
                .eq("status","pending")
                .eq("deleteStatus",false)
                .orderBy("appType asc")
                .findList();
    }
    public static List<InternshipAppeal> allProcessed(){
        return find.where()
                .not(Expr.eq("status", "pending"))
                .eq("deleteStatus",false)
                .orderBy("appType asc")
                .findList();
    }
    public static int totalPendingByApplicants(Long apId){
        int total = 0;
        for(InternshipAppeal i : pendingByApplicant(apId)){
            total += total;
        }
        return total;
    }

    public static List<InternshipAppeal> pendingByApplicant(Long apId){
        return find.where()
                .eq("deleteStatus",false)
                .eq("applicant.deleteStatus",false)
                .eq("applicant.id",apId)
                .eq("status", "pending")
                .findList();
    }
    public static InternshipAppeal byId(Long id){
        return find.where()
                .eq("id", id)
                .eq("deleteStatus",false)
                .setMaxRows(1)
                .findUnique();
    }
}
