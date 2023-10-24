package application;
	
import java.io.InputStream;

import java.sql.Connection;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Optional;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.json.JSONException;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.body.MultipartBody;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.application.Application;
import javafx.application.Platform;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
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
  
    private ImageView imageView = new ImageView();

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
        Scene homeScene = new Scene(gridPane, 700, 800);
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

        primaryStage.setTitle("ArtFace Application");
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

	Scene signUpScene = new Scene(signUpGrid, 700, 800);
	signUpScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
	
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
	
	Button profileButton = new Button("Go to Profile");
	signInGrid.add(profileButton, 0, 0);
	profileButton.setAlignment(Pos.TOP_RIGHT);

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

        Scene signInScene = new Scene(signInGrid, 700, 800);
        signInScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

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

    //create the page to generate an image
    public void createImageGenerationPage() {
        GridPane grid = new GridPane();
        grid.setStyle("-fx-background-color: lightblue;");
        
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        
        Button uploadButton = new Button("Upload Image");
        uploadButton.setAlignment(Pos.TOP_CENTER);
        
        Label promptLabel = new Label("Enter a Prompt:");
        TextField promptTextField = new TextField();

        Button generateImageButton = new Button("Generate Image");

        generateImageButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String prompt = promptTextField.getText();
                onGenerateImageButtonClicked(prompt);
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
                        String base64String = convertImageToBase64(uploadedImage);
                        System.out.println("Base64 String:\n" + base64String);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
            }
           }
        });
        
        grid.add(uploadButton, 1, 0);
        grid.add(promptLabel, 1, 1);
        grid.add(promptTextField, 1, 2);
        grid.add(generateImageButton, 1, 3);

        Scene imageScene = new Scene(grid, 800, 800);
        primaryStage.setScene(imageScene);
        primaryStage.show();
    }

  private String convertImageToBase64(File imageFile) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(imageFile)) {
            byte[] imageData = new byte[(int) imageFile.length()];
            fileInputStream.read(imageData);
            return Base64.getEncoder().encodeToString(imageData);
        }
   }
    
  //when the generate image button is clicked, a new scene will pop up with the generated image
  public void onGenerateImageButtonClicked(String prompt) {
      GridPane grid = new GridPane();
      grid.setAlignment(Pos.CENTER);
      grid.setHgap(10);
      grid.setVgap(10);
      grid.setPadding(new Insets(25, 25, 25, 25));

      initiateImageGeneration(prompt, "123").thenAccept(hash -> {
          if(hash != null) {
              retrieveAndDisplayImage(hash);
          }
      });

      grid.add(imageView, 0, 2, 2, 1);
      Scene generatedImageScene = new Scene(grid, 800, 800);
      Platform.runLater(() -> {
          primaryStage.setScene(generatedImageScene);
          primaryStage.show();
      });
  }

  //retrieve the image from the API and display it
  public void retrieveAndDisplayImage(String hash) throws CancellationException {
      final int MAX_ATTEMPTS = 10;
      ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

      AtomicInteger attempts = new AtomicInteger(0);

      scheduler.scheduleAtFixedRate(() -> {
          try {
              int currentAttempt = attempts.incrementAndGet();
              if (currentAttempt > MAX_ATTEMPTS) {
                  System.out.println("Max attempts reached.");
                  scheduler.shutdown(); 
                  return;
              }

              HttpResponse<InputStream> response = Unirest.get("https://arimagesynthesizer.p.rapidapi.com/get")
                      .header("X-RapidAPI-Key", "8b2bd64aa5msh34f679538ef2433p1e4a2djsn927a54490a26")
                      .header("X-RapidAPI-Host", "arimagesynthesizer.p.rapidapi.com")
                      .queryString("hash", hash)
                      .asBinary();

              int status = response.getStatus();

              if (status == 200) {
                  Image image = new Image(response.getBody());
                  Platform.runLater(() -> {
                      imageView.setImage(image);
                  });
                  System.out.println("Image retrieved successfully.");
                  scheduler.shutdown();
              }
              else if (status == 204 || status == 202) {
                  System.out.println("Image in queue.");
              } else {
                  System.err.println("Failed to retrieve the image: " + response.getStatusText());
              }

              response.getBody().close();

          } catch (UnirestException | IOException e) {
              e.printStackTrace();
          }

      }, 0, 1, TimeUnit.SECONDS);
      
  }
  
  //gets the image from the API
  public void getImage(String hash) {
	    int retryCount = 0;
	    while (retryCount < MAX_RETRIES) {
	        try {
	            HttpResponse<InputStream> response = Unirest.get("https://arimagesynthesizer.p.rapidapi.com/get")
	                    .header("X-RapidAPI-Key", "8b2bd64aa5msh34f679538ef2433p1e4a2djsn927a54490a26")
	                    .header("X-RapidAPI-Host", "arimagesynthesizer.p.rapidapi.com")
	                    .queryString("hash", hash)
	                    .asBinary();

	            if (response.getStatus() == 200) {
	                Image image = new Image(response.getBody());
	                Platform.runLater(() -> {
	                    imageView.setImage(image);
	                });
	                return;  // Successful, exit the method
	            } else {
	                System.err.println("Failed to get the image with status: " + response.getStatusText());
	                retryCount++;
	                if (retryCount < MAX_RETRIES) {
	                    Thread.sleep(RETRY_DELAY_MS);
	                }
	            }
	            response.getBody().close();

	        } catch (UnirestException e) {
	            e.printStackTrace();
	            retryCount++;
	            if (retryCount < MAX_RETRIES) {
	                try {
	                    Thread.sleep(RETRY_DELAY_MS);
	                } catch (InterruptedException ie) {
	                    Thread.currentThread().interrupt();
	                }
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        } catch (InterruptedException e) {
				e.printStackTrace();
			}
	    }
	    System.err.println("Exceeded maximum retries for hash: " + hash);
	}


        //iniate image generation
	public CompletableFuture<String> initiateImageGeneration(String prompt, String id) {
	   System.out.println("INITIATE IMAGE GENERATION");
	   return CompletableFuture.supplyAsync(() -> {
	       try {
	           //POST request to generate the image
	           MultipartBody request = Unirest.post("https://arimagesynthesizer.p.rapidapi.com/generate")
	                   .header("X-RapidAPI-Key", "8b2bd64aa5msh34f679538ef2433p1e4a2djsn927a54490a26")
	                   .header("X-RapidAPI-Host", "arimagesynthesizer.p.rapidapi.com")
	                   .field("prompt", prompt)
	                   .field("id", id); // include the ID field
	
	           HttpResponse<String> response = request.asString();
	
	           int status = response.getStatus();
	           String hash = extractHash(response.getBody());
	
	           if(hash != null) {
	               // Introduce a delay before attempting to fetch the image
	               try {
	                   Thread.sleep(5000);
	               } catch (InterruptedException e) {
	                   // Handle interruption
	                   Thread.currentThread().interrupt();
	               }
	
	               getImage(hash);
	               return hash;
	           } else {
	               System.err.println("Unexpected response status: " + status);
	               System.err.println("Response body: (initiateImageGeneration())" + response.getBody());
	               return null;
	           }
	       } catch (UnirestException e) {
	           e.printStackTrace();
	           return null;
	       }
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
  
  //main method
  public static void main(String[] args) throws ClassNotFoundException, SQLException{

    launch(args);
/*
    //panacea123
    Scanner reader = new Scanner(System.in); //for user input
    System.out.println("Enter Sql Pasword: ");
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
