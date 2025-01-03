package models;

import com.avaje.ebean.Expr;
import play.db.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.List;

@Entity
public class DaySession extends Model {
    @Id
    public long id;

    @ManyToOne(cascade = CascadeType.ALL)
    public Days day;

    @ManyToOne(cascade = CascadeType.ALL)
    public Session session;

    public Boolean deleteStatus = false;

    public static Finder<Long, DaySession> find = new Finder<Long, DaySession>(Long.class, DaySession.class);

    public static List<DaySession> all(){
        return find.where().eq("deleteStatus",false).findList();
    }

    public static DaySession finderById(Long id){
        return find.ref(id);
    }

    public Boolean exist(){
        DaySession d = find.where().not(Expr.eq("id",this.id)).eq("deleteStatus",false).eq("day.id",this.day.id).eq("session.id",this.session.id).setMaxRows(1).findUnique();
        return ( d != null );
    }
}
