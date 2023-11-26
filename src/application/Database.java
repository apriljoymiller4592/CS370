package application;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Scanner;


import java.io.File;//new
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
	


