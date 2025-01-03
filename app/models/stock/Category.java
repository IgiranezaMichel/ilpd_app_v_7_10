package models.stock;
import com.avaje.ebean.Expr;
import com.fasterxml.jackson.annotation.JsonBackReference;
import models.Users;
import play.db.ebean.Model;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Category extends Model {
    @Id
    public Long id;
    public String name = "";
    public String acronym = "";
    @Column(columnDefinition = "text")
    public String description = "";
    @ManyToOne
    public Group group;
    public boolean deleteStatus = false;
    public Date date = new Date();

    @JsonBackReference
    @OneToMany(mappedBy = "category")
    public List<Item> items = new ArrayList<>();


    public static Category find(Long id){
        return finder.where().eq("deleteStatus",false).eq("id",id).setMaxRows(1).findUnique();
    }


    public List<StockMovement> requested(Users user){
        if( user.employee == null ) return new ArrayList<>();
        return StockMovement.finder.where().eq("deleteStatus",false).eq("items.item.category.id",this.id)/*.eq("requestReceiption.employee.id",user.employee.id)*/.orderBy("logisticStatus desc").findList();
    }

    public static List<Request> requests(Users user){
        if( user.employee == null ) return new ArrayList<>();
        return Request.finder.where().eq("employee.id",user.employee.id).eq("deleteStatus",false).not(Expr.eq("status","Processed")).findList();
    }

    public static Model.Finder<Long, Category> finder = new Finder<>(Long.class, Category.class);
    public static List<Category> all(){
        return finder.where()
                .eq("deleteStatus",false)
                .orderBy("name asc")
                .findList();
    }
    public List<Item> myItems(){
        return Item.finder.where().eq("deleteStatus",false).eq("category.id",this.id).findList();
    }
    public List<Supplied> myItemsSupplied(){
        return Supplied.finder.where().eq("deleteStatus",false).eq("item.category.id",this.id).findList();
    }
}
