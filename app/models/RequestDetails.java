package models;

import com.avaje.ebean.Expr;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.List;

@Entity
public class RequestDetails extends BaseModel {
    @Id
    public long id;
    public Boolean deleteStatus = false;
    @ManyToOne
    public ReSitReTakeRequest request;
    @ManyToOne
    public Module module;


    public static Finder<Long, RequestDetails> finder = new Finder<Long, RequestDetails>(Long.class, RequestDetails.class);
    public static List<RequestDetails> all(){
        return finder.where()
                .not(Expr.eq("deleteStatus",true))
                .orderBy("id desc").findList();
    }
    public static int inRequest(Long sId, Long mId) {
        return RequestDetails.finder.where()
                .eq("request.student.id", sId)
                .eq("module.id", mId)
                .eq("deleteStatus", false)
                .findRowCount();
    }
    public static String count(){
        int api = all().size();
        return String.valueOf( (api>0) ? api : 1 );
    }
    public static List<RequestDetails> allBy(Long id){
        return finder.where()
                .not(Expr.eq("deleteStatus",true))
                .orderBy("id='"+id+"' desc")
                .findList();
    }
    public static RequestDetails finderById(Long id){
        return finder.ref(id);
    }
}
