package controllers;
import models.*;
import models.Utility.SendMailer;
import play.data.Form;
import play.mvc.*;
import views.html.register.applicants.details;
public class TestController extends Controller{

public  static Result sendEmailDemo(){
        SendMailer sm=new SendMailer();
        sm.sendMail("im.igiranezamichel@gmail.com","Test Email","Hello MIS Testing message","MIS Testing Email");
        return  ok("Email sent successful");
        }
public static Result CreateFolder(String path){
        React.getRoot(path);
        return  ok(path+" Test path sucess");
        }
        }