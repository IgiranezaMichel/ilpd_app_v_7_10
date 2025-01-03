package models;

import play.db.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Checked extends Model {
    @Id
    public long id;

    @ManyToOne(cascade = CascadeType.ALL)
    public Users user;

    @ManyToOne(cascade = CascadeType.ALL)
    public Announce announce;

    public Boolean deleteStatus = false;

    public static Model.Finder<Long, Checked> find = new Finder<Long, Checked>(Long.class, Checked.class);


    public Boolean notExist(){
        Checked r = find.where().eq("deleteStatus",false).eq("user.id",this.user.id).eq("announce.id",this.announce.id).setMaxRows(1).findUnique();
        return ( r == null );
    }
}
