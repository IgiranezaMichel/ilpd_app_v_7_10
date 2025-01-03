package models;

import com.avaje.ebean.Expr;
import com.fasterxml.jackson.annotation.JsonProperty;
import play.db.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.List;

@Entity
public class CampusProgram extends Model {
    @Id
    public long id;

    @ManyToOne(cascade = CascadeType.ALL)
    public Campus campus;

    @ManyToOne(cascade = CascadeType.ALL)
    public Program program;

    public Boolean deleteStatus = false;

    public static Model.Finder<Long, CampusProgram> find = new Finder<Long, CampusProgram>(Long.class, CampusProgram.class);

    public List<CampusProgramMode> myAll(){
        return CampusProgramMode.find.where().eq("deleteStatus",false).eq("campusProgram.id",this.id).findList();
    }
    public Boolean exist(){
        CampusProgram p = find.where().not(Expr.eq("id",this.id)).eq("deleteStatus",false).eq("program.id",this.program.id).eq("campus.id",this.campus.id).setMaxRows(1).findUnique();
        return ( p != null );
    }

    @JsonProperty
    public String print(){
        return this.program.programAcronym + " in "+ this.campus.campusName;
    }

    @JsonProperty
    public String shortPrint(){
        return this.program.programAcronym;
    }

    public static List<CampusProgram> all(){
        return find.where().eq("deleteStatus",false).findList();
    }

    public static CampusProgram finderById(Long id){
        return find.ref(id);
    }


}
