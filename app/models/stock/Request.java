package models.stock;

import com.fasterxml.jackson.annotation.JsonBackReference;
import models.Employee;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Entity
public class Request extends Model
{
    @Id
    public long id;
    @ManyToOne(cascade =  CascadeType.ALL)
    public Employee employee;
    public String status="pending";
    public boolean headOfUnitStatus=false;
    public boolean dafStatus=false;
    public boolean deleteStatus=false;

    public Date date=new Date();

    public static Model.Finder<Long, Request> finder= new Finder<>(Long.class, Request.class);
    @JsonBackReference
    @OneToMany(mappedBy = "request")
    public List<StockMovement> stockMovements = new ArrayList<>();
    public static Request find(Long id)
    {
        return finder.where()
                .eq("deleteStatus", false)
                .eq("id", id).setMaxRows(1)
                .findUnique();
    }
    public static List<Request> all()
    {
        return finder.where().eq("deleteStatus", false).findList();
    }
}
