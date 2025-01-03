package models;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Date;
import java.util.List;

@Entity
public class CourseMaterial extends BaseModel {

    public String name;
    public String fileName;
    @ManyToOne
    public Schedule schedule;
    public Boolean deleteStatus = false;
    public Date date = new Date();
    public static Finder<Long, CourseMaterial> finder = new Finder<>(Long.class, CourseMaterial.class);

    @JsonProperty
    public String print(){
        return name;
    }
    public static List<CourseMaterial> all(){
        return finder.findList();
    }

    public static List<CourseMaterial> materialList(long lectId) {
        return finder.where()
                .eq("schedule.lecture.id",lectId)
                .eq("deleteStatus", false)
                .orderBy("date desc")
                .findList();
    }
}
