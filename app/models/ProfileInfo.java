package models;
import com.avaje.ebean.annotation.ConcurrencyMode;
import com.avaje.ebean.annotation.EntityConcurrencyMode;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by SISI on 9/19/2017.
 */
@Entity
@EntityConcurrencyMode(ConcurrencyMode.NONE)
public class ProfileInfo extends Model {
    @Id
    public long id;
    public String profileLogo = "";
    public String profileName = "";
    public String profileAcronym = "";
    public String email = "";
    public String admissionEmail1 = "";
    public String admissionEmail2 = "";
    public String aboutInfo = "";
    public String website = "";
    public String registrarSignature = "";
    public String registrarName = "";
    public String rector = "";
    public String stamp = "";
    public String phone = "";
    public String address = "";
    public String defaultText = "";
    public String cancelText = "";
    public int minAge = 0;
    public int attendancePercentage = 0;
    public int deliberation = 0;
    public int experience = 0;
    public String deferText = "";
    public float failMax = 0;
    public float passMin = 0;
    public float passMax = 0;
    public float creditMin = 0;
    public float creditMax = 0;
    public float distinctionMin = 0;
    public float distinctionMax = 0;
    public Double meritMin = 0.0;
    public Double meritMax = 0.0;
    public Double satisfactoryMin = 0.0;
    public Double satisfactoryMax = 0.0;
    public Double failMin = 0.0;
    public int scoreOne = 0;
    public int scoreTwo = 0;
    public int scoreThree = 0;
    public Double aMin = 0.0;
    public Double aMax = 0.0;
    public Double bPlusMin = 0.0;
    public Double bPlusMax = 0.0;
    public Double bMin = 0.0;
    public Double bMax = 0.0;
    public Double cPlusMin = 0.0;
    public Double cPlusMax = 0.0;
    public Double cMin = 0.0;
    public Double cMax = 0.0;
    public Double dMin = 0.0;
    public Double dMax = 0.0;
    public Double eMin = 0.0;
    public Double eMax = 0.0;
    public Double fMin = 0.0;
    public Double fMax = 0.0;
    public Double maximumResit = 0.0;
    public Double gradePointA = 0.0;
    public Double gradePointBplus = 0.0;
    public Double gradePointB = 0.0;
    public Double gradePointCplus = 0.0;
    public Double gradePointC = 0.0;
    public Double gradePointD = 0.0;
    public Double gradePointE = 0.0;
    public Double gradePointF = 0.0;

    public static Finder<Long, ProfileInfo> finder = new Finder<Long, ProfileInfo>(Long.class, ProfileInfo.class);

    public static ProfileInfo finderById(Long id){
        return finder.ref(id);
    }
    public static ProfileInfo unique(){
        Long id = Long.parseLong("1");
        return finder.where()
                .eq("id", id)
                .setMaxRows(1)
                .findUnique();
    }

}
