package application;
	
import java.io.InputStream;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.security.MessageDigest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.util.Base64;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.json.JSONException;
import org.json.JSONObject;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamEvent;
import com.github.sarxos.webcam.WebcamListener;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.body.MultipartBody;

import application.WebcamCapture.VideoFeedTaker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

//TODO: Make code more modular, separate into classes?
public class Main extends Application {
     private static final String dbClassname = "com.mysql.cj.jdbc.Driver";
     private static final String CONNECTION = "jdbc:mysql://127.0.0.1/ArtFacedb";
     private Stage primaryStage = new Stage();

     private final ReentrantLock lock = new ReentrantLock();
     private static final int MAX_RETRIES = 3;  // Adjust as needed
     private static final long RETRY_DELAY_MS = 1000;
     private Scene[] sceneArray = new Scene[2];
  

    private ImageView imageView = new ImageView();
    static Webcam webcam;

    @Override
    public void start(Stage primaryStage) {
        try {
            this.primaryStage = primaryStage;
            createHomePage();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void createHomePage() 
    {
        GridPane gridPane = new GridPane();
        Scene homeScene = new Scene(gridPane, 800, 800);
        homeScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

        Text sceneTitle = new Text("Hello, ArtFace!");
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        gridPane.add(sceneTitle, 0, 0, 2, 1);
        gridPane.setStyle("-fx-background-color: teal;");

        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(25, 25, 25, 25));

        // Button
        Button signUpBtn = new Button("Sign Up");
        signUpBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                createSignUpPage();
            }
        });

        // Button
        Button signInBtn = new Button("Sign In");
        signInBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                createSignInPage();
            }
        });

        gridPane.add(signInBtn, 0, 1, 1, 1);
        gridPane.add(signUpBtn, 1, 1, 1, 1);
        sceneArray[0] = homeScene;
        primaryStage.setTitle("ArtFace Application")
        primaryStage.setTitle("ArtFace");
        primaryStage.setScene(homeScene);
        primaryStage.show();
    }

    //create sign up page for users to make an account
    //TODO: save the username and password into the database for later retrieval
    public void createSignUpPage() 
    {
	GridPane signUpGrid = new GridPane();
	signUpGrid.setStyle("-fx-background-color: yellow;");
	
	signUpGrid.setAlignment(Pos.CENTER);
	signUpGrid.setHgap(10);
	signUpGrid.setVgap(10);
	signUpGrid.setPadding(new Insets(25, 25, 25, 25));

	Label userName = new Label("User Name:");
	signUpGrid.add(userName, 0, 1);
	
	TextField userTextField = new TextField();
	signUpGrid.add(userTextField, 1, 1);

	Label pw = new Label("Password:");
	signUpGrid.add(pw, 0, 2);
	
	PasswordField pwBox = new PasswordField();
	signUpGrid.add(pwBox, 1, 2);

	Label name = new Label("Name:");
	signUpGrid.add(name, 0, 3);
	
	TextField nameField = new TextField();
	signUpGrid.add(nameField, 1, 3);

	Label email = new Label("Email:");
	signUpGrid.add(email, 0, 4);
	
	TextField emailField = new TextField();
	signUpGrid.add(emailField, 1, 4);

	Scene signUpScene = new Scene(signUpGrid, 800, 800);
	signUpScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
	
	CreateBackButton(signUpGrid,sceneArray[0]);
	
	primaryStage.setScene(signUpScene);
	primaryStage.show();
	
	
    }

    //create sign in page for user to log in
    public void createSignInPage()
    {
	GridPane signInGrid = new GridPane();
	signInGrid.setStyle("-fx-background-color: pink;");

    signInGrid.setAlignment(Pos.CENTER);
    signInGrid.setHgap(10);
    signInGrid.setVgap(10);
    signInGrid.setPadding(new Insets(25, 25, 25, 25));

        //Username
	Label user = new Label("User Name:");
	signInGrid.add(user, 0, 1);
	
	TextField userText = new TextField();
	signInGrid.add(userText, 1, 1);
	
	// Password
	Label pass = new Label("Password:");
	signInGrid.add(pass, 0, 2);
	
	PasswordField passwordTF = new PasswordField();
	signInGrid.add(passwordTF, 1, 2);
	
	Button signInBtn2 = new Button("Sign in");
	signInGrid.add(signInBtn2, 1, 4);

	signInBtn2.setOnAction(new EventHandler<ActionEvent>() {
	@Override
	public void handle(ActionEvent event) {
	    String enteredUsername = userText.getText();
	    String enteredPassword = passwordTF.getText();
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
		
		  createImageGenerationPage();		
		}
	
	});
	

     Scene signInScene = new Scene(signInGrid, 800, 800);
     signInScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

     CreateBackButton(signInGrid,sceneArray[0]);
     
     primaryStage.setScene(signInScene);
     primaryStage.show();

    }
    
    //show an error if there is no username or the password is < 5 characters long
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    
    //create the profile page
    public void createProfilePage()
    {
    	GridPane profileGrid = new GridPane();
    	profileGrid.setStyle("-fx-background-color: lightyellow;");
    	profileGrid.setHgap(10);
    	profileGrid.setVgap(10);
    	profileGrid.setPadding(new Insets(25, 25, 25, 25));
    	
    	Label profileLabel = new Label("My Profile");
    	profileLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 30));
    	profileLabel.setAlignment(Pos.CENTER);
    	profileGrid.add(profileLabel, 7, 0);
    	
    	Label spacerLabel = new Label();
    	spacerLabel.setPadding(new Insets(80, 80, 80, 80));
    	profileGrid.add(spacerLabel, 0, 1);
    	
    	Button uploadProfileBtn = new Button("Upload Profile Photo");
    	profileGrid.add(uploadProfileBtn, 0,2);
    	
        Scene profileScene = new Scene(profileGrid, 700, 800);
        profileScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        
        uploadProfileBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                	FileChooser fileChooser = new FileChooser();
	
	                fileChooser.setTitle("Select a File to Upload");
	                
	                File uploadedImage = fileChooser.showOpenDialog(primaryStage);
	                
	                if (uploadedImage != null) {
	                    try {
	                        Image image = new Image(uploadedImage.toURI().toString());
	                    	ImageView imageViewUpl = new ImageView(image);
	                    	
	                    	imageViewUpl.setFitWidth(200);
	                    	imageViewUpl.setFitHeight(200);
	                    	imageViewUpl.setPreserveRatio(true);
	                    	profileGrid.add(imageViewUpl, 0, 1);
	                    	
	                    } catch (Exception e) {
	                        e.printStackTrace();
	                    }
	            }
           }
        });
        CreateBackButton(profileGrid,sceneArray[1]);

        primaryStage.setScene(profileScene);
        primaryStage.show();

    }


    //create the page to generate an image
    public void createImageGenerationPage() {
        GridPane mainGrid = new GridPane();
        mainGrid.setStyle("-fx-background-color: lightblue;");
        mainGrid.setPadding(new Insets(25, 25, 25, 25));
        mainGrid.setAlignment(Pos.TOP_LEFT);

        Button profileButton = new Button("Go to Profile");
        mainGrid.add(profileButton, 0, 0);
        profileButton.setAlignment(Pos.TOP_LEFT);
        profileButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                createProfilePage();
            }
        });

        // Centered content grid
        GridPane centeredGrid = new GridPane();
        centeredGrid.setAlignment(Pos.CENTER);
        centeredGrid.setHgap(10);
        centeredGrid.setVgap(10);

        Text uploadText = new Text("Create an AI version of you");
        uploadText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        centeredGrid.add(uploadText, 25, 15);

        Button uploadButton = new Button("Upload Image");
        centeredGrid.add(uploadButton, 25, 16);
        
        Text promptText = new Text("...Or create someone new!");
        promptText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        centeredGrid.add(promptText, 25, 17);

        Label promptLabel = new Label("Enter a Prompt:");
        TextField promptTextField = new TextField();
        centeredGrid.add(promptLabel, 25, 18);
        centeredGrid.add(promptTextField, 25, 19);

        Button generateImageButton = new Button("Generate Image");
        centeredGrid.add(generateImageButton, 25, 20);

        generateImageButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String prompt = promptTextField.getText();
                if (prompt.length() < 1)
                {
                	showAlert("Error", "Please enter a prompt");
                	return;
                }
                else {
                onGenerateImageButtonClicked(prompt);
                }
            }
        });
        
        uploadButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                	FileChooser fileChooser = new FileChooser();
	
	                fileChooser.setTitle("Select a File to Upload");
	                
	                File uploadedImage = fileChooser.showOpenDialog(primaryStage);
	                
	                if (uploadedImage != null) {
	                    try {
	                    	String base64Image = encodeToBase64(uploadedImage.toPath());
	                    	String hash = generateHash(base64Image);
	                        System.out.println("Base64 String:\n" + hash);
	                        onGenerateImageButtonClicked(hash);
	                    } catch (Exception e) {
	                        e.printStackTrace();
	                    }
	            }
           }
        });
        
        profileButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                createProfilePage();
            }
        });
        
        mainGrid.add(centeredGrid, 0, 1);


        Scene imageScene = new Scene(grid, 800, 800);
        
        sceneArray[1] = imageScene;
        
        CreateBackButton(grid,sceneArray[0]);
        

        Scene imageScene = new Scene(mainGrid, 800, 800);
        primaryStage.setScene(imageScene);
        primaryStage.show();
  }

  public static String encodeToBase64(Path imagePath) throws Exception {
      //read image bytes
      byte[] imageBytes = Files.readAllBytes(imagePath);
      
      //encode bytes to Base64
      String encodedString = Base64.getEncoder().encodeToString(imageBytes);
      
      return encodedString;
  }
  
  public static String generateHash(String base64Image) throws Exception {
      MessageDigest digest = MessageDigest.getInstance("SHA-1");
      byte[] hashBytes = digest.digest(base64Image.getBytes("UTF-8"));

      StringBuilder sb = new StringBuilder();
      for (byte b : hashBytes) {
          sb.append(String.format("%02x", b));
      }
      
      return sb.toString();
  }

  //when the generate image button is clicked, a new scene will pop up with the generated image
  public void onGenerateImageButtonClicked(String prompt) {
	    GridPane grid = new GridPane();
	    grid.setAlignment(Pos.CENTER);
	    grid.setHgap(10);
	    grid.setVgap(10);
	    grid.setPadding(new Insets(25, 25, 25, 25));

	    StackPane stack = new StackPane();
	    grid.add(stack, 0, 1, 2, 1); 

	    ProgressIndicator progressIndicator = new ProgressIndicator();
	    stack.getChildren().add(progressIndicator);
	    progressIndicator.setVisible(true);

	    Task<String> imageGenerationTask = new Task<String>() {
	        @Override
	        protected String call() throws Exception {
	            return initiateImageGeneration(prompt, "face").get(); 
	        }
	    };

	    imageGenerationTask.setOnSucceeded(e -> {
	        progressIndicator.setVisible(false);
	        String hash = imageGenerationTask.getValue();
	        if(hash != null) {
	            try {
					getImage(hash);
					stack.getChildren().add(imageView);
					
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
	        }
	    });

	    imageGenerationTask.setOnFailed(e -> {
	        progressIndicator.setVisible(false);
	        Throwable exception = imageGenerationTask.getException();
	        exception.printStackTrace();
	        // Handle the failure if needed.
	    });

	   
	    new Thread(imageGenerationTask).start();

	    Scene generatedImageScene = new Scene(grid, 800, 800);
	    Platform.runLater(() -> {
	    	
	    	CreateBackButton(grid,sceneArray[1]);//during loading
	    	
	        primaryStage.setScene(generatedImageScene);
	        primaryStage.show();
	    });
	}

//gets image based on given hash
  public void getImage(String hash) throws InterruptedException {
	    Thread.sleep(3000);
	    GridPane imageGrid = new GridPane();
	    imageGrid.setAlignment(Pos.CENTER);
	    imageGrid.setHgap(10);
	    imageGrid.setVgap(10);
	    imageGrid.setPadding(new Insets(25, 25, 25, 25));
	    
        ImageView imageView2 = new ImageView();
	    
        try {
            HttpResponse<InputStream> response = Unirest.get("https://arimagesynthesizer.p.rapidapi.com/get")
                    .header("X-RapidAPI-Key", "8b2bd64aa5msh34f679538ef2433p1e4a2djsn927a54490a26")
                    .header("X-RapidAPI-Host", "arimagesynthesizer.p.rapidapi.com")
                    .queryString("hash", hash)
                    .asBinary();

            int status = response.getStatus();

            if (status == 200) {
	    	    System.out.println("IN GET IMAGE");
                Image image = new Image(response.getBody());
                Platform.runLater(() -> {
                	imageView2.setImage(image);
                    imageGrid.add(imageView2, 0, 0);
                    imageView2.setFitHeight(500);
                    imageView2.setFitWidth(500);
                    Scene getImageScene = new Scene(imageGrid, 800, 800);

                    primaryStage.setScene(getImageScene);
                    primaryStage.show();
                });
                // exit the loop once the image is retrieved
            } else if (status == 204) { 
                System.out.println("Image still in generation process.");
            } else {
                System.err.println("Failed to get the image with status: " + status + " - " + response.getStatusText());
            }
            response.getBody().close();
            
        } catch (UnirestException e) {
            e.printStackTrace();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
	}

//initiates image generation
public CompletableFuture<String> initiateImageGeneration(String prompt, String style) {
	    System.out.println("INITIATE IMAGE GENERATION");
	    return CompletableFuture.supplyAsync(() -> {
	        try {
	            //POST request to generate the image
	            MultipartBody request = Unirest.post("https://arimagesynthesizer.p.rapidapi.com/generate")
	                    .header("X-RapidAPI-Key", "8b2bd64aa5msh34f679538ef2433p1e4a2djsn927a54490a26")
	                    .header("X-RapidAPI-Host", "arimagesynthesizer.p.rapidapi.com")
	                    .field("prompt", prompt)
	                    .field("style", style);

	            HttpResponse<String> response = request.asString();
	            int status = response.getStatus();
	            String hash = extractHash(response.getBody());

	            if(hash != null) {
	                try {
	                    Thread.sleep(5000);
	                } catch (InterruptedException e) {
	                    Thread.currentThread().interrupt();
	                }
	                getImage(hash);
	            } else {
	                System.err.println("Unexpected response status: " + status);
	                System.err.println("Response body: " + response.getBody());
	            }
	            return hash;
	        } catch (UnirestException e) {
	            e.printStackTrace();
	            return null;
	        } catch (InterruptedException e) {
				e.printStackTrace();
			}
	        return null;
	    });
	}

  //extracts the hash of the image
  private synchronized String extractHash(String body) {
      try {
          JSONObject jsonResponse = new JSONObject(body);
          String hash = jsonResponse.getString("hash");
          hash.replace(" ", "");
          System.out.println("Extracted hash: " + hash);
          return hash;
      } catch (JSONException e) {
          e.printStackTrace();
          System.err.println("Failed to parse hash from response body: " + body);
          return null;
      }
  }
  
  private void CreateBackButton(GridPane grid,Scene backScene){
	  Button backButton = new Button("Back");
	  	grid.add(backButton, 0,10);
  		
  		
  		 backButton.setOnAction(new EventHandler<ActionEvent>() {
             @Override
             public void handle(ActionEvent event) {
            	primaryStage.setScene(backScene);
     	        primaryStage.show();
             }
         });
  		
  }
  
  //main method
  public static void main(String[] args) throws ClassNotFoundException, SQLException {

	launch(args);
/*
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
    Connection c = DriverManager.getConnection(CONNECTION,p);
    System.out.println("It works");

    c.close();*/
  }
}