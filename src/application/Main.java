package application;
	
import java.io.InputStream;

import javafx.embed.swing.SwingFXUtils;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
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
import java.util.Base64;
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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

//TODO: Make code more modular, separate into classes?
public class Main extends Application {
     private static final String dbClassname = "com.mysql.cj.jdbc.Driver";
     private static final String CONNECTION = "jdbc:mysql://127.0.0.1/ArtFaceDB";
     private Stage primaryStage = new Stage();
     private Scene[] sceneArray = new Scene[4];
     private ImageView imageView = new ImageView();
     private static Statement stmt;
     static Webcam webcam;
     static Connection c;
     Database data = new Database();
     public Boolean userCreated = false;
    @Override
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
        Scene homeScene = new Scene(gridPane, 800, 800);

        Text sceneTitle = new Text("Hello, ArtFace!");
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        gridPane.add(sceneTitle, 0, 0, 2, 1);
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

        gridPane.add(signInBtn, 0, 1, 1, 1);
        gridPane.add(signUpBtn, 1, 1, 1, 1);
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
	
		Scene signUpScene = new Scene(mainGrid, 800, 800);

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
			try {
				userCreated = data.newUser(c, enteredUsername, enteredPassword);
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
	    
	    Text signInTitle = new Text("Sign in");
	    signInTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 30));
	    signInGrid.add(signInTitle, 0, 1);
	
	    Label userLabel = new Label("User Name:");
	    TextField userTextField = new TextField();
	    signInGrid.add(userLabel, 0, 2);
	    signInGrid.add(userTextField, 1, 2);
	
	    Label passLabel = new Label("Password:");
	    PasswordField passwordField = new PasswordField();
	    signInGrid.add(passLabel, 0, 3);
	    signInGrid.add(passwordField, 1, 3);
	
	    // Sign-in button
	    Button signInBtn2 = new Button("Sign in");
	    signInGrid.add(signInBtn2, 1, 4);
	    Scene signInScene = new Scene(mainGrid, 800, 800);
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
			String sqlInput2 = "SELECT * FROM USERS WHERE username = '"+ enteredUsername + "' AND password = '" + enteredPassword + "'";
			ResultSet rs;
			try {
				rs = stmt.executeQuery(sqlInput2);
				int counter = 0;
				while (rs.next()) {//should only be one
					counter++;
				}
				//successful login
				if(counter >= 1)
				{
					System.out.println("Successfull login");
					createImageGenerationPage(signInScene);	
				}
				else
				{
					System.out.println("User not found");
					showAlert("Could not sign in", "User not found!");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		//-----------------------------------------------	
		}
		});
		
	    mainGrid.add(signInGrid, 20, 27);
	    
	    CreateBackButton(mainGrid, sceneArray[0], 0, 0);
	
	    primaryStage.setScene(signInScene);
	    primaryStage.show();
}

    
    //show an error with given title and message
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
    	ImageView defaultImView = new ImageView(defaultPic);
    	profileGrid.add(defaultImView, 0, 1);
    	defaultImView.setFitWidth(200);
    	defaultImView.setFitHeight(200);
    	defaultImView.setPreserveRatio(true);

    	Label profileLabel = new Label("My Profile");
    	profileLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 30));
    	profileLabel.setAlignment(Pos.CENTER_RIGHT);
    	profileGrid.add(profileLabel, 1, 0);
    	
    	Button uploadProfileBtn = new Button("Upload Profile Photo");
    	profileGrid.add(uploadProfileBtn, 0,2);
    	
        Scene profileScene = new Scene(profileGrid, 800, 800);
        
        uploadProfileBtn.setAlignment(Pos.TOP_CENTER);

        uploadProfileBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                	FileChooser fileChooser = new FileChooser();
	
	                fileChooser.setTitle("Select a File to Upload");
	                
	                //prompt user to upload photo
	                File uploadedImage = fileChooser.showOpenDialog(primaryStage);
	                
	                if (uploadedImage != null) {
	                    try {
	                    	//replace the default image with the new profile photo
	                    	defaultImView.setVisible(false);
	                        Image image = new Image(uploadedImage.toURI().toString());
	                    	ImageView imageViewUpl = new ImageView(image);

	                    	imageViewUpl.setFitWidth(200);
	                    	imageViewUpl.setFitHeight(200);
	                    	imageViewUpl.setPreserveRatio(true);
	                    	
	                    	profileGrid.add(imageViewUpl, 0, 1);
	                    } catch (Exception e) {
	                        e.printStackTrace();
	                        System.err.println("Uploaded image is null.");
	                    }
	            }
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
    
    //create the gallery
    public ScrollPane createGallery() {
        FlowPane galleryFlowPane = new FlowPane();
        galleryFlowPane.setPadding(new Insets(5, 5, 5, 5));
        galleryFlowPane.setVgap(4);
        galleryFlowPane.setHgap(4);
        galleryFlowPane.setStyle("-fx-background-color: cornflowerblue;");
        
        //creates the gallery of image views from the gallery folder
        try (Stream<Path> paths = Files.walk(Paths.get("/CS370/src/application/gallery"))) {
            paths.filter(Files::isRegularFile).forEach((Path path) -> {
                File file = path.toFile();
                if (file != null) {
                    Image galleryImage = new Image(file.toURI().toString(), 100, 0, true, true);
                    ImageView galleryImageView = new ImageView(galleryImage);
                    galleryImageView.setFitWidth(150);
                    galleryImageView.setPreserveRatio(true);

                    galleryFlowPane.getChildren().add(galleryImageView);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Could not find directory.");
        }


        //wrap the flowpane in a scrollpane
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(galleryFlowPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        return scrollPane;
    }


    //create the page to generate an image
    public void createImageGenerationPage(Scene scene) {
    	
    	//main grid to hold back button
    	GridPane mainGrid = new GridPane();
        mainGrid.setStyle("-fx-background-color: plum;");
        mainGrid.setPadding(new Insets(15, 15, 15, 15));
        
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
        
        
        CreateBackButton(mainGrid, sceneArray[1], 0, 0);
        profileGrid.add(centeredGrid, 0, 1);
        mainGrid.add(profileGrid, 0, 1);

        Scene imageScene = new Scene(mainGrid, 800, 800);
        sceneArray[3] = imageScene;
        primaryStage.setScene(imageScene);
        primaryStage.show();
  }

  public void saveImage(Stage stage, Image image) throws IOException {
	  
      FileChooser fileChooser = new FileChooser();
      FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
      
      fileChooser.getExtensionFilters().add(extFilter);
      
      File file = fileChooser.showSaveDialog(stage);

      if (file != null) {
          BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
          ImageIO.write(bImage, "png", file);
      }
		
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
				stack.getChildren().add(imageView);
	        }
	    });

	    imageGenerationTask.setOnFailed(e -> {
	        progressIndicator.setVisible(false);
	        Throwable exception = imageGenerationTask.getException();
	        exception.printStackTrace();
	    });

	   
	    new Thread(imageGenerationTask).start();

	    Scene generatedImageScene = new Scene(grid, 800, 800);
	    Platform.runLater(() -> {
	        primaryStage.setScene(generatedImageScene);
	        primaryStage.show();
	    });
	}

//gets image based on given hash
  public void getImage(String hash) throws InterruptedException {
	    Thread.sleep(3000);
	    GridPane imageGrid = new GridPane();
	    imageGrid.setStyle("-fx-background-color: lavenderblush;");
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


  //generates image based off of entered image
  public String initiateUploadedImageGeneration(JSONObject payload) throws Exception {
		 
	  String apiKey = "b59de626-ecce-4598-a190-e0944bf78658";
            if (status == 200) {
	    	    System.out.println("IN GET IMAGE");
                Image image = new Image(response.getBody());
                Platform.runLater(() -> {
                	
                	imageView2.setImage(image);
                	
                    WritableImage writableImage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
                    imageView2.snapshot(null, writableImage);
                    
                    File outputFile = new File("/CS370/src/application/gallery/" + image.hashCode() + ".png");
                    try {
                        ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", outputFile);
                        System.out.println("Image saved: " + outputFile.getAbsolutePath());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }           
                    
                	
                    imageGrid.add(imageView2, 1, 1);
                    imageView2.setFitHeight(500);
                    imageView2.setFitWidth(500);
                    
                    //CreateBackButton(imageGrid, sceneArray[3], 0, 0);
                    Button newImgBtn = new Button("Generate new Image");
                    imageGrid.add(newImgBtn, 1, 0);
                    newImgBtn.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                        	primaryStage.setScene(sceneArray[3]);
                        	primaryStage.show();
                        }
                    });
                    
                    Button saveButton = new Button("Save Image");
                    imageGrid.add(saveButton, 1, 2);
                    saveButton.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            try {
            					saveImage(primaryStage, imageView2.getImage());
            				} catch (IOException e) {
            					showAlert("Error!", "Photo could not be saved at this time.");
            					System.err.println("Photo could not be saved.");
            				}
                        }
                    });
                    
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

	    try {
	        HttpResponse<JsonNode> response = Unirest.post("https://api.novita.ai/v2/img2img")
	                .header("Authorization", "Bearer " + apiKey)
	                .header("Content-Type", "application/json")
	                .body(payload)
	                .asJson();
	        
	        if (response.getStatus() == 200) {
	        	
	            JSONObject responseBody = response.getBody().getObject();
	            System.out.println(responseBody.toString());
       
        } catch (UnirestException e) {
            e.printStackTrace();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
	}

  //ADD INIT_IMAGE TO BASE IT OFF OF THE PHOTO, MAY NEED TO BE IN GET IMAGE
//initiates image generation
public CompletableFuture<String> initiateImageGeneration(String prompt, String style) {
	    System.out.println("INITIATE IMAGE GENERATION");
	    GridPane grid = new GridPane();
	    ImageView imgView = new ImageView();
	    
	    return CompletableFuture.supplyAsync(() -> {
	        try {
	            //POST request to generate the image
	            MultipartBody request = Unirest.post("https://arimagesynthesizer.p.rapidapi.com/generate")
	                    .header("X-RapidAPI-Key", "8b2bd64aa5msh34f679538ef2433p1e4a2djsn927a54490a26")
	                    .header("X-RapidAPI-Host", "arimagesynthesizer.p.rapidapi.com")
	                    .field("prompt", prompt)
	                    .field("style", style);
	                   // .field("init_image", image);

	            HttpResponse<String> response = request.asString();
	            int status = response.getStatus();
	            String hash = extractHash(response.getBody());

	            if(hash != null) {
	                try {
	                    Thread.sleep(5000);
	                } catch (InterruptedException e) {
	                    Thread.currentThread().interrupt();
	                }
		           /* Platform.runLater(() -> {
		                imgView.setImage(image);
		                grid.add(imgView, 0, 0);
		                
		                Scene initImageScene = new Scene(grid, 800, 800);
	
	                    primaryStage.setScene(initImageScene);
	                    primaryStage.show();
	                });*/
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
				// TODO Auto-generated catch block
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
  
//back button
  private void CreateBackButton(GridPane grid, Scene backScene, int colIndex, int rowIndex) {
	    Button backButton = new Button("Back");
	    
	    backButton.setOnAction(event -> primaryStage.setScene(backScene));
	    
	    grid.add(backButton, colIndex, rowIndex);
	    GridPane.setHalignment(backButton, HPos.LEFT);
	    GridPane.setValignment(backButton, VPos.TOP);
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