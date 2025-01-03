package models;

import com.avaje.ebean.Expr;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import models.stock.Position;
import models.stock.Request;
import models.stock.Unit;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Employee extends Model
{
    @Id
    public long id;

    public String employeeFirstName = "";
    public String employeeLastName = "";
    public String gender = "";
    public String employeeTitle = "";
    public Boolean isUser;

    @ManyToOne(cascade = CascadeType.ALL)
    public Unit unit;
    @ManyToOne(cascade = CascadeType.ALL)
    public Position position;

    public Boolean isHeadOfUnit = false;
    public boolean deleteStatus = false;
    public Date date = new Date();


    @JsonBackReference
    @OneToMany(mappedBy = "employee")
    public List<Request> requests = new ArrayList<>();

    public static Finder<Long, Employee> finder = new Finder<Long, Employee>(Long.class, Employee.class);

    public static List<Employee> all()
    {
        return Employee.finder.where().eq("deleteStatus", false).findList();
    }

    public static String count()
    {
        int api = finder.where().eq("deleteStatus", false).findRowCount();
        return String.valueOf((api > 0) ? api : 1);
    }
    public static List<Employee> allBy(String q)
    {
        String[] arr = q.split(Vld.split);
        if (!q.equals("") && (arr.length <= 1))
        {
            return finder.where().not(Expr.eq("deleteStatus", true)).icontains("employeeFirstName", q).orderBy("id desc").findList();
        }

        int a = (arr.length > 1 && Vld.isNumeric(arr[1])) ? Integer.parseInt(arr[1]) : 0;
        return all(a);
    }

    public static List<Employee> all(int a)
    {
        int ap = (a - 1) * Vld.limit;
        return finder.where().not(Expr.eq("deleteStatus", true)).setFirstRow(ap).setMaxRows(Vld.limit).orderBy("id desc").findList();
    }
    public Boolean exist()
    {
        Employee obj = finder.where().eq("deleteStatus", false).eq("employeeFirstName", this.employeeFirstName).eq("employeeLastName", this.employeeLastName).eq("gender", this.gender).setMaxRows(1).findUnique();
        return (obj != null);
    }

    @JsonProperty
    public String print()
    {
        return employeeFirstName + " " + employeeLastName;
    }

    public static Employee find(Long id)
    {
        return Employee.finder.where().eq("deleteStatus", false).eq("id", id).setMaxRows(1).findUnique();
    }


    @JsonBackReference
    public Users getUser()
    {
        return Users.finder.where().eq("employee.id", this.id).eq("deleteStatus", false).setMaxRows(1).findUnique();
    }
}
