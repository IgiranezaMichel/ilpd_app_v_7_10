package models;

import com.avaje.ebean.Expr;
import play.db.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.List;

@Entity
public class AnounceRole extends Model {
    @Id
    public long id;
    @ManyToOne(cascade = CascadeType.ALL)
    public Roles role;
    @ManyToOne(cascade = CascadeType.ALL)
    public Announce announce;
    public Boolean deleteStatus = false;

    public static Finder<Long, AnounceRole> finder = new Finder<Long, AnounceRole>(Long.class, AnounceRole.class);
    public Boolean notExist(){
        AnounceRole anounceRole = finder.where()
                .eq("deleteStatus",false)
                .eq("announce.id",this.announce.id)
                .eq("role.id",this.role.id)
                .setMaxRows(1)
                .findUnique();
        return ( anounceRole == null );
    }
    public static List<AnounceRole> allByUserRole(String role) {
        return finder.setDistinct(true)
                .where()
                .eq("deleteStatus", false)
                .eq("role.sessionName", role)
                .orderBy("id desc")
                .findList();
    }
}
