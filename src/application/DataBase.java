package application;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner; //user input
public class DataBase {
	private static final String dbClassname = "com.mysql.cj.jdbc.Driver";
	private static final String CONNECTION = "jdbc:mysql://127.0.0.1/ArtFacedb";
	public static void main(String[] args) throws ClassNotFoundException, SQLException{
		//panacea123
		Scanner reader = new Scanner(System.in);//for user input
		System.out.println("Enter Sql Pasword: ");
		String password = reader.nextLine();//grabs input
		
		//connection
		System.out.println(dbClassname);
		Properties p = new Properties();
		p.put("user", "root");
		p.put("password",password);
		reader.close();
		Connection c = DriverManager.getConnection(CONNECTION,p);
		
		System.out.println("It works");
		c.close();
	}

}
