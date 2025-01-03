package models.stock;

import com.fasterxml.jackson.annotation.JsonBackReference;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Block extends Model
{
    @Id
    public Long  id;
    public String name = "";
    public String acronym = "";
    public boolean deleteStatus = false;
    public Date date = new Date();

    public static Finder<Long, Block> finder= new Finder<>(Long.class, Block.class);

    @JsonBackReference
    @OneToMany(mappedBy = "block")
    public List<Location> locations=new ArrayList<>();
    public static Block find(Long id)
    {
        return finder.where().eq("deleteStatus", false).eq("id", id).setMaxRows(1).findUnique();
    }

    public static List<Block> all()
    {
        return finder.where().eq("deleteStatus", false).findList();
    }
}
