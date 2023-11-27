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

public class WebcamCapture{
	private Webcam webcam;
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
				try {
					Image image = webcam.getImage();
					imageView.setImage(image));
					//imageHolder.setIcon(new ImageIcon(image));
					
					Thread.sleep(60);
				}catch(InterruptedException ex) {
					Logger.getLogger(WebcamCapture.class.getName()).log(Level.SEVERE,null,ex);
				}	
			}		
		}
		
		 
	}//end of class
	*/
	
	
	
	
	public Image TakePicture() throws IOException {
		 WebcamCapture webcamCapture = new WebcamCapture(); // Create an instance
			// BasicConfigurator.configure();
		    webcamCapture.webcam = Webcam.getDefault();
		    
		    webcamCapture.webcam.open();
			//need to use diffrent name every time or it will replace
			
			//takes a picture
			/*ImageIO.write(webcamCapture.webcam.getImage(),"JPG",new File("firstCapture.jpg"));
			webcamCapture.webcam.close();
			System.out.println("took a picture");
			*/
		    File imageFile = new File("firstCapture.jpg");
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
        imageView.setFitHeight(200);
        imageView.setFitWidth(200);
        
        Button takePictureButton = new Button("Take Picture");
        
        
        takePictureButton.setOnAction(e -> {
			try {
				Image pic = TakePicture();
				imageView.setImage(pic);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		});
        
        VBox popupLayout = new VBox(10);
        
        
        popupLayout.getChildren().addAll(label,imageView, takePictureButton);
        popupLayout.setAlignment(Pos.CENTER);
        
        
        // Set the scene for the popup stage
        Scene popupScene = new Scene(popupLayout,300,250);
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



