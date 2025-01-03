package models;

import play.db.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Created by SISI on 10/10/2017.
 */
@Entity
public class Verification extends Model {
    @Id
    public long id;

    @ManyToOne
    public Users verifier;

    @ManyToOne
    public Attachment attachment;

    public Boolean status = false;

    public Boolean shelfed = false;

    public String userComment = "";


    public Boolean deleteStatus = false;


    public static Finder<Long, Verification> finder = new Finder<Long, Verification>(Long.class, Verification.class);

    public static Verification finderById(Long id){
        return finder.ref(id);
    }

    public static Verification finderByAtt(Long id){
        return finder.where().eq("deleteStatus",false).eq("attachment.id",id).eq("attachment.deleteStatus",false).setMaxRows(1).findUnique();
    }

}
