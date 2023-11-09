package application;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
import javax.mail.Authenticator;

//https://www.tutorialspoint.com/java/java_sending_email.htm
public class JavaMail{

	public static void PasswordResetRequest(String userName, String email, String appPassword) {
		 // Recipient's email ID needs to be mentioned.
	      String to = "artface.art.icle@gmail.com";

	      // Sender's email ID needs to be mentioned
	      String from = "artface.art.icle@gmail.com";

	      // Assuming you are sending email from localhost
	      String host = "smtp.gmail.com";

	      // Get system properties
	      Properties properties = System.getProperties();

	      // Setup mail server
	      properties.setProperty("mail.smtp.host", host);
	      properties.setProperty("mail.smtp.port", "587"); // Use the appropriate port
	      properties.setProperty("mail.smtp.auth", "true");
	      properties.setProperty("mail.smtp.starttls.enable", "true"); //enable STARTTLS

	      // Get the default Session object.
	      //Session session = Session.getDefaultInstance(properties);
	      Session session = Session.getInstance(properties, new Authenticator() {
	      @Override
	      protected PasswordAuthentication getPasswordAuthentication() {
	          return new PasswordAuthentication("kennethha47@gmail.com", appPassword);
	      }
	   		});
	   		
	      try {
	         // Create a default MimeMessage object.
	         MimeMessage message = new MimeMessage(session);

	         // Set From: header field of the header.
	         message.setFrom(new InternetAddress(from));

	         // Set To: header field of the header.
	         message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

	         // Set Subject: header field
	         message.setSubject("Password Reset Request!");

	         // send the actual message
	         message.setText("Hey I need a new password"+"\n"+" my User is: "+ userName + "\n" + "my email is: " + email);

	         // Send message
	         Transport.send(message);
	         System.out.println("Sent message successfully....");
	      } catch (MessagingException mex) {
	         mex.printStackTrace();
	      }
	}
   public static void main(String [] args) {    
	   PasswordResetRequest("UserName","testmail@gmail.com");
   }
}

