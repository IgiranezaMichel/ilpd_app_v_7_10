package models.stock;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class Supplied extends Model {
    @Id
    public Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    public Item item;

    public int suppliedQty = 0;
    public Double unitPrice = 0.0;
    public String fund = "";
    @ManyToOne(cascade = CascadeType.ALL)
    public Supplier supplier;

    public int beginingQty = 0;

    public Date aquistionDate = new Date();

    public Date expirationDate = new Date();

    public Date date = new Date();

    public Boolean deleteStatus = false;

    public static Model.Finder<Long, Supplied> finder = new Finder<Long, Supplied>(Long.class, Supplied.class);



    public static List<Supplied> all(){
        return finder.where()
                .eq("deleteStatus",false)
                .orderBy("date desc")
                .findList();
    }
    public static Supplied find(Long id){
        return finder.where().eq("deleteStatus",false).eq("id",id).setMaxRows(1).findUnique();
    }
}
