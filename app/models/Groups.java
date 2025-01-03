package models;

import com.avaje.ebean.Expr;
import com.fasterxml.jackson.annotation.JsonProperty;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "groups_student")
public class Groups extends Model {
    @Id
    public long id;
    public String groupName = "";


    @ManyToOne(cascade = CascadeType.ALL)
    public Component component;

    @ManyToOne(cascade = CascadeType.ALL)
    public Training training;

    public Boolean deleteStatus = false;

    public static Model.Finder<Long, Groups> find = new Finder<Long, Groups>(Long.class, Groups.class);
    public  static String getName(Long id){
        if(id!=null){
            return finderById(id).groupName;
        }else {
            return null;
        }
    }
    public static List<Groups> trainGroups(Long id, Long tId) {
        return find.where("deleteStatus='false' and component.id='" + id + "' and training.id='" + tId + "' group by id").findList();
    }
    public  static Groups getGroup(Long id){
        if(id!=null){
            return finderById(id);
        }else {
            return null;
        }
    }



    public Boolean notExist(){
        Groups g = find.where()
                .eq("deleteStatus",false)
                .eq("training.id",this.training.id)
                .eq("component.id",this.component.id)
                .eq("groupName",this.groupName)
                .setMaxRows(1)
                .findUnique();
        return ( g == null );
    }
    public List<Groups> notThis(){
        return Groups.find.where().eq("deleteStatus",false).not(Expr.eq("id",this.id)).eq("training.id",this.training.id).eq("component.id",this.component.id).findList();
    }
    public List<GroupMembers> gMembers(){
        return GroupMembers.finder.where()
                .eq("deleteStatus",false)
                .eq("groups.id",this.id)
                .findList();
    }
    public List<Student> gStudents(String q){

        List<Student> lst = Student.finder.where().eq("deleteStatus",false).eq("training.id",this.training.id).like("familyName","%"+q+"%").findList();
        if( q.equals(":all") ){
            lst = Student.finder.where().eq("deleteStatus",false).eq("training.id",this.training.id).findList();
        }
        List<Student> nList = new ArrayList<>();
        for( Student stu : lst ){
            GroupMembers g = new GroupMembers();
            g.groups = this;
            g.student = stu;
            if( g.selfCheck() ) nList.add(stu);
        }

        return nList;
    }

    public static Groups finderById(Long id){
        return find.ref(id);
    }

    public int memberN(){
        return gMembers().size();
    }

    @JsonProperty
    public String print(){
        return this.groupName;
    }
    public static List<Groups> submittedByComponents(long cId){
        return find.setDistinct(true).where()
                .eq("component.id", cId)
                .findList();
    }


    public static List<Groups> all(){
        return find.setDistinct(true).where()
                .eq("deleteStatus", false)
                .setMaxRows(1)
                .findList();
    }
    public static List<Groups> allByComponent(long cId){
        return find.setDistinct(true).where()
                .eq("deleteStatus", false)
                .eq("component.id", cId)
                //.eq("training.id", tId)
                .setMaxRows(1)
                .findList();
    }

    public List<Assignment> myAssignments(){
        return Assignment.finder.setDistinct(true).where()
                .eq("group.id", this.id)
                .eq("grouped", 1)
                .findList();
    }
}
