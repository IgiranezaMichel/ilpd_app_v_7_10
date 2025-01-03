package models;
import com.avaje.ebean.Expr;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.mindrot.jbcrypt.BCrypt;
import play.db.ebean.Model;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Users extends Model {
    @Id
    public long id;
    public String names = "";
    public String profile = "";
    public String email = "";
    public String usedEmail = "";
    public String password = "";
    public String phone = "";
    public int code;
    public String role = "";
    public String stating = "";
    public String resetCode = "";
    public String path = "";
    public Boolean resetRequired = false;

    @JsonBackReference
    @OneToMany(mappedBy = "user")
    public List<Applicant> applicantList = new ArrayList<>();

    @OneToOne
    public Employee employee;

    public Boolean deleteStatus = false;

    public static Finder<Long, Users> finder = new Finder<>(Long.class, Users.class);
    public static Finder<Long, Users> find = new Finder<>(Long.class, Users.class);

    public Boolean notExist() {
        Users user = finder.where()
                .eq("deleteStatus", false)
                .eq("employee.id", this.employee.id)
                .setMaxRows(1)
                .findUnique();
        return (user == null);
    }

    public Lecture activeL() {
        return Lecture.byUser(this.id);
    }

    public List<ChatMessage> ourChat(Users u) {
        return ChatMessage.find.where("sendFrom.id='" + this.id + "' and sendTo.id='" + u.id + "' and deleteStatus=0 or sendFrom.id='" + u.id + "' and sendTo.id='" + this.id + "' and deleteStatus=0").findList();
    }

    public Boolean markRead(Users u) {
        List<ChatMessage> n = ChatMessage.find.where().eq("sendFrom.id", u.id).eq("sendTo.id", this.id).findList();
        for (ChatMessage nn : n) {
            nn.messageRead = true;
            nn.update();
        }
        return true;
    }

    public List<ChatMessage> newMess() {
        return ChatMessage.find.where("sendTo.id='" + this.id + "' and messageRead=0 group by sendFrom.id").setMaxRows(5).findList();
    }

    public int unread() {
        return ChatMessage.find.where().eq("deleteStatus", false).eq("sendTo.id", this.id).eq("messageRead", false).findRowCount();
    }

    public int SpecificUnread(Users u) {
        return ChatMessage.find.where().eq("deleteStatus", false).eq("sendTo.id", this.id).eq("sendFrom.id", u.id).eq("messageRead", false).findRowCount();
    }
    public Student me() {
        return Student.finder.where()
                .eq("deleteStatus", false)
                .eq("applicant.user.id", this.id)
                .eq("applicant.active", true)
                .setMaxRows(1)
                .findUnique();
    }


    public int myApplication() {
        return Applicant.finder.where()
                .eq("deleteStatus", false)
                .eq("user.id", this.id)
                .findRowCount();
    }

    public Applicant amApplicant() {
        return Applicant.finderByUserActive(this.id);
    }

    public List<Users> exceptMe() {
        return find.where()
                .not(Expr.eq("id", this.id))
                .setMaxRows(65)
                .findList();
    }

    public List<Users> exceptMe(String key) {
        return find.where()
                .not(Expr.eq("id", this.id)).like("email", "%" + key + "%")
                .setMaxRows(65)
                .findList();
    }

    public Account myAccount() {
        return Account.finder.where().eq("deleteStatus", false).eq("applicant.user.id", this.id).setMaxRows(1).findUnique();
    }

    public static List<Users> login(String u, String p) {
        return finder.where().not(Expr.eq("deleteStatus", 1)).like("email", u).like("password", p).findList();
    }

    public Boolean emailTaken() {
        Users u = find.where().eq("deleteStatus", false).eq("email", email).findUnique();
        return (u != null);
    }

    public static Users finderById(Long id) {
        return finder.ref(id);
    }

    public static Users finderByMail(String email) {
        return finder.where()
                .like("email", email)
                .setMaxRows(1)
                .findUnique();
    }

    public static Users finderByCodes(String code) {
        return finder.where().eq("deleteStatus", false).eq("resetCode", code).setMaxRows(1).findUnique();
    }
    public static List<Users> allByQ(String q)
    {
        String[] arr = q.split(Vld.split);
        if (!q.equals("") && (arr.length <= 1))
        {
            return finder.where().or(
                    com.avaje.ebean.Expr.like("email", "%" + q + "%"),
                    com.avaje.ebean.Expr.like("phone",  "%" + q + "%")
            )
            .not(Expr.eq("deleteStatus", true))
            .orderBy("id desc")
            .findList();
        }

        int a = (arr.length > 1 && Vld.isNumeric(arr[1])) ? Integer.parseInt(arr[1]) : 0;
        return all(a);
    }
    public static List<Users> allBy(String q) {
        String[] arr = q.split(Vld.split);
        if (!q.equals("") && (arr.length <= 1))
            return finder.where().or(
                    com.avaje.ebean.Expr.like("names", "%" + q + "%"),
                    com.avaje.ebean.Expr.like("email",  "%" + q + "%")
            )
            .not(Expr.eq("deleteStatus", true))
            .orderBy("id desc")
            .findList();
        int a = (arr.length > 1 && Vld.isNumeric(arr[1])) ? Integer.parseInt(arr[1]) : 0;
        return allPage(a);
    }
    public static List<Users> allPage(int a) {
        int ap = (a - 1) * Vld.limit;
        return finder.where()
                .not(Expr.eq("deleteStatus", true))
                .setFirstRow(ap).setMaxRows(Vld.limit).orderBy("id desc")
                .findList();
    }
    public static List<Users> all(int a) {
        int ap = (a - 1) * Vld.limit;
        return finder.where().not(Expr.eq("deleteStatus", true))
                .setFirstRow(ap).setMaxRows(Vld.limit).orderBy("id desc").findList();
    }
    public static List<Users> all() {
        return finder.findList();
    }
    public static List<Users> allUsers() {
        return finder.where()
                .eq("deleteStatus", false)
                .findList();
    }
    public static List<Users> allUnhashed() {
        return finder.where()
                .orderBy("role asc")
                .findList();
    }

    public static List<Users> five() {
        return finder.where().not(Expr.eq("deleteStatus", true)).not(Expr.eq("role", "student")).setMaxRows(5).orderBy("id desc").findList();
    }

    public static Boolean checkMail(String email) {
        List<Users> u = finder.where()
                .eq("deleteStatus", false)
                .like("email", email)
                .findList();
        return u.size() <= 0;
    }
    public static String count() {
        int api = finder.where().not(Expr.eq("deleteStatus", true))
                .findRowCount();
        return String.valueOf((api > 0) ? api : 1);
    }
    public static int totalSystemUsers() {
        return find.where().not(Expr.eq("role", "student")).eq("deleteStatus", false).findRowCount();
    }
    public UserRole getRole(Roles role) {
        return UserRole.finder.where()
                .eq("deleteStatus", false)
                .eq("role.id", role.id)
                .eq("user.id", this.id)
                .setMaxRows(1).findUnique();
    }
    @JsonProperty
    public String print() {
        return (this.employee != null) ? employee.employeeFirstName + " " + employee.employeeLastName : "";
    }
    public Boolean hasRole(Roles role) {
        return (getRole(role) != null);
    }
    public List<UserRole> RolesForUser() {
        return UserRole.finder.where()
                .eq("deleteStatus", false)
                .eq("user.id", this.id)
                .findList();
    }
    public static Users getEmployee(Employee employee) {
        return Users.finder.where().eq("deleteStatus", false).eq("employee.id", employee.id).setMaxRows(1).findUnique();
    }
   public static Users checkUser(String email, String password) {
        Users user =  finder.where()
                .eq("email", email)
                .eq("deleteStatus", false)
                .setMaxRows(1)
                .findUnique();
       if(user != null && user.deleteStatus){
           user.deleteStatus = false;
           user.update();
       }
        if(BCrypt.checkpw(password, user.password)) {
            return user;
        }else{
            return null;
        }
    }
}
