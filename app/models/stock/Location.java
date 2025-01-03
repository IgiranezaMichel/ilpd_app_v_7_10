package models.stock;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;
import java.util.List;

@Entity
public class Location extends Model
{
    @Id
    public Long  id;
    public String name="";
    @ManyToOne
    public Block block;
    public String acronym="";
    public boolean deleteStatus=false;
    public Date date=new Date();

    public static Model.Finder<Long, Location> finder= new Finder<>(Long.class, Location.class);
    public static Location find(Long id)
    {
        return finder.where()
                .eq("deleteStatus", false).
                        eq("id", id)
                .setMaxRows(1).findUnique();
    }

    public static List<Location> all()
    {
        return finder.where()
                .eq("deleteStatus", false)
                .orderBy("name asc")
                .findList();
    }

    @Override
    public String toString()
    {
        return name+" - "+block.name;
    }
}
