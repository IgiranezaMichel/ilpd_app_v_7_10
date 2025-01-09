package models.Utility;

import jdk.nashorn.internal.runtime.logging.Loggable;
import play.Logger;
import play.Play;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;
public class SendMailer
{

    private final static String host = Play.application().configuration().getString("mail.smtp.host");
    private final static String port = Play.application().configuration().getString("mail.smtp.port");
    private final static String username = Play.application().configuration().getString("mail.smtp.user");
    private final static String password = Play.application().configuration().getString("mail.smtp.pass");
    private final static String mailFrom = username;
    private final static String replyTo = "info@ilpd.ac.rw";


    private static void mailSender(String toAddress, String subject, String message, String title) throws UnsupportedEncodingException, MessagingException
    {
        // setting SMTP server properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.ssl.trust", host);
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.user", username);
        properties.put("mail.password", password);
        try {

            // creating a new session with an authenticator of username & password of the sender
            Authenticator auth = new Authenticator()
            {
                public PasswordAuthentication getPasswordAuthentication()
                {
                    return new PasswordAuthentication(username, password);
                }
            };
            Session session = Session.getInstance(properties, auth);

            // creating a new e-mail message (MimeMessage)
            Message msg = new MimeMessage(session);

            msg.setFrom(new InternetAddress(mailFrom, title));

            // creates message part
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(mainHtmlMessage+"<h4>"+title+"</h4>"+"<p>"+message+"</p>", "text/html");

            // creates multi-part
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            // add attachments

            // sets the multi-part as e-mail's content
            msg.setContent(multipart);

            //sending to multiple recipients
            if(!toAddress.equalsIgnoreCase("alice.benurugo@ilpd.ac.rw")) {
                msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress));
            }
            msg.setSubject(subject);
            msg.setSentDate(new Date());

            //Set the replyTo Email

            msg.setReplyTo(InternetAddress.parse(replyTo));

            // sends the e-mail
            Transport.send(msg);

            if(!toAddress.equalsIgnoreCase("alice.benurugo@ilpd.ac.rw")) {
                Logger.info("{} Email Sent to: {}",SendMailer.class.getName(), toAddress);
            }
        }catch (Exception e){
            Logger.error("{} Email not sent \nError: {}",SendMailer.class.getName(),e.getMessage());
        }
    }
    public static void sendMail(String toAddress, String subject, String msg)
    {
        try {
            if(!toAddress.equalsIgnoreCase("alice.benurugo@ilpd.ac.rw")) {
                mailSender(toAddress, subject, msg, "ILPD MIS Notification!!");
            }
        }
        catch (UnsupportedEncodingException | MessagingException e) {
            Logger.error(e.getMessage());
        }
    }

    public static void sendMail(String toAddress, String subject, String msg, String title)
    {
        try {
            if(!toAddress.equalsIgnoreCase("alice.benurugo@ilpd.ac.rw")) {
                mailSender(toAddress, subject, msg, title);
            }
        }
        catch (UnsupportedEncodingException | MessagingException e) {
            Logger.error(e.getMessage());
        }
    }
   private  static String mainHtmlMessage=" <head>\n" +
           "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
           "    <style>\n" +
           "        body {\n" +
           "            margin: 0;\n" +
           "            padding: 0;\n" +
           "            font-family: Arial, sans-serif;\n" +
           "        }\n" +
           "\n" +
           "        .header {\n" +
           "            border-bottom: 2px solid rgb(226, 0, 226);\n" +
           "            padding-bottom: 10px;\n" +
           "            padding-top: 5px;\n" +
           "            overflow: hidden;\n" +
           "        }\n" +
           "\n" +
           "        .header-left {\n" +
           "            float: left;\n" +
           "        }\n" +
           "\n" +
           "        .circles {\n" +
           "            display: inline-block;\n" +
           "            vertical-align: auto;\n" +
           "         }\n" +
           "\n" +
           "        .circle {\n" +
           "            width: 20px;\n" +
           "            height: 20px;\n" +
           "            border-radius: 50%;\n" +
           "            display: inline-block;\n" +
           "        }\n" +
           "\n" +
           "        .circle.grey {\n" +
           "            background-color: rgb(82, 101, 107);\n" +
           "        }\n" +
           "\n" +
           "        .circle.green, .circle.blue {\n" +
           "            background-color: rgb(226, 0, 226);\n" +
           "        }\n" +
           "\n" +
           "        .institute-name {\n" +
           "            color: rgb(82, 101, 107);\n" +
           "            border-left: 2px solid rgb(82, 101, 107);\n" +
           "            padding-left: 10px;\n" +
           "            font-weight: bold;\n" +
           "            font-size: 18px;\n" +
           "            line-height: 1.2;\n" +
           "            display: inline-block;\n" +
           "            margin-left: 10px;\n" +
           "        }\n" +
           "\n" +
           "        .logo {\n" +
           "            float: right;\n" +
           "            max-width: 50px;\n" +
           "            height: auto;\n" +
           "        }\n" +
           "\n" +
           "        @media screen and (max-width: 480px) {\n" +
           "            .circle {\n" +
           "                width: 10px;\n" +
           "                height: 10px;\n" +
           "            }\n" +
           "\n" +
           "            .institute-name {\n" +
           "                font-size: 14px;\n" +
           "            }\n" +
           "\n" +
           "            .logo {\n" +
           "                max-width: 40px;\n" +
           "            }\n" +
           "        }\n" +
           "    </style>\n" +
           "</head>\n" +
           "<body>\n" +
           "    <div class=\"header\">\n" +
           "        <div class=\"header-left\">\n" +
           "            <div class=\"circles\">\n" +
           "                <span class=\"circle grey\"></span>\n" +
           "                <span class=\"circle green\"></span>\n" +
           "                <span class=\"circle blue\"></span>\n" +
           "            </div>\n" +
           "            <div class=\"institute-name\">\n" +
           "                <div>Institute of Legal Practice</div>\n" +
           "                <div>and Development</div>\n" +
           "            </div>\n" +
           "        </div>\n" +
           "\n" +
           "    </div>\n" +
           "\n" +

           "</body>"+
           "\n";
    
}
