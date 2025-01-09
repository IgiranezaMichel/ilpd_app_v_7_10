package services;
import models.Applied;
import play.Logger;
import java.util.ArrayList;
import java.util.List;
public class ApplicantServices {
    public static List<Applied> filterByTraining(long id, String status) {
        boolean b = status.equalsIgnoreCase("Paid");
        
        List<Applied> appliedList = new ArrayList<>();
        for (Applied applied : appliedByTraining(id)) {
            if (applied.applicant.isRequiredPaidDone() == b) {
                appliedList.add(applied);
            }
        }

        Logger.info("Total applicants:"+appliedList.size());
        return appliedList;
    }
    public static List<Applied> appliedByTraining(long id) {
        return Applied.finder
                .where().eq("training.id", id).eq("applicationStatus","accepted").findList();
    }
}
