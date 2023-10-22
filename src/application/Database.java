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
	private static final String dbClassname = "com.mysql.cj.jdbc.Driver";
	private static final String CONNECTION = "jdbc:mysql://127.0.0.1/ArtFacedb";
	public static void main(String[] args) throws ClassNotFoundException, SQLException{
		Scanner reader = new Scanner(System.in);//for user input
		System.out.println("Enter Sql Pasword: ");
		String password = reader.nextLine();//grabs input
				
			//connection
		System.out.println(dbClassname);
		Properties p = new Properties();
		p.put("user", "root");
		p.put("password",password);
		//reader.close();
		Connection c = DriverManager.getConnection(CONNECTION,p);
		Statement stmt = c.createStatement();
		System.out.println("Your in ");
		
		//-----Creating new  username and password------
		//vurnerable to sql injection
		System.out.println("Enter New UserName ");
		String newUserName = reader.nextLine();
		
		System.out.println("Enter New Password ");
		String newPassword = reader.nextLine();
		String sqlInput = "INSERT INTO USER VALUES('"+newUserName+"','"+newPassword+"')";
		int x = stmt.executeUpdate(sqlInput);
		if(x>0)//if x = 1 then it went through
			System.out.println("Successfull input");
		else
			System.out.println("Error username already exist or Error");
		//------------------------------------------------------------
		
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
		reader.close();
		c.close();
	}
	
}
