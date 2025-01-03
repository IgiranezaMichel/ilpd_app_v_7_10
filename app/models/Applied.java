package models;
import Helper.ReportProperty;
import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import play.db.ebean.Model;
import javax.persistence.*;
import java.util.*;


@Entity
@ReportProperty(name = "Application reports")
public class Applied extends Model
{
    @Id
    public long id;

    @OneToOne(cascade = CascadeType.ALL)
    public Applicant applicant;

    @ManyToOne(cascade = CascadeType.ALL)
    public Training training;


    public String secondarySchool = "";
    public String firstPrincipal = "";
    public String secondPrincipal = "";
    public String firstGrade = "";
    public String secondGrade = "";

    public int year = 0;
    public String feespayment = "";
    public String sponsorName = "";
    public String sponsorPhone = "";
    public String sponsorEmail = "";
    public String statement = "";
    public String contactPerson = "";
    public String contactEmail = "";
    public String contactPhone = "";

    public Boolean disability = false;
    public String disabiltyDetails = "";

    public String publicInfo = "";
    public String publicInfoDetails = "";
    public String bornCountry = "";
    public String applicationDate;
    public String applicationStatus = "";
    public String statusComment = "";
    public String shelfNumber = "";

    public Boolean shelfed = false;
    public Boolean status = false;

    public Boolean deleteStatus = false;

    public static Model.Finder<Long, Applied> finder = new Finder<Long, Applied>(Long.class, Applied.class);

    public static List<Applied> all()
    {
        return finder.where().not(Expr.eq("deleteStatus", true)).eq("applicant.deleteStatus", false).eq("status", true).orderBy("id desc").findList();
    }

    public static List<Applied> allUncompleted()
    {
        return finder.where()
                .not(Expr.eq("deleteStatus", true))
                .eq("applicant.deleteStatus", false)
                .eq("status", false)
                .orderBy("id asc")
                .findList();
    }
    public static Applied byApp(Long id)
    {
        return finder.where()
                .not(Expr.eq("deleteStatus", true))
                .like("applicant", String.valueOf(id))
                .setMaxRows(1).findUnique();
    }

    public static List<Applied> allByIntake(Long id)
    {
        return finder.where().not(Expr.eq("deleteStatus", true))
                .eq("status", true)
                .eq("training.deleteStatus", false)
                .eq("applicant.deleteStatus", false)
                .eq("training.iMode.intake.id", id)
                .orderBy("id asc")
                .findList();
    }

    public static List<Applied> allByProg(Long id)
    {
        return finder.where().eq("deleteStatus", false).eq("status", true).eq("training.deleteStatus", false).eq("applicant.deleteStatus", false)
                .eq("training.intake.session.studyMode.faculty.id", id)
                .orderBy("id asc")
                .findList();
    }

    public static List<Applied> allByName(String var)
    {
        String vari = null;
        switch (var)
        {
            case ":accepted":
                vari = "accepted";
                break;
            case ":rejected":
                vari = "rejected";
                break;
            case ":derefered":
                vari = "derefered";
                break;
            case ":new":
                vari = "new";
                break;
            case ":student":
                vari = "student";
                break;
        }

        if (vari != null)
        {
            return finder.where().not(Expr.eq("deleteStatus", true)).eq("status", true).eq("session.deleteStatus", false).eq("intake.deleteStatus", false).eq("applicant.deleteStatus", false)
                    .eq("applicationStatus", vari)
                    .orderBy("id asc")
                    .findList();
        }

        if (var.equals(":all")) return all();
        return finder.where().not(Expr.eq("deleteStatus", true)).eq("status", true).eq("training.deleteStatus", false).eq("applicant.deleteStatus", false)
                .like("applicant.firstName", "%" + var + "%")
                .orderBy("id asc")
                .findList();
    }

    public static List<Applied> empty()
    {
        return finder.where().setMaxRows(1).findList();
    }

    public static List<Applied> allByNameAndStatus(String var, String status)
    {

        List<Applied> lst = allByName(var);
        if (status.equals(":all")) lst = all();

        if (status.equals("new")) status = "";
        List<Applied> lt = empty();
        if (lt.size() > 0) lt.remove(0);
        for (Applied app : lst)
        {
            if (app.applicationStatus.equals(status) || status.equals(":all"))
            {
                lt.add(app);
            }
        }
        return lt;
    }

    public static Applied finderById(long id)
    {
        return finder.ref(id);
    }

    public static Applied finderByApplicant(long id)
    {
        return finder.where().eq("applicant.id", id).findUnique();
    }

    public static int countable(String type,boolean isCLE)
    {
        ExpressionList<Applied> eq = finder.where()
                .eq("status", true)
                .eq("applicant.deleteStatus", false)
                //.eq("training.deleteStatus", false)
                .add(Student.cleExp(isCLE))
                .eq("deleteStatus", false);

        if ( !type.equals(":all"))
        {
            eq.eq("applicationStatus", type);
        }

        return eq.findRowCount();
    }


    public static int rejectedNbr(boolean b){
        return number(b,"rejected");
    }

    public static int number(boolean b){
        return Applied.finder.where().add(Student.cleExp(b)).findRowCount();
    }


    public static int number(boolean b,String sts){
        return Applied.finder.where().add(Student.cleExp(b)).eq("applicationStatus",sts).findRowCount();
    }



    public static int deferredNbr(boolean b){
        return number(b,"derefered");
    }


    public static int countable(String type)
    {
        List<Applied> lst;
        lst = finder.where()
                .eq("status", true)
                .eq("applicationStatus", type)
                .findList();
        if (type.equals(":all"))
        {
            lst = finder.where().eq("status", true).eq("deleteStatus", false).findList();
        }

        return lst.size();
    }

    public static Long lastUserId(String status)
    {
        if (status != null && status.equals("new")) status = "";
        if (status != null && status.equals(":all"))
        {
            return finder.where()
                    .setMaxRows(1)
                    .orderBy("id asc")
                    .findUnique().id;
        }
        if (status != null)
        {
            Applied apn = finder.where().eq("status", true)
                    .eq("applicationStatus", status)
                    .setMaxRows(1)
                    .orderBy("id asc")
                    .findUnique();
            if (apn != null)
            {
                return apn.id;
            }
            else
            {
                return null;
            }
        }
        else
        {
            return finder.where().eq("status", true)
                    .setMaxRows(1)
                    .orderBy("id asc")
                    .findUnique().id;
        }
    }

    public Boolean notExisted()
    {
        Applied ap = finder.where()
                .eq("applicant.id", this.applicant.id)
                .setMaxRows(1).findUnique();
        return (ap == null);
    }

    public static Applied getApplied(Users user)
    {
        try
        {
            return Applied.finder
                    .where()
                    .eq("applicant.user.id", user.id)
                    .eq("deleteStatus", false)
                    .setMaxRows(1)
                    .findUnique();
        } catch (NullPointerException e)
            {
                e.printStackTrace();
            }
        return null;
    }

    public static Applied getAppliedB(Applicant applicant)
    {
        try
        {
            return Applied.finder
                    .where()
                    .eq("applicant.id", applicant.id)
                    .setMaxRows(1)
                    .findUnique();
        } catch (NullPointerException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    public static Applied getAppliedByApplicant(Applicant applicant)
    {
        try
        {
            return Applied.finder
                    .where()
                    .eq("applicant.id", applicant.id)
                    .setMaxRows(1)
                    .findUnique();
        } catch (NullPointerException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Applied> appliedList(String statement){
        return finder.setDistinct(true)
                .where()
                .eq("applicant.user.email", statement)
                .eq("applicant.deleteStatus", false)
                .eq("training.deleteStatus", false)
                .eq("deleteStatus", false)
                .orderBy("id asc")
                .findList();
    }

    public static List<Applied> alls(String search) {
        return allSearch(search).findList();
    }
    public static ExpressionList<Applied> allSearch(String search) {
        return finder.where()
                .not(Expr.eq("deleteStatus", true))
                .eq("deleteStatus", false)
                .icontains("applicant.firstName", search)
                .orderBy("id asc")
                .where();
    }

    public static List<Applied> allDup() {
        return finder.where()
                .eq("applicant.user.deleteStatus", false)
                .orderBy("applicant.firstName asc")
                .findList();
    }
    public static boolean isApplied(Long aId){
        boolean isApplied = false;
        for(Applied a : all()){
            if(a.applicant != null && a.applicant.id == aId){
                isApplied = true;
            }
        }
        return isApplied;
    }

}
