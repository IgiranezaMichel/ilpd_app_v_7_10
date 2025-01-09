package controllers;

import models.*;
import play.mvc.Controller;
import play.mvc.Http;
import  play.Logger;
import javax.imageio.ImageIO;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.util.*;

import static controllers.Application.Ins;

public class React extends Controller
{
    public static String RootFolder = "public/";
    private static String defaultFolder = "public/uploads/";
    static String defaultSession = "logedUser";
    public static String qualityInsurance = "quality_insurance";

    static String randomString()
    {
        UUID uuid = UUID.randomUUID();
        String randomUUIDString = uuid.toString();
        return randomUUIDString;
    }
    public static List<Training> getTrainings() {
        List<Training> trainings = new ArrayList<>();
        if (session().containsKey("registrar")) {
            trainings = Training.withoutCle();
        } else if (session().containsKey("DTR/Coordinator")) {
            trainings = Training.withCle();
        }
        return trainings;
    }
    public static String getRoot(String fileDestinationPath)
    {
        String path=defaultFolder;
        if(!fileDestinationPath.equals(""))
            path=path.concat(fileDestinationPath);
        File testExist = new File(path);
        if (!testExist.exists())
        {
            testExist.mkdirs();
        }
        Logger.info("{} path before {}",path,React.class.getName());
        path=path.split("public/")[1];
        Logger.info("{} path after {}",path,React.class.getName());
        return path;
    }

    protected static boolean isInsurance(){
        return session().containsKey(qualityInsurance);
    }

    static Double getDouble(String val)
    {
        try
        {
            return Double.valueOf(val);
        }
        catch (Exception error)
        {
            return 0.0;
        }
    }

    static Long getLong(String val)
    {
        try
        {
            return Long.valueOf(val);
        }
        catch (Exception error)
        {
            return 0L;
        }
    }

    protected static Boolean isAjax()
    {
        String[] string = request().headers().get("X-Requested-With");
        return string != null;
    }

    protected static Boolean eval(String scripts) throws ScriptException
    {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        Object result = engine.eval(scripts);
        return true;
    }

    static String uploadFile(String dft, String fName,String destinationPath)
    {
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart picture = body.getFile(fName);
        String uploadPath=getRoot(destinationPath).concat(dft);
        if (picture != null)
        {
            String fileName = picture.getFilename();
            String cType = picture.getContentType();
            File file = picture.getFile();
            String text = (new Date().getTime()) + fileName;
            File uploadedFile=new File(getRoot(destinationPath));
           try {
               if(uploadedFile.exists())
                   file.renameTo(new File(uploadedFile, text));
               else{
                   Logger.info("{} uploaded file ",uploadPath,React.class.getName());
                   return uploadPath;
               }
           }catch(Exception e){
               e.printStackTrace();
               return null;
           }
           }
        else
        {
            return dft;
        }
        return  null;
    }
    static List<UserRole> CheckUser(Users user)
    {
        Users check = Users.checkUser(user.email, user.password);
        if (check != null)
        {
            session(defaultSession, check.email);
            return check.RolesForUser();
        }
        else
        {
            return null;
        }
    }

    static String uploadImage(String imageDestinationPath,String dflt)
    {
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart picture = body.getFile("photo");
        if (picture != null)
        {
            // It's an image (only BMP, GIF, JPG and PNG are recognized)
            try
            {
                ImageIO.read(picture.getFile()).toString();
                String fileName = picture.getFilename();
                String contentType = picture.getContentType();
                File file = picture.getFile();
                String text = (new Date().getTime()) + fileName;
                file.renameTo(new File(getRoot(imageDestinationPath), text));

                return getRoot(imageDestinationPath).concat(text);
            }
            catch (Exception e)
            {
                return dflt;
            }
        }
        else
        {
            flash("error", "Missing file");
            return dflt;
        }
    }

    static String uploadProfile(String dflt,String profileDestinationPath)
    {
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart picture = body.getFile("profile");
        if (picture != null)
        {
            // It's an image (only BMP, GIF, JPG and PNG are recognized)
            try
            {
                ImageIO.read(picture.getFile()).toString();
                String fileName = picture.getFilename();
                String contentType = picture.getContentType();
                File file = picture.getFile();
                String text = (new Date().getTime()) + fileName;
                file.renameTo(new File(getRoot(profileDestinationPath), text));

                return getRoot(profileDestinationPath).concat(text);
            }
            catch (Exception e)
            {
                return dflt;
            }
        }
        else
        {
            flash("error", "Missing file");
            return dflt;
        }
    }

    static Boolean newFolder(String folderName,String destinationPath)
    {
        File dir = new File(getRoot(destinationPath).concat(folderName));
        return dir.mkdir();
    }

    public static boolean isCleManager(){
        return session().containsKey("DTR/Coordinator");
    }

    protected static boolean isInstructor(){
        return session("Instructor") != null && Ins("Instructor") != null;
    }



    static Date toDayNext(int i){
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.YEAR,i);
        return c.getTime();
    }

    static boolean isCleOrCoordinator(){
        return isCleManager() || isCoordinator();
    }

    protected static boolean isRegistrar(){
        return session().containsKey("registrar");
    }
    protected static boolean isPlanning(){
        return session().containsKey("Planning");
    }

    protected static boolean isCoordinator()
    {
        return session().containsKey("Coordinator");
    }

    protected static boolean isMarkOfficer()
    {
        return session().containsKey("mark_officer");
    }

    static boolean isRegistrarOrCle(){
        return isRegistrar() || isCleManager();
    }

    public static boolean checkReg(){
        return isRegistrarOrCle();
    }

    static boolean isNumeric(String s){
        try {
            Double.parseDouble(s);
            return true;
        }catch (Exception ignored){
            return false;
        }
    }

    static Student getStudent(){
        return userStudent().me();
    }

    public static Applicant applicant(){
        Users user = Users.finderByMail(session().get("student"));
        return user.amApplicant();
    }

    public static boolean isApplicant(){
         return session().containsKey("student");
    }

    static Users userStudent(){
        return Users.finderByMail(session("student"));
    }
}
