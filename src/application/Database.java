package application;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner;
import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;

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
			
			String sqlExec = "INSERT INTO Saves (UserName, image) VALUES (?,?);"; //create execution statement
			PreparedStatement stmt = c.prepareStatement(sqlExec);	//prepare statement
			stmt.setString (1, userName);	//set username
			stmt.setBytes(2, imgByteArray); //set image
			stmt.executeUpdate();	//execute
			
			return true;
		}
		
		catch(Exception ex)
		{
			System.out.println(ex);
			return false;
		}
	}
	
	//this function adds the users profile picture to the database
	public boolean insertProfilePic(Connection c, String userName, Image profilePic)
	{
		try {
			//convert javafx image into a bufferedimage to use w same logic as insertImage()
			BufferedImage buffImg = convertToBufferedImage(profilePic);
			//convert generated BufferedImage into a Blob for our SQL DB
			ByteArrayOutputStream imgOutStream = new ByteArrayOutputStream();
			ImageIO.write(buffImg, "png", imgOutStream);
			byte[] imgByteArray = imgOutStream.toByteArray();
			
			String sqlExec = "UPDATE User SET ProfilePic = ? WHERE UserName = ?";	//create exec statement
			PreparedStatement stmt = c.prepareStatement(sqlExec);	//prepare statement
			stmt.setBytes(1, imgByteArray);	//add pic
			stmt.setString(2, userName);	//for user
			stmt.executeUpdate();	//execute statement
			
			return true;
		}
		catch (Exception ex)
		{
			System.out.println(ex);
			return false;
		}
	}
	
	  //this function returns a given users profile picture
	  public Image getProfilePic(Connection c, String userName)
	  {
		  try {
			  String sqlExec = "SELECT ProfilePic FROM User WHERE UserName = ?";
			  PreparedStatement stmt = c.prepareStatement(sqlExec);	//prepare statement
			  stmt.setString(1, userName);
			  ResultSet resultSet = stmt.executeQuery();
			  
			  if (resultSet.next()) 
			  {
				  byte[] profileData = resultSet.getBytes("ProfilePic");
				  if (profileData != null)
				  {
					  Image profilePic = convertToImage(profileData);
					  return profilePic;
				  }
			  }
		  }
		  catch (Exception ex)
		  {
			  System.out.println(ex);
		  }
		  return null;
	  }
	
	  //image to buffered image converter
	  public static BufferedImage convertToBufferedImage(Image javafxImage) {
	      int width = (int) javafxImage.getWidth();
	      int height = (int) javafxImage.getHeight();

	      BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	      PixelReader pixelReader = javafxImage.getPixelReader();

	      for (int x = 0; x < width; x++) {
	          for (int y = 0; y < height; y++) {
	              // Extract ARGB components from JavaFX Image
	              int argb = pixelReader.getArgb(x, y);

	              // Write ARGB value to BufferedImage
	              bufferedImage.setRGB(x, y, argb);
	          }
	      }

	      return bufferedImage;
	  }
	  
	  //byte array to Image for SQL
	  public Image convertToImage(byte[] bytes) {
		  try {
			  ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
			  BufferedImage bufferedImage = ImageIO.read(inputStream);
			  
			  Image img = SwingFXUtils.toFXImage(bufferedImage, null);
			  return img;
		  }
		  catch (Exception ex)
		  {
			  System.out.println(ex);
		  }
		  return null;
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
	


