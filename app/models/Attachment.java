package models;

import com.avaje.ebean.Expr;
import play.db.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.List;

/**
 * Created by SISI on 10/12/2017.
 */
@Entity
public class Attachment extends Model {
    @Id
    public long id;
    public String attachName = "";

    @ManyToOne
    public Applicant app;

    @ManyToOne
    public AcademicFiles file;

    public Boolean deleteStatus = false;

    public static Finder<Long, Attachment> find = new Finder<Long, Attachment>(Long.class, Attachment.class);

    public static List<Attachment> all(){
        return find.where().not(Expr.eq("deleteStatus",true)).eq("app.deleteStatus",false).eq("file.deleteStatus",false).findList();
    }
    public static Attachment checker(Long app,Long file){
        return find.where()
                .eq("app.id",app)
                .eq("file.id",file)
                .setMaxRows(1)
                .findUnique();
    }
    public static Attachment byApplicant(Long app){
        return find.where()
                .eq("app.id",app)
                .setMaxRows(1)
                .findUnique();
    }
    public static List<Attachment> byApp(Long id){
        return find.where().eq("app.id",id).findList();
    }
    public Verification amV(){
        return Verification.finder.where().eq("deleteStatus",false).eq("attachment.id",this.id).eq("attachment.deleteStatus",false).setMaxRows(1).findUnique();
    }
    public static Attachment finderById(Long id){
        return find.byId(id);
    }

}
