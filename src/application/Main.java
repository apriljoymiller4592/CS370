package application;
	
import java.io.InputStream;
import java.sql.Connection;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

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
import javafx.stage.Stage;
import javafx.scene.Scene;
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


public class Main extends Application {
	private static final String dbClassname = "com.mysql.cj.jdbc.Driver";
	private static final String CONNECTION = "jdbc:mysql://127.0.0.1/ArtFacedb";
	private Stage primaryStage = new Stage();
    private ImageView imageView = new ImageView();

    @Override
    public void start(Stage primaryStage) {
        try {
            this.primaryStage = primaryStage; // Store the primary stage for later use
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

        primaryStage.setTitle("ArtFace Application"); // Set a title for your application
        primaryStage.setScene(homeScene);
        primaryStage.show();
    }


    public void createSignUpPage() 
    {
        GridPane signUpGrid = new GridPane();

		signUpGrid.setAlignment(Pos.CENTER);
		signUpGrid.setHgap(10);
		signUpGrid.setVgap(10);
		signUpGrid.setPadding(new Insets(25, 25, 25, 25));

		// Username
		Label userName = new Label("User Name:");
		signUpGrid.add(userName, 0, 1);

		TextField userTextField = new TextField();
		signUpGrid.add(userTextField, 1, 1);

		// Password
		Label pw = new Label("Password:");
		signUpGrid.add(pw, 0, 2);

		PasswordField pwBox = new PasswordField();
		signUpGrid.add(pwBox, 1, 2);

		// Name
		Label name = new Label("Name:");
		signUpGrid.add(name, 0, 3);

		TextField nameField = new TextField();
		signUpGrid.add(nameField, 1, 3);

		// Email
		Label email = new Label("Email:");
		signUpGrid.add(email, 0, 4);

		TextField emailField = new TextField();
		signUpGrid.add(emailField, 1, 4);

		// Create a new scene for the sign-up page and set it to the stage
		Scene signUpScene = new Scene(signUpGrid, 700, 800);
		signUpScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

		primaryStage.setScene(signUpScene);
		primaryStage.show();
    }
    
    public void createSignInPage()
    {
    	GridPane signInGrid = new GridPane();
    	
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
        Label password = new Label("Password:");
        signInGrid.add(password, 0, 2);
        
		PasswordField passwordBox = new PasswordField();
		signInGrid.add(passwordBox, 1, 2);

		Button genBtn = new Button("Generate");
		signInGrid.add(genBtn, 1, 4);
		
		genBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				createImageGenerationPage();
			}
			
		});
        
        Scene signInScene = new Scene(signInGrid, 700, 800);
        signInScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

        primaryStage.setScene(signInScene);
        primaryStage.show();
        
    }
    
    public void createImageGenerationPage() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        try {
            FileInputStream inputstream = new FileInputStream("/Users/aprilmiller/Documents/GitHub/CS370/Hi/src/application/momma.jpg");
            Image image = new Image(inputstream);
            imageView.setImage(image);
            imageView.setFitHeight(400);
            imageView.setFitWidth(400);
        } catch (SecurityException e) {
            e.printStackTrace();
            System.err.println("Security exception: Operation not permitted");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.err.println("File not found: " + e.getMessage());
        }

        Label promptLabel = new Label("Enter a Prompt:");
        TextField promptTextField = new TextField();

        Button generateImageButton = new Button("Generate Image");
        generateImageButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                String prompt = promptTextField.getText();
                onGenerateImageButtonClicked(prompt);
            }
        });

        grid.add(promptLabel, 0, 0);
        grid.add(promptTextField, 1, 0);
        grid.add(generateImageButton, 1, 1);
        grid.add(imageView, 0, 2, 2, 1);

        Scene imageScene = new Scene(grid, 800, 800);
        primaryStage.setScene(imageScene);
        primaryStage.show();
    }

    
	 public void onGenerateImageButtonClicked(String prompt) {
		    GridPane grid = new GridPane();
		    grid.setAlignment(Pos.CENTER);
		    grid.setHgap(10);
		    grid.setVgap(10);
		    grid.setPadding(new Insets(25, 25, 25, 25));
		         
		    Button generateImageButton = new Button("Generate Image");
		    
		    // Create label and text field
		    Label promptLabel = new Label("Enter a Prompt:");
		    TextField promptTextField = new TextField();

		    // If you want to preset the text field with the previous prompt
		    promptTextField.setText(prompt);

		    generateImageButton.setOnAction(e -> {
		        String newPrompt = promptTextField.getText();
		        System.out.println("GENERATE IMAGE BUTTON CLICKED");
		        // Initiating image generation and retrieval could go here
		        CompletableFuture<String> hashFuture = 
		            (CompletableFuture<String>) initiateImageGeneration(newPrompt, Optional.empty());
		        
		        // Retrieve Image
		        hashFuture.thenCompose(hash -> getImageWithRetry(hash, 3, 3000)) // 3 retries, 3000ms delay
		            .thenAcceptAsync(imageStream -> updateUIWithImage(imageStream), Platform::runLater)
		            .exceptionally(e2 -> {
		                e2.printStackTrace();
		                return null;
		            });
		    });

		    // Add to grid
		    grid.add(promptLabel, 0, 0);
		    grid.add(promptTextField, 1, 0);
		    grid.add(generateImageButton, 1, 1);
		    grid.add(imageView, 0, 2, 2, 1);

		    // Create new scene and show
		    Scene generatedImageScene = new Scene(grid, 800, 800);
		    Platform.runLater(() -> {
		        primaryStage.setScene(generatedImageScene);
		        primaryStage.show();
		    });
		
	 
	}

//TODO: fix this so ID is not optional
public CompletableFuture<String> initiateImageGeneration(String prompt, Optional<String> idOptional) {
	 	System.out.println("INITIATE IMAGE GENERATION");
	    return CompletableFuture.supplyAsync(() -> {
	        try {
	            // Prepare an HTTP POST request to generate the image
	            MultipartBody request = Unirest.post("https://arimagesynthesizer.p.rapidapi.com/generate")
	                    .header("X-RapidAPI-Key", "8b2bd64aa5msh34f679538ef2433p1e4a2djsn927a54490a26")
	                    .header("X-RapidAPI-Host", "arimagesynthesizer.p.rapidapi.com")
	                    .field("prompt", prompt);
	            
	            // Add ID field if it's provided
	            idOptional.ifPresent(id -> request.field("id", id));

	            HttpResponse<String> response = request.asString();

	            int status = response.getStatus();
	            String hash = extractHash(response.getBody());
	            
	            if(hash != null) {
	                return hash;
	            }
	            else {
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

private String extractHash(String body) {
       System.out.println("EXTRACT HASH | HASH: ");
    try {
        JSONObject jsonResponse = new JSONObject(body);
        String hash = jsonResponse.getString("hash");
        System.out.println("Extracted hash: " + hash);
        return hash;
    } catch (JSONException e) {
        e.printStackTrace();
        System.err.println("Failed to parse hash from response body: " + body);
        return null;
    }
}


private void updateUIWithImage(InputStream imageStream) {
	    Platform.runLater(() -> {
	        try {
	            if(imageStream != null) {
	                Image image = new Image(imageStream);
	                if(image != null && image != null) {
	                    imageView.setImage(image);
	                } else {
	                    System.err.println("Image or ImageView is null");
	                }
	            } else {
	                System.err.println("Image stream is null");
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    });
	}


private CompletableFuture<InputStream> getImageWithRetry(String hash, int maxRetries, long delayMs) {
	    return CompletableFuture.supplyAsync(() -> {
	        for (int i = 0; i < maxRetries; i++) {
	            try {
	                InputStream imageStream = getImage(hash);
	                System.out.println("RETRY: " + hash);
	                if (imageStream != null) {
	                    return imageStream;
	                }
	                // Log an error or inform the user about the retry
	                System.err.println("Retry " + (i+1) + ": Image retrieval failed for hash " + hash);
	                // Exponential backoff: wait for 2^i * delayMs before the next try
	                Thread.sleep((long) Math.pow(2, i) * delayMs);
	            } catch (InterruptedException e) {
	                Thread.currentThread().interrupt();
	                // Handle the interrupt accordingly
	            }
	        }
	        throw new RuntimeException("Image retrieval failed after " + maxRetries + " retries");
	    });
	}

public InputStream getImage(String hash) {
	 System.out.println("GET IMAGE: " + hash);
	    try {
	        // Ensure hash is not null
	        if(hash == null) {
	            System.err.println("Hash is null");
	            return null;
	        }

	        HttpResponse<InputStream> response = Unirest.get("https://arimagesynthesizer.p.rapidapi.com/get")
	            .header("X-RapidAPI-Key", "8b2bd64aa5msh34f679538ef2433p1e4a2djsn927a54490a26") 
	            .header("X-RapidAPI-Host", "arimagesynthesizer.p.rapidapi.com")
	            .queryString("hash", hash)
	            .asObject(InputStream.class);
	        
	        int status = response.getStatus();

	        if (status == 200) {
	       
	            return response.getBody();
	        } else {
	            System.err.println("Unexpected response status: " + status);
	            System.err.println("Response body: " + response.getBody()); 
	            return null;
	        }
	    } catch (UnirestException e) {
	        e.printStackTrace();
	        System.err.println("Failed to retrieve image");
	    }
	    return null;
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
