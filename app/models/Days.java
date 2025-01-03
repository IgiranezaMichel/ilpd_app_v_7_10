package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import play.db.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.List;

@Entity
public class Days extends Model {
    @Id
    public long id;
    public String dayName = "";
    public int dayNumber;

    public Boolean deleteStatus = false;

    public static Finder<Long, Days> find = new Finder<Long, Days>(Long.class, Days.class);

    public static List<Days> all(){
        return find.where().eq("deleteStatus",false).findList();
    }

    public static Days finderById(Long id){
        return find.ref(id);
    }

    public Boolean exist(){
        Days d = find.where().eq("deleteStatus",false).eq("dayName",this.dayName).setMaxRows(1).findUnique();
        return ( d != null );
    }

    @JsonProperty
    public String print(){
        return dayName;
    }
}
