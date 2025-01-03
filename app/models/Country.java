package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

/**
 * Created by Noel on 07-Dec-17.
 */
@Entity
public class Country extends Model {
    @Id
    public int id;
    public String iso = "";
    public String name = "";
    public String nicename = "";
    public String iso3 = "";
    public int numcode;
    public int phonecode;

    public static Finder<Long, Country> finder = new Finder<Long, Country>(Long.class, Country.class);

    public static List<Country> all(){

        return finder.where().findList();
    }

    public static Country finderById(Long id){
        return finder.byId(id);
    }


    @JsonProperty
    public String print(){
        return nicename;
    }

}
