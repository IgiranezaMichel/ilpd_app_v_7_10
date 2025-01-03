package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;
import play.db.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.List;

@Entity
public class ILDPLibrary extends Model {
   @Id
   public long id;
   @ManyToOne(cascade = CascadeType.ALL)
   public Student student;
   public String bookName="";
   public String bookNumber="";
   public float bookCost=0;
//   public String comment="";
   public String act="";

   public boolean clear=false;
   public boolean deleteStatus=false;
   public static Finder<Long,ILDPLibrary> finder=new Finder<Long, ILDPLibrary>(Long.class,ILDPLibrary.class);
   public static ILDPLibrary finderById(long id){
      return finder.byId(id);
   }
   public static double cleanedLibrary(Long aId) {
      String s = "SELECT IFNULL(sum(b.book_cost), 0) AS amount FROM ildplibrary b INNER JOIN student s ON s.id = b.student_id INNER JOIN applicant a ON s.applicant_id = a.id WHERE b.clear = true and a.id =:id";
      SqlRow row = Ebean.createSqlQuery(s)
              .setParameter("id", aId).findUnique();
      return row.getDouble("amount");

   }
   public static List<ILDPLibrary> all(){
      return finder.where().eq("deleteStatus",false).eq("clear",false).orderBy("id DESC").findList();
   }
   public static double amountRefunded(long apId) {
      String s = "SELECT IFNULL(sum(b.book_cost), 0) AS amount FROM ildplibrary b INNER JOIN student s ON s.id = b.student_id INNER JOIN applicant a ON s.applicant_id = a.id WHERE a.id =:id";
      SqlRow row = Ebean.createSqlQuery(s)
              .setParameter("id", apId).findUnique();
      return row.getDouble("amount");
   }
   public static double libraryCharges(long apId) {
      String s = "SELECT IFNULL(sum(b.book_cost), 0) AS amount FROM ildplibrary b INNER JOIN student s ON s.id = b.student_id INNER JOIN applicant a ON s.applicant_id = a.id WHERE b.clear = false and a.id =:id";
      SqlRow row = Ebean.createSqlQuery(s)
              .setParameter("id", apId).findUnique();
      return row.getDouble("amount");
   }
}
