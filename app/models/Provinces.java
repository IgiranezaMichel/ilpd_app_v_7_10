package models;

import com.avaje.ebean.Expr;
import com.fasterxml.jackson.annotation.JsonProperty;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

/**
 * Created by SISI on 10/2/2017.
 */
@Entity
public class Provinces extends Model {
    @Id
    public long id;
    public String provinceName = "";


    public static Finder<Long, Provinces> finder = new Finder<Long, Provinces>(Long.class, Provinces.class);

    public static List<Provinces> all(){
        List<Provinces> user = finder.where().findList();
        return user;
    }

    @JsonProperty
    public String print(){
        return provinceName;
    }
}
