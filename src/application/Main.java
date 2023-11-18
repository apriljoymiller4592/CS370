package application;

import java.io.InputStream;

import net.coobird.thumbnailator.Thumbnails;

import java.io.InputStreamReader;


import javafx.embed.swing.SwingFXUtils;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

//import com.github.sarxos.webcam.Webcam;
//import com.github.sarxos.webcam.WebcamEvent;
//import com.github.sarxos.webcam.WebcamListener;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.body.MultipartBody;
import com.mashape.unirest.request.body.RequestBodyEntity;

//import application.WebcamCapture.VideoFeedTaker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

//TODO: Make code more modular, separate into classes?
public class Main extends Application {
     private static final String dbClassname = "com.mysql.cj.jdbc.Driver";
     private static final String CONNECTION = "jdbc:mysql://127.0.0.1/ArtFaceDB";
     private static Stage primaryStage = new Stage();
     private static Scene[] sceneArray = new Scene[5];
     private FlowPane galleryFlowPane = new FlowPane();
     private ImageView imageView = new ImageView();
     private static Statement stmt;
     private Image image;
 //    static Webcam webcam;
     static Connection c;
     Database data = new Database();
     public Boolean userCreated = false;
     public Boolean isUploaded = false;
     private File uploadedImageFile;

    public void start(Stage primaryStage) {
        try {
            this.primaryStage = primaryStage;
            createHomePage();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    //creates the welcome page
    public void createHomePage() 
    {
        GridPane gridPane = new GridPane();
        Scene homeScene = new Scene(gridPane, 850, 850);

        Text sceneTitle = new Text("Hello, ArtFace!");
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 30));
        gridPane.add(sceneTitle, 2, 1);
        gridPane.setStyle("-fx-background-color: teal;");

        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(25, 25, 25, 25));

        //when the sign up button is clicked, go to the sign up page
        Button signUpBtn = new Button("Sign Up");
        signUpBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                createSignUpPage();
            }
        });

        //when the sign in button is clicked, go to the sign in page
        Button signInBtn = new Button("Sign In");
        signInBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                createSignInPage();
            }
        });
        
        Image homeImage = new Image("application/Drawing.jpeg");
        ImageView homeImageView = new ImageView(homeImage);
        homeImageView.setFitHeight(200);
        homeImageView.setFitWidth(300);
        
        gridPane.add(homeImageView, 2, 0);
        gridPane.add(signInBtn, 1, 2);
        gridPane.add(signUpBtn, 3, 2);
        sceneArray[0] = homeScene;
        primaryStage.setTitle("ArtFace");
        primaryStage.setScene(homeScene);
        primaryStage.show();
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
	
	  Scene signUpScene = new Scene(mainGrid, 850, 850);
	
	  signUpButton.setOnAction(new EventHandler<ActionEvent>() {
	  @Override
	  public void handle(ActionEvent event) {
	        String enteredUsername = userTextField.getText();
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
	      createImageGenerationPage(signUpScene);
	    /*  try {
	        userCreated = data.newUser(c, enteredUsername, enteredPassword);
	      } catch (ClassNotFoundException | SQLException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	      }*/
	     /* System.out.println("userCreated" + userCreated);
	      //if all form fields are valid, go to create image generation page
	      if(userCreated)
	        	
	      else
	      {
	        userTextField.clear();
	        showAlert("Error", "Username taken, please enter new username");
	      }*/
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
      Scene signInScene = new Scene(mainGrid, 850, 850);
    signInBtn2.setOnAction(new EventHandler<ActionEvent>() {
    @Override
    public void handle(ActionEvent event) {
        String enteredUsername = userTextField.getText();
        String enteredPassword = passwordField.getText();

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
        //successful login
       System.out.println("Successfull login");
       createImageGenerationPage(signInScene);	
        
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
      Image defaultPic = new Image("application/icon.jpeg");
      ImageView defaultImageView = new ImageView(defaultPic);
      profileGrid.add(defaultImageView, 0, 1);
      defaultImageView.setFitWidth(200);
      defaultImageView.setFitHeight(200);
      defaultImageView.setPreserveRatio(true);

      Label profileLabel = new Label("My Profile");
      profileLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 30));
      profileLabel.setAlignment(Pos.CENTER_RIGHT);
      profileGrid.add(profileLabel, 1, 0);

      Button uploadProfileBtn = new Button("Upload Profile Photo");
      profileGrid.add(uploadProfileBtn, 0,2);

      Scene profileScene = new Scene(profileGrid, 850, 850);

      uploadProfileBtn.setAlignment(Pos.TOP_CENTER);
      
      ImageView profileImageView = new ImageView();

      uploadProfileBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	uploadProfilePhoto(profileImageView, defaultImageView, profileGrid, 1, 0);
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

    //create the page to generate an image
    public void createImageGenerationPage(Scene scene) {

    	//main grid to hold back button
    	GridPane mainGrid = new GridPane();
        mainGrid.setStyle("-fx-background-color: plum;");
        mainGrid.setPadding(new Insets(15, 15, 15, 15));
        ComboBox<String> comboBox = new ComboBox<>();

        ObservableList<String> items = FXCollections.observableArrayList(
        		"Animated",
        		"Abstract",
	            "Anime",
	            "Caricature",
	            "Cartoon",
	            "Cheery",
	            "Fantasy",
	            "Futuristic",
	            "Medieval",
	            "Nature",
	            "Outer space",
	            "Painting",
	            "Pointilism",
	            "Psychedelic",
	            "Prehistoric",
	            "Scary",
	            "Surprise",
	            "Surreal",
	            "Underwater"      
        );
        
        comboBox.setItems(items);

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
        
        Text styleText = new Text("First, enter a style:");
        styleText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 16));
        centeredGrid.add(styleText, 25, 12);
        centeredGrid.add(comboBox, 25, 13);
        
        Text thenText = new Text("Then...");
        thenText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 18));
        centeredGrid.add(thenText, 25, 14);
        
        Text uploadImageText = new Text("Upload an Image of Your Smile:");
        uploadImageText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 16));
        centeredGrid.add(uploadImageText, 25, 15);
        
        Button uploadButton = new Button("Upload Image");
        centeredGrid.add(uploadButton, 25, 16);

        Text orText = new Text("Or you can...");
        orText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 18));
        centeredGrid.add(orText, 25, 17);
        
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
        centeredGrid.add(promptText, 25, 18);
        centeredGrid.add(promptTextField, 25, 19);

        Button generateImageButton = new Button("Generate Image");
        centeredGrid.add(generateImageButton, 25, 20);
        
        
        uploadButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String style = getPrompt(comboBox.getValue());
                if (style == null || style.isEmpty()) {
                    showAlert("Error", "Please enter a style!");
                    return;
                }
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
                String style = getPrompt(comboBox.getValue());
                if (style == null || style.isEmpty()) {
                    showAlert("Error", "Please enter a style");
                    return;
                }
                try {
                	onGenerateImageButtonClicked(prompt, style);
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
        
        Scene imageScene = new Scene(mainGrid, 850, 850);

        CreateBackButton(mainGrid, sceneArray[1], 0, 0);
        profileGrid.add(centeredGrid, 0, 1);
        mainGrid.add(profileGrid, 0, 1);
        
        sceneArray[3] = imageScene;
        primaryStage.setScene(imageScene);
        primaryStage.show();
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
	
	          profileGrid.add(imageViewUpl, column, row);
	        } catch (Exception e) {
	            e.printStackTrace();
	            System.err.println("Uploaded image is null.");
	        }
	    }
	}

	//set the image to image generation prompt based on user's input style
	public String getPrompt(String style) {
		 String enteredPrompt = "";
		 
		 switch (style) {
	   case "Anime":
	       enteredPrompt = "anime, japanese cartoon, cute, big eyes";
	       break;
	   case "Abstract":
	       enteredPrompt = "abstract, geometric, julie mehretu";
	       break;
	   case "Animated":
	       enteredPrompt = "animated, animation";
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
	   case "Fantasy":
	       enteredPrompt = "fantasy, mystical, dreamy, colorful, wonderland";
	       break;
	   case "Futuristic":
	  	 enteredPrompt = "futuristic, future, realistic, chrome, technology";
	  	 break;
	   case "Medieval":
	  	 enteredPrompt = "medieval times, castles";
	  	 break;
	   case "Nature":
	  	 enteredPrompt = "nature, butterflies, landscape, dreamy";
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
	   case "Pointilism":
	       enteredPrompt = "pointilism, art";
	       break;
	   case "Prehistoric":
	  	 enteredPrompt = "prehistoric, dinosaurs in background, jurassic, t rex, triceratops, brontosaurus";
	  	 break;
	   case "Psychedelic":
	       enteredPrompt = "psychedelic, trippy, colorful";
	       break;
	   case "Scary":
	  	 enteredPrompt = "scary, creepy, unsettling, frightening";
	  	 break;
	   case "Surprise":
	  	 enteredPrompt = "random, weird, funny, 3d geometric objects";
	  	 break;
	   case "Surreal":
	       enteredPrompt = "surreal, realistic, salvador dali, vladimir kush";
	       break;
	   case "Underwater":
	  	 enteredPrompt = "underwater, ocean, sea";
	  	 break;
	   default:
	       enteredPrompt = " "; 
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
	
	    Scene generatedImageScene = new Scene(grid, 850, 850);
	    
	    ProgressIndicator progressIndicator = new ProgressIndicator();
	    progressIndicator.setVisible(false);
	    grid.add(progressIndicator, 0, 2);
	
	    Task<Image> imageGenerationTask = new Task<>() {
	        @Override
	        protected Image call() throws Exception {
	      	  updateProgress(ProgressIndicator.INDETERMINATE_PROGRESS, 1);
		  		if (style == null)
		  		{
		  			showAlert("Error", "You must enter a style!");
		  		}
		  		if (uploadedImageFile != null && !prompt.isEmpty())
		  		{
		  			showAlert("Error", "Please clear prompt before uploading an image.");
		  		}
	      	  	if (isUploaded == false) {
		            return generateImageFromText(prompt, style);
	      	  	} else {
	      	        ByteArrayOutputStream baosResized = new ByteArrayOutputStream();
		      	    Thumbnails.of(uploadedImageFile)
		      	              .forceSize(1024, 1024)
		      	              .toOutputStream(baosResized);
	
		      	    byte[] resizedBytes = baosResized.toByteArray();
		      	    String encodedImage = Base64.getEncoder().encodeToString(resizedBytes);
		      	    
		      	    byte[] imageData = generateImageFromImage(encodedImage, prompt, style);
		      	    if (imageData != null) {
		      	        return new Image(new ByteArrayInputStream(imageData));
		      	    }
	      	  	}
			return null;
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
		
		            Button saveButton = new Button("Save Image");
		            grid.add(saveButton, 0, 3);
		            saveButton.setOnAction(new EventHandler<ActionEvent>() {
		                @Override
		                public void handle(ActionEvent event) {
		                    try {
		                    	saveImage(primaryStage, genImageView.getImage());
		                    } catch (IOException e) {
		                    	showAlert("Error!", "Photo could not be saved at this time.");
		                    	System.err.println("Photo could not be saved.");
		                    }
		                }
		            });
	            });
	        }
	
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
          BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
          ImageIO.write(bImage, "png", file);
          
          saveImageToGallery(bImage);
      }

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
  public static Image generateImageFromText(String text, String style) throws UnirestException {
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
          System.err.println("Failed to generate image: " + response.getStatusText());
          return null;
      }
  }
  
  public byte[] generateImageFromImage(String base64Image, String prompt, String style) {
	    try {
	    	
	    	//convert the base 64 image to a byte array that contains the image data
	        byte[] imageData = Base64.getDecoder().decode(base64Image);
	        String negativePrompt = "bad quality, distorted features, cross eyes, six fingers, four fingers, abnormalities, nsfw, no pupils, multiple heads, detached head, inaccurate, multiple faces, face on neck, unnecessary faces, lazy eye, face on chest, bodily abnormalities, faces without heads";

	        String engine_id = "stable-diffusion-xl-1024-v1-0";
	        //String engine_id = "stable-diffusion-512-v2-1";
	        //String engine_id = "stable-diffusion-v1-5";

	        HttpResponse<JsonNode> response = Unirest.post("https://api.stability.ai/v1/generation/" + engine_id + "/image-to-image")
		              .header("Authorization", "Bearer sk-no6ZZBdGyv8LIVOZi0WGPTlgcQWk8rBzCv8VQKErIIMFGHwY")
		              .field("image_strength", 0.42)
		              .field("init_image_mode", "IMAGE_STRENGTH")
		              .field("init_image", imageData, "image.png")
		              .field("text_prompts[0][text]", prompt + " " + style)
		              .field("text_prompts[0][weight]", 1)
		              .field("text_prompts[1][text]", negativePrompt)
		              .field("text_prompts[1][weight]", -1)
		              .field("cfg_scale", 7)
		              .field("clip_guidance_preset", "FAST_BLUE")
		              .field("sampler", "K_DPM_2_ANCESTRAL")
		              .field("samples", 3)
		              .field("steps", 30)
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
	        e.printStackTrace();
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
      byte[] hashBytes = digest.digest(base64Image.getBytes("UTF-8"));

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
