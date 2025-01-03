package models.stock;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;
import com.fasterxml.jackson.annotation.JsonBackReference;
import org.joda.time.DateTime;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class Item extends Model {
    @Id
    public Long id;
    @ManyToOne(cascade = CascadeType.ALL)
    public Category category;
    public String name = "";
    public int min;
    public int max;
    public String unitMeasure = "";
    public Double unitPrice = 0.0;
    public int beginingQty = 0;
    public int purchaseQty = 0;
    public int consumptionQty = 0;
    public int balanceQty = 0;
    public int defQnty = 0;
    public String iType = "";
    public String removed = "";

    public String origin = "";
    public String allocationPlace = "";
    public int depleciationMinMonths;
    public String status = "";


    public boolean deleteStatus = false;
    public Date date = new Date();

    @JsonBackReference
    @OneToMany(mappedBy = "item")
    public List<Supplied> supplieds;

    @JsonBackReference
    @OneToMany(mappedBy = "item")
    public List<StockMovement> stockMovements;


    public static Model.Finder<Long, Item> finder = new Finder<>(Long.class, Item.class);

    public static Item find(Long id) {
        return finder.where().eq("deleteStatus", false).eq("id", id).setMaxRows(1).findUnique();
    }

    public static List<Item> all() {
        return finder.where().eq("deleteStatus", false).findList();
    }

    public int count() {
        int i = purchaseQty + consumptionQty - balanceQty;
        return i >= 0 ? i : i * -1;
    }

    public int countRemain() {
        int i = balanceQty;
        return i >= 0 ? i : i * -1;
    }

    public int getRemain(int beginning, int purchase, int consumed) {
        return ((beginning + purchase) - consumed);
    }

    public static int getStockValueWithDate(String date) {
        String s = "SELECT IFNULL(SUM(i.balance_qty),0) as qty FROM supplied s INNER JOIN supplier s2 ON s.supplier_id=s2.id INNER JOIN item i ON s.item_id=i.id WHERE DATE(s.date) <= DATE(:date)";
        return Ebean.createSqlQuery(s).setParameter("date", date).findUnique().getInteger("qty");
    }

    public int getBeginningStockQty(String date) {
        return getTotalInAtDate(date) - getTotaOutAtDate(date);
    }

    public int getTotalInAtDate(String date) {
        String s = "SELECT IFNULL(sum(s.supplied_qty),0) as qty FROM supplied s INNER JOIN item i ON i.id=s.item_id WHERE s.date <=:date AND i.id=:itemId ORDER BY s.id DESC";
        SqlRow sqlRow = Ebean.createSqlQuery(s)
                .setParameter("date", date)
                .setParameter("itemId", this.id).setMaxRows(1).findUnique();
        return sqlRow == null ? 0 : sqlRow.getInteger("qty");
    }

    public int getTotaOutAtDate(String date) {
        String s = "SELECT IFNULL(sum(m.confirmed_qty),0) as qty FROM stock_movement m INNER JOIN item i ON m.item_id=i.id WHERE m.date<=:date AND i.id=:itemId AND m.logistic_status=TRUE ORDER BY m.id DESC";
        SqlRow sqlRow = Ebean.createSqlQuery(s)
                .setParameter("date", date)
                .setParameter("itemId", this.id).setMaxRows(1).findUnique();
        return sqlRow == null ? 0 : sqlRow.getInteger("qty");
    }

    public int getPurchasedStockQty(String date1, String date2) {
        String s = "SELECT IFNULL(sum(s.supplied_qty),0) as qty FROM supplied s INNER JOIN item i ON i.id=s.item_id WHERE s.date BETWEEN :date1 AND :date2 AND i.id=:itemId ORDER BY s.id DESC";
        SqlRow sqlRow = Ebean.createSqlQuery(s)
                .setParameter("date1", date1)
                .setParameter("date2", date2)
                .setParameter("itemId", this.id).findUnique();
        return sqlRow.getInteger("qty");
    }


    public int getConsumedStockQty(String date1, String date2) {
        String s = "SELECT IFNULL(sum(s.confirmed_qty),0) as qty FROM stock_movement s INNER JOIN item i ON i.id=s.item_id WHERE i.id=:itemId AND DATE(s.date) BETWEEN :date1 AND :date2 AND s.head_of_unit_status=TRUE AND s.daf_status=TRUE AND s.logistic_status=TRUE";
        SqlRow sqlRow = Ebean.createSqlQuery(s)
                .setParameter("date1", date1)
                .setParameter("date2", date2)
                .setParameter("itemId", this.id).findUnique();
        return sqlRow.getInteger("qty");
    }

    public double getLastPrice() {
        Supplied supplied = Supplied.finder.where().eq("item.id", this.id).orderBy("id desc").setMaxRows(1).findUnique();
        return supplied == null ? 0 : supplied.unitPrice;
    }

    public double getAvgPriceInTwoDates() {
        String s = "SELECT IFNULL(avg(s.unit_price),0) as price FROM supplied s INNER JOIN item i ON i.id=s.item_id WHERE i.id=:itemId";
        SqlRow sqlRow = Ebean.createSqlQuery(s)
                .setParameter("itemId", this.id).findUnique();
        return sqlRow.getInteger("price");
    }

    public static List<Item> nonConsume() {
        return Item.finder.where().eq("iType", "Non-consumable").findList();
    }

    public int totalQtySupplied() {
        String s = "SELECT IFNULL(sum(s.supplied_qty), 0) AS qty FROM item i INNER JOIN supplied s ON i.id = s.item_id WHERE i.id=:itemId";
        SqlRow sqlRow = Ebean.createSqlQuery(s)
                .setParameter("itemId", this.id).findUnique();
        return sqlRow.getInteger("qty");
    }

    public int totalQtyConsumed() {
        String s = "SELECT IFNULL(sum(m.confirmed_qty), 0) AS qty FROM stock_movement m INNER JOIN item i ON i.id=m.item_id WHERE m.logistic_status=TRUE AND i.id=:itemId";
        SqlRow sqlRow = Ebean.createSqlQuery(s)
                .setParameter("itemId", this.id).findUnique();
        return sqlRow.getInteger("qty");
    }

    public int remainQty() {
        int total = totalQtySupplied() - totalQtyConsumed();
        return total < 0 ? 0 : total;
    }
    public static boolean checkExist(String name){
        boolean Exist = false;
        for(Item c : all()){
            if(!c.deleteStatus && c.name.equalsIgnoreCase(name)){
                Exist = true;
            }
        }
        return Exist;
    }
}
