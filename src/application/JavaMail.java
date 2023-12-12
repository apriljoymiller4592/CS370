package application;

import javax.mail.PasswordAuthentication;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
import javax.mail.Session;

//import javax.activation.*;
import javax.mail.Authenticator;

//template for sending a email through java
//https://www.tutorialspoint.com/java/java_sending_email.htm

public class JavaMail {

    public static void PasswordResetRequest(String userName, String userEmail, String newPassword) {
        // Recipient's email ID (user's email)
        String to = userEmail;

        // Sender's email and password (for SMTP authentication)
        String senderEmail = "artface.art.icle@gmail.com";
        String senderPassword = "ewix lkoq rkkp ehbs";

        // Assuming you are sending email from localhost
        String host = "smtp.gmail.com";

        // Setup mail server properties
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.smtp.port", "587");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");

        // Get the Session object with authentication
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            // Create a default MimeMessage object
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(senderEmail));

            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            message.setSubject("Password Reset Request");

            String emailContent = "Hi " + userName + ",\n\nYour password reset request has been received.\n" +
                                  "Your new password is: " + newPassword + "\n\nRegards,\nArtFace Team";
            message.setText(emailContent);

            Transport.send(message);
            System.out.println("Password reset email sent successfully...");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        PasswordResetRequest("april", "userEmail@example.com", "ewix lkoq rkkp ehbs");
    }
}
