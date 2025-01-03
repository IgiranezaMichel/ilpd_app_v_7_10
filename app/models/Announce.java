package models;
import com.fasterxml.jackson.annotation.JsonProperty;
import play.db.ebean.Model;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;
import java.util.List;

@Entity
public class Announce extends Model{
    @Id
    public long id;

    public String title = "";

    public String content = "";

    public Date date;

    public String attachment = "";

    public Date pubDate = new Date();

    public String type = "";

    public String category = "";


    public String status;
    public Boolean deleteStatus = false;


    public static Model.Finder<Long, Announce> find = new Finder<>(Long.class, Announce.class);

    public static List<Announce> all(Boolean req,String type){
        String stq = req  ? "announcement" : "event";
        return find.where("deleteStatus='false' and status='active' and type='"+type+"' and category='"+stq+"' or deleteStatus='false' and status='active' and type='All' and category='"+stq+"'").findList();
    }
    public static List<Announce> all(String type){
        return find.where("deleteStatus='false' and status='active' and type='"+type+"' or deleteStatus='false' and status='active' or type='All'").findList();
    }
    public static List<Announce> allByRole(String type){
        return find.where("deleteStatus='false' and status='active' and type='"+type+"' or deleteStatus='false' and status='active' and type='All'").findList();
    }
    public static List<Announce> all(){
        return find.where().eq("deleteStatus",false).eq("status","active").findList();
    }
    public static List<Announce> all(Boolean bool){
        String stq = bool ? "announcement" : "event";
        return find.where()
                .eq("category",stq)
                .eq("deleteStatus",false)
                .eq("status","active")
                .orderBy("pubDate desc")
                .findList();
    }
    public Boolean readBy(Users user){
        Checked n = Checked.find.where().eq("deleteStatus",false).eq("announce.id",this.id).eq("user.id",user.id).setMaxRows(1).findUnique();
        return n != null;
    }
    public static Announce finderById(Long id){
        return find.byId(id);
    }
    @JsonProperty
    public String print(){
        return title;
    }
}
