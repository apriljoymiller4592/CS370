package application;

//import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.*;

import java.awt.Dimension;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class WebcamCapture {
	Webcam webcam;
	//private javax.swing.JLabel imageHolder;
	
	
	class VideoFeedTaker extends Thread{
		
		//@Override
		public void run() {
			while(true) {
				try {
					Image image = webcam.getImage();
					Thread.sleep(5000);
					//imageHolder.setIcon(new ImageIcon(image));
				}catch(InterruptedException ex) {
					Logger.getLogger(WebcamCapture.class.getName()).log(Level.SEVERE,null,ex);
				}	
			}		
		}
	}//end of class
	public static void main(String[] args) throws IOException{
	/*	 WebcamCapture webcamCapture = new WebcamCapture(); // Create an instance
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
	}
	
}



