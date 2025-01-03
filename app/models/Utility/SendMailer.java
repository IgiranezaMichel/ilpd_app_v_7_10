package models.Utility;

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
        messageBodyPart.setContent(message, "text/html");

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
            Logger.info("Email Sent to:" + toAddress);
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

}
