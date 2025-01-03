package models;

import com.avaje.ebean.Expr;
import com.fasterxml.jackson.annotation.JsonProperty;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;
import play.db.ebean.Model.Finder;
/**
 * Created by SISI on 9/29/2017.
 */
@Entity
public class Session extends Model {
    @Id
    public long id;
    public String sessionName = "";
    public String sessionStart = "";
    public String sessionEnd = "";
    public String startHour = "";
    public String endHour = "";

    public Boolean deleteStatus = false;

    public static Finder<Long, Session> finder = new Finder<>(Long.class, Session.class);

    public List<HourSession> myHours(){
        return HourSession.find.where().eq("deleteStatus",false).eq("session.deleteStatus",false).eq("session.id",this.id).eq("hour.deleteStatus",false).findList();
    }
    public List<DaySession> myDays(){
        return DaySession.find.where().eq("deleteStatus",false).eq("session.deleteStatus",false).eq("day.deleteStatus",false).eq("session.id",this.id).findList();
    }


    public static List<Session> byPro(Long facId){
        return finder.where().eq("deleteStatus",false).eq("studyMode.id",facId).findList();
    }

    public static List<Session> allBy(String q){
        String[] arr = q.split(Vld.split);

        if( !q.equals("")  && ( arr.length <= 1 ) )
        return finder.where().not(Expr.eq("deleteStatus",true)).like("sessionName","%"+q+"%").orderBy("id desc").findList();

        int a = (arr.length > 1 && Vld.isNumeric(arr[1]) ) ? Integer.parseInt(arr[1]) : 0 ;
        return allPage(a);
    }
    public static String count(){
        int api = all().size();
        return String.valueOf( (api>0) ? api : 1 );
    }

    public static List<Session> allPage(int a){
        int ap = (a -1) * Vld.limit;
        return finder.where().eq("deleteStatus",false).setFirstRow(ap).setMaxRows(Vld.limit).orderBy("id desc").findList();
    }

    public static List<Session> byProgram(Long id){
        return finder.where().not(Expr.eq("deleteStatus",true)).eq("faculty.id",id).findList();
    }

    public static List<Session> all(){
        return finder.where().not(Expr.eq("deleteStatus",true)).orderBy("id asc").findList();
    }
    public static List<Session> byType(String type){
        return finder.where().not(Expr.eq("deleteStatus",true)).orderBy("id desc").findList();
    }
    public static Session finderById(Long id){
        return finder.ref(id);
    }

//    @Override
//    public String toString() {
//        return this.sessionName;
//    }

    @JsonProperty
    public String print(){
        return this.sessionName;
    }

}
