package models;

import com.avaje.ebean.Expr;
import com.fasterxml.jackson.annotation.JsonProperty;
import play.db.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.List;

@Entity
public class SessionMode extends Model {
    @Id
    public long id;

    @ManyToOne(cascade = CascadeType.ALL)
    public Session session;

    @ManyToOne(cascade = CascadeType.ALL)
    public StudyMode mode;

    public Boolean deleteStatus = false;

    public static Model.Finder<Long, SessionMode> find = new Finder<Long, SessionMode>(Long.class, SessionMode.class);

    public List<IntakeSessionMode> allbySession(){
        return IntakeSessionMode.find.where().eq("deleteStatus",false).eq("sessionMode.id",this.id).findList();
    }
    public List<IntakeSessionMode> allbySession(Long cProg){
        return IntakeSessionMode.find.where()
                .eq("deleteStatus",false)
                .eq("sessionMode.id",this.id)
                .eq("campusProgram.id",cProg)
                .eq("intake.isClosed",false)
                .findList();
    }
    public Boolean exist(){
        SessionMode p = find.where().not(Expr.eq("id",this.id)).eq("deleteStatus",false).eq("session.id",this.session.id).eq("mode.id",this.mode.id).setMaxRows(1).findUnique();
        return ( p != null );
    }

    @JsonProperty
    public String print(){
        return this.session.sessionName;
    }
    public static List<SessionMode> all(){
        return find.where().eq("deleteStatus",false).eq("session.deleteStatus",false).eq("mode.deleteStatus",false).findList();
    }

    public static SessionMode finderById(Long id){
        return find.ref(id);
    }
}
