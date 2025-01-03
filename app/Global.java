import com.avaje.ebean.Ebean;
import models.Users;
import play.Application;
import play.GlobalSettings;
import play.libs.Yaml;

import java.util.List;
import java.util.Map;

public class Global extends GlobalSettings {
    public void onStart(Application app) {
        InitialData.insert(app);
    }
    static class InitialData {
        static void insert(Application app) {
            if (Ebean.find(Users.class).findRowCount() == 0) {
                Map<String, List<Object>> all = (Map<String, List<Object>>) Yaml.load("initial.yml");
                Ebean.save(all.get("users"));
            }
        }
    }
}
