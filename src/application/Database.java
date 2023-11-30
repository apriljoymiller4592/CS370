package application;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
public class Database {
	public int count;
	public Boolean newUser(Connection c, String newUserName, String newPassword, String NewEmail) throws ClassNotFoundException, SQLException
	{	
		count = 0;
		System.out.println(newUserName + newPassword);
		Statement stmt = c.createStatement();
		String sqlSearch = "SELECT * FROM user WHERE userName = '"+ newUserName+"'";
		ResultSet x = stmt.executeQuery(sqlSearch);
		while(x.next())
		{
			count++;
		}
			
		if(count == 0)//if x = 1 then it went through
		{
			String sqlInput = "INSERT INTO USER VALUES('"+newUserName+"','"+newPassword+"', NULL,'"+NewEmail+"')";
			stmt.executeUpdate(sqlInput);
			System.out.println("Successfull input");
			return true;
		}
		else
		{
			System.out.println("Error username already exist or Error");
			return false;
		}
		
	}
	
	public Boolean checkUser(Connection c, String userName, String password) throws ClassNotFoundException, SQLException
	{
		count = 0;
		Statement stmt = c.createStatement();
		String sqlSearch = "SELECT * FROM user WHERE userName = '"+ userName+"' AND Password = '"+password+"'";
		ResultSet x = stmt.executeQuery(sqlSearch);
		while(x.next())
		{
			count++;
		}
		if(count == 1)
			return true;
		return false;
	}
	
	//this function allows the main function to pass through a generated image in a BufferedImage format for insertion
	//into our SQL server
	public boolean insertImage(Connection c, String userName, BufferedImage buffImg)
	{	
		try {
			//convert generated BufferedImage into a Blob for our SQL DB
			ByteArrayOutputStream imgOutStream = new ByteArrayOutputStream();
			ImageIO.write(buffImg, "png", imgOutStream);
			byte[] imgByteArray = imgOutStream.toByteArray();
			
			Statement stmt = c.createStatement();	//create statement
			String sqlExec = "INSERT INTO Saves VALUES ('" +userName +"','" +imgByteArray +"')";	//create command
			stmt.executeUpdate(sqlExec);	//execute command
		}
		
		catch(Exception ex)
		{
			System.out.println(ex);
			return false;
		}
		return true;
	}
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException{

	}
/*
		
		//--------------Login check------------------------------
		//vurnerable to sql injection
			System.out.println("Enter loginUserName ");
			String NameCheck = reader.nextLine();
				
			System.out.println("Enter login Password ");
			String PasswordCheck = reader.nextLine();
			String sqlInput2 = "SELECT * FROM USER WHERE UserName = '"+NameCheck+"' AND Password = '"+PasswordCheck+"'";
			ResultSet rs = stmt.executeQuery(sqlInput2);
			int counter = 0;
			while (rs.next()) {//should only be one
				counter++;
			}
			if(counter==1)//successful login
				System.out.println("Successfull login");
			else
				System.out.println("User not found");
		//----------------------------------------------------------------------
			
		//---------------------------Making directory----------------------------------
		File galleryFile = new File("CS370Project/Gallery");
		if(!galleryFile.exists()) {
			galleryFile.mkdirs();
		}
		
		//---------------------------insert Image---------------------------------
		System.out.println("Enter hash ");
		String hash = reader.nextLine();
		
		String sqlInput3 = "INSERT INTO SAVES(UserName,image) VALUES('"+newUserName+"','"+hash+"')";
		int y = stmt.executeUpdate(sqlInput3);
		if(y>0)//if x = 1 then it went through
			System.out.println("image successful");
		else
			System.out.println("Error with image");
		//-------------------------------------------------------------------------------
		reader.close();
		c.close();
		*/
	}
	


