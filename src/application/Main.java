package application;

import java.io.InputStream;

import application.JavaMail;

import net.coobird.thumbnailator.Thumbnails;

//import javafx.embed.swing.SwingFXUtils;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;

import javafx.scene.control.ButtonBar;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Base64;
import java.util.Optional;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;
import javax.imageio.ImageIO;

import org.json.JSONObject;
import org.json.JSONArray;

import com.github.sarxos.webcam.WebcamException;
//import com.github.sarxos.webcam.Webcam;
//import com.github.sarxos.webcam.WebcamEvent;
//import com.github.sarxos.webcam.WebcamListener;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

//TODO: Make code more modular, separate into classes?
public class Main extends Application {
     private static final String dbClassname = "com.mysql.cj.jdbc.Driver";
     private static final String CONNECTION = "jdbc:mysql://127.0.0.1/artfacedb";
     private static Stage primaryStage = new Stage();
     private static Scene[] sceneArray = new Scene[5];
     private FlowPane galleryFlowPane = new FlowPane();
     private ImageView imageView = new ImageView();
     private WebcamCapture cam = new WebcamCapture();
     private static Statement stmt;
     private Image image;
     GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
     int width = gd.getDisplayMode().getWidth();
     int height = gd.getDisplayMode().getHeight();
     private String currentUser;
     ProfilePicHandler profilePicHandler = new ProfilePicHandler();
     
     static Connection c;
     Database data = new Database();
     public Boolean userCreated = false;
     public Boolean userLogin = false;
     public Boolean isUploaded = false;
     private Boolean webcamClicked = false;
     private File uploadedImageFile;
     
    
    public void start(Stage primaryStage) {
        try {
            this.primaryStage = primaryStage;
            createHomePage();//start of program
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    //creates the welcome page
    public void createHomePage() {
        BorderPane borderPane = new BorderPane();
       

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(25, 25, 25, 25));
        gridPane.setStyle("-fx-background-color: teal;");

        Text sceneTitle = new Text("Hello, ArtFace!");
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 30));
        gridPane.add(sceneTitle, 0, 0, 2, 1); // Span 2 columns

        Image homeImage = new Image("application/Drawing.jpeg");
        ImageView homeImageView = new ImageView(homeImage);
        homeImageView.setFitHeight(200);
        homeImageView.setFitWidth(300);
        gridPane.add(homeImageView, 0, 1, 2, 1); 

        Button signUpBtn = new Button("Sign Up");
        signUpBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                 createSignUpPage();//changes screen to signup screen
            }
        });
        gridPane.add(signUpBtn, 0, 2);

        Button signInBtn = new Button("Sign In");
        signInBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                 createSignInPage();//changrs screen to sign in screen
            }
        });
        gridPane.add(signInBtn, 1, 2);

        // Create the "Forgot Password?" button
        Button forgotPasswordBtn = new Button("Forgot Password?");
        forgotPasswordBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showForgottenPasswordPrompt();
            }
        });
        
        HBox bottomBox = new HBox();
        bottomBox.setAlignment(Pos.BOTTOM_LEFT);
        bottomBox.getChildren().add(forgotPasswordBtn);
        bottomBox.setPadding(new Insets(15, 12, 15, 12));

        borderPane.setCenter(gridPane);
        borderPane.setBottom(bottomBox);
        borderPane.setStyle("-fx-background-color: orchid;");

        Scene homeScene = new Scene(borderPane, width, height);
        sceneArray[0] = homeScene;
        primaryStage.setTitle("ArtFace");
        primaryStage.setScene(homeScene);
        primaryStage.show();
    }

    private void showForgottenPasswordPrompt() {
        // Create a custom dialog
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Reset Your Password");
        dialog.setHeaderText("Enter your email and new password:");

        ButtonType resetButtonType = new ButtonType("Reset", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(resetButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        //user enter email and password that get sent to our email
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("New Password");

        grid.add(new Label("Email:"), 0, 0);
        grid.add(emailField, 1, 0);
        grid.add(new Label("New Password:"), 0, 1);
        grid.add(passwordField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(() -> emailField.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == resetButtonType) {
                return new Pair<>(emailField.getText(), passwordField.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(emailPassword -> {
            String email = emailPassword.getKey();
            String newPassword = emailPassword.getValue();
            JavaMail.PasswordResetRequest("april", email, newPassword);//email request
            //JavaMail.PasswordResetRequest("april", email, newPassword);
        });
    }


	//create sign up page for users to make an account
    //TODO: save the username and password into the database for later retrieval
    public void createSignUpPage() 
    {
      //main grid to hold the back button
      GridPane mainGrid = new GridPane();
      mainGrid.setAlignment(Pos.TOP_LEFT);
      mainGrid.setPadding(new Insets(25, 25, 25, 25));
      mainGrid.setHgap(10);
      mainGrid.setVgap(10);
      mainGrid.setStyle("-fx-background-color: pink;");

      //sign in grid that will contain the form fields
      GridPane signUpGrid = new GridPane();
      signUpGrid.setAlignment(Pos.CENTER);
      signUpGrid.setHgap(10);
      signUpGrid.setVgap(10);
      signUpGrid.setPadding(new Insets(25, 25, 25, 25));

      Text signUpTitle = new Text("Sign Up");
      signUpTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 30));
      signUpGrid.add(signUpTitle, 0, 1);

      Label userName = new Label("User Name:");
	  signUpGrid.add(userName, 0, 2);
	
	  TextField userTextField = new TextField();
	  signUpGrid.add(userTextField, 1, 2);
	    
	  Label pw = new Label("Password:");
	  signUpGrid.add(pw, 0, 3);
	
	  PasswordField pwBox = new PasswordField();
	  signUpGrid.add(pwBox, 1, 3);
	
	  Label name = new Label("Name:");
	  signUpGrid.add(name, 0, 4);
	
	  TextField nameField = new TextField();
	  signUpGrid.add(nameField, 1, 4);
	
	  Label email = new Label("Email:");
	  signUpGrid.add(email, 0, 5);
	
	  TextField emailField = new TextField();
	  signUpGrid.add(emailField, 1, 5);
	
	  Button signUpButton = new Button("Sign up");
	  signUpGrid.add(signUpButton, 0, 6);
	
	  Scene signUpScene = new Scene(mainGrid, width, height);
	
	  signUpButton.setOnAction(new EventHandler<ActionEvent>() {
	  @Override
	  public void handle(ActionEvent event) {
	        String enteredUsername = userTextField.getText();
	        currentUser = enteredUsername;
	        String enteredPassword = pwBox.getText();
	        String enteredEmail = emailField.getText();
	        String enteredName = nameField.getText();
	
	        //validate form fields
	      if (enteredUsername.trim().length() == 0)
	      {
	        showAlert("Error", "Please enter a username.");
	        return;
	      }
	
	      else if (enteredPassword.trim().length() < 5)
	      {
	        showAlert("Error", "Please enter a password longer than 5 characters.");
	        return;
	      }
	
	      else if (!enteredEmail.contains("@"))
	      {
	        showAlert("Error", "Please enter a valid email");
	        return;
	      }
	
	      else if (enteredName.trim().length() == 0)
	      {
	        showAlert("Error", "Please enter your name");
	        return;
	      }
	      
	      try {//enter info into database
	        userCreated = data.newUser(c, enteredUsername, enteredPassword, enteredEmail);
	      } catch (ClassNotFoundException | SQLException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	      }
	      System.out.println("userCreated" + userCreated);
	      //if all form fields are valid, go to create image generation page
	      if(userCreated)
	    	  createImageGenerationPage(signUpScene);
	      
	      else
	      {
	        userTextField.clear();
	        showAlert("Error", "Username taken, please enter new username");
	      }
	  }
	  });
	
		  mainGrid.add(signUpGrid, 20, 27);
		
		  CreateBackButton(mainGrid, sceneArray[0], 0, 0);
		
		  sceneArray[1] = signUpScene;
		  primaryStage.setScene(signUpScene);
		  primaryStage.show();


    }

    //create sign in page for user to sign in
    public void createSignInPage() {
      //main grid to hold back button
      GridPane mainGrid = new GridPane();
      mainGrid.setAlignment(Pos.TOP_LEFT);
      mainGrid.setPadding(new Insets(25, 25, 25, 25));
      mainGrid.setHgap(10);
      mainGrid.setVgap(10);
      mainGrid.setStyle("-fx-background-color: lightsalmon;");

      //sign in grid to hold form fields
      GridPane signInGrid = new GridPane();
      signInGrid.setAlignment(Pos.CENTER);
      signInGrid.setHgap(10);
      signInGrid.setVgap(10);
      
      Image signInImage = new Image("application/sweet.png");
      ImageView signInImageView = new ImageView(signInImage);
      signInGrid.add(signInImageView, 0, 0);
      signInImageView.setFitHeight(170);
      signInImageView.setFitWidth(170);
      signInImageView.setPreserveRatio(true);

      Text signInTitle = new Text("Sign in");
      signInTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 30));
      signInGrid.add(signInTitle, 0, 1);

      Text userText = new Text("User Name:");
      TextField userTextField = new TextField();
      signInGrid.add(userText, 0, 2);
      signInGrid.add(userTextField, 1, 2);

      Text passText = new Text("Password:");
      PasswordField passwordField = new PasswordField();
      signInGrid.add(passText, 0, 3);
      signInGrid.add(passwordField, 1, 3);

      // Sign-in button
      Button signInBtn2 = new Button("Sign in");
      signInGrid.add(signInBtn2, 1, 4);
      Scene signInScene = new Scene(mainGrid, width, height);
    signInBtn2.setOnAction(new EventHandler<ActionEvent>() {
    @Override
    public void handle(ActionEvent event) {
        String enteredUsername = userTextField.getText();
        currentUser = enteredUsername;
        String enteredPassword = passwordField.getText();

		System.out.println("Successfull login");
		
        //validate credentials
      if (enteredUsername.trim().length() == 0)
      {
        showAlert("Error", "Please enter a username.");
        return;
      }		
      if (enteredPassword.trim().length() < 5)
      {
        showAlert("Error", "Please enter a password longer than 5 characters.");
        return;
      }
      try {
	        userLogin = data.checkUser(c, enteredUsername, enteredPassword);
	      } catch (ClassNotFoundException | SQLException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	      }
      if(userLogin)
    	  createImageGenerationPage(signInScene);
      else
        showAlert("Error", "Incorrect username or password, please try again."); 
      
    }
    });

      mainGrid.add(signInGrid, 20, 27);

      CreateBackButton(mainGrid, sceneArray[0], 0, 0);

      primaryStage.setScene(signInScene);
      primaryStage.show();
}

    //create the profile page
    public void createProfilePage()
    {
      GridPane profileGrid = new GridPane();
      profileGrid.setStyle("-fx-background-color: cornflowerblue;");
      profileGrid.setHgap(10);
      profileGrid.setVgap(10);
      profileGrid.setPadding(new Insets(25, 25, 25, 25));

      FlowPane galleryFlowPane = new FlowPane();
      galleryFlowPane.setPadding(new Insets(5, 5, 5, 5));
      galleryFlowPane.setVgap(4);
      galleryFlowPane.setHgap(4);

      //set default profile picture
      //Image defaultPic = new Image("application/icon.jpeg");
      //ImageView defaultImageView = new ImageView(defaultPic);
      Image userProfilePic = profilePicHandler.getProfilePic(currentUser);
      ImageView userProfileImageView = new ImageView(userProfilePic);
      profileGrid.add(userProfileImageView, 0, 1);
      userProfileImageView.setFitWidth(200);
      userProfileImageView.setFitHeight(200);
      userProfileImageView.setPreserveRatio(true);

      Label profileLabel = new Label("My Profile");
      profileLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 30));
      profileLabel.setAlignment(Pos.CENTER_RIGHT);
      profileGrid.add(profileLabel, 1, 0);

      Button uploadProfileBtn = new Button("Upload Profile Photo");
      profileGrid.add(uploadProfileBtn, 0,2);

      Scene profileScene = new Scene(profileGrid, width, height);

      uploadProfileBtn.setAlignment(Pos.TOP_CENTER);
      
      ImageView profileImageView = new ImageView();

      uploadProfileBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	uploadProfilePhoto(profileImageView, userProfileImageView, profileGrid, 1, 0);
            }
      });

        //gallery to the grid containing profile contents
      ScrollPane gallery = createGallery();
      profileGrid.add(gallery, 1, 3);

      CreateBackButton(profileGrid, sceneArray[3], 0, 0);
      sceneArray[2] = profileScene;
      primaryStage.setScene(profileScene);
      primaryStage.show();

    }
    //option prompts for the generate image
    public ObservableList<String> getMediaItems()
    {
        ObservableList<String> mediaItems = FXCollections.observableArrayList(
        		"-",
          	    "Kim Kardashian",
          	    "Brad Pitt",
          	    "Ariana Grande",
          	    "Chris Evans",
          	    "Emma Watson",
          	    "Jim Carrey",
          	    "Leonardo DiCaprio",
          	    "Lady Gaga",
          	    "Snoop Dogg",
          	    "Angelina Jolie",
          	    "Megan Fox",
          	    "Nicolas Cage"
        );
        return mediaItems;
    }
    
    public ObservableList<String> getPlacesItems()
    {
        ObservableList<String> placesItems = FXCollections.observableArrayList(
        		"-",
       	        "American Spirit",
       	        "Ancient Egypt",
       	        "Antarctica",
       	        "Bejing",
       	        "Greece",
       	        "London",
       	        "Paris"
        );
        return placesItems;
    }
    
    public ObservableList<String> getArtItems()
    {
        
        ObservableList<String> artItems = FXCollections.observableArrayList(
        		"-",
	       	    "Abstract",
	       	    "Conceptual",
	       	    "Cubism",
	       	    "Contemporary",
	       	    "Futurism",
	            "Impressionism",
	      	    "Modern",
	      	    "Neoclassic",
	      	    "Pop Art",
	      	    "Rococo",
	            "Surrealism"
        );
        return artItems;
    }

    public ObservableList<String> getCartoonItems()
    {
        
        ObservableList<String> cartoonItems = FXCollections.observableArrayList(
        		"-",
	       	    "Anime",
	       	    "Animated",
	       	    "Cartoon",
	       	    "Caricature",
	       	    "Claymation",
	       	    "Comic",
	       	    "Doodle",
	            "Manga"      	     
        );
        return cartoonItems;
    }
    
    public ObservableList<String> getThemesItems()
    {
        
        ObservableList<String> themesItems = FXCollections.observableArrayList(
        		 "-",
        	     "Cheery",
        	     "Christmas",
        	     "Colorful",
        	     "Cyberpunk",
        	     "Dreamy",
        	     "Fanstasy",
        	     "Halloween",
        	     "Jungle",
        	     "Nature",
        	     "Outer space",
        	     "Painting",
        	     "Psychedelic",
        	     "Scary",
        	     "Steampunk",
        	     "Surprise",
        	     "Underwater",
        	     "Western"
        );
        return themesItems;
    }
    
    public ObservableList<String> getTimesItems()
    {
        ObservableList<String> timesItems = FXCollections.observableArrayList(
        		"-",
        		"50's Diner",
        		"90's Kids",
        		"Futuristic",
        		"Prehistoric",
        		"Medieval",
        		"Renaissance",
        		"Roaring 20's",
        		"Victorian"     		   		
        );
        return timesItems;
    }
    
    //create the page to generate an image
    public void createImageGenerationPage(Scene scene) {

    	//main grid to hold back button
    	GridPane mainGrid = new GridPane();
        mainGrid.setStyle("-fx-background-color: plum;");
        mainGrid.setPadding(new Insets(15, 15, 15, 15));
        HBox hbox = new HBox();
        hbox.setSpacing(10);
        hbox.setPadding(new Insets(15, 20, 10, 15));
        ComboBox<String> timesComboBox = new ComboBox<>();
        ComboBox<String> themesComboBox = new ComboBox<>();
        ComboBox<String> cartoonComboBox = new ComboBox<>();
        ComboBox<String> artComboBox = new ComboBox<>();
        ComboBox<String> placesComboBox = new ComboBox<>();
        ComboBox<String> mediaComboBox = new ComboBox<>();
        
        ObservableList<String> mediaItems = getMediaItems();
        ObservableList<String> placesItems = getPlacesItems();
        ObservableList<String> artItems = getArtItems();
        ObservableList<String> cartoonItems = getCartoonItems();
        ObservableList<String> themesItems = getThemesItems();
        ObservableList<String> timesItems = getTimesItems();
        
        timesComboBox.setItems(timesItems);
        themesComboBox.setItems(themesItems);
        cartoonComboBox.setItems(cartoonItems);
        artComboBox.setItems(artItems);
        placesComboBox.setItems(placesItems);
        mediaComboBox.setItems(mediaItems);
        
        //grid to add go to profile button
        GridPane profileGrid = new GridPane();
        profileGrid.setPadding(new Insets(15, 0, 25, 0));
        profileGrid.setAlignment(Pos.TOP_LEFT);

        Button profileButton = new Button("Go to Profile");
        profileGrid.add(profileButton, 0, 0);
        
        profileButton.setAlignment(Pos.TOP_LEFT);
        profileButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                createProfilePage();
            }
        });

        //center the contents of the page
        GridPane centeredGrid = new GridPane();
        centeredGrid.setAlignment(Pos.CENTER);
        centeredGrid.setHgap(10);
        centeredGrid.setVgap(10);
        
        Image mainImage = new Image("application/sage.png");
        ImageView mainImageView = new ImageView(mainImage);
        mainImageView.setFitWidth(200);
        mainImageView.setFitHeight(200);
        
        centeredGrid.add(mainImageView, 25, 7);

        Text uploadText = new Text("Create an AI version of you");
        uploadText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        centeredGrid.add(uploadText, 25, 10);

        Text createText = new Text("...Or create someone new!");
        createText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        centeredGrid.add(createText, 25, 11);
        
        Text styleText = new Text("First, enter in some style. You can...");
        styleText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 16));
        centeredGrid.add(styleText, 25, 12);
        hbox.getChildren().addAll(timesComboBox, themesComboBox, cartoonComboBox, artComboBox, placesComboBox, mediaComboBox);
        
        Text timesText = new Text("Pick a Time Period:");
        styleText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 16));
        centeredGrid.add(timesText, 25, 13);
        centeredGrid.add(timesComboBox, 26, 13);
        
        Text themesText = new Text("Pick a Theme:");
        themesText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 16));
        centeredGrid.add(themesText, 25, 14);
        centeredGrid.add(themesComboBox, 26, 14);
        
        Text cartoonText = new Text("Pick a Drawing Style:");
        cartoonText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 16));
        centeredGrid.add(cartoonText, 25, 15);
        centeredGrid.add(cartoonComboBox, 26, 15);
        
        Text artText = new Text("Pick an Art Style:");
        artText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 16));
        centeredGrid.add(artText, 25, 16);
        centeredGrid.add(artComboBox, 26, 16);
        
        Text placesText = new Text("Pick a Place:");
        placesText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 16));
        centeredGrid.add(placesText, 25, 17);
        centeredGrid.add(placesComboBox, 26, 17);
        
        Text mediaText = new Text("Pick a Celebrity:");
        mediaText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 16));
        centeredGrid.add(mediaText, 25, 18);
        centeredGrid.add(mediaComboBox, 26, 18);
        
        Text thenText = new Text("Then...");
        thenText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 18));
        centeredGrid.add(thenText, 25, 19);
        
        Text uploadImageText = new Text("Upload an Image of Your Smile:");
        uploadImageText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 16));
        centeredGrid.add(uploadImageText, 25, 20);
        
        Button uploadButton = new Button("Upload Image");
        centeredGrid.add(uploadButton, 25, 21);

        Text orText = new Text("Or you can...");
        orText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 18));
        centeredGrid.add(orText, 25, 22);
        
        Text promptText = new Text("Enter a Prompt:");
        TextField promptTextField = new TextField();
        promptTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                isUploaded = false;
                uploadedImageFile = null;
            }
        });
        
        promptText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 16));
        centeredGrid.add(promptText, 25, 23);
        centeredGrid.add(promptTextField, 25, 24);

        Button generateImageButton = new Button("Generate Image");
        centeredGrid.add(generateImageButton, 25, 25);
        
        Button webcamButton = new Button("Take a selfie");
        centeredGrid.add(webcamButton, 26, 21);
        
      //for uploading a image from local file
        uploadButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
					chooseFile();
	            	isUploaded = true;
				} catch (Exception e) {
					showAlert("Error", "Image could not be uploaded.");
					e.printStackTrace();
				}
            }
        });

        generateImageButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String prompt = promptTextField.getText();
                
                //Add the other styles and call them in the method. Add together in api call with space in betweeen
                String timesStyle = getPrompt(timesComboBox.getValue());
                String themesStyle = getPrompt(themesComboBox.getValue());
                String cartoonStyle = getPrompt(cartoonComboBox.getValue());
                String artStyle = getPrompt(artComboBox.getValue());
                String placesStyle = getPrompt(placesComboBox.getValue());
                String mediaStyle = getPrompt(mediaComboBox.getValue());
                
                String combinedStyles = combineStyles(prompt, timesStyle, themesStyle, cartoonStyle, artStyle, placesStyle, mediaStyle);

                try {
                	onGenerateImageButtonClicked(prompt, combinedStyles);
                } catch (Exception e) {       
                    showAlert("Error", "Image could not be generated.");
                    e.printStackTrace();
                }
            }
        });
        

        profileButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                createProfilePage();
            }
        });
        
        webcamButton.setOnAction(new EventHandler<ActionEvent>() {//button to start webcam
            @Override
            public void handle(ActionEvent event) {
            	webcamClicked = true;
            	try {
					cam.VideoFeed();
				} catch (WebcamException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	
            }
        });
        

        profileGrid.add(centeredGrid, 0, 1);
        //button currently not working the back button has same function
        // HBox for the bottom left "Log Out" button
        HBox bottomLeftBox = new HBox();
        bottomLeftBox.setAlignment(Pos.BOTTOM_LEFT);
        bottomLeftBox.setPadding(new Insets(10, 0, 0, 10));

        Button logOutButton = new Button("Log Out");
        logOutButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Handle the log out action
            	primaryStage.setScene(sceneArray[2]);
            }
        });
        bottomLeftBox.getChildren().add(logOutButton);

        // Add the profile grid and bottom left box to the main grid
        mainGrid.add(profileGrid, 0, 1);
        mainGrid.add(bottomLeftBox, 0, 3);
        
        Scene imageScene = new Scene(mainGrid, width, height);

        CreateBackButton(mainGrid, sceneArray[0], 0, 0);
        sceneArray[3] = imageScene;
        primaryStage.setScene(imageScene);
        primaryStage.show();
  }
    
    public String combineStyles(String prompt, String timesStyle, String themesStyle, String cartoonStyle, String artStyle, String placesStyle, String mediaStyle)
    {
        //combine the styles to pass into image generation style
        String combinedStyles = String.join(" ", timesStyle, themesStyle, cartoonStyle, artStyle, placesStyle, mediaStyle).trim();
        
        return combinedStyles;
    }
    
    //prompts user to upload profile picture
	private void uploadProfilePhoto(ImageView profileImageView, ImageView defaultImageView, GridPane profileGrid, int row, int column) {
	    FileChooser fileChooser = new FileChooser();
	
	    fileChooser.setTitle("Select a File to Upload");
	
	    //prompt user to upload photo
	    File uploadedImage = fileChooser.showOpenDialog(primaryStage);
	
	    if (uploadedImage != null) {
	        try {
	          //replace the default image with the new profile photo
	          defaultImageView.setVisible(false);
	          Image image = new Image(uploadedImage.toURI().toString());
	          ImageView imageViewUpl = new ImageView(image);
	
	          imageViewUpl.setFitWidth(200);
	          imageViewUpl.setFitHeight(200);
	          imageViewUpl.setPreserveRatio(true);
	          
	          data.insertProfilePic(c, currentUser, image);	//add profile pic to sql server
	          profilePicHandler.saveProfilePic(currentUser, uploadedImage);	//add profile pic to ProfilePicHandler class
	
	          profileGrid.add(imageViewUpl, column, row);
	        } catch (Exception e) {
	        	showAlert("Error", "Uploaded image is null.");
	        }
	    }
	}

	//get the detailed prompt of each style
	public String getPrompt(String style) {
	 String enteredPrompt = "";
	 
	 if (style == null)
	 {
		 return "";
	 }
		 
	//based on the user's input style, detail the prompt more so that the image comes out better
	switch (style) {
	   case "50's Diner":
		 enteredPrompt = "1950's diner, 1950's, 50s, diner, milkshake";
		 break;
	   case "90's Kids":
	     enteredPrompt = "1990, 90s, lisa frank, furbies, gameboy, tamagotchi, beanie babies";
	     break;
	   case "Abstract":
	     enteredPrompt = "abstract, geometric, julie mehretu";
	     break;
	   case "American Spirit":
		 enteredPrompt = "america, USA, soaring bald eagle, red white and blue";
		 break;
	   case "Ancient Egypt":
		 enteredPrompt = "ancient egypt, egypt, pyramids, hieroglyphs";
		 break;
	   case "Angelina Jolie":
		 enteredPrompt = "angelina jolie, celebrity";
		 break;
	   case "Animated":
	     enteredPrompt = "animated, animation";
	     break;
	   case "Anime":
         enteredPrompt = "anime, japanese cartoon, cute, big eyes";
         break;
	   case "Antarctica":
	     enteredPrompt = "antarctica, snow, cold, winter, blizzard";
	     break;
	   case "Ariana Grande":
		 enteredPrompt = "ariana grande, celebrity";
		 break;
	   case "Bejing":
		 enteredPrompt = "bejing, china, great wall of china";
		 break;
	   case "Brad Pitt":
		 enteredPrompt = "brad pitt, celebrity";
		 break;
	   case "Cartoon":
	     enteredPrompt = "cartoon, 2d animation";
	     break;
	   case "Caricature":
	     enteredPrompt = "caricature, exaggerated features";
	     break;
	   case "Cheery":
	     enteredPrompt = "rainbows, butterflies, unicorn in background, pink fluffy clouds, pink sky, dreamy";
	     break;
	   case "Chris Evans":
		 enteredPrompt = "chris evans, celebrity";
		 break;
	   case "Christmas":
	     enteredPrompt = "christmas, santa clause, reindeers in sky, snowmen, snowflakes, winter";
	     break;
	   case "Claymation":
		 enteredPrompt = "claymation, clay animation, clay";
		 break;
	   case "Colorful":
	     enteredPrompt = "vibrant, colorful, saturated bright";
	     break;
	   case "Comic":
		 enteredPrompt = "comic, drawing, newspaper art";
		 break;
	   case "Conceptual":
		 enteredPrompt = "conceptual art, conceptualism";
		 break;		
	   case "Contemporary":
		 enteredPrompt = "contemporary art, contemporary";
		 break;	
	   case "Cyberpunk":
	     enteredPrompt = "cyberpunk, cyborgs, technology, augmented reality, punk rock";
	     break;
	   case "Cubism":
		  enteredPrompt = "cubism, art, cubes";
		  break;
	   case "Doodle":
		 enteredPrompt = "doodle, hand drawn, drawing, sketch";
		 break;
	   case "Dreamy":
	     enteredPrompt = "lovely, nice, dreamy, pastel, whimsical";
	     break;
	   case "Emma Watson":
		  enteredPrompt = "emma watson, celebrity";
		  break;
	   case "Fantasy":
	     enteredPrompt = "fantasy, mystical, dreamy, colorful, wonderland";
	     break;
	   case "Futuristic":
	  	 enteredPrompt = "futuristic, future, realistic, chrome, technology";
	  	 break;
	   case "Futurism":
		 enteredPrompt = "futurism art style, futurism";
		 break;
	   case "Greece":
		 enteredPrompt = "greek, greece, philosophy, socrates";
		 break;
	   case "Halloween":
		 enteredPrompt = "halloween, autumn, pumpkins, scarecrows, black cats";
     	 break;
	   case "Impressionism":
		 enteredPrompt = "impressionism art style, claude monet";
     	 break;
	   case "Jim Carrey":
	     enteredPrompt = "jim carrey, celebrity";
	     break;
	   case "Jungle":
         enteredPrompt = "jungle, forest, leopard in tree, river in background";
		 break;
	   case "Kim Kardashian":
	     enteredPrompt = "kim kardashian, celebrity";
		 break;
	   case "Lady Gaga":
		 enteredPrompt = "lady gaga, celebrity";
		 break;
	   case "Leonardo DiCaprio":
		 enteredPrompt = "leonardo dicaprio, celebrity";
		 break;
	   case "London":
		 enteredPrompt = "london, england, big ben, london bridge, london tower, beefeaters, telephone booths";
		 break;
	   case "Manga":
		 enteredPrompt = "manga, anime, japanese comic";
		 break;
	   case "Medieval":
	  	 enteredPrompt = "medieval times, castles";
	  	 break;
	   case "Megan Fox":
		 enteredPrompt = "megan fox, celebrity";
		 break;
	   case "Modern":
		 enteredPrompt = "modern art, new age";
		 break;
	   case "Nature":
	  	 enteredPrompt = "nature, butterflies, landscape, dreamy";
	  	 break;
	   case "Neoclassic":
		 enteredPrompt = "neoclassical art style, neoclassic";
		 break;
	   case "Nicolas Cage":
		 enteredPrompt = "nicolas cage, celebrity";
		 break;
	   case "Outer space":
	  	 enteredPrompt = "outer space, galaxy, stars, moon, planets";
	  	 break;
	   case "Picasso":
	     enteredPrompt = "in the style of picasso, art";
	     break;
	   case "Painting":
	     enteredPrompt = "painting, art, claude monet";
	     break;
	   case "Paris":
		 enteredPrompt = "paris, france, eiffel tower, sunset";
		 break;
	   case "Pointilism":
	     enteredPrompt = "pointilism, art";
	     break;
	   case "Pop Art":
		 enteredPrompt = "pop art, colorful, andy warhol";
		 break;
	   case "Prehistoric":
	  	 enteredPrompt = "prehistoric, dinosaurs in background, jurassic, t rex, triceratops, brontosaurus";
	  	 break;
	   case "Psychedelic":
	     enteredPrompt = "psychedelic, trippy, colorful";
	     break;
	   case "Renaissance":
	     enteredPrompt = "Renaissance period, classical art, michelangelo";
	     break;
	   case "Roaring 20's":
	     enteredPrompt = "Roaring 20's, 1920's, flapper dancers, boa, jazz";
	     break;
	   case "Rococo":
		 enteredPrompt = "rococo art style, light, fluffy, pastel";
		 break;
	   case "Scary":
	  	 enteredPrompt = "scary, creepy, unsettling, frightening, spiders, bats";
	  	 break;
	   case "Snoop Dogg":
		 enteredPrompt = "snoop dogg, celebrity, rapper";
		 break;
	   case "Steampunk":
		 enteredPrompt = "steampunk, retro, technology, gears";
		 break;
	   case "Surprise":
	  	 enteredPrompt = "random, weird, funny, 3d geometric objects";
	  	 break;
	   case "Surrealism":
	     enteredPrompt = "surreal, realistic, salvador dali, vladimir kush";
	     break;
	   case "Underwater":
	  	 enteredPrompt = "underwater, ocean, under the sea";
	  	 break;
	   case "Victorian":
		 enteredPrompt = "victorian era, great britain";
		 break;
	   case "Western":
		 enteredPrompt = "western theme, country, cowboy";
		 break;
	   default:
	     enteredPrompt = ""; 
	     break;
		 }
		 
		 return enteredPrompt;
	}
	
	//when the generate image button is clicked, a new scene will pop up with the generated image
	public void onGenerateImageButtonClicked(String prompt, String style) {
	    GridPane grid = new GridPane();
	    grid.setAlignment(Pos.CENTER);
	    grid.setHgap(10);
	    grid.setVgap(10);
	    grid.setPadding(new Insets(25, 25, 25, 25));
	    grid.setStyle("-fx-background-color: lavenderblush;");
	
	    StackPane stack = new StackPane();
	    grid.add(stack, 0, 1, 2, 1); 
	
	    Scene generatedImageScene = new Scene(grid, width, height);
	    
	    ProgressIndicator progressIndicator = new ProgressIndicator();
	    progressIndicator.setVisible(false);
	    grid.add(progressIndicator, 0, 2);
	    
	    //require the user to enter a style
	    if (style == "" || style == "------")
	    {
	    	showAlert("Error", "Please enter a style!");
	    	return;
	    }
	    
	    //start the image generation
	    Task<Image> imageGenerationTask = new Task<>() {
	        @Override
	        protected Image call() throws Exception {
	      	  updateProgress(ProgressIndicator.INDETERMINATE_PROGRESS, 1);
	      	  	
	      	  	if(cam.takePicture&& webcamClicked){//if picture was taken
	      	  		cam.takePicture = false;
	      	  		webcamClicked = false;
	      	  		//"CS370Project/WebcamPic/FaceOfArt.jpg"
	      	  		encodeImage(style);
	      	  		
	      	  	} else if (isUploaded == false) {//no picture upload
	      	  		System.out.println("normal generate");
	      	  		return generateImageFromText(prompt, style);
	      	  	} else{
		      	    webcamClicked = false;	      	  		
	      	  		Image webcamImage = encodeImage(style);
	      	  		return webcamImage;
	      	  	} if (isUploaded == false) {
	      	  		System.out.println("normal generate");
	      	  		return generateImageFromText(prompt, style);
	      	  	}else {
	      	  		Image uploadImage = encodeImage(style);
	      	  		return uploadImage;
	      	  	}

	        }

	        @Override
	        protected void succeeded() {
	            Platform.runLater(() -> {
	                super.succeeded();
		            Image imageResponse = getValue();
		            ImageView genImageView = new ImageView(imageResponse);
		            genImageView.setFitHeight(500);
		            genImageView.setFitWidth(500);
		            genImageView.setPreserveRatio(true);
		            stack.getChildren().add(genImageView);
		            progressIndicator.setVisible(false);
		            
		            Button newImgBtn = new Button("Generate new Image");
		            grid.add(newImgBtn, 0, 2);
		            newImgBtn.setOnAction(new EventHandler<ActionEvent>() {
		                @Override
		                public void handle(ActionEvent event) {
		                  primaryStage.setScene(sceneArray[3]);
		                  primaryStage.show();
		                }
		            });
		            //opens the file explorer to save image,save into gallery and db
		            Button saveButton = new Button("Save Image");
		            grid.add(saveButton, 0, 3);
		            saveButton.setOnAction(new EventHandler<ActionEvent>() {
		                @Override
		                public void handle(ActionEvent event) {
		                    try {
		                    	saveImage(primaryStage, genImageView.getImage());
		                    } catch (IOException e) {
		                    	showAlert("Error!", "Photo could not be saved at this time.");
		                    }
		                }
		            });
	            });
	        }
	        //something gone wrong
	        @Override
	        protected void failed() {
	            super.failed();
	            System.err.println("Error generating image.");
	            progressIndicator.setVisible(false);
	        }
	    };
	
	    progressIndicator.setVisible(true);
	    Thread thread = new Thread(imageGenerationTask);
	    thread.setDaemon(true);
	    thread.start();
	
	    Platform.runLater(() -> {
	        primaryStage.setScene(generatedImageScene);
	        primaryStage.show();
	    });
	}
	
	//method to encode the image uploaded by the user
	public Image encodeImage(String style) throws Exception
	{
	    ByteArrayOutputStream baosResized = new ByteArrayOutputStream();
  	    Thumbnails.of(uploadedImageFile)
  	              .forceSize(1024, 1024)
  	              .toOutputStream(baosResized);

  	    byte[] resizedBytes = baosResized.toByteArray();
  	    String encodedImage = Base64.getEncoder().encodeToString(resizedBytes);
  	    
  	    byte[] imageData = generateImageFromImage(encodedImage, style);
  	    if (imageData != null) {
  	        return new Image(new ByteArrayInputStream(imageData));
  	    }
		return null;
	}
	
    
    //choose the image to upload
	private File chooseFile() throws Exception
	 {
	    FileChooser fileChooser = new FileChooser();
	    fileChooser.setTitle("Select an Image");
	    fileChooser.getExtensionFilters().addAll(
	        new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
	    );
	
	    File chosenFile = fileChooser.showOpenDialog(primaryStage);
	    if (chosenFile != null) {
	    	showSuccessAlert("Upload Success", "Click Generate Button to Continue");
	        uploadedImageFile = chosenFile;
	        return chosenFile;
	    }
	    else {
	    	showAlert("Error", "There was a problem uploading your photo. Please try again.");
	    }
		return null;
	 }

	public void showSuccessAlert(String title, String message)
	{
	      Alert alert = new Alert(Alert.AlertType.INFORMATION);
	      alert.setTitle(title);
	      alert.setHeaderText(null);
	      alert.setContentText(message);
	      alert.show();
	}
	//create the gallery
    public ScrollPane createGallery() {
        TilePane galleryTilePane = new TilePane();
        galleryTilePane.setPadding(new Insets(5, 5, 5, 5));
        galleryTilePane.setVgap(4);
        galleryTilePane.setHgap(4);
        galleryTilePane.setStyle("-fx-background-color: cornflowerblue;");
        galleryTilePane.setPrefColumns(4); 
        galleryTilePane.setAlignment(Pos.CENTER);

        //get the files from the gallery folder and display them
        try (Stream<Path> paths = Files.walk(Paths.get("src/application/gallery"))) {
            paths.filter(Files::isRegularFile).forEach(path -> {
                File file = path.toFile();
                Image galleryImage = new Image(file.toURI().toString(), 100, 0, true, true);
                ImageView galleryImageView = new ImageView(galleryImage);
                galleryImageView.setFitWidth(150);
                galleryImageView.setFitHeight(150);
                galleryImageView.setPreserveRatio(true);

                galleryTilePane.getChildren().add(galleryImageView);
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Could not find directory.");
        }

        //wrap tilepane in scrollpane
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(galleryTilePane);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        return scrollPane;
    }

  //save an image to user's computer
  public void saveImage(Stage stage, Image image) throws IOException {

      FileChooser fileChooser = new FileChooser();
      FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");

      fileChooser.getExtensionFilters().add(extFilter);

      File file = fileChooser.showSaveDialog(stage);

      if (file != null) {
          BufferedImage bImage = convertToBufferedImage(image);//SwingFXUtils.fromFXImage(image, null);
          ImageIO.write(bImage, "png", file);
          
          saveImageToGallery(bImage);
      }

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
  
  //save an image to the gallery folder to be later displayed
  public void saveImageToGallery(BufferedImage bImage) throws IOException {
	    File galleryFolder = new File("src/application/gallery");
	    if (!galleryFolder.exists()) {
	        galleryFolder.mkdirs();
	    }
	    
	    //file name must be unique	
	    String filename = "Image" + System.currentTimeMillis() + ".png";
	    File file = new File(galleryFolder, filename);
	    ImageIO.write(bImage, "png", file);

	    Platform.runLater(() -> addImageToGallery(file));
	}

  
  //adds an image to the gallery flow pane
  public void addImageToGallery(File file) {
      Image galleryImage = new Image(file.toURI().toString());
      ImageView galleryImageView = new ImageView(galleryImage);
      galleryImageView.setFitWidth(150);
      galleryImageView.setFitHeight(150);
      galleryImageView.setPreserveRatio(true);
      galleryFlowPane.getChildren().add(galleryImageView);
  }


  //generates the image from text input
  public Image generateImageFromText(String text, String style) throws UnirestException {
      JSONObject body = new JSONObject();
      body.put("text", text + style);

      HttpResponse<InputStream> response = Unirest.post("https://open-ai21.p.rapidapi.com/texttoimage2")
              .header("content-type", "application/json")
              .header("X-RapidAPI-Key", "8b2bd64aa5msh34f679538ef2433p1e4a2djsn927a54490a26")
              .header("X-RapidAPI-Host", "open-ai21.p.rapidapi.com")
              .body(body.toString())
              .asBinary();

      if (response.getStatus() == 200) {
          try (InputStream inputStream = response.getBody()) {
              return new Image(inputStream);
          } catch (IOException e) {
              e.printStackTrace();
              return null;
          }
      } else {
    	  showAlert("Error", "Failed to get image with status " + response.getStatusText());
          System.err.println("Failed to generate image: " + response.getStatusText());
          return null;
      }
  }
  
  public byte[] generateImageFromImage(String base64Image, String style) {
	    try {
	    	
	    	//convert the base 64 image to a byte array that contains the image data
	        byte[] imageData = Base64.getDecoder().decode(base64Image);
	        String negativePrompt = "bad quality, distorted features, cross eyes, six fingers, four fingers, abnormalities, nsfw, no pupils, multiple heads, detached head, inaccurate, multiple faces, face on neck, unnecessary faces, lazy eye, face on chest, bodily abnormalities, faces without heads";

	        String engine_id = "stable-diffusion-xl-1024-v1-0";
	        //String engine_id = "stable-diffusion-512-v2-1";
	        //String engine_id = "stable-diffusion-v1-5";

	        HttpResponse<JsonNode> response = Unirest.post("https://api.stability.ai/v1/generation/" + engine_id + "/image-to-image")
		              .header("Authorization", "Bearer sk-no6ZZBdGyv8LIVOZi0WGPTlgcQWk8rBzCv8VQKErIIMFGHwY")
		              .field("image_strength", 0.48)
		              .field("init_image_mode", "IMAGE_STRENGTH")
		              .field("init_image", imageData, "image.png")
		              .field("text_prompts[0][text]", style)
		              .field("text_prompts[0][weight]", 1)
		              .field("text_prompts[1][text]", negativePrompt)
		              .field("text_prompts[1][weight]", -1)
		              .field("cfg_scale", 7)
		              .field("clip_guidance_preset", "FAST_BLUE")
		              .field("sampler", "K_DPM_2_ANCESTRAL")
		              .field("samples", 3)
		              .field("steps", 40)
		              .asJson();

	        System.out.println(response.getBody());
	        if (response.getStatus() == 200) {
	        	JSONObject obj = new JSONObject(response.getBody().toString());
	        	JSONArray artifactsArray = obj.getJSONArray("artifacts");

	            if (artifactsArray.length() > 0) {
	                JSONObject firstArtifact = artifactsArray.getJSONObject(0);
	                String responseBase64Image = firstArtifact.getString("base64");
	            return Base64.getDecoder().decode(responseBase64Image);
	            }
	        } else {
	            System.err.println("API request failed: " + response.getStatus() + " : " + response.getStatusText());
	        }
	    } catch (Exception e) {
	        System.err.println("Image could not be generated from image.");
	    }
	    return null;
	} 
  
  //encode the image path to base 64
  public static String encodeToBase64(Path imagePath) throws Exception {
      //read image bytes
      byte[] imageBytes = Files.readAllBytes(imagePath);

      //encode bytes to Base64
      String encodedString = Base64.getEncoder().encodeToString(imageBytes);

      return encodedString;
  }

  //generates hash by applying SHA-1 hashing algorithm
  public static String generateHash(String base64Image) throws Exception {
      MessageDigest digest = MessageDigest.getInstance("SHA-1");
      //convert the base 64 image into a byte array and apply hashing algorithm
      byte[] hashBytes = digest.digest(base64Image.getBytes("UTF-8"));

      //build the hash
      StringBuilder sb = new StringBuilder();
      for (byte b : hashBytes) {
          sb.append(String.format("%02x", b));
      }

      return sb.toString();
  }

  //back button
  private void CreateBackButton(GridPane grid, Scene backScene, int colIndex, int rowIndex) {
      Button backButton = new Button("Back");

      backButton.setOnAction(event -> primaryStage.setScene(backScene));

      grid.add(backButton, colIndex, rowIndex);
      GridPane.setHalignment(backButton, HPos.LEFT);
      GridPane.setValignment(backButton, VPos.TOP);
  }
  
  //show an error with given title and message
  private void showAlert(String title, String message) {
      Alert alert = new Alert(Alert.AlertType.WARNING);
      alert.setTitle(title);
      alert.setHeaderText(null);
      alert.setContentText(message);
      alert.show();
  }

  //main method
  public static void main(String[] args) throws ClassNotFoundException, SQLException {

    //panacea123
    Scanner reader = new Scanner(System.in); //for user input
    System.out.println("Enter Sql Password: ");
    String password = reader.nextLine(); //grabs input

    //connection
    System.out.println(dbClassname);
    Properties p = new Properties();
    p.put("user", "root");
    p.put("password",password);
    reader.close();
    c = DriverManager.getConnection(CONNECTION,p);
    //stmt = c.createStatement();
    launch(args);

    //c.close();
  }    
}
