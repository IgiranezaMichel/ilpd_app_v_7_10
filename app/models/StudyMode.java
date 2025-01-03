package models;

import com.avaje.ebean.Expr;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class StudyMode extends Model {
    @Id
    public long id;
    public String modeName = "";
    public String modeAcronym = "";

    public Boolean deleteStatus = false;


    public static Model.Finder<Long, StudyMode> find = new Finder<Long, StudyMode>(Long.class, StudyMode.class);

    public static StudyMode finderById(Long id){
        return find.byId(id);
    }
    public static List<StudyMode> all(){
        return find.where().eq("deleteStatus",false).findList();
    }


    public static List<StudyMode> allBy(String q){
        String[] arr = q.split(Vld.split);

        if( !q.equals("")  && ( arr.length <= 1 ) )
            return find.where().not(Expr.eq("deleteStatus",true)).like("modeName","%"+q+"%").orderBy("id desc").findList();

        int a = (arr.length > 1 && Vld.isNumeric(arr[1]) ) ? Integer.parseInt(arr[1]) : 0 ;
        return allPage(a);
    }
    public static String count(){
        int api = all().size();
        return String.valueOf( (api>0) ? api : 1 );
    }

    public static List<StudyMode> allPage(int a){
        int ap = (a -1) * Vld.limit;
        return find.where().not(Expr.eq("deleteStatus",true)).setFirstRow(ap).setMaxRows(Vld.limit).orderBy("id desc").findList();
    }

    public static List<StudyMode> byProgram(Long id){
        return find.where().not(Expr.eq("deleteStatus",true)).eq("faculty.id",id).findList();
    }
    public static List<StudyMode> byType(String type){
        return find.where().not(Expr.eq("deleteStatus",true)).orderBy("id desc").findList();
    }

}
