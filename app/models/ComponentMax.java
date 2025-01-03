package models;

import com.avaje.ebean.Ebean;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class ComponentMax extends Model {
    @Id
    public long id;
    @ManyToOne
    public Component component;

    public float researchMax;
    public float examMax;
    public float resitMax;
    public float resitResearchMax;

    @ManyToOne
    public Lecture lecture;
    @ManyToOne
    public Training training;

    public static Finder<Long, ComponentMax> finder = new Finder<>(Long.class, ComponentMax.class);

    public static ComponentMax componentMax(long compId,long lectId,long tId){
        return finder.where()
                .eq("lecture.id",lectId)
                .eq("component.id",compId)
                .eq("training.id",tId)
                .setMaxRows(1).findUnique();
    }
    public static ComponentMax byCom(long compId,long lectId){
        return finder.where()
                .eq("lecture.id",lectId)
                .eq("component.id",compId)
                .setMaxRows(1).findUnique();
    }
    public static double totalExamDone(long cId, long tId) {
        String s = "SELECT DISTINCT IFNULL(COUNT(c.id),1) as total FROM component_max cm INNER JOIN component c ON cm.component_id = c.id INNER JOIN training t ON cm.training_id = t.id INNER JOIN module m ON c.module_id = m.id WHERE cm.exam_max >= 0 AND c.id=:cid AND t.id=:tid and c.delete_status = false and m.delete_status = false";
        double total = Ebean.createSqlQuery(s)
                .setParameter("tid", tId)
                .setParameter("cid", cId)
                .findUnique()
                .getDouble("total");
        return total;
    }
    public static double totalExamDoneModule(long cId, long tId) {
        String s = "SELECT DISTINCT IFNULL(COUNT(c.id),1) as total FROM component_max cm INNER JOIN component c ON cm.component_id = c.id INNER JOIN training t ON cm.training_id = t.id INNER JOIN module m ON c.module_id = m.id WHERE cm.exam_max >= 0 AND m.id=:mid AND t.id=:tid and c.delete_status = false and m.delete_status = false";
        double total = Ebean.createSqlQuery(s)
                .setParameter("tid", tId)
                .setParameter("mid", cId)
                .findUnique()
                .getDouble("total");
        return total;
    }
}
