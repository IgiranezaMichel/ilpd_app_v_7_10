package models;

import play.db.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;
import java.util.List;

@Entity
public class ChatMessage extends Model {
    @Id
    public long id;

    @ManyToOne(cascade = CascadeType.ALL)
    public Users sendTo;

    @ManyToOne(cascade = CascadeType.ALL)
    public Users sendFrom;

    public String content = "";

    public String type = "";

    public Date date;

    public Boolean messageRead = false;

    public Boolean deleteStatus = false;

    public static Model.Finder<Long, ChatMessage> find = new Finder<Long, ChatMessage>(Long.class, ChatMessage.class);




}
