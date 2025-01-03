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
public class IntakeSessionMode extends Model {
    @Id
    public long id;

    @ManyToOne(cascade = CascadeType.ALL)
    public Intake intake;

    @ManyToOne(cascade = CascadeType.ALL)
    public SessionMode sessionMode;

    @ManyToOne(cascade = CascadeType.ALL)
    public CampusProgram campusProgram;

    public Boolean deleteStatus = false;

    public static Model.Finder<Long, IntakeSessionMode> find = new Finder<Long, IntakeSessionMode>(Long.class, IntakeSessionMode.class);

    public Boolean exist(){
        IntakeSessionMode p = find.where().not(Expr.eq("id",this.id)).eq("deleteStatus",false).eq("campusProgram.id",this.campusProgram.id).eq("intake.id",this.intake.id).eq("sessionMode.id",this.sessionMode.id).setMaxRows(1).findUnique();
        return ( p != null );
    }
    @JsonProperty
    public String print(){
        return this.intake.print()+" - "+this.sessionMode.print()+" - "+this.campusProgram.print();
    }
    @JsonProperty
    public String shortPrint(){
        return this.intake.print()+" - "+this.campusProgram.shortPrint();
    }
    public static List<IntakeSessionMode> all(){
        return find.where()
                .eq("deleteStatus",false)
                .eq("sessionMode.session.deleteStatus",false)
                .eq("sessionMode.deleteStatus",false)
                .eq("intake.deleteStatus",false)
                .eq("sessionMode.mode.deleteStatus",false)
                .eq("campusProgram.program.deleteStatus",false)
                .eq("campusProgram.campus.deleteStatus",false)
                .orderBy("id desc")
                .findList();
    }
    public static List<IntakeSessionMode> allCLE(){
        return find.where()
                .eq("deleteStatus",false)
                .eq("sessionMode.session.deleteStatus",false)
                .eq("sessionMode.deleteStatus",false)
                .eq("intake.deleteStatus",false)
                .eq("sessionMode.mode.deleteStatus",false)
                .eq("campusProgram.program.deleteStatus",false)
                .eq("campusProgram.program.cle",true)
                .orderBy("id desc")
                .findList();
    }


    public static IntakeSessionMode finderById(Long id){
        return find.ref(id);
    }
}
