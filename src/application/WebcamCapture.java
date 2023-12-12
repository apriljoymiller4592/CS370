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
	public boolean takePicture = false;
	private boolean isVideoFeed = true;
	ImageView imageView = new ImageView();
    
	//private final ImageView imageView = new ImageView();
	
	static {
        // Initialize SLF4J here
        org.slf4j.LoggerFactory.getILoggerFactory();
    }
	/*
	
	class VideoFeedTaker extends Thread{
		
		//@Override
		public void run() {
			while(true) {
			webcam.open();
			while(isVideoFeed) {
				try {
					Image image = webcam.getImage();
					imageView.setImage(image));
					//imageHolder.setIcon(new ImageIcon(image));
					
					BufferedImage bufferedImage = webcam.getImage();
					
					Image image = convertToJavaFXImage(bufferedImage);
					imageView.setImage(image);
					
					//frames per second
					Thread.sleep(60);
				}catch(InterruptedException ex) {
					Logger.getLogger(WebcamCapture.class.getName()).log(Level.SEVERE,null,ex);
				}	
			}		
					ex.printStackTrace();
				}
				
			}
			webcam.close();
		}
		
			//part of the library to convert to Image
		 private Image convertToJavaFXImage(BufferedImage bufferedImage) {
		        WritableImage writableImage = new WritableImage(bufferedImage.getWidth(), bufferedImage.getHeight());
		        PixelWriter pixelWriter = writableImage.getPixelWriter();

		        for (int x = 0; x < bufferedImage.getWidth(); x++) {
		            for (int y = 0; y < bufferedImage.getHeight(); y++) {
		                pixelWriter.setArgb(x, y, bufferedImage.getRGB(x, y));
		            }
		        }

		        return writableImage;
		    }
	
		 
	}//end of class
	*/
	
	
	
	
	public Image TakePicture() throws IOException {
		 WebcamCapture webcamCapture = new WebcamCapture(); // Create an instance
			// BasicConfigurator.configure();
		    webcamCapture.webcam = Webcam.getDefault();
		    
		    webcamCapture.webcam.open();
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
        if (webcam == null) {
            System.out.println("No webcam found.");
            return;
        }

        //webcam.open();

        // Create a new stage for the popup
        Stage popupStage = new Stage();
        popupStage.setTitle("Picture Popup");

        
        
        // Create content for the popup
        Label label = new Label("take picture popup window");
        
        ImageView imageView = new ImageView();
        //ImageView imageView = new ImageView();
        imageView.setFitHeight(300);
        imageView.setFitWidth(300);
        
        Button takePictureButton = new Button("Take Picture");
        
        
        takePictureButton.setOnAction(e -> {
			try {
				isVideoFeed = false;
				Image pic = TakePicture();
				imageView.setImage(pic);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		});
        
        Button UsePictureButton = new Button("Use Picture");
        
        
        UsePictureButton.setOnAction(e -> {
			
        	File fileName = new File("CS370Project/WebcamPic/FaceOfArt.jpg");
        	if(!fileName.exists())
        		return;
        	takePicture = true;
        	//String encodedString = "CS370Project/WebcamPic/FaceOfArt.jpg";
        	Main mainclass = new Main();
        	mainclass.showSuccessAlert("picture successful", "Click Generate Button to Continue");
        	popupStage.hide();
        	
			
		});
        //exit event
        popupStage.setOnHiding(event -> {
            System.out.println("Popup is closed");
            webcam.close();
            isVideoFeed = false;
        });
        
        VBox popupLayout = new VBox(10);
        
        popupLayout.getChildren().addAll(label,imageView, takePictureButton,UsePictureButton);
        popupLayout.setAlignment(Pos.CENTER);
        
        //starts the videofeed
        //new VideoFeedTaker().start();
        
        // Set the scene for the popup stage
        Scene popupScene = new Scene(popupLayout,420,420);
        popupStage.setScene(popupScene);


        // Show the popup stage
        popupStage.show();

        
     }
	
	
	
	
	
	
	public static void main(String[] args){
		 /*WebcamCapture webcamCapture = new WebcamCapture(); // Create an instance
		// BasicConfigurator.configure();
	    webcamCapture.webcam = Webcam.getDefault(); 
		
	    webcamCapture.webcam.addWebcamListener(new WebcamListener(){//the event listener
			
		
		
			@Override
			public void webcamOpen(WebcamEvent we) {
				System.out.println("webcam open");
				VideoFeedTaker feed = webcamCapture.new VideoFeedTaker(); // Create an instance of VideoFeedTaker
                feed.start();
			}
			@Override
			public void webcamClosed(WebcamEvent we) {
				System.out.println("webcam closed");
			}
			@Override
			public void webcamDisposed(WebcamEvent we) {
				System.out.println("webcam disposed");
			}
			@Override
			public void webcamImageObtained(WebcamEvent we) {
				System.out.println("Image taken");
			}
			
		});
		
		for(Dimension supportedSize: webcamCapture.webcam.getViewSizes()) {//gets all the available sizes
			System.out.println(supportedSize.toString());
		}//we should make a gui so user can choose
		
		webcamCapture.webcam.setViewSize(new Dimension(640,480));//setting the dimension
		//webcam.setViewSize(WebcamResolution.<method>); another method for setting resolution
		
		
		webcamCapture.webcam.open();
		//need to use diffrent name every time or it will replace
		
		//takes a picture
		ImageIO.write(webcamCapture.webcam.getImage(),"JPG",new File("firstCapture.jpg"));
		webcamCapture.webcam.close();*/
		
		
		/*try {
			WebcamCapture picture = new WebcamCapture();
			picture.TakePicture();
		} catch(IOException e) {
			e.printStackTrace();
		}*/
		
	}
	
	
}





