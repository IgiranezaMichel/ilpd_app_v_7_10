package models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class EvCategory extends Model {
    @Id
    public long id;

    public String content = "";

    @JsonBackReference
    @OneToMany(mappedBy = "category")
    public List<EvQuestion> questionList = new ArrayList<>();


    public static Model.Finder<Long, EvCategory> finder = new Model.Finder<>(Long.class, EvCategory.class);


}
