package controllers;


import models.*;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;
public class ControlLecture extends Security.Authenticator {
    @Override
    public String getUsername(Context ctx) {
        String email = ctx.session().get("Instructor");//This retrieves the logged in user name(email).
        if ( email != null ){
            Users u = Application.Ins("Instructor");
            if( u != null ) {
                Lecture dix = u.activeL();
                if (dix != null) return email;
            }
        }else{
            email = ctx.session().get("Coordinator");
            if ( email != null ){
                return email;
            }
            email = ctx.session().get("mark_officer");
            if ( email != null ){
                return email;
            }
        }
        return null;
    }
    @Override
    public Result onUnauthorized(Context ctx) {
        return ok("error");
    }
}
