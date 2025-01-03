package models;

import com.avaje.ebean.Expr;
import com.fasterxml.jackson.annotation.JsonProperty;
import play.db.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.List;

/**
 * Created by Noel on 06-Dec-17.
 */
@Entity
public class Room extends Model {
    @Id
    public long id;
    public String roomName = "";
    public String roomCode = "";

    @ManyToOne(cascade = CascadeType.ALL)
    public Campus campus;

    public String flowNumber = "";
    public int numberSeat = 0;
    public String roomType = "";
    public String doneBy = "";
    public Boolean deleteStatus = false;

    public static Finder<Long, Room> find = new Finder<Long, Room>(Long.class, Room.class);

    public Boolean notExist() {
        Room g = find.where().eq("deleteStatus", false).eq("roomCode", this.roomCode).eq("campus.id", this.campus.id).setMaxRows(1).findUnique();
        return (g == null);
    }

    public String print() {
        return roomName + " " + roomCode;
    }

    public List<Component> possibleComp() {
        return Component.finder.where().eq("deleteStatus", false).findList();
    }

    public static List<Room> allBy(String q) {
        String[] arr = q.split(Vld.split);
        if (!q.equals("") && (arr.length <= 1))
            return find.where().not(Expr.eq("deleteStatus", true)).like("roomName", "%" + q + "%").orderBy("id desc").findList();

        int a = (arr.length > 1 && Vld.isNumeric(arr[1])) ? Integer.parseInt(arr[1]) : 0;
        return allPage(a);
    }

    public static String count() {
        int api = all().size();
        return String.valueOf((api > 0) ? api : 1);
    }

    public static List<Room> all() {
        return find.where().not(Expr.eq("deleteStatus", true)).orderBy("id desc").findList();
    }

    public static List<Room> allPage(int a) {
        int ap = (a - 1) * Vld.limit;
        return find.where().not(Expr.eq("deleteStatus", true)).setFirstRow(ap).setMaxRows(Vld.limit).orderBy("id desc").findList();
    }

    public static Room finderById(Long id) {
        return find.ref(id);
    }

    @Override
    public String toString() {
        return roomName + "/" + roomCode;
    }

    @JsonProperty
    public String print1() {
        return roomName + "/" + roomCode;
    }
}
