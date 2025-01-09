package controllers;

import Helper.BaseModel;
import Helper.ClassFinder;
import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import models.*;
import play.Configuration;
import play.api.templates.Html;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Call;
import play.mvc.Controller;
import play.mvc.Http;

import javax.imageio.ImageIO;
import javax.mail.*;
import javax.mail.Message;
import javax.mail.internet.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import jxl.*;
import play.mvc.Result;

public class SuperBase extends Controller {
    private static String rootFolder = "public/";
    static String validExt = "pdf,jpg,png,gif,bmp,jpeg";
    public static String defaultSession = "logedUser";
    static String resetSession = "reset";
    public static final String sessionStudent = "student";
    public static final String dynamicKey = "editableStudent";
    public static final String sessionFinance = "finance";
    public static final String sessionManager = "manager";
    public static final String sessionRegister = "register";
    public static final String sessionHOD = "HOD";
    public static final String sessionLogistic = "logistic";
    public static final String sessionAdmin = "admin";
    public static final String jobApplicant = "job_app";
    public static final String sessionSuper = "SuperAdmin";
    public static final String sessionDean = "dean";
    public static final String sessionSpecLeader = "specLeader";
    public static final String sessionCFO = "CFO";
    public static final String sessionAssistant = "assistant";
    public static final String sessionExaminer = "examiner";
    public static final String sessionChiefExam = "chief_exam";
    public static final String sessionHrAssistant = "HR_assistant";
    public static final String sessionHeadOfUnit = "HeadOfUnit";
    public static final String sessionDAF = "DAF";
    public static final String sessionInvigilator = "invigilator";
    public static final String sessionLecture = "lecture";
    public static final String sessionLibrary = "library";
    public static final String sessionEnglish = "Modern_english";
    public static final String examConst = "EXAM";
    public static final String sessionPlanning = "Planning";
    public static final String sessionVRAC = "VRAC";
    static String[] statusList = {"New", "Deferred", "Rejected", "Student", "Accepted"};
    public static String[] statusListStudent = ClassFinder.statusListStudent;
    static String[] moduleStatusList = {"First sitting", "Retake", "Compensation"};
    static String currentActivity = "currentActivity";

    private static String username = "kwizeraemile125@gmail.com";
    private static String password = "budha12345";
    private static String mailFrom = "uok@uok.ac.rw";
    protected static Result sError = ok("System error");

    public static Boolean isAjax() {
        String[] string = request().headers().get("X-Requested-With");
        return string != null;
    }

    protected static JsonNode appliedStatusList(){
        return statusList(statusList);
    }



    protected static JsonNode moduleStatusList(){
        return statusList(moduleStatusList);
    }


    protected static JsonNode studentStatusList(){
        return statusList(statusListStudent);
    }

    protected static JsonNode statusList(String[] statusList) {
        List<JsonNode> nodeList = new ArrayList<>();

        for (String string : statusList){
            ObjectNode node = Json.newObject();
            node.put("id", string);
            node.put("print", string);
            nodeList.add(node);
        }

        return Json.toJson(nodeList);
    }

    static Result defaultError() {
        return defaultError("duplx");
    }

    static Result one = ok("1");
    static Result duplicate = ok("duplicated");

    static Result defaultError(String SMS) {
        return defaultError(SMS, null);
    }

    static Result defaultError(String SMS,Html html) {
        return ok("");
    }

    public static String urlString = "http://" + Configuration.root().getString("server.hostname");

    protected static String defaultFolder() {
        return rootFolder + "uploads/";
    }

    private static String defaultFolderExcel() {
        return rootFolder + "Excel/";
    }

    static String randomString() {
        UUID uuid = UUID.randomUUID();
        String randomUUIDString = uuid.toString();
        UUID uuid2 = UUID.randomUUID();
        String randomUUIDString2 = uuid2.toString();
        return randomUUIDString + randomUUIDString2;
    }

    static Result delete(String table,Long id){
        return delete(table,"id",id);
    }

    static Result delete(String table,String col,Long id){
        String sql = "DELETE FROM "+table+" WHERE "+col+"=:id";

        try {
            Ebean.createSqlUpdate(sql).setParameter("id",id).execute();
        }catch (Exception e){
            System.out.println(e.getMessage());
            return ok("");
        }

        return one;
    }


    static boolean isEntity(Class<?> aClass){
        return BaseModel.isEntity(aClass);
    }

    static boolean isDate(Class<?> aClass){
        return aClass == Date.class;
    }

    static String modalPackage(){
        return Campus.class.getPackage().getName();
    }


    static String staticOpen() {
        return "<div class=\"alert alert-danger alert-dismissible\">";
    }

    static String staticHeader(String title) {
        return "<h4><i class=\"icon fa fa-ban\"></i> " + title + "</h4>";
    }

    static String staticBody(String body) {
        return "<p> " + body + "</p>";
    }

    static String staticButton() {
        return "<button type=\"button\" class=\"close\" data-dismiss=\"alert\" aria-hidden=\"true\">×</button>";
    }

    static String staticClose() {
        return "</div>";
    }

    static String staticDiv(String title, String b) {
        StringBuilder builder = new StringBuilder();
        builder.append(staticOpen());
        builder.append(staticButton());
        builder.append(staticHeader(title));
        builder.append(staticBody(b));
        builder.append(staticClose());
        return builder.toString();
    }

    static JsonNode toDayJson(){
        ObjectNode node = Json.newObject();
        Date date = new Date();

        node.put("day",new SimpleDateFormat("EEEE").format(date));
        node.put("date",new SimpleDateFormat("dd").format(date));
        node.put("year",new SimpleDateFormat("YYYY").format(date));
        node.put("month",new SimpleDateFormat("MMMM").format(date));

        return node;
    }



    static Time getTime(String urlString){
        SimpleDateFormat dateFormat = new SimpleDateFormat("h:m");
        try{
            Date parse = dateFormat.parse(urlString);
            return new Time(parse.getTime());
        }catch (ParseException e){
            return null;
        }
    }


    protected static String excel() {
        try {
            File newFile = new File(getDefExcel(), randomString() + ".xls");
            Boolean bool = false;
            if (!newFile.exists())
                bool = newFile.createNewFile();
            if (!bool) return null;

            WritableWorkbook wrk1 = Workbook.createWorkbook(newFile);
            WritableSheet sheet = wrk1.createSheet("Marks1", 0);
            double d;
            try {
                d = 10000;
                d = d / 1024;

                // Only keep 2 digits: avoid rounding errors
                d = 1d * (int) (d * 100) / 100;

                sheet.addCell(new jxl.write.Label(0, 0, "PROTOCOL_ID"));
            } catch (NumberFormatException ignored) {
            }
            wrk1.setOutputFile(newFile);
            wrk1.write();
            wrk1.close();
            return newFile.getAbsolutePath();
        } catch (IOException | WriteException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String realPath(String fileName) {
        return routes.Assets.at("uploads/" + fileName).absoluteURL(request());
    }

    static Users objt(String session) {
        return Users.finderByMail(session(session));
    }

    public static Boolean isForm() {
        String[] string = request().headers().get("form-data");
        return string != null;
    }



    public static boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (Exception nfe) {
            return false;
        }
        return true;
    }

    static boolean isArrayNumeric(String[] str) {
        if ( str == null || str.length < 1 ) return  false;
            for (String string : str){
                boolean numeric = isNumeric(string);
                if( !numeric ){
                    return false;
                }
            }
            return true;
    }

    public static String getDef(String fileDestinationPath) {
        String path=defaultFolder();
        if(fileDestinationPath.length()!=0){
            path.concat(fileDestinationPath);
        }
        File testExist = new File(path);
        if (!testExist.exists()) {
            boolean mkDir = testExist.mkdir();
        }
        return defaultFolder();
    }

    static String getDefExcel() {
        File testExist = new File(defaultFolderExcel());
        if (!testExist.exists()) {
            boolean mkDir = testExist.mkdir();
        }
        return defaultFolderExcel();
    }

    public static File getFileExt(String fileDestinationPath,String fileName) {
        String filePath=defaultFolder().concat(fileDestinationPath);
        return new File(filePath.concat(fileName));
    }

    static Boolean hasImage(String fileDestinationPath,String fileName) {
        File file = getFileExt(fileDestinationPath,fileName);

        return hasImage(file);

    }


    static Boolean hasImage(File fileName) {
        try {
            ImageIO.read(fileName);


            String mimeType = Files.probeContentType(fileName.toPath());

            return mimeType != null && mimeType.split("/")[0].equals("image");
        } catch (Exception e) {
            return false;
        }
    }

    static String str(Object object) {
        return String.valueOf(object);
    }

    private static Boolean newFolder(String folderDestinationPath,String folderName) {
        try {
            File dir = new File(getDef(folderDestinationPath).concat(folderName));
            return dir.mkdir();
        } catch (Exception e) {
            return false;
        }
    }


    static Double getDouble(String val) {
        try {
            return Double.valueOf(val);
        } catch (Exception error) {
            return 0.0;
        }
    }

    static int getInt(String val) {
        try {
            return Integer.valueOf(val);
        } catch (Exception error) {
            return 0;
        }
    }

    static Long getLong(String val) {
        try {
            return Long.valueOf(val);
        } catch (Exception error) {
            return null;
        }
    }




    static String uploadImage(String imageDestinationPath,String dflt) {
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart picture = body.getFile("photo");
        if (picture != null) {
            // It's an image (only BMP, GIF, JPG and PNG are recognized)
            try {
                ImageIO.read(picture.getFile()).toString();
                String fileName = picture.getFilename();
                String contentType = picture.getContentType();
                File file = picture.getFile();
                String text = (new Date().getTime()) + fileName;
                file.renameTo(new File(getDef(imageDestinationPath).concat(imageDestinationPath), text));

                return text;
            } catch (IOException e) {
                return dflt;
            }
        } else {
            flash("error", "Missing file");
            return dflt;
        }
    }

    static String singleFile(Http.MultipartFormData.FilePart file,String fileDestinationPath, String dft) {
        if (file != null) {
            String fileName = file.getFilename();
            String cType = file.getContentType();
            File newFile = file.getFile();
            String text = (new Date().getTime()) + randomString() + fileName;
            final boolean b = newFile.renameTo(new File(getDef(fileDestinationPath), text));
            return text;
        } else {
            return dft;
        }
    }

    static String uploadFile(String dft, String fName,String fileDestinationPath) {
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart picture = body.getFile(fName);
        return singleFile(picture,fileDestinationPath, dft);
    }

    static JsonNode parseJson(String json) {
        try {
            return Json.parse(json);
        } catch (Exception e) {
            return null;
        }
    }

    static Date parseDate(String string){
        return BaseModel.parseDef(string);
    }



    static File uploadRealFile(String fName, String fileDestinationPath) {
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart file = body.getFile(fName);
        if (file != null) {
            String fileName = file.getFilename();
            String cType = file.getContentType();
            File newFile = file.getFile();
            String text = (new Date().getTime()) + fileName;
            File laterFile = new File(fileDestinationPath, text);
            final boolean b = newFile.renameTo(laterFile);
            return laterFile;
        } else {
            return null;
        }
    }

    static File uploadExcelFile(String file,String destinationFolderPath) {
        return uploadRealFile(file, getDefExcel().concat(destinationFolderPath));
    }

    static File uploadDefaultFile(String file,String destinationPath) {
        return uploadRealFile(file, getDef(destinationPath));
    }


    static double formDouble(String s){
        DynamicForm f = Form.form().bindFromRequest();

        try{
            return Double.valueOf(f.field(s).value());
        }catch (Exception e){
            return 0.0;
        }
    }


    static int start(int s) {
        return (s - 1) * Vld.limit;
    }



    public static Boolean inArray(String[] hayStack, String needle) {
        Boolean bld = false;
        for (String i : hayStack) {
            if (needle.equals(i)) {
                bld = true;
                break;
            }
        }
        return bld;
    }

    static String getExt(String name) {
        return name.substring(name.lastIndexOf('.') + 1);
    }

    static String[] getRegNo(String name) {
        int i = name.lastIndexOf("/") + 1;
        int zero = 0;
        String string = name.substring(zero, i);
        String substring = name.substring(i);

        return new String[]{string,substring};
    }

    public static JsonNode headerInfo(){
        ObjectNode node = Json.newObject();
        ProfileInfo info = ProfileInfo.unique();
        node.put("image",realPath(info.profileLogo));
        node.put("email",info.email);
        node.put("address",info.address);
        node.put("phone",info.phone);
        node.put("website",info.website);
        node.put("toDay",dateNode(new Date()));
        node.put("date",toDay());
        return node;
    }

    static String toDay(){
        return day(new Date());
    }

    static String toDay(String p){
        return day(new Date(),p);
    }

    static String toDayCard(){
        return toDay("MMM-yyyy");
    }

    static String toDayNext(){
        return toDayNext(1);
    }

    static String toDayNext3years(){
        return toDayNext(3);
    }

    static String toDayNext2years(){
        return toDayNext(2);
    }

    static String toDayNext(int i){
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.YEAR,i);
        Date time = c.getTime();
        return day(time,"MMM-yyyy");
    }

    static String day(Date date){
        return BaseModel.format(date);
    }

    static String day(Date date,String p){
        return BaseModel.format(date,p);
    }


    static String patternLecture = "yyyy-MM-dd";

    static JsonNode attendanceColumns(){
        List<JsonNode> nodeList = new ArrayList<>();
        String s = BaseModel.defaultFormat(new Date());
        nodeList.add(nodeEl("image","Image").put("isImage",true));
        nodeList.add(nodeEl("regNo","Reg no"));
        nodeList.add(nodeEl("print","Student names"));
        nodeList.add(nodeEl("attendancePercentage","percent").put("isN",true));
        nodeList.add(nodeEl("attendanceNumber","number"));
        nodeList.add(nodeEl("todayDate","today").put("isCheck",s));
        nodeList.add(nodeEl("History","History").put("view",true));

        return Json.toJson(nodeList);
    }

    static JsonNode examColumns(){
        List<JsonNode> nodeList = new ArrayList<>();
        String att = "attendancePercentage";
        String s = BaseModel.defaultFormat(new Date());
        nodeList.add(nodeEl("image","Image").put("isImage",true));
        nodeList.add(nodeEl("regNo","Reg no"));
        nodeList.add(nodeEl("print","Student names"));
        nodeList.add(nodeEl(att,"percent").put("isN",true));
        nodeList.add(nodeEl("balance","Finance").put("elBalance","forceExam"));
        nodeList.add(nodeEl("examPaid","Eligible").put("elStatus",att));
        nodeList.add(nodeEl("examPaid","Add_to")
                .put("isExamCheck",att)
                .put("route","examRoute"));



        return Json.toJson(nodeList);
    }

    public static JsonNode jsonDate(List<Date> dateList){
        List<JsonNode> nodeList = new ArrayList<>();
        for (Date date : dateList){
            nodeList.add(dateNode(date));
        }
        return Json.toJson(nodeList);
    }

    public static JsonNode dateNode(Date date){

        ObjectNode node = Json.newObject();
        node.put("date",BaseModel.defaultFormat(date));
        node.put("newDate",BaseModel.format(date));
        node.put("day",BaseModel.day(date));
        node.put("dayNumber",BaseModel.dayN(date));
        node.put("dayYear",BaseModel.dayOfYear(date));
        node.put("month",BaseModel.month(date));
        node.put("time",date.getTime());

        return node;
    }

    private static ObjectNode nodeEl(String el){
        return nodeEl(el,el);
    }

    static ObjectNode nodeEl(String el,String title){
        ObjectNode node = Json.newObject();
        node.put("value",el);
        node.put("title",title);
        return node;
    }

    static long getSize(String fileDestinationPath,String fileName) {
        File file = getFileExt(fileDestinationPath,fileName);
        if (file.exists()) {
            return file.length();
        }
        return 0;
    }



}
