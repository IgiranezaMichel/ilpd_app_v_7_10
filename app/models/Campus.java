package models;

import com.avaje.ebean.Expr;
import com.fasterxml.jackson.annotation.JsonProperty;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

/**
 * Created by SISI on 9/28/2017.
 */
@Entity
public class Campus extends Model {
    @Id
    public long id;
    public String campusName = "";
    public String campusLocation = "";
    public Boolean deleteStatus = false;


    public static Finder<Long, Campus> finder = new Finder<Long, Campus>(Long.class, Campus.class);

    public static List<Campus> allBy(String q){
        String[] arr = q.split(Vld.split);
        if( !q.equals("")  && ( arr.length <= 1 ) )
            return finder
                    .where()
                    .not(Expr.eq("deleteStatus",true))
                    .like("campusName","%"+q+"%")
                    .orderBy("id desc")
                    .findList();

        int a = (arr.length > 1 && Vld.isNumeric(arr[1]) ) ? Integer.parseInt(arr[1]) : 0 ;
        return allPage(a);
    }
    public List<CampusProgram> myProgram(){
        return CampusProgram.find.where().eq("deleteStatus",false).eq("campus.id",this.id).findList();
    }

    public static List<Campus> all(){
        return finder.where().eq("deleteStatus",false).orderBy("id desc").findList();
    }

    public static List<Campus> allPage(int a){
        int ap = (a -1) * Vld.limit;
        return finder.where().not(Expr.eq("deleteStatus",true)).setFirstRow(ap).setMaxRows(Vld.limit).orderBy("id desc").findList();
    }

    public static String count(){
        int api = all().size();
        return String.valueOf( (api>0) ? api : 1 );
    }

    public static List<Campus> allBy(Long id){
        return finder.where().not(Expr.eq("deleteStatus",true)).orderBy("id='"+id+"' desc").findList();
    }

    public static Campus finderById(Long id){
        return finder.ref(id);
    }

    public List<Room> myRooms(){
        return Room.find.where().eq("deleteStatus",false).eq("campus.id",this.id).findList();
    }

    @JsonProperty
    public String print(){
        return this.campusName;
    }
}
