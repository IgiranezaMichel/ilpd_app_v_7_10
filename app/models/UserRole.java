package models;
import play.db.ebean.Model;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.util.List;
@Entity
public class UserRole extends Model {
    @Id
    public long id;
    @NotNull
    @ManyToOne
    public Roles role;
    @NotNull
    @ManyToOne
    public Users user;
    public Boolean deleteStatus = false;

    public static Model.Finder<Long, UserRole> finder = new Finder<>(Long.class, UserRole.class);

    public Boolean notExist(){
        UserRole roles = finder.where().eq("deleteStatus",false).eq("user.id",this.user.id).eq("role.id",this.role.id).setMaxRows(1).findUnique();
        return ( roles == null );
    }
    public static List<UserRole> all(){
        return finder.where()
                .eq("deleteStatus",false)
                .findList();
    }
    public List<Users> myUsers(){
        return Users.finder.where()
                .eq("id", this.user.id)
                .findList();
    }
}
