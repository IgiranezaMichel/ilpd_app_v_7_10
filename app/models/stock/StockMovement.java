package models.stock;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import play.db.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Entity
public class StockMovement extends Model
{
    @Id
    public Long id;
    @ManyToOne(cascade = CascadeType.ALL)
    public Item item;
    @ManyToOne(cascade = CascadeType.ALL)
    public Request request;

    public int proposedQty = 0;
    public Integer confirmedQty = 0;
    public Double unitPrice = 0.0;

    public String sourceFund = "";

    public Boolean logisticStatus = false;
    public String logisticComment = "";
    public Boolean headOfUnitStatus = false;
    public String headOfUnitComment = "";
    public Boolean dafStatus = false;
    public String dafComment = "";
    public String tagNumber;
    public String serialNumber = "";
    public Boolean status = false;
    public Integer depleciationYear;
    public String iType = "Stock";
    public Long fromLocation;
    public Long toLocation;
    @ManyToOne
    public Location location;

    public boolean deleteStatus = false;
    public String doneBy = "";
    public Timestamp doneAt = new Timestamp(new Date().getTime());
    public Date date = new Date();
    public static Model.Finder<Long, StockMovement> finder = new Finder<>(Long.class, StockMovement.class);
    public boolean employeeStatus = false;

    public static ExpressionList<StockMovement> query()
    {
        return finder.where().eq("deleteStatus", false);
    }

    public static int getRequests(long itId)
    {
        String s = "SELECT IFNULL(SUM(s.confirmed_qty),0) as qty FROM stock_movement s INNER JOIN item s2 ON s.item_id=s2.id WHERE s2.id=:id";
        return Ebean.createSqlQuery(s).setParameter("id", itId).findUnique().getInteger("qty");
    }

    public static StockMovement find(Long id)
    {
        return query().eq("id", id).setMaxRows(1).findUnique();
    }

    public static List<StockMovement> all()
    {
        return finder.where()
                .eq("deleteStatus", false)
                .orderBy("id desc")
                .findList();
    }

    public static List<StockMovement> allForHeadOfUnit()
    {
        return finder.where().eq("deleteStatus", false).orderBy("headOfUnitStatus asc").findList();
    }

    public static List<StockMovement> allForDAF()
    {
        return finder.where().eq("deleteStatus", false).eq("headOfUnitStatus", true).orderBy("dafStatus asc").findList();
    }

    public static List<StockMovement> allForLogistic()
    {
        return finder.where().eq("deleteStatus", false).eq("dafStatus", true).orderBy("logisticStatus asc").findList();
    }

    public static List<StockMovement> history()
    {
        return finder.where()
                .eq("deleteStatus", false)
                .order("id desc")
                .findList();
    }

    public List<Supplied> suppliedList()
    {
        return Supplied.finder.where().eq("deleteStatus", false).findList();
    }

    public String color()
    {
        return (this.logisticStatus) ? "bg-green" : "bg-red";
    }

    public String string()
    {
        return (this.logisticStatus) ? "accepted" : "pending";
    }

    public Boolean isEditable()
    {
        return (!this.logisticStatus && !this.status);
    }


    public static int totalRequest(Request request)
    {
        return finder.where().eq("deleteStatus", false).eq("request.id", request.id).findRowCount();
    }

    public static boolean hasLocation(Long id)
    {
        return false;
    }

    public String locationName(){
        return this.location != null ? this.location.toString() : "";
    }
}
