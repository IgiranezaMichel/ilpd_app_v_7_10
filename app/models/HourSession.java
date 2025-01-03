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
public class HourSession extends Model {
    @Id
    public long id;

    @ManyToOne(cascade = CascadeType.ALL)
    public Hours hour;

    @ManyToOne(cascade = CascadeType.ALL)
    public Session session;

    public Boolean deleteStatus = false;

    public static Finder<Long, HourSession> find = new Finder<Long, HourSession>(Long.class, HourSession.class);

    public static List<HourSession> all(){
        return find.where().eq("deleteStatus",false).findList();
    }

    public static HourSession finderById(Long id){
        return find.ref(id);
    }

    public Boolean exist(){
        HourSession d = find.where().not(Expr.eq("id",this.id)).eq("deleteStatus",false).eq("hour.id",this.hour.id).eq("session.id",this.session.id).setMaxRows(1).findUnique();
        return ( d != null );
    }

    @Override
    public String toString() {
        return hour.hourName+" - "+ session.sessionName;
    }

    @JsonProperty
    public String print(){
        return this.toString();
    }
}
