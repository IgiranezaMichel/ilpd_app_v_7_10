package models;

import org.apache.commons.mail.*;

import play.*;
import play.mvc.*;
import java.util.*;


public class Mails{

    /*public static void message(String address, String message) {
        try {

            SimpleEmail email = new SimpleEmail();
            email.setFrom(address);
            email.addTo("marti1125@gmail.com");
            email.setSubject("Question");
            email.setMsg(message);
            Mail.send(email);
            email.send();
            System.out.println("Send message was successful");

        } catch (Exception e) {
            System.out.println("Error...");
            System.out.println(e);
        }

    }*/

    public static String message(String address, String message) {
        try {

            String verificationInfo = "";
            verificationInfo="Hello Verification is successfully; your USERNAME: "+ message +" , PASSWORD: "+ message;
            Email mail = new SimpleEmail();
            mail.setHostName("smtp.gmail.com");
            mail.setAuthentication("kwizeraemile125@gmail.com","budha12345");
            mail.setSmtpPort(465);
            mail.setSSLOnConnect(true);
            mail.setFrom( "nsenguella@gmail.com" );
            mail.setSubject( "login info" );
            mail.setMsg( verificationInfo );
            mail.addTo( address );
            String b = mail.send();
            return b+"donk";

        } catch (Exception e) {
            System.out.println("Error...");
            System.out.println(e);
            return e.toString();
        }

    }
}