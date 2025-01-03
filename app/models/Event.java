package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Event extends Model {
    @Id
    public long id;

    public String eventName = "";
    public String eventDetail = "";

    public Date date;

    @JsonProperty
    public String print(){
        return eventName;
    }
}
