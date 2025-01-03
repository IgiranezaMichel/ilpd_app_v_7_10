package models;

import play.db.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class GroupMembers extends Model {
    @Id
    public long id;

    @ManyToOne(cascade = CascadeType.ALL)
    public Student student;

    @ManyToOne(cascade = CascadeType.ALL)
    public Groups groups;

    public Boolean deleteStatus = false;

    public static Model.Finder<Long, GroupMembers> finder = new Model.Finder<Long, GroupMembers>(Long.class, GroupMembers.class);

    public Boolean selfCheck(){
        GroupMembers g = finder.where()
                .eq("deleteStatus",false)
                .eq("student.id",this.student.id)
                .eq("groups.component.id",this.groups.component.id)
                .setMaxRows(1)
                .findUnique();
        return ( g == null );
    }
    public Boolean checkeer(){
        GroupMembers g = finder.where().eq("deleteStatus",false).eq("student.id",this.student.id).eq("groups.id",this.groups.id).setMaxRows(1).findUnique();
        return ( g == null );
    }

    public static GroupMembers finderById(Long id) {
        return finder.ref(id);
    }
    public  static String getName( Long stId, Long cId){
            return finder.where()
                    .eq("student.id", stId)
                    .eq("groups.component.id", cId)
                    .eq("student.deleteStatus", false)
                    .setMaxRows(1)
                    .findUnique().groups.groupName+" [Same group]";
        }
    public  static Groups getGroup( Long stId, Long cId){
            return finder.where()
                    .eq("student.id", stId)
                    .eq("groups.component.id", cId)
                    .eq("student.deleteStatus", false)
                    .setMaxRows(1)
                    .findUnique().groups;
        }

}
