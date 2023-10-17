package application;
	
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Scanner;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;


public class Main extends Application {
	private static final String dbClassname = "com.mysql.cj.jdbc.Driver";
	private static final String CONNECTION = "jdbc:mysql://127.0.0.1/ArtFacedb";
	@Override
	public void start(Stage primaryStage) {
		try {
			GridPane grid = new GridPane();
			
			
			
			grid.setAlignment(Pos.CENTER);
			grid.setHgap(10);
			grid.setVgap(10);
			grid.setPadding(new Insets(25, 25, 25, 25));
			
			Scene signUpScene = new Scene(grid, 300, 275);
			signUpScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			Text scenetitle = new Text("Welcome");
			scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
			grid.add(scenetitle, 0, 0, 2, 1);

			Label userName = new Label("User Name:");
			grid.add(userName, 0, 1);

			TextField userTextField = new TextField();
			grid.add(userTextField, 1, 1);

			Label pw = new Label("Password:");
			grid.add(pw, 0, 2);

			PasswordField pwBox = new PasswordField();
			grid.add(pwBox, 1, 2);
			
			primaryStage.setScene(signUpScene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	
	
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
		
		launch(args);
		c.close();
	}
}
