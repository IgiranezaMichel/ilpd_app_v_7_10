package models.stock;

import play.db.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.List;

@Entity
public class Supplier extends Model {
    @Id
    public long id;

    public String name = "";
    public String email = "";
    public String address = "";
    public int phone;
    public String tinNumber = "";

    public Boolean deleteStatus = false;

    public static Model.Finder<Long, Supplier> finder = new Finder<Long, Supplier>(Long.class, Supplier.class);

    public static List<Supplier> all(){

        return finder.where().eq("deleteStatus",false).findList();
    }
}
