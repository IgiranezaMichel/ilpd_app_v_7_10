package models.stock;

import com.fasterxml.jackson.annotation.JsonBackReference;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "category_group")
public class Group extends Model
{
    @Id
    public Long id;
    public String name = "";
    public String acronym = "";
    @Column(columnDefinition = "text")
    public String description = "";
    public boolean deleteStatus = false;
    public Date date = new Date();
    @JsonBackReference
    @OneToMany(mappedBy = "group")
    public List<Category> categories = new ArrayList<>();

    public static Model.Finder<Long, Group> finder = new Finder<>(Long.class, Group.class);


    public static Group find(Long id)
    {
        return finder.where().eq("deleteStatus", false).eq("id", id).setMaxRows(1).findUnique();
    }

    public static List<Group> all()
    {
        return finder.where().eq("deleteStatus", false).findList();
    }
}
