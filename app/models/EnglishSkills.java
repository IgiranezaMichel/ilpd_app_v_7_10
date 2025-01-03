package models;

import com.avaje.ebean.Expr;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

/**
 * Created by SISI on 10/16/2017.
 */
@Entity
public class EnglishSkills extends Model {
    @Id
    public long id;
    public String skillName = "";
    public String labelName = "";
    public Boolean deleteStatus = false;

    public static Finder<Long, EnglishSkills> finder = new Finder<Long, EnglishSkills>(Long.class, EnglishSkills.class);

    public static List<EnglishSkills> all(){
        return finder.where().not(Expr.eq("deleteStatus",true)).findList();
    }

    public static EnglishSkills finderById(Long id){
        return finder.byId(id);
    }
}
