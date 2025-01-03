package models;

import com.avaje.ebean.Expr;
import com.fasterxml.jackson.annotation.JsonProperty;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.List;

/**
 * Created by SISI on 10/2/2017.
 */
@Entity
public class Districts extends Model{
    @Id
    public long id;
    public String districtName = "";

    @ManyToOne(cascade = CascadeType.ALL)
    public Provinces provinces;

    public static Finder<Long, Districts> finder = new Finder<>(Long.class, Districts.class);

    public static List<Districts> allByPro(Long id){
        return finder.where().eq("provinces.id",id).findList();
    }

    public static Districts finderById(Long id){
        return finder.byId(id);
    }


    @JsonProperty
    public String print(){
        return districtName;
    }
}
