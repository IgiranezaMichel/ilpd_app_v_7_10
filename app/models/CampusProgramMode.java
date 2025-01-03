package models;

import com.avaje.ebean.Expr;
import play.db.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.List;

@Entity
public class CampusProgramMode extends Model{
    @Id
    public long id;

    @ManyToOne(cascade = CascadeType.ALL)
    public CampusProgram campusProgram;

    @ManyToOne(cascade = CascadeType.ALL)
    public StudyMode mode;

    public Boolean deleteStatus = false;

    public static Model.Finder<Long, CampusProgramMode> find = new Finder<Long, CampusProgramMode>(Long.class, CampusProgramMode.class);

    public List<SessionMode> allSess(){
        return SessionMode.find.where().eq("deleteStatus",false).eq("mode.id",this.mode.id).eq("mode.deleteStatus",false).findList();
    }
    public Boolean exist(){
       CampusProgramMode c = find.where().not(Expr.eq("id",this.id)).eq("deleteStatus",false).eq("campusProgram.id",this.campusProgram.id).eq("mode.id",this.mode.id).setMaxRows(1).findUnique();
       return ( c != null );
    }

    public static List<CampusProgramMode> all(){
        return find.where().eq("deleteStatus",false).findList();
    }

    public static CampusProgramMode finderById(Long id){
        return find.ref(id);
    }
}
