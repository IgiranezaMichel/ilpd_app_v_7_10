package controllers;
import com.avaje.ebean.*;
import models.*;
import models.Utility.SendMailer;
import models.Utility.Template;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import java.io.File;
import java.text.*;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.regex.Pattern;

import static java.time.temporal.ChronoUnit.DAYS;

public class Counts extends React {
    public static int getMalePercentage(String gender) {
        int total = Student.finder.findRowCount();
        String sql = "SELECT COUNT(student.id) as total FROM student INNER JOIN applicant ON student.applicant_id=applicant.id WHERE applicant.gender=\"" + gender + "\" AND student.delete_status=0";
        SqlQuery sqlQuery = Ebean.createSqlQuery(sql);
        SqlRow row = sqlQuery.findUnique();
        int totalMale = row.getInteger("total");
        float percentage = (float) (totalMale * 100) / total;
        return Math.round(percentage);
    }
    public static int getMalePercentage(boolean gender) {
        int total = Student.number(gender);
        int totalMale = Student.numberMale(gender);
        float percentage = (float) (totalMale * 100) / total;
        return Math.round(percentage);
    }
    public static int getFemalePercentage(boolean gender) {
        int total = Student.number(gender);
        int totalMale = Student.numberFemale(gender);
        float percentage = (float) (totalMale * 100) / total;
        return Math.round(percentage);
    }
    public static int getTotalModules(boolean b) {
        return Module.number(b);
    }
    public static int getTotalComponents(boolean b) {
        return Component.number(b);
    }
    public static int myAnnouncements(String userType) {
        return AnounceRole.finder
                .setDistinct(true)
                .where()
                .eq("deleteStatus", false)
                .eq("role.sessionName", userType)
                .findRowCount();
    }
    public static int getTotalSystemUsers() {
        return Users.finder.where()
                .eq("delete_status", false)
                .not(Expr.eq("role", "student"))
                .findRowCount();
    }
    public static int getTotalRequestStudentTraining(Long sId, String reType) {
        return ReSitReTakeRequest.finder.where()
                .eq("student.id", sId)
                .eq("requestType", reType)
                .eq("deleteStatus", false)
                .findRowCount();
    }
    public static int getTotalBranches() {
        return Campus.finder.where()
                .eq("delete_status", false)
                .findRowCount();
    }
    public static int getTrainingPerIntake(long inId) {
        return Training.find.where()
                .eq("iMode.deleteStatus", false)
                .eq("iMode.intake.deleteStatus", false)
                .eq("iMode.intake.id", inId)
                .findRowCount();
    }
    public static int getOpenTrainings() {
        return Training.find.where()
                .eq("iMode.deleteStatus", false)
                .eq("deleteStatus", false)
                .eq("hasGraduated", false)
                .eq("iMode.intake.deleteStatus", false)
                .findRowCount();
    }
    public static int getTrainingPerIntakeAll() {
        return Training.find.where()
                .eq("iMode.deleteStatus", false)
                .eq("deleteStatus", false)
                .eq("iMode.intake.deleteStatus", false)
                .eq("iMode.campusProgram.deleteStatus", false)
                .eq("iMode.campusProgram.program.deleteStatus", false)
                .eq("iMode.campusProgram.program.cle", true)
                .findRowCount();
    }
    public static int getStudentsIntake(long inId) {
        return Student.finder.where()
                .eq("training.iMode.deleteStatus", false)
                .eq("training.iMode.intake.deleteStatus", false)
                .eq("training.iMode.intake.id", inId)
                .findRowCount();
    }
    public static int getStudentsTraining(long tId) {
        return Student.finder.where()
                .eq("deleteStatus", false)
                .eq("training.deleteStatus", false)
                .eq("training.id", tId)
                .findRowCount();
    }
    public static int getStudentsTrainingGraduants(long tId) {
        return Student.finder.where()
                .eq("deleteStatus", false)
                .eq("training.deleteStatus", false)
                .eq("training.id", tId)
                .not(Expr.eq("status", "RE-SIT"))
                .not(Expr.eq("status", "RETAKE-MODULES"))
                .not(Expr.eq("status", "active"))
                .not(Expr.eq("status", "Graduated"))
                .not(Expr.eq("status", "suspended"))
                .findRowCount();
    }
    public static int getFinalDeliberated() {
        return Training.find.where()
                .eq("deleteStatus", false)
                .eq("hasGraduatedFinal", true)
                .findRowCount();
    }
    public static int getStudentsTrainingFinal(long tId) {
        return Student.finder.where()
                .eq("deleteStatus", false)
                .eq("training.deleteStatus", false)
                .eq("training.id", tId)
                .eq("status", "PASS")
                .findRowCount();
    }
    public static String firstWord(String s) {
        String[] arrayCount = s.split(Pattern.quote(" "));
        String s1 = arrayCount[0];
        return s1;
    }
    public static String secondWord(String s) {
        String[] arrayCount = s.split(Pattern.quote(" "));
        String s1 = arrayCount[1];
        return s1;
    }
    public static String thirdWord(String s) {
        String[] arrayCount = s.split(Pattern.quote(" "));
        String s1 = arrayCount[2];
        return s1;
    }
    public static String fourthWord(String s) {
        String[] arrayCount = s.split(Pattern.quote(" "));
        String s1 = arrayCount[3];
        return s1;
    }
    public static int getStudentsTrainingReSit(long tId) {
        return Student.finder.where()
                .eq("deleteStatus", false)
                .eq("training.deleteStatus", false)
                .eq("training.id", tId)
                .eq("status", "RE-SIT")
                .findRowCount();
    }
    public static int getStudentsIntakeAll() {
        return Student.finder.where()
                .eq("training.iMode.deleteStatus", false)
                .eq("training.deleteStatus", false)
                .eq("training.iMode.intake.deleteStatus", false)
                .eq("training.iMode.campusProgram.deleteStatus", false)
                .eq("training.iMode.campusProgram.program.deleteStatus", false)
                .eq("training.iMode.campusProgram.program.cle", true)
                .findRowCount();
    }
    public static int getStudentsIntakeMale(long inId) {
        return Student.finder.where()
                .eq("applicant.gender", "male")
                .eq("training.iMode.deleteStatus", false)
                .eq("training.iMode.intake.deleteStatus", false)
                .eq("training.iMode.intake.id", inId)
                .findRowCount();
    }
    public static int getStudentsIntakeMaleAll() {
        return Student.finder.where()
                .eq("applicant.gender", "male")
                .eq("training.iMode.deleteStatus", false)
                .eq("training.iMode.intake.deleteStatus", false)
                .eq("training.iMode.intake.deleteStatus", false)
                .eq("training.iMode.campusProgram.deleteStatus", false)
                .eq("training.iMode.campusProgram.program.deleteStatus", false)
                .eq("training.iMode.campusProgram.program.cle", true)
                .findRowCount();
    }
    public static int getStudentsIntakeFemale(long inId) {
        return Student.finder.where()
                .eq("applicant.gender", "female")
                .eq("training.iMode.deleteStatus", false)
                .eq("training.iMode.intake.deleteStatus", false)
                .eq("training.iMode.intake.id", inId)
                .findRowCount();
    }
    public static int getStudentsIntakeFemaleAll() {
        return Student.finder.where()
                .eq("applicant.gender", "female")
                .eq("training.iMode.deleteStatus", false)
                .eq("training.iMode.intake.deleteStatus", false)
                .eq("training.iMode.intake.deleteStatus", false)
                .eq("training.iMode.campusProgram.deleteStatus", false)
                .eq("training.iMode.campusProgram.program.deleteStatus", false)
                .eq("training.iMode.campusProgram.program.cle", true)
                .findRowCount();
    }
    public static int getStudentsIntakeInternational(long inId) {
        return Student.finder.where()
                .not(Expr.eq("applicant.country", "rwanda"))
                .eq("training.iMode.deleteStatus", false)
                .eq("training.iMode.intake.deleteStatus", false)
                .eq("training.iMode.intake.id", inId)
                .findRowCount();
    }
    public static int getStudentsIntakeInternationalAll() {
        return Student.finder.where()
                .not(Expr.eq("applicant.country", "rwanda"))
                .eq("training.iMode.deleteStatus", false)
                .eq("training.iMode.intake.deleteStatus", false)
                .eq("training.iMode.intake.deleteStatus", false)
                .eq("training.iMode.campusProgram.deleteStatus", false)
                .eq("training.iMode.campusProgram.program.deleteStatus", false)
                .eq("training.iMode.campusProgram.program.cle", true)
                .findRowCount();
    }
    public static int countComponentsPerModule(long modId, long sId) {
        return SubMark.find.setDistinct(true)
                .where("deleteStatus='false' and student.deleteStatus='false' and component.deleteStatus='false' and component.module.deleteStatus='false' and component.module.id = '"+modId+"' and student.id = '"+sId+"' ").findRowCount();
    }
    public static int getStudentsIntakeLocal(long inId) {
        return Student.finder.where()
                .eq("applicant.country", "rwanda")
                .eq("training.iMode.deleteStatus", false)
                .eq("training.iMode.intake.deleteStatus", false)
                .eq("training.iMode.intake.id", inId)
                .findRowCount();
    }
    public static int countMyCLE(long uId) {
        return Applicant.finder.where()
                .eq("user.id", uId)
                .eq("student.training.iMode.campusProgram.program.cle", true)
                .eq("student.training.iMode.intake.isClosed", false)
                .findRowCount();
    }
    public static int countMyDLP(long uId) {
        return Applicant.finder.where()
                .eq("user.id", uId)
                .eq("student.training.iMode.campusProgram.program.cle", false)
                .eq("student.training.iMode.intake.isClosed", false)
                .eq("student.training.isClosedA", false)
                .findRowCount();
    }
    public static int countSameTraining(long uId, Long tId) {
        return Applied.finder.where()
                .eq("applicant.user.id", uId)
                .eq("training.id", tId)
                .eq("training.iMode.intake.isClosed", false)
                .eq("training.isClosedA", false)
                .findRowCount();
    }
    public static int getStudentsIntakeLocalAll() {
        return Student.finder.where()
                .eq("applicant.country", "rwanda")
                .eq("training.iMode.deleteStatus", false)
                .eq("training.iMode.intake.deleteStatus", false)
                .eq("training.iMode.intake.deleteStatus", false)
                .eq("training.iMode.campusProgram.deleteStatus", false)
                .eq("training.iMode.campusProgram.program.deleteStatus", false)
                .eq("training.iMode.campusProgram.program.cle", true)
                .findRowCount();
    }
    public static int getTotalInstructors() {
        return Lecture.find.where().eq("delete_status", false).findRowCount();
    }
    public static int getTotalRooms() {
        return Room.find.where().eq("delete_status", false).findRowCount();
    }
    public static int getTotalPrograms() {
        return Program.finder.where().eq("deleteStatus", false).findRowCount();
    }
    //given assignments
    public static List<Assignment> getRecentAssignments(long id) {
        return Assignment.finder.where().eq("lecture.id", id).eq("deleteStatus", false).setMaxRows(5).findList();
    }
    public static List<SqlRow> getGivenComponents(long id) {
        String s = "SELECT c.code,c.comp_name as component,c.credits, p.program_acronym  FROM  schedule s INNER JOIN component c ON s.component_id=c.id inner join module m on c.module_id = m.id inner join program p on m.program_id = p.id INNER JOIN lecture l ON s.lecture_id=l.id WHERE s.lecture_id=:id AND s.component_id=c.id GROUP BY c.id";
        return Ebean.createSqlQuery(s)
                .setParameter("id", id)
                .findList();
    }
    //assignments
    public static int getTotalAssignments(long id) {
        return Assignment.finder.where().eq("lecture.id", id).eq("deleteStatus", false).findRowCount();
    }
    public static List<Schedule> getScheduleList(long id) {
        Calendar now = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String toDay = simpleDateFormat.format(now.getTime());
        return Schedule.find.where().eq("lecture.id", id).eq("date", toDay).eq("deleteStatus", false).findList();
    }
    public static int countComponents(long id) {
        String s = "SELECT l.f_name,c.comp_name  FROM  schedule s INNER JOIN component c ON s.component_id=c.id INNER JOIN lecture l ON s.lecture_id=l.id WHERE s.lecture_id=:id AND s.component_id=c.id GROUP BY c.id";
        List<SqlRow> sqlRows = Ebean.createSqlQuery(s).setParameter("id", id).findList();
        return sqlRows == null ? 0 : sqlRows.size();
    }
    public static List<SqlRow> scheduleListByTraining(long trainingId, long lectureId) {
        String s = "SELECT s.id as sId, concat(DATE(s.date),' : ',DAYNAME(s.date)) as day,concat(l.l_name,' ',l.f_name) as teacher,concat(s.start_hour,' - ',s.end_hour) as hour,c.comp_name as component,c.id as cId FROM schedule s INNER JOIN lecture l ON s.lecture_id=l.id INNER JOIN training t ON s.training_id=t.id INNER JOIN component c ON s.component_id=c.id WHERE l.id=:lId AND t.id=:tId";
        return Ebean.createSqlQuery(s).setParameter("lId", lectureId).setParameter("tId", trainingId).findList();
    }
    public static String totalRestauration() {
        String s = "SELECT count(id) as total,sum(p.restaurant_amount) as total_fees FROM payment p WHERE p.delete_status=0 AND  p.status=\"approved\" AND  year(p.date)=year(now())";
        SqlRow row = Ebean.createSqlQuery(s).findUnique();
        if (row.getDouble("total_fees") == null) {
            return formatAmount(0.0);
        }
        return formatAmount(row.getDouble("total_fees"));
    }
    public static String totalSchoolFees() {
        String s = "SELECT sum(p.fees_amount) as total_fees FROM payment p WHERE p.delete_status=0 AND  p.status=\"approved\"  AND  year(p.date)=year(now())";
        SqlRow row = Ebean.createSqlQuery(s).findUnique();
        if (row.getDouble("total_fees") == null) {
            return formatAmount(0.0);
        }
        return formatAmount(row.getDouble("total_fees"));
    }
    public static String totalAccomodation() {
        String s = "SELECT count(id) as total,sum(p.accomodation_amount) as total_fees FROM payment p WHERE p.delete_status=0 AND  p.status=\"approved\"  AND  year(p.date)=year(now())";
        SqlRow row = Ebean.createSqlQuery(s).findUnique();
        if (row.getDouble("total_fees") == null) {
            return formatAmount(0.0);
        }
        return formatAmount(row.getDouble("total_fees"));
    }
    public static String totalStudentPaidAll() {
        String s = "SELECT DISTINCT count(s.id) AS total FROM account a INNER JOIN applicant s ON a.applicant_id = s.id INNER JOIN student st ON st.applicant_id = s.id INNER JOIN payment p ON p.account_id = a.id WHERE p.remain = 0.0";
        return Ebean.createSqlQuery(s).findUnique()
                .getInteger("total").toString();
    }
    public static String totalStudentPaidFew() {
        String s = "SELECT DISTINCT count(st.id) as total FROM applicant s INNER JOIN student st ON st.applicant_id = s.id INNER JOIN account a ON a.applicant_id=s.id INNER JOIN payment p ON p.account_id = a.id WHERE p.remain > 0.0";

        return Ebean.createSqlQuery(s).findUnique().getInteger("total").toString();
    }
    public static int getTotalMale() {
        String s = "SELECT count(s.id) as total FROM student s INNER JOIN applicant a ON s.applicant_id=a.id WHERE year(now())=year(a.date) AND a.gender='male'";
        return Ebean.createSqlQuery(s).findUnique().getInteger("total");
    }
    public static int getTotalFemale() {
        String s = "SELECT count(s.id) as total FROM student s INNER JOIN applicant a ON s.applicant_id=a.id WHERE year(now())=year(a.date) AND a.gender='female'";
        return Ebean.createSqlQuery(s).findUnique().getInteger("total");
    }
    public static String totalStudentPaidNothing() {
        String s = "SELECT DISTINCT count(s.id) as total FROM applicant s INNER JOIN student st ON st.applicant_id = s.id INNER JOIN account a ON a.applicant_id=s.id LEFT JOIN payment p ON p.account_id = a.id WHERE p.id IS NULL ";
        return Ebean.createSqlQuery(s).findUnique().getInteger("total").toString();
    }
    public static String formatAmount(double amount) {
        Locale locale = new Locale("en_US");
        NumberFormat numberFormatter = NumberFormat.getNumberInstance(locale);
        return numberFormatter.format(amount);
    }
    public static List<Schedule> getWeekTimeTable(long id) {
        DateTime now = new DateTime(new Date()).withTimeAtStartOfDay();
        DateTime seven = now.plusDays(7);
        return Schedule.find.where().eq("deleteStatus", false).eq("lecture.id", id).between("date", now, seven).findList();
    }
    public static int getTotalAssignments() {
        String s = "SELECT count(a.id) as total FROM  assignment a WHERE  a.delete_status=0 AND year(a.end_date)=year(now())";
        return Ebean.createSqlQuery(s).findUnique().getInteger("total");
    }
    public static List<Schedule> getScheduleList() {
        Calendar now = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String toDay = simpleDateFormat.format(now.getTime());
        return Schedule.find.where().eq("date", toDay).eq("deleteStatus", false).findList();
    }
    public static List<Assignment> recentAssignments() {
        return Assignment.finder.where().eq("deleteStatus", false).setMaxRows(5).findList();
    }
    public static List<Component> recentComponents() {
        return Component.finder.where().eq("deleteStatus", false).setMaxRows(5).findList();
    }
    public static List<Users> recentInstructors() {
        return Users.finder.where().eq("deleteStatus", false).eq("role", "Instructor").setMaxRows(5).findList();
    }
    public static boolean dateIsBefore(Date date) {
        return date.before(new Date());
    }
    public static int daysLeft(Date date) {
        if (date.before(new Date())) {
            return 0;
        } else {
            long startTime = new Date().getTime();
            long endTime = date.getTime();
            return Days.daysBetween(new LocalDate(startTime), new LocalDate(endTime)).getDays();
        }
    }
    public static int daysBetween(Date date1, Date date2) {
        if (date2.before(date1)) {
            return 0;
        }
        return Days.daysBetween(new LocalDate(date1.getTime()), new LocalDate(date2.getTime())).getDays() +1;
    }
    public static Date dateAddMonth(Date date, int month) {
        DateTime dateTime = new DateTime(date);
        dateTime = dateTime.plusMonths(month);
        return dateTime.toDate();
    }
    public static Date dateAddDay(String date1)  throws ParseException{
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date dates = (Date)formatter.parse(date1);
        DateTime dateTime = new DateTime(dates);
        dateTime = dateTime.plusDays(1);
        return dateTime.toDate();
    }
    public static String dateToString(Date date1) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = dateFormat.format(date1);
        return strDate;
    }
    public static boolean isHaflyPaid(Applicant applicant) {
        Applied applied = applicant.applied;
        if (applied == null) {
            return false;
        }
        Double amount = applied.training.minPayment;
        return amount != null && applicant.hasAccount() && applicant.account.amountPaid >= amount;
    }
    public static List<SqlRow> getListOfStudentInLibraryWithProblems() {
        String s = "SELECT count(s.id) as count,concat(s.first_name,' ',s.family_name) as name,s.reg_no,format(sum(i.book_cost), 2) as total FROM ildplibrary i INNER JOIN student s ON i.student_id=s.id WHERE i.clear=FALSE GROUP BY s.id ";
        return Ebean.createSqlQuery(s).findList();
    }
    public static boolean isAccepted() {
        if( isApplicant() ) {
            Users users = userStudent();
            if (users == null) {
                return false;
            }
            Applicant applicant = applicant();
            return applicant != null && applicant.student != null || applicant != null && applicant.applied != null && applicant.applied.applicationStatus.equalsIgnoreCase("accepted");
        }
        return false;
    }
    public static boolean isResultAllowed() {
        Applicant applicant = applicant();
        if(applicant.student.training.isComponent){
            return true;
        }else {
            return false;
        }
    }
    public static boolean isShowViewKarks() {
        Applicant applicant = applicant();
        if(applicant.student.viewMarks){
            return true;
        }else {
            return false;
        }
    }
    public static boolean isShowTranscript() {
        Applicant applicant = applicant();
        if(applicant.student.training.transcript){
            return true;
        }else {
            return false;
        }
    }
    public static boolean isHasGraduated() {
        Applicant applicant = applicant();
        if(applicant.student.training.hasGraduated){
            return true;
        }else {
            return false;
        }
    }
    public static boolean isPassed() {
        Applicant applicant = applicant();
        if(applicant.student.status.equalsIgnoreCase("DISTINCTION") || applicant.student.status.equalsIgnoreCase("PASS") || applicant.student.status.equalsIgnoreCase("CREDIT")){
            return true;
        }else {
            return false;
        }
    }
    public static boolean isCle() {
        if( isApplicant() ) {
            Users users = userStudent();
            if (users == null) {
                return false;
            }
            Applicant applicant = applicant();

            return  applicant != null && applicant.applied != null && applicant.applied.training.iMode.campusProgram.program.cle;
        }
        return  false;
    }
    public static boolean isCles(Training training) {
        if (training == null) {
            return false;
        }

        return  training != null && training.iMode.campusProgram.program.cle;

    }
    public static String getRegistraionFees() {
        String s = "SELECT format(sum(p.application_fees),2 ) as registrationFees FROM  payment p";
        return Ebean.createSqlQuery(s).findUnique().getString("registrationFees");
    }
    public static Date dateConverter(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    // getting number of times a specific component has been stied attended
    public static float getNberOfTimesComponentHasAttended(long compId, long studentId) {
        String s = "SELECT COUNT(a.id) as count FROM attendance a INNER JOIN component c on a.component_id = c.id inner join student s on a.student_id = s.id WHERE a.attended = true and c.id=:cId AND a.student_id =:sId";
        return Ebean.createSqlQuery(s)
                .setParameter("cId", compId)
                .setParameter("sId", studentId)
                .findUnique()
                .getFloat("count");
    }
    public static int getNberOfTimesModuleHasAttended(long moduleId, long trainingId, long studentId) {
        String s = "SELECT COUNT(a.id) as count FROM attendance a INNER JOIN schedule s ON a.schedule_id=s.id INNER JOIN component c ON s.component_id = c.id inner join module m on c.module_id = m.id INNER JOIN training t  ON s.training_id=t.id WHERE a.attended = true and m.id=:mId AND t.id=:tId AND a.student_id =:sId";
        return Ebean.createSqlQuery(s)
                .setParameter("mId", moduleId)
                .setParameter("tId", trainingId)
                .setParameter("sId", studentId)
                .findUnique().getInteger("count");
    }
    private static int getNberOfTimesModuleHasAttended2(long moduleId, long trainingId, long studentId) {
        String s = "SELECT COUNT(a.id) as count FROM attendance a INNER JOIN schedule s ON a.schedule_id=s.id INNER JOIN component c ON s.component_id = c.id inner join module m on c.module_id = m.id INNER JOIN training t  ON s.training_id=t.id WHERE m.id=:mId AND t.id=:tId AND a.student_id =:sId";
        return Ebean.createSqlQuery(s)
                .setParameter("mId", moduleId)
                .setParameter("tId", trainingId)
                .setParameter("sId", studentId)
                .findUnique().getInteger("count");
    }
    // getting number of times a specific student has attended
    public static float getNberOfTimesAStudentAttended(long compId, long studentId) {
        String s = "SELECT COUNT(a.id) as count FROM attendance a INNER JOIN component c on a.component_id = c.id inner join student s on a.student_id = s.id  WHERE a.attendedp = true and c.id=:cId AND a.student_id =:sId";
        return Ebean.createSqlQuery(s)
                .setParameter("cId", compId)
                .setParameter("sId", studentId)
                .findUnique()
                .getFloat("count");
    }
    // getting number of times a specific student has attended
    public static int getNberOfTimesAStudentAttended2(long compId, long studentId) {
        String s = "SELECT COUNT(a.id) as count FROM attendance a INNER JOIN component c on a.component_id = c.id inner join student s on a.student_id = s.id  WHERE c.id=:cId AND a.student_id =:sId";
        return Ebean.createSqlQuery(s)
                .setParameter("cId", compId)
                .setParameter("sId", studentId)
                .findUnique()
                .getInteger("count");
    }
    private static int getNberOfTimesAStudentAttendedMod(long modId, long trainId, long studentId) {
        String s = "SELECT COUNT(a.id) as count FROM attendance a INNER JOIN schedule s ON a.schedule_id=s.id INNER JOIN component c ON s.component_id = c.id inner join module m on c.module_id = m.id INNER JOIN training t  ON s.training_id=t.id WHERE a.attendedp = true and m.id=:mId AND t.id=:tId AND a.student_id =:sId";
        return Ebean.createSqlQuery(s)
                .setParameter("mId", modId)
                .setParameter("tId", trainId)
                .setParameter("sId", studentId)
                .findUnique().getInteger("count");
    }
    public static float getPercentageScore(Student student, Training training, Component component) {
        float totalAM = getNberOfTimesComponentHasAttended(component.id, student.id);
        float totalPM = getNberOfTimesAStudentAttended(component.id, student.id);
        float am =  (totalAM * 40)/100;
        float pm =  (totalPM * 60)/100;
        int totalDays = getNberOfTimesAStudentAttended2(component.id, student.id);
        return Math.round((float)( ( am + pm )/ totalDays) * 100);        //((totalAM * 40)/100 + totalPM)/2) * 100/getNberOfTimesAStudentAttended2(component.id, student.id));
    }
    public static float getPercentageScorePerModule(Student student, Training training, Module module) {
        int totalAM = getNberOfTimesModuleHasAttended(module.id, training.id, student.id);
        int totalPM = getNberOfTimesAStudentAttendedMod(module.id, training.id, student.id);
        return Math.round((float) (((totalAM + totalPM)/2) * 100) / getNberOfTimesModuleHasAttended2(module.id, training.id, student.id));
    }
    public static int getTotalModules(Training training) {
        String s = "SELECT m.id FROM schedule s INNER JOIN training t ON s.training_id=t.id INNER JOIN component c ON s.component_id=c.id INNER JOIN module m ON c.module_id=m.id WHERE s.delete_status=0 AND c.delete_status=0 AND m.delete_status=0 AND t.delete_status=0 AND t.id=:id GROUP BY m.id";
        return Ebean.createSqlQuery(s).setParameter("id", training.id).findList().size();
    }
    public static int getTotalComponents(Training training) {
        String s = "SELECT c.comp_name FROM schedule s INNER JOIN training t ON s.training_id=t.id INNER JOIN component c ON s.component_id=c.id  WHERE s.delete_status=0 AND c.delete_status=0 AND t.delete_status=0 AND t.id=:id GROUP BY c.id";
        return Ebean.createSqlQuery(s).setParameter("id", training.id).findList().size();
    }
    public static List<SqlRow> getModulesList(Training training) {
        String s = "SELECT distinct m.id as identity, m.module_name as name FROM schedule s INNER JOIN training t ON s.training_id=t.id INNER JOIN component c ON s.component_id=c.id INNER JOIN module m ON c.module_id=m.id WHERE s.delete_status=0 AND c.delete_status=0 AND s.delete_status = 0 and m.delete_status=0 AND t.delete_status=0 AND t.id=:id GROUP BY m.id";
        return Ebean.createSqlQuery(s).setParameter("id", training.id).findList();
    }
    public static List<SqlRow> getComponentsList(Training training) {
        String s = "SELECT c.comp_name as name FROM schedule s INNER JOIN training t ON s.training_id=t.id INNER JOIN component c ON s.component_id=c.id  WHERE s.delete_status=0 AND c.delete_status=0 AND t.delete_status=0 AND t.id=:id GROUP BY c.id";
        return Ebean.createSqlQuery(s).setParameter("id", training.id).findList();
    }
    public static boolean hasGroup(long studentId, long componentId) {
        GroupMembers groupMembers = GroupMembers.finder.where().eq("deleteStatus", false).eq("groups.component.id", componentId).eq("student.id", studentId).setMaxRows(1).findUnique();
        return groupMembers != null;
    }
    public static List<SqlRow> stockMovementsLocations(Long id) {
        String s = "SELECT l.id as id, b.name as blockName,b.acronym as blockAcc, l.acronym as locationAcc,l.name as locationName FROM stock_movement m INNER JOIN location l ON m.location_id=l.id INNER JOIN block b ON l.block_id=b.id INNER JOIN item i ON m.item_id=i.id WHERE i.id=:id  GROUP BY l.id";
        return Ebean.createSqlQuery(s).setParameter("id", id).findList();
    }
    public static String selectTraining(String s, Long id) {
        if (s.equalsIgnoreCase(String.valueOf(id))) {
            return "selected";
        }
        return "";
    }
    public static double currentStockValue() {
        return Ebean.createSqlQuery(
                "SELECT IFNULL(SUM(s.unit_price*i.balance_qty),0.0) as total  FROM supplied s INNER JOIN item i on s.item_id=i.id WHERE i.balance_qty >0;")
                .findUnique()
                .getDouble("total");
    }
    public static double thisMonthStockOutItems() {
        return Ebean.createSqlQuery("" +
                "SELECT IFNULL(sum(m.confirmed_qty),0.0)  as total FROM stock_movement m WHERE m.i_type='stock' AND m.status=TRUE AND month(now())=month(m.date)")
                .findUnique()
                .getDouble("total");
    }
    public static double thisMonthStockInItems() {
        return Ebean.createSqlQuery("" +
                "SELECT IFNULL(sum(m.supplied_qty),0)  as total FROM supplied m WHERE  month(now())=month(m.date)")
                .findUnique()
                .getDouble("total");
    }
    public void sendEmail(String role, int count) {
        CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setContextClassLoader(Counts.this.getClass().getClassLoader());

                List<UserRole> usersRoles = UserRole.finder.where()
                        .eq("deleteStatus", false)
                        .eq("role.sessionName", role)
                        .findList();
                for (UserRole userRole : usersRoles) {
                    SendMailer.sendMail(
                            userRole.user.email,
                            "Pending request",
                            "Dear! You have " + count + " pending requests to resolve.",
                            "ILPD stock info");
                }
            }
        }, Executors.newSingleThreadExecutor()).exceptionally(new Function<Throwable, Void>() {
            @Override
            public Void apply(Throwable exc) {
                exc.printStackTrace();
                return null;
            }
        });
    }
    public void sendAppliedEmail(Applied app, String subject, String message) {
        CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setContextClassLoader(Counts.this.getClass().getClassLoader());
                SendMailer.sendMail(app.applicant.user.email, subject, Template.registrationConfirmation(app.applicant.user, message));
            }
        }, Executors.newSingleThreadExecutor()).exceptionally(new Function<Throwable, Void>() {
            @Override
            public Void apply(Throwable exc) {
                exc.printStackTrace();
                return null;
            }
        });
    }
    public void sendUserEmail(Users user, String subject, String message) {
        CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setContextClassLoader(Counts.this.getClass().getClassLoader());
                SendMailer.sendMail(user.email, subject, Template.registrationConfirmation(user, message));
            }
        }, Executors.newSingleThreadExecutor()).exceptionally(new Function<Throwable, Void>() {
            @Override
            public Void apply(Throwable exc) {
                exc.printStackTrace();
                return null;
            }
        });
    }
    public void sendUserEmail2(String email, String subject, String message) {
        CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setContextClassLoader(Counts.this.getClass().getClassLoader());
                Users user = Users.finderByMail(email);
                SendMailer.sendMail(email, subject, Template.registrationConfirmation(user, message));
            }
        }, Executors.newSingleThreadExecutor()).exceptionally(new Function<Throwable, Void>() {
            @Override
            public Void apply(Throwable exc) {
                exc.printStackTrace();
                return null;
            }
        });
    }
    public static String leftPadWithZeroes(String stringToPadLeft, int length) {
        StringBuilder paddedString = new StringBuilder(stringToPadLeft);
        while (paddedString.length() < length) {
            paddedString.insert(0, "0");
        }
        return paddedString.toString();
    }
    public static String selectThis(long id1, long id2) {
        return id1 == id2 ? "selected" : "";
    }
    public static void resetStudentAccount(Applied applied) {
        if (applied.applicant.account != null) {
            Account account = Account.finderById(applied.applicant.account.id);
            if (account != null) {
                Account account1 = new Account();
                account1.amount = applied.applicant.getAmountToPayByTraining() * -1;
                account1.applicant = account.applicant;
                account1.createdAt = account.createdAt;
                account1.update(account.id);
            }
        }
    }
    public static String selected(long id1, long id2) {
        return id1 == id2 ? "selected" : "";
    }
    public static String selected(String s1, String s2) {
        return s1.equals(s2) ? "selected" : "";
    }
    public static String profileImage(String name) {
        String path = "public/uploads/";
        File file = new File(path + name);
        if (file.exists() && !name.trim().equalsIgnoreCase("")) {
            return "uploads/" + name;
        }
        return "images/boys.jpg";
    }
    public static double    getStudentCatMarks(long studentId, long mId) {
        return AssignmentResult.componetResult(studentId, mId);
    }
    public static double    getStudentCatMarksComponent(long studentId, long cId) {
        return AssignmentResult.componetResultComponent(studentId, cId);
    }



    public static double    getAssignmentMax(long studentId, long mId) {
        String s = "SELECT DISTINCT IFNULL(SUM(a.max)/COUNT(c.id),0.0) as total FROM assignment_result ar INNER JOIN assignment a ON ar.assignment_id=a.id INNER JOIN student s ON ar.student_id=s.id INNER JOIN component c ON a.component_id = c.id INNER JOIN module m ON c.module_id = m.id WHERE s.id=:sid AND m.id=:mid AND a.types =\"assignment\" and a.id IS NOT NULL and ar.delete_status = false and a.delete_status = false";
        double total = Ebean.createSqlQuery(s)
                .setParameter("sid", studentId)
                .setParameter("mid", mId)
                .findUnique()
                .getDouble("total");
        return total;
    }
    public static double    getAssignmentMaxs(long studentId, long mId) {
        String s = "SELECT DISTINCT IFNULL(SUM((ar.result * m.re_max)/a.max)/COUNT(c.id),0.0) as total FROM assignment_result ar INNER JOIN assignment a ON ar.assignment_id=a.id INNER JOIN student s ON ar.student_id=s.id INNER JOIN component c ON a.component_id = c.id INNER JOIN module m ON c.module_id = m.id WHERE s.id=:sid AND m.id=:mid AND a.types =\"resitResearch\" and a.id IS NOT NULL and ar.delete_status = false and a.delete_status = false";
        double total = Ebean.createSqlQuery(s)
                .setParameter("sid", studentId)
                .setParameter("mid", mId)
                .findUnique()
                .getDouble("total");
        return total;
    }

    public static double    getStudentCatMarksModule(long studentId, long mId) {
        double results = 0.0;
        double tot1 = 0.0;
        double ass = 0.0;
        int inc = 1;
        Student student = Student.finderById(studentId);
        List<SubMark> subMarkList = SubMark.allPerModule(mId, studentId);

        for(SubMark s : subMarkList) {
            tot1 = tot1 + Counts.getStudentCatMarksComponent(studentId, s.component.id);
            ass = AssignmentResult.numberAssignmentDone(student.id, s.component.module.id, student.training.id);
            inc = inc + 1;
        }
        if(ass >= 0.0) {
            results = tot1 / (inc - 1);
        }
       return results;
    }
    public static double    getStudentCatMarksModuleBefore(long studentId, long cId) {
        return AssignmentResult.moduleResultBefore(studentId, cId);
    }
    public static double    getStudentResearchMarks(long studentId, long cId) {
        return AssignmentResult.componentResearchResult(studentId, cId);
    }
    public static double    getStudentResearchMarksResearch(long studentId, long cId) {
        return AssignmentResult.componentResearchResultResearch(studentId, cId);
    }
    public static double    getStudentResearchMarksResearchModule(long studentId, long mId) {
        return AssignmentResult.componentResearchResultResearchModule(studentId, mId);
    }
    public static double    getStudentResearchMarksModule(long studentId, long mId) {
        String s = "SELECT DISTINCT IFNULL(SUM((ar.result * m.re_max)/a.max)/COUNT(c.id),0.0) as total FROM assignment_result ar INNER JOIN assignment a ON ar.assignment_id=a.id INNER JOIN student s ON ar.student_id=s.id INNER JOIN component c ON a.component_id = c.id INNER JOIN module m ON c.module_id = m.id WHERE s.id=:sid AND m.id=:mid AND a.types =\"research\" and a.id IS NOT NULL and ar.delete_status = false and a.delete_status = false";
        double total = Ebean.createSqlQuery(s)
                .setParameter("sid", studentId)
                .setParameter("mid", mId)
                .findUnique()
                .getDouble("total");
        return total;
    }
    public static double    getStudentResearchMarksModuleBefore(long studentId, long mId) {
        String s = "SELECT DISTINCT IFNULL(SUM((ar.result * m.re_max)/a.max)/COUNT(c.id),0.0) as total FROM assignment_result ar INNER JOIN assignment a ON ar.assignment_id=a.id INNER JOIN student s ON ar.student_id=s.id INNER JOIN component c ON a.component_id = c.id INNER JOIN module m ON c.module_id = m.id WHERE s.id=:sid AND m.id=:mid and ar.is_resit_result = false AND a.types =\"research\"and a.id IS NOT NULL and ar.delete_status = false and a.delete_status = false";
        double total = Ebean.createSqlQuery(s)
                .setParameter("sid", studentId)
                .setParameter("mid", mId)
                .findUnique()
                .getDouble("total");
        return total;
    }
    public static double    getStudentResearchMarksModuleResit(long studentId, long mId) {
        String s = "SELECT DISTINCT IFNULL(SUM((ar.result * m.re_max)/a.max)/COUNT(c.id),0.0) as total FROM assignment_result ar INNER JOIN assignment a ON ar.assignment_id=a.id INNER JOIN student s ON ar.student_id=s.id INNER JOIN component c ON a.component_id = c.id INNER JOIN module m ON c.module_id = m.id WHERE s.id=:sid AND m.id=:mid AND a.types =\"resitResearch\" and a.id IS NOT NULL and ar.delete_status = false and a.delete_status = false";
        double total = Ebean.createSqlQuery(s)
                .setParameter("sid", studentId)
                .setParameter("mid", mId)
                .findUnique()
                .getDouble("total");
        return total;
    }
    public static float getExamMaxTrainingComponent(Long tId, Long cId) {
        String s = "SELECT IFNULL(SUM(c.exam_max),0.0) as total FROM component_max c INNER JOIN component comp ON c.component_id=comp.id inner join training t on c.training_id = t.id WHERE t.id =:tid and comp.id=:cid ";
        return Ebean.createSqlQuery(s)
                .setParameter("tid", tId)
                .setParameter("cid", cId)
                .findUnique()
                .getInteger("total");
    }
    public static float getExamMaxTrainingModule(Long tId, Long mId) {
        String s = "SELECT IFNULL(SUM(c.exam_max),0.0) as total FROM component_max c INNER JOIN component comp ON c.component_id=comp.id inner join module m on comp.module_id = m.id inner join training t on c.training_id = t.id WHERE t.id =:tid and m.id=:mid and m.delete_status = false";
        return Ebean.createSqlQuery(s)
                .setParameter("tid", tId)
                .setParameter("mid", mId)
                .findUnique()
                .getInteger("total");
    }
    public static float getExamMaxTrainingModuleResit(Long tId, Long mId) {
        String s = "SELECT IFNULL(SUM(c.resit_max),0.0) as total FROM component_max c INNER JOIN component comp ON c.component_id=comp.id inner join module m on comp.module_id = m.id inner join training t on c.training_id = t.id WHERE t.id =:tid and m.id=:mid and m.delete_status = false";
        return Ebean.createSqlQuery(s)
                .setParameter("tid", tId)
                .setParameter("mid", mId)
                .findUnique()
                .getInteger("total");
    }
    public static float getExamMaxTrainingModuleStudent(Long tId) {
        String s = "SELECT IFNULL(SUM(c.exam_max),0.0) as total FROM component_max c INNER JOIN component comp ON c.component_id=comp.id inner join module m on comp.module_id = m.id inner join training t on c.training_id = t.id WHERE t.id =:tid and m.delete_status = false";
        return Ebean.createSqlQuery(s)
                .setParameter("tid", tId)
                .findUnique()
                .getInteger("total");
    }
    public static boolean isSubmitted(long assId, long stId) {
        int count = AssignmentResult.find.where()
                .eq("student.id", stId)
                .eq("assignment.id", assId)
                .eq("deleteStatus", false)
                .findRowCount();
        return count > 0;
    }
    public static DateTime getTomorrow(){
        DateTime futureDate = DateTime.now();
        DateTime futureAdd = futureDate.plusDays(1);
        return futureAdd;
    }
    public static int getTotalApplicants() {
        return Applicant.finder.where().eq("delete_status", false).findRowCount();
    }
    public static int totalPendingAppeals(){
        return InternshipAppeal.find.where()
                .eq("deleteStatus",false)
                .eq("status","pending")
                .findRowCount();
    }
    public static int totalProcessedAppeals(){
        return InternshipAppeal.find.where()
                .eq("deleteStatus",false)
                .not(Expr.eq("status", "pending"))
                .findRowCount();
    }
    public static int assignmentPerGroup(Long gId) {
        return Assignment.finder.where()
                .eq("grouped", true)
                .eq("group.id", gId)
                .findRowCount();
    }
    public static double totalAttended(long sId) {
        return Ebean.createSqlQuery(
                "SELECT IFNULL(COUNT(a.id),0.0) as total  FROM attendance a INNER JOIN student s on a.student_id = s.id WHERE s.id =:id and a.attended=1")
                .setParameter("id", sId)
                .findUnique()
                .getDouble("total");
    }
    public static boolean isViewMarks() {
        Applicant applicant = applicant();
        if(applicant.student.training.isComponent){
            return true;
        }else {
            return false;
        }
    }
    public static boolean isHasGraduatedFinal() {
        Applicant applicant = applicant();
        if(applicant.student.training.hasGraduatedFinal){
            return true;
        }else {
            return false;
        }
    }
    public static double totalAbscent(long sId) {
        return Ebean.createSqlQuery(
                "SELECT IFNULL(COUNT(a.id),0.0) as total  FROM attendance a INNER JOIN student s on a.student_id = s.id WHERE s.id =:id and a.attended=0")
                .setParameter("id", sId)
                .findUnique()
                .getDouble("total");
    }
    public static double attendancePerStudent(long sId) {
        double dividend = totalAbscent(sId);
        if(dividend == 0){
            dividend = 1 ;
        }
        return (((totalAttended(sId) * 100)/ (totalAttended(sId)+dividend)));
    }

}
