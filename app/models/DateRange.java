package models;

import play.db.ebean.Model;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Entity
public class DateRange extends Model {
    @Id
    public Long id;
    public Date startDate;
    public Date endDate;
    public Date date=new Date();
    public Boolean deleteStatus=false;


    public static Finder<Long,DateRange> finder= new Finder<>(Long.class, DateRange.class);

    public static List<DateRange> all(){
        return finder.where()
                .eq("deleteStatus",false)
                .findList();
    }

    public static DateRange finderById(Long id){
        return finder.where().eq("deleteStatus",false).eq("id",id).findUnique();
    }

    @Override
    public String toString() {
        return new SimpleDateFormat("yyy/MM/dd").format(startDate)+" - "+ new SimpleDateFormat("yyy/MM/dd").format(endDate);
    }
}
