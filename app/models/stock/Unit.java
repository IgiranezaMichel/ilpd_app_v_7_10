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
public class Unit extends Model {
    @Id
    public Long id;
    public String name = "";
    public String acronym = "";
    @Column(columnDefinition = "text")
    public String description = "";
    public boolean deleteStatus=false;
    public Date date=new Date();

    @JsonBackReference
    @OneToMany(mappedBy = "unit")
    public List<Employee> employee=new ArrayList<>();

    public static Model.Finder<Long, Unit> finder= new Finder<>(Long.class, Unit.class);

    public static Unit find(Long id)
    {
        return finder.where().eq("deleteStatus", false).eq("id", id).setMaxRows(1).findUnique();
    }

    public static List<Unit> all()
    {
        return finder.where().eq("deleteStatus", false).findList();
    }
}
