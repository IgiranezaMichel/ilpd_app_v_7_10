package models;

import com.avaje.ebean.Expr;
import com.fasterxml.jackson.annotation.JsonProperty;
import play.db.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.List;

/**
 * Created by SISI on 10/2/2017.
 */
@Entity
public class Sector extends Model {
    @Id
    public long id;
    public String sectorName = "";

    @ManyToOne(cascade = CascadeType.ALL)
    public Districts districts;

    public static Finder<Long, Sector> finder = new Finder<Long, Sector>(Long.class, Sector.class);

    public static List<Sector> allByDis(Long id){
        List<Sector> user = finder.where().like("districts",String.valueOf(id)).findList();
        return user;
    }
    public static Sector finderById(Long id){
        return finder.ref(id);
    }
    public static Sector defaultSec(){
        return finder.where().setMaxRows(1).findUnique();
    }


    @JsonProperty
    public String print(){
        return sectorName;
    }
}
