package models;

import com.avaje.ebean.Expr;
import com.fasterxml.jackson.annotation.JsonProperty;
import play.db.ebean.Model;

import javax.persistence.Entity;
import java.util.List;

/**
 * Created by SISI on 9/27/2017.
 */
@Entity
public class AcademicYear extends BaseModel {
    public String yearName = "";
    public Boolean status = false;
    public Boolean deleteStatus = false;

    public  static boolean yearExist(String yearName){
        return find.where().eq("yearName",yearName)
                .and(Expr.eq("deleteStatus",false),Expr.eq("deleteStatus",false))
                .findList().parallelStream().findFirst().isPresent();
    }
    public static Model.Finder<Long, AcademicYear> find = new Finder<Long, AcademicYear>(Long.class, AcademicYear.class);

    public static List<AcademicYear> allBy(String q){
        String[] arr = q.split(Vld.split);
        if( !q.equals("")  && ( arr.length <= 1 ) )
            return find.where().not(Expr.eq("deleteStatus",true)).like("yearName","%"+q+"%").orderBy("id desc").findList();

        int a = (arr.length > 1 && Vld.isNumeric(arr[1]) ) ? Integer.parseInt(arr[1]) : 0 ;
        return allPage(a);
    }
    public static String active(){
        AcademicYear ac =  find.where().eq("deleteStatus", false).eq("status", true).setMaxRows(1).findUnique();
        return ( ac != null ) ? ac.yearName : "No active";
    }
    public static int activeTotal(){
        return find.where().eq("deleteStatus", false).eq("status", true).findRowCount();
    }

    public static List<AcademicYear> all(){
        return find.where().not(Expr.eq("deleteStatus",true)).orderBy("id desc").findList();
    }

    public static List<AcademicYear> bySpecific(Long id){
        return find.where().not(Expr.eq("deleteStatus",true)).orderBy("id='"+id+"' desc").findList();
    }

    public static String count(){
        int api = all().size();
        return String.valueOf( (api>0) ? api : 1 );
    }
    public static List<AcademicYear> allPage(int a){
        int ap = (a -1) * Vld.limit;
        return find.where().not(Expr.eq("deleteStatus",true)).setFirstRow(ap).setMaxRows(Vld.limit).orderBy("id desc").findList();
    }

    public static AcademicYear finderById(Long id){
        return find.ref(id);
    }

    @JsonProperty
    public String print(){
        return yearName;
    }

}
