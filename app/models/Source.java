package models;
import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;
import java.util.List;

@Entity
public class Source extends Model {
    @Id
    public Long id;
    public String name = "";
    @Column(columnDefinition = "text")
    public String description = "";
    public boolean deleteStatus = false;
    public Date date = new Date();

    public static Model.Finder<Long, Source> finder = new Finder<>(Long.class, Source.class);


    public static Source find(Long id)
    {
        return finder.where().eq("deleteStatus", false).eq("id", id).setMaxRows(1).findUnique();
    }

    public static List<Source> all()
    {
        return finder.where().eq("deleteStatus", false).findList();
    }
}
