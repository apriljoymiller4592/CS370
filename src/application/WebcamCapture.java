package application;

//import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

//import org.apache.log4j.BasicConfigurator;

import javax.imageio.ImageIO;

//import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.Webcam;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

//import javafx.application.Platform;
//import javafx.embed.swing.SwingFXUtils;//issue with this library
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class WebcamCapture{
	private Webcam webcam;
	public boolean takePicture = false;//checks if usePicture button was used
	private boolean isVideoFeed = true;//start videofeed at popup startup
	ImageView imageView = new ImageView();//initialize the imageview (to display image/ videofeed)
    
	
	static {//extention issue not sure if still need
        // Initialize SLF4J here
        org.slf4j.LoggerFactory.getILoggerFactory();
    }
	
	class VideoFeedTaker extends Thread{//thread for videofeed
		//set up as a class
		
		//@Override
		public void run() {
			webcam.open();//opens the webcam
			while(isVideoFeed) {//continuously takes a image
				try {
					
					BufferedImage bufferedImage = webcam.getImage();
					
					Image image = convertToJavaFXImage(bufferedImage);//convert buffer image to image
					imageView.setImage(image);//where the videofeed is set
					
					//frames per second less = better
					Thread.sleep(30);
				}catch(InterruptedException ex) {
					ex.printStackTrace();
				}
				
			}
			webcam.close();
		}
			//part of the library to convert to Image
		 private Image convertToJavaFXImage(BufferedImage bufferedImage) {
		        WritableImage writableImage = new WritableImage(bufferedImage.getWidth(), bufferedImage.getHeight());
		        PixelWriter pixelWriter = writableImage.getPixelWriter();

		        for (int x = 0; x < bufferedImage.getWidth(); x++) {//go through pixel by pixel
		            for (int y = 0; y < bufferedImage.getHeight(); y++) {
		                pixelWriter.setArgb(x, y, bufferedImage.getRGB(x, y));
		            }
		        }

		        return writableImage;//returns a image
		    }
	
		 
	}//end of  VideoFeedTaker class
	
	
	
	
	public Image TakePicture() throws IOException {//for take picture button
		 WebcamCapture webcamCapture = new WebcamCapture(); // Create an instance
			// BasicConfigurator.configure();
		    webcamCapture.webcam = Webcam.getDefault();
		    
		    webcamCapture.webcam.open();//already open if video feed is up 
			//need to use diffrent name every time or it will replace
		
		    
		    // Generate a unique file name, e.g., based on timestamp
	        //String fileName = "capture_" + System.currentTimeMillis() + ".jpg";
		    
		    // Create the directory if it doesn't exist
	        File imageDirectory = new File("CS370Project/WebcamPic");
	        imageDirectory.mkdirs();
	        
	        //takes a picture
		    File imageFile = new File("CS370Project/WebcamPic","FaceOfArt.jpg");
		    BufferedImage bufferedImage = webcamCapture.webcam.getImage();
	        ImageIO.write(bufferedImage, "JPG", imageFile);

	        webcamCapture.webcam.close();
	        System.out.println("Took a picture");

	        // Read the image file and return the Image
	        return new Image(imageFile.toURI().toString());
	}
	
	public void VideoFeed(){
	    // Initialize webcam
        webcam = Webcam.getDefault();
        if (webcam == null) {//error handleing if device has no camera
            System.out.println("No webcam found.");
            return;
        }

        // Create a new stage for the popup
        Stage popupStage = new Stage();
        popupStage.setTitle("Picture Popup");

        
        
        // Create content for the popup
        Label label = new Label("take picture popup window");
        
        //ImageView imageView = new ImageView();
        imageView.setFitHeight(300);
        imageView.setFitWidth(300);
        
        Button takePictureButton = new Button("Take Picture");
        
        //event function to handle the takePicture event
        takePictureButton.setOnAction(e -> {
			try {
				isVideoFeed = false;
				Image pic = TakePicture();//may cause IO exception, but is where the Image is stored to var
				imageView.setImage(pic);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		});
        
        Button UsePictureButton = new Button("Use Picture");
        
        //use picture button to transfer over to main
        UsePictureButton.setOnAction(e -> {
			
        	File fileName = new File("CS370Project/WebcamPic/FaceOfArt.jpg");
        	if(!fileName.exists())
        		return;
        	takePicture = true;
        	//String encodedString = "CS370Project/WebcamPic/FaceOfArt.jpg";
        	Main mainclass = new Main();
        	mainclass.showSuccessAlert("picture successful", "Click Generate Button to Continue");
        	popupStage.hide();//closes the popup
        	
			
		});
        
        //exit event
        popupStage.setOnHiding(event -> {
            System.out.println("Popup is closed");
            webcam.close();
            isVideoFeed = true;
        });
        //sets up the popup
        VBox popupLayout = new VBox(10);
        
        //inserts all the displays
        popupLayout.getChildren().addAll(label,imageView, takePictureButton,UsePictureButton);
        popupLayout.setAlignment(Pos.CENTER);
        
        //starts the videofeed
        new VideoFeedTaker().start();
        
        // Set the scene for the popup stage
        Scene popupScene = new Scene(popupLayout,420,420);
        popupStage.setScene(popupScene);


        // Show the popup stage
        popupStage.show();

        
     }
	
	
	public static void main(String[] args){
		
		
	}
	
	
}



