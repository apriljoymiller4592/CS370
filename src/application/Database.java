package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;


import java.io.File;//new
public class Database {
	
	//returns true if database is created and formatted correctly
	public boolean checkArticleDB(Connection Connect)
	{
	    try {
	        Statement stmt = Connect.createStatement();

	        //define the expected structure of the tables
	        Map<String, String> expectedSaves = new HashMap<>();
	        expectedSaves.put("imageID", "int");
	        expectedSaves.put("UserName", "char(200)");
	        expectedSaves.put("image", "blob");

	        Map<String, String> expectedUser = new HashMap<>();
	        expectedUser.put("UserName", "char(200)");
	        expectedUser.put("Password", "char(200)");

	        //check if the correct tables exist
	        ResultSet rs = stmt.executeQuery("SHOW TABLES IN artfacedb");
	        while (rs.next()) {
	            String tableName = rs.getString(1);

	            //check the structure of the 'Saves' and 'User' tables
	            if (tableName.equals("Saves") || tableName.equals("User")) {
	                Statement stmt2 = Connect.createStatement();
	                ResultSet rs2 = stmt2.executeQuery("DESCRIBE " + tableName);

	                Map<String, String> actual = new HashMap<>();
	                while (rs2.next()) {
	                    actual.put(rs2.getString("Field"), rs2.getString("Type"));
	                }

	                //compare the actual structure with the expected structure
	                if (tableName.equals("Saves") && !expectedSaves.equals(actual)) {
	                    return false;
	                }
	                if (tableName.equals("User") && !expectedUser.equals(actual)) {
	                    return false;
	                }

	                rs2.close();
	                stmt2.close();
	            }
	        }
	        rs.close();
	        stmt.close();

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }

	    //if no issues were found, the database is set up correctly
	    return true;
	}
	
	public boolean addUser(Connection Connect, String user, String pass, String email)
	{
		
	}



	
	/*	//POSSIBLY DEPRICATED
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
	}
	*/
}


