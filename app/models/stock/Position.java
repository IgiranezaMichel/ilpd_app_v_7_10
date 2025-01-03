package models.stock;

import com.fasterxml.jackson.annotation.JsonBackReference;
import models.Employee;
import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Position extends Model {
@Id
    public Long id;
    public String name = "";
    public String acronym = "";
    @Column(columnDefinition = "text")
    public String description = "";
    public boolean deleteStatus=false;
    public Date date=new Date();

    @JsonBackReference
    @OneToMany(mappedBy = "position")
    public List<Employee> employee=new ArrayList<>();

    public static Model.Finder<Long, Position> finder= new Finder<>(Long.class, Position.class);

    public static Position find(Long id)
    {
        return finder.where().eq("deleteStatus", false).eq("id", id).setMaxRows(1).findUnique();
    }

    public static List<Position> all()
    {
        return finder.where().eq("deleteStatus", false).findList();
    }

}
