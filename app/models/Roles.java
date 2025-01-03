package models;

import com.avaje.ebean.Expr;
import com.fasterxml.jackson.annotation.JsonProperty;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

@Entity
public class Roles extends Model {
    @Id
    public long id;
    public String roleName = "";
    public String sessionName = "";
    public Boolean isAllowed;

    public static Finder<Long, Roles> finder = new Finder<Long, Roles>(Long.class, Roles.class);

    public static List<Roles> all(){
        return finder.where()
                .eq("isAllowed",true)
                .findList();
    }
    public static List<Roles> allSet(){
        return finder.where()
                .eq("isAllowed",true)
                .not(Expr.eq("sessionName", "Instructor"))
                .findList();
    }
    public static List<Roles> allAll(){
        return finder.where()
                .orderBy("sessionName asc")
                .findList();
    }

    public static Roles finderByName(String name){
        return finder.where().eq("sessionName",name).findUnique();
    }
    @JsonProperty
    public String print(){
        return roleName;
    }

}
