package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import play.db.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.List;

@Entity
public class Hours extends Model {
    @Id
    public long id;
    public String hourName = "";

    public Boolean deleteStatus = false;

    public static Finder<Long, Hours> find = new Finder<Long, Hours>(Long.class, Hours.class);

    public static Hours finderById(Long id){
        return find.ref(id);
    }

    public static List<Hours> all(){
        return find.where().eq("deleteStatus",false).findList();
    }
    public Boolean exist(){
        Hours d = find.where().eq("deleteStatus",false).eq("hourName",this.hourName).setMaxRows(1).findUnique();
        return ( d != null );
    }

    @JsonProperty
    public String print(){
        return hourName;
    }
}
